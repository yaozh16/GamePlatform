package GameState.GameConfig;

import BasicState.PlayerGameState;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;

public class GameConfig implements Serializable {
    public static Hashtable<Integer,Integer> speedTranslater=new Hashtable<>();
    static {
        speedTranslater.put(1,800);
        speedTranslater.put(2,600);
        speedTranslater.put(3,400);
        speedTranslater.put(4,200);
        speedTranslater.put(5,150);
    }

    private int maxPlayer;
    private int gridWidth;
    private int gridHeight;
    private int bonusCount;
    private int lifeCount;
    private int speed;
    private int holePair;
    private GameType gameType;
    public GameConfig(int maxPlayer,int GridWidth,int GridHeight,int LifeCount,int BonusCount,int speed,int holePair,GameType gameType){
        this.maxPlayer=maxPlayer;
        this.gridHeight =GridHeight;
        this.gridWidth =GridWidth;
        this.lifeCount =LifeCount;
        this.bonusCount=BonusCount;
        this.speed=speed;
        this.holePair=holePair;
        this.gameType=gameType;
    }
    public GameConfig copy(){
        return new GameConfig(maxPlayer, gridWidth, gridHeight, lifeCount,bonusCount,speed,holePair,gameType);
    }
    public void copy(GameConfig gameConfig){
        maxPlayer=gameConfig.maxPlayer;
        gridHeight=gameConfig.gridHeight;
        gridWidth=gameConfig.gridWidth;
        lifeCount=gameConfig.lifeCount;
        bonusCount=gameConfig.bonusCount;
        speed=gameConfig.speed;
        holePair=gameConfig.holePair;
        gameType=gameConfig.gameType;
    }
    public Hashtable<String,PlayerGameState> InitPlayerGameStates(Hashtable<String,PlayerGameState> playerGameStateHashtable,HashSet<String> players){
        playerGameStateHashtable.clear();
        for(String account:players){
            playerGameStateHashtable.put(account,new PlayerGameState(lifeCount));
        }
        return playerGameStateHashtable;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getBonusCount() {
        return bonusCount;
    }

    public int getLifeCount() {
        return lifeCount;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHolePair() {
        return holePair;
    }

    public GameType getGameType() {
        return gameType;
    }


    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public void setBonusCount(int bonusCount) {
        this.bonusCount = bonusCount;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public void setLifeCount(int lifeCount) {
        this.lifeCount = lifeCount;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    public void setHolePair(int holePair) {
        this.holePair = holePair;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
