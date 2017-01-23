// EqualRelationTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;

import javax.swing.Icon;

public class EqualRelationTreeObject extends DomeTreeObject
{

	public EqualRelationTreeObject(EqualRelation r)
	{
		super(r);
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.RELATIONEQUAL);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.RELATIONEQUAL_OPEN);
	}

}
