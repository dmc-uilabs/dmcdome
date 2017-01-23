package deployProject;

import mit.cadlab.dome.gui.deploy.components.DeployIntegrationModelInterfaceSelectionTable;
import mit.cadlab.dome.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome.gui.deploy.components.DeployModelData;
import mit.cadlab.dome.gui.permission.PermissionUtils;
import mit.cadlab.dome.gui.permission.PermissionsPanel;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.functions.PermissionFunctions;
import mit.cadlab.dome.swing.CardLayout2;
import mit.cadlab.dome.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Apr 2, 2003
 * Time: 11:09:21 AM
 * To change this template use Options | File Templates.
 */
public class SetIntegrationModelPrivCard extends JPanel
{
	public static final GridBagConstraints gbc = null;
	public static final String IMODELS = "yes";
	public static final String NO_IMODELS = "no";
	public static final String NO_PERMISSONS = "permissions";

	private DeployProject data;
	private DeployProjectGui deployGui;
	private JPanel mainCardPanel = new JPanel(); //main card that the panels for the three cases above go into
	private JPanel iModelCardPanel = new JPanel();
	private DeployIntegrationModelInterfaceSelectionTable table;

	private JButton setPrivButton;

	public void setIntegrationModelPrivCard()
	{
		List iModels = data.getProjectData().getIntegrationModels();
		if (iModels!= null || !iModels.isEmpty()) {
			String permissionCategory = PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES;
			ServerConnection svr = data.getServerConnection();
			PermissionsPanel p = null;

			((CardLayout2) mainCardPanel.getLayout()).show(mainCardPanel, IMODELS);
			if (!((data.getiModelInterfacePermissions() == null) || (data.getiModelInterfacePermissions().isEmpty()))) resetIModelPriv();

			boolean onServer;
			if (data.isNewDeployment())
				onServer = false;
			else if (PermissionFunctions.sessionUserHasPermission(svr, data.getRedeployProjectId(), PermissionUtils.PERMISSION_TO_SET_IMODEL_PRIVS))
				onServer = true;
			else
			{
				((CardLayout2) mainCardPanel.getLayout()).show(mainCardPanel, NO_PERMISSONS);
				return;
			}
			iModelCardPanel.add("iModelTable", table = new DeployIntegrationModelInterfaceSelectionTable(data.getProjectData()));
			((CardLayout2)(iModelCardPanel.getLayout())).last(iModelCardPanel);

			table.addTreeSelectionListener(selInterface);

			DeployModelData[] modelData = (DeployModelData[]) (iModels.toArray(new DeployModelData[]{}));
			DeployInterfaceData[] interfaceData;
			List interfaces;
			for (int i =0; i<iModels.size();i++) {
				if (((interfaces = modelData[i].getModelInterfaces()).size()) !=0) {
					interfaceData = (DeployInterfaceData[])(interfaces.toArray(new DeployInterfaceData[]{}));
					for (int j=0; j<interfaceData.length; j++) {
						p = new PermissionsPanel(svr, permissionCategory, interfaceData[j].getDeployId(), interfaceData[j].getName(), onServer, true);
						data.getiModelInterfacePermissions().put(interfaceData[j], p);
						interfaceData[j].addPropertyChangeListener(DeployInterfaceData.IS_AVAILABLE, checkbox);
					}
				}
			}
			iModelCardPanel.validate();
		}
		else
			((CardLayout2) mainCardPanel.getLayout()).show(mainCardPanel, NO_IMODELS);
	}

	public PropertyChangeListener checkbox = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			DeployInterfaceData iface = table.getSelectedInterface();
			if (iface==null) {
				return;
			}
			if (iface.getIsAvailable().booleanValue()) {
				if (data.getiModelInterfacePermissions().containsKey(iface)) {
				    setPrivButton.setEnabled(true);
					return;
				}
			}
			setPrivButton.setEnabled(false);
		}
	};

	public TreeSelectionListener selInterface = new TreeSelectionListener()
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			DeployInterfaceData iface = table.getSelectedInterface();
			if (iface == null) {
				return;
			}
			if (iface.getIsAvailable().booleanValue()) {
				if (data.getiModelInterfacePermissions().containsKey(iface)) {
					setPrivButton.setEnabled(true);
					return;
				}
			}
			setPrivButton.setEnabled(false);
		}
	};


	public SetIntegrationModelPrivCard(DeployProject deployData, DeployProjectGui gui)
	{
		data = deployData;
		deployGui = gui;
		if (data != null) data.setIntegrationModelPrivCard(this);
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private void resetIModelPriv()
	{
		if (!((data.getiModelInterfacePermissions() == null) || (data.getiModelInterfacePermissions().isEmpty()))) {
			data.getiModelInterfacePermissions().clear();
			iModelCardPanel.removeAll();
		}
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		mainCardPanel.setLayout(new CardLayout2());
		mainCardPanel.add(IMODELS, makeIModelPanel());
		mainCardPanel.add(NO_IMODELS, makeNoModelPanel());
		mainCardPanel.add(NO_PERMISSONS, makeNoPermissionsPanel());

		JComponent[] comps = {mainCardPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeNoModelPanel()
	{
		JPanel p = new JPanel();

		JLabel msg = Templates.makeLabel("This project does not contain any iModels.");
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
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeIModelPanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Which integration model interfaces do you want to make available?", Templates.FONT12B);
		JLabel msg2 = Templates.makeLabel("(only to those with permission to see inside of the project)");

		setPrivButton = Templates.makeButton("set use privileges", new ActionListener()
		{
			public void actionPerformed
			        (ActionEvent e)
			{
				DeployInterfaceData iface = table.getSelectedInterface();
				((PermissionsPanel)(data.getiModelInterfacePermissions().get(iface))).show(iModelCardPanel);
			}
		});
		setPrivButton.setEnabled(false);
		iModelCardPanel.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, msg2, setPrivButton, iModelCardPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy set integration model priv card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SetIntegrationModelPrivCard(null, null));
		f.show();
	}
}
