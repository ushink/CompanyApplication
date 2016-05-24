package com.publish.monitorsystem.api.bean;

import java.util.List;

public class UserBean {
	public List<User> ds;
	public class User{
		public String UserID;
		public String TypeID;
		public String UserName;
		public String LoginName;
		public String UserPassword;
		public String UpdateTime;
		public String CreateTime;
		public String CreateUser;
		public String DeleteFlag;
	}
}
