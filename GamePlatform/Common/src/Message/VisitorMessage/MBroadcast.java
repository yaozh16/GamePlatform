package Message.VisitorMessage;

import Message.Common.MessageReply;

public class MBroadcast extends MessageReply {
    public Object broadcastObject;
    public MBroadcast(boolean OK, String info, String validateCode, Object broadcastObject){
        super(OK,info,validateCode);
        this.broadcastObject=broadcastObject;
    }
}
