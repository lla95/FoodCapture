package com.kristofercastro.foodcapture.activity;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Class containing helper functions
 * @author Kristofer Ken Castro
 * @date 10/23/2013
 *
 */
public class Utility {

	public static final int THUMBSIZE_WIDTH = 500;
	public static final int THUMBSIZE_HEIGHT = 300;
	public static void main(String[] args){
		System.out.println("test");
	}
	/*
	 * Structure that holds references to where the custom fonts are
	 * stored.  Note: must put the fonts inside /assets folder
	 */
	public static class CustomFonts{
		public static final String LANE_NARROW = "fonts/lane_narrow.ttf";
		public static final String TITILLIUM_BOLD = "fonts/titillium_bold.otf";
	}

	public static void changeTypeFace(TextView v, Context context, String font){
		Typeface tf = Typeface.createFromAsset(context.getAssets(), font);
		v.setTypeface(tf);
	}
	
	public static void changeFontLaneNarrow(TextView view, Context context){
		Utility.changeTypeFace(view, context, CustomFonts.LANE_NARROW);
	}
	
	public static void changeFontTitillium(TextView view, Context context){
		Utility.changeTypeFace(view, context, CustomFonts.TITILLIUM_BOLD);
	}
	
	public static String getCurrentDate(){		
		DateTime dateTime = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("M-d-YYYY");
		return formatter.print(dateTime);
	}
	
	/**
	 * This method is taken from developer.android.com/training/displaying-bitmaps.html
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    return inSampleSize;
	}
	
	/**
	 * This method is taken from developer.android.com/training/displaying-bitmaps.html
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String path,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    Bitmap newBitmap = BitmapFactory.decodeFile(path, options);
	   
	    // @TO-DO: may have to do if statement when in landscape mode
	    return Bitmap.createBitmap(newBitmap, 0,newBitmap.getHeight()/2 - newBitmap.getWidth()/2,reqWidth, reqHeight);

	}
	
}
