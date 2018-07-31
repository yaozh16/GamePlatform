package ServerBase;

import ServerHandler.ServerBasicHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Singleton
public class ServerForground extends ServerThread{
    private static ServerForground ourInstance = new ServerForground(Executors.newCachedThreadPool());

    public static ServerForground getInstance() {
        return ourInstance;
    }

    private ServerForground(ExecutorService executorService) {
        super(null);
        this.executorService=executorService;
    }

    private int serverPort;
    private ExecutorService executorService;
    private ServerSocket serverSocket;


    public ServerForground setPort(int serverPort){
        this.serverPort=serverPort;
        return this;
    }

    @Override
    public void perform() {
        try {
            try {
                serverSocket = new ServerSocket(serverPort);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(this + " build serverSocket failed");
                return;
            }
            if (serverSocket.isClosed()) {
                return;
            }
            try {
                System.out.println("server start at "+serverSocket);
                while (!Thread.interrupted()) {
                    Socket socket = serverSocket.accept();
                    System.out.println("accept "+socket);
                    ServerBasicHandler handler=new ServerBasicHandler(socket);
                    System.out.println(handler+" accept "+socket);
                    handler.start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(this + " found running Exception");
            }
        }finally {
            executorService.shutdownNow();
        }
    }
    @Override
    public void interrupt(){
        super.interrupt();
        executorService.shutdownNow();
    }
}
