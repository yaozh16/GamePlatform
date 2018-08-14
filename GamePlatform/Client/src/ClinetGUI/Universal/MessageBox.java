package ClinetGUI.Universal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageBox extends JDialog {

    private JButton confirmButton;
    public MessageBox(String info,String confirmButtonText,boolean modal){
        super();
        System.out.println("MessageBox for");
        setLayout(new GridLayout(0,1));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel bPanel=new JPanel(new FlowLayout());
        bPanel.add(new JLabel(info));
        add(bPanel);

        bPanel=new JPanel(new FlowLayout());
        if(confirmButtonText!=null) {
            bPanel.add((confirmButton = new JButton(confirmButtonText)));
        }else {
            bPanel.add((confirmButton = new JButton("确定")));
        }
        add(bPanel);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageBox.this.dispose();
            }
        });
        setModal(modal);
    }
    public void run(){
        setSize(400,200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
