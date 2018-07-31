package ClientEngine.GameControler;

import Direction.DirectionWriter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class GameDirectionKeyControler implements KeyListener {

    protected DirectionWriter directionWriter;
    GameDirectionKeyControler(DirectionWriter directionWriter){
        this.directionWriter=directionWriter;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
