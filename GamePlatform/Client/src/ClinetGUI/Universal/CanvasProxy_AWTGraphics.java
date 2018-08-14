package ClinetGUI.Universal;

import GameState.Proxy.CanvasProxy;
import GameState.Proxy.ColorProxy;

import java.awt.*;

public class CanvasProxy_AWTGraphics implements CanvasProxy {
    private final Graphics graphics;
    public CanvasProxy_AWTGraphics(Graphics graphics){
        this.graphics=graphics;
    }

    @Override
    public void setColor(ColorProxy color) {
        graphics.setColor(new Color(color.r,color.g,color.b,color.a));
    }

    @Override
    public void fillPolygon(int[] Xs, int[] Ys, int n) {
        graphics.fillPolygon(Xs,Ys,n);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        graphics.fillRect( x, y, width,  height);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {

        graphics.drawRect( x, y, width,  height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int rx, int ry) {

        graphics.fillRoundRect( x, y, width,  height,rx,ry);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {

        graphics.fillArc( x, y, width,  height,startAngle,arcAngle);
    }

    @Override
    public void drawLine(int x, int y, int nx, int ny) {
        graphics.drawLine(x,y,nx,ny);
    }
}
