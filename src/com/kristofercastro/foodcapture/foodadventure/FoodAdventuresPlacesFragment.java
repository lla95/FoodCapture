package com.kristofercastro.foodcapture.foodadventure;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.activity.Utility;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;

public class FoodAdventuresPlacesFragment extends Fragment {
	
	ArrayList<Place> places;
	ViewGroup parent;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		 View v = inflater.inflate(R.layout.activity_food_adventures_places_fragment, container, false);
		 // grab the local restaurants from the main activity
		 places = ((FoodAdventuresList) this.getActivity()).localRestaurants;
        // Inflate the layout for this fragment
        return v;
    }
	
	@Override
	public void onStart() {
		super.onStart();
		displayPlaces();
	}


	private void displayPlaces(){
		LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.adventures_list_layout);
				
		for (int i = 0 ; i < places.size(); i++){
			Place place = places.get(i);
			View placesRow = getActivity().getLayoutInflater().inflate(R.layout.adventure_item_row, parent, false);
			TextView foodTextView = (TextView) placesRow.findViewById(R.id.foodTextView);
			TextView foodIdentifier = (TextView) placesRow.findViewById(R.id.placesIdentifierTextView);
			
			// change fonts
			Utility.changeFontTitillium(foodTextView, getActivity());
			Utility.changeFontTitillium(foodIdentifier, getActivity());
			
			// set values
			foodTextView.setText(place.getName());
			foodIdentifier.setText(Utility.convertIntToLetter(i));
			
			LinearLayout placesIdentifierCircle = (LinearLayout) placesRow.findViewById(R.id.placesIdentifierCircle);
			placesIdentifierCircle.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// lets get the text view inside the layout
					TextView textView = (TextView) v.findViewById(R.id.placesIdentifierTextView);
					
					// the letter is the key for the hash map of markers so we extract that
					String letter = textView.getText().toString();
					
					// find the marker we are looking for that corresponds to the letter
					HashMap<Integer, Marker> markerHash = ((FoodAdventuresList) FoodAdventuresPlacesFragment
							.this.getActivity()).markers;
					Marker marker = markerHash.get((int)(letter.charAt(0) - 65));		
					
					// make maps focus on that restaurant
					GoogleMap maps = ((FoodAdventuresList) FoodAdventuresPlacesFragment
							.this.getActivity()).googleMaps;
					maps.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));								
					marker.showInfoWindow();								
				}
				
			});
			
			// add to layout
			parent.addView(placesRow);
			
			
		}		
		
		// color even number of child one color
		for(int i = 0; i < parent.getChildCount(); i+=2){
			View placeRow = parent.getChildAt(i);
			placeRow.setBackgroundColor(Color.parseColor("#dfd9d0"));
		}
		
		// color odd number of child odd color
		for(int i = 1; i < parent.getChildCount(); i+=2){
			View placeRow = parent.getChildAt(i);
			placeRow.setBackgroundColor(Color.parseColor("#e9e2d8"));
		}
	}
	
}
