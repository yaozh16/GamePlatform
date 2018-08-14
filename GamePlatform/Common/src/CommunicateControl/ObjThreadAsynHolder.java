package CommunicateControl;

import java.net.Socket;

public interface ObjThreadAsynHolder<R,T> {
    void setUpObjThread(Socket socket);
    void onRecvObj(R obj);
    void toSendObj(T obj);
    void finish();
    void exit(ObjThreadAsyn src);
}
