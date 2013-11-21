package com.kristofercastro.foodcapture.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Business Object that represents the menu item inside a Restaurants Menu
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class MenuItem implements Parcelable {
	long id;
	String name;
	String imagePath;
	
	public MenuItem(String name, String imagePath){
		this.name = name;
		this.imagePath = imagePath;
	}
	
	public MenuItem() {}

	public MenuItem(Parcel source) {
		id = source.readLong();
		name = source.readString();
		imagePath = source.readString();
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

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
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
		dest.writeString(imagePath);
	}
	
	public static Parcelable.Creator<MenuItem> CREATOR = new Parcelable.Creator<MenuItem>(){

		@Override
		public MenuItem createFromParcel(Parcel source) {
			return new MenuItem(source);
		}

		@Override
		public MenuItem[] newArray(int size) {
			return new MenuItem[size];
		}
		
	};
}
