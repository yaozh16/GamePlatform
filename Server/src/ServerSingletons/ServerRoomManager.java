package ServerSingletons;

import CommunicateControl.MsgThreadAsyn;
import GameState.GameConfig.RoomConfig;
import BasicState.RoomState;
import ServerHandler.ServerRoomHandler;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerRoomManager {
    private static ServerRoomManager ourInstance = new ServerRoomManager();

    public static ServerRoomManager getInstance() {
        return ourInstance;
    }

    private ServerRoomManager() {}

    private Hashtable<String,ServerRoomHandler> roomStateStorage=new Hashtable<>();//roomName to roomState
    private Hashtable<String,String> roomPlayerStorage=new Hashtable<>();//account to roomName
    private Lock roomStateLock=new ReentrantLock();
    public String tryBuildRoomAndJoin(String account, RoomConfig roomConfig,MsgThreadAsyn msgThreadAsyn){
        try{
            roomStateLock.lockInterruptibly();
            if(roomStateStorage.containsKey(roomConfig.roomName)){
                return "已有此房间";
            }else {
                HashSet<String> players=new HashSet<>();
                players.add(account);
                roomStateStorage.put(roomConfig.roomName,new ServerRoomHandler(new RoomState(players,new HashSet<>(),new HashSet<>(),roomConfig),msgThreadAsyn,account));
                roomPlayerStorage.put(account,roomConfig.roomName);
                return null;
            }
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return "服务器中断";
        }finally {
            roomStateLock.unlock();
        }
    }
    public String tryJoinRoom(String account, String roomName, MsgThreadAsyn msgThreadAsyn){
        try{
            roomStateLock.lockInterruptibly();
            if(roomPlayerStorage.containsKey(account))
                return "玩家不能加入多个房间";
            if(!roomStateStorage.containsKey(roomName))
                return "不存在此房间";
            if(!roomStateStorage.get(roomName).addPlayer(account,msgThreadAsyn)){
                return "房间已满";
            };
            roomPlayerStorage.put(account,roomName);
            return null;
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return "服务器中断";
        }finally {
            roomStateLock.unlock();
        }
    }
    public String tryLeaveRoom(String account,String roomName){
        try{
            roomStateLock.lockInterruptibly();
            if(!roomPlayerStorage.containsKey(account))
                return "玩家未加入任何房间";
            if(!roomStateStorage.containsKey(roomName))
                return "不存在此房间";
            roomPlayerStorage.remove(account);
            return null;
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return "服务器中断";
        }finally {
            roomStateLock.unlock();
        }
    }
    public String tryDismissRoom(String roomName){
        System.out.println("try to dismiss \033[1;35m"+roomName+"\033[0m");
        try{
            roomStateLock.lockInterruptibly();
            if(!roomStateStorage.containsKey(roomName))
                return "不存在该房间名";
            if(roomStateStorage.get(roomName).getRoomState().players.isEmpty()){
                roomStateStorage.remove(roomName).finish();
                return null;
            }else {
                return "房间非空";
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
            return "中断出错";
        }finally {
            roomStateLock.unlock();
        }
    }
    public RoomState getRoomState(String roomName){
        try{
            roomStateLock.lockInterruptibly();
            return roomStateStorage.get(roomName).getRoomState();
        }catch (InterruptedException ex){
            return null;
        }finally {
            roomStateLock.unlock();
        }
    }
    public boolean checkPlayerFreeOfRoom(String account){
        try{
            roomStateLock.lockInterruptibly();
            if(roomPlayerStorage.containsKey(account)) {
                System.out.println(this+" found player in "+roomPlayerStorage.get(account));
                return false;
            }
            return true;
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }finally {
            roomStateLock.unlock();
        }
    }
    public RoomState[] fetchAllRoomState(){
        try{
            roomStateLock.lockInterruptibly();
            RoomState[] allRoomStates=new RoomState[roomStateStorage.size()];
            int index=0;
            for(String roomName:roomStateStorage.keySet()){
                allRoomStates[index++]=roomStateStorage.get(roomName).getRoomState();
            }
            return allRoomStates;
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return new RoomState[0];
        }finally {
            roomStateLock.unlock();
        }
    }

}
