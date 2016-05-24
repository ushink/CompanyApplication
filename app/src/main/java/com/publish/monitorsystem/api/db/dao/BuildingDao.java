package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.publish.monitorsystem.api.bean.BuildingBean;
import com.publish.monitorsystem.api.bean.BuildingBean.Building;
import com.publish.monitorsystem.api.db.DBHelper;

public class BuildingDao{

	private DBHelper helper;

	private BuildingDao(Context context) {
		helper = new DBHelper(context);
	}

	public static BuildingDao instance;

	public synchronized static BuildingDao getInstance(Context context) {
		if (instance == null) {
			instance = new BuildingDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "building";

	public boolean addBuilding(Building building) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("BuildingID", building.BuildingID);
			values.put("BuildingName", building.BuildingName);
			database.insert(TABLE_NAME, null, values);
			flag = true;
		} catch (Exception e) {
			System.out.println("----addUserLog-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}
	
	public boolean deleteAllBuilding() {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			String sql = "delete from " + TABLE_NAME;
			database = helper.getWritableDatabase();
			database.execSQL(sql);
			flag = true;
		} catch (Exception e) {
			System.out.println("----deleteAllUserLog-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	public List<Building> getAllBuildingList() {
		List<Building> list = new ArrayList<Building>();
		BuildingBean bean = new BuildingBean();
		Building building;
		String sql = "select * from " + TABLE_NAME;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				building = bean.new Building();
				building.BuildingID = cursor.getString(cursor
						.getColumnIndex("BuildingID"));
				building.BuildingName = cursor.getString(cursor
						.getColumnIndex("BuildingName"));
				list.add(building);
				building = null;
			}
		} catch (Exception e) {
			System.out.println("----getAllUserLogList-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return list;
	}
	
	public int getSize(){
		int size = -1;
		SQLiteDatabase database = null;
		String sql = "select count(*) from " + TABLE_NAME;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			if(cursor.moveToNext()){
				size = cursor.getInt(0);
			}
		} catch (Exception e) {
			System.out.println("----getSize-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return size;
	}

}
