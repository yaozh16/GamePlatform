import ServerBase.ServerForground;
import ServerSingletons.ServerDB;

public class Test_Server {
    public static void main(String [] args){
        ServerDB.getInstance().printAll();
        ServerForground.getInstance().setPort(2333).start();
    }
}
