// ModelInterfaceTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelinterface;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.ViewSupport;

import javax.swing.*;

public class ModelInterfaceTreeObject extends DomeTreeObject
{

	public ModelInterfaceTreeObject(ModelInterface mi)
	{
		super(mi);
		if (mi instanceof ViewSupport) {
			((ViewSupport) mi).addViewListener(new TreeObjectDListListener());
		}
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.INTERFACE);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.INTERFACE_OPEN);
	}

}
