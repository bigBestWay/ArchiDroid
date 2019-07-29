//package main;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.xmlpull.v1.XmlPullParserException;
//
//import flowdroid.Flowdroid;
//import helper.DetectGettersSetters;
//import helper.FilterClass;
//import iccta.Ic3Data.Application.Component;
//import iccta.IntentMapping;
//import models.AdapterComponent;
//import models.AppComponent;
//import models.ComponentTransition;
//import models.FragmentTransition;
//import models.PlainJavaComp;
//import models.Results;
//import parser.XmlParser;
//import soot.Local;
//import soot.Scene;
//import soot.SootClass;
//import soot.SootField;
//import soot.SootMethod;
//import soot.Type;
//import soot.Unit;
//import soot.UnitPatchingChain;
//import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
//import soot.jimple.infoflow.android.SetupApplication;
//import soot.jimple.infoflow.android.manifest.ProcessManifest;
//import soot.jimple.toolkits.callgraph.CallGraph;
//import soot.util.Chain;
//import utils.PrintMethods;
//import utils.ProjectConfig;
//import utils.Utilities;
//import writer.WriteOutputJson;
//
///**
// * @author - Tanjina Islam
// *
// * @date - 26-06-2019
// */
//public class Main {
//
//	private final static Logger logger = LoggerFactory.getLogger(Main.class);
//	private final static String TAG = "[" + Main.class.getSimpleName() + "]";
//	static ProcessManifest manifest;
//	static String packageName;
//	static String updatedPackage;
//	static Chain<SootClass> applicationClasses;
//	static List<ComponentTransition> componentTransitionGraph_aman;
//	static List<ComponentTransition> componentTransitionGraph_iccta;
//	static Set<ComponentTransition> componentTransitionSetFinal;
//	static List<ComponentTransition> parentChildLink;
//	static List<ComponentTransition> finalList;
//	static Set<ComponentTransition> tempSet ;
//	static ProjectConfig projectConfig;
//	static Set<ComponentTransition> componentTransitions;
//
//	public static void main(String [] args) {
//
//		// load the configuration file
//		projectConfig = Utilities.loadConfig();
//
//		// Run Flowdroid and create the call graph
//		//		Flowdroid.getInstance().initFlowdroid();
//		Flowdroid.getInstance().initFlowdroid(projectConfig.getPathAndroidJars(), projectConfig.getApkPath(), projectConfig.getIccModelPath());
//
//		// For Testing 
//		//Flowdroid.getInstance().detectComponents(projectConfig.getApkPath());
//		
//		// Parse the Component transition graph from Amandroid
//		componentTransitionGraph_aman = XmlParser.getInstance().parseXml(projectConfig.getFilePathAmandroid());
//		//		componentTransitionGraph_aman = XmlParser.getInstance().parseXml();
//		//PrintMethods.printComponentTransitionList(TAG, componentTransitionGraph_aman);
//		System.out.println("List Size Amandroid -> " + componentTransitionGraph_aman.size());
//		
//		Set <ComponentTransition> newList = FilterClass.getInstance().removeDuplicates(componentTransitionGraph_aman); // remove duplicates 
//		//PrintMethods.printComponentTransitionList("Remove Duplicates", newList);
//		
//		// Retrieve ICC components 
//		componentTransitionGraph_iccta = resolveIcc(projectConfig.getIccModelPath());
//
//		System.out.println("List Size after resolveIcc() -> " + componentTransitionGraph_iccta.size());
//
//		if(componentTransitionGraph_aman.isEmpty() && componentTransitionGraph_iccta.isEmpty()) {
//			logger.error(TAG + " Amandroid Graph - > Empty\n IccTA Graph - > Empty");
//		}
//		else if(!componentTransitionGraph_aman.isEmpty()) {
//
//			//			tempSet = new LinkedHashSet<>(componentTransitionGraph_aman);
//			//			System.out.println("I'm Here -> " + tempSet.size());
//			//componentTransitionSetFinal = mergeLatest(tempSet, componentTransitionGraph_iccta);
//
//			componentTransitionSetFinal = merge_new(componentTransitionGraph_aman, componentTransitionGraph_iccta);
//
//		}else if(!componentTransitionGraph_iccta.isEmpty()) {
//			//PrintMethods.compTransitionWithICCMethod(TAG, componentTransitionGraph_iccta);
//			//componentTransitionSetFinal = mergeComp(componentTransitionGraph_aman, componentTransitionGraph_iccta);
//			//			componentTransitionSetFinal = merge_new(componentTransitionGraph_aman, componentTransitionGraph_iccta);
//			//			System.out.println("List Size after merging -> " + componentTransitionSetFinal.size());
//
//			//			tempSet = new LinkedHashSet<>(componentTransitionGraph_iccta);
//			//			componentTransitionSetFinal = mergeLatest(tempSet, componentTransitionGraph_aman);
//
//			componentTransitionSetFinal = merge_new(componentTransitionGraph_iccta, componentTransitionGraph_aman);
//
//		}
//		//PrintMethods.printMergedCompSet(componentTransitionSetFinal);
//		System.out.println("List Size Merged -> " + componentTransitionSetFinal.size());
//
//		if(!componentTransitionSetFinal.isEmpty()) {
//			finalList = new ArrayList<>(componentTransitionSetFinal);
//		}
//
//
//		try {
//
//			if(!componentTransitionGraph_iccta.isEmpty()) {
//				parentChildLink = Flowdroid.getInstance().findparentActivity(projectConfig.getApkPath(), componentTransitionGraph_iccta);
//
//				System.out.println("List Size findparentActivity() -> " + parentChildLink.size());
//				if(!parentChildLink.isEmpty()) {
//					//componentTransitionSetFinal = mergeComp(finalList, parentChildLink);
//					componentTransitionSetFinal = merge_new(finalList, parentChildLink);
//					//	PrintMethods.printComponentTransitionList(TAG, compTrans);
//				}
//			}
//
//		} catch (IOException | XmlPullParserException e) {
//			//e.printStackTrace();
//			logger.error(TAG + " Exception while invoking findParentActivity() method ", e.getMessage()); 
//		}
//
//		// Print icc components aman + iccta
//		//PrintMethods.printMergedCompSet(componentTransitionSetFinal);
//
//		// Run Flowdroid and create the call graph
//		//Flowdroid.getInstance().initFlowdroid();
//
//		// Retrieve Helper Class(Components)
//		initiateRefinement();
//		logger.info(TAG + " Flowdroid Application package name - > " + packageName);
//		// Check and update package name to filter classes that resided inside the app package only
//		FilterClass.getInstance().updatedPackage(applicationClasses, packageName);
//
//		String newPackageName = FilterClass.getInstance().getUpdatedpackage();
//
//
//		logger.info(TAG + " Flowdroid Application Class List size - > " + applicationClasses.size()); 
//
//		if(newPackageName != null) {
//			updatedPackage = newPackageName;
//		}
//		logger.info(TAG + " New updated package -> " + updatedPackage); 
//
//		List<SootClass> filteredClassList = FilterClass.getInstance().filteredClassList(applicationClasses, updatedPackage);
//
//		if(!filteredClassList.isEmpty()) {
//			logger.info(TAG + " Filtered Application Class List size - > " + filteredClassList.size()); 
//
//
//
//			//check fragments start
//			List<FragmentTransition> fragmentTransitionList = FilterClass.getInstance().fragmentTransition(filteredClassList, componentTransitionSetFinal);
//			logger.info(TAG + " Fragment Transition List size - > " + fragmentTransitionList.size()); 
//			// check fragment end
//
//			//detects adapter start
//			Set<AdapterComponent> adapterSet = FilterClass.getInstance().detectAdapters(filteredClassList);
//			if(!adapterSet.isEmpty()) {
//				for(AdapterComponent adapterComp : adapterSet) {
//					System.out.println("Adapter Class Name - > " + adapterComp.getClassName());
//					System.out.println("Component Kind - > " + adapterComp.getComponentKind());
//				}
//			}
//			//detects adapter end
//
//			//check for getter/setter class start
//			for(SootClass sClass : filteredClassList) {
//
//				FilterClass.getInstance().isAdapter(sClass);
//
//				if(FilterClass.getInstance().isSingletonClass(sClass)) {
//					System.out.println("Singleton -> " + sClass);
//				}
//				if(checkGetterSetter(sClass) == 0) {
//					System.out.println(" No Get/Set methods found in class -> " + sClass);
//					FilterClass.getInstance().filterByMethodNames(sClass);
//				}if(checkGetterSetter(sClass) == 1) {
//					System.out.println(" Get/Set method count < total method count * 0.5 So Possible to include class -> " + sClass);
//				}
//
//				// FOR TESTING START
//				if(sClass.getName().equalsIgnoreCase("it.feio.android.omninotes.utils.date.ReminderPickers")) { //Display, KeyboardUtils, BitmapHelper 
//					List<SootMethod> classMethods = sClass.getMethods();
//					List<SootMethod> getSetList = DetectGettersSetters.getInstance().findGettersSetters(sClass);
//					List<SootMethod> filteredList = new ArrayList<>();
//
//
//					for(SootMethod sMethod : classMethods) {
//						if(!getSetList.isEmpty()) {
//							for(SootMethod sMethod2 : getSetList) {
//								if(sMethod.getName().matches(sMethod2.getName())) {
//									System.out.println("Matched get/set - > " + sMethod2.getName());
//								}else {
//									filteredList.add(sMethod);
//								}
//							}
//						}else {
//							filteredList = classMethods;
//						}
//
//					}
//					System.out.println("New method list size - > " + filteredList.size()); 
//					for(SootMethod sMethod : filteredList) {
//						System.out.println("Display class method name  - > " + sMethod.getName());
//						int isMatch = -1;
//						if(sMethod.hasActiveBody()) {
//							System.out.println("get Body -> " + sMethod.getActiveBody());
//							//isMatch = 0;
//							Chain<Local> localChain = sMethod.getActiveBody().getLocals();
//							for(Local local : localChain) {
//								System.out.println("get locals name -> " + local.getType());
//								Type type = local.getType();
//
//								if(type.toString().matches("android.view.WindowManager")) { // For Display
//									//System.out.println("get locals name -> " + local.getType());
//									UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();
//									if(foundUnitMatch(findUnit)) {
//										isMatch = 1; // then should exclude
//									}
//								}if(type.toString().matches("android.view.inputmethod.InputMethodManager")) { // For KeyboardUtils
//									UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();
//									if(foundUnitMatch(findUnit)) {
//										isMatch = 1; // then should exclude
//									}
//								}if(type.toString().matches("android.net.Uri")) { // For BitmapHelper
//									UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();
//									if(foundUnitMatch(findUnit)) {
//										isMatch = 2; // then should include
//									}
//								}
//							}
//						}
//						if(isMatch == 1) {
//							System.out.println("Display class method to exclude - > " + sMethod.getName());
//						}if(isMatch == 2) {
//							System.out.println("Display class method to include - > " + sMethod.getName());
//						}
//					}
//				}
//
//				// FOR TESTING END
//			}
//
//			//check for getter/setter class end
//
//			// TEST START
//			List<PlainJavaComp> architecturalPojos = FilterClass.getInstance().findArchitecturalPojos(filteredClassList);
//
//			for(PlainJavaComp pJ : architecturalPojos) {
//				System.out.println("Architectural Comp Class name - > " + pJ.getClassName());
//				System.out.println("Architectural Comp type - > " + pJ.getComponentType());
//				System.out.println("Architectural Comp SOOT Class - > " + pJ.getSootClass());
//				List<SootMethod> methodsList = pJ.getClassMethods();
//				for(SootMethod sm : methodsList) {
//					//System.out.println("Architectural Comp Class method - > " + sm);
//				}
//			}
//		
//			for(ComponentTransition cT : componentTransitionSetFinal) {
//				for(SootClass sC : filteredClassList) {
//					if(cT.getSourceC().equalsIgnoreCase(sC.getName())) {
//						componentTransitions = FilterClass.getInstance().establishLink(sC, architecturalPojos);
//					}
//					
//					if(cT.getTargetC().equalsIgnoreCase(sC.getName())) {
//						componentTransitions = FilterClass.getInstance().establishLink(sC, architecturalPojos);
//					}
//				}
//			}
//			
//			for(ComponentTransition cT : componentTransitions) {
//				System.out.println("Architectural Comp Connections Source - > " + cT.getSourceC());
//				System.out.println("Architectural Comp Connections Target - > " + cT.getTargetC());
//				System.out.println("Architectural Comp Connections LinkType - > " + cT.getLinkType());
//				Set<String> callerMethods  = cT.getInvokedMethods();
//
//				for(String cM : callerMethods) { // Why getting null pointer exception???
//					//System.out.println("Architectural Comp Connections callerMethod - > "  + cM);
//				}
//			}
//			
//			Set<ComponentTransition> compTranitionSet = mergeSets(componentTransitionSetFinal, componentTransitions);
//
//			
////			try {
////				WriteOutputJson.writeToJSON(projectConfig.getOutputDir(), architecturalPojos, compTranitionSet);
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} //architecturalPojos, compTranitionSet
//
//			// TEST END
//		}else {
//			logger.error(TAG + " Filtered Class List is Empty!");
//		}
//
//		// At the end needs to clear 
//
//	}
//
//	public static boolean foundUnitMatch(UnitPatchingChain findFragmentUnit) {
//		Iterator<Unit> unit = findFragmentUnit.iterator();
//
//		boolean isFound = false;
//
//		while(unit.hasNext()) {
//
//			Unit u = unit.next();
//
//			if(u.toString().contains("android.view.WindowManager: android.view.Display getDefaultDisplay")){
//				//System.out.println("Found beginTransaction for fragment");
//				isFound = true;
//			}
//			if(u.toString().contains("android.view.inputmethod.InputMethodManager: boolean showSoftInput")) {
//				//System.out.println("Found replace() for fragment");
//				isFound = true;
//			}if(u.toString().contains("android.view.inputmethod.InputMethodManager: boolean isActive")) {
//				//System.out.println("Found replace() for fragment");
//				isFound = true;
//			}if(u.toString().contains("android.view.inputmethod.InputMethodManager: boolean hideSoftInputFromWindow")) {
//				//System.out.println("Found replace() for fragment");
//				isFound = true;
//			}if(u.toString().contains("android.view.inputmethod.InputMethodManager: void toggleSoftInput")) {
//				//System.out.println("Found replace() for fragment");
//				isFound = true;
//			}if(u.toString().contains("android.net.Uri getUri")) {
//				//System.out.println("Found replace() for fragment");
//				isFound = true;
//			}if(u.toString().contains("android.net.Uri: android.net.Uri parse")) {
//				//System.out.println("Found replace() for fragment");
//				isFound = true;
//			}
//
//		}
//		return isFound;
//	}
//
//	public static List<ComponentTransition> resolveIcc(String iccModelPath) {
//		IntentMapping intentMapping = new IntentMapping(iccModelPath);
//		// For Test Start
//		Set<AppComponent> componentList =  intentMapping.retrieveCoreComponents();
//		// FOR TEST : Print
////				for(AndroidCoreComp comp : componentList) {
////					System.out.println("Android Core Comp Class Name - > " + comp.getClassName());
////					System.out.println("Android Core Comp Type - > " + comp.getCompType());
////				}
//		// For Test End
//		List<ComponentTransition> componentTransitions =  intentMapping.resolveComponents();
//
//		//PrintMethods.compTransitionWithICCMethod(TAG, componentTransitions);
//		return componentTransitions;
//	}
//
//	public static void initiateRefinement() {
//		manifest = Flowdroid.getInstance().getManifest(projectConfig.getApkPath());
//		packageName =  Flowdroid.getInstance().getPackageName(manifest);
//		applicationClasses = Flowdroid.getInstance().getApplicationClasses();
//	}
//	public static Set<ComponentTransition> mergeSets(Set<ComponentTransition> finalSetICC, Set<ComponentTransition> finalSetArchJava) {
//		finalSetICC.addAll(finalSetArchJava);
//		//PrintMethods.printMergedCompSet(finalSetICC);
//		System.out.println("List Size Merged -> " + finalSetICC.size());
//
//		return finalSetICC;
//		//List<ComponentTransition> finalList = new ArrayList<>(finalSet);
//	}
//	
//	public static Set<ComponentTransition> mergeLatest(Set<ComponentTransition> finalSet, List<ComponentTransition> compTrans) {
//		finalSet.addAll(compTrans);
//		//PrintMethods.printMergedCompSet(finalSet);
//		System.out.println("List Size Merged -> " + finalSet.size());
//
//		return finalSet;
//		//List<ComponentTransition> finalList = new ArrayList<>(finalSet);
//	}
//
//	public static Set<ComponentTransition> mergeComp(List<ComponentTransition> componentTransitions_aman, List<ComponentTransition> componentTransitions_iccta) {
//
//		Set<ComponentTransition> finalSet = new LinkedHashSet<>(componentTransitions_aman);
//		finalSet.addAll(componentTransitions_iccta);
//		//PrintMethods.printMergedCompSet(finalSet);
//		System.out.println("List Size Merged -> " + finalSet.size());
//
//		return finalSet;
//		//List<ComponentTransition> finalList = new ArrayList<>(finalSet);
//	}
//
//	public static Set<ComponentTransition> merge_new(List<ComponentTransition> componentTransitions_aman, List<ComponentTransition> componentTransitions_iccta){
//		//		Set<ComponentTransition> finalSet;
//		//		if(!componentTransitions_aman.isEmpty()) {
//		//			finalSet = new LinkedHashSet<>(componentTransitions_iccta);
//		//		}else {
//		//			if(!componentTransitions_iccta.isEmpty()) {
//		//				finalSet = new LinkedHashSet<>(componentTransitions_iccta);
//		//			}else {
//		//				logger.error(TAG + " Both lists are empty! Nothing to merge!");
//		//				return finalSet;
//		//			}
//		//		}
//
//
//		Set<ComponentTransition> finalSet = new LinkedHashSet<>(componentTransitions_iccta);
//
//		for(ComponentTransition c_aman : componentTransitions_aman) {
//
//			for(ComponentTransition c_iccta : componentTransitions_iccta) {
//				if(! c_aman.getSourceC().equals(c_iccta.getSourceC()) || ! c_aman.getTargetC().equals(c_iccta.getTargetC())) {
//					finalSet.add(c_aman);
//					//finalSet.add(c_iccta);
//					//System.out.println("Found new Component to merge -> ");
//				}else{
//					//System.out.println("No new Component found to merge -> ");
//				}
//			}
//		}
//		//System.out.println("List Size Merged -> " + finalSet.size());
//		return finalSet;
//	}
//
//	public static int checkGetterSetter(SootClass sootClass) {
//		// check getter/setter
//		int isGetteerSetter = -1;
//		//System.out.println("Class Name checkGetterSetter - > " + sootClass.getName());
//		//System.out.println("Total Method Count checkGetterSetter - > " + sootClass.getMethodCount());
//		List<SootMethod> getSetList = DetectGettersSetters.getInstance().findGettersSetters(sootClass);
//		//System.out.println("Getter/Setter Class Method Count - > " + getSetList.size());
//		if(getSetList.isEmpty()) {
//			isGetteerSetter = 0; // possible to add but before include the class check other heruistics first
//		}else {
//			if(getSetList.size() < sootClass.getMethodCount() * 0.5) {  // How to measure ???
//				//System.out.println("Final Class checkGetterSetter - > " + sootClass.getName());
//				isGetteerSetter = 1; // possible to add
//			}if(getSetList.size() >= sootClass.getMethodCount() * 0.5) {
//				isGetteerSetter = 2; // Exclude
//			}if(getSetList.size() == sootClass.getMethodCount()) {
//				isGetteerSetter = 2; // Exclude
//			}
//		}
//		return isGetteerSetter;
//	}
//	
////	public static Set<AndroidCoreComp> retrieveComponents(String iccModelPath) {
////		IntentMapping intentMapping = new IntentMapping(iccModelPath);
////		List<ComponentTransition> componentTransitions =  intentMapping.resolveComponents();
////
////		//PrintMethods.compTransitionWithICCMethod(TAG, componentTransitions);
////		return componentTransitions;
////	}
//}
