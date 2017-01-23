// FilterTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;

import javax.swing.Icon;

public class FilterTreeObject extends DomeTreeObject
{

	public FilterTreeObject(Filter f)
	{
		super(f);
		f.addFilterListener(new TreeObjectDListListener());
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.FILTER);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.FILTER_OPEN);
	}

}
