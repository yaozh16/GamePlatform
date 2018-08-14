package ClinetGUI.Universal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColoredButton extends JButton {
    private Color color,enterColor;
    private final int radiusW,radiusH;
    private boolean entered=false;
    public ColoredButton(String text,Color color,int radiusW,int radiusH,Color foreground){
        this(text,color,radiusW,radiusH,foreground,3);
    }
    public ColoredButton(String text,Color color,int radiusW,int radiusH,Color foreground,int padding){
        super(text);
        setBackground(color);
        this.radiusW=radiusW;
        this.radiusH=radiusH;
        setForeground(foreground);
        setFocusPainted(false);//除去焦点的框
        setContentAreaFilled(false);//除去默认的背景填充
        setMargin(new Insets(2,0,2,0));//将边框外的上下左右空间设置为0
        setBorder(new EmptyBorder(padding,padding,padding,padding));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                entered=true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                entered=false;
            }
        });
    }
    @Override
    public void setBackground(Color color) {
        this.color = color;
        if (color != null) {
            Color tem = color.brighter();
            this.enterColor = new Color(tem.getRed(), tem.getGreen(), tem.getBlue(), Math.min(color.getAlpha() + 50, 255));
        }
    }
    @Override
    public void paint(Graphics g){
        if(!entered)
            g.setColor(color);
        else {
            g.setColor(enterColor);
        }
        g.fillRoundRect(0,0,getWidth(),getHeight(),radiusW,radiusH);
        g.setColor(color.darker());
        g.drawRoundRect(0,0,getWidth(),getHeight(),radiusW,radiusH);
        super.paint(g);
    }

}
