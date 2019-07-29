package main;

import java.util.List;
import java.util.Set;

import flowdroid.Flowdroid;
import helper.FilterClass;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.util.Chain;
import utils.ProjectConfig;
import utils.Utilities;

/**
 * @author - Tanjina Islam
 *
 * @date - 25-07-2019
 */
public class MainTest {
	static ProjectConfig projectConfig;
	static ProcessManifest manifest;
	static String packageName;
	static String updatedPackage;
	static Chain<SootClass> applicationClasses;
	static Set<SootClass> filteredClassList;

	public static void main(String [] args) {
		// load the configuration file
		projectConfig = Utilities.loadConfig();

		// Run Flowdroid and create the call graph
		Flowdroid.getInstance().initFlowdroid_Test(projectConfig.getPathAndroidJars(), projectConfig.getApkPath());

		initiateRefinement();

		// Retrieve the package name of the app
		String newPackageName = FilterClass.getInstance().getUpdatedpackage();




		if(newPackageName != null) {
			updatedPackage = newPackageName;
		}


		// Filter out the application classes that 
		filteredClassList = FilterClass.getInstance().filterClass(applicationClasses);
		if(!filteredClassList.isEmpty()) {

			for(SootClass sootClass : filteredClassList) {
				if(sootClass.getName().equalsIgnoreCase("com.example.pavneet_singh.roomdemo.AddNoteActivity")) { // notedb.NoteDatabase
					List<SootMethod> classMethods = sootClass.getMethods();

					for(SootMethod sMethod : classMethods) {
						if(sMethod.hasActiveBody()) {
							System.out.println("Body -> " + sMethod.getActiveBody());
						}
					}
				}
			}

		}

	}

	public static void initiateRefinement() {
//		manifest = Flowdroid.getInstance().getManifest(projectConfig.getApkPath());
		manifest = Flowdroid.getInstance().getManifest();
		packageName =  Flowdroid.getInstance().getPackageName(manifest);
		applicationClasses = Flowdroid.getInstance().getApplicationClasses();


		// Check and update package name to filter classes that reside inside the app package only
		FilterClass.getInstance().updatePackage(applicationClasses, packageName);
	}

}
