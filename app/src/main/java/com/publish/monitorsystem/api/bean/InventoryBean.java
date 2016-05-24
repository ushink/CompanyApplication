package com.publish.monitorsystem.api.bean;

import java.util.List;

public class InventoryBean {
	public List<Inventory> ds;
	public class Inventory{
		public String PlanID;
		public String ParentPlanID;
		public String TypeID;
		public String FunctionID;
		public String PlanName;
		public String BeginTimeTrue;
		public String EndTimeTrue;
	}
}
