package ClinetGUI.Universal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.TimeUnit;

//跑马灯标签，通过重写draw函数
public class ScrollDisplayLabel extends JLabel {
    private Thread scrollThread;
    private final int offsetStep;
    private final int period;
    private volatile int offset=0;
    public ScrollDisplayLabel(int offsetStep,int period,int padding){
        this.offsetStep=offsetStep;
        this.period=period;
        scrollThread=null;
        setOpaque(false);
        setBorder(new EmptyBorder(padding,padding,padding,padding));
        setOpaque(false);
    }
    @Override
    public synchronized void paint(Graphics graphics){
        //graphics.setColor(background);
        //graphics.fillRect(0,0,getWidth(),getHeight());
        int textlength=getFontMetrics(getFont()).stringWidth(getText());
        offset%=textlength;
        Image buffer=createImage(textlength,getHeight());
        Graphics bufferedGraphic=buffer.getGraphics();
        bufferedGraphic.drawChars(getText().toCharArray(),0,getText().toCharArray().length,0,getHeight()-getFont().getSize()/2);
        bufferedGraphic.dispose();
        graphics.drawImage(buffer,-offset,0,this);
        graphics.drawImage(buffer,textlength-offset,0,this);

    }
    public synchronized void startScroll(String text){
        if(scrollThread!=null){
            scrollThread.interrupt();
        }
        setText(text);
        offset=0;
        scrollThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        offset+=offsetStep;
                        repaint();
                        TimeUnit.MILLISECONDS.sleep(period);
                    }
                }catch (Exception ex){

                }
            }
        });
        scrollThread.start();
    }
    public synchronized void pauseScroll(){
        if(scrollThread!=null){
            scrollThread.interrupt();
            scrollThread=null;
        }
    }
    public synchronized void continueScroll(){
        if(scrollThread==null){
            scrollThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while (true){
                            offset+=offsetStep;
                            repaint();
                            TimeUnit.MILLISECONDS.sleep(period);
                        }
                    }catch (Exception ex){

                    }
                }
            });
            scrollThread.start();
        }
    }
}
