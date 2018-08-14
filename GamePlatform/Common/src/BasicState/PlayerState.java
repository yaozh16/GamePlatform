package BasicState;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;

public class PlayerState implements Serializable {
    private String account;
    private int win=0;
    private int lost=0;
    private LocalDateTime lastLogin=LocalDateTime.now();
    public enum OnlineState{READY, FREE,Game,LOST,InRoom,Pause};
    private OnlineState onlineState=OnlineState.FREE;
    public PlayerState(String account){
        this.account=account;
    }
    public PlayerState(String account,int win,int lost,LocalDateTime lastLogin,OnlineState onlineState){
        this.account=account;
        this.win=win;
        this.lost=lost;
        this.lastLogin=lastLogin;
        this.onlineState=onlineState;
    }
    public PlayerState copy(){
        try {
            return new PlayerState(this.account,this.win,this.lost,this.lastLogin,this.onlineState);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    public String formatToHTML(){
        String stateHTMLcolor=null;
        switch (onlineState){
            case READY:
                stateHTMLcolor="<font color='orange'>";
                break;
            case FREE:
                stateHTMLcolor="<font color='cyan'>";
                break;
            case Game:
                stateHTMLcolor="<font color='red'>";
                break;
            default:
                stateHTMLcolor="<font color='green'>";
                break;
        }
        return String.format("<html><b>%s</b><br>win/lose:%d/%d<br>State:%s%s",account,win,lost,stateHTMLcolor,onlineState.toString());
    }



    public String getAccount() {
        return account;
    }
    public int getLost() {
        return lost;
    }
    public int getWin() {
        return win;
    }
    public OnlineState getOnlineState() {
        return onlineState;
    }
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    public void setLost(int lost) {
        this.lost = lost;
    }
    public void setOnlineState(OnlineState onlineState) {
        this.onlineState = onlineState;
    }
    public void setWin(int win) {
        this.win = win;
    }
    public void winPlus(){win++;}
    public void lostPlus(){lost++;}



    private Integer loginCount=0;
    public void setLoginCount(Integer loginCount){
        this.loginCount=loginCount;
    }
    public synchronized void loginCountPlus(){
        loginCount++;
    }
    public synchronized void loginCountDel(){
        loginCount--;
    }

    public synchronized Integer getLoginCount() {
        return loginCount;
    }
}
