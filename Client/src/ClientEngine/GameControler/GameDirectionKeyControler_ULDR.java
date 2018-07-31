package ClientEngine.GameControler;

import Direction.Direction;
import Direction.DirectionWriter;

import java.awt.event.KeyEvent;

public class GameDirectionKeyControler_ULDR extends GameDirectionKeyControler {

    public GameDirectionKeyControler_ULDR(DirectionWriter directionWriter){
        super(directionWriter);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.print("\033[1;33mkeyEvent\033[0m");
        switch (e.getKeyCode()){
            case KeyEvent.VK_UP:
                directionWriter.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_LEFT:
                directionWriter.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_DOWN:
                directionWriter.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_RIGHT:
                directionWriter.setDirection(Direction.RIGHT);
                break;
        }
    }

}
