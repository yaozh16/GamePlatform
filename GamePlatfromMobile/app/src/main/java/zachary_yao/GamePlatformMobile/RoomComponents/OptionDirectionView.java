package zachary_yao.GamePlatformMobile.RoomComponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import Direction.Direction;
import Direction.DirectionWriter;
import zachary_yao.GamePlatformMobile.R;

/**
 * Created by yaozh16 on 18-8-12.
 */

public class OptionDirectionView extends AppCompatTextView{
    public OptionDirectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint mPaintCenter=new Paint();
    private Paint mPaintPos=new Paint();
    private float mR;
    private Integer CenterMargin=10;
    private Integer RadiusCenter=60;
    private Integer RadiusPos=30;
    private Float CenterX=null;
    private Float CenterY=null;
    private Float PosX=null;
    private Float PosY=null;
    float dx;
    float dy;
    private DirectionWriter directionWriter=null;
    public void setDirectionWriter(DirectionWriter directionWriter){
        this.directionWriter=directionWriter;
    }
    private void init(){

        mPaintCenter.setColor(getResources().getColor(R.color.room_option_controlPanel_CenterCircle));
        mPaintPos.setColor(getResources().getColor(R.color.room_option_controlPanel_PosCircle));
    }



    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (PosX!=null&&PosY!=null){
            canvas.drawCircle(CenterX,CenterY,RadiusCenter,mPaintCenter);
            canvas.drawCircle(PosX,PosY,RadiusPos, mPaintPos);
        }
        if(directionNeedDisplay){
            Paint directionPaint=new Paint();
            directionPaint.setColor(getResources().getColor(R.color.room_option_controlPanel_DirectionArrow));
            Path path=new Path();
            double degree=0;
            switch (direction) {
                case UP:
                    degree=Math.PI/2;
                    break;
                case DOWN:
                    degree=Math.PI/2*3;
                    break;
                case LEFT:
                    degree=Math.PI;
                    break;
                case RIGHT:
                    degree=0;
            }
            float Rx=getWidth()/2;
            float Ry=getHeight()/2;
            path.moveTo(Rx+(float)(Rx*Math.cos(degree)),Ry-(float)(Ry*Math.sin(degree)));
            path.lineTo(Rx+(float)(0.6*Rx*Math.cos(degree+Math.PI/6)),Ry-(float)(0.6*Ry*Math.sin(degree+Math.PI/6)));
            path.lineTo((float)(Rx+(float)(0.8*Rx*Math.cos(degree))),(float)(Ry-(float)(0.8*Ry*Math.sin(degree))));
            path.lineTo(Rx+(float)(0.6*Rx*Math.cos(degree-Math.PI/6)),Ry-(float)(0.6*Ry*Math.sin(degree-Math.PI/6)));
            path.lineTo(Rx+(float)(Rx*Math.cos(degree)),Ry-(float)(Ry*Math.sin(degree)));
            canvas.drawPath(path,directionPaint);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float X=event.getX();
        float Y=event.getY();
        switch(event.getAction()){
            case(MotionEvent.ACTION_DOWN):
                CenterX=X;
                CenterY=Y;
                NormalizeCenter();
                PosX=X;
                PosY=Y;
                setText(" ");
                break;
            case(MotionEvent.ACTION_MOVE):
                PosX=X;
                PosY=Y;
                NormalizePos();
                break;
            case(MotionEvent.ACTION_UP):
                PosX=null;
                PosY=null;
                setText("");
                directionNeedDisplay=false;
                break;
        }
        invalidate();
        return true;
    }


    public void NormalizePos(){

        dx = PosX - CenterX;
        dy = PosY - CenterY;
        mR= (float) Math.sqrt(dx*dx+dy*dy);
        if(mR>RadiusCenter){
            PosX=CenterX+(dx/mR*RadiusCenter);
            PosY=CenterY+(dy/mR*RadiusCenter);
        }
                updateDirection();
    }
    public void NormalizeCenter(){
        CenterX=Math.max(CenterX,CenterMargin);
        CenterX=Math.min(CenterX,getWidth()-CenterMargin);
        CenterY=Math.max(CenterY,CenterMargin);
        CenterY=Math.min(CenterY,getHeight()-CenterMargin);
    }

    private volatile boolean directionNeedDisplay=false;
    private Direction direction=null;
    public void updateDirection(){
        Direction newDirection=null;
        if(directionWriter!=null) {
            if (Math.abs(dx) > Math.abs(dy)) {
                newDirection=dx>0?Direction.RIGHT:Direction.LEFT;
            } else {
                newDirection=(dy>0?Direction.DOWN:Direction.UP);
            }
            if(newDirection.opposite().equals(direction)){
                return;
            }
            direction=newDirection;
            directionWriter.setDirection(direction);
            directionNeedDisplay=true;
        }
    }
}
