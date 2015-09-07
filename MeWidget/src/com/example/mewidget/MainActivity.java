package com.example.mewidget;

import com.example.mewidget.view.WeatherCardItemAdapter;
import com.example.mewidget.view.WeatherCardItemLayout;
import com.example.mewidget.view.WeatherCardLoader;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends StatusActivity {

    private static final int LOADER_ID = 1;
	private WeatherCardItemLayout weatherItemLayout;
    private WeatherCardItemAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherItemLayout = (WeatherCardItemLayout) findViewById(R.id.first_id);

        getLoaderManager().initLoader(LOADER_ID, null, loadCallback);
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
			return new WeatherCardLoader(MainActivity.this);
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
    	mAdapter = new WeatherCardItemAdapter(MainActivity.this, c);    
    	weatherItemLayout.setAdapter(mAdapter);
    }
}
