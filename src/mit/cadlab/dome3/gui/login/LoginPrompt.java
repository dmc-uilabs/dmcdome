// LoginPrompt.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.login;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.ServerMethodException;
import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPrompt extends JPanel
{
	// todo: remove commented out user/admin login buttons if we think we permanently do not want them
	// right now they are confusing to users
	
	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class

	// new connection panel
	protected String clientURL = "";
	protected JTextField usernameField;
	protected JPasswordField passwordField;
	protected JCheckBox savePwdCheckBox;
	protected JTextField serverField;
	//protected JRadioButton newUserLogin, newAdminLogin;

	// saved logins panel
	protected JComboBox loginsComboBox;
	//protected JRadioButton savedUserLogin, savedAdminLogin;
	protected JButton savedLoginButton;

	protected ServerConnection connectionInfo = null;

	/**
	 * LoginPrompt
	 * @param parentComponent
	 * @return ServerConnectionif successful
	 *    otherwise, return null if cancelled
	 */
	public static ServerConnection showDialog(Component parentComponent)
	{
		return showDialog(parentComponent, null, null);
	}

	/**
	 * LoginPrompt
	 * @param parentComponent
	 * @param clientURL
	 * @return ServerConnectionif successful
	 *    otherwise, return null if cancelled
	 */
	public static ServerConnection showDialog(Component parentComponent, String clientURL)
	{
		return showDialog(parentComponent, null, clientURL);
	}

	/**
	 * LoginPrompt
	 * @param parentComponent
	 * @param serverPort server:port (if this is specified, implies recent logins will not be shown)
	 * @param clientURL client URL
	 * @return ServerConnection if successful
	 *    otherwise, return null if cancelled
	 */
	public static ServerConnection showDialog(Component parentComponent, String serverPort, String clientURL)
	{
		LoginPrompt prompt = new LoginPrompt(serverPort, clientURL);
		JDialog dialog = DialogFactory.createDialog(parentComponent, "Login to DOME Server", prompt, true, false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.show();
		return prompt.connectionInfo;
	}

	private LoginPrompt(String serverPort, String clientURL)
	{
		this.clientURL = clientURL;
		LoginInfo[] recentLogins = (LoginInfo[]) LoginsCache.getLogins().toArray(new LoginInfo[]{});
		if (recentLogins == null || recentLogins.length == 0 || serverPort != null)
			formatNewLoginPanel(this, serverPort);
		else {
			createSavedLoginPanel(recentLogins, serverPort);
		}
	}

	protected void createSavedLoginPanel(LoginInfo[] recentLogins, String serverPort)
	{
		JPanel newLoginPanel = new JPanel();
		formatNewLoginPanel(newLoginPanel, serverPort);
		JComponent[] comps = {makeRecentLoginsPanel(recentLogins), newLoginPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// configured with no border (each panel has 5 pixel border)
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	protected void formatNewLoginPanel(JPanel p, String serverPort)
	{
		usernameField = Templates.makeTextField("");
		passwordField = new JPasswordField(25);
		savePwdCheckBox = Templates.makeCheckBox("remember password", true);
		serverField = Templates.makeTextField("");
		if (serverPort != null) {
			serverField.setText(serverPort);
			serverField.setEditable(false);
		}
		//newUserLogin = Templates.makeRadioButton("user", true);
		//newAdminLogin = Templates.makeRadioButton("administrator");
		//ButtonGroup loginType = new ButtonGroup();
		//loginType.add(newUserLogin);
		//loginType.add(newAdminLogin);
		JComponent[] comps = {Templates.makeLabel("user name:"),
		                      usernameField,
		                      Templates.makeLabel("password:"),
		                      passwordField,
		                      savePwdCheckBox,
		                      Templates.makeLabel("server:"),
		                      serverField,
		                      //Templates.makeLabel("login as:"),
		                      //newUserLogin,
		                      //newAdminLogin,
		                      makeNewLoginButtonPanel()
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// configured with 5 pixel borders around
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(1, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 2, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			//new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 4, 2, 1, 1.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
		};

		Templates.layoutGridBag(p, comps, gbcs);
	}

	protected JPanel makeNewLoginButtonPanel()
	{
		JPanel p = new JPanel();
		JButton newLoginButton = Templates.makeButton("login", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                WaitCursorUtils.showWaitCursor(true, LoginPrompt.this);
				processNewLogin();
                WaitCursorUtils.showWaitCursor(false, LoginPrompt.this);
			}
		});
		JButton cancelButton = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		JComponent[] comps = {newLoginButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// configured with no border
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	protected JPanel makeRecentLoginsPanel(LoginInfo[] recentLogins)
	{
		JPanel p = new JPanel();
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		loginsComboBox = Templates.makeComboBox(recentLogins);
		loginsComboBox.setOpaque(false);
		loginsComboBox.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		loginsComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				LoginInfo info = (LoginInfo) loginsComboBox.getSelectedItem();
				setSavedLoginTypeButtons(info.getLoginType());
			}
		});
//		savedUserLogin = Templates.makeRadioButton("user");
//		savedAdminLogin = Templates.makeRadioButton("administrator");
//		savedUserLogin.setBackground(Templates.DARKER_BACKGROUND_COLOR);
//		savedAdminLogin.setBackground(Templates.DARKER_BACKGROUND_COLOR);
//		ButtonGroup loginType = new ButtonGroup();
//		loginType.add(savedUserLogin);
//		loginType.add(savedAdminLogin);
		LoginInfo info = recentLogins[0];
		setSavedLoginTypeButtons(info.getLoginType());
		JComponent[] comps = {Templates.makeLabel("recent logins:"),
		                      loginsComboBox,
		                      //Templates.makeLabel("login as:"),
		                      //savedUserLogin,
		                      //savedAdminLogin,
		                      makeSavedLoginButtonPanel()
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// configured with 5 pixel borders around
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			//new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	protected void setSavedLoginTypeButtons(String loginType)
	{
//		if (loginType.equals(LoginUtils.GUEST)) {
//			savedAdminLogin.setSelected(false);
//			savedUserLogin.setSelected(false);
//			savedUserLogin.setEnabled(true);
//		} else {
//			savedAdminLogin.setEnabled(true);
//			savedUserLogin.setEnabled(true);
//		}
//		if (loginType.equals(LoginUtils.ADMIN))
//			savedAdminLogin.setSelected(true);
//		else if (loginType.equals(LoginUtils.USER))
//			savedUserLogin.setSelected(true);
	}

	protected JPanel makeSavedLoginButtonPanel()
	{
		JPanel p = new JPanel();
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		savedLoginButton = Templates.makeButton("login", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                WaitCursorUtils.showWaitCursor(true, LoginPrompt.this);
				processSavedLogin();
                WaitCursorUtils.showWaitCursor(false, LoginPrompt.this);
			}
		});
		savedLoginButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		JButton cancelButton = Templates.makeButton("cancel", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		cancelButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		JComponent[] comps = {savedLoginButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// configured with no border
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	protected void processNewLogin()
	{
		String user = usernameField.getText().trim();
		String loginType = null;
		if (user.equals("") || user.equalsIgnoreCase("guest")) { // guest login
			loginType = LoginUtils.GUEST;
		} else {
			loginType = LoginUtils.USER;
			//loginType = newAdminLogin.isSelected() ? LoginUtils.ADMIN : LoginUtils.USER;
		}
		char[] pwdChars = passwordField.getPassword();
		String pwd = new String(pwdChars);
		// clear password char array
		for (int i = 0; i < pwdChars.length; ++i)
			pwdChars[i] = '0';
		byte[] encryptedPwd = LoginUtils.encryptPassword(pwd);

		String svrPort = serverField.getText().trim(); // needs to include port
		if (svrPort.equals(""))
			svrPort = DomeServer.getDefaultServerPort();
		else if (svrPort.indexOf(':') == -1)
			svrPort += ":8080";
		try {
			ServerConnection conn = LoginUtils.login(loginType, user, clientURL, svrPort, encryptedPwd);
			if (savePwdCheckBox.isSelected()) {
				LoginsCache.addLoginInfo(new LoginInfo(conn.getLoginName(), svrPort, encryptedPwd, loginType));
			}
			connectionInfo = conn;
			dispose();
		} catch (Exception e) {
			handleLoginError(e, this);
		}
	}

	protected void processSavedLogin()
	{
		LoginInfo info = (LoginInfo) loginsComboBox.getSelectedItem();
		String loginType = info.getLoginType();
		if (!loginType.equals(LoginUtils.GUEST))
			loginType = LoginUtils.USER;
			//loginType = savedAdminLogin.isSelected() ? LoginUtils.ADMIN : LoginUtils.USER;
		try {
			ServerConnection conn = LoginUtils.login(loginType, info.getUsername(), clientURL, info.getServer(), info.getEncryptedPwd());
			info.setLoginType(loginType);
			LoginsCache.addLoginInfo(info);
			connectionInfo = conn;
			dispose();
		} catch (Exception e) {
			handleLoginError(e, this);
		}
	}

	public static void showConnectionErrorMsg(Component parent)
	{
		OneButton1Msg.showError(parent, "Login error", "Cannot connect to the specified server!", "ok",
		                        new Dimension(240, 100));
	}

	public static void showLoginProblemWarning(Component parent)
	{
		OneButton1Msg.showWarning(parent, "Login warning", "Incorrect user name or password!", "try again",
		                          new Dimension(240, 100));
	}

	public static void showServerMethodError(Exception ex, Component parent)
	{
		OneButton1Msg.showError(parent, "Login error", ex.getMessage(), "ok",
		                        new Dimension(400, 100));
	}

	protected void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}

	public static void handleLoginError(Exception ex, Component parent)
	{

		if (ex instanceof UnreachableServerException) {
			showConnectionErrorMsg(parent);
		} else if (ex instanceof ServerMethodException) {
			ServerMethodException e = (ServerMethodException) ex;
			if (e.getCode() == DbErrors.XMLRPC_LOGIN_FAILED || e.getCode() == DbErrors.XMLRPC_NO_SUCH_USER_GROUP)
				showLoginProblemWarning(parent);
			else
				showServerMethodError(e, parent);
		} else
			showServerMethodError(ex, parent);
	}
}
