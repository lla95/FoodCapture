package com.kristofercastro.foodcapture.model;

/**
 * Business Object that represents a Restaurant
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class Restaurant {
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
	
	
}
