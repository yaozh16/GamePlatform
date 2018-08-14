package Message.MessageProcessor;

import Message.Common.Message;

import java.util.Hashtable;
import java.util.Vector;

public class MessageProcessorCollection {
    private Hashtable<Class<? extends Message>,Vector<MessageProcessor>> installedProcessors=new Hashtable<>();
    public MessageProcessorCollection install(MessageProcessor processor){
        if(!installedProcessors.containsKey(processor.processedMessage)){
            installedProcessors.put(processor.processedMessage,new Vector<MessageProcessor>());
        }
        try {
            installedProcessors.get(processor.processedMessage).addElement(processor);
        }finally {}
        return this;
    }
    public synchronized void processMessage(final Message message){
        if(!installedProcessors.containsKey(message.getClass())) {
            System.err.println("unable to process "+message.getClass().getName());
            return;
        }
        //System.out.println("processor number for "+message.getClass().getName()+":"+installedProcessors.get(message.getClass()).size());

        for(final MessageProcessor messageProcessor:installedProcessors.get(message.getClass())){
            if(messageProcessor.processedMessage.equals(message.getClass())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        messageProcessor.process(message);
                    }
                }).start();
            }else
                System.err.println(message+" "+messageProcessor);
        }
    }
    public String updateValidateCode(String account){
        return null;
    }
}
