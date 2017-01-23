// RelationTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;

import javax.swing.Icon;

public class RelationTreeObject extends DomeTreeObject
{

	public RelationTreeObject(Relation r)
	{
		super(r);
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.RELATION);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.RELATION_OPEN);
	}

}
