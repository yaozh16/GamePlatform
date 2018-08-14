package GameState.GridObjects;

import GameState.Proxy.CanvasProxy;

import java.io.Serializable;

public interface GridMapObject extends Serializable {
    public final int flashControlMax=1024;
    public void finish();
    public void draw(CanvasProxy g, int x, int y, int GridWidth, int GridHeight, String myAccount, int flashControl);
}
