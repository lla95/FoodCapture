package com.kristofercastro.foodcapture.model;

import com.kristofercastro.foodcapture.foodadventure.Place;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Business Object that represents a Restaurant
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class Restaurant implements Parcelable {
	long id;
	String name;
	float latitude;
	float longitude;
	
	public Restaurant(String name, double latitude, double longitude){
		this.name = name;
		this.latitude = (float) latitude;
		this.longitude = (float) longitude;
	}
	
	public Restaurant(){}
	
	public Restaurant(Parcel source) {
		id = source.readLong();
		name = source.readString();
		latitude = source.readFloat();
		longitude = source.readFloat();
	}

/***************************************
 *
 * GETTER AND SETTER METHODS FOR FIELDS
 * 
 ***************************************/
	public long getId() {
		return id;
	}

	public void setId(long l) {
		this.id = l;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeFloat(latitude);
		dest.writeFloat(longitude);	
	}
	
	public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>(){

		@Override
		public Restaurant createFromParcel(Parcel source) {
			return new Restaurant(source);
		}

		@Override
		public Restaurant[] newArray(int size) {
			return new Restaurant[size];
		}
		
	};
	
}
