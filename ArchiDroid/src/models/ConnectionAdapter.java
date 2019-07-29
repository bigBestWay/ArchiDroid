package models;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonElement;
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
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Source", ct.getSourceC());
		jsonObject.addProperty("Target", ct.getTargetC());
		jsonObject.addProperty("Type", ct.getLinkType().name()); // I'm gettting exception for Omni-Note app why??
		if(ct.getLinkType().name().equalsIgnoreCase(utils.Utilities.LinkType.Direct.name()) && !(ct.getInvokedMethods() == null) ) {
			jsonObject.addProperty("CalledMethods", ct.getInvokedMethods().toString());
		}

		return jsonObject;
	}


}
