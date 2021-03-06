package ServerSingletons;

import BasicState.RoomState;
import CommunicateControl.MsgThreadAsyn;
import GameState.GameConfig.RoomConfig;
import ServerHandler.ServerRoomHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerRoomManager {
    private static ServerRoomManager ourInstance = new ServerRoomManager();

    public static ServerRoomManager getInstance() {
        return ourInstance;
    }

    private ServerRoomManager() {}

    private HashMap<String,ServerRoomHandler> roomStateStorage=new HashMap<>();//roomName to roomState
    private HashMap<String,String> roomPlayerStorage=new HashMap<>();//account to roomName
    private ReentrantReadWriteLock roomStateLock=new ReentrantReadWriteLock();
    public String tryBuildRoomAndJoin(String account, RoomConfig roomConfig,MsgThreadAsyn msgThreadAsyn){
        try{
            roomStateLock.writeLock().lockInterruptibly();
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
            roomStateLock.writeLock().unlock();
        }
    }
    public String tryJoinRoom(String account, String roomName, MsgThreadAsyn msgThreadAsyn){
        try{
            roomStateLock.writeLock().lockInterruptibly();
            if(roomPlayerStorage.containsKey(account))
                return "玩家不能加入多个房间";
            if(!roomStateStorage.containsKey(roomName))
                return "不存在此房间";
            String error=roomStateStorage.get(roomName).addPlayer(account,msgThreadAsyn);
            if(error!=null){
                return error;
            };
            roomPlayerStorage.put(account,roomName);
            return null;
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return "服务器中断";
        }finally {
            roomStateLock.writeLock().unlock();
        }
    }
    public String tryLeaveRoom(String account,String roomName){
        try{
            roomStateLock.writeLock().lockInterruptibly();
            System.out.println(this+"tryleaveRoom("+account+","+roomName+")");
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
            roomStateLock.writeLock().unlock();
        }
    }
    public String tryDismissRoom(String roomName){
        System.out.println("try to dismiss \033[1;35m "+roomName+"\033[0m");
        try{
            roomStateLock.writeLock().lockInterruptibly();
            if(!roomStateStorage.containsKey(roomName))
                return "不存在该房间名";
            if(roomStateStorage.get(roomName).getRoomState().getPlayers().isEmpty()){
                roomStateStorage.remove(roomName).finish();
                return null;
            }else {
                return "房间非空:"+roomStateStorage.get(roomName).getRoomState().getPlayers().toString();
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
            return "中断出错";
        }finally {
            roomStateLock.writeLock().unlock();
        }
    }
    public RoomState getRoomState(String roomName){
        try{
            roomStateLock.readLock().lockInterruptibly();
            return roomStateStorage.get(roomName).getRoomState();
        }catch (InterruptedException ex){
            return null;
        }finally {
            roomStateLock.readLock().unlock();
        }
    }
    public boolean checkPlayerFreeOfRoom(String account){
        try{
            roomStateLock.readLock().lockInterruptibly();
            return !roomPlayerStorage.containsKey(account);
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }finally {
            roomStateLock.readLock().unlock();
        }
    }
    public RoomState[] fetchAllRoomState(){
        try{
            roomStateLock.readLock().lockInterruptibly();
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
            roomStateLock.readLock().unlock();
        }
    }

}
