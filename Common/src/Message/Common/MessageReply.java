package Message.Common;

public class MessageReply implements Message {
    public boolean OK;
    public String info;
    public String newValidateCode;
    public MessageReply(boolean OK,String info,String newValidateCode){
        this.OK=OK;
        this.info=info;
        this.newValidateCode=newValidateCode;
    }

}
