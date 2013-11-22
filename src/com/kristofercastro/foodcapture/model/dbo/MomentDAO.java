package com.kristofercastro.foodcapture.model.dbo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kristofercastro.foodcapture.model.FoodAdventure;
import com.kristofercastro.foodcapture.model.MenuItem;
import com.kristofercastro.foodcapture.model.Moment;
import com.kristofercastro.foodcapture.model.Restaurant;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MomentTable;

/**
 * Data Access Object for the Moment table.  Supports CRUD operations.
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class MomentDAO extends DataAccessObject<Moment>{
	SQLiteOpenHelper dbHelper;

	public MomentDAO(SQLiteOpenHelper dbHelper){
		this.db = dbHelper.getWritableDatabase();
		this.dbHelper = dbHelper;
	}

	@Override
	public long create(Moment moment) {
		ContentValues values = new ContentValues();
		
		// put the values belonging to the moment
		values.put(MomentTable.COL_PRICE_RATING, moment.getPriceRating());
		values.put(MomentTable.COL_QUALITY_RATING, moment.getQualityRating());
		values.put(MomentTable.COL_DESCRIPTION, moment.getDescription());
		values.put(MomentTable.COL_DATE, moment.getDate());
		
		long restaurantID = 0;
		RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);
		String restaurantName = moment.getRestaurant().getName();
		
		boolean restaurantExists = false;
		
		// find the restaurant if it exists.
		ArrayList<Restaurant> restaurantList = restaurantDAO.retrieveAll();
		for(Restaurant restaurant : restaurantList){
			if (restaurant.getName().equalsIgnoreCase(restaurantName)){
				restaurantID = restaurant.getId();
				restaurantExists = true;
			}
		}
		if ( !restaurantExists )
			restaurantID = restaurantDAO.create(moment.getRestaurant());
		
		MenuItemDAO menuItemDAO = new MenuItemDAO(dbHelper);
		
		long menuItemID = 0;
		if (moment.getMenuItem() != null){
			menuItemID = menuItemDAO.create(moment.getMenuItem());
		}
		
		// add the id's of the foreign keys from Restaurant and Menu Item DB Tables
		values.put(MomentTable.COL_RESTAURANT_ID, restaurantID);
		values.put(MomentTable.COL_MENU_ITEM_ID, menuItemID);
		
		if (moment.getFoodAdventure() != null){
			values.put(MomentTable.COL_FOOD_ADVENTURE_ID, moment.getFoodAdventure().getId());
		}
		
		return db.insert(MomentTable.TABLE_NAME, null, values);
	}

	@Override
	public boolean delete(long id) {
		return db.delete(MomentTable.TABLE_NAME, MomentTable.COL_ID + "=" + id, null) > 0;
	}

	@Override
	public int update(Moment moment) {
		ContentValues values = new ContentValues();
		values.put(MomentTable.COL_PRICE_RATING, moment.getPriceRating());
		values.put(MomentTable.COL_QUALITY_RATING, moment.getQualityRating());
		values.put(MomentTable.COL_DESCRIPTION, moment.getDescription());

		long restaurantID = moment.getRestaurant().getId();
		RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);
		Restaurant restaurant = moment.getRestaurant();
		String restaurantName = restaurant.getName();
		
		boolean restaurantExists = false;
		
		// find the restaurant if it exists.
		ArrayList<Restaurant> restaurantList = restaurantDAO.retrieveAll();
		for(Restaurant r : restaurantList){
			if (r.getName().equalsIgnoreCase(restaurantName)){
				restaurantID = r.getId();
				restaurantExists = true;
			}
		}
			
		if ( !restaurantExists ){
			restaurantID = restaurantDAO.create(moment.getRestaurant());
		}else{
			// this doesn't actually return the id of the restaurant
			restaurantDAO.update(moment.getRestaurant());
		}
		
		MenuItemDAO menuItemDAO = new MenuItemDAO(dbHelper);
		Log.i("MyCameraApp", "file: " + moment.getMenuItem().getImagePath());
		menuItemDAO.update(moment.getMenuItem());
		
		// add the id's of the foreign keys from Restaurant and Menu Item DB Tables
		// add the id's of the foreign keys from Restaurant and Menu Item DB Tables
		values.put(MomentTable.COL_RESTAURANT_ID, restaurantID);
		values.put(MomentTable.COL_MENU_ITEM_ID, moment.getMenuItem().getId());
		
		return db.update(MomentTable.TABLE_NAME, values, MomentTable.COL_ID + "=" + moment.getId(), null);
	}
	@Override
	public Moment retrieve(long id) {
		String selectQuery = "SELECT * FROM " + MomentTable.TABLE_NAME + " WHERE " + MomentTable.COL_ID +"='" + id + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		Moment moment = null;
		try{
			if (cursor.moveToFirst()){
				moment = new Moment();
				moment.setId(cursor.getLong(0));
				moment.setPriceRating(cursor.getInt(1));
				moment.setQualityRating(cursor.getInt(2));
	
				RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);
				Restaurant restaurant = restaurantDAO.retrieve(cursor.getLong(3));
				moment.setRestaurant(restaurant);
				
				MenuItemDAO menuItemDAO = new MenuItemDAO(dbHelper);
				MenuItem menuItem = menuItemDAO.retrieve(cursor.getLong(4));
				moment.setMenuItem(menuItem);
				moment.setDescription(cursor.getString(5));
				moment.setDate(cursor.getString(6));
				
				FoodAdventureDAO foodAdventureDAO = new FoodAdventureDAO(dbHelper);
				FoodAdventure foodAdventure = foodAdventureDAO.retrieve(cursor.getLong(7));
				moment.setFoodAdventure(foodAdventure);
			}
		}finally{
			cursor.close();
		}
		
		return moment;
	}

	@Override
	public ArrayList<Moment> retrieveAll() {
		ArrayList<Moment> momentsList = new ArrayList<Moment>();
		String selectQuery = "SELECT * FROM " + MomentTable.TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		try{
		if (cursor.moveToFirst()){
			do{
				Moment moment = new Moment();
				moment.setId(cursor.getLong(0));
				moment.setPriceRating(cursor.getInt(1));
				moment.setQualityRating(cursor.getInt(2));
				
				RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);
				Restaurant restaurant = restaurantDAO.retrieve(cursor.getLong(3));
				moment.setRestaurant(restaurant);
				
				MenuItemDAO menuItemDAO = new MenuItemDAO(dbHelper);
				MenuItem menuItem = menuItemDAO.retrieve(cursor.getLong(4));
				moment.setMenuItem(menuItem);
				
				moment.setDescription(cursor.getString(5));
				moment.setDate(cursor.getString(6));
				
				FoodAdventureDAO foodAdventureDAO = new FoodAdventureDAO(dbHelper);
				FoodAdventure foodAdventure = foodAdventureDAO.retrieve(cursor.getLong(7));
				moment.setFoodAdventure(foodAdventure);
				
				momentsList.add(moment);
			}while(cursor.moveToNext());
		}
		}finally{
			cursor.close();
		}
		
		return momentsList;
	}
}
