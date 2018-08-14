package CommunicateControl;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjThreadAsyn<R,T> {
    private static int count=0;
    private final int id=count++;

    @Override
    public String toString() {
        return String.format("%s(%d:%s)",getClass(),id,socket.toString());
    }

    private ObjThreadAsynHolder ObjThreadAsynHolder;
    private Socket socket;
    private boolean EOFEfinish=false;
    private boolean activeFinish=false;
    public ObjThreadAsyn(ObjThreadAsynHolder ObjThreadAsynHolder,Socket socket){
        this.ObjThreadAsynHolder=ObjThreadAsynHolder;
        this.socket=socket;
        readThread=new ObjThreadAsyn.ReadThread();
        writeThread=new ObjThreadAsyn.WriteThread();
    }

    public void setObjThreadAsynHolder(ObjThreadAsynHolder ObjThreadAsynHolder) {
        this.ObjThreadAsynHolder = ObjThreadAsynHolder;
        System.out.println(this+" switch holder to "+ObjThreadAsynHolder);
    }
    private volatile boolean readThreadExitWhile=false;
    //读取线程
    class ReadThread extends Thread{
        ReadThread(){}
        @Override
        public synchronized void run() {
            try {
                Object RObject;
                try {
                    RObject=new ObjectInputStream(socket.getInputStream()).readObject();
                    while (RObject != null) {
                        System.out.println(ObjThreadAsynHolder + " read " + RObject);
                        ObjThreadAsynHolder.onRecvObj((R)RObject);
                        RObject=new ObjectInputStream(socket.getInputStream()).readObject();
                    }
                } catch (EOFException e) {
                    System.out.println(ObjThreadAsynHolder+" get EOFE");
                    EOFEfinish=true;
                } catch (SocketException ex){
                    System.out.println(ObjThreadAsynHolder+" get SocketException");
                    EOFEfinish=true;
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }finally {
                System.out.println(ObjThreadAsynHolder+" ReadThread Done");
                readThreadExitWhile=true;
                ObjThreadAsyn.this.exit();
            }
        }
    }
    //写线程构造器
    class WriteThread extends Thread {
        private LinkedBlockingQueue<T> RLinkedBlockingQueue =new LinkedBlockingQueue<>();
        WriteThread(){}
        @Override
        public synchronized void run() {
            try{
                try {
                    while (!Thread.interrupted()){
                        T T = RLinkedBlockingQueue.take();
                        new ObjectOutputStream(socket.getOutputStream()).writeObject(T);
                    }
                }catch (InterruptedException ex){
                    if(!EOFEfinish&&!activeFinish)
                        ex.printStackTrace();
                }catch (IOException ex){
                    if(!EOFEfinish&&!activeFinish)
                        ex.printStackTrace();
                }
            }finally {
                System.out.println(ObjThreadAsynHolder+" WriteThread Done");
            }
        }
        public void send(final T msg){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RLinkedBlockingQueue.put(msg);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private ObjThreadAsyn.ReadThread readThread;
    private ObjThreadAsyn.WriteThread writeThread;


    public void sendMsg(T T){
        writeThread.send(T);
    }
    public boolean start(){
        if(ObjThreadAsynHolder!=null) {
            readThread.start();
            writeThread.start();
            return true;
        }else
            return false;
    }
    public void finish(){
        try {
            activeFinish=true;
            if(!readThreadExitWhile)
                readThread.interrupt();
            writeThread.interrupt();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    //工作出错，内部结束
    public void exit(){
        try{
            ObjThreadAsynHolder.exit(this);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public Socket getSocket() {
        return socket;
    }

}
