package ClientEngine.GameControler;

import Direction.Direction;
import Direction.DirectionWriter;

import java.awt.event.KeyEvent;

public class GameDirectionKeyControler_Default extends GameDirectionKeyControler {
    public GameDirectionKeyControler_Default(DirectionWriter writer){
        super(writer);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                directionWriter.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                directionWriter.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                directionWriter.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                directionWriter.setDirection(Direction.RIGHT);
                break;
        }
    }
}
