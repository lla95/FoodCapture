package com.kristofercastro.foodcapture.activity;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.activity.Utility.CustomFonts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
	
	private void changeFont(){
		Utility.changeTypeFace(textView, activity, CustomFonts.LANE_NARROW);
	}
	
}
