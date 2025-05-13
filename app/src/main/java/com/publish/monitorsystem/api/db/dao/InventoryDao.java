package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.publish.monitorsystem.api.bean.InventoryBean;
import com.publish.monitorsystem.api.bean.InventoryBean.Inventory;
import com.publish.monitorsystem.api.db.DBHelper;

public class InventoryDao{

	private DBHelper helper;

	private InventoryDao(Context context) {
		helper = new DBHelper(context);
	}

	public static InventoryDao instance;

	public synchronized static InventoryDao getInstance(Context context) {
		if (instance == null) {
			instance = new InventoryDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "inventory";

	public boolean addInventory(Inventory inventory) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("PlanID", inventory.PlanID);
			values.put("ParentPlanID", inventory.ParentPlanID);
			values.put("TypeID", inventory.TypeID);
			values.put("FunctionID", inventory.FunctionID);
			values.put("PlanName", inventory.PlanName);
			values.put("BeginTimeTrue", inventory.BeginTimeTrue);
			values.put("EndTimeTrue", inventory.EndTimeTrue);
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

	public boolean up(String EPC,String CreateTime) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("PlanName", CreateTime);
			database.update(TABLE_NAME, values , "PlanID like ?", new String[]{"%"+EPC+"%"});
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
	
	public boolean deleteAllInventory(int FunctionID) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			String sql = "delete from " + TABLE_NAME + " where FunctionID = " + FunctionID;
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

	public boolean deleteAllInventory(String planID) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			String sql = "delete from " + TABLE_NAME + " where PlanID like'%" + planID + "%'";
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

	public List<Inventory> getAllInventoryList(int FunctionID) {
		List<Inventory> list = new ArrayList<Inventory>();
		InventoryBean bean = new InventoryBean();
		Inventory inventory;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME,null, "FunctionID = ?",
					new String[] { FunctionID + "" }, null, null, null);
			while (cursor.moveToNext()) {
				inventory = bean.new Inventory();
				int idxPlanID = cursor.getColumnIndex("PlanID");
				int idxParentPlanID = cursor.getColumnIndex("ParentPlanID");
				int idxTypeID = cursor.getColumnIndex("TypeID");
				int idxFunctionID = cursor.getColumnIndex("FunctionID");
				int idxPlanName = cursor.getColumnIndex("PlanName");
				int idxBeginTimeTrue = cursor.getColumnIndex("BeginTimeTrue");
				int idxEndTimeTrue = cursor.getColumnIndex("EndTimeTrue");
				if (idxPlanID != -1) inventory.PlanID = cursor.getString(idxPlanID);
				if (idxParentPlanID != -1) inventory.ParentPlanID = cursor.getString(idxParentPlanID);
				if (idxTypeID != -1) inventory.TypeID = cursor.getString(idxTypeID);
				if (idxFunctionID != -1) inventory.FunctionID = cursor.getString(idxFunctionID);
				if (idxPlanName != -1) inventory.PlanName = cursor.getString(idxPlanName);
				if (idxBeginTimeTrue != -1) inventory.BeginTimeTrue = cursor.getString(idxBeginTimeTrue);
				if (idxEndTimeTrue != -1) inventory.EndTimeTrue = cursor.getString(idxEndTimeTrue);
				list.add(inventory);
				inventory = null;
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
	
	public Inventory getInventory(String typeId,String Function) {
		InventoryBean bean = new InventoryBean();
		Inventory inventory = bean.new Inventory();
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, null, "TypeID = ? and FunctionID = ?",
					new String[] { typeId, Function}, null, null, null);
			if (cursor.moveToNext()) {
				int idxPlanID = cursor.getColumnIndex("PlanID");
				int idxParentPlanID = cursor.getColumnIndex("ParentPlanID");
				int idxTypeID = cursor.getColumnIndex("TypeID");
				int idxFunctionID = cursor.getColumnIndex("FunctionID");
				int idxPlanName = cursor.getColumnIndex("PlanName");
				int idxBeginTimeTrue = cursor.getColumnIndex("BeginTimeTrue");
				int idxEndTimeTrue = cursor.getColumnIndex("EndTimeTrue");
				if (idxPlanID != -1) inventory.PlanID = cursor.getString(idxPlanID);
				if (idxParentPlanID != -1) inventory.ParentPlanID = cursor.getString(idxParentPlanID);
				if (idxTypeID != -1) inventory.TypeID = cursor.getString(idxTypeID);
				if (idxFunctionID != -1) inventory.FunctionID = cursor.getString(idxFunctionID);
				if (idxPlanName != -1) inventory.PlanName = cursor.getString(idxPlanName);
				if (idxBeginTimeTrue != -1) inventory.BeginTimeTrue = cursor.getString(idxBeginTimeTrue);
				if (idxEndTimeTrue != -1) inventory.EndTimeTrue = cursor.getString(idxEndTimeTrue);
			}
		} catch (Exception e) {
			System.out.println("----getAllUserLogList-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return inventory;
	}
	
	public String getInventoryPlanID(String planName) {
		String str = "";
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, new String[]{"PlanID"}, "PlanName like ?",
					new String[] { "%"+planName+"%" }, null, null, null);
			if (cursor.moveToNext()) {
				int idxPlanID = cursor.getColumnIndex("PlanID");
				if (idxPlanID != -1) str = cursor.getString(idxPlanID);
			}
		} catch (Exception e) {
			System.out.println("----getAllUserLogList-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return str;
	}

	public String getInventoryPlanID1(String planName) {
		String str = "";
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, new String[]{"PlanID"}, "PlanName = ?",
					new String[] { planName}, null, null, null);
			if (cursor.moveToNext()) {
				int idxPlanID = cursor.getColumnIndex("PlanID");
				if (idxPlanID != -1) str = cursor.getString(idxPlanID);
			}
		} catch (Exception e) {
			System.out.println("----getAllUserLogList-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return str;
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
