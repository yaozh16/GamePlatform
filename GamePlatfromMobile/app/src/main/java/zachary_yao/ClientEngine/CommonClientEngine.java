package zachary_yao.ClientEngine;

import zachary_yao.ClientEngine.Configs.ClientConfigHolder;
import zachary_yao.ClientEngine.Configs.GameControlConfig;
import CommunicateControl.ObjThreadAsyn;
import CommunicateControl.ObjThreadAsynHolder;
import Direction.Direction;
import Direction.DirectionWriter;
import GameState.GridMap;
import GameState.GridMapControl.GridMapReader;
import Message.RoomMessage.MConnect;
import Message.RoomMessage.MMove;


import java.net.Socket;

public class CommonClientEngine implements ClientEngine,ObjThreadAsynHolder<GridMap,MMove> {
    private Socket socket;
    private ClientEngineHolder clientEngineHolder;
    private ClientConfigHolder clientConfigHolder;
    private ObjThreadAsyn<GridMap,MMove> objThreadAsyn;
    private GridMapReader reader;
    private DirectionWriter writer;
    private final GameControlConfig gameControlConfig;
    public CommonClientEngine(MConnect mConnect, ClientEngineHolder clientEngineHolder, ClientConfigHolder clientConfigHolder,GameControlConfig gameControlConfig){
        this.gameControlConfig=gameControlConfig;
        this.clientEngineHolder=clientEngineHolder;
        this.clientConfigHolder=clientConfigHolder;try {
            socket = new Socket(clientConfigHolder.getClientConfig().getServerAddress(), mConnect.port);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("\033[1;32mClientEngine Socket:"+socket+"\033[0m");
    }
    public void start(){
        setUpObjThread(socket);
        buildGridMapReader();
        buildOperationWriter();
        objThreadAsyn.start();
    }

    @Override
    public ClientEngine setClientEngineHolder(ClientEngineHolder clientEngineHolder) {
        this.clientEngineHolder=clientEngineHolder;
        return this;
    }

    @Override
    public void setUpObjThread(Socket socket) {
        if(objThreadAsyn!=null)
            objThreadAsyn.finish();
        objThreadAsyn=new ObjThreadAsyn<>(this,socket);
    }
    @Override
    public void buildGridMapReader(){
        reader=new GridMapReader() {
            private GridMap gridMap;
            @Override
            public synchronized GridMap getGridMapCopy() {
                return gridMap;
            }
            @Override
            public  void setGridMap(GridMap gridMap){
                synchronized(this) {
                    this.gridMap = gridMap;
                }
                //clientEngineHolder.repaint();
            }
        };
        clientEngineHolder.setGridMapReader(reader);
    }
    public void buildOperationWriter(){
        writer=new DirectionWriter() {
            private Direction direction;
            private int count=0;
            @Override
            public synchronized void setDirection(Direction direction) {
                if(direction.opposite().equals(this.direction))
                    return;
                System.out.println(" Write "+direction);
                if(this.direction!=direction){
                    this.direction=direction;
                    count=0;
                    toSendObj(new MMove(clientConfigHolder.getClientConfig().getAccount(),this.direction));
                }else{
                    count++;
                    if(count>5){
                        count=0;
                        toSendObj(new MMove(clientConfigHolder.getClientConfig().getAccount(),this.direction));
                    }
                }
            }
        };
        synchronized (gameControlConfig) {

                clientEngineHolder.transferDirectionWriter(writer);
        }
    }
    public void notifyControlConfigChange(){
        synchronized (gameControlConfig) {
            clientEngineHolder.transferDirectionWriter(writer);
        }

    }

    @Override
    public void onRecvObj(GridMap obj) {
        //System.out.println(this+"receive \033[1;33m"+obj+"\033[0m");
        reader.setGridMap(obj);
    }

    @Override
    public void toSendObj(MMove obj) {
        objThreadAsyn.sendMsg(obj);
    }

    boolean afterFinish=false;
    @Override
    public void finish() {
        afterFinish=true;
        System.out.println("client Engine finish");
        clientEngineClear();
    }

    @Override
    public void exit(ObjThreadAsyn src) {
        if(!afterFinish)
            clientEngineHolder.onLost();
    }

    @Override
    public void clientEngineClear(){
        System.out.println(this+":clientEngineClear");
        if(objThreadAsyn!=null){
            objThreadAsyn.finish();
            try {
                objThreadAsyn.getSocket().close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            objThreadAsyn=null;
        }
        if(writer!=null){
            clientEngineHolder.transferDirectionWriter(null);
            writer=null;
        }
        System.out.println("clientEngine clear");
    }
}
