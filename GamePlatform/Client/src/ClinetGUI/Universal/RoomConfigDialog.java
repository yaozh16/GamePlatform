package ClinetGUI.Universal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomConfigDialog extends JDialog {
    private RoomConfigNotifier roomConfigNotifier;
    private final RoomConfigPanel roomConfigPanel;

    public RoomConfigDialog(RoomConfigNotifier roomConfigNotifier){
        super();
        this.roomConfigNotifier=roomConfigNotifier;
        setModal(true);


        roomConfigPanel=new RoomConfigPanel(buildConfirmPanel(),10);
        add(roomConfigPanel);
    }
    private RoomConfigPanel getRoomConfigPanel(){
        return roomConfigPanel;
    }
    private JPanel buildConfirmPanel(){
        setTitle("建立房间设置");
        JPanel panel=new JPanel(new GridLayout(1,0));
        JButton button;
        button=new JButton("确定");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roomConfigNotifier.configDone(RoomConfigDialog.this.getRoomConfigPanel().getRoomConfig());
                System.out.println("Config Done");
                dispose();
            }
        });
        panel.add(button);
        button=new JButton("取消");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(button);
        return panel;
    }
    public void run(){
        setModal(true);
        setSize(400,400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
