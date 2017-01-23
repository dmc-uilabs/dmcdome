package com.ge.ceed.domeapi;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;

import javax.jms.JMSException;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.DomeFolder;
import mit.cadlab.dome3.api.DomeInterface;
import mit.cadlab.dome3.api.DomeModel;
import mit.cadlab.dome3.api.DomeProject;
import mit.cadlab.dome3.api.ParameterStatusChangeEvent;
import mit.cadlab.dome3.api.ParameterStatusChangeListener;
import mit.cadlab.dome3.api.ParameterValueChangeEvent;
import mit.cadlab.dome3.api.ParameterValueChangeListener;
import mit.cadlab.dome3.api.RuntimeInterface;
import mit.cadlab.dome3.api.RuntimeParameter;
import mit.cadlab.dome3.api.SolverStateTracker;
import mit.cadlab.dome3.api.deploy.DeployServerConnection;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployModelData;
import mit.cadlab.dome3.gui.deploy.components.DeployProjectData;
import mit.cadlab.dome3.gui.deploy.components.DeployProjectInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.UserGroupInfo;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.FileTransport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;
import mit.cadlab.dome3.util.DArrayList;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;

import com.ge.ceed.domeapi.web.ActiveMQClient;
import com.ge.ceed.domeapi.web.IMessageQueueClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * DomeProxy is the interface to a DOME Server. Instead of serializing the DOME
 * API objects directly, this class returns 'proxy' objects that represent the
 * requested DOME objects. All requests basically follow this pattern:
 * <ul>
 * <li>Using its 'path', get the DomeFolder for the requested object</li>
 * <li>Using the DomeFolder, get the requested object's information</li>
 * </ul>
 * Because the object organization is hierarchical, you can't just get a
 * DomeModel or a DomeProject, you always have to know what folder it's in.
 */
public class DomeProxy {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DomeProxy.class);

	private static final int SECOND = 1000;
	private static final String DEFAULT_TIMEOUT = String.format("%d", 60 * SECOND);
	private File webAppDir;
	private String webAppPathName;
	private String resultsDir;
	private Server server;
	private int timeout;
	
	// TODO: Really evaluate the need for this count being static. Just didn;t have the time to fully investigate - TDC
	// TODO: if the purpose is to track the number of messages for this run, I don't think it needs to be;
	// each instance of a run gets an instance of DomeProxy - drew
	private int domeMsgCount = 0;
	
	// Listen to changes to the running DOME model and report back to the queue.
	private ParameterStatusChangeListener modelStatusListener = null;
	private ParameterValueChangeListener modelValueListener = null;
	private IMessageQueueClient m_Queue = null;
	private DomeConnection m_connection = null;
	// For redirecting errors found during running a model
	private RedirectStdOutStdErr m_RedirectStdOutStdErr = null;
	private StringBuffer m_strAccumulatedMessages = null;


	/**
	 * Default constructor, that gets configuration from the default data in the archive, "config/config.properties"
	 * @throws IOException
	 * @throws DomeProxyException if something went wrong trying to get a queue
	 */
	public DomeProxy(String queue) throws IOException, DomeProxyException {
		
		m_Queue = new ActiveMQClient(queue);
		
		DomeClientApplication.DOME_SERVER_MODE = true;
		Properties props = new Properties();
		props.load(DomeProxy.class.getClassLoader().getResourceAsStream("config/config.properties"));
		load(props);
	}

	/**
	 * Constructor taking a specific set of properties
	 * @param props {@link Properties} object with initialization data
	 * @throws IOException
	 * @throws DomeProxyException if something went wrong trying to get a queue
	 */
	public DomeProxy(Properties props) throws IOException, DomeProxyException {
		DomeClientApplication.DOME_SERVER_MODE = true;
		load(props);
	}

	/**
	 * Primarily used by ctors, but could be used to manage an instance.
	 * @param props
	 * @throws IOException
	 * @throws DomeProxyException if something went wrong trying to get a queue
	 */
	void load(Properties props) throws IOException, DomeProxyException {
		webAppPathName = fixDirectoryPath(props.getProperty("webapp.dir", webAppPathName));
		resultsDir = fixDirectoryPath(props.getProperty("results.dir", resultsDir));
		
		String serverName = props.getProperty("dome.server.name");
		if (serverName != null) {
			server = new Server(serverName, props.getProperty("dome.server.port"), props.getProperty("dome.server.user"), props.getProperty("dome.server.pw"));
		}
		if (server == null) {
			throw new IllegalStateException("Illegal configuration; property 'dome.server.name' not found");
		}
		logger.debug("server params:\nname: {}\nport: {}", server.getName(), server.getPort());
		logger.debug("\nuser: {}\npassword: {}", server.getUser(), server.getPw());
		timeout = Integer.parseInt(props.getProperty("dome.executiontimelimit", DEFAULT_TIMEOUT));
		
		webAppDir = new File(webAppPathName);
		logger.debug("\nwebAppDir: '{}'", webAppDir);
	}

	/**
	 * Ensure the path sent in is formatted as a directory with a final slash char
	 * @param directoryPath String to be checked
	 * @return String with 'slash' character appended, if necessary
	 */
	static String fixDirectoryPath(String directoryPath) {
		if (directoryPath == null) {
			directoryPath = "";
		} else {
			if (!directoryPath.endsWith("/") || !directoryPath.endsWith("\\")) {
				directoryPath += File.separatorChar;
			}
		}
		return directoryPath;
	}

	/**
	 * Given a DomeFolder and a DomeModelEntity, return the same DomeModelEntity
	 * with its interfaces
	 * 
	 * @param folder
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IDomeEntity getInterfaces(DomeFolder folder, DomeModelEntity model) {
		if (folder == null) {
			throw new IllegalArgumentException("Cannot get interfaces; folder was not specified.");
		}
		if (model == null) {
			throw new IllegalArgumentException("Cannot get interfaces; model was not specified.");
		}
		DomeModel domeModel = folder.getModelById(model.getModelId());
		if (domeModel == null) {
			throw new IllegalArgumentException(String.format("Unable to find model with id: %s", model.getModelId()));
		}
		model = new DomeModelEntity(model.getPath(), domeModel);
		for (DomeInterface i : (List<DomeInterface>) domeModel.getInterfaces()) {
			model.addChild(new DomeInterfaceEntity(model, i));
		}
		return model;
	}

	/**
	 * Given an{@link IDomeEntity}, return the {@link DomeFolder} that contains it
	 * @param connection
	 * @param entity
	 * @return DomeFolder object
	 */
	static DomeFolder getEnclosingFolder(DomeConnection connection, IDomeEntity entity) {
		DomeFolder folder = getTopFolder(connection);
		if (entity != null) {
			logger.debug("Entity is not null: {}", (entity.getPath()));
			for (int folderId : entity.getPath()) {
				logger.debug("looking for sub-folder with id '{}'", folderId);
				folder = folder.getFolderById(folderId);
				if (folder == null) {
					logger.error("Invalid folder: {}", entity.getName());
					throw new IllegalStateException("Folder id " + folderId + " was not found.");
				}
			}
		}
		logger.debug("returning: {}", folder);
		return folder;
	}

	@SuppressWarnings("unchecked")
	static String getEnclosingFolderPath(DomeConnection connection, IDomeEntity entity) {
		DomeFolder folder = getTopFolder(connection);
		//TODO: all references are to "Public" playspace
		StringBuilder folderPath = new StringBuilder("darpa/Public/");
		if (entity != null) {
			logger.debug("Entity is not null: {}", (entity.getPath()));
			for (int folderId : entity.getPath()) {
				logger.debug("looking for sub-folder with id '{}'", folderId);
				folder = folder.getFolderById(folderId);
				if (folder == null) {
					logger.error("Invalid folder: {}", entity.getName());
					throw new IllegalStateException("Folder id " + folderId + " was not found.");
				}
				folderPath.append(folder.getFolderName()).append("/");
			}
		}
		folderPath.deleteCharAt(folderPath.length()-1);
		logger.debug("returning: {}", folderPath.toString());
		return folderPath.toString();
	}

	static String getFolderPath(DomeConnection connection, IDomeEntity entity) {
		DomeFolder folder = getTopFolder(connection);
		StringBuilder folderName = new StringBuilder(folder.getFolderName());
		if (entity != null) {
			logger.debug("Entity is not null: {}", (entity.getPath()));
			for (int folderId : entity.getPath()) {
				logger.debug("looking for sub-folder with id '{}'", folderId);
				folder = folder.getFolderById(folderId);
				if (folder == null) {
					logger.error("Invalid folder: {}", entity.getName());
					throw new IllegalStateException("Folder id " + folderId + " was not found.");
				}
				folderName.append("/").append(folder.getFolderName());
			}
		}
		logger.debug("returning: {}", folderName.toString());
		return folderName.toString();
	}

	/**
	 * Given a {@link DomeFolder} and a {@link DomeProjectEntity}, return the same
	 * DomeProjectEntity with its interfaces.
	 * 
	 * @param folder the DomeFolder in which to find project
	 * @param project the DomeProjectEntity to be "filled in"
	 * @return the DomeProjectEntity passed in, but with its interfaces
	 */
	@SuppressWarnings("unchecked")
	public IDomeEntity getInterfaces(DomeFolder folder, DomeProjectEntity project) {
		if (folder == null) {
			throw new IllegalArgumentException("Cannot get interfaces; folder was not specified.");
		}
		if (project == null) {
			throw new IllegalArgumentException("Cannot get interfaces; project was not specified.");
		}
		DomeProject p = folder.getProjectById(project.getName());
		// TODO: there is a bug in the DomeFolder.class where it is comparing
		// Ids to project Names (line 340). Until fixed, we need to pass the name in as the Id
		if (p == null) {
			throw new IllegalStateException("Unable to locate project with id " + project.getProjectId());
		}
		project = new DomeProjectEntity(project.getPath(), p);
		for (DomeInterface i : (List<DomeInterface>) p.getInterfaces()) {
			project.addChild(new DomeInterfaceEntity(project, i));
		}
		return project;
	}

	/**
	 * Given a {@link DomeInterfaceEntity}, return a {@link Model} with the parameters populated in it
	 * @param interFace
	 * @return an IModel object with the parameters defined 
	 */
	@SuppressWarnings("unchecked")
	public IModel getParams(DomeInterfaceEntity interFace) {

		DomeConnection connection = null;

		Model m = null;
		try {
			connection = server.openConnection();
			m = new Model(interFace);
			DomeInterface domeInterface = getDomeInterface(connection, interFace);
			RuntimeInterface run = domeInterface.createRuntimeInterface();
			m.setInParams(convertToModelParams(run.getIndependentParameters()));
			m.setOutParams(convertToModelParams(run.getResultParameters()));
			ModelParam[] intermediates = convertToModelParams(run.getIntermediateParameters());
			for (ModelParam modelParam : intermediates) {
				m.addOutParam(modelParam);
			}
			m.setServer(server);
			m.setDescription(domeInterface.getDescription());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					logger.error("Problem closing DOME server connection:", e);
				}
			}
		}
		return m;
	}

	/**
	 * interFace could be a Model's interface, or a Project's interface
	 * @param domeConnection
	 * @param interFace
	 * @return the original DomeInterface with 
	 */
	static DomeInterface getDomeInterface(DomeConnection domeConnection, DomeInterfaceEntity interFace) {
		DomeFolder folder = getEnclosingFolder(domeConnection, interFace);
		logger.debug("\ngetting interface: {}", interFace);
		if (interFace.isModelInterface()) {
			logger.debug("model interface");
			DomeModel model = folder.getModelById(interFace.getModelId());

			if (model == null) {
				throw new IllegalStateException("Model id " + interFace.getModelId() + " does not refer to a valid Model on the DomeServer");
			}
			return model.getInterfaceById(interFace.getInterfaceId());
		} else if (interFace.isProjectInterface()) {
			logger.debug("project interface");
			// assume it's an integrated project
			DomeProject project = folder.getProjectById(interFace.getProjectId());
			if (project == null) {
				throw new IllegalStateException("Project id " + interFace.getProjectId() + " does not refer to a valid project on the DomeServer");
			}
			return project.getInterfaceById(interFace.getInterfaceId());
		}

		throw new IllegalStateException("Unable to retrieve Interface.  Missing model or project id");
	}

	/**
	 * Takes an IDomeEntity and returns that same entity populated with all its
	 * children
	 * 
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IDomeEntity getChildren(IDomeEntity parent) {
		logger.debug("in getChildren");
		DomeConnection connection = null;
		logger.debug("\nGot domeEntity: {}", parent);
		try {
			connection = server.openConnection();
			DomeFolder parentFolder = getEnclosingFolder(connection, parent);
			Integer[] path = null;
			if (parent != null) {
				if (parent instanceof DomeProjectEntity) {
					logger.debug("getting interfaces for a DomeProjectEntity");
					return getInterfaces(parentFolder, (DomeProjectEntity) parent);
				} else if (parent instanceof DomeModelEntity) {
					logger.debug("getting interfaces for a DomeModelEntity");
					return getInterfaces(parentFolder, (DomeModelEntity) parent);
				}
				path = parent.getPath();
			}
			// must be a folder
			logger.debug("getting children for a DomeFolder");
			DomeFolderEntity parentWithChildren = new DomeFolderEntity(path, parentFolder);
			for (DomeFolder f : (List<DomeFolder>) parentFolder.getFolders()) {
				parentWithChildren.addChild(new DomeFolderEntity(path, f));
			}
			for (DomeModel m : (List<DomeModel>) parentFolder.getModels()) {
				parentWithChildren.addChild(new DomeModelEntity(path, m));
			}
			for (DomeProject p : (List<DomeProject>) parentFolder.getProjects()) {
				parentWithChildren.addChild(new DomeProjectEntity(path, p));
			}
			return parentWithChildren;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					logger.error("Problem closing DOME server connection:", e);
				}
			}
		}
	}

	/**
	 * Takes an {@link IntegrationModel} and deploys it to the DOME server
	 * 
	 * @param integrationModel The integration model to be shared
	 * @param paramFiles A map with pairs matching parameter name to input file.
	 * @return an {@link IModel} with the interface for the shared IntegrationModel
	 * @throws DomeProxyException 
	 */
	public IModel putIntegrationModel(IntegrationModel integrationModel, Map<String, String> paramFiles) throws DomeProxyException {

		IModel newModel = null;
		try {
			newModel = deployIntegrationModel(server, integrationModel, paramFiles);

		} catch (Exception ioe) {
			// throw something different
			throw new DomeProxyException("Unable to save integration model: " + ioe.getMessage());
		}

		return newModel;
	}

	/**
	 * left package-private for testing
	 * 
	 * @param domeConnection
	 * @param integrationModel 
	 * @param paramFiles any files that needed to be passed along as parameters
	 * @return an IModel that represents the integration model
	 * @throws IOException
	 * @throws DomeProxyException 
	 * @throws XmlRpcException 
	 */
	IModel deployIntegrationModel(Server server,  IntegrationModel integrationModel, Map<String, String> paramFiles) throws IOException, DomeProxyException, XmlRpcException {

		IModel interfaceWithParameters = null;
		File localFileFolder = null;
		DomeConnection domeConnection = server.openConnection();
		try {
			File integrationProjectFile = null;
			IntegrationProjectSaver integrationProjectSaver = new IntegrationProjectSaver();
			String sessid = UUID.randomUUID().toString();
			// save model on web server
			String moddir = webAppDir + "/" + sessid ;
			localFileFolder = new File(moddir);
			if (!localFileFolder.mkdirs()) {
				throw new IOException("Unable to create directory: " + localFileFolder);
			}
			integrationProjectFile = integrationProjectSaver.save(server, domeConnection,integrationModel, localFileFolder);

			logger.debug("\nSaved model to '{}'", integrationProjectFile);
			// deploy
			DomeProject savedProject;
			DomeFolderEntity folderEntity = integrationModel.getFolder();
			DomeFolder targetFolder = getEnclosingFolder(domeConnection, folderEntity);
			logger.debug("\n\tDeploying to server in {}" + "\n\tPublic/Projects: {}" + "\n\tPublic/Models: {}" + "\n\tPublic/Folders: {}", new Object[] {
					targetFolder.getFolderName(), targetFolder.getProjects().toString(), targetFolder.getModels().toString(),
					targetFolder.getFolders().toString() });
			DeployServerConnection deployConnection = new DeployServerConnection(server.getUser(), server.getPw(), DeployServerConnection.USER,
					server.getName() + ":" + server.getPort());
			deployProject(deployConnection, targetFolder.getFolderId(), integrationProjectFile);
			savedProject = targetFolder.getProjectByName(integrationModel.getName());
			if (savedProject == null) {
				logger.error("Project not found.");
			} else {
				logger.debug("\nFound project: {}", savedProject);
			}
			DomeInterface projectInterface = savedProject.getInterfaceByName(integrationModel.getInterface().getName());
			DomeProjectEntity projectEntity = new DomeProjectEntity(folderEntity.getPath(), savedProject);
			DomeInterfaceEntity newInterface = new DomeInterfaceEntity(projectEntity, projectInterface);
			interfaceWithParameters = getParams(newInterface);
		} finally {
			if (domeConnection!=null) {
				domeConnection.close();
			}
			if (interfaceWithParameters!=null) {
				FileUtils.deleteQuietly(localFileFolder);
			} else {
				logger.warn("Project data left in folder: {}", localFileFolder );
			}
		}

		return interfaceWithParameters;
	}

	/**
	 * Taken from test.deploy.SimpleProjectDeploy
	 */
	private static void deployProject(DeployServerConnection deployConnection, Integer folderId, File localFile) {
		String localFileName = localFile.getAbsolutePath();
		DeployProjectData project = new DeployProjectData(localFileName);

		ServerConnection svrConn = deployConnection.getServerConnection();
		project.setContentVisibilityPermissions(getAllAccessPermissions(deployConnection, PermissionUtils.PROJECT_VISIBILITY_PRIVILEGES));
		Vector iModelEditPriveleges = getAllAccessPermissions(deployConnection, PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES);
		project.setEditPermissions(iModelEditPriveleges);
		
		@SuppressWarnings("unchecked")
		List<DeployProjectInterfaceData> projectInterfaces = project.getInterfaces();
		Vector interfaceUsePermissions = getAllAccessPermissions(deployConnection, PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES);
		for (DeployProjectInterfaceData deployInterfaceData : projectInterfaces) {
			deployInterfaceData.setPermissions(interfaceUsePermissions);
			deployInterfaceData.setIsAvailable(true);
		}
		
		@SuppressWarnings("unchecked")
		List<DeployModelData> iModels = project.getIntegrationModels();
		for (DeployModelData iModel : iModels) {
			iModel.setPermissions(iModelEditPriveleges);
			@SuppressWarnings("unchecked")
			List<DeployInterfaceData> iModelInterfaces = iModel.getModelInterfaces();
			for (DeployInterfaceData interFace : iModelInterfaces) {
				interFace.setPermissions(interfaceUsePermissions);
				interFace.setIsAvailable(true);
			}
		}
		
		@SuppressWarnings("rawtypes")
		Vector[] preparedData = DeployUtilities.prepareDeployProjectData(folderId, project);

		@SuppressWarnings("rawtypes")
		Vector versionInfo = DeployFilesFunctions.deployNewProject(svrConn, preparedData[0], preparedData[1], preparedData[2]);
		
		logger.debug("\nversionInfo: {}", versionInfo);
		DeployUtilities.writeProjectVersionFile(localFileName, versionInfo, svrConn);
	}

    /**
     * Returns a Vector with user information for the user logged into the deploy connection,
     * and with all permissions enabled for the selected permission category.
     * from {@link mit.cadlab.dome3.gui.deploy.deployTool.FastDeployAnalysisTool}
	 * @param deployConnection A DeployServerConnection object to use in getting the user information
	 * @param permissionCategory A constant from {@link PermissionUtils}
	 * @return data structure containing users and which permissions they have set (userId,permissionId)
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector getAllAccessPermissions(DeployServerConnection deployConnection, String permissionCategory) {
		Vector permissionsVector = new Vector();
		ServerConnection _svrConnection = deployConnection.getServerConnection();
		Vector analysisToolEditPrivileges = PermissionUtils.getCategoryInfo(_svrConnection, permissionCategory);
		Vector allUserGroup = UserGroupFunctions.getSimpleActiveUsersAndGroupsList(_svrConnection);
		DArrayList remainingUsersAndGroups = new DArrayList(PermissionUtils.convertToUserGroupInfo((Vector) analysisToolEditPrivileges.get(0), allUserGroup));
		String userName = _svrConnection.getLoginName();
		UserGroupInfo userInfo = null;
		for (int i = 0; i < remainingUsersAndGroups.size(); i++) {
			UserGroupInfo tempInfo = (UserGroupInfo) remainingUsersAndGroups.get(i);
			if (tempInfo.getName().equals(userName)) {
				userInfo = tempInfo;
				break;
			}
		}
		if (userInfo != null) {
			remainingUsersAndGroups.remove(userInfo);
			boolean[] oldPermissions = userInfo.getPermissions();
			for (int j = 0; j < oldPermissions.length; j++) {
				oldPermissions[j] = true;
			}
		}
		for (int j = 0; j < userInfo.getPermissionID().length; j++) {
			if (userInfo.getPermissions()[j] == true) {
				Vector temp = new Vector();
				temp.addElement(new Integer(userInfo.getId()));
				temp.addElement(new Integer(userInfo.getPermissionID()[j]));
				permissionsVector.addElement(temp);
			}
		}
		logger.debug("\n\tAccessPermissions for {}: {}", permissionCategory, permissionsVector);

		return permissionsVector;

	}
	
	/**
	 * Helper method for converting a list of RumtimeParameters to an array of
	 * ModelParams
	 * 
	 * @param params
	 * @return
	 */
	private ModelParam[] convertToModelParams(List<RuntimeParameter> params) {
		List<ModelParam> modelParams = new ArrayList<ModelParam>();
		if (params != null) {
			for (RuntimeParameter p : params) {
				if (p != null) {
					modelParams.add(new ModelParam(this, p));
				}
			}
		}
		return modelParams.toArray(new ModelParam[modelParams.size()]);
	}

	/**
	 * Implement saving a File parameter to server's disk.
	 * @param p
	 * @return String with the path to the file saved 'locally'
	 */
	public String writeFile(RuntimeParameter p) {
		String uniquePath = resultsDir + System.currentTimeMillis();
		try {
			File dir = new File(webAppDir, uniquePath);
			logger.debug("writing to {}", dir);
			dir.mkdirs();
			File file = new File(dir, p.getFileName());
			FileWriter out = new FileWriter(file);
			Object o = p.getFileValue();
			if (o != null) {
				String ostr = new String((byte[]) o);
				out.write(ostr);
			}
			out.close();
			return uniquePath + File.separator + p.getFileName();
		} catch (IOException e) {
			throw new IllegalStateException("Unable to write file to " + uniquePath, e);
		}
	}

	/**
	 * Run a model on the DOME server
	 * @param model
	 * @param tracker
	 * @param queue
	 * @param field2FileItems
	 * @return the input IModel object with the results
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IModel runModel(final IModel model, SolverStateTracker tracker, final String queue, final Map<String, String> field2FileItems) throws IOException {
		if (model == null) {
			throw new IllegalArgumentException("Model is missing");
		}
		DomeInterfaceEntity intface = model.getInterface();
		if (intface == null) {
			throw new IllegalArgumentException("Interface is missing");
		}
		if (server == null) {
			throw new IllegalArgumentException("Model Def Server is missing");
		}
		
		//Prepare for the redirection of stdout and stderr streams
		m_RedirectStdOutStdErr = new RedirectStdOutStdErr();
		m_RedirectStdOutStdErr.startRedirect();
		// RuntimeInterface run = null;
		domeMsgCount = 0;
		
		try {
			m_connection = server.openConnection();

			DomeInterface i = getDomeInterface(m_connection, intface);
			
			final RuntimeInterface run = i.createRuntimeInterface();
			
			for (RuntimeParameter runtimeParameter : (List<RuntimeParameter>) run.getIndependentParameters()) {
				logger.debug("{}: {} id: {}", new Object[] { runtimeParameter.getParamName(), runtimeParameter.getDataType(), runtimeParameter.getParamId() });
				
				ModelParam modelParam = model.getInParam(runtimeParameter.getParamName());
				
				if (modelParam != null && modelParam.getValue() != null) {
					if (ModelParam.VECTOR_TYPE.equals(runtimeParameter.getDataType())) {
						Object op = modelParam.getValue();
						//runtimeParameter.setVectorValue();
						run.addChangedParameter(runtimeParameter, stripOutLazyNumbers((Vector)op));
						logger.debug("setting vector value {}", op.toString());
					} else if (ModelParam.MATRIX_TYPE.equals(runtimeParameter.getDataType())) {
						// TODO: test this!!
						logger.debug("setting matrix value {}", modelParam.getValue());
						runtimeParameter.setMatrixValue(stripOutLazyNumbers((Vector)modelParam.getValue()));
					} else if (ModelParam.FILE_TYPE.equals(runtimeParameter.getDataType())) {
						// in this case, modelParam.getValue() has the field name of the FileItem which contains the file uploaded
						String paramKey = new File( (String)modelParam.getValue()).getName();
						String fileItem = field2FileItems.get(paramKey.replace(" ", "_"));
						String originalFilename = (String) modelParam.getValue();
						logger.debug("setting file value for parameter '{}' from file '{}'", paramKey, fileItem);
						logger.debug("original filename: '{}'", originalFilename);
						InterfaceParameterClient ipc = runtimeParameter.getInterfaceParameterClient();
						FileData domeFile = (FileData) ipc.getDataObjectForType("File");
						domeFile.setFilePath(fileItem);
						domeFile.setActualFilename(originalFilename);
						byte[] newValue = null;
			            try {
			                newValue = mit.cadlab.dome3.util.FileUtils.readBinaryFileAsByteArray(fileItem);  //TODO: might consume too much memory, look at readBinaryFileAsByteArray1 version
			            } catch (Exception e) {
			            	logger.debug("Fail to read bytes from new file path: {}", fileItem);
			            }
						//MAK: run.addChangedParameter(runtimeParameter, newValue);
			            //FileTransport fileTransportObj = new FileTransport(fileItem, newValue);
			            FileTransport fileTransportObj = new FileTransport(originalFilename, newValue);
			            run.addChangedParameter(runtimeParameter, fileTransportObj);
						//run.addChangedParameter(runtimeParameter, domeFile);
						//runtimeParameter.getFileValue();
						//runtimeParameter.setValue(new FileData(fileItem));
					} else if (ModelParam.ENUM_TYPE.equals(runtimeParameter.getDataType())) {
						// in this case, modelParam.getValue() has the field name of the FileItem which contains the file uploaded
						runtimeParameter.setEnumerationValue(((com.google.gson.internal.LazilyParsedNumber) ((Vector) modelParam.getValue()).get(0)).intValue());
					} else {
						// real, integer, enumeration, boolean, string
						logger.debug("setting value {}", modelParam.getValue());
						// the innards of the RuntimeParameter.setValueAuto method expects a String
						// so need to convert it to a String in order to prevent ClassCastExceptions
						runtimeParameter.setValueAuto(modelParam.getValue() == null ? null : modelParam.getValue().toString());
					}
					logger.debug("Runtime Param: {}", runtimeParameter.toString());
				}
			}
			
			for (RuntimeParameter resultParameter : (List<RuntimeParameter>) run.getResultParameters()) {
				if ("File".equalsIgnoreCase(resultParameter.getDataType())) {
					InterfaceParameterClient ipc = resultParameter.getInterfaceParameterClient();
					DomeFile domeFile = (DomeFile) ipc.getDataObjectForType("File");
					String newFilePath = model.getOutParam(resultParameter.getParamName()).getValue().toString();
					domeFile.setFilePath(newFilePath);
				}
			}
//			File resultsPath = new File(webAppDir,resultsDir);
//			logger.debug("\nSending files to: {}",resultsPath.getAbsolutePath());
//			run.setDownloadFolder(resultsPath.getAbsolutePath());
			
			logger.debug("before submit");
			run.setExecutionTimeLimit(timeout);
			
			/*
			 * Create listeners to report back to the queue
			 */
			this.modelStatusListener = new ParameterStatusChangeListener() {
				// / ParameterStatusChangeEvent
				@Override
				public void statusChanged(ParameterStatusChangeEvent event) {
					synchronized(this) {
						domeMsgCount++;
					}
					Gson gson = new GsonBuilder().create();
					String qMsg = gson.toJson(new ParameterChangeProxy(event, domeMsgCount));
					m_Queue.sendMessage(qMsg, queue);
					
					logger.debug("modelStatusListener: " + ((ParameterStatusChangeEvent) event).toString());
					if (event.getNewValue().toString() == "SUCCESS") {
						logger.debug("Success: " + qMsg);
						// TODO: End thread and send success to queue
						// run.clearValueAndStatusChangeListener();
						run.close();
						try {
							closeConnections();
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			
			this.modelValueListener = new ParameterValueChangeListener() {
				// ParameterValueChangeEvent == event
				@Override
				public void valueChanged(ParameterValueChangeEvent event) {
					synchronized(this) {
						domeMsgCount++;
					}
					logger.debug("modelValueListener: " + event.toString());
					Gson gson = new GsonBuilder().create();
					
					m_Queue.sendMessage(gson.toJson(new ParameterChangeProxy(event, domeMsgCount)), queue);
				}
			};

			run.addParameterStatusChangeListener(modelStatusListener);
			run.addParameterValueChangeListener(modelValueListener);
			
			List<RuntimeParameter> outputs = run.getResultParameters();
			// note, we aren't exactly supporting intermediate parameters YET that
			// I know of, but just in case this should all just work!
			outputs.addAll(run.getIntermediateParameters());
			logger.debug("\nDomeProxy.runModel() -Run result parameters: {}", outputs.size());

			model.setOutParams(convertToModelParams(outputs));
			
			// Do it!
			tracker = run.submitAndGo();

			logger.debug("after submit");
			
			//Wait till the SolverstateTracker finishes 
			// However isOverTimeLimit in SolverstateTracker is never set to true, so 
			// added the bFlag to last for the timeout period.
		    long startTime = System.currentTimeMillis();
		    boolean bFlag = false;  
		    //waiting for termination of tracker will not happen 
			while ( tracker.status() != tracker.SUCCESS && !bFlag)  {
				Thread.sleep(500);
	            if ((System.currentTimeMillis() - startTime) > timeout) {
	            	bFlag = true;
                }				
			}
			m_RedirectStdOutStdErr.terminateRedirectThread();

		} catch (Exception ex) {

			domeMsgCount++;
			String qMsg = "{\"event\":\"class mit.cadlab.dome3.api.ParameterStatusChangeEvent\",\"param\":" + domeMsgCount + 
					",\"id\":{\"idString\":\"" + ex.toString() + "\"},\"old_val\":\"RUNNING\",\"new_val\":\"FAILURE\",\"occur\":" + 
					Calendar.getInstance().getTimeInMillis() + "}";
			m_Queue.sendMessage(qMsg, queue);
			logger.debug("DomeProxy.runModel() Exception: " + qMsg);
			if (m_RedirectStdOutStdErr.isRedirectThreadRunning()){
				m_RedirectStdOutStdErr.terminateRedirectThread();
				
			}
		} finally {
			m_RedirectStdOutStdErr.resetRedirectedStreams(); 
			m_strAccumulatedMessages = m_RedirectStdOutStdErr.getAccumulatedMessages();

			
			// if (run != null) {
			// run.close();
			// }
			if (m_connection != null) {
				try {
					//TODO: shouldn't we be closing this? - Yes, see closeConnections()
					// connection.close();
				} catch (Exception e) {
					logger.error("DomeProxy.runModel(): Problem closing DOME server connection:", e);
				}
			}
		}
		return model;
	}

	private Vector stripOutLazyNumbers(Vector op) {
		Vector ret = new Vector();
		for (Object val : op)
		{
			if (val instanceof com.google.gson.internal.LazilyParsedNumber)
				val = ((com.google.gson.internal.LazilyParsedNumber)val).doubleValue();
			ret.add(val);
		}
		return ret;
	}

	private static DomeFolder getTopFolder(DomeConnection connection) {
		DomeFolder folder = connection.getMyPublicFolder();

		if (folder == null) {
			throw new RuntimeException(
					"User Public Folder does not exist! CEED Services requires a public folder for the user logged in. The user public folder is not the same as the server's public folder.");
			// folder = connection.getPublicFolder();
		}
		return folder;
	}

	/*
	 * Methods for getting DOME API objects directly 
	 */
	DomeModel getDomeModel() {
		DomeModel model = null;
		return model;
	}
	
	public void closeConnections() throws JMSException {
		m_Queue.closeConnection();
		m_connection.close();
	}

    /**
     * Reset standard output stream and error output stream
     * 
     */
    public StringBuffer getAccumulatedMessages() {
       return m_strAccumulatedMessages;
    }
	
	
}
