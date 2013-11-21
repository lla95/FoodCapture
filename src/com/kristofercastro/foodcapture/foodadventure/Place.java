package com.kristofercastro.foodcapture.foodadventure;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Acts as a container for the data returned by Google Places API
 * @author Kristofer Castro
 * @date 11/7/2013
 */
public class Place implements Parcelable {

	private String id;
	private String icon;
	private String name;
	private double latitude;
	private double longitude;
	private String vicinity;
	private String phoneNumber;
	
	public Place(){};
	
	public Place(Parcel in){
		id = in.readString();
		icon = in.readString();
		name = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		vicinity = in.readString();
		phoneNumber = in.readString();
	}
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getVicinity() {
		return vicinity;
	}
	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}
	
	/**
	 * Parse the result from Places Web API into the Place object.
	 * Learn more about JSON key/value returned here:
	 * https://developers.google.com/places/documentation/details
	 * 
	 * @param placesJSONResult that is retrieved from Places Web API
	 * @return place object after data is extracted from the JSON
	 */
	public static Place parseJsonToPlaceObject(JSONObject placesJSONResult){
		try{
			Place newPlace = new Place();
			
			// location stuff
			JSONObject geometry = (JSONObject) placesJSONResult.get("geometry");
			JSONObject location = (JSONObject) geometry.get("location");
			newPlace.setLatitude((Double) location.get("lat"));
			newPlace.setLongitude((Double) location.get("lng"));
			
			// address stuff
			newPlace.setVicinity(placesJSONResult.get("vicinity").toString());
			
			// others
			newPlace.setId((String) placesJSONResult.get("id"));
			newPlace.setName(placesJSONResult.getString("name"));
			newPlace.setIcon(placesJSONResult.getString("icon"));
			return newPlace;
		}catch(JSONException ex){
			Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}	
	
	@Override
	public String toString(){
		return this.name + ", " + this.vicinity + ", latitude:" +  this.latitude 
				+ ", longitude:" + this.longitude;
		
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(icon);
		dest.writeString(name);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(vicinity);
		dest.writeString(phoneNumber);
		
	}
	
	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>(){

		@Override
		public Place createFromParcel(Parcel source) {
			return new Place(source);
		}

		@Override
		public Place[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Place[size];
		}
		
	};
}
