package toolinterfaceworkspace.gui.objectmodel.toolinterface.build;

import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.TreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.ShiftSupport;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 29, 2003
 * Time: 9:09:29 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceTreeBuilderPanel extends TreeBuilderPanel
{
    protected ToolInterface ifaceBuilder;
	protected BuildToolInterfaceTree tree;
	protected BuildTreeTable treeTable;

    public ToolInterfaceTreeBuilderPanel(ToolInterface ifaceBuilder,
	                                      String view)
	{
		this.ifaceBuilder = ifaceBuilder;
		tree = new BuildToolInterfaceTree(ifaceBuilder, view);

		treeTable = new BuildTreeTable(tree);

		layoutComponent(treeTable);
		upButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		downButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		upDownButtonPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
	}

    protected void moveUpAction()
    {
    }

    protected void moveDownAction()
	{

	}
}
