package ClientEngine.Configs;

import Message.VisitorMessage.MLogin;
import Message.VisitorMessage.MLoginReply;
import Message.VisitorMessage.MSignup;
import Message.VisitorMessage.MSignupReply;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientConfig implements Serializable {
    private volatile String serverAddress;
    private volatile int serverPort;
    private volatile String account;
    private volatile String password;
    private volatile String validateCode;


    public synchronized Socket setUpSocket(){
        System.out.println("build new Socket");
        Socket socket=null;
        try{
            socket=new Socket(serverAddress,serverPort);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            return socket;
        }
    }


    public void setAccount(String account) {
        this.account = account;
    }
    public void setServerAddress(String serverAddress){
        this.serverAddress=serverAddress;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getServerAddress() {
        return serverAddress;
    }
    public int getServerPort() {
        return serverPort;
    }
    public String getAccount() {
        return account;
    }
    public String getPassword() {
        return password;
    }
    public String getValidateCode() {
        return validateCode;
    }
    private void cleanAll(){
        validateCode=null;
        this.serverAddress=null;
        this.serverPort=0;
        this.account=null;
        this.password=null;
    }
    public String login(String serverAddress, int serverPort, String account, String password){
        Socket socket=null;
        try {
            validateCode=null;
            this.serverAddress=serverAddress;
            this.serverPort=serverPort;
            this.account=account;
            this.password=password;
            socket=setUpSocket();
            if(socket==null){
                cleanAll();
                return "网络连接配置出错或者服务器未启动！";
            }
            new ObjectOutputStream(socket.getOutputStream()).writeObject(new MLogin(this.password,this.account));
            MLoginReply reply=(MLoginReply)(new ObjectInputStream(socket.getInputStream()).readObject());
            if(reply.OK){
                this.validateCode=reply.newValidateCode;
            }
            socket.close();
            return reply.info;
        }catch (Exception ex){
            ex.printStackTrace();
            if(socket!=null)
                try {socket.close();}catch (Exception exx){}
            cleanAll();
            return "登录失败！";
        }finally {
            System.out.println("connection login done");
        }
    }
    public String signup(String serverAddress, int serverPort, String account, String password){
        Socket socket=null;
        try {
            validateCode=null;
            this.serverAddress=serverAddress;
            this.serverPort=serverPort;
            this.account=account;
            this.password=password;
            socket=setUpSocket();
            if(socket==null){
                cleanAll();
                return "网络连接配置出错或者服务器未启动！";
            }
            new ObjectOutputStream(socket.getOutputStream()).writeObject(new MSignup(this.account,this.password));
            MSignupReply reply=(MSignupReply)(new ObjectInputStream(socket.getInputStream()).readObject());
            socket.close();
            return reply.info;
        }catch (Exception ex){
            ex.printStackTrace();
            if(socket!=null)
                try {socket.close();}catch (Exception exx){}
            cleanAll();
            return "注册失败！";
        }finally {
            System.out.println("connection signup done");
        }
    }
}