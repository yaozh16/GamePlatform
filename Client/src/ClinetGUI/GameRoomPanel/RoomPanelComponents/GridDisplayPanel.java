package ClinetGUI.GameRoomPanel.RoomPanelComponents;


import ClientEngine.Configs.ClientConfig;
import ClientEngine.Configs.ClientConfigHolder;
import ClientEngine.Configs.GameControlConfig;
import ClientEngine.GameControler.ControlConfigChangeNotifier;
import ClinetGUI.GameRoomPanel.EnterLobbyNotifier;
import GameEngine.ClientEngine;
import GameEngine.ClientEngineHolder;
import GameState.GridMap;
import GameState.GridMapControl.GridMapReader;
import GameState.GridObjects.GridMapObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class GridDisplayPanel extends JPanel implements ClientEngineHolder,ControlConfigChangeNotifier {
    private EnterLobbyNotifier enterLobbyNotifier;
    private GridMapReader gridMapReader;
    private final ClientConfig clientConfig;
    private final GameControlConfig gameControlConfig;
    private boolean focused=false;

    public GridDisplayPanel(GridMapReader gridMapReader,EnterLobbyNotifier enterLobbyNotifier,ClientConfig clientConfig,GameControlConfig gameControlConfig) {
        this.gridMapReader = gridMapReader;
        this.enterLobbyNotifier=enterLobbyNotifier;
        this.clientConfig=clientConfig;
        this.gameControlConfig=gameControlConfig;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("\033[1;33m clicked\033[0m");
                GridDisplayPanel.this.requestFocus();
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused=true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                focused=false;
            }
        });
    }

    public void setGridMapReader(GridMapReader gridMapReader) {
        this.gridMapReader = gridMapReader;
    }

    private int flashControl=0;
    @Override
    public void paint(Graphics g) {
        try {
            if (gridMapReader != null) {
                GridMap localMap = gridMapReader.getGridMapCopy();
                if(localMap==null) {
                    //basic background
                    BufferedImage defaultBack = ImageIO.read(getClass().getResource("/images/01.jpg"));
                    g.drawImage(defaultBack.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, this);
                }else{
                    Image buffer = createImage(getWidth(), getHeight());
                    Graphics bufferedGraphics = buffer.getGraphics();
                    int GridHeight = getHeight() / localMap.height;
                    int GridWidth = getWidth() / localMap.width;

                    if(focused)
                        bufferedGraphics.setColor(new Color(115, 255, 36));
                    else
                        bufferedGraphics.setColor(new Color(115, 255, 36).darker());
                    for (int y = 0; y < localMap.height; y++) {
                        for (int x = 0; x < localMap.width; x++) {
                            if (((x + y) % 2 == 0)) {
                            }
                            bufferedGraphics.fillRect(x * GridWidth, y * GridHeight, GridWidth, GridHeight);
                        }
                    }

                    if(focused)
                        bufferedGraphics.setColor(new Color(25, 148, 15));
                    else
                        bufferedGraphics.setColor(new Color(25, 148, 15).darker());
                    for (int y = 0; y < localMap.height; y++) {
                        for (int x = 0; x < localMap.width; x++) {
                            if (((x + y) % 2 != 0)) {
                                bufferedGraphics.fillRect(x * GridWidth, y * GridHeight, GridWidth, GridHeight);
                            }
                        }
                    }
                    for (int y = 0; y < localMap.height; y++) {
                        for (int x = 0; x < localMap.width; x++) {
                            localMap.gridMapObjects.get(x + y * localMap.width).draw(bufferedGraphics, x, y, GridWidth, GridHeight,clientConfig.getAccount(),flashControl);
                        }
                    }
                    bufferedGraphics.dispose();
                    g.drawImage(buffer, 0, 0, getWidth(), getHeight(), this);
                    flashControl=(flashControl+1)%GridMapObject.flashControlMax;
                }
            } else {
                //basic background
                BufferedImage defaultBack = ImageIO.read(getClass().getResource("/images/01.jpg"));
                g.drawImage(defaultBack.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ClientEngine clientEngine;
    private Timer timer;
    public void installClientEngine (ClientEngine clientEngine){
        System.out.println(this+" install clientEngine:"+clientEngine);
        this.clientEngine = clientEngine;
        this.clientEngine.start();
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        },100,100);
        GridDisplayPanel.this.requestFocus();

    }
    //MEnd
    //roomPanel.finish
    public synchronized void removeEngine(){
        if(clientEngine!=null) {
            clientEngine.finish();
            clientEngine=null;
            System.out.println("Client Remove Engine Done");
        };
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
    public void onLost(){
        System.out.println("On Lost连接失败");
        removeEngine();
        JDialog dialog=new JDialog();
        dialog.setSize(200,200);
        dialog.setModal(true);
        dialog.add(new JLabel("连接失败！"));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        enterLobbyNotifier.enterLobby();

    }

    @Override
    public void notifyControlConfigChange(){
        if(clientEngine!=null){
            clientEngine.notifyControlConfigChange();
        }
    }
}
