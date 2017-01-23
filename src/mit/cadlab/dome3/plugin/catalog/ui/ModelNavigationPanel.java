package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CImplementation;
import mit.cadlab.dome3.plugin.catalog.core.CInterface;
import mit.cadlab.dome3.plugin.catalog.core.CModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 30.
 */
public class ModelNavigationPanel extends JPanel {
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private JPanel modelNamePanel;
    private JLabel modelNameLb;
    private JButton modelEditBt;

    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private ComponentReference compRef;

    public ModelNavigationPanel(ComponentReference compRef) {
        this.compRef = compRef;
        initComponents();

    }

    public void renameInterfaceNode(String oldItfName, String newItfName) {
        DefaultMutableTreeNode implNode;
        DefaultMutableTreeNode itfNode;
        NodeInfo nodeInfo;
        for (int i =0; i < treeModel.getChildCount(rootNode); i++) {
            implNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, i);
            nodeInfo = (NodeInfo) implNode.getUserObject();
            if (oldItfName.equals(nodeInfo.itfName)) {
                nodeInfo.itfName = newItfName;
                treeModel.nodeChanged(implNode);
            }
            for (int j = 0; j < treeModel.getChildCount(implNode); j++) {
                itfNode = (DefaultMutableTreeNode) treeModel.getChild(implNode, j);
                nodeInfo = (NodeInfo) itfNode.getUserObject();
                if (oldItfName.equals(nodeInfo.itfName)) {
                    nodeInfo.itfName = newItfName;
                    treeModel.nodeChanged(itfNode);
                }
            }
        }
    }

    /** add interface node to this tree. the same interface node should be placed under each of implementation nodes */
    public void addInterfaceNode(String itfName) {
        DefaultMutableTreeNode implNode;
        NodeInfo nodeInfo;
        for (int i =0; i < treeModel.getChildCount(rootNode); i++) {
            implNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, i);
            nodeInfo = (NodeInfo) implNode.getUserObject();
            DefaultMutableTreeNode itfNode = this.addObject(implNode, new NodeInfo(itfName, nodeInfo.implName), false);
            //treeModel.nodeChanged(implNode);
        }

        selectDefaultTreeNode(itfName, null);
    }

    /** remove interface node from this tree. the same interface node exists under each of implementation nodes, so iterate through and delete them */
    public void removeInterfaceNode(String itfName) {
        int itfIdxAfterRemoval = -1;

        DefaultMutableTreeNode implNode;
        DefaultMutableTreeNode itfNode;
        NodeInfo nodeInfo;
        for (int i =0; i < treeModel.getChildCount(rootNode); i++) {
            implNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, i);
            nodeInfo = (NodeInfo) implNode.getUserObject();
            for (int j = 0; j < treeModel.getChildCount(implNode); j++) {
                itfNode = (DefaultMutableTreeNode) treeModel.getChild(implNode, j);
                nodeInfo = (NodeInfo) itfNode.getUserObject();
                if (itfName.equals(nodeInfo.itfName)) {
                    treeModel.removeNodeFromParent(itfNode);
                    itfIdxAfterRemoval = j - 1;
                    //treeModel.nodeChanged(itfNode);
                }
            }
            //treeModel.nodeChanged(implNode);
        }

        /* show another, which default selected in the same implementation, interface after deletion */
        if (itfName.equals(compRef.getImplementationEditor().getInterfaceName())) {
            selectDefaultTreeNode(null, compRef.getImplementationEditor().getImplementationName());
        }
    }

    /** add new implementation node. first add implNode to rootNode. second add itfNode to implNode */
    public void addImplementationNode(String implName) {
        //todo: how to deal with the case when there were no existing implementation
        DefaultMutableTreeNode firstImplNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, 0);

        DefaultMutableTreeNode implNode = this.addObject(rootNode, new NodeInfo(implName), true);


        for (int i =0; i < treeModel.getChildCount(firstImplNode); i++) {
            DefaultMutableTreeNode itfNode = (DefaultMutableTreeNode) treeModel.getChild(firstImplNode, i);
            NodeInfo nodeInfo = (NodeInfo) itfNode.getUserObject();
            this.addObject(implNode, new NodeInfo(nodeInfo.itfName, implName), true);
        }
    }

    public void setModelNameEnabled(boolean isEnabled) {
        modelNameLb.setEnabled(isEnabled);
        modelEditBt.setEnabled(isEnabled);
        if (! isEnabled) {
            setModelNameInNavPanel("(model not loaded)");
        }
    }

    public void setModelNameInNavPanel(String modelName) {
//        rootNode.setUserObject(modelName);
//        treeModel.nodeChanged(rootNode);
        modelNameLb.setText(modelName);
        modelNameLb.setToolTipText(modelName);
    }

    /** remove implementation node from this tree. implementation nodes are just under root node */
    public void removeImplementationNode(String implName) {
        DefaultMutableTreeNode implNode;
        NodeInfo nodeInfo;
        for (int i =0; i < treeModel.getChildCount(rootNode); i++) {
            implNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, i);
            nodeInfo = (NodeInfo) implNode.getUserObject();
            if (implName.equals(nodeInfo.implName)) {
                treeModel.removeNodeFromParent(implNode);

//                /* show another (default selected) implementation after deletion */
//                selectDefaultTreeNode(implName);
            }
        }
    }

    public void renameImplementationNode(String oldImplName, String newImplName) {
        DefaultMutableTreeNode implNode;
        DefaultMutableTreeNode itfNode;
        NodeInfo nodeInfo;
        for (int i = 0; i < treeModel.getChildCount(rootNode); i++) {
            implNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, i);
            nodeInfo = (NodeInfo) implNode.getUserObject();
            if (oldImplName.equals(nodeInfo.implName)) {
                nodeInfo.implName = newImplName;
                treeModel.nodeChanged(implNode);
            }
            for (int j = 0; j < treeModel.getChildCount(implNode); j++) {
                itfNode = (DefaultMutableTreeNode) treeModel.getChild(implNode, j);
                nodeInfo = (NodeInfo) itfNode.getUserObject();
                if (oldImplName.equals(nodeInfo.implName)) {
                    nodeInfo.implName = newImplName;
                    treeModel.nodeChanged(itfNode);
                }
            }
        }
    }

    public void unloadModel() {
        clear();
    }

    /** clear the current tree, and load a new model */
    public void loadModel(CModel model) {
        unloadModel();

        Map itfMap = model.getInterfaceMap();
        Set implNameSet = new TreeSet();
        Set itfNameSet = new TreeSet();
        for (Iterator i = itfMap.values().iterator(); i.hasNext(); ) {
            CInterface itf = (CInterface) i.next();
            itfNameSet.add(itf.getName());
            Map implMap = itf.getImplementationMap();
            for (Iterator j = implMap.values().iterator(); j.hasNext(); ) {
                CImplementation impl = (CImplementation) j.next();
                implNameSet.add(impl.getName());
            }
        }

        rootNode.setUserObject(model.getName());

        DefaultMutableTreeNode implNode;

        for (Iterator i = implNameSet.iterator(); i.hasNext();) {
            String implName = (String) i.next();
            implNode = this.addObject(rootNode, new NodeInfo(implName), true);
            for (Iterator j = itfNameSet.iterator(); j.hasNext();) {
                String itfName = (String) j.next();
                this.addObject(implNode, new NodeInfo(itfName, implName), true);
            }
        }
    }

    public void selectDefaultTreeNode() {
        selectDefaultTreeNode(null, null);
    }

    /** select default tree node, which is 'the first itf node under the first impl node' when implName is null
     * and 'the first itf node under impl node with a given implName' when implName is not null.
     * you can call this when there is no node, but will not do anything, either.
     * you can also specify itfName, too. if itfName is null, it will select first occurence of interface node,
     * and otherwise it will choose interface node with a given name
     * if wrong itfName or implName is given, it will select the last bottom node.
     * if you give argument implName */
    public void selectDefaultTreeNode(String itfName, String implName) {
        /* move selection to the first impl-itf node */
        DefaultMutableTreeNode firstItfNode = null;
        DefaultMutableTreeNode firstImplNode = null;
        TreeModel treeModel = tree.getModel();
        if (treeModel.getChildCount(rootNode) > 0) {
            if (implName == null) {
                firstImplNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, 0);
            } else {
                for (int i = 0; i < treeModel.getChildCount(rootNode); i++) {
                    firstImplNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, i);
                    if (implName.equals(((NodeInfo) firstImplNode.getUserObject()).implName)) {
                        break;
                    }
                }
            }

            if (treeModel.getChildCount(firstImplNode) > 0) {
                if (itfName == null) {
                    firstItfNode = (DefaultMutableTreeNode) treeModel.getChild(firstImplNode, 0);
                } else {
                    for (int i = 0; i < treeModel.getChildCount(firstImplNode); i++) {
                        firstItfNode = (DefaultMutableTreeNode) treeModel.getChild(firstImplNode, i);
                        if (itfName.equals(((NodeInfo) firstImplNode.getUserObject()).itfName)) {
                            break;
                        }
                    }
                }

                tree.setSelectionPath(new TreePath(new Object[] { rootNode, firstImplNode, firstItfNode }));
            }
        }
    }

    /** after loading, Navigation Panel one of three status. not selected, interface node selected, or implementation node selected.
     * "implementation" or "interface" or "notselected" */
    public String getSelectedNodeType() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return "notselected";
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (((NodeInfo) node.getUserObject()).isImplementationNode()) {
            return "implementation";
        } else {
            return "interface";
        }
    }

    /** returns currently NodeInfo of currently selected node */
    public NodeInfo getSelectedNodeInfo() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        return ((NodeInfo) node.getUserObject());
    }

    private void initComponents() {
        rootNode = new DefaultMutableTreeNode("Root Node");
        treeModel = new DefaultTreeModel(rootNode);

        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setRowHeight(22);
        tree.setFont(UIUtil.NAV_TREE_FONT);

        ImageIcon leafIcon = UIUtil.createImageIcon("images/interface.gif", "interface");
        ImageIcon openIcon = UIUtil.createImageIcon("images/contextOpen.gif", "implementation");
        ImageIcon closedIcon = UIUtil.createImageIcon("images/context.gif", "implementation");
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(leafIcon);
        renderer.setOpenIcon(openIcon);
        renderer.setClosedIcon(closedIcon);

        tree.setCellRenderer(new NavTreeRenderer());
        tree.setBorder(new EmptyBorder(3, 3, 3, 3));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        modelNamePanel = new JPanel();
        modelNamePanel.setBackground(UIUtil.NAV_PANEL_BG);
        modelNamePanel.setLayout(new BoxLayout(modelNamePanel, BoxLayout.X_AXIS));
        modelNamePanel.setBorder(UIUtil.MODEL_NAME_LABEL_BORDER);
        modelNameLb = new JLabel("(model not loaded)", UIUtil.createImageIcon("images/model.gif", "model"), JLabel.LEFT);
        modelNameLb.setFont(UIUtil.NAV_TREE_FONT);
        modelNameLb.setIconTextGap(5);
        modelNameLb.setPreferredSize(new Dimension(UIUtil.NAV_BT_PANEL_WIDTH - 30, modelNameLb.getPreferredSize().height));
        modelNameLb.setMaximumSize(new Dimension(Short.MAX_VALUE, modelNameLb.getPreferredSize().height));
        modelNameLb.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() >= 2) {
                    if (compRef.getCurrentCModel() != null) {
                        ModelEditorKit.EditModelAction.actionPerformed(new ActionEvent(this, 0, null));
                    }
                }
            }
        });
        modelEditBt = new JButton("edit");
        modelEditBt.setFont(UIUtil.DIALOG_SMALL_FONT);
        modelNameLb.setPreferredSize(new Dimension(30, modelNameLb.getPreferredSize().height));
        modelEditBt.setMaximumSize(new Dimension(30, modelNameLb.getPreferredSize().height));
        modelEditBt.addActionListener(ModelEditorKit.EditModelAction);
        modelNamePanel.add(modelNameLb);
        //modelNamePanel.add(modelEditBt);
        modelNamePanel.setPreferredSize(new Dimension(UIUtil.NAV_BT_PANEL_WIDTH, 36));
        modelNamePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        //modelNamePanel.setMinimumSize(new Dimension(UIUtil.NAV_BT_PANEL_WIDTH, 36));

        this.add(modelNamePanel);

        JScrollPane treeView = new JScrollPane(tree);
        treeView.setBorder(UIUtil.NAV_TREE_BORDER);
        treeView.setBackground(UIUtil.NAV_PANEL_BG);
        this.add(treeView);

        setModelNameEnabled(false);

        /* listen to tree selection changes */
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                /* clear selection of relations */
                compRef.clearBarAndCellSelection();

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                if (node == null) return;
                if (! (node.getUserObject() instanceof NodeInfo)) {
                    return;
                }

                NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
                if (node.isLeaf()) {
                    if (compRef.getImplementationEditor().getInterfaceName() == null && compRef.getImplementationEditor().getImplementationName() == null) {
                        compRef.getImplementationEditor().displayImplementation(compRef.getCurrentCModel(), nodeInfo.itfName, nodeInfo.implName);
                    } else if (nodeInfo.itfName.equals(compRef.getImplementationEditor().getInterfaceName()) && nodeInfo.implName.equals(compRef.getImplementationEditor().getImplementationName())) {
                        /* same interface & implementation no need for refresh the display */
                    } else {
                        compRef.getImplementationEditor().displayImplementation(compRef.getCurrentCModel(), nodeInfo.itfName, nodeInfo.implName);
                    }
                } else {
                    /* display does not change */
                }
            }
        });

        this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    }

    /** Remove all nodes except the root node. */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }

        // Either there was no selection, or the root was selected.
        toolkit.beep();
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    class NodeInfo {
        String itfName;
        String implName;

        /** for implementation node */
        public NodeInfo(String implName) {
            this.itfName = null;
            this.implName = implName;
        }

        /** for interface node with implementation node as its parent */
        public NodeInfo(String itfName, String implName) {
            this.itfName = itfName;
            this.implName = implName;
        }

        /**
         * - impl X
         *    - itf A
         *    - itf B
         *    - itf C
         * - impl Y
         *    - itf A
         *    - itf B
         *    - itf C
         *
         * in the above example, impl X and impl Y node will give true for isImplementationNode().
         * itf A, itf B, and itf C node will give false for isImplementationNode().
         */
        public boolean isImplementationNode() {
            return itfName == null;
        }

        public String toString() {
            if (isImplementationNode()) {
                return implName;
            } else {
                return itfName;
            }
        }
    }

    class NavTreeRenderer extends DefaultTreeCellRenderer {
        ImageIcon itfIcon = UIUtil.createImageIcon("images/interface.gif", "interface");
        ImageIcon implOpenIcon = UIUtil.createImageIcon("images/contextOpen.gif", "implementation");
        ImageIcon implClosedIcon = UIUtil.createImageIcon("images/context.gif", "implementation");

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (isInterface(value)) {
                setIcon(itfIcon);
            } else if (isImplementation(value)) {
                if  (expanded) { setIcon(implOpenIcon); }
                else { setIcon(implClosedIcon); }
            }
            return this;
        }
    }

    protected static boolean isInterface(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node != null && node.getUserObject() instanceof NodeInfo) {
            return ! ((NodeInfo) node.getUserObject()).isImplementationNode();
        }
        return false;
    }

    protected static boolean isImplementation(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node != null && node.getUserObject() instanceof NodeInfo) {
            return ((NodeInfo) node.getUserObject()).isImplementationNode();
        }
        return false;
    }
}


