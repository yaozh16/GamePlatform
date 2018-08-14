package Message.RoomMessage;

import Message.Common.MessageReply;

public class MStart extends MessageReply {
    public int countDown;
    public MStart(boolean OK, String info, String newValidateCode,int countDown) {
        super(OK, info, newValidateCode);
        this.countDown=countDown;
    }
}
