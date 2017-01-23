package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;

import javax.swing.Icon;
import java.util.List;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 11:09:26 PM
 * To change this template use Options | File Templates.
 */
public class DeployFilterTreeObject extends DefaultTreeObject
{
	protected Filter _filter;

	public DeployFilterTreeObject(Filter filter)
	{
		super(filter, true);
		this._filter = filter;
	}

	public String getTreeValue()
	{
		return _filter.getName();
	}

	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.FILTER_OPEN : DomeIcons.FILTER);
	}

	// override with application specific implementation
	public List getChildren()
	{
		return _filter.getItems();
	}
}

