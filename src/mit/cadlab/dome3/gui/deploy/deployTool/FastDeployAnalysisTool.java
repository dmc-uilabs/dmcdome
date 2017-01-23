package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.*;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.UserGroupInfo;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.*;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.FileUtils;

import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jun 3, 2004
 * Time: 7:14:06 PM
 * To change this template use Options | File Templates.
 */
public class FastDeployAnalysisTool
{
    public static final String DEFAULT_FOLDER = "Prototype";
    public static final String DEPLOY_DESCRIPTION = "Fast Deploy";
    public static final String ANALYSIS_TOOL_EDIT_PRIVILEGES = PermissionUtils.ANALYSIS_TOOL_EDIT_PRIVILEGES;
    public static final String PROJECT_EDIT_PRIVILEGES = PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES;
    public static final String INTERFACE_USE_PRIVILEGES = PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES;
    public static final String PROJECT_VISIBILITY_PRIVILEGES = PermissionUtils.PROJECT_VISIBILITY_PRIVILEGES;

    private static final int NULL = -1;

    private ServerConnection _svrConnection;

    private DeployAnalysisToolData _analysisToolDeployData = null;
    private DeployProjectData _projectDataInsideAnalysisTool = null;

    private Integer _defaultFolderId;
    private Integer _projectFolderId;
    private Integer _privateFolderId;

    private boolean _isNewDeployment;

    public FastDeployAnalysisTool(String analysisToolFileName)
    {
        _analysisToolDeployData = new DeployAnalysisToolData(analysisToolFileName);
        _analysisToolDeployData.setDescription(DEPLOY_DESCRIPTION);
        _projectDataInsideAnalysisTool = new DeployProjectData(_analysisToolDeployData.getToolProjectPath());
        _defaultFolderId = DbConstants.NULL_FOLDER_ID;
        _projectFolderId = DbConstants.NULL_FOLDER_ID;
    }

    public void fastDeploy()
    {
        Vector analysisToolInfo = null;

        loginUser();

        if (!hasDefaultFolderBeenCreated())
            createDefaultFolder();

        _analysisToolDeployData.setEditPermissions(getPermissionsVector(ANALYSIS_TOOL_EDIT_PRIVILEGES));

        setAllInterfacesToAvailable();

        for (Iterator iter = _analysisToolDeployData.getToolInterfaces().iterator(); iter.hasNext();)
        {
            DeployAnalysisToolInterfaceData analysisToolInterface = (DeployAnalysisToolInterfaceData) iter.next();
            analysisToolInterface.setPermissions(getPermissionsVector(INTERFACE_USE_PRIVILEGES));
        }

        if (isNewDeployment())
            analysisToolInfo = DeployUtilities.createNewToolInfoVector((Integer) _defaultFolderId,
                    DEPLOY_DESCRIPTION, _analysisToolDeployData.getXmlContent(), _analysisToolDeployData.getEditPermissions());

        Vector interfaceInfoVector = DeployUtilities.createDeployToolInterfaceInfoVector(_analysisToolDeployData.getToolInterfaces());

        int option = 1;
        Vector customGuiFileInfos = DeployUtilities.loadCustomGuiFilesForDeploy(_analysisToolDeployData.getXmlContent(), interfaceInfoVector, option);
        Vector customGuiFileInfosForUploadUse = new Vector();

        if (customGuiFileInfos.size() != 0)
        {
            //append customGuiFileInfo into auxFileInfos
            for (Iterator i = customGuiFileInfos.iterator(); i.hasNext();)
            {
                Vector aCustomGuiFileInfo = (Vector) i.next();
                String interface_build_Id = (String) aCustomGuiFileInfo.get(0);
                CustomGuiInfo cf = (CustomGuiInfo) aCustomGuiFileInfo.get(1);
                String filepath = cf.getJarFilePath();

                if (filepath == null)
                {//still can't find
                    System.out.println("DeployAnalysisTool Error: can't find location of customGUIfile");
                }
                else
                {
                    File cfile = new File(filepath);
                    byte[] fileContent = null;
                    try
                    {
                        fileContent = FileUtils.readBinaryFileAsByteArray(filepath);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    customGuiFileInfosForUploadUse.add(Vectors.create(cf.getJarFileId().toString(), cfile.getName(), filepath, interface_build_Id, fileContent));
                }
            }
        }

        try
        {
            Vector verInfo = null;
            if (isNewDeployment())
            {
                verInfo = DeployFilesFunctions.deployNewTool(_svrConnection, analysisToolInfo, interfaceInfoVector);
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
                    DeployFilesFunctions.uploadCustomGuiFiles(_svrConnection, analysisToolId, customGuiFileInfosForUploadUse);
                }

                DeployUtilities.writeAnalysisToolVersionFile(_analysisToolDeployData.getAnalysisToolFileName(), verInfo, _svrConnection);

                _projectDataInsideAnalysisTool.setEditPermissions(getPermissionsVector(PROJECT_EDIT_PRIVILEGES));

                _projectDataInsideAnalysisTool.setContentVisibilityPermissions(getPermissionsVector(PROJECT_VISIBILITY_PRIVILEGES));

                for (Iterator iter = _projectDataInsideAnalysisTool.getInterfaces().iterator(); iter.hasNext();)
                {
                    DeployProjectInterfaceData projectInterface = (DeployProjectInterfaceData) iter.next();
                    projectInterface.setPermissions(getPermissionsVector(INTERFACE_USE_PRIVILEGES));
                }

                List iModels = _projectDataInsideAnalysisTool.getIntegrationModels();
                DeployModelData[] modelData = (DeployModelData[]) (iModels.toArray(new DeployModelData[]{}));
                DeployInterfaceData[] interfaceData;
                List interfaces;
                for (int i = 0; i < iModels.size(); i++)
                {
                    if ((interfaces = modelData[i].getModelInterfaces()).size() != 0)
                    {
                        interfaceData = (DeployInterfaceData[]) (interfaces.toArray(new DeployInterfaceData[]{}));
                        for (int j = 0; j < interfaceData.length; j++)
                        {
                            interfaceData[j].setPermissions(getPermissionsVector(INTERFACE_USE_PRIVILEGES));
                        }
                    }
                }

                Vector[] preparedData = DeployUtilities.prepareDeployProjectData(_projectFolderId, _projectDataInsideAnalysisTool);

                Vector projectInterfaceInfoVector = preparedData[1];

                int projectOption = 2;//for project
                Vector projectCustomGuiFileInfos = DeployUtilities.loadCustomGuiFilesForDeploy(
                        _projectDataInsideAnalysisTool.getXmlContent(), projectInterfaceInfoVector, projectOption);
                Vector customGuiFileInfos_for_upload_use = new Vector();

                if (projectCustomGuiFileInfos.size() != 0) {}

                Vector projectVersionInfo = DeployFilesFunctions.deployNewProject(_svrConnection, preparedData[0], preparedData[1], preparedData[2]);
                Vector projectInfo = (Vector) projectVersionInfo.get(0);
                String projectId = (String) projectInfo.get(0);

                Vector iface_info = (Vector) projectVersionInfo.get(1);

                if (customGuiFileInfos_for_upload_use != null && customGuiFileInfos_for_upload_use.size() != 0)
                {
                    for (int i = 0; i < customGuiFileInfos_for_upload_use.size(); i++)
                    {
                        Vector one_info = (Vector) customGuiFileInfos_for_upload_use.get(i);
                        String iface_build_id = (String) one_info.get(3);
                        String iface_deploy_id = DeployUtilities.getIfaceDeployId(iface_info, iface_build_id);
                        if (iface_deploy_id != null)
                            one_info.set(3, iface_deploy_id);
                    }
                    DeployFilesFunctions.uploadCustomGuiFiles(_svrConnection, projectId, customGuiFileInfos_for_upload_use);
                }

                DeployUtilities.writeProjectVersionFile(_analysisToolDeployData.getToolProjectPath(), projectVersionInfo, _svrConnection);

                projectId = (String) ((Vector) projectVersionInfo.get(0)).get(0);

                if (projectId != null)
                {
                    DeployFilesFunctions.insertAnalysisToolProjectId(_svrConnection, analysisToolId, projectId);
                    OneButton1Msg.show(null, "Deploy Mode", "Deployment status",
                            "Fast deployment to the server was sucessful!", "ok", new Dimension(180, 80));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OneButton1Msg.show(null, "Deploy Mode: Error", "Deployment Status",
                    "There was an error deploying the analysis tool\n" + e.getMessage().trim(),
                    "Cancel", new Dimension(250, 100));
        }
    }

    private void createDefaultFolder()
    {
        _defaultFolderId = new Integer(FileSystemFunctions.createModelFolder(_svrConnection, _privateFolderId.intValue(), DEFAULT_FOLDER));
    }

    private void loginUser()
    {
        _svrConnection = LoginPrompt.showDialog(null);
    }

    private Vector getPermissionsVector(String permissionCategory)
    {
        Vector permissionsVector = new Vector();
        Vector analysisToolEditPrivileges = PermissionUtils.getCategoryInfo(_svrConnection, permissionCategory);
        Vector allUserGroup = UserGroupFunctions.getSimpleActiveUsersAndGroupsList(_svrConnection);
        DArrayList remainingUsersAndGroups = new DArrayList(PermissionUtils.convertToUserGroupInfo((Vector) analysisToolEditPrivileges.get(0), allUserGroup));
        String userName = _svrConnection.getLoginName();
        UserGroupInfo userInfo = null;
        for (int i = 0; i < remainingUsersAndGroups.size(); i++)
        {
            UserGroupInfo tempInfo = (UserGroupInfo) remainingUsersAndGroups.get(i);
            if (tempInfo.getName().equals(userName))
            {
                userInfo = tempInfo;
                break;
            }
        }
        if (userInfo != null)
        {
            remainingUsersAndGroups.remove(userInfo);
            boolean[] oldPermissions = userInfo.getPermissions();
            for (int j = 0; j < oldPermissions.length; j++)
            {
                oldPermissions[j] = true;
            }
        }
        for (int j = 0; j < userInfo.getPermissionID().length; j++)
        {
            if (userInfo.getPermissions()[j] == true)
            {
                Vector temp = new Vector();
                temp.addElement(new Integer(userInfo.getId()));
                temp.addElement(new Integer(userInfo.getPermissionID()[j]));
                permissionsVector.addElement(temp);
            }
        }

        return permissionsVector;
    }

    private void setAllInterfacesToAvailable()
    {
        for (Iterator iter = _analysisToolDeployData.getToolInterfaces().iterator(); iter.hasNext();)
        {
            DeployAnalysisToolInterfaceData iface = (DeployAnalysisToolInterfaceData) iter.next();
            iface.setIsAvailable(new Boolean(true));
        }
    }

    private boolean hasDefaultFolderBeenCreated()
    {
        Vector v = null;
        if (_svrConnection != null)
        {
            if (_svrConnection.getLoginType().equals(LoginUtils.ADMIN))
                v = DeployFileSystemFunctions.getServerModelSpaceNoMembershipCheck(_svrConnection);
            else if (_svrConnection.getLoginType().equals(LoginUtils.USER))
                v = DeployFileSystemFunctions.getUserModelSpace(_svrConnection);
            else
                return false;
        }

        if (v != null)
            _privateFolderId = (Integer) v.get(1);

        v = FileSystemFunctions.hasAnalysisToolPrototypeFolderBeenCreated(_svrConnection, _privateFolderId, DEFAULT_FOLDER);

        return ((Boolean)v.get(0)).booleanValue();
    }

    public boolean isNewDeployment()
    {
        return true;
    }
}
