package Direction;

public enum  Direction {
    UP(0),DOWN(1),LEFT(2),RIGHT(3);
    private final int id;
    private Direction(int id){
        this.id=id;
    }
    public Direction opposite(){
        switch (this){
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case RIGHT:
                return LEFT;
            default:
                return RIGHT;
        }
    }
    public int dx(){
        switch (this){
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                return 0;
        }
    }
    public int dy(){
        switch (this){
            case DOWN:
                return 1;
            case UP:
                return -1;
            default:
                return 0;
        }
    }
    public boolean isVerticalDirection(){
        return this.id<2;
    }
}
