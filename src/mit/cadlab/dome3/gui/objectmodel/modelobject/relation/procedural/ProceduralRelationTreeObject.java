// ProceduralRelationTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;

import javax.swing.Icon;

public class ProceduralRelationTreeObject extends DomeTreeObject
{

	public ProceduralRelationTreeObject(ProceduralRelation r)
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
