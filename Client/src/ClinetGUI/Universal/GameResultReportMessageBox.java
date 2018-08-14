package ClinetGUI.Universal;

import BasicState.PlayerGameState;
import GameState.GameResult.GameResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Vector;

public class GameResultReportMessageBox extends JDialog {
    private int height=400;
    private int width=200;
    public GameResultReportMessageBox(GameResult gameResult){
        setTitle("游戏结束");
        JPanel titlePanel=titlePanel();
        JPanel rankPanel=rankPanel(gameResult);
        JButton confirm=new JButton("确定"){
            {addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GameResultReportMessageBox.this.dispose();
                }
            });}
        };

        setLayout(new BorderLayout());
        add(titlePanel,BorderLayout.NORTH);
        add(rankPanel,BorderLayout.CENTER);
        add(confirm,BorderLayout.SOUTH);
        add(new JPanel(),BorderLayout.WEST);
        add(new JPanel(),BorderLayout.EAST);
        setSize(width,height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    private JPanel titlePanel(){
        JPanel titlePanel=new JPanel();
        titlePanel.add(new ColoredLabel("得分榜",new Color(12,1,234),10,10,Color.WHITE,5));
        return titlePanel;
    }
    private JPanel rankPanel(GameResult gameResult){
        JPanel rankPanel=new JPanel();
        rankPanel.setLayout(new GridLayout(0,1));
        Vector<String> ranks=new Vector<>(gameResult.getScores().keySet());
        int M=20;
        ranks.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (gameResult.getScores().get(o2).getScore()-gameResult.getScores().get(o1).getScore())*M
                        +(gameResult.getScores().get(o2).getLife()-gameResult.getScores().get(o1).getLife());
            }
        });
        for(int i=0;i<gameResult.getScores().keySet().size()&&i<5;i++){
            PlayerGameState playerGameState=gameResult.getScores().get(ranks.get(i));
            rankPanel.add(new JLabel(String.format("%s:\nscore:%d\t life:%d",ranks.get(i),playerGameState.getScore(),playerGameState.getLife())));
        }
        return rankPanel;
    }
    public void run(){
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
