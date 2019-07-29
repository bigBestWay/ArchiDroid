package models;
import java.util.List;

public class ClassObject {
	
	String className;
	List<ClassField> classFieldList;
	List<ClassMethod> classMethodList;

	public ClassObject(String className, List<ClassField> classFieldList, List<ClassMethod> classMethodList) {
		this.className = className;
		this.classFieldList = classFieldList;
		this.classMethodList = classMethodList;
	}

	public String getClasName() {
		return className;
	}

	public void setClasName(String className) {
		this.className = className;
	}



	public List<ClassField> getClassFieldList() {
		return classFieldList;
	}

	public void setClassFieldList(List<ClassField> classFieldList) {
		this.classFieldList = classFieldList;
	}

	public List<ClassMethod> getClassMethodList() {
		return classMethodList;
	}

	public void setClassMethodList(List<ClassMethod> classMethodList) {
		this.classMethodList = classMethodList;
	}

	@Override
	public String toString() {
		String fieldName = null;
		String fieldType = null;
		
		String methodName = null;
		String methodParamType = null;
		String methodReturnType = null;
		
		String outputString = "<className = " + className + ">\n\t<classField>";
		
		if(!classFieldList.isEmpty()) {
			for(ClassField classField : classFieldList) {
				fieldName = classField.getFieldName();
				fieldType = classField.getFieldType();
				
				outputString += "\n\t\t<fieldName> " + fieldName + " </fieldName>\n\t\t<fieldType> " + fieldType + " </fieldType>\n";
			}
			outputString += "\t</classField>";
		}
		outputString += "\n\t<classMethod>";
		if(!classMethodList.isEmpty()) {
			for(ClassMethod classMethod : classMethodList) {
				methodName = classMethod.getMethodName();
				methodParamType = classMethod.getMethodParamType().toString();
				methodReturnType = classMethod.getReturnType();
				
				outputString += "\n\t\t<methodName> " + methodName + " </methodName>\n\t\t<methodParamType> " + methodParamType + " </methodParamType>\n\t\t<methodReturnType> " + methodReturnType + " </methodReturnType>\n";
			}
			outputString += "\t</classMethod>";
		}
		return outputString += "\n</className = " + className + ">" ;
	}




}
