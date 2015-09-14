package com.example.mewidget.view;

import java.util.ArrayList;
import java.util.List;

import com.example.mewidget.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;

public abstract class ChartView extends View {
	private static final float LEFT = 1.5F / 16F, TOP = 5 / 16F, RIGHT = 14.5F / 16F, BOTTOM = 13F / 16F;// 网格区域相对位置
	private static final float TIME_X = 3 / 32F, TIME_Y = 1 / 16F, MONEY_X = 31 / 32F, MONEY_Y = 15 / 16F;// 文字坐标相对位置
	private static final float TEXT_SIGN = 1 / 16F;// 文字相对大小
	private static final float THICK_LINE_WIDTH = 1 / 128F, THIN_LINE_WIDTH = 1 / 512F;// 粗线和细线相对大小
    private static int ANIMATION_DURATION = 300;
    private static int Y_DIVISOR_LENGTH = 8;

	private TextPaint mTextPaint;// 文字画笔
	private Paint linePaint, pointPaint, areaPaint;// 线条画笔和点画笔
	private Path mPath, mAreaPath;// 路径对象
	private Bitmap mBitmap;// 绘制曲线的Btimap对象
	private Canvas mCanvas;// 装载mBitmap的Canvas对象

	private List<ForecastWeatherInfo> pointFs;// 数据列表
	private float[] rulerX, rulerY;// xy轴向刻度

	private String signX, signY;// 设置X和Y坐标分别表示什么的文字
	private float textY_X, textY_Y, textX_X, textX_Y;// 文字坐标
	private float textSignSzie;// xy坐标标识文本字体大小
	private float thickLineWidth, thinLineWidth;// 粗线和细线宽度
	private float left, top, right, bottom;// 网格区域左上右下两点坐标
	private int viewSize_x, viewSize_y;// 控件尺寸
	private float maxY;// 横纵轴向最大刻度
	private float spaceX, spaceY;// 刻度间隔
	private int selectPosition;
    private float phase, selectCricle_x;
    private int xDivisor, yDivisor;
	private GestureDetector mGesture;
	private int areaStartColor, areaEndColor;

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 实例化文本画笔并设置参数
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setColor(Color.WHITE);

		// 实例化线条画笔并设置参数
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setColor(Color.WHITE);

		// 实例化点画笔并设置参数
		pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		pointPaint.setStyle(Paint.Style.FILL);
		pointPaint.setColor(Color.WHITE);

		areaPaint = new Paint();

		areaStartColor = context.getResources().getColor(R.color.area_start_color);
		areaEndColor = context.getResources().getColor(R.color.area_end_color);
		
		mPath = new Path();
		mAreaPath = new Path();
		mCanvas = new Canvas();
		selectPosition = 0;
		selectCricle_x = 0;
		mGesture = new GestureDetector(getContext(), mOnGesture);
		pointFs = new ArrayList<ForecastWeatherInfo>(); 
		
		//initData();
	}
	
	public synchronized void setData(List<ForecastWeatherInfo> pointFs, String signX, String signY) {
		if (null == pointFs || pointFs.size() == 0)
			throw new IllegalArgumentException("No data to display !");
		if (pointFs.size() > 10)
			throw new IllegalArgumentException("The data is too long to display !");
		this.pointFs = pointFs;
		this.signX = signX;
		this.signY = signY;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
// 在我们没学习测量控件之前强制宽高一致
//		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
//      int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(specSize, specSize*9/10);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// 获取控件尺寸
		viewSize_x = w;
		viewSize_y = h;

		// 计算纵轴标识文本坐标
		textY_X = viewSize_x * TIME_X;
		textY_Y = viewSize_y * TIME_Y;

		// 计算横轴标识文本坐标
		textX_X = viewSize_x * MONEY_X;
		textX_Y = viewSize_y * MONEY_Y;

		// 计算xy轴标识文本大小
		textSignSzie = viewSize_x * TEXT_SIGN;

		// 计算网格左上右下两点坐标
		left = viewSize_x * LEFT;
		top = viewSize_y * TOP;
		right = viewSize_x * RIGHT;
		bottom = viewSize_y * BOTTOM;

		// 计算粗线宽度
		thickLineWidth = viewSize_x * THICK_LINE_WIDTH;
		thinLineWidth = viewSize_x * THIN_LINE_WIDTH;
		
		xDivisor = pointFs.size()+1;
		yDivisor = 3;

		// 计算纵轴数据最大值
		maxY = 0;
		for (int j = 0; j < pointFs.size(); j++) {
			if (maxY < pointFs.get(j).getHighTemp()) {
				maxY = pointFs.get(j).getHighTemp();
			}
		}
		// 计算纵轴最近的能被count整除的值
		int remainderY = ((int) maxY) % (yDivisor);
		maxY = remainderY == 0 ? ((int) maxY) : yDivisor - remainderY + ((int) maxY);

		// 生成纵轴刻度值
		rulerY = new float[yDivisor+1];
		for (int i = 0; i < rulerY.length; i++) {
			rulerY[i] = maxY / (yDivisor) * i;
		}

		// 计算横纵坐标刻度间隔
		spaceY = viewSize_y * (BOTTOM - TOP) / yDivisor;
		spaceX = viewSize_x * (RIGHT - LEFT) / xDivisor;
		
		selectCricle_x = left + spaceX;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 填充背景
		//canvas.drawColor(0xFF9596C4);
		// 绘制标识元素
		drawSign(canvas);
		// 绘制网格
		drawGrid(canvas);
		// 绘制曲线
		drawPolyline(canvas);
	}

	abstract protected void onDrawChart(Canvas canvas, ArrayList<ForecastWeatherInfo> data);
	
	private Bitmap getBitmapById(int id, int width, int height){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id) ;  
        Matrix matrix = new Matrix(); 
        
        int dstSize = width<height?width:height;
        int srcSize = bitmap.getWidth()<bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
        
        matrix.postScale((float)dstSize/srcSize,(float)dstSize/srcSize);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
	}
	
	private void drawPolyline(Canvas canvas) {
			// 生成一个Bitmap对象大小和我们的网格大小一致
			mBitmap = Bitmap.createBitmap((int) (viewSize_x * (RIGHT - LEFT)), 
					                      (int) (viewSize_y * (BOTTOM - TOP)), Bitmap.Config.ARGB_8888);
			// 将Bitmap注入Canvas
			mCanvas.setBitmap(mBitmap);
	
			// 为画布填充一个半透明的红色
			//mCanvas.drawARGB(75, 255, 0, 0);

			float textXRulerSize = textSignSzie / 2F, textWidth;
			
			mTextPaint.setTextSize(textXRulerSize);
			
			mPath.reset();
			pointPaint.setColor(Color.RED);
			for (int i = 0; i < pointFs.size(); i++) {
				float x = mCanvas.getWidth() / xDivisor * i + left + spaceX;
				float y = mCanvas.getHeight() / maxY * pointFs.get(i).getHighTemp();
				y = mCanvas.getHeight() - y + top;
				
				Bitmap bitmap = getBitmapById(pointFs.get(i).getWeatherIcon(), (int)spaceX, (int)spaceX);
                canvas.drawBitmap(bitmap, x-spaceX/2, (top - spaceX)/2, null);
				
                if(i == selectPosition){
                	mTextPaint.setTextSize(textSignSzie/1.5F);
                	mTextPaint.setColor(Color.GREEN);
                }
                else{
                	mTextPaint.setTextSize(textSignSzie/2F);
                	mTextPaint.setColor(Color.WHITE);
                }
            	canvas.drawCircle(x, y, thickLineWidth*5/4, pointPaint);
    			textWidth = mTextPaint.measureText(pointFs.get(i).getDate());
            	canvas.drawText(String.valueOf(pointFs.get(i).getHighTemp()), x-textWidth/2, y  - textXRulerSize, mTextPaint);
                
				if (i == 0) {
					mPath.moveTo(x, y);
				}
	
				mPath.lineTo(x, y);
			}
			// 设置PathEffect
//            Path p = new Path();  
//            p.addRect(0, 0, 8, 8, Path.Direction.CCW); 
//			linePaint.setPathEffect(new PathDashPathEffect(p,12,phase,PathDashPathEffect.Style.ROTATE));
			//linePaint.setPathEffect(new CornerPathEffect(0));

			// 重置线条宽度
			linePaint.setColor(Color.RED);
			linePaint.setStrokeWidth(thickLineWidth);
	
			// 将Path绘制到我们自定的Canvas上
			canvas.drawPath(mPath, linePaint);
	
			// 将mBitmap绘制到原来的canvas
			//canvas.drawBitmap(mBitmap, left, top + spaceY, null);
			
			mPath.reset();
			mAreaPath.reset();
			pointPaint.setColor(Color.BLUE);
			for (int i = 0; i < pointFs.size(); i++) {
				float x = mCanvas.getWidth() / xDivisor * i + left + spaceX;
				float y = mCanvas.getHeight() / maxY * pointFs.get(i).getLowTemp();
				y = mCanvas.getHeight() - y + top;

                if(i == selectPosition){
                	mTextPaint.setTextSize(textSignSzie/1.5F);
                	mTextPaint.setColor(Color.GREEN);
                }
                else{
                	mTextPaint.setTextSize(textSignSzie/2F);
                	mTextPaint.setColor(Color.WHITE);
                }
				canvas.drawCircle(x, y, thickLineWidth*5/4, pointPaint);
    			textWidth = mTextPaint.measureText(pointFs.get(i).getDate());
            	canvas.drawText(String.valueOf(pointFs.get(i).getLowTemp()), x-textWidth/2, y  - textXRulerSize, mTextPaint);
            	
				if (i == 0) {
					mPath.moveTo(x, y);
					mAreaPath.moveTo(x, top + spaceY * yDivisor-Y_DIVISOR_LENGTH);
					Shader mShader=new LinearGradient(x,y,x,top + spaceY * yDivisor-Y_DIVISOR_LENGTH,areaStartColor,areaEndColor,Shader.TileMode.CLAMP);
					areaPaint.setShader(mShader);
				}
				mPath.lineTo(x, y);
				mAreaPath.lineTo(x, y);
				if(i == pointFs.size() -1){
					mAreaPath.lineTo(x, top + spaceY * yDivisor-Y_DIVISOR_LENGTH);
				}
			}
			mAreaPath.close();	
			// 重置线条宽度
			linePaint.setColor(Color.BLUE);
			linePaint.setStrokeWidth(thickLineWidth);
			//int sc = canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 25, Canvas.ALL_SAVE_FLAG);
			canvas.drawPath(mAreaPath, areaPaint);
			//canvas.restoreToCount(sc);
			canvas.drawPath(mPath, linePaint);
	}

	private void drawGrid(Canvas canvas) {
		// 锁定画布
		canvas.save();

		// 设置线条画笔宽度
		linePaint.setColor(Color.WHITE);
		linePaint.setStrokeWidth(thickLineWidth);

		// 计算xy轴Path
		mPath.moveTo(left, bottom);
		mPath.lineTo(right, bottom);
		canvas.drawPath(mPath, linePaint);

		// 绘制线条
		drawLines(canvas);
		// 绘制刻度
		drawRulers(canvas);
		// 释放画布
		canvas.restore();
	}
	
	private void drawLines(Canvas canvas) {
		// 锁定画布并设置画布透明度为75%
		int sc = canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 75, Canvas.ALL_SAVE_FLAG);
		// 绘制横纵线段
		linePaint.setStrokeWidth(thickLineWidth);
		for (float x = left + spaceX; x < right; x += spaceX) {
			canvas.drawLine(x, top + spaceY * yDivisor-Y_DIVISOR_LENGTH, x, top + spaceY * yDivisor, linePaint);
		}
		linePaint.setStrokeWidth(thinLineWidth);
		for (float y = bottom - spaceY; y >= top; y -= spaceY) {
			canvas.drawLine(right, y, right - spaceX * xDivisor, y, linePaint);
		}
		// 还原画布
		canvas.restoreToCount(sc);
	}
	
	private void drawRulers(Canvas canvas) {
		pointPaint.setColor(Color.GREEN);
		canvas.drawCircle(selectCricle_x, bottom + textSignSzie, thickLineWidth*6, pointPaint);
		
		// 绘制横纵轴向刻度值
		int index_x = 0, index_y = 1;
		float textWidth=0;	
		// 计算刻度文字尺寸
		float textXRulerSize = textSignSzie / 2F;
		mTextPaint.setTextSize(textXRulerSize);
    	mTextPaint.setColor(Color.WHITE);
		for (float x = left + spaceX; x < right; x += spaceX) {
			textWidth = mTextPaint.measureText(pointFs.get(index_x).getDate());
			canvas.drawText(pointFs.get(index_x).getDate(), x-textWidth/2, bottom  + textSignSzie, mTextPaint);
			index_x++;
		}

		float textYRulerSize = textSignSzie / 3F;
		mTextPaint.setTextSize(textYRulerSize);
		for (float y = bottom - spaceY; y >= top; y -= spaceY) {
		    canvas.drawText(String.valueOf((int)rulerY[index_y])+"°C", left - thickLineWidth, y + textYRulerSize, mTextPaint);
			index_y++;
		}
	}

	private void drawSign(Canvas canvas) {
		// 锁定画布
		canvas.save();

		// 设置文本画笔文字尺寸
		mTextPaint.setTextSize(textSignSzie);

		// 绘制纵轴标识文字
		if(!signY.isEmpty()){
			mTextPaint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText(signY, textY_X, textY_Y, mTextPaint);
		}
		// 绘制横轴标识文字
		if(!signX.isEmpty()){
			mTextPaint.setTextAlign(Paint.Align.RIGHT);
			canvas.drawText(signX, textX_X, textX_Y, mTextPaint);
		}
		// 释放画布
		canvas.restore();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		return handled;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
		    return true;	
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			int touchPosition = isEventWithinView(e);
			Log.i("WeatherLineView","touchPosition="+touchPosition);
			if(touchPosition >= 0){
				startAnimation(touchPosition);
				return true;
			}
			else{
				return false;
			}
		}		
		
		private int isEventWithinView(MotionEvent e) {
            float x = left + spaceX/2;
            float y = bottom;
            if( e.getX() > x && e.getX() < x + spaceX*pointFs.size()  && e.getY() > y){
            	return (int) Math.ceil((e.getX()-x)/spaceX)-1;
            }
            return -1;
        }
	};

	private void startAnimation(final int touchPosition){
        ValueAnimator animator = ValueAnimator.ofFloat(selectCricle_x, left + (touchPosition +1)*spaceX); 
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
            @Override  
            public void onAnimationUpdate(ValueAnimator valueAnimator) { 
            	selectCricle_x = (Float) valueAnimator.getAnimatedValue();
            	invalidate();
            }  
        });          
        animator.addListener(new AnimatorListenerAdapter() {  
        	@Override  
            public void onAnimationStart(Animator animation) {  

            } 
        	
            @Override  
            public void onAnimationEnd(Animator animation) {  
            	selectPosition = touchPosition;
            	invalidate();
            }  
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
	}
}
