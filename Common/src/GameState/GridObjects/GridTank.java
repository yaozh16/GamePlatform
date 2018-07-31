package GameState.GridObjects;

import Direction.Direction;
import GameState.GridObjects.Manager.ColorManager;

import java.awt.*;

public class GridTank implements GridMapObject {

    private final String account;
    private Direction direction;
    private boolean justBorn=false;
    private boolean isDead=false;
    public GridTank(String account, Direction direction){
        this.account=account;
        this.direction=direction;
    }
    public void setDirection(Direction direction){
        this.direction=direction;
    }

    public String getAccount(){
        return account;
    }
    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void setJustBorn(boolean justBorn) {
        this.justBorn = justBorn;
    }

    @Override
    public void finish() {

    }

    @Override
    public void draw(Graphics g, int x, int y, int GridWidth, int GridHeight, String myAccount, int flashControl) {
        if(justBorn||isDead){
            if(flashControl%2==0){
                return;
            }
        }
        g.setColor(ColorManager.getInstance().getColor(account+"_tank"));
        g.fillRect(x*GridWidth-((int) (0.5*GridWidth)),y*GridHeight-((int) (0.5*GridHeight)),GridWidth+2*((int) (0.5*GridWidth)),GridHeight+2*((int) (0.5*GridHeight)));
        g.setColor(ColorManager.getInstance().getColor(account+"_body"));
        g.fillRect(x*GridWidth-(int) (0.2*GridWidth),y*GridHeight-(int) (0.2*GridWidth),GridWidth+((int) (0.2*GridWidth))*2,GridHeight+((int) (0.2*GridHeight))*2);
        g.setColor(ColorManager.getInstance().getColor(account+"_gun"));
        switch (direction){
            case LEFT:
                g.fillRect(x*GridWidth-((int) (0.6*GridWidth)),y*GridHeight+(int) (0.4*GridHeight),GridWidth,GridHeight-((int) (0.4*GridHeight))*2);
                break;
            case RIGHT:
                g.fillRect(x*GridWidth+((int) (0.6*GridWidth)),y*GridHeight+(int) (0.4*GridHeight),GridWidth,GridHeight-((int) (0.4*GridHeight))*2);
                break;
            case DOWN:
                g.fillRect(x * GridWidth+(int)(GridWidth*0.4), y * GridHeight+(int)(0.6*GridHeight), GridWidth-((int)(GridWidth*0.4))*2, GridHeight);
                break;
            case UP:
                g.fillRect(x * GridWidth+(int)(GridWidth*0.4), y * GridHeight-(int)(0.6*GridHeight), GridWidth-((int)(GridWidth*0.4))*2, GridHeight);
                break;
        }
    }
    public String toString(){
        return "T";
    }

}
