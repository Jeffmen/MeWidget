package com.example.mewidget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.mewidget.R;
import com.example.mewidget.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

public class BlurLayout extends RelativeLayout {


    //private View mHoverView;

    private boolean enableBlurBackground = true;
    private int mBlurRadius = 20;
    //private ImageView mBlurImage;

    private static long DURATION = 500;

    private boolean enableBackgroundZoom = false;
    private float mZoomRatio = 1.14f;

    private boolean enableTouchEvent = true;

    private Animator mHoverAppearAnimator;
    private Animator mHoverDisappearAnimator;

    private long mBlurDuration = DURATION;
    private boolean isScrolling;
    private int cityDistanceY = 0;
    
    public enum HOVER_STATUS {
        APPEARING, APPEARED, DISAPPEARING, DISAPPEARED
    };

    private HOVER_STATUS mHoverStatus = HOVER_STATUS.DISAPPEARED;

    public BlurLayout(Context context) {
        this(context, null);
    }

    public BlurLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlurLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    private View getHoverView(){
        return findViewById(R.id.hover_content);
    }
    
    private ImageView getBlurView(){
    	return (ImageView) findViewById(R.id.blur_image);
    }
    
    private View getCityView(){
    	return findViewById(R.id.city_name);
    }
    
    public void reset(){
    	//getHoverView().setVisibility(View.INVISIBLE);
    	getBlurView().setVisibility(View.INVISIBLE);
    	mHoverStatus = HOVER_STATUS.DISAPPEARED;
    }
    
    public void show(){
    	//getHoverView().setVisibility(View.VISIBLE);
    	getBlurView().setVisibility(View.VISIBLE);
    	mHoverStatus = HOVER_STATUS.APPEARED;
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		//handled |= enableTouchEvent && gestureDetector.onTouchEvent(ev);
		return handled;
	}
	
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return enableTouchEvent && gestureDetector.onTouchEvent(event);
//    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new BlurLayoutDetector());

    class BlurLayoutDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
			isScrolling = false;
            return true;
        }

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (isScrolling) {
                isScrolling = false;
                ValueAnimator animation = ValueAnimator.ofInt(cityDistanceY, cityDistanceY > 0 ? getHeight()-getCityView().getHeight() : 0);
                //animation.setDuration(1000);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                    	cityDistanceY = (Integer)animation.getAnimatedValue();
						cityDistanceY = cityDistanceY < 0 ? 0 : cityDistanceY;
						cityDistanceY = cityDistanceY > getHeight()-getCityView().getHeight() ? getHeight()-getCityView().getHeight() : cityDistanceY;
						ViewHelper.setTranslationY(getCityView(), -cityDistanceY);
						
						mBlurRadius = 25*cityDistanceY/(getHeight()-getCityView().getHeight());
						mBlurRadius = mBlurRadius < 1 ? 1 : mBlurRadius;
						mBlurRadius = mBlurRadius >24 ? 24 : mBlurRadius;
	
						refreshBlurImage();
						show();
                    }
                });
                animation.start();
				return true;
			}
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float angle = Math.abs((e2.getRawY() - e1.getRawY()) / (e2.getRawX() - e1.getRawX()));
            angle = (float)Math.toDegrees(Math.atan(angle));
            
			if (angle > 30) {
		        //ViewHelper.setAlpha(getBlurView(), 1);
				cityDistanceY += (int)distanceY;
				cityDistanceY = cityDistanceY < 0 ? 0 : cityDistanceY;
				cityDistanceY = cityDistanceY > getHeight()-getCityView().getHeight() ? getHeight()-getCityView().getHeight() : cityDistanceY;
				ViewHelper.setTranslationY(getCityView(), -cityDistanceY);
				
				mBlurRadius = 25*cityDistanceY/(getHeight()-getCityView().getHeight());
				mBlurRadius = mBlurRadius < 1 ? 1 : mBlurRadius;
				mBlurRadius = mBlurRadius >24 ? 24 : mBlurRadius;

				refreshBlurImage();
				show();
				isScrolling = true;
				return true;
			}
			return false;
		}
		
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
	        //ViewHelper.setTranslationY(getCityView(), -20);
    		toggleHover();
    		return true;
//            if(hover()){
//                BlurLayout.this.requestLayout();
//                return true;
//            }else{
//                return super.onSingleTapConfirmed(e);
//            }
        }
    };

    public void showHover(){
        hover();
    }

    /**
     * Let hover show.
     * @return
     */
    private boolean hover(){
        if(getHoverView() == null)  return false;

        if(getHoverStatus() != HOVER_STATUS.DISAPPEARED)    return true;

        if(enableBlurBackground){
            refreshBlurImage();
        }
//        if(mHoverView.getParent() != null){
//            ((ViewGroup)(mHoverView.getParent())).removeView(mHoverView);
//        }
        //mHoverView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        //addView(mHoverView, getFullParentSizeLayoutParams());
        
        startBlurImageAppearAnimator();

        startHoverAppearAnimator();

        mHoverStatus = HOVER_STATUS.APPEARED;
//        mHoverView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//        		Log.i("BlurLayout","onGlobalLayout"+ "");
//                startBlurImageAppearAnimator();
//
//                startHoverAppearAnimator();
//
//                mHoverStatus = HOVER_STATUS.APPEARED;
//                if(Build.VERSION.SDK_INT >= 16)
//                    mHoverView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                else
//                    mHoverView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            }
//        });
        return true;

    }

    /**
     * Let hover view dismiss.
     * Notice: only when hover view status is appeared, then, this may work.
     */
    public void dismissHover(){
        if(getHoverStatus() != HOVER_STATUS.APPEARED)
            return;
        
        mHoverStatus = HOVER_STATUS.DISAPPEARED;
        startBlurImageDisappearAnimator();

        startHoverDisappearAnimator();

    }

    public void toggleHover(){
        if(getHoverStatus() == HOVER_STATUS.DISAPPEARED)
            showHover();
        else if(getHoverStatus() == HOVER_STATUS.APPEARED)
            dismissHover();
    }

    /**
     * get currently hover status.
     * @return
     */
    public HOVER_STATUS getHoverStatus(){
        return mHoverStatus;
    }

    private void refreshBlurImage(){
        Bitmap b = Utils.getViewBitmap(this.getChildAt(0));
        if(b == null)
            return;
        Bitmap bm = Utils.blur(getContext(), b, mBlurRadius);
        //ImageView im = new ImageView(getContext());
        getBlurView().setImageBitmap(bm);
        //im.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));  
        //mBlurImage = im;
        //this.addView(im);
    }

    /**
     * set background blur duration.
     * @param duration
     */
    public void setBlurDuration(long duration){
        if(duration > 100)
            mBlurDuration = duration;
    }


    /**
     * set background blur radius.
     * @param radius radius to be used for the gaussian blur operation, integer between 0 and 25 (inclusive)
     */
    public void setBlurRadius(int radius) {
        if(radius < 0 || radius > 25){
            throw new IllegalArgumentException("Radius must be between 0 and 25 (inclusive)");
        }
        this.mBlurRadius = radius;
    }

    /**
     * Sets whether or not touching the BlurLayout will trigger the Hover View and blur effect
     * @param enableTouchEvent
     */
    public void enableTouchEvent(boolean enableTouchEvent) {
        this.enableTouchEvent = enableTouchEvent;
    }

    public void enableBlurBackground(boolean enable){
        enableBlurBackground = enable;
    }

    public void enableZoomBackground(boolean enable) {
        enableBackgroundZoom = enable;
    }

    public void setBlurZoomRatio(float ratio){
        if(ratio < 0)
            throw new IllegalArgumentException("Can not set ratio less than 0");
        mZoomRatio = ratio;
    }

    private void startBlurImageAppearAnimator(){
        if(!enableBlurBackground || getBlurView() == null)    return;

        AnimatorSet set = new AnimatorSet();
         if(enableBackgroundZoom){
            set.playTogether(
                    ObjectAnimator.ofFloat(getBlurView(), "alpha", 0.8f, 1f),
                    ObjectAnimator.ofFloat(getBlurView(), "scaleX", 1f, mZoomRatio),
                    ObjectAnimator.ofFloat(getBlurView(), "scaleY", 1f, mZoomRatio)
            );
        }
        else{
            set.playTogether(
                    ObjectAnimator.ofFloat(getBlurView(), "alpha", 0f, 1f)
            );
        }
        set.setDuration(mBlurDuration);
        set.addListener(new AnimatorListener(){

			@Override
			public void onAnimationStart(Animator animation) {
				show();
			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
        });
        set.start();
    }

    private void startBlurImageDisappearAnimator(){
        if(!enableBlurBackground || getBlurView() == null)    return;

        AnimatorSet set = new AnimatorSet();
        if(enableBackgroundZoom)
            set.playTogether(
                    ObjectAnimator.ofFloat(getBlurView(), "alpha", 1f, 0.8f),
                    ObjectAnimator.ofFloat(getBlurView(), "scaleX", mZoomRatio, 1f),
                    ObjectAnimator.ofFloat(getBlurView(), "scaleY", mZoomRatio, 1f)
            );
        else
            set.playTogether(
                    ObjectAnimator.ofFloat(getBlurView(), "alpha", 1f, 0f)
            );
        set.setDuration(mBlurDuration);
        set.addListener(new AnimatorListener(){

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				reset();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
        });
        set.start();
    }

    private void startHoverAppearAnimator(){
        if(mHoverAppearAnimator != null)
            mHoverAppearAnimator.start();
    }

    private void startHoverDisappearAnimator(){
        if(mHoverDisappearAnimator != null)
            mHoverDisappearAnimator.start();
    }

    public LayoutParams getFullParentSizeLayoutParams(){
        LayoutParams pm = (LayoutParams)this.generateDefaultLayoutParams();
        pm.width = this.getWidth();
        pm.height = this.getHeight();
        return pm;
    }

    public static void setGlobalDefaultDuration(long duration){
        if(duration < 100)
            throw new IllegalArgumentException("Duration can not be set to less than 100");
        DURATION = duration;
    }
}
