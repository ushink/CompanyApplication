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

		@Override
		public boolean equals (Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Eqpt eqpt = (Eqpt) o;

			if (EquipmentID != null ? !EquipmentID.equals(eqpt.EquipmentID) : eqpt.EquipmentID != null)
				return false;
			if (TypeID != null ? !TypeID.equals(eqpt.TypeID) : eqpt.TypeID != null) return false;
			if (TypeName != null ? !TypeName.equals(eqpt.TypeName) : eqpt.TypeName != null)
				return false;
			if (EquipmentCode != null ? !EquipmentCode.equals(eqpt.EquipmentCode) : eqpt.EquipmentCode != null)
				return false;
			if (FileCode != null ? !FileCode.equals(eqpt.FileCode) : eqpt.FileCode != null)
				return false;
			if (ImageName != null ? !ImageName.equals(eqpt.ImageName) : eqpt.ImageName != null)
				return false;
			if (OutFactoryNum != null ? !OutFactoryNum.equals(eqpt.OutFactoryNum) : eqpt.OutFactoryNum != null)
				return false;
			if (ProjectName != null ? !ProjectName.equals(eqpt.ProjectName) : eqpt.ProjectName != null)
				return false;
			if (IsSecret != null ? !IsSecret.equals(eqpt.IsSecret) : eqpt.IsSecret != null)
				return false;
			if (InitialValue != null ? !InitialValue.equals(eqpt.InitialValue) : eqpt.InitialValue != null)
				return false;
			if (EquipmentName != null ? !EquipmentName.equals(eqpt.EquipmentName) : eqpt.EquipmentName != null)
				return false;
			if (Factory != null ? !Factory.equals(eqpt.Factory) : eqpt.Factory != null)
				return false;
			if (FactoryName != null ? !FactoryName.equals(eqpt.FactoryName) : eqpt.FactoryName != null)
				return false;
			if (UsePerson != null ? !UsePerson.equals(eqpt.UsePerson) : eqpt.UsePerson != null)
				return false;
			if (ManagePerson != null ? !ManagePerson.equals(eqpt.ManagePerson) : eqpt.ManagePerson != null)
				return false;
			if (EquipmentSource != null ? !EquipmentSource.equals(eqpt.EquipmentSource) : eqpt.EquipmentSource != null)
				return false;
			if (DepartmentID != null ? !DepartmentID.equals(eqpt.DepartmentID) : eqpt.DepartmentID != null)
				return false;
			if (DepartmentName != null ? !DepartmentName.equals(eqpt.DepartmentName) : eqpt.DepartmentName != null)
				return false;
			if (Specification != null ? !Specification.equals(eqpt.Specification) : eqpt.Specification != null)
				return false;
			if (ManufactureDate != null ? !ManufactureDate.equals(eqpt.ManufactureDate) : eqpt.ManufactureDate != null)
				return false;
			if (EquipmentPosition != null ? !EquipmentPosition.equals(eqpt.EquipmentPosition) : eqpt.EquipmentPosition != null)
				return false;
			if (State != null ? !State.equals(eqpt.State) : eqpt.State != null) return false;
			if (Remark != null ? !Remark.equals(eqpt.Remark) : eqpt.Remark != null) return false;
			if (CreateTime != null ? !CreateTime.equals(eqpt.CreateTime) : eqpt.CreateTime != null)
				return false;
			if (TagID != null ? !TagID.equals(eqpt.TagID) : eqpt.TagID != null) return false;
			return EPC != null ? EPC.equals(eqpt.EPC) : eqpt.EPC == null;

		}

		@Override
		public int hashCode () {
			int result = EquipmentID != null ? EquipmentID.hashCode() : 0;
			result = 31 * result + (TypeID != null ? TypeID.hashCode() : 0);
			result = 31 * result + (TypeName != null ? TypeName.hashCode() : 0);
			result = 31 * result + (EquipmentCode != null ? EquipmentCode.hashCode() : 0);
			result = 31 * result + (FileCode != null ? FileCode.hashCode() : 0);
			result = 31 * result + (ImageName != null ? ImageName.hashCode() : 0);
			result = 31 * result + (OutFactoryNum != null ? OutFactoryNum.hashCode() : 0);
			result = 31 * result + (ProjectName != null ? ProjectName.hashCode() : 0);
			result = 31 * result + (IsSecret != null ? IsSecret.hashCode() : 0);
			result = 31 * result + (InitialValue != null ? InitialValue.hashCode() : 0);
			result = 31 * result + (EquipmentName != null ? EquipmentName.hashCode() : 0);
			result = 31 * result + (Factory != null ? Factory.hashCode() : 0);
			result = 31 * result + (FactoryName != null ? FactoryName.hashCode() : 0);
			result = 31 * result + (UsePerson != null ? UsePerson.hashCode() : 0);
			result = 31 * result + (ManagePerson != null ? ManagePerson.hashCode() : 0);
			result = 31 * result + (EquipmentSource != null ? EquipmentSource.hashCode() : 0);
			result = 31 * result + (DepartmentID != null ? DepartmentID.hashCode() : 0);
			result = 31 * result + (DepartmentName != null ? DepartmentName.hashCode() : 0);
			result = 31 * result + (Specification != null ? Specification.hashCode() : 0);
			result = 31 * result + (ManufactureDate != null ? ManufactureDate.hashCode() : 0);
			result = 31 * result + (EquipmentPosition != null ? EquipmentPosition.hashCode() : 0);
			result = 31 * result + (State != null ? State.hashCode() : 0);
			result = 31 * result + (Remark != null ? Remark.hashCode() : 0);
			result = 31 * result + (CreateTime != null ? CreateTime.hashCode() : 0);
			result = 31 * result + (TagID != null ? TagID.hashCode() : 0);
			result = 31 * result + (EPC != null ? EPC.hashCode() : 0);
			return result;
		}
	}
}
