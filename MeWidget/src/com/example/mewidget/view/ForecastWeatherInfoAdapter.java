package com.example.mewidget.view;

import java.util.List;

import com.example.mewidget.R;
import com.example.mewidget.weatherlocation.CityInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ForecastWeatherInfoAdapter extends ArrayAdapter<ForecastWeatherInfo> {
	  private LayoutInflater mInflater;

	  public ForecastWeatherInfoAdapter(Context ctx) {
	    super(ctx, android.R.layout.simple_list_item_2);
	    mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View view;

	    if (convertView == null) {
	      view = mInflater.inflate(R.layout.weather_detail_info_listview_item, parent, false);
	    } else {
	      view = convertView;
	    }

	    ForecastWeatherInfo item = getItem(position);
	    ((TextView) view.findViewById(R.id.date)).setText(item.getDate());
	    ((TextView) view.findViewById(R.id.weather_text)).setText(item.getWeatherInfo());
	    ((TextView) view.findViewById(R.id.high_temp)).setText(item.getHighTemp());
	    ((TextView) view.findViewById(R.id.low_temp)).setText(item.getLowTemp());

	    return view;
	  }

	  public void setData(List<ForecastWeatherInfo> data) {
	    clear();
	    if (data != null) {
	      for (int i = 0; i < data.size(); i++) {
	        add(data.get(i));
	      }
	    }
	  }
}
