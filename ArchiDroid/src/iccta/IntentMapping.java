package iccta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icc.IntentMappingInterface;
import iccta.Ic3Data.Application.Component;
import models.AppComponent;
import models.ComponentTransition;
import soot.Body;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import utils.Utilities;

/**
 * @author - Tanjina Islam
 *
 * @date - 26-06-2019
 */
public class IntentMapping implements IntentMappingInterface{

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String iccModelPath = null;

	public IntentMapping(String iccModelPath) {
		logger.info("[Icc Mapping] Launching Intent Mapping...");

		logger.info("[Icc Mapping] Loading the ICC Model...");	
		this.iccModelPath = iccModelPath;
	}

	@Override
	public List<ComponentTransition> resolveComponents() {
		// TODO Auto-generated method stub
		List<ComponentTransition> componentTransitions = new ArrayList<ComponentTransition>();
		App app = Ic3ResultLoader.load(iccModelPath);

		if (app == null) {
			logger.error("[Icc Mapping] %s is not a valid IC3 model");
			return componentTransitions;
		}

		Set<Intent> intents = app.getIntents();

		if (intents.isEmpty()) {
			logger.error("[Icc Mapping] %s No exit_point intents found in the IC3 model");
			return componentTransitions;
		}

		logger.info("[Icc Mapping] ...End Loading the ICC Model");

		logger.info("[Icc Mapping] Lauching Component Resolving Algorithm...");
		for (Intent intent : intents) {
			//System.out.println("FOR -> " + intent);
			//			for (Component comp : intent.getApp().getComponentList()) {
			//				
			//				System.out.println("Component Name Sooot -> " + comp.getName());
			//				System.out.println("Component Kind Sooot -> " + comp.getKind().name());
			//			}

			if (intent.isImplicit()) {
				if (null == intent.getAction()) {
					continue;
				}

				List<Component> targetedComps = intent.resolve(app.getComponentList());

				for (Component targetComp : targetedComps) {
					System.out.println("Implicit Intent -> " + targetComp.getName());
					if (!availableTargetedComponent(intent.getApp(), targetComp.getName())) {
						continue;
					}

					String callerMethod = intent.getLoggingPoint().getCallerMethodSignature();
					String callerComp = callerMethod.substring(1, callerMethod.indexOf(":"));

					if(callerComp.contains("$")) {
						//System.out.println("Holaaaaaaaa - > ");
						callerComp = callerMethod.substring(1, callerMethod.indexOf("$"));
					}

					String calleeMethod = intent.getLoggingPoint().getCalleeMethodSignature();
					String iccMethod = calleeMethod.substring(calleeMethod.indexOf(":") + 1, calleeMethod.indexOf(">"));

					String calleeComp = targetComp.getName();

					System.out.println("Soot Class callerMethod - > " + callerMethod);
					System.out.println("Soot Class calleeMethod - > " + calleeMethod);

					ComponentTransition componentTransition = new ComponentTransition(callerComp, iccMethod, calleeComp); // object with icc method
					//ComponentTransition componentTransition = new ComponentTransition(callerComp, null, calleeComp); // object without icc method
					componentTransition.setLinkType(Utilities.LinkType.ICC);
					//						System.out.println("Source Comp -> " + callerComp);
					//						System.out.println("ICC Method -> " + iccMethod);
					//						System.out.println("Target Comp -> " + calleeComp);
					componentTransitions.add(componentTransition);
					//						System.out.println("ComponentTransitions List -> " + componentTransitions);	
					//											System.out.println("Soot Class Target - > " + Scene.v().getSootClassUnsafe(targetComp.getName()));


					// For Testing Start
					SootMethod fromSM = Scene.v().grabMethod(intent.getLoggingPoint().getCallerMethodSignature());
					System.out.println("Implicit - FROM Sm Sooot -> " + fromSM.getDeclaringClass() );
					Stmt fromU = linkWithTarget(fromSM, intent.getLoggingPoint().getStmtSequence());

					System.out.println("Implicit - fromU Sooot -> " + fromU);
					System.out.println("Implicit - targetComp Sooot -> " + Scene.v().getSootClassUnsafe(targetComp.getName()));
					System.out.println("Implicit - Exit Kind Sooot -> " + targetComp.getKind().name());

					//					IccLink iccLink = new IccLink(fromSM, fromU, Scene.v().getSootClassUnsafe(targetComp.getName()));
					//					iccLink.setExit_kind(targetComp.getKind().name());
					//
					//					iccLinks.add(iccLink);

					// For Testing End

				}
			}else {
				String targetCompName = intent.getComponentClass();
				System.out.println("Explicit Intent -> " + targetCompName);
				if (!availableTargetedComponent(intent.getApp(), targetCompName)) {
					continue;
				}

				String callerMethod = intent.getLoggingPoint().getCallerMethodSignature();
				String callerComp = callerMethod.substring(1, callerMethod.indexOf(":"));
				if(callerComp.contains("$")) {
					//System.out.println("Holaaaaaaaa - > ");
					callerComp = callerMethod.substring(1, callerMethod.indexOf("$"));
				}

				String calleeMethod = intent.getLoggingPoint().getCalleeMethodSignature();
				String iccMethod = calleeMethod.substring(calleeMethod.indexOf(":") + 1, calleeMethod.indexOf(">"));

				String calleeComp = targetCompName;

				System.out.println("Soot Class callerMethod - > " + callerMethod);
				System.out.println("Soot Class calleeMethod - > " + calleeMethod);

				ComponentTransition componentTransition = new ComponentTransition(callerComp, iccMethod, calleeComp); // object with icc method
				//ComponentTransition componentTransition = new ComponentTransition(callerComp, null, calleeComp); // object without icc method
				componentTransition.setLinkType(utils.Utilities.LinkType.ICC);
				//				System.out.println("Source Comp -> " + callerComp);
				//				System.out.println("ICC Method -> " + iccMethod);
				//				System.out.println("Target Comp -> " + calleeComp);
				componentTransitions.add(componentTransition);
				//				System.out.println("ComponentTransitions List -> " + componentTransitions);
				//								System.out.println("Soot Class Target - > " + Scene.v().getSootClassUnsafe(targetCompName));




				// For Testing Start

				SootMethod fromSM = Scene.v().grabMethod(intent.getLoggingPoint().getCallerMethodSignature());
				System.out.println("Explicit - FROM Sm Sooot -> " + fromSM.getDeclaringClass());
				if (fromSM != null) {
					Stmt fromU = linkWithTarget(fromSM, intent.getLoggingPoint().getStmtSequence());
					//IccLink iccLink = new IccLink(fromSM, fromU, Scene.v().getSootClassUnsafe(targetCompName));
					System.out.println("Explicit - fromU Sooot -> " + fromU);
					System.out.println("Explicit - targetComp Sooot -> " + Scene.v().getSootClassUnsafe(targetCompName));
					for (Component comp : intent.getApp().getComponentList()) {
						if (comp.getName().equals(targetCompName)) {
							//iccLink.setExit_kind(comp.getKind().name());
							System.out.println("Explicit - Exit Kind Sooot -> " + comp.getKind().name());
						}
					}

					//iccLinks.add(iccLink);
				}
				// For Testing End

			}
		}
		logger.info("[Icc Mapping] ...End Component Resolving Algorithm");
		return componentTransitions;
	}

	// For Testing
	private Stmt linkWithTarget(SootMethod fromSM, int stmtIdx) {
		Body body = fromSM.retrieveActiveBody();

		int i = 0;
		for (Iterator<Unit> iter = body.getUnits().snapshotIterator(); iter.hasNext();) {
			Stmt stmt = (Stmt) iter.next();

			if (i == stmtIdx) {
				return stmt;
			}
			i++;
		}
		return null;
	}

	private static boolean availableTargetedComponent(App app, String targetedComponentName) {
		for (Component comp : app.getComponentList()) {
			//System.out.println("Comp name from App to match with target comp -> " + comp.getName());
			if (comp.getName().equals(targetedComponentName)) {
				//System.out.println("Available Target Comp -> " + targetedComponentName);
				return true;
			}
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see icc.IntentMappingInterface#retrieveCoreComponents()
	 */
	@Override
	public Set<AppComponent> retrieveCoreComponents() {
		// TODO Auto-generated method stub
		//		return null;
		Set<AppComponent> componentList = new LinkedHashSet<>();
		App app = Ic3ResultLoader.load(iccModelPath);

		if (app == null) {
			logger.error("[Icc Mapping] %s is not a valid IC3 model");
			return componentList;
		}

		Set<Intent> intents = app.getIntents();

		if (intents.isEmpty()) {
			logger.error("[Icc Mapping] %s No exit_point intents found in the IC3 model");
			return componentList;
		}

		logger.info("[Icc Mapping] ...End Loading the ICC Model");

		logger.info("[Icc Mapping] Retrieving Components...");
		System.out.println("Intents size -> " + intents.size());


		for (Intent intent : intents) {
			//System.out.println("FOR -> " + intent);
			List<Component> appComponentList = intent.getApp().getComponentList();
			for (Component comp : appComponentList) {
				AppComponent coreComp = new AppComponent();
				coreComp.setClassName(comp.getName());
				if(comp.hasKind()) {
					coreComp.setCompType(comp.getKind().name());
					//					if(comp.getKind().name().matches(utils.Utilities.Type.Activity.toString())) {
					//						coreComp.setComponentType(utils.Utilities.Type.Activity);
					//					}else if(comp.getKind().name().matches(utils.Utilities.Type.Service.name())) {
					//						coreComp.setComponentType(utils.Utilities.Type.Service);
					//					}else if(comp.getKind().name().matches(utils.Utilities.Type.Receiver.name())) {
					//						coreComp.setComponentType(utils.Utilities.Type.Receiver);
					//					}else if(comp.getKind().name().matches(utils.Utilities.Type.Provider.name())) {
					//						coreComp.setComponentType(utils.Utilities.Type.Provider);
					//					}
				}
				//				
				//				System.out.println("Component Name Sooot -> " + comp.getName());
				//				System.out.println("Component Kind Sooot -> " + comp.getKind().name());

				componentList.add(coreComp);
				//break;
			}
		}
		return componentList;
	}

	/* (non-Javadoc)
	 * @see icc.IntentMappingInterface#resolveComponentsSet()
	 */
	@Override
	public Set<ComponentTransition> resolveComponentsSet() {
		// TODO Auto-generated method stub
		Set<ComponentTransition> componentTransitions = new LinkedHashSet<ComponentTransition>();
		App app = Ic3ResultLoader.load(iccModelPath);

		if (app == null) {
			logger.error("[Icc Mapping] %s is not a valid IC3 model");
			return componentTransitions;
		}

		Set<Intent> intents = app.getIntents();

		if (intents.isEmpty()) {
			logger.error("[Icc Mapping] %s No exit_point intents found in the IC3 model");
			return componentTransitions;
		}

		logger.info("[Icc Mapping] ...End Loading the ICC Model");

		logger.info("[Icc Mapping] Lauching Component Resolving Algorithm...");
		for (Intent intent : intents) {

			if (intent.isImplicit()) {
				if (null == intent.getAction()) {
					continue;
				}

				List<Component> targetedComps = intent.resolve(app.getComponentList());

				for (Component targetComp : targetedComps) {
					System.out.println("Implicit Intent -> " + targetComp.getName());
					if (!availableTargetedComponent(intent.getApp(), targetComp.getName())) {
						continue;
					}

					String callerMethod = intent.getLoggingPoint().getCallerMethodSignature();
					String callerComp = callerMethod.substring(1, callerMethod.indexOf(":"));

					if(callerComp.contains("$")) {
						callerComp = callerMethod.substring(1, callerMethod.indexOf("$"));
					}

					String calleeMethod = intent.getLoggingPoint().getCalleeMethodSignature();
					String iccMethod = calleeMethod.substring(calleeMethod.indexOf(":") + 1, calleeMethod.indexOf(">"));

					String calleeComp = targetComp.getName();

					ComponentTransition componentTransition = new ComponentTransition();
					componentTransition.setSourceC(callerComp);
					componentTransition.setTargetC(calleeComp);
					componentTransition.setICCMethod(iccMethod);
					componentTransition.setLinkType(Utilities.LinkType.ICC);

					componentTransitions.add(componentTransition);
				}
			}else {
				String targetCompName = intent.getComponentClass();
				System.out.println("Explicit Intent -> " + targetCompName);
				if (!availableTargetedComponent(intent.getApp(), targetCompName)) {
					continue;
				}

				String callerMethod = intent.getLoggingPoint().getCallerMethodSignature();
				String callerComp = callerMethod.substring(1, callerMethod.indexOf(":"));
				if(callerComp.contains("$")) {
					callerComp = callerMethod.substring(1, callerMethod.indexOf("$"));
				}

				String calleeMethod = intent.getLoggingPoint().getCalleeMethodSignature();
				String iccMethod = calleeMethod.substring(calleeMethod.indexOf(":") + 1, calleeMethod.indexOf(">"));

				String calleeComp = targetCompName;

				ComponentTransition componentTransition = new ComponentTransition();
				componentTransition.setSourceC(callerComp);
				componentTransition.setTargetC(calleeComp);
				componentTransition.setICCMethod(iccMethod);
				componentTransition.setLinkType(utils.Utilities.LinkType.ICC);

				componentTransitions.add(componentTransition);

			}
		}
		logger.info("[Icc Mapping] ...End Component Resolving Algorithm");
		return componentTransitions;
	}
}
