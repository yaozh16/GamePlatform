package ServerBase;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Vector;

public abstract class ServerThread extends Thread implements Serializable {
    private static int count=0;
    private final int id=count++;
    private Socket socket;
    public ServerThread(Socket socket){
        this.socket=socket;
        System.out.println(this+" build up");
    }
    @Override
    public String toString(){
        return String.format("ServerBase.ServerThread(%s:%d)",getClass().getName(),id);
    }

    @Override
    public void run(){
        try{
            perform();
        }finally {
            System.out.println(this+ " exit run");
        }
    }

    protected abstract void perform();
}
