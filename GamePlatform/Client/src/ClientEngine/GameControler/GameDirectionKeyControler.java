package ClientEngine.GameControler;

import Direction.Direction;
import Direction.DirectionWriter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class GameDirectionKeyControler implements KeyListener {
    private DirectionWriter directionWriter;
    public GameDirectionKeyControler(DirectionWriter directionWriter){
        this.directionWriter=directionWriter;
    }
    public void setDirection(Direction direction){
        directionWriter.setDirection(direction);
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
