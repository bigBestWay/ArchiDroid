package models;

import java.util.Set;

import utils.Utilities.LinkType;

/**
 * @author - Tanjina Islam
 *
 * @date - 26-06-2019
 */
public class ComponentTransition {
	private String sourceC;
	private String ICCMethod;
	private String targetC;
	private LinkType linkType;
	private Set<String> invokedMethods;

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

	public Set<String> getInvokedMethods() {
		return invokedMethods;
	}

	public void setInvokedMethods(Set<String> invokedMethods) {
		this.invokedMethods = invokedMethods;
	}

	public String getSourceC() {
		return sourceC;
	}

	public void setSourceC(String sourceC) {
		this.sourceC = sourceC;
	}

	public String getICCMethod() {
		return ICCMethod;
	}

	public void setICCMethod(String iCCMethod) {
		ICCMethod = iCCMethod;
	}

	public String getTargetC() {
		return targetC;
	}

	public void setTargetC(String targetC) {
		this.targetC = targetC;
	}
	

	public ComponentTransition() {
	}

	public ComponentTransition(String sourceC, String ICCMethod, String targetC) {
		this.sourceC = sourceC;
		this.ICCMethod = ICCMethod;
		this.targetC = targetC;
	}
}
