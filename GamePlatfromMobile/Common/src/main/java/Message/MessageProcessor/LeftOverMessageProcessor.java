package Message.MessageProcessor;

import Message.Common.Message;

public interface LeftOverMessageProcessor {
    public void onRecvObj(Message obj);
}
