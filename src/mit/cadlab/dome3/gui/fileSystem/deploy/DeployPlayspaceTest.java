package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.gui.fileSystem.deploy.DeployPlayspaceFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.Folder;

import org.apache.xmlrpc.XmlRpcException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 5, 2003
 * Time: 6:07:48 PM
 * To change this template use Options | File Templates.
 */
public class DeployPlayspaceTest extends JFrame
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
	public static DeployPlayspaceFileSystemTable myselfTable;
	public static DeployPlayspaceFileSystemTable usersTable;
	public static DeployPlayspaceFileSystemTable groupsTable;
	public static DeployPlayspaceFileSystemTable serverTable;

	public static DeployPlayspaceFileSystemTable currentTable;

	public static String displayScope;


	public static void main(String[] args)
	{


		//DbUtils.setDbUrl(9001);
		m_serverConnection = getServerConnection(LoginUtils.USER, "Charles", "localhost:8080", "123");
		//UserGroupDbFunctions.editUserGroupInfo(0,"root", "", true, true, "ACTIVE");

		myselfTable = new DeployPlayspaceFileSystemTable(m_serverConnection, Folder.USER_HOME);
		usersTable = new DeployPlayspaceFileSystemTable(m_serverConnection, Folder.USERS_ROOT);
		groupsTable = new DeployPlayspaceFileSystemTable(m_serverConnection, Folder.GROUPS_ROOT);
		serverTable = new DeployPlayspaceFileSystemTable(m_serverConnection, Folder.SERVER_ROOT);
		currentTable = myselfTable;

		TreeSelectionListener l = new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				System.out.print(currentTable.getSelectedPath());
				System.out.println("\t" + currentTable.getSelectedObjectId());
			}
		};

		//myselfTable.setSelection(new Integer(21));
		//String[] tps = {"Public", "charles", "dumont"};
		/*System.out.println("children = ");
		Enumeration rootChildren = myselfTable.rootNode.children();
		while (rootChildren.hasMoreElements()) {
		    FileSystemObject fObj = (FileSystemObject)((AbstractTreeObjectFactoryTreeNode) rootChildren.nextElement()).getTreeNodeObject();
		    System.out.println(fObj);
		}*/

		Object[] tps = {myselfTable.getRootNode()};
		TreePath tp = new TreePath(tps);
		System.out.println(tp);
		currentTable.getTree().expandPath(tp);

		currentTable.addTreeSelectionListener(l);
		usersTable.addTreeSelectionListener(l);
		groupsTable.addTreeSelectionListener(l);

		f = new JFrame("Playspace Browser");

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

		JButton deleteFolderButton = new JButton("Delete Folder");
		deleteFolderButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentTable.deleteSelectedItem();
				f.show();
				f.repaint();
			}
		});

		JButton renameFolderButton = new JButton("Rename Folder");
		renameFolderButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentTable.renameFolder();
				f.show();
				f.repaint();
			}
		});

		p.add(cbType);
		p.add(refreshButton);
		p.add(addFolderButton);
		p.add(deleteFolderButton);
		p.add(renameFolderButton);
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
