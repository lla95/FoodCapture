package com.kristofercastro.foodcapture.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Business Object that represents the food daventure table
 * In addition, it also contains the list of moments.
 * @author Kristofer Castro
 * @date 11/28/2013
 */
public class FoodAdventure implements Parcelable{
	long id;
	String name;
	String date;
	ArrayList<Moment> moments;
	
	public FoodAdventure(){}

	public FoodAdventure(Parcel source) {
		this.id = source.readLong();
		this.name = source.readString();
		this.date = source.readString();
		source.readTypedList(moments, Moment.CREATOR);
	}

	public long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<Moment> getMoments() {
		return moments;
	}
	
	public void setMoments(ArrayList<Moment> moments) {
		this.moments = moments;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.getName());
		dest.writeString(this.getDate());
		dest.writeTypedList(this.moments);
	}	
	
	public static Parcelable.Creator<FoodAdventure> CREATOR = new Parcelable.Creator<FoodAdventure>(){
		@Override
		public FoodAdventure createFromParcel(Parcel source) {
			return new FoodAdventure(source);
		}

		@Override
		public FoodAdventure[] newArray(int size) {
			return new FoodAdventure[size];
		}
	};
}
