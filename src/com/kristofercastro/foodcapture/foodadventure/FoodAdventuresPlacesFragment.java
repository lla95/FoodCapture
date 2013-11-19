package com.kristofercastro.foodcapture.foodadventure;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.R;
import com.kristofercastro.foodcapture.activity.MomentInformation;
import com.kristofercastro.foodcapture.activity.Utility;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
			
			
			Utility.changeFontTitillium(foodTextView, this.getActivity());
			
			foodTextView.setText(place.getName());
			foodIdentifier.setText(String.format("%s", (char)(65+i)).toUpperCase());
			parent.addView(placesRow);
		}		
		
		for(int i = 0; i < parent.getChildCount(); i+=2){
			View placeRow = parent.getChildAt(i);
			placeRow.setBackgroundColor(Color.parseColor("#dfd9d0"));
		}
		for(int i = 1; i < parent.getChildCount(); i+=2){
			View placeRow = parent.getChildAt(i);
			placeRow.setBackgroundColor(Color.parseColor("#e9e2d8"));
		}
	}
	
}
