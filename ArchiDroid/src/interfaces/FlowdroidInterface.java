package interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import models.AppComponent;
import models.ComponentTransition;
import soot.jimple.infoflow.android.axml.AXmlNode;
import utils.Utilities.CompType;

/**
 * @author - Tanjina Islam
 *
 * @date - 20-08-2019
 */
public interface FlowdroidInterface {
	
	public void initFlowdroid(String pathAndroidJars, String apkPath, String iccModelPath, String enableICC);
	public void addComp(List<AXmlNode> compNodeList, CompType compType, Set<AppComponent> appCompList);
	public Set<AppComponent> detectCoreComponents();
	public Set<ComponentTransition> findparentActivity() throws IOException, XmlPullParserException;
	
}
