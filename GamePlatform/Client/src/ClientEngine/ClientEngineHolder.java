package ClientEngine;

import ClientEngine.GameControler.GameDirectionKeyControler;
import GameState.GridMapControl.GridMapReader;

public interface ClientEngineHolder {
    public void installClientEngine(ClientEngine clientEngine);
    public void setGridMapReader(GridMapReader gridMapReader);
    public void addDirectionControler(GameDirectionKeyControler keyListener);
    public void removeDirectionControler(GameDirectionKeyControler keyListener);
    public void requestFocus();
    public void repaint();
    public void onLost();
}
