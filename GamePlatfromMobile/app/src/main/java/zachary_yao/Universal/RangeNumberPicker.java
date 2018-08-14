package zachary_yao.Universal;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import zachary_yao.GamePlatformMobile.R;

/**
 * Created by yaozh16 on 18-8-13.
 */

public class RangeNumberPicker extends NumberPicker {
    private int max,min,init;
    public RangeNumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes =context.obtainStyledAttributes(attrs,R.styleable.RangeNumberPicker);
        max=attributes.getInt(R.styleable.RangeNumberPicker_maxValue,5);
        min=attributes.getInt(R.styleable.RangeNumberPicker_minValue,1);
        init=attributes.getInt(R.styleable.RangeNumberPicker_initValue,2);
        setMaxValue(max);
        setMinValue(min);
        setValue(init);
        attributes.recycle();
        setOnValueChangedListener(new OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setTooltipText(""+newVal);
            }
        });
    }
}
