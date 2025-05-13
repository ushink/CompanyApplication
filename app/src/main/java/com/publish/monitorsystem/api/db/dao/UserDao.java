package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.publish.monitorsystem.api.bean.UserBean;
import com.publish.monitorsystem.api.bean.UserBean.User;
import com.publish.monitorsystem.api.db.DBHelper;

public class UserDao{

	private DBHelper helper;

	private UserDao(Context context) {
		helper = new DBHelper(context);
	}

	public static UserDao instance;

	public synchronized static UserDao getInstance(Context context) {
		if (instance == null) {
			instance = new UserDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "user";

	public boolean addUserLog(User user) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("UserID", user.UserID);
			values.put("TypeID", user.TypeID);
			values.put("UserName", user.UserName);
			values.put("LoginName", user.LoginName);
			values.put("UserPassword", user.UserPassword);
			values.put("UpdateTime", user.UpdateTime);
			values.put("CreateTime", user.CreateTime);
			values.put("CreateUser", user.CreateUser);
			values.put("DeleteFlag", user.DeleteFlag);
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

	public boolean deleteAllUserLog() {
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

	public List<User> getAllUserLogList() {
		List<User> list = new ArrayList<User>();
		UserBean bean = new UserBean();
		User user;
		String sql = "select * from " + TABLE_NAME;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				user = bean.new User();
				int idxUserID = cursor.getColumnIndex("UserID");
				if (idxUserID != -1) {
					user.UserID = cursor.getString(idxUserID);
				}
				int idxTypeID = cursor.getColumnIndex("TypeID");
				if (idxTypeID != -1) {
					user.TypeID = cursor.getString(idxTypeID);
				}
				int idxUserName = cursor.getColumnIndex("UserName");
				if (idxUserName != -1) {
					user.UserName = cursor.getString(idxUserName);
				}
				int idxLoginName = cursor.getColumnIndex("LoginName");
				if (idxLoginName != -1) {
					user.LoginName = cursor.getString(idxLoginName);
				}
				int idxUserPassword = cursor.getColumnIndex("UserPassword");
				if (idxUserPassword != -1) {
					user.UserPassword = cursor.getString(idxUserPassword);
				}
				int idxUpdateTime = cursor.getColumnIndex("UpdateTime");
				if (idxUpdateTime != -1) {
					user.UpdateTime = cursor.getString(idxUpdateTime);
				}
				int idxCreateTime = cursor.getColumnIndex("CreateTime");
				if (idxCreateTime != -1) {
					user.CreateTime = cursor.getString(idxCreateTime);
				}
				int idxCreateUser = cursor.getColumnIndex("CreateUser");
				if (idxCreateUser != -1) {
					user.CreateUser = cursor.getString(idxCreateUser);
				}
				int idxDeleteFlag = cursor.getColumnIndex("DeleteFlag");
				if (idxDeleteFlag != -1) {
					user.DeleteFlag = cursor.getString(idxDeleteFlag);
				}
				list.add(user);
				user = null;
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

	public boolean avaiLogin(String userName, String pwd) {
		SQLiteDatabase database = null;
		boolean flag = false;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, null,
					"LoginName = ? and UserPassword = ? ", new String[] { userName,
							pwd }, null, null, null);
			if (cursor.moveToNext()) {
				flag = true;
			}
		} catch (Exception e) {
			System.out.println("----getUserInfo-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return flag;
	}

	public User getUserInfo(String userNmae) {
		UserBean bean = new UserBean();
		User user = null;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, null, "LoginName = ?",
					new String[] { userNmae }, null, null, null);
			if (cursor.moveToNext()) {
				user = bean.new User();
				int idxUserID = cursor.getColumnIndex("UserID");
				if (idxUserID != -1) {
					user.UserID = cursor.getString(idxUserID);
				}
				int idxTypeID = cursor.getColumnIndex("TypeID");
				if (idxTypeID != -1) {
					user.TypeID = cursor.getString(idxTypeID);
				}
				int idxUserName = cursor.getColumnIndex("UserName");
				if (idxUserName != -1) {
					user.UserName = cursor.getString(idxUserName);
				}
				int idxLoginName = cursor.getColumnIndex("LoginName");
				if (idxLoginName != -1) {
					user.LoginName = cursor.getString(idxLoginName);
				}
				int idxUserPassword = cursor.getColumnIndex("UserPassword");
				if (idxUserPassword != -1) {
					user.UserPassword = cursor.getString(idxUserPassword);
				}
				int idxUpdateTime = cursor.getColumnIndex("UpdateTime");
				if (idxUpdateTime != -1) {
					user.UpdateTime = cursor.getString(idxUpdateTime);
				}
				int idxCreateTime = cursor.getColumnIndex("CreateTime");
				if (idxCreateTime != -1) {
					user.CreateTime = cursor.getString(idxCreateTime);
				}
				int idxCreateUser = cursor.getColumnIndex("CreateUser");
				if (idxCreateUser != -1) {
					user.CreateUser = cursor.getString(idxCreateUser);
				}
				int idxDeleteFlag = cursor.getColumnIndex("DeleteFlag");
				if (idxDeleteFlag != -1) {
					user.DeleteFlag = cursor.getString(idxDeleteFlag);
				}
			}
		} catch (Exception e) {
			System.out.println("----getUserInfo-->" + e.getMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return user;
	}
}
