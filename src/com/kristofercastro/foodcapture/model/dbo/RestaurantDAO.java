package com.kristofercastro.foodcapture.model.dbo;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.model.Restaurant;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MenuItemTable;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.RestaurantTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Data Access Object for the Restaurant table. Supports CRUD operations.
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class RestaurantDAO extends DataAccessObject{

	public RestaurantDAO(SQLiteOpenHelper dbHelper){
		this.db = dbHelper.getWritableDatabase();
	}
	/*
	public long createRestaurant(Restaurant restaurant){
		ContentValues values = new ContentValues();
		
		values.put(RestaurantTable.COL_NAME, restaurant.getName());
		values.put(RestaurantTable.COL_GPS_LAT, restaurant.getLatitude());
		values.put(RestaurantTable.COL_GPS_LON, restaurant.getLongitude());		
		return db.insert(RestaurantTable.TABLE_NAME, null, values);
	}
	
	public ArrayList<Restaurant> getAllRestaurants(){
		
		ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
		
		String selectQuery = "SELECT * FROM " + RestaurantTable.TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);
				
		if(cursor.moveToFirst()){
			do{
				Restaurant restaurant = new Restaurant();
				restaurant.setId(cursor.getLong(0));
				restaurant.setName(cursor.getString(1));
				restaurant.setLatitude(cursor.getFloat(2));
				restaurant.setLongitude(cursor.getFloat(3));
				restaurantList.add(restaurant);
			}while(cursor.moveToNext());
		}
		
		return restaurantList;
	}
	
	public void deleteRestaurant(long id){
		String deleteQuery = "DELETE FROM " + RestaurantTable.TABLE_NAME + " where " + RestaurantTable.COL_ID + "='" + id + "'";
		db.execSQL(deleteQuery);				
	}
	
	public int updateRestaurant(Restaurant restaurant){
		// Need to open database for reading/writing
		
		ContentValues values = new ContentValues();
		values.put(RestaurantTable.COL_NAME, restaurant.getName());
		values.put(RestaurantTable.COL_GPS_LAT, restaurant.getLatitude());
		values.put(RestaurantTable.COL_GPS_LON, restaurant.getLongitude());
		
		return db.update(RestaurantTable.TABLE_NAME, values, RestaurantTable.COL_ID + "=" + restaurant.getId(), null);
	}
	
	public Restaurant getRestaurant(long id){
		String selectQuery = "SELECT * FROM " + RestaurantTable.TABLE_NAME +
				" WHERE " + RestaurantTable.COL_ID + "='" + id + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		Restaurant restaurant = null;
		if(cursor.moveToFirst()){
			restaurant = new Restaurant();
			restaurant.setId(cursor.getLong(0));
			restaurant.setName(cursor.getString(1));
			restaurant.setLatitude(cursor.getFloat(2));
			restaurant.setLongitude(cursor.getFloat(3));
		}
		return restaurant;
	}
*/
	@Override
	public long create(Object object) {
		Restaurant restaurant = (Restaurant) object;
		ContentValues values = new ContentValues();
		
		values.put(RestaurantTable.COL_NAME, restaurant.getName());
		values.put(RestaurantTable.COL_GPS_LAT, restaurant.getLatitude());
		values.put(RestaurantTable.COL_GPS_LON, restaurant.getLongitude());		
		return db.insert(RestaurantTable.TABLE_NAME, null, values);
	}

	@Override
	public boolean delete(long id) {
		return db.delete(RestaurantTable.TABLE_NAME, RestaurantTable.COL_ID + "=" + id, null) > 0;
	}

	@Override
	public int update(Object object) {
		Restaurant restaurant = (Restaurant) object;
		
		ContentValues values = new ContentValues();
		values.put(RestaurantTable.COL_NAME, restaurant.getName());
		values.put(RestaurantTable.COL_GPS_LAT, restaurant.getLatitude());
		values.put(RestaurantTable.COL_GPS_LON, restaurant.getLongitude());
		
		return db.update(RestaurantTable.TABLE_NAME, values, RestaurantTable.COL_ID + "=" + restaurant.getId(), null);
	}

	@Override
	public Restaurant retrieve(long id) {
		String selectQuery = "SELECT * FROM " + RestaurantTable.TABLE_NAME +
				" WHERE " + RestaurantTable.COL_ID + "='" + id + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		Restaurant restaurant = null;
		if(cursor.moveToFirst()){
			restaurant = new Restaurant();
			restaurant.setId(cursor.getLong(0));
			restaurant.setName(cursor.getString(1));
			restaurant.setLatitude(cursor.getFloat(2));
			restaurant.setLongitude(cursor.getFloat(3));
		}
		return restaurant;
	}

	@Override
	public ArrayList<Restaurant> retrieveAll() {
		ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
		
		String selectQuery = "SELECT * FROM " + RestaurantTable.TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);
				
		if(cursor.moveToFirst()){
			do{
				Restaurant restaurant = new Restaurant();
				restaurant.setId(cursor.getLong(0));
				restaurant.setName(cursor.getString(1));
				restaurant.setLatitude(cursor.getFloat(2));
				restaurant.setLongitude(cursor.getFloat(3));
				restaurantList.add(restaurant);
			}while(cursor.moveToNext());
		}
		
		return restaurantList;
	}
}
