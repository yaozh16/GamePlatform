package Message.VisitorMessage;

import GameState.GameConfig.RoomConfig;
import Message.Common.MessageRequest;

public class MBuildRoom extends MessageRequest {
    public RoomConfig roomConfig;
    public MBuildRoom(String account,String validateCode,RoomConfig roomConfig){
        super(account,validateCode);
        this.roomConfig=roomConfig;
    }
}
