package GameState.GameConfig;

import java.io.Serializable;

public class RoomConfig implements Serializable {
    public String roomName="";
    private final GameConfig gameConfig;

    public synchronized void copy(RoomConfig roomConfig){
        this.roomName=roomConfig.roomName;
        this.gameConfig.copy(roomConfig.gameConfig);
    }
    public synchronized RoomConfig copy(){
        return new RoomConfig(roomName,gameConfig);
    }
    public RoomConfig(String roomName,GameConfig gameConfig){
        this.roomName=roomName;
        this.gameConfig=gameConfig.copy();
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }
}
