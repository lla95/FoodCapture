package com.kristofercastro.foodcapture.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Business Object an instance of a moment in the "Capture the Moment" feature
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class Moment implements Parcelable {
	long id;
	Restaurant restaurant;
	MenuItem menuItem;
	int priceRating;
	int qualityRating;
	String description;
	String date;
	
	public Moment(int id, int priceRating, int qualityRating, Restaurant restaurant, MenuItem menuItem, String description, String date){
		this.id = id;
		this.priceRating = priceRating;
		this.qualityRating = qualityRating;
		this.restaurant = restaurant;
		this.menuItem = menuItem;
		this.description = description;
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Moment() {
		// TODO Auto-generated constructor stub

	}
	
	public Moment(Parcel source) {
		id = source.readLong();
		priceRating = source.readInt();
		qualityRating = source.readInt();
		restaurant = source.readParcelable(Restaurant.class.getClassLoader());
		menuItem = source.readParcelable(MenuItem.class.getClassLoader());
		description = source.readString();
		date = source.readString();
	}

/***************************************
 *
 * GETTER AND SETTER METHODS FOR FIELDS
 * 
 ***************************************/
	public Long getId() {
		return id;
	}

	public void setId(long l) {
		this.id = l;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public int getPriceRating() {
		return priceRating;
	}

	public void setPriceRating(int priceRating) {
		this.priceRating = priceRating;
	}

	public int getQualityRating() {
		return qualityRating;
	}

	public void setQualityRating(int qualityRating) {
		this.qualityRating = qualityRating;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeParcelable(restaurant, 0);
		dest.writeParcelable(menuItem, 0);
		dest.writeInt(priceRating);
		dest.writeInt(qualityRating);
		dest.writeString(description);
		dest.writeString(date);
	}
	
	public static Parcelable.Creator<Moment> CREATOR = new Parcelable.Creator<Moment>(){

		@Override
		public Moment createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Moment(source);
		}

		@Override
		public Moment[] newArray(int size) {
			return new Moment[size];
		}
		
	};
}
