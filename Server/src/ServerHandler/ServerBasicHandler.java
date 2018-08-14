package ServerHandler;

import BasicState.PlayerState;
import BasicState.RoomState;
import CommunicateControl.MsgThreadAsyn;
import CommunicateControl.MsgThreadAsynHolder;
import CommunicateControl.ObjThreadAsyn;
import GameState.GridObjects.Manager.ColorManager;
import Message.Common.Message;
import Message.Common.MessageRequest;
import Message.MessageProcessor.MessageProcessor;
import Message.MessageProcessor.MessageProcessorCollection;
import Message.UpdateMessage.MUpdatePlayers;
import Message.UpdateMessage.MUpdatePlayersReply;
import Message.UpdateMessage.MUpdateRooms;
import Message.UpdateMessage.MUpdateRoomsReply;
import Message.VisitorMessage.*;
import ServerBase.ServerThread;
import ServerSingletons.ServerBasicManager;
import ServerSingletons.ServerDB;
import ServerSingletons.ServerRoomManager;

import java.net.Socket;
import java.time.LocalDateTime;

public class ServerBasicHandler extends ServerThread implements MsgThreadAsynHolder {
    public ServerBasicHandler(Socket socket) {
        super(socket);
        setUpObjThread(socket);
    }
    boolean OK=false;
    private String Logined=null;

    public synchronized String getLogined() {
        return Logined;
    }

    public synchronized void setLogined(String logined) {
        Logined = logined;
    }

    public synchronized boolean validateMessageRequest(MessageRequest request){
        System.out.println(request.account+","+request.getClass());
        if(ServerDB.getInstance().validateCodeCheck(request.account,request.validateCode)){
            if(getLogined()==null){
                ServerDB.getInstance().login(request.account);
                System.err.println(this+" validateMessageRequest (loginCount->"+ServerDB.getInstance().queryPlayer(request.account).getLoginCount()+")");
            }else if(getLogined().equals(request.account)){
            }else {
                System.err.println("some one seem to switch account suddenly");
                ServerDB.getInstance().logout(getLogined());
                ServerDB.getInstance().login(request.account);
            }
            setLogined(request.account);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void perform(){
        try {
            ServerBasicManager.getInstance().registerBasicHandler(this);
            synchronized (this) {
                while (!OK) {
                    System.out.println(this+" to wait");
                    this.wait();
                }
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        ServerBasicManager.getInstance().cancelBasicHandler(this);
    }
    private synchronized void sendMessage(Message message){
        toSendObj(message);
    }
    private MessageProcessorCollection messageProcessorCollection=new MessageProcessorCollection()
            .install(new MessageProcessor(MSignup.class) {
                @Override
                public void process(Message message) {
                    System.out.println(message.getClass());
                    MSignup mSignUp=(MSignup)message;
                    for(String illegalPrefix:ColorManager.getInstance().illegalName()){
                        if(mSignUp.account.startsWith(illegalPrefix)){
                            sendMessage(new MSignupReply(false,"此用户名含有非法字符"));
                            return;
                        }
                    }
                    if(ServerDB.getInstance().signupAccount(mSignUp.account,mSignUp.password)){
                        sendMessage(new MSignupReply(true,"注册成功,请登录"));
                        ServerBasicManager.getInstance().broadcastExcept(new MUpdateRoomsReply(true,"OK",null,ServerRoomManager.getInstance().fetchAllRoomState()),ServerBasicHandler.this);
                        ServerBasicManager.getInstance().broadcastExcept(new MUpdatePlayersReply(true,"OK",null,ServerDB.getInstance().fetchAllPlayerState()),ServerBasicHandler.this);
                    }else {
                        sendMessage(new MSignupReply(false,"此用户名已被占用"));
                    }
                }
            })
            .install(new MessageProcessor(MLogin.class) {
                @Override
                public void process(Message message) {
                    MLogin mLogin=(MLogin)message;
                    if(ServerDB.getInstance().accountExistenceCheck(mLogin.account)) {
                        if (ServerDB.getInstance().accountPasswordCheck(mLogin.account, mLogin.password)) {
                            if(ServerDB.getInstance().queryPlayer(mLogin.account).getLoginCount()==0){
                                sendMessage(new MLoginReply(true, "登录成功", ServerDB.getInstance().generateValidateCode(mLogin.account)));
                                PlayerState playerState = ServerDB.getInstance().queryPlayer(mLogin.account);
                                playerState.setLastLogin(LocalDateTime.now());
                                ServerDB.getInstance().updatePlayer(playerState);
                                ServerDB.getInstance().printAll();
                            }else {
                                sendMessage(new MLoginReply(false,"已经有玩家登录在线,您不能以此帐号登录",null));
                            }
                        } else {
                            sendMessage(new MLoginReply(false, "密码错误", null));
                        }
                    }else {
                        sendMessage(new MLoginReply(false,"帐号不存在",null));
                    }
                }
            })
            .install(new MessageProcessor(MTouch.class) {
                @Override
                public void process(Message message) {
                    sendMessage(new MTouch());
                }
            })
            .install(new MessageProcessor(MUpdateRooms.class) {
                @Override
                public void process(Message message) {
                    if(!validateMessageRequest((MessageRequest)message)){
                    System.out.println(this+"验证码出错");
                    synchronized (this){
                        msgThreadAsyn.finish();
                        ServerBasicHandler.this.notifyAll();
                    }
                    return;
                }
                    MUpdateRooms mUpdateRooms=(MUpdateRooms)message;
                    if(ServerDB.getInstance().validateCodeCheck(mUpdateRooms.account,mUpdateRooms.validateCode)){
                        sendMessage(new MUpdateRoomsReply(true,"OK",messageProcessorCollection.updateValidateCode(mUpdateRooms.account),ServerRoomManager.getInstance().fetchAllRoomState()));
                    }else {
                        System.out.println(this+" found validateCode error");
                        interrupt();
                    }
                }
            })
            .install(new MessageProcessor(MUpdatePlayers.class) {
                @Override
                public void process(Message message) {
                    if(!validateMessageRequest((MessageRequest)message)){
                        System.out.println(this+"验证码出错");
                        synchronized (this){
                            msgThreadAsyn.finish();
                            ServerBasicHandler.this.notifyAll();
                        }
                        return;
                    }
                    MUpdatePlayers mUpdatePlayers=(MUpdatePlayers)message;
                    if(ServerDB.getInstance().validateCodeCheck(mUpdatePlayers.account,mUpdatePlayers.validateCode)){
                        sendMessage(new MUpdatePlayersReply(true,"OK",messageProcessorCollection.updateValidateCode(mUpdatePlayers.account),ServerDB.getInstance().fetchAllPlayerState()));
                    }else {
                        interrupt();
                    }
                }
            })
            .install(new MessageProcessor(MBuildRoom.class) {
                @Override
                public void process(Message message) {
                    if(!validateMessageRequest((MessageRequest)message)){
                        System.out.println(this+"验证码出错");
                        synchronized (this){
                            msgThreadAsyn.finish();
                            ServerBasicHandler.this.notifyAll();
                        }
                        return;
                    }
                    //建立房间逻辑：
                    // 查找是否已经在房间中,
                    // 尝试创建加入房间并将MsgThreadAsyn的holder转为房间线程,
                    // 当前线程退出
                    MBuildRoom mBuildRoom=(MBuildRoom)message;
                    System.out.println(ServerBasicHandler.this.toString()+"校验码正确!");
                    if(!ServerRoomManager.getInstance().checkPlayerFreeOfRoom(mBuildRoom.account)){
                        System.out.println(ServerBasicHandler.this+"玩家不能加入多个房间!");
                        sendMessage(new MBuildRoomReply(false,"玩家不能加入多个房间",messageProcessorCollection.updateValidateCode(mBuildRoom.account),null));
                    }else {
                        String error=ServerRoomManager.getInstance().tryBuildRoomAndJoin(mBuildRoom.account,mBuildRoom.roomConfig,msgThreadAsyn);
                        System.out.println("创建房间:"+error);
                        if(error==null){
                            System.out.println(ServerBasicHandler.this+"创建房间");
                            ServerDB.getInstance().trySetPlayerOnlineState(mBuildRoom.account,PlayerState.OnlineState.InRoom);
                            ServerBasicManager.getInstance().broadcastExcept(new MUpdateRoomsReply(true,"OK",null,ServerRoomManager.getInstance().fetchAllRoomState()),ServerBasicHandler.this);
                            ServerBasicManager.getInstance().broadcastExcept(new MUpdatePlayersReply(true,"OK",null,ServerDB.getInstance().fetchAllPlayerState()),ServerBasicHandler.this);
                            sendMessage(new MBuildRoomReply(true,"OK",
                                    messageProcessorCollection.updateValidateCode(mBuildRoom.account),
                                    ServerRoomManager.getInstance().getRoomState(mBuildRoom.roomConfig.roomName)));

                            System.out.println(ServerBasicHandler.this+"创建房间发送结束");
                            synchronized (ServerBasicHandler.this){
                                OK=true;
                                ServerBasicHandler.this.notifyAll();
                            }
                        }else {
                            System.out.println(ServerBasicHandler.this+"创建房间失败:"+error);
                            sendMessage(new MBuildRoomReply(false,error,
                                    messageProcessorCollection.updateValidateCode(mBuildRoom.account),
                                    null));
                            System.out.println(ServerBasicHandler.this+"创建房间失败发送结束");
                        }
                    }


                }
            })
            .install(new MessageProcessor(MJoinRoom.class) {
                @Override
                public void process(Message message) {
                    if(!validateMessageRequest((MessageRequest)message)){
                        System.out.println(this+"验证码出错");
                        synchronized (this){
                            msgThreadAsyn.finish();
                            ServerBasicHandler.this.notifyAll();
                        }
                        return;
                    }
                    MJoinRoom mJoinRoom=(MJoinRoom) message;
                    if(!ServerRoomManager.getInstance().checkPlayerFreeOfRoom(mJoinRoom.account)){
                        System.out.println(this+"玩家不能加入多个房间");
                        sendMessage(new MBuildRoomReply(false,"玩家不能加入多个房间",null,null));
                    }else {
                        String error;
                        RoomState roomState;
                        if(((error=ServerRoomManager.getInstance().tryJoinRoom(mJoinRoom.account,mJoinRoom.roomName,msgThreadAsyn))==null)
                                &&((roomState=ServerRoomManager.getInstance().getRoomState(mJoinRoom.roomName))!=null)){
                            ServerDB.getInstance().trySetPlayerOnlineState(mJoinRoom.account,PlayerState.OnlineState.InRoom);
                            ServerBasicManager.getInstance().broadcastExcept(new MUpdateRoomsReply(true,"OK",null,ServerRoomManager.getInstance().fetchAllRoomState()),ServerBasicHandler.this);
                            ServerBasicManager.getInstance().broadcastExcept(new MUpdatePlayersReply(true,"OK",null,ServerDB.getInstance().fetchAllPlayerState()),ServerBasicHandler.this);
                            sendMessage(new MJoinRoomReply(true,"OK",messageProcessorCollection.updateValidateCode(mJoinRoom.account),roomState));
                            synchronized (ServerBasicHandler.this) {
                                OK = true;
                                ServerBasicHandler.this.notifyAll();
                            }
                        }else {
                            System.out.println(this+" "+error);
                            sendMessage(new MJoinRoomReply(false,error,messageProcessorCollection.updateValidateCode(mJoinRoom.account),null));
                        }
                    }

                }
            });
    private MsgThreadAsyn msgThreadAsyn;
    @Override
    public void setUpObjThread(Socket socket) {
        if(msgThreadAsyn!=null)
            msgThreadAsyn.finish();
        msgThreadAsyn=new MsgThreadAsyn(this,socket);
        msgThreadAsyn.start();
    }

    @Override
    public void onRecvObj(Message message) {
        messageProcessorCollection.processMessage(message);
    }

    @Override
    public void toSendObj(Message message) {
        msgThreadAsyn.sendMsg(message);
    }

    @Override
    public void finish() {
        msgThreadAsyn.finish();
    }

    @Override
    public void exit(ObjThreadAsyn src) {
        msgThreadAsyn.finish();
        if(getLogined()!=null){
            ServerDB.getInstance().logout(getLogined());
            System.out.println(this+"found \033[1;33m"+getLogined()+"\033[0m left"+"(loginCount->"+ServerDB.getInstance().queryPlayer(getLogined()).getLoginCount()+")");
            setLogined(null);
        }
        synchronized (this) {
            OK = true;
            System.out.println(this+" to notifyAll");
            this.notifyAll();
        }
    }
}
