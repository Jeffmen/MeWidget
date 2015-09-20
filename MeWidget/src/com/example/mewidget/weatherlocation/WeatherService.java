package com.example.mewidget.weatherlocation;

import com.example.mewidget.provider.Weather;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class WeatherService extends Service {
    
	private CityLocationManager mCityLocationManager;	
	private WeatherRequest mWeatherRequest;
	private LocationRequest mLocationRequest;
	private RequestManager requestManager;
	public static final String CITY_ID = "city_id";
	public static final String CITY_NAME = "city_name";
	public static final String CITY_LATITUDE = "city_latitude";
	public static final String CITY_LONGITUDE = "city_longitude";
	private MyBinder mBinder = new MyBinder();
	
	@Override
	public void onCreate() {
		super.onCreate();
		mWeatherRequest = new WeatherRequest(this);
		mLocationRequest = new LocationRequest(this);
		mCityLocationManager = new CityLocationManager(this);
		requestManager = new RequestManager(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		mCityLocationManager.unregister();
	}
	
	public class MyBinder extends Binder {  
		  
        public void weatherInfoDownLoad(Uri uri) {  
            // Download weather information 
            AsyncTask<Uri, Void, Integer> mTask = new WeatherInfoDownloadTask();
    		if (mTask.getStatus() == AsyncTask.Status.PENDING) {
    			mTask.execute(uri);
    		}
        }  
        
        public void latLngToCity(String latitude, String longitude) {
            // Download location information 
            AsyncTask<String, Void, Integer> mTask = new LatLngToCityTask();
    		if (mTask.getStatus() == AsyncTask.Status.PENDING) {
    			mTask.execute(latitude, longitude);
    		}
        } 
    } 

	private class WeatherInfoDownloadTask extends AsyncTask<Uri, Void, Integer> {
		@Override
		protected Integer doInBackground(Uri... params) {
		    String selection = null;
		    mWeatherRequest.clearCityInfo();
		    Uri uri  = params[0];
			Cursor cursor = getContentResolver().query(uri, Weather.PROJECTION_CITY_INFO, selection, null, null);
		    while(cursor.moveToNext()){
		    	CityInfo info = new CityInfo();
		    	info.setCityName(cursor.getString(cursor.getColumnIndex(Weather.Columns.CITY_NAME)));
		    	mWeatherRequest.addCityInfo(info);
		    }
            requestManager.addRequest(mWeatherRequest.getRequest());
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (isCancelled()) {
				return;
			}
		}
	}

	private class LatLngToCityTask extends AsyncTask<String, Void, Integer> {
		
		@Override
		protected Integer doInBackground(String... params) {
			mLocationRequest.setLatLon(params[0], params[1]);
            requestManager.addRequest(mLocationRequest.getRequest());
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (isCancelled()) {
				return;
			}
			Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "islocation/1");
			mBinder.weatherInfoDownLoad(uri);
		}
	}
}
