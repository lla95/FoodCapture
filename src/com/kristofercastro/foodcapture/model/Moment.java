package com.kristofercastro.foodcapture.model;
/**
 * Business Object an instance of a moment in the "Capture the Moment" feature
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class Moment {
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
}
