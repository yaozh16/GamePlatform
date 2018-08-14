package Message.UpdateMessage;

import BasicState.RoomState;
import Message.Common.MessageReply;

public class MUpdateRoomsReply extends MessageReply {
    public RoomState[] roomStates;
    public MUpdateRoomsReply(boolean OK, String info, String newValidateCode,RoomState[] roomStates) {
        super(OK, info, newValidateCode);
        this.roomStates=roomStates;
    }
}
