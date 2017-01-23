package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;

import javax.swing.Icon;
import java.util.List;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 3:34:33 PM
 * To change this template use Options | File Templates.
 */
public class DeployProjectIModelTreeObject extends DefaultTreeObject
{
	protected DeployProjectData project;

	public DeployProjectIModelTreeObject(DeployProjectData project)
	{
		super(project, true);
		this.project = project;
	}

	public String getTreeValue()
	{
		return project.getName();
	}

	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.PROJECT_OPEN : DomeIcons.PROJECT);
	}

	// override with application specific implementation
	public List getChildren()
	{
		return project.getIntegrationModels();
	}
}
