package com.example.mewidget.provider;

import android.net.Uri;

public class City {
	public static final String TABLE_NAME = "city";
    public static final Uri CONTENT_URI  = Uri.parse("content://"+DBProvider.AUTHORITY+"/"+TABLE_NAME);

	public interface Columns {
		public static final String _ID = "_id";
		public static final String CITY_ID = "city_id";
		public static final String CITY_NAME = "city_name";
		public static final String COUNTRY_NAME = "country_name";
		//public static final String IS_VALID = "is_valid";
	}
}
