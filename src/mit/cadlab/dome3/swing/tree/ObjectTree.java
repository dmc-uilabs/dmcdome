// ObjectTree.java
package mit.cadlab.dome3.swing.tree;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

public class ObjectTree extends JTree
{
	// A tree that uses ObjectTreeModel and assumes all nodes are ObjectTreeNodes.
	// ObjectTreeModel and ObjectTreeNodes support "drilling down" hierarchy.

	public static final String ROOT_PROPERTY = "root";
	protected Vector historyListeners;
	protected Vector nodeHistory = new Vector();
	protected ObjectTreeNode originalRoot;

	public ObjectTree(ObjectTreeModel model)
	{
		super(model);
		setRootVisible(false);
		setShowsRootHandles(true);
		ObjectTreeCellRenderer tcr = new ObjectTreeCellRenderer();
		setCellRenderer(tcr); // uses custom object icons
		TreeCellEditor editor = getTreeCellEditor();
		ObjectTreeCellEditor tce;
		if (editor == null)
			tce = new ObjectTreeCellEditor(this, tcr);
		else
			tce = new ObjectTreeCellEditor(this, tcr, editor);
		setCellEditor(tce); // double-click on text to start editing
		MouseListener mouseListener = getMouseListener();
		if (mouseListener != null)
			addMouseListener(mouseListener);
		setToggleClickCount(3); // three clicks to toggle tree expand/collapse
	}

	public ObjectTree(ObjectTreeNode root)
	{
		this(new ObjectTreeModel(root));
	}

	// override by subclasses
	protected MouseListener getMouseListener()
	{
		return null;
	}

	protected TreeCellEditor getTreeCellEditor()
	{
		return null;
	}

	public String convertValueToText(Object value,
	                                 boolean selected,
	                                 boolean expanded,
	                                 boolean leaf,
	                                 int row,
	                                 boolean hasFocus)
	{
		if (value == null)
			return "";
		if (value instanceof ObjectTreeNode) {
			TreeObject tObj = ((ObjectTreeNode) value).getTreeObject();
			if (tObj != null)
				return tObj.getTreeValue();
			else
				return "null";
		} else {
			throw new RuntimeException("[convertValueToText] Illegal node - not ObjectTreeNode: " + value);
		}
	}

	// support for model root tracking events ("history")
	public void addHistoryListener(TreeHistoryListener l)
	{
		if (l == null) return;
		if (historyListeners == null)
			historyListeners = new Vector();
		if (!historyListeners.contains(l))
			historyListeners.add(l);
	}

	public void removeHistoryListener(TreeHistoryListener l)
	{
		historyListeners.remove(l);
		if (historyListeners.isEmpty())
			historyListeners = null;
	}

	public void fireHistoryEventAdded()
	{
		notifyHistoryListeners(new TreeHistoryEvent(this, TreeHistoryEvent.EVENT_ADDED));
	}

	public void fireHistoryEventRemoved()
	{
		notifyHistoryListeners(new TreeHistoryEvent(this, TreeHistoryEvent.EVENT_REMOVED));
	}

	public void fireHistoryEventQueueNonempty()
	{
		notifyHistoryListeners(new TreeHistoryEvent(this, TreeHistoryEvent.EVENT_QUEUE_NONEMPTY));
	}

	public void fireHistoryEventQueueEmpty()
	{
		notifyHistoryListeners(new TreeHistoryEvent(this, TreeHistoryEvent.EVENT_QUEUE_EMPTY));
	}

	protected void notifyHistoryListeners(TreeHistoryEvent event)
	{
		if (historyListeners == null) return;
		synchronized (historyListeners) {
			Enumeration e = historyListeners.elements();
			while (e.hasMoreElements()) {
				((TreeHistoryListener) e.nextElement()).historyChanged(event);
			}
		}
	}

	protected void setRoot(ObjectTreeNode node)
	{
		Object oldRoot = getModel().getRoot();
		((DefaultTreeModel) getModel()).setRoot(node);
		int nodeIndex = nodeHistory.indexOf(node);
		if (nodeIndex == -1) { // not in history, add item
			nodeHistory.add(node);
			fireHistoryEventAdded();
			if (nodeHistory.size() == 2) // root+1
				fireHistoryEventQueueNonempty();
		} else { // node in event queue
			// remove all the nodes after it
			if (nodeIndex == (nodeHistory.size() - 1)) return; // no change
			for (int i = (nodeHistory.size() - 1); i > nodeIndex; --i)
				nodeHistory.remove(i);
			fireHistoryEventRemoved();
			if (nodeHistory.size() == 1)
				fireHistoryEventQueueEmpty();
		}
		firePropertyChange(ROOT_PROPERTY, oldRoot, node);
	}

	protected void expandAllVisibleRows()
	{
		int nRows = getRowCount();
		for (int i = nRows - 1; i >= 0; --i)
			expandRow(i);
	}

	public void selectAllVisibleRows()
	{
		setSelectionInterval(0, getRowCount() - 1);
	}

	// support for custom mouse listener events
	protected boolean isIconClicked(MouseEvent me)
	{
		TreePath path = getClosestPathForLocation(me.getX(), me.getY());
		if (path != null) {
			Rectangle bounds = getPathBounds(path);
			if (bounds == null) return false;
			if (me.getY() > (bounds.y + bounds.height)) // not in path bounds
				return false;
			TreeObject tObj = ((ObjectTreeNode) path.getLastPathComponent()).getTreeObject();
			Icon editingIcon;
			if (!tObj.allowsChildren()) {
				editingIcon = tObj.getIcon(TreeObject.LEAF_ICON);
			} else if (isExpanded(path)) {
				editingIcon = tObj.getIcon(TreeObject.OPEN_ICON);
			} else {
				editingIcon = tObj.getIcon(TreeObject.CLOSED_ICON);
			}
			return (me.getX() >= bounds.x) &&
			        (me.getX() <= (bounds.x + editingIcon.getIconWidth()));
		}
		return false;
	}

	protected boolean isTextClicked(MouseEvent me)
	{
		TreePath path = getClosestPathForLocation(me.getX(), me.getY());
		if (path != null) {
			Rectangle bounds = getPathBounds(path);
			if (bounds == null) return false;
			if (me.getY() > (bounds.y + bounds.height)) // not in path bounds
				return false;
			TreeObject tObj = ((ObjectTreeNode) path.getLastPathComponent()).getTreeObject();
			Icon editingIcon;
			if (!tObj.allowsChildren()) {
				editingIcon = tObj.getIcon(TreeObject.LEAF_ICON);
			} else if (isExpanded(path)) {
				editingIcon = tObj.getIcon(TreeObject.OPEN_ICON);
			} else {
				editingIcon = tObj.getIcon(TreeObject.CLOSED_ICON);
			}
			int offset = editingIcon.getIconWidth() +
			        ((JLabel) getCellRenderer()).getIconTextGap();
			return ((me.getX() >= (bounds.x + offset)) // after icon
			        && (me.getX() <= (bounds.x + bounds.width))); // within bounds
		}
		return false;
	}

	protected class ObjectTreeMouseListener extends MouseAdapter implements ActionListener
	{
		protected transient Timer timer; // Used to detect double/triple-clicks
		protected transient MouseEvent lastMouseEvent;

		public ObjectTreeMouseListener()
		{
			timer = new Timer(500, this); // half-second delay processing mouse events
			timer.setRepeats(false);
		}

		public synchronized void mouseClicked(MouseEvent event)
		{
			if (!timer.isRunning() || lastMouseEvent == null) {
				lastMouseEvent = event;
				if (!timer.isRunning()) timer.start();
			} else if (event.getClickCount() > lastMouseEvent.getClickCount()) {
				lastMouseEvent = event; // continue waiting
			} else { // new series of mouse clicks, process old one first
				timer.stop();
				switch (lastMouseEvent.getClickCount()) {
					case 1:
						singleClickAction();
						break;
					case 2:
						doubleClickAction();
						break;
					case 3:
						tripleClickAction();
				}
				lastMouseEvent = event;
				timer.start();
			}
		}

		public synchronized void actionPerformed(ActionEvent event)
		{
			// timer stopped
			if (lastMouseEvent != null) {
				switch (lastMouseEvent.getClickCount()) {
					case 1:
						singleClickAction();
						break;
					case 2:
						doubleClickAction();
						break;
					case 3:
						tripleClickAction();
				}
				lastMouseEvent = null;
			}
		}

		protected void singleClickAction()
		{
			//System.out.println("single-click");
		}

		protected void doubleClickAction()
		{
			//System.out.println("double-click");
		}

		protected void tripleClickAction()
		{
			//System.out.println("triple-click");
		}

	}

}
