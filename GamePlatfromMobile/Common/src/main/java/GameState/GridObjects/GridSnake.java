package GameState.GridObjects;

import Direction.Direction;
import GameState.GridObjects.Manager.ColorManager;
import GameState.Proxy.CanvasProxy;

public class GridSnake implements GridMapObject {
    public String account;
    public Direction from;//null if tail
    public Direction to;//null if head
    public boolean isDead =false;
    public boolean JustBorn=false;
    public GridSnake(Direction from,Direction to,String account){
        this.account=account;
        this.from=from;
        this.to=to;
    }

    @Override
    public void finish() {

    }

    @Override
    public void draw(CanvasProxy g, int x, int y, int GridWidth, int GridHeight, String myAccount, int flashControl) {
        if(myAccount.equals(account)){
            if(isDead&&flashControl%2==0)
                return;
            if(JustBorn&&flashControl%4>1)
                return;
        }
        g.setColor(ColorManager.getInstance().getColor(account));
        if(from==null){//尾巴
            int[] Xs,Ys;
            if(to.dx()==0){
                Xs=new int[]{((int)(x * GridWidth+GridWidth*0.5)),((int)(x * GridWidth+GridWidth*0.3)),((int)(x * GridWidth+GridWidth*0.7))};
            }else if(to.dx()==1){
                Xs=new int[]{((int)(x * GridWidth+GridWidth*0.5)),((int)(x * GridWidth+GridWidth*1.0)),((int)(x * GridWidth+GridWidth*1.0))};
            }else {
                Xs=new int[]{((int)(x * GridWidth+GridWidth*0.5)),((int)(x * GridWidth+GridWidth*0.0)),((int)(x * GridWidth+GridWidth*0.0))};
            }
            if(to.dy()==0){
                Ys=new int[]{((int)(y * GridHeight+GridHeight*0.5)),((int)(y * GridHeight+GridHeight*0.3)),((int)(y * GridHeight+GridHeight*0.7))};
            }else if(to.dy()==1){
                Ys=new int[]{((int)(y * GridHeight+GridHeight*0.5)),((int)(y * GridHeight+GridHeight*1.0)),((int)(y * GridHeight+GridHeight*1.0))};
            }else {
                Ys=new int[]{((int)(y * GridHeight+GridHeight*0.5)),((int)(y * GridHeight+GridHeight*0.0)),((int)(y * GridHeight+GridHeight*0.0))};
            }
            g.fillPolygon(Xs,Ys,3);
        }else if(to==null) {//头
            switch (from){
                case LEFT:
                    g.fillRect(((int)(x * GridWidth)), ((int)(y * GridHeight+GridHeight*0.3)), ((int)(GridWidth*0.8)), GridHeight-((int)(GridHeight*0.3))*2);
                    break;
                case RIGHT:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.2)), ((int)(y * GridHeight+GridHeight*0.3)), ((int)(GridWidth*0.8)), GridHeight-((int)(GridHeight*0.3))*2);
                    break;
                case DOWN:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.3)), ((int)(y * GridHeight+GridHeight*0.2)),GridWidth-((int)(GridWidth*0.3))*2, ((int)(GridHeight*0.8)));
                    break;
                case UP:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.3)), ((int)(y * GridHeight)), GridWidth-((int)(GridWidth*0.3))*2, ((int)(GridHeight*0.8)));
                    break;
            }
            g.fillRoundRect(x * GridWidth, y * GridHeight, GridWidth, GridHeight, ( GridWidth),  ( GridHeight));
        }else {
            switch (from){
                case LEFT:
                    g.fillRect(((int)(x * GridWidth)), ((int)(y * GridHeight+GridHeight*0.3)), ((int)(GridWidth*0.5)), GridHeight-((int)(GridHeight*0.3))*2);
                    break;
                case RIGHT:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.5)), ((int)(y * GridHeight+GridHeight*0.3)), (GridWidth-(int)(GridWidth*0.5)),GridHeight-((int)(GridHeight*0.3))*2);
                    break;
                case DOWN:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.3)), ((int)(y * GridHeight+GridHeight*0.5)), GridWidth-((int)(GridWidth*0.3))*2, (GridHeight-(int)(GridHeight*0.5)));
                    break;
                case UP:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.3)), ((int)(y * GridHeight)), GridWidth-((int)(GridWidth*0.3))*2, ((int)(GridHeight*0.5)));
                    break;
            }
            switch (to){
                case LEFT:
                    g.fillRect(((int)(x * GridWidth)), ((int)(y * GridHeight+GridHeight*0.3)), ((int)(GridWidth*0.5)), GridHeight-((int)(GridHeight*0.3))*2);
                    break;
                case RIGHT:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.5)), ((int)(y * GridHeight+GridHeight*0.3)), (GridWidth-(int)(GridWidth*0.5)), GridHeight-((int)(GridHeight*0.3))*2);
                    break;
                case DOWN:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.3)), ((int)(y * GridHeight+GridHeight*0.5)), GridWidth-((int)(GridWidth*0.3))*2, (GridHeight-(int)(GridHeight*0.5)));
                    break;
                case UP:
                    g.fillRect(((int)(x * GridWidth+GridWidth*0.3)), ((int)(y * GridHeight)),GridWidth-((int)(GridWidth*0.3))*2, ((int)(GridHeight*0.5)));
                    break;
            }
            g.fillArc(((int)(x * GridWidth+GridWidth*0.3)), ((int)(y * GridHeight+GridHeight*0.3)), GridWidth-((int)(GridWidth*0.3))*2,GridHeight-((int)(GridHeight*0.3))*2,0,360);
        }
    }
    public String toString(){
        return "S";
    }

    public void markDead(){
        isDead =true;
    }
    public void setJustBorn(boolean justBorn){
        this.JustBorn=justBorn;
    }
}
