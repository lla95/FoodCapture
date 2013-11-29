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
import com.kristofercastro.foodcapture.activity.PriceWidget;
import com.kristofercastro.foodcapture.activity.QualityWidget;
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
import android.widget.HorizontalScrollView;
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
		setupCurrentMenuItem();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("localRestaurants", localRestaurants);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		if (currentSavedInstanceState != null){
			localRestaurants = currentSavedInstanceState.getParcelableArrayList("localRestaurants"); 
		}else{
			grabPlaces();
		}
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
			moments = currentFoodAdventure.getMoments();
			selectedMoment = moments.get(0);
			updateReview();
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
				
				// clear child views
				HorizontalScrollView restaurantListContainer = (HorizontalScrollView) FoodAdventureInformation.this.findViewById(R.id.restaurant_lists);
			    restaurantListContainer.removeAllViews();
			    
				placesFragment = new FoodAdventuresPlacesFragment();
			    placesFragment.addChangeListener(FoodAdventureInformation.this);
				fragmentTransaction.add(R.id.restaurant_lists, placesFragment);
				fragmentTransaction.commit();
			}
			
			// retrieve all saved moments
			new RetrieveAllSavedMoments().execute(currentFoodAdventure);
		}		

	}
	

	private class RetrieveAllSavedMoments extends AsyncTask<FoodAdventure, Void, Void>{

		@Override
		protected Void doInBackground(FoodAdventure... params) {
			FoodAdventure foodAdventure = params[0];
			FoodAdventureDAO foodAdventureDAO = new FoodAdventureDAO(new DBHelper(FoodAdventureInformation.this));
			moments = foodAdventureDAO.retrieveMoments(foodAdventure);
			return null;
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
				bringFoodReviewIntent();
			}			
		});
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
		updateSelectedMoment();	
		updateReview();		
	}

	/*
	 * Based on the currently selected restaurant name, figure out its corresponding moment
	 */
	private void updateSelectedMoment() {
		for (int i = 0; i < moments.size(); i++){
			String restaurantNameOfMoment = moments.get(i).getRestaurant().getName();
			String selectedRestaurantName = placesFragment.getSelectedPlace().getName();
			
			if (restaurantNameOfMoment.equalsIgnoreCase(selectedRestaurantName)){
				selectedMoment = moments.get(i);
			}
		}		
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
		i.putExtra("foodAdventureID", currentFoodAdventure.getId());
		i.putExtra("momentID",selectedMoment.getId());
		i.putExtra("mode", Message.EDIT_EXISTING_MOMENT);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		this.startActivity(i);	
	}
	
	private void updateReview(){
		LinearLayout menuItemRow = (LinearLayout) this.findViewById(R.id.current_menu_item);
		TextView restaurantTextView = (TextView) menuItemRow.findViewById(R.id.restaurantTextView);
    	TextView foodTextView = (TextView) menuItemRow.findViewById(R.id.foodTextView);
    	TextView descriptionTextView = (TextView) menuItemRow.findViewById(R.id.descriptionTextView);
    	TextView dateTextView = (TextView) menuItemRow.findViewById(R.id.dateTimeTextView);
    	ImageView foodThumbnail = (ImageView) menuItemRow.findViewById(R.id.pictureThumbnail);
    	
    	restaurantTextView.setText(selectedMoment.getRestaurant().getName());
   
    	// reset review
    	foodTextView.setText("Review Not Completed");
    	foodThumbnail.setImageBitmap(null);
    	descriptionTextView.setText(null);
    	clearRatings(selectedMoment, menuItemRow);
    	
    	if(selectedMoment.getMenuItem() != null){
	    	foodTextView.setText(selectedMoment.getMenuItem().getName());
	    	descriptionTextView.setText(selectedMoment.getDescription());
	    	String imagePath = selectedMoment.getMenuItem().getImagePath();
			if (imagePath != null && imagePath.length() > 0)
				foodThumbnail.setImageBitmap(Utility.decodeSampledBitmapFromFile(selectedMoment.getMenuItem().getImagePath(), Utility.THUMBSIZE_WIDTH, Utility.THUMBSIZE_HEIGHT));
			
	    	restaurantTextView.setText("@ " + selectedMoment.getRestaurant().getName());
    	}
    	//dateTextView.setText(selectedMoment.getDate());
    	displayRatings(selectedMoment, menuItemRow);
	}
	
	private void displayRatings(Moment moment, View momentRow) {
		LinearLayout qRatingsLayout = (LinearLayout) momentRow.findViewById(R.id.qualityRatingLayout);
		for (int i = 0; i < moment.getQualityRating(); i++){
			ImageView ratingIcon = (ImageView) qRatingsLayout.getChildAt(i);
			ratingIcon.setImageResource(R.drawable.quality_icon_selected);
		}
		LinearLayout pRatingsLayout = (LinearLayout) momentRow.findViewById(R.id.priceRatingLayout);
		for (int i = 0; i < moment.getPriceRating(); i++){
			ImageView ratingIcon = (ImageView) pRatingsLayout.getChildAt(i);
			ratingIcon.setImageResource(R.drawable.price_icon_selected);
		}
		TextView priceMeaningTextView = (TextView)momentRow.findViewById(R.id.price_meaning);
		TextView qualityMeaningTextView = (TextView)momentRow.findViewById(R.id.quality_meaning);
		priceMeaningTextView.setText(PriceWidget.getMeaning(moment.getPriceRating(), this));
		qualityMeaningTextView.setText(QualityWidget.getMeaning(moment.getQualityRating(), this));
	}
	
	private void clearRatings(Moment moment, View momentRow){
		LinearLayout qRatingsLayout = (LinearLayout) momentRow.findViewById(R.id.qualityRatingLayout);
		for (int i = 0; i < 5; i++){
			ImageView ratingIcon = (ImageView) qRatingsLayout.getChildAt(i);
			ratingIcon.setImageResource(R.drawable.quality_icon_default);
		}
		LinearLayout pRatingsLayout = (LinearLayout) momentRow.findViewById(R.id.priceRatingLayout);
		for (int i = 0; i < 5; i++){
			ImageView ratingIcon = (ImageView) pRatingsLayout.getChildAt(i);
			ratingIcon.setImageResource(R.drawable.price_icon_default);
		}
	}
	
	/*
	 * Based on the selected place, display the appropriate information in the review
	 */
	private void updateCurrentReview(){
		LinearLayout menuItemRow = (LinearLayout) this.findViewById(R.id.current_menu_item);
		TextView restaurantTextView = (TextView) menuItemRow.findViewById(R.id.restaurantTextView);
    	TextView foodTextView = (TextView) menuItemRow.findViewById(R.id.foodTextView);
    	TextView descriptionTextView = (TextView) menuItemRow.findViewById(R.id.descriptionTextView);
    	TextView qualityTextView = (TextView) menuItemRow.findViewById(R.id.qualityTextView);
    	TextView priceTextView = (TextView) menuItemRow.findViewById(R.id.priceTextView);
    	TextView dateTextView = (TextView) menuItemRow.findViewById(R.id.dateTimeTextView);
    	ImageView foodThumbnail = (ImageView) menuItemRow.findViewById(R.id.pictureThumbnail);
    	
    	if (this.selectedMoment == null){
    		restaurantTextView.setText(placesFragment.getSelectedPlace().getName());
    	}else{
    		String imagePath = selectedMoment.getMenuItem().getImagePath();
    		if (imagePath != null && imagePath.length() > 0)
    			foodThumbnail.setImageBitmap(Utility.decodeSampledBitmapFromFile(selectedMoment.getMenuItem().getImagePath(), Utility.THUMBSIZE_WIDTH, Utility.THUMBSIZE_HEIGHT));
    		
        	restaurantTextView.setText("@ " + selectedMoment.getRestaurant().getName());
        	foodTextView.setText(selectedMoment.getMenuItem().getName());
        	descriptionTextView.setText(selectedMoment.getDescription());
        	dateTextView.setText(selectedMoment.getDate());
    	}
	}
	
}
