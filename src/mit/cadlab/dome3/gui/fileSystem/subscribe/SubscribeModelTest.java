package mit.cadlab.dome3.gui.fileSystem.subscribe;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.functions.UserGroupDbFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.apache.xmlrpc.XmlRpcException;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.subscribe.SubscribeModelFileSystemTable;
import mit.cadlab.dome3.DomeInit;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 5, 2003
 * Time: 6:07:48 PM
 * To change this template use Options | File Templates.
 */
public class SubscribeModelTest extends JFrame
{

	private static JFrame f;
	private static ServerConnection m_serverConnection;
	private static int userGroupId;

	public static BrowseModelFileSystemTable myselfTable;
	public static BrowseModelFileSystemTable usersTable;
	public static BrowseModelFileSystemTable groupsTable;
	public static BrowseModelFileSystemTable serverTable;

	public static BrowseModelFileSystemTable currentTable;

	public static String displayScope;
	private static int currentViewSelection;


	public static void main(String[] args)
	{

		DomeInit.initializeDOME();
		try {
			DbUtils.setDbUrl(9001);
			//m_serverConnection = getServerConnection(LoginUtils.GUEST, "", "localhost:8080", "");
			m_serverConnection = getServerConnection(LoginUtils.USER, "t1", "localhost:8080", "t1");
			//	m_serverConnection = getServerConnection(LoginUtils.ENTER, "root", "localhost:8080", "cadlab");
			//	UserGroupDbFunctions.editUserGroupInfo(0,"root", "", true, true, "ACTIVE");

			myselfTable = new SubscribeModelFileSystemTable(m_serverConnection, Folder.USER_HOME);
			usersTable = new SubscribeModelFileSystemTable(m_serverConnection, Folder.USERS_ROOT);
			groupsTable = new SubscribeModelFileSystemTable(m_serverConnection, Folder.GROUPS_ROOT);
			serverTable = new SubscribeModelFileSystemTable(m_serverConnection, Folder.SERVER_ROOT);
			currentTable = myselfTable;

			f = new JFrame("Model Subscriber");

			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add(makeControlPanel(), BorderLayout.NORTH);
			f.getContentPane().add(currentTable, BorderLayout.SOUTH);

			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.pack();
			f.show();
		} catch (Exception e) {
			if (e instanceof XmlRpcException) {
				XmlRpcException ex = (XmlRpcException) e;
				System.out.println(ex.code + "\t" + ex.getMessage());
			}
			System.out.println(e);
		}
	}


	private static JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());

		String[] items = {"Myself", "Users", "Groups", "Server"};
		JComboBox cbType = new JComboBox(items);
		cbType.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox cb = (JComboBox) e.getSource();
				String item = (String) cb.getSelectedItem();
				switchTableTo(item);
				f.show();
				f.repaint();
			}
		});

		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentTable.refresh();
				f.show();
				f.repaint();
			}
		});

		final String[] views = {"Interface", "System", "Build"};
		final JComboBox viewType = new JComboBox(views);
		viewType.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox cb = (JComboBox) e.getSource();
				currentTable.switchInterfaceView((String) cb.getSelectedItem());


				//currentViewSelection = viewType.getSelectedIndex();
				f.show();
				f.repaint();
			}
		});


		p.add(cbType);
		p.add(refreshButton);
		p.add(viewType);
		return p;
	}

	private static void switchTableTo(String type)
	{

		displayScope = type;
		f.getContentPane().remove(currentTable);
		if (type.equalsIgnoreCase("myself")) {
			currentTable = myselfTable;
		} else if (type.equalsIgnoreCase("users")) {
			currentTable = usersTable;
		} else if (type.equalsIgnoreCase("groups")) {
			currentTable = groupsTable;
		} else if (type.equalsIgnoreCase("server")) {
			currentTable = serverTable;
		} else {
			System.out.println("Sorry this is not an option");
		}
		f.getContentPane().add(currentTable);

	}

	private static ServerConnection getServerConnection(String type, String user, String svrPort, String password)
	{
		ServerConnection m_serverConnection = LoginUtils.login(type, user, svrPort, LoginUtils.encryptPassword(password));
		return m_serverConnection;
	}

}
