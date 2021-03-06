package models;

import java.util.Set;
import utils.Utilities.LinkType;
import utils.Utilities.Style;

/**
 * @author - Tanjina Islam
 *
 * @date - 26-06-2019
 */
public class ComponentTransition {
	//	private SootClass sourceSootClass;
	//	private SootClass targetSootClass;
	private String sourceC;
	private String ICCMethod;
	private String targetC;
	private LinkType linkType;
	private Style style;
	private Set<String> invokedMethods;



	/**
	 * @return the style
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(Style style) {
		this.style = style;
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
		result = prime * result + ((sourceC == null) ? 0 : sourceC.hashCode());
		result = prime * result + ((targetC == null) ? 0 : targetC.hashCode());
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
		if (!(obj instanceof ComponentTransition)) {
			return false;
		}
		ComponentTransition other = (ComponentTransition) obj;
		if (linkType != other.linkType) {
			return false;
		}
		if (sourceC == null) {
			if (other.sourceC != null) {
				return false;
			}
		} else if (!sourceC.equals(other.sourceC)) {
			return false;
		}
		if (targetC == null) {
			if (other.targetC != null) {
				return false;
			}
		} else if (!targetC.equals(other.targetC)) {
			return false;
		}
		return true;
	}
}
