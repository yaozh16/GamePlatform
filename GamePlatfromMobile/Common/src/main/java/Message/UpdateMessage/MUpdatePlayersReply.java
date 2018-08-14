package Message.UpdateMessage;

import BasicState.PlayerState;
import Message.Common.MessageReply;

public class MUpdatePlayersReply extends MessageReply {
    public PlayerState[] playerStates;
    public MUpdatePlayersReply(boolean OK, String info, String newValidateCode, PlayerState[] playerStates) {
        super(OK, info, newValidateCode);
        this.playerStates=playerStates;
        for(PlayerState account:playerStates){
            System.out.println("State:"+account.getAccount()+":"+account.getLastLogin());
        }

    }
}
