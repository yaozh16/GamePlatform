package GameState.GridObjects;

import Direction.Direction;
import GameState.GridMap;
import GameState.GridObjects.Manager.ColorManager;

import java.awt.*;

public class GridHole implements GridMapObject {
    public int index;//坐标
    public GridHole connected=null;//当前洞连接的洞
    public Direction occupyFromDirection=null;
    public String account=null;
    private String id;
    @Override
    public void finish() {
        connected=null;
        occupyFromDirection=null;
    }

    @Override
    public void draw(Graphics g, int x, int y, int GridWidth, int GridHeight,String myAccount,int flashControl) {
        g.setColor(ColorManager.getInstance().getColor(id));
        g.fillRect(x*GridWidth,y*GridHeight,GridWidth,GridHeight);
        if(occupyFromDirection!=null&&account!=null){
            g.setColor(ColorManager.getInstance().getColor(account));
            switch (occupyFromDirection){
                case LEFT:
                    g.fillRoundRect(((int)(x * GridWidth)), ((int)(y * GridHeight+GridHeight*0.2)), ((int)(GridWidth*0.5)), ((int)(GridHeight*0.6)),GridWidth,GridHeight);
                    break;
                case RIGHT:
                    g.fillRoundRect(((int)(x * GridWidth+GridWidth*0.5)), ((int)(y * GridHeight+GridHeight*0.2)), ((int)(GridWidth*0.5)), ((int)(GridHeight*0.6)),GridWidth,GridHeight);
                    break;
                case DOWN:
                    g.fillRoundRect(((int)(x * GridWidth+GridWidth*0.2)), ((int)(y * GridHeight+GridHeight*0.5)), ((int)(GridWidth*0.6)), ((int)(GridHeight*0.5)),GridWidth,GridHeight);
                    break;
                case UP:
                    g.fillRoundRect(((int)(x * GridWidth+GridWidth*0.2)), ((int)(y * GridHeight)), ((int)(GridWidth*0.6)), ((int)(GridHeight*0.5)),GridWidth,GridHeight);
                    break;
            }
            g.fillArc(((int)(x*GridWidth+GridWidth*0.2)),((int)(y*GridHeight+GridHeight*0.2)),((int)(GridWidth*0.6)),((int)(GridHeight*0.6)),0,360);
        }
        if(connected.occupyFromDirection!=null&&connected.account!=null&&flashControl%4!=0){
            g.setColor(ColorManager.getInstance().getColor(connected.account));
            g.drawRect(x*GridWidth,y*GridHeight,GridWidth,GridHeight);
        }
    }
    private GridHole(int index,int id){
        this.index=index;this.id=String.format("HolePair%d",id);
    }
    private static int count=0;
    public static void  generateHolePair(int position1,int position2, GridMap gridMap){
        count++;
        GridHole hole1=new GridHole(position1,count);
        GridHole hole2=new GridHole(position2,count);
        hole1.connected=hole2;
        hole2.connected=hole1;
        gridMap.setGrid(hole1,position1);
        gridMap.setGrid(hole2,position2);
    }

    public boolean legal(){
        return this.occupyFromDirection==null&&connected.occupyFromDirection==null;
    }
    public void occupy(Direction occupyFromDirection,String account){
        this.occupyFromDirection=occupyFromDirection;
        this.account=account;
    }
    public String toString(){
        return "H";
    }
}
