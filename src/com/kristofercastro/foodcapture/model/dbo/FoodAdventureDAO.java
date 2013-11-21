package com.kristofercastro.foodcapture.model.dbo;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.model.FoodAdventure;
import com.kristofercastro.foodcapture.model.MenuItem;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.FoodAdventureTable;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MenuItemTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

public class FoodAdventureDAO extends DataAccessObject<FoodAdventure> {

	public FoodAdventureDAO(SQLiteOpenHelper dbHelper){
		this.db = dbHelper.getWritableDatabase();
	}
	
	@Override
	public long create(FoodAdventure dbo) {
		
		FoodAdventure foodAdventure = (FoodAdventure) dbo;
		ContentValues values = new ContentValues();
		values.put(FoodAdventureTable.COL_NAME, foodAdventure.getName());
		values.put(FoodAdventureTable.COL_DATE, foodAdventure.getDate());
		return db.insert(FoodAdventureTable.TABLE_NAME, null, values);
	}
	
	@Override
	public boolean delete(long id) {
		return db.delete(FoodAdventureTable.TABLE_NAME, FoodAdventureTable.COL_ID + "=" + id, null) > 0;
	}

	@Override
	public int update(FoodAdventure foodAdventure) {
		ContentValues values = new ContentValues();
		values.put(FoodAdventureTable.COL_NAME,foodAdventure.getName());
		values.put(FoodAdventureTable.COL_DATE, foodAdventure.getDate());
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
					foodAdventureList.add(foodAdventure);
				}while(cursor.moveToNext());
			}
		}finally{
			cursor.close();
		}
		
		return foodAdventureList;
	}
	
	public void 

	
}
