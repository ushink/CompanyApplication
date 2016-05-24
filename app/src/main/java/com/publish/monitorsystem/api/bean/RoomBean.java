package com.publish.monitorsystem.api.bean;

import java.util.List;

public class RoomBean {
	public List<Room> ds;
	public class Room{
		public String RoomID;
		public String BuildingID;
		public String RoomName;
		public String ThisFloor;
	}
}
