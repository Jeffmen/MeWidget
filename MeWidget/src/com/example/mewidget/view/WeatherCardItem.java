package com.example.mewidget.view;

import com.example.mewidget.R;
import com.example.mewidget.view.DragListView.OnDrager;
import com.nineoldandroids.view.ViewHelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherCardItem extends LinearLayout implements OnDrager {
	private Context mContext;
	private LinearLayout weatherInfoLayout,weatherLinear1,weatherLinear2;
	private TextView txtCityName;
	private ImageView isLoacImageView;
	private TextView txtTemperature;
	private AutofitTextView txtWeather;
	private TextView txtWindSpeed;
	private TextView txtHumidity;
	private ImageView weahterIcon1,weahterIcon2;
	private float weatherIconTop;
	
	public WeatherCardItem(Context context) {
		this(context, null);
	}
	
	public WeatherCardItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeatherCardItemLayout);
		int backgroundColor = a.getColor(R.styleable.WeatherCardItemLayout_backgroundColor, getResources().getColor(R.color.weather_item_default_color));
		this.setBackgroundColor(backgroundColor);
		View root = LayoutInflater.from(mContext).inflate(R.layout.weathercard_item, null);
		addView(root, lp);
		weatherInfoLayout = (LinearLayout)root.findViewById(R.id.weather_info_layout);
		txtCityName = (TextView)root.findViewById(R.id.city_name);
		isLoacImageView = (ImageView)root.findViewById(R.id.is_location);
		txtTemperature = (TextView)root.findViewById(R.id.txt_temperature);
		txtWeather = (AutofitTextView)root.findViewById(R.id.txt_weather);
		txtWindSpeed = (TextView)root.findViewById(R.id.txt_wind_speed);
		txtHumidity = (TextView)root.findViewById(R.id.txt_humidity);
		weahterIcon1 = (ImageView)root.findViewById(R.id.weatherIcon1);
		weahterIcon2 = (ImageView)root.findViewById(R.id.weatherIcon2);
		weatherLinear1 = (LinearLayout)root.findViewById(R.id.weatherLinear1);
		weatherLinear2 = (LinearLayout)root.findViewById(R.id.weatherLinear2);
		weatherIconTop = 0;
	}
	public void setCityName(String name){
		this.txtCityName.setText(name);
	}
	public void setLocationVisable(int visable){
		this.isLoacImageView.setVisibility(visable);
	}
	public void setTemperature(String temperature){
		this.txtTemperature.setText(temperature);
	}
	public void setWeather(String weather){
		this.txtWeather.setText(weather);
		txtWeather.setMaxLines(2);
	}
	public void setWindSpeed(String windSpeed){
		this.txtWindSpeed.setText(windSpeed);
	}
	public void setHumidity(String humidity){
		this.txtHumidity.setText(humidity);
	}
	public void setWeatherLinearvisable(boolean visable){
		this.weatherInfoLayout.setVisibility(visable?View.VISIBLE:View.INVISIBLE);
	}
	public void setWeatherIcon1(int id){
		this.weahterIcon1.setImageDrawable(getResources().getDrawable(id));
	}
	public void setWeatherIcon2(int id){
		this.weahterIcon2.setImageDrawable(getResources().getDrawable(id));
	}
	public void startWeatherInfoAni(){
		if(weatherInfoLayout != null){
			this.weatherInfoLayout.setVisibility(View.VISIBLE);
			ObjectAnimator oa=ObjectAnimator.ofFloat(weatherInfoLayout, "y", weatherInfoLayout.getTop()*3, weatherInfoLayout.getTop());
			oa.setDuration(300);
			oa.setInterpolator(new OvershootInterpolator());
			oa.addListener(new AnimatorListenerAdapter(){
			    public void onAnimationEnd(Animator animation){
			        Log.i("Animation","end");
			    }
			});
			oa.start();
		}
	}
	
	public void moveWeahterIconBy(float deltaY){
		weatherIconTop += deltaY;
		//this.requestLayout();
        ViewHelper.setTranslationY(this.weatherLinear1, -weatherIconTop);	
        ViewHelper.setTranslationY(this.weatherLinear2, -weatherIconTop);	
	}
	
	public void moveWeahterIconTo(float deltaY){
		weatherIconTop = deltaY;
		//this.requestLayout();
        ViewHelper.setTranslationY(this.weatherLinear1, -weatherIconTop);	
        ViewHelper.setTranslationY(this.weatherLinear2, -weatherIconTop);	
	}
	
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
}
