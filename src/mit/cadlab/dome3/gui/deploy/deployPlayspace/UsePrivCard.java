package mit.cadlab.dome3.gui.deploy.deployPlayspace;

import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;


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
public class UsePrivCard extends JPanel
{
	public static final GridBagConstraints gbc = null;
	private DeployPlayspace data;
	private DeployPlayspaceGui deployGui;
	private JPanel usePrivPanel = new JPanel();

	public void setUsePrivPanel()
	{
		String permissionCategory = PermissionUtils.PLAYSPACE_USE_PRIVILEGES;
		PermissionsPanel panel = null;

		if (data.isNewDeployment()) {
			panel = new PermissionsPanel(data.getServerConnection(), permissionCategory, null, data.getPlayspaceData().getName(), false, false);
			usePrivPanel.add("use permissions", panel.getGui());
			((CardLayout2) usePrivPanel.getLayout()).last(usePrivPanel);
			data.setUsePermissionsPanel(panel);
		} else if (PermissionFunctions.sessionUserHasPermission(data.getServerConnection(), data.getRedeployPlayspaceId(), PermissionUtils.PERMISSION_TO_SET_PLAYSPACE_USE_PRIV)) {
			ServerConnection svr = data.getServerConnection();
			panel = new PermissionsPanel(svr, permissionCategory, data.getRedeployPlayspaceId(), data.getPlayspaceData().getName(), true, false);
			data.setUsePermissionsPanel(panel);
			usePrivPanel.add("use permissions", panel.getGui());
			((CardLayout2) usePrivPanel.getLayout()).last(usePrivPanel);
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
			usePrivPanel.add("edit permissions", p);
			((CardLayout2) usePrivPanel.getLayout()).last(usePrivPanel);
		}
	}


	public UsePrivCard(DeployPlayspace deployData, DeployPlayspaceGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setUsePrivCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who may enter the playspace?", Templates.FONT12B);
		usePrivPanel.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, usePrivPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Set interface privileges");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new UsePrivCard(null, null));
		f.show();
	}
}
