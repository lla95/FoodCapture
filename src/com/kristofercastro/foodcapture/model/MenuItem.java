package com.kristofercastro.foodcapture.model;

/**
 * Business Object that represents the menu item inside a Restaurants Menu
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class MenuItem {
	long id;
	String name;
	String imagePath;
	
	public MenuItem(String name, String imagePath){
		this.name = name;
		this.imagePath = imagePath;
	}
	
	public MenuItem() {}

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
}
