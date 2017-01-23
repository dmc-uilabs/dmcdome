package mit.cadlab.dome3.gui.bookmark;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.tree.ObjectTreeCellEditor;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 11, 2003
 * Time: 10:36:32 AM
 * To change this template use Options | File Templates.
 */
// this is really Organise favorites
// add action listeners to the List and to the table
// when you delete from list you should also have it delete from table


public class BookmarkManager extends JPanel implements PropertyChangeListener {
    protected JTree tree;
    protected DArrayList bookmarks;
    protected DefaultTreeModel treeModel;
    protected JLabel nameTextLabel;
    //protected JLabel urlTextLabel;
    protected JTextArea urlTextArea;
    protected DefaultMutableTreeNode rootNode;

    protected static Icon closed_Icon = UIManager.getIcon("Tree.closedIcon");
    protected static Icon open_Icon = UIManager.getIcon("Tree.openIcon");
    protected static Icon InterfaceIcon = DomeIcons.getIcon(DomeIcons.INTERFACE);
    protected static Icon AnaylsisIcon = DomeIcons.getIcon(DomeIcons.ANALYSIS_TOOL);
    protected static Icon ProjectIcon = DomeIcons.getIcon(DomeIcons.PROJECT);
    protected static Icon PlayspaceIcon = DomeIcons.getIcon(DomeIcons.PLAYSPACE);
    protected static Icon ServerIcon = Templates.makeImageIcon("mit/cadlab/dome3/icons/navigation/home.gif");

    protected JButton moveToFolderButton,removeButton,renameButton;

    public BookmarkManager() {
        bookmarks = BookmarkCache.getBookmarks();
        createGUI();

        this.setPreferredSize(new Dimension(400, 300));
    }

    public void createGUI() {
        JButton closeButton = Templates.makeButton("close", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JLabel nameLabel = Templates.makeLabel("name: ", Templates.FONT11I);
        nameTextLabel = Templates.makeLabel("", Templates.FONT11B);
        JLabel urlLabel = Templates.makeLabel("url: ", Templates.FONT11I);
        //urlTextLabel = Templates.makeLabel("", Templates.FONT11);
        urlTextArea = Templates.makeDisplayTextArea("", Templates.FONT11);
        urlTextArea.setLineWrap(true);
        urlTextArea.setWrapStyleWord(true);
        JComponent[] comps = {nameLabel, nameTextLabel, urlLabel, urlTextArea, makeButtonPanel(), makeTree(), closeButton};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0),

            new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    public void setSelectionName(String name) {
        nameTextLabel.setText(name);
    }

    public void setSelectionUrl(String url) {
        urlTextArea.setText(url);
    }

    private JPanel makeButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton createFolderButton = Templates.makeButton("create...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                create();
            }
        });
        createFolderButton.setToolTipText("create a new bookmark folder");
        renameButton = Templates.makeButton("rename", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rename();
            }
        });
        renameButton.setToolTipText("rename the selected folder or bookmark");
        removeButton = Templates.makeButton("delete", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });
        removeButton.setToolTipText("delete the selected folder or bookmark");
        moveToFolderButton = Templates.makeButton("move...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveToFolder();
            }
        });
        moveToFolderButton.setToolTipText("move selected bookmark to a folder");

        JComponent[] comps = {createFolderButton, renameButton, removeButton, moveToFolderButton};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(buttonPanel, comps, gbcs);
        return buttonPanel;
    }

    private void moveToFolder() {
        //getfolderlist
        ArrayList folders = new ArrayList();
        for (int i = 0; i < bookmarks.size(); i++) {
            BookmarkFolder f = (BookmarkFolder) bookmarks.get(i);
            if (!f.getFoldername().equals("root"))
                folders.add(f.getFoldername());
        }

        //create a new gui
        String tofolder = MoveBookmarkFolder.showValueInput(this, folders);
        if (tofolder == null) return;//cancle
        //DefaultMutableTreeNode newfolder=tree.get
        //repaint the tree
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());
            Object nodeInfo = currentNode.getUserObject();
            if (!(nodeInfo instanceof BookmarkInfo)) return;//must be an error
            BookmarkInfo bmk = (BookmarkInfo) nodeInfo;
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                DefaultMutableTreeNode newparent = getFolderNode(tofolder);
                //if (newparent != null) addObject(newparent, bmk, true);
                BookmarkCache.moveToFolder(bmk, BookmarkCache.getFolder(tofolder));
            }
        }
    }


    private void delete() {
        removeCurrentNode();
    }

    private DefaultMutableTreeNode getFolderNode(String foldername) {
        if (foldername.equals("root")) return rootNode;
        if (!BookmarkCache.containsFolder(foldername)) return null;
        int all = rootNode.getChildCount();
        for (int i = 0; i < all; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if (node.getUserObject() instanceof BookmarkFolder) {
                BookmarkFolder fd = (BookmarkFolder) node.getUserObject();
                if (fd.getFoldername().equals(foldername)) return node;
            }
        }
        return null;
    }

    private void rename() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

        if (node == null) return;
        tree.startEditingAtPath(new TreePath(node.getPath()));
    }

    private void create() {
        //create a new gui
        BookmarkFolder newFolder = BookmarkCache.creatFolder(getNewFolderName());
        newFolder.addPropertyChangeListener(this);
        addObject(rootNode, newFolder, true);
    }

    private String getNewFolderName() {
        String base = "New Folder";
        int suffix = 2;
        if (!BookmarkCache.containsFolder(base)) return base;

        while (BookmarkCache.containsFolder(base + suffix)) {
            suffix++;
        }
        return base + suffix;
    }

    private JScrollPane makeTree() {
        rootNode = new DefaultMutableTreeNode("Bookmarks");
        treeModel = new myTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setCellRenderer(new MyTreeCellRenderer());
        if (bookmarks.size() != 0) {
            for (Iterator iter = bookmarks.iterator(); iter.hasNext();) {
                BookmarkFolder f = (BookmarkFolder) iter.next();
                f.addPropertyChangeListener(this);
                if (f.getFoldername().equals("root"))
                //insert the thing directly on root
                {
                    for (Iterator i = f.getBookmarks().iterator(); i.hasNext();) {
                        BookmarkInfo bmk = (BookmarkInfo) i.next();
                        addObject(rootNode, bmk, true);
                    }

                } else {
                    DefaultMutableTreeNode folderNode = addObject(rootNode, f, true);
                    for (Iterator i = f.getBookmarks().iterator(); i.hasNext();) {
                        BookmarkInfo bmk = (BookmarkInfo) i.next();
                        addObject(folderNode, bmk, true);
                    }

                }
            }
        }
        treeModel.addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                DefaultMutableTreeNode node;
                node = (DefaultMutableTreeNode)
                        (e.getTreePath().getLastPathComponent());

                /*
                 * If the event lists children, then the changed
                 * node is the child of the node we've already
                 * gotten.  Otherwise, the changed node and the
                 * specified node are the same.
                 */
                try {
                    int index = e.getChildIndices()[0];
                    node = (DefaultMutableTreeNode)
                            (node.getChildAt(index));
                    //refresh the display
                    showInfo(node);
                } catch (NullPointerException exc) {
                }

            }

            public void treeNodesInserted(TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
            }
        });


        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(false);
        tree.setRowHeight(22);
        tree.setFont(Templates.FONT11);
        JScrollPane treeView = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Templates.setEmptyBorder(treeView);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();

                if (node == null) return;
                showInfo(node);
            }
        });

        tree.setCellEditor(new MyTreeCellEditor(tree, (MyTreeCellRenderer) tree.getCellRenderer()));
        return treeView;
    }

    protected void showInfo(DefaultMutableTreeNode node) {
        Object nodeInfo = node.getUserObject();
        if (node == rootNode) {
            setSelectionName("Bookmarks");
            setSelectionUrl("Root Folder");
            if (moveToFolderButton.isEnabled()) moveToFolderButton.setEnabled(false);
            if (removeButton.isEnabled()) removeButton.setEnabled(false);
            if (renameButton.isEnabled()) renameButton.setEnabled(false);
        } else if (node.isLeaf() && nodeInfo instanceof BookmarkInfo) {
            BookmarkInfo book = (BookmarkInfo) nodeInfo;
            setSelectionName(book.getAliasname());
            setSelectionUrl(book.getInfo());
            if (!moveToFolderButton.isEnabled()) moveToFolderButton.setEnabled(true);
            if (!removeButton.isEnabled()) removeButton.setEnabled(true);
            if (!renameButton.isEnabled()) renameButton.setEnabled(true);
        } else {
            if (nodeInfo instanceof BookmarkFolder) {
                BookmarkFolder book = (BookmarkFolder) nodeInfo;
                setSelectionName(book.getFoldername());
                setSelectionUrl("Folder");
                if (moveToFolderButton.isEnabled()) moveToFolderButton.setEnabled(false);
                if (!removeButton.isEnabled()) removeButton.setEnabled(true);
                if (!renameButton.isEnabled()) renameButton.setEnabled(true);
            }
        }
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (child instanceof BookmarkFolder) childNode.setAllowsChildren(true);
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    public BookmarkInfo getSelectedBookmark() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());
            Object nodeInfo = currentNode.getUserObject();
            if (nodeInfo instanceof BookmarkInfo) return (BookmarkInfo) nodeInfo;
        }
        return null;
    }

    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());
            Object nodeInfo = currentNode.getUserObject();

            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                if (nodeInfo instanceof BookmarkFolder) {
                    ((BookmarkFolder) nodeInfo).removePropertyChangeListener(this);
                    BookmarkCache.RemoveBookmarkFolder((BookmarkFolder) nodeInfo);
                } else if (nodeInfo instanceof BookmarkInfo)
                    BookmarkCache.RemoveBookmark((BookmarkInfo) nodeInfo);
                return;
            }

        }

        // Either there was no selection, or the root was selected.
        Toolkit.getDefaultToolkit().beep();
    }


    public static void main(String args[]) {
        BookmarkManager window = new BookmarkManager();
        window.setSelectionName("my bookmark");
        window.setSelectionUrl("http://wwsw.myModel.dml//abd.ede.fddd.gth.kus.kkk/wkrwqo/erwerqere/werwer/4512512");
        JFrame frame = Templates.makeTestFrame("test bookmark manager");
        frame.getContentPane().add(window);
        frame.pack();
        frame.setVisible(true);
    }

    private void dispose() {

        SwingUtilities.windowForComponent(this).dispose();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        //update tree
        if (evt.getPropertyName().endsWith("added")) {
           // System.out.println("added");
            //update tree
            Object src = evt.getSource();//bookmarkfolder
            if (!(src instanceof BookmarkFolder)) return;
            java.util.List newValues = (java.util.List) evt.getNewValue();//should be new items,

            BookmarkFolder f = (BookmarkFolder) src;
            for (Iterator i = newValues.iterator(); i.hasNext();) {
                Object obj = i.next();
                if (obj instanceof BookmarkInfo) {
                    addBookmarkInfoLeafNode((BookmarkInfo) obj, f);
                }
            }
        } else if (evt.getPropertyName().endsWith("removed")) {
           // System.out.println("removed");
            //update tree
            Object src = evt.getSource();//bookmarkfolder
            if (!(src instanceof BookmarkFolder)) return;
            //first get the node from the tree
            BookmarkFolder f = (BookmarkFolder) src;
            DefaultMutableTreeNode folderNode = getFolderNode(f.getFoldername());
            java.util.List newValues = (java.util.List) evt.getNewValue();//should be new items,
            for (Iterator i = newValues.iterator(); i.hasNext();) {
                Object obj = i.next();
                if (obj instanceof BookmarkInfo) {
                    removeBookmarkInfoLeafNode((BookmarkInfo) obj);
                }

            }
        }

    }

    protected void removeBookmarkInfoLeafNode(BookmarkInfo inf) {
        int index = -1;
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if (!node.isLeaf()) return;
            if (node.getUserObject() instanceof BookmarkInfo) {
                BookmarkInfo fi = (BookmarkInfo) node.getUserObject();
                if (fi.equals(inf)) index = i;
            }
        }
        if (index == -1) return;
        rootNode.remove(index);
    }

    protected void addBookmarkInfoLeafNode(BookmarkInfo inf, BookmarkFolder f) {
        DefaultMutableTreeNode folderNode = getFolderNode(f.getFoldername());
        if (folderNode == null) folderNode = rootNode;
        addObject(folderNode, inf, true);
    }

//inner classes
    class myTreeModel extends DefaultTreeModel {
        public void valueForPathChanged(TreePath path, Object newValue) {
            if (newValue == null || newValue.toString().equals(""))
                return; // cancellation
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                Object obj = ((DefaultMutableTreeNode) node).getUserObject();
                if (obj instanceof BookmarkFolder) {
                    if (BookmarkCache.containsFolder(newValue.toString())) {
                        OneButton1Msg.showError(BookmarkManager.this, "Error renaming folder", "Cannot rename the folder " + ((BookmarkFolder) obj).getFoldername() + " : a folder with same name already exists, please specify another name", "Ok", OneButton1Msg.DEFAULT_SIZE);
                        return;//cancel
                    }
                    ((BookmarkFolder) obj).setFoldername(newValue == null ? "" : newValue.toString());
                } else if (obj instanceof BookmarkInfo)
                    ((BookmarkInfo) obj).setAliasname((newValue == null ? "" : newValue.toString()));
                nodeChanged((DefaultMutableTreeNode) node);
            } else {
                super.valueForPathChanged(path, newValue);
            }
        }

        public myTreeModel(TreeNode root) {
            super(root);

        }
    }

    class MyTreeCellEditor extends ObjectTreeCellEditor {
        public Component getTreeCellEditorComponent(JTree tree, Object value,
                                                    boolean isSelected,
                                                    boolean expanded,
                                                    boolean leaf, int row) {
            return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
        }

        public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
        }

        public boolean stopCellEditing() {
            System.out.println(this.getCellEditorValue().getClass());
            /* String text = this.getCellEditorValue();
             if (text.equals("")) {
                 cancelCellEditing();
                 return false;
             } else {
                 try {
                     parseText(text);   */
            return super.stopCellEditing();
            /*	} catch (Exception ex) {
                    System.err.println("invalid data");
                    return false;
                }
            }*/
        }

        public boolean isCellEditable(EventObject event) {
            if ((event instanceof MouseEvent) &&
                    // should not activate if modifiers present??
                    SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
                MouseEvent me = (MouseEvent) event;
                if ((me.getClickCount() == clickCountToEdit) &&
                        inHitRegion(me.getX(), me.getY())) { // clicked on text
                    prepareForEditing();
                    me.consume();
                    return true;
                }
                return false;
            } else {
                if (event == null) return true;
                return false;
            }
        }

    }

    class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        // queries TreeObject for appropriate icon
        // does not paint focus


        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            if (value instanceof DefaultMutableTreeNode) {
                Object tObj = ((DefaultMutableTreeNode) value).getUserObject();
                if (tObj != null) {
                    if (tObj instanceof BookmarkFolder) {
                        setLeafIcon(closed_Icon);
                        if (expanded)
                            setOpenIcon(open_Icon);
                        else
                            setClosedIcon(closed_Icon);
                    } else if (tObj instanceof BookmarkInfo) {   //then they r all leaves
                        if (((BookmarkInfo) tObj).getType().equals(DomeFile.PROJECT_TYPE)) {
                            setLeafIcon(ProjectIcon);
                        } else if (((BookmarkInfo) tObj).getType().equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
                            setLeafIcon(AnaylsisIcon);
                        } else if (((BookmarkInfo) tObj).getType().equals(DomeFile.PLAYSPACE_TYPE)) {
                            setLeafIcon(PlayspaceIcon);
                        } else if (((BookmarkInfo) tObj).getType().equals(BookmarkCache.ANALYSIS_Interface)) {
                            setLeafIcon(InterfaceIcon);
                        } else if (((BookmarkInfo) tObj).getType().equals(DomeFile.INTERFACE_TYPE) || ((BookmarkInfo) tObj).getType().equals(BookmarkCache.Project_Interface)) {
                            setLeafIcon(InterfaceIcon);
                        }  else if (((BookmarkInfo) tObj).getType().equals(BookmarkCache.Server)) {
                            setLeafIcon(ServerIcon);
                        }
                    }

                }
            }
            return super.getTreeCellRendererComponent(tree, value, selected, expanded,
                    leaf, row, false);
        }

    }

}