package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;
import flowdroid.Flowdroid;
import helper.FilterClass;
import iccta.IntentMapping;
import models.AppComponent;
import models.ComponentTransition;
import parser.XmlParser;
import soot.SootClass;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.util.Chain;
import utils.PrintMethods;
import utils.ProjectConfig;
import utils.Utilities;
import writer.WriteOutputJson;

/**
 * @author - Tanjina Islam
 *
 * @date - 19-07-2019
 */

public class MainArchiDroid {

	private final static Logger logger = LoggerFactory.getLogger(MainArchiDroid.class);
	private final static String TAG = "[" + MainArchiDroid.class.getSimpleName() + "]";
	static ProjectConfig projectConfig;
	static Set<ComponentTransition> componentTransitionGraph_amandroid;
	static Set<ComponentTransition> componentTransitionGraph_iccta;
	static Set<ComponentTransition> componentTransitionSetFinal;
	static Set<ComponentTransition> componentTransitionSetFragment;
	static List<ComponentTransition> mergedList;
	static List<ComponentTransition> finalList;
	static Set <ComponentTransition> finalSet;
	static Set<ComponentTransition> parentChildLink;
	static Set<ComponentTransition> parentChildFragmentLink;
	static ProcessManifest manifest;
	static String packageName;
	static String updatedPackage;
	static Chain<SootClass> applicationClasses;
	static Set<SootClass> filteredClassList;
	static Set<AppComponent> fragmentComp;
	static Set<AppComponent> appComp;
	static Set<AppComponent> architecturalPojoComp;
	static Set<AppComponent> appComponentSetFinal;
	static Set<ComponentTransition> componentTransitions;
	static List<ComponentTransition> mergedListFragment;
	static Set <ComponentTransition> finalSetFragment;
	static Set <ComponentTransition> finalSetArchitecturalPojos;
	static List<ComponentTransition> mergedComponentTransitionList;

	public static void main(String [] args) {

		// Initialize the Sets
		init();
		// load the configuration file
		projectConfig = Utilities.loadConfig();

		// Run Flowdroid and create the call graph
		Flowdroid.getInstance().initFlowdroid(projectConfig.getPathAndroidJars(), projectConfig.getApkPath(), projectConfig.getIccModelPath());

		// Initiate the refinement 
		initiateRefinement();

		// Filter out the application classes that 
		filteredClassList = FilterClass.getInstance().filterClass(applicationClasses);
		if(!filteredClassList.isEmpty()) {
			logger.info(TAG + " Filtered Application Class List size - > " + filteredClassList.size()); 
		}

		// Retrieve Core Components(Activity, Service, Receiver, Provider) from Flowdroid
		appComp = Flowdroid.getInstance().detectCoreComponents(projectConfig.getApkPath());

		if(!appComp.isEmpty()) {
			appComponentSetFinal = new LinkedHashSet<AppComponent>(appComp);
			//PrintMethods.printCompSet(appComp);
			Flowdroid.getInstance().setAppCoreComponents(appComp);
		}else {
			logger.info(TAG + "NO Core Components found!");
			appComponentSetFinal = new LinkedHashSet<AppComponent>();
		}

		/**
		 * Establishing ICC link - START
		 */

		// Parse the Component transition graph from Amandroid
		componentTransitionGraph_amandroid = XmlParser.getInstance().parseXml_Set(projectConfig.getFilePathAmandroid());
		System.out.println("CompTransition List Size Amandroid - > " + componentTransitionGraph_amandroid.size());

		// Retrieve ICC components using IC3 and IccTA
		componentTransitionGraph_iccta = resolveIcc(projectConfig.getIccModelPath());
		System.out.println("CompTransition List Size IccTA - > " + componentTransitionGraph_iccta.size());

		// Merging two Component Transition Graphs from AMandroid and IccTA

		if(!componentTransitionGraph_iccta.isEmpty()) {
			componentTransitionSetFinal = new LinkedHashSet<ComponentTransition>(componentTransitionGraph_iccta);
		}else {
			logger.info(TAG + " No ICC transition found from - > IccTA!");
			componentTransitionSetFinal = new LinkedHashSet<ComponentTransition>();
		}

		if(!componentTransitionGraph_amandroid.isEmpty()) {
			// Add the collection of fragments to the list
			componentTransitionSetFinal.addAll(componentTransitionGraph_amandroid);
		}else {
			logger.info(TAG + " No ICC transition found from - > Amandroid!");
		}

		System.out.println("Comp Transition List Size after Merging outputs from Amandroid and IccTA -> " + componentTransitionSetFinal.size());

		/**
		 * Establishing ICC link - END
		 */

		/**
		 *  Refinement Part - START
		 */

		// Filter outputs to remove duplicates from ICC transition

		if(!componentTransitionSetFinal.isEmpty()) {
			mergedList = new ArrayList<>(componentTransitionSetFinal);
		}

		if(!mergedList.isEmpty()) {
			componentTransitionSetFinal = FilterClass.getInstance().removeDuplicates(mergedList);
		}

		// Detects Fragments
		fragmentComp = FilterClass.getInstance().detectFragments(filteredClassList);
		//PrintMethods.printCompSet(fragmentComp);

		if(!fragmentComp.isEmpty()) {
			// Add the collection of fragments to the list
			appComponentSetFinal.addAll(fragmentComp);
		}else {
			logger.info(TAG + " No Fragments Found!");
		}


		//		if(!componentTransitions.isEmpty() && !fragmentComp.isEmpty()) {
		//			componentTransitionSetFragment = detectParentChildFragmentLink(componentTransitions, fragmentComp);
		//		}


		architecturalPojoComp = FilterClass.getInstance().detectArchitecturalPojos(filteredClassList);
		//PrintMethods.printCompSet(architecturalPojoComp);

		if(!architecturalPojoComp.isEmpty()) {
			// Add the collection of architectural POJOs to the list
			appComponentSetFinal.addAll(architecturalPojoComp);
		}else {
			logger.info(TAG + " No Architectural POJOs Found!");
		}

		if(!appComponentSetFinal.isEmpty()) {
			FilterClass.getInstance().setAppComponents(appComponentSetFinal);
		}

		/**
		 * Establish Direct Link - START
		 */

		// Find Direct Link between Parent Activity & Child Activity(If any)
		try {
			parentChildLink = Flowdroid.getInstance().findparentActivity(projectConfig.getApkPath());
		} catch (IOException | XmlPullParserException e) {
			//e.printStackTrace();
			logger.error(TAG + " Exception while invoking findParentActivity() method " + e.getMessage()); 
		}
		if(!parentChildLink.isEmpty()) {
			componentTransitionSetFinal.addAll(parentChildLink);
			System.out.println("CompTransition List Size After adding Direct Link for Parent-Child Activity - > " + componentTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link between Parent-Child Activity Found!");
		}



		// Establish the connection link for fragments
		if(!fragmentComp.isEmpty() && !componentTransitionSetFinal.isEmpty() && !filteredClassList.isEmpty()) {
			finalSetFragment = FilterClass.getInstance().establishLink_fragments(componentTransitionSetFinal, filteredClassList, fragmentComp);
		}

		if(!finalSetFragment.isEmpty()) {
			componentTransitionSetFinal.addAll(finalSetFragment);
			System.out.println("CompTransition List Size after Fragments Links - > " + componentTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link for Fragments Found!");
		}

		// Establish the connection link for architectural POJOs
		if(!architecturalPojoComp.isEmpty() && !componentTransitionSetFinal.isEmpty() && !filteredClassList.isEmpty()) {
			finalSetArchitecturalPojos = FilterClass.getInstance().establishLink_POJOs(componentTransitionSetFinal, filteredClassList, architecturalPojoComp);
			//finalSetArchitecturalPojos = FilterClass.getInstance().establishLink_POJOs_Test(appComponentSetFinal, componentTransitionSetFinal, filteredClassList, architecturalPojoComp);
		}

		if(!finalSetArchitecturalPojos.isEmpty()) {
			componentTransitionSetFinal.addAll(finalSetArchitecturalPojos);
			System.out.println("CompTransition List Size after POJOs Links  - > " + componentTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link for Architectural POJOs Found!");
		}

		// Merging Component Transitions for Direct Link 
		mergedComponentTransitionList = new ArrayList<>(componentTransitionSetFinal);

		// Filter outputs to remove duplicates from Direct transition
		componentTransitionSetFinal = FilterClass.getInstance().removeDuplicates(mergedComponentTransitionList); 
		System.out.println("CompTransition List Size Final Final - > " + componentTransitionSetFinal.size());

		/**
		 * Establish Direct Link - END
		 */

		// Writing Outputs to output.JSON file
		if(!appComponentSetFinal.isEmpty() && !componentTransitionSetFinal.isEmpty()) {
			try {
				WriteOutputJson.writeToJSON(projectConfig.getOutputDir(), appComponentSetFinal, componentTransitionSetFinal);
			} catch (IOException e) {
				logger.error(TAG + " Exception while writing output in JSON file " + e.getMessage());
			}
		}else{
			if(appComponentSetFinal.isEmpty()) {
				logger.error(TAG + " NO Components found!\n Component List - > Empty");
				// Stop the system - > Exit
			}
			if(componentTransitionSetFinal.isEmpty()) {
				logger.error(TAG + " NO ICC Transitions found!\n Component Transition Graph - > Empty");
				// Stop the system - > Exit
			}
		}

		/**
		 *  Refinement Part - END
		 */
	}

	public static void init() {
		componentTransitionGraph_amandroid = new LinkedHashSet<ComponentTransition>();
		componentTransitionGraph_iccta = new LinkedHashSet<ComponentTransition>();
		componentTransitionSetFinal = new LinkedHashSet<ComponentTransition>();
		componentTransitionSetFragment = new LinkedHashSet<ComponentTransition>();
		mergedList = new ArrayList<ComponentTransition>();
		finalList = new ArrayList<ComponentTransition>();
		finalSet = new LinkedHashSet<ComponentTransition>();
		parentChildLink = new LinkedHashSet<ComponentTransition>();
		parentChildFragmentLink = new LinkedHashSet<ComponentTransition>();
		filteredClassList = new LinkedHashSet<SootClass>();
		fragmentComp= new LinkedHashSet<AppComponent>();
		appComp= new LinkedHashSet<AppComponent>();
		architecturalPojoComp= new LinkedHashSet<AppComponent>();
		appComponentSetFinal= new LinkedHashSet<AppComponent>();
		componentTransitions= new LinkedHashSet<ComponentTransition>();
		mergedListFragment = new ArrayList<ComponentTransition>();
		finalSetFragment= new LinkedHashSet<ComponentTransition>();
		finalSetArchitecturalPojos= new LinkedHashSet<ComponentTransition>();
		mergedComponentTransitionList = new ArrayList<ComponentTransition>();
	}

	public static Set<ComponentTransition> resolveIcc(String iccModelPath) {
		IntentMapping intentMapping = new IntentMapping(iccModelPath);

		Set<ComponentTransition> componentTransitions =  intentMapping.resolveComponentsSet();

		return componentTransitions;
	}

	//	public static Set<ComponentTransition> detectParentChildFragmentLink(Set<ComponentTransition> componentTransitions, Set <AppComponent> fragmentComp){
	//
	//		if(!fragmentComp.isEmpty()) {
	//			parentChildFragmentLink = FilterClass.getInstance().findparentFragment(componentTransitions, fragmentComp);
	//
	//			//System.out.println("List Size findparentActivity() -> " + parentChildLink.size());
	//			if(!parentChildFragmentLink.isEmpty()) {
	//				componentTransitionSetFragment = mergeCompTransitionSets(componentTransitions, parentChildFragmentLink);
	//			}
	//		}
	//
	//		return componentTransitionSetFragment;
	//
	//	}

	public static void initiateRefinement() {
		//manifest = Flowdroid.getInstance().getManifest(projectConfig.getApkPath());
		manifest = Flowdroid.getInstance().getManifest();
		packageName =  Flowdroid.getInstance().getPackageName(manifest);
		applicationClasses = Flowdroid.getInstance().getApplicationClasses();

		logger.info(TAG + " Flowdroid Application package name - > " + packageName);
		// Check and update package name to filter classes that reside inside the app package only
		FilterClass.getInstance().updatePackage(applicationClasses, packageName);
	}
}
