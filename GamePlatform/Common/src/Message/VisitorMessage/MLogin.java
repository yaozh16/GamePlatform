package Message.VisitorMessage;

import Message.Common.MessageRequest;

public class MLogin extends MessageRequest {
    public String password;
    public MLogin(String password,String account){
        super(account,null);
        this.password=password;
    }
}

