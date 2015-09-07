package com.example.mewidget.weatherlocation;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

public class CitySearchRequest {
	
	private final String REQUEST_URL = "http://sugg.us.search.yahoo.net/gossip-gl-location/?appid=weather&output=xml&command=%s";
	private Context mContext;
	private String searchStr;
	private List<CityInfo> cityInfo = new ArrayList<CityInfo>();
	
	public CitySearchRequest(Context context) {
		mContext = context;
	}
	
	public void setSearchString(String value){
		searchStr = value;
	}
	
	public List<CityInfo> request(){
		// 获取解析数据
		if(!searchStr.isEmpty()){
			String strUrl = String.format(REQUEST_URL, searchStr);
			try {
				URL url = new URL(strUrl);
				// 解析XML文件
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader xmlreader = parser.getXMLReader();
				CityXMLHandler handler = new CityXMLHandler();
				xmlreader.setContentHandler(handler);
				// 网络获取接口返回的XML文件
				InputStreamReader isr = new InputStreamReader(url.openStream());
				InputSource is = new InputSource(isr);
				xmlreader.parse(is);
				return handler.getCityInfo();
			} catch (Exception e) {
				Log.i("zy", "startParseXml:thought an Exception");
				e.printStackTrace();
			}
		}
		return null;
	}
}
