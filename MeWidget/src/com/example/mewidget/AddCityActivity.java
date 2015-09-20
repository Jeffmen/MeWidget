package com.example.mewidget;

import java.util.List;

import com.example.mewidget.provider.CityLoader;
import com.example.mewidget.provider.Weather;
import com.example.mewidget.view.PullToRefreshLayout;
import com.example.mewidget.weatherlocation.CityInfo;
import com.example.mewidget.weatherlocation.WeatherService;

import android.app.LoaderManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddCityActivity extends StatusActivity implements TextWatcher{

    private static final int LOADER_ID = 1;
    private ListView listView; 
	private PullToRefreshLayout refreshLayout;
    private EditText mSearchView;
    private ImageView mClearBtn;
    private String mSearchString;
    private CityListAdapter adapter;
    private TextView cancelButton;
    private WeatherService.MyBinder myBinder;  
    private String cityName;
    
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new  SwipeRefreshLayout.OnRefreshListener(){
        @Override
		public void onRefresh() {
//        	refreshLayout.setRefreshing(false);
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					//mSwipeLayout.setRefreshing(false);
//				}
//			}, 5000);
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
        setContentView(R.layout.add_city);

		adapter = new CityListAdapter(AddCityActivity.this);
        listView = (ListView) findViewById(R.id.listView);   
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InsertCityInfo(adapter.getItem(position));
				startWeatherDownLoad();
				finish();
			}
		});
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setOnRefreshListener(swipeRefreshListener);
		
        mSearchView = (EditText)(findViewById(R.id.search_view));
        //mSearchView.setVisibility(View.GONE);
        mSearchView.addTextChangedListener(this);
        
        mClearBtn = (ImageView)(findViewById(R.id.clear_button));
        mClearBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
            	mSearchView.setText("");
            	refreshLayout.setRefreshing(false);
    			getLoaderManager().destroyLoader(LOADER_ID);
                mClearBtn.setVisibility(View.GONE);
            }           
        });

        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private LoaderManager.LoaderCallbacks<List<CityInfo>> loadCallback = new LoaderManager.LoaderCallbacks<List<CityInfo>>(){

		@Override
		public Loader<List<CityInfo>> onCreateLoader(int id, Bundle args) {
			return new CityLoader(AddCityActivity.this, mSearchString);
		}

		@Override
		public void onLoadFinished(Loader<List<CityInfo>> loader,
				List<CityInfo> data) {
			showSearchCitys(data);
		}

		@Override
		public void onLoaderReset(Loader<List<CityInfo>> loader) {
			showSearchCitys(null);
		}
		
	};
	
	private void showSearchCitys(List<CityInfo> data) {
		adapter.setData(data);
		listView.setAdapter(adapter);
		adapter.setNotifyOnChange(true);
		refreshLayout.setRefreshing(false);
	}
    
	private void InsertCityInfo(CityInfo info){
		if(info != null){
			cityName = info.getCityName();
            Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "cityname/" + info.getCityName());
	        Cursor c = getContentResolver().query(uri, Weather.PROJECTION_CITY_INFO, null, null, null);
            if(c == null || c.getCount() == 0){
		        ContentValues values = new ContentValues();
		        
		        values.put(Weather.Columns.CITY_NAME, info.getCityName());
		        values.put(Weather.Columns.CITY_ID, info.getCityId());
		        values.put(Weather.Columns.COUNTRY_NAME, info.getCountroy());
		        values.put(Weather.Columns.LATITUDE, info.getLatitude());
		        values.put(Weather.Columns.LONGITUDE, info.getLongitude());
		        values.put(Weather.Columns.IS_LOCATION, "0");
		        getContentResolver().insert(Weather.CONTENT_URI, values);
            }
		}
	}	
	
	private void startWeatherDownLoad(){
        Intent bindIntent = new Intent(AddCityActivity.this, WeatherService.class);  
        bindService(bindIntent, connection, Service.BIND_AUTO_CREATE); 
	}
	
	private ServiceConnection connection = new ServiceConnection() {  
		  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        }  
  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            if(!cityName.isEmpty()){
				Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "cityname/" + cityName);
	            myBinder = (WeatherService.MyBinder) service;  
	            myBinder.weatherInfoDownLoad(uri);
	            unbindService(connection); 
            }
        }
    };

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count){
		if(s.length() > 0){
			mClearBtn.setVisibility(View.VISIBLE);
			mSearchString = s.toString();
			refreshLayout.setRefreshing(true);
			getLoaderManager().restartLoader(LOADER_ID, null, loadCallback);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}
}
