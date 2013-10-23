package com.kristofercastro.foodcapture.activity;

import com.example.foodcapture.R;
import com.example.foodcapture.R.layout;
import com.example.foodcapture.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MomentsList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moments);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.moments, menu);
		return true;
	}

}
