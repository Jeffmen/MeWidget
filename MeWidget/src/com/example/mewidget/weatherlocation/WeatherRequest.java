package com.example.mewidget.weatherlocation;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mewidget.provider.Weather;
import com.example.mewidget.weatherlocation.RequestManager.Request;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class WeatherRequest implements Request {
	//搜索城市http://sugg.us.search.yahoo.net/gossip-gl-location/?appid=weather&output=xml&command=%E5%B9%BF
	//查询城市select * from flickr.places where lon=116.32298703399 and lat=39.983424051248 and accuracy=6
	//http://maps.google.cn/maps/api/geocode/json?latlng=41.00,29.00&sensor=true&language=zh-CN
    private final String REQUEST_URL = "http://query.yahooapis.com/v1/public/yql?format=json&q=";
	private List<CityInfo> cityList = new ArrayList<CityInfo>();
	private Context mContext;
	
	public WeatherRequest(Context context) {
		mContext = context;
	}

	public void addCityInfo(CityInfo info){
		cityList.add(info);
	}
	
	public void removeCityInfo(CityInfo info){
		cityList.remove(info);
	}
	
	public void clearCityInfo(){
		cityList.clear();
	}
	
	private String geCityNameSql(){
		String res = "";
		CityInfo info;
		for(int i=0; i<cityList.size();i++){
			info = cityList.get(i);
			if(!info.getCityName().isEmpty() && info.getCityName() != ""){
				res += "'"+info.getCityName().replace("'", "")+"',";
			}
		}
		if(res.endsWith(",")){
			res = res.substring(0, res.length()-1);
		}
		if(!res.isEmpty()){
			res = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text in (%s))",res);
		}
		return res;
	}
	
	private String getCityIdSql(){
		String res = "";
		CityInfo info;
		for(int i=0; i<cityList.size();i++){
			info = cityList.get(i);
			if(!info.getCityId().isEmpty() && info.getCityId() != ""){
				res += info.getCityId()+",";
			}
		}
		if(res.endsWith(",")){
			res = res.substring(0, res.length()-1);
		}
		if(!res.isEmpty()){
			res = String.format("select * from weather.forecast where woeid in (%s)",res);
		}
		return res;
	}
	
	public JsonObjectRequest getRequest(){
		String strUrl = "";
		if(cityList.size()>0){
			String sql = geCityNameSql();
	//		else if(!mCityInfo.getLatitude().isEmpty() && !mCityInfo.getLongitude().isEmpty()){
	//			strUrl += mCityInfo.getLatitude() + "," + mCityInfo.getLongitude();
	//		}
            if(!sql.isEmpty()){
				try {
					strUrl = REQUEST_URL + URLEncoder.encode(sql, "UTF-8");
					JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strUrl, null,  
					        new Response.Listener<JSONObject>() {  
					            @Override  
					            public void onResponse(JSONObject response) {  
					            	parseJson(response); 
					            }  
					        }, new Response.ErrorListener() {  

								@Override
								public void onErrorResponse(VolleyError error) {
									Log.i("zy", "startParseXml:thought an Exception");
								}  
					        });

					return jsonObjectRequest;
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
		}
		return null;
	}
	
	private void parseJson(JSONObject input){
        //String json=new String(Utils.convertIsToByteArray(input));
        try {
        	JSONObject  jObject =input;
        	JSONObject query = jObject.getJSONObject("query");
        	int count = query.getInt("count");
	    	JSONObject results = query.getJSONObject("results");
	    	if(count > 1){
		    	JSONArray channel = results.getJSONArray("channel");
		    	for(int j=0; j< channel.length(); j++){
		    		
		        	saveToDatabase(channel.getJSONObject(j));
		        	
		    	}
	    	}
	    	else if(count == 1){
	    		saveToDatabase(results.getJSONObject("channel"));
	    	}

        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
    
    //https://developer.yahoo.com/weather/
    private void saveToDatabase(JSONObject channel){
    	
		try {
			
	    	JSONObject location = channel.getJSONObject("location");
	    	JSONObject wind = channel.getJSONObject("wind");
	    	JSONObject atmosphere = channel.getJSONObject("atmosphere");
	    	JSONObject astronomy = channel.getJSONObject("astronomy");
	    	JSONObject item = channel.getJSONObject("item");
	    	JSONObject condition = item.getJSONObject("condition");
	        JSONArray forecast = item.getJSONArray("forecast");
	    	
	        ContentValues values = new ContentValues();
	        
	    	values.put(Weather.Columns.CITY_NAME, location.getString("city"));
	    	values.put(Weather.Columns.COUNTRY_NAME, location.getString("country"));
	    	values.put(Weather.Columns.WIND_SPEED, wind.getString("speed"));
	    	values.put(Weather.Columns.WIND_DIRECTION, wind.getString("direction"));
	    	values.put(Weather.Columns.HUMIDITY, atmosphere.getString("humidity"));
	    	values.put(Weather.Columns.VISIBILITY, atmosphere.getString("visibility"));
	    	values.put(Weather.Columns.SUNRISE, astronomy.getString("sunrise"));
	    	values.put(Weather.Columns.SUNSET, astronomy.getString("sunset"));
			int temp = Integer.parseInt(condition.getString("temp"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.CURRENT_TEMP, String.valueOf(temp));
	    	
	    	temp = Integer.parseInt(forecast.getJSONObject(0).getString("low"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MIN_TEMP1, temp);
	    	temp = Integer.parseInt(forecast.getJSONObject(0).getString("high"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MAX_TEMP1, temp);
	    	values.put(Weather.Columns.W_DATE1, forecast.getJSONObject(0).getString("day"));
	    	values.put(Weather.Columns.WEATHER_TEXT1, forecast.getJSONObject(0).getString("text"));

	    	temp = Integer.parseInt(forecast.getJSONObject(1).getString("low"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MIN_TEMP2, temp);
	    	temp = Integer.parseInt(forecast.getJSONObject(1).getString("high"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MAX_TEMP2, temp);
	    	values.put(Weather.Columns.W_DATE2, forecast.getJSONObject(1).getString("day"));
	    	values.put(Weather.Columns.WEATHER_TEXT2, forecast.getJSONObject(1).getString("text"));


	    	temp = Integer.parseInt(forecast.getJSONObject(2).getString("low"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MIN_TEMP3, temp);
	    	temp = Integer.parseInt(forecast.getJSONObject(2).getString("high"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MAX_TEMP3, temp);
	    	values.put(Weather.Columns.W_DATE3, forecast.getJSONObject(2).getString("day"));
	    	values.put(Weather.Columns.WEATHER_TEXT3, forecast.getJSONObject(2).getString("text"));


	    	temp = Integer.parseInt(forecast.getJSONObject(3).getString("low"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MIN_TEMP4, temp);
	    	temp = Integer.parseInt(forecast.getJSONObject(3).getString("high"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MAX_TEMP4, temp);
	    	values.put(Weather.Columns.W_DATE4, forecast.getJSONObject(3).getString("day"));
	    	values.put(Weather.Columns.WEATHER_TEXT4, forecast.getJSONObject(3).getString("text"));


	    	temp = Integer.parseInt(forecast.getJSONObject(4).getString("low"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MIN_TEMP5, temp);
	    	temp = Integer.parseInt(forecast.getJSONObject(4).getString("high"));
			temp = (int)((temp-32)/1.8);
	    	values.put(Weather.Columns.MAX_TEMP5, temp);
	    	values.put(Weather.Columns.W_DATE5, forecast.getJSONObject(4).getString("day"));
	    	values.put(Weather.Columns.WEATHER_TEXT5, forecast.getJSONObject(4).getString("text"));
	        
   
        	Uri uri = Uri.withAppendedPath(Weather.CONTENT_URI, "cityname/"+location.getString("city"));
        	mContext.getContentResolver().update(uri, values, null, null);

	    	values.put(Weather.Columns.CITY_NAME, "Chizhou");
        	uri = Uri.withAppendedPath(Weather.CONTENT_URI, "islocation/1");
        	mContext.getContentResolver().update(uri, values, null, null);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
        
        
    }
}
