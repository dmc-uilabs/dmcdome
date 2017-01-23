// ProjectResourceLocationTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.project;

import mit.cadlab.dome3.gui.guiutils.tree.GenericDomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;

import javax.swing.*;

public class ProjectResourceLocationTreeObject extends GenericDomeTreeObject
{
	protected ProjectResourceInfo.LocationInfo info;

	public ProjectResourceLocationTreeObject(ProjectResourceInfo.LocationInfo r)
	{
		super(r, false);
		this.info = r;
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.PROPERTY);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.PROPERTY);
	}

	public String getTreeValue()
	{
		return info.getName();
	}
}
