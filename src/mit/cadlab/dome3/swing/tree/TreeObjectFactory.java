// TreeObjectFactory.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.tree;

/**
 * Interface for a factory that generates TreeObjects for specified objects.
 */
public interface TreeObjectFactory
{

	/**
	 * Creates/returns the TreeObject for the given object.
	 * Some implementations may create a new TreeObject each time or cache instances of
	 * the tree object.
	 * @param obj the object to return a TreeObject for
	 * @return a TreeObject for the object specified.
	 */
	public TreeObject getTreeObject(Object obj);

}
