package mit.cadlab.dome3.gui.deploy.deployPlayspace;

import mit.cadlab.dome3.gui.deploy.components.DeployPlayspaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.PlayspaceVersionData;
import mit.cadlab.dome3.gui.deploy.deployModel.DeployModelGui;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;

import javax.swing.JFrame;
import java.awt.Dimension;
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
public class DeployPlayspace
{
	//data needed to run the wizard
	private DeployPlayspaceGui gui;
	private LoginCard loginCard;
	private LocateCard locateCard;
	private SelectPlayspaceCard selectPlayspaceCard;
	private EditPrivCard editPrivCard;
	private UsePrivCard usePrivCard;
	private ConfirmCard confirmCard;

	private boolean isNewDeployment = true;
	private String playspacePath = "";
	private String locationPath = ""; //location on the server

	//data needed for deployment
	private ServerConnection serverConnection = null;
	private Object locationFolderId = null; // used only if it is a new PS deployment so know where to add it
	private String redeployPlayspaceId = null;
	private String description = "";
	private PermissionsPanel editPermissionsPanel = null;
	private PermissionsPanel usePermissionsPanel = null;
	private DeployPlayspaceData playspaceData = null;

	public void deployData()
	{
		Vector playspaceEditPermission = null;
		if (editPermissionsPanel != null) // user has permissions to set these priv
			playspaceEditPermission = editPermissionsPanel.getSetPermissions();

		Vector playspaceUsePermission = null;
		if (usePermissionsPanel != null) // user has permission to set these priv
			playspaceUsePermission = usePermissionsPanel.getSetPermissions();

		//now create the data structures that the server expects
		try {
			Vector verInfo;
			if (isNewDeployment) {
				verInfo = DeployFilesFunctions.deployNewPlayspace(serverConnection, ((Integer) locationFolderId).intValue(),
				                                                  description, playspaceData.getXmlContent(), playspaceEditPermission, playspaceUsePermission);
			} else {
				verInfo = DeployFilesFunctions.redeployPlayspace(serverConnection, redeployPlayspaceId,
				                                                 description, playspaceData.getXmlContent(), playspaceEditPermission, playspaceUsePermission);
			}
			PlayspaceVersionData psVersionData = new PlayspaceVersionData(verInfo, serverConnection);
			DeployUtilities.writePlayspaceVersionFile(playspacePath, psVersionData);
			OneButton1Msg.show(gui, "option", "Deployment status",
			                   "Deployment to the server was sucessful!", "ok", new Dimension(180, 80));
		} catch (Exception e) {
			e.printStackTrace();
			OneButton1Msg.show(gui, "error", "Deployment status",
			                   "There was an error deploying the playspace\n" + e.getMessage(),
			                   "sorry", new Dimension(250, 100));
		}
	}

	/**
	 * reset all the data after a full deploymnet cycle
	 */
	public void initDeployData()
	{
		isNewDeployment = true;
		playspacePath = "";
		locationPath = "";
		//serverConnection = null; don't want to init this so can allow addition cycles without new login
		locationFolderId = null;
		redeployPlayspaceId = null;
		description = "";
		editPermissionsPanel = null;
		usePermissionsPanel = null;
		playspaceData = null;
	}

	public DeployPlayspaceData getPlayspaceData()
	{
		return playspaceData;
	}

	public String getLocationPath()
	{
		return locationPath;
	}

	public void setLocationPath(String locationPath)
	{
		this.locationPath = locationPath;
	}


	public void setPlayspaceData(DeployPlayspaceData playspaceData)
	{
		this.playspaceData = playspaceData;
	}

	public String getPlayspacePath()
	{
		return playspacePath;
	}

	public void setPlayspacePath(String playspacePath)
	{
		this.playspacePath = playspacePath;
	}

	public SelectPlayspaceCard getSelectPlayspaceCard()
	{
		return selectPlayspaceCard;
	}

	public void setSelectPlayspaceCard(SelectPlayspaceCard selectPlayspaceCard)
	{
		this.selectPlayspaceCard = selectPlayspaceCard;
	}


	public PermissionsPanel getUsePermissionsPanel()
	{
		return usePermissionsPanel;
	}

	public void setUsePermissionsPanel(PermissionsPanel usePermissionsPanel)
	{
		System.out.println("Inside UsePrivDard.setUserPermisssonsPanel");
		this.usePermissionsPanel = usePermissionsPanel;
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

	public String getRedeployPlayspaceId()
	{
		return redeployPlayspaceId;
	}

	public void setRedeployPlayspaceId(String redeployPlayspaceId)
	{
		this.redeployPlayspaceId = redeployPlayspaceId;
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

	public EditPrivCard getEditPrivCard()
	{
		return editPrivCard;
	}

	public void setEditPrivCard(EditPrivCard editPrivCard)
	{
		this.editPrivCard = editPrivCard;
	}

	public UsePrivCard getUsePrivCard()
	{
		return usePrivCard;
	}

	public void setUsePrivCard(UsePrivCard usePrivCard)
	{
		this.usePrivCard = usePrivCard;
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
		DeployPlayspace dm = new DeployPlayspace();
		JFrame f = new JFrame("Deploy Playspace");
		f.getContentPane().add(dm.gui);
		f.setSize(DeployModelGui.DEFAULT_SIZE);
		return f;
	}

	/**
	 * Constructor for the Deployplayspace class
	 */
	public DeployPlayspace()
	{
		gui = new DeployPlayspaceGui(this);
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

	public DeployPlayspaceGui getGui()
	{
		return gui;
	}


	public static void main(String[] args)
	{
		JFrame f = createDeployGui();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.show();
	}
}
