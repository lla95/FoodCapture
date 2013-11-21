package com.kristofercastro.foodcapture.foodadventure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kristofercastro.foodcapture.R;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Class that handles request to the Google Places Web API
 * Go to https://developers.google.com/places/documentation/search
 * information about how to use Google Places Service
 * 
 * @author Kristofer Castro
 * @date 11/7/2013
 */
public class GooglePlacesWebService {

	Context context;
	private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	
	// if you put final, reassignment is not possible
	private final String API_KEY;
	
	// keeps track of page tokens to use to view next page of results
	private String currentPageToken;
	private ArrayList<Place> placesPage1;
	private ArrayList<Place> placesPage2;
	private ArrayList<Place> placesPage3;
	private ArrayList<Place> resultPlaces;

	

	public GooglePlacesWebService(Context context){
		API_KEY = context.getString(R.string.BROWSER_API_KEY);
	}
	
	public ArrayList<Place> findPlaces(int maxNumberOfPlaces, double latitude, double longitude){
		placesPage1 = findPlacesHelper(latitude, longitude);
		placesPage2 = findPlacesHelper(latitude,longitude);
		placesPage3 = findPlacesHelper(latitude, longitude);
		resultPlaces = new ArrayList<Place>();
		placesPage1.addAll(placesPage2);
		placesPage1.addAll(placesPage3);

		int startOfChosenPlaces = randomlyChoosePlaces(placesPage1, 5);
	
		for (int i = startOfChosenPlaces; i < placesPage1.size(); i++){
			resultPlaces.add(placesPage1.get(i));
		}

		return resultPlaces;
	}
	
	/**
	 * Efficient way of choosing random places with O(1) space complexity
	 * @param placesArray
	 * @param maxResults
	 * @return the start of head index of chosen places
	 */
	public int randomlyChoosePlaces(ArrayList<Place> placesArray, int maxResults){
		int tailOffset = 0;
		int arraySize = placesArray.size();
		Random rand = new Random();
		for(int i = 0; i < maxResults; i++){
			int ranNumber = rand.nextInt(arraySize-tailOffset);

			// swap with tail
			Place temp = placesArray.get(ranNumber);
			placesArray.set(ranNumber, placesArray.get(tailOffset));
			placesArray.set(tailOffset, temp);
			tailOffset +=1;
		}
		
		return arraySize-tailOffset;
	}
	
	public ArrayList<Place> findPlacesHelper(double latitude, double longitude){
		ArrayList<Place> places = new ArrayList<Place>();
		String httpRequestUrl = generateUrl(latitude,longitude, "restaurant|food");

		try{
			String jsonString = getPlaceJSON(httpRequestUrl);
			JSONObject jsonResult = new JSONObject(jsonString); // turn the result into JSONObject 
			JSONArray jsonArray = jsonResult.getJSONArray("results");
			try{
				currentPageToken = (jsonResult.getString("next_page_token"));
			}catch(JSONException ex){
				currentPageToken = null;
			}
			for ( int i = 0 ; i < jsonArray.length(); i++){
				try{
					Place newPlace = Place.parseJsonToPlaceObject((JSONObject) jsonArray.get(i));
					places.add(newPlace);
				}catch( Exception e ){}
			}
			
			/*  introduce delay for next pages
				note: This may be a bad way to do this since it is dependent on the network.
				It can be the case where we don't wait a sufficient enough time inorder
				to retrieve the next page of results.
			*/
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}catch (JSONException ex){
			Logger.getLogger(GooglePlacesWebService.class.getName()).log(Level.SEVERE,
				     null, ex);
			Log.i("MyCameraApp", ex.toString());

		}		
		return places;
	}

	/**
	 * Randomly places random restaurants at the end of the lists
	 * @return
	 */
	private void randomPlaces(JSONArray placesArray, int numPlacesNeeded){
		int maxSize = placesArray.length();
		int tailOffset = 0;
		int numGeneratedPlaces = 0;
		Random rand = new Random();
		while ( numGeneratedPlaces != numPlacesNeeded ){
			
			// pick rand place from 0 to tail-offset, put the chosen one at the end of the list
			int ranNumber = rand.nextInt(maxSize-tailOffset);
			Log.i("MyCameraApp", ranNumber +" ::Size: " + placesArray.length());
			try {
				JSONObject randObject = (JSONObject) placesArray.get(ranNumber);
				JSONObject lastNonVisitedObject = (JSONObject) placesArray.get(maxSize-tailOffset);
				JSONObject tempObject = lastNonVisitedObject;
				
				placesArray.put(maxSize-tailOffset, randObject);
				placesArray.put(ranNumber, tempObject);
				
				lastNonVisitedObject = randObject;
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			numGeneratedPlaces++;
			tailOffset++;
		}
	}
	
	/**
	 * 
	 * Retrieve the JSON result from the server and put it in a string
	 * @param httpRequestUrl the http request 
	 * @return
	 */
	private String getPlaceJSON(String httpRequestUrl) {
		StringBuilder content = new StringBuilder();
		
		try {
			URL url = new URL(httpRequestUrl);
			URLConnection connection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			
			String currentLine;
			while ( (currentLine = bufferedReader.readLine()) != null){
				content.append(currentLine+"\n");
			}
			bufferedReader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content.toString();
	}
	
	/**
	 * Creates the properly formatted HTTP request URL to be sent
	 * to the Google Places API
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private String generateUrl(double latitude, double longitude, String placeType){
		StringBuilder urlString = new StringBuilder(BASE_URL);
		
		urlString.append("&location=");
		urlString.append(latitude).append(",").append(longitude);
		urlString.append("&radius=7500");
		urlString.append("&types="+placeType);
		if (currentPageToken != null && !currentPageToken.isEmpty()){
			urlString.append("&pagetoken="+currentPageToken);
		}
		urlString.append("&sensor=true").append("&key=" + API_KEY);
		return urlString.toString();
	}	
}
