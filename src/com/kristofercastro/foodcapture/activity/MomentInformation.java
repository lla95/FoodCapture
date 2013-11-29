package com.kristofercastro.foodcapture.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
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
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	TextView reviewTextView;
	
	ImageView pictureImageView;
	
	GoogleMap googleMaps;
	Moment moment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moment_information);	
		
		bindControls();
		Bundle extras = this.getIntent().getExtras();
		setupGoogleMaps();
		new GetMomentTask().execute(extras.getLong("momentID"));
	}

	private void bindControls() {
		foodTextView = (TextView) this.findViewById(R.id.foodTextView);
		reviewTextView = (TextView) this.findViewById(R.id.reviewTextView);
		qualityTextView = (TextView) this.findViewById(R.id.qualityTextView);
		priceTextView = (TextView) this.findViewById(R.id.priceTextView);
    	descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		restaurantTextView = (TextView) this.findViewById(R.id.restaurantTextView);
		dateTimeTextView = (TextView) this.findViewById(R.id.dateTimeTextView);
		locationTextView = (TextView) this.findViewById(R.id.locationTextView);
		pictureTextView = (TextView) this.findViewById(R.id.pictureTextView);
		pictureImageView = (ImageView) this.findViewById(R.id.pictureThumbnail);		
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
			
			foodTextView.setText(moment.getMenuItem().getName());
			descriptionTextView.setText(moment.getDescription());
        	restaurantTextView.setText("@ " + moment.getRestaurant().getName());
			dateTimeTextView.setText(moment.getDate());
			String imagePath = moment.getMenuItem().getImagePath();
    		if (imagePath != null && imagePath.length() > 0)
    			pictureImageView.setImageBitmap(Utility.decodeSampledBitmapFromFile(moment.getMenuItem().getImagePath(), Utility.THUMBSIZE_WIDTH, Utility.THUMBSIZE_HEIGHT));
			displayRatings(moment);
			
			changeFont();    		
			super.onPostExecute(result);
		}

		private void changeFont() {
        	Utility.changeFontLaneNarrow(descriptionTextView, MomentInformation.this);
    		Utility.changeFontTitillium(foodTextView, MomentInformation.this);
    		Utility.changeFontTitillium(qualityTextView, MomentInformation.this);
    		Utility.changeFontTitillium(priceTextView, MomentInformation.this);
    		Utility.changeFontTitillium(restaurantTextView, MomentInformation.this);
    		
    		Utility.changeFontLaneNarrow(reviewTextView, MomentInformation.this);
    		Utility.changeFontLaneNarrow(locationTextView, MomentInformation.this);		
		}

		@Override
		protected Void doInBackground(Long... params) {
			Long momentID = params[0];
			MomentDAO momentDAO = new MomentDAO(new DBHelper(MomentInformation.this));
			moment = momentDAO.retrieve(momentID);
			return null;
		}	
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
		
		TextView priceMeaningTextView = (TextView)findViewById(R.id.price_meaning);
		TextView qualityMeaningTextView = (TextView)findViewById(R.id.quality_meaning);
		priceMeaningTextView.setText(PriceWidget.getMeaning(moment.getPriceRating(), this));
		qualityMeaningTextView.setText(QualityWidget.getMeaning(moment.getQualityRating(), this));
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
		.title("Location");
		googleMaps.addMarker(markerOptions);
		googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.moment_information, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()){
			case R.id.action_edit_moment : {
				editMomentHandler();
				finish();
			}
		}
		return true;
	}
	
	/**
	 * Event handler for when the user click the add moment
	 * in the action bar
	 */
	private void editMomentHandler(){
		Intent i = new Intent(this, EditMoment.class);
		i.putExtra("mode", Message.EDIT_EXISTING_MOMENT);
		i.putExtra("momentID", getIntent().getExtras().getLong("momentID"));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		this.startActivity(i);
	}
}
