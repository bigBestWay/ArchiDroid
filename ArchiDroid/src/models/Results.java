package models;

import java.util.Set;

/**
 * @author - Tanjina Islam
 *
 * @date - 15-07-2019
 */
public class Results {

	Set<AppComponent> components;
	Set<ComponentTransition> connections;

	public Set<AppComponent> getAppComponents() {
		return components;
	}
	public void setAppComponents(Set<AppComponent> components) {
		this.components = components;
	}
	public Set<ComponentTransition> getCompTranitions() {
		return connections;
	}
	public void setCompTranitions(Set<ComponentTransition> connections) {
		this.connections = connections;
	}


}
