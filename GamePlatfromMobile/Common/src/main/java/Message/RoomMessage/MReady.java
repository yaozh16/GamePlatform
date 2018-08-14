package Message.RoomMessage;

import Message.Common.MessageRequest;

public class MReady extends MessageRequest {
    public boolean ready;
    public MReady(String account, String validateCode,boolean ready) {
        super(account, validateCode);
        this.ready=ready;
    }
}
