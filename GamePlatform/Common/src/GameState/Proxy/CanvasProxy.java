package GameState.Proxy;

/**
 * Created by yaozh16 on 18-8-10.
 */

public interface CanvasProxy {
    public void setColor(ColorProxy color);
    public void fillPolygon(int[] Xs,int[] Ys,int n);
    public void fillRect(int x, int y, int width, int height);
    public void drawRect(int x, int y, int width, int height);
    public void fillRoundRect(int x, int y, int width, int height,int rx,int ry);
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);
    public void drawLine(int x,int y,int nx,int ny);
}
