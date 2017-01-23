package deployProject;

import mit.cadlab.dome.gui.serverPanel.ServerPanel;
import mit.cadlab.dome.gui.serverPanel.ServerPanelSelectionListener;
import mit.cadlab.dome.gui.fileSystem.Folder;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome.swing.CardLayout2;
import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.server.db.DeployFilesDbFunctions;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Created by IntelliJ IDEA.
 * Date: Feb 26, 2003
 * Time: 5:56:22 PM
 * To change this template use Options | File Templates.
 */
/**
 * Card for selecting where the playspace will be deployed to on the server
 */
public class LocateCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

	private JTextField selectedLocation;
	private ServerPanel serverPanel;
	private JPanel serverPanelCard = new JPanel();

	private String selectionPath = "";
	private Object selectionId = null;

	private DeployProject data;
	private DeployProjectGui deployGui; //used later to call the sucessfulCompletion method

	private ServerPanelSelectionListener l = new ServerPanelSelectionListener()
	{
		public void selectionChanged(String path, Object id, ServerConnection svr)
		{
			setDataAndTextField(path, id);
		}
	};


	public void setServerPanelCard()
	{
		serverPanel = new ServerPanel(data.getServerConnection(), ServerPanel.MODEL_DEPLOY);
		((ServerPanel) ((CardLayout2) (serverPanelCard.getLayout())).getActiveComponent()).removeSelectionListeners(l);
		serverPanelCard.remove(0);
		serverPanel.addSelectionListeners(l);
		serverPanelCard.add("serverPanel", serverPanel);
		serverPanelCard.validate();
		initGui();

		if (!data.isNewDeployment()) { //redeploy case so need to set selection to correct playspace
			String root = DeployFilesFunctions.getRootForProject(data.getServerConnection(), data.getRedeployProjectId());
			if (root.startsWith(Folder.SERVER_ROOT + DeployFilesDbFunctions.slash))
				serverPanel.setRootTo(ServerPanel.SERVER);
			else if (root.startsWith(Folder.GROUPS_ROOT + DeployFilesDbFunctions.slash))
				serverPanel.setRootTo(ServerPanel.GROUP);
			else if (root.startsWith(Folder.USERS_ROOT + DeployFilesDbFunctions.slash))
				serverPanel.setRootTo(ServerPanel.USER);
			else
				serverPanel.setRootTo(ServerPanel.SELF);

			serverPanel.setObjectSelection(data.getRedeployProjectId(), "project"); //this also locks the table selection to this item
			deployGui.successfulCompletion();
		}
	}

	public ServerPanel getServerPanel()
	{
		return (ServerPanel) ((CardLayout2) (serverPanelCard.getLayout())).getActiveComponent();
	}

	public LocateCard(DeployProject deployData, DeployProjectGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setLocateCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	/**
	 * used to reset data in Gui components after successful deployment
	 */
	public void initGui()
	{
		selectedLocation.setText("");
		selectedLocation.setBackground(Color.white);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Where will the iProject be located the server?", Templates.FONT12B);

		JLabel pathLabel = Templates.makeLabel("The current deployment destination is:", Templates.FONT12B);
		selectedLocation = Templates.makeTextField("");
		selectedLocation.setEditable(false);

		serverPanelCard.setLayout(new CardLayout2());
		serverPanel = new ServerPanel(data.getServerConnection(), ServerPanel.MODEL_DEPLOY);
		serverPanelCard.add("serverPanel",serverPanel);

		JComponent[] comps = {msg1, serverPanelCard, pathLabel, selectedLocation};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(20, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	private void setDataAndTextField(String path, Object id)
	{
		selectionPath = path;
		selectionId = id;
	
		if (selectionId != null) {
			selectedLocation.setText(selectionPath);
			data.setLocationPath(selectionPath);
			data.setLocationFolderId(selectionId);
			deployGui.successfulCompletion();
		}
		else {
			selectedLocation.setText("");
			deployGui.enableNextButton(false);
		}
	}


	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy select location on server");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new LocateCard(null,null));
		f.show();
	}
}
