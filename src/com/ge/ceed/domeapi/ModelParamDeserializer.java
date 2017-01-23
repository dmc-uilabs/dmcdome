package com.ge.ceed.domeapi;

import java.lang.reflect.Type;
import java.util.Vector;

import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Helps Gson to deserialize {@ ModelParam} objects.
 * Serialization works fine, and needs no override. 
 * @author dliscomb
 *
 */
public class ModelParamDeserializer implements JsonDeserializer<ModelParam> {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ModelParamDeserializer.class);

	private JsonObject object;

	@Override
	public ModelParam deserialize(JsonElement element, Type clazz, JsonDeserializationContext context) throws JsonParseException {
		object = (JsonObject)element;
		String name = getString("name");
		String type = getString("type");
		String unit = getString("unit");
		ModelParam mp = new ModelParam(name, type, unit);
		String id = getString("parameterid");
		mp.setId(id);
		String category = getString("category");
		mp.setCategory(category);
		String instancename = getString("instancename");
		mp.setInstancename(instancename);
		JsonElement el = object.get("value");
		mp.setValue(convert(el, false));		
		logger.debug("\ntype: {}\njson: {}\nmodelParam: {}", new Object[]{type, element.toString(), mp});
		return mp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Object convert(JsonElement el, boolean isNumber) {
		if (el == null) {
			return null;
		}
		if (el.isJsonArray()) {
			Vector v = new Vector();
			JsonArray arr = el.getAsJsonArray();
			for (int i = 0; i < arr.size(); ++i) {
				v.add(convert(arr.get(i), true));
			}
			return v;
		} else if (el.isJsonPrimitive()) {		
			if (isNumber) {	// This is used to force vectors to contain only numbers as
				// expected by the DomeAPI
				return el.getAsNumber();
			}
			return el.getAsString();
		} else if (el.isJsonObject()) {
			JsonObject o = (JsonObject)el;		
			if (o.has("obj")) {	// TODO: test this... this comes from EnumerationParameter
				return o.get("obj").getAsString();
			}
		}
		return null;
	}

	protected String getString(String elementName) {
		JsonElement element = object.get(elementName);
		return (element==null ? null : element.getAsString());
	}
}


