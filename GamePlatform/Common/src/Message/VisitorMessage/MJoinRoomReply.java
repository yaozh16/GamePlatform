package Message.VisitorMessage;

import BasicState.RoomState;
import Message.Common.MessageReply;

public class MJoinRoomReply extends MessageReply {
    public RoomState roomState;
    public MJoinRoomReply(boolean OK, String info, String validateCode, RoomState roomState){
        super(OK,info,validateCode);
        this.roomState=roomState;
    }
}
