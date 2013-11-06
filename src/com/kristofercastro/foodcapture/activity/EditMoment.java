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
import android.widget.TextView;
import android.widget.Toast;


public class EditMoment extends Activity implements LocationListener{

	int qualityRating = 0;
	int priceRating = 0;
	
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
	
	ArrayList<ImageView> qualityRatingIcons;
	ArrayList<ImageView> priceRatingIcons;
	
	// Quality Rating Icons
	ImageView firstStar;
	ImageView secondStar;
	ImageView thirdStar;
	ImageView fourthStar;
	ImageView fifthStar;
	
	// Price Rating Icons
	ImageView firstDollar;
	ImageView secondDollar;
	ImageView thirdDollar;
	ImageView fourthDollar;
	ImageView fifthDollar;
	
	// Camera variables
	Button cameraButton;
	Button takePictureButton;
	ImageView pictureImageView;
	ImageView cameraImageView;
	private Uri fileUri;

	Bitmap thumbImage; 

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int THUMBSIZE_HEIGHT = 300;
	private static final int THUMBSIZE_WIDTH = 300;
	private int MEDIA_TYPE_IMAGE = 1;
	private String imagePath;
	
	// date
	TextView dateTextView;
	
	// map
	private GoogleMap mMap;
	LocationManager mLocManager;
	Location mLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_moment);
		
		foodTextView = (TextView) this.findViewById(R.id.foodTextView);
		qualityTextView = (TextView) this.findViewById(R.id.qualityTextView);
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
		initializeIcons();
		
		setupLocManager();
		mMap =  ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		setUpMapIfNeeded();
		
	}

	private void setupLocManager() {
		mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		String bestProvider = mLocManager.getBestProvider(criteria, true);	
		mLocation = mLocManager.getLastKnownLocation(bestProvider);
		//Log.i("MyCameraApp", + mLocation.getLatitude() + " : " +  mLocation.getLongitude());
	}

	private void setUpMapIfNeeded() {
		if (mMap == null){
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
		}
		
		if (mMap != null){
			mMap.setMyLocationEnabled(true);
			GoogleMapOptions options = new GoogleMapOptions();
			
			options.mapType(GoogleMap.MAP_TYPE_NORMAL)
				.compassEnabled(false)
				.rotateGesturesEnabled(false)
				.tiltGesturesEnabled(false);
			drawMarker(mLocation);
		}
	}

	/**
	 * Draws the current location.
	 * @param mLocation2
	 */
	private void drawMarker(Location location) {
		mMap.clear();
		LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition)
		.snippet("Lat: " + location.getLatitude() + "Lng: "+ location.getLongitude())
		.icon(BitmapDescriptorFactory.defaultMarker((BitmapDescriptorFactory.HUE_AZURE)))
		.title("Current Location");
		mMap.addMarker(markerOptions);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));

	}

	/**
	 * Bind the rating variables to the actual ID and store 
	 * them into their corresponding array list.
	 */
	private void initializeIcons() {
		firstStar = (ImageView) findViewById(R.id.firstStar);
		secondStar = (ImageView) findViewById(R.id.secondStar);
		thirdStar = (ImageView) findViewById(R.id.thirdStar);
		fourthStar = (ImageView) findViewById(R.id.fourthStar);
		fifthStar = (ImageView) findViewById(R.id.fifthStar);

		// Price Rating Icons
		firstDollar = (ImageView) findViewById(R.id.firstDollar);
		secondDollar= (ImageView) findViewById(R.id.secondDollar);
		thirdDollar = (ImageView) findViewById(R.id.thirdDollar);
		fourthDollar = (ImageView) findViewById(R.id.fourthDollar);
		fifthDollar = (ImageView) findViewById(R.id.fifthDollar);

		
		qualityRatingIcons = new ArrayList<ImageView>();
		qualityRatingIcons.add(firstStar);
		qualityRatingIcons.add(secondStar);
		qualityRatingIcons.add(thirdStar);
		qualityRatingIcons.add(fourthStar);
		qualityRatingIcons.add(fifthStar);
		
		priceRatingIcons = new ArrayList<ImageView>();
		priceRatingIcons.add(firstDollar);
		priceRatingIcons.add(secondDollar);
		priceRatingIcons.add(thirdDollar);
		priceRatingIcons.add(fourthDollar);
		priceRatingIcons.add(fifthDollar);
		
		toggleQualitySelected();
		togglePriceSelected();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_moment, menu);
		return true;
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
	
	/**
	 * Toggle between selected icon image versus non-selected icon image
	 */
	private void togglePriceSelected(){
		for (final ImageView icon : priceRatingIcons){
			icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// reset
					priceRating = 0;
					for (ImageView icon : priceRatingIcons){
						icon.setImageResource(R.drawable.price_icon_default);
					}		
					// look for the icon clicked by iterating through each icon and marking it
					for (ImageView currentIcon : priceRatingIcons){
						if ( !icon.equals(currentIcon) ){
							currentIcon.setImageResource(R.drawable.price_icon_selected);
							priceRating++;
						}else{
							currentIcon.setImageResource(R.drawable.price_icon_selected);
							priceRating++;
							break;
						}
					}
				}
			});
		}				
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
			return saveMoment();
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (result != null){
				Toast.makeText(EditMoment.this, "Moment Saved!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(EditMoment.this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
				EditMoment.this.startActivity(i);
			}
			else
				Toast.makeText(EditMoment.this, "Couldn't save the moment.", Toast.LENGTH_SHORT).show();

		}			
	}

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
		moment.setPriceRating(priceRating);
		moment.setQualityRating(qualityRating);
		moment.setDescription(descriptionEditText.getText().toString());
		moment.setDate(Utility.getCurrentDate());
		MomentDAO momentDAO = new MomentDAO(new DBHelper(this));
		return momentDAO.create(moment);
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
		Utility.changeTypeFace(qualityTextView, getApplicationContext(), CustomFonts.LANE_NARROW);
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
	 * This method is taken from developer.android.com/training/displaying-bitmaps.html
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    return inSampleSize;
	}
	
	/**
	 * This method is taken from developer.android.com/training/displaying-bitmaps.html
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String path,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	/**
	 * Display the image in the preview image view.
	 */
	private void handleCameraPhoto(){
		pictureImageView.setImageBitmap(decodeSampledBitmapFromFile(this.fileUri.getPath(), THUMBSIZE_WIDTH, THUMBSIZE_HEIGHT));
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

}
