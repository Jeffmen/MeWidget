package com.example.mewidget;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.example.mewidget.provider.Weather;

import android.animation.AnimatorSet;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider{
	private Context mContext;
	private RemoteViews remoteViews;    
	private Calendar calendar;
	private Date curDate;
	private String ITEM_CLICK_ACTION = "com.example.zteweather.item_click";
    private String[] MouthString = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    private String[] WeekString = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
    private Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "islocation/1");
    
	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
	      @Override
	    public void onChange(boolean selfChange) {
	    	  updateWeather();
	    }
	};
	
	@Override
	public void onEnabled(Context context) {
		  context.getContentResolver().
          registerContentObserver(uri, false, mObserver);
		super.onEnabled(context);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		  context.getContentResolver().
          registerContentObserver(uri, false, mObserver);
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) { 
		Log.i("WidgetProvider", "onUpdate");
		this.mContext = context;
		this.calendar = new GregorianCalendar(); 
		this.curDate = new Date(System.currentTimeMillis());
		remoteViews=new RemoteViews(context.getPackageName(), R.layout.widget_layout);

		Calendar cal = getCalendar(0);
		int mouth = cal.get(Calendar.MONTH);
		int week = cal.get(Calendar.DAY_OF_WEEK)-1; 
	    int date = cal.get(Calendar.DATE); 
		Log.i("WidgetProvider", "onUpdate:mouth"+mouth);
		remoteViews.setTextViewText(R.id.mouthText_id, MouthString[mouth]);
		remoteViews.setTextViewText(R.id.weekText_id, WeekString[week]);
		remoteViews.setTextViewText(R.id.dayText3_id, String.valueOf(date));
		cal = getCalendar(-2);
		date = cal.get(Calendar.DATE); 
		remoteViews.setTextViewText(R.id.dayText1_id, String.valueOf(date));
		remoteViews.setInt(R.id.dayText1_id, "setTextColor", Color.argb(20,0,0,0));
		cal = getCalendar(-1);
		date = cal.get(Calendar.DATE); 
		remoteViews.setTextViewText(R.id.dayText2_id, String.valueOf(date));
		remoteViews.setInt(R.id.dayText2_id, "setTextColor", Color.argb(120,0,0,0));
		cal = getCalendar(1);
		date = cal.get(Calendar.DATE); 
		remoteViews.setTextViewText(R.id.dayText4_id, String.valueOf(date));
		remoteViews.setInt(R.id.dayText4_id, "setTextColor", Color.argb(120,0,0,0));
		cal = getCalendar(2);
		date = cal.get(Calendar.DATE); 
		remoteViews.setTextViewText(R.id.dayText5_id, String.valueOf(date));
		remoteViews.setInt(R.id.dayText5_id, "setTextColor", Color.argb(20,0,0,0));

		updateWeather();
	    
		Intent fullIntent = new Intent(context,CityMainActivity.class); 
		PendingIntent Pfullintent = PendingIntent.getActivity(context, 0, fullIntent,PendingIntent.FLAG_CANCEL_CURRENT); 
		remoteViews.setOnClickPendingIntent(R.id.widget_main, Pfullintent);
		
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
    
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
	public Calendar getCalendar(int position)
	{	     
    	calendar.setTime(curDate); 
        calendar.add(Calendar.DATE, position);
		return calendar;
	}
	
	private void updateWeather(){
		//Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "islocation/1");
		Cursor c = this.mContext.getContentResolver().query(uri, Weather.PROJECTION_WEATHER_INFO, null, null, null);
	
	    if(c != null && c.getCount() > 0){
	    	c.moveToNext();
	    	int imageId = Utils.getWidgetWeatherIconDrawableID(c.getString(c.getColumnIndex(Weather.Columns.WEATHER_TEXT1)));
	    	remoteViews.setImageViewResource(R.id.weatherIcon2, imageId);
	    	
	    	if(Utils.isShowSun(imageId)){
	    		if(imageId == R.drawable.widget_sunny_88){
	            	remoteViews.setViewVisibility(R.id.weatherLinear1, View.VISIBLE);
	    		}
	    		else{
	            	remoteViews.setViewVisibility(R.id.weatherLinear1, View.VISIBLE);
	            	remoteViews.setViewVisibility(R.id.weatherLinear2, View.VISIBLE);
	    		}
	    	}
	    	else{
	        	remoteViews.setViewVisibility(R.id.weatherLinear2, View.VISIBLE);
	    	}
	    }
	    else{
	    	remoteViews.setViewVisibility(R.id.weatherLinear1, View.VISIBLE);
	    }
	}
}
