package com.example.mewidget.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "Weather.db";
	private static final int VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + City.TABLE_NAME +" (" +
				City.Columns._ID +" INTEGER PRIMARY KEY,"+
				City.Columns.CITY_NAME +" TEXT,"+
				City.Columns.CITY_ID +" TEXT,"+
				City.Columns.COUNTRY_NAME +" TEXT"+
				//City.Columns.IS_VALID +" INTEGER,"+
				")");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Weather.TABLE_NAME +" (" +
				Weather.Columns._ID +" INTEGER PRIMARY KEY,"+
				Weather.Columns.CITY_NAME +" TEXT NOT NULL,"+
				Weather.Columns.CITY_ID +" TEXT,"+
				Weather.Columns.COUNTRY_NAME +" TEXT,"+
				Weather.Columns.LATITUDE +" TEXT,"+
				Weather.Columns.LONGITUDE +" TEXT,"+
				Weather.Columns.IS_LOCATION +" INTEGER NOT NULL,"+

				Weather.Columns.BEGIN_DATE +" DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')) ,"+
				
				Weather.Columns.CURRENT_TEMP +" INTEGER,"+
				Weather.Columns.WIND_SPEED +" INTEGER,"+
				Weather.Columns.WIND_DIRECTION +" INTEGER,"+
				Weather.Columns.HUMIDITY +" INTEGER,"+
				Weather.Columns.VISIBILITY +" TEXT,"+
				Weather.Columns.SUNRISE +" TEXT,"+
				Weather.Columns.SUNSET +" TEXT,"+
				
				Weather.Columns.MAX_TEMP1 +" INTEGER,"+
				Weather.Columns.MIN_TEMP1 +" INTEGER,"+
				Weather.Columns.W_DATE1 +" TEXT,"+
				Weather.Columns.WEATHER_TEXT1 +" TEXT,"+
				
				Weather.Columns.MAX_TEMP2 +" INTEGER,"+
				Weather.Columns.MIN_TEMP2 +" INTEGER,"+
				Weather.Columns.W_DATE2 +" TEXT,"+
				Weather.Columns.WEATHER_TEXT2 +" TEXT,"+

				Weather.Columns.MAX_TEMP3 +" INTEGER,"+
				Weather.Columns.MIN_TEMP3 +" INTEGER,"+
				Weather.Columns.W_DATE3 +" TEXT,"+
				Weather.Columns.WEATHER_TEXT3 +" TEXT,"+

				Weather.Columns.MAX_TEMP4 +" INTEGER,"+
				Weather.Columns.MIN_TEMP4 +" INTEGER,"+
				Weather.Columns.W_DATE4 +" TEXT,"+
				Weather.Columns.WEATHER_TEXT4 +" TEXT,"+

				Weather.Columns.MAX_TEMP5 +" INTEGER,"+
				Weather.Columns.MIN_TEMP5 +" INTEGER,"+
				Weather.Columns.W_DATE5 +" TEXT,"+
				Weather.Columns.WEATHER_TEXT5 +" TEXT"+
				")");
		//默认插入当前定位的记录
        SQLiteStatement stmt = db.compileStatement("INSERT OR IGNORE INTO weather(city_name,is_location) VALUES('Chizhou', 1);");
        stmt.execute();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        db.execSQL("ALTER TABLE " + Weather.TABLE_NAME + " ADD COLUMN other STRING"); 
	}

}
