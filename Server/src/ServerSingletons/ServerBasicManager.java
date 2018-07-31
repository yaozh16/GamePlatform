package ServerSingletons;

import Message.Common.Message;
import ServerHandler.ServerBasicHandler;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerBasicManager {
    private static ServerBasicManager ourInstance = new ServerBasicManager();

    public static ServerBasicManager getInstance() {
        return ourInstance;
    }

    private ServerBasicManager() {}

    private HashSet<ServerBasicHandler> registeredHandlers=new HashSet<>();
    private Lock registeredHandlerLock=new ReentrantLock();
    public void registerBasicHandler(ServerBasicHandler basicHandler){
        try {
            registeredHandlerLock.lock();
            registeredHandlers.add(basicHandler);
        }finally {
            registeredHandlerLock.unlock();
        }

    }
    public void cancelBasicHandler(ServerBasicHandler basicHandler){
        try {
            registeredHandlerLock.lock();
            registeredHandlers.remove(basicHandler);
        }finally {
            registeredHandlerLock.unlock();
        }

    }
    public void broadcast(Message message){
        try {
            registeredHandlerLock.lock();
            for(ServerBasicHandler serverBasicHandler:registeredHandlers){
                serverBasicHandler.toSendObj(message);
            }
        }finally {
            registeredHandlerLock.unlock();
        }
    }
    public void broadcastExcept(Message message,ServerBasicHandler exceptHandler){
        try {
            registeredHandlerLock.lock();
            for(ServerBasicHandler serverBasicHandler:registeredHandlers){
                if(serverBasicHandler.equals(exceptHandler))
                    continue;
                serverBasicHandler.toSendObj(message);
            }
        }finally {
            registeredHandlerLock.unlock();
        }
    }

}
