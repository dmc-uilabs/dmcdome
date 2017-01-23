// VisualizationTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;

import javax.swing.Icon;

public class VisualizationTreeObject extends DomeTreeObject
{

	public VisualizationTreeObject(Visualization vis)
	{
		super(vis);
		vis.addAvailableListListener(new TreeObjectDListListener());
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.VISUALIZATION);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.VISUALIZATION_OPEN);
	}

}
