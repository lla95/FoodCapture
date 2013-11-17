package com.kristofercastro.foodcapture.foodadventure;

import java.util.zip.Inflater;

import com.kristofercastro.foodcapture.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kristofercastro.foodcapture.R.layout;
import com.kristofercastro.foodcapture.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Menu;

public class FoodAdventuresPlacesFragment extends Fragment {
	
	

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	        return inflater.inflate(R.layout.activity_food_adventures_places_fragment, container, false);
	    }
}
