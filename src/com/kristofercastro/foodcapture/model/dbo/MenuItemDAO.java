package com.kristofercastro.foodcapture.model.dbo;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.model.MenuItem;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MenuItemTable;
import com.kristofercastro.foodcapture.model.dbo.DBHelper.MomentTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Data Access Object for the Menu Item table.  Supports CRUD operations.
 * @author Kristofer Castro
 * @date 10/23/2013
 */
public class MenuItemDAO extends DataAccessObject<MenuItem> {
	
	public MenuItemDAO(SQLiteOpenHelper dbHelper){
		this.db = dbHelper.getWritableDatabase();
	}

	@Override
	public long create(MenuItem dbo) 
	{	
		MenuItem menuItem = (MenuItem) dbo;
		ContentValues values = new ContentValues();
		
		values.put(MenuItemTable.COL_NAME, menuItem.getName());
		values.put(MenuItemTable.COL_IMAGE_PATH, menuItem.getImagePath());
		
		return db.insert(MenuItemTable.TABLE_NAME, null, values);
	}

	@Override
	public boolean delete(long id) {
		return db.delete(MenuItemTable.TABLE_NAME, MenuItemTable.COL_ID + "=" + id, null) > 0;
	}

	@Override
	public int update(MenuItem menuItem) {
		ContentValues values = new ContentValues();
		values.put(MenuItemTable.COL_NAME, menuItem.getName());
		values.put(MenuItemTable.COL_IMAGE_PATH, menuItem.getImagePath());
		return db.update(MenuItemTable.TABLE_NAME, values, MenuItemTable.COL_ID + "=" + menuItem.getId(), null);
	}

	@Override
	public MenuItem retrieve(long id) {
		String selectQuery = "SELECT * FROM " + MenuItemTable.TABLE_NAME 
				+ " WHERE " + MenuItemTable.COL_ID +"='" + id + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		MenuItem menuItem = null;
		if (cursor.moveToFirst()){
			menuItem = new MenuItem();
			menuItem.setId(cursor.getLong(0));
			menuItem.setName(cursor.getString(1));
			menuItem.setImagePath(cursor.getString(2));
		}
		return menuItem;
	}

	@Override
	public ArrayList<MenuItem> retrieveAll() {
		ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
		
		String selectQuery = "SELECT * FROM " + MenuItemTable.TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()){
			do{
				MenuItem menuItem = new MenuItem();
				menuItem.setId(cursor.getLong(0));
				menuItem.setName(cursor.getString(1));
				menuItem.setImagePath(cursor.getString(2));
				menuItemList.add(menuItem);
			}while(cursor.moveToNext());
		}
		
		return menuItemList;
	}
}
