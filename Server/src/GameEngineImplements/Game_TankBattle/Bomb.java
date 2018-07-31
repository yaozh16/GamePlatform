package GameEngineImplements.Game_TankBattle;

import Direction.Direction;
import GameState.GridMap;
import GameState.GridObjects.*;

public class Bomb {
    private final String sender;
    private boolean init=true;
    private Integer hitPostion=null;
    private final Direction direction;
    private int posX,posY;
    private final GridMap gridMap;
    private final int firePower;
    private GridBomb controled;
    public Bomb(Direction direction, int position, String sender, GridMap gridMap,int firePower){
        this.direction=direction;
        this.gridMap=gridMap;
        this.sender=sender;
        this.posX=position%gridMap.width;
        this.posY=position/gridMap.width;
        this.firePower=firePower;
        this.controled=new GridBomb(firePower,this.sender);
    }
    public void clearBody(){
        if(controled!=null) {
            if(gridMap.getGridMapObject(posX + posY * gridMap.width).equals(controled))
                gridMap.setBlank(posX + posY * gridMap.width);
            controled=null;
        }
    }

    public int getFirePower() {
        return firePower;
    }

    public String getSender(){
        return sender;
    }
    public void tryMoveAndHit(){

        if(controled==null||controled.isHit()){

        }else{
            int newX=(posX+direction.dx()+gridMap.width)%gridMap.width;
            int newY=(posY+direction.dy()+gridMap.height)%gridMap.height;

            System.out.printf("Bomb (%d,%d) to (%d,%d)\n",posX,posY,posX+direction.dx(),posY+direction.dy());
            Integer next=newX+newY*gridMap.width;
            Class<? extends GridMapObject> meet=gridMap.getGridMapObject(next).getClass();
            if(meet.equals(GridBlank.class)){
                if(!init) {
                    gridMap.setBlank(posX + posY * gridMap.width);
                }
                gridMap.setGrid(controled,next);
                posY=newY;
                posX=newX;
            }else {
                System.out.println("\33[1;35m Bomb hit("+posX+","+posY+")\033[0m");
                controled.markBeHit();
                hitPostion=next;
            }
        }
        init=false;
    }
    public boolean isAlive(){
        return controled!=null&&!controled.isHit();
    }
    public Integer getHitPostion(){
        return hitPostion;
    }
}
