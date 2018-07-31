package ClinetGUI.Universal;

import javax.swing.*;
import java.awt.*;

public class ColoredLabel extends JLabel {
    private final Color color;
    private final int radiusW,radiusH;
    public ColoredLabel(String text,Color color,int radiusW,int radiusH,Color foreground){
        super(text);
        this.color=color;
        this.radiusW=radiusW;
        this.radiusH=radiusH;
        setBackground(color);
        //setOpaque(true);
        this.setForeground(foreground);
        setBorder(null);//除去边框
    }
    @Override
    public void paint(Graphics g){
        g.setColor(color);
        g.fillRoundRect(0,0,getWidth(),getHeight(),radiusW,radiusH);
        super.paint(g);
    }
}
