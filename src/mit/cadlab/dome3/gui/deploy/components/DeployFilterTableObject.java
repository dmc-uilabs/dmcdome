package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.swing.table.AbstractTableObject;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 11:29:30 PM
 * To change this template use Options | File Templates.
 */
public class DeployFilterTableObject extends AbstractTableObject
{
	protected Filter _filter;

	public DeployFilterTableObject(Filter filterObject)
	{
		super(filterObject);
		this._filter = filterObject;
	}

	public Object getValueAt(int column)
	{
		if (column == 0)
			return null;
		else if (column == 1) {
			return null;
		} else if (column == 2)
			return null;
		else
			return null;
	}

}
