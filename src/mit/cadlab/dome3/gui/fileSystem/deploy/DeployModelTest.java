package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.gui.fileSystem.deploy.DeployModelFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;

import org.apache.xmlrpc.XmlRpcException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 5, 2003
 * Time: 6:07:48 PM
 * To change this template use Options | File Templates.
 */
public class DeployModelTest extends JFrame
{

	private static JFrame f;
	private static ServerConnection m_serverConnection;
	private static int userGroupId;

	/*
	public static ModelBrowserTable myselfTable;
	public static ModelBrowserTable usersTable;
	public static ModelBrowserTable groupsTable;
	public static ModelBrowserTable serverTable;

	public static ModelBrowserTable currentTable;
	*/
	public static DeployModelFileSystemTable myselfTable;
	public static DeployModelFileSystemTable usersTable;
	public static DeployModelFileSystemTable groupsTable;
	public static DeployModelFileSystemTable serverTable;

	public static DeployModelFileSystemTable currentTable;

	public static String displayScope;


	public static void main(String[] args)
	{


		//DbUtils.setDbUrl(9001);
		m_serverConnection = getServerConnection(LoginUtils.USER, "Charles", "localhost:8080", "123");
		//UserGroupDbFunctions.editUserGroupInfo(0,"root", "", true, true, "ACTIVE");

		myselfTable = new DeployModelFileSystemTable(m_serverConnection, Folder.USER_HOME);
		usersTable = new DeployModelFileSystemTable(m_serverConnection, Folder.USERS_ROOT);
		groupsTable = new DeployModelFileSystemTable(m_serverConnection, Folder.GROUPS_ROOT);
		serverTable = new DeployModelFileSystemTable(m_serverConnection, Folder.SERVER_ROOT);
		currentTable = myselfTable;
		TreeSelectionListener l = new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				System.out.print(currentTable.getSelectedPath());
				System.out.println("\t" + currentTable.getSelectedObjectId());
			}
		};
	    
		//todo uncomment
		//todo the line
		//todo below

		// currentTable.setSelection("71e7fe70-b6b3-1004-8ff7-6f7590c1f261");

		currentTable.addTreeSelectionListener(l);
		//usersTable.addTreeSelectionListener(l);
		//groupsTable.addTreeSelectionListener(l);

		f = new JFrame("Model Browser");

		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(makeControlPanel(), BorderLayout.NORTH);
		f.getContentPane().add(currentTable, BorderLayout.SOUTH);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.show();

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

		JButton addFolderButton = new JButton("Add Folder");
		addFolderButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentTable.addFolder();
				f.show();
				f.repaint();
			}
		});

		p.add(cbType);
		p.add(refreshButton);
		p.add(addFolderButton);
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
