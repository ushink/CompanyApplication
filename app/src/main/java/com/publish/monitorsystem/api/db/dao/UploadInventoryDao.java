package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.publish.monitorsystem.api.bean.InventoryBean;
import com.publish.monitorsystem.api.bean.UploadInventory;
import com.publish.monitorsystem.api.bean.InventoryBean.Inventory;
import com.publish.monitorsystem.api.db.DBHelper;

public class UploadInventoryDao{

	private DBHelper helper;

	private UploadInventoryDao(Context context) {
		helper = new DBHelper(context);
	}

	public static UploadInventoryDao instance;

	public synchronized static UploadInventoryDao getInstance(Context context) {
		if (instance == null) {
			instance = new UploadInventoryDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "uploadinventory";

	public boolean addUploadInventory(UploadInventory inventory) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("InventoryID", inventory.InventoryID);
			values.put("ParentPlanID", inventory.ParentPlanID);
			values.put("UploadTime", inventory.UploadTime);
			database.insert(TABLE_NAME, null, values);
			flag = true;
		} catch (Exception e) {
			System.out.println("----addInventory-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	public boolean deleteAllUploadInventory() {
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
	
	public boolean updateUploadInventory(String ParentPlanID,String UploadTime) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("ParentPlanID", ParentPlanID);
			values.put("UploadTime", UploadTime);
			database.update(TABLE_NAME, values , null, null);
			flag = true;
		} catch (Exception e) {
			System.out.println("----updateUploadInventory-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}
	public List<UploadInventory> getAllUploadInventory() {
		List<UploadInventory> list = new ArrayList<UploadInventory>();
		UploadInventory uploadInventory;
		String sql = "select * from " + TABLE_NAME;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				uploadInventory = new UploadInventory();
				uploadInventory.InventoryID = cursor.getString(cursor
						.getColumnIndex("InventoryID"));
				uploadInventory.ParentPlanID = cursor.getString(cursor
						.getColumnIndex("ParentPlanID"));
				uploadInventory.UploadTime = cursor.getString(cursor
						.getColumnIndex("UploadTime"));
				list.add(uploadInventory);
				uploadInventory = null;
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
