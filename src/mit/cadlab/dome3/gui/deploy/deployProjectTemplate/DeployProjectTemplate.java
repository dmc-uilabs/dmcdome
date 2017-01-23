package mit.cadlab.dome3.gui.deploy.deployProjectTemplate;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.search.framework.utils.processing.DomeXMLHandler;
import mit.cadlab.dome3.search.framework.utils.processing.InterfaceData;
import mit.cadlab.dome3.search.framework.utils.processing.CreateTemplates;
import mit.cadlab.dome3.integrationwizards.templatecreation.IModelToTemplateXMLHandler;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployModelData;
import mit.cadlab.dome3.gui.deploy.components.DeployProjectData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.DeployProjectInterfaceData;
import mit.cadlab.dome3.gui.deploy.deployModel.DeployModelGui;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.client.functions.Vectors;

import javax.swing.JFrame;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Ligon
 * Date: Mar 3, 2003
 * Time: 8:22:07 AM
 * To change this template use Options | File Templates.
 */

/**
 * Data model class for running the deploy playspace wizard
 */
public class DeployProjectTemplate {
    //data needed to run the wizard
    private DeployProjectGui gui;
    private LoginCard loginCard;
    private SelectProjectCard selectProjectCard;
    private LocateCard locateCard;
    private SelectProjectInterfacesCard selectProjectInterfaces;
    private ConfirmCard confirmCard;

    private boolean isNewDeployment = true;
    private String localProjectPath = "";
    private String locationPath = ""; //location on the server
    private DeployProjectData projectData = null;
    private boolean availableProjectInterfacesChanged = false;
    private Hashtable projectInterfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel: keyed on interface list index
    private Hashtable iModelInterfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel keyed on interfaceDeployData object

    //data needed for deployment
    private ServerConnection serverConnection = null;
    private Object locationFolderId = null; // used only if it is a new project deployment so know where to add it
    private String redeployProjectId = null;
    private String description = "";
    private PermissionsPanel editPermissionsPanel = null;
    private PermissionsPanel visibilityPermissionsPanel = null;

    public String deployData(boolean isProjectEmbedded) {

        String projectXMLString = projectData.getXmlContent();

        DeployModelData iModelData = (DeployModelData)projectData.getIntegrationModels().iterator().next();
        String iModelXMLString = iModelData.getXmlContent();
        try{
            IModelToTemplateXMLHandler.loadIModel(iModelXMLString,projectXMLString);
        }
        catch(Exception e){
            OneButton1Msg.show(gui, "error", "Deployment status",
                            "Deployment was unsuccessful!", "ok", new Dimension(180, 80));
            return null;
        }

        List interfaces =  projectData.getInterfaces();
        for(int ifaceIndex=0;ifaceIndex<interfaces.size();ifaceIndex++){
            DeployProjectInterfaceData data = (DeployProjectInterfaceData)interfaces.get(ifaceIndex);
            if(data.getIsAvailable().booleanValue()){
                String xmlData = data.getXmlContent();
                Element xmlElement = XMLUtils.stringToXmlElement(xmlData);
                if(!CreateTemplates.createTemplateFromXML(xmlElement))
                    OneButton1Msg.show(gui, "error", "Deployment status",
                                    "Deployment was unsuccessful!", "ok", new Dimension(180, 80));
            }
        }

        OneButton1Msg.show(gui, "option", "Deployment status",
                            "Deployment was successful!", "ok", new Dimension(180, 80));
        return null;

        //todo: get correct interface id's not the new id's referenced in the iModel

        /*
        projectData.setDescription(description);

        if (editPermissionsPanel != null) // if user has permission to set mode edit permissions
            projectData.setEditPermissions(editPermissionsPanel.getSetPermissions());
        if (visibilityPermissionsPanel != null) // if user has permission to set visibility permissions
            projectData.setContentVisibilityPermissions(visibilityPermissionsPanel.getSetPermissions());

        //get the permissions for each project interface and add the permissions result vector to projectData
        if (!projectInterfacePermissions.isEmpty()) { //if user has permission to set inteface use permissions
            for (int i = 0; i < (projectData.getInterfaces().size()); i++) {
                ((DeployProjectInterfaceData) (projectData.getInterfaces().get(i))).setPermissions(
                        ((PermissionsPanel) (projectInterfacePermissions.get(Integer.toString(i)))).getSetPermissions());
            }
        }

        //get the permissions for each iModel interface and add the permissions result vector to the projectData
        if (!iModelInterfacePermissions.isEmpty()) {
            List iModels = projectData.getIntegrationModels();
            DeployModelData[] modelData = (DeployModelData[]) (iModels.toArray(new DeployModelData[]{}));
            DeployInterfaceData[] interfaceData;
            List interfaces;
            for (int i = 0; i < iModels.size(); i++) {
                if ((interfaces = modelData[i].getModelInterfaces()).size() != 0) {
                    interfaceData = (DeployInterfaceData[]) (interfaces.toArray(new DeployInterfaceData[]{}));
                    for (int j = 0; j < interfaceData.length; j++) {
                        interfaceData[j].setPermissions(
                                ((PermissionsPanel) (iModelInterfacePermissions.get(interfaceData[j]))).getSetPermissions());
                    }
                }
            }
        }

        Vector[] preparedData = null;
        Vector versionInfo = null;

        if (isNewDeployment) {
            preparedData = DeployUtilities.prepareDeployProjectData((Integer) locationFolderId, projectData);
        } else {
            preparedData = DeployUtilities.prepareRedeployProjectData(redeployProjectId, projectData);
        }

        Vector interfaceInfoVector=preparedData[1];

//Qing added Nov 21, 03 for customGui in Project Interface
        int option = 2;//for project
        Vector customGuiFileInfos = DeployUtilities.loadCustomGuiFilesForDeploy(projectData.getXmlContent(), interfaceInfoVector, option);
        Vector customGuiFileInfos_for_upload_use = new Vector();

        if (customGuiFileInfos.size() != 0) {
//append customGuiFileInfo into auxFileInfos
            for (Iterator i = customGuiFileInfos.iterator(); i.hasNext();) {
                Vector aCustomGuiFileInfo = (Vector) i.next();
                String interface_build_Id = (String) aCustomGuiFileInfo.get(0);
                CustomGuiInfo cf = (CustomGuiInfo) aCustomGuiFileInfo.get(1);
                String filepath = cf.getJarFilePath();

                if (filepath == null) {//still can't find
                    System.out.println("DeployModel Error: can't find location of customGUIfile");
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
                    customGuiFileInfos_for_upload_use.add(Vectors.create(cf.getJarFileId().toString(), cfile.getName(), filepath, interface_build_Id, fileContent));
                }
            }
        }


        try {
            if (isNewDeployment) {
                versionInfo = DeployFilesFunctions.deployNewProject(serverConnection, preparedData[0], preparedData[1], preparedData[2]);
                Vector info = (Vector) versionInfo.get(0);
                String projectId = (String) info.get(0);

                //upload the customGui
                //to get interface build id   {{iface_deploy_id,version,iface_build_id}}
                Vector iface_info = (Vector) versionInfo.get(1);

                if (customGuiFileInfos_for_upload_use != null && customGuiFileInfos_for_upload_use.size() != 0) {
                    for (int i = 0; i < customGuiFileInfos_for_upload_use.size(); i++) {
                        Vector one_info = (Vector) customGuiFileInfos_for_upload_use.get(i);
                        String iface_build_id = (String) one_info.get(3);
                        String iface_deploy_id = DeployUtilities.getIfaceDeployId(iface_info, iface_build_id);
                        if (iface_deploy_id != null)
                            one_info.set(3, iface_deploy_id);
                    }
                    DeployFilesFunctions.uploadCustomGuiFiles(serverConnection, projectId, customGuiFileInfos_for_upload_use);
                }

                DeployUtilities.writeProjectVersionFile(localProjectPath, versionInfo, serverConnection);

                /**
                 * This if statement is here because a project could be
                 * embedded inside an analysis tool.  In which case, we do
                 * not want to announce a successful deploy until after the
                 * analysis tool has been deployed.
                 */
/*              if (!isProjectEmbedded)
                    OneButton1Msg.show(gui, "option", "Deployment status",
                            "Deployment to the server was successful!", "ok", new Dimension(180, 80));
            } else {
                versionInfo = DeployFilesFunctions.redeployProject(serverConnection, preparedData[0], preparedData[1], preparedData[2]);
                Vector info = (Vector) versionInfo.get(0);
                String projectId = (String) info.get(0);

                 //upload the customGui
                //to get interface build id   {{iface_deploy_id,version,iface_build_id}}
                Vector iface_info = (Vector) versionInfo.get(1);
                if (customGuiFileInfos_for_upload_use != null && customGuiFileInfos_for_upload_use.size() != 0) {
                    for (int i = 0; i < customGuiFileInfos_for_upload_use.size(); i++) {
                        Vector one_info = (Vector) customGuiFileInfos_for_upload_use.get(i);
                        String iface_build_id = (String) one_info.get(3);
                        String iface_deploy_id = DeployUtilities.getIfaceDeployId(iface_info, iface_build_id);
                        if (iface_deploy_id != null)
                            one_info.set(3, iface_deploy_id);
                    }
                    DeployFilesFunctions.uploadCustomGuiFiles(serverConnection, projectId, customGuiFileInfos_for_upload_use);
                }

                DeployUtilities.writeProjectVersionFile(localProjectPath, versionInfo, serverConnection);

                /**
                 * This if statement is here because a project could be
                 * embedded inside an analysis tool.  In which case, we do
                 * not want to announce a successful re-deploy until after the
                 * analysis tool has been re-deployed.
                 */
/*              if (!isProjectEmbedded)
                    OneButton1Msg.show(gui, "option", "Deployment status",
                            "Redeployment to the server was successful!", "ok", new Dimension(180, 80));
            }
            return (String) ((Vector) versionInfo.get(0)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            OneButton1Msg.show(gui, "error", "Deployment status",
                    "There was an error deploying the iProject\n" + e.getMessage().trim(),
                    "sorry", new Dimension(250, 100));
            return null;
        }  */
    }

    /**
     * reset all the data after a full deploymnet cycle
     */
    public void initDeployData() {
        //server connection is not initialized!
        isNewDeployment = true;
        localProjectPath = "";
        locationPath = "";
        locationFolderId = null;
        redeployProjectId = null;
        description = "";
        editPermissionsPanel = null;
        visibilityPermissionsPanel = null;
        availableProjectInterfacesChanged = false;
        projectData = null;
        projectInterfacePermissions.clear();
        iModelInterfacePermissions.clear();
    }

    public Hashtable getiModelInterfacePermissions() {
        return iModelInterfacePermissions;
    }

    public void setiModelInterfacePermissions(Hashtable iModelInterfacePermissions) {
        this.iModelInterfacePermissions = iModelInterfacePermissions;
    }

    public boolean isAvailableProjectInterfacesChanged() {
        return availableProjectInterfacesChanged;
    }

    public void setAvailableProjectInterfacesChanged(boolean availableProjectInterfacesChanged) {
        this.availableProjectInterfacesChanged = availableProjectInterfacesChanged;
    }

    public Hashtable getProjectInterfacePermissions() {
        return projectInterfacePermissions;
    }

    public void setProjectInterfacePermissions(Hashtable projectInterfacePermissions) {
        this.projectInterfacePermissions = projectInterfacePermissions;
    }

    public DeployProjectData getProjectData() {
        return projectData;
    }

    public void setProjectData(DeployProjectData projectData) {
        this.projectData = projectData;
    }

    public PermissionsPanel getVisibilityPermissionsPanel() {
        return visibilityPermissionsPanel;
    }

    public void setVisibilityPermissionsPanel(PermissionsPanel visibilityPermissionsPanel) {
        this.visibilityPermissionsPanel = visibilityPermissionsPanel;
    }

    public SelectProjectInterfacesCard getSelectProjectInterfaces() {
        return selectProjectInterfaces;
    }

    public void setSelectProjectInterfaces(SelectProjectInterfacesCard selectProjectInterfaces) {
        this.selectProjectInterfaces = selectProjectInterfaces;
    }

    public String getLocationPath() {
        return locationPath;
    }

    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }

    public String getLocalProjectPath() {
        return localProjectPath;
    }

    public void setLocalProjectPath(String localProjectPath) {
        this.localProjectPath = localProjectPath;
    }

    public SelectProjectCard getSelectProjectCard() {
        return selectProjectCard;
    }

    public void setSelectProjectCard(SelectProjectCard selectProjectCard) {
        this.selectProjectCard = selectProjectCard;
    }

    public PermissionsPanel getEditPermissionsPanel() {
        return editPermissionsPanel;
    }

    public void setEditPermissionsPanel(PermissionsPanel editPermissionsPanel) {
        this.editPermissionsPanel = editPermissionsPanel;
    }

    public Object getLocationFolderId() {
        return locationFolderId;
    }

    public void setLocationFolderId(Object locationFolderId) {
        this.locationFolderId = locationFolderId;
    }

    public String getRedeployProjectId() {
        return redeployProjectId;
    }

    public void setRedeployProjectId(String redeployProjectId) {
        this.redeployProjectId = redeployProjectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isNewDeployment() {
        return isNewDeployment;
    }

    public void setNewDeployment(boolean newDeployment) {
        isNewDeployment = newDeployment;
    }

    public LoginCard getLoginCard() {
        return loginCard;
    }

    public void setLoginCard(LoginCard loginCard) {
        this.loginCard = loginCard;
    }

    public LocateCard getLocateCard() {
        return locateCard;
    }

    public void setLocateCard(LocateCard locateCard) {
        this.locateCard = locateCard;
    }

    public ConfirmCard getConfirmCard() {
        return confirmCard;
    }

    public void setConfirmCard(ConfirmCard confirmCard) {
        this.confirmCard = confirmCard;
    }

    public static JFrame createDeployGui() {
        DeployProjectTemplate dp = new DeployProjectTemplate();
        JFrame f = new JFrame("Deploy iProject");
        f.getContentPane().add(dp.gui);
        f.setSize(DeployModelGui.DEFAULT_SIZE);
        return f;
    }

    /**
     * Constructor for the Deployplayspace class
     */
    public DeployProjectTemplate() {

        gui = new DeployProjectGui(this);
    }

    /**
     * constructor used by the analysis tool deploy gui
     * @param gui
     */

    public DeployProjectTemplate(DeployProjectGui gui) {
        this.gui = gui;
    }

    /**
     * Used to get the server connection info
     * @return the current server connection info
     */
    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * Used to set the server connection info
     * @param serverConnection The new server connection
     */
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    public DeployProjectGui getGui() {
        return gui;
    }

    public void setDeployProjectGui(DeployProjectGui gui) {
        this.gui = gui;
    }

    public static void main(String[] args) {
        DomeInit.initializeDOME();
        JFrame f = createDeployGui();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.show();
    }
}
