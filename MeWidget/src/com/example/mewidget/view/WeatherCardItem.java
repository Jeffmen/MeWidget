package com.example.mewidget.view;

import com.example.mewidget.R;
import com.example.mewidget.view.SpreadListView.OnDrager;
import com.nineoldandroids.view.ViewHelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherCardItem extends LinearLayout implements OnDrager {
	private static float SCALE_NORMAL = 1.0f;
	private static float SCALE_BIG = 1.3f;
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
		this.weatherLinear1.setVisibility(View.VISIBLE);
		this.weahterIcon1.setImageDrawable(getResources().getDrawable(id));
	}
	public void setWeatherIcon2(int id){
		this.weatherLinear2.setVisibility(View.VISIBLE);
		this.weahterIcon2.setImageDrawable(getResources().getDrawable(id));
	}	
	public void setWeatherIcon2ani(int id){
		this.weatherLinear2.setVisibility(View.VISIBLE);
		this.weahterIcon2.setBackgroundResource(id);
		AnimationDrawable anim = (AnimationDrawable) this.weahterIcon2.getBackground();
		anim.start();
	}
	public void startWeatherInfoAni(int type){
		if(weatherInfoLayout != null){
			//this.weatherInfoLayout.setVisibility(View.VISIBLE);
			AnimatorSet bouncer = new AnimatorSet();
			if(type == 0){
				ObjectAnimator anim1=ObjectAnimator.ofFloat(weatherInfoLayout, "alpha", 0.0f);
	            ObjectAnimator anim2=ObjectAnimator.ofFloat(txtCityName, "scaleX", 1.0f);
	            ObjectAnimator anim4=ObjectAnimator.ofFloat(txtCityName, "scaleY", 1.0f);
	            ObjectAnimator anim3=ObjectAnimator.ofFloat(txtTemperature, "scaleX", 1.0f);
	            ObjectAnimator anim5=ObjectAnimator.ofFloat(txtTemperature, "scaleY", 1.0f);
	            ObjectAnimator anim6=ObjectAnimator.ofFloat(txtCityName, "alpha", 0.7f);
	            ObjectAnimator anim7=ObjectAnimator.ofFloat(txtTemperature, "alpha", 0.7f);
	            bouncer.play(anim1).with(anim2);
	            bouncer.play(anim1).with(anim3);
	            bouncer.play(anim1).with(anim4);
	            bouncer.play(anim1).with(anim5);
	            bouncer.play(anim1).with(anim6);
	            bouncer.play(anim1).with(anim7);
			}
			else if(type == 1){
				ObjectAnimator anim1=ObjectAnimator.ofFloat(weatherInfoLayout, "alpha", 1.0f);
	            ObjectAnimator anim2=ObjectAnimator.ofFloat(txtCityName, "scaleX", 1.5f);
	            ObjectAnimator anim4=ObjectAnimator.ofFloat(txtCityName, "scaleY", 1.5f);
	            ObjectAnimator anim3=ObjectAnimator.ofFloat(txtTemperature, "scaleX", 2.3f);
	            ObjectAnimator anim5=ObjectAnimator.ofFloat(txtTemperature, "scaleY", 2.3f);
	            ObjectAnimator anim6=ObjectAnimator.ofFloat(txtCityName, "alpha", 1.0f);
	            ObjectAnimator anim7=ObjectAnimator.ofFloat(txtTemperature, "alpha", 1.0f);
	            bouncer.play(anim1).with(anim2);
	            bouncer.play(anim1).with(anim3);
	            bouncer.play(anim1).with(anim4);
	            bouncer.play(anim1).with(anim5);
	            bouncer.play(anim1).with(anim6);
	            bouncer.play(anim1).with(anim7);
	            bouncer.setInterpolator(new OvershootInterpolator());
			}
            bouncer.setDuration(SpreadListView.ANIMATION_DURATION);
            bouncer.start();
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
