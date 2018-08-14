package GameState.Proxy;

/**
 * Created by yaozh16 on 18-8-10.
 */

public class ColorProxy {
    public int r;
    public int g;
    public int b;
    public int a;
    public ColorProxy(int r,int g,int b,int a){
        this.r=r;
        this.g=g;
        this.b=b;
        this.a=a;
    }
    public ColorProxy(int r,int g,int b){
        this.r=r;
        this.g=g;
        this.b=b;
        this.a=255;
    }
    public ColorProxy darker(){
        return new ColorProxy((int)(r*0.7),(int)(g*0.7),(int)(b*0.7));
    }
}
