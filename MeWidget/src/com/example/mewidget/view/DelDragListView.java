package com.example.mewidget.view;

import java.util.LinkedList;
import java.util.Queue;

import com.example.mewidget.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class DelDragListView extends AdapterView<CursorAdapter> {
	private Context mContext;
    public static int ANIMATION_DURATION = 300;
	protected CursorAdapter mAdapter;
	private int mTopViewIndex = -1;
	private int mDownViewIndex = 0;
	protected int mCurrentY;
	protected int mNextY;
	private float mMoveX;
	private int mMaxY = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;
    private int openPosition = 0;
    private int touchPosition = 0;
    private View openView = null, touchView;
    private int moveStep = 0;
    private boolean notMove = false;
    private boolean openViewExist = false;
    private View lastView;
    private OnItemClickListener lastViewClicked;
    private boolean isXMove = false;
    
	
	public interface OnDrager {
		public void startWeatherInfoAni();
		public void moveWeahterIconTo(float deltaY);
		public void moveWeahterIconBy(float deltaY);
	}

	public DelDragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	private synchronized void initView() {
		mTopViewIndex = -1;
		mDownViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentY = 0;
		mNextY = 0;
		mMaxY = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
	    openPosition = 0;
	    touchPosition = 0;
	    openView = null;
	    moveStep = 0;
	    lastNowY = 0;
	}
	
	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}
	
	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
		mOnItemClicked = listener;
	}
	
	public void setLastItemClickListener(AdapterView.OnItemClickListener listener){
		lastViewClicked = listener;
	}
	
	@Override
	public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized(DelDragListView.this){
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
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		int oldNextY = mNextY;
		int oldOpenPosition = openPosition;
		reset();
		mNextY = oldNextY;
		openPosition = oldOpenPosition;
	}
	
	private void reset(){
		initView();
		removeAllViewsInLayout();
        requestLayout();
	}

	@Override
	public void setSelection(int position) {
	}
	
	public void setLastView(View view){
		lastView = view;
	}
	
	private void addAndMeasureChild(final View child, int viewPos, int position) {
		LayoutParams params = child.getLayoutParams();
		if(params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
//		if(position == mAdapter.getCount()) {
			addViewInLayout(child, viewPos, params, true);
//		}
//		else{
//			View root = LayoutInflater.from(mContext).inflate(R.layout.draglistview_item_layout, null);
//			LinearLayout firstView = (LinearLayout)root.findViewById(R.id.first);
//			LinearLayout secondView = (LinearLayout)root.findViewById(R.id.second);
//			firstView.addView(child);
//			secondView.addView(LayoutInflater.from(mContext).inflate(R.layout.draglistview_hander, null));
//			addViewInLayout(root, viewPos, params, true);
//		}
		
		if(position == openPosition - 1){
			child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(getHeight()/6, MeasureSpec.EXACTLY));
			if(child instanceof OnDrager){
				((OnDrager)child).moveWeahterIconTo(-getHeight()/6);
			}
		}
		else if(position == openPosition){
			openView = child;
			child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(getHeight()/2, MeasureSpec.EXACTLY));
		}
		else{
			child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(getHeight()/6, MeasureSpec.EXACTLY));
			if(child instanceof OnDrager){
				((OnDrager)child).moveWeahterIconTo(getHeight()/2);
			}
		}
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if(mAdapter == null){
			return;
		}
		
		if(mDataChanged){
			int oldCurrentY = mCurrentY;
			initView();
			removeAllViewsInLayout();
			mNextY = oldCurrentY;
			mDataChanged = false;
		}

		if(mScroller.computeScrollOffset()){
			mNextY = mScroller.getCurrY();
		}
		
		if(mNextY <= 0){
			mNextY = 0;
			mScroller.forceFinished(true);
		}
		if(mNextY >= mMaxY) {
			mNextY = mMaxY;
			mScroller.forceFinished(true);
		}
		
		int dy = mCurrentY - mNextY;
		if(notMove){
			fillList(0);
			positionItems(0);
			removeNonVisibleItems(0);
			notMove = false;
		}
		else{
			removeNonVisibleItems(dy);
			fillList(dy);
			positionItems(dy);
		}
		
		mCurrentY = mNextY;
		
		if(!mScroller.isFinished()){
			post(new Runnable(){
				@Override
				public void run() {
					requestLayout();
				}
			});
			
		}
	}
	
	private void fillList(final int dy) {
		int edge = 0;
		View child = getChildAt(getChildCount()-1);
		if(child != null) {
			edge = child.getBottom();
		}
		fillListDown(edge, dy);
		
		edge = 0;
		child = getChildAt(0);
		if(child != null) {
			edge = child.getTop();
		}
		fillListTop(edge, dy);
	}
	
	private void fillListDown(int bottomEdge, final int dy) {
		View child;
		while(bottomEdge + dy < getHeight() && mDownViewIndex <= mAdapter.getCount()) {
			if(mDownViewIndex == mAdapter.getCount()) {
				child = lastView;
			}
			else{
				child = mAdapter.getView(mDownViewIndex, mRemovedViewQueue.poll(), null);
			}
			addAndMeasureChild(child, -1, mDownViewIndex);
			bottomEdge += child.getMeasuredHeight();
			if(mDownViewIndex == mAdapter.getCount()) {
				mMaxY = mCurrentY + bottomEdge - getHeight();
			}
			if (mMaxY < 0) {
				mMaxY = 0;
			}
			mDownViewIndex++;
		}
	}
	
	private void fillListTop(int topEdge, final int dy) {
		while(topEdge + dy > 0 && mTopViewIndex >= 0) {
			View child = mAdapter.getView(mTopViewIndex, mRemovedViewQueue.poll(), null);
			addAndMeasureChild(child, 0, mTopViewIndex);			
			topEdge -= child.getMeasuredHeight();
			mDisplayOffset -= child.getMeasuredHeight();
			mTopViewIndex--;
		}
	}
	
	private void removeNonVisibleItems(final int dy) {
		View child = getChildAt(0);
		while(child != null && child.getBottom() + dy <= 0) {
			if(openView == child && openViewExist){
				break;
			}
			mDisplayOffset += child.getMeasuredHeight();
			//mRemovedViewQueue.offer(child);
			if(openPosition == mTopViewIndex+1){
				openView = null;
			}
			removeViewInLayout(child);
			mTopViewIndex++;
			child = getChildAt(0);	
		}
		child = getChildAt(getChildCount()-1);
		while(child != null && child.getTop() + dy >= getHeight()) {
			//mRemovedViewQueue.offer(child);
			if(openPosition == mDownViewIndex-1){
				openView = null;
			}
			removeViewInLayout(child);
			mDownViewIndex--;
			child = getChildAt(getChildCount()-1);
		}
	}
	
	private void positionItems(final int dy) {
		if(getChildCount() > 0){
			mDisplayOffset += dy;
			int top = mDisplayOffset;
			for(int i=0,j=mTopViewIndex+1;i<getChildCount();i++,j++){
				View child = getChildAt(i);
				int childHeight = child.getMeasuredHeight();

				child.layout(0, top, child.getMeasuredWidth(), top + childHeight);
				top += childHeight + child.getPaddingBottom();
				if(touchPosition-1 == openPosition){
			        if(touchPosition == j && child instanceof OnDrager){
			        	((OnDrager)child).moveWeahterIconBy((float)-moveStep*3/2);
			        }
			        else if(openPosition == j && child instanceof OnDrager){
			        	((OnDrager)child).moveWeahterIconBy((float)-moveStep/2);
			        }
				}
				else if(touchPosition+1 == openPosition){
					if(touchPosition == j && child instanceof OnDrager){
			        	((OnDrager)child).moveWeahterIconBy((float)moveStep/2);
			        }
			        else if(openPosition == j && child instanceof OnDrager){
			        	((OnDrager)child).moveWeahterIconBy((float)moveStep*3/2);
			        }
				}
			}
		}
	}
	
	public synchronized void scrollTo(int y) {
		mScroller.startScroll(0, mNextY, 0, y - mNextY);
		requestLayout();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		switch(ev.getAction()){
        case MotionEvent.ACTION_UP:
        	if(isXMove && mMoveX > 0 && touchView != null){
            	if(mMoveX > touchView.getWidth() * 2/3){
    	            ViewPropertyAnimator.animate(touchView)  
                    .translationX(-touchView.getWidth())//X轴方向的移动距离  
                    .alpha(0)  
                    //.setDuration(ANIMATION_DURATION)  
                    .setListener(new AnimatorListenerAdapter() {  
                        @Override  
                        public void onAnimationEnd(Animator animation) {  
                            //Item滑出界面之后执行删除  
                        	deleteItemView();  
                        }  
                    }); 
            	}
            	else{
            		pullItemViewBack();
            	}
        	}
        	break;
		}
		return handled;
	}
	
    private void pullItemViewBack(){
    	if(touchView != null){
	        ViewPropertyAnimator.animate(touchView)  
	        .translationX(0)  
	        .alpha(1)     
	        .setDuration(ANIMATION_DURATION).setListener(null); 
	        touchView = null;
    	}
    }
    
    private void deleteItemView(){ 
    	final int originalHeight = touchView.getHeight();
    	final int nextPosition;
    	if(touchPosition+1 == mAdapter.getCount()){
    		nextPosition = touchPosition-1;
    	}
    	else{
    		nextPosition = touchPosition+1;
    	}
    	final View nextView = getChildAt(nextPosition - mTopViewIndex - 1);
    	ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0); 
	    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
	        @Override  
	        public void onAnimationUpdate(ValueAnimator valueAnimator) { 
	            int nowY = (Integer) valueAnimator.getAnimatedValue();
                if(touchView != null)
		            touchView.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
							          MeasureSpec.makeMeasureSpec(nowY, MeasureSpec.EXACTLY));
                if(nextView != null)
		            nextView.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
							          MeasureSpec.makeMeasureSpec(originalHeight/3 + (originalHeight-nowY)*2/3, MeasureSpec.EXACTLY));
	        	requestLayout();
	        }  
	    });          
	    animator.addListener(new AnimatorListenerAdapter() {        	
	    	@Override  
            public void onAnimationStart(Animator animation) {  
        		if(nextView instanceof OnDrager){
        			((OnDrager)nextView).moveWeahterIconTo(0);
        		}
            } 
	        @Override  
	        public void onAnimationEnd(Animator animation) {  
                ViewHelper.setAlpha(touchView, 1f);  
                ViewHelper.setTranslationX(touchView, 0);
                touchView.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
						          MeasureSpec.makeMeasureSpec(originalHeight/3, MeasureSpec.EXACTLY));
                if(touchView instanceof OnDrager){
    				((OnDrager)touchView).moveWeahterIconTo(-getHeight()/6);
    			}
                if(nextView != null){
	            	openView = nextView;
	            	openPosition = nextPosition;
                }
	        }  
	    });
	    animator.setDuration(ANIMATION_DURATION);
	    animator.start(); 
    }
    
	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
		synchronized(DelDragListView.this){
			mScroller.fling(0, mNextY, 0, (int)-velocityY, 0, 0, 0, mMaxY);
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
			mMoveX = 0;
			isXMove = false;
			touchPosition = pointToPosition((int)e.getX(), (int)e.getY());
			touchView = getChildAt(touchPosition - mTopViewIndex - 1);
			return DelDragListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(!isXMove){
				return DelDragListView.this.onFling(e1, e2, velocityX, velocityY);
			}
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
            float angle = Math.abs((e2.getRawY() - e1.getRawY()) / (e2.getRawX() - e1.getRawX()));
            angle = (float)Math.toDegrees(Math.atan(angle));
			synchronized(DelDragListView.this){
				if(angle > 45){
					mNextY += (int)distanceY;
				}
				else if(touchPosition == openPosition){
					isXMove = true;
					mMoveX += distanceX;
			        if(mMoveX > 0){ //左滑移动
			            ViewHelper.setTranslationX(touchView, -mMoveX);  
			            // 透明度渐变  
			            ViewHelper.setAlpha(touchView, Math.max(0f, Math.min(1f, 1f - 1.2f * Math.abs(mMoveX)/ touchView.getWidth()))); 
			        }
				}
			}
			requestLayout();
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			/*
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				if(lastView == child){
					lastViewClicked.onItemClick(DragListView.this, child, mAdapter.getCount(), -1);
				}
				else if (isEventWithinView(e, child)) {
					int pos = mTopViewIndex + 1 + i;
					touchPosition = pos;
					if(touchPosition != openPosition){
						expendViewHeight(child, pos);
					}
					else{
						if(mOnItemClicked != null){
							mOnItemClicked.onItemClick(DragListView.this, child, pos, mAdapter.getItemId(pos));
						}
						if(mOnItemSelected != null){
							mOnItemSelected.onItemSelected(DragListView.this, child, pos, mAdapter.getItemId(pos));
						}
					}
					break;
				}
			}*/
            if(touchView == null){
            	return false;
            }
			if(lastView == touchView){
				lastViewClicked.onItemClick(DelDragListView.this, touchView, mAdapter.getCount(), -1);
			}
			else{
				if(touchPosition != openPosition){
					expendViewHeight(touchView, touchPosition);
				}
				else{
					if(mOnItemClicked != null){
						mOnItemClicked.onItemClick(DelDragListView.this, touchView, touchPosition, mAdapter.getItemId(touchPosition));
					}
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(DelDragListView.this, touchView, touchPosition, mAdapter.getItemId(touchPosition));
					}
				}
			}
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			if(touchPosition == openPosition){
				int childCount = getChildCount();
				for (int i = 0; i < childCount; i++) {
					View child = getChildAt(i);
					if (isEventWithinView(e, child)) {
						if (mOnItemLongClicked != null) {
							int pos = mTopViewIndex + 1 + i;
							mOnItemLongClicked.onItemLongClick(DelDragListView.this, child, pos, mAdapter.getItemId(pos));
						}
						break;
					}
				}
			}
		}

		private boolean isEventWithinView(MotionEvent e, View child) {
            Rect viewRect = new Rect();
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            int left = childPosition[0];
            int right = left + child.getWidth();
            int top = childPosition[1];
            int bottom = top + child.getHeight();
            viewRect.set(left, top, right, bottom);
            return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
        }
	};
	
    public int pointToPosition(int x, int y) {
        Rect frame = new Rect();

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return mTopViewIndex + 1 + i;
                }
            }
        }
        return INVALID_POSITION;
    }
    
	private int lastNowY=0;
    private void expendViewHeight(final View view, final int position){
        final int originalHeight = view.getHeight(); 
        final int openOriginalHeight;
        if(openView != null){
        	openViewExist = true;
        	openOriginalHeight = openView.getHeight();
        }
        else{
        	openViewExist = false;
        	openOriginalHeight = 0;
        }

        ValueAnimator animator = ValueAnimator.ofInt(0, originalHeight*2); 
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
            @Override  
            public void onAnimationUpdate(ValueAnimator valueAnimator) { 
                int nowY = (Integer) valueAnimator.getAnimatedValue();
                moveStep = nowY-lastNowY;
                lastNowY=nowY;
                if(openView != null){
	            	openView.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
						     MeasureSpec.makeMeasureSpec(openOriginalHeight-nowY, MeasureSpec.EXACTLY));
                }
//            	if(openView == null && openViewExist){
//            		mNextY += -moveStep;
//            	}
            	view.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
    					     MeasureSpec.makeMeasureSpec(originalHeight+nowY, MeasureSpec.EXACTLY));
            	requestLayout();
            }  
        });          
        animator.addListener(new AnimatorListenerAdapter() {  
        	@Override  
            public void onAnimationStart(Animator animation) {  
        		if(touchPosition+1 != openPosition && touchPosition-1 != openPosition && view instanceof OnDrager){
        			((OnDrager)view).moveWeahterIconTo(0);
        		}
            } 
        	
            @Override  
            public void onAnimationEnd(Animator animation) {  
        		//if(touchPosition+1 != openPosition && touchPosition-1 != openPosition){
        			View child;
    				for(int i=0,j=mTopViewIndex+1;i<getChildCount();i++,j++){
    					child = getChildAt(i);
            			if(j == position - 1 && child instanceof OnDrager){
            				((OnDrager)child).moveWeahterIconTo(-getHeight()/6);
            			}
            			else if(j != position && child instanceof OnDrager){
            				((OnDrager)child).moveWeahterIconTo(getHeight()/2);
            			}
    				}
        		//}
            	if(!openViewExist){
            		mNextY += -originalHeight*2;
            		notMove = true;
            	}
            	openViewExist = false;
            	openView = view;
            	openPosition = position;
            	lastNowY = 0; 
            	moveStep = 0;
            }  
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.start(); 
    }
    
}
