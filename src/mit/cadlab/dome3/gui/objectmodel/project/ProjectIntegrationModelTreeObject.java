// ProjectResourceTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.project;

import mit.cadlab.dome3.gui.guiutils.tree.GenericDomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;

import javax.swing.Icon;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class ProjectIntegrationModelTreeObject extends GenericDomeTreeObject
{
	protected ProjectIntegrationModelInfo info;

	public ProjectIntegrationModelTreeObject(ProjectIntegrationModelInfo m)
	{
		super(m);
		this.info = m;
		m.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if(e.getPropertyName().equals(ProjectIntegrationModelInfo.NAME)) {
					ProjectIntegrationModelTreeObject.this.fireNodeValueChanged();
				}
			}
		});
	}

	protected Icon getClosedIcon()
	{
		return DomeIcons.getIcon(DomeIcons.MODEL);
	}

	protected Icon getOpenIcon()
	{
		return DomeIcons.getIcon(DomeIcons.MODEL_OPEN);
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
