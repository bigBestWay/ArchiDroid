package helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import flowdroid.Flowdroid;
import interfaces.FilterClassInterface;
import models.AdapterComponent;
import models.AppComponent;
import models.ComponentTransition;
import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.util.Chain;
import utils.Utilities;

/**
 * @author - Tanjina Islam
 *
 * @date - 23-06-2019
 */
public class FilterClass implements FilterClassInterface{
	private final static Logger logger = LoggerFactory.getLogger(FilterClass.class);
	private final static String TAG = "[" + FilterClass.class.getSimpleName() + "]";

	private static FilterClass instance = null;
	private String updatedPackage;
	//static String newPackageName = null;
	private Set<AppComponent> appComponents;
	static Set<AppComponent> appComp = new LinkedHashSet<AppComponent>();


	public Set<AppComponent> getAppComponents() {
		return appComponents;
	}

	public void setAppComponents(Set<AppComponent> appComponents) {
		this.appComponents = appComponents;
	}

	public String getUpdatedpackage() {
		return updatedPackage;
	}

	public void setUpdatedpackage(String updatedPackage) {
		this.updatedPackage = updatedPackage;
	}

	private FilterClass() {

	}

	public static FilterClass getInstance() {
		if(instance == null) {
			instance = new FilterClass();
		}
		return instance;
	}


	/*
	 * Method for checking applicationVariant in package name and update it 
	 */
	public void updatePackage(Chain<SootClass> applicationClasses , String currentpackage) {

		for(SootClass sootClass : applicationClasses) {

			if((sootClass.getName().startsWith(currentpackage)))
			{
				updatedPackage = currentpackage; 
				setUpdatedpackage(updatedPackage);
				break;

			}else if((sootClass.getName().startsWith(currentpackage.substring(0,currentpackage.lastIndexOf("."))))) {
				updatedPackage = currentpackage.substring(0,currentpackage.lastIndexOf("."));
				setUpdatedpackage(updatedPackage);
				break;

			}
		}

		//return updatedpackage;
	}

	/*
	 * Method for filtering out application classes which resides outside the app package, R class, BuildConfig class and Classes that extend AsyncTask. 
	 */
	public Set<SootClass> filterClass(Chain<SootClass> applicationClasses) {
		Set<SootClass> filteredList = new LinkedHashSet<SootClass>();
		//newPackageName = currentpackage;

		for(SootClass sootClass : applicationClasses) {
			String className = sootClass.getName();
			String shortClassName = sootClass.getShortName();

			if(updatedPackage != null && className.startsWith(updatedPackage) && ! (className.contains("$"))){ // 
				if(! (shortClassName.equalsIgnoreCase("R")) && ! (shortClassName.equalsIgnoreCase("BuildConfig"))) { 
					//System.out.println("Filtered Class Name - > " + sootClass);
					filteredList.add(sootClass);
				}
			}
		}
		return filteredList;
	}

	/*
	 * Method to check whether a particular class is singleton or not. 
	 */
	private boolean isSingletonClass(SootClass sClass) {

		// Check singleton start
		String findClassName = sClass.getName();
		Chain<SootField> findClassFields = sClass.getFields();

		for(SootField sField : findClassFields) {

			if(sField.getType().toString().matches(findClassName) && sField.isPrivate() && sField.isStatic()) {

				List<SootMethod> classMethods = sClass.getMethods();
				int isMatch = 0;

				for(SootMethod sMethod : classMethods) {

					if(sMethod.isConstructor() && sMethod.isPrivate()) {
						isMatch = 1;
					}if(isMatch == 1 && !sMethod.isConstructor() 
							&& sMethod.getReturnType().toString().matches(findClassName) 
							&& sMethod.isPublic() && sMethod.isStatic()) {
						//System.out.println("Which class After-> " + findClassName);
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * Method to check whether a particular class is responsible for doing background task or not. 
	 */
	private boolean isAsyncTask(SootClass sClass) {
		if(sClass.hasSuperclass() && sClass.getSuperclass().getName().matches("android.os.AsyncTask")) {
			//System.out.println("Super Class Name with AsyncTask - > " + sClass);
			return true;
		}
		return false;
	}

	/*
	 * find if a class is an Adapter class
	 */
	private boolean isAdapter(SootClass sClass) {
		if(sClass.hasSuperclass() && sClass.getSuperclass().getName().startsWith("android.widget") && sClass.getSuperclass().getName().endsWith("Adapter")) {
			return true;
		}
		return false;
	}

	/*
	 * find if a class belongs to a widget class
	 */
	private boolean isWidget(SootClass sClass) {
		if(hasWidgetInterface(sClass) || (sClass.hasSuperclass() && sClass.getSuperclass().getName().startsWith("android.widget"))) {
			return true;
		}
		return false;
	}

	/*
	 * find if a class implements a widget interface
	 */
	private boolean hasWidgetInterface(SootClass sClass) {
		boolean isFound = false;
		Chain<SootClass> findClassInterfaces = sClass.getInterfaces();
		for(SootClass sC : findClassInterfaces) {
			if(sC.getName().startsWith("android.widget")) {
				isFound = true;	
				break;
			}
		}
		return isFound;
	}
	/*
	 * Detects the Adapter used in the app and store them in a List
	 */
	public Set<AdapterComponent> detectAdapters(List<SootClass> filteredClassList){
		Set<AdapterComponent> adapterClassSet = new LinkedHashSet<>();

		for(SootClass sootClass : filteredClassList) {

			if(isAdapter(sootClass)) {
				String className = sootClass.getName();
				String componentKind = "Adapter";
				AdapterComponent adapterComponent = new AdapterComponent(className, componentKind);
				adapterClassSet.add(adapterComponent);
			}
		}
		return adapterClassSet;
	}
	/*
	 * find if a class is a Fragment class
	 */
	// New For detecting Fragments
	private boolean isFragment(SootClass sClass) {
		if(sClass.hasSuperclass() && (sClass.getSuperclass().getName().startsWith("android.support") || sClass.getSuperclass().getName().startsWith("android.app") || sClass.getSuperclass().getName().startsWith("androidx")) && sClass.getSuperclass().getName().endsWith("Fragment")) {
			return true;
		}if(sClass.hasSuperclass() && sClass.getSuperclass().hasSuperclass() && (sClass.getSuperclass().getSuperclass().getName().startsWith("android.support") || sClass.getSuperclass().getSuperclass().getName().startsWith("android.app") || sClass.getSuperclass().getName().startsWith("androidx")) && sClass.getSuperclass().getName().endsWith("Fragment")) {
			return true;
		}
		return false;
	}

	// For detecting Android App Components
	public boolean isAppComponent(SootClass sootClass) {
		for(AppComponent appComp : appComponents) {
			if((sootClass.getName()).equalsIgnoreCase(appComp.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// For detecting Android App Core Components
	private boolean isCoreComponent(SootClass sootClass) {
		boolean isFound = false;
		Set<AppComponent> coreComponents = Flowdroid.getInstance().getAppCoreComponents();
		if(coreComponents != null && !coreComponents.isEmpty()) {
			for(AppComponent appComp : coreComponents) {
				if((sootClass.getName()).equalsIgnoreCase(appComp.getClassName())) {
					isFound = true;
				}
			}
		}else {
			logger.info(TAG + " NO Core Android Components Found!");
			isFound = false;
		}
		return isFound;
	}

	/*
	 * Detects the fragments used in the app and store them in a List
	 */
	public Set<AppComponent> detectFragments(Set<SootClass> filteredClassList){
		Set<AppComponent> fragmentClassList = new LinkedHashSet<AppComponent>();

		for(SootClass sootClass : filteredClassList) {

			if(isFragment(sootClass)) {
				AppComponent comp = new AppComponent();
				comp.setClassName(sootClass.getName());
				comp.setSootClass(sootClass);
				if(!sootClass.getMethods().isEmpty()) {
					comp.setClassMethods(sootClass.getMethods());
				}else {
					comp.setClassMethods(null);
				}
				comp.setComponentType(utils.Utilities.CompType.Fragment);

				fragmentClassList.add(comp);
			}
		}
		System.out.println("Fragment Component List Size -> " + fragmentClassList.size());
		return fragmentClassList;
	}

	// Found Parent - Child fragment Direct Link (i.e. Fragment SubClass to Fragment SuperClass)
	//Transition: From Child fragment To Parent fragment 
	public Set<ComponentTransition> findparentFragment(Set<ComponentTransition> componentTransitions, Set<AppComponent> fragmentComp){

		Set<ComponentTransition> localList = new LinkedHashSet<ComponentTransition>(componentTransitions);

		List<AppComponent> fragmentList = new ArrayList<>(fragmentComp);

		if(componentTransitions.isEmpty()) {
			logger.info(TAG, " Empty Component Transition List " );
		}

		if(fragmentComp.isEmpty()) {
			logger.info(TAG, " Empty Fragment List " );
			return localList;
		}

		AppComponent frag1, frag2;

		for(int i = 0; i < fragmentList.size(); i++) {
			frag1 = fragmentList.get(i);

			SootClass fragment1 = frag1.getSootClass();
			boolean hasSuperClass = fragment1.hasSuperclass();
			if(hasSuperClass) {
				String frag1_SuperClass = fragment1.getSuperclass().getName();

				for(int j = 0; j < fragmentList.size(); j++) {
					frag2 = fragmentList.get(j);
					String frag2_Class = frag2.getClassName(); 
					if(frag1_SuperClass.matches(frag2_Class)) {

						ComponentTransition componentTransition = new ComponentTransition();
						componentTransition.setSourceC(fragment1.getName()); // Child fragment - > Source component
						componentTransition.setTargetC(frag2_Class); // Parent fragment - > Target component
						componentTransition.setLinkType(Utilities.LinkType.ParentChild);

						localList.add(componentTransition);
					}
				}
			}
		}
		return localList;
	}

	public Set<ComponentTransition> establishLink_fragments(Set<AppComponent> fragmentComp) {

		System.out.println("Adding Direct link to Fragments START - > ");

		Set<ComponentTransition> fragmentTransitionSet = new LinkedHashSet<ComponentTransition>();
		Set<AppComponent> targetCompSet = new LinkedHashSet<AppComponent>();

		Set<AppComponent> localSet = new LinkedHashSet<AppComponent>(appComponents);
		System.out.println("App Comp List Size - > " + localSet.size());

		for (Iterator<AppComponent> itr = localSet.iterator(); itr.hasNext(); ) {
			AppComponent appComp = itr.next();

			SootClass sClass = appComp.getSootClass();

			String callerComp = sClass.getName();

			targetCompSet = foundFragments(sClass, fragmentComp);
			//System.out.println("Target Comp List Size - > " + targetCompSet.size());
			if(!targetCompSet.isEmpty()) {

				//System.out.println("Checking end using Source - > " + sClass);

				for(Iterator<AppComponent> iterator = targetCompSet.iterator(); iterator.hasNext(); ) {
					AppComponent targetComp = iterator.next();
					String calleeComp = targetComp.getClassName();
					if(calleeComp.matches(callerComp)) {
						itr.remove();
					}else if(!calleeComp.matches(callerComp)) {
						ComponentTransition componentTransition = new ComponentTransition();
						componentTransition.setSourceC(callerComp);
						componentTransition.setTargetC(calleeComp);
						componentTransition.setLinkType(Utilities.LinkType.Direct);


						// Code block to add called methods - start

						//UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();
						List<SootMethod> targetCompclassMethods = targetComp.getClassMethods();

						if(!targetCompclassMethods.isEmpty() && targetCompclassMethods != null) {
							Set<String> callerMethods = foundMethodCall(sClass, targetCompclassMethods); //foundMethodCall(findUnit, comp.getClassMethods());
							if(!callerMethods.isEmpty()) {
								componentTransition.setInvokedMethods(callerMethods);
							}else {
								componentTransition.setInvokedMethods(null);
							}
						}

						// Code block to add called methods - end

						fragmentTransitionSet.add(componentTransition);
						iterator.remove();
					}

				}	
			}
		}

		System.out.println("Fragment Links List Size - > " + fragmentTransitionSet.size());
		System.out.println("Adding Direct link to Fragments END - > ");

		return fragmentTransitionSet;
	}

	private boolean isSerializable(Chain<SootClass> findClassInterfaces) {
		boolean isFound = false;
		for(SootClass sC : findClassInterfaces) {
			if(sC.getName().equalsIgnoreCase("java.io.Serializable") || sC.getName().equalsIgnoreCase("java.io.Externalizable")) {
				//System.out.println("Interface Class - > " + sC.getName());
				isFound = true;
				break;
			}
		}
		return isFound;
	}
	private boolean hasPublicNoArgConstructor(List<SootMethod> classMethods) {
		boolean isFound = false;
		for(SootMethod sM : classMethods) {
			if(sM.isConstructor() && sM.isPublic() && sM.getParameterCount() == 0) {
				//System.out.println("Constructor Name - > " + sM.getName() + " Type - > " + sM.isPublic() + " Param Count - > " + sM.getParameterCount());
				isFound = true;
			}
		}
		return isFound;
	}
	// Count the number of private attributes of a class
	private int countPrivateFiled(Chain<SootField> findClassFields) {
		// Check private field count = get/set method count

		int countPrivateFiled = 0;

		for(SootField sField : findClassFields) {
			// Second condition is for the default type checking for attributes 
			//which don't explicitly define public/private/protected type. But the default value in java = private
			if(sField.isPrivate() || (!sField.isPrivate() && !sField.isPublic() && !sField.isProtected())) { 
				countPrivateFiled++;
			}
		}
		//System.out.println("Num of private attribute - > " + countPrivateFiled);

		return countPrivateFiled;
	}

	// Case 1 : Look for static Class  
	private Set<ComponentTransition> foundClass(SootClass sClass, Set<AppComponent> architecturalPojoComp) {
		Set<ComponentTransition> pojoCompTransition_2 = new LinkedHashSet<ComponentTransition>();

		List<SootMethod> classMethods = sClass.getMethods();
		if(!classMethods.isEmpty()) {
			for(SootMethod sMethod : classMethods) { 
				if(sMethod.hasActiveBody()) {
					for (Iterator<AppComponent> itr = architecturalPojoComp.iterator(); itr.hasNext(); ) {
						AppComponent targetComp = itr.next();
						if(isFoundTargetClass(sClass, targetComp)) {

							ComponentTransition componentTransition = new ComponentTransition();
							componentTransition.setSourceC(sClass.getName());
							componentTransition.setTargetC(targetComp.getClassName());
							componentTransition.setLinkType(Utilities.LinkType.Direct);

							// Code block to add called methods - start

							//UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();
							List<SootMethod> targetCompclassMethods = targetComp.getClassMethods();

							if(!targetCompclassMethods.isEmpty() && targetCompclassMethods != null) {
								Set<String> callerMethods = foundMethodCall(sClass, targetCompclassMethods); //foundMethodCall(findUnit, comp.getClassMethods());
								if(!callerMethods.isEmpty()) {
									componentTransition.setInvokedMethods(callerMethods);
								}else {
									componentTransition.setInvokedMethods(null);
								}
							}

							// Code block to add called methods - end

							pojoCompTransition_2.add(componentTransition);
							//itr.remove();
						}
					}
					break;
				}
			}
		}
		return pojoCompTransition_2;
	}

	// Case 2 : Look for local type match for fragments
	private Set<AppComponent> foundFragments(SootClass sClass, Set<AppComponent> fragments) {

		//System.out.println("Checking start using Source - > " + sClass);
		Set<AppComponent> targetCompSet = new LinkedHashSet<AppComponent>();

		List<SootMethod> classMethods = sClass.getMethods();
		if(!classMethods.isEmpty()) {
			for(SootMethod sMethod : classMethods) {
				if(sMethod.hasActiveBody()) {

					Chain<Local> localChain = sMethod.getActiveBody().getLocals();

					Set<String> localType = new LinkedHashSet<String>();
					for(Local local : localChain) {
						Type type = local.getType();
						if(type.toString().startsWith(updatedPackage)) {

							localType.add(type.toString());
						}
					}

					if(!localType.isEmpty()) {
						for(String type : localType) {
							for (Iterator<AppComponent> itr = fragments.iterator(); itr.hasNext(); ) {
								AppComponent targetComp = itr.next();

								if(!type.matches(sClass.getName()) && type.matches(targetComp.getClassName())) { // add the link to this class
									targetCompSet.add(targetComp);
								}
							}
							//break;
						}
						//break;
					}
				}
			}
		}
		return targetCompSet;
	}

	// Case 2 : Look for local type match for architectural POJOs
	private Set<AppComponent> foundClass2(SootClass sClass, Set<AppComponent> architecturalPojoComp) {

		//System.out.println("Checking start using Source - > " + sClass);
		Set<AppComponent> targetCompSet = new LinkedHashSet<AppComponent>();

		List<SootMethod> classMethods = sClass.getMethods();
		if(!classMethods.isEmpty()) {
			for(SootMethod sMethod : classMethods) {
				if(sMethod.hasActiveBody()) {

					Chain<Local> localChain = sMethod.getActiveBody().getLocals();

					Set<String> localType = new LinkedHashSet<String>();
					for(Local local : localChain) {
						Type type = local.getType();
						if(type.toString().startsWith(updatedPackage)) {

							localType.add(type.toString());
						}
					}

					if(!localType.isEmpty()) {
						for(String type : localType) {
							for (Iterator<AppComponent> itr = architecturalPojoComp.iterator(); itr.hasNext(); ) {
								AppComponent targetComp = itr.next();

								if(!type.matches(sClass.getName()) && type.matches(targetComp.getClassName())) { // add the link to this class
									targetCompSet.add(targetComp);
								}
							}
							//break;
						}
						//break;
					}
				}
			}
		}
		return targetCompSet;
	}

	public Set<ComponentTransition> establishLink_POJOs_2(Set<AppComponent> architecturalPojoComp){
		System.out.println("Adding Direct link to Architectural POJOs Case 2 START - > ");

		Set<ComponentTransition> pojoCompTransitionSet = new LinkedHashSet<ComponentTransition>();
		Set<ComponentTransition> pojoCompTransition_2 = new LinkedHashSet<ComponentTransition>();

		Set<AppComponent> localSet = new LinkedHashSet<AppComponent>(appComponents);
		System.out.println("App Comp List Size - > " + localSet.size());

		for (Iterator<AppComponent> itr = localSet.iterator(); itr.hasNext(); ) {


			AppComponent appComp = itr.next();

			SootClass sClass = appComp.getSootClass();


			pojoCompTransition_2 = foundClass(sClass, architecturalPojoComp);
			if(!pojoCompTransition_2.isEmpty()) {
				pojoCompTransitionSet.addAll(pojoCompTransition_2);
				itr.remove();
			}
		}

		if(!pojoCompTransitionSet.isEmpty()) {
			System.out.println("Architectural POJO CompTransitionSet 2 List Size - > " + pojoCompTransitionSet.size());
			//PrintMethods.compTransition(TAG, pojoCompTransitionSet);
		}

		System.out.println("Adding Direct link to Architectural POJOs Case 2 END - > ");

		return pojoCompTransitionSet;
	}

	public Set<ComponentTransition> establishLink_POJOs_1(Set<AppComponent> architecturalPojoComp){
		System.out.println("Adding Direct link to Architectural POJOs Case 1 START - > ");

		//Set<ComponentTransition> pojoCompTransitionSet = new LinkedHashSet<ComponentTransition>();
		Set<ComponentTransition> pojoCompTransition_1 = new LinkedHashSet<ComponentTransition>();
		Set<AppComponent> targetCompSet = new LinkedHashSet<AppComponent>();

		Set<AppComponent> localSet = new LinkedHashSet<AppComponent>(appComponents);
		System.out.println("App Comp List Size - > " + localSet.size());

		for (Iterator<AppComponent> itr = localSet.iterator(); itr.hasNext(); ) {

			AppComponent appComp = itr.next();

			SootClass sClass = appComp.getSootClass();

			String callerComp = sClass.getName();

			targetCompSet = foundClass2(sClass, architecturalPojoComp);
			//System.out.println("Target Comp List Size - > " + targetCompSet.size());
			if(!targetCompSet.isEmpty()) {

				//System.out.println("Checking end using Source - > " + sClass);

				for(Iterator<AppComponent> iterator = targetCompSet.iterator(); iterator.hasNext(); ) {
					AppComponent targetComp = iterator.next();
					String calleeComp = targetComp.getClassName();
					if(calleeComp.matches(callerComp)) {
						itr.remove();
					}else if(!calleeComp.matches(callerComp)) {
						ComponentTransition componentTransition = new ComponentTransition();
						componentTransition.setSourceC(callerComp);
						componentTransition.setTargetC(calleeComp);
						componentTransition.setLinkType(Utilities.LinkType.Direct);


						// Code block to add called methods - start

						//UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();
						List<SootMethod> targetCompclassMethods = targetComp.getClassMethods();

						if(!targetCompclassMethods.isEmpty() && targetCompclassMethods != null) {
							Set<String> callerMethods = foundMethodCall(sClass, targetCompclassMethods); //foundMethodCall(findUnit, comp.getClassMethods());
							if(!callerMethods.isEmpty()) {
								componentTransition.setInvokedMethods(callerMethods);
							}else {
								componentTransition.setInvokedMethods(null);
							}
						}

						// Code block to add called methods - end

						pojoCompTransition_1.add(componentTransition);
						iterator.remove();
					}

				}	

			}
		}

		//		if(!pojoCompTransition_1.isEmpty()) {
		//			//PrintMethods.compTransition(TAG, pojoCompTransition_1);
		//			pojoCompTransitionSet.addAll(pojoCompTransition_1);
		//		}

		System.out.println("Architectural POJO CompTransitionSet 1 List Size - > " + pojoCompTransition_1.size());
		System.out.println("Adding Direct link to Architectural POJOs Case 1 END - > ");

		return pojoCompTransition_1;
	}

	// Nee - Test : OK it works

	private static Set<String> foundMethodCall(SootClass sourceCompSootClass, List<SootMethod> targetCompclassMethods) {

		Set<String> invokedMethods = new LinkedHashSet<>();

		List<SootMethod> sourceCompClassMethods = sourceCompSootClass.getMethods();
		for(SootMethod sMethod : sourceCompClassMethods) {
			if(sMethod.hasActiveBody()) {

				UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();

				if(!findUnit.isEmpty()) {

					Iterator<Unit> unit = findUnit.iterator();

					while(unit.hasNext()) {
						Unit u = unit.next();

						for(SootMethod calledMethod: targetCompclassMethods) {
							if(!calledMethod.isConstructor() && u.toString().contains(calledMethod.toString())){
								//System.out.println("establishLink found plainJavaCompclassMethods " + sm.toString());
								//isFound = true; // need to add the methods and the class in the connection link of the soot class

								invokedMethods.add(calledMethod.getName());
							}
						}
					}
				}
			}
		}

		return invokedMethods;
	}

	// Test - to check else condition for static method calling class
	private static boolean isFoundTargetClass(SootClass sourceCompSootClass, AppComponent targetComp) {

		boolean isFound = false;

		List<SootMethod> sourceCompClassMethods = sourceCompSootClass.getMethods();
		for(SootMethod sMethod : sourceCompClassMethods) {
			if(sMethod.hasActiveBody()) {

				UnitPatchingChain findUnit = sMethod.getActiveBody().getUnits();

				Iterator<Unit> unit = findUnit.iterator();

				while(unit.hasNext()) {
					Unit u = unit.next();

					String callerComp = sourceCompSootClass.getName();
					String targetCompName = targetComp.getClassName();

					if(!callerComp.matches(targetCompName) && u.toString().contains("staticinvoke <" + targetCompName)) {
						isFound = true;
					}
				}
			}
		}

		return isFound;
	}

	private boolean findDefinedField(List<SootMethod> filteredList) {
		//		String packageArray [] = {"com.squareup.retrofit2.Retrofit", "com.squareup.okhttp3.OkHttpClient", "com.squareup.okhttp4.OkHttpClient", "com.android.volley.RequestQueue", "com.android.volley.Request", "com.loopj.android.http.AsyncHttpClient", 
		//				"android.database.sqlite.SQLiteDatabase", "android.database.Cursor", "android.database.sqlite.SQLiteCursor", "android.database.sqlite.SQLiteQuery", "android.arch.persistence.room.RoomDatabase", "io.realm.Realm", "io.realm.DynamicRealm",
		//		"android.content.ContentResolver"};

		//		String packageArray [] = {"com.squareup.retrofit2.Retrofit", "com.squareup.okhttp3.OkHttpClient", "com.squareup.okhttp4.OkHttpClient", "com.android.volley.RequestQueue", "com.android.volley.Request", "com.loopj.android.http.AsyncHttpClient", 
		//				"android.database.sqlite.SQLiteDatabase", "android.database.Cursor", "android.database.sqlite.SQLiteCursor", "android.arch.persistence.room.RoomDatabase", "android.content.ContentResolver"};

		String packageArray [] = {"com.squareup.retrofit2.Retrofit", "com.squareup.okhttp3.OkHttpClient", "com.squareup.okhttp4.OkHttpClient", "com.android.volley.RequestQueue", "com.android.volley.Request", "com.loopj.android.http.AsyncHttpClient",
				"java.net.HttpURLConnection", "javax.net.ssl.HttpsURLConnection", "android.database.sqlite.SQLiteDatabase", "android.database.Cursor", "android.database.sqlite.SQLiteCursor", "android.arch.persistence.room.RoomDatabase",
				"io.realm.Realm", "io.realm.DynamicRealm", "android.content.ContentResolver"};

		boolean isFound = false;
		for(SootMethod sMethod : filteredList) {
			//System.out.println("Display class method name  - > " + sMethod.getName());
			int isMatch = -1;
			if(sMethod.hasActiveBody()) {
				//System.out.println("get Body -> " + sMethod.getActiveBody());
				//isMatch = 0;
				Chain<Local> localChain = sMethod.getActiveBody().getLocals();
				for(Local local : localChain) {
					//System.out.println("get locals name -> " + local.getType());
					Type type = local.getType();

					for(int i = 0; i < packageArray.length; i++) {
						if(packageArray[i].equalsIgnoreCase(type.toString())) {
							isMatch = 1;
							break;
						}
					}				

				}
			}
			if(isMatch == 1) {
				//System.out.println("Display class method to include - > " + sMethod.getName());
				isFound = true;
			}
		}
		return isFound;
	}

	private List<SootMethod> filteredMethodList(SootClass sClass){
		List<SootMethod> classMethods = sClass.getMethods();
		List<SootMethod> getSetList = DetectGettersSetters.getInstance().findGettersSetters(sClass);
		List<SootMethod> filteredList = new ArrayList<>();

		// what if classMethods is empty?. Need to check that
		if(!classMethods.isEmpty()) {
			for(SootMethod sMethod : classMethods) {
				//System.out.println("Final Architectural POJO Class's All methods - > " + sMethod);
				if(!getSetList.isEmpty()) {
					for(SootMethod sMethod2 : getSetList) {
						if(sMethod.getName().matches(sMethod2.getName())) {
							//System.out.println("Matched get/set - > " + sMethod2.getName());
						}else {
							filteredList.add(sMethod); // Not mutator/accessor(i.e. getter/setter) method. So add in the list
						}
					}
				}else {
					filteredList = classMethods;
				}
			}
		}
		return filteredList;
	}

	private boolean isJavaBean(SootClass sClass) {
		boolean isFound = false;
		Chain<SootField> findClassFields = sClass.getFields();
		List<SootMethod> classMethods = sClass.getMethods();
		Chain<SootClass> findClassInterfaces = sClass.getInterfaces();

		List<SootMethod> getSetList = DetectGettersSetters.getInstance().findGettersSetters(sClass);
		//System.out.println("Get/Set Method Count - > " + getSetList.size());
		int getSetMethodCount = getSetList.size();

		if(!findClassFields.isEmpty() && !classMethods.isEmpty() && !findClassInterfaces.isEmpty()) {
			// Check public default constructor, is Serializable and private field count = get/set method count
			if(hasPublicNoArgConstructor(classMethods) && isSerializable(findClassInterfaces) && countPrivateFiled(findClassFields) == getSetMethodCount) {
				//System.out.println("Java Bean Class - > " + sClass.getName());
				isFound = true;
			}
		}
		return isFound;
	}
	// New using Set
	public Set<AppComponent> detectArchitecturalPojos(Set<SootClass> filteredClassList) {
		//Set<AndroidCoreComp> detectFragments_Set(Set<SootClass> filteredClassList)
		Set<AppComponent> architecturalPojos = new LinkedHashSet<AppComponent>();

		for(SootClass sootClass : filteredClassList) {

			if(!isCoreComponent(sootClass) && !isJavaBean(sootClass) && !isFragment(sootClass) && (!isWidget(sootClass)) && (!isAdapter(sootClass))  && ! (isAsyncTask(sootClass))) { // && ! (isAsyncTask(sootClass))
				if(isSingletonClass(sootClass)) {
					String className = sootClass.getName();
					AppComponent plainJavaComp = new AppComponent();
					plainJavaComp.setClassName(className);
					plainJavaComp.setSootClass(sootClass);
					if(!sootClass.getMethods().isEmpty()) {
						plainJavaComp.setClassMethods(sootClass.getMethods());
					}
					plainJavaComp.setComponentType(utils.Utilities.CompType.PlainJava);

					architecturalPojos.add(plainJavaComp);
				}else {
					List<SootMethod> filteredList = filteredMethodList(sootClass);
					if(!filteredList.isEmpty()) {
						if(findDefinedField(filteredList)){
							//	System.out.println("Final Architectural POJO Class - > " + sClass.getName());
							String className = sootClass.getName();
							AppComponent plainJavaComp = new AppComponent();
							plainJavaComp.setClassName(className);
							plainJavaComp.setSootClass(sootClass);
							if(!sootClass.getMethods().isEmpty()) {
								plainJavaComp.setClassMethods(sootClass.getMethods());
							}
							plainJavaComp.setComponentType(utils.Utilities.CompType.PlainJava);

							architecturalPojos.add(plainJavaComp);
						}
					}
				}
			}
		}
		System.out.println("Architectural POJO Component List Size -> " + architecturalPojos.size());
		return architecturalPojos;
	}
}
