package GameEngine;

import GameState.GridMap;

public interface GameEngineCore extends Runnable {
    public GridMap getGridMap();
    public GridMap buildGridMapAndSetInitDirection();
    public void setPause(boolean pause);
    public void setFinish(boolean finish);
    public void notifyLeave(String account);

}
