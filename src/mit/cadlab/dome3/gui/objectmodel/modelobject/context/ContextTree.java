// ContextTree.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context;

import mit.cadlab.dome3.gui.guiutils.tree.DomeModelTree;
import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectTreeNode;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;

import javax.swing.tree.TreePath;
import java.awt.event.MouseListener;

public abstract class ContextTree extends DomeModelTree
{

	public ContextTree(DomeObjectTreeNode contextNode, boolean isEditable)
	{
		super(contextNode, isEditable);
		nodeHistory.add(getModel().getRoot());
		if(!(getRootContext().getScope() instanceof Model ||
		     getRootContext().getScope() instanceof ModelInterface)) {
			expandAllVisibleRows();
		}
	}

	protected boolean areNewNodesExpanded()
	{
		return true;
	}

	// add list model support here
	protected MouseListener getMouseListener()
	{
		return new ContextTreeMouseListener();
	}

	public Context getRootContext()
	{
		DomeObjectTreeNode rootNode = (DomeObjectTreeNode) getModel().getRoot();
		return (Context) rootNode.getDomeObject();
	}

	protected void setRootContext(int x, int y)
	{
		// invoked when user double-clicks on tree node icon
		TreePath path = getClosestPathForLocation(x, y);
		ObjectTreeNode node = (ObjectTreeNode) path.getLastPathComponent();
		TreeObject tObj = node.getTreeObject();
		if (tObj instanceof ContextTreeObject) {
			setRoot(node);
			expandAllVisibleRows();
		}
	}

	public void setRootContextBack()
	{
		// go back one in context history
		if (nodeHistory.size() == 1) return; // already at root
		// current last element is current root; replace with next-to-last element
		ObjectTreeNode node = (ObjectTreeNode) nodeHistory.get(nodeHistory.size() - 2);
		setRoot(node);
		expandAllVisibleRows();
	}

	public class ContextTreeMouseListener extends GuiObjectTreeMouseListener
	{
		protected void doubleClickAction()
		{
			if (isIconClicked(lastMouseEvent)) {
				if (lastMouseEvent.isShiftDown()) {
					setRootContext(lastMouseEvent.getX(), lastMouseEvent.getY());
				} else if (lastMouseEvent.getModifiers() == 16 ||
				        (!lastMouseEvent.isAltDown() &&
				        !lastMouseEvent.isControlDown() &&
				        !lastMouseEvent.isMetaDown())) {
					popupContextGui(lastMouseEvent.getX(), lastMouseEvent.getY());
				} else {
					System.out.println(lastMouseEvent);
				}
			}
		}

		protected void tripleClickAction()
		{
			if (isIconClicked(lastMouseEvent))
				setRootContext(lastMouseEvent.getX(), lastMouseEvent.getY());
		}
	}

}
