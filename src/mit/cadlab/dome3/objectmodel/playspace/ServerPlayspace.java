package mit.cadlab.dome3.objectmodel.playspace;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.ModelInterfaceListener;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.functions.*;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfoRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.PluginUtils;
import mit.cadlab.dome3.tool.AnalysisToolUtils;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.DomePropertyChangeEvent;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 28, 2003
 * Time: 1:23:54 PM
 * To change this template use Options | File Templates.
 */
public class ServerPlayspace extends AbstractPlayspace {
    public static final String PROPERTY_CLOSED = "PlayspaceClosed";

    private HashMap userStatusMap = new HashMap();  // maps user ids to connections
    ModelInterfaceListener interfaceListener = new ModelInterfaceListener();

    // flag indicating that the project has been loaded for the first time
    // and should be made consistent when a client interface is opened
    boolean makeConsistent = true;
    private ModelStatusListener modelStatusListener = new ModelStatusListener();
	private ModelKilledListener modelKilledListener = new ModelKilledListener();
	private Integer activePlayspaceModels = new Integer(0);

    public String tempSessionId;  //stores instance session Id  _i

	protected HashMap resources = new HashMap(); // key=runtime id; value=object
	// does not handle embedded analysis tools

    // use same one for all models/projects to avoid duplicate notifications

    /**
     * start a transient playspace.
     */
    public ServerPlayspace() {
        runtimeId.setPlayspaceRuntimeId(UUIDGenerator.create());
        name = "transient";
    }


    /**
     * start a transient playspace with a given runtime id.
     */
    public ServerPlayspace(String runtimeId) {
        this.runtimeId.setPlayspaceRuntimeId(runtimeId);
        name = "transient";
    }


    /**
     * Start a distributed playspace. A distributed playspace is one that has a definition
     * but the definition is deployed on a different server than the one where
     * this playspace is being created. This would happen when
     * the playspace objects are not located on the same server as the playspace.
     * For example, Playspace A is created on Server A. The client creates Interface
     * B which is located on Server B. Server B starts a distributed playspace with the
     * same runtime id as Playspace A.
     * @param playspaceCompoundId
     */
    public ServerPlayspace(CompoundId playspaceCompoundId) {
        runtimeId = new CompoundId();

        runtimeId.setPlayspaceStaticId(playspaceCompoundId.getPlayspaceStaticId());

        if (playspaceCompoundId.getPlayspaceRuntimeId() != null)
            runtimeId.setPlayspaceRuntimeId(playspaceCompoundId.getPlayspaceRuntimeId());
        else {
            String playspaceRuntimeId = UUIDGenerator.create();
            runtimeId.setPlayspaceRuntimeId(playspaceRuntimeId);
            playspaceCompoundId.setPlayspaceRuntimeId(playspaceRuntimeId);
        }
        name = "transient";
    }

    /**
     * Start a predefined playspace.
     * @param xmlElement Playspace XML description
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public ServerPlayspace(Element xmlElement, String staticId)
            throws XmlRpcException {
        super(xmlElement);
        runtimeId.setPlayspaceRuntimeId(UUIDGenerator.create());
	    runtimeId.setPlayspaceStaticId(staticId);

        // load project xml descriptions
        List projectElements = xmlElement.selectNodes("projects/" + IntegrationProject.XML_TAG);
        for (Iterator iter = projectElements.iterator(); iter.hasNext();) {
            Element projectElement = (Element) iter.next();
            addStaticObjectInfo(projectElement, projectInfo);
        }

        // load model xml descriptions
        List modelElements = xmlElement.selectNodes("models/" + DomeModelRuntime.XML_TAG);
        for (Iterator iter = modelElements.iterator(); iter.hasNext();) {
            Element modelElement = (Element) iter.next();
            addStaticObjectInfo(modelElement, modelInfo);
        }

// load tool ids
        List toolElements = xmlElement.selectNodes("tools/" + OptimizationToolRuntime.XML_TAG);
        for (Iterator iter = toolElements.iterator(); iter.hasNext();) {
            Element toolElement = (Element) iter.next();
            addStaticObjectInfo(toolElement, analysisToolInfo);
        }
    }


    /**
     * Get a list of active members.
     * @return Member list
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public Vector getMembers()
            throws XmlRpcException {
        Vector memberList = new Vector();

        synchronized (userStatusMap) {
            for (Iterator iter = userStatusMap.keySet().iterator(); iter.hasNext();) {
                String connectionId = (String) iter.next();

                Vector v = UserGroupDbFunctions.getDetailedSpecificUser(connectionId);
                Vector userRecord = null;
                if (v.size() > 0) {
                    userRecord = new Vector(2);
                    Vector userData = (Vector) v.get(0);
                    userRecord.add(userData.get(1));  //adding "name" info
                    userRecord.add(userData.get(3));  //adding "status" info
                }
                if (userRecord != null) {
                    memberList.add(userRecord);
                }
            }
        }

        return memberList;
    }


    /**
     * Get file content
     * @return file content
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public Object getFileContent(CompoundId parameterId)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer)getInterface(parameterId); // find interface once per hashtable
        String paramIdString = parameterId.getDataObjectStaticId();
        Parameter ifaceParam = (Parameter) iface.getModelObjectById(new Id(paramIdString));

        ConnectionMappingManagerRuntime cmgr = null;
        Object modelObj = iface.getModel();
        if (modelObj instanceof IntegrationProjectServerRuntime) {
            IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) modelObj;
            cmgr = (ConnectionMappingManagerRuntime) project.getMappingManager();
        } else {
            ModelRuntime model = (ModelRuntime) modelObj;
            cmgr = (ConnectionMappingManagerRuntime) model.getMappingManager();
        }
        Collection mappings = cmgr.getMappingsForParameter(ifaceParam);
        Parameter modelParam = (Parameter) mappings.iterator().next();  //interface param is mapped to only one model param
        List contentList = modelParam.getCurrentDataObject().getValues();
        return contentList.get(0);
    }

    /**
     * Load a model/project/tool description from the database and create it.
     * @param modelId model id (may include desired runtime id)
     * @return model object instance
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    private Model loadModel(CompoundId modelId, String modelTable, boolean isProjectResource)
            throws XmlRpcException {
	    // get model static id
	    String modelStaticId = modelId.getModelStaticId();
	    if (modelStaticId == null)
		    modelStaticId = modelId.getFirstProjectStaticId();
	    if (modelStaticId == null)
		    throw new RuntimeException("ServerPlayspace.loadModel - unable to get model static id: " + modelId);

        Model parentObject = null;
        String type = null;

        // get xml content from the database
        String xmlContent = null;
        String[] staticInfo = null;
        if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_MODEL)) {
	        try {
		        xmlContent = FileSystemDbFunctions.getModelDescription(modelStaticId);
		        staticInfo = FileSystemDbFunctions.getModelStaticInfo(modelStaticId);
		        type = staticInfo[3];
	        }
	        catch (XmlRpcException e) {
				throw e;
	        }
        }
	    if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_PROJECT)) {
		    try {
			    xmlContent = FileSystemDbFunctions.getProjectDescription(modelStaticId);
		    }
		    catch (XmlRpcException e) {
				throw e;
		    }
	    }
	    if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_ANALYSIS_TOOL)) {
		    try {
			    xmlContent = FileSystemDbFunctions.getAnalysisToolDescription(modelStaticId);
			    staticInfo = FileSystemDbFunctions.getAnalysisToolStaticInfo(modelStaticId);
			    type = staticInfo[3];
		    }
		    catch (XmlRpcException e) {
				throw e;
		    }
        }

        //find imodel xml description when loading as nested subscription _i
        if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_IMODEL)) {
		    try {
			    xmlContent = FileSystemDbFunctions.getIntegrationModelDescription(modelStaticId);
		        staticInfo = FileSystemDbFunctions.getIntegrationModelStaticInfo(modelStaticId);
		    }
		    catch (XmlRpcException e) {
				throw e;
		    }
	    }

        if (xmlContent==null || xmlContent.equals(""))
            throw new XmlRpcException(DbErrors.XMLRPC_NO_XML_DESCRIPTION,
                    DbErrors.XMLRPC_NO_XML_DESCRIPTION_MSG);
 	    // create xml element and model/project/tool instance
        Element modelElement = XMLUtils.stringToXmlElement(xmlContent);
        if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_MODEL)) {
            String modelRuntimeId = null;
            if (type.equals(DbConstants.MODEL_TYPE_DOME)) { // dome model
                parentObject = new DomeModelRuntime(modelId, modelElement, isProjectResource);
                DomeModelRuntime model = (DomeModelRuntime) parentObject;
	            Debug.trace(Debug.ALL, "completed creating model: " + model.getName());
                modelRuntimeId = model.getRuntimeId().getModelRuntimeId();
            } else { // plugin model
                try {
                    parentObject = PluginUtils.getPluginRuntime(type, modelId, modelElement, isProjectResource);
	                PluginModelRuntime model = (PluginModelRuntime) parentObject;
	                Debug.trace(Debug.ALL, "completed creating plugin: " + model.getName());
	                modelRuntimeId = model.getRuntimeId().getModelRuntimeId();
                } catch (UnsatisfiedLinkError e) {
                    throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
                            e.getMessage());
                }
            }
            if (parentObject == null)
                throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
                        DbErrors.XMLRPC_MODEL_INVOCATION_MSG);

            ((ModelRuntime) parentObject).addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener);
	        ((ModelRuntime) parentObject).addPropertyChangeListener(ModelRuntime.MODEL_KILLED, modelKilledListener);
	        if (isProjectResource)
	            synchronized(resources) {
		            resources.put(modelRuntimeId, parentObject);
	            }
	        else
	           synchronized (modelInfo) {
                    modelInfo.addObject(modelStaticId, modelRuntimeId, parentObject);
                }
	        incrementActivePlayspaceModels();
        } else if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_ANALYSIS_TOOL)) {
            String analysisToolRuntimeId = null;
            try {
                parentObject = AnalysisToolUtils.getAnalysisToolRuntime(type, modelId, modelElement);
	            OptimizationToolRuntime analysisTool = (OptimizationToolRuntime) parentObject;
	            Debug.trace(Debug.ALL, "completed creating analysis tool: " + analysisTool.getName());
	            analysisToolRuntimeId = analysisTool.getRuntimeId().getModelRuntimeId();
            } catch (UnsatisfiedLinkError e) {
                throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR, e.getMessage());
            }
            if (parentObject == null)
                throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
                        DbErrors.XMLRPC_MODEL_INVOCATION_MSG);

            ((ModelRuntime) parentObject).addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener);
	        ((ModelRuntime) parentObject).addPropertyChangeListener(ModelRuntime.MODEL_KILLED, modelKilledListener);
	        if (isProjectResource) // never the case
		        synchronized (resources) {
			        resources.put(analysisToolRuntimeId, parentObject);
		        }
	        else
		        synchronized (analysisToolInfo) {
			        analysisToolInfo.addObject(modelStaticId, analysisToolRuntimeId, parentObject);
		        }
	        incrementActivePlayspaceModels();
        } else if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_PROJECT)) {
	        IntegrationProjectServerRuntime project = null;
	        String projectRuntimeId = null;
            try {
                parentObject = new IntegrationProjectServerRuntime(modelId, runtimeId, modelElement, isProjectResource);
	            project = (IntegrationProjectServerRuntime) parentObject;
	            Debug.trace(Debug.ALL, "completed creating project: " + project.getName());
	            projectRuntimeId = project.getRuntimeId().getFirstProjectRuntimeId();
            } catch (Exception e) {
                throw new RuntimeException("Error loading project: "+e, e);
            }
	        if (parentObject == null)
		        throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
		                                  DbErrors.XMLRPC_MODEL_INVOCATION_MSG);

            project.addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener);
	        project.addPropertyChangeListener(ModelRuntime.MODEL_KILLED, modelKilledListener);
	        if (isProjectResource)
		        synchronized (resources) {
			        resources.put(projectRuntimeId, parentObject);
		        }
	        else
		        synchronized (projectInfo) {
			        projectInfo.addObject(modelStaticId, projectRuntimeId, project);
		        }
	        incrementActivePlayspaceModels();

	        // also register imodels of projects
	        Iterator imodels = project.getIntegrationModels().iterator();
	        ProjectIntegrationModelInfoRuntime pimir;
	        DomeModelRuntime imodel;
	        while (imodels.hasNext()) {
		        pimir = (ProjectIntegrationModelInfoRuntime) imodels.next();
		        imodel = (DomeModelRuntime)pimir.getModel();
		        imodel.addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener);
		        imodel.addPropertyChangeListener(ModelRuntime.MODEL_KILLED, modelKilledListener);
		        synchronized (resources) {
			        resources.put(imodel.getRuntimeId().getModelRuntimeId(), imodel);
		        }
		        incrementActivePlayspaceModels();
	        }
        }

        //create imodel instance for nested subscription _i
        else if (modelTable.equals(DbConstants.IFACE_PARENT_TYPE_IMODEL)) {

            // note: here i try to open the imodel using the methodology for dome models described above
            // however it is an i-model - must check with imodel creation
            // at i-projectRuntime to make sure everything is done right
            // todo load i-model subscription info!



            String modelRuntimeId = null; //id
            parentObject = new DomeModelRuntime(modelId, modelElement, true); //create instance
            DomeModelRuntime model = (DomeModelRuntime) parentObject;
	        Debug.trace(Debug.ALL, "completed creating model: " + model.getName());
            modelRuntimeId = model.getRuntimeId().getModelRuntimeId(); //id

            if (parentObject == null)      //check
                throw new XmlRpcException(DbErrors.XMLRPC_MODEL_INVOCATION_ERROR,
                        DbErrors.XMLRPC_MODEL_INVOCATION_MSG);

            ((ModelRuntime) parentObject).addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener); //listeners
	        ((ModelRuntime) parentObject).addPropertyChangeListener(ModelRuntime.MODEL_KILLED, modelKilledListener);

            if (isProjectResource) //add to resource map - should always be the case
	            synchronized(resources) {
		            resources.put(modelRuntimeId, parentObject);
	            }
	        else                    // should never be the case - it won't exist independently
	           synchronized (modelInfo) {
                    modelInfo.addObject(modelStaticId, modelRuntimeId, parentObject);
                }
	        incrementActivePlayspaceModels();
        }

        return parentObject;
    }

	/**
	 * Also sets interface parent static id if it does not exist
	 * @param ifaceId
	 * @return which table the interface parent belongs to
	 */
	private String getInterfaceParentType(CompoundId ifaceId) {
		String ifaceStaticId = ifaceId.getInterfaceStaticId();
		if (ifaceStaticId != null) {
			String getModelIdQuery = "select PARENT_ID,PARENT_TABLE from INTERFACES where ID='"
			        + ifaceStaticId + "'";
			try {
				Statement stmt = DbUtils.getStatement();
				ResultSet r;

				// get model id
				r = stmt.executeQuery(getModelIdQuery);
				if (!r.next())
					throw new XmlRpcException(DbErrors.XMLRPC_NO_INTERFACE_PARENT,
					                          DbErrors.XMLRPC_NO_INTERFACE_PARENT_MSG);
				else {
					String ifaceParentStaticId = r.getString("PARENT_ID");
					String parentTable = r.getString("PARENT_TABLE");
					if (parentTable.equals(DbConstants.IFACE_PARENT_TYPE_PROJECT)) {
						if (ifaceId.getFirstProjectStaticId() == null)
							ifaceId.addProjectStaticId(ifaceParentStaticId);
					} else { // assume model/optimization model type
						if (ifaceId.getModelStaticId() == null)
							ifaceId.setModelStaticId(ifaceParentStaticId);
					}
					return parentTable;
				}
			}
			catch (Exception e) {
				System.err.println("getInterfaceParentType failed: " + e.getMessage());
			}
		} else {
			System.err.println("getInterfaceParentType failed - no interface static id: " + ifaceId);
		}
		return null;
	}

    private OptimizationToolRuntime loadAnalysisTool(CompoundId analysisToolId) throws XmlRpcException {
        OptimizationToolRuntime analysisTool = null;

        String xmlContent = null;
        String analysisToolStaticId = analysisToolId.getModelStaticId();
        try {
            xmlContent = DeployFilesDbFunctions.getMostRecentAnalysisToolXmlDefinition(analysisToolStaticId);
        } catch (XmlRpcException e) {
            analysisToolId.resetStaticInfo();
            return null;
        }

        Element analysisToolElement = XMLUtils.stringToXmlElement(xmlContent);
        analysisTool = new OptimizationToolRuntime(analysisToolId, analysisToolElement);
        if (analysisTool == null)
            throw new XmlRpcException(DbErrors.XMLRPC_ANALYSIS_TOOL_INVOCATION_ERROR,
                    DbErrors.XMLRPC_ANALYSIS_TOOL_INVOCATION_MSG);

        // get the analysis tool static info
        String description = null;
        String[] staticInfo = FileSystemDbFunctions.getAnalysisToolStaticInfo(analysisToolStaticId);
        if (staticInfo != null) {
            description = staticInfo[2];
        }

        synchronized (analysisToolInfo) {
            String analysisToolRuntimeId = analysisTool.getRuntimeId().getModelRuntimeId();
            analysisToolInfo.addObject(analysisToolStaticId, analysisToolRuntimeId, analysisTool);
            analysisToolInfo.addStaticInfo(analysisToolStaticId, analysisTool.getName(), description);
            analysisToolId.setModelRuntimeId(analysisToolRuntimeId);
	        incrementActivePlayspaceModels();
        }

        analysisTool.addPropertyChangeListener(ModelRuntime.RUN_STATUS, modelStatusListener);
	    analysisTool.addPropertyChangeListener(ModelRuntime.MODEL_KILLED, modelKilledListener);
        return analysisTool;
    }

    public IntegrationProjectServerRuntime getProject(CompoundId projectId, boolean loadProject)
            throws XmlRpcException {
	    Object proj = getModel(projectId);
	    if (proj == null && loadProject) {
		    proj = loadModel(projectId, DbConstants.IFACE_PARENT_TYPE_PROJECT, false);
	    }
	    if (proj instanceof IntegrationProjectServerRuntime) {
		    if (projectId.getFirstProjectRuntimeId() == null)
		        projectId.addProjectRuntimeId(((IntegrationProjectServerRuntime) proj).getRuntimeId().getFirstProjectRuntimeId());
	        return (IntegrationProjectServerRuntime)proj;
	    }
	    return null;
    }

    public OptimizationToolRuntime getAnalysisTool(CompoundId analysisToolId, boolean loadAnalysisTool)
            throws XmlRpcException {
        // retrieve the analysis tool
        OptimizationToolRuntime analysisTool = null;

        // try to retrieve an existing analysis tool
        String analysisToolRuntimeId = analysisToolId.getModelRuntimeId();
        if (analysisToolRuntimeId != null) {
            synchronized (analysisToolInfo) {
                analysisTool = (OptimizationToolRuntime) analysisToolInfo.getObjectFromRuntimeId(analysisToolRuntimeId);
                if (analysisTool != null) {
                    makeConsistent = false;
                }
            }
        }

        if (analysisTool == null) {
            String analysisToolStaticId = analysisToolId.getModelStaticId();
            if (analysisToolStaticId != null) {
                synchronized (analysisToolInfo) {
                    analysisTool = (OptimizationToolRuntime) analysisToolInfo.getObjectFromStaticId(analysisToolStaticId);
                }
                // if the analysis tool exists, set its runtime id
                if (analysisTool != null) {
                    makeConsistent = false;
                    analysisToolId.setModelRuntimeId(analysisTool.getRuntimeId().getModelRuntimeId());
                }
            }
        }

        // load the the analysis tool from the database
        if (analysisTool == null && loadAnalysisTool) {
            analysisTool = loadAnalysisTool(analysisToolId);
        }

        return analysisTool;
    }

    public Vector getAnalysisToolProjectInfo(CompoundId analysisToolId) throws XmlRpcException {
        OptimizationToolRuntime analysisTool = getAnalysisTool(analysisToolId, false);
        Vector result = analysisTool.getProjectInfo();
        synchronized (projectInfo) {
            String projectStaticId = analysisTool.getProjectForClient().getRuntimeId().getFirstProjectStaticId();
            String projectDescription = null;
            String[] projectStaticInfo = FileSystemDbFunctions.getProjectStaticInfo(projectStaticId);
            if (projectStaticInfo != null)
                projectDescription = projectStaticInfo[2];
            String clientProjectRuntimeId = analysisTool.getProjectForClient().getRuntimeId().getFirstProjectRuntimeId();
            projectInfo.addObject(projectStaticId, clientProjectRuntimeId, analysisTool.getProjectForClient());
            projectInfo.addStaticInfo(projectStaticId, analysisTool.getProjectForClient().getName(), projectDescription);
        }
        return result;
    }

	/**
	 * Try to get interface info; create interface if necessary.
	 * @param sessionId this client is registered as client to interface
	 * @param interfaceId
	 * @return xml description of interface
	 * @throws XmlRpcException
	 */
    public String createAnalysisToolInterface(String sessionId, CompoundId interfaceId)
            throws XmlRpcException {
		return createInterface(sessionId, interfaceId, false);
    }


    /**
     * Get an existing interface from the interface cache or, if none, load an interface
     * description from the database and create an instance.
     * Attach value change listeners to all the model data objects to communicate changes
     * back to the client.
     * @param sessionId User session id (used by listeners)
     * @param interfaceId Interface static or runtime id
     * @return Interface xml description
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public String createInterface(String sessionId, CompoundId interfaceId, boolean isProjectResource)
            throws XmlRpcException {
	    Object genericIface = getInterface(interfaceId, !isProjectResource); // try to find the interface first
	    CompoundId ifaceId = null;
	    CompoundId ifaceParentId = null;
	    String ifaceParentRuntimeId = null;
	    if (genericIface == null) { // need to create the interface
		    String ifaceStaticId = interfaceId.getInterfaceStaticId(); // save since it is destroyed by project creation

		    // get the interface parent
		    Object ifaceParent = getModel(interfaceId);
		    if (ifaceParent != null) { // model already created
			    isProjectResource = false; // not a subscription interface request
		    }
		    if (ifaceParent == null && !isProjectResource)
			    ifaceParent = getModelByStaticId(interfaceId);
		    if (ifaceParent == null) { // need to load the interface parent
			    String ifaceParentType = getInterfaceParentType(interfaceId);
			    ifaceParent = loadModel(interfaceId, ifaceParentType, isProjectResource);
		    }
		    if (ifaceParent == null) {
			    throw new RuntimeException("ServerPlayspace.createInterface - unable to get interface parent: " + interfaceId);
		    }

		    if (interfaceId.getInterfaceStaticId() == null) // restore if destroyed
		        interfaceId.setInterfaceStaticId(ifaceStaticId);
		    String[] xmlContentMappings
		            = DeployFilesDbFunctions.getMostRecentInterfaceXmlDefinitionAndMappings(ifaceStaticId);
		    if (xmlContentMappings.length == 2) {
			    String xmlContent = xmlContentMappings[0];
			    String xmlMappings = xmlContentMappings[1];
			    try {
				    if (ifaceParent instanceof OptimizationToolRuntime) {
					    OptimizationToolRuntime tool = (OptimizationToolRuntime) ifaceParent;
					    ifaceParentId = tool.getRuntimeId();
					    ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
					    interfaceId.setModelStaticId(ifaceParentId.getModelStaticId()); // need to set static id before runtime id!
					    interfaceId.setModelRuntimeId(ifaceParentRuntimeId);
					    genericIface = tool.loadInterface(interfaceId, xmlContent, xmlMappings);
				    }
				    else if (ifaceParent instanceof IntegrationProjectServerRuntime) {
					    IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) ifaceParent;
					    ifaceParentId = project.getRuntimeId();
					    ifaceParentRuntimeId = ifaceParentId.getFirstProjectRuntimeId();
					    if (interfaceId.getFirstProjectStaticId() == null)
						    interfaceId.addProjectStaticId(ifaceParentId.getFirstProjectStaticId());
					    if (interfaceId.getFirstProjectRuntimeId() == null)
						    interfaceId.addProjectRuntimeId(ifaceParentRuntimeId);
					    genericIface = project.loadInterface(interfaceId, xmlContent, xmlMappings);
				    }
				    else if (ifaceParent instanceof PluginModelRuntime || ifaceParent instanceof DomeModelRuntime) {
					    ModelRuntime model = (ModelRuntime) ifaceParent;
					    ifaceParentId = model.getRuntimeId();
					    ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
					    interfaceId.setModelStaticId(ifaceParentId.getModelStaticId());
					    interfaceId.setModelRuntimeId(ifaceParentRuntimeId);
					    genericIface = ((DomeModelBase) ifaceParent).loadRuntimeInterface(interfaceId, xmlContent, xmlMappings);
				    }
				    else {
					    System.err.println("ServerPlayspace.createInterface - unsupported ifaceParent type: " + ClassUtils.getClassName(ifaceParent));
				    }
			    }
			    catch (Exception e) {
				    throw new RuntimeException("Error loading interface: " + e);
			    }
		    }
		    if (genericIface == null)
			    throw new RuntimeException("Interface not created");
	    }

	    String xml = null;
	    if (genericIface instanceof ModelInterfaceRuntimeServer) {
		    ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer)genericIface;
		    ifaceId = iface.getRuntimeId();
		    interfaceId.setInterfaceRuntimeId(ifaceId.getInterfaceRuntimeId());
		    synchronized (interfaceListener) {
			    interfaceListener.addListeners(sessionId, iface, interfaceId, isProjectResource);
		    }
		    DomeServer.addToResourceClientMap(iface.getRuntimeId().getInterfaceRuntimeId(), sessionId);
		    xml = iface.getLiveXmlDescription();
	    } else if (genericIface instanceof OptimizationToolInterfaceRuntimeServer) {
		    OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) genericIface;
		    ifaceId = iface.getRuntimeId();
		    interfaceId.setInterfaceRuntimeId(ifaceId.getInterfaceRuntimeId());
		    synchronized (interfaceListener) {
			    interfaceListener.addListeners(sessionId, iface, interfaceId);
		    }
		    DomeServer.addToResourceClientMap(iface.getRuntimeId().getInterfaceRuntimeId(), sessionId);
		    xml = iface.getLiveXmlDescription();
	    } else {
		    throw new RuntimeException("ServerPlayspace.createInterface - can not register unsupported interface type: " + ClassUtils.getClassName(genericIface));
	    }
	    if (isProjectResource)
		    DomeServer.registerSubscriptionInterface(ifaceParentId,
		                                             sessionId, ifaceId);
	    else
		    DomeServer.addToResourceClientMap(ifaceParentRuntimeId, sessionId);
	    return xml;
    }

	/**
	 * Get an existing interface from the interface cache or, if none, return the interface
	 * xml and load an interface description from the database in background thread.
	 * Attach value change listeners to all the model data objects to communicate changes
	 * back to the client.
	 * @param sessionId User session id (used by listeners)
	 * @param interfaceId Interface static or runtime id
	 * @return interfaceId, xml description, iface created on server (true/false)
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public Vector createInterfaceQuick(String sessionId, CompoundId interfaceId)
	        throws XmlRpcException
	{
		// todo: handle possible race conditions with multiple playspace clients
		Object genericIface = getInterface(interfaceId, true); // try to find the interface first
		CompoundId ifaceId = null;
		CompoundId ifaceParentId = null;
		String ifaceParentRuntimeId = null;
		String ifaceParentType = null;
		String xmlContent = null, xmlMappings = null;

		if (genericIface == null) { // need to create the interface
			String ifaceStaticId = interfaceId.getInterfaceStaticId();

			// get the interface parent
			Object ifaceParent = getModel(interfaceId);
			if (ifaceParent == null)
				ifaceParent = getModelByStaticId(interfaceId);
			if (ifaceParent == null) { // need to load the interface parent later
				ifaceParentType = getInterfaceParentType(interfaceId);
			}

			String[] xmlContentMappings
			        = DeployFilesDbFunctions.getMostRecentInterfaceXmlDefinitionAndMappings(ifaceStaticId);
			if (xmlContentMappings.length == 2) {
				xmlContent = xmlContentMappings[0];
				xmlMappings = xmlContentMappings[1];
				if (ifaceParent != null) {
					try {
						if (ifaceParent instanceof OptimizationToolRuntime) {
							OptimizationToolRuntime tool = (OptimizationToolRuntime) ifaceParent;
							ifaceParentId = tool.getRuntimeId();
							ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
							interfaceId.setModelStaticId(ifaceParentId.getModelStaticId()); // need to set static id before runtime id!
							interfaceId.setModelRuntimeId(ifaceParentRuntimeId);
							genericIface = tool.loadInterface(interfaceId, xmlContent, xmlMappings);
						}
						else if (ifaceParent instanceof IntegrationProjectServerRuntime) {
							IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) ifaceParent;
							ifaceParentId = project.getRuntimeId();
							ifaceParentRuntimeId = ifaceParentId.getFirstProjectRuntimeId();
							if (interfaceId.getFirstProjectStaticId() == null)
								interfaceId.addProjectStaticId(ifaceParentId.getFirstProjectStaticId());
							if (interfaceId.getFirstProjectRuntimeId() == null)
								interfaceId.addProjectRuntimeId(ifaceParentRuntimeId);
							genericIface = project.loadInterface(interfaceId, xmlContent, xmlMappings);
						}
						else if (ifaceParent instanceof PluginModelRuntime || ifaceParent instanceof DomeModelRuntime) {
							ModelRuntime model = (ModelRuntime) ifaceParent;
							ifaceParentId = model.getRuntimeId();
							ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
							interfaceId.setModelStaticId(ifaceParentId.getModelStaticId());
							interfaceId.setModelRuntimeId(ifaceParentRuntimeId);
							genericIface = ((DomeModelBase) ifaceParent).loadRuntimeInterface(interfaceId, xmlContent, xmlMappings);
						}
						else {
							System.err.println("ServerPlayspace.createInterface - unsupported ifaceParent type: " + ClassUtils.getClassName(ifaceParent));
						}
					}
					catch (Exception e) {
						throw new RuntimeException("Error loading interface: " + e);
					}
					if (genericIface == null)
						throw new RuntimeException("Interface not created");
				}
			}
		}

		if (genericIface != null) {
			String xml = null;
			if (genericIface instanceof ModelInterfaceRuntimeServer) {
				ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer) genericIface;
				ifaceId = iface.getRuntimeId();
				interfaceId.setInterfaceRuntimeId(ifaceId.getInterfaceRuntimeId());
				synchronized (interfaceListener) {
					interfaceListener.addListeners(sessionId, iface, interfaceId, false);
				}
				DomeServer.addToResourceClientMap(iface.getRuntimeId().getInterfaceRuntimeId(), sessionId);
				xml = iface.getLiveXmlDescription();
			}
			else if (genericIface instanceof OptimizationToolInterfaceRuntimeServer) {
				OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) genericIface;
				ifaceId = iface.getRuntimeId();
				interfaceId.setInterfaceRuntimeId(ifaceId.getInterfaceRuntimeId());
				synchronized (interfaceListener) {
					interfaceListener.addListeners(sessionId, iface, interfaceId);
				}
				DomeServer.addToResourceClientMap(iface.getRuntimeId().getInterfaceRuntimeId(), sessionId);
				xml = iface.getLiveXmlDescription();
			}
			else {
				throw new RuntimeException("ServerPlayspace.createInterface - can not register unsupported interface type: " + ClassUtils.getClassName(genericIface));
			}
			if (ifaceParentRuntimeId == null) { // if interface already existed
				ifaceParentRuntimeId = ifaceId.getModelRuntimeId();
				if (ifaceParentRuntimeId == null)
					ifaceParentRuntimeId = ifaceId.getFirstProjectRuntimeId();
			}
			DomeServer.addToResourceClientMap(ifaceParentRuntimeId, sessionId);
			return Vectors.create(ifaceId.toString(), xml, Boolean.TRUE, Boolean.FALSE);
		} else { // will need to create model and interface
			synchronized (ifaceThreads) {
				// if in permanent playspace, check if another client has started this interface
				CreateTopLevelInterfaceThread t = null;
				if (interfaceId.getPlayspaceStaticId() != null)
					t = (CreateTopLevelInterfaceThread) ifaceThreads.get(interfaceId.getPlayspaceStaticId()+"."+interfaceId.getInterfaceStaticId());
				if (t == null) { // create thread and register it
					// set runtime ids for interface and model
					String modelRuntimeId = UUIDGenerator.create();
					if (DbConstants.IFACE_PARENT_TYPE_PROJECT.equals(ifaceParentType)) {
						interfaceId.setFirstProjectRuntimeId(modelRuntimeId);
					} else { // assume model/tool
						interfaceId.setModelRuntimeId(modelRuntimeId);
					}
					interfaceId.setInterfaceRuntimeId(UUIDGenerator.create());
					t = new CreateTopLevelInterfaceThread(interfaceId, sessionId, xmlContent, xmlMappings, ifaceParentType);
					ifaceThreads.put(interfaceId.getInterfaceRuntimeId(), t);
					ifaceStatuses.put(interfaceId.getInterfaceRuntimeId(), "waiting to be started");
					if (interfaceId.getPlayspaceStaticId() == null) {
						ifaceThreads.put(interfaceId.getPlayspaceStaticId() + "." + interfaceId.getInterfaceStaticId(), t);
					}
					return Vectors.create(interfaceId.toString(), xmlContent, Boolean.FALSE, Boolean.TRUE);
				} else {
					if (t.isDone()) {
						t.registerSessionId(sessionId);
						return Vectors.create(t.interfaceId.toString(), t.getInterfaceXml(), Boolean.TRUE, Boolean.FALSE);
					} else {
						t.addSessionId(sessionId);
						return Vectors.create(t.interfaceId.toString(), xmlContent, Boolean.FALSE, Boolean.FALSE);
					}
				}
			}
		}
	}

	public void startInterfaceParent(String ifaceRuntimeId) {
		CreateTopLevelInterfaceThread r = (CreateTopLevelInterfaceThread)ifaceThreads.get(ifaceRuntimeId);
		if (r == null)
			System.err.println("startInterfaceParent - unable to find interface parent start thread for "+ifaceRuntimeId);
		else
		{
			new Thread(r).start();
		}
	}

	// used for retrieving information when multiple playspace clients try to find same interface
	private HashMap ifaceThreads = new HashMap(); // ifaceRuntimeId/playspaceStaticId.ifaceStaticId - CreateTopLevelInterfaceThread instance
	private HashMap ifaceStatuses = new HashMap(); // ifaceRuntimeId - creation status

	public String getInterfaceStatus(CompoundId ifaceId) {
		synchronized (ifaceStatuses) {
			String status = (String)ifaceStatuses.get(ifaceId.getInterfaceRuntimeId());
			if (status == null) {
				status = "";
			}
			return status;
		}
	}

	class CreateTopLevelInterfaceThread implements Runnable {
		CompoundId interfaceId; // has iface and iface parent static & runtime ids
		String ifaceStaticId, ifaceRuntimeId, ifaceParentRuntimeId;
		List sessionIds = new ArrayList(); // could have multiple playspace clients
		String xmlContent, xmlMappings;
		String ifaceParentType;
		Object genericIface = null;
		Model ifaceParent = null;
		boolean done = false, killRequested = false;

		public CreateTopLevelInterfaceThread(CompoundId interfaceId, String sessionId, String xmlContent, String xmlMappings, String ifaceParentType)
		{
			this.interfaceId = new CompoundId(interfaceId);
			this.sessionIds.add(sessionId);
			this.xmlContent = xmlContent;
			this.xmlMappings = xmlMappings;
			this.ifaceParentType = ifaceParentType;
			this.ifaceStaticId = interfaceId.getInterfaceStaticId(); // save since it is destroyed by project creation
			this.ifaceRuntimeId = interfaceId.getInterfaceRuntimeId();
		}

		public void requestKill() { // called within lock on ifaceThreads
			if (done) {
				killInterfaceParent(ifaceParent);
			} else {
				killRequested = true;
			}
		}

		// do not call if not done!
		public String getInterfaceXml() {
			if (genericIface instanceof ModelInterfaceRuntimeServer) {
				ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer) genericIface;
				return iface.getLiveXmlDescription();
			}
			else if (genericIface instanceof OptimizationToolInterfaceRuntimeServer) {
				OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) genericIface;
				return iface.getLiveXmlDescription();
			}
			return "";
		}

		public void registerSessionId(String sessionId) {
			if (genericIface instanceof ModelInterfaceRuntimeServer) {
				ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer) genericIface;
				synchronized (interfaceListener) {
					DomeServer.addToResourceClientMap(ifaceParentRuntimeId, sessionId);
					DomeServer.addToResourceClientMap(interfaceId.getInterfaceRuntimeId(), sessionId);
					interfaceListener.addListeners(sessionId, iface, interfaceId, false);
				}
			}
			else if (genericIface instanceof OptimizationToolInterfaceRuntimeServer) {
				OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) genericIface;
				synchronized (interfaceListener) {
					DomeServer.addToResourceClientMap(ifaceParentRuntimeId, sessionId);
					DomeServer.addToResourceClientMap(interfaceId.getInterfaceRuntimeId(), sessionId);
					interfaceListener.addListeners(sessionId, iface, interfaceId);
				}
			}
		}

		public void addSessionId(String sessionId) {
			this.sessionIds.add(sessionId);
		}

		public boolean isDone() {
			synchronized (ifaceThreads) { // overkill synchronization...
				return done;
			}
		}

		public void run()
		{
			Debug.trace(Debug.ALL, ModelRuntime.STATUS_IFACE_PARENT_STARTING);
			Debug.trace(Debug.ALL, "AT_TIME: "+new Date());
			handleStatusMessage(ModelRuntime.STATUS_IFACE_PARENT_STARTING);
			CompoundId ifaceParentId = null;
			try {
				ifaceParent = loadModel(new CompoundId(interfaceId), ifaceParentType, false); // send in an id copy so it does not get destroyed
				Debug.trace(Debug.ALL, "finished creating interface parent in thread: " + Names.getName(ifaceParent));
			}
			catch (Exception e) {
				handleException("Error loading interface parent: \n"+e.getMessage());
				return;
			}
			if (killRequested) {
				killInterfaceParent(ifaceParent);
				return;
			} else if (ifaceParent == null) {
				handleException("Unable to get interface parent");
				return;
			}

			handleStatusMessage(ModelRuntime.STATUS_IFACE_STARTING);
			try {
				if (ifaceParent instanceof OptimizationToolRuntime) {
					OptimizationToolRuntime tool = (OptimizationToolRuntime) ifaceParent;
					ifaceParentId = tool.getRuntimeId();
					ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
					genericIface = tool.loadInterface(interfaceId, xmlContent, xmlMappings);
				}
				else if (ifaceParent instanceof IntegrationProjectServerRuntime) {
					IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) ifaceParent;
					ifaceParentId = project.getRuntimeId();
					ifaceParentRuntimeId = ifaceParentId.getFirstProjectRuntimeId();
					genericIface = project.loadInterface(interfaceId, xmlContent, xmlMappings);
				}
				else if (ifaceParent instanceof PluginModelRuntime || ifaceParent instanceof DomeModelRuntime) {
					ModelRuntime model = (ModelRuntime) ifaceParent;
					ifaceParentId = model.getRuntimeId();
					ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
					genericIface = ((DomeModelBase) ifaceParent).loadRuntimeInterface(interfaceId, xmlContent, xmlMappings);
				}
				else {
					handleException("Unsupported interface parent type: " + ClassUtils.getClassName(ifaceParent));
					return;
				}
			}
			catch (Exception e) {
				handleException("Error loading interface: \n" + e.getMessage());
				return;
			}
			synchronized (ifaceThreads) { // finish up
				if (killRequested) {
					killInterfaceParent(ifaceParent);
					return;
				} else if (genericIface == null) {
					handleException("Interface not created");
					return;
				}
				if (genericIface instanceof ModelInterfaceRuntimeServer) {
					ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer) genericIface;
					Debug.trace(Debug.ALL, "completed creating interface: " + iface.getName());
					synchronized (interfaceListener) {
						String sessionId;
						for (int i = 0; i < sessionIds.size(); i++) {
							sessionId = (String) sessionIds.get(i);
							DomeServer.addToResourceClientMap(ifaceParentRuntimeId, sessionId);
							DomeServer.addToResourceClientMap(iface.getRuntimeId().getInterfaceRuntimeId(), sessionId);
							interfaceListener.addListeners(sessionId, iface, interfaceId, false);
						}
					}
				}
				else if (genericIface instanceof OptimizationToolInterfaceRuntimeServer) {
					OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) genericIface;
					Debug.trace(Debug.ALL, "completed creating interface: " + iface.getName());
					synchronized (interfaceListener) {
						String sessionId;
						for (int i = 0; i < sessionIds.size(); i++) {
							sessionId = (String) sessionIds.get(i);
							DomeServer.addToResourceClientMap(ifaceParentRuntimeId, sessionId);
							DomeServer.addToResourceClientMap(iface.getRuntimeId().getInterfaceRuntimeId(), sessionId);
							interfaceListener.addListeners(sessionId, iface, interfaceId);
						}
					}
				}
				else {
					handleException("can not register unsupported interface type: " + ClassUtils.getClassName(genericIface));
				}
				handleStatusMessage(ModelRuntime.STATUS_IFACE_CREATED);
				done = true;
			}
		}

		private void handleStatusMessage(String msg)
		{
			synchronized (ifaceStatuses) {
				ifaceStatuses.put(this.ifaceRuntimeId, msg);
			}
			String sessionId;
			for (int i = 0; i < sessionIds.size(); i++) {
				sessionId = (String) sessionIds.get(i);
				MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_STATUS_CHANGED, sessionId,
				                             Vectors.create(interfaceId.toString(), new Integer(Integer.MIN_VALUE), msg));
			}
		}

		private void handleException(String msg)
		{
			String sessionId;
			for (int i = 0; i < sessionIds.size(); i++) {
				sessionId = (String) sessionIds.get(i);
				MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_STARTUP_ERROR, sessionId, Vectors.create(msg));
			}
		}

	}

    /**
     * Create (or retrieve an existing) interface instance for the client.
     * @param projectId Project static id
     * @return Project XML description
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public Object[] createProject(String sessionId, CompoundId projectId)
            throws XmlRpcException {
        IntegrationProjectServerRuntime project = getProject(projectId, true);
        Object[] xml_id = new Object[3];
        xml_id[0] = project.getRuntimeId().toString();
        xml_id[1] = project.toXmlElement().asXML();
        xml_id[2] = project.getResourceIdMap();
        return xml_id;
    }

    public Object[] createAnalysisTool(String sessionId, CompoundId analysisToolId)
            throws XmlRpcException {
        OptimizationToolRuntime analysisTool = getAnalysisTool(analysisToolId, true);
        Object[] xml_id = new Object[5];
        xml_id[0] = analysisTool.getRuntimeId().toString();
        xml_id[1] = analysisTool.toXmlElement().asXML();
        xml_id[2] = analysisTool.getClientProjectId().toString();
        String[] idNameDesc = FileSystemDbFunctions.getProjectStaticInfo(analysisTool.getClientProjectId().getCurrentProjectStaticId());
        xml_id[3] = idNameDesc[1]; // project name
        xml_id[4] = idNameDesc[2]; // project description
        //xml_id[4] = analysisTool.getProjectForClient().getResourceIdMap();
        return xml_id;
    }

	public Vector getResourceGraph(CompoundId resourceId, Vector interfaceIds)
	        throws XmlRpcException {
		Object resource = getModel(resourceId);
		if (resource instanceof IntegrationProjectServerRuntime)
			return ((IntegrationProjectServerRuntime) resource).getAggregatedInterfaceInfo(interfaceIds);
		else {
			if (resource != null) {
				if (resource instanceof DomeModelRuntime)
					return ((DomeModelRuntime) resource).getAggregatedInterfaceInfo(interfaceIds);
				else if (resource instanceof PluginModelRuntime)
					return ((PluginModelRuntime) resource).getAggregatedInterfaceInfo(interfaceIds);
				else
					System.err.println("ServerPlayspace.getResourceGraph: unknown model type - " +
					                   ClassUtils.getClassName(resource));
			}
		}
		System.err.println("ServerPlayspace.getResourceGraph: unable to get graph for " + resourceId);
		return DbUtils.NO_VECTOR;
    }


    public void setResourceExternalGraph(CompoundId resourceId, String extGraphXml) {

        synchronized (modelInfo) {
            Object model = modelInfo.getObjectFromRuntimeId(resourceId.getModelRuntimeId());
            if (model != null) {
                if (model instanceof DomeModelRuntime)
                    ((DomeModelRuntime) model).addExternalGraph(extGraphXml);
                else if (model instanceof PluginModelRuntime)
                    ((PluginModelRuntime) model).addExternalGraph(extGraphXml);
                else if (model instanceof IntegrationProjectServerRuntime)
                    ((IntegrationProjectServerRuntime) model).addExternalGraph(extGraphXml);
                else
                    System.err.println("ServerPlayspace.setResourceExternalGraph: unknown model type - " +
                            ClassUtils.getClassName(model));
            }
        }
    }

    /**
     * Admit a client to a playspace.
     * Qing: change here, same session should be only added once
     * @param sessionId User session id
     */
    public void join(String sessionId) {
        synchronized (userStatusMap) {
            userStatusMap.put(sessionId, DbConstants.SESSION_STATUS_ACTIVE);
        }
    }

    /**
     * Set a batch of data object values and run the necessary models.
     * @param sessionId User session id
     * @param changeMaps Vector of value change maps (assumes different interfaces, same model)
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public void setItems(String sessionId, Vector changeMaps, boolean shouldSolve)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = null;

        DSet models = new DSet();

        // iterate through the list of change maps
        for (Iterator mapsIter = changeMaps.iterator(); mapsIter.hasNext();) {
            // get next change map
            Hashtable changeMap = (Hashtable) mapsIter.next();
            Enumeration keys = changeMap.keys();
            iface = null; // start with no interface for each hashtable
            if (keys.hasMoreElements()) {
                // get the interface
                String idString = keys.nextElement().toString();
                CompoundId objectId = new CompoundId(idString);
                if (iface == null) {
                    iface = (ModelInterfaceRuntimeServer)getInterface(objectId); // find interface once per hashtable
                }
                if (iface != null) {
                    iface.setValues(changeMap);
                    models.add(iface.getModel());
                } else {
	                Model model = getModel(objectId);
	                if (model == null)
		                System.err.println("ServerPlayspace.setItems - could not find interface parent " + objectId);
	                else
	                    System.err.println("ServerPlayspace.setItems - could not find interface " + objectId);
                }
            }
        }

        // run models
        if (shouldSolve) {
            Model model;
            for (int i = 0; i < models.size(); i++) {
                model = (Model) models.get(i);
                if (model instanceof ModelRuntime)
                    ((ModelRuntime) model).startModel();
                else if (model instanceof IntegrationProjectServerRuntime)
                    ((IntegrationProjectServerRuntime) model).startProject();
                else
                    System.err.println("ServerPlayspace.setItems(): don't know how to start model " + model.getName());
            }
        }
    }

    public void setAnalysisToolClientProjectItems(String sessionId, String compoundId, Vector items) throws XmlRpcException {
        Debug.trace(Debug.ALL, "Entering setAnalysisToolClientProjectItems");

        Hashtable variables = (Hashtable) items.get(1);
        Parameter targetParameter = null;

        CompoundId objectId = new CompoundId(compoundId);
        OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer)getInterface(objectId);

        if (iface != null) {
            Model m = iface.getModel();
            if (m instanceof ModelRuntime) {
                ModelRuntime model = (ModelRuntime) m;
                IntegrationProjectServerRuntime project = ((OptimizationToolRuntime) model).getProjectForClient();
                if (project == null)
                    return;
                ConnectionMappingManager mgr = model.getMappingManager();
                Iterator interfaceParameters = iface.getInterfaceObjectsFlatMap().values().iterator();
                while (interfaceParameters.hasNext()) {
                    Parameter p = (Parameter) interfaceParameters.next(); // getting the interface parameter object
                    if (iface.getInterfaceVariableMap().containsKey(p) && variables.containsKey(p.getId().toString())) {
                        Collection analysisToolParameters = mgr.getMappingsForParameter(p);
                        if (!analysisToolParameters.isEmpty()) {
                            Parameter analysisToolParameter = (Parameter) analysisToolParameters.toArray()[0]; // getting the analysis tool parameter
                            if (analysisToolParameter.getScope() instanceof OptimizationToolRuntime) {
                                Collection modelParameters = mgr.getMappingsForParameter(analysisToolParameter);
                                if (!modelParameters.isEmpty()) {
                                    targetParameter = (Parameter) modelParameters.toArray()[0]; // getting the model parameter
                                    if (targetParameter.getScope() instanceof DefaultSubscription) {
                                        DefaultSubscription def = (DefaultSubscription) targetParameter.getScope();
                                        targetParameter = def.getInterfaceParameter(targetParameter.getId().toString());
                                    }
                                    List iModels = project.getIntegrationModels();
                                    Iterator iModelIterator = iModels.listIterator();
                                    while (iModelIterator.hasNext()) {
                                        Object object = iModelIterator.next();

                                        if (object instanceof ProjectIntegrationModelInfoRuntime) {
                                            DomeModelRuntime iModel = (DomeModelRuntime) ((ProjectIntegrationModelInfoRuntime) object).getModel();
                                            Object obj = iModel.getModelObjectById(targetParameter.getId());
                                            if (obj instanceof Parameter) {
                                                Parameter parameter = (Parameter) obj;
                                                if (parameter.getCurrentDataObject().getTypeName().equals(DomeReal.TYPE_INFO.getTypeName()))
                                                    ((DomeReal) parameter.getCurrentDataObject()).setRealValue((Double) variables.get(p.getId().toString()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Integer msgId = new Integer(Integer.MIN_VALUE);
                Iterator clientIds = DomeServer.getResourceClients(project.getRuntimeId().getCurrentProjectRuntimeId()).iterator();
                while (clientIds.hasNext()) {
                    String clientId = (String) clientIds.next();
                    MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_STATUS_CHANGED,
                            clientId, Vectors.create(project.getRuntimeId().toString(), msgId, ModelRuntime.STATUS_INCONSISTENT));
                }
            }
        }
    }

    public void startAnalysisToolSolving(String sessionId, Vector compoundIds, Hashtable parameterAttributes, boolean shouldSolve)
            throws XmlRpcException
    {
        Debug.trace(Debug.ALL, "Entering setAnalysisToolItems");

        OptimizationToolInterfaceRuntimeServer iface = null;

        // iterate through the list of change maps
        for (Iterator mapsIter = compoundIds.iterator(); mapsIter.hasNext();)
        {
// compoundsIds is a vector of strings

// get the interface
            String idString = (String) mapsIter.next();
            CompoundId objectId = new CompoundId(idString);
            if (iface == null)
            {
                iface = (OptimizationToolInterfaceRuntimeServer)getInterface(objectId); // find interface once per hashtable
                iface.setParameterAttributes(parameterAttributes);
            }
        }
        // run model
        if (iface != null && shouldSolve)
        {
            Model m = iface.getModel();
            if (m instanceof ModelRuntime)
            {
                ModelRuntime model = (ModelRuntime) m;
                if (m instanceof OptimizationToolRuntime)
                    ((OptimizationToolRuntime) m).setConfigurationFromInterface(iface);
                model.startModel();
            }
        }

        Debug.trace(Debug.ALL, "Leaving SetItems");
    }

    public void setItemStatus(String sessionId, CompoundId objectId, String status)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = null;
        iface = (ModelInterfaceRuntimeServer)getInterface(objectId); // find interface once per hashtable

        if (iface != null) {
            // get the interface parameter
            InterfaceParameterRuntime p = null;
            Id objectIdString = new Id(objectId.getDataObjectStaticId());
            if (objectIdString != null) {
                p = (InterfaceParameterRuntime) iface.getModelObjectById(objectIdString);
                p.setValueStatus(status);
            }
        }
    }

    public void setItemStatus(CompoundId objectId, String status)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = null;
        iface = (ModelInterfaceRuntimeServer) getInterface(objectId);

        if (iface != null) {
            // get the interface parameter
            InterfaceParameterRuntime p = null;
            Id objectIdString = new Id(objectId.getDataObjectStaticId());
            if (objectIdString != null) {
                p = (InterfaceParameterRuntime) iface.getModelObjectById(objectIdString);
                p.setValueStatus(status);
            }
        }
    }

	public void setParametersInconsistent(Vector paramIds)
	        throws XmlRpcException
	{
		ModelInterfaceRuntimeServer iface = null;
		iface = (ModelInterfaceRuntimeServer)getInterface((CompoundId)paramIds.get(0));

		if (iface != null) {
			iface.setParametersInconsistent(paramIds);
		} else {
			System.err.println("ServerPlayspace.setParametersInconsistent could not find interface for parameters: " + paramIds);
		}
	}

    /**
     * Start the solving process for the model(s) associated with the given interface.
     * @param interfaceId Interface id
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public void startSolving(CompoundId interfaceId)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer)getInterface(interfaceId);
        if (iface != null) {
            ModelRuntime model = (ModelRuntime) iface.getModel();
            model.startModel();
        }
    }


    /**
     * Pause the solving process for the model(s) associated with the given interface.
     * @param interfaceId Interface id
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public void pauseSolving(CompoundId interfaceId)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer)getInterface(interfaceId);
        if (iface != null) {
            Model m = iface.getModel();
            if (m instanceof ModelRuntime) {
                ModelRuntime model = (ModelRuntime) m;
                model.pauseModel();
            } else if (m instanceof IntegrationProjectServerRuntime) {
                throw new UnsupportedOperationException("ServerPlaysapce.pauseSolving: can't pause solving in project interface");
            }
        }
    }


    /**
     * Resume the the solving process for the model(s) associated with the given interface.
     * @param interfaceId Interface id
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public void resumeSolving(CompoundId interfaceId)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer)getInterface(interfaceId);
        if (iface != null) {
            Model m = iface.getModel();
            if (m instanceof ModelRuntime) {
                ModelRuntime model = (ModelRuntime) m;
                model.resumeModel();
            } else if (m instanceof IntegrationProjectServerRuntime) {
                throw new UnsupportedOperationException("ServerPlaysapce.resumeSolving: can't resume solving in project interface");
            }
        }
    }


    /**
     * Kill the solving process for the model(s) associated with the given interface.
     * @param interfaceId Interface id
     * @throws org.apache.xmlrpc.XmlRpcException
     */
    public void killSolving(CompoundId interfaceId)
            throws XmlRpcException {
        ModelInterfaceRuntimeServer iface = (ModelInterfaceRuntimeServer)getInterface(interfaceId);
        if (iface != null) {
            if (iface.getModel() instanceof ModelRuntime) {
                ModelRuntime model = (ModelRuntime) iface.getModel();
                //todo: model.stopModel(); -> now this gives java.lang.UnsupportedOperationException exception
            } else if (iface.getModel() instanceof IntegrationProjectServerRuntime) {
                IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) iface.getModel();
                //todo: project.stopProject(); -> now this gives java.lang.UnsupportedOperationException exception
            }

        }
    }


    /**
     * Remove the user from the playspace member list. Kill the playspace if there
     * are no more members.
     * @return Flag indicating whether playspace is dead. A playspace is killed when
     * the last user has quit the playspace.
     */
    public boolean leave(String sessionId) throws XmlRpcException {
        synchronized (userStatusMap) {
            if (sessionId != null) {
                // remove the member
                userStatusMap.remove(sessionId);
            }
	        if (userStatusMap.isEmpty())
		        shutdown();
        }
        return false;
    }


    /**
     * Terminate the playspace and all its objects. Do not wait for client interfaces to close.
     * This is done when the server is shutting down.
     */
    public void shutdown() {
	    if (activePlayspaceModels.intValue() == 0) { // all top-level playspace models killed
		    ServerPlayspace.this.firePropertyChange(PROPERTY_CLOSED); // unregister from server
	        return;
	    }

	    // be careful not to trigger ConcurrentModificationException!

        // kill all projects
        for (Iterator iter = new ArrayList(projectInfo.getAllObjects()).iterator(); iter.hasNext();) {
            IntegrationProjectServerRuntime project;
            project = (IntegrationProjectServerRuntime) iter.next();
            project.killProject();
	        //qing---release the memory
	        project=null;
        }

        // kill all models
        for (Iterator iter = new ArrayList(modelInfo.getAllObjects()).iterator(); iter.hasNext();) {
            ModelRuntime model;
            model = (ModelRuntime) iter.next();
            if (model instanceof PluginModelRuntime) {
                ((PluginModelRuntime) model).deleteModel();
            } else if (model instanceof DomeModelRuntime) {
                ((DomeModelRuntime) model).deleteModel();
            }
	        //qing---release the memory
	        model=null;
        }

        for (Iterator iter = new ArrayList(analysisToolInfo.getAllObjects()).iterator(); iter.hasNext();) {
            ModelRuntime model;
            model = (ModelRuntime) iter.next();
            if (model instanceof OptimizationToolRuntime) {
                ((OptimizationToolRuntime) model).deleteModel();
            }
	        //qing---release the memory
	        model=null;
        }
    }

    /**
     * Kill a specific model
     * @param modelId Model id
     */
    public void killModel(CompoundId modelId) {
	    ModelRuntime model = (ModelRuntime)getModel(modelId);
	    if (model == null) {
	        System.err.println("ServerPlayspace.killModel - unable to find model: " + modelId);
	        return;
	    }
        if (model instanceof PluginModelRuntime) {
            ((PluginModelRuntime) model).deleteModel();
        } else if (model instanceof DomeModelRuntime) {
            ((DomeModelRuntime) model).deleteModel();
        }
	    //qing---release the memory
	    model=null;
    }


    /**
     * Kill a specific project
     * @param projectId Project id
     */
    public void killProject(CompoundId projectId) {
        IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime)getModel(projectId);
	    if (project == null) {
		    System.err.println("ServerPlayspace.killProject - unable to find project: " + projectId);
		    return;
	    }
        project.killProject();
    }

	/**
	 * Kill a specific analysis tool
	 * @param toolId id of tool
	 */
	public void killTool(CompoundId toolId)
	{
		OptimizationToolRuntime tool = (OptimizationToolRuntime) getModel(toolId);
		if (tool == null) {
			System.err.println("ServerPlayspace.killTool - unable to find tool: " + toolId);
			return;
		}
		tool.deleteModel();
	}

	protected String getRuntimeId(CompoundId cId) {
		String runtimeId = cId.getModelRuntimeId();
		if (runtimeId == null) {
			runtimeId = cId.getFirstProjectRuntimeId();
		}
		return runtimeId;
	}

	protected Model getModel(CompoundId cId) {
		String runtimeId = getRuntimeId(cId);
		if (runtimeId == null)
			return null;
		Object model = resources.get(runtimeId);
		if (model == null) {
			model = modelInfo.getObjectFromRuntimeId(runtimeId);
			if (model == null)
				model = projectInfo.getObjectFromRuntimeId(runtimeId);
			if (model == null)
				model = analysisToolInfo.getObjectFromRuntimeId(runtimeId);
		}
		return (Model)model;
	}

	protected Model getModelByStaticId(CompoundId cId)
	{
		String staticId = cId.getModelStaticId();
		if (staticId == null) {
			staticId = cId.getFirstProjectStaticId();
		}
		if (staticId == null)
			return null;
		Object model = modelInfo.getObjectFromStaticId(staticId);
		if (model == null) {
			model = projectInfo.getObjectFromStaticId(staticId);
			if (model == null)
				model = analysisToolInfo.getObjectFromStaticId(staticId);
		}
		return (Model) model;
	}

	/**
	 * Returns either a ModelInterfaceRuntimeServer or an OptimizationToolInterfaceRuntimeServer
	 * @param cId
	 * @return
	 */
	protected Object getInterface(CompoundId cId) {
		return getInterface(cId, false);
	}

	protected Object getInterface(CompoundId cId, boolean useParentStaticId) {
		Model model = getModel(cId);
		if (model == null && useParentStaticId)
			model = getModelByStaticId(cId);
		if (model == null)
			return null;
		String ifaceId = cId.getInterfaceRuntimeId();
		if (ifaceId == null)
			ifaceId = cId.getInterfaceStaticId();
		if (ifaceId == null)
			return null;
		if (model instanceof OptimizationToolRuntime) {
			return ((OptimizationToolRuntime) model).getAnalysisToolInterfacesManager().getById(ifaceId);
		}
		else if (model instanceof IntegrationProjectServerRuntime) {
			return ((IntegrationProjectServerRuntime) model).getProjectInterfacesManager().getById(ifaceId);
		}
		else if (model instanceof PluginModelRuntime || model instanceof DomeModelRuntime) {
			return ((DomeModelBase) model).getModelInterfacesManager().getById(ifaceId);
		}
		return null;
	}

    /**
     * Find the interface parent and kill it.
     * @param interfaceId Interface static or runtime id
     */
    public void killInterfaceParent(CompoundId interfaceId)
            throws XmlRpcException {
	    Model parent = getModel(interfaceId);
	    if (parent != null) {
		    killInterfaceParent(parent);
	    } else {
			// check to see if model is in process
		    synchronized (ifaceThreads) {
			    CreateTopLevelInterfaceThread t = (CreateTopLevelInterfaceThread) ifaceThreads.get(interfaceId.getInterfaceRuntimeId());
			    if (t != null) {
					t.requestKill();
			    } else
				    // Print err message instead of throwing an exception
                    System.err.println("ServerPlayspace.killInterfaceParent - unable to find parent model: " + interfaceId);
                    // throw new RuntimeException("ServerPlayspace.killInterfaceParent - unable to find parent model: " + interfaceId);
		    }
	    }
    }

	protected void killInterfaceParent(Model parent) {
		if (parent instanceof PluginModelRuntime) {
			((PluginModelRuntime) parent).deleteModel();
		}
		else if (parent instanceof DomeModelRuntime) {
			((DomeModelRuntime) parent).deleteModel();
		}
		else if (parent instanceof IntegrationProjectServerRuntime) {
			((IntegrationProjectServerRuntime) parent).killProject();
		}
		else if (parent instanceof OptimizationToolRuntime) {
			((OptimizationToolRuntime) parent).deleteModel();
		}
		else {
			throw new RuntimeException("ServerPlayspace.killInterfaceParent - unsupported parent type: " + ClassUtils.getClassName(parent));
		}
	}

    public Hashtable getParameterSystemCausality(String sessionId, CompoundId interfaceId)
            throws XmlRpcException {
		Object iface = getInterface(interfaceId);
	    if (iface instanceof ModelInterfaceRuntimeServer) {
		    return ((ModelInterfaceRuntimeServer)iface).getParameterSystemCausality();
	    }
	    else if (iface != null) {
		    throw new RuntimeException("ServerPlayspace.getParameterSystemCausality - unsupported interface type: " + ClassUtils.getClassName(iface));
	    } else {
	        throw new RuntimeException("ServerPlayspace.getParameterSystemCausality - unable to find interface: " + interfaceId);
        }
    }

    private class ModelStatusListener implements PropertyChangeListener {
        private HashMap lastModelStatusChangeId = new HashMap(); // key=modelId, value = changeId

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ModelRuntime.RUN_STATUS)) {
                Integer msgId = ((DomePropertyChangeEvent) evt).getEventId();
                Object source = evt.getSource();
                String sourceType = null, sourceName = null;
                CompoundId compoundId = null;
                String runtimeId = null;
                if (source instanceof ModelRuntime) {
                    ModelRuntime model = (ModelRuntime) source;
                    sourceType = "MODEL";
                    sourceName = model.getName();
                    compoundId = model.getRuntimeId();
                    runtimeId = compoundId.getModelRuntimeId();
                } else if (source instanceof IntegrationProjectServerRuntime) {
                    IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) source;
                    sourceType = "PROJECT";
                    sourceName = project.getName();
                    compoundId = project.getRuntimeId();
                    runtimeId = compoundId.getCurrentProjectRuntimeId();
                } else {
                    System.err.println("ServerPlayspace.ModelStatusListener received status from unsupported source type: " +
                            ClassUtils.getClassName(source));
                    return;
                }
                String status = (String) evt.getNewValue();

                // avoid processing mixed up messages and setting earlier values on top of later values
                synchronized (lastModelStatusChangeId) {
                    Integer lastMsgId = (Integer) lastModelStatusChangeId.get(runtimeId);
                    if (lastMsgId == null || msgId.intValue() > lastMsgId.intValue())
                        lastModelStatusChangeId.put(runtimeId, msgId);
                    else {
                        Debug.trace(Debug.STATUS, sourceType + " STATUS IGNORED (out of order): " + status + " for \"" + sourceName + "\"");
                        return; // ignore changes that come in reverse
                    }
                }

                Debug.trace(Debug.STATUS, sourceType + " STATUS: " + status + " for \"" + sourceName + "\"");
                Collection subscriptionIdList = DomeServer.getSubscriptionInterfaces(compoundId);
                if (subscriptionIdList != null) {
                    Object[] subInfo;
                    for (Iterator iterator = subscriptionIdList.iterator(); iterator.hasNext();) {
                        subInfo = (Object[]) iterator.next();
                        String sessionId = (String) subInfo[0];
                        CompoundId ifaceId = (CompoundId) subInfo[1];
                        MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_STATUS_CHANGED,
                                sessionId,
                                Vectors.create(ifaceId.toString(), msgId, status));
                    }
                }
                Collection clientIdList = DomeServer.getResourceClients(runtimeId);
                if (clientIdList == null)
                    return;
                if (subscriptionIdList != null)
                    clientIdList = DSet.removeSet(clientIdList, subscriptionIdList);
                for (Iterator clientIter = clientIdList.iterator(); clientIter.hasNext();) {
                    String clientId = (String) clientIter.next();
                    MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_STATUS_CHANGED,
                            clientId,
                            Vectors.create(compoundId.toString(), msgId, status));
                }
            }
        }
    }

    public void notifyProjectRunComplete(CompoundId resourceId)
            throws XmlRpcException {
        IntegrationProjectServerRuntime project = null;
        project = getProject(resourceId, false);
        if (project != null) {
            project.notifyProjectRunComplete();
        } else {
            synchronized (resources) {
                Object model = resources.get(resourceId.getModelRuntimeId());
                if (model != null) {
                    if (model instanceof DomeModelRuntime)
                        ((DomeModelRuntime) model).notifyProjectRunComplete();
                    else if (model instanceof PluginModelRuntime)
                        ((PluginModelRuntime) model).notifyProjectRunComplete();
                    else if (model instanceof IntegrationProjectServerRuntime)
                        ((IntegrationProjectServerRuntime) model).notifyProjectRunComplete();
                    else
                        System.err.println("ServerPlayspace.notifyProjectRunComplete: not implemented yet for unknown model type - " +
                                ClassUtils.getClassName(model));
                }
            }
        }
    }

	public String getXmlDescription()
	{
		return xmlDescription; // always use original copy! generated copies include project resources
	}

	private void incrementActivePlayspaceModels() {
		synchronized (activePlayspaceModels) {
			activePlayspaceModels = new Integer(activePlayspaceModels.intValue() + 1);
		}
	}

	private void decrementActivePlayspaceModels() {
		synchronized (activePlayspaceModels) {
			activePlayspaceModels = new Integer(activePlayspaceModels.intValue() - 1);
		}
	}

	private class ModelKilledListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt)
		{
			// todo: remove other things from server data structures

			Object model = evt.getSource();
			if (model instanceof DomeModelRuntime || model instanceof PluginModelRuntime) {
				ModelRuntime m = (ModelRuntime) model;
				releaseInterfaces(((DomeModelBase)model).getModelInterfacesManager(), m.isProjectResource());
				String runtimeId = m.getRuntimeId().getModelRuntimeId();
				if (m.isProjectResource())
					synchronized (resources) {
						resources.remove(runtimeId);
					}
				else
					synchronized (modelInfo) {
						modelInfo.removeUsingRuntimeId(runtimeId);
					}
			} else if (model instanceof IntegrationProjectServerRuntime) {
				IntegrationProjectServerRuntime p = (IntegrationProjectServerRuntime) model;
				releaseInterfaces(p.getProjectInterfacesManager(), p.isProjectResource());
				String runtimeId = p.getRuntimeId().getFirstProjectRuntimeId(); // todo: make sure this works for projects in projects
				if (p.isProjectResource())
					synchronized (resources) {
						resources.remove(runtimeId);
					}
				else
					synchronized (projectInfo) {
						projectInfo.removeUsingRuntimeId(runtimeId);
					}
			} else if (model instanceof OptimizationToolRuntime) {
				OptimizationToolRuntime m = (OptimizationToolRuntime) model;
				releaseInterfaces(m.getAnalysisToolInterfacesManager());
				String runtimeId = m.getRuntimeId().getModelRuntimeId();
				if (m.isProjectResource())
					synchronized (resources) {
						resources.remove(runtimeId);
					}
				else
					synchronized (analysisToolInfo) {
						analysisToolInfo.removeUsingRuntimeId(m.getRuntimeId().getModelRuntimeId());
					}
			} else {
				System.err.println("ModelKilledListener unknown model type: " + ClassUtils.getClassName(model));
				return;
			}
			decrementActivePlayspaceModels();
			if (activePlayspaceModels.intValue() == 0) { // all top-level playspace models killed
				ServerPlayspace.this.firePropertyChange(PROPERTY_CLOSED); // unregister from server
			}
		}

		private void releaseInterfaces(ModelInterfaceManager mgr, boolean isResource) {
			Iterator interfaces = mgr.getInterfaces().iterator();
			ModelInterfaceRuntimeServer ifaceSvr;
			while (interfaces.hasNext()) {
				ifaceSvr = (ModelInterfaceRuntimeServer) interfaces.next();
				interfaceListener.removeListeners(ifaceSvr, isResource);
			}
			((ModelInterfaceManagerRuntime)mgr).cleanup();
		}

		private void releaseInterfaces(AnalysisToolInterfaceManager mgr)
		{
			Iterator interfaces = mgr.getInterfaces().iterator();
			OptimizationToolInterfaceRuntimeServer ifaceSvr;
			while (interfaces.hasNext()) {
				ifaceSvr = (OptimizationToolInterfaceRuntimeServer) interfaces.next();
				interfaceListener.removeListeners(ifaceSvr);
			}
		}
	}

    // external access to resources hashmap, so that an i-model resource can be added before project finishes loading _i
    public void addResource(String runtimeId, DomeModelRuntime model) {
        resources.put(runtimeId, model);
        return;
    }

// iason - elaine
    public Vector loadImodelInterfaceAsResource(String ifaceStaticId, DomeModelRuntime model, String sessionId) {
        try {
            String[] xmlContentMappings
                    = DeployFilesDbFunctions.getMostRecentInterfaceXmlDefinitionAndMappings(ifaceStaticId);
            if (xmlContentMappings.length == 2) {
                String xmlContent = xmlContentMappings[0];
                String xmlMappings = xmlContentMappings[1];

                CompoundId interfaceId = new CompoundId(model.getRuntimeId());
                interfaceId.setInterfaceStaticId(ifaceStaticId);
                CompoundId ifaceParentId = model.getRuntimeId();
                String ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
                interfaceId.setModelStaticId(ifaceParentId.getModelStaticId());
                interfaceId.setModelRuntimeId(ifaceParentRuntimeId);

                ModelInterfaceRuntimeServer iface = model.loadRuntimeInterface(interfaceId, xmlContent, xmlMappings);

                CompoundId ifaceId = iface.getRuntimeId();
                interfaceId.setInterfaceRuntimeId(ifaceId.getInterfaceRuntimeId());
                synchronized (interfaceListener) {
                    interfaceListener.addListeners(sessionId, iface, interfaceId, true);
                }
                //DomeServer.addToResourceClientMap(iface.getRuntimeId().getInterfaceRuntimeId(), this.tempSessionId);

                String xml = iface.getLiveXmlDescription();
                Integer version = FileSystemDbFunctions.getInterfaceVersion(interfaceId.getInterfaceStaticId());

                //register subscription interface - ????
                DomeServer.registerSubscriptionInterface(ifaceParentId,
		                                             sessionId, ifaceId);

                return Vectors.create(null, null, interfaceId, version.toString(), xml);
            }
        } catch (XmlRpcException e) {
            return null;
        }
        return null;
    }

    public void setTempSessionId(String idString) {
        tempSessionId = idString;
    }
}

