package com.example.mewidget.view;

import java.util.ArrayList;
import java.util.List;

import com.example.mewidget.R;
import com.example.mewidget.Utils;
import com.example.mewidget.provider.Weather;
import com.example.mewidget.weatherlocation.CityInfo;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class HorizontalScrollViewAdapter extends CursorAdapter{

	private Context mContext;
    private static TypedArray sColors;
    private static int sDefaultColor;
    private int position =0;
    private LayoutInflater inflater;
	
	public HorizontalScrollViewAdapter(Context context, Cursor c) {
		super(context, c);
		this.mContext = context;
        sColors = mContext.getResources().obtainTypedArray(R.array.weather_item_colors);
        sDefaultColor = mContext.getResources().getColor(R.color.weather_item_default_color);
        inflater = LayoutInflater.from(mContext);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();
        View item = inflater.inflate(R.layout.horizontal_scrollview_blur_layout, null);
        //View content = inflater.inflate(R.layout.horizontal_scrollview_content, null);

		viewHolder.blurLayout = (BlurLayout) item.findViewById(R.id.blur_layout);
		viewHolder.sourceView = (ImageView) item.findViewById(R.id.source);
		viewHolder.blurView = (ImageView) item.findViewById(R.id.blur_image);
		viewHolder.isLocation = (ImageView) item.findViewById(R.id.is_location);
		viewHolder.scrollView = (ScrollView) item.findViewById(R.id.scroll_view);
		viewHolder.cityName = (TextView) item.findViewById(R.id.city_name);
		viewHolder.weatherTxt = (TextView) item.findViewById(R.id.weather_txt);
		viewHolder.currentTemp = (TextView) item.findViewById(R.id.current_temp_txt);
		viewHolder.humidity = (TextView) item.findViewById(R.id.txt_humidity);
		viewHolder.windSpeed = (TextView) item.findViewById(R.id.txt_wind_speed);
		viewHolder.listView = (ListView) item.findViewById(R.id.listView);
		viewHolder.forecastView = (WeatherLineView)item.findViewById(R.id.forecst_info);
		item.setTag(viewHolder);
		
        return item;//返回的view传给bindView。
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//final int color = position % sColors.length();
		//view.setBackgroundColor( sColors.getColor(color, sDefaultColor));
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        List<ForecastWeatherInfo> list = this.getForecastWeatherInfo(cursor);
		if(viewHolder.position == -1){
			viewHolder.position = position;
		}
        viewHolder.blurLayout.reset();
        final int pos = viewHolder.position%Utils.backGroundPics.length;
		//viewHolder.sourceView.setBackgroundResource(getWeatherDrawableID(cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT1))));
        //viewHolder.blurLayout.setHoverView(viewHolder.content);
		viewHolder.cityName.setText(cursor.getString(cursor.getColumnIndex(Weather.Columns.CITY_NAME)));
		int isLocation = cursor.getInt(cursor.getColumnIndex(Weather.Columns.IS_LOCATION));
		viewHolder.isLocation.setVisibility(isLocation == 1 ? View.VISIBLE : View.INVISIBLE);

		viewHolder.weatherTxt.setText(cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT1)));
		viewHolder.currentTemp.setText(cursor.getString(cursor.getColumnIndex(Weather.Columns.CURRENT_TEMP))+"°");
		
		viewHolder.humidity.setText(cursor.getString(cursor.getColumnIndex(Weather.Columns.HUMIDITY))+"%");
		viewHolder.windSpeed.setText(cursor.getString(cursor.getColumnIndex(Weather.Columns.WIND_SPEED))+" mph");
		
		//ForecastWeatherInfoAdapter adapter = new ForecastWeatherInfoAdapter(mContext);
		//adapter.setData(list);
	    //viewHolder.listView.setAdapter(adapter);
	    //viewHolder.scrollView.smoothScrollBy(0, 0);
		viewHolder.forecastView.setData(list, "", "");
		position++;
	}
	
	final static class ViewHolder {
		BlurLayout blurLayout;
		ImageView blurView;
		ImageView sourceView;
		ImageView isLocation;
		ScrollView scrollView;
		TextView cityName;
		TextView weatherTxt;
		TextView currentTemp;
		TextView humidity;
		TextView windSpeed;
		ListView listView;
		WeatherLineView forecastView;
		int position = -1;
	}

	private List<ForecastWeatherInfo> getForecastWeatherInfo(Cursor cursor){
		List<ForecastWeatherInfo> list = new ArrayList<ForecastWeatherInfo>();
		String date = cursor.getString(cursor.getColumnIndex(Weather.Columns.W_DATE1));
		String high = cursor.getString(cursor.getColumnIndex(Weather.Columns.MAX_TEMP1));
		String low = cursor.getString(cursor.getColumnIndex(Weather.Columns.MIN_TEMP1));
		String text = cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT1));
		ForecastWeatherInfo info = new ForecastWeatherInfo();
		info.setDate(date);
		info.setHighTemp(high);
		info.setLowTemp(low);
		info.setWeatherInfo(text);
		info.setWeatherIcon(Utils.getWeatherIconDrawableID(info.getWeatherInfo()));
		list.add(info);
		date = cursor.getString(cursor.getColumnIndex(Weather.Columns.W_DATE2));
		high = cursor.getString(cursor.getColumnIndex(Weather.Columns.MAX_TEMP2));
		low = cursor.getString(cursor.getColumnIndex(Weather.Columns.MIN_TEMP2));
		text = cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT2));
		info = new ForecastWeatherInfo();
		info.setDate(date);
		info.setHighTemp(high);
		info.setLowTemp(low);
		info.setWeatherInfo(text);
		info.setWeatherIcon(Utils.getWeatherIconDrawableID(info.getWeatherInfo()));
		list.add(info);
		date = cursor.getString(cursor.getColumnIndex(Weather.Columns.W_DATE3));
		high = cursor.getString(cursor.getColumnIndex(Weather.Columns.MAX_TEMP3));
		low = cursor.getString(cursor.getColumnIndex(Weather.Columns.MIN_TEMP3));
		text = cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT3));
		info = new ForecastWeatherInfo();
		info.setDate(date);
		info.setHighTemp(high);
		info.setLowTemp(low);
		info.setWeatherInfo(text);
		info.setWeatherIcon(Utils.getWeatherIconDrawableID(info.getWeatherInfo()));
		list.add(info);
		date = cursor.getString(cursor.getColumnIndex(Weather.Columns.W_DATE4));
		high = cursor.getString(cursor.getColumnIndex(Weather.Columns.MAX_TEMP4));
		low = cursor.getString(cursor.getColumnIndex(Weather.Columns.MIN_TEMP4));
		text = cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT4));
		info = new ForecastWeatherInfo();
		info.setDate(date);
		info.setHighTemp(high);
		info.setLowTemp(low);
		info.setWeatherInfo(text);
		info.setWeatherIcon(Utils.getWeatherIconDrawableID(info.getWeatherInfo()));
		list.add(info);
		date = cursor.getString(cursor.getColumnIndex(Weather.Columns.W_DATE5));
		high = cursor.getString(cursor.getColumnIndex(Weather.Columns.MAX_TEMP5));
		low = cursor.getString(cursor.getColumnIndex(Weather.Columns.MIN_TEMP5));
		text = cursor.getString(cursor.getColumnIndex(Weather.Columns.WEATHER_TEXT5));
		info = new ForecastWeatherInfo();
		info.setDate(date);
		info.setHighTemp(high);
		info.setLowTemp(low);
		info.setWeatherInfo(text);
		info.setWeatherIcon(Utils.getWeatherIconDrawableID(info.getWeatherInfo()));
		list.add(info);
		return list;
	}
}
