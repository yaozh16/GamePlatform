package GameState.GridObjects;

import java.awt.*;

public class GridWall implements GridMapObject {

    @Override
    public void finish() {

    }

    @Override
    public void draw(Graphics g, int x, int y, int GridWidth, int GridHeight,String myAccount,int flashControl) {
        g.setColor(Color.YELLOW);
        g.fillRoundRect(((int)(x*GridWidth+GridWidth*0.1)),((int)(y*GridHeight+GridHeight*0.1)),((int)(GridWidth*0.8)),((int)(GridHeight*0.8)),((int) (0.2 * GridWidth)), ((int) (0.2 * GridHeight)));
    }
    public String toString(){
        return "W";
    }
}
