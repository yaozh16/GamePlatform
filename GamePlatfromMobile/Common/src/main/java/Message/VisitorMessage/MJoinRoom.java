package Message.VisitorMessage;

import Message.Common.MessageRequest;

public class MJoinRoom  extends MessageRequest {
    public String roomName;
    public MJoinRoom(String account,String validateCode,String roomName){
        super(account,validateCode);
        this.roomName=roomName;
    }
}
