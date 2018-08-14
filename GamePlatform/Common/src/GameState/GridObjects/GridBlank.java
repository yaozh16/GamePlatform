package GameState.GridObjects;

import GameState.Proxy.CanvasProxy;

public class GridBlank implements GridMapObject {
    @Override
    public void finish() {

    }
    @Override
    public void draw(CanvasProxy g, int x, int y, int GridWidth, int GridHeight, String myAccount, int flashControl) {

    }
    public String toString(){
        return "_";
    }
}
