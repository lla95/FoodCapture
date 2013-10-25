package com.kristofercastro.foodcapture.activity;

import java.net.URL;
import java.util.ArrayList;

import com.example.foodcapture.R;
import com.kristofercastro.foodcapture.activity.Utility.CustomFonts;
import com.kristofercastro.foodcapture.model.Moment;
import com.kristofercastro.foodcapture.model.dbo.DBHelper;
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
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ArrayList<Moment> momentsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void changeFontLaneNarrow(TextView view){
		Utility.changeTypeFace(view, getApplicationContext(), CustomFonts.LANE_NARROW);
	}
	
	private void changeFontTitillium(TextView view){
		Utility.changeTypeFace(view, getApplicationContext(), CustomFonts.TITILLIUM_BOLD);
	}
	
	/**
	 * Takes the list of moments and display them.
	 */
	private void displayAllMoments(){
		MomentDAO momentDAO = new MomentDAO(new DBHelper(this));
		momentsList = momentDAO.retrieveAll();
		clearMoments();
        LinearLayout item = (LinearLayout) findViewById(R.id.momentsListLayout);
		for (Moment moment : momentsList){
        	View momentRow = getLayoutInflater().inflate(R.layout.menu_item_row, item, false);
        	
        	final long momentID = moment.getId();
        	TextView restaurantTextView = (TextView) momentRow.findViewById(R.id.restaurantTextView);
        	TextView foodTextView = (TextView) momentRow.findViewById(R.id.foodTextView);
        	TextView descriptionTextView = (TextView) momentRow.findViewById(R.id.descriptionTextView);
        	TextView qualityTextView = (TextView) momentRow.findViewById(R.id.qualityTextView);
        	TextView priceTextView = (TextView) momentRow.findViewById(R.id.priceTextView);
        	ImageView foodThumbnail = (ImageView) momentRow.findViewById(R.id.pictureThumbnail);
        	
        	changeFontLaneNarrow(descriptionTextView);
    		changeFontTitillium(foodTextView);
    		changeFontTitillium(qualityTextView);
    		changeFontTitillium(priceTextView);
    		changeFontTitillium(restaurantTextView);
    		
    		
			final int THUMBSIZE_WIDTH = 500;
			final int THUMBSIZE_HEIGHT = 300;
			
			Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(moment.getMenuItem().getImagePath()),  THUMBSIZE_WIDTH, THUMBSIZE_HEIGHT);
			foodThumbnail.setImageBitmap(thumbImage);
    		
        	restaurantTextView.setText("@ " + moment.getRestaurant().getName());
        	foodTextView.setText(moment.getMenuItem().getName());
        	descriptionTextView.setText(moment.getDescription());
   	
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
        	
        	displayRatings(moment, momentRow);
		}
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
	
	private Boolean deleteMoment(long momentID){
		MomentDAO momentDAO = new MomentDAO(new DBHelper(this));
		return momentDAO.delete(momentID);
	}
	
	private void clearMoments(){
        LinearLayout item = (LinearLayout) findViewById(R.id.momentsListLayout);
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
		}
		return true;	
	}
	
	/**
	 * Event handler for when the user click the add moment
	 * in the action bar
	 */
	private void addMomentHandler(){
		Intent i = new Intent(this, EditMoment.class);
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
