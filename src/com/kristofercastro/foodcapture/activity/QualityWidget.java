package com.kristofercastro.foodcapture.activity;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.activity.Utility.CustomFonts;
import com.kristofercastro.foodcapture.model.Moment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class manages code that deals with the quality rating widget.
 * @author Kristofer Castro
 *
 */
public class QualityWidget {
	
	// Quality Rating Icons
	private ImageView firstStar;
	private ImageView secondStar;
	private ImageView thirdStar;
	private ImageView fourthStar;
	private ImageView fifthStar;
	private ArrayList<ImageView> qualityRatingIcons;
	private TextView textView;
	private Activity activity;
	private int qualityRating = 0;
	private TextView ratingMeaningTextView;

	public QualityWidget(Activity act) {
		this.activity = act;
		textView = (TextView) activity.findViewById(R.id.qualityTextView);
		initializeIcons();
		changeFont();
	}
	
	/**
	 * Bind the rating variables to the actual ID and store 
	 * them into their corresponding array list.
	 */
	private void initializeIcons() {
		firstStar = (ImageView) activity.findViewById(R.id.firstStar);
		secondStar = (ImageView) activity.findViewById(R.id.secondStar);
		thirdStar = (ImageView) activity.findViewById(R.id.thirdStar);
		fourthStar = (ImageView) activity.findViewById(R.id.fourthStar);
		fifthStar = (ImageView) activity.findViewById(R.id.fifthStar);

		qualityRatingIcons = new ArrayList<ImageView>();
		qualityRatingIcons.add(firstStar);
		qualityRatingIcons.add(secondStar);
		qualityRatingIcons.add(thirdStar);
		qualityRatingIcons.add(fourthStar);
		qualityRatingIcons.add(fifthStar);

		toggleQualitySelected();
	}

	/**
	 * Toggle between selected icon image versus non-selected icon image
	 */
	private void toggleQualitySelected(){
		for (final ImageView icon : qualityRatingIcons){
			icon.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {				
					// reset
					qualityRating = 0;
					for (ImageView icon : qualityRatingIcons){
						icon.setImageResource(R.drawable.quality_icon_default);
					}		
					// look for the icon clicked by iterating through each icon and marking it
					for (ImageView currentIcon : qualityRatingIcons){
						if ( !icon.equals(currentIcon) ){
							currentIcon.setImageResource(R.drawable.quality_icon_selected);
							qualityRating++;
						}else{
							currentIcon.setImageResource(R.drawable.quality_icon_selected);
							qualityRating++;
							break;
						}
					}
					updateRatingMeaning();
				}		
			});
		}
	}
	
	public int getQualityRating() {
		return qualityRating;
	}

	public void setQualityRating(int qualityRating) {
		this.qualityRating = qualityRating;
	}
	
	public void updateDisplayRatings() {
		LinearLayout qRatingsLayout = (LinearLayout) activity.findViewById(R.id.qualityRatingLayout);
		for (int i = 0; i < getQualityRating(); i++){
			ImageView ratingIcon = (ImageView) qRatingsLayout.getChildAt(i);
			ratingIcon.setImageResource(R.drawable.quality_icon_selected);
		}
	}
	
	private void changeFont(){
		Utility.changeTypeFace(textView, activity, CustomFonts.LANE_NARROW);
	}
	
	public void updateRatingMeaning(){
		ratingMeaningTextView = (TextView) activity.findViewById(R.id.quality_meaning);	
		ratingMeaningTextView.setText(QualityWidget.getMeaning(qualityRating, activity));
	}
	
	public static String getMeaning(int rating, Activity activity){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);

		if(rating == 1){
			return settings.getString("one quality rating", null);
		}
		if(rating == 2){
			return settings.getString("two quality rating", null);

		}
		if(rating == 3){
			return settings.getString("three quality rating", null);

		}
		if(rating == 4){
			return settings.getString("four quality rating", null);

		}
		if(rating == 5){
			return settings.getString("five quality rating", null);
		}
		return null;
	}
	
}
