package com.publish.monitorsystem.api.bean;

import java.util.List;

public class InventoryEqptBean {
	public List<InventoryEqpt> ds;
	public class InventoryEqpt{
		public String ResultID;
		public String PlanID;
		public String ParentPlanID;
		public String EquipmentID;
		public String IsInventory;
	}
}
