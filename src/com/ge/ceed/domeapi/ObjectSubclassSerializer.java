package com.ge.ceed.domeapi;

import java.lang.reflect.Type;

import com.ge.ceed.domeapi.web.getModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Helps Gson to deserialize {@ Object} objects.
 * Serialization works fine, and needs no override.
 * Used in {@link getModel} 
 * @author dliscomb
 *
 */
public class ObjectSubclassSerializer implements JsonSerializer<Object> {

	@Override
	public JsonElement serialize(Object o, Type type, JsonSerializationContext context) {
		if (o == null) {
			return new JsonNull();
		}
		if (o.getClass().equals(Object.class)) {
			return new JsonObject();			
		}
		return context.serialize(o, o.getClass());
	}

}
