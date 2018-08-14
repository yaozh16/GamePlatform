package BasicState;

import java.io.Serializable;

public class PlayerGameState implements Serializable {
    private int life=1;
    private int score=0;
    public void delLife(int del){
        life-=del;
    }
    public void addScore(int score){
        this.score+=score;
    }
    public int getScore(){return score;}
    public int getLife() {
        return life;
    }
    public PlayerGameState(int life){
        this.life=life;
        this.score=0;
    }
}
