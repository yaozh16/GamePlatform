package ClinetGUI.GameRoomPanel.RoomPanelComponents;

import BasicState.PlayerGameState;
import BasicState.PlayerState;
import ClinetGUI.Universal.ColoredLabel;
import ClinetGUI.Universal.LocalColorManagerTransfer;
import ClinetGUI.UpdateUINotifier;
import GameState.GridObjects.Manager.ColorManager;
import Message.RoomMessage.MRoomStateBroadcast;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class ScorePanel  extends JPanel {
    private UpdateUINotifier updateUINotifier;
    private Hashtable<String,PlayerLabel> playerLabelHashtable=new Hashtable<>();
    public class PlayerLabel extends ColoredLabel {
        private JLabel playerOnlineStateLabel;
        private JLabel playerScoreLabel;
        private JLabel playerLifeLabel;
        public PlayerLabel(){
            super("",new Color(255, 247, 236),10,10,Color.WHITE,5);
            setBorder(BorderFactory.createBevelBorder(0));
            setEnabled(false);
        }
        public PlayerLabel(PlayerState playerState,PlayerGameState playerGameState){
            super(playerState.getAccount(),LocalColorManagerTransfer.transfer(ColorManager.getInstance().getColor(playerState.getAccount()).darker()),10,10,Color.WHITE,5);

            setLayout(new GridLayout(0,1,10,10));
            setPreferredSize(new Dimension(0,140));
            JLabel label=new JLabel(playerState.getAccount());
            label.setForeground(LocalColorManagerTransfer.transfer(ColorManager.getInstance().getColor(playerState.getAccount())));
            add(label);
            label=new JLabel("Win/Lost:"+playerState.getWin()+"/"+playerState.getLost());
            label.setForeground(LocalColorManagerTransfer.transfer(ColorManager.getInstance().getColor(playerState.getAccount())));
            add(label);
            new Color(1,2,3).darker();
            playerOnlineStateLabel=new ColoredLabel("State:"+playerState.getOnlineState(),
                    LocalColorManagerTransfer.transfer( ColorManager.getInstance().getColor(playerState.getAccount()).darker()),10,10,Color.WHITE);
            playerLifeLabel=new ColoredLabel("Life:"+String.valueOf(playerGameState.getLife()),
                    LocalColorManagerTransfer.transfer(ColorManager.getInstance().getColor(playerState.getAccount()).darker()),10,10,new Color(255, 234, 167));
            playerScoreLabel=new ColoredLabel("Score:"+String.valueOf(playerGameState.getScore()),
                    LocalColorManagerTransfer.transfer(ColorManager.getInstance().getColor(playerState.getAccount())).darker(),10,10,new Color(255, 234, 167));
            add(playerOnlineStateLabel);
            add(playerLifeLabel);
            add(playerScoreLabel);
            setBorder(BorderFactory.createBevelBorder(0));
            setForeground(LocalColorManagerTransfer.transfer(ColorManager.getInstance().getColor(playerState.getAccount())));
        }

    }
    public ScorePanel(UpdateUINotifier updateUINotifier){
        this.updateUINotifier=updateUINotifier;
    }
    public synchronized void updateScorePanel(MRoomStateBroadcast mRoomStateBroadcast){
        if(!mRoomStateBroadcast.playerStates.keySet().equals(playerLabelHashtable.keySet())){
            doUpdate(mRoomStateBroadcast);
            return;
        }
        for(String account:mRoomStateBroadcast.roomState.players){
            if(!playerLabelHashtable.containsKey(account)){
                doUpdate(mRoomStateBroadcast);
            }else{
                PlayerLabel label=playerLabelHashtable.get(account);
                PlayerState.OnlineState state=mRoomStateBroadcast.playerStates.get(account).getOnlineState();
                PlayerGameState gameState=mRoomStateBroadcast.playerGameStates.get(account);
                label.playerOnlineStateLabel.setText("State:"+state);
                label.playerOnlineStateLabel.setForeground(LocalColorManagerTransfer.transfer(ColorManager.getInstance().getColor(state.toString())));
                label.playerLifeLabel.setText("Life:"+String.valueOf(gameState.getLife()));
                label.playerScoreLabel.setText("Score:"+String.valueOf(gameState.getScore()));
            }
        }
    }
    private synchronized void doUpdate(MRoomStateBroadcast mRoomStateBroadcast){
        MRoomStateBroadcast local=mRoomStateBroadcast;
        System.out.println("\033[1;33mupdate ScorePanel\033[0m");
        playerLabelHashtable.clear();
        removeAll();
        setLayout(new GridLayout(0,1));
        JPanel top=new JPanel(new GridLayout(0,1));
        for(String account:local.roomState.players){
            System.out.println(account+":Score:"+local.playerGameStates.get(account).getScore()+",Life:"+local.playerGameStates.get(account).getLife());
            playerLabelHashtable.put(account,new PlayerLabel(local.playerStates.get(account),local.playerGameStates.get(account)));
            top.add(playerLabelHashtable.get(account));
        }
        for(String account:local.roomState.viewers){
            top.add(new JLabel(local.playerStates.get(account).formatToHTML()));
        }
        for(int i=local.roomState.players.size();i<local.roomState.getRoomConfig().getGameConfig().getMaxPlayer();i++){
            top.add(new PlayerLabel());
        }
        add(new JScrollPane(top));
        System.out.println("\033[1;33mupdate ScorePanel Done\033[0m");
        updateUINotifier.frameUpdate(null);
    }
}
