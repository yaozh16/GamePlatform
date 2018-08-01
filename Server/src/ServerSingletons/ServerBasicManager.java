package ServerSingletons;

import Message.Common.Message;
import ServerHandler.ServerBasicHandler;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerBasicManager {
    private static ServerBasicManager ourInstance = new ServerBasicManager();

    public static ServerBasicManager getInstance() {
        return ourInstance;
    }

    private ServerBasicManager() {}

    private HashSet<ServerBasicHandler> registeredHandlers=new HashSet<>();
    private ReentrantReadWriteLock registeredHandlerLock=new ReentrantReadWriteLock();
    public void registerBasicHandler(ServerBasicHandler basicHandler){
        try {
            registeredHandlerLock.writeLock().lockInterruptibly();
            registeredHandlers.add(basicHandler);
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            registeredHandlerLock.writeLock().unlock();
        }

    }
    public void cancelBasicHandler(ServerBasicHandler basicHandler){
        try {
            registeredHandlerLock.writeLock().lockInterruptibly();
            registeredHandlers.remove(basicHandler);
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            registeredHandlerLock.writeLock().unlock();
        }

    }
    public void broadcast(Message message){
        try {
            registeredHandlerLock.readLock().lockInterruptibly();
            for(ServerBasicHandler serverBasicHandler:registeredHandlers){
                serverBasicHandler.toSendObj(message);
            }
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            registeredHandlerLock.readLock().unlock();
        }
    }
    public void broadcastExcept(Message message,ServerBasicHandler exceptHandler){
        try {
            registeredHandlerLock.readLock().lockInterruptibly();
            for(ServerBasicHandler serverBasicHandler:registeredHandlers){
                if(serverBasicHandler.equals(exceptHandler))
                    continue;
                serverBasicHandler.toSendObj(message);
            }
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            registeredHandlerLock.readLock().unlock();
        }
    }

}
