package mit.cadlab.dome3.api;

import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.gui.permission.PermissionUtils;

import java.util.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Mar 28, 2005
 * Time: 12:27:48 PM
 * To change this template use Options | File Templates.
 */

/**
 * DomeAnalysisTool is a static representation of the analysis tools deployed on the server.
 */

public class DomeAnalysisTool implements DomeSimulation
{
    public static final String TYPE = "ANALYSIS_TOOL";

    private String _analysisToolId, _analysisToolName, _analysisToolDescription;
    private Date _lastModified;
    private int _version;
    private DomeConnection _domeConnection;
    private List _interfaceList = new ArrayList();
    private RuntimePlayspace _deployedPlayspace;
    private DomeProject _projectInsideAnalysisTool;

    public DomeAnalysisTool(String analysisToolId, String analysisToolName, String analysisToolDescription,
                            Date lastModified, int version, RuntimePlayspace deployedPlayspace, DomeConnection domeConn)
    {
        _analysisToolId = analysisToolId;
        _analysisToolName = analysisToolName;
        _analysisToolDescription = analysisToolDescription;
        _lastModified = lastModified;
        _version = version;
        _deployedPlayspace = deployedPlayspace;
        _domeConnection = domeConn;
        initInterfaceList();

    }

    private void initInterfaceList()
    {
        Vector interfaceVector = FileSystemFunctions.getAvailableInterfacesInfo(_domeConnection.getServerConnection(), _analysisToolId, PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
        for (int i = 0; i < interfaceVector.size(); i++)
        {
            Vector anInterface = (Vector)interfaceVector.get(i);
            String interfaceName = (String) anInterface.get(0);
            String interfaceId = (String) anInterface.get(1);
            String description = (String) anInterface.get(2);
            int version = ((Integer)anInterface.get(3)).intValue();
            Date lastModified = (Date) anInterface.get(4);
            _interfaceList.add(new DomeInterface(interfaceName, interfaceId, description, version, lastModified, this, _domeConnection));
        }
    }

    public RuntimePlayspace getDeployedPlayspace() {
        return _deployedPlayspace;
    }

    /**
     * @return if runtime playspace is null, this method returns true.
     */
    public boolean isInTransientPlayspace() {
        return (_deployedPlayspace == null);
    }

    public String getAnalysisToolId()
    {
        return _analysisToolId;
    }

    public DomeProject getProject()
    {
        Vector projectInfo = FileSystemFunctions.getProjectInsideAnalysisTool(_domeConnection.getServerConnection(), _analysisToolId);

        if (projectInfo != null && !projectInfo.isEmpty())
        {
            String projectId = (String) projectInfo.get(0);
            String projectName = (String) projectInfo.get(1);
            String projectDescription = (String) projectInfo.get(2);

            Vector modifiedVec = FileSystemFunctions.getProjectLastModified(_domeConnection.getServerConnection(), projectId);
            Date lastModified = (Date) modifiedVec.get(0);
            int version = ((Integer) modifiedVec.get(1)).intValue();

            return new DomeProject(projectId, projectName, projectDescription, lastModified, version, false, null, _domeConnection);
        }

        return null;
    }


    /**
     * retrieve the DomeInterface instance with the given name. returns null when there is no match.
     */
    public DomeInterface getInterfaceById(String findingInterfaceId) {
        for (Iterator i = _interfaceList.iterator(); i.hasNext(); ) {
            DomeInterface anInterface = (DomeInterface) i.next();
            if(anInterface.getInterfaceId().equals(findingInterfaceId)) {
                return anInterface;
            }
        }
        return null;
    }

    /**
     * retrieve the DomeInterface instance with the given name. returns null when there is no match.
     */
    public DomeInterface getInterfaceByName(String findingInterfaceName) {
        for (Iterator i = _interfaceList.iterator(); i.hasNext(); ) {
            DomeInterface anInterface = (DomeInterface) i.next();
            if(anInterface.getInterfaceName().equals(findingInterfaceName)) {
                return anInterface;
            }
        }
        return null;
    }

    public String getSimulationId() {
        return _analysisToolId;
    }

    public String getSimulationName() {
        return _analysisToolName;
    }

    public String getDescription() {
        return _analysisToolDescription;
    }

    public Date getLastModified() {
        return _lastModified;
    }

    public int getVersion() {
        return _version;
    }

    public List getInterfaces() {
        return _interfaceList;
    }

    public String getType() {
        return TYPE;
    }
}
