package com.example.mewidget.weatherlocation;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CityXMLHandler extends DefaultHandler {
	// private String currentValue = null;
	private StringBuffer currentValueBuffer = new StringBuffer();
	private String info;
	private boolean currentElement = false;
	private List<CityInfo> mCityInfoList = new ArrayList<CityInfo>();
	private int start,end;

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	//<s k="Guangzhou (广州市)" d="pt:iso=CN&woeid=2161838&lon=113.268&lat=23.1074&s=Guangdong&c=China&country_woeid=23424781&pn=广州市&n=Guangzhou (广州市), Guangdong, China"/>
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentValueBuffer.delete(0, currentValueBuffer.length());
		if (localName.equals("s")) {
			currentElement = true;
			CityInfo mCityInfo = new CityInfo();
			//woeid
			info = attributes.getValue("d");
			start = info.indexOf("&woeid=");
			if(start > 0 && start < info.length()){
				start += 7;
				end = info.indexOf("&", start);
				if(end > start){
					mCityInfo.setCityId(info.substring(start,end));
				}
			}
			//lat
			info = attributes.getValue("d");
			start = info.indexOf("&lat=");
			if(start > 0 && start < info.length()){
				start += 5;
				end = info.indexOf("&", start);
				if(end > start){
					mCityInfo.setLatitude(info.substring(start,end));
				}
			}
			//lon
			info = attributes.getValue("d");
			start = info.indexOf("&lon=");
			if(start > 0 && start < info.length()){
				start += 5;
				end = info.indexOf("&", start);
				if(end > start){
					mCityInfo.setLongitude(info.substring(start,end));
				}
			}
			//country
			info = attributes.getValue("d");
			start = info.indexOf("&c=");
			if(start > 0 && start < info.length()){
				start += 3;
				end = info.indexOf("&", start);
				if(end > start){
					mCityInfo.setCountry(info.substring(start,end));
				}
			}
			//cityshowname
			info = attributes.getValue("d");
			start = info.indexOf("&n=");
			if(start > 0 && start < info.length()){
				start += 3;
				end = info.length();
				if(end > start){
					mCityInfo.setCityShowName(info.substring(start,end));
				}
			}
			//cityname
			info = attributes.getValue("k");
			if(!info.isEmpty()){
					mCityInfo.setCityName(info);
					mCityInfoList.add(mCityInfo);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if (currentElement) {
			String currentValue = currentValueBuffer.toString();
		}
		currentElement = false;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (currentElement) {
			currentValueBuffer.append(ch, start, length);
		}
	}
	
	public List<CityInfo> getCityInfo(){
		return mCityInfoList;
	}
}
