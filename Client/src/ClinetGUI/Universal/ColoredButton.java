package ClinetGUI.Universal;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;

public class ColoredButton extends JButton {
    private Color color;
    private final int radiusW,radiusH;
    public ColoredButton(String text,Color color,int radiusW,int radiusH,Color foreground){
        super(text);
        this.color=color;
        this.radiusW=radiusW;
        this.radiusH=radiusH;
        setForeground(foreground);
        //setFocusPainted(false);//除去焦点的框
        //setBorder(new BasicBorders.ButtonBorder(color.brighter(),color.brighter(),color.brighter(),color.brighter()));
        //setBorder(new BasicBorders.SplitPaneBorder(color.brighter(),color.brighter()));
        setBorder(new BasicBorders.FieldBorder(color.brighter(),color.brighter(),color.brighter(),color.brighter()));
        setContentAreaFilled(false);//除去默认的背景填充
        setMargin(new Insets(0,0,0,0));//将边框外的上下左右空间设置为0
        //setBorder(null);//除去边框
    }
    public void setColor(Color color){
        this.color=color;
    }
    @Override
    public void paint(Graphics g){
        g.setColor(color);
        g.fillRoundRect(0,0,getWidth()-1,getHeight()-1,radiusW,radiusH);
        super.paint(g);
    }
}
