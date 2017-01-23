// BrowseInterfaceTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.project;

import mit.cadlab.dome3.gui.guiutils.tree.GenericDomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;

import javax.swing.Icon;

public class BrowseInterfaceTreeObject extends GenericDomeTreeObject
{
	protected BrowseInterface info;

	public BrowseInterfaceTreeObject(BrowseInterface ifaceInfo)
	{
		super(ifaceInfo);
		this.info = ifaceInfo;
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.INTERFACE);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.INTERFACE_OPEN);
	}

	public String getTreeValue()
	{
		return info.getName();
	}

	public void setTreeValue(String value)
	{
		super.setTreeValue(value);
	}

}
