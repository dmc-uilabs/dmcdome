package deployProject;

import mit.cadlab.dome.DomeInit;
import mit.cadlab.dome.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome.gui.deploy.components.DeployModelData;
import mit.cadlab.dome.gui.deploy.components.DeployProjectData;
import mit.cadlab.dome.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome.gui.permission.PermissionsPanel;
import mit.cadlab.dome.gui.swing.msg.OneButton1Msg;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.functions.DeployFilesFunctions;

import javax.swing.JFrame;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.awt.Dimension;

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
public class DeployProject
{
	//data needed to run the wizard
	private DeployProjectGui gui;
	private LoginCard loginCard;
	private SelectProjectCard selectProjectCard;
	private LocateCard locateCard;
	private EditProjectPrivCard editPrivCard;
	private SetProjectVisibility projectVisCard;
	private SelectProjectInterfacesCard selectProjectInterfaces;
	private SetProjectInterfacePrivCard interfaceUsePrivCard;
	private SetIntegrationModelPrivCard integrationModelPrivCard;
	private ConfirmCard confirmCard;

	private boolean isNewDeployment=true;
	private String localProjectPath="";
	private String locationPath=""; //location on the server
	private DeployProjectData projectData = null;
	private boolean availableProjectInterfacesChanged = false;
	private Hashtable projectInterfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel: keyed on interface list index
	private Hashtable iModelInterfacePermissions = new Hashtable(); //Hashtable of PermissionsPanel keyed on interfaceDeployData object

	//data needed for deployment
	private ServerConnection serverConnection = null;
	private Object locationFolderId=null; // used only if it is a new project deployment so know where to add it
	private String redeployProjectId=null;
	private String description="";
	private PermissionsPanel editPermissionsPanel = null;
	private PermissionsPanel visibilityPermissionsPanel = null;

	public void deployData()
	{
		projectData.setDescription(description);

		if (editPermissionsPanel != null) // if user has permission to set mode edit permissions
			projectData.setEditPermissions(editPermissionsPanel.getSetPermissions());
		if (visibilityPermissionsPanel !=null) // if user has permission to set visibility permissions
			projectData.setContentVisibilityPermissions(visibilityPermissionsPanel.getSetPermissions());

		//get the permissions for each project interface and add the permissions result vector to projectData
		if (!projectInterfacePermissions.isEmpty()) { //if user has permission to set inteface use permissions
			for (int i = 0; i < (projectData.getInterfaces().size()); i++) {
				((DeployInterfaceData) (projectData.getInterfaces().get(i))).setPermissions(
				        ((PermissionsPanel) (projectInterfacePermissions.get(Integer.toString(i)))).getSetPermissions());
			}
		}

		//get the permissions for each iModel interface and add the permissions result vector to the projectData
		if (!iModelInterfacePermissions.isEmpty()){
			List iModels = projectData.getIntegrationModels();
			DeployModelData[] modelData = (DeployModelData[]) (iModels.toArray(new DeployModelData[]{}));
			DeployInterfaceData[] interfaceData;
			List interfaces;
			for (int i = 0; i < iModels.size(); i++) {
				if ((interfaces = modelData[i].getModelInterfaces()).size() != 0) {
					interfaceData = (DeployInterfaceData[]) (interfaces.toArray(new DeployInterfaceData[]{}));
					for (int j = 0; j < interfaceData.length; j++) {
						interfaceData[j].setPermissions(
						        ((PermissionsPanel)(iModelInterfacePermissions.get(interfaceData[j]))).getSetPermissions());
					}
				}
			}
		}

		Vector[] preparedData= null;
		Vector versionInfo = null;
		try {
			if (isNewDeployment){
				preparedData = DeployUtilities.prepareDeployProjectData((Integer) locationFolderId, projectData);
				versionInfo = DeployFilesFunctions.deployNewProject(serverConnection, preparedData[0], preparedData[1], preparedData[2]);
				DeployUtilities.writeProjectVersionFile(localProjectPath, versionInfo, serverConnection);
				OneButton1Msg.show(gui, "option", "Deployment status",
				        "Deployment to the server was sucessful!", "ok", new Dimension(180, 80));
			}
			else {
				preparedData = DeployUtilities.prepareRedeployProjectData(redeployProjectId, projectData);
				versionInfo = DeployFilesFunctions.redeployProject(serverConnection, preparedData[0], preparedData[1], preparedData[2]);
				DeployUtilities.writeProjectVersionFile(localProjectPath, versionInfo, serverConnection);
				OneButton1Msg.show(gui, "option", "Deployment status",
				        "Redeployment to the server was sucessful!", "ok", new Dimension(180, 80));
			}
		}
		catch (Exception e) {
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

	public DeployProjectData getProjectData()
	{
		return projectData;
	}

	public void setProjectData(DeployProjectData projectData)
	{
		this.projectData = projectData;
	}

	public PermissionsPanel getVisibilityPermissionsPanel()
	{
		return visibilityPermissionsPanel;
	}

	public void setVisibilityPermissionsPanel(PermissionsPanel visibilityPermissionsPanel)
	{
		this.visibilityPermissionsPanel = visibilityPermissionsPanel;
	}

	public SetIntegrationModelPrivCard getIntegrationModelPrivCard()
	{
		return integrationModelPrivCard;
	}

	public void setIntegrationModelPrivCard(SetIntegrationModelPrivCard integrationModelPrivCard)
	{
		this.integrationModelPrivCard = integrationModelPrivCard;
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

	public String getLocalProjectPath()
	{
		return localProjectPath;
	}

	public void setLocalProjectPath(String localProjectPath)
	{
		this.localProjectPath = localProjectPath;
	}

	public SelectProjectCard getSelectProjectCard()
	{
		return selectProjectCard;
	}

	public void setSelectProjectCard(SelectProjectCard selectProjectCard)
	{
		this.selectProjectCard = selectProjectCard;
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

	public String getRedeployProjectId()
	{
		return redeployProjectId;
	}

	public void setRedeployProjectId(String redeployProjectId)
	{
		this.redeployProjectId = redeployProjectId;
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

	public LocateCard getLocateCard()
	{
		return locateCard;
	}

	public void setLocateCard(LocateCard locateCard)
	{
		this.locateCard = locateCard;
	}

	public EditProjectPrivCard getEditPrivCard()
	{
		return editPrivCard;
	}

	public void setEditPrivCard(EditProjectPrivCard editPrivCard)
	{
		this.editPrivCard = editPrivCard;
	}

	public SetProjectInterfacePrivCard getInterfaceUsePrivCard()
	{
		return interfaceUsePrivCard;
	}

	public void setInterfaceUsePrivCard(SetProjectInterfacePrivCard interfaceUsePrivCard)
	{
		this.interfaceUsePrivCard = interfaceUsePrivCard;
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
		DeployProject dp = new DeployProject();
		JFrame f = new JFrame("Deploy iProject");
		f.getContentPane().add(dp.gui);
		f.setSize(DeployProjectGui.DEFAULT_SIZE);
		return f;
	}

	/**
	 * Constructor for the Deployplayspace class
	 */
	public DeployProject()
	{

		gui = new DeployProjectGui(this);
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

	public DeployProjectGui getGui(){
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
