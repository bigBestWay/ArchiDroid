package models;

public class ClassField {
	
	String fieldName;
	String fieldType;
	
	public ClassField(String fieldName, String fieldType) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

}
