package Message.RoomMessage;

import Message.Common.MessageReply;

public class MDismiss extends MessageReply {
    public MDismiss(boolean OK, String info, String newValidateCode) {
        super(OK, info, newValidateCode);
    }
}
