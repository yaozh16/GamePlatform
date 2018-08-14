package ClientEngine.GameControler;

import Direction.Direction;
import Direction.DirectionWriter;

import java.awt.event.KeyEvent;

public class GameDirectionKeyControler_WASD extends GameDirectionKeyControler {
    public GameDirectionKeyControler_WASD(DirectionWriter directionWriter){
        super(directionWriter);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key Typed");
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:
                setDirection(Direction.UP);
                break;
            case KeyEvent.VK_S:
                setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_A:
                setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_D:
                setDirection(Direction.RIGHT);
                break;
        }
    }
}
