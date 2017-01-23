package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.swing.table.AbstractTableObject;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 1:53:39 PM
 * To change this template use Options | File Templates.
 */
public class DeployModelTableObject extends AbstractTableObject
{
	protected DeployModelData _modelObject;

	public DeployModelTableObject(DeployModelData modelObject)
	{
		super(modelObject);
		this._modelObject = modelObject;
	}

	public Object getValueAt(int column)
	{
		if (column == 0)
			return this._modelObject.getName();
		else if (column == 1) {
			return null;
		} else if (column == 2)
			return null;
		else
			return null;
	}
}


