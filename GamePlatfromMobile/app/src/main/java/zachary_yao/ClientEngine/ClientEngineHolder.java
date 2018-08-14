package zachary_yao.ClientEngine;

import Direction.DirectionWriter;
import GameState.GridMapControl.GridMapReader;

public interface ClientEngineHolder {
    public void installClientEngine(ClientEngine clientEngine);
    public void setGridMapReader(GridMapReader gridMapReader);
    public void transferDirectionWriter(DirectionWriter directionWriter);
    public void onLost();
}
