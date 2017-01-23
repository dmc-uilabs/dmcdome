package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.deployProject.DeployProject;
import mit.cadlab.dome3.gui.deploy.deployProject.DeployProjectGui;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.util.FileUtils;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Sep 30, 2003
 * Time: 8:49:24 PM
 * To change this template use Options | File Templates.
 */
public class DeployAnalysisTool
{
    private ServerConnection _serverConnection;

    private boolean _isNewDeployment = true;
	private boolean _availableToolInterfacesChanged = false;

    private String _toolDescription;
	private String _serverLocationPath = ""; //location on the server
	private String _localToolPath;
    private String _redeployAnalysisToolId = null;

    private DeployToolGui _gui;
    private DeployAnalysisToolData _toolData = null;

	private Hashtable _analysisToolInterfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel: keyed on interface list index

    private Object _locationFolderId = null;

    private DeployProject _deployProject;

    private LoginCard _loginCard;
    private SelectAnalysisToolCard _selectToolCard;
    private LocateCard _locateCard;
    private EditToolPrivilagesCard _editToolPrivilagesCard;
    private SelectToolInterfacesCard _selectToolInterfaceCard;
    private SetToolInterfacePrivilegesCard _setToolInterfacePrivilegesCard;
    private ConfirmAnalysisToolCard _confirmCard;

    private PermissionsPanel _editPermissionsPanel = null;

    public DeployAnalysisTool()
    {
        _deployProject = new DeployProject(null);

        _gui = new DeployToolGui(this);

        _deployProject.setDeployProjectGui(_gui);
    }

    public DeployToolGui getGui()
    {
        return _gui;
    }

    public void setLoginCard(LoginCard loginCard)
    {
        _loginCard = loginCard;
    }

    public void setLocalToolPath(String localToolPath)
	{
		_localToolPath = localToolPath;
	}

    public String getServerLocationPath()
    {
        return _serverLocationPath;
    }

    public void deployData()
    {
        //now create the data structures that the server expects
        Vector analysisToolInfo = null;
        Vector projectInfo = null;
        String projectId = null;

        if (_editPermissionsPanel != null)
            _toolData.setEditPermissions(_editPermissionsPanel.getSetPermissions());

        //get the permissions for each analysis tool interface and add the permissions result vector to analysisToolData
		if (!_analysisToolInterfacePermissions.isEmpty()) { //if user has permission to set inteface use permissions
			for (int i = 0; i < (_toolData.getToolInterfaces().size()); i++) {
				((DeployAnalysisToolInterfaceData) (_toolData.getToolInterfaces().get(i))).setPermissions(
				        ((PermissionsPanel) (_analysisToolInterfacePermissions.get(Integer.toString(i)))).getSetPermissions());
			}
		}

        if (_isNewDeployment == true)
            analysisToolInfo = DeployUtilities.createNewToolInfoVector((Integer) _locationFolderId,
                    _toolDescription, _toolData.getXmlContent(), _toolData.getEditPermissions());
        else
            analysisToolInfo = DeployUtilities.createUpdateAnalysisToolInfoVector(_redeployAnalysisToolId,
                    _toolDescription, _toolData.getXmlContent(), _toolData.getEditPermissions());


        Vector interfaceInfoVector = DeployUtilities.createDeployToolInterfaceInfoVector(_toolData.getToolInterfaces());

        int option = 1;
        Vector customGuiFileInfos = DeployUtilities.loadCustomGuiFilesForDeploy(_toolData.getXmlContent(), interfaceInfoVector, option);
        Vector customGuiFileInfosForUploadUse = new Vector();

        if (customGuiFileInfos.size() != 0) {
        //append customGuiFileInfo into auxFileInfos
            for (Iterator i = customGuiFileInfos.iterator(); i.hasNext();) {
                Vector aCustomGuiFileInfo = (Vector) i.next();
                String interface_build_Id = (String) aCustomGuiFileInfo.get(0);
                CustomGuiInfo cf = (CustomGuiInfo) aCustomGuiFileInfo.get(1);
                String filepath = cf.getJarFilePath();

                if (filepath == null) {//still can't find
                    System.out.println("DeployAnalysisTool Error: can't find location of customGUIfile");
                } else {
                    File cfile = new File(filepath);
                    byte[] fileContent = null;
                    try {
                        fileContent = FileUtils.readBinaryFileAsByteArray(filepath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    customGuiFileInfosForUploadUse.add(Vectors.create(cf.getJarFileId().toString(), cfile.getName(), filepath, interface_build_Id, fileContent));
                }
            }
        }

        try
        {
            Vector verInfo = null;
            if (_isNewDeployment == true)
            {
                verInfo = DeployFilesFunctions.deployNewTool(_serverConnection, analysisToolInfo, interfaceInfoVector);
                Vector info = (Vector) verInfo.get(0);
                String analysisToolId = (String) info.get(0);

                /**
                 * since the project will be deployed as part of the analysis tool
                 * we set the project deploy folder ID to -1 (null folder id)
                 */
                Vector interfaceInfo = (Vector) verInfo.get(1);
                if (customGuiFileInfosForUploadUse != null && customGuiFileInfosForUploadUse.size() != 0)
                {
                    for (int i = 0; i < customGuiFileInfosForUploadUse.size(); i++)
                    {
                        Vector one_info = (Vector) customGuiFileInfosForUploadUse.get(i);
                        String iface_build_id = (String) one_info.get(3);
                        String iface_deploy_id = DeployUtilities.getIfaceDeployId(interfaceInfo, iface_build_id);
                        if (iface_deploy_id != null)
                            one_info.set(3, iface_deploy_id);
                    }
                    DeployFilesFunctions.uploadCustomGuiFiles(_serverConnection, analysisToolId, customGuiFileInfosForUploadUse);
                }

                DeployUtilities.writeAnalysisToolVersionFile(_localToolPath, verInfo, _serverConnection);

                _deployProject.setLocationFolderId(DbConstants.NULL_FOLDER_ID);
                projectId = _deployProject.deployData(true);
                if(projectId != null)
                {
                    DeployFilesFunctions.insertAnalysisToolProjectId(_serverConnection, analysisToolId, projectId);
                    OneButton1Msg.show(_gui, "option", "Deployment status",
                            "Deployment to the server was sucessful!", "ok", new Dimension(180, 80));
                }
            }
            else
            {
                verInfo = DeployFilesFunctions.redeployAnalysisTool(_serverConnection, analysisToolInfo, interfaceInfoVector);
                Vector info = (Vector) verInfo.get(0);

                Vector interfaceInfo = (Vector) verInfo.get(1);
                if (customGuiFileInfosForUploadUse != null && customGuiFileInfosForUploadUse.size() != 0) {
                    for (int i = 0; i < customGuiFileInfosForUploadUse.size(); i++) {
                        Vector one_info = (Vector) customGuiFileInfosForUploadUse.get(i);
                        String iface_build_id = (String) one_info.get(3);
                        String iface_deploy_id = DeployUtilities.getIfaceDeployId(interfaceInfo, iface_build_id);
                        if (iface_deploy_id != null)
                            one_info.set(3, iface_deploy_id);
                    }
                    DeployFilesFunctions.uploadCustomGuiFiles(_serverConnection, projectId, customGuiFileInfosForUploadUse);
                }

                DeployUtilities.writeAnalysisToolVersionFile(_localToolPath, verInfo, _serverConnection);

                _deployProject.setLocationFolderId(DbConstants.NULL_FOLDER_ID);
                projectId = _deployProject.deployData(true);
                if (projectId != null)
                {
                    DeployFilesFunctions.insertAnalysisToolProjectId(_serverConnection, _redeployAnalysisToolId, projectId);
                    OneButton1Msg.show(_gui, "option", "deployment status", "re-deployment to the server was successful!", "ok", new Dimension(180, 80));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OneButton1Msg.show(_gui, "error", "Deployment status",
                    "There was an error deploying the model\n" + e.getMessage().trim(),
                    "sorry", new Dimension(250, 100));
        }
    }

    /**
	 * Used to get the server connection info
	 * @return the current server connection info
	 */
	public ServerConnection getServerConnection()
	{
		return _serverConnection;
	}

	/**
	 * Used to set the server connection info
	 * @param serverConnection The new server connection
	 */
	public void setServerConnection(ServerConnection serverConnection)
	{
		_serverConnection = serverConnection;
	}

    public void initDeployData()
	{
		//server connection is not initialized!
		_isNewDeployment = true;
		_serverLocationPath = "";
		_availableToolInterfacesChanged = false;
		_analysisToolInterfacePermissions.clear();
	}

    public boolean isNewDeployment()
	{
		return _isNewDeployment;
	}

    public void setNewDeployment(boolean newDeployment)
	{
		_isNewDeployment = newDeployment;
	}

    public String getLocalAnalysisToolPath()
    {
        return _localToolPath;
    }

    public SelectAnalysisToolCard getSelectToolCard()
	{
		return _selectToolCard;
	}

    public LocateCard getLocateCard()
    {
        return _locateCard;
    }

    public String getDescription()
    {
        return _toolDescription;
    }

    public void setDescription(String description)
	{
		_toolDescription = description;
	}

    public DeployAnalysisToolData getToolData()
	{
		return _toolData;
	}

    public void setSelectToolCard(SelectAnalysisToolCard selectProjectCard)
    {
        _selectToolCard = selectProjectCard;
    }

    public void setToolData(DeployAnalysisToolData toolData)
    {
        _toolData = toolData;
    }

    public String getRedeployAnalysisToolId()
    {
        return _redeployAnalysisToolId;
    }

    public void setRedeployAnalysisToolId(String redeployAnalysisToolId)
    {
        _redeployAnalysisToolId = redeployAnalysisToolId;
    }

    public DeployProject getDeployProject()
    {
        return _deployProject;
    }

    public void setDeployProject(DeployProject deployProject)
    {
        _deployProject = deployProject;
    }

    public void setLocateCard(LocateCard locateCard)
	{
		_locateCard = locateCard;
	}

    public void setServerLocationPath(String locationPath)
	{
		_serverLocationPath = locationPath;
	}

    public void setLocationFolderId(Object locationFolderId)
	{
		_locationFolderId = locationFolderId;
	}

    public void setEditPrivilagesCard(EditToolPrivilagesCard editPrivCard)
	{
		_editToolPrivilagesCard = editPrivCard;
	}

    public EditToolPrivilagesCard getEditPrivilagesCard()
	{
		return _editToolPrivilagesCard;
	}

    public SelectToolInterfacesCard getSelectToolInterfaceCard()
    {
        return _selectToolInterfaceCard;
    }

    public void setSelectToolInterfaceCard(SelectToolInterfacesCard toolInterfaceCard)
    {
        _selectToolInterfaceCard = toolInterfaceCard;
    }

    public SetToolInterfacePrivilegesCard getToolInterfaceUsePrivilegesCard()
	{
		return _setToolInterfacePrivilegesCard;
	}

	public void setToolInterfaceUsePrivilegesCard(SetToolInterfacePrivilegesCard interfaceUsePrivCard)
	{
		_setToolInterfacePrivilegesCard = interfaceUsePrivCard;
	}

    public void setEditPermissionsPanel(PermissionsPanel editPermissionsPanel)
	{
		_editPermissionsPanel = editPermissionsPanel;
	}

    public void setAvailableToolInterfacesChanged(boolean availableToolInterfacesChanged)
	{
		_availableToolInterfacesChanged = availableToolInterfacesChanged;
	}

    public ConfirmAnalysisToolCard getConfirmCard()
    {
        return _confirmCard;
    }

    public void setConfirmCard(ConfirmAnalysisToolCard confirmCard)
	{
		_confirmCard = confirmCard;
	}

    public Hashtable getAnalysisToolInterfacePermissions()
    {
        return _analysisToolInterfacePermissions;
    }

}
