package com.kristofercastro.foodcapture.activity;

import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.R.layout;
import com.kristofercastro.foodcapture.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MomentInformation extends Activity {
	
	// static labels
	TextView foodTextView;
	TextView qualityTextView;
	TextView priceTextView;
	TextView descriptionTextView;
	TextView restaurantTextView;
	TextView dateTimeTextView;
	TextView locationTextView;
	TextView pictureTextView;
	
	ImageView pictureImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moment_information);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.moment_information, menu);
		return true;
	}

}
