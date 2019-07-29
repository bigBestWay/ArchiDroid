package models;
import java.util.List;

import soot.Type;

public class ClassMethod {
	String methodName;
	List<Type> methodParamType;
	String returnType;
	
	public ClassMethod(String methodName, List<Type> methodParamType, String returnType) {
		this.methodName = methodName;
		this.methodParamType = methodParamType;
		this.returnType = returnType;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<Type> getMethodParamType() {
		return methodParamType;
	}
	public void setMethodParamType(List<Type> methodParamType) {
		this.methodParamType = methodParamType;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

}
