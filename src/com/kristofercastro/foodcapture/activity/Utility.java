package com.kristofercastro.foodcapture.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Class containing helper functions
 * @author Kristofer Ken Castro
 * @date 10/23/2013
 *
 */
public class Utility {

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
}
