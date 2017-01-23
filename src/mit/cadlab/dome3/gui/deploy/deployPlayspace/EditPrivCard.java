package mit.cadlab.dome3.gui.deploy.deployPlayspace;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 8:28:43 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;


/**
 * Card used to set editing priveleges on the playspace during deployment
 */
public class EditPrivCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

	private DeployPlayspace data;
	private DeployPlayspaceGui deployGui;
	private JPanel editPrivPanel = new JPanel();

	public void setEditPrivPanel()
	{
		String permissionCategory = PermissionUtils.PLAYSPACE_EDIT_PRIVILEGES;
		PermissionsPanel panel = null;

		if (data.isNewDeployment()) {
			panel = new PermissionsPanel(data.getServerConnection(), permissionCategory, null, data.getPlayspaceData().getName(), false, false);
			editPrivPanel.add("edit permissions", panel.getGui());
			((CardLayout2) editPrivPanel.getLayout()).last(editPrivPanel);
			data.setEditPermissionsPanel(panel);
		} else if (PermissionFunctions.sessionUserHasPermission(data.getServerConnection(), data.getRedeployPlayspaceId(), PermissionUtils.PERMISSION_TO_SET_PLAYSPACE_EDIT_PRIV)) {
			ServerConnection svr = data.getServerConnection();
			panel = new PermissionsPanel(svr, permissionCategory, data.getRedeployPlayspaceId(), data.getPlayspaceData().getName(), true, false);
			editPrivPanel.add("edit permissions", panel.getGui());
			((CardLayout2) editPrivPanel.getLayout()).last(editPrivPanel);
			data.setEditPermissionsPanel(panel);
		} else {
			JPanel p = new JPanel();
			JLabel msg = Templates.makeLabel("You do have not been given privileges to edit this permission set.");
			JPanel fill = new JPanel();
			JComponent[] comps = {msg, fill};
			// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
			GridBagConstraints[] gbcs = {
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
				new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
			};
			Templates.layoutGridBag(p, comps, gbcs);
			editPrivPanel.add("edit permissions", p);
			((CardLayout2) editPrivPanel.getLayout()).last(editPrivPanel);
		}
	}

	public EditPrivCard(DeployPlayspace deployData, DeployPlayspaceGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setEditPrivCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who will be able to edit the playspace?", Templates.FONT12B);
		editPrivPanel.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, editPrivPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy playspace edit privildges");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new EditPrivCard(null, null));
		f.show();
	}

}
