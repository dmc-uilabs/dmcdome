package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.gui.login.LoginPrompt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 8, 2003
 * Time: 11:14:19 PM
 * To change this template use Options | File Templates.
 */
public class LoginCard extends JPanel
{
    private static final GridBagConstraints gbc = null;

    private JButton _loginButton;

    private DeployAnalysisTool _data;
    private DeployToolGui _deployGui;
    private JLabel _serverLabel;
    private JLabel _userLabel;

    public LoginCard(DeployAnalysisTool deployData, DeployToolGui gui)
	{
		_data = deployData;
		_deployGui = gui;
		if (_data != null)
            _data.setLoginCard(this);
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Let's get started!", Templates.FONT12);
		JLabel msg2 = Templates.makeLabel("On which server are you deploying?", Templates.FONT12B);

		_loginButton = Templates.makeButton("login", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ServerConnection conn = LoginPrompt.showDialog(LoginCard.this);

				if (conn != null) {
					_deployGui.setFarthestCompleted(0);
					_data.setLocalToolPath("");

                    //not nice, but needed so that if you have backed up in wizard and
					//then go to next panel and click redeploy before selecting the new file
					if (_data.getServerConnection() != null) _data.getServerConnection().logout();
					_data.setServerConnection(conn);
                    _data.getDeployProject().setServerConnection(conn);
					_serverLabel.setText("You are logged into the server: " + conn.getServerPort());
					if (conn.getLoginType() == LoginUtils.ADMIN)
						_userLabel.setText("as administrator: " + conn.getLoginName());
					else
						_userLabel.setText("as user: " + conn.getLoginName());
					_deployGui.successfulCompletion();
				}
			}
		});

		_serverLabel = Templates.makeLabel(" ");
		_userLabel = Templates.makeLabel(" ");

		JPanel fill = new JPanel();

		JComponent[] comps = {
            msg1,
            msg2,
            _loginButton,
            _serverLabel,
            _userLabel,
            fill
        };
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



}
