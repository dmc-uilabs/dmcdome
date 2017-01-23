package com.ge.ceed.domeapi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mit.cadlab.dome3.api.deploy.DeployServerConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ModelTestUtility {
	
	private static final String EXTERNAL_PERIMETER = "perimeter";
	private static final String EXTERNAL_AREA = "area";
	private static final String EXTERNAL_WIDTH = "width";
	private static final String EXTERNAL_LENGTH = "externalLength";
	private static final File FNAME = new File("./test/com/ge/ceed/domeapi","Model.json");

	public static void main(String[] args) throws IOException {

		GsonBuilder bldr = new GsonBuilder();
		// unnecessary when upgrading to gson-2.1
//				bldr.registerTypeHierarchyAdapter(Collection.class, new RuntimeTypeCollectionAdapter());
				bldr.setPrettyPrinting();
				Gson gson = bldr.create();
				System.out.println("filename: " + FNAME.getAbsolutePath());
				
				IModel m = makeDummyIModel();
				System.out.println(gson.toJson(m));
				FileWriter writer = new FileWriter(FNAME);
				gson.toJson(m, writer);
				writer.close();

	}
	
	private static IModel makeDummyIModel() {
		List<Integer> folderPath = Arrays.asList(1,2,4);
		
		DomeFolderEntity domeFolderEntity  = new DomeFolderEntity(folderPath.toArray(new Integer[]{}), "folderName", 12123);
		DomeModelEntity domeModelEntity = new DomeModelEntity(domeFolderEntity.getPath(), "domeModelName", 65, "domeModelId", "domeModel Description", 2012042515);
		DomeInterfaceEntity domeInterfaceEntity = new DomeInterfaceEntity(domeModelEntity, "domeInterfaceName", 66, "domeInterfaceId");
		
		//define the Model
		Model m = new Model(domeInterfaceEntity);
		
		// the in & out parameters of the new IntegrationModel
		ModelParam inParam = new ModelParam(EXTERNAL_LENGTH, "int", "cm");
		m.addInParam(inParam);
		ModelParam inParam2 = new ModelParam(EXTERNAL_WIDTH, "int", "cm");
		m.addInParam(inParam2);

		ModelParam outParam1 = new ModelParam(EXTERNAL_AREA, "int", "cm2");
		m.addOutParam(outParam1);
		ModelParam outParam2 = new ModelParam(EXTERNAL_PERIMETER, "int", "cm");
		m.addOutParam(outParam2);
		
 		return m;
		
	}

}
