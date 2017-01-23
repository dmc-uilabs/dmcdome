package mit.cadlab.dome3.gui.deploy.components.tool;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 17, 2003
 * Time: 9:49:21 PM
 * To change this template use Options | File Templates.
 */
public class DeployToolTreeObject extends DefaultTreeObject
{
	protected DeployAnalysisToolData _tool;

	public DeployToolTreeObject(DeployAnalysisToolData tool)
	{
		super(tool, true);
		_tool = tool;
	}

	public String getTreeValue()
	{
		return _tool.getName();
	}

	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int itemState)
	{
		return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.ANALYSIS_TOOL_OPEN : DomeIcons.ANALYSIS_TOOL);
	}

	// override with application specific implementation
	public List getChildren()
	{
		return _tool.getToolInterfaces();
	}
}
