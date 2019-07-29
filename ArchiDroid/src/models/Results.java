package models;

import java.util.Set;

/**
 * @author - Tanjina Islam
 *
 * @date - 15-07-2019
 */
public class Results {

	Set<AppComponent> appComponents;
	Set<ComponentTransition> compTranitions;

	public Set<AppComponent> getAppComponents() {
		return appComponents;
	}
	public void setAppComponents(Set<AppComponent> appComponents) {
		this.appComponents = appComponents;
	}
	public Set<ComponentTransition> getCompTranitions() {
		return compTranitions;
	}
	public void setCompTranitions(Set<ComponentTransition> compTranitions) {
		this.compTranitions = compTranitions;
	}


}
