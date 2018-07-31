package Message.VisitorMessage;

import Message.Common.MessageReply;

public class MSignupReply extends MessageReply {
    public MSignupReply(boolean OK, String info) {
        super(OK, info, null);
    }
}
