package Message.RoomMessage;

import GameState.GameResult.GameResult;
import Message.Common.MessageReply;

public class MEnd extends MessageReply {
    public GameResult gameResult;
    public MEnd(GameResult gameResult) {
        super(true, "finish", null);
        this.gameResult=gameResult;
    }
}
