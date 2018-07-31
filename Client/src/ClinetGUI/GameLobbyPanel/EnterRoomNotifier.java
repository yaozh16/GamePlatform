package ClinetGUI.GameLobbyPanel;

import CommunicateControl.MsgThreadAsyn;

import javax.swing.*;

public interface EnterRoomNotifier {
    public void enterRoom(MsgThreadAsyn msgThreadAsyn);
    public void frameUpdate(JComponent nextFocus);
}
