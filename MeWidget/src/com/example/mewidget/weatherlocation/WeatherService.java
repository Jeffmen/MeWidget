package com.example.mewidget.weatherlocation;

import com.example.mewidget.provider.Weather;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WeatherService extends Service {
    
	private CityLocationManager mCityLocationManager;	
	private WeatherRequest mWeatherRequest;
	private LocationRequest mLocationRequest;
	public static final String CITY_ID = "city_id";
	public static final String CITY_NAME = "city_name";
	public static final String CITY_LATITUDE = "city_latitude";
	public static final String CITY_LONGITUDE = "city_longitude";
	private MyBinder mBinder = new MyBinder();
	
	public enum Type{
		ALL,//原有城市
		LOCATION,//定位城市
		OTHER//除了定位城市
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mWeatherRequest = new WeatherRequest(this);
		mLocationRequest = new LocationRequest(this);
		mCityLocationManager = new CityLocationManager(this);
		Log.i("WeatherService", "WeatherService:onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*String id = intent.getStringExtra(CITY_ID);
		String name = intent.getStringExtra(CITY_NAME);
		double weatherLatitude = intent.getDoubleExtra(CITY_LATITUDE, -1);
		double weatherLongitude = intent.getDoubleExtra(CITY_LONGITUDE, -1);*/
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		mCityLocationManager.unregister();
	}
	
	public class MyBinder extends Binder {  
		  
        public void weatherInfoDownLoad(Type type) {  
            Log.d("TAG", "weatherInfoDownLoad() executed");  
            // 执行具体的下载任务  
            AsyncTask<Type, Void, Integer> mTask = new WeatherInfoDownloadTask();
    		if (mTask.getStatus() == AsyncTask.Status.PENDING) {
    			mTask.execute(type);
    		}
        }  
        
        public void latLngToCity(String latitude, String longitude) {  
            Log.d("TAG", "latLngToCity() executed"); 
            // 执行具体的下载任务  
            AsyncTask<String, Void, Integer> mTask = new LatLngToCityTask();
    		if (mTask.getStatus() == AsyncTask.Status.PENDING) {
    			mTask.execute(latitude, longitude);
    		}
        } 
    } 
	//下载天气数据
	private class WeatherInfoDownloadTask extends AsyncTask<Type, Void, Integer> {
		@Override
		protected Integer doInBackground(Type... params) {
		    String selection = null;
		    mWeatherRequest.clearCityInfo();
		    Uri uri;
		    uri = Weather.CONTENT_URI;
		    if(params[0] == Type.LOCATION){
		    	uri = Uri.withAppendedPath(Weather.CONTENT_URI, "islocation/1");
		    }
		    else if(params[0] == Type.OTHER){
		    	uri = Uri.withAppendedPath(Weather.CONTENT_URI, "islocation/0");
		    }
			Cursor cursor = getContentResolver().query(uri, Weather.PROJECTION_CITY_INFO, selection, null, null);
		    while(cursor.moveToNext()){
		    	CityInfo info = new CityInfo();
		    	info.setCityName(cursor.getString(cursor.getColumnIndex(Weather.Columns.CITY_NAME)));
		    	mWeatherRequest.addCityInfo(info);
		    }
            mWeatherRequest.request();
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (isCancelled()) {
				return;
			}
		}
	}

	//通过经纬度查查找城市
	private class LatLngToCityTask extends AsyncTask<String, Void, Integer> {
		
		@Override
		protected Integer doInBackground(String... params) {
			mLocationRequest.setLatLon(params[0], params[1]);
			mLocationRequest.request();
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (isCancelled()) {
				return;
			}
			mBinder.weatherInfoDownLoad(Type.ALL);
		}
	}
}
