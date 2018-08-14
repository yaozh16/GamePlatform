package Direction.TCP;

import Direction.Direction;
import Direction.DirectionBuffer;


//构造时创建好ServerSocket
public class SocketDirectionBuffer implements DirectionBuffer {
    private volatile Direction direction;
    private final String account;
    public SocketDirectionBuffer(String account){
        this.account=account;
    }

    @Override
    public synchronized Direction getDirection() {
        return direction;
    }



    public synchronized void setDirection(Direction direction) {
        this.direction=direction;
    }

}
