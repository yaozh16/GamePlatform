package Message.RoomMessage;

import BasicState.PlayerGameState;
import BasicState.PlayerState;
import BasicState.RoomState;
import Message.Common.MessageReply;

import java.util.Hashtable;

public class MRoomStateBroadcast extends MessageReply {
    public Hashtable<String, PlayerState> playerStates;//from serverDB
    public Hashtable<String, PlayerGameState> playerGameStates;
    public RoomState roomState;//from RoomHandler
    public MRoomStateBroadcast(Hashtable<String,PlayerState> playerStates,Hashtable<String,PlayerGameState> playerGameStates, RoomState roomState) {
        super(true, "OK", null);
        this.playerStates=playerStates;
        this.playerGameStates=playerGameStates;
        this.roomState=roomState.copy();
    }
}
