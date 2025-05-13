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
			values.put("LangChaoBianHao", eqpt.LangChaoBianHao);
			values.put("ContractName", eqpt.ContractName);
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
				sql.append(eqpt.LangChaoBianHao + ",");
				sql.append(eqpt.ContractName + ",");
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

	public boolean updateEqpt(String EPC,String CreateTime) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("CreateTime", CreateTime);
			database.update(TABLE_NAME, values , "EPC like ?", new String[]{"%"+EPC+"%"});
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
				int idxLangChaoBianHao = cursor.getColumnIndex("LangChaoBianHao");
				int idxContractName = cursor.getColumnIndex("ContractName");
				int idxEPC = cursor.getColumnIndex("EPC");
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
				if (idxLangChaoBianHao != -1) eqpt.LangChaoBianHao = cursor.getString(idxLangChaoBianHao);
				if (idxContractName != -1) eqpt.ContractName = cursor.getString(idxContractName);
				if (idxEPC != -1) eqpt.EPC = cursor.getString(idxEPC);
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
		Eqpt eqpt = null;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, null, "EPC = ?",
					new String[] { EPC }, null, null, null);
			if (cursor.moveToNext()) {
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
				int idxLangChaoBianHao = cursor.getColumnIndex("LangChaoBianHao");
				int idxContractName = cursor.getColumnIndex("ContractName");
				int idxEPC = cursor.getColumnIndex("EPC");
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
				if (idxLangChaoBianHao != -1) eqpt.LangChaoBianHao = cursor.getString(idxLangChaoBianHao);
				if (idxContractName != -1) eqpt.ContractName = cursor.getString(idxContractName);
				if (idxEPC != -1) eqpt.EPC = cursor.getString(idxEPC);
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
