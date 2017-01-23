// DomeTreeUI.java
package mit.cadlab.dome3.swing.plaf;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.plaf.metal.MetalTreeUI;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class DTreeUI extends MetalTreeUI
{
	// override to always paint expand control
	// new items start expanded
	// mouse handler to clear selection when other areas of tree are clicked
	protected void paintExpandControl(Graphics g,
	                                  Rectangle clipBounds, Insets insets,
	                                  Rectangle bounds, TreePath path,
	                                  int row, boolean isExpanded,
	                                  boolean hasBeenExpanded,
	                                  boolean isLeaf)
	{
		Object value = path.getLastPathComponent();

		// Draw icons if not a leaf and either hasn't been loaded,
		// or the model child count is > 0.
		if (!isLeaf) {
			int middleXOfKnob;
			if (tree.getComponentOrientation().isLeftToRight()) {
				middleXOfKnob = bounds.x - (getRightChildIndent() - 1);
			} else {
				middleXOfKnob = bounds.x + bounds.width + getRightChildIndent();
			}
			int middleYOfKnob = bounds.y + (bounds.height / 2);

			if (isExpanded) {
				Icon expandedIcon = getExpandedIcon();
				if (expandedIcon != null)
					drawCentered(tree, g, expandedIcon, middleXOfKnob,
					             middleYOfKnob);
			} else {
				Icon collapsedIcon = getCollapsedIcon();
				if (collapsedIcon != null)
					drawCentered(tree, g, collapsedIcon, middleXOfKnob,
					             middleYOfKnob);
			}
		}
	}

	/*
	 * Add this if you want to use the custom TreeModelHandler
	 * to expand all new nodes.
	protected TreeModelListener createTreeModelListener() {
	  return new ExpandNewNodeTreeModelHandler();
	}
	*/

	public class ExpandNewNodeTreeModelHandler extends TreeModelHandler
	{

		public void treeNodesInserted(TreeModelEvent e)
		{
			super.treeNodesInserted(e); // do normal stuff first
			TreePath parentPath = e.getTreePath();
			Object parentObj = parentPath.getLastPathComponent();
			if (parentObj instanceof TreeNode) {
				TreeNode parentNode = (TreeNode) parentObj;
				int[] indices = e.getChildIndices();
				for (int i = indices.length - 1; i >= 0; --i) {
					TreeNode childNode = parentNode.getChildAt(indices[i]);
					tree.expandPath(parentPath.pathByAddingChild(childNode));
				}
			}
		}

	}

	protected MouseListener createMouseListener()
	{
		return new DMouseHandler();
	}

	// modification of MouseHandler to clear selection when
	// mouse click is below tree
	public class DMouseHandler extends MouseAdapter
	{
		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		public void mousePressed(MouseEvent e)
		{
			if (tree != null && tree.isEnabled()) {
				tree.requestFocus();
				TreePath path = getClosestPathForLocation(tree, e.getX(),
				                                          e.getY());

				if (path != null) {
					Rectangle bounds = getPathBounds(tree, path);

					if (e.getY() > (bounds.y + bounds.height)) {
						// not in path bounds, unselect everything
						tree.clearSelection();
						return;
					}

					// Preferably checkForClickInExpandControl could take
					// the Event to do this it self!
					if (SwingUtilities.isLeftMouseButton(e))
						checkForClickInExpandControl(path, e.getX(), e.getY());

					int x = e.getX();

					// Perhaps they clicked the cell itself. If so,
					// select it.
					if (x > bounds.x) {
						if (x <= (bounds.x + bounds.width) &&
						        !startEditing(path, e)) {
							selectPathForEvent(path, e);
						}
					}
				}
			}
			// PENDING: Should select on mouse down, start a drag if
			// the mouse moves, and fire selection change notice on
			// mouse up. That is, the explorer highlights on mouse
			// down, but doesn't update the pane to the right (and
			// open the folder icon) until mouse up.
		}
	} // End of BasicTreeUI.MouseHandler

	// override to not paint lines between nodes
	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
	                                      Insets insets, TreePath path)
	{

	}

	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
	                                        Insets insets, Rectangle bounds,
	                                        TreePath path, int row,
	                                        boolean isExpanded,
	                                        boolean hasBeenExpanded, boolean isLeaf)
	{

	}

}
