package com.kristofercastro.foodcapture.activity;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.activity.Utility.CustomFonts;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PriceWidget {

	private int priceRating;

	// Price Rating Icons
	private ImageView firstDollar;
	private ImageView secondDollar;
	private ImageView thirdDollar;
	private ImageView fourthDollar;
	private ImageView fifthDollar;
	private ArrayList<ImageView> priceRatingIcons;
	private TextView textView;
	private TextView ratingMeaningTextView;
	
	private Activity activity;
	
	public PriceWidget(Activity act){
		this.activity = act;
		textView = (TextView) activity.findViewById(R.id.priceTextView);
		initializeIcons();
		
	}
	
	public void updateRatingMeaning() {
		ratingMeaningTextView = (TextView) activity.findViewById(R.id.price_meaning);
		
		ratingMeaningTextView.setText(getMeaning(priceRating, activity));
	}
	
	public static String getMeaning(int rating, Activity activity){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);

		if(rating == 1){
			return settings.getString("one price rating", null);
		}
		if(rating == 2){
			return settings.getString("two price rating", null);

		}
		if(rating == 3){
			return settings.getString("three price rating", null);

		}
		if(rating == 4){
			return settings.getString("four price rating", null);

		}
		if(rating == 5){
			return settings.getString("five price rating", null);
		}
		return null;
	}
	/**
	 * Bind the rating variables to the actual ID and store 
	 * them into their corresponding array list.
	 */
	private void initializeIcons() {

		// Price Rating Icons
		firstDollar = (ImageView) activity.findViewById(R.id.firstDollar);
		secondDollar= (ImageView) activity.findViewById(R.id.secondDollar);
		thirdDollar = (ImageView) activity.findViewById(R.id.thirdDollar);
		fourthDollar = (ImageView) activity.findViewById(R.id.fourthDollar);
		fifthDollar = (ImageView) activity.findViewById(R.id.fifthDollar);
		
		priceRatingIcons = new ArrayList<ImageView>();
		priceRatingIcons.add(firstDollar);
		priceRatingIcons.add(secondDollar);
		priceRatingIcons.add(thirdDollar);
		priceRatingIcons.add(fourthDollar);
		priceRatingIcons.add(fifthDollar);
		
		togglePriceSelected();
	}
	
	/**
	 * Toggle between selected icon image versus non-selected icon image
	 */
	private void togglePriceSelected(){
		for (final ImageView icon : priceRatingIcons){
			icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// reset
					priceRating = 0;
					for (ImageView icon : priceRatingIcons){
						icon.setImageResource(R.drawable.price_icon_default);
					}		
					// look for the icon clicked by iterating through each icon and marking it
					for (ImageView currentIcon : priceRatingIcons){
						if ( !icon.equals(currentIcon) ){
							currentIcon.setImageResource(R.drawable.price_icon_selected);
							priceRating++;
						}else{
							currentIcon.setImageResource(R.drawable.price_icon_selected);
							priceRating++;
							break;
						}
					}
					updateRatingMeaning();
				}
			});
		}				
	}
	
	public void updateDisplayRatings() {
		LinearLayout qRatingsLayout = (LinearLayout) activity.findViewById(R.id.priceRatingLayout);
		for (int i = 0; i < getPriceRating(); i++){
			ImageView ratingIcon = (ImageView) qRatingsLayout.getChildAt(i);
			ratingIcon.setImageResource(R.drawable.price_icon_selected);
		}
	}
	
	private void changeFont(){
		Utility.changeTypeFace(textView, activity, CustomFonts.LANE_NARROW);
	}
	
	public int getPriceRating() {
		return priceRating;
	}
	public void setPriceRating(int priceRating) {
		this.priceRating = priceRating;
	}
}
