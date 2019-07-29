package models;

import java.util.List;

import soot.SootClass;
import soot.SootMethod;
import utils.Utilities.CompType;
/**
 * @author - Tanjina Islam
 *
 * @date - 15-07-2019
 */
public class AppComponent {

	private SootClass sootClass;
	private String className;
	private CompType componentType;
	private String compType;
	private List<SootMethod> classMethods;
	
	public List<SootMethod> getClassMethods() {
		return classMethods;
	}
	public void setClassMethods(List<SootMethod> classMethods) {
		this.classMethods = classMethods;
	}
	public String getCompType() {
		return compType;
	}
	public void setCompType(String compType) {
		this.compType = compType;
	}
	public CompType getComponentType() {
		return componentType;
	}
	public void setComponentType(CompType componentType) {
		this.componentType = componentType;
	}
	public SootClass getSootClass() {
		return sootClass;
	}
	public void setSootClass(SootClass sootClass) {
		this.sootClass = sootClass;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
}
