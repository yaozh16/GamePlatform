package GameEngineImplements.Game_TankBattle;

import BasicState.PlayerGameState;
import Direction.Direction;
import Direction.DirectionBuffer;
import GameEngine.*;

import GameState.GameConfig.GameConfig;
import GameState.GameResult.GameResult;
import GameState.GridMap;
import GameState.GridObjects.*;
import Message.RoomMessage.MRoomBroadcast;
import ServerBase.ServerThread;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TankBattleEngineCore  extends ServerThread implements GameEngineCore {
    private final GameConfig gameConfig;
    private final Hashtable<String,DirectionBuffer> directionBufferHashtable;
    private final GameEngine gameEngine;
    private final GameResult gameResult;
    private final Random r=new Random(LocalDateTime.now().getNano());
    public TankBattleEngineCore(GameConfig gameConfig, Hashtable<String,DirectionBuffer> directionBufferHashtable, GameEngine gameEngine,GameResult gameResult){
        super(null);
        this.gameConfig=gameConfig;
        this.directionBufferHashtable = directionBufferHashtable;
        this.gameEngine=gameEngine;
        this.gameResult=gameResult;
    }

    private GridMap gridMap;
    private Hashtable<String,Tank> tankHashtable=new Hashtable<>();
    private HashSet<Bomb> bombs=new HashSet<>();
    private volatile boolean pause=false;
    private volatile boolean finish=false;

    public synchronized GridMap buildGridMapAndSetInitDirection(){
        gridMap=new GridMap(gameConfig.getGridWidth(),gameConfig.getGridWidth());

        //墙的分配
        gridMap.setWall(0,0,gameConfig.getGridWidth(),1);
        gridMap.setWall(0,0,1,gameConfig.getGridWidth());
        gridMap.setWall(gameConfig.getGridWidth()-1,0,1,gameConfig.getGridWidth());
        gridMap.setWall(0,gameConfig.getGridHeight()-1,gameConfig.getGridWidth(),1);


        //Tank的分配
        Direction d;
        Integer pos=null;
        for(String account: directionBufferHashtable.keySet()){
            do {
                d = Direction.values()[r.nextInt(4)];
                pos=gridMap.tryGetLongObject(1,d);
                if(pos!=null)
                    break;
            }while (true);
            tankHashtable.put(account,new Tank(d,account,pos%gridMap.width,pos/gridMap.width,gridMap));
            directionBufferHashtable.get(account).setDirection(d);
        }
        //洞的分配
        //no hole

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
    public synchronized void notifyLeave(String account) {
        synchronized (gridMap){
            Tank tank=tankHashtable.get(account);
            if(tank!=null){
                tank.markDie();
                gameResult.delLife(account,gameResult.getScores().get(account).getLife());
                gameEngine.notifyPlayerStateUpdated();
            }
        }
    }

    private boolean determineEnd(){
        if(tankHashtable.keySet().size()==1){
            //单人游戏
            for(String acc:tankHashtable.keySet()){
                if(!tankHashtable.get(acc).isDead()){
                    return false;
                }
                if(!tankHashtable.get(acc).canRestart(8)){
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
            for(String acc:tankHashtable.keySet()){
                if(gameResult.getScores().get(acc).getLife()>0){
                    aliveCount++;
                }
            }
            return aliveCount<=1;
        }
    }
    private void calculateWinner(GameResult gameResult){
        if(tankHashtable.keySet().size()==1){
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

    private int bonusNeedPut=0;
    private void BombMove(){
        synchronized (gridMap) {
            HashSet<Bomb> left=new HashSet<>();
            left.clear();
            for (Bomb bomb : bombs) {
                bomb.tryMoveAndHit();
                if (bomb.isAlive()) {
                    left.add(bomb);
                } else {
                    bomb.clearBody();
                    Integer hitPosition = bomb.getHitPostion();
                    if (hitPosition != null) {
                        GridMapObject meetObj = gridMap.getGridMapObject(hitPosition);

                        System.out.println("a bomb is hit "+meetObj.getClass());
                        if (meetObj.getClass().equals(GridTank.class)) {
                            GridTank tank = (GridTank) meetObj;
                            if(!tankHashtable.get(tank.getAccount()).isDead()) {
                                tankHashtable.get(tank.getAccount()).markDie();
                                gameResult.delLife(tank.getAccount(), bomb.getFirePower());
                                gameEngine.broadcastMessage(new MRoomBroadcast(true, bomb.getSender() + "击中" + tank.getAccount() + ",Hp-" + bomb.getFirePower() + "！", null));
                            }
                        } else if (meetObj.getClass().equals(GridWall.class)) {
                            gridMap.setBlank(hitPosition);
                        } else if (meetObj.getClass().equals(GridBonus.class)) {
                            gridMap.setBlank(hitPosition);
                            bonusNeedPut++;
                            tankHashtable.get(bomb.getSender()).onBombTarget();
                            gameEngine.broadcastMessage(new MRoomBroadcast(true,bomb.getSender()+"的炮弹击中物资！",null));
                            gameResult.addScore(bomb.getSender(), bomb.getFirePower());
                        } else if (meetObj.getClass().equals(GridBomb.class)) {
                            gridMap.setBlank(hitPosition);
                            ((GridBomb) meetObj).markBeHit();
                        }
                    }
                }
            }
            bombs.clear();
            bombs.addAll(left);
            bonusNeedPut = gridMap.tryPutBonus(bonusNeedPut);
            finish = determineEnd();
        }
    }
    private void TankMove(){
        synchronized (gridMap) {
            System.out.println("\33[1;35m Tanks move\033[0m");
            for (String account : tankHashtable.keySet()) {
                Tank tank = tankHashtable.get(account);
                synchronized (tank) {
                    if (tank.isDead()) {
                        if (tank.deadWait() >= 8) {
                            tank.clearBody();
                            if (gameResult.getScores().get(account).getLife() > 0) {
                                Integer tail = null;
                                Direction direction = null;
                                for (Direction cDirection : Direction.values()) {
                                    direction = cDirection;
                                    tail = gridMap.tryGetLongObject(1, direction);
                                    if (tail != null) {
                                        break;
                                    }
                                }
                                if (tail != null) {
                                    int x = tail % gridMap.width;
                                    int y = tail / gridMap.width;
                                    directionBufferHashtable.get(account).setDirection(direction);
                                    tankHashtable.put(account, new Tank(direction, account, x, y, gridMap));
                                }
                            }
                        }
                        continue;
                    }
                    Direction curDirection = directionBufferHashtable.get(account).getDirection();


                    Class<? extends GridMapObject> meet = tank.tryMove(curDirection);
                    if (meet == null) {
                        Bomb bomb = tank.tryOpenFire();
                        if (bomb != null)
                            bombs.add(bomb);
                    } else {
                        System.out.println("\33[1;35m Tank meet " + meet + "\033[0m");
                        if (meet.equals(GridBonus.class)) {
                            bonusNeedPut++;
                            gameResult.addScore(account, 1);
                            tank.onMeetBonus();
                            gameEngine.broadcastMessage(new MRoomBroadcast(true,account+"获得物资,弹药威力加倍！",null));
                        } else {
                            tank.markDie();
                            gameResult.delLife(account,1);
                        }
                    }
                }
            }

            gameEngine.notifyPlayerStateUpdated();
            finish = determineEnd();
        }
    }
    @Override
    public void perform(){
        try {
            Timer timer=new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!pause) {
                        gameEngine.broadcastGridMap();
                    }
                }
            },100,100);
            int index=0;
            while (!interrupted()&&!finish) {
                //gameEngine.broadcastGridMap();
                TimeUnit.MILLISECONDS.sleep(GameConfig.speedTranslater.get(gameConfig.getSpeed()));
                if(!pause) {
                    index++;
                    index%=3;
                    if(index==1) {
                        TankMove();
                    }else {
                        BombMove();
                    }
                }
            }
            calculateWinner(gameResult);
            gameEngine.coreExit(gameResult);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            System.out.println(this+" exit");
        }
    }
}

