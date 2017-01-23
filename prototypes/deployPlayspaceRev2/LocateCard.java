package deployPlayspaceRev2;

import mit.cadlab.dome.gui.serverPanel.ServerPanel;
import mit.cadlab.dome.gui.serverPanel.ServerPanelSelectionListener;
import mit.cadlab.dome.gui.swing.msg.OneButton1Msg;
import mit.cadlab.dome.gui.fileSystem.Folder;
import mit.cadlab.dome.gui.fileSystem.FileSystemFilters;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.swing.CardLayout2;
import mit.cadlab.dome.swing.Templates;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import deployPlayspace.DeployPlayspace;
import deployPlayspace.DeployPlayspaceGui;

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

	private JRadioButton newPs;
	private JRadioButton editPs;
	private JTextField name;
	private JTextField description;
	private JTextField selectedLocation;
	private ServerPanel serverPanel;
	private JPanel serverPanelCard = new JPanel();

	private String selectionPath = "";
	private Object selectionId = null;

	private DeployPlayspace data;
	private DeployPlayspaceGui deployGui; //used later to call the sucessfulCompletion method

	private ServerPanelSelectionListener l = new ServerPanelSelectionListener()
	{
		public void selectionChanged(String path, Object id, ServerConnection svr)
		{
			setDataAndTextField(path, id);
		}
	};

	/**
	 *
	 * @param serverPanel
	 */
	public void setServerPanelCard(ServerPanel serverPanel)
	{
		((ServerPanel) ((CardLayout2) (serverPanelCard.getLayout())).getActiveComponent()).removeSelectionListeners(l);
		serverPanelCard.remove(0);
		serverPanel.addSelectionListeners(l);
		serverPanelCard.add("serverPanel", serverPanel);
		serverPanelCard.validate();
	}

	public ServerPanel getServerPanel()
	{
		return (ServerPanel) ((CardLayout2) (serverPanelCard.getLayout())).getActiveComponent();
	}

	public void setNewDeploy(boolean b){
		newPs.setSelected(true);
		data.setNewDeployment(true);
	}

	public LocateCard(DeployPlayspace deployData, DeployPlayspaceGui gui)
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

	private ActionListener nameListener = new ActionListener()
	{
		public void actionPerformed (ActionEvent e)
		{
			data.setNewPlayspaceName(name.getText());
			name.setBackground(Color.white);
			if (newPs.isSelected()) {
				if (!(data.getLocationFolderId()==null))
					deployGui.successfulCompletion();
			}
		}
	};

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Where is the playspace located the server?", Templates.FONT12B);

		JLabel nameLabel = Templates.makeLabel("playspace name:");
		name= Templates.makeDTextField("");
		name.addActionListener(nameListener);
		JLabel descriptionLabel = Templates.makeLabel("description:");
		description = Templates.makeDTextField("");
		description.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				data.setDescription(description.getText());
				description.setBackground(Color.white);
			}
		});

		JPanel r = makeRadioPanel();

		JLabel pathLabel = Templates.makeLabel("The current deployment destination is:", Templates.FONT12B);
		selectedLocation = Templates.makeTextField("");
		selectedLocation.setEditable(false);

		serverPanelCard.setLayout(new CardLayout2());
		serverPanel = new ServerPanel(data.getServerConnection(), ServerPanel.PLAYSPACE_DEPLOY);
		serverPanelCard.add("serverPanel",serverPanel);

		JButton selectButton = Templates.makeButton("update", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (selectionId != null) {
					selectedLocation.setText(selectionPath);
					data.setLocationPath(selectionPath);
				    if (newPs.isSelected()) {
						data.setLocationFolderId(selectionId);
					    if (!data.getNewPlayspaceName().equals(""))
						    deployGui.successfulCompletion();
					    else
						    OneButton1Msg.showWarning(null, "Deploy warning",
						            "You still need to name the new playspace", "ok", new Dimension(240, 80));
				    }
					else {
					    data.setLocationFolderId(null);
					    data.setEditPlayspaceId(selectionId);
					    name.removeActionListener(nameListener);
					    //name.setText();//todo on edit; get the playspace object name
					    name.addActionListener(nameListener);
						//get the PS description, update GUI and add to the data model//todo on edit: get the playspace object name
					    deployGui.successfulCompletion();
				    }
				}
				else
					OneButton1Msg.showWarning(null, "Deploy warning",
					        "You must select a folder first", "ok", new Dimension(240, 100));
			}
		});

		JComponent[] comps = {msg1, nameLabel, name, descriptionLabel, description, r, serverPanelCard, pathLabel, selectedLocation, selectButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(20, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 6, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0),
			new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	private JPanel makeRadioPanel()
	{
		JPanel r = new JPanel();

		newPs = Templates.makeRadioButton("create a new playspace", true);

		editPs = Templates.makeRadioButton("edit an existing playspace", false);
		//todo just disabled temporarily
		editPs.setEnabled(false);

		ButtonGroup b = new ButtonGroup();
		b.add(newPs);
		b.add(editPs);
		b.getSelection().addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				deployGui.setFarthestCompleted(1); //reset the number of steps completed
				deployGui.enableNextButton(false);
				if (newPs.isSelected()) {
					name.setEditable(true);
					serverPanel.setSelectionModel(new FileSystemFilters.FoldersFilterTreeSelectionModel());
				}
				else {
					name.setText("");
					name.setEditable(false);
					selectedLocation.setText("");
					selectionPath = "";
					data.setLocationPath("");
					serverPanel.setSelectionModel(new FileSystemFilters.PlayspacesFilterTreeSelectionModel());
				}
			}
		});

		JComponent[] comps = {newPs, editPs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
		};

		Templates.layoutGridBag(r, comps, gbcs);

		return r;
	}

	private void setDataAndTextField(String path, Object id)
	{
		selectionPath = path;
		selectionId = id;
	}


	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy select location on server");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new LocateCard(null,null));
		f.show();
	}
}
