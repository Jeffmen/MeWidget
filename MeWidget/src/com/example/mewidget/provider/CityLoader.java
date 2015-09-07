package com.example.mewidget.provider;

import java.util.List;

import com.example.mewidget.weatherlocation.CityInfo;
import com.example.mewidget.weatherlocation.CitySearchRequest;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class CityLoader extends AsyncTaskLoader<List<CityInfo>> {

	private Context mContext;
	private String searchKey;
	
	public CityLoader(Context context, String value) {
		super(context);
		mContext = context;
		searchKey = value;
	}

	@Override
	public List<CityInfo> loadInBackground() {
        CitySearchRequest request = new CitySearchRequest(mContext);
        request.setSearchString(searchKey);
		return request.request();
	}

	  @Override
	  protected void onStartLoading() {
		  forceLoad();
	  }
}
