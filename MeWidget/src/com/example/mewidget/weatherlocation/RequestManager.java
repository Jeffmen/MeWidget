package com.example.mewidget.weatherlocation;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class RequestManager {	
	private Context mContext;
	private RequestQueue mQueue;
	
	public interface Request {
		public JsonObjectRequest getRequest();
	}
	
	public RequestManager(Context context) {
		mContext = context;
		mQueue = Volley.newRequestQueue(mContext);
	}
	
	public void addRequest(JsonObjectRequest request){
		mQueue.add(request);
	}
}
