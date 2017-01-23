package mit.cadlab.dome3.network.client.objectrecord;

import mit.cadlab.dome3.network.CompoundId;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.fileSystem.Folder;

import java.util.Vector;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 26, 2004
 * Time: 3:36:39 PM
 * To change this template use Options | File Templates.
 */
public class ClientAnalysisToolRecord extends ClientObjectRecord
{
    private ClientPlayspaceRuntime _playspaceRef = null;
    private boolean _contentAdded;
    private OptimizationToolClientRuntime _analysisTool;
	private ServerConnection svrConn = null;

    // for run
	public ClientAnalysisToolRecord(CompoundId id, String name, String description,
	                           String url, OptimizationToolClientRuntime analysisTool,
	                           ClientPlayspaceRuntime playspace)
	{
		super(id, name, description, url);
        _analysisTool = analysisTool;
		_playspaceRef = playspace;
	}

    public void listChildren()
	{
		if (!_contentAdded)
        {
			if (svrConn == null) {
                ServerConnection playspaceConn = _playspaceRef.getServerConnection();
                svrConn = LoginUtils.compareServersAndGetConnection(playspaceConn, url);
			}
            addAnalysisToolAvailableContent();
            _contentAdded = true;
        }
	}

    public String getStaticId ()
	{
		return compoundId.getModelStaticId();  //todo:  is this correct?? check with Jacob
	}

	public ServerConnection getServerConnection() {
		return svrConn;
	}

    private void addAnalysisToolAvailableContent()
    {
        Vector v1 = FileSystemFunctions.getAvailableInterfacesInfo(svrConn,
                compoundId.getModelStaticId(),
                PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
        Vector v2 = FileSystemFunctions.getProjectInsideAnalysisTool(svrConn, compoundId.getModelStaticId());

        String projectName, projectDescription;
        BrowseModelFolder iFolder, pFolder;

        if (!v1.isEmpty())
        {

            iFolder = new BrowseModelFolder(Folder.ANALYSIS_TOOL_CONTENT_FOLDER, new Integer(0), "Interfaces");
            if (v1 != null)
            {
                for (int i = 0; i < v1.size(); i++)
                {
                    Vector analysisToolIfaceVector = (Vector) v1.get(i);
                    String ifaceName = (String) analysisToolIfaceVector.get(0);
                    String ifaceid = (String) analysisToolIfaceVector.get(1);
                    String ifaceDesc = (String) analysisToolIfaceVector.get(2);
                    CompoundId cid = new CompoundId(compoundId);
                    cid.setInterfaceStaticId(ifaceid);
                    ClientAnalysisToolInterfaceRecord iface = new ClientAnalysisToolInterfaceRecord(svrConn, cid, ifaceName,
                            ifaceDesc, url, _playspaceRef);
                    iFolder.getContent().add(iface);
                }
                content.add(iFolder);
            }


        }
        if (!v2.isEmpty())
        {
            pFolder = new BrowseModelFolder(Folder.ANALYSIS_TOOL_CONTENT_FOLDER, new Integer(0), "Project");
            content.add(pFolder);
            Object[] projectInfo = _analysisTool.getProjectInfo();
            CompoundId projectId = (CompoundId) projectInfo[0];
            projectName = (String) projectInfo[1];
            projectDescription = (String) projectInfo[2];
            ClientProjectRecord project = new ClientProjectRecord(projectId, projectName, projectDescription, url, _playspaceRef, this);
            pFolder.getContent().add(project);
        }
    }
}
