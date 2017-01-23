// TableObjectFactory.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.table;

/**
 * Interface for a factory that generates TableObjects for specified objects.
 */
public interface TableObjectFactory
{
	/**
	 * Creates/returns the TableObject for the given object.
	 * Some implementations may create a new TableObject each time or cache instances of
	 * the tree object.
	 * @param obj the object to return a TableObject for
	 * @return a TableObject for the object specified.
	 */
	public TableObject getTableObject(Object obj);

}
