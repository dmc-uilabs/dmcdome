package com.ge.ceed.domeapi;

import java.lang.reflect.Type;

import org.slf4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Helps Gson to deserialize IModel objects.
 * Serialization works fine, and needs no override. 
 * @author dliscomb
 *
 */
public class IModelDeSerializer implements JsonDeserializer<IModel> {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IModelDeSerializer.class);

	@Override
	public IModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		Model connector = context.deserialize(jsonElement, Model.class);
		return connector;
	}

}
