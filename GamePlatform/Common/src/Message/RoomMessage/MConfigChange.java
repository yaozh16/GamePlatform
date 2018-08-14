package Message.RoomMessage;

import GameState.GameConfig.GameConfig;
import Message.Common.MessageRequest;

public class MConfigChange extends MessageRequest {
    private final GameConfig gameConfig;
    public MConfigChange(String account, String validateCode, GameConfig gameConfig) {
        super(account, validateCode);
        this.gameConfig=gameConfig.copy();
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }
}
