package com.example.mewidget.weatherlocation;

import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mewidget.provider.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class LocationRequest {
	
	private final String REQUEST_URL = "http://maps.google.cn/maps/api/geocode/json?latlng=%s,%s&language=EN";
	private String latitude;
	private String longitude;
	private Context mContext;
	
	public LocationRequest(Context context) {
		mContext = context;
	}
	
	public void setLatLon(String lat, String lon){
		latitude = lat;
		longitude = lon;
	}
	
	public void request(){
		// 获取解析数据
		if(!latitude.isEmpty() && !longitude.isEmpty()){
			String strUrl = String.format(REQUEST_URL, latitude, longitude);
			try {
				URL url = new URL(strUrl);
				
				parseJson(url.openStream());
				
			} catch (Exception e) {
				Log.i("zy", "startParseXml:thought an Exception");
				e.printStackTrace();
			}
		}
	}

	private void parseJson(InputStream input){
        String json=new String(com.example.mewidget.Utils.convertIsToByteArray(input));
        try {
        	JSONObject  jObject =new JSONObject(json);
        	if(jObject.getString("status").equals("OK")){
        		JSONArray results = jObject.getJSONArray("results");
	    		if(results.length() > 0){
			    	JSONArray address = results.getJSONObject(0).getJSONArray("address_components");
			    	saveToDatabase(address);
	    		}
        	}
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

    private void saveToDatabase(JSONArray address){
        String cityName = null;
        String country = null;

    	JSONArray types;
		try {
	    	for(int j=0; j< address.length(); j++){
				types = address.getJSONObject(j).getJSONArray("types");
	    		if(types.length() > 0){
	    			if(types.getString(0).equals("locality")){
	    				cityName = address.getJSONObject(j).getString("long_name");
		    		}
	    			if(types.getString(0).equals("country")){
	    				country = address.getJSONObject(j).getString("long_name");
		    		}
	    		}
	    	}

			if(!cityName.isEmpty()){
		    	ContentValues values = new ContentValues();
		    	values.put(Weather.Columns.CITY_NAME, cityName);
		    	values.put(Weather.Columns.COUNTRY_NAME, country);
		    	values.put(Weather.Columns.IS_LOCATION, 1);
		    	values.put(Weather.Columns.LATITUDE, latitude);
		    	values.put(Weather.Columns.LONGITUDE, longitude);
		    	
		    	Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "islocation/1");
				mContext.getContentResolver().update(uri, values,null,null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
