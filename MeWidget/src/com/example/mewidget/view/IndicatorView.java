package com.example.mewidget.view;


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
	private static final float TOP = 1 / 2F;
	private static final int RADIUS = 4;//dp
	private float left, spaceX, top, radius, gap;// 网格区域左上右下两点坐标
	private CursorAdapter mAdapter;
	private Paint pointPaint;
	private int position = 0;
	private int viewWidthSize;// 控件尺寸
	
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
		pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		pointPaint.setStyle(Paint.Style.FILL);
		pointPaint.setColor(Color.WHITE);
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
		top = height * TOP;
		radius = Utils.dpToPx(mContext, RADIUS);
		gap = radius * 2F;
		spaceX = radius * 2F + gap;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(mAdapter != null){
			left = (viewWidthSize - mAdapter.getCount() * radius * 2 - (mAdapter.getCount()-1) * gap)/2F + radius;
			for (int i = 0; i < mAdapter.getCount(); i++) {
				float x = left + spaceX * i;
				if(position == i){
					pointPaint.setColor(Color.GREEN);
				}
				else{
					pointPaint.setColor(Color.WHITE);
				}
	        	canvas.drawCircle(x, top, radius, pointPaint);
			}
		}
	}

	@Override
	public void onPositionChange(int value) {
         position = value;
         invalidate();
	}
}
