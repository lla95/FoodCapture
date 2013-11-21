package com.kristofercastro.foodcapture.activity;

import java.io.File;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.R.layout;
import com.kristofercastro.foodcapture.R.menu;
import com.kristofercastro.foodcapture.activity.Utility.CustomFonts;
import com.kristofercastro.foodcapture.foodadventure.Place;
import com.kristofercastro.foodcapture.model.*;
import com.kristofercastro.foodcapture.model.dbo.DBHelper;
import com.kristofercastro.foodcapture.model.dbo.MomentDAO;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class EditMoment extends Activity{
	
	// static labels
	TextView foodTextView;
	TextView qualityTextView;
	TextView priceTextView;
	TextView descriptionTextView;
	TextView restaurantTextView;
	TextView locationTextView;
	TextView pictureTextView;
	
	// Input widgets
	EditText foodEditText;
	EditText descriptionEditText;
	EditText restaurantEditText;

	// Rating widgets
	QualityWidget qualityWidget;
	PriceWidget priceWidget;
	
	// Camera variables
	Button cameraButton;
	Button takePictureButton;
	ImageView pictureImageView;
	ImageView cameraImageView;
	private Uri fileUri;

	Bitmap thumbImage; 

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int THUMBSIZE_HEIGHT = 256;
	private static final int THUMBSIZE_WIDTH = 256;
	private int MEDIA_TYPE_IMAGE = 1;
	private String imagePath;
	
	// date
	TextView dateTextView;
	
	// map
	private GoogleMap mMap;
	LocationManager mLocManager;
	Location mLocation;
	private String bestProvider;
	
	// state of moment
	int MODE_CREATE_OR_EDIT;
	Place currentPlace;
	Moment moment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_moment);
		
		Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();

		foodTextView = (TextView) this.findViewById(R.id.foodTextView);
		priceTextView = (TextView) this.findViewById(R.id.priceTextView);
		descriptionTextView = (TextView) this.findViewById(R.id.descriptionTextView);
		restaurantTextView = (TextView) this.findViewById(R.id.restaurantTextView);
		locationTextView = (TextView) this.findViewById(R.id.locationTextView);
		pictureTextView = (TextView) this.findViewById(R.id.pictureTextView);
	
		foodEditText = (EditText) this.findViewById(R.id.foodEditText);
		descriptionEditText = (EditText) this.findViewById((R.id.descriptionEditText));
		restaurantEditText = (EditText) this.findViewById(R.id.restaurantEditText);
		
		takePictureButton = (Button) this.findViewById(R.id.takePictureButton);
		pictureImageView = (ImageView) this.findViewById(R.id.pictureImageView);
		cameraImageView = (ImageView) this.findViewById(R.id.cameraImageView);
		//dateTextView = (TextView) this.findViewById(R.id.dateTextView);
		
		takePictureButton.setOnClickListener(cameraButtonOnClickHandler);
		
		cameraImageView.setOnClickListener(cameraButtonOnClickHandler);
		changeFont();
		
		// Initialize widgets
		qualityWidget = new QualityWidget(this);
		priceWidget = new PriceWidget(this);
		
		// Figure out which mode we in: creating new moment or editting new one
		Bundle extras = this.getIntent().getExtras();
		MODE_CREATE_OR_EDIT = (Integer) extras.get("mode");
		currentPlace = new Place();
		
		setupEditTextValues();
		setupLocManager();
		setUpMapIfNeeded();
	}
	
	
	
	@Override
	protected void onStart() {
		Toast.makeText(this, "OnStart", Toast.LENGTH_SHORT).show();
		super.onStart();
	}



	private void setupEditTextValues() {
		if (MODE_CREATE_OR_EDIT == Message.EDIT_EXISTING_MOMENT){
			Long momentID = getIntent().getExtras().getLong("momentID");
			
			new GetMomentTask().execute(momentID);
		}
	}
	
	private class GetMomentTask extends AsyncTask<Long, Void, Void>{

		@Override
		protected void onPostExecute(Void result) {
			drawMarkerAlreadyExists();
			
			foodEditText.setText(moment.getMenuItem().getName());
			descriptionEditText.setText(moment.getDescription());
        	restaurantEditText.setText(moment.getRestaurant().getName());
			pictureImageView.setImageBitmap(Utility.decodeSampledBitmapFromFile(moment.getMenuItem().getImagePath(), Utility.THUMBSIZE_WIDTH, Utility.THUMBSIZE_HEIGHT));
			imagePath = moment.getMenuItem().getImagePath();
			
			qualityWidget.setQualityRating(moment.getQualityRating());
			priceWidget.setPriceRating(moment.getPriceRating());
			displayRatings(moment);	
			
			changeFont();    		
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Long... params) {
			Long momentID = params[0];
			MomentDAO momentDAO = new MomentDAO(new DBHelper(EditMoment.this));
			moment = momentDAO.retrieve(momentID);
			return null;
		}	
		
		private void displayRatings(Moment moment) {
			LinearLayout qRatingsLayout = (LinearLayout) findViewById(R.id.qualityRatingLayout);
			for (int i = 0; i < moment.getQualityRating(); i++){
				ImageView ratingIcon = (ImageView) qRatingsLayout.getChildAt(i);
				ratingIcon.setImageResource(R.drawable.quality_icon_selected);
			}
			LinearLayout pRatingsLayout = (LinearLayout) findViewById(R.id.priceRatingLayout);
			for (int i = 0; i < moment.getPriceRating(); i++){
				ImageView ratingIcon = (ImageView) pRatingsLayout.getChildAt(i);
				ratingIcon.setImageResource(R.drawable.price_icon_selected);
			}
		}
	}
	
	@Override
	protected void onResume() {
		if ( mLocManager == null)
			setupLocManager();
		setUpMapIfNeeded();
		super.onResume();
	}

	private void setupLocManager() {
		mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		bestProvider = mLocManager.getBestProvider(criteria, true);	
		mLocation = mLocManager.getLastKnownLocation(bestProvider);
	}

	private void setUpMapIfNeeded() {
		if (mMap == null){
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
		}
		
		if (mMap != null){
			mMap.clear();
			mMap.setMyLocationEnabled(true);
			
			// update location
			mLocation = mLocManager.getLastKnownLocation(bestProvider);
			
			if (MODE_CREATE_OR_EDIT == Message.CREATE_NEW_MOMENT){
				drawMarker(mLocation);
			}else if (MODE_CREATE_OR_EDIT == Message.EDIT_EXISTING_MOMENT){
				Log.i("MyCameraApp", "id: " + this.getIntent().getExtras().getLong("momentID"));
				//drawMarkerAlreadyExists();
			}
		}
	}

	/**
	 * Draws the current location.
	 * @param mLocation2
	 */
	private void drawMarker(Location location) {
		LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition)
		.icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_AZURE)))
		.title("Current Location");
		mMap.addMarker(markerOptions);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));

	}
	
	/**
	 * Draw the restaurants location
	 * @param mLocation2
	 */
	private void drawMarkerAlreadyExists() {
		Restaurant restaurant = moment.getRestaurant();
		LatLng currentPosition = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition)
		.icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_AZURE)))
		.title("Location");
		mMap.addMarker(markerOptions);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_moment, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		
		case R.id.action_save_moment : {
			// only save if we validated the input
			if (validateInput()){		
				new SaveMomentTask().execute();
			}
			break;
		}
	}
	
	return true;	
	}

	/**
	 * Save the moment in a separate thread task
	 */
	private class SaveMomentTask extends AsyncTask<URL, Integer, Long>{

		
		@Override
		protected Long doInBackground(URL... params) {
			Long result = null;
			if (MODE_CREATE_OR_EDIT == Message.CREATE_NEW_MOMENT){
				result = saveMoment();
			}
			else if (MODE_CREATE_OR_EDIT == Message.EDIT_EXISTING_MOMENT){
				result = (long) saveMomentAlreadyExist();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (result != null){
				Toast.makeText(EditMoment.this, "Moment Saved!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(EditMoment.this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
				EditMoment.this.startActivity(i);
				finish();
			}
			else
				Toast.makeText(EditMoment.this, "Couldn't save the moment.", Toast.LENGTH_SHORT).show();

		}			
	}

/*	private class GetMomentTask extends AsyncTask<Long, Void, Void>{


		@Override
		protected void onPostExecute(Void result) {
	
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Long... params) {
			Long momentID = params[0];
			MomentDAO momentDAO = new MomentDAO(new DBHelper(MomentInformation.this));
			moment = momentDAO.retrieve(momentID);
			return null;
		}	
	}*/
	
	/**
	 * 
	 * @return the id of the moment
	 */
	private Long saveMoment() {
		
		// create our menu item object
		com.kristofercastro.foodcapture.model.MenuItem 
			menuItem = new com.kristofercastro.foodcapture.model.MenuItem();
		
		menuItem.setImagePath(imagePath);
		menuItem.setName(foodEditText.getText().toString());
		
		// create our restaurant object
		com.kristofercastro.foodcapture.model.Restaurant
			restaurant = new com.kristofercastro.foodcapture.model.Restaurant();
		restaurant.setName(restaurantEditText.getText().toString());
		
		restaurant.setLatitude((float) mLocation.getLatitude());
		restaurant.setLongitude((float) mLocation.getLongitude());

		// create our moment object
		com.kristofercastro.foodcapture.model.Moment
			moment = new com.kristofercastro.foodcapture.model.Moment();
		
		moment.setMenuItem(menuItem);
		moment.setRestaurant(restaurant);
		moment.setPriceRating(priceWidget.getPriceRating());
		moment.setQualityRating(qualityWidget.getQualityRating());
		moment.setDescription(descriptionEditText.getText().toString());
		moment.setDate(Utility.getCurrentDate());
		MomentDAO momentDAO = new MomentDAO(new DBHelper(this));
		return momentDAO.create(moment);
	}
	
	private int saveMomentAlreadyExist(){
		moment.setPriceRating(priceWidget.getPriceRating());
		moment.setQualityRating(qualityWidget.getQualityRating());
		moment.setDescription(descriptionEditText.getText().toString());
		
		// create our restaurant object
		com.kristofercastro.foodcapture.model.Restaurant
			restaurant = moment.getRestaurant();
		restaurant.setName(restaurantEditText.getText().toString());
		MomentDAO momentDAO = new MomentDAO(new DBHelper(this));
		
		// create our menu item object
		com.kristofercastro.foodcapture.model.MenuItem 
			menuItem = moment.getMenuItem();
		
		menuItem.setImagePath(imagePath);
		menuItem.setName(foodEditText.getText().toString());
		
		return momentDAO.update(moment);
	}

	private boolean validateInput() {
		boolean allGood = true;
		if ( foodEditText.getText().toString().length() == 0){
			foodEditText.setError("Food name is required!");
			allGood = false;
		}
		if ( restaurantEditText.getText().toString().length() == 0 ){
			restaurantEditText.setError("Restuarant name is required!");
			allGood = false;
		}

		return allGood;
	}

	private void changeFont(){
		Utility.changeTypeFace(foodTextView, getApplicationContext(), CustomFonts.LANE_NARROW);
		Utility.changeTypeFace(priceTextView, getApplicationContext(), CustomFonts.LANE_NARROW);
		Utility.changeTypeFace(descriptionTextView, getApplicationContext(), CustomFonts.LANE_NARROW);
		Utility.changeTypeFace(restaurantTextView, getApplicationContext(), CustomFonts.LANE_NARROW);
		Utility.changeTypeFace(locationTextView, getApplicationContext(), CustomFonts.LANE_NARROW);
		Utility.changeTypeFace(pictureTextView, getApplicationContext(), CustomFonts.LANE_NARROW);
		Utility.changeTypeFace(foodEditText, getApplicationContext(), CustomFonts.TITILLIUM_BOLD);
		Utility.changeTypeFace(descriptionEditText, getApplicationContext(), CustomFonts.LANE_NARROW);
		Utility.changeTypeFace(restaurantEditText, getApplicationContext(), CustomFonts.LANE_NARROW);	
		Utility.changeTypeFace(takePictureButton, getApplicationContext(), CustomFonts.TITILLIUM_BOLD);
	}
	
	/**
	 * Call back function that handles taking photos and saving it to the disk
	 * and displaying the thumbnail.
	 */
	OnClickListener cameraButtonOnClickHandler = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
			i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}

		private Uri getOutputMediaFileUri(int mediaTypeImage) {
			
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FoodCapture");
			
			if ( !mediaStorageDir.exists()){
				if ( !mediaStorageDir.mkdir() ){
					Log.d("MyCameraApp", "failed to create directory");
					return null;
				}
			}
			
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			File mediaFile = null;
			
			if(mediaTypeImage == MEDIA_TYPE_IMAGE){
				mediaFile = new File(mediaStorageDir + File.separator + "IMG_" + timeStamp + ".jpg");
				imagePath = mediaFile.getPath();
			}
			
			return Uri.fromFile(mediaFile);
		}		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("MyCameraApp","Data: " + fileUri.getPath());
		if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ){
			if ( resultCode == RESULT_OK ){
				//if ( data == null) return;
				handleCameraPhoto();
			}else if ( resultCode == RESULT_CANCELED){
				// cancelled the image capture
			}else{
				// failed to capture image
				Toast.makeText(this, "Failed!", Toast.LENGTH_LONG).show();

			}
		}
	}
	
	/**
	 * Display the image in the preview image view.
	 */
	private void handleCameraPhoto(){
		pictureImageView.setImageBitmap(Utility.decodeSampledBitmapFromFile(fileUri.getPath(), THUMBSIZE_WIDTH, THUMBSIZE_HEIGHT));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
