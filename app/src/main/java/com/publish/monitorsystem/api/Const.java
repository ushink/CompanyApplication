package com.publish.monitorsystem.api;

import android.os.Environment;

import com.msystemlib.utils.FileUtils;

/**
 * 一些系统常量的定义
 * 
 */
public class Const {
	//用户相关
	public static final String ADMIN = "admin";
	public static final String ADMINPWD = "admin";
	public static final String USERNAME = "username";
	public static final String TYPEID = "TypeID"; //1设备 2营具 3档案
	public static final String PASSWORD = "password";
	
	
	public static final String DB_NAME = "MonitoringSystem.db";
	
	//服务器相关
	public static final String URL = "http://192.168.5.1:7001/RSWebService.asmx";
	public static final String URL_IMG = "http://192.168.5.1:7002";
//	public static final String URL = "http://192.168.1.50:7003/RSWebService.asmx";
//	public static final String URL_IMG = "http://192.168.1.50:7002";
	public static final String NAMESPACE = "http://tempuri.org/";
	public static final String TESTCONNECT = "TestConnect";
	public static final String GETLOGINUSERLIST = "GetLoginUserList";//得到用户列表
	public static final String GETEQUIPMENTTAGINFO = "GetEquipmentTagInfo";//得到设备信息
	public static final String GETEQUIPMENTTAGNUMBER = "GetEquipmentTagNumber";//得到设备总数
	public static final String GETINVENTORYPLAN = "GetInventoryPlan";//得到盘点、查找、核检计划
	public static final String GETBUILDINGLIST = "GetBuildingList";//得到建筑列表
	public static final String GETROOMLIST = "GetRoomList";//得到房间列表
	public static final String GETINVENTORYEQPTNUM = "GetInventoryEqptNum";//得到盘点设备总数
	public static final String GETINVENTORYEQPT = "GetInventoryEqpt";//得到盘点设备
	public static final String UPLOADINVENTORY = "UploadInventory";//上传盘点设备主表
	public static final String UPLOADINVENTORYINFO = "UploadInventoryInfo";//上传盘点设备子表
	
	
}
