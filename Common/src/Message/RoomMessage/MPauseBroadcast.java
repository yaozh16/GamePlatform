package Message.RoomMessage;

import Message.Common.MessageReply;

public class MPauseBroadcast extends MessageReply {
    public boolean pauseConfirmed;
    public MPauseBroadcast(boolean OK, String info, String newValidateCode,boolean pauseConfirmed) {
        super(OK, info, newValidateCode);
        this.pauseConfirmed=pauseConfirmed;
    }
}
