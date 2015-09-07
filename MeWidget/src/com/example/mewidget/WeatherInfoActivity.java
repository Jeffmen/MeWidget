package com.example.mewidget;

import com.example.mewidget.provider.Weather;
import com.example.mewidget.view.BlurLayout;
import com.example.mewidget.view.HorizontalScrollView;
import com.example.mewidget.view.HorizontalScrollViewAdapter;
import com.example.mewidget.view.IndicatorView;
import com.example.mewidget.view.WeatherCardLoader;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class WeatherInfoActivity extends StatusActivity {

    private static final int LOADER_ID = 1;
	private HorizontalScrollView scrollView;
    private HorizontalScrollViewAdapter mAdapter;
    private IndicatorView indicatorView;
    private int currentPosition = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState !=null && savedInstanceState.containsKey("currentPosition")){
        	currentPosition = savedInstanceState.getInt("currentPosition");
        }
        currentPosition = getIntent().getIntExtra(CityMainActivity.CITY_POSITION, 0);
        setContentView(R.layout.city_weather_layout);
        scrollView = (HorizontalScrollView) findViewById(R.id.mainView);
        indicatorView = (IndicatorView) findViewById(R.id.indicator);
        scrollView.setIndicator(indicatorView);
        scrollView.setSelection(currentPosition);
        getLoaderManager().initLoader(LOADER_ID, null, loadCallback);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {        

    	super.onRestoreInstanceState(savedInstanceState);
    }
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
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
			return new WeatherCardLoader(WeatherInfoActivity.this);
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

    private void bindAdapter(Cursor c)
    {
    	mAdapter = new HorizontalScrollViewAdapter(WeatherInfoActivity.this, c);
    	scrollView.setAdapter(mAdapter);
    }
}
