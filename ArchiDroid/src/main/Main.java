package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import utils.ProjectConfig;
import utils.Utilities;
import writer.WriteOutputJson;

/**
 * @author - Tanjina Islam
 *
 * @date - 19-07-2019
 */

public class Main {

	private final static Logger logger = LoggerFactory.getLogger(Main.class);
	private final static String TAG = "[" + Main.class.getSimpleName() + "]";
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
	static Set <ComponentTransition> finalSetFragmentLinks;
	static Set <ComponentTransition> pojoCompTransitionSetFinal;
	static List<ComponentTransition> mergedComponentTransitionList;

	static Set<ComponentTransition> pojoCompTransition_2;
	static Set<ComponentTransition> pojoCompTransition_1;
	static long startTimeTemp;
	static long startTimeComp;
	static long startTimeLinkIcc;
	static long startTimeLinkDirect;
	static long startTimeWrite;

	public static void main(String [] args) {

		final long startTime = System.nanoTime();

		System.out.println("[ArchiDroid] Start Time : " + startTime);

		// Initialize the Sets
		init();
		// load the configuration file
		projectConfig = Utilities.loadConfig();

		// Run Flowdroid and create the call graph
		startTimeTemp = System.nanoTime();
		System.out.println("[ArchiDroid] Start Time to Run Flowdroid and create the call graph : " + startTimeTemp);
		Flowdroid.getInstance().initFlowdroid(projectConfig.getPathAndroidJars(), projectConfig.getApkPath(), projectConfig.getIccModelPath(), projectConfig.getIccConfig());
		System.out.println("[ArchiDroid] End Time to Run Flowdroid and create the call graph : " + System.nanoTime());
		printDuration("[ArchiDroid] Total time elapsed for Callgraph construction using Flowdroid : " , startTimeTemp);

		// Initiate the refinement 
		initiateRefinement();

		// Filter out the application classes
		filteredClassList = FilterClass.getInstance().filterClass(applicationClasses);
		if(!filteredClassList.isEmpty()) {
			logger.info(TAG + " Filtered Application Class List size - > " + filteredClassList.size()); 
		}

		// Retrieve Core Components(Activity, Service, Receiver, Provider) from Flowdroid
		startTimeComp = System.nanoTime();
		System.out.println("[ArchiDroid] Start Time App Component Extraction : " + startTimeComp);

		appComp = Flowdroid.getInstance().detectCoreComponents();

		if(!appComp.isEmpty()) {
			appComponentSetFinal.addAll(appComp);
			Flowdroid.getInstance().setAppCoreComponents(appComp);
		}else {
			logger.info(TAG + "NO Core Components found!");
		}

		/**
		 *  Refinement Part - START
		 */

		// Detects Fragments
		fragmentComp = FilterClass.getInstance().detectFragments(filteredClassList);
		//PrintMethods.printCompSet(fragmentComp);

		if(!fragmentComp.isEmpty()) {
			// Add the collection of fragments to the list
			appComponentSetFinal.addAll(fragmentComp);
		}else {
			logger.info(TAG + " No Fragments Found!");
		}

		// Detects Architectural POJOs
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
			//System.out.println("appComponentSetFinal List Size - > " + appComponentSetFinal.size());
		}

		System.out.println("[ArchiDroid] End Time App Component Extraction : " + System.nanoTime());
		printDuration("[ArchiDroid] Total duration for App Component Extraction : " , startTimeComp);


		/**
		 * Establishing ICC link - START
		 */
		startTimeLinkIcc = System.nanoTime();

		// Retrieve ICC components using IC3 and IccTA
		startTimeTemp = System.nanoTime();
		System.out.println("[ArchiDroid] Start Time resolving ICC using Flowdroid's IccTA and IC3 : " + startTimeTemp);
		componentTransitionGraph_iccta = resolveIcc(projectConfig.getIccModelPath());
		System.out.println("ICC CompTransition List Size IccTA - > " + componentTransitionGraph_iccta.size());
		System.out.println("[ArchiDroid] End Time resolving ICC using Flowdroid's IccTA and IC3 : " + System.nanoTime());
		printDuration("[ArchiDroid] Total time elapsed for resolving ICC using Flowdroid's IccTA and IC3 : " , startTimeTemp);


		// Parse the Component transition graph from Amandroid
		startTimeTemp = System.nanoTime();
		System.out.println("[ArchiDroid] Start Time parsing component transition graph using Amandroid : " + startTimeTemp);
		componentTransitionGraph_amandroid = XmlParser.getInstance().parseXml_Set(projectConfig.getFilePathAmandroid());
		System.out.println("ICC CompTransition List Size Amandroid - > " + componentTransitionGraph_amandroid.size());
		System.out.println("[ArchiDroid] End Time parsing component transition graph using Amandroid : " + System.nanoTime());
		printDuration("[ArchiDroid] Total time elapsed for parsing component transition graph using Amandroid : " , startTimeTemp);


		// Merging two ICC Component Transition Graphs from AMandroid and IccTA

		if(!componentTransitionGraph_iccta.isEmpty()) {
			componentTransitionSetFinal.addAll(componentTransitionGraph_iccta);
		}else {
			logger.info(TAG + " No ICC transition found from - > IccTA!");
		}

		if(!componentTransitionGraph_amandroid.isEmpty()) {
			// Add the collection of ICC Component Transitions to the list
			componentTransitionSetFinal.addAll(componentTransitionGraph_amandroid);
		}else {
			logger.info(TAG + " No ICC transition found from - > Amandroid!");
		}

		System.out.println("Comp Transition List Size after Merging outputs from Amandroid and IccTA -> " + componentTransitionSetFinal.size());

		System.out.println("[ArchiDroid] End Time ICC Links Extraction : " + System.nanoTime());
		printDuration("[ArchiDroid] Total duration for ICC Links Extraction : " , startTimeLinkIcc);


		/**
		 * Establishing ICC link - END
		 */

		/**
		 * Establish Direct Link - START
		 */

		startTimeLinkDirect = System.nanoTime();
		System.out.println("[ArchiDroid] Start Time Direct Links Extraction : " + startTimeLinkDirect);
		// Find Parent-Child Link between Parent Activity & Child Activity(If any)
		try {
			parentChildLink = Flowdroid.getInstance().findparentActivity();
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

		// Establish the Direct connection link for fragments
		if(!fragmentComp.isEmpty()) {

			finalSetFragmentLinks = FilterClass.getInstance().establishLink_fragments(fragmentComp);

		}

		if(!finalSetFragmentLinks.isEmpty()) {
			System.out.println("Component Transition List Size before adding Direct link to fragments  - > " + componentTransitionSetFinal.size());
			componentTransitionSetFinal.addAll(finalSetFragmentLinks);
			System.out.println("Component Transition List Size after adding Direct link to fragments  - > " + componentTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link for Fragments Found!");
		}

		// Find Parent-Child Link between Child Fragment and it's Parent Fragment(If any)
		Set<ComponentTransition> parentChildFragment = FilterClass.getInstance().findparentFragment(componentTransitionSetFinal, fragmentComp);

		if(!parentChildFragment.isEmpty()) {
			componentTransitionSetFinal.addAll(parentChildFragment);
			System.out.println("CompTransition List Size After adding Direct Link for Parent-Child Fragment - > " + componentTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link Found for a Child Fragment to it's Parent Fragment!");
		}

		// Establish the Direct connection link for architectural POJOs
		if(!architecturalPojoComp.isEmpty()) {

			startTimeTemp = System.nanoTime();
			System.out.println("[ArchiDroid] Start Time Direct Links Extraction architectural POJOs[Case 1] : " + startTimeTemp);
			pojoCompTransition_1 = FilterClass.getInstance().establishLink_POJOs_1(architecturalPojoComp);
			System.out.println("[ArchiDroid] End Time Direct Links Extraction architectural POJOs[Case 1] : " + System.nanoTime());
			printDuration("[ArchiDroid] Total time elapsed to establish direct connection links to architectural POJOs[Case 1] : " , startTimeTemp);

			startTimeTemp = System.nanoTime();
			System.out.println("[ArchiDroid] Start Time Direct Links Extraction architectural POJOs[Case 2] : " + startTimeTemp);
			pojoCompTransition_2 = FilterClass.getInstance().establishLink_POJOs_2(architecturalPojoComp); // This takes time!!!
			System.out.println("[ArchiDroid] End Time Direct Links Extraction architectural POJOs[Case 2] : " + System.nanoTime());
			printDuration("[ArchiDroid] Total time elapsed to establish direct connection links to architectural POJOs[Case 2] : " , startTimeTemp);

		}

		if(!pojoCompTransition_1.isEmpty()) {
			pojoCompTransitionSetFinal.addAll(pojoCompTransition_1);
			System.out.println("CompTransition List Size after POJOs Links Case 1  - > " + pojoCompTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link for Architectural POJOs Case 1 Found!");
		}

		if(!pojoCompTransition_2.isEmpty()) {
			pojoCompTransitionSetFinal.addAll(pojoCompTransition_2);
			System.out.println("CompTransition List Size after POJOs Links Case 2 - > " + pojoCompTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link for Architectural POJOs Case 2 Found!");
		}

		if(!pojoCompTransitionSetFinal.isEmpty()) {
			componentTransitionSetFinal.addAll(pojoCompTransitionSetFinal);
			System.out.println("CompTransition List Size after POJOs Links  - > " + componentTransitionSetFinal.size());
		}else {
			logger.info(TAG + " No Direct Link for Architectural POJOs Found!");
		}

		System.out.println("Final CompTransition List Size - > " + componentTransitionSetFinal.size());
		System.out.println("[ArchiDroid] End Time Direct Links Extraction : " + System.nanoTime());
		printDuration("[ArchiDroid] Total duration for Direct Links Extraction : " , startTimeLinkDirect);
		/**
		 * Establish Direct Link - END
		 */

		// Writing Outputs to output.JSON file
		startTimeWrite = System.nanoTime();
		System.out.println("[ArchiDroid] Start Time : " + startTimeWrite);
		if(!appComponentSetFinal.isEmpty() && !componentTransitionSetFinal.isEmpty()) {
			try {
				WriteOutputJson.writeToJSON(projectConfig.getOutputDir(), appComponentSetFinal, componentTransitionSetFinal);
				System.out.println("[ArchiDroid] End Time Writing Outputs to output.JSON file : " + System.nanoTime());
				printDuration("[ArchiDroid] Total time elapsed to write final reconstructed architecture to JSON : " , startTimeWrite);
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

		System.out.println("[ArchiDroid] End Time : " + System.nanoTime());
		printDuration("[ArchiDroid] Total duration for app architecture reconstruction : " , startTime);

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
		finalSetFragmentLinks= new LinkedHashSet<ComponentTransition>();
		pojoCompTransitionSetFinal= new LinkedHashSet<ComponentTransition>();
		mergedComponentTransitionList = new ArrayList<ComponentTransition>();

		pojoCompTransition_2 = new LinkedHashSet<ComponentTransition>();
		pojoCompTransition_1 = new LinkedHashSet<ComponentTransition>();

	}

	public static Set<ComponentTransition> resolveIcc(String iccModelPath) {
		IntentMapping intentMapping = new IntentMapping(iccModelPath);

		Set<ComponentTransition> componentTransitions =  intentMapping.resolveComponentsSet();

		return componentTransitions;
	}

	public static void initiateRefinement() {
		//manifest = Flowdroid.getInstance().getManifest(projectConfig.getApkPath());
		manifest = Flowdroid.getInstance().getManifest();
		packageName =  Flowdroid.getInstance().getPackageName(manifest);
		applicationClasses = Flowdroid.getInstance().getApplicationClasses();

		logger.info(TAG + " Flowdroid Application package name - > " + packageName);
		// Check and update package name to filter classes that reside inside the app package only
		FilterClass.getInstance().updatePackage(applicationClasses, packageName);
	}

	public static void printDuration(String message, long startTime) {
		long endTime   = System.nanoTime();
		long duration = endTime - startTime;
		long duration_inSeconds = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
		System.out.println(message + duration_inSeconds + " seconds");
	}
}