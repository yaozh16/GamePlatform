package CommunicateControl;

import Message.Common.Message;

import java.net.Socket;

public class MsgThreadAsyn extends ObjThreadAsyn<Message,Message> {
    public MsgThreadAsyn(CommunicateControl.ObjThreadAsynHolder ObjThreadAsynHolder, Socket socket) {
        super(ObjThreadAsynHolder, socket);
    }
}
