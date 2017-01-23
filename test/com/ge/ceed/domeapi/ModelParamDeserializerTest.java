package com.ge.ceed.domeapi;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Vector;

import mit.cadlab.dome3.api.DomeInterface;
import mit.cadlab.dome3.api.DomeModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModelParamDeserializerTest {

	private static final String INTERFACE_NAME = "Default Interface";
	private static final String MODEL_NAME = "Type Handling Sample Model";
	private static final String MODEL_ID = "6fb05d8d-bdd4-1004-8fec-6f57457681ee";
	private static final String INTERFACE_ID = "d8d50bf6-bdd4-1004-8fec-6f57457681ee";
	private static final String MODEL_DESCRIPTION = "Model's Description";
	private static final int VERSION = 1;
	private static final int FOLDER_ID = 123;
	private static final Integer[] PATH = new Integer[]{FOLDER_ID};
	
	private static final Date NOW = new Date();
	private static final String INTERFACE_JSON = 
		"{\"version\":" + VERSION + 
		",\"modelId\":\"" + MODEL_ID + 
		"\",\"interfaceId\":\"" + INTERFACE_ID + 
		"\",\"type\":\"" +	DomeInterfaceEntity.TYPE + 
		"\",\"name\":\"" + INTERFACE_NAME + 
		"\",\"path\":[" + FOLDER_ID + "]}";
	
	private static final String MODEL_JSON = 
	    "{\"interFace\":" + INTERFACE_JSON + 
	    ",\"inParams\":%s,"+
	    "\"outParams\":%s,"+
	    "\"modelName\":\""+INTERFACE_NAME+"\"}";
		
	private static GsonBuilder getGsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ModelParam.class, new ModelParamDeserializer());
		builder.registerTypeAdapter(Object.class, new ObjectSubclassSerializer());
		return builder;
	}

	private static Gson getGson() {
		GsonBuilder builder = getGsonBuilder();
		return builder.create();
	}

	private static IModel getModel() {
		DomeModelEntity m = new DomeModelEntity(PATH, new DomeModel(MODEL_ID, MODEL_NAME, MODEL_DESCRIPTION, DomeModelEntity.TYPE, NOW, VERSION, false, null, null));
		DomeInterface intface = new DomeInterface(INTERFACE_NAME, INTERFACE_ID, null, VERSION, NOW, null, null);				
		DomeInterfaceEntity entity = new DomeInterfaceEntity(m, intface);
		return new Model(entity);
	}

	private static String getParamJson(String name, String type, String unit, Object value) {
		String v = "" + value;
		if (value instanceof String) {
			v = "\"" + v + "\"";
		} else if (value instanceof Vector) {
			v = v.replaceAll(" ", ""); // Vector.toString produces [0, 0] where we need [0,0]
		}
		return String.format("{\"%1$s\":{\"name\":\"%1$s\",\"type\":\"%2$s\",\"unit\":\"%3$s\",\"value\":%4$s}}", name, type, unit, v);
	}
	
	private static String getParamJsonOut(String name, String type, String unit, Object value) {
		String v = "" + value;
		if (value instanceof String) {
			v = "\"" + v + "\"";
		} else if (value instanceof Vector) {
			v = v.replaceAll(" ", ""); // Vector.toString produces [0, 0] where we need [0,0]
		}
		return String.format("{\"%1$s\":{\"name\":\"%1$s\",\"type\":\"%2$s\",\"unit\":\"%3$s\",\"value\":%4$s,\"instancename\":\"%1$s\"}}", name, type, unit, v);
	}
		
	@Test
	public void testSerializeNoParams() {
		DomeModelEntity m = new DomeModelEntity(PATH, new DomeModel(MODEL_ID, MODEL_NAME, MODEL_DESCRIPTION, DomeModelEntity.TYPE, NOW, VERSION, false, null, null));
		DomeInterface intface = new DomeInterface(INTERFACE_NAME, INTERFACE_ID, null, VERSION, NOW, null, null);				
		DomeInterfaceEntity entity = new DomeInterfaceEntity(m, intface);
		IModel model = new Model(entity);
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, "{}", "{}"), gson.toJson(model));
	}

	@Test
	public void testDeserializeNoParams() {
		Gson gson = getGson();
		IModel model = gson.fromJson(String.format(MODEL_JSON, "{}", "{}"), Model.class);
		assertNotNull(model);
		assertEquals(INTERFACE_NAME, model.getInterface().getName());
		assertEquals(VERSION, model.getInterface().getVersion());
		assertNotNull(model.getInParams());
		assertNotNull(model.getOutParams());
		assertEquals(0, model.getInParams().length);
		assertEquals(0, model.getOutParams().length);
	}

	// Test in for boolean.
	@Test
	public void testSerializeWithBooleanParam() {
		IModel model = getModel();
		ModelParam param;
		model.addInParam(param = new ModelParam("MyBoolean", "Boolean", ""));
		param.setValue(false);
		
		Gson gson = getGson();
		String json = gson.toJson(model);
		String expectedJSON = String.format(MODEL_JSON, getParamJson("MyBoolean", "Boolean", "", false), "{}");
		assertEquals(expectedJSON, json);
	}
	
	// Test out for boolean.
	@Test
	public void testSerializeWithBooleanParamOut() {
		IModel model = getModel();
		ModelParam param;
		model.addOutParam(param = new ModelParam("MyBoolean", "Boolean", ""));
		param.setValue(false);
		
		Gson gson = getGson();
		String json = gson.toJson(model);
		String expectedJSON = String.format(MODEL_JSON, "{}", getParamJsonOut("MyBoolean", "Boolean", "", false));
		assertEquals(expectedJSON, json);
	}

	
	@Test
	public void testSerializeWithRealParam() {
		IModel model = getModel();
		ModelParam param;
		model.addInParam(param = new ModelParam("MyReal", "Real", "no unit"));
		param.setValue(45.3);
		
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, getParamJson("MyReal", "Real", "no unit", 45.3), "{}"), gson.toJson(model));
	}
	
	// Test out for real.
	@Test
	public void testSerializeWithRealParamOut() {
		IModel model = getModel();
		ModelParam param;

		model.addOutParam(param = new ModelParam("MyRealOutput", "Real", "no unit"));
		param.setValue(46.3);
		
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON,"{}",getParamJsonOut("MyRealOutput", "Real", "no unit", 46.3)), gson.toJson(model));
	}
  

	// Test in for int.
	@Test
	public void testSerializeWithIntegerParam() {
		IModel model = getModel();
		ModelParam param;
		model.addInParam(param = new ModelParam("MyInteger", "Integer", "no unit"));
		param.setValue(23);
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, getParamJson("MyInteger", "Integer", "no unit", 23), "{}"), gson.toJson(model));
	}
	
	// Test out for int.
	@Test
	public void testSerializeWithIntegerParamOut() {
		IModel model = getModel();
		ModelParam param;
		model.addOutParam(param = new ModelParam("MyInteger", "Integer", "no unit"));
		param.setValue(23);
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, "{}", getParamJsonOut("MyInteger", "Integer", "no unit", 23)), gson.toJson(model));
	}

	// Test in for String.
	@Test
	public void testSerializeWithStringParam() {
		IModel model = getModel();
		ModelParam param;
		model.addInParam(param = new ModelParam("MyString", "String", ""));
		param.setValue("Hello");
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, getParamJson("MyString", "String", "", "Hello"), "{}"), gson.toJson(model));
	}

	// Test out for String.
	@Test
	public void testSerializeWithStringParamOut() {
		IModel model = getModel();
		ModelParam param;
		model.addOutParam(param = new ModelParam("MyString", "String", ""));
		param.setValue("Hello");
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON,  "{}", getParamJsonOut("MyString", "String", "", "Hello")), gson.toJson(model));
	}

	// Test in for vector
	@Test
	public void testSerializeWithVectorParam() {
		IModel model = getModel();
		ModelParam param;
		model.addInParam(param = new ModelParam("MyVector", "Vector", "no unit"));
		Vector<Integer> v = new Vector<Integer>();
		v.add(0);
		v.add(0);
		param.setValue(v);
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, getParamJson("MyVector", "Vector", "no unit", v), "{}"), gson.toJson(model));
	}
	
	// Test out for vector
	@Test
	public void testSerializeWithVectorParamOut() {
		IModel model = getModel();
		ModelParam param;
		model.addOutParam(param = new ModelParam("MyVector", "Vector", "no unit"));
		Vector<Integer> v = new Vector<Integer>();
		v.add(0);
		v.add(0);
		param.setValue(v);
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, "{}", getParamJsonOut("MyVector", "Vector", "no unit", v) ), gson.toJson(model));
	}

	// Test Matrix in
	@Test 
	public void testSerializeWithMatrixParam() {
		IModel model = getModel();
		ModelParam param;
		model.addInParam(param = new ModelParam("MyMatrix", "Matrix", "no unit"));
		Vector<Vector<Double>> matrix = new Vector<Vector<Double>>();
		Vector<Double> v = new Vector<Double>();
		v.add(1.1);
		v.add(2.2);
		v.add(3.3);
		matrix.add(v);
		v = new Vector<Double>();
		v.add(4.4);
		v.add(5.5);
		v.add(6.6);
		matrix.add(v);
		param.setValue(matrix);
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, getParamJson("MyMatrix", "Matrix", "no unit", matrix), "{}"), gson.toJson(model));
	}
	
	// Test Matrix out
	@Test 
	public void testSerializeWithMatrixParamOut() {
		IModel model = getModel();
		ModelParam param;
		model.addOutParam(param = new ModelParam("MyMatrix", "Matrix", "no unit"));
		Vector<Vector<Double>> matrix = new Vector<Vector<Double>>();
		Vector<Double> v = new Vector<Double>();
		v.add(1.1);
		v.add(2.2);
		v.add(3.3);
		matrix.add(v);
		v = new Vector<Double>();
		v.add(4.4);
		v.add(5.5);
		v.add(6.6);
		matrix.add(v);
		param.setValue(matrix);
		Gson gson = getGson();
		assertEquals(String.format(MODEL_JSON, "{}", getParamJsonOut("MyMatrix", "Matrix", "no unit", matrix)), gson.toJson(model));
	}


	@Test
	public void testDeserializeWithBooleanParam() {
		GsonBuilder builder = getGsonBuilder();
		Gson gson = builder.create();
		
		String modelJSON = String.format(MODEL_JSON, getParamJson("MyBoolean", "Boolean", "no unit", true), "{}");
		IModel model = gson.fromJson(modelJSON, Model.class);
		assertEquals(INTERFACE_NAME, model.getInterface().getName());
		assertEquals(INTERFACE_ID, model.getInterface().getInterfaceId());
		assertArrayEquals(PATH, model.getInterface().getPath());
		assertNotNull(model.getInParams());
		assertNotNull(model.getOutParams());
		assertEquals(1, model.getInParams().length);
		assertEquals(0, model.getOutParams().length);
		assertEquals("MyBoolean", model.getInParams()[0].getName());
		assertEquals("Boolean", model.getInParams()[0].getType());
		assertEquals("no unit", model.getInParams()[0].getUnit());
		assertEquals("true", model.getInParams()[0].getValue());
	}

	@Test
	public void testDeserializeWithRealParam() {
		GsonBuilder builder = getGsonBuilder();
		Gson gson = builder.create();
		
		IModel model = gson.fromJson(String.format(MODEL_JSON, getParamJson("MyReal", "Real", "no unit", 45.4), "{}"), Model.class);
		assertEquals(INTERFACE_NAME, model.getInterface().getName());
		assertEquals(INTERFACE_ID, model.getInterface().getInterfaceId());
		assertArrayEquals(PATH, model.getInterface().getPath());
		assertNotNull(model.getInParams());
		assertNotNull(model.getOutParams());
		assertEquals(1, model.getInParams().length);
		assertEquals(0, model.getOutParams().length);
		assertEquals("MyReal", model.getInParams()[0].getName());
		assertEquals("Real", model.getInParams()[0].getType());
		assertEquals("no unit", model.getInParams()[0].getUnit());
		assertEquals("45.4", model.getInParams()[0].getValue());
	}
	
	@Test
	public void testDeserializeWithIntegerParam() {
		GsonBuilder builder = getGsonBuilder();
		Gson gson = builder.create();
		
		IModel model = gson.fromJson(String.format(MODEL_JSON, getParamJson("MyInteger", "Integer", "int unit", 123), "{}"), Model.class);
		assertEquals(INTERFACE_NAME, model.getInterface().getName());
		assertEquals(INTERFACE_ID, model.getInterface().getInterfaceId());
		assertArrayEquals(PATH, model.getInterface().getPath());
		assertNotNull(model.getInParams());
		assertNotNull(model.getOutParams());
		assertEquals(1, model.getInParams().length);
		assertEquals(0, model.getOutParams().length);
		assertEquals("MyInteger", model.getInParams()[0].getName());
		assertEquals("Integer", model.getInParams()[0].getType());
		assertEquals("int unit", model.getInParams()[0].getUnit());
		assertEquals("123", model.getInParams()[0].getValue());
	}

	@Test
	public void testDeserializeWithVectorParam() {
		GsonBuilder builder = getGsonBuilder();
		Gson gson = builder.create();
		Vector<Double> v = new Vector<Double>();
		v.add(1.1);
		v.add(2.2);
		v.add(3.3);
		
		IModel model = gson.fromJson(String.format(MODEL_JSON, getParamJson("MyVector", "Vector", "vec unit", v), "{}"), Model.class);
		assertEquals(INTERFACE_NAME, model.getInterface().getName());
		assertEquals(INTERFACE_ID, model.getInterface().getInterfaceId());
		assertArrayEquals(PATH, model.getInterface().getPath());
		assertNotNull(model.getInParams());
		assertNotNull(model.getOutParams());
		assertEquals(1, model.getInParams().length);
		assertEquals(0, model.getOutParams().length);
		assertEquals("MyVector", model.getInParams()[0].getName());
		assertEquals("Vector", model.getInParams()[0].getType());
		assertEquals("vec unit", model.getInParams()[0].getUnit());
		
		for (int i = 0; i < v.size(); ++i) {
			assertEquals(v.get(i).toString(), ((Vector)model.getInParams()[0].getValue()).get(i).toString());
		}
	}

}
