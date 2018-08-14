package ServerSingletons;

import BasicState.PlayerState;
import javafx.util.Pair;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Singleton
public class ServerDB implements Serializable{
    private static ServerDB ourInstance = new ServerDB();

    public static ServerDB getInstance() {
        return ourInstance;
    }

    public static ServerDB load(String DBName)throws IOException,ClassNotFoundException {
        ourInstance=(ServerDB)(new ObjectInputStream(new FileInputStream(DBName)).readObject());
        return ourInstance;
    }

    private ServerDB() {}
    private final Random r=new Random(LocalDateTime.now().getNano());

    public void dump(String DBName)throws IOException{
        new ObjectOutputStream(new FileOutputStream(DBName)).writeObject(this);
    }


    private Hashtable<String,Pair<String,LocalDateTime>> validateCodeStorage=new Hashtable<>();
    private ReentrantReadWriteLock validateCodeLock=new ReentrantReadWriteLock();
    public String generateValidateCode(String account){
        try {
            validateCodeLock.writeLock().lock();
            byte[] buffer = new byte[20];
            r.nextBytes(buffer);
            String validateCode = String.valueOf(buffer);
            validateCodeStorage.put(account, new Pair<>(validateCode, LocalDateTime.now()));
            return validateCode;
        }finally {
            validateCodeLock.writeLock().unlock();
        }
    }
    public boolean validateCodeCheck(String account,String validateCode){
        try {

            validateCodeLock.readLock().lock();
            Pair<String, LocalDateTime> entry = validateCodeStorage.get(account);
            if (entry == null)
                return false;
            return entry.getKey().equals(validateCode);
        }finally {
            validateCodeLock.readLock().unlock();
        }
    }


    private Hashtable<String,String> accountPasswordStorage=new Hashtable<>();
    private ReentrantReadWriteLock accountPasswordLock=new ReentrantReadWriteLock();
    public boolean accountExistenceCheck(String account){
        accountPasswordLock.readLock().lock();
        try{
            return accountPasswordStorage.containsKey(account);
        }finally {
            accountPasswordLock.readLock().unlock();
        }
    }
    public boolean accountPasswordCheck(String account,String password){
        accountPasswordLock.readLock().lock();
        try{
            return accountPasswordStorage.containsKey(account)&&accountPasswordStorage.get(account).equals(password);
        }finally {
            accountPasswordLock.readLock().unlock();
        }
    }
    public boolean signupAccount(String account,String password){
        accountPasswordLock.writeLock().lock();
        try {
            if(accountPasswordStorage.containsKey(account))
                return false;
            accountPasswordStorage.put(account,password);
            playerStateLock.writeLock().lock();
            try {
                playerStateStorage.put(account,new PlayerState(account));
            }finally {
                playerStateLock.writeLock().unlock();
            }
            return true;
        }finally {
            accountPasswordLock.writeLock().unlock();
        }
    }


    private Hashtable<String,PlayerState> playerStateStorage=new Hashtable<>();
    private ReentrantReadWriteLock playerStateLock=new ReentrantReadWriteLock();
    public void updatePlayer(PlayerState playerState){
        playerStateLock.writeLock().lock();
        try {
            playerStateStorage.put(playerState.getAccount(), playerState.copy());
        }finally {
            playerStateLock.writeLock().unlock();
        }
    }
    public void login(String account){
        try{
            playerStateLock.readLock().lockInterruptibly();
            playerStateStorage.get(account).loginCountPlus();
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            playerStateLock.readLock().unlock();
        }
    }
    public void logout(String account){
        try{
            playerStateLock.readLock().lockInterruptibly();
            playerStateStorage.get(account).loginCountDel();
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }finally {
            playerStateLock.readLock().unlock();
        }
    }
    public PlayerState queryPlayer(String account){
        try{
            playerStateLock.readLock().lockInterruptibly();
            if(playerStateStorage.containsKey(account))
                return playerStateStorage.get(account);
            else {
                playerStateStorage.put(account,new PlayerState(account));
                return new PlayerState(account);
            }
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }finally {
            playerStateLock.readLock().unlock();
        }
    }
    public Hashtable<String,PlayerState> queryPlayerStates(HashSet<String> accounts){
        try {
            playerStateLock.readLock().lockInterruptibly();
            Hashtable<String, PlayerState> playerStateHashTable = new Hashtable<>();
            for (String account : accounts) {
                playerStateHashTable.put(account, playerStateStorage.get(account));
            }
            return playerStateHashTable;
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }finally{
            playerStateLock.readLock().unlock();
        }
    }
    public void trySetPlayerOnlineStates(Set<String> accounts, PlayerState.OnlineState onlineState){
        try {

            playerStateLock.writeLock().lockInterruptibly();
            for(String account:accounts) {
                if (playerStateStorage.containsKey(account)) {
                    playerStateStorage.get(account).setOnlineState(onlineState);
                }
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally {
            playerStateLock.writeLock().unlock();
        }
    }
    public void trySetPlayerOnlineState(String account, PlayerState.OnlineState onlineState){
        try {
            playerStateLock.writeLock().lockInterruptibly();
            System.out.println("set "+account+" as \033[1;32m"+onlineState+"\033[0m");
            if (playerStateStorage.containsKey(account)){
                playerStateStorage.get(account).setOnlineState(onlineState);
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally {
            playerStateLock.writeLock().unlock();
        }
    }
    public void trySetPlayerWinLose(HashSet<String> winners,HashSet<String> losers){
        try {
            playerStateLock.writeLock().lockInterruptibly();
            for (String account : winners) {
                if(playerStateStorage.containsKey(account)){
                    playerStateStorage.get(account).winPlus();
                }
            }
            for (String account : losers) {
                if(playerStateStorage.containsKey(account)){
                    playerStateStorage.get(account).lostPlus();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            playerStateLock.writeLock().unlock();
        }
    }
    public PlayerState[] fetchAllPlayerState(){
        playerStateLock.readLock().lock();
        try{
            PlayerState[] allPlayerState=new PlayerState[playerStateStorage.keySet().size()];
            int index=0;
            for(String playerAccount:playerStateStorage.keySet()){
                allPlayerState[index++]=playerStateStorage.get(playerAccount);
            }
            return allPlayerState;
        }finally {
            playerStateLock.readLock().unlock();
        }
    }




    public void printAll(){
        for(String account:validateCodeStorage.keySet()){
            System.out.println(String.format("%s:%s",account,validateCodeStorage.get(account)));
        }
        for(String account:accountPasswordStorage.keySet()){
            System.out.println(String.format("%s:%s",account,accountPasswordStorage.get(account)));
        }
        for(String account:playerStateStorage.keySet()){
            System.out.println(String.format("%s:%s",account,playerStateStorage.get(account)));
        }
    }
}
