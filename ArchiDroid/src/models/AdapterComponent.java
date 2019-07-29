package models;

/**
 * @author - Tanjina Islam
 *
 * @date - 02-07-2019
 */
public class AdapterComponent {

	private String className;
	private String componentKind;
	
	public AdapterComponent(String className, String componentKind) {
		this.className = className;
		this.componentKind = componentKind;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getComponentKind() {
		return componentKind;
	}
	public void setComponentKind(String componentKind) {
		this.componentKind = componentKind;
	}
	
	
}
