package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import soot.SootMethod;

/**
 * @author - Tanjina Islam
 *
 * @date - 15-07-2019
 */
public class ConnectionAdapter implements JsonSerializer<ComponentTransition> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(ComponentTransition ct, Type type, JsonSerializationContext jsc) {
		// TODO Auto-generated method stub
		Set<String> invokedMethodList = new LinkedHashSet<>();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("source", ct.getSourceC());
		jsonObject.addProperty("target", ct.getTargetC());
		jsonObject.addProperty("type", ct.getLinkType().name()); // I'm gettting exception for Omni-Note app why??
		if(ct.getLinkType().name().equalsIgnoreCase(utils.Utilities.LinkType.Direct.name()) && !(ct.getInvokedMethods() == null) ) {
			
			JsonArray calledMethods = new JsonArray();
			invokedMethodList = ct.getInvokedMethods();
			for(String method : invokedMethodList) {
				calledMethods.add(method);
			}
			jsonObject.add("calledMethods", calledMethods);
			//jsonObject.addProperty("calledMethods", calledMethods.toString());
		}

		return jsonObject;
	}
	
//	public String toJson() {
//		 JsonObject details = new JsonObject();
//		 details.addProperty(FIELD_LEVEL, level.toString());
//		 JsonArray conditionResults = new JsonArray();
//		 for (EvaluatedCondition condition : this.conditions) {
//		  conditionResults.add(toJson(condition));
//		 }
//		 details.add("conditions", conditionResults);
//		 details.addProperty(FIELD_IGNORED_CONDITIONS, ignoredConditions);
//		 return details.toString();
//		}
	
	public String toJson(Set<String> invokedMethodList) {
		 JsonObject methodObj = new JsonObject();
		 JsonArray calledMethods = new JsonArray();
			for(String method : invokedMethodList) {
				calledMethods.add(method);
			}
		 methodObj.add("calledMethods", calledMethods);
		 return methodObj.toString();
		}


}
