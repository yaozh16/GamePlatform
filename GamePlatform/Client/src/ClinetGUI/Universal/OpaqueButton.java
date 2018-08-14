package ClinetGUI.Universal;

import javax.swing.*;
import java.awt.*;

public class OpaqueButton extends JButton {
    public OpaqueButton(Color foreground,String text){
        super(text);
        setForeground(foreground);
        setOpaque(false);
        setMargin(new Insets(0,0,0,0));//将边框外的上下左右空间设置为0
        setContentAreaFilled(false);
        setBorder(null);
    }
}
