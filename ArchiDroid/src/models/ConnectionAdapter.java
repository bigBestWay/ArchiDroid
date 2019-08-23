package models;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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

		String sourceComp = ct.getSourceC();
		String targetComp = ct.getTargetC();
		sourceComp = sourceComp.substring(sourceComp.lastIndexOf(".") + 1);
		targetComp = targetComp.substring(targetComp.lastIndexOf(".") + 1); 

		String linkType = ct.getLinkType().name();

		jsonObject.addProperty("source", sourceComp);
		jsonObject.addProperty("target", targetComp);
		jsonObject.addProperty("type", linkType); // I'm gettting exception for Omni-Note app why??

		if(! (ct.getStyle() == null)) {
			jsonObject.addProperty("style", ct.getStyle().name());
			//System.out.println("Style -> " + ct.getStyle().name());
		}
		if(linkType.equalsIgnoreCase(utils.Utilities.LinkType.Direct.name()) && !(ct.getInvokedMethods() == null) ) {

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
