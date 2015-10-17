package com.example.mewidget.provider;

import android.net.Uri;

public class Weather {
	public static final String TABLE_NAME = "weather";
    public static final Uri CONTENT_URI  = Uri.parse("content://"+DBProvider.AUTHORITY+"/"+TABLE_NAME);
    public static String[] PROJECTION_CITY_INFO = new String[] { 
    		Columns._ID,
    		Columns.CITY_NAME, 
    		Columns.CITY_ID,
    		Columns.LATITUDE,
    		Columns.LONGITUDE,
    		Columns.IS_LOCATION
    };
    public static String[] PROJECTION_WEATHER_INFO = new String[] { 
		Columns._ID,
		Columns.CITY_NAME, 
		Columns.CITY_ID,
		Columns.LATITUDE,
		Columns.LONGITUDE, 		
		
		Columns.IS_LOCATION,
		Columns.BEGIN_DATE, 
		Columns.CURRENT_TEMP,
		Columns.WIND_SPEED,
		Columns.WIND_DIRECTION,
		Columns.HUMIDITY,
		Columns.VISIBILITY, 
		Columns.SUNRISE,
		Columns.SUNSET,
		
		Columns.MAX_TEMP1,
		Columns.MIN_TEMP1,
		Columns.W_DATE1, 
		Columns.WEATHER_TEXT1,

		Columns.MAX_TEMP2,
		Columns.MIN_TEMP2,
		Columns.W_DATE2, 
		Columns.WEATHER_TEXT2,
		
		Columns.MAX_TEMP3,
		Columns.MIN_TEMP3,
		Columns.W_DATE3, 
		Columns.WEATHER_TEXT3,

		Columns.MAX_TEMP4,
		Columns.MIN_TEMP4,
		Columns.W_DATE4, 
		Columns.WEATHER_TEXT4,

		Columns.MAX_TEMP5,
		Columns.MIN_TEMP5,
		Columns.W_DATE5, 
		Columns.WEATHER_TEXT5,
};
    public interface Columns{
	    public static final String _ID = "_id";
	    public static final String CITY_NAME = "city_name";
	    public static final String CITY_ID = "city_id";
		public static final String COUNTRY_NAME = "country_name";
	    public static final String LATITUDE = "latitude";
	    public static final String LONGITUDE = "longitude";
	    public static final String IS_LOCATION = "is_location";
		public static final String BEGIN_DATE = "begin_date";
	    public static final String CURRENT_TEMP = "current_temp";
    	public static final String WIND_SPEED = "wind_speed";
    	public static final String WIND_DIRECTION = "wind_direction";
    	public static final String HUMIDITY = "humidity";
    	public static final String VISIBILITY = "visibility";
    	public static final String SUNRISE = "sunrise";
    	public static final String SUNSET = "sunset";
	    public static final String MAX_TEMP1 = "max_temp1";
	    public static final String MIN_TEMP1 = "min_temp1";
	    public static final String W_DATE1 = "w_date1";
	    public static final String WEATHER_TEXT1 = "weather_text1";
	    public static final String MAX_TEMP2 = "max_temp2";
	    public static final String MIN_TEMP2 = "min_temp2";
	    public static final String W_DATE2 = "w_date2";
	    public static final String WEATHER_TEXT2 = "weather_text2";
	    public static final String MAX_TEMP3 = "max_temp3";
	    public static final String MIN_TEMP3 = "min_temp3";
	    public static final String W_DATE3 = "w_date3";
	    public static final String WEATHER_TEXT3 = "weather_text3";
	    public static final String MAX_TEMP4 = "max_temp4";
	    public static final String MIN_TEMP4 = "min_temp4";
	    public static final String W_DATE4 = "w_date4";
	    public static final String WEATHER_TEXT4 = "weather_text4";
	    public static final String MAX_TEMP5 = "max_temp5";
	    public static final String MIN_TEMP5 = "min_temp5";
	    public static final String W_DATE5 = "w_date5";
	    public static final String WEATHER_TEXT5 = "weather_text5";
    }
}
