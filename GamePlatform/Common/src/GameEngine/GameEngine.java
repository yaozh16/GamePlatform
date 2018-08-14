package GameEngine;


import BasicState.PlayerGameState;
import GameState.GameResult.GameResult;
import Message.Common.Message;

import java.util.Hashtable;

//GameEngine包含的部分
//1. 主要结算部分（Core）
//2. 与各个玩家的通信部分（收/发）
//3. 建立在Core与收发部分之间的缓冲可读写资源
public interface GameEngine {
    //初始化Core提供外部读出的棋盘资源
    public void initGridMap();
    //初始化Core从外部读入的控制信息
    public void buildupBufferAndConnection();
    //broadcaster
    public void buildupGridBroadcaster();
    //初始化游戏结算线程
    public void initCore();
    //结束时各个ObjThread的结束与broadcaster的结束
    public void finish();

    public void perform();//整体的启动

    public void start();

    public void killConnection(String account);

    public Hashtable<String,PlayerGameState> getPlayerGameStates();

    public void setPause(boolean pause);



    //接下来供子部件的调用
    public void coreExit(GameResult result);
    public void broadcastGridMap();
    public void notifyPlayerStateUpdated();
    public void broadcastMessage(Message message);



}
