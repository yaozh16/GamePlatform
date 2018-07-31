package GameState.GridMapControl;

import CommunicateControl.ObjThreadAsyn;
import GameState.GridMap;

import java.util.concurrent.TimeUnit;

public class SocketGridMapBroadcaster extends Thread implements GridMapBroadcaster {

    public final GridMap[] gridMap;
    private ObjThreadAsyn<?,GridMap> objThreadAsyn;
    public SocketGridMapBroadcaster(GridMap[] gridMap, ObjThreadAsyn<?,GridMap> objThreadAsyn){
        this.gridMap=gridMap;
        this.objThreadAsyn=objThreadAsyn;
        setPriority(9);
    }
    private boolean finish=false;
    @Override
    public void run() {
        try {
            while (!interrupted()) {
                TimeUnit.MILLISECONDS.sleep(400);
                synchronized (gridMap) {
                    objThreadAsyn.sendMsg(gridMap[0]);
                }
            }
        }catch (InterruptedException ex){
            if(!finish)
                ex.printStackTrace();
        }finally {
            System.out.println(this+" exit run");
        }
    }
    public void finishActive(){
        finish=true;
        SocketGridMapBroadcaster.this.interrupt();
    }
}
