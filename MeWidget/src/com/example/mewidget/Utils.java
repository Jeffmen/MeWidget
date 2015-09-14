package com.example.mewidget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.mewidget.view.WeatherCardItem;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

public class Utils {
    private static final int DEFAULT_BLUR_RADIUS = 10;
	public static final int[] backGroundPics = {
		R.drawable.clear_bg, 
		R.drawable.cloudy_bg, 
		R.drawable.rain_bg, 
		R.drawable.rain_snow_bg, 
		R.drawable.thunder_bg, 
		R.drawable.snow_bg};
	public static final int[] weatherIcons = {
		R.drawable.drizzle, 
		R.drawable.thunderstorms, 
		R.drawable.cloudy, 
		R.drawable.cloudy2, 
		R.drawable.snow, 
		R.drawable.sunny, 
		R.drawable.slight_drizzle, 
		R.drawable.haze, 
		R.drawable.thunder_bg, 
		R.drawable.mostly_cloudy, 
		R.drawable.drizzle2, 
		R.drawable.moon, 
		R.drawable.thunderstorms2};
	
	public static int dpToPx(Context context,int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
	
    public static byte[] convertIsToByteArray(InputStream inputStream) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte buffer[]=new byte[1024];
        int length=0;
        try {
            while ((length=inputStream.read(buffer))!=-1) {
                baos.write(buffer, 0, length);             
            }
            inputStream.close();
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }    
    
    public static Bitmap getViewBitmap(View v) {
        if(v.getWidth() == 0 || v.getHeight() == 0)
            return null;
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(b);
//        v.draw(c);
        Canvas canvas = new Canvas(b); 
        v.setDrawingCacheEnabled(true);
        canvas.drawBitmap(v.getDrawingCache(), 0, 0, null);
        return b;
    }

    public static void reset(View target) {
        ViewHelper.setAlpha(target, 1);
        ViewHelper.setScaleX(target, 1);
        ViewHelper.setScaleY(target, 1);
        ViewHelper.setTranslationX(target, 0);
        ViewHelper.setTranslationY(target, 0);
        ViewHelper.setRotation(target, 0);
        ViewHelper.setRotationY(target, 0);
        ViewHelper.setRotationX(target, 0);
        ViewHelper.setPivotX(target, target.getMeasuredWidth() / 2.0f);
        ViewHelper.setPivotY(target, target.getMeasuredHeight() / 2.0f);
    }
    
    public static Bitmap blur(Context context, Bitmap sentBitmap) {
        return blur(context, sentBitmap, DEFAULT_BLUR_RADIUS);
    }

    public static Bitmap blur(Context context, Bitmap sentBitmap, int radius) {
        final Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        final RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        sentBitmap.recycle();
        rs.destroy();
        input.destroy();
        output.destroy();
        script.destroy();

        return bitmap;
    }
    
    public static int getWidgetWeatherIconDrawableID(String str){
    	if(str==null || str.isEmpty()){
			return 0;
    	}
		if(str.contains("Clear")||str.contains("Sunny")){
			return R.drawable.widget_sunny_88;
		}
		else if(str.contains("Partly Cloudy")){
			return R.drawable.widget_cloud_88;
		}
		else if(str.contains("Cloudy") || str.contains("Mostly Cloudy")){
			return R.drawable.widget_cloudy_88;
		}
		else if(str.contains("Rain")){
			return R.drawable.widget_drizzle_88;
		}
		else if(str.contains("Thunder")){
			return R.drawable.widget_thunderstorms_88;
		}
		else if(str.contains("Snow")){
			return R.drawable.widget_snow88;
		}
		else if(str.contains("Haze")){
			return R.drawable.widget_haze_88;
		}
		else if(str.contains("Thundershowers")){
			return R.drawable.widget_slight_drizzle_88;
		}
		return R.drawable.widget_sunny_88;
    }
    
    public static void setImageAnimation(WeatherCardItem item, String str){
    	if(str==null || str.isEmpty()){
			item.setWeatherIcon1(R.drawable.sunny_200);
			return;
    	}
		if(str.contains("Clear")||str.contains("Sunny")){
			item.setWeatherIcon1(R.drawable.sunny_200);
		}
		else if(str.contains("Partly Cloudy")){
			item.setWeatherIcon1(R.drawable.sunny_200);
			item.setWeatherIcon2(R.drawable.cloud_200);
		}
		else if(str.contains("Cloudy") || str.contains("Mostly Cloudy")){
			item.setWeatherIcon2ani(R.anim.cloudy_animation);
		}
		else if(str.contains("Rain")){
			item.setWeatherIcon2ani(R.anim.drizzle_animation);
		}
		else if(str.contains("Thunder")){
			item.setWeatherIcon2ani(R.anim.thunderstorms_animation);
		}
		else if(str.contains("Snow")){
			item.setWeatherIcon2ani(R.anim.snow_animation);
		}
		else if(str.contains("Haze")){
			item.setWeatherIcon1(R.drawable.sunny_200);
			item.setWeatherIcon2(R.drawable.haze_200);
		}
		else if(str.contains("Thundershowers")){
			item.setWeatherIcon1(R.drawable.sunny_200);
			item.setWeatherIcon2(R.drawable.slight_drizzle_200);
		}
    }
    
	public static int getWeatherBackgroundDrawableID(String str){
		if(str!=null && !str.isEmpty() && str.contains("Clear")){
			return R.drawable.clear_bg;
		}
		if(str!=null && !str.isEmpty() && str.contains("Cloudy")){
			return R.drawable.cloudy_bg;
		}
		if(str!=null && !str.isEmpty() && str.contains("Rain")){
			return R.drawable.rain_bg;
		}
		if(str!=null && !str.isEmpty() && str.contains("Thunder")){
			return R.drawable.thunder_bg;
		}
		if(str!=null && !str.isEmpty() && str.contains("Snow")){
			return R.drawable.snow_bg;
		}
		if(str!=null && !str.isEmpty() && str.contains("Rain_snow")){
			return R.drawable.rain_snow_bg;
		}
		return R.drawable.clear_bg;
	}
	
	public static boolean isShowSun(int id){
		if(id == R.drawable.widget_sunny_88 || id == R.drawable.widget_cloud_88 
		|| id == R.drawable.widget_haze_88 || id == R.drawable.widget_slight_drizzle_88){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static int getWeatherIconDrawableID(String str){
		if(str!=null && !str.isEmpty() && (str.contains("Clear")||str.contains("Sunny"))){
			return R.drawable.sunny;
		}
		if(str!=null && !str.isEmpty() && (str.contains("Cloudy") || str.contains("Mostly Cloudy"))){
			return R.drawable.cloudy;
		}
		if(str!=null && !str.isEmpty() && str.contains("Partly Cloudy")){
			return R.drawable.mostly_cloudy;
		}
		if(str!=null && !str.isEmpty() && str.contains("Rain")){
			return R.drawable.drizzle;
		}
		if(str!=null && !str.isEmpty() && str.contains("Thunder")){
			return R.drawable.thunderstorms;
		}
		if(str!=null && !str.isEmpty() && str.contains("Snow")){
			return R.drawable.snow;
		}
		if(str!=null && !str.isEmpty() && str.contains("Haze")){
			return R.drawable.haze;
		}
		if(str!=null && !str.isEmpty() && str.contains("Thundershowers")){
			return R.drawable.slight_drizzle;
		}
		return R.drawable.sunny;
	}
}
