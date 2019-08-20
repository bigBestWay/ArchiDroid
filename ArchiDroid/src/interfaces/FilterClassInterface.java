package interfaces;

import java.util.List;
import java.util.Set;

import models.AdapterComponent;
import models.AppComponent;
import models.ComponentTransition;
import soot.SootClass;
import soot.util.Chain;

/**
 * @author - Tanjina Islam
 *
 * @date - 20-08-2019
 */
public interface FilterClassInterface {

	/*
	 * Method for checking applicationVariant in package name and update it 
	 */
	public void updatePackage(Chain<SootClass> applicationClasses , String currentpackage);
	/*
	 * Method for filtering out application classes which resides outside the app package, R class, BuildConfig class and Classes that extend AsyncTask. 
	 */
	public Set<SootClass> filterClass(Chain<SootClass> applicationClasses);

	/*
	 * Detects the Adapter used in the app and store them in a List
	 */
	public Set<AdapterComponent> detectAdapters(List<SootClass> filteredClassList);
	// For detecting Android App Components
	public boolean isAppComponent(SootClass sootClass);
	/*
	 * Detects the fragments used in the app and store them in a List
	 */
	public Set<AppComponent> detectFragments(Set<SootClass> filteredClassList);
	// Found Parent - Child fragment Direct Link (i.e. Fragment SubClass to Fragment SuperClass)
	//Transition: From Child fragment To Parent fragment 
	public Set<ComponentTransition> findparentFragment(Set<ComponentTransition> componentTransitions, Set<AppComponent> fragmentComp);
	public Set<ComponentTransition> establishLink_fragments(Set<AppComponent> fragmentComp);
	public Set<AppComponent> detectArchitecturalPojos(Set<SootClass> filteredClassList);
	public Set<ComponentTransition> establishLink_POJOs_2(Set<AppComponent> architecturalPojoComp);
	public Set<ComponentTransition> establishLink_POJOs_1(Set<AppComponent> architecturalPojoComp);



}
