package mit.cadlab.dome3.gui.bookmark;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Feb 4, 2004
 * Time: 7:15:37 PM
 * To change this template use Options | File Templates.
 */
public class MoveBookmarkFolder extends JPanel {

    protected ArrayList folderList;
    protected JTree list;
    protected DefaultMutableTreeNode rootNode;
    static String selectedFolder = null;

    public static String showValueInput(Component parent, ArrayList folderList) {

        MoveBookmarkFolder editor = new MoveBookmarkFolder(folderList);

        JDialog d = DialogFactory.createDialog(parent, "Move bookmarks", editor, true, false);

        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.pack();
        d.show();

        return selectedFolder;

    }

    /**
     *
     * @param folderList:only a string list of folders, no inside contents
     */
    public MoveBookmarkFolder(ArrayList folderList) {
        this.folderList = folderList;
        creatGui();
        this.setPreferredSize(new Dimension(300, 200));
      }


    protected void creatGui() {

        //create two buttons
        JButton cancleButton = Templates.makeButton("cancel", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JButton okButton = Templates.makeButton("ok", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedFolder = getSelectedFolder();
                dispose();
            }
        });
        //create a JList outof the data
        rootNode = new DefaultMutableTreeNode("Bookmarks");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        for (int i = 0; i < folderList.size(); i++) {
            String folder = (String) folderList.get(i);
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(folder);
            treeModel.insertNodeInto(child, rootNode, rootNode.getChildCount());
        }

        list = new JTree(treeModel);
        list.setEditable(false);
        list.setCellRenderer(new MyCellRenderer());
        list.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        list.setShowsRootHandles(false);
        list.setRowHeight(22);
        list.setFont(Templates.FONT11);
        JScrollPane sp = new JScrollPane(list);
        //create intruction
        String intr = "Select the folder in which the bookmark should be located";
        JTextArea instruction = Templates.makeDisplayTextArea(intr);

        JComponent[] comps = {instruction, sp, okButton, cancleButton};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    protected String getSelectedFolder() {
        TreePath currentSelection = list.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                    (currentSelection.getLastPathComponent());
            if(currentNode==rootNode) return "root";
            Object nodeInfo = currentNode.getUserObject();
            return nodeInfo.toString();
        }
        return null;

    }

    private void dispose() {

        SwingUtilities.windowForComponent(this).dispose();
    }

    public static void main(String[] args) {
        ArrayList dummydata = new ArrayList();
        dummydata.add("folder 1");
        dummydata.add("folder 2");
        dummydata.add("folder 3");
        dummydata.add("folder 4");
        showValueInput(null, dummydata);
    }

    class MyCellRenderer extends DefaultTreeCellRenderer {
        // queries TreeObject for appropriate icon
        // does not paint focus
      public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            if (value instanceof DefaultMutableTreeNode) {

                setLeafIcon(BookmarkManager.closed_Icon);
                if (expanded)
                    setOpenIcon(BookmarkManager.closed_Icon);
                else
                    setClosedIcon(BookmarkManager.open_Icon);
           }
            return super.getTreeCellRendererComponent(tree, value, selected, expanded,
                    leaf, row, false);
        }

    }

}
