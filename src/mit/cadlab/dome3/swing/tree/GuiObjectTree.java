// GuiObjectTree.java
package mit.cadlab.dome3.swing.tree;

import java.awt.event.MouseListener;
import javax.swing.tree.TreePath;

/* This version of the Object tree utilizes the GuiTreeObjects.
 * Custom guis are shown when user single-clicks on icon.
 * Double-click to edit.
 * Triple-click for tree expansion/collapsing.
 */

public class GuiObjectTree extends ObjectTree
{

	// store so that subclasses can remove it and use their own instead
	protected MouseListener mouseListener = new GuiObjectTreeMouseListener();

	public GuiObjectTree(ObjectTreeModel model)
	{
		super(model);
	}

	public GuiObjectTree(ObjectTreeNode root)
	{
		this(new ObjectTreeModel(root));
	}

	protected MouseListener getMouseListener()
	{
		return new GuiObjectTreeMouseListener();
	}

	public void popupContextGui(int x, int y)
	{
		// invoked when user single-clicks on tree node icon
		TreePath path = getClosestPathForLocation(x, y);
		// know node is ObjectTreeNode or else it would have crashed during rendering
		ObjectTreeNode node = (ObjectTreeNode) path.getLastPathComponent();
		TreeObject tObj = node.getTreeObject();
		if (tObj instanceof GuiTreeObject) { // not a requirement
			((GuiTreeObject) tObj).showGui();
		}
	}

	public class GuiObjectTreeMouseListener extends ObjectTreeMouseListener
	{
		protected void doubleClickAction()
		{
			if ((lastMouseEvent.getModifiers() == 16 ||
			        (!lastMouseEvent.isAltDown() &&
			        !lastMouseEvent.isControlDown() &&
			        !lastMouseEvent.isMetaDown())) && isIconClicked(lastMouseEvent))
				popupContextGui(lastMouseEvent.getX(), lastMouseEvent.getY());
		}
	}

}
