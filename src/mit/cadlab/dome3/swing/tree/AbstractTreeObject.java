// AbstractTreeObject.java
package mit.cadlab.dome3.swing.tree;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.UIManager;

public abstract class AbstractTreeObject implements TreeObject, TreeData
{
	// Implements basic TreeObjectListener support

	protected Vector listeners = new Vector();

	// TreeData interface
	public TreeObject getTreeObject()
	{
		return this;
	}

	// TreeObject change notification support
	public void addTreeObjectListener(TreeObjectListener l)
	{
		if (!listeners.contains(l))
			listeners.add(l);
	}

	public void removeTreeObjectListener(TreeObjectListener l)
	{
		listeners.remove(l);
	}

	public void fireNodeValueChanged()
	{
		TreeObjectEvent event = new TreeObjectEvent(this, TreeObjectEvent.VALUE_CHANGED);
		synchronized (listeners) {
			Enumeration e = listeners.elements();
			while (e.hasMoreElements()) {
				((TreeObjectListener) e.nextElement()).nodeValueChanged(event);
			}
		}
	}

	public void fireNodeStructureChanged()
	{
		TreeObjectEvent event = new TreeObjectEvent(this, TreeObjectEvent.STRUCTURE_CHANGED);
		synchronized (listeners) {
			Enumeration e = listeners.elements();
			while (e.hasMoreElements()) {
				((TreeObjectListener) e.nextElement()).nodeStructureChanged(event);
			}
		}
	}

	public void fireChildrenChanged(int[] indices)
	{
		TreeObjectEvent event = new TreeObjectEvent(this, TreeObjectEvent.CHILDREN_CHANGED, indices);
		synchronized (listeners) {
			Enumeration e = listeners.elements();
			while (e.hasMoreElements()) {
				((TreeObjectListener) e.nextElement()).childrenChanged(event);
			}
		}
	}

	public void fireChildrenAdded(int[] indices)
	{
		TreeObjectEvent event = new TreeObjectEvent(this, TreeObjectEvent.CHILDREN_ADDED, indices);
		synchronized (listeners) {
			Enumeration e = listeners.elements();
			while (e.hasMoreElements()) {
				((TreeObjectListener) e.nextElement()).childrenAdded(event);
			}
		}

	}

	public void fireChildrenRemoved(int[] indices)
	{
        TreeObjectEvent event = new TreeObjectEvent(this, TreeObjectEvent.CHILDREN_REMOVED, indices);
		synchronized (listeners) {
			Enumeration e = listeners.elements();
			while (e.hasMoreElements()) {
                ((TreeObjectListener) e.nextElement()).childrenRemoved(event);
			}
		}
	}


	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int type)
	{
		switch (type) {
			case LEAF_ICON:
				return UIManager.getIcon("Tree.leafIcon");
			case OPEN_ICON:
				return UIManager.getIcon("Tree.openIcon");
			case CLOSED_ICON:
				return UIManager.getIcon("Tree.closedIcon");
			default:
				return null; // editor does not display correctly
		}
	}

}
