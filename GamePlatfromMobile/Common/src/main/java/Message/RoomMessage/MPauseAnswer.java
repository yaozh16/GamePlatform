package Message.RoomMessage;

import Message.Common.MessageRequest;

public class MPauseAnswer extends MessageRequest {
    public boolean agree;
    public MPauseAnswer(String account, String validateCode,boolean agree) {
        super(account, validateCode);
        this.agree=agree;
    }
}
