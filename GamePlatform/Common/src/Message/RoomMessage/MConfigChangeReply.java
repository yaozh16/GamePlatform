package Message.RoomMessage;

import GameState.GameConfig.GameConfig;
import Message.Common.MessageReply;

public class MConfigChangeReply extends MessageReply {
    private final GameConfig gameConfig;
    public MConfigChangeReply(boolean OK, String info, String newValidateCode,GameConfig gameConfig) {
        super(OK, info, newValidateCode);
        this.gameConfig=gameConfig.copy();
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }
}
