package ClinetGUI.Universal;

import javax.swing.*;
import java.awt.*;

public class ColoredPanel extends JPanel {
    private final Color color;
    private final int radiusW,radiusH;
    public ColoredPanel(Color color,int radiusW,int radiusH){
        this.color=color;
        this.radiusW=radiusW;
        this.radiusH=radiusH;
    }
    @Override
    public void paint(Graphics g){
        g.setColor(color);
        g.fillRoundRect(0,0,getWidth(),getHeight(),radiusW,radiusH);
        super.paint(g);
    }
}
