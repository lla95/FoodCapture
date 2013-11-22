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
import com.kristofercastro.foodcapture.R.layout;
import com.kristofercastro.foodcapture.R.menu;
import com.kristofercastro.foodcapture.activity.EditMoment;
import com.kristofercastro.foodcapture.activity.MainActivity;
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
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EditAdventure extends FragmentActivity implements FoodAdventureActivityInterface {

	GooglePlacesWebService placesService;
	GoogleMap googleMaps;
	private LocationManager mLocManager;
	private String bestProvider;
	private Location mLocation;
	ArrayList<Place> localRestaurants;
	HashMap<Integer, Marker> markers;
	
	Bundle currentSavedInstanceState;
	
	Button generateAdventureButton;
	EditText foodAdventureNameEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_adventure);
		
		markers = new HashMap<Integer,Marker>();
		placesService = new GooglePlacesWebService(this);
		
		setupLocManager();
		setupGoogleMaps();
		
		bindUIControls();
		changeFont();
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
	
	/**
	 * Draws all the markers for all returned places
	 */
	private void drawAllMarkers(){
		googleMaps.clear();
		for (int i = 0; i < localRestaurants.size(); i++){
			Place place = localRestaurants.get(i);
			drawMarker(place, i, place.getLatitude(), place.getLongitude());
		}
	}
	
	private void focusCameraAtCurrentLocation(){
		googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),11));
	}
	
	private void setupLocManager() {
		mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		bestProvider = mLocManager.getBestProvider(criteria, true);	
		mLocation = mLocManager.getLastKnownLocation(bestProvider);
	}
	
	private void updateCurrentLocation(){
		mLocation = mLocManager.getLastKnownLocation(bestProvider);
	}
	
	@Override
	protected void onStart() {
		updateCurrentLocation();		
		super.onStart();
	}

	private void changeFont() {
		TextView titleTextView = (TextView) this.findViewById(R.id.foodAdventureTextView);
		TextView foodTextView = (TextView) this.findViewById(R.id.foodAdentureNameTextView);
	
		Utility.changeFontLaneNarrow(titleTextView, this);
		Utility.changeFontLaneNarrow(foodTextView, this);
		Utility.changeFontTitillium(generateAdventureButton, this);
	}

	private void bindUIControls() {
		generateAdventureButton = (Button) this.findViewById(R.id.generateAdventureButton);
		foodAdventureNameEditText = (EditText) this.findViewById(R.id.foodAdventureNameEditText);
	
		generateAdventureButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {	
				updateCurrentLocation();
				grabPlaces();
			}
			
		});
	}

	private void grabPlaces() {
		Void params = null;
		new RetrieveLocalRestaurants().execute(params);
	}

	private class RetrieveLocalRestaurants extends AsyncTask<Void, Void, Void>{

		private ProgressDialog dialog = new ProgressDialog(EditAdventure.this);
		
		
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
				
				// since fragments don't get recreated when their parent does, we must make sure
				// the horizontal view remains as at most 1 child view by first clearing it
				HorizontalScrollView fragmentContainer = (HorizontalScrollView) EditAdventure.this.findViewById(R.id.restaurant_lists);
				fragmentContainer.removeAllViews();
				
				android.app.FragmentManager fragmentManager = getFragmentManager();
				android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				Fragment placesFragment = new FoodAdventuresPlacesFragment();
				fragmentTransaction.add(R.id.restaurant_lists, placesFragment);
				fragmentTransaction.commit();
			}
		}		
	}
	
	private boolean validateInput() {
		boolean allGood = true;
		if ( this.foodAdventureNameEditText.getText().toString().length() == 0){
			foodAdventureNameEditText.setError("Adventure name/description is required!");
			allGood = false;
		}
		if ( localRestaurants == null ||  this.localRestaurants.size()  == 0 ){
			
			new AlertDialog.Builder(this)
			.setTitle("Missing input")
			.setMessage("\nYou need to click on 'Generate Places' to randomly find places for this feature to work.\n")
			.setNeutralButton("ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).show();
			allGood = false;
		}

		return allGood;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()){

			case R.id.action_save_adventure : {
				if (this.validateInput()){
					Void params = null;
					new SaveAdventureTask().execute(params);
				}
				break;
			}
		}
		return true;	
	}
	
	private class SaveAdventureTask extends AsyncTask<Void, Void, Long>{


		@Override
		protected Long doInBackground(Void... params) {
			return saveAdventure();
		}
		
		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (result != null){
				Toast.makeText(EditAdventure.this, "Moment Saved!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(EditAdventure.this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
				EditAdventure.this.startActivity(i);
				finish();
			}
			else
				Toast.makeText(EditAdventure.this, "Couldn't save the adventure.", Toast.LENGTH_SHORT).show();

		}	
	}
	
	private Long saveAdventure(){
		FoodAdventureDAO foodAdventureDAO = new FoodAdventureDAO(new DBHelper(EditAdventure.this));
		
		FoodAdventure foodAdventure = new FoodAdventure();
		foodAdventure.setName(foodAdventureNameEditText.getText().toString());
		foodAdventure.setDate(Utility.getCurrentDate());
		
		ArrayList<Moment> momentsArray = new ArrayList<Moment>();
					
		for (int i = 0; i < localRestaurants.size(); i++){
			Place place = localRestaurants.get(i);
			Moment newMoment = new Moment();
			
			Restaurant newRestaurant = new Restaurant();
			newRestaurant.setLatitude(place.getLatitude());
			newRestaurant.setLongitude(place.getLongitude());
			newRestaurant.setName(place.getName());
			
			// TO-DO : add address
			newMoment.setRestaurant(newRestaurant);	
			
			momentsArray.add(newMoment);
			
		}
		foodAdventure.setMoments(momentsArray);
		return foodAdventureDAO.create(foodAdventure);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_adventure, menu);
		return true;
	}

	@Override
	public HashMap<Integer, Marker> getMarkers() {
		return this.markers;
	}

	@Override
	public ArrayList<Place> getPlaces() {
		// TODO Auto-generated method stub
		return this.localRestaurants;
	}

	@Override
	public GoogleMap getGoogleMaps() {
		return this.googleMaps;
	}

}
