package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.publish.monitorsystem.api.bean.UploadInventoryEqpt;
import com.publish.monitorsystem.api.db.DBHelper;

public class UploadInventoryEqptDao{

	private DBHelper helper;

	private UploadInventoryEqptDao(Context context) {
		helper = new DBHelper(context);
	}

	public static UploadInventoryEqptDao instance;

	public synchronized static UploadInventoryEqptDao getInstance(Context context) {
		if (instance == null) {
			instance = new UploadInventoryEqptDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "uploadinventoryeqpt";

	public boolean addUploadInventoryEqpt(UploadInventoryEqpt eqpt) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("InfoID", eqpt.InfoID);
			values.put("PlanID", eqpt.PlanID);
			values.put("ParentPlanID", eqpt.ParentPlanID);
			values.put("InventoryID", eqpt.InventoryID);
			values.put("EquipmentID", eqpt.EquipmentID);
			values.put("RoomID", eqpt.RoomID);
			values.put("InventoryTime", eqpt.InventoryTime);
			database.insert(TABLE_NAME, null, values);
			flag = true;
		} catch (Exception e) {
			System.out.println("----addUploadInventoryEqpt-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	public boolean deleteAllUploadInventoryEqpt() {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
//			String sql = "delete uploadinventory from uploadinventory,uploadinventoryeqpt where uploadinventory.InventoryID = uploadinventoryeqpt.InventoryID";
			String sql = "delete from uploadinventoryeqpt";
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
	
	public boolean deleteAllUploadInventory() {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			String sql = "delete from uploadinventory";
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

	public List<UploadInventoryEqpt> getUploadInventoryEqptList() {
		List<UploadInventoryEqpt> list = new ArrayList<UploadInventoryEqpt>();
		UploadInventoryEqpt eqpt;
		String sql = "select * from " + TABLE_NAME;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				eqpt = new UploadInventoryEqpt();
				eqpt.InfoID = cursor.getString(cursor
						.getColumnIndex("InfoID"));
				eqpt.PlanID = cursor.getString(cursor
						.getColumnIndex("PlanID"));
				eqpt.ParentPlanID = cursor.getString(cursor
						.getColumnIndex("ParentPlanID"));
				eqpt.InventoryID = cursor.getString(cursor
						.getColumnIndex("InventoryID"));
				eqpt.EquipmentID = cursor.getString(cursor
						.getColumnIndex("EquipmentID"));
				eqpt.RoomID = cursor.getString(cursor
						.getColumnIndex("RoomID"));
				eqpt.InventoryTime = cursor.getString(cursor
						.getColumnIndex("InventoryTime"));
				list.add(eqpt);
				eqpt = null;
			}
		} catch (Exception e) {
			System.out.println("----getUploadInventoryEqptList-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return list;
	}
	
	public boolean containsEqptID(String EquipmentID) {
		boolean flag = false;
		SQLiteDatabase database = null;
		String sql = "select * from " + TABLE_NAME + " where EquipmentID like '%" + EquipmentID + "%'";
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			if(cursor.moveToNext()){
				flag = true;
			}
		} catch (Exception e) {
			System.out.println("----isInventoryEqptID-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}
	
	
	/**
	 * 分页查找到的盘点设备
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<UploadInventoryEqpt> getPageInventoryEqpt(int pageSize,int pageIndex){
		List<UploadInventoryEqpt> list = new ArrayList<UploadInventoryEqpt>();
		UploadInventoryEqpt eqpt;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery("select * from uploadinventoryeqpt order by EquipmentID limit ? offset ?;", new String[]{"" + pageSize,"" + (pageIndex * pageSize)});
			while(cursor.moveToNext()){
				eqpt = new UploadInventoryEqpt();
				eqpt.InfoID = cursor.getString(cursor
						.getColumnIndex("InfoID"));
				eqpt.PlanID = cursor.getString(cursor
						.getColumnIndex("PlanID"));
				eqpt.ParentPlanID = cursor.getString(cursor
						.getColumnIndex("ParentPlanID"));
				eqpt.InventoryID = cursor.getString(cursor
						.getColumnIndex("InventoryID"));
				eqpt.EquipmentID = cursor.getString(cursor
						.getColumnIndex("EquipmentID"));
				eqpt.RoomID = cursor.getString(cursor
						.getColumnIndex("RoomID"));
				eqpt.InventoryTime = cursor.getString(cursor
						.getColumnIndex("InventoryTime"));
				list.add(eqpt);
				eqpt = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
