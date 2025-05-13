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
				int idxResultID = cursor.getColumnIndex("ResultID");
				int idxPlanID = cursor.getColumnIndex("PlanID");
				int idxParentPlanID = cursor.getColumnIndex("ParentPlanID");
				int idxEquipmentID = cursor.getColumnIndex("EquipmentID");
				int idxIsInventory = cursor.getColumnIndex("IsInventory");
				if (idxResultID != -1) inventoryEqpt.ResultID = cursor.getString(idxResultID);
				if (idxPlanID != -1) inventoryEqpt.PlanID = cursor.getString(idxPlanID);
				if (idxParentPlanID != -1) inventoryEqpt.ParentPlanID = cursor.getString(idxParentPlanID);
				if (idxEquipmentID != -1) inventoryEqpt.EquipmentID = cursor.getString(idxEquipmentID);
				if (idxIsInventory != -1) inventoryEqpt.IsInventory = cursor.getString(idxIsInventory);
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
				int idxEquipmentID = cursor.getColumnIndex("EquipmentID");
				int idxTypeID = cursor.getColumnIndex("TypeID");
				int idxTypeName = cursor.getColumnIndex("TypeName");
				int idxEquipmentCode = cursor.getColumnIndex("EquipmentCode");
				int idxFileCode = cursor.getColumnIndex("FileCode");
				int idxImageName = cursor.getColumnIndex("ImageName");
				int idxOutFactoryNum = cursor.getColumnIndex("OutFactoryNum");
				int idxProjectName = cursor.getColumnIndex("ProjectName");
				int idxIsSecret = cursor.getColumnIndex("IsSecret");
				int idxInitialValue = cursor.getColumnIndex("InitialValue");
				int idxEquipmentName = cursor.getColumnIndex("EquipmentName");
				int idxFactory = cursor.getColumnIndex("Factory");
				int idxFactoryName = cursor.getColumnIndex("FactoryName");
				int idxUsePerson = cursor.getColumnIndex("UsePerson");
				int idxManagePerson = cursor.getColumnIndex("ManagePerson");
				int idxEquipmentSource = cursor.getColumnIndex("EquipmentSource");
				int idxDepartmentID = cursor.getColumnIndex("DepartmentID");
				int idxDepartmentName = cursor.getColumnIndex("DepartmentName");
				int idxSpecification = cursor.getColumnIndex("Specification");
				int idxManufactureDate = cursor.getColumnIndex("ManufactureDate");
				int idxEquipmentPosition = cursor.getColumnIndex("EquipmentPosition");
				int idxState = cursor.getColumnIndex("State");
				int idxRemark = cursor.getColumnIndex("Remark");
				int idxCreateTime = cursor.getColumnIndex("CreateTime");
				int idxTagID = cursor.getColumnIndex("TagID");
				int idxEPC = cursor.getColumnIndex("EPC");
				int idxLangChaoBianHao = cursor.getColumnIndex("LangChaoBianHao");
				int idxContractName = cursor.getColumnIndex("ContractName");
				if (idxEquipmentID != -1) eqpt.EquipmentID = cursor.getString(idxEquipmentID);
				if (idxTypeID != -1) eqpt.TypeID = cursor.getString(idxTypeID);
				if (idxTypeName != -1) eqpt.TypeName = cursor.getString(idxTypeName);
				if (idxEquipmentCode != -1) eqpt.EquipmentCode = cursor.getString(idxEquipmentCode);
				if (idxFileCode != -1) eqpt.FileCode = cursor.getString(idxFileCode);
				if (idxImageName != -1) eqpt.ImageName = cursor.getString(idxImageName);
				if (idxOutFactoryNum != -1) eqpt.OutFactoryNum = cursor.getString(idxOutFactoryNum);
				if (idxProjectName != -1) eqpt.ProjectName = cursor.getString(idxProjectName);
				if (idxIsSecret != -1) eqpt.IsSecret = cursor.getString(idxIsSecret);
				if (idxInitialValue != -1) eqpt.InitialValue = cursor.getString(idxInitialValue);
				if (idxEquipmentName != -1) eqpt.EquipmentName = cursor.getString(idxEquipmentName);
				if (idxFactory != -1) eqpt.Factory = cursor.getString(idxFactory);
				if (idxFactoryName != -1) eqpt.FactoryName = cursor.getString(idxFactoryName);
				if (idxUsePerson != -1) eqpt.UsePerson = cursor.getString(idxUsePerson);
				if (idxManagePerson != -1) eqpt.ManagePerson = cursor.getString(idxManagePerson);
				if (idxEquipmentSource != -1) eqpt.EquipmentSource = cursor.getString(idxEquipmentSource);
				if (idxDepartmentID != -1) eqpt.DepartmentID = cursor.getString(idxDepartmentID);
				if (idxDepartmentName != -1) eqpt.DepartmentName = cursor.getString(idxDepartmentName);
				if (idxSpecification != -1) eqpt.Specification = cursor.getString(idxSpecification);
				if (idxManufactureDate != -1) eqpt.ManufactureDate = cursor.getString(idxManufactureDate);
				if (idxEquipmentPosition != -1) eqpt.EquipmentPosition = cursor.getString(idxEquipmentPosition);
				if (idxState != -1) eqpt.State = cursor.getString(idxState);
				if (idxRemark != -1) eqpt.Remark = cursor.getString(idxRemark);
				if (idxCreateTime != -1) eqpt.CreateTime = cursor.getString(idxCreateTime);
				if (idxTagID != -1) eqpt.TagID = cursor.getString(idxTagID);
				if (idxEPC != -1) eqpt.EPC = cursor.getString(idxEPC);
				if (idxLangChaoBianHao != -1) eqpt.LangChaoBianHao = cursor.getString(idxLangChaoBianHao);
				if (idxContractName != -1) eqpt.ContractName = cursor.getString(idxContractName);
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
				int idxEquipmentID = cursor.getColumnIndex("EquipmentID");
				int idxTypeID = cursor.getColumnIndex("TypeID");
				int idxTypeName = cursor.getColumnIndex("TypeName");
				int idxEquipmentCode = cursor.getColumnIndex("EquipmentCode");
				int idxFileCode = cursor.getColumnIndex("FileCode");
				int idxImageName = cursor.getColumnIndex("ImageName");
				int idxOutFactoryNum = cursor.getColumnIndex("OutFactoryNum");
				int idxProjectName = cursor.getColumnIndex("ProjectName");
				int idxIsSecret = cursor.getColumnIndex("IsSecret");
				int idxInitialValue = cursor.getColumnIndex("InitialValue");
				int idxEquipmentName = cursor.getColumnIndex("EquipmentName");
				int idxFactory = cursor.getColumnIndex("Factory");
				int idxFactoryName = cursor.getColumnIndex("FactoryName");
				int idxUsePerson = cursor.getColumnIndex("UsePerson");
				int idxManagePerson = cursor.getColumnIndex("ManagePerson");
				int idxEquipmentSource = cursor.getColumnIndex("EquipmentSource");
				int idxDepartmentID = cursor.getColumnIndex("DepartmentID");
				int idxDepartmentName = cursor.getColumnIndex("DepartmentName");
				int idxSpecification = cursor.getColumnIndex("Specification");
				int idxManufactureDate = cursor.getColumnIndex("ManufactureDate");
				int idxEquipmentPosition = cursor.getColumnIndex("EquipmentPosition");
				int idxState = cursor.getColumnIndex("State");
				int idxRemark = cursor.getColumnIndex("Remark");
				int idxCreateTime = cursor.getColumnIndex("CreateTime");
				int idxTagID = cursor.getColumnIndex("TagID");
				int idxEPC = cursor.getColumnIndex("EPC");
				int idxLangChaoBianHao = cursor.getColumnIndex("LangChaoBianHao");
				int idxContractName = cursor.getColumnIndex("ContractName");
				if (idxEquipmentID != -1) eqpt.EquipmentID = cursor.getString(idxEquipmentID);
				if (idxTypeID != -1) eqpt.TypeID = cursor.getString(idxTypeID);
				if (idxTypeName != -1) eqpt.TypeName = cursor.getString(idxTypeName);
				if (idxEquipmentCode != -1) eqpt.EquipmentCode = cursor.getString(idxEquipmentCode);
				if (idxFileCode != -1) eqpt.FileCode = cursor.getString(idxFileCode);
				if (idxImageName != -1) eqpt.ImageName = cursor.getString(idxImageName);
				if (idxOutFactoryNum != -1) eqpt.OutFactoryNum = cursor.getString(idxOutFactoryNum);
				if (idxProjectName != -1) eqpt.ProjectName = cursor.getString(idxProjectName);
				if (idxIsSecret != -1) eqpt.IsSecret = cursor.getString(idxIsSecret);
				if (idxInitialValue != -1) eqpt.InitialValue = cursor.getString(idxInitialValue);
				if (idxEquipmentName != -1) eqpt.EquipmentName = cursor.getString(idxEquipmentName);
				if (idxFactory != -1) eqpt.Factory = cursor.getString(idxFactory);
				if (idxFactoryName != -1) eqpt.FactoryName = cursor.getString(idxFactoryName);
				if (idxUsePerson != -1) eqpt.UsePerson = cursor.getString(idxUsePerson);
				if (idxManagePerson != -1) eqpt.ManagePerson = cursor.getString(idxManagePerson);
				if (idxEquipmentSource != -1) eqpt.EquipmentSource = cursor.getString(idxEquipmentSource);
				if (idxDepartmentID != -1) eqpt.DepartmentID = cursor.getString(idxDepartmentID);
				if (idxDepartmentName != -1) eqpt.DepartmentName = cursor.getString(idxDepartmentName);
				if (idxSpecification != -1) eqpt.Specification = cursor.getString(idxSpecification);
				if (idxManufactureDate != -1) eqpt.ManufactureDate = cursor.getString(idxManufactureDate);
				if (idxEquipmentPosition != -1) eqpt.EquipmentPosition = cursor.getString(idxEquipmentPosition);
				if (idxState != -1) eqpt.State = cursor.getString(idxState);
				if (idxRemark != -1) eqpt.Remark = cursor.getString(idxRemark);
				if (idxCreateTime != -1) eqpt.CreateTime = cursor.getString(idxCreateTime);
				if (idxTagID != -1) eqpt.TagID = cursor.getString(idxTagID);
				if (idxEPC != -1) eqpt.EPC = cursor.getString(idxEPC);
				if (idxLangChaoBianHao != -1) eqpt.LangChaoBianHao = cursor.getString(idxLangChaoBianHao);
				if (idxContractName != -1) eqpt.ContractName = cursor.getString(idxContractName);
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
