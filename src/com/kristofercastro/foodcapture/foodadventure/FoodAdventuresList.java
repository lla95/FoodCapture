package com.kristofercastro.foodcapture.foodadventure;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.activity.Utility;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.Menu;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FoodAdventuresList extends FragmentActivity {

	GooglePlacesWebService placesService;
	GoogleMap googleMaps;
	private LocationManager mLocManager;
	private String bestProvider;
	private Location mLocation;
	ArrayList<Place> localRestaurants;
	HashMap<Integer, Marker> markers;
	
	Bundle currentSavedInstanceState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.currentSavedInstanceState = savedInstanceState;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_adventures_list);
		markers = new HashMap<Integer,Marker>();
		placesService = new GooglePlacesWebService(this);
				
		setupLocManager();
		setupGoogleMaps();
		
		if (savedInstanceState == null){
			grabPlaces();
		}
		else{
			localRestaurants = savedInstanceState.getParcelableArrayList("localRestaurants"); 
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("localRestaurants", localRestaurants);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void grabPlaces() {
		Void params = null;
		new RetrieveLocalRestaurants().execute(params);
	}

	private class RetrieveLocalRestaurants extends AsyncTask<Void, Void, Void>{

		private ProgressDialog dialog = new ProgressDialog(FoodAdventuresList.this);
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.dialog.setMessage("Generating places...");
			this.dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			localRestaurants = placesService.findPlaces(5, mLocation.getLatitude(), mLocation.getLongitude());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (dialog.isShowing())
				dialog.dismiss();
			drawAllMarkers();
			
			if (currentSavedInstanceState == null){
				android.app.FragmentManager fragmentManager = getFragmentManager();
				android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				
				Fragment placesFragment = new FoodAdventuresPlacesFragment();
				fragmentTransaction.add(R.id.restaurant_lists, placesFragment);
				fragmentTransaction.commit();
			}
		}		
	}
	
	/**
	 * Draws all the markers for all returned places
	 */
	private void drawAllMarkers(){
		for (int i = 0; i < localRestaurants.size(); i++){
			Place place = localRestaurants.get(i);
			drawMarker(place, i, place.getLatitude(), place.getLongitude());
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
			Place tempPlace = new Place();
			tempPlace.setName("Current Location");
			drawMarker(tempPlace,mLocation.getLatitude(),mLocation.getLongitude());
			focusCameraAtCurrentLocation();
		}
	}
	
	
	private void drawMarker(Place place, double latitude, double longitude) {
		drawMarker(place, null, latitude, longitude);
	}

	/**
	 * Draws a marker at a location
	 * @param mLocation2
	 */
	private void drawMarker(Place place, Integer index, double latitude, double longitude) {
		LatLng currentPosition = new LatLng(latitude, longitude);
		
		String titleText = place.getName() + "\n";
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition)
		.icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_AZURE)))
		.title(titleText);
		Marker marker = googleMaps.addMarker(markerOptions);
		markers.put(index,marker);
	}
	
	private void focusCameraAtCurrentLocation(){
		googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),12));
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
