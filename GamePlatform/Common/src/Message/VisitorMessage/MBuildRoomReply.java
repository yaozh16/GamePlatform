package Message.VisitorMessage;

import BasicState.RoomState;
import Message.Common.MessageReply;

public class MBuildRoomReply  extends MessageReply {
    public RoomState roomState;
    public MBuildRoomReply(boolean OK, String info, String validateCode,RoomState roomState){
        super(OK,info,validateCode);
        this.roomState=roomState;
    }
}
