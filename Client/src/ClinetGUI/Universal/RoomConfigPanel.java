package ClinetGUI.Universal;

import GameState.GameConfig.GameConfig;
import GameState.GameConfig.GameType;
import GameState.GameConfig.RoomConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RoomConfigPanel extends JPanel {
    private JTextField roomNameTextField =new JTextField();
    private class RoomConfigItemSpinnerPanel extends JPanel{
        private final JLabel textLabel;
        private final JSpinner valueSpinner;
        private final SpinnerNumberModel numberModel;
        public RoomConfigItemSpinnerPanel(String text, int value, int min, int max){
            super(new BorderLayout());
            textLabel=new ColoredLabel(text,new Color(255, 136, 29),25,15,new Color(255,255,255));
            valueSpinner=new JSpinner();

            numberModel=new SpinnerNumberModel(value,min,max,1);
            valueSpinner.setModel(numberModel);

            add(textLabel,BorderLayout.WEST);
            add(valueSpinner,BorderLayout.CENTER);
        }
        public void setAlive(boolean alive){
            valueSpinner.setEnabled(alive);
        }
        public int getValue(){
            return (Integer) numberModel.getValue();
        }
        public void setValue(int value){
            numberModel.setValue(value);
        }
    }

    private JComboBox<GameType> gameTypeJComboBox=new JComboBox<>();
    private RoomConfigItemSpinnerPanel maxPlayer=new RoomConfigItemSpinnerPanel(" maxPlayer ",2,1,4);
    private RoomConfigItemSpinnerPanel mapWidth=new RoomConfigItemSpinnerPanel(" width ",30,10,50);
    private RoomConfigItemSpinnerPanel mapHeight=new RoomConfigItemSpinnerPanel(" height ",30,10,50);
    private RoomConfigItemSpinnerPanel lifeCount=new RoomConfigItemSpinnerPanel(" Life ",3,1,15);
    private RoomConfigItemSpinnerPanel bonusCount=new RoomConfigItemSpinnerPanel(" Bonus ",5,1,40);
    private RoomConfigItemSpinnerPanel coreSpeed=new RoomConfigItemSpinnerPanel(" Speed ",3,1,5);
    private RoomConfigItemSpinnerPanel holePairCount=new RoomConfigItemSpinnerPanel(" HolePair ",3,0,10);

    public RoomConfigPanel(JPanel confirmPanel,int vspace){
        super();
        setLayout(new GridLayout(0,1,0,vspace));
        JPanel panel;

        panel=new JPanel(new BorderLayout());
        panel.add(new ColoredLabel(" 房间名字 ",new Color(255, 136, 29),20,10,new Color(255,255,255)),BorderLayout.WEST);
        panel.add(roomNameTextField);
        add(panel);

        add(maxPlayer);
        add(mapWidth);
        add(mapHeight);
        add(lifeCount);
        add(bonusCount);
        add(coreSpeed);
        add(holePairCount);

        panel=new JPanel(new BorderLayout());
        panel.add(new ColoredLabel(" 游戏类型 ",new Color(255, 136, 29),20,10,new Color(255,255,255)),BorderLayout.WEST);
        panel.add(gameTypeJComboBox);
        gameTypeJComboBox.addItem(GameType.GluttonousSnake);
        gameTypeJComboBox.addItem(GameType.TankBattle);
        add(panel);

        if(confirmPanel!=null){
            add(confirmPanel);
        }
    }
    public RoomConfig getRoomConfig(){
        return new RoomConfig(roomNameTextField.getText(),
                new GameConfig(
                        maxPlayer.getValue(),
                        mapWidth.getValue(),
                        mapHeight.getValue(),
                        lifeCount.getValue(),
                        bonusCount.getValue(),
                        coreSpeed.getValue(),
                        holePairCount.getValue(),
                        (GameType) gameTypeJComboBox.getSelectedItem()
                )
        );
    }
    public RoomConfigPanel setRoomConfig(RoomConfig roomConfig){
        roomNameTextField.setText(roomConfig.roomName);
        maxPlayer.setValue(roomConfig.getGameConfig().getMaxPlayer());
        mapWidth.setValue(roomConfig.getGameConfig().getGridWidth());
        mapHeight.setValue(roomConfig.getGameConfig().getGridHeight());
        lifeCount.setValue(roomConfig.getGameConfig().getLifeCount());
        bonusCount.setValue(roomConfig.getGameConfig().getBonusCount());
        coreSpeed.setValue(roomConfig.getGameConfig().getSpeed());
        holePairCount.setValue(roomConfig.getGameConfig().getHolePair());
        gameTypeJComboBox.setSelectedItem(roomConfig.getGameConfig().getGameType());
        return this;
    }
    public RoomConfigPanel onStart(){
        gameTypeJComboBox.setEnabled(false);
        maxPlayer.setAlive(false);
        mapWidth.setAlive(false);
        mapHeight.setAlive(false);
        lifeCount.setAlive(false);
        bonusCount.setAlive(false);
        holePairCount.setAlive(false);
        return this;
    }
    public RoomConfigPanel onInit(){
        gameTypeJComboBox.setEnabled(true);
        maxPlayer.setAlive(true);
        mapWidth.setAlive(true);
        mapHeight.setAlive(true);
        lifeCount.setAlive(true);
        bonusCount.setAlive(true);
        holePairCount.setAlive(true);
        return this;
    }
    public RoomConfigPanel setTitleEditable(boolean editable){
        roomNameTextField.setEditable(editable);
        return this;
    }

}
