package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.msystemlib.utils.LogUtils;
import com.publish.monitorsystem.api.bean.EqptBean;
import com.publish.monitorsystem.api.bean.InventoryEqptBean;
import com.publish.monitorsystem.api.bean.EqptBean.Eqpt;
import com.publish.monitorsystem.api.bean.InventoryEqptBean.InventoryEqpt;
import com.publish.monitorsystem.api.db.DBHelper;

public class InventoryEqptDao{

	private DBHelper helper;

	private InventoryEqptDao(Context context) {
		helper = new DBHelper(context);
	}

	public static InventoryEqptDao instance;

	public synchronized static InventoryEqptDao getInstance(Context context) {
		if (instance == null) {
			instance = new InventoryEqptDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "inventoryeqpt";

	public boolean addInventoryEqpt(InventoryEqpt inventoryEqpt) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("ResultID", inventoryEqpt.ResultID);
			values.put("PlanID", inventoryEqpt.PlanID);
			values.put("ParentPlanID", inventoryEqpt.ParentPlanID);
			values.put("EquipmentID", inventoryEqpt.EquipmentID);
			values.put("IsInventory", inventoryEqpt.IsInventory);
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
	
	public boolean isInventoryEqptID(String EquipmentID,String PlanID) {
		boolean flag = false;
		SQLiteDatabase database = null;
		String sql = "select * from " + TABLE_NAME + " where PlanID like '%" + PlanID + "%' and EquipmentID like '%" + EquipmentID + "%'";
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
	
	public boolean updateInventoryEqpt(String EquipmentID,String PlanID) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("IsInventory", "1");
			database.update(TABLE_NAME, values , "EquipmentID like ? and PlanID like ?", new String[]{"%"+EquipmentID+"%","%"+PlanID+"%"});
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


	public boolean updateInventoryEqpts(List<String> sqls) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			database.beginTransaction();
			for (int i = 0; i < sqls.size(); i++) {
				database.execSQL(sqls.get(i));
			}
			database.setTransactionSuccessful();
			database.endTransaction();
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
	
	public boolean updateInventoryEqpt() {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("IsInventory", "0");
			database.update(TABLE_NAME, values , null, null);
			flag = true;
		} catch (Exception e) {
			System.out.println("----updateInventoryEqpt-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}
	
	
	public boolean deleteAllInventoryEqpt() {
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

	public boolean deleteInventoryEqptByParentPlan(String parentPlan) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			String sql = "delete from " + TABLE_NAME + " where ParentPlanID like '%"+parentPlan+"%'";
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

	public List<InventoryEqpt> getAllInventoryEqptList(String parentPlanID) {
		List<InventoryEqpt> list = new ArrayList<InventoryEqpt>();
		InventoryEqptBean bean = new InventoryEqptBean();
		InventoryEqpt inventoryEqpt;
		String sql = "select * from " + TABLE_NAME + " where ParentPlanID like '%" + "%'";
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				inventoryEqpt = bean.new InventoryEqpt();
				inventoryEqpt.ResultID = cursor.getString(cursor
						.getColumnIndex("ResultID"));
				inventoryEqpt.PlanID = cursor.getString(cursor
						.getColumnIndex("PlanID"));
				inventoryEqpt.ParentPlanID = cursor.getString(cursor
						.getColumnIndex("ParentPlanID"));
				inventoryEqpt.EquipmentID = cursor.getString(cursor
						.getColumnIndex("EquipmentID"));
				inventoryEqpt.IsInventory = cursor.getString(cursor
						.getColumnIndex("IsInventory"));
				list.add(inventoryEqpt);
				inventoryEqpt = null;
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
	
	
	public List<Eqpt> getInventoryEqptList(String IsInventory,String PlanID) {
		List<Eqpt> list = new ArrayList<Eqpt>();
		EqptBean bean = new EqptBean();
		Eqpt eqpt;
		String sql ="select * from eqpt INNER JOIN inventoryeqpt on eqpt.EquipmentID = inventoryeqpt.EquipmentID where inventoryeqpt.IsInventory = "+IsInventory+" and inventoryeqpt.PlanID like '%"+PlanID+"%' order by CreateTime desc;";
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				eqpt = bean.new Eqpt();
				eqpt.EquipmentID = cursor.getString(cursor
						.getColumnIndex("EquipmentID"));
				eqpt.TypeID = cursor.getString(cursor
						.getColumnIndex("TypeID"));
				eqpt.TypeName = cursor.getString(cursor
						.getColumnIndex("TypeName"));
				eqpt.EquipmentCode = cursor.getString(cursor
						.getColumnIndex("EquipmentCode"));
				eqpt.FileCode = cursor.getString(cursor
						.getColumnIndex("FileCode"));
				eqpt.ImageName = cursor.getString(cursor
						.getColumnIndex("ImageName"));
				eqpt.OutFactoryNum = cursor.getString(cursor
						.getColumnIndex("OutFactoryNum"));
				eqpt.ProjectName = cursor.getString(cursor
						.getColumnIndex("ProjectName"));
				eqpt.IsSecret = cursor.getString(cursor
						.getColumnIndex("IsSecret"));
				eqpt.InitialValue = cursor.getString(cursor
						.getColumnIndex("InitialValue"));
				eqpt.EquipmentName = cursor.getString(cursor
						.getColumnIndex("EquipmentName"));
				eqpt.Factory = cursor.getString(cursor
						.getColumnIndex("Factory"));
				eqpt.FactoryName = cursor.getString(cursor
						.getColumnIndex("FactoryName"));
				eqpt.UsePerson = cursor.getString(cursor
						.getColumnIndex("UsePerson"));
				eqpt.ManagePerson = cursor.getString(cursor
						.getColumnIndex("ManagePerson"));
				eqpt.EquipmentSource = cursor.getString(cursor
						.getColumnIndex("EquipmentSource"));
				eqpt.DepartmentID = cursor.getString(cursor
						.getColumnIndex("DepartmentID"));
				eqpt.DepartmentName = cursor.getString(cursor
						.getColumnIndex("DepartmentName"));
				eqpt.Specification = cursor.getString(cursor
						.getColumnIndex("Specification"));
				eqpt.ManufactureDate = cursor.getString(cursor
						.getColumnIndex("ManufactureDate"));
				eqpt.EquipmentPosition = cursor.getString(cursor
						.getColumnIndex("EquipmentPosition"));
				eqpt.State = cursor.getString(cursor
						.getColumnIndex("State"));
				eqpt.Remark = cursor.getString(cursor
						.getColumnIndex("Remark"));
				eqpt.CreateTime = cursor.getString(cursor
						.getColumnIndex("CreateTime"));
				eqpt.TagID = cursor.getString(cursor
						.getColumnIndex("TagID"));
				eqpt.EPC = cursor.getString(cursor
						.getColumnIndex("EPC"));
				list.add(eqpt);
				eqpt = null;
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

	public List<Eqpt> getInventoryEqptList1(String PlanID) {
		List<Eqpt> list = new ArrayList<Eqpt>();
		EqptBean bean = new EqptBean();
		Eqpt eqpt;
		String sql ="select * from eqpt a INNER JOIN inventoryeqpt b on a.EquipmentID = b.EquipmentID where b.PlanID like '%"+PlanID+"%' order by IsInventory, CreateTime desc";
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				eqpt = bean.new Eqpt();
				eqpt.EquipmentID = cursor.getString(cursor
						.getColumnIndex("EquipmentID"));
				eqpt.TypeID = cursor.getString(cursor
						.getColumnIndex("TypeID"));
				eqpt.TypeName = cursor.getString(cursor
						.getColumnIndex("TypeName"));
				eqpt.EquipmentCode = cursor.getString(cursor
						.getColumnIndex("EquipmentCode"));
				eqpt.FileCode = cursor.getString(cursor
						.getColumnIndex("FileCode"));
				eqpt.ImageName = cursor.getString(cursor
						.getColumnIndex("ImageName"));
				eqpt.OutFactoryNum = cursor.getString(cursor
						.getColumnIndex("OutFactoryNum"));
				eqpt.ProjectName = cursor.getString(cursor
						.getColumnIndex("ProjectName"));
				eqpt.IsSecret = cursor.getString(cursor
						.getColumnIndex("IsSecret"));
				eqpt.InitialValue = cursor.getString(cursor
						.getColumnIndex("InitialValue"));
				eqpt.EquipmentName = cursor.getString(cursor
						.getColumnIndex("EquipmentName"));
				eqpt.Factory = cursor.getString(cursor
						.getColumnIndex("Factory"));
				eqpt.FactoryName = cursor.getString(cursor
						.getColumnIndex("FactoryName"));
				eqpt.UsePerson = cursor.getString(cursor
						.getColumnIndex("UsePerson"));
				eqpt.ManagePerson = cursor.getString(cursor
						.getColumnIndex("ManagePerson"));
				eqpt.EquipmentSource = cursor.getString(cursor
						.getColumnIndex("EquipmentSource"));
				eqpt.DepartmentID = cursor.getString(cursor
						.getColumnIndex("DepartmentID"));
				eqpt.DepartmentName = cursor.getString(cursor
						.getColumnIndex("DepartmentName"));
				eqpt.Specification = cursor.getString(cursor
						.getColumnIndex("Specification"));
				eqpt.ManufactureDate = cursor.getString(cursor
						.getColumnIndex("ManufactureDate"));
				eqpt.EquipmentPosition = cursor.getString(cursor
						.getColumnIndex("EquipmentPosition"));
				eqpt.State = cursor.getString(cursor
						.getColumnIndex("State"));
				eqpt.Remark = cursor.getString(cursor
						.getColumnIndex("Remark"));
				eqpt.CreateTime = cursor.getString(cursor
						.getColumnIndex("CreateTime"));
				eqpt.TagID = cursor.getString(cursor
						.getColumnIndex("TagID"));
				eqpt.EPC = cursor.getString(cursor
						.getColumnIndex("EPC"));
				list.add(eqpt);
				eqpt = null;
			}
		} catch (Exception e) {
			System.out.println("ckj" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return list;
	}

	public String getParentPlanID(String planID) {
		
		String str = null;
		String sql = "select ParentPlanID from " + TABLE_NAME + " where PlanID like '%"+planID+"%'";
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			if(cursor.moveToNext()){
				str = cursor.getString(0);
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

	public int getinventoryeqptSize(String PlanID,int IsInventory){
		int size = -1;
		SQLiteDatabase database = null;
		String sql = "select count(*) from eqpt a INNER JOIN inventoryeqpt b on a.EquipmentID = b.EquipmentID where b.PlanID like '%"+PlanID+"%' and b.IsInventory = "+IsInventory;
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

	public int gettagplanSize(String PlanID,String tag){
		int size = -1;
		SQLiteDatabase database = null;
		String sql = "select count(*) from eqpt a INNER JOIN inventoryeqpt b on a.EquipmentID = b.EquipmentID where b.PlanID like '%"+PlanID+"%' and a.EPC like '%"+tag+ "%'";
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
