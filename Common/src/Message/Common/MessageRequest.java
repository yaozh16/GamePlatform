package Message.Common;



public class MessageRequest implements Message {
    public String account;
    public String validateCode;
    public MessageRequest(String account,String validateCode){
        this.account=account;
        this.validateCode=validateCode;
    }

}
