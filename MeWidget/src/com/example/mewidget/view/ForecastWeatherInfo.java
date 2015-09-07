package com.example.mewidget.view;

public class ForecastWeatherInfo {
	private String date;
	public void setDate(String value){
		date = value;
	}
	public String getDate(){
		return date == null ? "" : date;
	}
	
	private String weatherInfo;
	public void setWeatherInfo(String value){
		weatherInfo = value;
	}
	public String getWeatherInfo(){
		return weatherInfo == null ? "" : weatherInfo;
	}
	
	private int weatherIcon;
	public void setWeatherIcon(int value){
		weatherIcon = value;
	}
	public int getWeatherIcon(){
		return weatherIcon;
	}
	
	private int highTemp;
	public void setHighTemp(String value){
		if(value != null && !value.isEmpty()){
			highTemp = Integer.parseInt(value);
		}
		else{
			highTemp = 0;
		}
	}
	public int getHighTemp(){
		return highTemp;
	}
	
	private int lowTemp;
	public void setLowTemp(String value){
		if(value != null && !value.isEmpty()){
			lowTemp = Integer.parseInt(value);
		}
		else{
			lowTemp = 0;
		}
	}
	public int getLowTemp(){
		return lowTemp;
	}		
	
	public ForecastWeatherInfo(){
	}	
	
	public ForecastWeatherInfo(int _high, int _low, String _date){
		this.highTemp = _high;
		this.lowTemp = _low;
		this.date = _date;
	}
	
	public ForecastWeatherInfo(String _high, String _low, String _date){
		this.highTemp = Integer.parseInt(_high);
		this.lowTemp = Integer.parseInt(_low);
		this.date = _date;
	}
}
