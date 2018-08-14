package Message.RoomMessage;

import Direction.Direction;
import Message.Common.MessageRequest;

public class MDirection extends MessageRequest {
    private Direction direction;
    public MDirection(String account, Direction direction) {
        super(account, null);
        this.direction=direction;
    }
}
