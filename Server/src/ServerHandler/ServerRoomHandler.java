package ServerHandler;

import CommunicateControl.MsgThreadAsyn;
import CommunicateControl.MsgThreadAsynHolder;
import BasicState.PlayerState;
import BasicState.RoomState;
import CommunicateControl.ObjThreadAsyn;
import GameState.GameResult.GameResult;
import Message.Common.Message;
import Message.MessageProcessor.MessageProcessor;
import Message.MessageProcessor.MessageProcessorCollection;
import Message.RoomMessage.*;
import Message.UpdateMessage.MUpdatePlayersReply;
import Message.UpdateMessage.MUpdateRoomsReply;
import GameEngine.GameEngine;
import GameEngine.GameEngineHolder;
import GameEngineImplements.CommonGameEngine;
import ServerSingletons.ServerDB;
import ServerBase.ServerThread;
import ServerSingletons.ServerRoomManager;
import ServerSingletons.ServerBasicManager;

import java.net.Socket;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerRoomHandler extends ServerThread implements MsgThreadAsynHolder,GameEngineHolder {
    private final Hashtable<String,MsgThreadAsyn> players=new Hashtable<>();
    private final Hashtable<String,MsgThreadAsyn> viewers=new Hashtable<>();
    private final Hashtable<String,Boolean> pauseConfirmTable=new Hashtable<>();
    private Lock roomPersonLock=new ReentrantLock();
    private RoomState roomState;
    private Lock roomStateLock=new ReentrantLock();
    private GameEngine gameEngine;
    private Timer timer;
    public ServerRoomHandler(RoomState roomState,MsgThreadAsyn initMsgThreadAsyn,String initPlayer){
        super(null);
        this.roomState=roomState.copy();
        addPlayer(initPlayer,initMsgThreadAsyn);
    }

    public synchronized RoomState getRoomState(){
        return roomState;
    }

    //return if succeed
    public synchronized boolean addPlayer(String account,MsgThreadAsyn msgThreadAsyn){
        try{
            roomStateLock.lockInterruptibly();
            roomPersonLock.lockInterruptibly();
            if(roomState.getRoomConfig().getGameConfig().getMaxPlayer()>players.keySet().size()) {
                msgThreadAsyn.setObjThreadAsynHolder(this);
                players.put(account, msgThreadAsyn);
                roomState.players.add(account);
                broadcastToAllMessage(
                        new MRoomStateBroadcast(
                                ServerDB.getInstance().queryPlayerStates(roomState.roomParticipants()),
                                roomState.getRoomConfig().getGameConfig().InitPlayerGameStates(new Hashtable<>(),roomState.players),
                                roomState));
                return true;
            }else {
                return false;
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
            return false;
        }finally {
            roomPersonLock.unlock();
            roomStateLock.unlock();
        }
    }

    @Deprecated
    @Override public void setUpObjThread(Socket socket) {}

    @Override
    public void onRecvObj(Message message) {
        messageProcessorCollection.processMessage(message);
    }

    @Deprecated
    @Override public void toSendObj(Message message) {}


    //dismiss
    @Override
    public void finish() {
        try{
            broadcastToAllMessage(new MDismiss(true,"Dismiss",null));
            for(String account:players.keySet()){
                ServerDB.getInstance().trySetPlayerOnlineState(account, PlayerState.OnlineState.FREE);
                ServerRoomManager.getInstance().tryLeaveRoom(account,roomState.getRoomConfig().roomName);
                players.get(account).finish();
            }
            for(String account:viewers.keySet()){
                ServerDB.getInstance().trySetPlayerOnlineState(account, PlayerState.OnlineState.FREE);
                ServerRoomManager.getInstance().tryLeaveRoom(account,roomState.getRoomConfig().roomName);
                viewers.get(account).finish();
            }
            synchronized (ServerRoomHandler.this){
                performDone=true;
                ServerRoomHandler.this.notifyAll();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {

            System.out.println(this+" Broadcast for finish");
            ServerBasicManager.getInstance().broadcast(new MUpdateRoomsReply(true,"OK",null,ServerRoomManager.getInstance().fetchAllRoomState()));
            ServerBasicManager.getInstance().broadcast(new MUpdatePlayersReply(true,"OK",null,ServerDB.getInstance().fetchAllPlayerState()));
            System.out.println(this+" dismissed");
        }
    }

    //一方失去连接
    @Override
    public synchronized void exit(ObjThreadAsyn src) {
        for(String account:players.keySet()){
            if(players.get(account).equals(src)){
                onLeaveMsg(account);
                return;
            }
        }
        for(String account:viewers.keySet()){
            if(viewers.get(account).equals(src)){
                onLeaveMsg(account);
                return;
            }
        }
    }

    public void broadcastPlayersMessage(Message message){
        try{
            roomPersonLock.lockInterruptibly();
            for(String account:players.keySet()){
                players.get(account).sendMsg(message);
            }
        }catch (InterruptedException ex){

        }finally {
            roomPersonLock.unlock();
        }
    }
    public void broadcastPlayersExceptSomeOneMessage(String accountExcept,Message message){
        try{
            roomPersonLock.lockInterruptibly();
            for(String account:players.keySet()){
                if(accountExcept.equals(account))
                    continue;
                players.get(account).sendMsg(message);
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally {
            roomPersonLock.unlock();
        }
    }
    public void broadcastViewersMessage(Message message){
        try{
            roomPersonLock.lockInterruptibly();
            for(String account:viewers.keySet()){
                viewers.get(account).sendMsg(message);
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally {
            roomPersonLock.unlock();
        }
    }
    public void sendMessageToSinglePlayer(String account,Message message){
        try{
            roomPersonLock.lockInterruptibly();
            players.get(account).sendMsg(message);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally {
            roomPersonLock.unlock();
        }
    }
    public void broadcastToAllMessage(Message message){
        try{
            roomPersonLock.lockInterruptibly();
            for(String account:players.keySet()){
                players.get(account).sendMsg(message);
            }
            for(String account:viewers.keySet()){
                viewers.get(account).sendMsg(message);
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally {
            roomPersonLock.unlock();
        }
    }

    public synchronized void checkDismiss(){
        String error=ServerRoomManager.getInstance().tryDismissRoom(roomState.getRoomConfig().roomName);
    }
    public synchronized void installGameEngine(GameEngine gameEngine){
        System.out.println(this+" install GameEngine:"+gameEngine);
        ServerRoomHandler.this.gameEngine=gameEngine;
        System.out.println(this+" installed GameEngine:"+this.gameEngine);
        ServerRoomHandler.this.gameEngine.start();
        System.out.println(this+" installed GameEngine start:"+this.gameEngine);
    };
    public synchronized void onLeaveMsg(String account){
        //工作：将roomState.players中除去该player
        //然后广播新的信息
        //然后根据情况确认是否checkDismiss
        ServerDB.getInstance().queryPlayer(account).loginCountDel();
        System.out.println(this+"found \033[1;33m"+account+"\033[0m left"+"(loginCount->"+ServerDB.getInstance().queryPlayer(account).getLoginCount()+")");
        try{
            roomStateLock.lockInterruptibly();
            MsgThreadAsyn msgThreadAsyn=players.remove(account);
            roomState.players.remove(account);
            roomState.lefters.add(account);
            if(msgThreadAsyn!=null){
                msgThreadAsyn.finish();
                try{
                    if(!msgThreadAsyn.getSocket().isClosed()){
                        msgThreadAsyn.getSocket().close();
                    }
                }catch (Exception ex){
                    System.out.println("kill connection from lefter "+account);
                }
            }else {
                System.err.println("something missed?");
            }
            ServerDB.getInstance().trySetPlayerOnlineState(account, PlayerState.OnlineState.FREE);
            ServerRoomManager.getInstance().tryLeaveRoom(account,roomState.getRoomConfig().roomName);
            if(gameEngine!=null) {
                gameEngine.killConnection(account);
            }
            checkDismiss();
        }catch (InterruptedException exc){
            exc.printStackTrace();
        }finally {
            roomStateLock.unlock();
        }

        System.out.println(this+" Broadcast for onLeaveMsg");
        ServerBasicManager.getInstance().broadcast(new MUpdateRoomsReply(true,"OK",null,ServerRoomManager.getInstance().fetchAllRoomState()));
        ServerBasicManager.getInstance().broadcast(new MUpdatePlayersReply(true,"OK",null,ServerDB.getInstance().fetchAllPlayerState()));
    }

    MessageProcessorCollection messageProcessorCollection=new MessageProcessorCollection()
            .install(new MessageProcessor(MReady.class) {
                @Override
                public void process(Message message) {
                    MReady mReady=(MReady)message;
                    try{
                        roomStateLock.lockInterruptibly();
                        if(roomState.getRoomStateType()==RoomState.RoomStateType.Free) {
                            if (roomState.players.contains(mReady.account)) {
                                if(mReady.ready)
                                    ServerDB.getInstance().trySetPlayerOnlineState(mReady.account, PlayerState.OnlineState.READY);
                                else
                                    ServerDB.getInstance().trySetPlayerOnlineState(mReady.account, PlayerState.OnlineState.InRoom);

                                System.out.println(this+" Broadcast for Ready");
                                ServerBasicManager.getInstance().broadcast(new MUpdateRoomsReply(true,"OK",null,ServerRoomManager.getInstance().fetchAllRoomState()));
                                ServerBasicManager.getInstance().broadcast(new MUpdatePlayersReply(true,"OK",null,ServerDB.getInstance().fetchAllPlayerState()));
                                broadcastToAllMessage(new MRoomStateBroadcast(
                                        ServerDB.getInstance().queryPlayerStates(roomState.roomParticipants()),
                                        roomState.getRoomConfig().getGameConfig().InitPlayerGameStates(new Hashtable<>(),roomState.players),
                                        roomState));
                            }
                            Hashtable<String,PlayerState> allPlayerState=ServerDB.getInstance().queryPlayerStates(roomState.players);
                            for(String key:allPlayerState.keySet()){
                                if(allPlayerState.get(key).getOnlineState()!=PlayerState.OnlineState.READY){
                                    return;
                                }
                            }
                            //game start!
                            ServerRoomHandler.this.roomState.setRoomStateType(RoomState.RoomStateType.Game);
                            ServerDB.getInstance().trySetPlayerOnlineStates(roomState.players,PlayerState.OnlineState.Game);
                            broadcastToAllMessage(
                                    new MRoomStateBroadcast(
                                    ServerDB.getInstance().queryPlayerStates(roomState.roomParticipants()),
                                    roomState.getRoomConfig().getGameConfig().InitPlayerGameStates(new Hashtable<>(),roomState.players),
                                    roomState));
                            installGameEngine(new CommonGameEngine(roomState,ServerRoomHandler.this));
                        }else if(roomState.getRoomStateType()==RoomState.RoomStateType.Pause){
                            Hashtable<String,PlayerState> allPlayerState=ServerDB.getInstance().queryPlayerStates(roomState.players);
                            for(String key:allPlayerState.keySet()){
                                if(allPlayerState.get(key).getOnlineState()!=PlayerState.OnlineState.READY){
                                    return;
                                }
                            }
                            //Game Continue
                            ServerRoomHandler.this.roomState.setRoomStateType(RoomState.RoomStateType.Game);
                            ServerDB.getInstance().trySetPlayerOnlineStates(roomState.players,PlayerState.OnlineState.Game);

                            gameEngine.setPause(false);
                            broadcastToAllMessage(new MStart(true,"继续",null,1));
                            broadcastToAllMessage(
                                    new MRoomStateBroadcast(
                                            ServerDB.getInstance().queryPlayerStates(roomState.roomParticipants()),
                                            roomState.getRoomConfig().getGameConfig().InitPlayerGameStates(new Hashtable<>(),roomState.players),
                                            roomState));
                        }
                    }catch (InterruptedException exc){
                        exc.printStackTrace();
                    }finally {
                        roomStateLock.unlock();
                    }
                }
            })
            .install(new MessageProcessor(MLeave.class) {
                @Override
                public void process(Message message) {
                    MLeave mLeave=(MLeave)message;
                    onLeaveMsg(mLeave.account);
                }
            })
            .install(new MessageProcessor(MPause.class) {
                @Override
                public void process(Message message) {
                    try {
                        roomStateLock.lockInterruptibly();
                        if(roomState.getRoomStateType()!=RoomState.RoomStateType.Game){
                            return;
                        }
                        //发起暂停
                        MPause mPause = (MPause) message;
                        synchronized (pauseConfirmTable) {
                            pauseConfirmTable.put(mPause.account, true);
                            mPause.validateCode = null;
                            broadcastPlayersExceptSomeOneMessage(mPause.account, mPause);
                            broadcastPlayersMessage(new MRoomBroadcast(true, mPause.account + "请求暂停", null));
                            if(timer!=null){
                                timer.cancel();
                                timer=null;
                            }
                            timer=new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    synchronized (pauseConfirmTable) {
                                        if (roomState.getRoomStateType() == RoomState.RoomStateType.Game) {
                                            if(timer!=null) {
                                                for (String account : players.keySet()) {
                                                    if (!pauseConfirmTable.containsKey(account)) {
                                                        broadcastPlayersMessage(new MPauseBroadcast(true, "玩家" + account + "未回应暂停请求", null, false));
                                                        timer=null;
                                                        pauseConfirmTable.clear();
                                                        return;
                                                    }
                                                    if (!pauseConfirmTable.get(account).equals(true)) {
                                                        broadcastPlayersMessage(new MPauseBroadcast(true, "玩家" + account + "拒绝了暂停请求", null, false));
                                                        timer=null;
                                                        pauseConfirmTable.clear();
                                                        return;
                                                    }
                                                }
                                                broadcastPlayersMessage(new MPauseBroadcast(true, "游戏暂停", null, true));
                                                roomState.setRoomStateType( RoomState.RoomStateType.Pause);
                                                for(String account:players.keySet()){
                                                    ServerDB.getInstance().trySetPlayerOnlineState(account,PlayerState.OnlineState.Pause);
                                                }
                                                notifyBroadcastRoomState();
                                                gameEngine.setPause(true);
                                                timer=null;
                                                pauseConfirmTable.clear();
                                            }
                                        }
                                    }
                                }

                            }, 5000);//5s
                            checkPause();
                        }

                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }finally {
                        roomStateLock.unlock();
                    }
                }
            })
            .install(new MessageProcessor(MPauseAnswer.class) {
                @Override
                public void process(Message message) {
                    MPauseAnswer mPauseAnswer=(MPauseAnswer)message;
                    synchronized (pauseConfirmTable) {
                        pauseConfirmTable.put(mPauseAnswer.account, mPauseAnswer.agree);
                        checkPause();
                    }
                }
            })
            .install(new MessageProcessor(MChat.class) {
                @Override
                public void process(Message message) {
                    broadcastPlayersMessage(message);
                    broadcastViewersMessage(message);
                }
            })
            .install(new MessageProcessor(MConfigChange.class) {
                @Override
                public void process(Message message) {
                    MConfigChange mConfigChange=(MConfigChange)message;
                    try{
                        roomStateLock.lockInterruptibly();
                        if(roomState.getRoomStateType().equals(RoomState.RoomStateType.Free)){
                            if(roomState.players.size()<=mConfigChange.getGameConfig().getMaxPlayer()){
                                roomState.getRoomConfig().getGameConfig().copy(mConfigChange.getGameConfig());
                                sendMessageToSinglePlayer(mConfigChange.account,new MConfigChangeReply(true,"游戏配置修改成功！",null,roomState.getRoomConfig().getGameConfig()));
                                broadcastToAllMessage(new MConfigChangeBroadcast(true,mConfigChange.account+"修改了房间整体配置！",null,roomState.getRoomConfig().getGameConfig()));
                            }else {
                                sendMessageToSinglePlayer(mConfigChange.account,new MConfigChangeReply(false,"游戏房间内已有玩家人数大于您的配置！",null,roomState.getRoomConfig().getGameConfig()));
                            }
                        }else {
                            //中途只允许修改速度
                            if(mConfigChange.getGameConfig().getSpeed()==roomState.getRoomConfig().getGameConfig().getSpeed()){
                                sendMessageToSinglePlayer(mConfigChange.account,new MConfigChangeReply(false,"游戏中只允许改变游戏速度！",null,roomState.getRoomConfig().getGameConfig()));
                            }else {
                                roomState.getRoomConfig().getGameConfig().setSpeed(mConfigChange.getGameConfig().getSpeed());
                                broadcastToAllMessage(new MConfigChangeBroadcast(true,mConfigChange.account+"修改了房间速度配置！",null,roomState.getRoomConfig().getGameConfig()));
                            }
                        }
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }finally {
                        roomStateLock.unlock();
                    }
                }
            });

    private void checkPause(){
        synchronized (pauseConfirmTable) {
            if (roomState.getRoomStateType() == RoomState.RoomStateType.Game) {
                if(timer!=null) {
                    for (String account : players.keySet()) {
                        if (!pauseConfirmTable.containsKey(account)) {
                            return;
                        }
                        if (!pauseConfirmTable.get(account).equals(true)) {
                            broadcastPlayersMessage(new MPauseBroadcast(true, "玩家" + account + "拒绝了暂停请求", null, false));
                            pauseConfirmTable.clear();
                            timer.cancel();
                            timer=null;
                            return;
                        }
                    }
                    timer.cancel();
                    timer=null;
                    broadcastPlayersMessage(new MPauseBroadcast(true, "游戏得到同意,暂停", null, true));
                    roomState.setRoomStateType( RoomState.RoomStateType.Pause);
                    ServerDB.getInstance().trySetPlayerOnlineStates(players.keySet(),PlayerState.OnlineState.InRoom);
                    pauseConfirmTable.clear();
                    gameEngine.setPause(true);
                }
            }
        }
    }
    //根据结果更新玩家战绩
    //更新房间状态,玩家状态
    //广播
    @Override
    public synchronized void onResult(GameResult result) {
        System.out.println(result);
        broadcastToAllMessage(new MEnd(result));
        try{
            roomStateLock.lockInterruptibly();
            roomState.setRoomStateType(RoomState.RoomStateType.Free);
            //恢复
            for (String account : players.keySet()) {
                ServerDB.getInstance().trySetPlayerOnlineState(account, PlayerState.OnlineState.InRoom);
            }
            //记分
            ServerDB.getInstance().trySetPlayerWinLose(result.getWinner(),result.getLosers());
            //重新加入
            Hashtable<String,MsgThreadAsyn> oldViewers=viewers;
            viewers.clear();
            for(String account:oldViewers.keySet()){
                addPlayer(account,oldViewers.get(account));
            }
            //广播
            System.out.println(this+" Broadcast for Game Done");
            ServerBasicManager.getInstance().broadcast(new MUpdateRoomsReply(true, "OK", null, ServerRoomManager.getInstance().fetchAllRoomState()));
            ServerBasicManager.getInstance().broadcast(new MUpdatePlayersReply(true, "OK", null, ServerDB.getInstance().fetchAllPlayerState()));
            broadcastToAllMessage(
                    new MRoomStateBroadcast(
                    ServerDB.getInstance().queryPlayerStates(roomState.roomParticipants()),
                    gameEngine.getPlayerGameStates(),
                    roomState));
            System.out.println(this+" try to finish "+gameEngine);
            gameEngine.finish();
            gameEngine=null;
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally{
            roomStateLock.unlock();
        }
    }

    public void notifyBroadcastRoomState(){
        broadcastToAllMessage(new MRoomStateBroadcast(
                ServerDB.getInstance().queryPlayerStates(roomState.roomParticipants()),
                gameEngine.getPlayerGameStates(),
                ServerRoomHandler.this.roomState));
    }

    private boolean performDone=false;
    @Override
    protected void perform() {
        try {
            synchronized (this) {
                while (!performDone) {
                    wait();
                }
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
