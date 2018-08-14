package ClinetGUI.GameLobbyPanel;

import BasicState.PlayerState;
import BasicState.RoomState;
import ClientEngine.Configs.ClientConfig;
import ClinetGUI.Universal.*;
import CommunicateControl.MsgThreadAsyn;
import CommunicateControl.MsgThreadAsynHolder;
import CommunicateControl.ObjThreadAsyn;
import GameState.GameConfig.RoomConfig;
import Message.Common.Message;
import Message.MessageProcessor.LeftOverMessageProcessor;
import Message.MessageProcessor.MessageProcessor;
import Message.MessageProcessor.MessageProcessorCollection;
import Message.RoomMessage.MRoomStateBroadcast;
import Message.UpdateMessage.MUpdatePlayers;
import Message.UpdateMessage.MUpdatePlayersReply;
import Message.UpdateMessage.MUpdateRooms;
import Message.UpdateMessage.MUpdateRoomsReply;
import Message.VisitorMessage.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class LobbyPanel extends JPanel implements MsgThreadAsynHolder,RoomConfigNotifier {
    private final EnterRoomNotifier enterRoomNotifier;
    private final LoginSuccessNotifier loginSuccessNotifier;
    private final ClientConfig clientConfig;
    private final RoomState roomState;
    private MsgThreadAsyn msgThreadAsyn;
    private final LeftOverMessageProcessor leftOverMessageProcessor;
    @Override
    public String toString(){
        return "Client.LobbyPanel";
    }

    @Override
    public void configDone(RoomConfig roomConfig) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                msgThreadAsyn.sendMsg(new MBuildRoom(clientConfig.getAccount(),clientConfig.getValidateCode(),roomConfig));
            }
        }).start();
    }


    private class GraphicRoomPanel extends JPanel{
        private int minButtonCount=12;
        private int lineButtonCount=4;
        class RoomButton extends JButton{
            public RoomButton(String text,ActionListener l){
                super(text);
                addActionListener(l);
            }
            public RoomButton(RoomState roomState){
                super(roomState.formatToHTML());
                addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                msgThreadAsyn.sendMsg(new MJoinRoom(clientConfig.getAccount(),clientConfig.getValidateCode(),roomState.getRoomConfig().roomName));
                            }
                        }).start();
                    }
                });
            }
            public RoomButton(){
                setEnabled(false);
            }
        }
        public JButton newRoomButton=new ColoredButton("创建新房间",new Color(36,223,0,255),20,20,new Color(255,247,244)) {
            {
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RoomConfigDialog roomConfigDialog = new RoomConfigDialog(LobbyPanel.this);
                                roomConfigDialog.run();
                            }
                        }).start();
                    }
                });
            }
        };
        public JButton updateButton=new ColoredButton("Update",new Color(45, 46, 223,200),20,20,new Color(255, 247, 244)) {
            {
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                requestUpdate();
                            }
                        }).start();
                    }
                });
            }
        };
        public void update(RoomState[] roomStates){
            int n=roomStates.length+1;
            setPreferredSize(new Dimension(0,150*(Math.max(n/lineButtonCount+1,minButtonCount/lineButtonCount+1))));
            setLayout(new GridLayout(0,lineButtonCount,10,10));
            removeAll();
            for(int i=0;i<roomStates.length;i++){
                add(new RoomButton(roomStates[i]));
            }
            add(newRoomButton);
            add(updateButton);
            for(int i=getComponentCount();i<minButtonCount;i++){
                add(new RoomButton());
            }
        }
        public GraphicRoomPanel(){
            update(new RoomState[]{});
        }
    }
    private class ListPlayerPanel extends JPanel {
        private class PlayerLabel extends JLabel{
            public PlayerLabel(PlayerState state){
                super(state.formatToHTML());
                setBorder(BorderFactory.createBevelBorder(0));
            }
            public PlayerLabel(){
                setBorder(BorderFactory.createBevelBorder(0));
            }
        }
        public synchronized void update(PlayerState[] playerStates){
            setLayout(new GridLayout(0,1));
            removeAll();
            for(int i=0;i<playerStates.length;i++){
                add(new PlayerLabel(playerStates[i]));
            }
            for(int i=playerStates.length;i<10;i++){
                add(new PlayerLabel());
            }
        }
        public ListPlayerPanel(){
            update(new PlayerState[]{});
        }
    }
    private GraphicRoomPanel graphicPanel=new GraphicRoomPanel();
    private ListPlayerPanel listPanel =new ListPlayerPanel();
    public LobbyPanel(EnterRoomNotifier enterRoomNotifier, LoginSuccessNotifier loginSuccessNotifier, ClientConfig clientConfig, RoomState roomState, LeftOverMessageProcessor leftOverMessageProcessor){
        super(new GridBagLayout());
        this.enterRoomNotifier=enterRoomNotifier;
        this.loginSuccessNotifier=loginSuccessNotifier;
        this.clientConfig=clientConfig;
        this.roomState=roomState;
        this.leftOverMessageProcessor=leftOverMessageProcessor;

        add(new JScrollPane(graphicPanel),
                new GridBagConstraints(0,0,5,1,5,1,10,GridBagConstraints.BOTH,
                        new Insets(0,0,0,0),0,0));

        add(new JScrollPane(listPanel),
                new GridBagConstraints(5,0,5,1,1,1,10,GridBagConstraints.BOTH,
                        new Insets(0,0,0,0),0,0));


    };
    public void requestUpdate(){
        System.out.println("request update");
        toSendObj(new MUpdatePlayers(clientConfig.getAccount(),clientConfig.getValidateCode()));
        toSendObj(new MUpdateRooms(clientConfig.getAccount(),clientConfig.getValidateCode()));
    }

    @Override
    public void setUpObjThread(Socket socket) {
        if(msgThreadAsyn!=null)
            msgThreadAsyn.finish();
        msgThreadAsyn=new MsgThreadAsyn(this,socket);
        msgThreadAsyn.start();
    }

    @Override
    public void onRecvObj(Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageProcessorCollection.processMessage(message);
            }
        }).start();
    }

    @Override
    public void toSendObj(Message message) {
        msgThreadAsyn.sendMsg(message);
    }

    @Override
    public void finish(){}

    @Override
    public void exit(ObjThreadAsyn src){
        loginSuccessNotifier.connectionLost();
    }


    MessageProcessorCollection messageProcessorCollection=new MessageProcessorCollection()
            .install(new MessageProcessor(MUpdatePlayersReply.class) {
                @Override
                public void process(Message message) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            MUpdatePlayersReply mUpdatePlayersReply=(MUpdatePlayersReply)message;
                            if(mUpdatePlayersReply.OK) {
                                listPanel.update(mUpdatePlayersReply.playerStates);
                                enterRoomNotifier.frameUpdate(null);
                            }
                            System.out.println("process MUpdatePlayersReply done");
                        }
                    });

                }
            })
            .install(new MessageProcessor(MUpdateRoomsReply.class) {
                @Override
                public void process(Message message) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            MUpdateRoomsReply mUpdateRoomsReply=(MUpdateRoomsReply)message;
                            if(mUpdateRoomsReply.OK){
                                graphicPanel.update(mUpdateRoomsReply.roomStates);
                                enterRoomNotifier.frameUpdate(null);
                            }
                            System.out.println("process MUpdateRoomsReply done");
                        }
                    });
                }
            })
            .install(new MessageProcessor(MBroadcast.class) {
                @Override
                public void process(Message message) {
                    MBroadcast mBroadcast=(MBroadcast)message;
                    System.out.println(mBroadcast.info);
                }
            })
            .install(new MessageProcessor(MBuildRoomReply.class) {
                @Override
                public void process(Message message) {
                    MBuildRoomReply mBuildRoomReply=(MBuildRoomReply)message;
                    if(mBuildRoomReply.OK) {
                        System.out.println(mBuildRoomReply.roomState);
                        roomState.copy(mBuildRoomReply.roomState);
                        enterRoomNotifier.enterRoom(msgThreadAsyn);
                    }else {
                        new MessageBox(mBuildRoomReply.info,null,false).run();
                    }
                }
            })
            .install(new MessageProcessor(MJoinRoomReply.class) {
                @Override
                public void process(Message message) {
                    MJoinRoomReply mJoinRoomReply=(MJoinRoomReply)message;
                    if(mJoinRoomReply.OK) {
                        System.out.println(mJoinRoomReply.roomState);
                        roomState.copy(mJoinRoomReply.roomState);
                        enterRoomNotifier.enterRoom(msgThreadAsyn);
                    }else {
                        new MessageBox(mJoinRoomReply.info,null,false).run();
                    }
                }
            })
            .install(new MessageProcessor(MRoomStateBroadcast.class) {
                @Override
                public void process(Message message) {
                    leftOverMessageProcessor.onRecvObj(message);
                }
            });
}
