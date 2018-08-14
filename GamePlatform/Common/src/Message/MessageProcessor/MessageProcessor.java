package Message.MessageProcessor;

import Message.Common.Message;

public abstract class MessageProcessor {
    public Class<? extends Message> processedMessage;
    public MessageProcessor(Class<? extends Message> processedMessage){
        this.processedMessage=processedMessage;
    }
    public abstract void process(Message message);
}
