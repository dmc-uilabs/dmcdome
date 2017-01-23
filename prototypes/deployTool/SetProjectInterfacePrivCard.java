package deployTool;

import mit.cadlab.dome3.gui.deploy.components.DeployProjectInterfaceData;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelComboBoxModel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 8:40:18 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card used to set interface priviledes during deployment
 */
public class SetProjectInterfacePrivCard extends JPanel
{
	public static final GridBagConstraints gbc = null;
	public static final String INTERFACES_AVAILABLE = "yes";
	public static final String NO_INTERFACES_AVAILABLE = "no";
	public static final String NO_INTERFACES_PERMISSONS = "permissions";

	private DeployTool data;
	private DeployToolGui deployGui;

	private JPanel interfaceUsePrivPanel = new JPanel(); //the main panel that has cards with combo box for selecting interaces
	//or the no interfaces available card
	private JPanel interfacesCardPanel = new JPanel(); //the card panel used to store the permissions panels for the different intefaces

	private ServerPanelComboBoxModel interfaceModel;
	private JComboBox interfaceCombo;

	public void setUsePrivPanel()
	{
		if (data.getToolProjectData().getNumAvailable() > 0) {
			String permissionCategory = PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES;
			ServerConnection svr = data.getServerConnection();
			PermissionsPanel p = null;

			DeployProjectInterfaceData[] intData =
			        (DeployProjectInterfaceData[]) (data.getToolProjectData().getInterfaces().toArray(new DeployProjectInterfaceData[]{}));

			((CardLayout2) interfaceUsePrivPanel.getLayout()).show(interfaceUsePrivPanel, INTERFACES_AVAILABLE);
			resetInterfacePriv();

			if (data.isNewDeployment()) {
				for (int i = 0; i < intData.length; i++) {
					p = new PermissionsPanel(svr, permissionCategory, null, intData[i].getName(), false, false);
					data.getProjectInterfacePermissions().put(Integer.toString(i), p);
					// only add interface to combo box if they are avaiable
					if ((intData[i]).getIsAvailable().booleanValue()) {
						interfacesCardPanel.add(Integer.toString(i), p.getGui());
						interfaceModel.addEntry(intData[i].getName(), Integer.toString(i));
					}
				}
				interfaceCombo.setSelectedIndex(0);
				((CardLayout2) interfacesCardPanel.getLayout()).first(interfacesCardPanel);
				deployGui.successfulCompletion();
				return;
			} else if (PermissionFunctions.sessionUserHasPermission(svr, data.getRedeployToolProjectId(), PermissionUtils.PERMISSION_TO_SET_INTERFACE_USE_PRIV)) {
				for (int i = 0; i < intData.length; i++) {
					String deployedId = (intData[i]).getDeployId();
					if (deployedId != null)
						p = new PermissionsPanel(svr, permissionCategory, deployedId, intData[i].getName(), true, false);
					else {
						//this should never, ever happen
						System.out.println("error in SetInterfacePrivCard: deployed Id for interface " + intData[i].getName() + " is null");
						p = new PermissionsPanel(svr, permissionCategory, null, intData[i].getName(), false, false); //stuffing a new panel in so that things can go on
					}
					data.getProjectInterfacePermissions().put(Integer.toString(i), p);
					//only add the available ones to the combo box
					if ((intData[i]).getIsAvailable().booleanValue()) {
						interfacesCardPanel.add(Integer.toString(i), p.getGui());
						interfaceModel.addEntry(intData[i].getName(), Integer.toString(i));
					}
				}
				interfaceCombo.setSelectedIndex(0);
				((CardLayout2) interfacesCardPanel.getLayout()).first(interfacesCardPanel);
			} else { //do not have permission to set interface use privs.
				((CardLayout2) interfaceUsePrivPanel.getLayout()).show(interfaceUsePrivPanel, NO_INTERFACES_PERMISSONS);
			}
		} else {
			((CardLayout2) interfaceUsePrivPanel.getLayout()).show(interfaceUsePrivPanel, NO_INTERFACES_AVAILABLE);
		}
	}

	public void resetInterfacePriv()
	{
		interfacesCardPanel.removeAll();
		interfaceModel.removeAllElements();
		if (!((data.getProjectInterfacePermissions() == null) || (data.getProjectInterfacePermissions().isEmpty()))) {
			data.getProjectInterfacePermissions().clear();
		}

	}

	public SetProjectInterfacePrivCard(DeployTool deployData, DeployToolGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setProjectInterfaceUsePrivCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who may use the iProject interfaces?", Templates.FONT12B);

		interfaceUsePrivPanel.setLayout(new CardLayout2());
		interfaceUsePrivPanel.add(INTERFACES_AVAILABLE, makeInterfacesPanel());
		interfaceUsePrivPanel.add(NO_INTERFACES_AVAILABLE, makeNoInterfacesPanel());
		interfaceUsePrivPanel.add(NO_INTERFACES_PERMISSONS, makeNoPermissionsPanel());

		JComponent[] comps = {msg1, interfaceUsePrivPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	private JPanel makeInterfacesPanel()
	{
		JPanel p = new JPanel();
		interfacesCardPanel.setLayout(new CardLayout2());
		interfaceModel = new ServerPanelComboBoxModel();
		interfaceCombo = Templates.makeDComboBox(interfaceModel);
		interfaceCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					((CardLayout2) interfacesCardPanel.getLayout()).show(interfacesCardPanel, interfaceModel.getSelectedValue());
				} catch (Exception interfaceCombo) {
					System.out.println("invalid key in combo box");
				}
			}
		});
		Templates.setFixedSize(interfaceCombo, new Dimension(230, 20));

		JComponent[] comps = {interfaceCombo, interfacesCardPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	private JPanel makeNoInterfacesPanel()
	{
		JPanel p = new JPanel();
		JLabel msg = Templates.makeLabel("No iProject interfaces are available.");
		JPanel fill = new JPanel();

		JComponent[] comps = {msg, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeNoPermissionsPanel()
	{
		JPanel p = new JPanel();
		JLabel msg = Templates.makeLabel("You have not been given privileges to edit this permission set.");
		JPanel fill = new JPanel();

		JComponent[] comps = {msg, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Set interface privileges");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SetProjectInterfacePrivCard(null, null));
		f.show();
	}
}
