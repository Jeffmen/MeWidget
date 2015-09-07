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
	private static final float LEFT = 1.5F / 16F, TOP = 5 / 16F, RIGHT = 14.5F / 16F, BOTTOM = 13F / 16F;// �����������λ��
	private static final float TIME_X = 3 / 32F, TIME_Y = 1 / 16F, MONEY_X = 31 / 32F, MONEY_Y = 15 / 16F;// �����������λ��
	private static final float TEXT_SIGN = 1 / 16F;// ������Դ�С
	private static final float THICK_LINE_WIDTH = 1 / 128F, THIN_LINE_WIDTH = 1 / 512F;// ���ߺ�ϸ����Դ�С
    private static int ANIMATION_DURATION = 300;
    private static int Y_DIVISOR_LENGTH = 8;

	private TextPaint mTextPaint;// ���ֻ���
	private Paint linePaint, pointPaint, areaPaint;// �������ʺ͵㻭��
	private Path mPath, mAreaPath;// ·������
	private Bitmap mBitmap;// �������ߵ�Btimap����
	private Canvas mCanvas;// װ��mBitmap��Canvas����

	private List<ForecastWeatherInfo> pointFs;// �����б�
	private float[] rulerX, rulerY;// xy����̶�

	private String signX, signY;// ����X��Y����ֱ��ʾʲô������
	private float textY_X, textY_Y, textX_X, textX_Y;// ��������
	private float textSignSzie;// xy�����ʶ�ı������С
	private float thickLineWidth, thinLineWidth;// ���ߺ�ϸ�߿��
	private float left, top, right, bottom;// ������������������������
	private int viewSize_x, viewSize_y;// �ؼ��ߴ�
	private float maxY;// �����������̶�
	private float spaceX, spaceY;// �̶ȼ��
	private int selectPosition;
    private float phase, selectCricle_x;
    private int xDivisor, yDivisor;
	private GestureDetector mGesture;
	private int areaStartColor, areaEndColor;

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// ʵ�����ı����ʲ����ò���
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setColor(Color.WHITE);

		// ʵ�����������ʲ����ò���
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setColor(Color.WHITE);

		// ʵ�����㻭�ʲ����ò���
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
// ������ûѧϰ�����ؼ�֮ǰǿ�ƿ��һ��
//		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
//      int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(specSize, specSize*9/10);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// ��ȡ�ؼ��ߴ�
		viewSize_x = w;
		viewSize_y = h;

		// ���������ʶ�ı�����
		textY_X = viewSize_x * TIME_X;
		textY_Y = viewSize_y * TIME_Y;

		// ��������ʶ�ı�����
		textX_X = viewSize_x * MONEY_X;
		textX_Y = viewSize_y * MONEY_Y;

		// ����xy���ʶ�ı���С
		textSignSzie = viewSize_x * TEXT_SIGN;

		// ������������������������
		left = viewSize_x * LEFT;
		top = viewSize_y * TOP;
		right = viewSize_x * RIGHT;
		bottom = viewSize_y * BOTTOM;

		// ������߿��
		thickLineWidth = viewSize_x * THICK_LINE_WIDTH;
		thinLineWidth = viewSize_x * THIN_LINE_WIDTH;
		
		xDivisor = pointFs.size()+1;
		yDivisor = 3;

		// ���������������ֵ
		maxY = 0;
		for (int j = 0; j < pointFs.size(); j++) {
			if (maxY < pointFs.get(j).getHighTemp()) {
				maxY = pointFs.get(j).getHighTemp();
			}
		}
		// ��������������ܱ�count������ֵ
		int remainderY = ((int) maxY) % (yDivisor);
		maxY = remainderY == 0 ? ((int) maxY) : yDivisor - remainderY + ((int) maxY);

		// ��������̶�ֵ
		rulerY = new float[yDivisor+1];
		for (int i = 0; i < rulerY.length; i++) {
			rulerY[i] = maxY / (yDivisor) * i;
		}

		// �����������̶ȼ��
		spaceY = viewSize_y * (BOTTOM - TOP) / yDivisor;
		spaceX = viewSize_x * (RIGHT - LEFT) / xDivisor;
		
		selectCricle_x = left + spaceX;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// ��䱳��
		//canvas.drawColor(0xFF9596C4);
		// ���Ʊ�ʶԪ��
		drawSign(canvas);
		// ��������
		drawGrid(canvas);
		// ��������
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
			// ����һ��Bitmap�����С�����ǵ������Сһ��
			mBitmap = Bitmap.createBitmap((int) (viewSize_x * (RIGHT - LEFT)), 
					                      (int) (viewSize_y * (BOTTOM - TOP)), Bitmap.Config.ARGB_8888);
			// ��Bitmapע��Canvas
			mCanvas.setBitmap(mBitmap);
	
			// Ϊ�������һ����͸���ĺ�ɫ
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
			// ����PathEffect
//            Path p = new Path();  
//            p.addRect(0, 0, 8, 8, Path.Direction.CCW); 
//			linePaint.setPathEffect(new PathDashPathEffect(p,12,phase,PathDashPathEffect.Style.ROTATE));
			//linePaint.setPathEffect(new CornerPathEffect(0));

			// �����������
			linePaint.setColor(Color.RED);
			linePaint.setStrokeWidth(thickLineWidth);
	
			// ��Path���Ƶ������Զ���Canvas��
			canvas.drawPath(mPath, linePaint);
	
			// ��mBitmap���Ƶ�ԭ����canvas
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
			// �����������
			linePaint.setColor(Color.BLUE);
			linePaint.setStrokeWidth(thickLineWidth);
			//int sc = canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 25, Canvas.ALL_SAVE_FLAG);
			canvas.drawPath(mAreaPath, areaPaint);
			//canvas.restoreToCount(sc);
			canvas.drawPath(mPath, linePaint);
	}

	private void drawGrid(Canvas canvas) {
		// ��������
		canvas.save();

		// �����������ʿ��
		linePaint.setColor(Color.WHITE);
		linePaint.setStrokeWidth(thickLineWidth);

		// ����xy��Path
		mPath.moveTo(left, bottom);
		mPath.lineTo(right, bottom);
		canvas.drawPath(mPath, linePaint);

		// ��������
		drawLines(canvas);
		// ���ƿ̶�
		drawRulers(canvas);
		// �ͷŻ���
		canvas.restore();
	}
	
	private void drawLines(Canvas canvas) {
		// �������������û���͸����Ϊ75%
		int sc = canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 75, Canvas.ALL_SAVE_FLAG);
		// ���ƺ����߶�
		linePaint.setStrokeWidth(thickLineWidth);
		for (float x = left + spaceX; x < right; x += spaceX) {
			canvas.drawLine(x, top + spaceY * yDivisor-Y_DIVISOR_LENGTH, x, top + spaceY * yDivisor, linePaint);
		}
		linePaint.setStrokeWidth(thinLineWidth);
		for (float y = bottom - spaceY; y >= top; y -= spaceY) {
			canvas.drawLine(right, y, right - spaceX * xDivisor, y, linePaint);
		}
		// ��ԭ����
		canvas.restoreToCount(sc);
	}
	
	private void drawRulers(Canvas canvas) {
		pointPaint.setColor(Color.GREEN);
		canvas.drawCircle(selectCricle_x, bottom + textSignSzie, thickLineWidth*6, pointPaint);
		
		// ���ƺ�������̶�ֵ
		int index_x = 0, index_y = 1;
		float textWidth=0;	
		// ����̶����ֳߴ�
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
		    canvas.drawText(String.valueOf((int)rulerY[index_y])+"��C", left - thickLineWidth, y + textYRulerSize, mTextPaint);
			index_y++;
		}
	}

	private void drawSign(Canvas canvas) {
		// ��������
		canvas.save();

		// �����ı��������ֳߴ�
		mTextPaint.setTextSize(textSignSzie);

		// ���������ʶ����
		if(!signY.isEmpty()){
			mTextPaint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText(signY, textY_X, textY_Y, mTextPaint);
		}
		// ���ƺ����ʶ����
		if(!signX.isEmpty()){
			mTextPaint.setTextAlign(Paint.Align.RIGHT);
			canvas.drawText(signX, textX_X, textX_Y, mTextPaint);
		}
		// �ͷŻ���
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
