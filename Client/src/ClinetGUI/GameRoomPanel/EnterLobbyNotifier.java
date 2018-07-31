package ClinetGUI.GameRoomPanel;

import javax.swing.*;

public interface EnterLobbyNotifier {
    public void enterLobby();
    public void frameUpdate(JComponent nextFocus);
    public void frameUpdateSycn(JComponent nextFocus);
}
