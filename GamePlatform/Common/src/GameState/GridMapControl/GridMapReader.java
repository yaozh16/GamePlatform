package GameState.GridMapControl;

import GameState.GridMap;

//注意同步
public interface GridMapReader {
    public GridMap getGridMapCopy();
    public void setGridMap(GridMap gridMap);
}

