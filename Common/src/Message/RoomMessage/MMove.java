package Message.RoomMessage;

import Direction.Direction;
import Message.Common.Message;

public class MMove implements Message {
    public final String account;
    public final Direction direction;
    public MMove(String account,Direction direction){
        this.account=account;
        this.direction=direction;
    }
}
