package mit.cadlab.dome3.objectmodel.model.tool.optimization.run;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.functions.MessageFunctions;
import mit.cadlab.dome3.network.server.functions.RuntimeFunctionsServer;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.OptimizationToolBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.run.AnalysisToolInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.OptimizationToolInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceConfiguration;
import mit.cadlab.dome3.objectmodel.util.Version;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.AnalysisToolExecutionManager;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOTool;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;


/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 11, 2003
 * Time: 1:51:42 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationToolRuntime extends OptimizationToolBase
                                                implements ModelRuntime
{
    public static final String STATUS_LOADING_PROJECT = "loading project";

    protected QMOOTool plg;
    protected boolean isRemoteModel = false;
	protected CompoundId parentProjectId = null;
	protected String parentServerSessionId = null;
    protected HashMap parametersFlatMap = new HashMap();    // flat list of all model objects
    protected CompoundId _runtimeId = new CompoundId();
    protected Element _mappingsXml = null;
    protected CompoundId _clientProjectId;
	protected String runStatus;

    protected OptimizationToolInterfaceRuntimeServer _iface = null;
    protected IntegrationProjectServerRuntime _clientProject = null;

    private AnalysisToolExecutionManager _solver;

    public OptimizationToolRuntime(String file, Element xml)
	{
		super(file, xml);
	}

    public OptimizationToolRuntime(CompoundId parentId, Element xml)
	{
		super(xml);
        _runtimeId = new CompoundId(parentId);
        if (_runtimeId.getModelRuntimeId() == null)
            _runtimeId.setModelRuntimeId(UUIDGenerator.create());
        assignXmlMappings(xml);
        createClientProjectId();
        populateParametersFlatMap();
        _solver = new AnalysisToolExecutionManager(this);
        _solver.addPropertyChangeListener(AnalysisToolExecutionManager.SOLVING_STATUS, new SolverStatusListener());
        registerListenersOnParameters();
        loadNativeModel();
	}

    /**
     *
     * @param parentServerSessionId
     * @param parentProjectId
     */
	public void setRemoteModel (String parentServerSessionId, CompoundId parentProjectId)
	{
		this.parentServerSessionId = parentServerSessionId;
		parentProjectId = new CompoundId (parentProjectId);
        isRemoteModel = true;
	}

	/**
     * Create a flat list (hashmap) of all parameters, including relation parameters.
     */
    private void populateParametersFlatMap()
    {
        for (Iterator mObjIter = modelObjects.iterator(); mObjIter.hasNext();)
        {
            ModelObject mObj = (ModelObject) mObjIter.next();
            if (mObj instanceof Parameter)
                parametersFlatMap.put(mObj.getId(), mObj);
        }
    }

    protected void registerListenersOnParameters()
    {
        ParameterChangeListener paramListener = new ParameterChangeListener();
        Iterator it = getModelObjects().iterator();
        while (it.hasNext())
        {
            Object obj = it.next();
            if (obj instanceof ModelParameterRuntime)
            {
                // listen to parameter changes
                ((ModelParameterRuntime) obj).addModelQueueListener(paramListener);
            }
        }
    }

    protected ConnectionMappingManager createConnectionMappingManager()
	{
		return new ConnectionMappingManagerRuntime(this);
	}

    public OptimizationToolInterfaceRuntimeServer loadInterface(CompoundId interfaceId, String xmlContent,
                                                                String xmlMappings)
    {
        OptimizationToolInterfaceRuntimeServer iface;

        iface = ((AnalysisToolInterfaceManagerRuntime) _interfaces).loadInterface(interfaceId, xmlContent, xmlMappings);
        return iface;
    }

    class ParameterChangeListener extends DataObjectChangeListener
    {
        public ParameterChangeListener()
        {
            super(OptimizationToolRuntime.this);
        }

        public void dataObjectChanged(DataObjectChangeEvent e)
        {
        }
    }

    public boolean isVariableParameterInInterface(VariableParameter parameter)
    {
        return _iface.isInterfaceParameterMappedToModelParameter(parameter.getParameter());
    }

    public boolean isObjectiveParameterInInterface(ObjectiveParameter parameter)
    {
        return _iface.isInterfaceParameterMappedToModelParameter(parameter.getParameter());
    }

    protected void loadNativeModel()
	{
        plg = new QMOOTool(this);
        plg.createModel();
	}

	public void sendIndividualToClient(Vector isRankOne, Vector variables, Vector objectives)
	{
		plg.sendIndividualToClient(isRankOne, variables, objectives);
	}

    public String getParentServerSessiondId()
    {
        return parentServerSessionId;
    }

    public CompoundId getRootProjectId()
    {
        return parentProjectId;
    }

    public boolean isRemoteModel()
    {
        return isRemoteModel;
    }

    public CompoundId getRuntimeId()
    {
        return _runtimeId;
    }

    public OptimizationToolInterfaceRuntimeServer getAnalysisToolInterface()
    {
        return _iface;
    }

	/**
	 * Method of ModelRuntime. Doesn't really apply here.
	 * @return
	 */
	public boolean isProjectResource()
	{
		return false;
	}

	/**
	 * Method of ModelRuntime. Doesn't really apply here.
	 * @return
	 */
	public boolean hasAtLeastOneInterfaceLoaded()
	{
		return true;
	}

    protected AnalysisToolInterfaceManager createAnalysisToolInterfacesManager()
    {
        return new AnalysisToolInterfaceManagerRuntime(this);
    }

    protected void parseModelInfoElement(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml model info");
		XMLUtils.makeRootElement(xmlElement);
		Element versionXml = (Element) xmlElement.selectSingleNode("/modelinfo/version");
		if (versionXml == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml version: " + xmlElement.asXML());
		version = new Version(versionXml);
	}

    protected void assignXmlMappings(Element xml)
    {
        if (xml == null)
            throw new IllegalArgumentException(getTypeName() + " - no xml model info");
        XMLUtils.makeRootElement(xml);

        _mappingsXml = (Element) xml.selectSingleNode("/" + getXmlTag() + "/mappings/modelMappings");

    }

    protected void loadProjectAndMappings()
    {
         // Getting the integration project embedded inside the analysis tool model.
        createAnalysisToolIntegrationProject();

        if (_mappingsXml != null)
            mappingManager.addMappings(_mappingsXml);
    }

    protected void createClientProjectId()
    {
        try
        {
            Vector v = FileSystemDbFunctions.getProjectInsideAnalysisTool(_runtimeId.getModelStaticId());
            _clientProjectId = new CompoundId();
            _clientProjectId.setPlayspaceRuntimeId(_runtimeId.getPlayspaceRuntimeId());
            _clientProjectId.addProjectStaticId((String) v.get(0));
            _clientProjectId.addProjectRuntimeId(UUIDGenerator.create());
        }
        catch (XmlRpcException e)
        {
            e.printStackTrace();
        }

    }


    protected void createClientProject()
    {
        try
        {
            _clientProject = RuntimeFunctionsServer.createProjectInPlayspace(_clientProjectId);
        }
        catch(XmlRpcException e)
        {
            e.printStackTrace();
        }

    }

    private void createAnalysisToolIntegrationProject()
    {
        // Getting integration project from server.
        try
        {
            Vector v = FileSystemDbFunctions.getProjectInsideAnalysisTool(_runtimeId.getModelStaticId());

            // v: (i) project deploy id (ii) xml content
            CompoundId projectId = new CompoundId();
            projectId.addProjectStaticId((String)v.get(0));

            _toolProject = RuntimeFunctionsServer.createProjectInPlayspace(projectId);
	        _toolProject.setName("toolProject:"+_toolProject.getName());
        }
        catch (XmlRpcException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

	public String getRunStatus()
	{
		return this.runStatus;
	}

	private void setRunStatus(String newRunStatus)
	{
		String oldRunStatus = this.runStatus;
		this.runStatus = newRunStatus;
		firePropertyChange(ModelRuntime.RUN_STATUS, oldRunStatus, this.runStatus);
	}

    class SolverStatusListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (AnalysisToolExecutionManager.SOLVING_STARTED.equals(evt.getNewValue()))
				setRunStatus(STATUS_RUNNING);
			else if (AnalysisToolExecutionManager.SOLVING_PAUSED.equals(evt.getNewValue()))
				setRunStatus(STATUS_PAUSED);
			else if (AnalysisToolExecutionManager.SOLVING_STOPPED.equals(evt.getNewValue()))
            {
                stopModel();
				setRunStatus(ModelRuntime.STATUS_DONE);
            }
		}
	}

    public Object getItem(String objectId, String methodName, List args)
    {
        return null;
    }

	public void startModel ()
	{
        if (getIntegrationProject() == null) {
            setRunStatus(STATUS_LOADING_PROJECT);
            loadProjectAndMappings();
        }
        _solver.startSolving();
	}

    public void pauseModel(){}

    public void resumeModel(){}

    public void startModelAndWait(){}

    public Collection addItemsToFilterListener(DListListener l)
    {
        return null;
    }

    public void addSubscriptionsListener(DListListener l)
    {
    }

    public void addViewListener(String viewName, DListListener l)
    {
    }

    public Context getBuildContext()
    {
        return null;
    }

    public Filter getFilter(String filterName)
    {
        return null;
    }

    public Collection getModelInterfaces()
    {
        return null;
    }

    public List getSubscriptions()
    {
        return null;
    }

    public List getView(String viewName)
    {
        return null;
    }

    public List getViewNames()
    {
        return null;
    }

    public boolean isIntegrationModel()
    {
        return false;
    }

    public Collection removeItemsToFilterListener(DListListener l)
    {
        return null;
    }

    public void removeSubscriptionsListener(DListListener l)
    {
    }

    public void removeViewListener(String viewName, DListListener l)
    {
    }

    public void sendIndividualToClient(Vector v)
    {
        Collection analysisToolInterfaces = getAnalysisToolInterfaces();
        Iterator iterator = analysisToolInterfaces.iterator();
        while (iterator.hasNext())
        {
            OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) iterator.next();
            String analysisToolInterafaceCompoundId = iface.getRuntimeId().toString();
            String analysisToolInterfaceRuntimeId = iface.getRuntimeId().getInterfaceRuntimeId();
            Collection clientIdList = DomeServer.getResourceClients(analysisToolInterfaceRuntimeId);
            for (Iterator clientIter = clientIdList.iterator(); clientIter.hasNext();)
            {
                String clientId = (String) clientIter.next();
                MessageFunctions.sendIndividualToClient(clientId, Vectors.create(analysisToolInterafaceCompoundId, v));
            }
        }
    }

    public void preparePlotForNextGeneration()
    {
        Collection analysisToolInterfaces = getAnalysisToolInterfaces();
        Iterator iterator = analysisToolInterfaces.iterator();
        while (iterator.hasNext())
        {
            OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) iterator.next();
            String analysisToolInterfaceCompoundId = iface.getRuntimeId().toString();
            String analysisToolInterfaceRuntimeId = iface.getRuntimeId().getInterfaceRuntimeId();
            Collection clientIdList = DomeServer.getResourceClients(analysisToolInterfaceRuntimeId);
            for (Iterator clientIterator = clientIdList.iterator(); clientIterator.hasNext(); )
            {
                String clientId = (String) clientIterator.next();
                MessageFunctions.preparePlotForNextGeneration(clientId, Vectors.create(analysisToolInterfaceCompoundId));
            }
        }
    }

    public void signalClientOptimizationAnalysisIsComplete()
    {
        Collection analysisToolInterfaces = getAnalysisToolInterfaces();
        Iterator iterator = analysisToolInterfaces.iterator();
        while (iterator.hasNext())
        {
            OptimizationToolInterfaceRuntimeServer iface = (OptimizationToolInterfaceRuntimeServer) iterator.next();
            String analysisToolInterfaceCompoundId = iface.getRuntimeId().toString();
            String analysisToolInterfaceRuntimeId = iface.getRuntimeId().getInterfaceRuntimeId();
            Collection clientIdList = DomeServer.getResourceClients(analysisToolInterfaceRuntimeId);
            for (Iterator clientIterator = clientIdList.iterator(); clientIterator.hasNext(); )
            {
                String clientId = (String) clientIterator.next();
                MessageFunctions.optimizationAnalysisIsComplete(clientId, Vectors.create(analysisToolInterfaceCompoundId));
            }
        }
    }

    /**
     * This method sets the optimization model parameters to
     * match the interface configuration parameters passed in
     * from the client.
     * @param iface
     */
    public void setConfigurationFromInterface(OptimizationToolInterfaceRuntimeServer iface)
    {
        _iface = iface;
        plg.importInterfaceConfiguration(((DomeInteger) iface.getInterfaceConfiguration().getSetupParameter
                (OptimizationInterfaceConfiguration.SOLUTION_UPDATE_INTERVAL).getCurrentDataObject()).getIntegerValue());
    }

    public void executeNativeAnalysisTool()
	{
		if (!plg.isModelLoaded())  plg.loadModel();
        plg.getDomeObjectiveForQMOO().sortModelInterfaceParameters();
        plg.execute();
	}

	/**
	 * Halt the native model.
	 */
	public void stopModel()
	{
		//plg.stopExecution();
        plg.unloadModel();
	}

	public void deleteModel()
	{
		stopModel ();
		plg.deleteModel();
		if (_clientProject != null)
			_clientProject.killProject();
		if (_toolProject != null)
			((IntegrationProjectServerRuntime)_toolProject).killProject();
		firePropertyChange(MODEL_KILLED);
		Debug.trace(Debug.ALL, "deleting qmoo model " + getName());
	}

    public Element toXmlElement()
    {
        // creates the model element
        Element xml = headerToXmlElement();
        xml.addAttribute("toolType", getToolXmlType());

        // creates the modelinfo element
        xml.add(createModelInfoElement());

        if (!doc.isEmpty())
            xml.add(doc.toXmlElement());

        return xml;
    }

	/**
	 * loads project if it does not exist
	 * @return Vector of <project xml, idMap>
	 */
	public Vector getProjectInfo() {
		if (_clientProject == null)
			createClientProject();
		return Vectors.create(_clientProject.toXmlElement().asXML(), _clientProject.getResourceIdMap());
	}

    public IntegrationProjectServerRuntime getProjectForClient()
    {
        return _clientProject;
    }

    public CompoundId getClientProjectId() {
        return _clientProjectId;
    }

}