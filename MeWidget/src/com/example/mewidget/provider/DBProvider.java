package com.example.mewidget.provider;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DBProvider extends ContentProvider {
    private DBHelper dbHelper;
	private static final UriMatcher uriMatcher;
    public static final String AUTHORITY  = "com.example.mewidget.provider";
	private static final int CITY = 1;
	private static final int CITY_ID = 2;
	private static final int CITY_NAME_FILTER = 3;
	private static final int WEATHER = 4;
	private static final int WEATHER_CITY_ID= 5;
	private static final int WEATHER_CITY_NAME = 6;
	private static final int WEATHER_IS_LOCATION = 7;
	static {
	    // ��û��ƥ��ɹ���ʱ������NO_MATCH��ֵ
	    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//content://com.example.mewidget.provider/city
	    uriMatcher.addURI(AUTHORITY, City.TABLE_NAME, CITY);
		//content://com.example.mewidget.provider/city/21
	    uriMatcher.addURI(AUTHORITY, City.TABLE_NAME+"/#", CITY_ID);
		//content://com.example.mewidget.provider/city/filter/�Ϻ�
	    uriMatcher.addURI(AUTHORITY, City.TABLE_NAME+"/filter/*", CITY_NAME_FILTER);
	    uriMatcher.addURI(AUTHORITY, Weather.TABLE_NAME, WEATHER);
	    uriMatcher.addURI(AUTHORITY, Weather.TABLE_NAME+"/cityid/#", WEATHER_CITY_ID);
	    uriMatcher.addURI(AUTHORITY, Weather.TABLE_NAME+"/cityname/*", WEATHER_CITY_NAME);
	    uriMatcher.addURI(AUTHORITY, Weather.TABLE_NAME+"/islocation/#", WEATHER_IS_LOCATION);
	}

    //private static final String INVALID_CITY = "";
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new DBHelper(this.getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        String orderColumns = sortOrder;
		switch(uriMatcher.match(uri)){
		case CITY:
			sqb.setTables(City.TABLE_NAME);
			orderColumns = City.Columns.CITY_NAME + " desc";
			break;
		case CITY_ID:
			sqb.setTables(City.TABLE_NAME);
            sqb.appendWhere(City.Columns._ID + "=" + uri.getPathSegments().get(1));
            break;
		case CITY_NAME_FILTER:
			sqb.setTables(City.TABLE_NAME);
            List<String> pathSegments = uri.getPathSegments();
            String cityName = pathSegments.size() > 0 ? pathSegments.get(2) : null;
            if (!TextUtils.isEmpty(cityName)) {
                sqb.appendWhere(City.Columns.CITY_NAME + " like \"%" + cityName +"%\"");
            }
            orderColumns = City.Columns.CITY_NAME + " desc";
            break;
		case WEATHER:
			sqb.setTables(Weather.TABLE_NAME);
            orderColumns = Weather.Columns.IS_LOCATION + " desc, " + Weather.Columns.BEGIN_DATE + " asc";
            break;
		case WEATHER_CITY_ID:
			sqb.setTables(Weather.TABLE_NAME);
            sqb.appendWhere(Weather.Columns.CITY_ID + "=" + uri.getPathSegments().get(2));
            break;
		case WEATHER_CITY_NAME:
			sqb.setTables(Weather.TABLE_NAME);
            sqb.appendWhere(Weather.Columns.CITY_NAME + "=\"" + uri.getPathSegments().get(2)+"\"");
            break;
		case WEATHER_IS_LOCATION:
			sqb.setTables(Weather.TABLE_NAME);
            sqb.appendWhere(Weather.Columns.IS_LOCATION + "=" + uri.getPathSegments().get(2));
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
		}
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = sqb.query(db, projection, selection, selectionArgs,
                null, null, orderColumns);
        return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		switch(uriMatcher.match(uri)){
	    case WEATHER:
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        long _id = db.insert(Weather.TABLE_NAME, Weather.Columns.CITY_ID, values);
	        if (_id > 0) {
	            Uri uri1 = ContentUris.withAppendedId(Weather.CONTENT_URI, _id);
	            this.getContext().getContentResolver().notifyChange(Weather.CONTENT_URI, null);
	            return uri1;
	        }
	        break;
	    default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
		}
        return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
        int count = 0;
		switch(uriMatcher.match(uri)){
	    case WEATHER_CITY_NAME:
            List<String> pathSegments = uri.getPathSegments();
            if(pathSegments.size() > 0){
    			SQLiteDatabase db = dbHelper.getWritableDatabase();
                String cityName = pathSegments.get(2);
                count = db.delete(Weather.TABLE_NAME, Weather.Columns.CITY_NAME + "=\"" + cityName+"\" and " + Weather.Columns.IS_LOCATION + " = 0", selectionArgs);
            }
	        break;
	    default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
		}
        this.getContext().getContentResolver().notifyChange(Weather.CONTENT_URI, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
        int count = 0;
        List<String> pathSegments = uri.getPathSegments();
		switch(uriMatcher.match(uri)){
	    case WEATHER_CITY_NAME:
            if(pathSegments.size() > 0){
    			SQLiteDatabase db = dbHelper.getWritableDatabase();
                String noteName = pathSegments.get(2);
                count = db.update(Weather.TABLE_NAME, values, "UPPER("+Weather.Columns.CITY_NAME + ")= UPPER(\"" + noteName + "\")", selectionArgs);
            }
	        break;
	    case WEATHER_IS_LOCATION:
            if(pathSegments.size() > 0){
    			SQLiteDatabase db = dbHelper.getWritableDatabase();
                String isLocation = pathSegments.get(2);
                count = db.update(Weather.TABLE_NAME, values, Weather.Columns.IS_LOCATION + "=" + isLocation, selectionArgs);
            }
	        break;
	    default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
		}
        this.getContext().getContentResolver().notifyChange(Weather.CONTENT_URI, null);
		return count;
	}

}
