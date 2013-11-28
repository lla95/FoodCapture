package com.kristofercastro.foodcapture.model.dbo;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.model.FoodAdventure;
import com.kristofercastro.foodcapture.model.MenuItem;
import com.kristofercastro.foodcapture.model.Moment;
import com.kristofercastro.foodcapture.model.Restaurant;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.FoodAdventureTable;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MenuItemTable;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MomentTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FoodAdventureDAO extends DataAccessObject<FoodAdventure> {

	private SQLiteOpenHelper dbHelper;

	public FoodAdventureDAO(SQLiteOpenHelper dbHelper){
		this.db = dbHelper.getWritableDatabase();
		this.dbHelper = dbHelper;

	}
	
	@Override
	public long create(FoodAdventure dbo) {
		
		FoodAdventure foodAdventure = (FoodAdventure) dbo;
		ContentValues values = new ContentValues();
		values.put(FoodAdventureTable.COL_NAME, foodAdventure.getName());
		values.put(FoodAdventureTable.COL_DATE, foodAdventure.getDate());
		Long rowID = db.insert(FoodAdventureTable.TABLE_NAME, null, values);
		
		foodAdventure = this.retrieve(rowID);
		if ( rowID != -1){
			ArrayList<Moment> momentsArray = dbo.getMoments();
			MomentDAO momentDAO = new MomentDAO(dbHelper);
			for(Moment moment : momentsArray){
				moment.setFoodAdventureID(foodAdventure.getId());
				momentDAO.create(moment);
			}
		}
		return rowID;
	}
	
	@Override
	public boolean delete(long id) {
		FoodAdventure foodAdventure = retrieve(id);
		for ( Moment moment : foodAdventure.getMoments()){
			MomentDAO momentDAO = new MomentDAO(dbHelper);
			momentDAO.delete(moment.getId());
		}
		return db.delete(FoodAdventureTable.TABLE_NAME, FoodAdventureTable.COL_ID + "=" + id, null) > 0;
	}

	@Override
	public int update(FoodAdventure foodAdventure) {
		ContentValues values = new ContentValues();
		values.put(FoodAdventureTable.COL_NAME,foodAdventure.getName());
		//values.put(FoodAdventureTable.COL_DATE, foodAdventure.getDate());
		return db.update(FoodAdventureTable.TABLE_NAME, values, FoodAdventureTable.COL_ID + "=" + foodAdventure.getId() , null);
	}

	@Override
	public FoodAdventure retrieve(long id) {
		String selectQuery = "SELECT * FROM " + FoodAdventureTable.TABLE_NAME 
				+ " WHERE " + FoodAdventureTable.COL_ID +"='" + id + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		FoodAdventure foodAdventure = null;
		try{
			if (cursor.moveToFirst()){
				foodAdventure = new FoodAdventure();
				foodAdventure.setId(cursor.getLong(0));
				foodAdventure.setName(cursor.getString(1));
				foodAdventure.setDate(cursor.getString(2));
				foodAdventure.setMoments(retrieveMoments(foodAdventure));
			}
		}finally{
			cursor.close();
		}
		
		return foodAdventure;
	}

	@Override
	public ArrayList<FoodAdventure> retrieveAll() {
		ArrayList<FoodAdventure> foodAdventureList = new ArrayList<FoodAdventure>();
		String selectQuery = "SELECT * FROM " + FoodAdventureTable.TABLE_NAME;
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()){
				do{
					FoodAdventure foodAdventure = new FoodAdventure();
					foodAdventure.setId(cursor.getLong(0));
					foodAdventure.setName(cursor.getString(1));
					foodAdventure.setDate(cursor.getString(2));
					
					foodAdventure.setMoments(retrieveMoments(foodAdventure));
					foodAdventureList.add(foodAdventure);
					
				}while(cursor.moveToNext());
			}
		}finally{
			cursor.close();
		}
		
		return foodAdventureList;
	}
	
	/**
	 * Returns all moments that the adventures generated that need to be reviewed
	 * @param foodAdventure
	 * @return
	 */
	public ArrayList<Moment> retrieveMoments(FoodAdventure foodAdventure){
		ArrayList<Moment> result = new ArrayList<Moment>();
		
		String selectColumns = 
				MomentTable.TABLE_NAME + "." + MomentTable.COL_ID + "," 
				+ MomentTable.TABLE_NAME + "." + MomentTable.COL_PRICE_RATING + ","
				+ MomentTable.TABLE_NAME + "." + MomentTable.COL_QUALITY_RATING + ","
				+ MomentTable.TABLE_NAME + "." + MomentTable.COL_RESTAURANT_ID + ","
				+ MomentTable.TABLE_NAME + "." + MomentTable.COL_MENU_ITEM_ID + ","
				+ MomentTable.TABLE_NAME + "." + MomentTable.COL_DESCRIPTION + ","
				+ MomentTable.TABLE_NAME + "." + MomentTable.COL_DATE + ","
				+ MomentTable.TABLE_NAME + "." + MomentTable.COL_FOOD_ADVENTURE_ID;
		String selectFrom = MomentTable.TABLE_NAME + " ";
		String selectInner = "INNER JOIN " + FoodAdventureTable.TABLE_NAME + " ";
		String selectOn = "ON " + FoodAdventureTable.TABLE_NAME + "." + FoodAdventureTable.COL_ID
			+ "=" 
			+ MomentTable.TABLE_NAME + "." + MomentTable.COL_FOOD_ADVENTURE_ID;
		String selectWhere = " WHERE " + FoodAdventureTable.TABLE_NAME + "." + FoodAdventureTable.COL_ID + "=" + foodAdventure.getId();
		String selectQuery = 
				"SELECT " + selectColumns + " FROM " + selectFrom + selectInner + selectOn + selectWhere;
		//Log.i("MyCameraApp",selectQuery);
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
					
					FoodAdventure foodAdventureResult = foodAdventure;
					moment.setFoodAdventureID(foodAdventureResult.getId());
					
					result.add(moment);
				}while(cursor.moveToNext());
			}
			}finally{
				cursor.close();
			}
		
		return result;
	}

	
}
