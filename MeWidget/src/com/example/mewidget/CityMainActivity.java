package com.example.mewidget;

import android.app.LoaderManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.mewidget.provider.Weather;
import com.example.mewidget.view.SpreadListView;
import com.example.mewidget.view.WeatherCardItemAdapter;
import com.example.mewidget.view.WeatherCardLoader;
import com.example.mewidget.weatherlocation.CityLocationManager;
import com.example.mewidget.weatherlocation.WeatherService;

public class CityMainActivity extends StatusActivity {

    private static final int LOADER_ID = 1;
    public static final String CITY_POSITION = "city_position";
	private SpreadListView dragListView;
    private WeatherCardItemAdapter mAdapter;
    private WeatherService.MyBinder myBinder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_main_layout);
        dragListView = (SpreadListView) findViewById(R.id.first_id);
        dragListView.setLastView(LayoutInflater.from(this).inflate(R.layout.weathercard_last_item, null));
        dragListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	            Intent intent=new Intent(CityMainActivity.this, WeatherInfoActivity.class);
	            intent.putExtra(CITY_POSITION, position);
	            startActivity(intent);
		    }
		});
        dragListView.setLastItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	            Intent intent=new Intent(CityMainActivity.this, AddCityActivity.class);
	            startActivity(intent);
			}
	    });        
        dragListView.setDeleteItemListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Cursor cursor = (Cursor)mAdapter.getItem(position);
				String cityName = cursor.getString(cursor.getColumnIndex(Weather.Columns.CITY_NAME));
	            Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "cityname/" + cityName);
		        getContentResolver().delete(uri, null, null);
			}

		});
        getLoaderManager().initLoader(LOADER_ID, null, loadCallback);
		startLocation();
		startWeatherDownLoad();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent serviceintent = new Intent(CityMainActivity.this, WeatherService.class);
		stopService(serviceintent);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private LoaderManager.LoaderCallbacks<Cursor> loadCallback = new LoaderManager.LoaderCallbacks<Cursor>(){

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new WeatherCardLoader(CityMainActivity.this);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	        bindAdapter(data);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			bindAdapter(null);
		}
		
	};
	
	private void startLocation(){
		Intent intent = new Intent();
		intent.setAction(CityLocationManager.CITY_LOCATION);
		sendBroadcast(intent);
	}	
	
	private void startWeatherDownLoad(){
        Intent bindIntent = new Intent(CityMainActivity.this, WeatherService.class);  
        bindService(bindIntent, connection, Service.BIND_AUTO_CREATE); 
	}
	
	private ServiceConnection connection = new ServiceConnection() {  
		  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        }  
  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  

			Uri uri = Weather.CONTENT_URI;
            myBinder = (WeatherService.MyBinder) service;  
            myBinder.weatherInfoDownLoad(uri);
            unbindService(connection); 
        }
    };
    
    private void bindAdapter(Cursor c)
    {
    	mAdapter = new WeatherCardItemAdapter(CityMainActivity.this, c);    
    	dragListView.setAdapter(mAdapter);
    }
}
