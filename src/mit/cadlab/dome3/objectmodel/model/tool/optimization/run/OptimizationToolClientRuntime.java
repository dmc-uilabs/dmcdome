package mit.cadlab.dome3.objectmodel.model.tool.optimization.run;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.RuntimeObjectInfo;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.OptimizationToolBase;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import org.dom4j.Element;

import java.util.Hashtable;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 25, 2004
 * Time: 3:13:25 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationToolClientRuntime extends OptimizationToolBase
{
    private CompoundId _analysisToolId;
    protected ServerConnection _serverConnection = null;
    private ClientPlayspaceRuntime _parentPlayspace = null;
    private String status = "";
    private int referenceCount = 0;
    private Object resourceLock = new Object();

    private Object[] _projectInfo;

    public static final String ANALYSIS_TOOL_STATUS = "analysisToolStatus";
    public final static String PROPERTY_LEAVE = "propertyLeave";    // playspace closed/client wants to leave
    public final static String PROPERTY_IDLE = "propertyIdle";      // no active interfaces/projects
    public final static String PROPERTY_CLOSED = "propertyClosed";

    public OptimizationToolClientRuntime(CompoundId analysisToolId, ServerConnection serverConn,
                                         Element xmlElement, CompoundId projectId, String projectName, String projectDescription,
                                         ClientPlayspaceRuntime playspace)
    {
        super(xmlElement);
        _analysisToolId = new CompoundId(analysisToolId);
        _serverConnection = serverConn;
        _parentPlayspace = playspace;
        if (_serverConnection != null)
            _serverConnection.addReference();
        if (serverConn != null)
            serverConn.addReference();
        _projectInfo = new Object[]{projectId, projectName, projectDescription};
        //createProjectClientRuntimeObject(projectId, projectName, projectDescription);
    }

    protected void createProjectClientRuntimeObject(CompoundId projectId, Element projectXmlElement, Hashtable idMap)
    {
        _toolProject = new IntegrationProjectClientRuntime(projectId, _serverConnection, projectXmlElement, idMap, _parentPlayspace);
    }

    /**
     * @return Object[]{projectId, name, description}
     */
    public Object[] getProjectInfo() {
        return _projectInfo;
    }

    protected void loadXml(Element xmlElement)
    {
        initModel();
    }

    public CompoundId getRuntimeId()
    {
        return _analysisToolId;
    }

    protected AnalysisToolInterfaceManager createAnalysisToolInterfacesManager()
    {
        return null;
    }

    protected ConnectionMappingManager createConnectionMappingManager()
    {
        return null;
    }

    public void incrementReferenceCount ()
    {
        referenceCount++;
    }
}
