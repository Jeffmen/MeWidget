package com.example.mewidget;

import java.util.List;

import com.example.mewidget.weatherlocation.CityInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CityListAdapter  extends ArrayAdapter<CityInfo> {
	  private LayoutInflater mInflater;

	  public CityListAdapter(Context ctx) {
	    super(ctx, android.R.layout.simple_list_item_2);
	    mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View view;

	    if (convertView == null) {
	      view = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
	    } else {
	      view = convertView;
	    }

	    CityInfo item = getItem(position);
	    ((TextView) view.findViewById(android.R.id.text1)).setText(item.getCityShowName());

	    return view;
	  }

	  public void setData(List<CityInfo> data) {
	    clear();
	    if (data != null) {
	      for (int i = 0; i < data.size(); i++) {
	        add(data.get(i));
	      }
	    }
	  }
	}
