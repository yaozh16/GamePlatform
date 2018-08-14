package Message.RoomMessage;

import Message.Common.MessageReply;

import java.net.InetAddress;

public class MConnect extends MessageReply {
    public InetAddress inetAddress;
    public int port;
    public MConnect(InetAddress inetAddress,int port) {
        super(true, "OK", null);
        this.inetAddress=inetAddress;
        this.port=port;
    }
}
