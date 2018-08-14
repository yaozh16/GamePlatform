package GameEngineImplements.Game_GluttonousSnake;


import BasicState.PlayerGameState;
import Direction.Direction;
import Direction.DirectionBuffer;
import GameEngine.GameEngine;
import GameEngine.*;
import GameState.GameConfig.GameConfig;
import GameState.GameResult.GameResult;
import GameState.GridMap;
import GameState.GridObjects.GridBlank;
import GameState.GridObjects.GridBonus;
import GameState.GridObjects.GridHole;
import GameState.GridObjects.GridMapObject;
import Message.RoomMessage.MRoomBroadcast;
import ServerBase.ServerThread;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GluttonousSnakeEngineCore extends ServerThread implements GameEngineCore {
    private final GameConfig gameConfig;
    private final Hashtable<String,DirectionBuffer> directionBufferHashtable;
    private final GameEngine gameEngine;
    private final GameResult gameResult;
    private final Random r=new Random(LocalDateTime.now().getNano());
    public GluttonousSnakeEngineCore(GameConfig gameConfig, Hashtable<String,DirectionBuffer> directionBufferHashtable, GameEngine gameEngine,GameResult gameResult){
        super(null);
        this.gameConfig=gameConfig;
        this.directionBufferHashtable = directionBufferHashtable;
        this.gameEngine=gameEngine;
        this.gameResult=gameResult;
    }

    private GridMap gridMap;
    private Hashtable<String, Snake> snakeHashtable=new Hashtable<>();
    private volatile boolean pause=false;
    private volatile boolean finish=false;

    public synchronized GridMap buildGridMapAndSetInitDirection(){
        gridMap=new GridMap(gameConfig.getGridWidth(),gameConfig.getGridHeight());
        //蛇的分配
        Direction d;
        int hx,hy;
        for(String account: directionBufferHashtable.keySet()){
            do {
                hx = r.nextInt(gameConfig.getGridWidth() - 4) + 2;
                hy = r.nextInt(gameConfig.getGridHeight() - 4) + 2;
                d = Direction.values()[r.nextInt(4)];
                if(!gridMap.gridMapObjects.get(hx+hy*gameConfig.getGridWidth()).getClass().equals(GridBlank.class)){
                    continue;
                }
                if(!gridMap.gridMapObjects.get(hx-d.dx()+(hy-d.dy())*gameConfig.getGridHeight()).getClass().equals(GridBlank.class)){
                    continue;
                }
                break;
            }while (true);
            System.out.printf("init Snake(%s) at (%d,%d) in %s\n",account,hx,hy,d);
            snakeHashtable.put(account,new Snake(d,hx,hy,gridMap,account));
            directionBufferHashtable.get(account).setDirection(d);
        }
        //墙的分配
        //gridMap.setWall(0,0,gameConfig.gridWidth,1);
        for(int i=r.nextInt(10)+4;i>=0;i--){
            int walls=r.nextInt(5)+2;
            Direction direction=Direction.values()[r.nextInt(4)];
            gridMap.tryPutWall(walls,direction);
        }
        System.out.println(gridMap.tryPutWall(gameConfig.getGridWidth()*2/3,Direction.LEFT));
        System.out.println(gridMap.tryPutWall(gameConfig.getGridHeight()/2,Direction.UP));

        //洞的分配
        HashSet<Integer> holegroup;
        for(int i=0;i<gameConfig.getHolePair();i++) {
            holegroup = gridMap.tryGetBlank(2);
            if (holegroup.size() > 1) {
                Vector<Integer> holeArray = new Vector<>();
                holeArray.addAll(holegroup);
                GridHole.generateHolePair(holeArray.firstElement(), holeArray.lastElement(), gridMap);
            }
        }
        //bonus分配
        gridMap.tryPutBonus(gameConfig.getBonusCount());
        System.out.printf("init GridMap:%s\n",gridMap.toString());
        return gridMap;
    }

    public GridMap getGridMap(){
        return gridMap;
    }

    public synchronized void setPause(boolean pause){
        this.pause=pause;
    }
    public synchronized void setFinish(boolean finish){
        this.finish=finish;
    }
    public synchronized void notifyLeave(String account){
        synchronized (gridMap) {
            Snake snake = snakeHashtable.get(account);
            if (snake != null) {
                snake.markDie();
                gameResult.delLife(account,gameResult.getScores().get(account).getLife());
                gameEngine.notifyPlayerStateUpdated();
            }
        }
    }

    private boolean determineEnd(){
        if(snakeHashtable.keySet().size()==1){
            //单人游戏
            for(String acc:snakeHashtable.keySet()){
                if(!snakeHashtable.get(acc).dead){
                    return false;
                }
                if(!snakeHashtable.get(acc).canRestart(8)){
                    return false;
                }
                if(gameResult.getScores().get(acc).getLife()>0){
                    return false;
                }
            }
            return true;
        }else {
            //多人游戏
            int aliveCount=0;
            for(String acc:snakeHashtable.keySet()){
                if(gameResult.getScores().get(acc).getLife()>0){
                    aliveCount++;
                }
            }
            return aliveCount<=1;
        }
    }
    private void calculateWinner(GameResult gameResult){
        if(snakeHashtable.keySet().size()==1){
            //单人游戏
            gameResult.setResultReport("Game End:单人游戏不累积胜负次数");
        }else {
            //多人游戏
            Hashtable<String,PlayerGameState> scores=gameResult.getScores();
            String winner=null;
            for(String account:scores.keySet()){
                if(winner==null||scores.get(account).getScore()>scores.get(winner).getScore()){
                    winner=account;
                }
            }
            //1 winner
            HashSet<String> winners=new HashSet<>();
            winners.add(winner);
            gameResult.setWinners(winners);

            //rest loser
            HashSet<String> losers=new HashSet<>();
            losers.addAll(scores.keySet());
            losers.remove(winner);
            gameResult.setLosers(losers);
        }
    }
    @Override
    public void perform(){
        try {
            int bonusNeedPut=0;
            Timer timer=new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!pause) {
                        gameEngine.broadcastGridMap();
                    }
                }
            },100,80);
            while (!interrupted()&&!finish) {
                //gameEngine.broadcastGridMap();
                TimeUnit.MILLISECONDS.sleep(GameConfig.speedTranslater.get(gameConfig.getSpeed()));
                if(!pause) {
                    synchronized (gridMap) {
                        for (String account : snakeHashtable.keySet()) {
                            Snake snake = snakeHashtable.get(account);
                            synchronized (snake) {
                                if (snake.dead) {
                                    if(snake.deadWait()>=8){
                                        snake.clearBody();
                                        if(gameResult.getScores().get(account).getLife()>0) {
                                            Integer tail = null;
                                            Direction direction = null;
                                            for (Direction cDirection : Direction.values()) {
                                                direction = cDirection;
                                                tail = gridMap.tryGetLongObject(2, direction);
                                                if (tail != null) {
                                                    break;
                                                }
                                            }
                                            if (tail != null) {
                                                int x=tail%gridMap.width;
                                                int y=tail/gridMap.width;
                                                directionBufferHashtable.get(account).setDirection(direction);
                                                snakeHashtable.put(account, new Snake(direction,(x+direction.dx()+gridMap.width)%gridMap.width,(y+direction.dy()+gridMap.height)%gridMap.height,gridMap,account));
                                            }
                                        }
                                    }
                                    continue;
                                }
                                Direction curDirection = directionBufferHashtable.get(account).getDirection();
                                snake.updateOldHeadTo(curDirection);
                                Class<? extends GridMapObject> meet = snake.tryMove(curDirection);
                                if (meet == null) {
                                    snake.updateNewTailFrom();
                                } else if (meet.equals(GridBonus.class)) {
                                    bonusNeedPut++;
                                    gameResult.addScore(account,1);
                                    gameEngine.notifyPlayerStateUpdated();
                                } else {
                                    //撞到了东西
                                    snake.markDie();
                                    gameResult.delLife(account,1);
                                    gameEngine.notifyPlayerStateUpdated();
                                }
                            }
                        }
                        bonusNeedPut = gridMap.tryPutBonus(bonusNeedPut);
                        finish = determineEnd();
                    }
                }
            }
            timer.cancel();
            calculateWinner(gameResult);
            gameEngine.coreExit(gameResult);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            System.out.println(this+" exit");
        }
    }
}
