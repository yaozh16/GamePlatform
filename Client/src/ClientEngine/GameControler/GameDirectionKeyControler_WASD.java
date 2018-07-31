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
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:
                directionWriter.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_A:
                directionWriter.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_S:
                directionWriter.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_D:
                directionWriter.setDirection(Direction.RIGHT);
                break;
        }
    }
}
