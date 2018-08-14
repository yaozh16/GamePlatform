package ClinetGUI.Universal;

import GameState.Proxy.ColorProxy;

import java.awt.*;

public class LocalColorManagerTransfer {
    public static Color transfer(ColorProxy colorProxy){
        return new Color(colorProxy.r,colorProxy.g,colorProxy.b,colorProxy.a);
    }
}
