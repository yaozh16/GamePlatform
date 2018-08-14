package GameEngineImplements.Game_TankBattle;

import Direction.Direction;
import GameState.GridMap;
import GameState.GridObjects.*;

public class Tank {
    private GridMap gridMap;
    private Direction direction;
    private String account;
    private Integer posX;
    private Integer posY;
    private int justBorn=6;
    private boolean dead=false;
    public Tank(Direction initDirection, String account, int x, int y, GridMap gridMap){
        this.direction=initDirection;
        this.account=account;
        this.gridMap=gridMap;
        occupy(x,y);

        ((GridTank)gridMap.getGridMapObject(posX+posY*gridMap.width)).setJustBorn(true);
    }
    public boolean isDead(){
        return dead;
    }

    private int deadWait=0;
    private void occupy(int x,int y){
        posY=y;
        posX=x;
        gridMap.setGrid(new GridTank(account,direction),posX+posY*gridMap.width);
    }
    public void clearBody(){
        if(posX!=null&&posY!=null){
            gridMap.setBlank(posX+posY*gridMap.width);
            posX=null;
            posY=null;
        }
    }
    public int deadWait(){
        return ++deadWait;
    }
    public boolean canRestart(int Standard){
        return deadWait>=Standard;
    }

    public void onMeetBonus(){
        if(power<100) {
            power *= 2;
            if(power>100){
                power=100;
            }
        }
    }
    public void onBombTarget(){
        if(power<100){
            power+=1;
        }
    }
    public Class<? extends GridMapObject> tryMove(Direction newDirection){
        if(direction!=newDirection){
            direction=newDirection;
            ((GridTank)gridMap.getGridMapObject(posX+posY*gridMap.width)).setDirection(direction);
            if(justBorn>0)
                justBorn--;
            if(justBorn==0){
                ((GridTank)gridMap.getGridMapObject(posX+posY*gridMap.width)).setJustBorn(false);
            }
            System.out.println("\33[1;35m Tank change Direction\033[0m");
            return null;
        }else {
            if(justBorn>0){
                justBorn--;
                if(justBorn==0) {
                    ((GridTank) gridMap.getGridMapObject(posX + posY * gridMap.width)).setJustBorn(false);
                }
                System.out.println("\33[1;35m Tank justBorn\033[0m");
                return null;
            }
            int newX=(posX+direction.dx()+gridMap.width)%gridMap.width;
            int newY=(posY+direction.dy()+gridMap.height)%gridMap.height;
            System.out.printf("(%d,%d) to (%d,%d)\n",posX,posY,posX+direction.dx(),posY+direction.dy());
            Integer next=newX+newY*gridMap.width;
            Class<? extends GridMapObject> meet=gridMap.getGridMapObject(next).getClass();
            if(meet.equals(GridBlank.class)){
                gridMap.setBlank(posX+posY*gridMap.width);
                occupy(newX,newY);
                return null;
            }else if(meet.equals(GridWall.class)){
                //do not move
                return null;
            }else if(meet.equals(GridBomb.class)){
                ((GridBomb)gridMap.getGridMapObject(next)).markBeHit();
                return meet;
            }else if(meet.equals(GridBonus.class)){
                gridMap.setBlank(posX+posY*gridMap.width);
                occupy(newX,newY);
                fireRefillCount=0;
                return meet;
            }else {
                return meet;
            }
        }
    }
    int power=1;
    int fireRefillCount =0;
    public Bomb tryOpenFire(){
        fireRefillCount++;
        fireRefillCount %=5;
        if(fireRefillCount ==1)
            return new Bomb(direction,posX+posY*gridMap.width,account,gridMap,power);
        else
            return null;
    }
    public void markDie(){
        dead=true;
        GridTank gridTank=(GridTank)gridMap.getGridMapObject(posX+posY*gridMap.width);
        if(gridTank!=null){
            gridTank.setDead(true);
        }
    }

}
