package com.example.mewidget.view;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.provider.SyncStateContract.Constants;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.FrameLayout.LayoutParams;

public class WeatherCardItemLayout extends ViewGroup {
	private Context mContext;
    private float mLastDownY; 
    private float mLastItemY;
	private WeatherCardItem curTouchItem;
	private int[] itemY;
	private int[] itemMinY;
	private int[] itemMaxY;
	private boolean isFirst = true;
	private int childHeight;
	private int touchPosition;
	private View moveView;
	private float moveViewY;
	private int lastLayoutY;
	private int openPosition;
	private Scroller mScroller;
	private WeatherCardItem aniItem;
	private CursorAdapter mAdapter;
	
	public WeatherCardItemLayout(Context context) {
		this(context, null);
	}
	
	public WeatherCardItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mScroller = new Scroller(context);
        moveViewY = 0;
        lastLayoutY=0;
        openPosition = 0;
        touchPosition = 0;
	}
	
	public void setAdapter(WeatherCardItemAdapter adapter) {
		mAdapter = adapter;
		if (mAdapter == null) return;
		updateView();
	}
	
	private void updateView(){
		int count = mAdapter.getCount();
		removeAllViews();
		for(int i=0; i<count; i++){        
			//item = new WeatherItem(this.mContext);
			WeatherCardItem item = (WeatherCardItem) mAdapter.getView(i, null, this);

			addView(item);
		}

		itemY = new int[count];
		itemMinY = new int[count];
		itemMaxY = new int[count];
		isFirst = true;
	}
	
	public void setMoveView(View view){
		this.moveView = view;
	}
	
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {
    	measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int count = getChildCount();
		if(count >0){
			for(int i=0; i< count; i++){
				WeatherCardItem childView = (WeatherCardItem)getChildAt(i);
		        int width  = childView.getMeasuredWidth();
		        int height = childView.getMeasuredHeight();
	
		        if(isFirst){
		        	childHeight = height;
		        	itemMinY[i] = i * height /3;
		        	if(i==0){
			        	itemMaxY[i] = 0;
			        	itemY[i] = 0;	
		        		//childView.moveWeahterIconTo(-122);//122 is peddingTop
		        	}
		        	else{
			        	itemMaxY[i] = height + (i-1) * height /3;	
		        		itemY[i] = height + (i-1) * height /3;
		        		childView.moveWeahterIconTo(height);
		        	}   			     	
		        }
		        else{
			        if(touchPosition == i){
			        	childView.moveWeahterIconBy((float)(itemY[touchPosition] - lastLayoutY)*3/2);
			        }
			        else if(touchPosition - 1 == i){
			        	childView.moveWeahterIconBy((float)(itemY[touchPosition] - lastLayoutY)/2);
			        }
		        }
	            Log.i("onLayout", "itemY[i]="+itemY[i]);
		        childView.layout(left, itemY[i], left+width, itemY[i]+height);
		        
			}
	    	lastLayoutY = itemY[touchPosition];
			isFirst = false;	
		}
//    	//移动天气图标
//    	if(this.moveView != null){
//    		moveViewY -= (float)(itemY[touchPosition] - lastLayoutY)/2;
//            Log.i("onLayout", "onLayout:itemY[touchPosition]="+itemY[touchPosition]);
//            Log.i("onLayout", "onLayout:lastLayoutY="+lastLayoutY);
//            Log.i("onLayout", "onLayout:moveViewY="+moveViewY);
//            ViewHelper.setTranslationY(this.moveView, moveViewY);
//    		lastLayoutY = itemY[touchPosition];
//    	}	
//		if(!mScroller.isFinished()){
//			post(new Runnable(){
//				@Override
//				public void run() {
//					requestLayout();
//				}
//			});
//			
//		}
	}
	
  @Override
  public void computeScroll() {
      if (mScroller.computeScrollOffset()) {
      	itemY[touchPosition] = mScroller.getCurrY();
        Log.i("computeScroll", "computeScroll="+mScroller.getCurrY());
        this.requestLayout();
      } 
  }
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

//        touchPosition = findView(event);
//        Log.i("onInterceptTouchEvent", "touchPosition="+touchPosition);
//        Log.i("onInterceptTouchEvent", "openPosition="+openPosition);
//        if(touchPosition > 0 && (touchPosition == openPosition || touchPosition == openPosition + 1)){
//        	return super.onInterceptTouchEvent(event);
//        }
        return true;
		//return super.onInterceptTouchEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
        //if(touchPosition > 0 && (touchPosition == openPosition || touchPosition == openPosition + 1)){
	        int action = event.getAction();
	        if (mScroller.computeScrollOffset()){
	        	return false;
	        }
	        switch(action){
	        case MotionEvent.ACTION_DOWN:
	            mLastDownY = event.getY();
	            touchPosition = findView(event);
	            Log.i("onTouchEvent", "onTouchEventACTION_DOWN:touchPosition="+touchPosition);
	        	if(touchPosition > 0 && (touchPosition == openPosition || touchPosition == openPosition + 1)){
		            mLastItemY = itemY[touchPosition];
		            curTouchItem = (WeatherCardItem)getChildAt(touchPosition);
		    		lastLayoutY = itemY[touchPosition];
		            return true;
	        	}
	        	return false;
	        case MotionEvent.ACTION_MOVE:
	            Log.i("onTouchEvent", "onTouchEventACTION_MOVE:touchPosition="+touchPosition);
		        Log.i("onTouchEvent", "onTouchEventACTION_MOVE:openPosition="+openPosition);
	        	if(touchPosition > 0 && (touchPosition == openPosition || touchPosition == openPosition + 1)){
		            float deltaY = event.getY() - mLastDownY; 
			        Log.i("onTouchEvent", "onTouchEventACTION_MOVE:deltaY="+deltaY);
		            if(touchPosition == openPosition + 1){
		            	aniItem = (WeatherCardItem)getChildAt(touchPosition);
		            	aniItem.setWeatherLinearvisable(false);
		            }
		            if(touchPosition > 0 && curTouchItem != null && deltaY != 0){
		            	itemY[touchPosition] += deltaY;
		            	itemY[touchPosition] = itemY[touchPosition] < itemMinY[touchPosition] ? itemMinY[touchPosition] :itemY[touchPosition];
		            	itemY[touchPosition] = itemY[touchPosition] > itemMaxY[touchPosition] ? itemMaxY[touchPosition] :itemY[touchPosition];
		                this.requestLayout();
		            }
		            
		            mLastDownY = event.getY(); 
		            return true;
	        	}
        		return false;
	        case MotionEvent.ACTION_UP:
		          Log.i("onTouchEvent", "onTouchEventACTION_UP:touchPosition="+touchPosition);
		          Log.i("onTouchEvent", "onTouchEventACTION_UP:openPosition="+openPosition);
	        	if(touchPosition > 0 && (touchPosition == openPosition || touchPosition == openPosition + 1)){
		        	float sumDeltaY = itemY[touchPosition] - mLastItemY;

		        	if(sumDeltaY > 0){
		            	if( Math.abs(sumDeltaY) > childHeight/3 ){
		            		moveToMax(touchPosition);
		            	}
		            	else{
		            		moveToMin(touchPosition);
		            	}
		        	}
		        	else if(sumDeltaY < 0){
		            	if( Math.abs(sumDeltaY) > childHeight/3 ){
		            		moveToMin(touchPosition);
		            	}
		            	else{
		            		moveToMax(touchPosition);
		            	}
		        	}
		            mLastDownY = 0; 
	        	}
	        	break;
	        }
        //}
		return super.onTouchEvent(event);
	}    
	
	private void moveToMin(int position){
        final int delta = itemMinY[position]-itemY[position];
        final int duration = Math.abs(delta) * 5;
        mScroller.startScroll(0, itemY[position], 0, delta, duration);
    	openPosition = position;
        this.requestLayout();
        aniItem = (WeatherCardItem)getChildAt(openPosition);
        aniItem.startWeatherInfoAni();
	}
	
	private void moveToMax(int position){
        final int delta = itemMaxY[position]-itemY[position];
        final int duration = Math.abs(delta) * 5;
        mScroller.startScroll(0, itemY[position], 0, delta, duration);
    	openPosition = position - 1;
        this.requestLayout();
	}
	
	private int findView(MotionEvent event){
	    int childCount =getChildCount();  
	    WeatherCardItem childView;
	    for(int i=0; i<childCount;i++){
	        childView = (WeatherCardItem)getChildAt(i);
	        int[] loc = new int[2];
	        childView.getLocationOnScreen(loc);
	        int left = loc[0], top = loc[1];
	        
	        if(event.getRawY() < top){
	            return i-1;
	        }
	    }
	    return childCount-1;
	}
}
