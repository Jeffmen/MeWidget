package com.example.mewidget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.mewidget.provider.Weather;
import com.example.mewidget.view.DragListView;
import com.example.mewidget.view.PullToRefreshLayout;
import com.example.mewidget.view.WeatherCardItemAdapter;
import com.example.mewidget.view.WeatherCardLoader;
import com.example.mewidget.weatherlocation.CityLocationManager;
import com.example.mewidget.weatherlocation.WeatherService;
import com.example.mewidget.weatherlocation.WeatherService.Type;

public class CityMainActivity extends StatusActivity {

    private static final int LOADER_ID = 1;
    public static final String CITY_POSITION = "city_position";
	private DragListView dragListView;
    private WeatherCardItemAdapter mAdapter;
    private WeatherService.MyBinder myBinder;  
	private PullToRefreshLayout refreshLayout;
    
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new  SwipeRefreshLayout.OnRefreshListener(){
        @Override
		public void onRefresh() {
            new Handler()  
            {  
                @Override  
                public void handleMessage(Message msg)  
                {  
                    refreshLayout.refreshFinish(PullToRefreshLayout.REFRESH_SUCCEED);  
                }  
            }.sendEmptyMessageDelayed(0, 3000);
		}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_main_layout);
        dragListView = (DragListView) findViewById(R.id.first_id);
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
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setOnRefreshListener(swipeRefreshListener);
        getLoaderManager().initLoader(LOADER_ID, null, loadCallback);
		//Intent serviceintent = new Intent(CityMainActivity.this, WeatherService.class);
		//startService(serviceintent);
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
            myBinder = (WeatherService.MyBinder) service;  
            myBinder.weatherInfoDownLoad(Type.ALL);
            unbindService(connection); 
        }
    };
    
    private void bindAdapter(Cursor c)
    {
    	mAdapter = new WeatherCardItemAdapter(CityMainActivity.this, c);    
    	dragListView.setAdapter(mAdapter);
    }
}
