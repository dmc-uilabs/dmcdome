package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.Icon;
import java.util.List;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 7:23:08 PM
 * To change this template use Options | File Templates.
 */

public class DeployInterfaceTreeObject extends DefaultTreeObject
{
	protected DeployInterfaceData _model;

	public DeployInterfaceTreeObject(DeployInterfaceData interfaceModel)
	{
		super(interfaceModel, true);
		this._model = interfaceModel;
	}

	public String getTreeValue()
	{
		return _model.getName();
	}

	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.INTERFACE_OPEN : DomeIcons.INTERFACE);
	}

	// override with application specific implementation
	public List getChildren()
	{
		return _model.getFilters();
	}
}
