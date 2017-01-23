package com.ge.ceed.domeapi;

import java.lang.reflect.Type;
import org.slf4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Helps Gson to deserialize & serialize IDomeEntity objects.
 * @author dliscomb
 *
 */
public class DomeEntitySerializer implements JsonDeserializer<IDomeEntity>, JsonSerializer<IDomeEntity> {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DomeEntitySerializer.class.getCanonicalName());

	@Override
	public DomeEntity deserialize(JsonElement element, Type clazz, JsonDeserializationContext context) throws JsonParseException {
		
		JsonObject object = (JsonObject)element;
		JsonElement el = object.get("type");
		String type = (el == null? null : el.getAsString());
		logger.debug(String.format("looking at element type '%s'", type));
		if (type != null) {
			if (type.equals(DomeFolderEntity.TYPE)) {
				return context.deserialize(element, DomeFolderEntity.class);
			} else if (type.equals(DomeModelEntity.TYPE)) {
				return context.deserialize(element, DomeModelEntity.class);
			} else if (type.equals(DomeInterfaceEntity.TYPE)) {
				return context.deserialize(element, DomeInterfaceEntity.class);
			} else if (type.equals(DomeProjectEntity.TYPE)){
				return context.deserialize(element, DomeProjectEntity.class);
			}
		}
		return null;
	}
	
	@Override
	public JsonElement serialize(IDomeEntity entity, Type type, JsonSerializationContext context) {		
		return context.serialize(entity, entity.getClass());		
	}

}
