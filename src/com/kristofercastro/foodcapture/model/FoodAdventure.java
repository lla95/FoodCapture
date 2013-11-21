package com.kristofercastro.foodcapture.model;

import java.util.ArrayList;

public class FoodAdventure {
	Long id;
	String name;
	String date;
	ArrayList<Restaurant> restaurants;
	ArrayList<Moment> moments;
	
	public FoodAdventure(){}

	public Long getId() {
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

	public ArrayList<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(ArrayList<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public ArrayList<Moment> getMoments() {
		return moments;
	}
	
	public void setMoments(ArrayList<Moment> moments) {
		this.moments = moments;
	}	
}
