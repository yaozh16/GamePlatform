package ClinetGUI.GameRoomPanel.RoomPanelComponents;

import ClientEngine.Configs.ClientConfigHolder;
import ClientEngine.Configs.GameControlConfig;
import ClientEngine.GameControler.ControlConfigChangeNotifier;
import ClientEngine.GameControler.GameControlerType;
import ClinetGUI.GameRoomPanel.EnterLobbyNotifier;
import CommunicateControl.MsgThreadAsynHolder;
import Message.RoomMessage.*;
import javafx.scene.media.AudioClip;
import javazoom.jl.player.Player;

import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class OptionPanel extends JPanel {
    private final GameControlConfig gameControlConfig;
    private final MsgThreadAsynHolder msgThreadAsynHolder;
    private final ClientConfigHolder clientConfigHolder;
    private final EnterLobbyNotifier enterLobbyNotifier;
    private final ControlConfigChangeNotifier controlConfigChangeNotifier;
    //与网络连接有关
    private class GamePanel extends JPanel{
        JButton leaveButton=new JButton("离开房间");
        JButton readyButton=new JButton("准备");
        JButton pauseButton=new JButton("请求暂停");
        JButton pauseAnswerButton_Yes=new JButton("同意暂停");
        JButton pauseAnswerButton_No =new JButton("拒绝暂停");
        JLabel msgDisplayBoard=new JLabel();
        {
            pauseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgThreadAsynHolder.toSendObj(new MPause(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode()));
                }
            });
            pauseAnswerButton_Yes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgThreadAsynHolder.toSendObj(new MPauseAnswer(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),true));
                    markPauseAnswered(true);;
                }
            });
            pauseAnswerButton_No .addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgThreadAsynHolder.toSendObj(new MPauseAnswer(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),false));
                    markPauseAnswered(false);;
                }
            });

            readyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgThreadAsynHolder.toSendObj(new MReady(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),true));
                }
            });
            leaveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgThreadAsynHolder.toSendObj(new MLeave(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode()));
                    enterLobbyNotifier.enterLobby();
                }
            });
        }
        private GamePanel(){
            markInit();
        }
        private void markInit(){
            super.removeAll();
            setLayout(new GridLayout(1,0,10,10));


            readyButton.setForeground(new Color(25, 250, 16));
            readyButton.setText("准备");
            add(readyButton);


            leaveButton.setForeground(new Color(255,0,0));
            add(leaveButton);
            enterLobbyNotifier.frameUpdate(null);
        }
        private void markReadyDone(boolean ready){
            if(ready){
                readyButton.setForeground(new Color(248, 183, 29));
                readyButton.setText("取消准备");
            }else {
                readyButton.setForeground(new Color(25, 250, 16));
                readyButton.setText("准备");
            }
        }
        private void markGame(){
            super.removeAll();
            setLayout(new GridLayout(1,0,10,10));

            pauseButton.setForeground(new Color(152, 77, 255));
            add(pauseButton);

            leaveButton.setForeground(new Color(255,0,0));
            add(leaveButton);

            enterLobbyNotifier.frameUpdate(null);
        }
        private void markPauseRequested(MPause mPause){
            removeAll();
            setLayout(new GridLayout(1,0,10,10));
            JPanel panel=new JPanel(new GridLayout(2,1));
            msgDisplayBoard.setText(mPause.account+"请求暂停 是否同意？");
            panel.add(msgDisplayBoard);
            JPanel selectionPanel=new JPanel(new GridLayout(1,0));
            pauseAnswerButton_No.setEnabled(true);
            pauseAnswerButton_Yes.setEnabled(true);
            pauseAnswerButton_No.setForeground(new Color(9, 14, 91));
            pauseAnswerButton_Yes.setForeground(new Color(255, 17, 127));
            selectionPanel.add(pauseAnswerButton_No);
            selectionPanel.add(pauseAnswerButton_Yes);
            panel.add(selectionPanel);
            add(panel);

            leaveButton.setForeground(Color.WHITE);
            leaveButton.setForeground(new Color(255,0,0));
            add(leaveButton);

            enterLobbyNotifier.frameUpdate(null);
        }
        private void markPauseAnswered(boolean agree){
            msgDisplayBoard.setText("已经"+(agree?"同意暂停":"拒绝暂停"));
            pauseAnswerButton_No.setForeground(new Color(9, 14, 91,127));
            pauseAnswerButton_Yes.setForeground(new Color(255, 17, 127,127));
            pauseAnswerButton_No.setEnabled(false);
            pauseAnswerButton_No.setEnabled(false);
        }
        private void markPauseConfirmed(){
            super.removeAll();
            setLayout(new GridLayout(1,0,10,10));


            readyButton.setForeground(new Color(25, 250, 16));
            readyButton.setText("继续(准备)");
            add(readyButton);


            leaveButton.setForeground(new Color(255,0,0));
            add(leaveButton);

            enterLobbyNotifier.frameUpdate(null);
        }
        private void markEnd(){
            super.removeAll();
            setLayout(new GridLayout(1,0,10,10));


            readyButton.setForeground(new Color(25, 250, 16));
            readyButton.setText("重新开始");
            add(readyButton);


            leaveButton.setForeground(new Color(255,0,0));
            add(leaveButton);
            enterLobbyNotifier.frameUpdate(null);
        };
    }
    //与本地配置有关
    private class ControlPanel extends JPanel{

        private JComboBox<GameControlerType> gameControlerTypeJComboBox=new JComboBox<>();

        public ControlPanel(){
            setLayout(new FlowLayout());

            add(new JLabel("游戏控制类型"));
            add(gameControlerTypeJComboBox);
            initOper();

        }
        private void initOper(){
            for(GameControlerType gameControlerType:GameControlerType.values()) {
                gameControlerTypeJComboBox.addItem(gameControlerType);
            }
            gameControlerTypeJComboBox.setSelectedItem(gameControlConfig.getGameControlerType());
            gameControlerTypeJComboBox.addActionListener(new ActionListener() {
                @Override
                public synchronized void actionPerformed(ActionEvent e) {
                    GameControlerType curValue=(GameControlerType)(gameControlerTypeJComboBox.getSelectedItem());
                    synchronized (gameControlConfig) {
                        if (curValue.equals(gameControlConfig.getGameControlerType())) {
                            return;
                        }else {
                            gameControlConfig.setGameControlerType(curValue);
                            controlConfigChangeNotifier.notifyControlConfigChange();
                            enterLobbyNotifier.frameUpdate(null);
                        }

                    }
                }
            });
        }
        public void finish(){};
    }
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
    public OptionPanel(
            GameControlConfig gameControlConfig,
            MsgThreadAsynHolder msgThreadAsynHolder,
            ClientConfigHolder clientConfigHolder,
            EnterLobbyNotifier enterLobbyNotifier,
            ControlConfigChangeNotifier controlConfigChangeNotifier){
        this.gameControlConfig=gameControlConfig;
        this.msgThreadAsynHolder=msgThreadAsynHolder;
        this.clientConfigHolder=clientConfigHolder;
        this.enterLobbyNotifier=enterLobbyNotifier;
        this.controlConfigChangeNotifier=controlConfigChangeNotifier;
        removeAll();
        setLayout(new GridLayout(1,0,10,10));
        gamePanel=new GamePanel();
        controlPanel=new ControlPanel();
        add(controlPanel);
        add(gamePanel);
        enterLobbyNotifier.frameUpdate(null);
        msgThreadAsynHolder.toSendObj(new MReady(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),false));
    }
    public void notifyReadyDone(boolean ready){
        gamePanel.markReadyDone(ready);
    }
    public void notifyPauseDone(boolean pause){
        if(!pause){
            gamePanel.markGame();//return
        }else {
            gamePanel.markPauseConfirmed();
        }
    }
    public void notifyStart(){
        gamePanel.markGame();
    }
    public void notifyPause(MPause mPause){
        gamePanel.markPauseRequested(mPause);
    }
    public void notifyEnd(){
        gamePanel.markEnd();
    }
    public void finish(){
        controlPanel.finish();
    }
}
