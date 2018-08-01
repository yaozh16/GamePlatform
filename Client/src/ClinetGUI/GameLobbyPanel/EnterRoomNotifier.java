package ClinetGUI.GameLobbyPanel;

import CommunicateControl.MsgThreadAsyn;

import javax.swing.*;
import java.util.concurrent.FutureTask;

public interface EnterRoomNotifier {
    public void enterRoom(MsgThreadAsyn msgThreadAsyn);
    public void frameUpdate(FutureTask<Void> task);
}
