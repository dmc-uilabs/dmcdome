package deployTool;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.deploy.components.*;
import mit.cadlab.dome3.gui.deploy.deployModel.DeployModelGui;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 3, 2003
 * Time: 8:22:07 AM
 * To change this template use Options | File Templates.
 */

/**
 * Data model class for running the deploy playspace wizard
 */
public class DeployTool
{
	//data needed to run the wizard
	private DeployToolGui gui;
	private LoginCard loginCard;
	private SelectToolCard selectToolCard;
	private LocateToolCard locateToolCard;
    //todo private SelectToolInterfacesCard selectToolInterfaces;
    //todo private IsToolProjectVisibleCard isToolVisibleCard;
	private EditProjectPrivCard editProjectPrivCard;
	private SetProjectVisibility projectVisCard;
	private SelectProjectInterfacesCard selectProjectInterfaces;
	private SetProjectInterfacePrivCard projectInterfaceUsePrivCard;
	private SetProjectIntegrationModelPrivCard projectIntegrationModelPrivCard;
	private ConfirmCard confirmCard;

	private boolean isNewDeployment = true;
	private String localToolPath = "";
	private String locationPath = ""; //location on the server
    //todo private DeployToolData toolData = null;
	private DeployProjectData toolProjectData = null;
	private boolean availableProjectInterfacesChanged = false;
	private Hashtable projectInterfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel: keyed on interface list index
	private Hashtable iModelInterfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel keyed on interfaceDeployData object

	//data needed for deployment
	private ServerConnection serverConnection = null;
	private Object locationFolderId = null; // used only if it is a new project deployment so know where to add it



    private String redeployToolId = null;
    private String redeployToolProjectId = null;
	private String description = "";
	private PermissionsPanel editPermissionsPanel = null;
	private PermissionsPanel visibilityPermissionsPanel = null;

	public void deployData()
	{
		toolProjectData.setDescription(description);

		if (editPermissionsPanel != null) // if user has permission to set mode edit permissions
			toolProjectData.setEditPermissions(editPermissionsPanel.getSetPermissions());
		if (visibilityPermissionsPanel != null) // if user has permission to set visibility permissions
			toolProjectData.setContentVisibilityPermissions(visibilityPermissionsPanel.getSetPermissions());

		//get the permissions for each project interface and add the permissions result vector to toolProjectData
		if (!projectInterfacePermissions.isEmpty()) { //if user has permission to set inteface use permissions
			for (int i = 0; i < (toolProjectData.getInterfaces().size()); i++) {
				((DeployProjectInterfaceData) (toolProjectData.getInterfaces().get(i))).setPermissions(
				        ((PermissionsPanel) (projectInterfacePermissions.get(Integer.toString(i)))).getSetPermissions());
			}
		}

		//get the permissions for each iModel interface and add the permissions result vector to the toolProjectData
		if (!iModelInterfacePermissions.isEmpty()) {
			List iModels = toolProjectData.getIntegrationModels();
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
		try {
			if (isNewDeployment) {
				preparedData = DeployUtilities.prepareDeployProjectData((Integer) locationFolderId, toolProjectData);
				versionInfo = DeployFilesFunctions.deployNewProject(serverConnection, preparedData[0], preparedData[1], preparedData[2]);
				DeployUtilities.writeProjectVersionFile(localToolPath, versionInfo, serverConnection);
				OneButton1Msg.show(gui, "option", "Deployment status",
				                   "Deployment to the server was sucessful!", "ok", new Dimension(180, 80));
			} else {
				preparedData = DeployUtilities.prepareRedeployProjectData(redeployToolProjectId, toolProjectData);
				versionInfo = DeployFilesFunctions.redeployProject(serverConnection, preparedData[0], preparedData[1], preparedData[2]);
				DeployUtilities.writeProjectVersionFile(localToolPath, versionInfo, serverConnection);
				OneButton1Msg.show(gui, "option", "Deployment status",
				                   "Redeployment to the server was sucessful!", "ok", new Dimension(180, 80));
			}
		} catch (Exception e) {
			e.printStackTrace();
			OneButton1Msg.show(gui, "error", "Deployment status",
			                   "There was an error deploying the iProject\n" + e.getMessage().trim(),
			                   "sorry", new Dimension(250, 100));
		}
	}

	/**
	 * reset all the data after a full deploymnet cycle
	 */
	public void initDeployData()
	{
		//server connection is not initialized!
		isNewDeployment = true;
		localToolPath = "";
		locationPath = "";
		locationFolderId = null;
		redeployToolProjectId = null;
		description = "";
		editPermissionsPanel = null;
		visibilityPermissionsPanel = null;
		availableProjectInterfacesChanged = false;
		toolProjectData = null;
		projectInterfacePermissions.clear();
		iModelInterfacePermissions.clear();
	}

	public Hashtable getiModelInterfacePermissions()
	{
		return iModelInterfacePermissions;
	}

	public void setiModelInterfacePermissions(Hashtable iModelInterfacePermissions)
	{
		this.iModelInterfacePermissions = iModelInterfacePermissions;
	}

	public boolean isAvailableProjectInterfacesChanged()
	{
		return availableProjectInterfacesChanged;
	}

    public String getRedeployToolId() {
        return redeployToolId;
    }

    public void setRedeployToolId(String redeployToolId) {
        this.redeployToolId = redeployToolId;
    }

    public void setAvailableProjectInterfacesChanged(boolean availableProjectInterfacesChanged)
	{
		this.availableProjectInterfacesChanged = availableProjectInterfacesChanged;
	}

	public Hashtable getProjectInterfacePermissions()
	{
		return projectInterfacePermissions;
	}

	public void setProjectInterfacePermissions(Hashtable projectInterfacePermissions)
	{
		this.projectInterfacePermissions = projectInterfacePermissions;
	}

	public DeployProjectData getToolProjectData()
	{
		return toolProjectData;
	}

	public void setToolProjectData(DeployProjectData toolProjectData)
	{
		this.toolProjectData = toolProjectData;
	}

	public PermissionsPanel getVisibilityPermissionsPanel()
	{
		return visibilityPermissionsPanel;
	}

	public void setVisibilityPermissionsPanel(PermissionsPanel visibilityPermissionsPanel)
	{
		this.visibilityPermissionsPanel = visibilityPermissionsPanel;
	}

	public SetProjectIntegrationModelPrivCard getProjectIntegrationModelPrivCard()
	{
		return projectIntegrationModelPrivCard;
	}

	public void setProjectIntegrationModelPrivCard(SetProjectIntegrationModelPrivCard projectIntegrationModelPrivCard)
	{
		this.projectIntegrationModelPrivCard = projectIntegrationModelPrivCard;
	}

	public SelectProjectInterfacesCard getSelectProjectInterfaces()
	{
		return selectProjectInterfaces;
	}

	public void setSelectProjectInterfaces(SelectProjectInterfacesCard selectProjectInterfaces)
	{
		this.selectProjectInterfaces = selectProjectInterfaces;
	}

	public SetProjectVisibility getProjectVisCard()
	{
		return projectVisCard;
	}

	public void setProjectVisCard(SetProjectVisibility projectVisCard)
	{
		this.projectVisCard = projectVisCard;
	}

	public String getLocationPath()
	{
		return locationPath;
	}

	public void setLocationPath(String locationPath)
	{
		this.locationPath = locationPath;
	}

	public String getLocalToolPath()
	{
		return localToolPath;
	}

	public void setLocalToolPath(String localToolPath)
	{
		this.localToolPath = localToolPath;
	}

	public SelectToolCard getSelectToolCard()
	{
		return selectToolCard;
	}

	public void setSelectToolCard(SelectToolCard selectToolCard)
	{
		this.selectToolCard = selectToolCard;
	}

	public PermissionsPanel getEditPermissionsPanel()
	{
		return editPermissionsPanel;
	}

	public void setEditPermissionsPanel(PermissionsPanel editPermissionsPanel)
	{
		this.editPermissionsPanel = editPermissionsPanel;
	}

	public Object getLocationFolderId()
	{
		return locationFolderId;
	}

	public void setLocationFolderId(Object locationFolderId)
	{
		this.locationFolderId = locationFolderId;
	}

	public String getRedeployToolProjectId()
	{
		return redeployToolProjectId;
	}

	public void setRedeployToolProjectId(String redeployToolProjectId)
	{
		this.redeployToolProjectId = redeployToolProjectId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}


	public boolean isNewDeployment()
	{
		return isNewDeployment;
	}

	public void setNewDeployment(boolean newDeployment)
	{
		isNewDeployment = newDeployment;
	}

	public LoginCard getLoginCard()
	{
		return loginCard;
	}

	public void setLoginCard(LoginCard loginCard)
	{
		this.loginCard = loginCard;
	}

	public LocateToolCard getLocateToolCard()
	{
		return locateToolCard;
	}

	public void setLocateToolCard(LocateToolCard locateToolCard)
	{
		this.locateToolCard = locateToolCard;
	}

	public EditProjectPrivCard getEditProjectPrivCard()
	{
		return editProjectPrivCard;
	}

	public void setEditProjectPrivCard(EditProjectPrivCard editProjectPrivCard)
	{
		this.editProjectPrivCard = editProjectPrivCard;
	}

	public SetProjectInterfacePrivCard getProjectInterfaceUsePrivCard()
	{
		return projectInterfaceUsePrivCard;
	}

	public void setProjectInterfaceUsePrivCard(SetProjectInterfacePrivCard projectInterfaceUsePrivCard)
	{
		this.projectInterfaceUsePrivCard = projectInterfaceUsePrivCard;
	}

	public ConfirmCard getConfirmCard()
	{
		return confirmCard;
	}

	public void setConfirmCard(ConfirmCard confirmCard)
	{
		this.confirmCard = confirmCard;
	}

	public static JFrame createDeployGui()
	{
		DeployTool dp = new DeployTool();
		JFrame f = new JFrame("Deploy iProject");
		f.getContentPane().add(dp.gui);
		f.setSize(DeployModelGui.DEFAULT_SIZE);
		return f;
	}

	/**
	 * Constructor for the Deployplayspace class
	 */
	public DeployTool()
	{

		gui = new DeployToolGui(this);
	}

	/**
	 * Used to get the server connection info
	 * @return the current server connection info
	 */
	public ServerConnection getServerConnection()
	{
		return serverConnection;
	}

	/**
	 * Used to set the server connection info
	 * @param serverConnection The new server connection
	 */
	public void setServerConnection(ServerConnection serverConnection)
	{
		this.serverConnection = serverConnection;
	}

	public DeployToolGui getGui()
	{
		return gui;
	}


	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		JFrame f = createDeployGui();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.show();
	}
}
