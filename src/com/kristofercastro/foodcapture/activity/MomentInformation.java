package com.kristofercastro.foodcapture.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.R.layout;
import com.kristofercastro.foodcapture.R.menu;
import com.kristofercastro.foodcapture.model.Moment;
import com.kristofercastro.foodcapture.model.Restaurant;
import com.kristofercastro.foodcapture.model.dbo.DBHelper;
import com.kristofercastro.foodcapture.model.dbo.MomentDAO;

import android.location.Location;
import android.os.AsyncTask;
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
	
	GoogleMap googleMaps;
	Moment moment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moment_information);	
		
		Bundle extras = this.getIntent().getExtras();
		setupGoogleMaps();
		
		
		new GetMomentTask().execute(extras.getLong("momentID"));
	}

	private void setupGoogleMaps() {
		if (googleMaps == null){
			googleMaps = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
		}
		
		if (googleMaps != null){
			googleMaps.clear();
			googleMaps.setMyLocationEnabled(true);
		}
		
	}
	
	private class GetMomentTask extends AsyncTask<Long, Void, Void>{

		@Override
		protected void onPostExecute(Void result) {
			drawMarker();
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Long... params) {
			Long momentID = params[0];
			MomentDAO momentDAO = new MomentDAO(new DBHelper(MomentInformation.this));
			moment = momentDAO.retrieve(momentID);
			return null;
		}	
	}
		
	/**
	 * Draws the current location.
	 * @param mLocation2
	 */
	private void drawMarker() {
		Restaurant restaurant = moment.getRestaurant();
		LatLng currentPosition = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition)
		.icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_AZURE)))
		.title("Current Location");
		googleMaps.addMarker(markerOptions);
		googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.moment_information, menu);
		return true;
	}

}
