package GameState.GridObjects;

import GameState.GridObjects.Manager.ColorManager;
import GameState.Proxy.CanvasProxy;

public class GridBomb  implements GridMapObject{
    private final String sender;
    private int firePower;
    public GridBomb(int firePower,String sender){
        this.firePower=firePower;
        this.sender=sender;
    }
    boolean hit=false;
    public void markBeHit(){
        System.out.println("a bomb is hit");
        hit=true;
    }

    public boolean isHit() {
        return hit;
    }

    @Override
    public void finish() {

    }

    @Override
    public void draw(CanvasProxy g, int x, int y, int GridWidth, int GridHeight, String myAccount, int flashControl) {
        g.setColor(ColorManager.getInstance().getColor(sender+"_body"));
        g.fillArc(x*GridWidth+((int)(0.2*GridWidth)),y*GridHeight+((int)(0.2*GridHeight)),(int)(0.6*GridWidth),(int)(0.6*GridHeight),0,360);
    }
    public String toString(){
        return "b";
    }
}
