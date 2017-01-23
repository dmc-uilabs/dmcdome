// ContextTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;

import javax.swing.Icon;

public class ContextTreeObject extends DomeTreeObject
{

	public ContextTreeObject(Context c)
	{
		super(c);
		c.addModelObjectReferencesListener(new TreeObjectDListListener());
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.CONTEXT);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.CONTEXT_OPEN);
	}

}
