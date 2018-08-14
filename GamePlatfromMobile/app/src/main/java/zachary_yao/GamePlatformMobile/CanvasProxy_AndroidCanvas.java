package zachary_yao.GamePlatformMobile;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import GameState.Proxy.CanvasProxy;
import GameState.Proxy.ColorProxy;

/**
 * Created by yaozh16 on 18-8-10.
 */

public class CanvasProxy_AndroidCanvas implements CanvasProxy {
    private final Canvas canvas;
    private final Paint paint=new Paint();
    public CanvasProxy_AndroidCanvas(Canvas canvas){
        this.canvas=canvas;
    }
    public void setColor(ColorProxy color){
        paint.setColor(Color.argb(color.a,color.r,color.g,color.b));
    }
    public void fillPolygon(int[] Xs,int[] Ys,int n){
        paint.setStyle(Paint.Style.FILL);
        Path path=new Path();
        path.moveTo(Xs[0],Ys[0]);
        for(int i=1;i<n;i++){
            path.lineTo(Xs[i],Ys[i]);
        }
        canvas.drawPath(path,paint);
    }
    public void fillRect(int x, int y, int width, int height){
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x,y,x+width,y+height,paint);
    }
    public void drawRect(int x, int y, int width, int height){
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x,y,x+width,y+height,paint);
    }
    public void fillRoundRect(int x, int y, int width, int height,int rx,int ry){
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(x,y,x+width,y+height,rx,ry,paint);
    }
    public void fillArc(int x, int y, int width, int height,
                                 int startAngle, int arcAngle){

        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc(x,y,x+width,y+height,startAngle,arcAngle,true,paint);
    }
    public void drawLine(int x,int y,int nx,int ny){
        canvas.drawLine(x,y,nx,ny,paint);
    }
}
