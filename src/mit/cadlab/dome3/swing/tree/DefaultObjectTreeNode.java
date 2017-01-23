// DefaultObjectTreeNode.java
package mit.cadlab.dome3.swing.tree;

import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.BuildParameterTreeObject;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;

import javax.swing.tree.MutableTreeNode;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Constructor;

/**
 * Assumes that all nodes carry treeobject.
 */
public class DefaultObjectTreeNode extends AbstractObjectTreeNode
{
	// remember that a tree node can only be in one tree model
	// in only one place in a tree model at a time

	protected DefaultTreeObjectListener listener = null;

	// DefaultTreeObjectListener assumes all nodes are DefaultTreeObjects

	// This implementation of ObjectTreeNode has the TreeObject as
	// the data in the node.
	public DefaultObjectTreeNode(TreeData data)
	{
		if (data == null)
			throw new IllegalArgumentException("DefaultObjectTreeNode - null TreeObject");
		setUserObject(data.getTreeObject());
		listener = new DefaultTreeObjectListener();
		getTreeObject().addTreeObjectListener(listener);

		loadChildren();
	}

	protected DefaultObjectTreeNode()
	{
		super();
	}

	// implemented to create instance of self
	// override to create different types of tree nodes, if desired
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		Constructor[] ctrs = getClass().getConstructors();
		if (ctrs.length == 1)
			try {
				return (MutableTreeNode) ctrs[0].newInstance(new Object[]{obj});
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new IllegalArgumentException(ClassUtils.getClassName(this) + ".makeTreeNode: no constructors for " + ClassUtils.getClassName(obj));
			}
		else if (ctrs.length == 0)
			throw new UnsupportedOperationException(ClassUtils.getClassName(this) + " has no constructors - makeTreeNode must be overridden");
		else
			throw new UnsupportedOperationException(ClassUtils.getClassName(this) + " has multiple constructors - makeTreeNode must be overridden");
	}


	protected void destroyTreeNode(Object obj)
	{
		if (obj instanceof DefaultObjectTreeNode)
			((DefaultObjectTreeNode) obj).disconnectTreeObject();
	}

	protected void disconnectTreeObject()
	{
		if (listener != null) {
			getTreeObject().removeTreeObjectListener(listener);
			userObject = null;
		}
	}

	// ObjectTreeNode interface
	public TreeObject getTreeObject()
	{
		return (TreeObject) getUserObject();
	}

	// override DefaultMutableTreeNode methods
	public boolean getAllowsChildren()
	{
		TreeObject tObj = getTreeObject();
		if (tObj != null)
			return tObj.allowsChildren();
		else
			return false;
	}

	public void setAllowsChildren(boolean allows)
	{
		throw new UnsupportedOperationException("DefaultObjectTreeNode.setAllowsChildren not supported. Implement TreeObject.allowsChildren");
	}

	public void remove(int childIndex)
	{
		Object node = getChildAt(childIndex);
		destroyTreeNode(node);
		super.remove(childIndex);
	}

	public void remove(MutableTreeNode aChild)
	{
		destroyTreeNode(aChild);
		super.remove(aChild);
	}

	public void removeAllChildren()
	{
		Enumeration e = children();
		while (e.hasMoreElements()) {
			Object node = e.nextElement();
			destroyTreeNode(node);
		}
		super.removeAllChildren();
	}

	protected void loadChildren()
	{
		loadChildren(null);
	}

	protected void loadChildren(String view)
	{
		Iterator it;
		if (getTreeObject().allowsChildren()) {
			if (view == null)
				it = ((DefaultTreeObject) getTreeObject()).getChildren().iterator();
			else
				it = ((DefaultTreeObject) getTreeObject()).getChildren(view).iterator();
			while (it.hasNext()) {
				add(makeTreeNode(it.next()));
			}
		}
	}

	protected class DefaultTreeObjectListener implements TreeObjectListener
	{
		// converts TreeObject events to TreeNode/Model events
		// assumes DefaultTreeObjects are used
		public DefaultTreeObjectListener()
		{
		};

		public void nodeValueChanged(TreeObjectEvent event)
		{
			notifyNodeValueChanged();
		}

		public void nodeStructureChanged(TreeObjectEvent event)
		{
			notifyNodeStructureChanged();
		}

		public void childrenChanged(TreeObjectEvent event)
		{
			int[] indices = event.getIndices();
			// remove children
			for (int i = indices.length - 1; i >= 0; --i) {
				remove(indices[i]);
			}
			// add children
			List children = ((DefaultTreeObject) getTreeObject()).getChildren();
			for (int i = 0; i < indices.length; ++i) {
				int index = indices[i];
				insert(makeTreeNode(children.get(index)), index);
			}
			notifyNodeStructureChanged();
		}

		public void childrenAdded(TreeObjectEvent event)
		{
			int[] indices = event.getIndices();
			TreeObject tObj = getTreeObject();
			List children = ((DefaultTreeObject) tObj).getChildren();
			for (int i = 0; i < indices.length; ++i) {
				int index = indices[i];
				insert(makeTreeNode(children.get(index)), index);
			}
			notifyChildrenAdded(indices);
		}

		public void childrenRemoved(TreeObjectEvent event)
		{
            int[] indices = event.getIndices();
			Object[] removedNodes = new Object[indices.length];

			for (int i = indices.length - 1; i >= 0; --i) {
				int index = indices[i];
				removedNodes[i] = getChildAt(index);

                // sangmok memory debug start
                // if removedNodes [i] is an instance of BuildParameterTreeObject, invoke releaseDataObjectReferenceOfEditors() to release reference to data object.
                if (((ObjectTreeNode) removedNodes [i]).getTreeObject() instanceof BuildParameterTreeObject) {
                    try {
                        DefaultTreeObject tableObject = (DefaultTreeObject) ((ObjectTreeNode) removedNodes [i]).getTreeObject();
//                        BuildTreeTable.ParameterTableObject paramTableObj = (BuildTreeTable.ParameterTableObject) BuildTreeTable.getTableObjectFactory().getTableObject(tableObject.data);
//                        if (paramTableObj != null) {
//                            paramTableObj.releaseDataObjectReferenceOfEditors();
//                        }

                        // Remove cached tree object (=concrete parameter instance). It should be remove from cache, because it is removed & no more valid)
                        // BuildTreeTable.getTableObjectFactory().getCachingObjectFactory().removeCachedTreeObject(tableObject.data);
                        try {
                            BuildTreeTable.getTableObjectFactory().getCachingObjectFactory().removeCachedTreeObject(tableObject.data);
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        try {
                            //((ConcreteParameter) tableObject.data).delete(null);
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                    } catch (Exception ex) {
                        System.err.println("childrenRemoved : " + ex + "/n/t" + "Error occured while releasing data object instances from Editors");
                    }
                }
                // sangmok : memory debugging end

                remove(index);
			}

			notifyChildrenRemoved(indices, removedNodes);
		}

	}

}
