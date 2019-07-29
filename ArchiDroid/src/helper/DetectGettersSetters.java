package helper;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.util.Chain;
import utils.CaseConverter;

/**
 * @author - Tanjina Islam
 *
 * @date - 22-06-2019
 */
public class DetectGettersSetters {
	private final static Logger logger = LoggerFactory.getLogger(DetectGettersSetters.class);
	private final static String TAG = "[" + DetectGettersSetters.class.getSimpleName() + "]";

	private static DetectGettersSetters instance = null;

	private DetectGettersSetters() {

	}

	public static DetectGettersSetters getInstance() {
		if(instance == null) {
			instance = new DetectGettersSetters();
		}
		return instance;
	}

	public boolean isGetterMethod(SootMethod method, String fieldNme) {
		if(method.isPublic() && method.getParameterCount() == 0) {
			//			if (method.getName().matches("^get[A-Z].*") &&
			//					!method.getReturnType().toString().matches("void"))
			//				return true;
			//			if (method.getName().matches("^is[A-Z].*") &&
			//					method.getReturnType().toString().matches("boolean"))
			//				return true;

			if (method.getName().matches("get" + CaseConverter.capatalizeFieldName(fieldNme)) &&
					!method.getReturnType().toString().matches("void"))
				return true;
			if (method.getName().matches("is" + CaseConverter.capatalizeFieldName(fieldNme)) &&
					method.getReturnType().toString().matches("boolean"))
				return true;
		}
		return false;
	}

	public boolean isSetterMethod(SootMethod method, String fieldNme) {
		//return method.isPublic() && method.getReturnType().toString().matches("void") && method.getParameterCount() == 1 && method.getName().matches("^set[A-Z].*");
		return method.isPublic() && method.getReturnType().toString().matches("void") && method.getParameterCount() == 1 && method.getName().matches("set" + CaseConverter.capatalizeFieldName(fieldNme));
	}

	public boolean isGetterMethodFinal(SootMethod method, String finalFieldname) {
		if(method.isPublic() && method.getParameterCount() == 0) {
			if (method.getName().matches("get" + CaseConverter.allCapsToCamelCase(finalFieldname)) &&
					!method.getReturnType().toString().matches("void"))
				return true;
			if (method.getName().matches("is" + CaseConverter.allCapsToCamelCase(finalFieldname)) &&
					method.getReturnType().toString().matches("boolean"))
				return true;
		}
		return false;
	}
	public List<SootMethod> findGettersSetters(SootClass sClass) {

		Chain<SootField> findClassFields = sClass.getFields();
		List<SootMethod> list = new ArrayList<SootMethod>();
		list.clear();

		if(!findClassFields.isEmpty()) {

			for(SootField sField : findClassFields) {
				//list.clear();
				// Second condition is for the default type checking for attributes 
				//which don't explicitly define public/private/protected type. But the default value in java = private
				if(sField.isPrivate() || (!sField.isPrivate() && !sField.isPublic() && !sField.isProtected())) { 
					List<SootMethod> classMethods = sClass.getMethods();
					if(!sField.isFinal()) {
						for (SootMethod method : classMethods)
							if (isGetterMethod(method, sField.getName()) || isSetterMethod(method, sField.getName()))
								list.add(method);
						//break;
					}else {
						for (SootMethod method : classMethods)
							if (isGetterMethodFinal(method, sField.getName()))
								list.add(method);
						//break;
					}
				}
			}
		}
		return list;
	}
}
