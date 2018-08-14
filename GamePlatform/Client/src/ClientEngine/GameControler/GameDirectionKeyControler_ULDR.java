package ClientEngine.GameControler;

import Direction.Direction;
import Direction.DirectionWriter;

import java.awt.event.KeyEvent;

public class GameDirectionKeyControler_ULDR extends GameDirectionKeyControler {
    public GameDirectionKeyControler_ULDR(DirectionWriter  directionWriter){
        super(directionWriter);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key Typed");
        switch (e.getKeyCode()){
            case KeyEvent.VK_UP:
                setDirection(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                setDirection(Direction.RIGHT);
                break;
        }
    }
}
