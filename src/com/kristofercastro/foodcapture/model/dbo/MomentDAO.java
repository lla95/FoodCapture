package com.kristofercastro.foodcapture.model.dbo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kristofercastro.foodcapture.model.MenuItem;
import com.kristofercastro.foodcapture.model.Moment;
import com.kristofercastro.foodcapture.model.Restaurant;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MomentTable;

/**
 * Data Access Object for the Moment table.  Supports CRUD operations.
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class MomentDAO{

	SQLiteDatabase db;
	SQLiteOpenHelper dbHelper;

	public MomentDAO(SQLiteOpenHelper dbHelper){
		this.db = dbHelper.getWritableDatabase();
		this.dbHelper = dbHelper;
	}
	
	public long createMoment(Moment moment){
		ContentValues values = new ContentValues();
		
		// put the values belonging to the moment
		values.put(MomentTable.COL_PRICE_RATING, moment.getPriceRating());
		values.put(MomentTable.COL_QUALITY_RATING, moment.getQualityRating());
		values.put(MomentTable.COL_DESCRIPTION, moment.getDescription());
		
		long restaurantID = 0;
		RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);
		String restaurantName = moment.getRestaurant().getName();
		
		boolean restaurantExists = false;
		
		// find the restaurant if it exists.
		ArrayList<Restaurant> restaurantList = restaurantDAO.getAllRestaurants();
		for(Restaurant restaurant : restaurantList){
			if (restaurant.getName().equalsIgnoreCase(restaurantName)){
				restaurantID = restaurant.getId();
				restaurantExists = true;
			}
		}
		if ( !restaurantExists )
			restaurantID = restaurantDAO.createRestaurant(moment.getRestaurant());
		
		MenuItemDAO menuItemDAO = new MenuItemDAO(dbHelper);
		long menuItemID = menuItemDAO.createMenuItem(moment.getMenuItem());
		
		// add the id's of the foreign keys from Restaurant and Menu Item DB Tables
		values.put(MomentTable.COL_RESTAURANT_ID, restaurantID);
		values.put(MomentTable.COL_MENU_ITEM_ID, menuItemID);
		
		return db.insert(MomentTable.TABLE_NAME, null, values);
	}

	public void deleteMoment(long id){
		String deleteQuery = "DELETE FROM " + MomentTable.TABLE_NAME + " where " + MomentTable.COL_ID + "='" + id + "'";
		db.rawQuery(deleteQuery, null);
	}
	
	public ArrayList<Moment> getAllMoments(){
		ArrayList<Moment> momentsList = new ArrayList<Moment>();
		String selectQuery = "SELECT * FROM " + MomentTable.TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()){
			do{
				Moment moment = new Moment();
				moment.setId(cursor.getLong(0));
				moment.setPriceRating(cursor.getInt(1));
				moment.setQualityRating(cursor.getInt(2));
				
				RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);
				Restaurant restaurant = restaurantDAO.getRestaurant(cursor.getLong(3));
				moment.setRestaurant(restaurant);
				
				MenuItemDAO menuItemDAO = new MenuItemDAO(dbHelper);
				MenuItem menuItem = menuItemDAO.getMenuItem(cursor.getLong(4));
				moment.setMenuItem(menuItem);
			}while(cursor.moveToNext());
		}
		
		return momentsList;
	}
	
	public int updateMoment(Moment moment){
		ContentValues values = new ContentValues();
		values.put(MomentTable.COL_PRICE_RATING, moment.getPriceRating());
		values.put(MomentTable.COL_QUALITY_RATING, moment.getQualityRating());
		values.put(MomentTable.COL_DESCRIPTION, moment.getDescription());
		return db.update(MomentTable.TABLE_NAME, values, MomentTable.COL_ID + "=" + moment.getId(), null);
	}

	public Moment getMoment(long id) {
		String selectQuery = "SELECT * FROM " + MomentTable.TABLE_NAME + " WHERE " + MomentTable.COL_ID +"='" + id + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		Moment moment = null;
		if (cursor.moveToFirst()){
			moment = new Moment();
			moment.setId(cursor.getLong(0));
			moment.setPriceRating(cursor.getInt(1));
			moment.setQualityRating(cursor.getInt(2));
			
			RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);
			Restaurant restaurant = restaurantDAO.getRestaurant(cursor.getLong(3));
			moment.setRestaurant(restaurant);
			
			MenuItemDAO menuItemDAO = new MenuItemDAO(dbHelper);
			MenuItem menuItem = menuItemDAO.getMenuItem(cursor.getLong(4));
			moment.setMenuItem(menuItem);
		}
		
		return moment;
	}
}
