package mit.cadlab.dome3.gui.deploy.components.tool;

import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.swing.tree.CachingTreeObjectFactory;

import javax.swing.tree.MutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 17, 2003
 * Time: 9:15:11 PM
 * To change this template use Options | File Templates.
 */
public class DeployToolInterfaceTreeNode extends AbstractTreeObjectFactoryTreeNode
{
    private static TreeObjectFactory treeObjFactory = createTreeObjectFactory();

	public DeployToolInterfaceTreeNode(Object toolData)
	{
		super(toolData);
	}

	// use factory for specific tree
	protected TreeObjectFactory getTreeObjectFactory()
	{
		return treeObjFactory;
	}

	// first item can not be an interface, second item is the name of the tree object class,
	// third item optional, which specifies the name of the class which is in the constructor
	// of the tree object

	private static TreeObjectFactory createTreeObjectFactory()
	{
		CachingTreeObjectFactory factory = new CachingTreeObjectFactory("DeployToolInterfaceTreeNode");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData",
                                       "mit.cadlab.dome3.gui.deploy.components.tool.DeployToolTreeObject");

		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolInterfaceData",
                                       "mit.cadlab.dome3.gui.deploy.components.tool.DeployToolInterfaceTreeObject");

		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.toolinterface.optimization.OptimizationToolInterfaceBase$ToolInterfaceFilter",
                                       "mit.cadlab.dome3.gui.deploy.components.DeployFilterTreeObject",
		                               "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime",
		                               "mit.cadlab.dome3.gui.deploy.components.DeployParameterTreeObject");
		return factory;
	}

	// implemented to create instance of self
	// override to create different types of tree nodes, if desired
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		return super.makeTreeNode(obj);
	}
}
