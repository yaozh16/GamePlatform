package Message.VisitorMessage;

import Message.Common.MessageRequest;

public class MSignup extends MessageRequest {
    public String password;
    public MSignup(String account, String password) {
        super(account, null);
        this.password=password;
    }
}
