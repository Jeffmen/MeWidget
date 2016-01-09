package com.example.mewidget.view;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.Scroller;

	public class HorizontalScrollView extends AdapterView<CursorAdapter> {
		
		private Context mContext;
		public boolean mAlwaysOverrideTouch = true;
		protected CursorAdapter mAdapter;
		private int mLeftViewIndex = -1;
		private int mRightViewIndex = 0;
		protected int mCurrentX;
		protected int mNextX;
		private int mMaxX = Integer.MAX_VALUE;
		private int mDisplayOffset = 0;
		protected Scroller mScroller;
		private GestureDetector mGesture;
		private Queue<View> mRemovedViewQueue = new LinkedList<View>();
		private boolean mDataChanged = false;
		private int screenWidth ;
	    private boolean isScrolling = false;
	    private int sumDistanceX = 0;
	    private int currentPosition = 0;
	    private OnChanger positionChanger;

		public interface OnChanger{
			public void onPositionChange(int value);
			public void setAdapter(CursorAdapter adapter);
		}
		
		public HorizontalScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
			mContext = context;
			initView();
		}
		
		private synchronized void initView() {
			mLeftViewIndex = -1;
			mRightViewIndex = 0;
			mDisplayOffset = 0;
			mCurrentX = 0;
			mNextX = 0;
			mMaxX = Integer.MAX_VALUE;
			mScroller = new Scroller(getContext());
			mGesture = new GestureDetector(getContext(), mOnGesture);
//			WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);  
//	        DisplayMetrics outMetrics = new DisplayMetrics();  
//	        wm.getDefaultDisplay().getMetrics(outMetrics);  
//	        screenWidth = outMetrics.widthPixels;
		}

		private DataSetObserver mDataObserver = new DataSetObserver() {

			@Override
			public void onChanged() {
				synchronized(HorizontalScrollView.this){
					mDataChanged = true;
				}
				invalidate();
				requestLayout();
			}

			@Override
			public void onInvalidated() {
				reset();
				invalidate();
				requestLayout();
			}
			
		};

		@Override
		public CursorAdapter getAdapter() {
			return mAdapter;
		}

		@Override
		public View getSelectedView() {
			return null;
		}

		@Override
		public void setAdapter(CursorAdapter adapter) {
			if(mAdapter != null) {
				mAdapter.unregisterDataSetObserver(mDataObserver);
				if(currentPosition < 0 &&  currentPosition >= mAdapter.getCount()){
					currentPosition = 0;
				}
			}
			mAdapter = adapter;
			if(positionChanger != null){
				positionChanger.setAdapter(mAdapter);
			}
			mAdapter.registerDataSetObserver(mDataObserver);
			
			int oldNextX = currentPosition * screenWidth;
			reset();
			mNextX = oldNextX;
		}
		
		private synchronized void reset(){
			initView();
			removeAllViewsInLayout();
	        requestLayout();
		}

		public void setIndicator(OnChanger changer){
			positionChanger = changer;
		}
		
		@Override
		public void setSelection(int position) {
			currentPosition = position;	
			//positionChanger.onPositionChange(currentPosition);
			if(position >=0 && mAdapter != null && position < mAdapter.getCount()){
				mNextX = position * screenWidth;		
				if(positionChanger != null){
					positionChanger.onPositionChange(currentPosition);
				}
				this.requestLayout();
			}
		}
		
		public int getSelection(){
			return currentPosition;
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	        setMeasuredDimension(screenWidth,
//	                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			screenWidth = w;
		}
		
		@Override
		protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);

			if(mAdapter == null){
				return;
			}
			
			if(mDataChanged){
				int oldCurrentX = mCurrentX;
				initView();
				removeAllViewsInLayout();
				mNextX = oldCurrentX;
				mDataChanged = false;
			}

			if(mScroller.computeScrollOffset()){
				int scrollx = mScroller.getCurrX();
				mNextX = scrollx;
			}
			
//			if(mNextX <= 0){
//				mNextX = 0;
//				mScroller.forceFinished(true);
//			}
//			if(mNextX >= mMaxX) {
//				mNextX = mMaxX;
//				mScroller.forceFinished(true);
//			}
			
			int dx = mCurrentX - mNextX;
			
			removeNonVisibleItems(dx);
			fillList(dx);
			positionItems(dx);
			
			mCurrentX = mNextX;
			
			if(!mScroller.isFinished()){
				post(new Runnable(){
					@Override
					public void run() {
						requestLayout();
					}
				});
			}
			int position = new BigDecimal(mNextX/screenWidth).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			if(currentPosition != position){
				currentPosition = position;
				if(positionChanger != null){
					positionChanger.onPositionChange(currentPosition);
				}
			}
		}

//	    @Override
//	    public void computeScroll() {
//	        if (mScroller.computeScrollOffset()) {
//	            requestLayout();
//	        } 
//	    }
	    
		private void fillList(final int dx) {
			int edge = 0;
			View child = getChildAt(getChildCount()-1);
			if(child != null) {
				edge = child.getRight();
			}
			fillListRight(edge, dx);
			
			edge = 0;
			child = getChildAt(0);
			if(child != null) {
				edge = child.getLeft();
			}
			fillListLeft(edge, dx);
		}
		
		private void fillListRight(int rightEdge, final int dx) {
			while(rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
				//currentPosition = mRightViewIndex;
				View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
				addAndMeasureChild(child, -1);
				rightEdge += child.getMeasuredWidth();
				
				if(mRightViewIndex == mAdapter.getCount()-1) {
					mMaxX = mCurrentX + rightEdge - getWidth();
				}
				
				if (mMaxX < 0) {
					mMaxX = 0;
				}
				mRightViewIndex++;
			}
		}

		private void addAndMeasureChild(final View child, int viewPos) {
			LayoutParams params = child.getLayoutParams();
			if(params == null) {
				params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			}
			child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
				          MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));

			addViewInLayout(child, viewPos, params, true);
		}
		
		private void fillListLeft(int leftEdge, final int dx) {
			while(leftEdge + dx > 0 && mLeftViewIndex >= 0) {
				//currentPosition = mLeftViewIndex;
				View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
				addAndMeasureChild(child, 0);
				leftEdge -= child.getMeasuredWidth();
				mLeftViewIndex--;
				mDisplayOffset -= child.getMeasuredWidth();
			}
		}
		
		private void removeNonVisibleItems(final int dx) {
			View child = getChildAt(0);
			while(child != null && child.getRight() + dx <= 0) {
				mDisplayOffset += child.getMeasuredWidth();
				//mRemovedViewQueue.offer(child);
				removeViewInLayout(child);
				mLeftViewIndex++;
				child = getChildAt(0);
			}
			
			child = getChildAt(getChildCount()-1);
			while(child != null && child.getLeft() + dx >= getWidth()) {
				//mRemovedViewQueue.offer(child);
				removeViewInLayout(child);
				mRightViewIndex--;
				child = getChildAt(getChildCount()-1);
			}
		}
		
		private void positionItems(final int dx) {
			if(getChildCount() > 0){
				mDisplayOffset += dx;
				int left = mDisplayOffset;
				for(int i=0;i<getChildCount();i++){
					View child = (View)getChildAt(i);
					int childWidth = child.getMeasuredWidth();
					child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
					left += childWidth + child.getPaddingRight();
				}
			}
		}
		
		public synchronized void scrollTo(int x) {
			mScroller.startScroll(mNextX, 0, x - mNextX, 0);
			requestLayout();
		}

		public synchronized void scrollBy(int x) {
			mScroller.startScroll(mNextX, 0, x, 0);
			requestLayout();
		}
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			boolean handled = super.dispatchTouchEvent(ev);
			handled |= mGesture.onTouchEvent(ev);
			switch(ev.getAction()){
	        case MotionEvent.ACTION_UP:
	        	if(isScrolling){
					if(mNextX <= 0 || mNextX >= mMaxX){
						HorizontalScrollView.this.scrollBy(-sumDistanceX);
					}
					else{
						if(Math.abs(sumDistanceX) < screenWidth/2){
							HorizontalScrollView.this.scrollBy(-sumDistanceX);
						}
						else{
							HorizontalScrollView.this.scrollBy(sumDistanceX > 0 ? screenWidth-sumDistanceX : -screenWidth-sumDistanceX);
						}
					}
	        	}
	        	break;
			}
			return handled;
		}
		
		protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
			synchronized(HorizontalScrollView.this){
				mScroller.fling(mNextX, 0, (int)-velocityX, 0, 0, mMaxX, 0, 0);
			}
			requestLayout();
			return true;
		}
		
		protected boolean onDown(MotionEvent e) {
			mScroller.forceFinished(true);
			return true;
		}
		
		private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				isScrolling = false;
				sumDistanceX = 0;
				return HorizontalScrollView.this.onDown(e);
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) { 
				//return HorizontalScrollView.this.onFling(e1, e2, velocityX, velocityY);
				if (isScrolling) {
					if(mNextX <= 0 || mNextX >= mMaxX){
						HorizontalScrollView.this.scrollBy(-sumDistanceX);
					}
					else{
						HorizontalScrollView.this.scrollBy(sumDistanceX > 0 ? screenWidth-sumDistanceX : -screenWidth-sumDistanceX);
					}
					isScrolling = false;
					return true;
				}
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	            float angle = Math.abs((e2.getRawY() - e1.getRawY()) / (e2.getRawX() - e1.getRawX()));
	            angle = (float)Math.toDegrees(Math.atan(angle));
	            
				if (angle < 60) {
					//Log.i("OnGestureListener","onScroll:"+(int)distanceX);
					synchronized(HorizontalScrollView.this){
						if(mNextX <= 0 || mNextX >= mMaxX){
							mNextX += (int)distanceX/2;
							sumDistanceX += (int)distanceX/2;
						}
						else{
							mNextX += (int)distanceX;
							sumDistanceX += (int)distanceX;
						}
					}
					requestLayout();
					isScrolling = true;
					return true;
				}
				return false;
			}
		};
	}