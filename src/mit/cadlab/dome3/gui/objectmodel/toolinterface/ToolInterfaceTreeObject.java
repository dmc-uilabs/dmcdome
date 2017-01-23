package mit.cadlab.dome3.gui.objectmodel.toolinterface;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 22, 2003
 * Time: 8:30:46 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceTreeObject extends DomeTreeObject
{

	public ToolInterfaceTreeObject(ToolInterface mi)
	{
		super(mi);
		if (mi instanceof ViewSupport) {
			((ViewSupport) mi).addViewListener(new DefaultTreeObject.TreeObjectDListListener());
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