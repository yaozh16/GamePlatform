package Message.RoomMessage;

import Message.Common.MessageRequest;

public class MLeave extends MessageRequest {
    public MLeave(String account, String validateCode) {
        super(account, validateCode);
    }
}
