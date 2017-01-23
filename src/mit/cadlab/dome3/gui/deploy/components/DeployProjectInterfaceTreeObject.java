/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 26, 2003
 * Time: 3:06:17 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.*;
import java.util.List;

public class DeployProjectInterfaceTreeObject extends DefaultTreeObject
{
	protected DeployProjectInterfaceData _model;

	public DeployProjectInterfaceTreeObject(DeployProjectInterfaceData interfaceModel)
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
