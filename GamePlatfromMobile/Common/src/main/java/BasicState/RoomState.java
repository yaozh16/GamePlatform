package BasicState;

import GameState.GameConfig.RoomConfig;

import java.io.Serializable;
import java.util.HashSet;

public class RoomState implements Serializable {
    public HashSet<String> players=new HashSet<>();
    public HashSet<String> viewers=new HashSet<>();
    public HashSet<String> lefters=new HashSet<>();
    private final RoomConfig roomConfig;
    private RoomStateType roomStateType=RoomStateType.Free;
    public enum RoomStateType{Free,Game,Pause}

    public RoomState(HashSet<String> players,HashSet<String> viewers,HashSet<String> lefters,RoomConfig roomConfig){
        this.players = players;
        this.viewers=viewers;
        this.lefters=lefters;
        this.roomConfig=roomConfig;
    }
    public String formatToHTML(){
        String htmlText="<html>";
        htmlText+="<h2>Room:"+roomConfig.roomName+"</h2>";
        htmlText+="<br>Num:"+players.size()+"/"+roomConfig.getGameConfig().getMaxPlayer();
        htmlText+="<br>player:\t"+String.join("<br>player:\t",players);
        htmlText+="</html>";
        return htmlText;
    }
    public RoomState copy(){
        try {
            RoomState roomState=new RoomState(new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),roomConfig.copy());
            roomState.copy(this);
            return roomState;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    public void copy(RoomState roomState){
        players=new HashSet<>();
        players.addAll(roomState.players);
        viewers=new HashSet<>();
        viewers.addAll(roomState.viewers);
        lefters=new HashSet<>();
        lefters.addAll(roomState.lefters);
        roomConfig.copy(roomState.roomConfig);
        roomStateType=roomState.roomStateType;
    }

    public HashSet<String> getPlayers() {
        return players;
    }

    public synchronized void setPlayers(HashSet<String> players) {
        this.players = players;
    }

    public synchronized void setRoomConfig(RoomConfig roomConfig) {
        this.roomConfig.copy(roomConfig);
    }
    public HashSet<String> roomParticipants(){
        HashSet<String> hashSet=new HashSet<>();
        hashSet.addAll(players);
        hashSet.addAll(viewers);
        return hashSet;
    }

    public RoomConfig getRoomConfig() {
        return roomConfig;
    }

    public RoomStateType getRoomStateType() {
        return roomStateType;
    }

    public void setRoomStateType(RoomStateType roomStateType) {
        this.roomStateType = roomStateType;
    }
}
