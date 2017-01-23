// ObjectTreeCellRenderer.java
package mit.cadlab.dome3.swing.tree;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ObjectTreeCellRenderer extends DefaultTreeCellRenderer
{
	// queries TreeObject for appropriate icon
	// does not paint focus

	public Component getTreeCellRendererComponent(JTree tree, Object value,
	                                              boolean selected, boolean expanded,
	                                              boolean leaf, int row, boolean hasFocus)
	{
		if (value instanceof ObjectTreeNode) {
			TreeObject tObj = ((ObjectTreeNode) value).getTreeObject();
			if (tObj != null) {
				if (leaf)
					setLeafIcon(tObj.getIcon(TreeObject.LEAF_ICON));
				else if (expanded)
					setOpenIcon(tObj.getIcon(TreeObject.OPEN_ICON));
				else
					setClosedIcon(tObj.getIcon(TreeObject.CLOSED_ICON));
			}
		}
		return super.getTreeCellRendererComponent(tree, value, selected, expanded,
		                                          leaf, row, false);
	}

}
