package com.ge.ceed.domeapi;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;

import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.DomeInterface;
import mit.cadlab.dome3.api.DomeModel;
import mit.cadlab.dome3.api.DomePlayspace;
import mit.cadlab.dome3.api.RuntimePlayspace;
import mit.cadlab.dome3.api.deploy.DeployServerConnection;

import com.ge.ceed.domeapi.web.Configurator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.*;
import org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pemsweb.IntegrationContext;


public class IntegrationModelTestUtility {
	
	private static final String TEST_FILE_FOLDER = "./test/com/ge/ceed/domeapi";

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IntegrationModelTestUtility.class);

	private static final InputStream PROPERTIES_STREAM = IntegrationModelTestUtility.class.getResourceAsStream("IntegrationModelTestUtility.properties");
	private static final String EXTERNAL_PERIMETER = "externalperimeter";
	private static final String EXTERNAL_AREA = "externalArea";
	private static final String EXTERNAL_WIDTH = "externalWidth";
	private static final String EXTERNAL_LENGTH = "externalLength";
	private static final File FNAME = new File(TEST_FILE_FOLDER,"IntegrationModel.json");
	private static final File realIMFileJSON = new File(TEST_FILE_FOLDER, "RealIntegrationModel.json");

	private static Gson gson;

	private static DomeProxy proxy;
	
	private Map<String, String> emptyMap = Collections.emptyMap();	

	@BeforeClass
	public static void startup() throws IOException, DomeProxyException {
		org.apache.log4j.Logger.getLogger("com.ge.ceed.domeapi").setLevel(Level.DEBUG);
		gson = new GsonBuilder()
		.setPrettyPrinting()
		.registerTypeAdapter(IConnector.class, new ConnectorDeSerializer())
		.registerTypeAdapter(IModel.class, new IModelDeSerializer())
		.registerTypeAdapter(DomeEntity.class, new DomeEntitySerializer())
		.registerTypeAdapter(ModelParam.class, new ModelParamDeserializer())
		.create();

		Properties domeAPIServicesProperties = Configurator.getConfig();
		proxy = new DomeProxy(domeAPIServicesProperties);
		logger.debug("Finished startup()");
	}
	@Ignore
	@Test
	public void deployJMIntegrationModel() throws IOException, DomeProxyException {
		String fileName = "JMIntegration.json";
		File realIMFileJSON = new File(TEST_FILE_FOLDER, fileName);
		Reader reader = getRealIntegrationModelReader(realIMFileJSON);
		IntegrationModel integrationModel = gson.fromJson(reader, IntegrationModel.class);
		if (logger.isDebugEnabled()) {
			logger.debug("\n{}", integrationModel);
			for (IConnector connector : integrationModel.getConnectors().values()) {
				logger.debug("\n{}", connector.toEqn());
			}
		}
		IModel returnEntity = proxy.putIntegrationModel(integrationModel, emptyMap);
		logger.debug(gson.toJson(returnEntity));
	}
	@Ignore
	@Test
	public void deployRealIntegrationModel() throws IOException, DomeProxyException {
		
		IntegrationModel integrationModel = makeRealIM(realIMFileJSON);
		if (logger.isDebugEnabled()) {
			logger.debug("\n{}", integrationModel);
			for (IConnector connector : integrationModel.getConnectors().values()) {
				logger.debug("\n{}", connector.toEqn());
			}
		}
		IModel returnEntity = proxy.putIntegrationModel(integrationModel, emptyMap);
		logger.debug(gson.toJson(returnEntity));
	}
	@Ignore
	@Test
	public void deserializeIM() throws IOException {
		IntegrationModel reference = makeDummyIM();
		logger.debug("\nreference:\n{}", reference);
		saveDummyIM(FNAME);
		Reader jsonSource = new FileReader(FNAME);
		IModel imFromGson = gson.fromJson(jsonSource, IntegrationModel.class);
		assertNotNull(imFromGson);
		logger.debug("\n{}", imFromGson);
		assertEquals(reference.toString(), imFromGson.toString());
		
		imFromGson = makeRealIM(realIMFileJSON);
		assertNotNull("Error making IntegrationModel from " + realIMFileJSON.toString(), imFromGson);
		
	}

	public static void main(String[] args) throws IOException {
		saveDummyIM(FNAME);
		logger.debug("\nSaved file: {}", FNAME);
	}
	
	private static void saveDummyIM(File jsonFile) throws IOException {

		GsonBuilder bldr = new GsonBuilder();
		bldr.setPrettyPrinting();
		Gson gson = bldr.create(); // NOTE: this one has no custom TypeAdapters
		logger.debug("\nfilename: {}", jsonFile);

		IModel m = makeDummyIM();
		logger.debug(gson.toJson(m));
		FileWriter writer = null;
		try {
			writer = new FileWriter(jsonFile);
			gson.toJson(m, writer);
		} finally {
			writer.close();
		}

	}
	
	private static IntegrationModel makeDummyIM() {

		Integer[] folderPath = new Integer[]{29};
		long modDate = Calendar.getInstance().getTime().getTime();
		DomeFolderEntity domeFolderEntity  = new DomeFolderEntity(folderPath, "DrewsModels", 32);
		
		DomeModelEntity domeModelEntity = new DomeModelEntity(domeFolderEntity.getPath(), "newIntegrationModelName", 65, "newIntegrationModel_id", "newIntegrationModel Description", modDate);
		DomeInterfaceEntity domeInterfaceEntity = new DomeInterfaceEntity(domeModelEntity, "newIntegrationInterfaceName", 66, "newIntegrationInterfaceId");
		
		//define the IntegrationModel
		IntegrationModel m = new IntegrationModel(domeFolderEntity, domeInterfaceEntity);
		m.setName("integrationModelName1");
		m.setSpace(DeployServerConnection.USERS);
		m.setDescription("Description of intgrationModelName1");
		
		// add references to the interfaces
		//Perimeter model:
		String perimeterModelInstanceName = "PerimeterModel1";
		DomeModelEntity perimeterDomeModelEntity = new DomeModelEntity(folderPath, perimeterModelInstanceName, 67, "PerimeterModel_id", "Return perimeter: 2*l + 2*w", modDate);
		DomeInterfaceEntity perimeterInterface = new DomeInterfaceEntity(perimeterDomeModelEntity, "PerimeterInterface", 68, "PerimeterInterface_id");
		Model perimeterModel = new Model(perimeterInterface);
		Server server = new Server("serverName", "8080", "userName", "password");
		perimeterModel.setServer(server);

		ModelParam perimModelLength = new ModelParam("PerimModelLength", "Real", "cm");
		perimModelLength.setId("PerimModelLength_id");
		perimeterModel.addInParam(perimModelLength);

		ModelParam perimModelWidth = new ModelParam("PerimModelWidth", "Real", "cm");
		perimModelWidth.setId("PerimModelWidth_id");
		perimeterModel.addInParam(perimModelWidth);
		
		ModelParam perimModelResult = new ModelParam("OutputInternalPerimeter", "Real", "cm");
		perimModelResult.setId("OutputInternalPerimeter_id");
		perimeterModel.addOutParam(perimModelResult);
		
		m.getInternalInterfaces().put(perimeterModelInstanceName, perimeterModel);
			
		//Area model:
		String areaModelInstanceName = "AreaModel1";
		DomeModelEntity areaDomeModelEntity = new DomeModelEntity(folderPath, areaModelInstanceName, 69, "AreaModel_id", "Return area: l*w", modDate);
		DomeInterfaceEntity areaInterface = new DomeInterfaceEntity(areaDomeModelEntity, "AreaInterface", 70, "AreaInterface_id");
		Model areaModel = new Model(areaInterface);
		areaModel.setServer(server);

		ModelParam areaModelWidth = new ModelParam("AreaModelWidth", "Real", "cm");
		areaModelWidth.setId("AreaModelWidth_id");
		areaModel.addInParam(areaModelWidth);

		ModelParam areaModelLength = new ModelParam("AreaModelLength", "Real", "cm");
		areaModelLength.setId("AreaModelLength_id");
		areaModel.addInParam(areaModelLength);
		
		ModelParam areaModelResult = new ModelParam("InternalAreaOutput","Real", "cm2");
		areaModelResult.setId("InternalAreaOutput_id");
		areaModel.addOutParam(areaModelResult);

		m.getInternalInterfaces().put(areaModelInstanceName, areaModel);
		
		// the in & out parameters of the new IntegrationModel
		ModelParam externalLength = new ModelParam(EXTERNAL_LENGTH, "int", "cm");
		externalLength.setInstancename("XP");
		m.addInParam(externalLength);
		ModelParam externalWidth = new ModelParam(EXTERNAL_WIDTH, "int", "cm");
		externalWidth.setInstancename("XP");
		m.addInParam(externalWidth);

		ModelParam externalArea = new ModelParam(EXTERNAL_AREA, "int", "cm2");
		externalArea.setInstancename("XP");
		m.addOutParam(externalArea);
		ModelParam externalPerimeter = new ModelParam(EXTERNAL_PERIMETER, "int", "cm");
		externalPerimeter.setInstancename("XP");
		m.addOutParam(externalPerimeter);

		
		//Now connect the inputs to the internal models
		
		ModelParam areaModelLengthInst = new ModelParam(null, null, null);
		areaModelLengthInst.setInstancename(areaModelInstanceName);
		areaModelLengthInst.setId("AreaModelLength_id");		
		SimplePassThroughConnector connector1 = new SimplePassThroughConnector(externalLength, areaModelLengthInst); 
		m.getConnectors().put("connector1",connector1);

		ModelParam perimModelLengthInstance = new ModelParam(null, null, null);
		perimModelLengthInstance.setInstancename(perimeterModelInstanceName);
		perimModelLengthInstance.setId("PerimModelLength_id");
		SimplePassThroughConnector connector1_1 = new SimplePassThroughConnector(externalLength, perimModelLengthInstance); 
		m.getConnectors().put("connector1_1",connector1_1);

		ModelParam areaModelWidthInst = new ModelParam(null, null, null);
		areaModelWidthInst.setInstancename(areaModelInstanceName);
		areaModelWidthInst.setId("AreaModelWidth_id");		
		SimplePassThroughConnector connector2 = new SimplePassThroughConnector(externalWidth, areaModelWidthInst); 
 		m.getConnectors().put("connector2",connector2);
 		
		ModelParam perimModelWidthInstance = new ModelParam(null, null, null);
		perimModelWidthInstance.setInstancename(perimeterModelInstanceName);
		perimModelWidthInstance.setId("PerimModelWidth_id");
		perimeterModel.addInParam(perimModelWidth);
 		SimplePassThroughConnector connector3 = new SimplePassThroughConnector(externalWidth, perimModelWidthInstance); 
 		m.getConnectors().put("connector3",connector3);
		
		//Now connect the outputs to the internal models
		ModelParam areaModelResultInstance = new ModelParam(null, null, null);
		areaModelResultInstance.setInstancename(areaModelInstanceName);
		areaModelResultInstance.setId("InternalAreaOutput_id");
		SimplePassThroughConnector oconnector1 = new SimplePassThroughConnector(areaModelResultInstance, externalArea); 
		m.getConnectors().put("oconnector1",oconnector1);
		
		ModelParam perimModelResultInstance = new ModelParam(null, null, null);
		perimModelResultInstance.setInstancename(perimeterModelInstanceName);
		perimModelResultInstance.setId("OutputInternalPerimeter_id");
		SimplePassThroughConnector oconnector2 = new SimplePassThroughConnector(perimModelResultInstance, externalPerimeter);
 		m.getConnectors().put("oconnector2",oconnector2);
		
 		return m;
		
	}

	static IntegrationModel makeRealIM(File imFile) throws FileNotFoundException {
		Reader reader = getRealIntegrationModelReader(imFile);
		IntegrationModel newIM = gson.fromJson(reader, IntegrationModel.class);
		logger.debug("\nIntegrationModel:\n ", gson.toJson(newIM));
		return newIM;
	}

	private static Reader getRealIntegrationModelReader(File imFile) throws FileNotFoundException {
		if (!imFile.exists()) {
			throw new FileNotFoundException("Can't find file: " + imFile.getAbsolutePath());
		}
		Reader reader = new FileReader(imFile);
		return reader;
	}

}
