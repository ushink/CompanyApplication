package com.publish.monitorsystem.api.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.publish.monitorsystem.api.bean.RoomBean;
import com.publish.monitorsystem.api.bean.RoomBean.Room;
import com.publish.monitorsystem.api.db.DBHelper;

public class RoomDao{

	private DBHelper helper;

	private RoomDao(Context context) {
		helper = new DBHelper(context);
	}

	public static RoomDao instance;

	public synchronized static RoomDao getInstance(Context context) {
		if (instance == null) {
			instance = new RoomDao(context);
		}
		return instance;
	}

	private final String TABLE_NAME = "room";

	public boolean addRoom(Room room) {
		boolean flag = false;
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put("RoomID", room.RoomID);
			values.put("BuildingID", room.BuildingID);
			values.put("RoomName", room.RoomName);
			values.put("ThisFloor", room.ThisFloor);
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
	
	public boolean deleteAllRoom() {
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

	public List<Room> getAllRoomList() {
		List<Room> list = new ArrayList<Room>();
		RoomBean bean = new RoomBean();
		Room room;
		String sql = "select * from " + TABLE_NAME;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				room = bean.new Room();
				room.RoomID = cursor.getString(cursor
						.getColumnIndex("RoomID"));
				room.BuildingID = cursor.getString(cursor
						.getColumnIndex("BuildingID"));
				room.RoomName = cursor.getString(cursor
						.getColumnIndex("RoomName"));
				room.ThisFloor = cursor.getString(cursor
						.getColumnIndex("ThisFloor"));
				list.add(room);
				room = null;
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

	public String getRoomID(String roomName) {
		String str = null;
		SQLiteDatabase database = null;
		try {
			database = helper.getReadableDatabase();
			Cursor cursor = database.query(TABLE_NAME, new String[]{"RoomID"}, "RoomName = ?",
					new String[] { roomName }, null, null, null);
			if (cursor.moveToNext()) {
				str = cursor.getString(cursor.getColumnIndex("RoomID"));
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
