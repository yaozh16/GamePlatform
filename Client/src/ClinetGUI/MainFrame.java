package ClinetGUI;

import CommunicateControl.MsgThreadAsyn;
import ClientEngine.Configs.ClientConfig;
import ClinetGUI.GameLobbyPanel.LobbyPanel;
import ClinetGUI.GameLobbyPanel.EnterRoomNotifier;
import ClinetGUI.GameRoomPanel.EnterLobbyNotifier;
import ClinetGUI.GameRoomPanel.RoomPanel;
import ClinetGUI.Universal.LoginDialog;
import ClinetGUI.Universal.LoginSuccessNotifier;
import BasicState.RoomState;
import GameState.GameConfig.GameConfig;
import GameState.GameConfig.GameType;
import GameState.GameConfig.RoomConfig;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.util.HashSet;

public class MainFrame extends JFrame implements EnterRoomNotifier,EnterLobbyNotifier,LoginSuccessNotifier,UpdateUINotifier {

    private ClientConfig clientConfig=new ClientConfig();
    private RoomState roomState=new RoomState(new HashSet<>(),new HashSet<>(),new HashSet<>(),new RoomConfig("",
            new GameConfig(2,20,20,20,20,5,20,GameType.GluttonousSnake)));
    private LobbyPanel lobbyPanel=null;
    private RoomPanel roomPanel=null;
    private LoginDialog loginDialog=new LoginDialog(this,clientConfig,this);

    public MainFrame(){

    }
    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        enterLobby();
        setMinimumSize(new Dimension(400,300));
        setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        loginDialog.run();
    }
    private JComponent lastFocus=null;
    @Override
    public synchronized void frameUpdate(JComponent nextFocus){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame.this.setContentPane(MainFrame.this.getContentPane());
                MainFrame.this.setEnabled(true);
                if(nextFocus!=null) {
                    lastFocus=nextFocus;
                }
                if(lastFocus!=null){
                    lastFocus.requestFocus();
                }
            }
        });
    }
    @Override
    public synchronized void frameUpdateSycn(JComponent nextFocus){
        MainFrame.this.setContentPane(MainFrame.this.getContentPane());
        MainFrame.this.setEnabled(true);
        MainFrame.this.setVisible(true);
        if(nextFocus!=null) {
            lastFocus=nextFocus;
        }
        if(lastFocus!=null){
            lastFocus.requestFocus();
        }
    }
    @Override
    public synchronized void enterRoom(MsgThreadAsyn msgThreadAsyn) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                roomPanel=new RoomPanel(MainFrame.this,MainFrame.this,clientConfig,roomState,msgThreadAsyn);
                if(lobbyPanel!=null) {
                    lobbyPanel.finish();
                    remove(lobbyPanel);
                }
                lobbyPanel=null;
                add(roomPanel);
                frameUpdate(roomPanel);
            }
        }).start();
    }
    @Override
    public synchronized void enterLobby() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("enterLobby()");
                if(roomPanel!=null) {
                    roomPanel.finish();
                    remove(roomPanel);
                }
                roomPanel=null;

                if(lobbyPanel==null){
                    lobbyPanel=new LobbyPanel(MainFrame.this,MainFrame.this,clientConfig,roomState);
                    add(lobbyPanel);
                }
                if(clientConfig.getValidateCode()!=null) {
                    Socket socket=clientConfig.setUpSocket();
                    if(socket!=null){
                        lobbyPanel.setUpObjThread(socket);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                lobbyPanel.requestUpdate();
                            }
                        }).start();
                    }else {
                        connectionLost();
                    }
                }
                frameUpdate(lobbyPanel);
                System.out.println("enterLobby()Done");
            }
        }).start();

    }

    @Override
    public synchronized void loginSuccess() {
        System.out.println("loginSuccess");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("loginSuccessThread");
                enterLobby();
            }
        }).start();
    }

    @Override
    public void connectionLost() {
        loginDialog.run();
    }
}
