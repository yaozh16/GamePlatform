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
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainFrame extends JFrame implements EnterRoomNotifier,EnterLobbyNotifier,LoginSuccessNotifier,UpdateUINotifier {

    private ClientConfig clientConfig=new ClientConfig();
    private RoomState roomState=new RoomState(new HashSet<>(),new HashSet<>(),new HashSet<>(),new RoomConfig("",
            new GameConfig(2,20,20,20,20,5,20,GameType.GluttonousSnake)));
    private LobbyPanel lobbyPanel;
    private RoomPanel roomPanel;
    private LoginDialog loginDialog=new LoginDialog(this,clientConfig,this);

    private JPanel contentPane=new JPanel();
    private CardLayout cardLayout=new CardLayout();
    public MainFrame(){
        roomPanel=new RoomPanel(this,this,clientConfig,roomState,null);
        lobbyPanel=new LobbyPanel(this,this,clientConfig,roomState);
        contentPane.setLayout(cardLayout);
        contentPane.add(lobbyPanel,"lobbyPanel");
        contentPane.add(roomPanel,"roomPanel");

        this.getContentPane().add(contentPane);
    }
    public void run() {
        try {
            System.out.println("Try to set "+UIManager.getSystemLookAndFeelClassName());
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
    public synchronized void frameUpdate(FutureTask<Void> task){
        System.out.println("frameUpdate");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("frameUpdate done");
                MainFrame.this.setContentPane(MainFrame.this.getContentPane());
                MainFrame.this.setEnabled(true);
                if(task!=null){
                    task.run();
                }
            }
        });
    }

    @Override
    public synchronized void enterRoom(MsgThreadAsyn msgThreadAsyn) {
        cardLayout.show(contentPane,"roomPanel");
        frameUpdate(new FutureTask<Void>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                roomPanel.requestFocus();
                roomPanel.wakeUp(msgThreadAsyn);
                return null;
            }
        }));
    }
    @Override
    public synchronized void enterLobby() {
        System.out.println("enterLobby()");
        roomPanel.finish();
        cardLayout.show(contentPane,"lobbyPanel");
        frameUpdate(null);
        if(clientConfig.getValidateCode()!=null) {
            lobbyPanel.setUpObjThread(clientConfig.setUpSocket());
            lobbyPanel.requestUpdate();
        }
        System.out.println("enterLobby()Done");

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
