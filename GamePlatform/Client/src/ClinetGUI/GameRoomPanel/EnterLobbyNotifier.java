package ClinetGUI.GameRoomPanel;

import javax.swing.*;
import java.util.concurrent.FutureTask;

public interface EnterLobbyNotifier {
    public void enterLobby();
    public void frameUpdate(FutureTask<Void> task);
}
