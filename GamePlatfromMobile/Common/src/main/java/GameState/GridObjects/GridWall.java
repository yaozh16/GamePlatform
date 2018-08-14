package GameState.GridObjects;


import GameState.Proxy.CanvasProxy;
import GameState.Proxy.ColorProxy;

public class GridWall implements GridMapObject {

    @Override
    public void finish() {

    }

    @Override
    public void draw(CanvasProxy g, int x, int y, int GridWidth, int GridHeight, String myAccount, int flashControl) {
        int cx=x*GridWidth;
        int cy=y*GridHeight;
        g.setColor(new ColorProxy(254, 164, 0));
        g.fillRect(cx,cy,GridWidth, GridHeight);
        g.setColor(new ColorProxy(0, 0, 0));
        for(int i=0;i<4;i++){
            int ny=cy+(int)(i*GridHeight/3);
            g.drawLine(cx,ny,cx+GridWidth,ny);
        }
        if((x+y)%2==0){//中
            g.drawLine(cx+(GridWidth/2),cy,cx+GridWidth/2,cy+GridHeight/3);
            g.drawLine(cx+(GridWidth/2),cy+GridWidth*2/3,cx+GridWidth/2,cy+GridHeight);
            g.drawLine(cx+(GridWidth/4),cy+GridHeight/3,cx+GridWidth/4,cy+(GridHeight*2/3));
            g.drawLine(cx+(GridWidth*3/4),cy+GridHeight/3,cx+GridWidth*3/4,cy+GridHeight*2/3);
            g.drawLine(cx,cy,cx,cy+GridHeight/3);
            g.drawLine(cx+GridWidth,cy,cx+GridWidth,cy+GridHeight/3);
            g.drawLine(cx,cy+GridHeight*2/3,cx,cy+GridHeight);
            g.drawLine(cx+(GridWidth),cy+GridHeight*2/3,cx+GridWidth,cy+GridHeight);
        }else {//工
            g.drawLine(cx+(GridWidth/4),cy,cx+GridWidth/4,cy+GridHeight/3);
            g.drawLine(cx+(GridWidth*3/4),cy,cx+GridWidth*3/4,cy+GridHeight/3);
            g.drawLine(cx+(GridWidth/4),cy+GridHeight*2/3,cx+GridWidth/4,cy+GridHeight);
            g.drawLine(cx+(GridWidth*3/4),cy+GridHeight*2/3,cx+GridWidth*3/4,cy+GridHeight);
            g.drawLine(cx+(GridWidth/2),cy+GridHeight*1/3,cx+GridWidth/2,cy+(GridHeight*2/3));
        }

    }
    public String toString(){
        return "W";
    }
}
