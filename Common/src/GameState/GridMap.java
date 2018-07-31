package GameState;

import Direction.Direction;
import GameState.GridObjects.GridBlank;
import GameState.GridObjects.GridBonus;
import GameState.GridObjects.GridMapObject;
import GameState.GridObjects.GridWall;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;
import java.util.function.Function;

public class GridMap implements Serializable {
    public volatile int width;
    public volatile int height;
    public Vector<GridMapObject> gridMapObjects=new Vector<>();
    private Vector<Integer> blanks=new Vector<>();
    Random r=new Random(LocalDateTime.now().getNano());
    public GridMap(int width,int height){
        this.width=width;
        this.height=height;
        for(int i=0;i<height*width;i++) {
            gridMapObjects.addElement(new GridBlank());
            blanks.add(i);
        }
    }
    public GridMap(int width,int height,Vector<GridMapObject> gridMapObjects){
        assert width*height==gridMapObjects.size();
        this.width=width;
        this.height=height;
        this.gridMapObjects.addAll(gridMapObjects);
        for(int i=0;i<height*width;i++) {
            blanks.add(i);
        }
    }
    public synchronized Class<? extends GridMapObject> getGridType(int index){
        return gridMapObjects.get(index).getClass();
    }
    public synchronized void setGrid(GridMapObject object,int index){
        gridMapObjects.get(index).finish();
        gridMapObjects.set(index,object);
    }
    public synchronized void updateGrid(int index, Function<GridMapObject,Void> updateFunc){
        updateFunc.apply(gridMapObjects.get(index));
    }
    public synchronized void setWall(int x,int y,int w,int h){
        for(int i=x;i<x+w&&i<width;i++){
            for(int j=y;j<y+h&&j<height;j++){
                setGrid(new GridWall(),((i+width)%width)+((j+height)%height)*width);
            }
        }
    }
    public synchronized void setBlank(int index){
        setGrid(new GridBlank(),index);
        blanks.add(index);
    }
    public synchronized GridMapObject getGridMapObject(int index){
        return gridMapObjects.get(index);
    }
    public synchronized int tryPutBonus(int bonus){
        while (!blanks.isEmpty()&&bonus>0){
            int rindex=r.nextInt(blanks.size());
            int index=blanks.get(rindex);
            if(gridMapObjects.get(index).getClass().equals(GridBlank.class)){
                gridMapObjects.set(index,new GridBonus());
                bonus--;
            }
            blanks.remove(rindex);
        }
        return bonus;
    }
    public synchronized HashSet<Integer> tryGetBlank(int Size){
        HashSet<Integer> blank=new HashSet<>();
        while (!blanks.isEmpty()&&blank.size()<Size){
            int rindex=r.nextInt(blanks.size());
            int index=blanks.get(rindex);
            if(gridMapObjects.get(index).getClass().equals(GridBlank.class)){
                blank.add(index);
            }else {
                blanks.remove(rindex);
            }
        }
        return blank;
    }
    public synchronized Integer tryGetLongObject(int length,Direction direction){
        Integer tail=null;
        if(blanks.size()<length){
            return null;
        }
        int start=r.nextInt(blanks.size());
        for(int i=0;i<start;i++){
            blanks.add(blanks.remove(0));
        }
        for(Integer each:blanks){
            tail=each;
            Integer x=tail%width;
            Integer y=tail/width;
            for(int i=0;i<length;i++){
                int cx=(x+i*direction.dx()+width)%width;
                int cy=(y+i*direction.dy()+height)%height;
                if(!gridMapObjects.get(cx+cy*width).getClass().equals(GridBlank.class)){
                    //not blank
                    tail=null;
                    break;
                };
            }
            if(tail!=null){
                return tail;
            }
        }
        return null;
    }
    public synchronized boolean tryPutWall(int walls,Direction direction){
        Integer start=tryGetLongObject(walls,direction);
        if(start!=null){
            int x=start%width;
            int y=start/ width;
            for(int j=0;j<walls;j++){
                int cx=(x+j*direction.dx()+ width)% width;
                int cy=(y+j*direction.dy()+ height)% height;
                if(! getGridMapObject(cx+cy* width).getClass().equals(GridBlank.class)){
                    //not blank
                    return false;
                }else {
                    setGrid(new GridWall(),((cx+width)%width)+((cy+height)%height)*width);
                }
            }
            return true;
        }else {
            return false;
        }
    }
    public String toString(){
        StringBuilder builder=new StringBuilder();

        /*for(int y=0;y<height;y++){
            builder.append("\n");
            for(int x=0;x<width;x++){
                builder.append(gridMapObjects.get(x+y*width));
            }
        }*/
        return String.format("GridMap(%dx%d)%s",width,height,builder.toString());
    }
}
