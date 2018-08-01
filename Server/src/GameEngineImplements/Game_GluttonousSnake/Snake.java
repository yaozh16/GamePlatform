package GameEngineImplements.Game_GluttonousSnake;

import Direction.Direction;
import GameState.GridMap;
import GameState.GridObjects.*;

import java.util.LinkedList;
import java.util.function.Function;

public class Snake {
    public boolean dead=false;
    public String account;
    public Direction direction;
    public int width;
    public int height;
    public LinkedList<Integer> occupied=new LinkedList<>();
    private GridMap gridMap;
    private int justBorn=4;
    public Snake(Direction initDirection, int hx, int hy, GridMap gridMap,String account){
        direction=initDirection;
        this.height=gridMap.height;
        this.width=gridMap.width;
        this.gridMap=gridMap;
        this.account=account;

        int index;
        GridSnake gridSnake;

        System.out.printf("Snake for(%d,%d)\n",(hx+direction.opposite().dx()),(hy+direction.opposite().dy()));
        index=(hx+direction.opposite().dx()+width)%width+((hy+direction.opposite().dy()+height)%height)*this.width;
        occupied.push(index);
        gridSnake=new GridSnake(null,direction,account);
        gridSnake.setJustBorn(true);
        occupy(index,gridSnake);


        System.out.printf("Snake for(%d,%d)\n",hx,hy);
        index=hx+hy*this.width;
        occupied.push(index);
        gridSnake=new GridSnake(direction.opposite(),null,account);
        gridSnake.setJustBorn(true);
        occupy(index,gridSnake);

    }
    public void occupy(int index, GridSnake gridSnake){
        gridMap.setGrid(gridSnake,index);
    }
    public Function<GridMapObject,Void> updateHeaderTo =new Function<GridMapObject, Void>() {
        @Override
        public Void apply(GridMapObject gridSnake) {
            ((GridSnake)gridSnake).to=Snake.this.direction;
            return null;
        }
    };
    public Function<GridMapObject,Void> updateRemoveTailFrom =new Function<GridMapObject, Void>() {
        @Override
        public Void apply(GridMapObject gridSnake) {
            ((GridSnake)gridSnake).from=null;
            return null;
        }
    };
    public Function<GridMapObject,Void> updateRemoveHeadTo =new Function<GridMapObject, Void>() {
        @Override
        public Void apply(GridMapObject gridSnake) {
            ((GridSnake)gridSnake).to=null;
            return null;
        }
    };


    int bonusGrow=0;
    Integer tailInHoleCount=null;//包括正好在洞上:0标识刚好在洞上，null标识空地
    Integer headInHoleCount=null;//包括正好在洞上:0标识刚好在洞上，null标识空地
    Integer headInHolePos=null;//x标识进洞过程，null标识非进洞过程
    Integer headFromHolePos=null;//x标识出洞过程，null标识非出洞过程

    //状态：
    //tailInHoleCount   | headInHoleCount   | headInHolePos | headFromHolePos   | description      |  feature  |
    //  x               |   0               |   null        |    x              |     出洞开始      |  头在洞口,尾在洞
    //  x               |   null            |   null        |    x              |     出洞中       |   头自由,尾在洞
    //  0               |   null            |   null        |    x              |     出洞结束      |  头自由,尾在洞口
    //  x               |   x               |   x           |    x              |     进出洞同时     |  头尾在洞
    //  null            |   x               |   x           |    null           |     进洞开始/进洞中 |  头在洞,尾自由
    //  0               |   x               |   x           |    null           |     进洞结束       |  头尾在洞
    //  null            |   null            |   null        |    null           |     空地          |  头尾自由

    //每次首先调用
    public void updateOldHeadTo(Direction newDirection){
        if(justBorn>0){
            return;
        }
        if(!this.direction.opposite().equals(newDirection)){
            direction=newDirection;
        }
        if(!dead&&occupied.size()>0) {
            if (headInHoleCount == null) {
                gridMap.updateGrid(occupied.getFirst(), updateHeaderTo);
            }
        }
    }
    //最后调用
    public void updateNewTailFrom(){
        if(!dead&&occupied.size()>0){
            if(tailInHoleCount== null){
                gridMap.updateGrid(occupied.getLast(),updateRemoveTailFrom);
            }
        }
    }

    //return die
    private Class<? extends GridMapObject> extendHead(){
        if(headInHolePos!=null){//头正在进洞
            headInHoleCount++;
            return null;
        }else if(headInHoleCount!=null&&headInHoleCount==0) {//出洞瞬间
            int curPos=headFromHolePos;
            int x=curPos%width;
            int y=curPos/width;
            int next=(x+direction.dx()+width)%width+(((y+direction.dy()+height))%height)*width;
            Class<? extends GridMapObject> gridType=gridMap.getGridType(next);
            if(gridType.equals(GridBlank.class)){//出洞
                occupied.push(next);
                GridHole hole=(GridHole)gridMap.getGridMapObject(headFromHolePos);
                if(hole.getOccupyFromDirection()!=direction){
                    hole.occupy(direction,account);
                }
                occupy(next,new GridSnake(direction.opposite(),null,account));
                headInHoleCount=null;
                return null;
            }else if(gridType.equals(GridHole.class)){
                GridHole hole=(GridHole) (gridMap.getGridMapObject(next));
                if(hole.legal()){
                    hole.occupy(direction.opposite(),account);
                    headInHolePos=next;
                    headInHoleCount=0;
                    return null;
                }else {
                    System.out.println(account+" meet illegal hole at "+x+","+y);
                    return gridType;
                }
            }else if(gridType.equals(GridBonus.class)) {
                bonusGrow++;
                occupied.push(next);
                occupy(next,new GridSnake(direction.opposite(),null,account));
                return gridType;
            }else {
                System.out.println(account+" meet object "+gridType+" at "+x+","+y);
                return gridType;
            }
        }else {//head free
            int curPos=occupied.getFirst();
            int x=curPos%width;
            int y=curPos/width;
            System.out.printf("(%d,%d) to (%d,%d)\n",x,y,x+direction.dx(),y+direction.dy());
            int next=(x+direction.dx()+width)%width+(((y+direction.dy()+height))%height)*width;
            Class<? extends GridMapObject> gridType=gridMap.getGridType(next);
            if(gridType.equals(GridBlank.class)){
                occupied.push(next);
                occupy(next,new GridSnake(direction.opposite(),null,account));
                return null;
            }else if(gridType.equals(GridHole.class)){
                GridHole hole=(GridHole) (gridMap.getGridMapObject(next));
                if(hole.legal()){
                    hole.occupy(direction.opposite(),account);
                    headInHolePos=next;
                    headInHoleCount=0;
                    return null;
                }else {
                    System.out.println(account+" meet illegal hole at "+x+","+y);
                    return gridType;
                }
            }else if(gridType.equals(GridBonus.class)) {
                bonusGrow++;
                occupied.push(next);
                occupy(next,new GridSnake(direction.opposite(),null,account));
                return gridType;
            }else {
                System.out.println(account+" meet object "+gridType+" at "+x+","+y);
                return gridType;
            }
        }
    }

    private void collapseTail(){
        if(bonusGrow>0){
            bonusGrow--;
            return;
        }
        if(tailInHoleCount!=null){//尾巴不在自由位置
            if(tailInHoleCount>0){//出洞中
                tailInHoleCount--;
            }else {//tailInHoleCount==0   进洞结束或者出洞结束或者进出洞同时
                if(headFromHolePos==null) {//进洞结束
                    examineMoveHeadToAnotherHole();
                }else {//tailInHoleCount==0 headFromHolePos!=null  出洞结束或者正好同时进出洞
                    GridHole hole = (GridHole) (gridMap.getGridMapObject(headFromHolePos));
                    hole.occupy(null, null);
                    headFromHolePos=null;
                    if(occupied.size()==0){
                        tailInHoleCount=0;
                    }else {
                        tailInHoleCount=null;
                    }
                }
            }
        }else {//尾巴自由
            gridMap.setBlank(occupied.pollLast());
            if(occupied.isEmpty()){
                tailInHoleCount=0;
            }
        }
    }

    private void examineMoveHeadToAnotherHole(){
        //调用状态：进洞结束
        //调用结束：出洞开始
        assert tailInHoleCount==0;
        assert headFromHolePos==null;
        assert headInHoleCount!=null;
        assert headInHolePos!=null;
        //tailInHoleCount   | headInHoleCount   | headInHolePos | headFromHolePos   | description      |  feature  |
        //  x               |   0               |   null        |    x              |     出洞开始      |  头在洞口,尾在洞
        //  0               |   x               |   x           |    null           |     进洞结束       |  头尾在洞
        GridHole hole = (GridHole) (gridMap.getGridMapObject(headInHolePos));
        headFromHolePos=hole.connected.index;
        headInHolePos=null;
        tailInHoleCount=headInHoleCount-1;
        headInHoleCount=0;
        hole.connected.occupy(direction,account);
        hole.occupy(null,null);

    }
    //return object if die
    public Class<? extends GridMapObject> tryMove(Direction newDirection){
        if(headInHoleCount!=null)
            direction=newDirection;
        if(!this.direction.opposite().equals(newDirection)){
            direction=newDirection;
        }
        if(justBorn>0){
            if(--justBorn==0){
                for(Integer i:occupied){
                    ((GridSnake)(gridMap.getGridMapObject(i))).setJustBorn(false);
                }
            }
            return null;
        }

        Class<? extends GridMapObject> meet=extendHead();
        if(meet!=null&&!meet.equals(GridBonus.class)) {
            if(!occupied.isEmpty()) {
                gridMap.updateGrid(occupied.getFirst(), updateRemoveHeadTo);//恢复蛇头
            }
            return meet;
        }
        collapseTail();

        return meet;
    }
    public void markDie(){
        for(Integer each:occupied){
            ((GridSnake)(gridMap.getGridMapObject(each))).markDead();
        }
        if(headInHolePos!=null){
            ((GridHole)gridMap.getGridMapObject(headFromHolePos)).markDead();
        }
        if(headFromHolePos!=null){
            ((GridHole)gridMap.getGridMapObject(headFromHolePos)).markDead();
        }
        System.out.println("\033[1;32m"+account+"\033[0m die!");
        dead=true;
    }
    private int deadWait =0;
    public void clearBody(){

        if(headFromHolePos!=null){
            GridHole hole=(GridHole)(gridMap.getGridMapObject(headFromHolePos));
            if(hole!=null){
                hole.occupy(null,null);
            }else {
                System.err.println("something should not be there for tail");
            }
            headFromHolePos=null;
        }
        if(headInHolePos!=null){
            GridHole hole=(GridHole)(gridMap.getGridMapObject(headInHolePos));
            if(hole!=null){
                hole.occupy(null,null);
            }else {
                System.err.println("something should not be there for head");
            }
            headInHolePos=null;
        }
        for(Integer eachIndex:occupied){
            gridMap.setBlank(eachIndex);
        }
        occupied.clear();
    }
    //返回值控制是否可以复活
    public synchronized int deadWait(){
        return ++deadWait;
    }
    public boolean canRestart(int standard){
        return deadWait >=standard;
    }

}
