package utils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import models.AppComponent;
import models.ComponentTransition;

/**
 * @author - Tanjina Islam
 *
 * @date - 27-06-2019
 */
public class PrintMethods {

	public static void printComponentTransitionList(String TAG, List<ComponentTransition> componentTransitionGraph) {
		System.out.println(TAG + " Start of Printing the Component Transition Graph from amndroid ");
		for(ComponentTransition comp : componentTransitionGraph) {
			System.out.println(TAG + " Caller Component - > " + comp.getSourceC());
			System.out.println(TAG + " Callee Component - > " + comp.getTargetC());
		}
		System.out.println(TAG + " End Printing out the Component Transition Graph from amandroid");
	}

	public static void compTransitionWithICCMethod(String TAG, List<ComponentTransition> componentTransitions) {
		System.out.println(TAG + " Start of Printing the Component Transition Graph from iccta");
		for (ComponentTransition componentTransition : componentTransitions) {
			if (componentTransition.getSourceC() == null) {
				continue;
			}
			System.out.println(TAG + " Source Comp -> " + componentTransition.getSourceC());
			System.out.println(TAG + " ICC Method -> " + componentTransition.getICCMethod());
			System.out.println(TAG + " Target Comp -> " + componentTransition.getTargetC());
		}
		System.out.println(TAG + " End Printing out the Component Transition Graph from iccta");
	}

	public static void printMergedCompSet(Set<ComponentTransition> finalSet) {

		for(ComponentTransition comp : finalSet) {
			System.out.println("Check Mapped SRC - > " + comp.getSourceC());
			System.out.println("Check Mapped TAR - > " + comp.getTargetC());
		}

	}

	// New
	public static void printCompSet(Set<AppComponent> CompSet) {

		for(AppComponent comp : CompSet) {
			System.out.println("Comp name - > " + comp.getClassName());
			System.out.println("Comp type - > " + comp.getComponentType().name());
		}

	}

	//New
	public static void compTransition(String TAG, Set<ComponentTransition> componentTransitions) {
		//		System.out.println(TAG + " Start of Printing the Component Transition Graph");

		if(!componentTransitions.isEmpty()) {
			for (ComponentTransition componentTransition : componentTransitions) {
				//			if (componentTransition.getSourceC() == null) {
				//				continue;
				//			}
				System.out.println(TAG + " Source Comp -> " + componentTransition.getSourceC());
				//			System.out.println(TAG + " ICC Method -> " + componentTransition.getICCMethod());
				System.out.println(TAG + " Target Comp -> " + componentTransition.getTargetC());
				System.out.println(TAG + " Link Type -> " + componentTransition.getLinkType());
				System.out.println(TAG + " Style Type -> " + componentTransition.getStyle());
				System.out.println();
			}

		}
//		else {
//			System.out.println(TAG + " The Component Transition Graph is Empty!");
//		}
		//		System.out.println(TAG + " End Printing out the Component Transition Graph");
	}
	
	public static void printDuration(String message, long startTime) {
		long endTime   = System.nanoTime();
		long duration = endTime - startTime;
		long duration_inSeconds = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
		System.out.println(message + duration_inSeconds + " seconds");
	}
}
