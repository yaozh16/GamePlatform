package GameState.GameResult;

import BasicState.PlayerGameState;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;

public interface GameResult extends Serializable {
    public String formatForLabel();
    public void setResultReport(String resultReport);
    public void addScore(String account,int score);
    public void delLife(String account,int del);
    public Hashtable<String,PlayerGameState> getScores();
    public void setWinners(HashSet<String> winner);
    public HashSet<String> getWinner();
    public void setLosers(HashSet<String> losers);
    public HashSet<String> getLosers();
}
