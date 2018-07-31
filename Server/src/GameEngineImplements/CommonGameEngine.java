package GameEngineImplements;

import BasicState.PlayerGameState;
import BasicState.RoomState;
import CommunicateControl.ObjThreadAsyn;
import CommunicateControl.ObjThreadAsynHolder;
import Direction.DirectionBuffer;
import Direction.TCP.SocketDirectionBuffer;
import GameEngineImplements.Game_GluttonousSnake.GluttonousSnakeEngineCore;
import GameEngineImplements.Game_TankBattle.TankBattleEngineCore;
import GameState.GameConfig.GameConfig;
import GameState.GameResult.GameResult;
import GameState.GameResult.GameResult_NormalEnd;
import GameState.GridMap;
import Message.Common.Message;
import Message.RoomMessage.*;
import GameEngine.GameEngine;
import GameEngine.GameEngineCore;
import GameEngine.GameEngineHolder;
import ServerBase.ServerThread;;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommonGameEngine extends ServerThread implements GameEngine {
    class Transfer implements ObjThreadAsynHolder<MMove,GridMap>{
        public void setUpObjThread(Socket socket){}
        public void onRecvObj(MMove obj){
            System.out.println(CommonGameEngine.this+" receive direction from \033[1;33m"+obj.account+"\033[0m");
            directionBufferHashtable.get(obj.account).setDirection(obj.direction);
        }
        public void toSendObj(GridMap obj){}
        public void finish(){}
        public void exit(ObjThreadAsyn src){}
    }
    private GameConfig gameConfig;
    private GameEngineHolder gameEngineHolder;
    private Hashtable<String,PlayerGameState> playerGameStateHashtable =new Hashtable<>();


    private ServerSocket serverSocket;
    Hashtable<String, DirectionBuffer> directionBufferHashtable =new Hashtable<>();
    Hashtable<String, ObjThreadAsyn<MMove,GridMap>> objThreadAsynHashtable=new Hashtable<>();
    private Transfer transfer=new Transfer();
    GameEngineCore gameEngineCore;
    GameResult gameResult;

    public CommonGameEngine(RoomState roomState, GameEngineHolder gameEngineHolder){
        super(null);
        this.gameConfig=roomState.getRoomConfig().getGameConfig();
        this.gameEngineHolder=gameEngineHolder;
        gameConfig.InitPlayerGameStates(playerGameStateHashtable,roomState.players);
        this.gameResult=new GameResult_NormalEnd(playerGameStateHashtable);
    }

    private Random r=new Random(LocalDateTime.now().getNano());


    //建立读取区域，告知客户端新连接信息,建立连接
    @Override
    public void buildupBufferAndConnection() {
        try {
            serverSocket = new ServerSocket(0);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true,"建立缓冲分配处理资源...",null));
        Socket socket=null;
        for(String account:playerGameStateHashtable.keySet()){
            //给一个玩家发送连接信息
            gameEngineHolder.sendMessageToSinglePlayer(account,new MConnect(serverSocket.getInetAddress(),serverSocket.getLocalPort()));
            try {
                socket = serverSocket.accept();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            System.out.println(this+" connected with "+account);
            ObjThreadAsyn<MMove,GridMap> objThreadAsyn=new ObjThreadAsyn<>(transfer,socket);
            SocketDirectionBuffer reader=new SocketDirectionBuffer(account);
            objThreadAsyn.start();
            directionBufferHashtable.put(account,reader);
            objThreadAsynHashtable.put(account,objThreadAsyn);
        }
    }

    @Override
    public void initCore(){
        gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true,"创建游戏核..."+gameConfig.getGameType(),null));
        switch (gameConfig.getGameType()){
            case TankBattle:
                gameEngineCore=new TankBattleEngineCore(gameConfig,directionBufferHashtable,this,gameResult);
                break;
            case GluttonousSnake:
                gameEngineCore=new GluttonousSnakeEngineCore(gameConfig,directionBufferHashtable,this,gameResult);
                break;
        }
    }
    @Override
    public void initGridMap() {
        gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true,"随机化游戏棋盘...",null));
        gameEngineCore.buildGridMapAndSetInitDirection();
    }

    @Override
    public void buildupGridBroadcaster() {
        gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true,"建立连接资源...",null));
        broadcastGridMap();
    }
    @Override
    public synchronized void killConnection(String account){
        ObjThreadAsyn objThreadAsyn=objThreadAsynHashtable.get(account);
        try {
            objThreadAsyn.finish();
            objThreadAsyn.getSocket().close();
        }catch (IOException ex){
            System.err.println("some thread seem to be release already");
        }
        if(gameEngineCore!=null){
            gameEngineCore.notifyLeave(account);
        }
    }


    @Override
    public synchronized void finish() {
        System.out.println(this+" finish called");
        synchronized (this){
            this.notifyAll();
        }
        //objThreadAsyn关闭
        for(String account:playerGameStateHashtable.keySet()){
            objThreadAsynHashtable.get(account).finish();
        }
        //serverSocket关闭
        if(serverSocket!=null)
            try {
                serverSocket.close();
                serverSocket=null;
            }catch (IOException ex){
                ex.printStackTrace();
            }
        gameEngineCore.setFinish(true);
    }

    @Override
    public void perform() {
        try{
            buildupBufferAndConnection();
            initCore();
            initGridMap();
            buildupGridBroadcaster();

            gameEngineHolder.broadcastToAllMessage(new MStart(true,"count Down",null,3));
            gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true,"倒计时 3",null));
            TimeUnit.SECONDS.sleep(1);
            gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true,"倒计时 2",null));
            TimeUnit.SECONDS.sleep(1);
            gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true,"倒计时 1",null));
            TimeUnit.SECONDS.sleep(1);
            new Thread(gameEngineCore).start();
            synchronized (this){
                this.wait();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            System.out.println(this+ " exit perform");
        }
    }

    @Override
    public void coreExit(GameResult gameResult){
        gameEngineHolder.onResult(gameResult);
    }
    @Override
    public void broadcastGridMap(){
        GridMap gridMap=gameEngineCore.getGridMap();
        synchronized (gridMap) {
            for (String account : objThreadAsynHashtable.keySet()) {
                objThreadAsynHashtable.get(account).sendMsg(gridMap);
            }
        }
    }

    public void setPause(boolean pause){
        if(gameEngineCore!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(pause)
                        gameEngineCore.setPause(pause);
                    else{
                        try {
                            gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true, "倒计时 3", null));
                            TimeUnit.SECONDS.sleep(1);
                            gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true, "倒计时 2", null));
                            TimeUnit.SECONDS.sleep(1);
                            gameEngineHolder.broadcastToAllMessage(new MRoomBroadcast(true, "倒计时 1", null));
                            TimeUnit.SECONDS.sleep(1);
                            gameEngineCore.setPause(pause);
                        }catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    public Hashtable<String,PlayerGameState> getPlayerGameStates(){
        return playerGameStateHashtable;
    }

    public void notifyPlayerStateUpdated(){
        gameEngineHolder.notifyBroadcastRoomState();
    }


    public void broadcastMessage(Message message){
        gameEngineHolder.broadcastToAllMessage(message);
    }
}
