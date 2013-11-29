package com.kristofercastro.foodcapture.foodadventure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;

/**
 * A horizontal scroll view of places.  This fragment allows so that when you select one of the places
 * it focuses the place in google maps
 * @author Kristofer Castro
 *
 */
public class FoodAdventuresPlacesFragment extends Fragment {
	
	ArrayList<Place> places;
	ViewGroup parent;
	Place currentlySelectedPlace;
	
	ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		 View v = inflater.inflate(R.layout.activity_food_adventures_places_fragment, container, false);
		 // grab the local restaurants from the main activity
		 places = ((FoodAdventureActivityInterface) this.getActivity()).getPlaces();
		 
		 // Inflate the layout for this fragment
        return v;
    }
	
	@Override
	public void onStart() {
		super.onStart();
		displayPlaces();
		defaultPlaceSelected();
	}

	private void defaultPlaceSelected() {
		currentlySelectedPlace = places.get(0);
		notifyAllMyListeners();		
	}

	/**
	 * For each place in the places list, turn it into a UI View element we can interact with
	 */
	private void displayPlaces(){
		LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.adventures_list_layout);
				
		try{
			for (int i = 0 ; i < places.size(); i++){
				final Place place = places.get(i);
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
						
						if (currentlySelectedPlace == null || !currentlySelectedPlace.getName().equals(place.getName())){
							currentlySelectedPlace = place;
							notifyAllMyListeners();
						}
						
						// lets get the text view inside the layout
						TextView textView = (TextView) v.findViewById(R.id.placesIdentifierTextView);
						
						// the letter is the key for the hash map of markers so we extract that
						String letter = textView.getText().toString();
						
						// find the marker we are looking for that corresponds to the letter
						HashMap<Integer, Marker> markerHash = ((FoodAdventureActivityInterface) FoodAdventuresPlacesFragment
								.this.getActivity()).getMarkers();
						Marker marker = markerHash.get((int)(letter.charAt(0) - 65));		
						
						// make maps focus on that restaurant
						GoogleMap maps = ((FoodAdventureActivityInterface) FoodAdventuresPlacesFragment
								.this.getActivity()).getGoogleMaps();
						try{
							maps.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));								
							marker.showInfoWindow();	
						}catch(NullPointerException e){
							String err = (maps == null)? "Maps is null" : e.getMessage();
							err += "\t" + marker == null? "Marker is null" : e.getMessage();
							Log.e(getTag(), err);
						}
					}			
				});			
				// add to layout
				parent.addView(placesRow);
				}
			}catch(NullPointerException e){
				String err = (e.getMessage() == null)? "Places is null" : e.getMessage();
				Log.e("MyCameraApp", err);
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

	protected void notifyAllMyListeners() {
		
		for (PropertyChangeListener listener : this.listeners){
			listener.propertyChange(new PropertyChangeEvent(this, "currentPlace", null, this.currentlySelectedPlace));
		}
	}

	public Place getSelectedPlace() {
		// TODO Auto-generated method stub
		return currentlySelectedPlace;
	}
	
	public void addChangeListener(PropertyChangeListener newListener){
		listeners.add(newListener);
	}
	
	
}
