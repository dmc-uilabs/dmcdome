package com.ge.ceed.domeapi;

import java.lang.reflect.Type;

import org.slf4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Helps Gson to deserialize IConnector objects.
 * Serialization works fine, and needs no override.
 * 
 * @author dliscomb
 *
 */
public class ConnectorDeSerializer implements JsonDeserializer<IConnector> {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ConnectorDeSerializer.class);

// TODO: extend this to handle more specific types of IConnectors
	@Override
	public IConnector deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		logger.debug(jsonElement.toString());
		SimplePassThroughConnector connector = context.deserialize(jsonElement, SimplePassThroughConnector.class);
		logger.debug("\n{}",connector);
		return connector;
	}

}
