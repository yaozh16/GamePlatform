package ClinetGUI.GameRoomPanel.RoomPanelComponents;

import ClientEngine.Configs.ClientConfigHolder;
import CommunicateControl.MsgThreadAsynHolder;
import GameState.GameResult.GameResult;
import GameState.GridObjects.Manager.ColorManager;
import GameState.Proxy.ColorProxy;
import Message.Common.MessageReply;
import Message.RoomMessage.MChat;
import Message.RoomMessage.MPauseBroadcast;
import Message.RoomMessage.MRoomBroadcast;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatPanel  extends JPanel {
    private final MsgThreadAsynHolder msgThreadAsynHolder;
    private final ClientConfigHolder clientConfigHolder;
    private JButton sendButton=new JButton("发送");
    private JButton clearButton=new JButton("清空");
    public ChatPanel(MsgThreadAsynHolder msgThreadAsynHolder,ClientConfigHolder clientConfigHolder){
        this.clientConfigHolder=clientConfigHolder;
        this.msgThreadAsynHolder=msgThreadAsynHolder;
        setLayout(new GridBagLayout());

        JScrollPane scrollPane=new JScrollPane(messagePane);
        add(scrollPane,
                new GridBagConstraints(0,0,1,1,1,5,10,GridBagConstraints.BOTH,
                        new Insets(0,0,0,0),0,0));

        add(inputField,
                new GridBagConstraints(0,5,1,1,1,0,10,GridBagConstraints.HORIZONTAL,
                        new Insets(0,0,0,0),0,0));

        JPanel buttonPanel=new JPanel(new GridLayout(1,0,20,20));
        sendButton.setForeground(new Color(0,255,0));
        clearButton.setForeground(new Color(255, 248, 17));
        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);
        add(buttonPanel,
                new GridBagConstraints(0,6,1,1,1,0,10,GridBagConstraints.HORIZONTAL,
                        new Insets(0,0,0,0),0,0));


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text=inputField.getText();
                if(text.isEmpty())
                    return;
                MChat mChat=new MChat(ChatPanel.this.clientConfigHolder.getClientConfig().getAccount(),ChatPanel.this.clientConfigHolder.getClientConfig().getValidateCode(),text,LocalDateTime.now());
                ChatPanel.this.msgThreadAsynHolder.toSendObj(mChat);
                inputField.setText("");
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (messagePane) {
                    try {
                        messagePane.getDocument().remove(0, messagePane.getDocument().getLength());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    ChatPanel.this.sendButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
    private JTextPane messagePane=new JIMSendTextPane();
    private JTextField inputField=new JTextField();

    private void appendText(String info,MutableAttributeSet keyAttr){
        synchronized (messagePane) {
            try {
                messagePane.getDocument().insertString(messagePane.getDocument().getLength(), info, keyAttr);
                messagePane.setCaretPosition(messagePane.getDocument().getLength());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void appendBroadcast(MRoomBroadcast mBroadcast){
        MutableAttributeSet keyAttr=new SimpleAttributeSet();
        StyleConstants.setForeground(keyAttr, new Color(92,200, 59));
        StyleConstants.setBold(keyAttr,true);
        appendText(mBroadcast.info+"\n",keyAttr);
    }
    public void appendChat(MChat mChat){
        MutableAttributeSet keyAttr=new SimpleAttributeSet();
        ColorProxy colorProxy=ColorManager.getInstance().getColor(clientConfigHolder.getClientConfig().getAccount());
        StyleConstants.setForeground(keyAttr, new Color(colorProxy.r,colorProxy.g,colorProxy.b,colorProxy.a));
        StyleConstants.setBold(keyAttr,false);
        appendText(mChat.account+"\n("+mChat.time.format(DateTimeFormatter.ISO_DATE_TIME)+"):\n",keyAttr);
        StyleConstants.setBold(keyAttr,true);
        appendText(mChat.info+"\n",keyAttr);
    }
    public void notifyResult(GameResult result){
        MutableAttributeSet keyAttr=new SimpleAttributeSet();
        StyleConstants.setForeground(keyAttr, new Color(253, 34, 7));
        StyleConstants.setBold(keyAttr,true);
        appendText(result.formatForLabel()+"\n",keyAttr);
    }
    public void appendPauseBroadcast(MPauseBroadcast mPauseBroadcast){
        MutableAttributeSet keyAttr=new SimpleAttributeSet();
        StyleConstants.setForeground(keyAttr, new Color(96, 178, 253));
        StyleConstants.setBold(keyAttr,true);
        appendText(mPauseBroadcast.info+"\n",keyAttr);
    }
    public void appendMessageReply(MessageReply reply){
        MutableAttributeSet keyAttr=new SimpleAttributeSet();
        StyleConstants.setForeground(keyAttr, new Color(157, 226, 15));
        StyleConstants.setBold(keyAttr,true);
        appendText(reply.info+"\n",keyAttr);
    }




    private class JIMSendTextPane extends JTextPane {

        // 内部类
        // 以下内部类全都用于实现自动强制折行

        private class WarpEditorKit extends StyledEditorKit {

            private ViewFactory defaultFactory = new WarpColumnFactory();

            @Override
            public ViewFactory getViewFactory() {
                return defaultFactory;
            }
        }

        private class WarpColumnFactory implements ViewFactory {

            public View create(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    if (kind.equals(AbstractDocument.ContentElementName)) {
                        return new WarpLabelView(elem);
                    } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                        return new ParagraphView(elem);
                    } else if (kind.equals(AbstractDocument.SectionElementName)) {
                        return new BoxView(elem, View.Y_AXIS);
                    } else if (kind.equals(StyleConstants.ComponentElementName)) {
                        return new ComponentView(elem);
                    } else if (kind.equals(StyleConstants.IconElementName)) {
                        return new IconView(elem);
                    }
                }

                // default to text display
                return new LabelView(elem);
            }
        }

        private class WarpLabelView extends LabelView {

            public WarpLabelView(Element elem) {
                super(elem);
            }

            @Override
            public float getMinimumSpan(int axis) {
                switch (axis) {
                    case View.X_AXIS:
                        return 0;
                    case View.Y_AXIS:
                        return super.getMinimumSpan(axis);
                    default:
                        throw new IllegalArgumentException("Invalid axis: " + axis);
                }
            }
        }

        // 本类

        // 构造函数
        public JIMSendTextPane() {
            super();
            this.setEditorKit(new WarpEditorKit());
        }
    }
}
