package com.example.mewidget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class AbsoluteView extends LinearLayout {

    public AbsoluteView(Context context) {
        super(context);
    }

    public AbsoluteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsoluteView(Context context, AttributeSet attrs,
        int defStyle) {
        super(context, attrs, defStyle);
    }
        
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//        MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);        
    	int hightSize = wm.getDefaultDisplay().getHeight()/2;
        int expandSpec = MeasureSpec.makeMeasureSpec(hightSize, MeasureSpec.EXACTLY);
        
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
