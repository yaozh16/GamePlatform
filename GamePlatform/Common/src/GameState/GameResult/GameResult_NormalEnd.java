package GameState.GameResult;

import BasicState.PlayerGameState;

import java.util.HashSet;
import java.util.Hashtable;

public class GameResult_NormalEnd implements GameResult {
    private HashSet<String> winners=new HashSet<>();
    private HashSet<String> losers=new HashSet<>();
    private Hashtable<String,PlayerGameState> playerScores=new Hashtable<>();
    private String resultReport=null;

    @Override
    public void setResultReport(String resultReport) {
        this.resultReport = resultReport;
    }

    @Override
    public String formatForLabel() {
        if(resultReport!=null){
            return resultReport;
        }
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("GameEnd!\n");
        stringBuilder.append("Winner:");
        for(String account:winners){
            stringBuilder.append(account);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
    public GameResult_NormalEnd(Hashtable<String,PlayerGameState> playerScores){
        this.playerScores=playerScores;
    }
    public synchronized void addScore(String account,int score){
        if(playerScores.containsKey(account)){
            playerScores.get(account).addScore(score);
        }
    }

    public void delLife(String account,int del){
        if(playerScores.containsKey(account)){
            playerScores.get(account).delLife(del);
        }
    }
    public synchronized Hashtable<String,PlayerGameState> getScores(){
        return playerScores;
    }
    public void setWinners(HashSet<String> winners) {
        this.winners.clear();
        this.winners.addAll(winners);
    }
    public HashSet<String> getWinner(){
        return winners;
    }
    public void setLosers(HashSet<String> losers){
        this.losers.clear();
        this.losers.addAll(losers);
    }
    public HashSet<String> getLosers(){
        return losers;
    }
}
