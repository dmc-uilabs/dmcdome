package mit.cadlab.dome3.gui.runbrowser;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.fileSystem.browse.AbstractBrowseFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.AbstractFileSystemTable;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.serverPanel.ModelPlayspaceComboBoxListener;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelComboBoxModel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelSelectionListener;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.CompoundId;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * The browser for looking at DOME servers while in run mode
 */
public class RunBrowser extends JPanel implements DomeGui {
    public static final GridBagConstraints gbc = null;
    private JPanel serverCards;
    private Hashtable loginLabelText = new Hashtable();
    private Hashtable serverConnections = new Hashtable();

    private ServerPanelComboBoxModel serverModel;
    private JComboBox serverCombo;
    private JButton addBookmarkButton;
    private JButton backButton;
    private JButton forwardButton;

    private JButton guestButton;
    private JButton loginButton;
    private JButton _closeButton;
    private JLabel loginLabel;
    private int type;
    private int treeSelectionMode;
    private String serverAdd = "";

    private List selectionListeners = new ArrayList();
    private List modelPlayspaceComboBoxListeners = new ArrayList();

    private ServerPanelSelectionListener l = new ServerPanelSelectionListener() {
        public void selectionChanged(String path, Object id, ServerConnection svr) {
            notifyListeners(path, id, svr);
        }
    };

    private ModelPlayspaceComboBoxListener m = new ModelPlayspaceComboBoxListener() {
        public void selectionChanged() {
            notifyModelPlayspaceComboBoxListeners();
        }
    };

    /**
     * Method to return the server connection associated with the current Server panel
     * @return
     */
    public ServerConnection getCurrentServerConnection() {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null)
            return ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getSvr();
        else
            return null;
    }

    public Object getCurrentSelectedObject() {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null)
            return ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getSelectedObject();
        else
            return null;
    }

    public Object getSelectedObjects() {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null)
            return ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getSelectedObjects();
        else
            return null;
    }

    public String getCurrentFilterType() {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null)
            return ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getCurrentFilterType();
        else
            return null;
    }

    public CompoundId getCurrentObjectId() {
        String filterType = getCurrentFilterType();
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null) {
            TreePath tp = ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getSelectedTreePath();
            Object[] nodes = tp.getPath();
            CompoundId id = new CompoundId();
// walk up the tree and create the compound id of the target object
            for (int i = nodes.length - 1; i >= 0; i--) {
                AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) nodes[i];
                Object obj = node.getTreeNodeObject();
                if (obj instanceof Folder) {
                    if (!(((Folder) obj).getFolderType().equals(Folder.PROJECT_CONTENT_FOLDER)
                            || ((Folder) obj).getFolderType().equals(Folder.ANALYSIS_TOOL_CONTENT_FOLDER)))
                        return id;
                }
                if (obj instanceof DomeFile) {
                    DomeFile f = (DomeFile) obj;
                    String fileType = f.getFileType();
                    if (fileType.equals(DomeFile.PLAYSPACE_TYPE)) {
                        id.setPlayspaceStaticId((String) f.getId());
                        return id;
                    } else if (fileType.equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
                        /**
                         * If an interface is opened inside a project that belongs
                         * to an analysis tool, the project itself is not the
                         * item inside a Folder object and this if statement will
                         * be satisfied causing the model static id to be assigned.
                         *
                         * This will mis-guide the code later on.  The if-statement
                         * below will fix this problem, by making sure that if a
                         * project static id is set, the model static id will not be.
                         */

                        if (id.getFirstProjectStaticId() == null)
                            id.setModelStaticId((String) f.getId());
                    } else if (fileType.equals(DomeFile.MODEL_TYPE))
                        id.setModelStaticId((String) f.getId());
                    else if (fileType.equals(DomeFile.PROJECT_TYPE))
                        id.addParentProjectStaticId((String) f.getId());
                    else if (fileType.equals(DomeFile.INTERFACE_TYPE))
                        id.setInterfaceStaticId((String) f.getId());
                }
            }
            return id;
        } else
            return null;
    }

    public boolean isAnalysisToolParent() {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null) {
            TreePath tp = ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getSelectedTreePath();
            Object[] nodes = tp.getPath();
            for (int i = nodes.length - 1; i >= 0; i--) {
                AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) nodes[i];
                Object obj = node.getTreeNodeObject();
                if (obj instanceof Folder) {
                    Folder f = (Folder) obj;
                    if (f.getFolderType().equals(Folder.ANALYSIS_TOOL_CONTENT_FOLDER))
                        return true;
                    else if (f.getFolderType().equals(Folder.PROJECT_CONTENT_FOLDER))
                        return false;
                }
            }
        }

        return false;
    }

    public boolean isProjectParent() {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null) {
            TreePath tp = ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getSelectedTreePath();
            Object[] nodes = tp.getPath();
            for (int i = nodes.length - 1; i >= 0; i--) {
                AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) nodes[i];
                Object obj = node.getTreeNodeObject();
                if (obj instanceof Folder) {
                    Folder f = (Folder) obj;
                    if (f.getFolderType().equals(Folder.PROJECT_CONTENT_FOLDER))
                        return true;
                    else if (f.getFolderType().equals(Folder.ANALYSIS_TOOL_CONTENT_FOLDER))
                        return false;
                }
            }
        }

        return false;
    }

    public AbstractFileSystemTable getCurrentFileSystemTable() {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null)
            return ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getCurrentFileSystemTable();
        else
            return null;
    }


    public ServerConnection getTopLevelProjectConnection(ServerConnection ifaceSvrConn) {
        if ((((CardLayout2) serverCards.getLayout()).getActiveComponent()) != null) {
            TreePath tp = ((ServerPanel) (((CardLayout2) (serverCards.getLayout())).getActiveComponent())).getSelectedTreePath();
            Object[] nodes = tp.getPath();
            CompoundId id = new CompoundId();
            // walk up the tree and create the compound id of the target object
            for (int i = nodes.length - 1; i >= 0; i--) {
                AbstractTreeObjectFactoryTreeNode node = (AbstractTreeObjectFactoryTreeNode) nodes[i];
                Object obj = node.getTreeNodeObject();
                if (obj instanceof DomeFile) {
                    DomeFile f = (DomeFile) obj;
                    String fileType = f.getFileType();
                    if (fileType.equals(DomeFile.PROJECT_TYPE)) {
                        id.addParentProjectStaticId((String) f.getId());
                        ServerConnection projSvrConn;
                        projSvrConn = LoginUtils.compareServersAndGetConnection(ifaceSvrConn, f.getUrl());
                        return projSvrConn;
                    }
                }
            }
        }

        return null;
    }


    /**
     * Constuctor for a run browser
     * @param type The use type as defined in the ServerPanel class
     */
    public RunBrowser(int type) {
        this.type = type;
        makePanel();
        setPreferredSize(DomeBuildFrame.DEFAULT_SIZE);
        addModelPlayspaceComboBoxListener(new ModelPlayspaceComboBoxListener() {
            public void selectionChanged() {
                if (DomeClientApplication.getMode() == 2)
                    setMenuContext();
            }
        });
    }

    public RunBrowser(int type, int treeSelectionMode) {
        this.type = type;
        this.treeSelectionMode = treeSelectionMode;
        makePanel(treeSelectionMode);
        setPreferredSize(DomeBuildFrame.DEFAULT_SIZE);
        addModelPlayspaceComboBoxListener(new ModelPlayspaceComboBoxListener() {
            public void selectionChanged() {
                if (DomeClientApplication.getMode() == 2)
                    setMenuContext();
            }
        });

    }

    public void addSelectionListeners(ServerPanelSelectionListener l) {
        if ((l != null) && (!selectionListeners.contains(l)))
            selectionListeners.add(l);
    }

    public void removeSelectionListeners(ServerPanelSelectionListener l) {
        selectionListeners.remove(l);
    }

    private void notifyListeners(String selectionPath, Object selectionObjectId, ServerConnection svr) {
        for (int i = 0; i < selectionListeners.size(); i++) {
            ((ServerPanelSelectionListener) selectionListeners.get(i)).selectionChanged(selectionPath, selectionObjectId, svr);
            //((ServerPanelSelectionListener) selectionListeners.get(i)).getObjectDescription(obj);
        }
    }

    public void addModelPlayspaceComboBoxListener(ModelPlayspaceComboBoxListener l) {
        if ((l != null) && (!modelPlayspaceComboBoxListeners.contains(l)))
            modelPlayspaceComboBoxListeners.add(l);
    }

    public void removeModelPlayspaceComboBoxListener(ModelPlayspaceComboBoxListener l) {
        modelPlayspaceComboBoxListeners.remove(l);
    }

    private void notifyModelPlayspaceComboBoxListeners() {
        for (int i = 0; i < modelPlayspaceComboBoxListeners.size(); i++) {
            ((ModelPlayspaceComboBoxListener) modelPlayspaceComboBoxListeners.get(i)).selectionChanged();
        }
    }


    private void makePanel() {
        //todo lower browser pael with clode button commented out. Should be done differently so that do not get close and done button for locating resources, adding to playspace
        JComponent[] comps = {makeUpperServerPanel(), makeMiddleBrowsePanel() /*, makeLowerBrowsePanel()*/};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.EAST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
//new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(this, comps, gbcs);
    }

    private void makePanel(int treeSelectionMode) {
//todo lower browser pael with clode button commented out. Should be done differently so that do not get close and done button for locating resources, adding to playspace
        JComponent[] comps = {makeUpperServerPanel(), makeMiddleBrowsePanel(treeSelectionMode)/*, makeLowerBrowsePanel()*/};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.EAST, gbc.BOTH, new Insets(0, 0, 5, 0), 0, 0),
//    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(this, comps, gbcs);
    }

    private JPanel makeUpperServerPanel() {

        JPanel sp = new JPanel();
        sp.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        serverModel = new ServerPanelComboBoxModel();
        serverCombo = Templates.makeDComboBox(serverModel);
        serverCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent svrComboEvent) {
                ((CardLayout2) serverCards.getLayout()).
                        show(serverCards, serverModel.getSelectedValue());
                loginLabel.setText((String) loginLabelText.get(serverModel.getSelectedValue()));

                //notify menu
                setBrowserLocation(serverCombo.getSelectedItem().toString());
                notifyModelPlayspaceComboBoxListeners();
            }
        });


        serverCombo.setEditable(true);
        serverCombo.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        guestButton = Templates.makeButton("guest", new ActionListener() {
            public void actionPerformed(ActionEvent guestEvent) {
                String svrPort = (serverCombo.getSelectedItem().toString()).trim();
                if (svrPort.equals(""))
                    svrPort = DomeServer.getDefaultServerPort();
                else if (svrPort.indexOf(':') == -1)
                    svrPort += ":8080";
                try {
                    WaitCursorUtils.showWaitCursor(true, RunBrowser.this);

                    ServerConnection conn = LoginUtils.login(LoginUtils.GUEST, null, RunMode.getClientUrl(), svrPort, null);
                    String serverPort = conn.getServerPort();
                    ServerPanel newPanel = new ServerPanel(conn, type);
                    newPanel.addSelectionListeners(l);
                    newPanel.addModelPlayspaceComboBoxListener(m);
                    serverCards.add(conn.getConnectionId(), newPanel);
                    ((CardLayout2) serverCards.getLayout()).show(serverCards, conn.getConnectionId());
                    serverModel.addEntry(serverPort, conn.getConnectionId());
                    loginLabelText.put(conn.getConnectionId(), "logged in as guest");
                    loginLabel.setText((String) loginLabelText.get(conn.getConnectionId()));
                    serverConnections.put(conn.getConnectionId(), conn); // this is needed to cleanup on browser closing
                    conn.addReference();
                    serverAdd = conn.getServerPort();
                    setBrowserLocation(serverAdd);

                    WaitCursorUtils.showWaitCursor(false, RunBrowser.this);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    LoginPrompt.handleLoginError(ex, RunBrowser.this);
                }
            }
        });
        guestButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        loginButton = Templates.makeButton("login", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConnection conn = LoginPrompt.showDialog(RunBrowser.this, RunMode.getClientUrl());

                if (conn != null) {
                    String serverName = conn.getServerPort();
                    String userName = conn.getLoginName();
                    String loginType = conn.getLoginType();
                    ServerPanel newPanel = null;
                    //	if(treeSelectionMode == ServerPanel.Multiple_Selection_Mode) //change to the following  to allow tool to be selected in playspace add dialog in build mode
                    if (treeSelectionMode == ServerPanel.Multiple_Selection_Mode || treeSelectionMode == ServerPanel.Models_Projects_Tools_Filter_Tree_Selection_Model)
                        newPanel = new ServerPanel(conn, type, treeSelectionMode);
                    else
                        newPanel = new ServerPanel(conn, type);
                    newPanel.addSelectionListeners(l);
                    newPanel.addModelPlayspaceComboBoxListener(m);
                    serverCards.add(conn.getConnectionId(), newPanel);
                    ((CardLayout2) serverCards.getLayout()).show(serverCards, conn.getConnectionId());
                    serverModel.addEntry(serverName, conn.getConnectionId());


                    if (loginType.equals(LoginUtils.ADMIN))
                        loginLabelText.put(conn.getConnectionId(), "logged in as administrator: " + userName);
                    else
                        loginLabelText.put(conn.getConnectionId(), "logged in as user: " + userName);
                    loginLabel.setText((String) loginLabelText.get(conn.getConnectionId()));
                    serverConnections.put(conn.getConnectionId(), conn); // this is needed to cleanup on browser closing
                    conn.addReference();
                    serverAdd = conn.getServerPort();
                    setBrowserLocation(serverAdd);
                    //set menu
                    notifyModelPlayspaceComboBoxListeners();
                }
            }
        });
        loginButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        loginLabel = Templates.makeLabel(" ");

        JComponent[] comps = {serverCombo, guestButton, loginButton, makeNavigationPanel(),
                              loginLabel};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, -3),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 5, 0), 0, 0)
        };
        Templates.layoutGridBag(sp, comps, gbcs);
        return sp;
    }

    private JPanel makeMiddleBrowsePanel() {
        serverCards = new JPanel();
        serverCards.setLayout(new CardLayout2());
        serverCards.add("", new ServerPanel(null, ServerPanel.RUN_BROWSE));

        return serverCards;
    }

    private JPanel makeMiddleBrowsePanel(int treeSelectionMode) {
        serverCards = new JPanel();
        serverCards.setLayout(new CardLayout2());
        serverCards.add("", new ServerPanel(null, ServerPanel.RUN_BROWSE, treeSelectionMode));

        return serverCards;
    }

    private JPanel makeLowerBrowsePanel() {
        JPanel p = new JPanel();


        _closeButton = Templates.makeButton("close");
        _closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                dispose();
            }
        });

        JComponent[] comps = new JComponent[]{

            new JPanel(), _closeButton
        };

        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    private JPanel makeNavigationPanel() {
        JPanel p = new JPanel();

        ImageIcon addBookIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/navigation/addBook.gif");
        addBookmarkButton = Templates.makeImageButton(addBookIcon, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RunMode.addBookmark();
            }
        });
        addBookmarkButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        backButton = Templates.makeListArrowButton("left", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (serverCombo.getItemCount() > 0) {
                    int index = serverCombo.getSelectedIndex();
                    if (index == 0)
                        return;
                    else
                        serverCombo.setSelectedIndex(index - 1);
                }
            }
        });
        backButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        forwardButton = Templates.makeListArrowButton("right", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (serverCombo.getItemCount() > 0) {
                    int index = serverCombo.getSelectedIndex();
                    if (index > serverCombo.getItemCount() - 2)
                        return;
                    else
                        serverCombo.setSelectedIndex(index + 1);
                }
            }
        });
        forwardButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JComponent[] comps = {backButton, forwardButton, addBookmarkButton};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0)

        };
        Templates.layoutGridBag(p, comps, gbcs);
        p.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        return p;
    }

    /**
     * method to logout from servers when the GUI is closed
     */
    public void logoutOnClose() {
        for (int i = 0; i < serverCombo.getItemCount(); i++) {
            serverCombo.setSelectedIndex(i);
            ((ServerConnection) (serverConnections.get(serverModel.getSelectedValue()))).removeReference();
        }
    }

    public void dispose() {
        SwingUtilities.windowForComponent(this).dispose();
    }

    public WindowListener getWindowListener() {
        return new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logoutOnClose();
                dispose();
            }
        };
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("DOME run browser");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        RunBrowser rb = new RunBrowser(ServerPanel.RUN_BROWSE);
        f.getContentPane().add(rb);
        f.setSize(DomeBuildFrame.DEFAULT_SIZE);
        f.addWindowListener(rb.getWindowListener());

        f.show();
    }


    //Qing : DomeGui Interface
    private static final String TITLE_PREFIX = "Browser: ";

    public String getTitle() {
        String titleSuffix = "";
        if (loginLabel.getText() == null)
            titleSuffix = serverAdd;
        else if (loginLabel.getText().trim().equals(""))
            titleSuffix = serverAdd;
        else
            titleSuffix = serverAdd + "(" + loginLabel.getText() + ")";
        return TITLE_PREFIX + titleSuffix;
    }

    public String getHelpContext() {
        return null;
    }

    public void setMenuContext() {
        if (getCurrentServerConnection() == null) {
            //server panel not initialized yet
            MenuManager.setContext(ModeContexts.RUN_BROWSER);
            return;
        }
        if (getCurrentFilterType() != null && getCurrentFilterType().equalsIgnoreCase(ServerPanel.MODEL))
            MenuManager.setContext(ModeContexts.RUN_BROWSER_MODEL_VIEW);
        else if (getCurrentFilterType() != null && getCurrentFilterType().equalsIgnoreCase(ServerPanel.PLAYSPACE))
            MenuManager.setContext(ModeContexts.RUN_BROWSER_PLAYSPACE_VIEW);
        else
            MenuManager.setContext(ModeContexts.RUN_BROWSER);
    }

    public Object getGuiObject() {
        return null; // not pertinent to this gui
    }

    public void close() {
        logoutOnClose();

    }

    //notify menuitem
    public void setBrowserLocation(String newServerAdd) {
        serverAdd = newServerAdd;
        firePropertyChange(NameListener.NAME, "", getTitle());//notify DomeRunFrame to change frame Title and notify menuitem to change menu text

    }

    //add for setting different views
    public void setView(String choice) {
        AbstractFileSystemTable table = getCurrentFileSystemTable();
        if (table == null) return;
        if (table instanceof AbstractBrowseFileSystemTable) {
            if (choice.equals(RunMode.INTERFACECAUSALITYVIEW)) {
                ((AbstractBrowseFileSystemTable) table).switchInterfaceView("Interface");
            } else if (choice.equals(RunMode.BUILDVIEW)) {
                ((AbstractBrowseFileSystemTable) table).switchInterfaceView("Build");
            } else if (choice.equals(RunMode.SYSTEMCAUSALITYVIEW)) {
                ((AbstractBrowseFileSystemTable) table).switchInterfaceView("System");
            }
        }
    }


    //Qing add for bookmark use:shouldn't be called by others
    public void createFromBookmark(ServerConnection conn) {
        //copy from loginButton actionPerformed code
        if (conn != null) {
            String serverName = conn.getServerPort();
            String userName = conn.getLoginName();
            String loginType = conn.getLoginType();
            if (loginType.equals(LoginUtils.GUEST)) {
                ServerPanel newPanel = new ServerPanel(conn, type);
                newPanel.addSelectionListeners(l);
                newPanel.addModelPlayspaceComboBoxListener(m);
                serverCards.add(conn.getConnectionId(), newPanel);
                ((CardLayout2) serverCards.getLayout()).show(serverCards, conn.getConnectionId());
                serverModel.addEntry(serverName, conn.getConnectionId());
                loginLabelText.put(conn.getConnectionId(), "logged in as guest");
                loginLabel.setText((String) loginLabelText.get(conn.getConnectionId()));
                serverConnections.put(conn.getConnectionId(), conn); // this is needed to cleanup on browser closing
                conn.addReference();
                serverAdd = conn.getServerPort();
                setBrowserLocation(serverAdd);
                //set menu
                notifyModelPlayspaceComboBoxListeners();
            } else {
                ServerPanel newPanel = null;
                //	if(treeSelectionMode == ServerPanel.Multiple_Selection_Mode) //change to the following  to allow tool to be selected in playspace add dialog in build mode
                if (treeSelectionMode == ServerPanel.Multiple_Selection_Mode || treeSelectionMode == ServerPanel.Models_Projects_Tools_Filter_Tree_Selection_Model)
                    newPanel = new ServerPanel(conn, type, treeSelectionMode);
                else
                    newPanel = new ServerPanel(conn, type);
                newPanel.addSelectionListeners(l);
                newPanel.addModelPlayspaceComboBoxListener(m);
                serverCards.add(conn.getConnectionId(), newPanel);
                ((CardLayout2) serverCards.getLayout()).show(serverCards, conn.getConnectionId());
                serverModel.addEntry(serverName, conn.getConnectionId());


                if (loginType.equals(LoginUtils.ADMIN))
                    loginLabelText.put(conn.getConnectionId(), "logged in as administrator: " + userName);
                else
                    loginLabelText.put(conn.getConnectionId(), "logged in as user: " + userName);
                loginLabel.setText((String) loginLabelText.get(conn.getConnectionId()));
                serverConnections.put(conn.getConnectionId(), conn); // this is needed to cleanup on browser closing
                conn.addReference();
                serverAdd = conn.getServerPort();
                setBrowserLocation(serverAdd);
                //set menu
                notifyModelPlayspaceComboBoxListeners();
            }
        }
    }
}
