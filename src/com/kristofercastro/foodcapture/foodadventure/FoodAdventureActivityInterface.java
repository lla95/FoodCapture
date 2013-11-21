package com.kristofercastro.foodcapture.foodadventure;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Used so we can re-use fragments on different Food Adventure related activities.
 * Fixes problem of sharing data without casting to a concrete type
 * @author Kier
 *
 */
public interface FoodAdventureActivityInterface {

	public HashMap<Integer,Marker> getMarkers();
	public ArrayList<Place> getPlaces();
	public GoogleMap getGoogleMaps();
}
