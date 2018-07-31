package Message.VisitorMessage;

import Message.Common.MessageReply;

public class MLoginReply extends MessageReply {
    public MLoginReply(boolean OK,String info,String validateCode){
        super(OK,info,validateCode);
    }
}
