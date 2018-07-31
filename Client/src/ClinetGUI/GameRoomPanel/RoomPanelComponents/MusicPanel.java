package ClinetGUI.GameRoomPanel.RoomPanelComponents;

import ClinetGUI.Universal.ColoredButton;
import ClinetGUI.Universal.ColoredLabel;
import ClinetGUI.Universal.MessageBox;
import ClinetGUI.Universal.ScrollDisplayLabel;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Hashtable;

public class MusicPanel extends JPanel{
    Player player=null;
    Thread playerThread=null;
    private ColoredButton startStopButton =new ColoredButton("音乐开始",new Color(8, 188, 42),10,10,Color.WHITE);
    private ColoredButton changeButton=new ColoredButton("音乐切换",new Color(6, 166, 188),10,10,Color.WHITE);

    private Hashtable<String,URL> musicPaths=new Hashtable<>();
    private JComboBox<String> musicFileNameComboBox=new JComboBox<>();
    private String selected=null;

    private ScrollDisplayLabel musicScrollLabel=new ScrollDisplayLabel(4,100);
    private ColoredButton chooseLocalFileButton=new ColoredButton("添加本地音乐",new Color(188, 35, 170),10,10,Color.WHITE);
    public MusicPanel(){
        setLayout(new GridLayout(0,1,10,10));
        initChoice();
        JPanel panel=new JPanel(new GridLayout(1,0,10,10));
        panel.add(startStopButton);
        panel.add(changeButton);
        add(panel);
        add(musicFileNameComboBox);
        add(musicScrollLabel);
        add(chooseLocalFileButton);

        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (MusicPanel.this) {
                    if(player==null) {
                        start();
                    }else {
                        finish();
                    }
                }
            }
        });
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (MusicPanel.this){
                    switchMusic();
                }
            }
        });
        musicFileNameComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (MusicPanel.this){
                    if(musicFileNameComboBox.getItemCount()==0){
                        return;
                    }
                    if(!((String)(musicFileNameComboBox.getSelectedItem())).equals(selected)){
                        selected=((String)(musicFileNameComboBox.getSelectedItem()));
                        finish();
                        start();
                    }
                }
            }
        });
        chooseLocalFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName=null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("音频文件(mp3)","mp3"));
                int rVal = fileChooser.showDialog(MusicPanel.this, "确定");
                if (rVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                try{
                    musicPaths.put(fileChooser.getSelectedFile().getName(),fileChooser.getSelectedFile().toURL());
                    musicFileNameComboBox.addItem(fileChooser.getSelectedFile().getName());
                    musicFileNameComboBox.setSelectedItem(fileChooser.getSelectedFile().getName());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }
    private void initChoice(){
        musicPaths.put("竹取飛翔 ～ Lunatic Princess - 上海アリス幻樂団.mp3",getClass().getResource((String) "/musics/竹取飛翔 ～ Lunatic Princess - 上海アリス幻樂団.mp3" ));
        musicPaths.put("月まで届け、不死の煙 - 上海アリス幻樂団.mp3",getClass().getResource((String) "/musics/月まで届け、不死の煙 - 上海アリス幻樂団.mp3" ));
        musicPaths.put("少女綺想曲 ～ Dream Battle - 上海アリス幻樂団.mp3",getClass().getResource((String) "/musics/少女綺想曲 ～ Dream Battle - 上海アリス幻樂団.mp3" ));
        musicPaths.put("RegaSound - 月の雫 (东方萃梦想).mp3",getClass().getResource("/musics/RegaSound - 月の雫 (东方萃梦想).mp3") );
        for(String music:musicPaths.keySet()){
            musicFileNameComboBox.addItem(music);
        }
        musicFileNameComboBox.setSelectedIndex(0);
        selected=(String) musicFileNameComboBox.getSelectedItem();
        musicScrollLabel.setText((String) musicFileNameComboBox.getSelectedItem());

    }
    private synchronized void switchMusic(){
        musicFileNameComboBox.setSelectedIndex((musicFileNameComboBox.getSelectedIndex()+1)%musicFileNameComboBox.getItemCount());
        finish();
        start();
    }
    private volatile boolean playFlag =false;
    public synchronized void finish(){
        if(player!=null){
            try{
                playFlag =false;
                System.out.println("close "+ player);
                player.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            playerThread=null;
            player=null;
            startStopButton.setColor(new Color(8, 188, 42));
            startStopButton.setText("音乐开始");
        }
        musicScrollLabel.pauseScroll();
    }
    public synchronized void start(){
        String fileName=null,filePath=null;
        if(player==null){
            try {
                fileName=(String) musicFileNameComboBox.getSelectedItem();
                filePath=URLDecoder.decode(musicPaths.get(fileName).getPath(),"UTF-8");
                System.out.println(filePath);
                player = new Player(new BufferedInputStream(new FileInputStream(filePath)));
            }catch (Exception ex){
                musicPaths.remove(fileName);
                musicFileNameComboBox.removeItem(fileName);
                if(musicFileNameComboBox.getItemCount()>0) {
                    try {
                        musicFileNameComboBox.setSelectedIndex(0);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new MessageBox(ex.getMessage(),"关闭",true).run();
                    }
                }).start();
            }
            if(player!=null){

                playFlag =true;
                playerThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Player LocalPlayer=player;
                            System.out.println("ready for "+ LocalPlayer);
                            LocalPlayer.play();
                            if(playFlag)
                                finish();
                            System.out.println("exit "+LocalPlayer);
                        }catch (Exception ex){
                            if(playFlag)
                                ex.printStackTrace();
                        }
                    }
                });
                playerThread.start();
                startStopButton.setColor(new Color(200,10,10));
                startStopButton.setText("停止音乐");
                musicScrollLabel.startScroll(fileName);
            }
        }
    }
}
