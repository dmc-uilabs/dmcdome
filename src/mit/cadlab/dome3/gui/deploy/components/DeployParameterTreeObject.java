package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;

import javax.swing.Icon;
import java.util.List;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 14, 2003
 * Time: 10:51:50 AM
 * To change this template use Options | File Templates.
 */
public class DeployParameterTreeObject extends DefaultTreeObject
{
	protected ParameterRuntime _parameter;

	public DeployParameterTreeObject(ParameterRuntime parameter)
	{
		super(parameter, false);
		this._parameter = parameter;
	}

	public String getTreeValue()
	{
		return _parameter.getName();
	}

	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.INTERFACE_PARAMETER : DomeIcons.INTERFACE_PARAMETER);
	}

	// override with application specific implementation
}
