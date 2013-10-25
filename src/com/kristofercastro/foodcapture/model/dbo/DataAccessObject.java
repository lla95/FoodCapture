package com.kristofercastro.foodcapture.model.dbo;

import java.util.ArrayList;

import com.kristofercastro.foodcapture.model.MenuItem;

import android.database.sqlite.SQLiteDatabase;

public abstract class DataAccessObject<T> {

	SQLiteDatabase db;
	
	public abstract long create(T object);
	
	public abstract boolean delete(long id);
	
	public abstract int update(T object);
	
	public abstract T retrieve(long id);
	
	public abstract ArrayList<T> retrieveAll();

}
