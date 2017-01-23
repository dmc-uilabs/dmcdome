// DefaultDomeTreeObject.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.objectmodel.DomeObject;

import javax.swing.Icon;
import javax.swing.UIManager;

public class DefaultDomeTreeObject extends DomeTreeObject
{

	protected static Icon closedIcon = UIManager.getIcon("Tree.closedIcon");
	protected static Icon openIcon = UIManager.getIcon("Tree.openIcon");

	public DefaultDomeTreeObject(DomeObject obj)
	{
		super(obj);
	}

	protected Icon getClosedIcon()
	{
		return closedIcon;
	}

	protected Icon getOpenIcon()
	{
		return openIcon;
	}

}
