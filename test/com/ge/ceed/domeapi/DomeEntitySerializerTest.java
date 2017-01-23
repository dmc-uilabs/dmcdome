package com.ge.ceed.domeapi;

import static org.junit.Assert.*;

import java.util.Date;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.DomeFolder;
import mit.cadlab.dome3.api.DomeInterface;
import mit.cadlab.dome3.api.DomeModel;
import mit.cadlab.dome3.api.DomeProject;
import mit.cadlab.dome3.api.RuntimePlayspace;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DomeEntitySerializerTest {
	
	private static final String JSON_NULL = "null";

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DomeEntitySerializerTest.class);

	private static final int VERSION = 1;
	private static final String PROJECT_DESCRIPTION = "Dome Project Description";
	private static final long DATE_MODIFIED = System.currentTimeMillis();
	private static final String PROJECT_NAME = "Dome Project Name";
	private static final String PROJECT_ID = "Project XXXX";
	private static final int FOLDER_ID = 3;
	private static final int PARENT_FOLDER = 12;
	private static final int GRANDPARENT_FOLDER = 11;
    private static final Integer[] FOLDER_PATH = new Integer[]{GRANDPARENT_FOLDER, PARENT_FOLDER};
	private static final String FOLDER_NAME = "Test Folder";
    private static final String FOLDER_JSON = String.format("{\"type\":\"%s\",\"name\":\"%s\",\"path\":[%d,%d,%d]}", DomeFolderEntity.TYPE, FOLDER_NAME, GRANDPARENT_FOLDER, PARENT_FOLDER, FOLDER_ID);

	private static final String MODEL_ID = "ModelXXX";
	private static final String MODEL_NAME = "Dome Model Name";
	private static final String MODEL_DESCRIPTION = "Model Description";
	private static final String MODEL_TYPE = "DOME"; 
    private static final String MODEL_JSON = String.format("{\"version\":%d,\"modelId\":\"%s\",\"description\":\"%s\",\"dateModified\":%d,\"type\":\"%s\",\"name\":\"%s\",\"path\":[%d,%d]}", VERSION, MODEL_ID, MODEL_DESCRIPTION, DATE_MODIFIED, DomeModelEntity.TYPE, MODEL_NAME, GRANDPARENT_FOLDER, PARENT_FOLDER);

	private static final String INTERFACE_ID = "Interface ID";
	private static final String INTERFACE_NAME = "Dome Model Interface Name";
	private static final String INTERFACE_DESCRIPTION = "Interface Description";
    private static final String MODEL_INTERFACE_JSON = String.format("{\"version\":%d,\"modelId\":\"%s\",\"interfaceId\":\"%s\",\"type\":\"%s\",\"name\":\"%s\",\"path\":[%d,%d]}", VERSION, MODEL_ID, INTERFACE_ID, DomeInterfaceEntity.TYPE, INTERFACE_NAME, GRANDPARENT_FOLDER, PARENT_FOLDER);

	private static final String PROJECT_JSON = String.format("{\"version\":%d,\"projectId\":\"%s\",\"description\":\"%s\",\"dateModified\":%d,\"type\":\"%s\",\"name\":\"%s\",\"path\":[%d,%d]}", VERSION, PROJECT_ID, PROJECT_DESCRIPTION, DATE_MODIFIED, DomeProjectEntity.TYPE, PROJECT_NAME, GRANDPARENT_FOLDER, PARENT_FOLDER);
	private static final String PROJECT_INTERFACE_JSON = String.format("{\"version\":%d,\"interfaceId\":\"%s\",\"projectId\":\"%s\",\"type\":\"%s\",\"name\":\"%s\",\"path\":[%d,%d]}", VERSION, INTERFACE_ID, PROJECT_NAME, DomeInterfaceEntity.TYPE, INTERFACE_NAME, GRANDPARENT_FOLDER, PARENT_FOLDER);

	private static final GsonBuilder gsonBldr = new GsonBuilder();
	private Gson gson = gsonBldr.create();
	
	static {
		gsonBldr.registerTypeAdapter(DomeEntity.class, new DomeEntitySerializer());
		gsonBldr.registerTypeAdapter(IDomeEntity.class, new DomeEntitySerializer());
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDeserializeNull() {
		assertNull(gson.fromJson((String)null, DomeEntity.class));
	}

	@Test
	public void testSerializeNull() {
		IDomeEntity entity = null;
		String json = gson.toJson(entity);
		logger.debug("\njson: {}\nsize: {}",json, json.length());
		assertEquals(JSON_NULL, json);
	}

	@Test
	public void testDeserializeNonTypedEntity() {		
		String json = String.format("{ \"name\": \"%s\", \"folderId\": %d }", PROJECT_NAME, FOLDER_ID);
		DomeEntity entity = gson.fromJson(json, DomeEntity.class); 
		assertNull(entity);
	}

	@Test
	public void testSerializeNonTypedEntity() {
		class NonDomeEntity extends DomeEntity {
			public NonDomeEntity() {
				super(null, new Integer[]{0}, 0, "Bogus Type Name");
			}
		}
		String result = gson.toJson(new NonDomeEntity());
		assertEquals(JSON_NULL, result);
	}

	@Test
	public void testDeserializeUnrecognizedTypedEntity() {
		String json = String.format("{ \"type\": \"Bogus Type\", \"name\": \"%s\", \"folderId\": [%d] }", PROJECT_NAME, FOLDER_ID);		
		assertNull(gson.fromJson(json, DomeEntity.class));
	}

	@Test
	public void testSerializeUnrecognizedTypedEntity() {
		class NonDomeEntity extends DomeEntity {

			public NonDomeEntity() {
				super("Bogus Type", FOLDER_PATH, 0, PROJECT_NAME);
			}
		}
		assertEquals(JSON_NULL, gson.toJson(new NonDomeEntity()));
	}

	@Test
	public void testDeserializeDomeFolderEntity() {
       		DomeFolderEntity entity = gson.fromJson(FOLDER_JSON, DomeFolderEntity.class);
		assertEquals("Compare entity type", DomeFolderEntity.TYPE, entity.getType());
		assertEquals("Compare folder name", FOLDER_NAME, entity.getName());
		assertEquals("Compare path length", FOLDER_PATH.length+1, entity.getPath().length);
		int k =0;
		for (; k < FOLDER_PATH.length; k++) {
			assertEquals("path difference: ", FOLDER_PATH[k], entity.getPath()[k]);
		}
		assertEquals("Compare folder ID", FOLDER_ID, (int)entity.getPath()[k]);
	}

	@Test
	public void testSerializeDomeFolderEntity() {
		DomeFolder folder = new DomeFolder(FOLDER_NAME, FOLDER_ID, null);
		DomeFolderEntity entity = new DomeFolderEntity(FOLDER_PATH, folder);
		assertEquals("Compare folder JSON", FOLDER_JSON, gson.toJson(entity, DomeFolderEntity.class));
	}

	@Test
	public void testDeserializeDomeModelEntity() {
		DomeModelEntity entity = gson.fromJson(MODEL_JSON, DomeModelEntity.class);
		assertEquals("Compare entity type", DomeModelEntity.TYPE, entity.getType());
		assertEquals("Compare model ID", MODEL_ID, entity.getModelId());
		assertEquals("Compare model name", MODEL_NAME, entity.getName());
		assertEquals("Compare model description", MODEL_DESCRIPTION, entity.getDescription());
		assertEquals("Compare date modified", DATE_MODIFIED, entity.getDateModified());
		assertNotNull(entity.getPath());
		assertEquals("Compare path length", FOLDER_PATH.length, entity.getPath().length);
		for (int i = 0; i < FOLDER_PATH.length-1; i++) {
		    assertEquals("Compare path elements", FOLDER_PATH[i], entity.getPath()[i]);
		}
	}

	@Test
	public void testSerializeDomeModelEntity() {
		DomeModel model = new DomeModel(MODEL_ID, MODEL_NAME, MODEL_DESCRIPTION, MODEL_TYPE, new Date(DATE_MODIFIED), VERSION, false, null, null);		
		DomeModelEntity entity = new DomeModelEntity(FOLDER_PATH, model);
		assertEquals(MODEL_JSON, gson.toJson(entity, DomeModelEntity.class));
	}

	@Test
	public void testDeserializeDomeModelInterfaceEntity() {
		DomeInterfaceEntity entity = gson.fromJson(MODEL_INTERFACE_JSON, DomeInterfaceEntity.class);
		assertNotNull(entity);
		assertEquals("Version comparison", VERSION, entity.getVersion());
		assertEquals("Model ID comparison", MODEL_ID, entity.getModelId());
		assertNull(entity.getProjectId());
		assertEquals("Interface type comparision", DomeInterfaceEntity.TYPE, entity.getType());
		assertEquals("Interface name comparison", INTERFACE_NAME, entity.getName());
		assertNotNull(entity.getPath());
		assertEquals("Folder path length comparison", FOLDER_PATH.length, entity.getPath().length);
		for (int i = 0; i < FOLDER_PATH.length; i++) {
		    assertEquals("Filder path comparision", FOLDER_PATH[i], entity.getPath()[i]);
		}
	}

	/**
	 * Tests DomeInterfaceEntity using the following constructor :
	 *  public DomeInterfaceEntity(DomeModelEntity model, DomeInterface i) {
	 */
	@Test
	public void testSerializeDomeModelInterfaceEntity() {
		DomeModelEntity model = new DomeModelEntity(FOLDER_PATH, new DomeModel(MODEL_ID, MODEL_NAME, MODEL_DESCRIPTION, MODEL_TYPE, new Date(DATE_MODIFIED), VERSION, false, null, null));
		DomeInterface intface = new DomeInterface(INTERFACE_NAME, INTERFACE_ID, INTERFACE_DESCRIPTION, VERSION, new Date(DATE_MODIFIED), null, null);				
		DomeInterfaceEntity entity = new DomeInterfaceEntity(model, intface);
		assertEquals(MODEL_INTERFACE_JSON, gson.toJson(entity, DomeInterfaceEntity.class));
	}

	@Test
	public void testDeserializeDomeProjectInterfaceEntity() {
		DomeInterfaceEntity entity = (DomeInterfaceEntity)gson.fromJson(PROJECT_INTERFACE_JSON, DomeEntity.class);
		assertEquals(VERSION, entity.getVersion());
		assertNull(entity.getModelId());
		// FIXME: there is a bug in the dome api where it compares IDs to the name field for searching
		// This needs to be changed to 
		// assertEquals(PROJECT_ID, entity.getProjectId());
		// as soon as the bug is fixed
		assertEquals(PROJECT_NAME, entity.getProjectId());
		assertEquals(DomeInterfaceEntity.TYPE, entity.getType());
		assertEquals(INTERFACE_NAME, entity.getName());
		assertEquals(FOLDER_PATH.length, entity.getPath().length);
		for (int i = 0; i < FOLDER_PATH.length; i++) {
			assertEquals(FOLDER_PATH[i], entity.getPath()[i]);
		}
	}

	/**
	 * Tests DomeInterfaceEntity using the following constructor:
	 * 	public DomeInterfaceEntity(DomeProjectEntity project, DomeInterface i) {
	 */	
	@Test
	public void testSerializeDomeProjectInterfaceEntity() {
		DomeProjectEntity project = new DomeProjectEntity(FOLDER_PATH, new DomeProject(PROJECT_ID, PROJECT_NAME, PROJECT_DESCRIPTION, new Date(DATE_MODIFIED), VERSION, false, null, null));
		DomeInterface intface = new DomeInterface(INTERFACE_NAME, INTERFACE_ID, INTERFACE_DESCRIPTION, VERSION, new Date(DATE_MODIFIED), null, null);				
		DomeInterfaceEntity entity = new DomeInterfaceEntity(project, intface);
		assertEquals(PROJECT_INTERFACE_JSON, gson.toJson(entity));
	}
	

	@Test
	public void testDeserializeDomeProjectEntity() {
		DomeProjectEntity entity = (DomeProjectEntity)gson.fromJson(PROJECT_JSON, DomeEntity.class);
		assertEquals("Compare project ID", PROJECT_ID, entity.getProjectId());
		assertEquals("Compare modified date", DATE_MODIFIED, entity.getDateModified());
		assertEquals("Compare project description", PROJECT_DESCRIPTION, entity.getDescription());
		assertEquals("Compare project type", DomeProjectEntity.TYPE, entity.getType());
		assertEquals("Compare project name", PROJECT_NAME, entity.getName());
		assertEquals("Compare project version", VERSION, entity.getVersion());
		assertEquals(FOLDER_PATH.length, entity.getPath().length);
		for (int i = 0; i < FOLDER_PATH.length; i++) {
		    assertEquals(FOLDER_PATH[i], entity.getPath()[i]);
                }
	}

	@Test
	public void testSerializeDomeProjectEntity() {
		DomeProject project = new DomeProject(PROJECT_ID, PROJECT_NAME, PROJECT_DESCRIPTION, new Date(DATE_MODIFIED), VERSION, false, null, null);
		DomeProjectEntity entity = new DomeProjectEntity(FOLDER_PATH, project);
		System.out.println(PROJECT_JSON+"\n"+gson.toJson(entity));
		assertEquals("Project json comparison", PROJECT_JSON, gson.toJson(entity));
	}

	 
}
