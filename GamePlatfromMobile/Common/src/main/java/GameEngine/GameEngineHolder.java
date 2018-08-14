package GameEngine;

import GameState.GameResult.GameResult;
import Message.Common.Message;

public interface GameEngineHolder {
    //Engine 运行结束
    public void onResult(GameResult result);
    public void broadcastPlayersMessage(Message message);
    public void broadcastToAllMessage(Message message);
    public void sendMessageToSinglePlayer(String account,Message message);

    public void notifyBroadcastRoomState();
}
