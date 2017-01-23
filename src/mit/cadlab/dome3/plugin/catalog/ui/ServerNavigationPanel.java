package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.api.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 2.
 */
public class ServerNavigationPanel extends JScrollPane {
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private RelationDialog relDialog;
    private DomeConnection domeConn;

    private ComponentReference compRef;
    private ServerNavSelectionListener selectionListener;

    public ServerNavigationPanel(RelationDialog relDialog, ComponentReference compRef) {
        this.relDialog = relDialog;
        this.compRef = compRef;
        initComponents();
        tree.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    /** should be called each time space changes and dome connection changes */
    public void updateTree(DomeConnection domeConn) {
        if (domeConn == null) {
            return;
        }

        this.domeConn = domeConn;

        clear();
        tree.addTreeSelectionListener(selectionListener);

        if ("Server".equals(relDialog.getSpace())) {
            addNode(rootNode, new ServerNodeInfo(ServerNodeInfo.PUBLIC_FOLDER_TYPE, "Public"), true);
            addNode(rootNode, new ServerNodeInfo(ServerNodeInfo.PRIVATE_FOLDER_TYPE, "Private"), true);
        }

        if ("User".equals(relDialog.getSpace())) {
            String[] userNames = domeConn.getUserNames();
            for (int i = 0; i < userNames.length; i++) {
                String userName = userNames[i];
                DefaultMutableTreeNode newNode = addNode(rootNode, new ServerNodeInfo(ServerNodeInfo.USER_TYPE, userName), true);
                ((ServerNodeInfo) newNode.getUserObject()).setChildrenAdded(true);
                addNode(newNode, new ServerNodeInfo(ServerNodeInfo.PUBLIC_FOLDER_TYPE, "Public"), true);
                addNode(newNode, new ServerNodeInfo(ServerNodeInfo.PRIVATE_FOLDER_TYPE, "Private"), true);
            }
        }

        if ("Group".equals(relDialog.getSpace())) {
            String[] groupNames = domeConn.getGroupNames();
            for (int i = 0; i < groupNames.length; i++) {
                String groupName = groupNames[i];
                DefaultMutableTreeNode newNode = addNode(rootNode, new ServerNodeInfo(ServerNodeInfo.GROUP_TYPE, groupName), true);
                ((ServerNodeInfo) newNode.getUserObject()).setChildrenAdded(true);
                addNode(newNode, new ServerNodeInfo(ServerNodeInfo.PUBLIC_FOLDER_TYPE, "Public"), true);
                addNode(newNode, new ServerNodeInfo(ServerNodeInfo.PRIVATE_FOLDER_TYPE, "Private"), true);
            }
        }
    }

    private void initComponents() {
        rootNode = new DefaultMutableTreeNode(new ServerNodeInfo(ServerNodeInfo.ROOT_TYPE, "Root Node"));
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent event) {
                DefaultMutableTreeNode node;
                node = (DefaultMutableTreeNode) (event.getTreePath().getLastPathComponent());
                try {
                    int index = event.getChildIndices()[0];
                    node = (DefaultMutableTreeNode) (node.getChildAt(index));
                } catch (NullPointerException exc) { System.out.println("this is normal"); }

                System.out.println("The user has finished editing the node.");
                System.out.println("New value: " + node.getUserObject());
            }

            public void treeNodesInserted(TreeModelEvent event) {
            }

            public void treeNodesRemoved(TreeModelEvent event) {
            }

            public void treeStructureChanged(TreeModelEvent event) {
            }
        });

        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setRowHeight(21);
        tree.setFont(UIUtil.DIALOG_FONT);
        selectionListener = new ServerNavSelectionListener();
        tree.addTreeSelectionListener(selectionListener);

        ImageIcon modelIcon = UIUtil.createImageIcon("images/model.gif", "model");
        ImageIcon projIcon = UIUtil.createImageIcon("images/iProject.gif", "project");
        ImageIcon itfIcon = UIUtil.createImageIcon("images/interface.gif", "interface");
        ImageIcon folderOpenIcon = UIUtil.createImageIcon("images/contextOpen.gif", "folder");
        ImageIcon folderClosedIcon = UIUtil.createImageIcon("images/context.gif", "folder");
        ImageIcon userIcon = UIUtil.createImageIcon("images/userSmall.gif", "user");
        ImageIcon groupIcon = UIUtil.createImageIcon("images/groupSmall.gif", "group");

        ServerNavTreeRenderer treeRenderer = new ServerNavTreeRenderer(folderOpenIcon, folderClosedIcon, modelIcon, projIcon, itfIcon, userIcon, groupIcon);
        tree.setCellRenderer(treeRenderer);

        this.getViewport().setView(tree);

        this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    }

    public ServerNodeInfo getSelectedNodeInfo() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            return (ServerNodeInfo) node.getUserObject();
        }
        return null;
    }

    /**
     * returns DomeFolder, DomeModel, DomeProject, DomeInterface specified using given path string such as "Public/FolderA/FolderB", space is one of "Server", "User", and "Group"
     * returns object is one of DomeFolder, DomeModel, or DomeProject depending on the path.
     * returns null if path is not valid or wrong folder name, model name, project name, or interface name is given.
     */
    private Object getFolderOrModelOrProjectUsingPath(String space, String pathStr, DomeConnection domeConn) {
        DomeFolder curFolder = null;
        String[] pathElements = pathStr.split("/");
        int normalFolderStartIdx = 0;

        /* expecting "Public" */
        if ("Server".equalsIgnoreCase(space)) {
            if (pathElements.length < 1) {
                return null;
            } else if ("Public".equalsIgnoreCase(pathElements[0])) {
                curFolder = domeConn.getServerPublicFolder();
                normalFolderStartIdx = 1;
            } else if ("Private".equalsIgnoreCase(pathElements[0])) {
                curFolder = domeConn.getServerPrivateFolder();
                normalFolderStartIdx = 1;
            }
        }

        /* expecting "username/Public" */
        if ("User".equalsIgnoreCase(space)) {
            if (pathElements.length < 2) {
                return null;
            } else if ("Public".equalsIgnoreCase(pathElements[1])) {
                curFolder = domeConn.getUserPublicFolder(pathElements[0]);
                normalFolderStartIdx = 2;
            } else if ("Private".equalsIgnoreCase(pathElements[1])) {
                curFolder = domeConn.getUserPrivateFolder(pathElements[0]);
                normalFolderStartIdx = 2;
            }
        }

        /* expecting "groupname/Public" */
        if ("Group".equalsIgnoreCase(space)) {
            if (pathElements.length < 2) {
                return null;
            } else if ("Public".equalsIgnoreCase(pathElements[1])) {
                curFolder = domeConn.getGroupPublicFolder(pathElements[0]);
                normalFolderStartIdx = 2;
            } else if ("Private".equalsIgnoreCase(pathElements[1])) {
                curFolder = domeConn.getGroupPrivateFolder(pathElements[0]);
                normalFolderStartIdx = 2;
            }
        }

        /* this could happend because username or groupname is wrong */
        if (curFolder == null) {
            return null;
        }

        /* expecting normalFolderStartIdx is set to the starting point like 2 for FolderA in case of "groupname/Public/FolderA/ModelX/MyInterface" */
        for (int i = normalFolderStartIdx; i < pathElements.length; i++) {
            DomeFolder tempFolder = curFolder.getFolder(pathElements[i]);
            if (tempFolder == null) {
                /* if we have one more path element after a model or a project, it should be interface name */
                DomeModel model =  curFolder.getModelByName(pathElements[i]);
                if (model != null) {
                    if ((i + 1) < pathElements.length) {
                        return model.getInterfaceByName(pathElements[i + 1]);
                    } else {
                        return model;
                    }
                }
                DomeProject proj = curFolder.getProjectByName(pathElements[i]);
                if (proj != null) {
                    if ((i + 1) < pathElements.length) {
                        return proj.getInterfaceByName(pathElements[i + 1]);
                    } else {
                        return proj;
                    }
                } else {
                    return null;
                }
            } else {
                /* if tempFolder exists look into it in the next iteration */
                curFolder = tempFolder;
            }
        }
        return curFolder;
    }

    /** Remove all nodes except the root node. */
    public void clear() {
        tree.removeTreeSelectionListener(selectionListener);
        rootNode.removeAllChildren();
        //treeModel.();
        treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
    }

    public DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent, Object userObj, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(userObj);

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        tree.setEnabled(isEnabled);
        tree.setBackground(isEnabled ? Color.WHITE : Color.LIGHT_GRAY);
    }

    /** administrators/Public/Tutorial examples/Polymer curing model/polymer curing interface
     * Public/Tutorial examples/Polymer curing model/polymer curing interface */
    public void openPath(String pathStr) {
        boolean success = relDialog.tryLogin();
        /* remove first / of pathStr, it shoud start like Public/, john/Private or administrators/Public */
        if (pathStr.startsWith("/")) {
            pathStr.substring(1);
        }

        if (! success) {
            return;
        }

        if ("User".equals(relDialog.getSpace()) || "Group".equals(relDialog.getSpace())) {
            String[] path = pathStr.split("/");
            DefaultMutableTreeNode userOrGruopNode = getChildNodeWithDescription(rootNode, path[0]);
            DefaultMutableTreeNode publicOrPrivateNode = getChildNodeWithDescription(userOrGruopNode, path[1]);
            DefaultMutableTreeNode curParent = publicOrPrivateNode;
            String currentPath = path[0]; // (ex) john
            for (int i = 1; i < path.length - 1; i++) {
                currentPath = currentPath + "/" + path[i];
                expandNode(curParent, currentPath);
                curParent = getChildNodeWithDescription(curParent, path[i + 1]);
            }
            tree.getSelectionModel().setSelectionPath(new TreePath(curParent.getPath()));
        } else if ("Server".equals(relDialog.getSpace())) {
            String[] path = pathStr.split("/");
            DefaultMutableTreeNode publicOrPrivateNode = getChildNodeWithDescription(rootNode, path[0]);
            DefaultMutableTreeNode curParent = publicOrPrivateNode;
            String currentPath = ""; // (ex)
            for (int i = 0; i < path.length - 1; i++) {
                if (i == 0) {
                    currentPath = path[i];
                } else {
                    currentPath = currentPath + "/" + path[i];
                }
                expandNode(curParent, currentPath);
                ((ServerNodeInfo) curParent.getUserObject()).setChildrenAdded(true);
                curParent = getChildNodeWithDescription(curParent, path[i + 1]);
            }
            tree.getSelectionModel().setSelectionPath(new TreePath(curParent.getPath()));
        }
    }

    /** returns a child node with given description (which is model name, project name or interface name */
    public static DefaultMutableTreeNode getChildNodeWithDescription(DefaultMutableTreeNode parent, String childDesc) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (childDesc.equals(((ServerNodeInfo) childNode.getUserObject()).getDescription())) {
                return childNode;
            }
        }
        return null;
    }

    class ServerNodeInfo {
        public static final int ROOT_TYPE = 0;
        public static final int MODEL_TYPE = 1;
        public static final int PROJECT_TYPE = 2;
        public static final int NORMAL_FOLDER_TYPE = 3;
        public static final int PUBLIC_FOLDER_TYPE = 4;
        public static final int PRIVATE_FOLDER_TYPE = 5;
        public static final int USER_TYPE = 6;
        public static final int GROUP_TYPE = 7;
        public static final int INTERFACE_TYPE = 8;

        public boolean isChildrenAdded = false;
        public int type;
        public String desc;

        ServerNodeInfo(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public int getType() {
            return type;
        }

        public String getDescription() {
            return desc;
        }

        public String toString() {
            return getDescription();
        }

        /** use this method to check if this folder, model or project has already populated its children. if it has added children, we should not re-add on it. */
        public boolean isChildrenAdded() {
            return isChildrenAdded;
        }

        public void setChildrenAdded(boolean isChildrenAdded) {
            this.isChildrenAdded = isChildrenAdded;
        }
    }

    class ServerNavTreeRenderer extends DefaultTreeCellRenderer {
        Icon folderOpenIcon;
        Icon folderClosedIcon;
        Icon modelIcon;
        Icon projIcon;
        Icon itfIcon;
        Icon userIcon;
        Icon groupIcon;

        public ServerNavTreeRenderer(Icon folderOpenIcon, Icon folderClosedIcon, Icon modelIcon, Icon projIcon, Icon itfIcon, Icon userIcon, Icon groupIcon) {
            this.folderOpenIcon = folderOpenIcon;
            this.folderClosedIcon = folderClosedIcon;
            this.modelIcon = modelIcon;
            this.projIcon = projIcon;
            this.itfIcon = itfIcon;
            this.userIcon = userIcon;
            this.groupIcon = groupIcon;
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            ServerNodeInfo nodeInfo = (ServerNodeInfo) node.getUserObject();
            int nodeType = nodeInfo.getType();

            if (nodeType == ServerNodeInfo.MODEL_TYPE) {
                setIcon(modelIcon);
            } else if (nodeType == ServerNodeInfo.PROJECT_TYPE) {
                setIcon(projIcon);
            } else if (nodeType == ServerNodeInfo.NORMAL_FOLDER_TYPE || nodeType == ServerNodeInfo.PUBLIC_FOLDER_TYPE || nodeType == ServerNodeInfo.PRIVATE_FOLDER_TYPE) {
                if  (expanded) { setIcon(folderOpenIcon); }
                else { setIcon(folderClosedIcon); }
            } else if (nodeType == ServerNodeInfo.USER_TYPE) {
                setIcon(userIcon);
            } else if (nodeType == ServerNodeInfo.GROUP_TYPE) {
                setIcon(groupIcon);
            } else if (nodeType == ServerNodeInfo.INTERFACE_TYPE) {
                setIcon(itfIcon);
            }
            return this;
        }
    }

    /** expand parent node, the path to parent node is given as pathStr */
    public void expandNode(DefaultMutableTreeNode parent, String pathStr) {
        Object domeXXXX = getFolderOrModelOrProjectUsingPath(relDialog.getSpace(), pathStr, domeConn);

        if (domeXXXX instanceof DomeFolder) {
            DomeFolder curFolder = (DomeFolder) domeXXXX;
            java.util.List subFolders = curFolder.getFolders();
            java.util.List models = curFolder.getModels();
            java.util.List projects = curFolder.getProjects();
            for (int i = 0; i < subFolders.size(); i++) {
                DomeFolder subFolder = (DomeFolder) subFolders.get(i);
                addNode(parent, new ServerNodeInfo(ServerNodeInfo.NORMAL_FOLDER_TYPE, subFolder.getFolderName()), true);
            }
            for (int i = 0; i < models.size(); i++) {
                DomeModel model = (DomeModel) models.get(i);
                addNode(parent, new ServerNodeInfo(ServerNodeInfo.MODEL_TYPE, model.getModelName()), true);
            }
            for (int i = 0; i < projects.size(); i++) {
                DomeProject project = (DomeProject) projects.get(i);
                addNode(parent, new ServerNodeInfo(ServerNodeInfo.PROJECT_TYPE, project.getProjectName()), true);
            }
        }

        if (domeXXXX instanceof DomeModel) {
            DomeModel curModel = (DomeModel) domeXXXX;
            java.util.List interfaces = curModel.getInterfaces();
            for (int i = 0; i < interfaces.size(); i++) {
                DomeInterface itf = (DomeInterface) interfaces.get(i);
                addNode(parent, new ServerNodeInfo(ServerNodeInfo.INTERFACE_TYPE, itf.getInterfaceName()), true);
            }
        }

        if (domeXXXX instanceof DomeProject) {
            DomeProject curProj= (DomeProject) domeXXXX;
            java.util.List interfaces = curProj.getInterfaces();
            for (int i = 0; i < interfaces.size(); i++) {
                DomeInterface itf = (DomeInterface) interfaces.get(i);
                addNode(parent, new ServerNodeInfo(ServerNodeInfo.INTERFACE_TYPE, itf.getInterfaceName()), true);
            }
        }
    }

    class ServerNavSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent event) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            ServerNodeInfo  nodeInfo = (ServerNodeInfo) node.getUserObject();

            String pathStr = "";
            int pathStartIndex = 1; // for Server, a path would be like "RootNode/Public/FolderA/FolderB", so FolderA has index 2.  for User and Group, a path would be like "RootNode/username/Public/FolderA/FolderB", so FolderA has index 3.
            if ("Server".equals(relDialog.getSpace())) {
                pathStartIndex = 1;
            } else { // Group or User
                pathStartIndex = 1;
            }

            TreePath path = tree.getSelectionPath();

            if (path == null) { /* in case there is no selection */ return; }

            for (int i = pathStartIndex; i < path.getPathCount(); i++) {
                DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode) path.getPathComponent(i);
                pathStr = pathStr + pathNode.getUserObject().toString();
                if (i != (path.getPathCount() - 1)) {
                    pathStr = pathStr + "/";
                }
            }
            relDialog.setPath(pathStr);

            /* an interface type node is always a leaf */
            if (! nodeInfo.isChildrenAdded() && nodeInfo.getType() != ServerNodeInfo.INTERFACE_TYPE) {
                /* populate children of current node */
                nodeInfo.setChildrenAdded(true);
                expandNode(node, pathStr);
                tree.scrollPathToVisible(new TreePath(node.getPath()));
            }

            if (getSelectedNodeInfo().getType() == ServerNodeInfo.INTERFACE_TYPE) {
                relDialog.setAddButtonEnabled(true);
            } else {
                relDialog.setAddButtonEnabled(false);
            }
        }
    }
}


