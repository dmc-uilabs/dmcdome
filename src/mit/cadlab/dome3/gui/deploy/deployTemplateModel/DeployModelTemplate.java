package mit.cadlab.dome3.gui.deploy.deployTemplateModel;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.search.framework.templatemanagement.TemplateRegistry;
import mit.cadlab.dome3.search.framework.utils.processing.CreateTemplates;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployModelData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.ModelVersionData;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;

import javax.swing.JFrame;
import java.awt.*;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Element;

public class DeployModelTemplate {
    // data needed to run the wizard
    private DeployModelGui gui;
    private boolean newDeployment = true;
    private String serverLocationPath;
    private String localModelPath;
    private boolean interfacePermissionsCreated = false;

    private LoginCard loginCard;
    private SelectModelCard selectModelCard;
    private SelectInterfaceCard selectInterfaceCard;
    private ConfirmCard confirmCard;

    //data needed to put things into the database
    private ServerConnection serverConnection = null;
    private String modelDescription = "";
    private Object serverLocationId = null;
    private String redeployModelId = null;
    private PermissionsPanel modelPermPanel = null;
    private DeployModelData modelData = null;
    private Hashtable interfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel

    /**
     * Method called to package data and make calls to the server
     */
    public void deployData() {
        boolean success = false;
        List interfaces = modelData.getModelInterfaces();
        for(int ifaceIndex=0;ifaceIndex<interfaces.size();ifaceIndex++){
            DeployInterfaceData iface = (DeployInterfaceData)interfaces.get(ifaceIndex);
            if(iface.getIsAvailable().booleanValue()){
                String ifaceXMLString = iface.getXmlContent();
                Element xmlElement = XMLUtils.stringToXmlElement(ifaceXMLString);
                if(CreateTemplates.createTemplateFromXML(xmlElement))
                    success = true;
            }
        }
        if(success)
            OneButton1Msg.show(gui, "option", "Deployment status",
                                "Deployment was successful!", "ok", new Dimension(180, 80));
        else
            OneButton1Msg.show(gui, "error", "Deployment status",
                                "Deployment was unsuccessful!", "ok", new Dimension(180, 80));



        //get the permissions for each interface and add the permissions result vector to modelData
        /*if (!interfacePermissions.isEmpty()) { //if user has permission to set inteface use permissions
            for (int i = 0; i < (modelData.getModelInterfaces().size()); i++) {
                ((DeployInterfaceData) (modelData.getModelInterfaces().get(i))).setPermissions(
                        ((PermissionsPanel) (interfacePermissions.get(Integer.toString(i)))).getSetPermissions());
            }
        }
        Vector modelPermission = null;
        if (modelPermPanel != null) // if user has permission to set mode edit permissions
            modelPermission = modelPermPanel.getSetPermissions();

        //now create the data structures that the server expects
        Vector modelInfo;
        if (newDeployment == true)
            modelInfo = DeployUtilities.createNewModelInfoVector((Integer) serverLocationId, modelDescription, modelData.getXmlContent(), modelPermission);
        else
            modelInfo = DeployUtilities.createUpdateModelInfoVector(redeployModelId, modelDescription, modelData.getXmlContent(), modelPermission);
        Vector interfaceInfoVector = DeployUtilities.createDeployInterfaceInfoVector(modelData.getModelInterfaces());

        //now create vector to handel auxiliary file content
	    Vector auxFilesInfos = null;
	    Vector customGuiFileInfos_for_upload_use = null;
	    try {
		    auxFilesInfos = DeployUtilities.loadAuxFilesForDeploy(modelData.getXmlContent());

		    //July15 Qing added--separate customGui from model into interface only
		    //Aug 04 Qing change--- customGUI save to database using interfaceId and should saveto ModelId/InterfaceId/ folder instead
		    int option=1;//for model
		    Vector customGuiFileInfos = DeployUtilities.loadCustomGuiFilesForDeploy(modelData.getXmlContent(),interfaceInfoVector,option);
		    customGuiFileInfos_for_upload_use = new Vector();
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
	                    fileContent = FileUtils.readBinaryFileAsByteArray(filepath);
	                    customGuiFileInfos_for_upload_use.add(Vectors.create(cf.getJarFileId().toString(), cfile.getName(), filepath, interface_build_Id, fileContent));
	                }
	            }
	        }
	    }
	    catch (Exception e) {
		    OneButton1Msg.show(gui, "error", "Deployment status",
		                       "Model was not deployed due to \n" + e.getMessage().trim(),
		                       "sorry", new Dimension(250, 100));
		    return;
	    }

	    try {
            Vector verInfo;
            if (newDeployment == true) {
                verInfo = DeployFilesFunctions.deployNewModel(serverConnection, modelInfo, interfaceInfoVector);
                Vector info = (Vector) verInfo.get(0);
                String modelId = (String) info.get(0);


                if (auxFilesInfos != null)
                    DeployFilesFunctions.uploadFile(serverConnection, modelId, auxFilesInfos);

                //to get interface build id   {{iface_deploy_id,version,iface_build_id}}
                Vector iface_info = (Vector) verInfo.get(1);
                if (customGuiFileInfos_for_upload_use != null && customGuiFileInfos_for_upload_use.size() != 0) {
                    for (int i = 0; i < customGuiFileInfos_for_upload_use.size(); i++) {
                        Vector one_info = (Vector) customGuiFileInfos_for_upload_use.get(i);
                        String iface_build_id = (String) one_info.get(3);
                        String iface_deploy_id = DeployUtilities.getIfaceDeployId(iface_info, iface_build_id);
                        if (iface_deploy_id != null)
                            one_info.set(3, iface_deploy_id);
                    }
                    DeployFilesFunctions.uploadCustomGuiFiles(serverConnection, modelId, customGuiFileInfos_for_upload_use);
                }
                OneButton1Msg.show(gui, "option", "Deployment status",
                        "Deployment to the server was sucessful!", "ok", new Dimension(180, 80));
            } else {
                verInfo = DeployFilesFunctions.redeployModel(serverConnection, modelInfo, interfaceInfoVector);
                //upload auxfile
                Vector info = (Vector) verInfo.get(0);
                String modelId = (String) info.get(0);
                if (auxFilesInfos != null)
                    DeployFilesFunctions.uploadFile(serverConnection, modelId, auxFilesInfos);

                //to get interface build id   {{iface_deploy_id,version,iface_build_id}}
                Vector iface_info = (Vector) verInfo.get(1);
                if (customGuiFileInfos_for_upload_use != null && customGuiFileInfos_for_upload_use.size() != 0) {
                    for (int i = 0; i < customGuiFileInfos_for_upload_use.size(); i++) {
                        Vector one_info = (Vector) customGuiFileInfos_for_upload_use.get(i);
                        String iface_build_id = (String) one_info.get(3);
                        String iface_deploy_id = DeployUtilities.getIfaceDeployId(iface_info, iface_build_id);
                        if (iface_deploy_id != null)
                            one_info.set(3, iface_deploy_id);
                    }
                    DeployFilesFunctions.uploadCustomGuiFiles(serverConnection, modelId, customGuiFileInfos_for_upload_use);
                }
                OneButton1Msg.show(gui, "option", "Deployment status",
                        "Redeployment to the server was sucessful!", "ok", new Dimension(180, 80));
            }
            DeployUtilities.writeModelVersionFile(localModelPath, new ModelVersionData(verInfo, serverConnection));
        } catch (Exception e) {
            e.printStackTrace();
            OneButton1Msg.show(gui, "error", "Deployment status",
                    "There was an error deploying the model\n" + e.getMessage().trim(),
                    "sorry", new Dimension(250, 100));
        }     */
    }

    public void initDeployData() {
        //if (serverConnection!=null) serverConnection.logout(); don't want to init this so can allow additional cycles without new login
        //serverConnection = null;
        serverLocationPath = "";
        localModelPath = "";
        newDeployment = true;
        serverLocationId = null;
        redeployModelId = "";
        modelDescription = "";
        modelPermPanel = null;
        modelData = null;
        interfacePermissionsCreated = false;
        interfacePermissions.clear();
    }


    public String getRedeployModelId() {
        return redeployModelId;
    }

    public void setRedeployModelId(String redeployModelId) {
        this.redeployModelId = redeployModelId;
    }


    public LoginCard getLoginCard() {
        return loginCard;
    }

    public void setLoginCard(LoginCard loginCard) {
        this.loginCard = loginCard;
    }

    public ConfirmCard getConfirmCard() {
        return confirmCard;
    }

    public void setConfirmCard(ConfirmCard confirmCard) {
        this.confirmCard = confirmCard;
    }

    public boolean isInterfacePermissionsCreated() {
        return interfacePermissionsCreated;
    }

    public void setInterfacePermissionsCreated(boolean interfacePermissionsCreated) {
        this.interfacePermissionsCreated = interfacePermissionsCreated;
    }

    public Hashtable getInterfacePermissions() {
        return interfacePermissions;
    }

    public void setInterfacePermissions(Hashtable interfacePermissions) {
        this.interfacePermissions = interfacePermissions;
    }

    public DeployModelData getModelData() {
        return modelData;
    }

    public void setModelData(DeployModelData modelData) {
        this.modelData = modelData;
    }

    public PermissionsPanel getModelPermPanel() {
        return modelPermPanel;
    }

    public void setModelPermPanel(PermissionsPanel modelPermPanel) {
        this.modelPermPanel = modelPermPanel;
    }

    public SelectInterfaceCard getSelectInterfaceCard() {
        return selectInterfaceCard;
    }

    public void setSelectInterfaceCard(SelectInterfaceCard selectInterfaceCard) {
        this.selectInterfaceCard = selectInterfaceCard;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
        System.out.println("saving data");
    }


    public SelectModelCard getSelectModelCard() {
        return selectModelCard;
    }

    public void setSelectModelCard(SelectModelCard selectModelCard) {
        this.selectModelCard = selectModelCard;
    }

    public String getServerLocationPath() {
        return serverLocationPath;
    }

    public void setServerLocationPath(String serverLocationPath) {
        this.serverLocationPath = serverLocationPath;
    }

    public Object getServerLocationId() {
        return serverLocationId;
    }

    public void setServerLocationId(Object serverLocationId) {
        this.serverLocationId = serverLocationId;
    }

    public boolean getNewDeployment() {
        return newDeployment;
    }

    public void setNewDeployment(boolean newDeployment) {
        this.newDeployment = newDeployment;
    }

    public String getLocalModelPath() {
        return localModelPath;
    }

    public void setLocalModelPath(String localModelPath) {
        this.localModelPath = localModelPath;
    }

    public DeployModelGui getGui() {
        return gui;
    }

    public static JFrame createDeployGui() {
        DeployModelTemplate dm = new DeployModelTemplate();
        JFrame f = new JFrame("Deploy model or iProject");
        f.getContentPane().add(dm.gui);
        f.setSize(DeployModelGui.DEFAULT_SIZE);
        return f;
    }

    public DeployModelTemplate() {
        gui = new DeployModelGui(this);
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }


    public static void main(String[] args) {
        DomeInit.initializeDOME(); //not needed when in the DOME environment
        JFrame f = createDeployGui();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.show();
    }
}
