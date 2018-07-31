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
    private int height=100;
    private int width=200;
    public GameResultReportMessageBox(GameResult gameResult){
        Vector<String> ranks=new Vector<>(gameResult.getScores().keySet());
        int M=1000;
        ranks.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (gameResult.getScores().get(o2).getScore()-gameResult.getScores().get(o1).getScore())*M
                        +(gameResult.getScores().get(o2).getLife()-gameResult.getScores().get(o1).getLife());
            }
        });
        setLayout(new GridLayout(0,1));
        for(int i=0;i<gameResult.getScores().keySet().size()&&i<5;i++){
            height+=100;
            PlayerGameState playerGameState=gameResult.getScores().get(ranks.get(i));
            add(new JLabel(String.format("%s:\nscore:%d\t life:%d",ranks.get(i),playerGameState.getScore(),playerGameState.getLife())));
        }
        JButton confirm=new JButton("确定");
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(confirm);
        setSize(width,height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    public void run(){
        setVisible(true);
    }
}
