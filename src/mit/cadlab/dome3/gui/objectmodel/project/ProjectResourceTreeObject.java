// ProjectResourceTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.project;

import mit.cadlab.dome3.gui.guiutils.tree.GenericDomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;

import javax.swing.Icon;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class ProjectResourceTreeObject extends GenericDomeTreeObject
{
	protected ProjectResourceInfo info;

	public ProjectResourceTreeObject(ProjectResourceInfo r)
	{
		super(r);
		this.info = r;
		r.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				if (e.getPropertyName().equals(ProjectResourceInfo.NAME)) {
					ProjectResourceTreeObject.this.fireNodeValueChanged();
				}
			}
		});
	}

	protected Icon getClosedIcon()
	{
		if (info.getType().equals(ProjectResourceInfo.MODEL_RESOURCE) || info.getType().equals(ProjectResourceInfo.IMODEL_RESOURCE)) //treat imodel internal resource as model
			return DomeIcons.getIcon(DomeIcons.MODEL);
		else if (info.getType().equals(ProjectResourceInfo.PROJECT_RESOURCE))
			return DomeIcons.getIcon(DomeIcons.PROJECT);
		else
			return null;
	}

	protected Icon getOpenIcon()
	{
		if (info.getType().equals(ProjectResourceInfo.MODEL_RESOURCE) || info.getType().equals(ProjectResourceInfo.IMODEL_RESOURCE)) //treat imodel internal resource as model
			return DomeIcons.getIcon(DomeIcons.MODEL_OPEN);
		else if (info.getType().equals(ProjectResourceInfo.PROJECT_RESOURCE))
			return DomeIcons.getIcon(DomeIcons.PROJECT_OPEN);
		else
			return null;
	}

	public String getTreeValue()
	{
		return info.getName();
	}

	public void setTreeValue(String value)
	{
		info.setName(value);
	}

}
