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
	
	public CitySearchRequest(Context context) {
		mContext = context;
	}
	
	public void setSearchString(String value){
		searchStr = value;
	}
	
	public List<CityInfo> request(){
		if(!searchStr.isEmpty()){
			String strUrl = String.format(REQUEST_URL, searchStr);
			try {
				URL url = new URL(strUrl);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader xmlreader = parser.getXMLReader();
				CityXMLHandler handler = new CityXMLHandler();
				xmlreader.setContentHandler(handler);
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
