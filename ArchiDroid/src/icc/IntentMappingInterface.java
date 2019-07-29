package icc;

import java.util.List;
import java.util.Set;

import models.AppComponent;
import models.ComponentTransition;

/**
 * @author - Tanjina Islam
 *
 * @date - 26-06-2019
 */
public interface IntentMappingInterface {
	public List<ComponentTransition> resolveComponents();
	public Set<ComponentTransition> resolveComponentsSet();
	public Set<AppComponent> retrieveCoreComponents();
}
