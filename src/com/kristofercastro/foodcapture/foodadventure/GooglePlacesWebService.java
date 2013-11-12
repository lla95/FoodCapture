package com.kristofercastro.foodcapture.foodadventure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kristofercastro.foodcapture.R;

import android.content.Context;
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
	
	public GooglePlacesWebService(Context context){
		API_KEY = context.getString(R.string.BROWSER_API_KEY);
	}
	
	public ArrayList<Place> findPlaces(int numberOfPlaces, double latitude, double longitude){
		ArrayList<Place> places = new ArrayList<Place>();
		
		String httpRequestUrl = generateUrl(latitude,longitude, "restaurant");
		//Log.i("MyCameraApp", httpRequestUrl);

		try{
			String jsonString = getPlaceJSON(httpRequestUrl);
			JSONObject jsonResult = new JSONObject(jsonString); // turn the result into JSONObject 
			JSONArray jsonArray = jsonResult.getJSONArray("results");
			Log.i("MyCameraApp", "array size: " + jsonArray.length());
			
			int maxResults = jsonArray.length();
			if ( jsonArray.length() > numberOfPlaces)
				maxResults = numberOfPlaces;
			
			for ( int i = 0 ; i < maxResults; i++){
				try{
					Place newPlace = Place.parseJsonToPlaceObject((JSONObject) jsonArray.get(i));
					places.add(newPlace);
				}catch( Exception e ){}
			}
			
		}catch (JSONException ex){
			Logger.getLogger(GooglePlacesWebService.class.getName()).log(Level.SEVERE,
				     null, ex);
			Log.i("MyCameraApp", ex.toString());

		}
		
		return places;
	}
	
	/**
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
		urlString.append("&radius=50000");
		urlString.append("&types="+placeType);
		urlString.append("&sensor=true").append("&key=" + API_KEY);
		return urlString.toString();
	}
	
	
}
