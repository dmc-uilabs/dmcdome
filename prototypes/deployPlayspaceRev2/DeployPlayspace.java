package deployPlayspaceRev2;

import mit.cadlab.dome.gui.deploy.deployModel.DeployModelGui;
import mit.cadlab.dome.gui.permission.PermissionsPanel;
import mit.cadlab.dome.network.client.connection.ServerConnection;

import javax.swing.JFrame;

import editPlayspace.EditPlayspaceTable;
import deployPlayspace.DeployPlayspaceGui;
import deployPlayspace.LoginCard;
import deployPlayspace.LocateCard;
import deployPlayspace.EditCard;
import deployPlayspace.EditPrivCard;
import deployPlayspace.UsePrivCard;
import deployPlayspace.ConfirmCard;
import deployPlayspace.DecideNextCard;

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
	private EditCard editCard;
	private EditPrivCard editPrivCard;
	private UsePrivCard usePrivCard;
	private ConfirmCard confirmCard;
	private DecideNextCard decideNextCard;

	private boolean isNewDeployment;
	private String locationPath="";

	//data needed for deployment
	private ServerConnection serverConnection;
	private Object locationFolderId=null; // used only if it is a new PS deployment so know where to add it
	private String newPlayspaceName="";// used only if it is a new PS deployment so know where to add it
	private Object editPlayspaceId=null;
	private String description="";
	private EditPlayspaceTable playspaceTable= null;
	private PermissionsPanel editPermissionsPanel = null;
	private PermissionsPanel usePermissionsPanel = null;

	public PermissionsPanel getUsePermissionsPanel()
	{
		return usePermissionsPanel;
	}

	public void setUsePermissionsPanel(PermissionsPanel usePermissionsPanel)
	{
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

	public EditPlayspaceTable getPlayspaceTable()
	{
		return playspaceTable;
	}

	public void setPlayspaceTable(EditPlayspaceTable playspaceTable)
	{
		this.playspaceTable = playspaceTable;
	}

	public Object getLocationFolderId()
	{
		return locationFolderId;
	}

	public void setLocationFolderId(Object locationFolderId)
	{
		this.locationFolderId = locationFolderId;
	}

	public Object getEditPlayspaceId()
	{
		return editPlayspaceId;
	}

	public void setEditPlayspaceId(Object editPlayspaceId)
	{
		this.editPlayspaceId = editPlayspaceId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getNewPlayspaceName()
	{
		return newPlayspaceName;
	}

	public void setNewPlayspaceName(String newPlayspaceName)
	{
		this.newPlayspaceName = newPlayspaceName;
	}


	public String getLocationPath()
	{
		return locationPath;
	}

	public void setLocationPath(String locationPath)
	{
		this.locationPath = locationPath;
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

	public EditCard getEditCard()
	{
		return editCard;
	}

	public void setEditCard(EditCard editCard)
	{
		this.editCard = editCard;
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

	public DecideNextCard getDecideNextCard()
	{
		return decideNextCard;
	}

	public void setDecideNextCard(DecideNextCard decideNextCard)
	{
		this.decideNextCard = decideNextCard;
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

	public static void main(String[] args)
	{
		JFrame f = createDeployGui();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.show();
	}
}
