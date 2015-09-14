package com.example.mewidget.view;

import java.util.Random;

import com.example.mewidget.R;
import com.example.mewidget.Utils;
import com.example.mewidget.provider.Weather;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherCardItemAdapter extends CursorAdapter{

	private Context mContext;
    private static TypedArray sColors;
    private static int sDefaultColor;
    private int position =0;
    private static int random = (new Random()).nextInt(30);
    
	public WeatherCardItemAdapter(Context context, Cursor c) {
		super(context, c);
		this.mContext = context;
        sDefaultColor = mContext.getResources().getColor(R.color.weather_item_default_color);
        sColors = mContext.getResources().obtainTypedArray(R.array.weather_item_colors);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		WeatherCardItem item = new WeatherCardItem(this.mContext);
		item.setCityName(cursor.getString(cursor.getColumnIndex(Weather.Columns.CITY_NAME)));
		int isLocation = cursor.getInt(cursor.getColumnIndex(Weather.Columns.IS_LOCATION));
		item.setLocationVisable(isLocation == 1 ? View.VISIBLE : View.INVISIBLE);
		item.setTemperature(cursor.getString(cursor.getColumnIndex(Weather.Columns.CURRENT_TEMP))+"°C");
		item.setWeather(cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT1)));
		item.setHumidity(cursor.getString(cursor.getColumnIndex(Weather.Columns.HUMIDITY))+"%");
		item.setWindSpeed(cursor.getString(cursor.getColumnIndex(Weather.Columns.WIND_SPEED))+" mph");
		Utils.setImageAnimation(item, cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT1)));
        return item;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		position = cursor.getPosition() + random;
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		view.setBackgroundColor(sColors.getColor(position % sColors.length(), sDefaultColor));
	}
	
    private int pickColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, sDefaultColor);
    }
    
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView ivIcon;
	}
}
