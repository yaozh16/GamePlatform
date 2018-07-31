package GameEngine;

import GameState.GridMapControl.GridMapReader;

import java.awt.event.KeyListener;

public interface ClientEngineHolder {
    public void installClientEngine(ClientEngine clientEngine);
    public void setGridMapReader(GridMapReader gridMapReader);
    public void addKeyListener(KeyListener keyListener);
    public void removeKeyListener(KeyListener keyListener);
    public void requestFocus();
    public void repaint();
    public void onLost();
}
