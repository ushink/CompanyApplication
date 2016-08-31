package com.publish.monitorsystem.api.db;

import com.publish.monitorsystem.api.Const;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

	private static String name = Const.DB_NAME;// 表示数据库的名称
	private static int version = 1;// 表示数据库的版本号码

	public DBHelper(Context context) {
		super(context, name, null, version);
	}

	// 当数据库创建的时候，是第一次被执行,完成对数据库的表的创建
	@Override
	public void onCreate(SQLiteDatabase db) {
		// boolean类型 1是true 0是false
		createUserTable(db);// 用户表
		createEqptTable(db);// 设备表
		createInventoryTable(db);// 盘点计划表
		createBuildingTable(db);// 楼房表
		createRoomTable(db);// 房间表
		createInventoryEqptTable(db);//盘点设备表
		createUploadInventoryTable(db);//盘点回传主表
		createUploadInventoryEqptTable(db);//盘点回传子表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private void createUserTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table user (");
		sql.append("UserID varchar(100) primary key unique not null,");
		sql.append("TypeID varchar(50),");
		sql.append("UserName varchar(50) not null,");
		sql.append("LoginName varchar(50) not null,");
		sql.append("UserPassword varchar(100),");
		sql.append("UpdateTime varchar(100),");
		sql.append("CreateTime varchar(100),");
		sql.append("CreateUser varchar(100),");
		sql.append("DeleteFlag varchar(100)");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	private void createEqptTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table eqpt (");
		sql.append("EquipmentID varchar(100) ,");
		sql.append("TypeID varchar(100),");
		sql.append("TypeName varchar(100),");
		sql.append("EquipmentCode varchar(100),");
		sql.append("FileCode varchar(100),");
		sql.append("ImageName varchar(100),");
		sql.append("OutFactoryNum varchar(100),");
		sql.append("ProjectName varchar(100),");
		sql.append("IsSecret varchar(100),");
		sql.append("InitialValue varchar(100),");
		sql.append("EquipmentName varchar(100),");
		sql.append("Factory varchar(100),");
		sql.append("FactoryName varchar(100),");
		sql.append("UsePerson varchar(100),");
		sql.append("ManagePerson varchar(100),");
		sql.append("EquipmentSource varchar(100),");
		sql.append("DepartmentID varchar(100),");
		sql.append("DepartmentName varchar(100),");
		sql.append("Specification varchar(100),");
		sql.append("ManufactureDate varchar(100),");
		sql.append("EquipmentPosition varchar(100),");
		sql.append("State varchar(100),");
		sql.append("Remark varchar(100),");
		sql.append("CreateTime varchar(100),");
		sql.append("TagID varchar(100) primary key,");
		sql.append("LangChaoBianHao varchar(100),");
		sql.append("ContractName varchar(100),");
		sql.append("EPC varchar(100)");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	private void createInventoryTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table inventory (");
		sql.append("PlanID varchar(100) primary key unique not null,");
		sql.append("ParentPlanID varchar(100),");
		sql.append("TypeID varchar(100) not null,");
		sql.append("FunctionID varchar(100),");
		sql.append("PlanName varchar(100),");
		sql.append("BeginTimeTrue varchar(100),");
		sql.append("EndTimeTrue varchar(100)");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	private void createBuildingTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table building(");
		sql.append("BuildingID varchar(100) primary key unique not null,");
		sql.append("BuildingName varchar(100)");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	private void createRoomTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table room(");
		sql.append("RoomID varchar(100) primary key unique not null,");
		sql.append("BuildingID varchar(100) not null,");
		sql.append("RoomName varchar(100),");
		sql.append("ThisFloor varchar(100)");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	private void createInventoryEqptTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table inventoryeqpt(");
		sql.append("ResultID varchar(100) not null,");
		sql.append("PlanID varchar(100) not null,");
		sql.append("ParentPlanID varchar(100) not null,");
		sql.append("EquipmentID varchar(100) not null,");
		sql.append("IsInventory varchar(100)");
		sql.append(");");
		db.execSQL(sql.toString());
	}
	
	private void createUploadInventoryTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table uploadinventory(");
		sql.append("InventoryID varchar(100) primary key not null,");
		sql.append("ParentPlanID varchar(100) not null,");
		sql.append("UploadTime varchar(100)");
		sql.append(");");
		db.execSQL(sql.toString());
	}
	
	private void createUploadInventoryEqptTable(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table uploadinventoryeqpt(");
		sql.append("InfoID varchar(100) primary key not null,");
		sql.append("PlanID varchar(100),");
		sql.append("ParentPlanID varchar(100),");
		sql.append("InventoryID varchar(100),");
		sql.append("EquipmentID varchar(100),");
		sql.append("RoomID varchar(100),");
		sql.append("InventoryTime varchar(100),");
		sql.append("foreign key (InventoryID) references  uploadinventory(InventoryID) on delete cascade on update cascade");
		sql.append(");");
		db.execSQL(sql.toString());
	}


}
