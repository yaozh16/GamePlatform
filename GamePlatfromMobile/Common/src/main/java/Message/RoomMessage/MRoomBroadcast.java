package Message.RoomMessage;

import Message.Common.MessageReply;

public class MRoomBroadcast extends MessageReply {
    public MRoomBroadcast(boolean OK, String info, String newValidateCode) {
        super(OK, info, null);
    }
}
