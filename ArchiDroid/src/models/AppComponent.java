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
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((componentType == null) ? 0 : componentType.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AppComponent)) {
			return false;
		}
		AppComponent other = (AppComponent) obj;
		if (className == null) {
			if (other.className != null) {
				return false;
			}
		} else if (!className.equals(other.className)) {
			return false;
		}
		if (componentType != other.componentType) {
			return false;
		}
		return true;
	}
	
}
