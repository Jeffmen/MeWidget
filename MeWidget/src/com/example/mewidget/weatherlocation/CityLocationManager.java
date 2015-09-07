package com.example.mewidget.weatherlocation;

import com.example.mewidget.weatherlocation.WeatherService.Type;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class CityLocationManager {

	private static final String TAG = "WeatherLocationManager";
	public static final String CITY_LOCATION = "city_location";
	private static final int CMD_TIMEOUT = 0x321;
	private Service mContext;
	private LocationManager locationManager;
	private boolean isLocationListenerGpsSet = false;
	private boolean isLocationListenerNetworkSet = false;
	private Location currentLocation;
	private LocationListener gpsListener;
	private LocationListener networkListner;
    private WeatherService.MyBinder myBinder;  
	
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (CMD_TIMEOUT == msg.what) {
				doLocationTimeout();
			}
		}
	};
	
	private ServiceConnection connection = new ServiceConnection() {  
		  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        }  
  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) { 
        	myBinder = (WeatherService.MyBinder) service;   
            if(currentLocation != null){
                myBinder.latLngToCity(String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()));  
    			Toast.makeText(mContext, "定位成功。。。。", Toast.LENGTH_SHORT).show();
            }
            else{
            	myBinder.weatherInfoDownLoad(Type.LOCATION);
            }
			mContext.unbindService(connection); 
        }
    };
    
	private final BroadcastReceiver mCityLocationReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.i("zy", "Receive city location broadcast....");

			if(!checkGpsAvailable()
					&& !checkNetworkAvailable()) {
				Toast.makeText(mContext, "请打开定位功能。。。。", Toast.LENGTH_SHORT).show();
				return;
			}
			startLocation();
		}
	};
	
	private class MyLocationListner implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the location provider
			if (location == null)
				return;

			currentLocation = location;
			doUpdateLocation();

			if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
				// if network location success
				removeNetworkLocationListener();
			} else {
				// if gps location success
				removeNetworkLocationListener();
				removeGpsLocationListener();
				// remove time out message
				myHandler.removeMessages(CMD_TIMEOUT);
			}
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
			if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
				removeNetworkLocationListener();
			} else {
				removeGpsLocationListener();
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public CityLocationManager(Service service) {
		mContext = service;
		init();
	}
	
	private void init(){
		locationManager =(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		register();
		Log.i("CityLocationManager", "CityLocationManager:init");
	}

    void register() {
		IntentFilter locationFilter = new IntentFilter(CITY_LOCATION);
		mContext.registerReceiver(mCityLocationReceiver, locationFilter);
    }
    
    void unregister() {
    	mContext.unregisterReceiver(mCityLocationReceiver);
      }
	
	private boolean checkGpsAvailable() {
		return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
}
	
	private boolean checkNetworkAvailable() {
		return locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
	}
	
	public void startLocation() {
		networkListner = new MyLocationListner();
		gpsListener = new MyLocationListner();
		if (!isLocationListenerNetworkSet && checkNetworkAvailable()) {
			Log.i("zy", "test location___________ NETWORK_PROVIDER is Enabled.....");
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, networkListner);
			isLocationListenerNetworkSet = true;
		}
		if (!isLocationListenerGpsSet && checkGpsAvailable()) {
			Log.i("zy", "test location___________ GPS_PROVIDER is Enabled.....");
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 10, gpsListener);
			isLocationListenerGpsSet = true;
		}
		if ((isLocationListenerGpsSet == true) || (isLocationListenerNetworkSet == true)) {
			Message message = new Message();
			message.what = CMD_TIMEOUT;
			myHandler.sendMessageDelayed(message, 120000);
		}
	}
	
	private void doUpdateLocation(){
        Intent bindIntent = new Intent(mContext, WeatherService.class);  
        mContext.bindService(bindIntent, connection, Service.BIND_AUTO_CREATE); 
	}
	
	private void removeGpsLocationListener() {
		if (gpsListener != null) {
			locationManager.removeUpdates(gpsListener);
			isLocationListenerGpsSet = false;
			Log.i("zy", "removeGpsLocationListener....");
		}
	}

	private void removeNetworkLocationListener() {
		if (networkListner != null) {
			locationManager.removeUpdates(networkListner);
			isLocationListenerNetworkSet = false;
			Log.i("zy", "removeNetworkLocationListener....");
		}
	}
	
	private void doLocationTimeout() {
		try {
			removeGpsLocationListener();
			removeNetworkLocationListener();
			if (currentLocation == null) {
				Toast.makeText(mContext, "定位失败。。。。", Toast.LENGTH_SHORT).show();
			}
			doUpdateLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
