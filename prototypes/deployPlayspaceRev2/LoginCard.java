package deployPlayspaceRev2;

import mit.cadlab.dome.gui.login.LoginPrompt;
import mit.cadlab.dome.gui.serverPanel.ServerPanel;
import mit.cadlab.dome.network.client.connection.LoginUtils;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import deployPlayspace.DeployPlayspace;
import deployPlayspace.DeployPlayspaceGui;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 4:26:11 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card for the login step in deployment
 */
public class LoginCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

	private DeployPlayspace data;
	private DeployPlayspaceGui deployGui;

	private JButton loginButton;
	private JLabel serverLabel;
	private JLabel userLabel;

	public LoginCard(DeployPlayspace deployData, DeployPlayspaceGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setLoginCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("It's almost time to play!", Templates.FONT12);
		JLabel msg2 = Templates.makeLabel("On which server are you deploying?", Templates.FONT12B);

		loginButton = Templates.makeButton("login", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ServerConnection conn = LoginPrompt.showDialog(LoginCard.this);

				if (conn != null) {

					deployGui.setFarthestCompleted(0);
					//data.getSetInterfacePrivCard().resetInterfacePriv();
					//data.setModelPermPanel(null);

					// set the deployment to true
					data.getLocateCard().setNewDeploy(true);
					//data.setServerLocationId(null);
					//data.setServerLocationPath(null);

					if (data.getServerConnection() != null) data.getServerConnection().logout();

					// now start gathering the new data.
					data.setServerConnection(conn);
					//create new server view panels used in different cards
					(data.getLocateCard()).setServerPanelCard(new ServerPanel(conn, ServerPanel.PLAYSPACE_DEPLOY));

					//create the new model permissions panel
					//data.getSetModelPrivCard().setModelPermPanel();

					serverLabel.setText("You are logged into the server: " + conn.getServerPort());
					if (conn.getLoginType() == LoginUtils.ADMIN)
						userLabel.setText("as administrator: " + conn.getLoginName());
					else
						userLabel.setText("as user: " + conn.getLoginName());
					deployGui.successfulCompletion();
				}
			}
		});

		serverLabel = Templates.makeLabel(" ");
		userLabel = Templates.makeLabel(" ");

		JPanel fill = new JPanel();

		JComponent[] comps = {msg1, msg2, loginButton, serverLabel, userLabel, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy login card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new LoginCard(null, null));
		f.show();
	}
}
