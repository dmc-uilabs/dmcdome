package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowsePlayspaceFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import org.apache.xmlrpc.XmlRpcException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 5, 2003
 * Time: 6:07:48 PM
 * To change this template use Options | File Templates.
 */
public class BrowsePlayspaceTest extends JFrame
{

	private static JFrame f;
	private static ServerConnection m_serverConnection;
	private static int userGroupId;

	public static BrowsePlayspaceFileSystemTable myselfTable;
	public static BrowsePlayspaceFileSystemTable usersTable;
	public static BrowsePlayspaceFileSystemTable groupsTable;
	public static BrowsePlayspaceFileSystemTable serverTable;

	public static BrowsePlayspaceFileSystemTable currentTable;

	public static String displayScope;


	public static void main(String[] args)
	{

		DomeInit.initializeDOME();
		try {
			DbUtils.setDbUrl(9001);
			//m_serverConnection = getServerConnection(LoginUtils.GUEST, "", "localhost:8080", "");
			m_serverConnection = getServerConnection(LoginUtils.USER, "Charles", "localhost:8080", "123");
			//m_serverConnection = getServerConnection(LoginUtils.ENTER, "root", "localhost:8080", "cadlab");

			System.out.println("here");
			//UserGroupDbFunctions.editUserGroupInfo(0,"root", "", true, true, "ACTIVE");

			myselfTable = new BrowsePlayspaceFileSystemTable(m_serverConnection, Folder.USER_HOME);
			usersTable = new BrowsePlayspaceFileSystemTable(m_serverConnection, Folder.USERS_ROOT);
			groupsTable = new BrowsePlayspaceFileSystemTable(m_serverConnection, Folder.GROUPS_ROOT);
			serverTable = new BrowsePlayspaceFileSystemTable(m_serverConnection, Folder.SERVER_ROOT);
			currentTable = myselfTable;

			f = new JFrame("Playspace Browser");

			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add(makeControlPanel(), BorderLayout.NORTH);
			f.getContentPane().add(currentTable, BorderLayout.SOUTH);


			//f.getContentPane().setBackground(Color.green);
			//f.setBackground(Color.pink);

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


		String[] views = {"Interface", "System", "Build"};
		JComboBox viewType = new JComboBox(views);
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
			System.out.println("Sorry this his not an option");
		}
		f.getContentPane().add(currentTable);

	}

	private static ServerConnection getServerConnection(String type, String user, String svrPort, String password)
	{
		ServerConnection m_serverConnection = LoginUtils.login(type, user, svrPort, LoginUtils.encryptPassword(password));
		return m_serverConnection;
	}

}
