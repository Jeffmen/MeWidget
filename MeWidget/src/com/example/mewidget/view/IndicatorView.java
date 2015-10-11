package com.example.mewidget.view;


import com.example.mewidget.R;
import com.example.mewidget.Utils;
import com.example.mewidget.view.HorizontalScrollView.OnChanger;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CursorAdapter;

public class IndicatorView extends View implements OnChanger{

	private Context mContext;
	private static final int RADIUS = 4;//dp
	private float spaceX, radius, gap;// 网格区域左上右下两点坐标
	private CursorAdapter mAdapter;
	private Paint pointPaint;
	private int position = 0;
	private int viewWidthSize, viewHeightSize;// 控件尺寸
	private int selectColor;
	private int count;
	
	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			invalidate();
			requestLayout();
		}
		
	};
	
	public IndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext =context;
		selectColor = context.getResources().getColor(R.color.date_bg_color);
		pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		pointPaint.setStyle(Paint.Style.FILL);
	}

	public void setAdapter(CursorAdapter adapter) {
		if(mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
	}
	
	public void setPosition(int value){
		position = value;
		invalidate();
	}
	
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {		
		// 获取控件尺寸
		viewWidthSize = width;
		viewHeightSize = height;
		radius = Utils.dpToPx(mContext, RADIUS);
		gap = radius * 2F;
		spaceX = radius * 2F + gap;
		count = (int)(viewWidthSize/spaceX);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(mAdapter != null){
			int lines = (int)Math.ceil((float)mAdapter.getCount()/count);
			int topStep = viewHeightSize/(lines+1);
			for(int j = 0; j < lines; j++){
                int step;
				if(j==lines-1){
					step = mAdapter.getCount()%count;
				}
				else{
					step = count;
				}
				float x = (viewWidthSize - step * radius * 2 - (step-1) * gap)/2F + radius;
				float top = topStep*(j+1);
				for (int i = 0; i < step; i++) {
					if(position == j*count + i){
						pointPaint.setColor(selectColor);
					}
					else{
						pointPaint.setColor(Color.WHITE);
					}
		        	canvas.drawCircle(x, top, radius, pointPaint);
					x += spaceX;
				}			
			}
		}
	}

	@Override
	public void onPositionChange(int value) {
         position = value;
         invalidate();
	}
}
