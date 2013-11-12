package com.kristofercastro.foodcapture.foodadventure;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.R.layout;
import com.kristofercastro.foodcapture.R.menu;
import com.kristofercastro.foodcapture.model.Restaurant;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class FoodAdventuresList extends Activity {

	GooglePlacesWebService placesService;
	GoogleMap googleMaps;
	private LocationManager mLocManager;
	private String bestProvider;
	private Location mLocation;
	ArrayList<Place> localRestaurants;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_adventures_list);
		
		placesService = new GooglePlacesWebService(this);
		setupLocManager();
		setupGoogleMaps();
		grabPlaces();
	}

	private void grabPlaces() {
		Void params = null;
		new RetrieveLocalRestaurants().execute(params);
	}

	private class RetrieveLocalRestaurants extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			localRestaurants = placesService.findPlaces(10, mLocation.getLatitude(), mLocation.getLongitude());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.i("MyCameraApp", "Entered onPost");
			for ( Place place : localRestaurants){
				Log.i("MyCameraApp", place.toString());
			}
			Log.i("MyCameraApp", "Finished on Post");
			super.onPostExecute(result);
			
		}	
		
		
	}
	
	private void setupGoogleMaps() {
		if (googleMaps == null){
			googleMaps = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
		}
		
		if (googleMaps != null){
			googleMaps.clear();
			googleMaps.setMyLocationEnabled(true);
			drawMarker(mLocation.getLatitude(),mLocation.getLongitude());
		}
	}
	
	/**
	 * Draws a marker at a location
	 * @param mLocation2
	 */
	private void drawMarker(double latitude, double longitude) {
		LatLng currentPosition = new LatLng(latitude, longitude);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition)
		.icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_AZURE)))
		.title("Location");
		googleMaps.addMarker(markerOptions);
		googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));

	}
	
	private void setupLocManager() {
		mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		bestProvider = mLocManager.getBestProvider(criteria, true);	
		mLocation = mLocManager.getLastKnownLocation(bestProvider);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.food_adventures_list, menu);
		return true;
	}

}
