package GameState.GridObjects.Manager;


import GameState.Proxy.ColorProxy;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

public class ColorManager {
    private static ColorManager ourInstance = new ColorManager();

    public static ColorManager getInstance() {
        return ourInstance;
    }

    private ColorManager() {
        HashSet<String> illegalName=new HashSet<>();
        illegalName.addAll(reservedColorTable.keySet());
        illegalName.add("HolePair");//给hole用
    }


    private final Hashtable<String,ColorProxy> colorHashtable=new Hashtable<>();
    private final Hashtable<String,ColorProxy> reservedColorTable=new Hashtable<>();
    private final HashSet<String> illegalName=new HashSet<>();
    public synchronized ColorProxy getColor(String account){
        if(!colorHashtable.containsKey(account)){
            Random r=new Random(account.hashCode());
            int[] bytes=new int[3];
            ColorProxy c=null;
            while(c==null||colorHashtable.contains(c)){
                for(int i=0;i<3;i++){
                    bytes[i]=r.nextInt(256);
                }
                c=new ColorProxy(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            }
            colorHashtable.put(account,c);
        }
        return colorHashtable.get(account);
    }
    public boolean registerIllegalColor(String name,ColorProxy color){
        if(reservedColorTable.containsKey(name)||reservedColorTable.contains(color))
            return false;
        reservedColorTable.put(name,color);
        return true;
    }
    public Set<String> illegalName(){
        return illegalName;
    }
}
