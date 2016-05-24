package com.publish.monitorsystem.api.bean;

import java.io.Serializable;
import java.util.List;

public class EqptBean implements Serializable{
	public List<Eqpt> ds;
	public class Eqpt implements Serializable{
		public String EquipmentID;
		public String TypeID;
		public String TypeName;
		public String EquipmentCode;
		public String FileCode;
		public String ImageName;
		public String OutFactoryNum;
		public String ProjectName;
		public String IsSecret;
		public String InitialValue;
		public String EquipmentName;
		public String Factory;
		public String FactoryName;
		public String UsePerson;
		public String ManagePerson;
		public String EquipmentSource;
		public String DepartmentID;
		public String DepartmentName;
		public String Specification;
		public String ManufactureDate;
		public String EquipmentPosition;
		public String State;
		public String Remark;
		public String CreateTime;
		public String TagID;
		public String EPC;
	}
}
