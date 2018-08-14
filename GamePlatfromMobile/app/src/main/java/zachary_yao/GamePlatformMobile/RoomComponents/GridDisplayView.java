package zachary_yao.GamePlatformMobile.RoomComponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import GameState.GridMap;
import GameState.GridMapControl.GridMapReader;
import GameState.GridObjects.GridMapObject;
import GameState.Proxy.CanvasProxy;
import GameState.Proxy.ColorProxy;
import zachary_yao.ClientEngine.Configs.ClientConfig;
import zachary_yao.GamePlatformMobile.CanvasProxy_AndroidCanvas;
import zachary_yao.GamePlatformMobile.R;

/**
 * Created by yaozh16 on 18-8-12.
 */

public class GridDisplayView extends View {

    Paint backgroundPaint=new Paint();
    public GridDisplayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        backgroundPaint.setColor(getResources().getColor(R.color.opacityGreen));
    }

    private GridMapReader gridMapReader=null;
    private ClientConfig clientConfig=null;
    public GridDisplayView setViewGridMapReader(GridMapReader gridMapReader, ClientConfig clientConfig){
        this.gridMapReader=gridMapReader;
        this.clientConfig=clientConfig;
        return this;
    }

    private CanvasProxy mCanvasProxy=null;
    private Canvas mBufferCanvas=null;
    private Bitmap mBufferBitmap=null;
    private int offsetHeight,offsetWidth;
    private int flashControl=0;
    public synchronized void repaint() {
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
        mCanvasProxy=new CanvasProxy_AndroidCanvas(mBufferCanvas);

        GridMap localMap;
        if (gridMapReader != null&&((localMap=gridMapReader.getGridMapCopy())!= null)) {
            int GridHeight = getHeight() / localMap.height;
            int GridWidth = getWidth() / localMap.width;
            int GridSize=Math.min(GridHeight,GridWidth);
            offsetHeight=localMap.height*GridSize;
            offsetWidth=localMap.width*GridSize;
            mCanvasProxy.setColor(new ColorProxy(115, 255, 36));
            for (int y = 0; y < localMap.height; y++) {
                for (int x = 0; x < localMap.width; x++) {
                    if (((x + y) % 2 == 0)) {
                    }
                    mCanvasProxy.fillRect(x * GridSize, y * GridSize, GridSize, GridSize);
                }
            }

            mCanvasProxy.setColor(new ColorProxy(25, 148, 15));
            for (int y = 0; y < localMap.height; y++) {
                for (int x = 0; x < localMap.width; x++) {
                    if (((x + y) % 2 != 0)) {
                        mCanvasProxy.fillRect(x * GridSize, y * GridSize, GridSize, GridSize);
                    }
                }
            }
            for (int y = 0; y < localMap.height; y++) {
                for (int x = 0; x < localMap.width; x++) {
                    localMap.gridMapObjects.get(x + y * localMap.width).draw(mCanvasProxy, x, y, GridSize, GridSize,clientConfig.getAccount(),flashControl);
                }
            }
            updateOccured=true;
        }
    }

    private volatile boolean updateOccured=false;

    @Override
    public synchronized void invalidate(){
        super.invalidate();
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        try {
            if (updateOccured) {
                canvas.drawBitmap(mBufferBitmap,new Rect(0,0,offsetWidth,offsetHeight),new Rect(getLeft()+(getWidth()-offsetWidth)/2,getTop()+(getHeight()-offsetHeight)/2,getLeft()+(getWidth()+offsetWidth)/2,getTop()+(getHeight()+offsetHeight)/2),null);
                flashControl=(flashControl+1)% GridMapObject.flashControlMax;
                updateOccured=false;
            }else {
                if(mBufferBitmap==null){
                    Paint paint=new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(getWidth()/10);
                    //计算文字所在矩形，可以得到宽高
                    Rect rect = new Rect();
                    String str="未开始";
                    paint.getTextBounds(str, 0, str.length(), rect);
                    canvas.drawText(str,(getWidth()-rect.width())/2,(getHeight()-rect.height())/2,paint);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
