package mit.cadlab.dome3.gui.serverPanel;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 25, 2003
 * Time: 11:48:12 AM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.gui.fileSystem.AbstractFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.FileSystemFilters;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowsePlayspaceFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.deploy.DeployModelFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.deploy.DeployPlayspaceFileSystemTable;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a file view on a specific server.
 * The class handles setup for guests, user and admin login.
 * The class can be configured for many uses: model deploy, playspace deploy, model subscribe, run browse, and manage filespace
 */
public class ServerPanel extends JPanel
{
	//constants used in the constructor so panel has the ringth tables in for the given use
	public static final int MODEL_DEPLOY = 1;
	public static final int PLAYSPACE_DEPLOY = 2;
	public static final int MODEL_SUBSCRIBE = 3;
	public static final int RUN_BROWSE = 4;
	public static final int MANAGE_FILESPACE = 5;

	//constants used as values in the filter and root combo boxes
	public static final String MODEL = "model";
	public static final String PLAYSPACE = "playspace";
	public static final String PROJECT = "project";
    public static final String ANALYSIS_TOOL = "analysis tool";
	public static final String SELF = "Self";
	public static final String USER = "User";
	public static final String GROUP = "Group";
	public static final String SERVER = "Server";

    //combination box text for usetype
    private static final String INDIVIDUAL = "individual use";
    private static final String COLLABORATIVE = "collaborative use";

	//constants used to specify different selection modes for the tree
	public static final int Default_Selection_Mode = 0;
	public static final int Folders_And_Model_Filter_Tree_Selection_Model = 1;
	public static final int Folders_And_Playspace_Filter_Tree_Selection_Model = 2;
	public static final int Folders_Filter_Tree_Selection_Model = 3;
	public static final int Interfaces_Filter_Tree_Selection_Model = 4;
	public static final int Models_Projects_Filter_Tree_Selection_Model = 5;
	public static final int Playspaces_Filter_Tree_Selection_Model = 6;
	public static final int Playspaces_Interfaces_Filter_Tree_Selection_Model = 7;
	public static final int Projects_Filter_Tree_Selection_Model = 8;
    public static final int Models_Filter_Tree_Selection_Model = 9;
    public static final int Analysis_Tool_Filter_Tree_Selection_Model = 10;
	public static final int Multiple_Selection_Mode = 11;

    public static final int Models_Projects_Tools_Filter_Tree_Selection_Model = 12;//Qing added March 5th, for playspace add tool inside

	private ServerConnection svr = null;
	private int type;
	private Object selectionObjectId;
	private String selectionPath;
	private List selectionListeners = new ArrayList();
	private List modelPlayspaceComboBoxListeners = new ArrayList();
	private int tableSelectionMode;


	/**
	 * Creates server panel and tables with preset tree selection models for the predefined 5 use cases
	 * @param svr server connection to be used by the panel
	 * @param type one of 5 predefined use types
	 */
	public ServerPanel(ServerConnection svr, int type)
	{
		this.svr = svr;
		this.type = type;
		this.tableSelectionMode = Default_Selection_Mode;

		makeGui();
	}

	/**
	 * Creates the serval panel and table with custom filter
	 * @param svr svr server connection to be used by the panel
	 * @param type type one of 5 predefined use types
	 * @param tableSelectionMode custom tree selection model to be used in the tabel
	 */
	public ServerPanel(ServerConnection svr, int type, int tableSelectionMode)
	{
		this.svr = svr;
		this.type = type;
		this.tableSelectionMode = tableSelectionMode;

		makeGui();
	}


	/**
	 *
	 * @param l ServerPanelSelectionListener
	 */
	public void addSelectionListeners(ServerPanelSelectionListener l)
	{
		selectionListeners.add(l);
	}

	public void removeSelectionListeners(ServerPanelSelectionListener l)
	{
		selectionListeners.remove(l);
	}

	public void addModelPlayspaceComboBoxListener(ModelPlayspaceComboBoxListener l)
	{
		if ((l != null) && (!modelPlayspaceComboBoxListeners.contains(l)))
			modelPlayspaceComboBoxListeners.add(l);
	}

	public void removeModelPlayspaceComboBoxListener(ModelPlayspaceComboBoxListener l)
	{
		modelPlayspaceComboBoxListeners.remove(l);
	}

	private void notifyModelPlayspaceComboBoxListeners()
	{
		for (int i = 0; i < modelPlayspaceComboBoxListeners.size(); i++) {
			((ModelPlayspaceComboBoxListener) modelPlayspaceComboBoxListeners.get(i)).selectionChanged();
		}
	}

	/**
	 * Method to get the server connection for the panel
	 * @return serverConnection used by the panel
	 */
	public ServerConnection getSvr()
	{
		return svr;
	}

	public void setSelectionModel(int selectionMode)
	{

		// should call method on all the tables in the mit.cadlab.dome3.gui.serverPanel
		//needs charles to write a corresponding method
		if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) == null) {
			//figure out how many cards there are
			int numPanels = rootCombo.getItemCount() * filterCombo.getItemCount();
			((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
			for (int i = 0; i < numPanels; i++) {
				((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).setSelectionModel(createSelectionModel(selectionMode));
				((CardLayout2) dataCardPanel.getLayout()).next(dataCardPanel);
			}
			//put the selection back to the original state
			((CardLayout2) dataCardPanel.getLayout()).show(dataCardPanel, filterModel.getSelectedValue() + rootModel.getSelectedValue());

		}
	}

	/**
	 * Method used to set the server panel combobox to a specific user root and filespace
	 * @param root The value that the root combination box should be set to (see ServerPanel constants)
	 * @param spaceFilter The value that the filter combination box should be set to (see serverPanel constants)
	 * @return
	 */
	public boolean setTableTo(String root, String spaceFilter)
	{
		if ((filterModel.setSelectionByValue(spaceFilter)) && (rootModel.setSelectionByValue(root)))
			return true;
		else
			return false;
	}

	/**
	 * Method to set the filter combo box to a specific value
	 * @param value the ServerPanel constant for the value you want selected
	 * @return true if successful
	 */
	public boolean setFilterTo(String value)
	{
		if (filterModel.setSelectionByValue(value))
			return true;
		else
			return false;
	}

	/**
	 * Method to set the root combo box to a specific value
	 * @param value value the ServerPanel constant for the value you want selected
	 * @return true if successful
	 */
	public boolean setRootTo(String value)
	{
		if (rootModel.setSelectionByValue(value))
			return true;
		else
			return false;
	}

	public boolean setObjectSelection(Object id, String type)
	{

		if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) == null)
			return false;

		return ((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).setSelection(id, type);
	}


	public String getSelectedPath()
	{
		if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) == null)
			return "";
		return ((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).getSelectedPath();
	}

	public TreePath getSelectedTreePath()
	{
		if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) == null)
			return null;
		return ((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).getSelectedTreePath();
	}

	public Object getSelectedObjectId()
	{
		if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) == null)
			return null;
		return ((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).getSelectedObjectId();
	}

	public Object getSelectedObject()
	{
		if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) == null)
			return null;
		return ((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).getSelectedItem();
	}

	public Object getSelectedObjects()
	{
		if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) == null)
			return null;
		return ((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).getSelectedItems();
	}

	public void enableFilterCombo(boolean b)
	{
		filterCombo.setEnabled(b);
	}

	public void enableRootCombo(boolean b)
	{
		rootCombo.setEnabled(b);
	}


	private void setSelectionData()
	{
		locationText.setText(getSelectedPath());
		selectionPath = rootModel.getListText(rootCombo.getSelectedIndex()) + ": " + getSelectedPath();
		selectionObjectId = getSelectedObjectId();
		notifyListeners();
	}

	private void notifyListeners()
	{
		for (int i = 0; i < selectionListeners.size(); i++) {
			((ServerPanelSelectionListener) selectionListeners.get(i)).selectionChanged(selectionPath, selectionObjectId, svr);
		}
	}

	public AbstractFileSystemTable getCurrentFileSystemTable()
	{
		return (AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent());
	}
	// GUI stuff from here on.

	private static final GridBagConstraints gbc = null;

	private JButton homeButton;
	private JButton refreshButton;
	private JTextField locationText;

	private ServerPanelComboBoxModel filterModel;
	private ActionListener filterListener;
	private JComboBox filterCombo;

	private ServerPanelComboBoxModel rootModel;
	private ActionListener rootListener;
	private JComboBox rootCombo;
	private JButton addFolderButton;
	private JButton renameButton;
	private JButton deleteButton;

	private JPanel dataCardPanel = new JPanel();
	private TreeSelectionListener dataCardListener;


	public String getCurrentFilterType()
	{
		return ((ServerPanelComboBoxModel.ComboBoxEntry) filterCombo.getSelectedItem()).getEntryValue();
	}

	public void makeGui()
	{
		JPanel addDeletePanel = makeAddDeletePanel();
		JPanel mainPanel = makeMainPanel();
		setupOptionsAndDataCard();
		JComponent[] comps = {mainPanel, addDeletePanel};
		//put the constraints here
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makeMainPanel()
	{
		JPanel p = new JPanel();

		ImageIcon homeIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/navigation/home.gif");
		homeButton = Templates.makeImageButton(homeIcon, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (rootCombo.getItemCount() != 0) {
					rootCombo.setSelectedIndex(0);
					filterCombo.setSelectedIndex(0);
				}
			}
		});

		ImageIcon refreshIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/navigation/refresh.gif");
		refreshButton = Templates.makeImageButton(refreshIcon, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) != null)
					((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).refresh();
			}
		});


		locationText = Templates.makeDTextField();
		locationText.setEditable(false);

		rootModel = new ServerPanelComboBoxModel();
		rootCombo = Templates.makeDComboBox(rootModel);

		Templates.setFixedSize(rootCombo, new Dimension(100, 20));
		rootCombo.addActionListener(rootListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					((CardLayout2) dataCardPanel.getLayout()).
					        show(dataCardPanel, filterModel.getSelectedValue() + rootModel.getSelectedValue());
					setSelectionData();
				} catch (Exception rootCombo) {
					System.out.println("nice coding");
				}
			}
		});
		JLabel filterLabel = Templates.makeLabel("view:");

		filterModel = new ServerPanelComboBoxModel();
		filterCombo = Templates.makeDComboBox(filterModel);
		Templates.setFixedSize(filterCombo, new Dimension(130, 20));
		filterCombo.addActionListener(filterListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					((CardLayout2) dataCardPanel.getLayout()).
					        show(dataCardPanel, filterModel.getSelectedValue() + rootModel.getSelectedValue());
					setSelectionData();
					notifyModelPlayspaceComboBoxListeners();
				} catch (Exception filterCombo) {
					System.out.println("nice coding");
				}
			}
		});

		JComponent[] comps = {homeButton, refreshButton, locationText, rootCombo, filterLabel, filterCombo, dataCardPanel};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 6, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeAddDeletePanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = null;
		GridBagConstraints[] gbcs = null;

		addFolderButton = Templates.makeImageButton(UIManager.getIcon("Tree.collapsedIcon"), new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) != null)
					((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).addFolder();
			}
		});
		renameButton = Templates.makeButton("rename", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) != null)
					((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).renameFolder();
			}
		});
		deleteButton = Templates.makeButton("delete", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if ((((CardLayout2) dataCardPanel.getLayout()).getActiveComponent()) != null)
					((AbstractFileSystemTable) (((CardLayout2) (dataCardPanel.getLayout())).getActiveComponent())).deleteSelectedItem();
			}
		});
		// MODEL_SUBSCRIBE = 3;
		// RUN_BROWSE = 4;
		switch (type) {
			case 1: // MODEL_DEPLOY = 1;
				comps = new JComponent[]{addFolderButton};
				gbcs = new GridBagConstraints[]
				{new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)};
				Templates.layoutGridBag(p, comps, gbcs);
				break;
			case 2: // PLAYSPACE_DEPLOY = 2
				comps = new JComponent[]{addFolderButton};
				gbcs = new GridBagConstraints[]
				{new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)};
				Templates.layoutGridBag(p, comps, gbcs);
				break;
			case 5: //MANAGE_FILESPACE = 5;
				comps = new JComponent[]{deleteButton, renameButton, addFolderButton};
				gbcs = new GridBagConstraints[]
				{new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
				 new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
				 new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
				Templates.layoutGridBag(p, comps, gbcs);
				break;
			default:
				break;//will have neither edit nor add button
		}
		// later on there may be cases where the delete button is actually added to the GUI

		return p;
	}

	private void setupOptionsAndDataCard()
	{
		dataCardPanel.setLayout(new CardLayout2());
		dataCardListener = new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				setSelectionData();
			}
		};
		if (svr != null) {
			if (svr.getLoginType().equals(LoginUtils.ADMIN))
				setupForAdmin();
			else if (svr.getLoginType().equals(LoginUtils.GUEST))
				setupForGuest();
			else
				setupForUser();
		}
	}


	private void setupForGuest()
	{
		int modelSelectionMode;
		int playspaceSelectionMode;
		switch (type) {
			case 1: // MODEL_DEPLOY = 1;
				OneButton1Msg.showWarning(null, "Deploy warning",
				                          "You are logged in to the as a guest.\n" +
				                          "Guests do not have deploy permissions", "ok",
				                          new Dimension(240, 100));
				break;
			case 2:	// PLAYSPACE_DEPLOY = 2;
				OneButton1Msg.showWarning(null, "Deploy warning",
				                          "You are logged in to the as a guest.\n" +
				                          "Guests do not have deploy permissions", "ok",
				                          new Dimension(240, 100));
				break;
			case 3: // MODEL_SUBSCRIBE = 3;

				if (tableSelectionMode == Default_Selection_Mode) tableSelectionMode = Models_Projects_Filter_Tree_Selection_Model;
				// if there is admin file space on this server
				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue()) {
					rootModel.addEntry("server", SERVER);
					BrowseModelFileSystemTable msmser = new BrowseModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(tableSelectionMode));
					msmser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, msmser);
				}
				rootModel.addEntry("users", USER);
				BrowseModelFileSystemTable msmu = new BrowseModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(tableSelectionMode));
				dataCardPanel.add(MODEL + USER, msmu);
				msmu.addTreeSelectionListener(dataCardListener);

				rootModel.addEntry("groups", GROUP);
				BrowseModelFileSystemTable msmg = new BrowseModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(tableSelectionMode));
				dataCardPanel.add(MODEL + GROUP, msmg);
				msmg.addTreeSelectionListener(dataCardListener);

				filterModel.addEntry(INDIVIDUAL, MODEL);
				filterCombo.setEnabled(false);

				// getcombo boxes and cards in sync
				rootCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);

				break;
			case 4: // RUN_BROWSE = 4;
				if (tableSelectionMode == Default_Selection_Mode) {
					modelSelectionMode = Interfaces_Filter_Tree_Selection_Model;
					playspaceSelectionMode = Playspaces_Interfaces_Filter_Tree_Selection_Model;
				} else {
					modelSelectionMode = tableSelectionMode;
					playspaceSelectionMode = tableSelectionMode;
				}

				// if there is admin file space on this server
				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue() ||
				        (UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr, 1)).booleanValue()) {
					rootModel.addEntry("server", SERVER);
					BrowseModelFileSystemTable rbmser = new BrowseModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(modelSelectionMode));
					rbmser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, rbmser);
					BrowsePlayspaceFileSystemTable rbpser = new BrowsePlayspaceFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(playspaceSelectionMode));
					rbpser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(PLAYSPACE + SERVER, rbpser);
				}

				rootModel.addEntry("users", USER);
				BrowseModelFileSystemTable rbmu = new BrowseModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(modelSelectionMode));
				dataCardPanel.add(MODEL + USER, rbmu);
				rbmu.addTreeSelectionListener(dataCardListener);
				BrowsePlayspaceFileSystemTable rbpu = new BrowsePlayspaceFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(playspaceSelectionMode));
				dataCardPanel.add(PLAYSPACE + USER, rbpu);
				rbpu.addTreeSelectionListener(dataCardListener);

				rootModel.addEntry("groups", GROUP);
				BrowseModelFileSystemTable rbmg = new BrowseModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(modelSelectionMode));
				dataCardPanel.add(MODEL + GROUP, rbmg);
				rbmg.addTreeSelectionListener(dataCardListener);
				BrowsePlayspaceFileSystemTable rbpg = new BrowsePlayspaceFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(playspaceSelectionMode));
				dataCardPanel.add(PLAYSPACE + GROUP, rbpg);
				rbpg.addTreeSelectionListener(dataCardListener);

				filterModel.addEntry(INDIVIDUAL, MODEL);
				filterModel.addEntry(COLLABORATIVE, PLAYSPACE);

				// getcombo boxes and cards in sync
				rootCombo.setSelectedIndex(0);
				filterCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				break;
			case 5:	// MANAGE_FILESPACE = 5;
				OneButton1Msg.showWarning(null, "Manage filespace warning",
				                          "You are logged in to the as a guest.\n" +
				                          "Guests do not have file management permissions", "ok",
				                          new Dimension(240, 100));
				break;
			default:
				break;
		}
	}

	private void setupForUser()
	{
		boolean hasOption = false;
		rootCombo.removeActionListener(rootListener);
		filterCombo.removeActionListener(filterListener);
		int modelSelectionMode;
		int playspaceSelectionMode;
		switch (type) {
			case 1: // MODEL_DEPLOY = 1;
				if (tableSelectionMode == Default_Selection_Mode) tableSelectionMode = Folders_Filter_Tree_Selection_Model;

				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr)).booleanValue()) {
					//System.out.println("have user save");
					rootModel.addEntry(svr.getLoginName(), SELF);
					DeployModelFileSystemTable mdms = new DeployModelFileSystemTable(svr, Folder.USER_HOME, createSelectionModel(tableSelectionMode));
					mdms.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SELF, mdms);
					hasOption = true;
				}
				if ((UserGroupFunctions.userInGroupWithSaveModelPermission(svr)).booleanValue()) {
					//System.out.println("have group save");
					rootModel.addEntry("groups", GROUP);
					DeployModelFileSystemTable mdmg = new DeployModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(tableSelectionMode));
					dataCardPanel.add(MODEL + GROUP, mdmg);
					mdmg.addTreeSelectionListener(dataCardListener);
					hasOption = true;
				}
				if (hasOption) {
					filterModel.addEntry(INDIVIDUAL, MODEL);
					filterCombo.setEnabled(false);
					rootCombo.setSelectedIndex(0);
					((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				} else
					OneButton1Msg.showWarning(null, "Deploy warning",
					                          "You are logged into a server on which\n" +
					                          "you do NOT have permission to deploy", "ok",
					                          new Dimension(240, 100));
				break;

			case 2: // PLAYSPACE_DEPLOY = 2;
				if (tableSelectionMode == Default_Selection_Mode) tableSelectionMode = Folders_Filter_Tree_Selection_Model;

				if ((UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr)).booleanValue()) {
					rootModel.addEntry(svr.getLoginName(), SELF);
					DeployPlayspaceFileSystemTable pdms = new DeployPlayspaceFileSystemTable(svr, Folder.USER_HOME, createSelectionModel(tableSelectionMode));
					pdms.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(PLAYSPACE + SELF, pdms);
					hasOption = true;
				}
				if ((UserGroupFunctions.userInGroupWithSavePlayspacePermission(svr)).booleanValue()) {
					rootModel.addEntry("groups", GROUP);
					DeployPlayspaceFileSystemTable pdmg = new DeployPlayspaceFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(tableSelectionMode));
					dataCardPanel.add(PLAYSPACE + GROUP, pdmg);
					pdmg.addTreeSelectionListener(dataCardListener);
					hasOption = true;
				}
				if (hasOption) {
					filterModel.addEntry(COLLABORATIVE, PLAYSPACE);
					filterCombo.setEnabled(false);
					rootCombo.setSelectedIndex(0);
					((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				} else
					OneButton1Msg.showWarning(null, "Deploy warning",
					                          "You are logged into a server on which\n" +
					                          "you do NOT have permission to deploy", "ok",
					                          new Dimension(240, 100));
				break;
			case 3: // MODEL_SUBSCRIBE = 3;
				if (tableSelectionMode == Default_Selection_Mode) tableSelectionMode = Models_Projects_Filter_Tree_Selection_Model;

				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr)).booleanValue()) {
					rootModel.addEntry(svr.getLoginName(), SELF);
					BrowseModelFileSystemTable msms = new BrowseModelFileSystemTable(svr, Folder.USER_HOME, createSelectionModel(tableSelectionMode));
					msms.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SELF, msms);
				}
				rootModel.addEntry("users", USER);
				BrowseModelFileSystemTable msmu = new BrowseModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(tableSelectionMode));
				dataCardPanel.add(MODEL + USER, msmu);
				msmu.addTreeSelectionListener(dataCardListener);

				rootModel.addEntry("groups", GROUP);
				BrowseModelFileSystemTable msmg = new BrowseModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(tableSelectionMode));
				dataCardPanel.add(MODEL + GROUP, msmg);
				msmg.addTreeSelectionListener(dataCardListener);

				// if there is admin file space on this server
				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue()) {
					rootModel.addEntry("server", SERVER);
					BrowseModelFileSystemTable msmser = new BrowseModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(tableSelectionMode));
					msmser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, msmser);
				}
				filterModel.addEntry(INDIVIDUAL, MODEL);
				filterCombo.setEnabled(false);

				// getcombo boxes and cards in sync
				rootCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				break;

			case 4: // RUN_BROWSE = 4;
				if (tableSelectionMode == Default_Selection_Mode) {
					modelSelectionMode = Interfaces_Filter_Tree_Selection_Model;
					playspaceSelectionMode = Playspaces_Interfaces_Filter_Tree_Selection_Model;
				} else {
					modelSelectionMode = tableSelectionMode;
					playspaceSelectionMode = tableSelectionMode;
				}

                boolean canSaveModel = UserGroupFunctions.userOrGroupHasSaveModelPermission(svr).booleanValue();
                boolean canSavePs = UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr).booleanValue();


				if (canSaveModel ||canSavePs) {
					rootModel.addEntry(svr.getLoginName(), SELF);
					BrowseModelFileSystemTable rbms = new BrowseModelFileSystemTable(svr, Folder.USER_HOME, createSelectionModel(modelSelectionMode));
					rbms.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SELF, rbms);
					BrowsePlayspaceFileSystemTable rbps = new BrowsePlayspaceFileSystemTable(svr, Folder.USER_HOME, createSelectionModel(playspaceSelectionMode));
					rbps.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(PLAYSPACE + SELF, rbps);
				}

				rootModel.addEntry("users", USER);
				BrowseModelFileSystemTable rbmu = new BrowseModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(modelSelectionMode));
				dataCardPanel.add(MODEL + USER, rbmu);
				rbmu.addTreeSelectionListener(dataCardListener);
				BrowsePlayspaceFileSystemTable rbpu = new BrowsePlayspaceFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(playspaceSelectionMode));
				dataCardPanel.add(PLAYSPACE + USER, rbpu);
				rbpu.addTreeSelectionListener(dataCardListener);

				rootModel.addEntry("groups", GROUP);
				BrowseModelFileSystemTable rbmg = new BrowseModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(modelSelectionMode));
				dataCardPanel.add(MODEL + GROUP, rbmg);
				rbmg.addTreeSelectionListener(dataCardListener);
				BrowsePlayspaceFileSystemTable rbpg = new BrowsePlayspaceFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(playspaceSelectionMode));
				dataCardPanel.add(PLAYSPACE + GROUP, rbpg);
				rbpg.addTreeSelectionListener(dataCardListener);

				// if there is admin file space on this server
                boolean serverCanSave = UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1).booleanValue();
				if (serverCanSave | (UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr, 1)).booleanValue())
                {
					rootModel.addEntry("server", SERVER);
					BrowseModelFileSystemTable rbmser = new BrowseModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(modelSelectionMode));
					rbmser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, rbmser);
					BrowsePlayspaceFileSystemTable rbpser = new BrowsePlayspaceFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(playspaceSelectionMode));
					rbpser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(PLAYSPACE + SERVER, rbpser);
				}

				filterModel.addEntry(INDIVIDUAL, MODEL);
				filterModel.addEntry(COLLABORATIVE, PLAYSPACE);

				// getcombo boxes and cards in sync
                if (!(canSaveModel || canSavePs)&& (serverCanSave)) {//point to server space if they don't have their own space
                    rootCombo.setSelectedItem(SERVER);
                    filterCombo.setSelectedIndex(0);
                    ((CardLayout2) dataCardPanel.getLayout()).show(dataCardPanel, MODEL + SERVER);
                }
                else {  //point to whatever is first in the combo box.
                    rootCombo.setSelectedIndex(0);
                    filterCombo.setSelectedIndex(0);
                    ((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
                }
				break;

			case 5: //MANAGE_FILESPACE = 5
				if (tableSelectionMode == Default_Selection_Mode) {
					modelSelectionMode = Folders_And_Model_Filter_Tree_Selection_Model;
					playspaceSelectionMode = Folders_And_Playspace_Filter_Tree_Selection_Model;
				} else {
					modelSelectionMode = tableSelectionMode;
					playspaceSelectionMode = tableSelectionMode;
				}

				boolean hasModel = false;
				boolean hasPlayspace = false;
				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr)).booleanValue() ||
				        (UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr)).booleanValue()) {
					rootModel.addEntry(svr.getLoginName(), SELF);
					hasOption = true;

					if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr)).booleanValue()) {
						filterModel.addEntry(INDIVIDUAL, MODEL);
						//DeployModelFileSystemTable mfms = new DeployModelFileSystemTable(svr, Folder.USER_HOME, filter);
						DeployModelFileSystemTable mfms = new DeployModelFileSystemTable(svr, Folder.USER_HOME, createSelectionModel(modelSelectionMode));
						mfms.addTreeSelectionListener(dataCardListener);
						dataCardPanel.add(MODEL + SELF, mfms);
						hasModel = true;
					}
					if ((UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr)).booleanValue()) {
						filterModel.addEntry(COLLABORATIVE, PLAYSPACE);
						DeployPlayspaceFileSystemTable mfps = new DeployPlayspaceFileSystemTable(svr, Folder.USER_HOME, createSelectionModel(playspaceSelectionMode));
						mfps.addTreeSelectionListener(dataCardListener);
						dataCardPanel.add(PLAYSPACE + SELF, mfps);
						hasPlayspace = true;
					}
				}
				if ((UserGroupFunctions.userInGroupWithSaveModelPermission(svr)).booleanValue() ||
				        (UserGroupFunctions.userInGroupWithSavePlayspacePermission(svr)).booleanValue()) {
					hasOption = true;
					rootModel.addEntry("groups", GROUP);

					if (hasModel == true || (UserGroupFunctions.userInGroupWithSaveModelPermission(svr)).booleanValue()) {
						DeployModelFileSystemTable mfmg = new DeployModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(modelSelectionMode));
						mfmg.addTreeSelectionListener(dataCardListener);
						dataCardPanel.add(MODEL + GROUP, mfmg);
						if (hasModel == false) filterModel.addEntry(INDIVIDUAL, MODEL);
					}
					if (hasPlayspace == true || (UserGroupFunctions.userInGroupWithSavePlayspacePermission(svr)).booleanValue()) {
						DeployPlayspaceFileSystemTable mfpg = new DeployPlayspaceFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(playspaceSelectionMode));
						mfpg.addTreeSelectionListener(dataCardListener);
						dataCardPanel.add(PLAYSPACE + GROUP, mfpg);
						if (hasModel == false) filterModel.addEntry("playspace", PLAYSPACE);
					}
				}

				if (hasOption) {
					rootCombo.setSelectedIndex(0);
					filterCombo.setSelectedIndex(0);
					((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				} else
					OneButton1Msg.showWarning(null, "Manage filespace warning",
					                          "You are logged into a server on which\n" +
					                          "you do NOT have a file space", "ok",
					                          new Dimension(240, 100));
				break;
			default:
				break;
		}
		rootCombo.addActionListener(rootListener);
		filterCombo.addActionListener(filterListener);
	}

	private void setupForAdmin()
	{
		rootCombo.removeActionListener(rootListener);
		filterCombo.removeActionListener(filterListener);
		int modelSelectionMode;
		int playspaceSelectionMode;

		switch (type) {
			case 1: // MODEL_DEPLOY = 1;
				if (tableSelectionMode == Default_Selection_Mode) tableSelectionMode = Folders_Filter_Tree_Selection_Model;
				// admin group is 1
				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue()) {
					//System.out.println("have admin save");
					rootModel.addEntry("server", SERVER);
					DeployModelFileSystemTable mdms = new DeployModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(tableSelectionMode));
					mdms.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, mdms);
				}
				rootModel.addEntry("users", USER);
				DeployModelFileSystemTable mdmu = new DeployModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(tableSelectionMode));
				mdmu.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(MODEL + USER, mdmu);

				rootModel.addEntry("groups", GROUP);
				DeployModelFileSystemTable mdmg = new DeployModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(tableSelectionMode));
				mdmg.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(MODEL + GROUP, mdmg);

				filterModel.addEntry(INDIVIDUAL, MODEL);
				filterCombo.setEnabled(false);
				rootCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				break;

			case 2: // PLAYSPACE_DEPLOY = 2;
				if (tableSelectionMode == Default_Selection_Mode) tableSelectionMode = Folders_Filter_Tree_Selection_Model;

				if ((UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr, 1)).booleanValue()) {
					rootModel.addEntry("server", SERVER);
					DeployPlayspaceFileSystemTable pdms = new DeployPlayspaceFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(tableSelectionMode));
					pdms.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(PLAYSPACE + SERVER, pdms);
				}
				rootModel.addEntry("users", USER);
				DeployPlayspaceFileSystemTable pdmu = new DeployPlayspaceFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(tableSelectionMode));
				pdmu.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(PLAYSPACE + USER, pdmu);

				rootModel.addEntry("groups", GROUP);
				DeployPlayspaceFileSystemTable pdmg = new DeployPlayspaceFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(tableSelectionMode));
				pdmg.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(PLAYSPACE + GROUP, pdmg);

				filterModel.addEntry(COLLABORATIVE, PLAYSPACE);
				filterCombo.setEnabled(false);
				rootCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				break;
			case 3: // MODEL_SUBSCRIBE = 3;
				if (tableSelectionMode == Default_Selection_Mode) tableSelectionMode = Models_Projects_Filter_Tree_Selection_Model;
				// if there is admin file space on this server
				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue()) {
					rootModel.addEntry("server", SERVER);
					BrowseModelFileSystemTable msmser = new BrowseModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(tableSelectionMode));
					msmser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, msmser);
				}
				rootModel.addEntry("users", USER);
				BrowseModelFileSystemTable msmu = new BrowseModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(tableSelectionMode));
				dataCardPanel.add(MODEL + USER, msmu);
				msmu.addTreeSelectionListener(dataCardListener);

				rootModel.addEntry("groups", GROUP);
				BrowseModelFileSystemTable msmg = new BrowseModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(tableSelectionMode));
				dataCardPanel.add(MODEL + GROUP, msmg);
				msmg.addTreeSelectionListener(dataCardListener);

				filterModel.addEntry(INDIVIDUAL, MODEL);

				// getcombo boxes and cards in sync
				rootCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);

				break;
			case 4: // RUN_BROWSE = 4;
				if (tableSelectionMode == Default_Selection_Mode) {
					modelSelectionMode = Interfaces_Filter_Tree_Selection_Model;
					playspaceSelectionMode = Playspaces_Interfaces_Filter_Tree_Selection_Model;
				} else {
					modelSelectionMode = tableSelectionMode;
					playspaceSelectionMode = tableSelectionMode;
				}

				// if there is admin file space on this server
				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue() ||
				        (UserGroupFunctions.userOrGroupHasSavePlayspacePermission(svr, 1)).booleanValue()) {
					rootModel.addEntry("server", SERVER);
					BrowseModelFileSystemTable rbmser = new BrowseModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(modelSelectionMode));
					rbmser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, rbmser);
					BrowsePlayspaceFileSystemTable rbpser = new BrowsePlayspaceFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(playspaceSelectionMode));
					rbpser.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(PLAYSPACE + SERVER, rbpser);
				}
				rootModel.addEntry("users", USER);
				BrowseModelFileSystemTable rbmu = new BrowseModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(modelSelectionMode));
				dataCardPanel.add(MODEL + USER, rbmu);
				rbmu.addTreeSelectionListener(dataCardListener);
				BrowsePlayspaceFileSystemTable rbpu = new BrowsePlayspaceFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(playspaceSelectionMode));
				dataCardPanel.add(PLAYSPACE + USER, rbpu);
				rbpu.addTreeSelectionListener(dataCardListener);

				rootModel.addEntry("groups", GROUP);
				BrowseModelFileSystemTable rbmg = new BrowseModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(modelSelectionMode));
				dataCardPanel.add(MODEL + GROUP, rbmg);
				rbmg.addTreeSelectionListener(dataCardListener);
				BrowsePlayspaceFileSystemTable rbpg = new BrowsePlayspaceFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(playspaceSelectionMode));
				dataCardPanel.add(PLAYSPACE + GROUP, rbpg);
				rbpg.addTreeSelectionListener(dataCardListener);

				filterModel.addEntry(INDIVIDUAL, MODEL);
				filterModel.addEntry(COLLABORATIVE, PLAYSPACE);

				// getcombo boxes and cards in sync
				rootCombo.setSelectedIndex(0);
				filterCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				break;
			case 5: //MANAGE_FILESPACE = 5
				if (tableSelectionMode == Default_Selection_Mode) {
					modelSelectionMode = Folders_And_Model_Filter_Tree_Selection_Model;
					playspaceSelectionMode = Folders_And_Playspace_Filter_Tree_Selection_Model;
				} else {
					modelSelectionMode = tableSelectionMode;
					playspaceSelectionMode = tableSelectionMode;
				}

				if ((UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue() ||
				        (UserGroupFunctions.userOrGroupHasSaveModelPermission(svr, 1)).booleanValue()) {
					rootModel.addEntry("server", SERVER);
					DeployModelFileSystemTable mfms = new DeployModelFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(modelSelectionMode));
					mfms.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(MODEL + SERVER, mfms);
					DeployPlayspaceFileSystemTable mfps = new DeployPlayspaceFileSystemTable(svr, Folder.SERVER_ROOT, createSelectionModel(playspaceSelectionMode));
					mfps.addTreeSelectionListener(dataCardListener);
					dataCardPanel.add(PLAYSPACE + SERVER, mfps);
				}
				rootModel.addEntry("users", USER);
				DeployModelFileSystemTable mfmu = new DeployModelFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(modelSelectionMode));
				mfmu.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(MODEL + USER, mfmu);
				DeployPlayspaceFileSystemTable mfpu = new DeployPlayspaceFileSystemTable(svr, Folder.USERS_ROOT, createSelectionModel(playspaceSelectionMode));
				mfpu.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(PLAYSPACE + USER, mfpu);

				rootModel.addEntry("groups", GROUP);
				DeployModelFileSystemTable mfmg = new DeployModelFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(modelSelectionMode));
				mfmg.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(MODEL + GROUP, mfmg);
				DeployPlayspaceFileSystemTable mfpg = new DeployPlayspaceFileSystemTable(svr, Folder.GROUPS_ROOT, createSelectionModel(playspaceSelectionMode));
				mfpg.addTreeSelectionListener(dataCardListener);
				dataCardPanel.add(PLAYSPACE + GROUP, mfpg);

				filterModel.addEntry(INDIVIDUAL, MODEL);
				filterModel.addEntry(COLLABORATIVE, PLAYSPACE);

				rootCombo.setSelectedIndex(0);
				filterCombo.setSelectedIndex(0);
				((CardLayout2) dataCardPanel.getLayout()).first(dataCardPanel);
				break;
			default:
				break;
		}
		rootCombo.addActionListener(rootListener);
		filterCombo.addActionListener(filterListener);
	}

	private TreeSelectionModel createSelectionModel(int selectionMode)
	{
		TreeSelectionModel s = null;
		switch (selectionMode) {
			case 1:  //Folders_And_Model_Filter_Tree_Selection_Model = 1;
				s = new FileSystemFilters.FoldersAndModelFilterTreeSelectionModel();
				break;
			case 2: //Folders_And_Playspace_Filter_Tree_Selection_Model = 2;
				s = new FileSystemFilters.FoldersAndPlayspaceFilterTreeSelectionModel();
				break;
			case 3: //Folders_Filter_Tree_Selection_Model = 3;
				s = new FileSystemFilters.FoldersFilterTreeSelectionModel();
				break;
			case 4: //Interfaces_And_Projects_Filter_Tree_Selection_Model = 4;
				//s = new FileSystemFilters.InterfacesFilterTreeSelectionModel();
				s = new FileSystemFilters.InterfacesAndProjectsAndAnalysisToolsFilterTreeSelectionModel();
				break;
			case 5: //Models_Projects_Filter_Tree_Selection_Model = 5;
				s = new FileSystemFilters.ModelsProjectsFilterTreeSelectionModel();
				break;
			case 6: //Playspaces_Filter_Tree_Selection_Model = 6;
				s = new FileSystemFilters.PlayspacesFilterTreeSelectionModel();
				break;
			case 7: //Playspaces_Interfaces_Filter_Tree_Selection_Model = 7;
				s = new FileSystemFilters.PlayspacesInterfacesFilterTreeSelectionModel();
				break;
			case 8: //Projects_Filter_Tree_Selection_Model = 8;
				s = new FileSystemFilters.ProjectsFilterTreeSelectionModel();
				break;
            case 9: //Models_Filter_Tree_Selection_Model = 8;
                s = new FileSystemFilters.ModelsFilterTreeSelectionModel();
                break;
            case 10: // Analysis_Tool_Tree_Selection_Model = 10;
                s = new FileSystemFilters.AnalysisToolFilterTreeSelectionModel();
                break;
			case 11:  //Folders_And_Model_Filter_Tree_Selection_Model = 1;
				s = new FileSystemFilters.MultipleFoldersAndModelFilterTreeSelectionModel();
				s.setSelectionMode(ServerPanel.Multiple_Selection_Mode);
				break;
             case 12:  //Models_Projects_Tools_Filter_Tree_Selection_Model
				s = new FileSystemFilters.ModelsProjectsToolsFilterTreeSelectionModel();
				break;
			default:
				System.out.println("invalid option in ServerPanel::createSelectionModel(int selectionMode");
				break;

		}
		return s;
	}

	public void dispose()
	{
		SwingUtilities.windowForComponent(ServerPanel.this).dispose();
	}

	public WindowAdapter getWindowAdapter()
	{
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{

				dispose();
			}
		};
	}

	public static void main
	        (String[] args)
	{
		JFrame f = new JFrame("ServerPanel test2");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ServerConnection conn = LoginPrompt.showDialog(null);

		if (conn != null) {
			f.getContentPane().add(new ServerPanel(conn, MANAGE_FILESPACE));
			f.show();
		}

	}
}
