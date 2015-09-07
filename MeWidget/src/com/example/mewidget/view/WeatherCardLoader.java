package com.example.mewidget.view;

import com.example.mewidget.provider.Weather;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

public class WeatherCardLoader extends AsyncTaskLoader<Cursor>{
	
	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
	      @Override
	    public void onChange(boolean selfChange) {
	        //forceLoad();
	        onContentChanged();
	    }
	};
	  
	public WeatherCardLoader(Context context) {
		super(context);
	}
	
	@Override
	public Cursor loadInBackground() {
	    Cursor c = getContext().getContentResolver().query(Weather.CONTENT_URI, Weather.PROJECTION_WEATHER_INFO, null, null, null);
	    System.out.println("WeatherLoader query...");
	    
	    return c;
	}
	
	  @Override
	  protected void onStartLoading() {
		  getContext().getContentResolver().
	              registerContentObserver(Weather.CONTENT_URI, false, mObserver);
	      forceLoad();
	  }

	  @Override
	  protected void onStopLoading() {
	      getContext().getContentResolver().unregisterContentObserver(mObserver);
	  }
	  
	  @Override
	  protected void onReset() {
	      stopLoading();
	  }
	  
}