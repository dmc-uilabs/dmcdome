// AbstractTreeObjectFactoryTreeNode.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.tree;

import mit.cadlab.dome3.util.ClassUtils;

/**
 * This tree node should be subclassed for trees that use a factory
 * to get the tree object for a particular object.
 */
public abstract class AbstractTreeObjectFactoryTreeNode extends DefaultObjectTreeNode
{

	protected Object treeNodeObject;

	public AbstractTreeObjectFactoryTreeNode(Object obj)
	{
		if (obj == null)
			throw new IllegalArgumentException(ClassUtils.getClassName(this) + " constructor - null object");
		treeNodeObject = obj;
		TreeObject tObj = getTreeObjectFactory().getTreeObject(obj);
		if (tObj == null)
			throw new NullPointerException("TreeObject creation failed for " + obj);
		setUserObject(tObj);
		listener = new DefaultTreeObjectListener();
		getTreeObject().addTreeObjectListener(listener);

		loadChildren();
	}

	/**
	 * @return the object this tree node wraps
	 */
	public Object getTreeNodeObject()
	{
		return treeNodeObject;
	}

	// use factory for specific tree
	protected abstract TreeObjectFactory getTreeObjectFactory();

}
