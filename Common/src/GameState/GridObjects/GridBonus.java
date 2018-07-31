package GameState.GridObjects;

import java.awt.*;

public class GridBonus implements GridMapObject {
    @Override
    public void finish() {

    }
    @Override
    public void draw(Graphics g, int x, int y, int GridWidth, int GridHeight,String myAccount,int flashControl) {
        g.setColor(Color.RED);
        int[] Xs=new int[]{((int)(GridWidth*x+GridWidth/2)),GridWidth*x+GridWidth            ,((int)(GridWidth*x+GridWidth/2)) ,GridWidth*x                      ,((int)(GridWidth*x+GridWidth/2))};
        int[] Ys=new int[]{GridHeight*y                     ,((int)(GridHeight*y+GridHeight/2)),GridHeight*y+GridHeight           ,((int)(GridHeight*y+GridHeight/2)),GridHeight*y                      };
        g.fillPolygon(Xs,Ys,          4);
    }
    public String toString(){
        return "B";
    }
}
