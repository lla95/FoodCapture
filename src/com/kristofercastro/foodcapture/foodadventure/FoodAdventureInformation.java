package com.kristofercastro.foodcapture.foodadventure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import com.kristofercastro.foodcapture.activity.EditMoment;
import com.kristofercastro.foodcapture.activity.MainActivity;
import com.kristofercastro.foodcapture.activity.Message;
import com.kristofercastro.foodcapture.activity.MomentInformation;
import com.kristofercastro.foodcapture.activity.Utility;
import com.kristofercastro.foodcapture.model.FoodAdventure;
import com.kristofercastro.foodcapture.model.Moment;
import com.kristofercastro.foodcapture.model.Restaurant;
import com.kristofercastro.foodcapture.model.dbo.DBHelper;
import com.kristofercastro.foodcapture.model.dbo.FoodAdventureDAO;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FoodAdventureInformation extends FragmentActivity implements FoodAdventureActivityInterface, PropertyChangeListener {

	GooglePlacesWebService placesService;
	GoogleMap googleMaps;
	private LocationManager mLocManager;
	private String bestProvider;
	private Location mLocation;
	ArrayList<Place> localRestaurants;
	FoodAdventure currentFoodAdventure;
	
	HashMap<Integer, Marker> markers;
	
	Bundle currentSavedInstanceState;
	
	// Moment specific variables
	ImageView pictureImageView;
	Moment currentMoment;
	private TextView descriptionTextView;
	private TextView foodTextView;
	private TextView restaurantTextView;
	
	private FoodAdventuresPlacesFragment placesFragment = null;
	private ArrayList<Moment> moments = new ArrayList<Moment>();
	private Moment selectedMoment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.currentSavedInstanceState = savedInstanceState;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_adventures_list);
		changeFont();
		markers = new HashMap<Integer,Marker>();
		placesService = new GooglePlacesWebService(this);
				
		setupLocManager();
		setupGoogleMaps();
				
		if (savedInstanceState != null){
			localRestaurants = savedInstanceState.getParcelableArrayList("localRestaurants"); 
		}else{
			grabPlaces();
		}
		
		setupCurrentMenuItem();
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

		private ProgressDialog dialog = new ProgressDialog(FoodAdventureInformation.this);		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.dialog.setMessage("Generating places...");
			this.dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			FoodAdventureDAO foodAdventureDAO = new FoodAdventureDAO(new DBHelper(FoodAdventureInformation.this));
			
			Long foodAdventureID = FoodAdventureInformation.this.getIntent().getExtras().getLong("foodAdventureID");
			currentFoodAdventure = foodAdventureDAO.retrieve(foodAdventureID);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ArrayList<Place> restaurantLists = new ArrayList<Place>();
			ArrayList<Moment> moments = currentFoodAdventure.getMoments();
			for(int i = 0; i < moments.size(); i++){
				Moment moment = moments.get(i);
				Place place = new Place();
				place.setName(moment.getRestaurant().getName());
				place.setLatitude((double) moment.getRestaurant().getLatitude());
				place.setLongitude((double) moment.getRestaurant().getLongitude());
				restaurantLists.add(place);
			}
			localRestaurants = restaurantLists;
			if (dialog.isShowing())
				dialog.dismiss();
			drawAllMarkers();
			
			if (currentSavedInstanceState == null){
				android.app.FragmentManager fragmentManager = getFragmentManager();
				android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				
			    placesFragment = new FoodAdventuresPlacesFragment();
			    placesFragment.addChangeListener(FoodAdventureInformation.this);
				fragmentTransaction.add(R.id.restaurant_lists, placesFragment);
				fragmentTransaction.commit();
			}
		}		
	}
	
	public void changeFont(){	
		TextView restaurantsScrollHeader = (TextView) this.findViewById(R.id.restaurantsScrollHeader);
		TextView selectedRestaurantHeader = (TextView) this.findViewById(R.id.selecetedRestaurantHeader);
		TextView mapHeader = (TextView) this.findViewById(R.id.mapHeader);
		
		Utility.changeFontLaneNarrow(restaurantsScrollHeader, this);
		Utility.changeFontLaneNarrow(selectedRestaurantHeader, this);
		Utility.changeFontLaneNarrow(mapHeader, this);
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

	@Override
	public HashMap<Integer, Marker> getMarkers() {
		// TODO Auto-generated method stub
		return this.markers;
	}

	@Override
	public ArrayList<Place> getPlaces() {
		// TODO Auto-generated method stub
		return this.localRestaurants;
	}

	@Override
	public GoogleMap getGoogleMaps() {
		// TODO Auto-generated method stub
		return this.googleMaps;
	}
	
	private void setupCurrentMenuItem(){
		changeFontOfReview();
		LinearLayout currentReview = (LinearLayout) this.findViewById(R.id.current_menu_item);
		
		currentReview.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(FoodAdventureInformation.this, "Clicked on it!", Toast.LENGTH_SHORT).show();
				/* if moment is null go to create moment with preset inputs such as
				 * food adventure id and restaurant name
				 * 
				 * if moment is not null then it already exists so we go to edit screen
				 * with locked restaurant name and make sure to persist food adventure id
				 */
			}			
		});
	}
	
	protected Moment getCurrentMoment(){
		if (currentMoment == null){
			currentMoment = new Moment();
		}
		return currentMoment;
	}
	
	private void displayCurrentMomentReview(){
		android.app.FragmentManager fragManager = getFragmentManager();
		FoodAdventuresPlacesFragment placesFragment = (FoodAdventuresPlacesFragment) fragManager.findFragmentById(R.id.restaurant_lists);
		Log.i("MyCameraApp", "Current place: " + placesFragment.getSelectedPlace());
	}
	
	private void changeFontOfReview(){
		LinearLayout currentReview = (LinearLayout) this.findViewById(R.id.current_menu_item);
		// static labels
		foodTextView = (TextView) currentReview.findViewById(R.id.foodTextView);
		//TextView reviewTextView = (TextView) currentReview.findViewById(R.id.reviewTextView);
		TextView qualityTextView = (TextView) currentReview.findViewById(R.id.qualityTextView);
		TextView priceTextView = (TextView) currentReview.findViewById(R.id.priceTextView);
		descriptionTextView = (TextView) currentReview.findViewById(R.id.descriptionTextView);
		restaurantTextView = (TextView) currentReview.findViewById(R.id.restaurantTextView);
		//TextView dateTimeTextView = (TextView) currentReview.findViewById(R.id.dateTimeTextView);
		//TextView locationTextView = (TextView) currentReview.findViewById(R.id.locationTextView);
		//TextView pictureTextView = (TextView) currentReview.findViewById(R.id.pictureTextView);
		Utility.changeFontLaneNarrow(descriptionTextView, this);
		Utility.changeFontTitillium(foodTextView, this);
		Utility.changeFontTitillium(qualityTextView, this);
		Utility.changeFontTitillium(priceTextView, this);
		Utility.changeFontTitillium(restaurantTextView, this);
		
		//Utility.changeFontLaneNarrow(reviewTextView, this);
		//Utility.changeFontLaneNarrow(locationTextView, this);		
		pictureImageView = (ImageView) currentReview.findViewById(R.id.pictureThumbnail);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		/*Toast.makeText(this, "Got the event!" , Toast.LENGTH_SHORT).show();
		displayCurrentMomentReview();*/
		updateRestarauntText();
	}

	/*
	 * Checks to see if the current moment exists in the database
	 */
	private boolean currentMomentExists(){
		for (int i = 0; i < moments.size(); i++){
			String restaurantNameOfMoment = moments.get(i).getRestaurant().getName();
			String selectedRestaurantName = placesFragment.getSelectedPlace().getName();
			
			if (restaurantNameOfMoment.equalsIgnoreCase(selectedRestaurantName)){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Sets up the intent with the appropriate extra information
	 * so that the Edit Moment screen is properly set up.
	 */
	private void bringFoodReviewIntent(){
		Intent i = new Intent(this, EditMoment.class);
		if ( selectedMoment != null){
			Restaurant restaurantInfo = new Restaurant();
			Place selectedPlace = placesFragment.getSelectedPlace();
			restaurantInfo.setName(selectedPlace.getName());
			restaurantInfo.setLongitude(selectedPlace.getLongitude());
			restaurantInfo.setLatitude(selectedPlace.getLatitude());
			i.putExtra("food adventure", currentFoodAdventure);
			i.putExtra("restaurant", restaurantInfo);
			i.putExtra("mode", Message.CREATE_NEW_MOMENT_WITH_ADVENTURE);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
			this.startActivity(i);	
		}else{
			i.putExtra("moment", this.selectedMoment);
			i.putExtra("mode", Message.EDIT_EXISTING_MOMENT_WITH_ADVENTURE);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
			this.startActivity(i);	
		}
	}
	
	private void updateRestarauntText(){
		LinearLayout menuItemRow = (LinearLayout) this.findViewById(R.id.current_menu_item);
		TextView restaurantTextView = (TextView) menuItemRow.findViewById(R.id.restaurantTextView);
		restaurantTextView.setText(placesFragment.getSelectedPlace().getName());
	}
	
}
