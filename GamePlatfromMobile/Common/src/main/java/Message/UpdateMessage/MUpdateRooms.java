package Message.UpdateMessage;

import Message.Common.MessageRequest;

public class MUpdateRooms extends MessageRequest {
    public MUpdateRooms(String account,String validateCode){
        super(account,validateCode);
    }
}
