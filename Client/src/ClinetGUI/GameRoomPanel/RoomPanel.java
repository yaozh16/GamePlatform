package ClinetGUI.GameRoomPanel;

import ClientEngine.Configs.ClientConfigHolder;
import ClientEngine.Configs.GameControlConfig;
import ClientEngine.GameControler.ControlConfigChangeNotifier;
import ClinetGUI.Universal.*;
import ClinetGUI.GameRoomPanel.RoomPanelComponents.*;
import ClinetGUI.UpdateUINotifier;
import BasicState.PlayerState;
import CommunicateControl.MsgThreadAsyn;
import CommunicateControl.MsgThreadAsynHolder;
import ClientEngine.Configs.ClientConfig;
import BasicState.RoomState;
import CommunicateControl.ObjThreadAsyn;
import ClientEngine.CommonClientEngine;
import Message.Common.Message;
import Message.MessageProcessor.MessageProcessor;
import Message.MessageProcessor.MessageProcessorCollection;
import Message.RoomMessage.*;
import Message.VisitorMessage.MTouch;
import javafx.concurrent.Task;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class RoomPanel extends JPanel implements MsgThreadAsynHolder,EnterLobbyNotifier,UpdateUINotifier,ClientConfigHolder,ControlConfigChangeNotifier {
    private EnterLobbyNotifier enterLobbyNotifier;
    private LoginSuccessNotifier loginSuccessNotifier;
    private final ClientConfig clientConfig;
    private final RoomState roomState;
    private MsgThreadAsyn msgThreadAsyn;
    public RoomPanel(EnterLobbyNotifier enterLobbyNotifier, LoginSuccessNotifier loginSuccessNotifier, ClientConfig clientConfig, RoomState roomState,MsgThreadAsyn msgThreadAsyn){
        System.out.println("RoomPanel Build Start");
        this.enterLobbyNotifier=enterLobbyNotifier;
        this.loginSuccessNotifier=loginSuccessNotifier;
        this.clientConfig=clientConfig;
        this.roomState=roomState;
        initView();
    }

    private ChatPanel chatPanel;
    private GridDisplayPanel gridDisplayPanel;
    private OptionPanel optionPanel;
    private ScorePanel scorePanel;
    private MusicPanel musicPanel;
    private RoomStatePanel roomStatePanel;
    private final GameControlConfig gameControlConfig= new GameControlConfig();

    private JButton roomStateButton=new ColoredButton("房间信息",new Color(255, 161, 21),10,10,Color.WHITE);
    private JButton scoreButton=new ColoredButton("玩家信息",new Color(15, 27, 163),10,10,Color.WHITE);
    private JButton musicButton=new ColoredButton("其他选项",new Color(22, 248, 9),10,10,Color.WHITE);



    private JSplitPane leftPanel;
    private JSplitPane rightPanel;
    private JSplitPane splitPane;
    private void initView(){
        System.out.println("RoomPanel InitView Start");
        chatPanel=new ChatPanel(this,this);
        gridDisplayPanel=new GridDisplayPanel(null,this,clientConfig,gameControlConfig);
        optionPanel=new OptionPanel(gameControlConfig,this,this,this,this);
        scorePanel=new ScorePanel(this);
        roomStatePanel=new RoomStatePanel(roomState,this,this);
        musicPanel=new MusicPanel();


        Color back=new Color(255, 247, 236);
        JGroupPanel groupPanel=new JGroupPanel();
        groupPanel.insertGroup(0,"房间配置",back,roomStateButton);
        groupPanel.insertMember(0,0,roomStatePanel);
        groupPanel.insertGroup(1,"玩家(得分)信息",back,scoreButton);
        groupPanel.insertMember(1,0,scorePanel);
        groupPanel.insertGroup(2,"其他选项",back,musicButton);
        groupPanel.insertMember(2,0,musicPanel);
        groupPanel.expandGroup(2);

        leftPanel=new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,gridDisplayPanel,optionPanel);
        rightPanel=new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,groupPanel,chatPanel);
        splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,leftPanel,rightPanel);


        setLayout(new GridLayout(1,1));
        add(splitPane);
        splitPane.setVisible(true);

        System.out.println("RoomPanel Build Done");
    }



    @Deprecated
    @Override public void setUpObjThread(Socket socket) {}

    @Override
    public void onRecvObj(Message message) {
        messageProcessorCollection.processMessage(message);
    }

    @Override
    public void toSendObj(Message message) {
        if(msgThreadAsyn!=null)
            msgThreadAsyn.sendMsg(message);
    }

    private boolean afterFinish=false;
    public void  wakeUp(MsgThreadAsyn msgThreadAsyn){
        if(msgThreadAsyn!=null){
            msgThreadAsyn.setObjThreadAsynHolder(this);
        }
        afterFinish=false;
        leftPanel.setDividerLocation(0.8);
        rightPanel.setDividerLocation(0.7);
        splitPane.setDividerLocation(0.8);
        this.msgThreadAsyn = msgThreadAsyn;
        msgThreadAsyn.sendMsg(new MReady(clientConfig.getAccount(),clientConfig.getValidateCode(),false));
        System.out.println("RoomPanel wakeup split Done");

    }
    public void finish(){
        if(afterFinish)
            System.out.println("Finish Twice");
        afterFinish=true;
        if(msgThreadAsyn!=null) {
            msgThreadAsyn.finish();
            try {
                if (!msgThreadAsyn.getSocket().isClosed()) {
                    System.err.println("try release");
                    msgThreadAsyn.getSocket().close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        gridDisplayPanel.removeEngine();
        optionPanel.finish();
        musicPanel.finish();
    }

    @Override
    public void exit(ObjThreadAsyn src) {
        if(!afterFinish)
            loginSuccessNotifier.connectionLost();
    }

    @Override
    public void enterLobby() {
        enterLobbyNotifier.enterLobby();
    }

    @Override
    public void frameUpdate(FutureTask<Void> task) {
        enterLobbyNotifier.frameUpdate(new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                task.run();
                gridDisplayPanel.requestFocus();
                return null;
            }
        }));
    }

    private MessageProcessorCollection messageProcessorCollection=new MessageProcessorCollection()
            .install(new MessageProcessor(MRoomStateBroadcast.class) {
                @Override
                public void process(Message message) {
                    MRoomStateBroadcast mRoomStateBroadcast =(MRoomStateBroadcast)message;
                    roomState.copy(mRoomStateBroadcast.roomState);
                    scorePanel.updateScorePanel(mRoomStateBroadcast);
                    optionPanel.notifyReadyDone(mRoomStateBroadcast.playerStates.get(clientConfig.getAccount()).getOnlineState()==PlayerState.OnlineState.READY);
                    if(roomStatePanel!=null)
                        roomStatePanel.notifyRoomStateUpdated();

                }
            })
            .install(new MessageProcessor(MConnect.class) {
                @Override
                public void process(Message message) {
                    System.out.println("\033[1;32mGame Ready to Start\033[0m");
                    MConnect mConnect=(MConnect)message;
                    System.out.println("MConnect:\033[1;32m"+mConnect.inetAddress+","+mConnect.port+"\033[0m");
                    gridDisplayPanel.installClientEngine(new CommonClientEngine(mConnect,gridDisplayPanel,RoomPanel.this,gameControlConfig));
                }
            })
            .install(new MessageProcessor(MRoomBroadcast.class) {
                @Override
                public void process(Message message) {
                    chatPanel.appendBroadcast((MRoomBroadcast)message);
                    gridDisplayPanel.requestFocus();
                }
            })
            .install(new MessageProcessor(MChat.class) {
                @Override
                public void process(Message message) {
                    chatPanel.appendChat((MChat)message);
                }
            })
            .install(new MessageProcessor(MEnd.class) {
                @Override
                public void process(Message message) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MEnd mEnd=(MEnd)message;
                            System.out.println("MEnd remove Client Engine");
                            gridDisplayPanel.removeEngine();
                            chatPanel.notifyResult(mEnd.gameResult);
                            optionPanel.notifyEnd();
                            enterLobbyNotifier.frameUpdate(null);
                            System.out.println("MEnd Process Done");
                            new GameResultReportMessageBox(mEnd.gameResult).run();
                        }
                    }).start();
                }
            })
            .install(new MessageProcessor(MPauseBroadcast.class) {
                @Override
                public void process(Message message) {
                    MPauseBroadcast mPauseBroadcast=(MPauseBroadcast)message;
                    optionPanel.notifyPauseDone(mPauseBroadcast.pauseConfirmed);
                    chatPanel.appendPauseBroadcast(mPauseBroadcast);
                    enterLobbyNotifier.frameUpdate(null);
                }
            })
            .install(new MessageProcessor(MPause.class) {
                @Override
                public void process(Message message) {
                    MPause mPause=(MPause)message;
                    optionPanel.notifyPause(mPause);
                    enterLobbyNotifier.frameUpdate(null);
                }
            })
            .install(new MessageProcessor(MStart.class) {
                @Override
                public void process(Message message) {
                    scoreButton.doClick();
                    optionPanel.notifyStart();
                    roomStatePanel.notifyRoomStateUpdated();
                    gridDisplayPanel.requestFocus();
                }
            })
            .install(new MessageProcessor(MTouch.class) {
                @Override
                public void process(Message message) {
                    System.out.println("Touched");
                }
            })
            .install(new MessageProcessor(MConfigChangeReply.class) {
                @Override
                public void process(Message message) {
                    MConfigChangeReply mConfigChangeReply=(MConfigChangeReply)message;
                    chatPanel.appendMessageReply(mConfigChangeReply);
                    roomState.getRoomConfig().getGameConfig().copy(mConfigChangeReply.getGameConfig());
                    roomStatePanel.notifyRoomStateUpdated();
                }
            }).install(new MessageProcessor(MConfigChangeBroadcast.class) {
                @Override
                public void process(Message message) {
                    MConfigChangeBroadcast mConfigChangeBroadcast=(MConfigChangeBroadcast)message;
                    chatPanel.appendMessageReply(mConfigChangeBroadcast);
                    roomState.getRoomConfig().getGameConfig().copy(mConfigChangeBroadcast.getGameConfig());
                    roomStatePanel.notifyRoomStateUpdated();
                }
            })
            ;

    @Override
    public ClientConfig getClientConfig() {
        return clientConfig;
    }


    @Override
    public void notifyControlConfigChange(){
        gridDisplayPanel.notifyControlConfigChange();
    }
}
