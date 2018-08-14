package Message.RoomMessage;

import Message.Common.MessageRequest;

import java.time.LocalDateTime;

public class MChat extends MessageRequest {
    public String info;
    public LocalDateTime time;
    public MChat(String account,String validateCode,String info,LocalDateTime time) {
        super(account,validateCode);
        this.info=info;
        this.time=time;
    }
}
