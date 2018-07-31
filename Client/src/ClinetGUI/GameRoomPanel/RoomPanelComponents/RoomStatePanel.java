package ClinetGUI.GameRoomPanel.RoomPanelComponents;

import BasicState.RoomState;
import ClientEngine.Configs.ClientConfigHolder;
import ClinetGUI.Universal.ColoredButton;
import ClinetGUI.Universal.RoomConfigPanel;
import CommunicateControl.MsgThreadAsynHolder;
import Message.RoomMessage.MConfigChange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomStatePanel extends JPanel {
    private final RoomState roomState;
    private final MsgThreadAsynHolder msgThreadAsynHolder;
    private final ClientConfigHolder clientConfigHolder;

    private final RoomState lastRoomState;
    private final RoomConfigPanel content;

    private JButton confirmButton=new ColoredButton("修改",new Color(2,123,34),10,10,Color.WHITE);
    public RoomStatePanel(RoomState roomState, MsgThreadAsynHolder msgThreadAsynHolder, ClientConfigHolder clientConfigHolder){
        this.roomState=roomState;
        this.msgThreadAsynHolder=msgThreadAsynHolder;
        this.clientConfigHolder=clientConfigHolder;
        this.lastRoomState=roomState.copy();
        setLayout(new BorderLayout());
        JPanel panel=new JPanel(new FlowLayout());
        panel.add(confirmButton);
        content=new RoomConfigPanel(null,1);
        content.setTitleEditable(false);
        add(content,BorderLayout.NORTH);
        add(panel,BorderLayout.CENTER);
        doUpdate();
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                msgThreadAsynHolder.toSendObj(new MConfigChange(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),content.getRoomConfig().getGameConfig()));
            }
        });
    }
    public synchronized void notifyRoomStateUpdated(){
        if(roomState.getRoomStateType().equals(RoomState.RoomStateType.Free)){
            System.out.println("\033[1;32mRoomStateInit\033[0m");
            content.onInit();
        }else {
            System.out.println("\033[1;32mRoomStateStart\033[0m");
            content.onStart();
        }
        if(!roomState.getRoomConfig().equals(lastRoomState.getRoomConfig())){
            doUpdate();
            lastRoomState.copy(roomState);
        }
    }
    public void doUpdate(){
        content.setRoomConfig(roomState.getRoomConfig());
    }
}
