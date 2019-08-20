package flowdroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;
import helper.FilterClass;
import models.AppComponent;
import models.ComponentTransition;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.util.Chain;
import utils.Utilities;
import utils.Utilities.CompType;

/**
 * @author - Tanjina Islam
 *
 * @date - 27-06-2019
 */
public class Flowdroid {

	private final static Logger logger = LoggerFactory.getLogger(Flowdroid.class);
	private final static String TAG = "[" + Flowdroid.class.getSimpleName() + "]";

	private static Flowdroid instance = null;//singleton instance

	private ProcessManifest manifest;
	private String packageName;
	private Chain<SootClass> applicationClasses;
	private Set<AppComponent> appCoreComponents;


	public Set<AppComponent> getAppCoreComponents() {
		return appCoreComponents;
	}
	public void setAppCoreComponents(Set<AppComponent> appCoreComponents) {
		this.appCoreComponents = appCoreComponents;
	}
	public Chain<SootClass> getApplicationClasses() {
		return applicationClasses;
	}
	public void setApplicationClasses(Chain<SootClass> applicationClasses) {
		this.applicationClasses = applicationClasses;
	}
	public String getPackageName(ProcessManifest manifestInstance) {
		return manifestInstance.getPackageName();
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	private Flowdroid() {

	}
	public static Flowdroid getInstance() {
		if(instance == null) {
			instance = new Flowdroid();
		}
		return instance;
	}

	public ProcessManifest getManifest() {
		return manifest;
	}
	public void setManifest(String apkPath) {
		try {
			this.manifest = new ProcessManifest(apkPath);
		}catch (IOException | XmlPullParserException e) {
			//e.printStackTrace();
			logger.error(TAG + " Exception in Process Manifest " + e.getMessage());
		}
	}

	public void initFlowdroid(String pathAndroidJars, String apkPath, String iccModelPath, String enableICC) {

		InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
		config.getAnalysisFileConfig().setAndroidPlatformDir(pathAndroidJars);
		config.getAnalysisFileConfig().setTargetAPKFile(apkPath);
		config.setExcludeSootLibraryClasses(true);

		if(enableICC.equalsIgnoreCase(Utilities.ICCConfig.ENABLE.name())) {
			config.getIccConfig().setIccModel(iccModelPath); // Need to disable it for k-9 mail app, OpenSudoku app! Otherwise It will through Wrong ICC Link from IccTA in Flowdroid
		}

		SetupApplication analyzer = new SetupApplication(config);
		analyzer.constructCallgraph();
		CallGraph appCallGraph = Scene.v().getCallGraph();

		applicationClasses = Scene.v().getApplicationClasses();
		System.out.println("SOOT : Application Class List size Before filtering - > " + applicationClasses.size());
		setManifest(apkPath);
		setApplicationClasses(applicationClasses);
	}

	public void addComp(List<AXmlNode> compNodeList, CompType compType, Set<AppComponent> appCompList) {

		//applicationClasses = getApplicationClasses();

		Set<SootClass> filteredClassList = FilterClass.getInstance().filterClass(applicationClasses);

		String updatedPackage = FilterClass.getInstance().getUpdatedpackage();



		for(AXmlNode appComp : compNodeList) {
			String appCompName = appComp.getAttribute("name").getValue().toString(); //  component
			//			if(appCompName.contains(".")) {
			if(updatedPackage != null && !appCompName.startsWith(updatedPackage) && appCompName.startsWith(".")) {
				//appCompName = appCompName.substring(appCompName.lastIndexOf(".") + 1);
				appCompName = updatedPackage + appCompName;
			}

			for(SootClass sootClass : filteredClassList) {

				if(appCompName.equalsIgnoreCase(sootClass.getName())) {
					AppComponent comp = new AppComponent();
					comp.setClassName(sootClass.getName());
					comp.setSootClass(sootClass);
					if(!sootClass.getMethods().isEmpty()) {
						comp.setClassMethods(sootClass.getMethods());
					}else {
						comp.setClassMethods(null);
					}
					comp.setComponentType(compType);

					appCompList.add(comp);
				}
			}
		}
	}
	public Set<AppComponent> detectCoreComponents() { 

		Set<AppComponent> appCompList = new LinkedHashSet<AppComponent>();
		//manifest = getManifest(apkPath);

		List<AXmlNode> activities = new ArrayList<AXmlNode>();
		activities = manifest.getActivities();

		if(!activities.isEmpty()) {
			// Iterate all the Activities
			addComp(activities, utils.Utilities.CompType.Activity, appCompList);
		}else {
			logger.info(TAG + " No Activity found in the manifest file");
			//return appCompList;
		}

		List<AXmlNode> services = new ArrayList<AXmlNode>();
		services = manifest.getServices();

		if(!services.isEmpty()) {
			// Iterate all the Services
			addComp(services, utils.Utilities.CompType.Service, appCompList);
		}else {
			logger.info(TAG + " No Service found in the manifest file");
			//return appCompList;
		}

		List<AXmlNode> receivers = new ArrayList<AXmlNode>();
		receivers = manifest.getReceivers();

		if(!receivers.isEmpty()) {
			// Iterate all the Receivers
			addComp(receivers, utils.Utilities.CompType.Receiver, appCompList);
		}else {
			logger.info(TAG + " No Receiver found in the manifest file");
			//return appCompList;
		}

		List<AXmlNode> providers = new ArrayList<AXmlNode>();
		providers = manifest.getProviders();

		if(!providers.isEmpty()) {
			// Iterate all the Providers
			addComp(providers, utils.Utilities.CompType.Provider, appCompList);
		}else {
			logger.info(TAG + " No Provider found in the manifest file");
			//return appCompList;
		}

		System.out.println("Android Core Component List Size -> " + appCompList.size());

		return appCompList;

	}

	// Find Parent-Child Activity Direct Link from Manifest's "parentActivityName" tag
	//Transition: From Child Activity To Parent Activity on 'back button pressed'
	public Set<ComponentTransition> findparentActivity() throws IOException, XmlPullParserException {

		Set<ComponentTransition> localList = new LinkedHashSet<ComponentTransition>();

		List<AXmlNode> activities = new ArrayList<AXmlNode>();
		activities = manifest.getActivities();


		if(activities.isEmpty()) {
			logger.error(TAG + " No Activities found in the manifest file");
			return localList;
		}

		// Iterate all the Activities to extract parent-child links
		Iterator<AXmlNode> iter = activities.iterator();

		while(iter.hasNext()){
			AXmlNode activity = iter.next();
			if(activity.hasAttribute("parentActivityName")) {

				String parentActivity = activity.getAttribute("parentActivityName").getValue().toString(); // Target component 
				String childActivity = activity.getAttribute("name").getValue().toString(); // Source component

				ComponentTransition componentTransition = new ComponentTransition();
				componentTransition.setSourceC(childActivity);
				componentTransition.setTargetC(parentActivity);
				componentTransition.setLinkType(Utilities.LinkType.ParentChild);

				localList.add(componentTransition);
			}  
		}
		return localList;
	}
}
