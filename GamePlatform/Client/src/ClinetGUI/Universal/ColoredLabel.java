package ClinetGUI.Universal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ColoredLabel extends JLabel {
    private Color color;
    private final int radiusW,radiusH;
    public ColoredLabel(String text,Color color,int radiusW,int radiusH,Color foreground,int padding){
        super(text,JLabel.CENTER);
        this.color=color;
        this.radiusW=radiusW;
        this.radiusH=radiusH;
        setBackground(color);
        //setOpaque(true);
        this.setForeground(foreground);
        setBorder(new EmptyBorder(padding,padding,padding,padding));//除去边框

    }
    public ColoredLabel(String text,Color color,int radiusW,int radiusH,Color foreground){
        super(text,JLabel.CENTER);
        this.color=color;
        this.radiusW=radiusW;
        this.radiusH=radiusH;
        setBackground(color);
        //setOpaque(true);
        this.setForeground(foreground);
        setBorder(new EmptyBorder(5,5,5,5));//除去边框
    }
    public ColoredLabel(String text,Color color,int radiusW,int radiusH,Color foreground,int padding,int fontSize){
        this(text,color,radiusW,radiusH,foreground,padding);
        Font f=getFont();
        setFont(new Font("方正行楷_GBK",f.getStyle(),fontSize));
    }
    @Override
    public void paint(Graphics g){
        if(color!=null)
            g.setColor(color);
        g.fillRoundRect(0,0,getWidth(),getHeight(),radiusW,radiusH);
        super.paint(g);
    }
    public void setColor(Color color){
        this.color=color;
        repaint();
    }
}
