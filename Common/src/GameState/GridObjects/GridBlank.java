package GameState.GridObjects;

import java.awt.*;

public class GridBlank implements GridMapObject {
    @Override
    public void finish() {

    }
    @Override
    public void draw(Graphics g, int x, int y, int GridWidth, int GridHeight,String myAccount,int flashControl) {

    }
    public String toString(){
        return "_";
    }
}
