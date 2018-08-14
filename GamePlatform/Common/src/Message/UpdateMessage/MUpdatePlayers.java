package Message.UpdateMessage;

import BasicState.PlayerState;
import Message.Common.MessageRequest;

public class MUpdatePlayers extends MessageRequest {
    public PlayerState[] playerStates;
    public MUpdatePlayers(String account,String validateCode){
        super(account,validateCode);
    }
}
