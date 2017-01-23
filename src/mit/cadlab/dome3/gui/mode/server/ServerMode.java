// RunMode.java
package mit.cadlab.dome3.gui.mode.server;

import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.mode.Mode;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.servermode.password.UserChangePasswordPanel;
import mit.cadlab.dome3.gui.servermode.usergroup.ListGroupPanel;
import mit.cadlab.dome3.gui.servermode.usergroup.ListUserPanel;
import mit.cadlab.dome3.gui.servermode.usergroup.NewGroupPanel;
import mit.cadlab.dome3.gui.servermode.usergroup.NewUserPanel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.swing.DefaultWindowTracker;
import mit.cadlab.dome3.DomeClientApplication;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;

public class ServerMode implements Mode
{

	public static final String NAME = "Server";

	private static ServerConnection svrConn = null;
	private static DefaultWindowTracker windows = new ServerModeWindowTracker();
	private static ServerModeFrame usersList = null;
	private static ServerModeFrame groupsList = null;
	private static ServerModeFrame manageFileSpaceFrame = null;

	public static Point getWindowLocation()
	{
		// the following code will be called when the deployplayspace window also exist,
		// to determine the window location
		if (usersList == null && groupsList == null && manageFileSpaceFrame == null)
			return new Point(0, DomeClientApplication.getBottomCoordinate());
		else if (usersList != null && usersList.isShowing() && usersList.getState() == Frame.NORMAL) {
			Point p = usersList.getLocationOnScreen();
			return new Point(p.x + 25, p.y + 25);
		} else if (groupsList != null && groupsList.isShowing() && groupsList.getState() == Frame.NORMAL) {
			Point p = groupsList.getLocationOnScreen();
			return new Point(p.x + 25, p.y + 25);
		} else if (manageFileSpaceFrame != null && manageFileSpaceFrame.isShowing() && manageFileSpaceFrame.getState() == Frame.NORMAL) {
			Point p = manageFileSpaceFrame.getLocationOnScreen();
			return new Point(p.x + 25, p.y + 25);
		}


		return new Point(0, DomeClientApplication.getBottomCoordinate());
	}

	public static String getMenuContext()
	{
		if (svrConn != null)
			return svrConn.getLoginType().equals(LoginUtils.ADMIN) ? ModeContexts.SERVER_ADMIN : ModeContexts.SERVER_USER;
		else
			return ModeContexts.LOGGED_OFF;
	}

	public static ServerConnection getServerConnection()
	{
		return svrConn;
	}

	public static void loginLogout()
	{
		if (svrConn == null)
			login();
		else
			logout();
	}

	private static void login()
	{
		svrConn = LoginPrompt.showDialog(null);
		if (svrConn != null) {
			ServerMenus.setLoggedInMenus(svrConn.getServerPort(), svrConn.getLoginType().equals(LoginUtils.ADMIN));
		}
	}

	private static void logout()
	{
		if (svrConn == null)
			return;
		windows.closeAll();
		svrConn.logout();
		svrConn = null;
		ServerMenus.setLoggedOutMenus();
	}

	public static void close()
	{
		windows.closeAll();
	}

	// user management functions

	public static void showUsersList()
	{
		if (usersList != null) {
			if (usersList.getState() == Frame.ICONIFIED)
				usersList.setState(Frame.NORMAL);
			usersList.show();
			return;
		}
		ListUserPanel lup = new ListUserPanel(svrConn);
		usersList = new ServerModeFrame("Users on " + svrConn.getServerPort(), lup);
		usersList.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				usersList = null;
			}
		});
		usersList.addWindowListener(lup.getWindowAdapter());
		usersList.pack();
		usersList.show();
	}

	public static void newUser()
	{
		if (NewUserPanel.showPanel(null, svrConn)) {
			if (usersList != null)
				((ListUserPanel) usersList.gui).loadData();
		}
	}

	public static void editUser()
	{
		if (usersList != null)
			((ListUserPanel) usersList.gui).edit();
	}

	public static void deleteUser()
	{
		if (usersList != null)
			((ListUserPanel) usersList.gui).delete();
	}

	// group management functions

	public static void showGroupsList()
	{
		if (groupsList != null) {
			if (groupsList.getState() == Frame.ICONIFIED)
				groupsList.setState(Frame.NORMAL);
			groupsList.show();
			return;
		}
		ListGroupPanel lgp = new ListGroupPanel(svrConn);
		groupsList = new ServerModeFrame("Groups on " + svrConn.getServerPort(), lgp);
		groupsList.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				groupsList = null;
			}
		});
		groupsList.addWindowListener(lgp.getWindowAdapter());
		groupsList.pack();
		groupsList.show();
	}

	public static void newGroup()
	{
		if (NewGroupPanel.showPanel(null, svrConn)) {
			if (groupsList != null)
				((ListGroupPanel) groupsList.gui).loadData();
		}
	}

	public static void editGroup()
	{
		if (groupsList != null)
			((ListGroupPanel) groupsList.gui).edit();
	}

	public static void deleteGroup()
	{
		if (groupsList != null)
			((ListGroupPanel) groupsList.gui).delete();
	}

	// user functions

	public static void changeUserPassword()
	{
		UserChangePasswordPanel.showPanel(null, svrConn);
	}

	public static void manageFileSpace()
	{
		if (manageFileSpaceFrame != null) {
			if (manageFileSpaceFrame.getState() == Frame.ICONIFIED)
				manageFileSpaceFrame.setState(Frame.NORMAL);
			manageFileSpaceFrame.show();
			return;
		}
		ServerPanel serverP = new ServerPanel(svrConn, ServerPanel.MANAGE_FILESPACE);
		manageFileSpaceFrame = new ServerModeFrame("Manage file space on " + svrConn.getServerPort(), serverP);
		manageFileSpaceFrame.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				manageFileSpaceFrame = null;
			}
		});
		manageFileSpaceFrame.addWindowListener(serverP.getWindowAdapter());
        manageFileSpaceFrame.setSize(DomeBuildFrame.DEFAULT_SIZE);
		manageFileSpaceFrame.show();
	}

	// admin functions
	public static void shutdownServer()
	{
		if (svrConn == null)
			return;
		JFrame waitWin = StatusWindow.show("Shutting down server:",
		                                   svrConn.getServerPort(), ServerMode.getWindowLocation());
		ShutdownServerWorker worker = new ShutdownServerWorker(waitWin, svrConn.getServerPort());
		worker.start();
	}

	// this is the actual work done by the ShutdownServerWorker
	private static void _shutdownServer()
	{
		windows.closeAll();
		svrConn.shutdown();
		svrConn = null;
		ServerMenus.setLoggedOutMenus();
	}

	// Mode interface
	public static String name()
	{
		return NAME;
	}

	public static void show()
	{
		windows.showAll();
	}

	public static void hide()
	{
		windows.hideAll();
	}

	public static void exit()
	{
		logout();
	}

	// --- actions for menus and buttons --------------------
	public static AbstractAction loginLogoutAction = new AbstractAction("Login")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.loginLogout();
		}
	};

	public static AbstractAction closeAllAction = new AbstractAction("Close all")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.close();
		}
	};

	public static AbstractAction showUsersListAction = new AbstractAction("List Users")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.showUsersList();
		}
	};

	public static AbstractAction newUserAction = new AbstractAction("New")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.newUser();
		}
	};

	public static AbstractAction editUserAction = new AbstractAction("Edit")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.editUser();
		}
	};

	public static AbstractAction deleteUserAction = new AbstractAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.deleteUser();
		}
	};

	public static AbstractAction showGroupsListAction = new AbstractAction("List Groups")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.showGroupsList();
		}
	};

	public static AbstractAction newGroupAction = new AbstractAction("New")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.newGroup();
		}
	};

	public static AbstractAction editGroupAction = new AbstractAction("Edit")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.editGroup();
		}
	};

	public static AbstractAction deleteGroupAction = new AbstractAction("Delete")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.deleteGroup();
		}
	};

	public static AbstractAction manageFileSpaceAction = new AbstractAction("Manage file space")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.manageFileSpace();
		}
	};


	public static AbstractAction changePasswordAction = new AbstractAction("Change password")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.changeUserPassword();
		}
	};

	public static AbstractAction shutdownServerAction = new AbstractAction("Shutdown server")
	{
		public void actionPerformed(ActionEvent e)
		{
			ServerMode.shutdownServer();
		}
	};

	private static class ServerModeWindowTracker extends DefaultWindowTracker
	{
		public void closeAll()
		{
			// traverse vector backwards since closing may be destructive to vector
			int i = children.size() - 1;
			for (; i >= 0; --i) {
				Object obj = children.get(i);
				if (obj instanceof Window)
					((Window) obj).dispose();
			}
		}
	}

	public static class ServerModeFrame extends JFrame
	{
		JComponent gui = null;

		public ServerModeFrame(String title, JComponent gui) throws HeadlessException
		{
			super(title);
			this.gui = gui;
			getContentPane().add(gui);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent e)
				{
					ServerMode.windows.removeChildWindow(ServerModeFrame.this);
				}

				public void windowActivated(WindowEvent e)
				{
					ServerMode.windows.notifyInFront(ServerModeFrame.this);
				}
			});
			setLocation(getWindowLocation());
		}

		public JComponent getContentPanel()
		{
			return gui;
		}
	}

	static class ShutdownServerWorker extends SwingWorker
	{
		JFrame waitWin;
		String server;

		public ShutdownServerWorker(JFrame waitWin, String svrName)
		{
			this.waitWin = waitWin;
			this.server = svrName;
		}

		public Object construct()
		{
			try {
				_shutdownServer();
			}
			catch (Exception e) {
				OneButton1Msg.showError(null, "Error shutting down " + server,
				                        e.getMessage(),
				                        "ok", new Dimension(200, 1));
			}
			return new Object();
		}

		public void finished()
		{
			waitWin.setVisible(false);
			waitWin.dispose();
		}
	};

}
