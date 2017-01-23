package deployTool;

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Apr 2, 2003
 * Time: 7:51:41 PM
 * To change this template use Options | File Templates.
 */
public class SetProjectVisibility extends JPanel
{
	public static final GridBagConstraints gbc = null;

	private DeployTool data;
	private DeployToolGui deployGui;
	private JPanel setVisibilityCard = new JPanel();

	public void setProjectVisCard()
	{
		String permissionCategory = PermissionUtils.PROJECT_VISIBILITY_PRIVILEGES;
		PermissionsPanel panel = null;

		if (data.isNewDeployment()) {
			panel = new PermissionsPanel(data.getServerConnection(), permissionCategory, null, data.getToolProjectData().getName(), false, false);
			setVisibilityCard.add("visibility permissions", panel.getGui());
			((CardLayout2) setVisibilityCard.getLayout()).last(setVisibilityCard);
			data.setVisibilityPermissionsPanel(panel);
		} else if (PermissionFunctions.sessionUserHasPermission(data.getServerConnection(), data.getRedeployToolProjectId(), PermissionUtils.PERMISSION_TO_SET_PROJECT_VISIBILITY_PRIVS)) {
			ServerConnection svr = data.getServerConnection();
			panel = new PermissionsPanel(svr, permissionCategory, data.getRedeployToolProjectId(), data.getToolProjectData().getName(), true, false);
			setVisibilityCard.add("visibility permissions", panel.getGui());
			((CardLayout2) setVisibilityCard.getLayout()).last(setVisibilityCard);
			data.setVisibilityPermissionsPanel(panel);
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
			setVisibilityCard.add("visibility permissions", p);
			((CardLayout2) setVisibilityCard.getLayout()).last(setVisibilityCard);
		}
	}


	public SetProjectVisibility(DeployTool deployData, DeployToolGui gui)
	{
		data = deployData;
		deployGui = gui;
		if (data != null) data.setProjectVisCard(this);
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who may see inside the iProject.", Templates.FONT12B);
		JLabel msg2 = Templates.makeLabel("(subject to the permissions set on each model within)");

		setVisibilityCard.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, msg2, setVisibilityCard};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(10, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy set visibility card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SetProjectVisibility(null, null));
		f.show();
	}
}
