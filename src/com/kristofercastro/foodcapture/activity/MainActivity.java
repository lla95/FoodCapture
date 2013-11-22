package com.kristofercastro.foodcapture.activity;

import java.net.URL;
import java.util.ArrayList;

import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.activity.Utility.CustomFonts;
import com.kristofercastro.foodcapture.foodadventure.EditAdventure;
import com.kristofercastro.foodcapture.foodadventure.FoodAdventuresList;
import com.kristofercastro.foodcapture.model.FoodAdventure;
import com.kristofercastro.foodcapture.model.Moment;
import com.kristofercastro.foodcapture.model.dbo.DBHelper;
import com.kristofercastro.foodcapture.model.dbo.FoodAdventureDAO;
import com.kristofercastro.foodcapture.model.dbo.MomentDAO;

import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ArrayList<Moment> momentsList;
	ArrayList<FoodAdventure> foodAdventuresList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		changeFont();
	}

	private void changeFont() {
		TextView capturedMomentsHeader = (TextView) this.findViewById(R.id.capturedMomentsHeaderTextView);
		TextView foodAdventuresHeader = (TextView) this.findViewById(R.id.foodAdventuresHeaderTextView);
		
		Utility.changeFontTitillium(capturedMomentsHeader, this);
		Utility.changeFontTitillium(foodAdventuresHeader, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Takes the list of moments and display them.
	 */
	private void displayAllMoments(){
		new GetAllMomentsTask().execute(1);
	}
	
	private void displayAllAdventures(){
		new GetAllAdventuresTask().execute();
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
	}
	
	private class GetAllMomentsTask extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... params) {
			MomentDAO momentDAO = new MomentDAO(new DBHelper(MainActivity.this));
			momentsList = momentDAO.retrieveAll();			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			clearMoments();
	        LinearLayout item = (LinearLayout) findViewById(R.id.momentsListLayout);
			for (final Moment moment : momentsList){
				
				// only display moments that actually has a review.  That is it has a menu item
				if (moment.getMenuItem() == null || moment.getPriceRating() == 0
						|| moment.getQualityRating() == 0 || moment.getMenuItem().getName().length() == 0) break;
				
	        	View momentRow = getLayoutInflater().inflate(R.layout.menu_item_row, item, false);
	        	
	        	final long momentID = moment.getId();
	        	TextView restaurantTextView = (TextView) momentRow.findViewById(R.id.restaurantTextView);
	        	TextView foodTextView = (TextView) momentRow.findViewById(R.id.foodTextView);
	        	TextView descriptionTextView = (TextView) momentRow.findViewById(R.id.descriptionTextView);
	        	TextView qualityTextView = (TextView) momentRow.findViewById(R.id.qualityTextView);
	        	TextView priceTextView = (TextView) momentRow.findViewById(R.id.priceTextView);
	        	TextView dateTextView = (TextView) momentRow.findViewById(R.id.dateTimeTextView);
	        	ImageView foodThumbnail = (ImageView) momentRow.findViewById(R.id.pictureThumbnail);
	        	
	        	Utility.changeFontLaneNarrow(descriptionTextView, MainActivity.this);
	    		Utility.changeFontTitillium(foodTextView, MainActivity.this);
	    		Utility.changeFontTitillium(qualityTextView, MainActivity.this);
	    		Utility.changeFontTitillium(priceTextView, MainActivity.this);
	    		Utility.changeFontTitillium(restaurantTextView, MainActivity.this);
	    		
	    		String imagePath = moment.getMenuItem().getImagePath();
	    		if (imagePath != null && imagePath.length() > 0)
	    			foodThumbnail.setImageBitmap(Utility.decodeSampledBitmapFromFile(moment.getMenuItem().getImagePath(), Utility.THUMBSIZE_WIDTH, Utility.THUMBSIZE_HEIGHT));
	    		
	        	restaurantTextView.setText("@ " + moment.getRestaurant().getName());
	        	foodTextView.setText(moment.getMenuItem().getName());
	        	descriptionTextView.setText(moment.getDescription());
	        	dateTextView.setText(moment.getDate());
	        	item.addView(momentRow);
	        	
	        	// delete functionality
	        	momentRow.setOnLongClickListener(new OnLongClickListener(){

					@Override
					public boolean onLongClick(View v) {
						
						// Create confirmation dialog
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage("Are you sure you want to delete this moment?")
						       .setTitle("Confirm Delete");
						builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
									new DeleteMomentTask().execute(momentID);
					           }
					       });
						builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					               // User cancelled the dialog
					           }
					       });
						AlertDialog dialog = builder.create();
						dialog.show();
						return false;
					}
	        		
	        	});
	        	
	        	momentRow.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Intent i = new Intent(MainActivity.this, MomentInformation.class);
						i.putExtra("momentID", moment.getId());
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
						MainActivity.this.startActivity(i);		
					}
	        		
	        	});
	        	
	        	displayRatings(moment, momentRow);
			}
			super.onPostExecute(result);
		}
		
	}

	/**
	 * Save the moment in a separate thread task
	 */
	private class DeleteMomentTask extends AsyncTask<Long, Integer, Boolean>{


		@Override
		protected void onPostExecute(Boolean deleteSuccessfull) {
			super.onPostExecute(deleteSuccessfull);
			if (deleteSuccessfull){
				displayAllMoments();
			}
			else{
				Log.i("MyCameraApp", "Did not succesfully delete");
			}
		}

		@Override
		protected Boolean doInBackground(Long... params) {
			// TODO Auto-generated method stub
			return deleteMoment(params[0]);
		}			
	}
	
	
	private class GetAllAdventuresTask extends AsyncTask<Void, Void, Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			FoodAdventureDAO foodAdventureDAO = new FoodAdventureDAO(new DBHelper(MainActivity.this));
			foodAdventuresList= foodAdventureDAO.retrieveAll();			
			return foodAdventuresList.size();
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			clearAdventures();
	        LinearLayout item = (LinearLayout) findViewById(R.id.foodAdventuresListLayout);
			for (final FoodAdventure foodAdventure : foodAdventuresList){
						
	        	View foodAdventureItem = getLayoutInflater().inflate(R.layout.adventure_item_row_main, item, false);
	        	
	        	final long foodAdventureID = foodAdventure.getId();
	        	
	        	TextView adventureNameTextView = (TextView) foodAdventureItem.findViewById(R.id.adventureNameTextView);
	        	TextView adventureDateTextView = (TextView) foodAdventureItem.findViewById(R.id.adventureDateTextView);
	        	TextView adventureProgressTextView = (TextView) foodAdventureItem.findViewById(R.id.adventureProgressTextView);
	        	
	        	adventureNameTextView.setText(foodAdventure.getName());
	        	adventureDateTextView.setText(foodAdventure.getDate());
	        	
	        	int progress = 0;
	        	
	        	ArrayList<Moment> moments = foodAdventure.getMoments();
	        	Log.i("MyCameraApp", "moments size: " + moments.size());
	        	for (Moment moment : moments){
	        		if ( moment.getMenuItem() == null || moment.getPriceRating() == 0 || moment.getQualityRating() == 0){
	        			// don't add to counter
	        		}else{
	        			progress += 1; // since we only generate 5
	        		}
	        	}
	        	
	        	int percentage = (int) ((float) progress/moments.size() * 100);
	        	adventureProgressTextView.setText(percentage+"%");
	        	
	        	Utility.changeFontTitillium(adventureNameTextView, MainActivity.this);
	    		Utility.changeFontTitillium(adventureDateTextView, MainActivity.this);
	    		Utility.changeFontTitillium(adventureProgressTextView, MainActivity.this);
	
	        	item.addView(foodAdventureItem);
	        	
	        	// delete functionality
	        	foodAdventureItem.setOnLongClickListener(new OnLongClickListener(){

					@Override
					public boolean onLongClick(View v) {
						
						// Create confirmation dialog
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage("Are you sure you want to delete this adventure?")
						       .setTitle("Confirm Delete");
						builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
									//new DeleteAdventureTask().execute(foodAdventureID);
					           }
					       });
						builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					               // User cancelled the dialog
					           }
					       });
						AlertDialog dialog = builder.create();
						dialog.show();
						return false;
					}
	        		
	        	});	        	
	        	foodAdventureItem.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Intent i = new Intent(MainActivity.this, FoodAdventuresList.class);
						i.putExtra("foodAdventureID", foodAdventure.getId());
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
						MainActivity.this.startActivity(i);		
					}
	        		
	        	});
			}
			super.onPostExecute(result);
		}		
	}
	
	private Boolean deleteMoment(long momentID){
		MomentDAO momentDAO = new MomentDAO(new DBHelper(this));
		return momentDAO.delete(momentID);
	}
	
	private void clearMoments(){
        LinearLayout item = (LinearLayout) findViewById(R.id.momentsListLayout);
        item.removeAllViews();
	}
	
	private void clearAdventures(){
		LinearLayout item = (LinearLayout) findViewById(R.id.foodAdventuresListLayout);
		item.removeAllViews();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		displayAllMoments();
		displayAllAdventures();
		super.onStart();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()){
		
			case R.id.action_add_moment : {
				addMomentHandler();
				break;
			}
			
			case R.id.action_delete_moment : {
				deleteMomentHandler();
				break;
			}
			case R.id.action_add_adventure : {
				addAdventureHandler();
				break;
			}
		}
		return true;	
	}
	
	private void addAdventureHandler() {
		Intent i = new Intent(this, EditAdventure.class);
		//i.putExtra("mode", "hi");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		this.startActivity(i);
		
	}

	/**
	 * Event handler for when the user click the add moment
	 * in the action bar
	 */
	private void addMomentHandler(){
		Intent i = new Intent(this, EditMoment.class);
		i.putExtra("mode", Message.CREATE_NEW_MOMENT);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		this.startActivity(i);
	}
	
	/**
	 * Event handler for when the user click the delete moment
	 * handler
	 */
	private void deleteMomentHandler(){
		new AlertDialog.Builder(this)
		.setTitle("How to Delete a Moment?")
		.setMessage("\nTouch and hold a moment for a few seconds to delete it.\n")
		.setNeutralButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).show();
	}

}
