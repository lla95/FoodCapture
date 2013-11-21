package com.kristofercastro.foodcapture.model.dbo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Contains the database schema and SQLiteOpenHelper methods that setupts the backend database
 * @author Kristofer Ken Castro
 * @date 10/23/2013
 */
public final class DBHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "FoodCaptureDB";
	private static final int DATABASE_VERSION = 3;
	SQLiteDatabase db;
	
	public static abstract class MenuItemTable implements BaseColumns{
		public static final String TABLE_NAME = "menu_item";
		public static final String COL_ID = BaseColumns._ID;
		public static final String COL_NAME = "name";
		public static final String COL_IMAGE_PATH = "imagePath";
		private static final String TABLE_CREATE = 
				String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"%s TEXT, %s TEXT)", TABLE_NAME, COL_ID, COL_NAME, COL_IMAGE_PATH);
	}
	
	public static abstract class RestaurantTable implements BaseColumns{
		public static final String TABLE_NAME = "restaurant";
		public static String COL_ID = BaseColumns._ID;
		public static String COL_NAME = "name";
		public static String COL_GPS_LAT = "latitude";
		public static String COL_GPS_LON = "longitude";
		private static final String TABLE_CREATE = 
				String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"%s TEXT, %s REAL, %s REAL)", TABLE_NAME, COL_ID, COL_NAME, 
						COL_GPS_LAT, COL_GPS_LON);
	}
	
	public static abstract class MomentTable implements BaseColumns{
		public static final String TABLE_NAME = "moment";
		public static final String COL_ID = BaseColumns._ID;
		public static final String COL_PRICE_RATING = "priceRating";
		public static final String COL_QUALITY_RATING = "qualityRating";
		public static final String COL_RESTAURANT_ID = "restaurantId";
		public static final String COL_MENU_ITEM_ID = "menuItemId";
		public static final String COL_DESCRIPTION = "description";
		public static final String COL_DATE = "date";
		private static final String TABLE_CREATE = 
				String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"%s INTEGER, %s INTEGER,%s INTEGER, %s INTEGER, %s TEXT, %s TEXT, " +
						
						String.format("FOREIGN KEY(%s) REFERENCES %s(%s), ", COL_RESTAURANT_ID, 
								RestaurantTable.TABLE_NAME, RestaurantTable.COL_ID ) +
						String.format("FOREIGN KEY(%s) REFERENCES %s(%s)", COL_MENU_ITEM_ID,
								MenuItemTable.TABLE_NAME, MenuItemTable.COL_ID) +
						")", TABLE_NAME, COL_ID, 
						COL_PRICE_RATING, COL_QUALITY_RATING, COL_RESTAURANT_ID, COL_MENU_ITEM_ID, COL_DESCRIPTION, COL_DATE);
	}
	
	public static abstract class FoodAdventureTable implements BaseColumns{
		public static final String TABLE_NAME = "food_adventure";
		public static final String COL_ID = BaseColumns._ID;
		public static final String COL_NAME = "name";
		public static final String COL_DATE = "date_started";
		
		private static final String TABLE_CREATE = 
				String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"%s TEXT, %s TEXT)", FoodAdventureTable.TABLE_NAME, FoodAdventureTable.COL_ID, 
						FoodAdventureTable.COL_NAME, FoodAdventureTable.COL_DATE); 
	}
	
	public DBHelper(Context context){
		// grab database and create it for us
		super(context, DBHelper.DATABASE_NAME , null, DATABASE_VERSION);
		//Log.i("MyCameraApp", TABLE_CREATE);
	}
	
	@Override 
	public void onCreate(SQLiteDatabase db){
		// create table in the database
		db.execSQL(MenuItemTable.TABLE_CREATE);
		db.execSQL(RestaurantTable.TABLE_CREATE);	
		db.execSQL(MomentTable.TABLE_CREATE);
		db.execSQL(FoodAdventureTable.TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MenuItemTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + RestaurantTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MomentTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FoodAdventureTable.TABLE_NAME);
		onCreate(db);
	}
	
	
}
