package ClinetGUI;

import BasicState.RoomState;
import ClientEngine.Configs.ClientConfig;
import ClinetGUI.GameLobbyPanel.EnterRoomNotifier;
import ClinetGUI.GameLobbyPanel.LobbyPanel;
import ClinetGUI.GameRoomPanel.EnterLobbyNotifier;
import ClinetGUI.GameRoomPanel.RoomPanel;
import ClinetGUI.Universal.LoginDialog;
import ClinetGUI.Universal.LoginSuccessNotifier;
import ClinetGUI.Universal.MessageBox;
import CommunicateControl.MsgThreadAsyn;
import GameState.GameConfig.GameConfig;
import GameState.GameConfig.GameType;
import GameState.GameConfig.RoomConfig;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.concurrent.Callable;
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
        setTitle("GamePlatform by yaozh16");
        roomPanel=new RoomPanel(this,this,clientConfig,roomState,null);
        lobbyPanel=new LobbyPanel(this,this,clientConfig,roomState,roomPanel);
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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800,600);
        setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setState(JFrame.ICONIFIED);
                loginDialog.run();
            }
        });
    }
    public void frameUpdate(FutureTask<Void> task){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame.this.setContentPane(MainFrame.this.getContentPane());
                MainFrame.this.setEnabled(true);
                if(task!=null){
                    task.run();
                }
            }
        });
    }

    @Override
    public void enterRoom(final MsgThreadAsyn msgThreadAsyn) {
        setTitle(roomState.getRoomConfig().roomName);
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
        setTitle("GamePlatform by yaozh16");
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
        System.out.println("loginSuccess normalize window");
        setState(JFrame.NORMAL);
        setTitle("GamePlatform by yaozh16");
        System.out.println("loginSuccessThread");
        enterLobby();
    }

    @Override
    public void connectionLost() {
        System.out.println("connection Lost minimize window");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setState(JFrame.ICONIFIED);
                new MessageBox("失去连接","确认",true).run();
                loginDialog.run();
            }
        });
    }
}
