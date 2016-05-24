package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.publish.monitorsystem.api.bean.EqptBean;
import com.publish.monitorsystem.api.bean.EqptBean.Eqpt;
import com.publish.monitorsystem.api.db.DBHelper;

public class EqptDao {

	private DBHelper helper;

	private EqptDao(Context context) {
		helper = new DBHelper(context);
	}

	public static EqptDao instance;

	public synchronized static EqptDao getInstance(Context context) {
		if (instance == null) {
			instance = new EqptDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "eqpt";

	public boolean addEqpt(Eqpt eqpt) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("EquipmentID", eqpt.EquipmentID);
			values.put("TypeID", eqpt.TypeID);
			values.put("TypeName", eqpt.TypeName);
			values.put("EquipmentCode", eqpt.EquipmentCode);
			values.put("FileCode", eqpt.FileCode);
			values.put("ImageName", eqpt.ImageName);
			values.put("OutFactoryNum", eqpt.OutFactoryNum);
			values.put("ProjectName", eqpt.ProjectName);
			values.put("IsSecret", eqpt.IsSecret);
			values.put("InitialValue", eqpt.InitialValue);
			values.put("EquipmentName", eqpt.EquipmentName);
			values.put("Factory", eqpt.Factory);
			values.put("FactoryName", eqpt.FactoryName);
			values.put("UsePerson", eqpt.UsePerson);
			values.put("ManagePerson", eqpt.ManagePerson);
			values.put("EquipmentSource", eqpt.EquipmentSource);
			values.put("DepartmentID", eqpt.DepartmentID);
			values.put("DepartmentName", eqpt.DepartmentName);
			values.put("Specification", eqpt.Specification);
			values.put("ManufactureDate", eqpt.ManufactureDate);
			values.put("EquipmentPosition", eqpt.EquipmentPosition);
			values.put("State", eqpt.State);
			values.put("Remark", eqpt.Remark);
			values.put("CreateTime", eqpt.CreateTime);
			values.put("TagID", eqpt.TagID);
			values.put("EPC", eqpt.EPC);
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

	public boolean addEqptList(List<Eqpt> eqptList) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			database.beginTransaction();
			for (Eqpt eqpt : eqptList) {
				StringBuffer sql = new StringBuffer();
				sql.append("insert into" + TABLE_NAME + "values(");
				sql.append(eqpt.EquipmentID + ",");
				sql.append(eqpt.TypeID + ",");
				sql.append(eqpt.TypeName + ",");
				sql.append(eqpt.EquipmentCode + ",");
				sql.append(eqpt.FileCode + ",");
				sql.append(eqpt.ImageName + ",");
				sql.append(eqpt.OutFactoryNum + ",");
				sql.append(eqpt.ProjectName + ",");
				sql.append(eqpt.IsSecret + ",");
				sql.append(eqpt.InitialValue + ",");
				sql.append(eqpt.EquipmentName + ",");
				sql.append(eqpt.Factory + ",");
				sql.append(eqpt.FactoryName + ",");
				sql.append(eqpt.UsePerson + ",");
				sql.append(eqpt.ManagePerson + ",");
				sql.append(eqpt.EquipmentSource + ",");
				sql.append(eqpt.DepartmentID + ",");
				sql.append(eqpt.DepartmentName + ",");
				sql.append(eqpt.Specification + ",");
				sql.append(eqpt.ManufactureDate + ",");
				sql.append(eqpt.EquipmentPosition + ",");
				sql.append(eqpt.State + ",");
				sql.append(eqpt.Remark + ",");
				sql.append(eqpt.CreateTime + ",");
				sql.append(eqpt.TagID + ",");
				sql.append(eqpt.EPC);
				sql.append(");");
				database.execSQL(sql.toString());
			}
			database.setTransactionSuccessful();
			flag = true;
			database.endTransaction();
		} catch (Exception e) {
			System.out.println("----addUserLog-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	public boolean deleteAllEqpt() {
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

	public List<Eqpt> getAllEqptList() {
		List<Eqpt> list = new ArrayList<Eqpt>();
		EqptBean bean = new EqptBean();
		Eqpt eqpt;
		String sql = "select * from " + TABLE_NAME;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				eqpt = bean.new Eqpt();
				eqpt.EquipmentID = cursor.getString(cursor
						.getColumnIndex("EquipmentID"));
				eqpt.TypeID = cursor.getString(cursor.getColumnIndex("TypeID"));
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
				eqpt.State = cursor.getString(cursor.getColumnIndex("State"));
				eqpt.Remark = cursor.getString(cursor.getColumnIndex("Remark"));
				eqpt.CreateTime = cursor.getString(cursor
						.getColumnIndex("CreateTime"));
				eqpt.TagID = cursor.getString(cursor.getColumnIndex("TagID"));
				eqpt.EPC = cursor.getString(cursor.getColumnIndex("EPC"));
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

	public Eqpt getEqptByEPC(String EPC) {
		EqptBean bean = new EqptBean();
		Eqpt eqpt = bean.new Eqpt();
		;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, null, "EPC = ?",
					new String[] { EPC }, null, null, null);
			if (cursor.moveToNext()) {
				eqpt.EquipmentID = cursor.getString(cursor
						.getColumnIndex("EquipmentID"));
				eqpt.TypeID = cursor.getString(cursor.getColumnIndex("TypeID"));
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
				eqpt.State = cursor.getString(cursor.getColumnIndex("State"));
				eqpt.Remark = cursor.getString(cursor.getColumnIndex("Remark"));
				eqpt.CreateTime = cursor.getString(cursor
						.getColumnIndex("CreateTime"));
				eqpt.TagID = cursor.getString(cursor.getColumnIndex("TagID"));
				eqpt.EPC = cursor.getString(cursor.getColumnIndex("EPC"));
			}
		} catch (Exception e) {
			System.out.println("----getAllUserLogList-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return eqpt;
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
