package models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 * @author - Tanjina Islam
 *
 * @date - 15-07-2019
 */
public class AppComponentAdapter implements JsonSerializer<AppComponent> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(AppComponent appComp, Type type, JsonSerializationContext jsc) {
		// TODO Auto-generated method stub
		JsonObject jsonObject = new JsonObject();

		String compName = appComp.getClassName();
		compName = compName.substring(compName.lastIndexOf(".") + 1);
		String compType = appComp.getComponentType().name();

		jsonObject.addProperty("name", compName);
		jsonObject.addProperty("type", compType);

		return jsonObject;
	}

}