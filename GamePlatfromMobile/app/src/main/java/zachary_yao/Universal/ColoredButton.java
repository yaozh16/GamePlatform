package zachary_yao.Universal;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by yaozh16 on 18-8-12.
 */

public class ColoredButton extends android.support.v7.widget.AppCompatButton {
    public ColoredButton(Context context) {
        super(context);
    }

    public ColoredButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColoredButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public static ColoredButton newInstance(Context context, String text, int rx, int ry, int color,int fontsize){
        ColoredButton coloredButton=new ColoredButton(context);
        coloredButton.setText(text);
        coloredButton.setTextColor(color);
        coloredButton.setTextSize(fontsize);
        coloredButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1));
        return coloredButton;
    }
    public ColoredButton setParameter(String text, int rx, int ry, int color,int fontsize){
        setText(text);
        setTextColor(color);
        setTextSize(fontsize);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1));
        return this;
    }
    @Override
    public void setBackgroundColor(int color){
        super.setBackgroundColor(Color.argb(127,Color.red(color),Color.green(color),Color.blue(color)));
    }
}
