// FileSystemTreeNode.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.swing.tree.CachingTreeObjectFactory;

import javax.swing.tree.MutableTreeNode;

public class DeployProjectTreeNode extends AbstractTreeObjectFactoryTreeNode
{

	private static TreeObjectFactory treeObjFactory = createTreeObjectFactory();

	public DeployProjectTreeNode(Object projectData)
	{
		super(projectData);
	}

	// use factory for specific tree
	protected TreeObjectFactory getTreeObjectFactory()
	{
		return treeObjFactory;
	}

	// first item cannot be an interface, second item is the name of the tree object class,
	// third item optional, which specifies the name of the class which is in the constructor
	// of the tree object

	private static TreeObjectFactory createTreeObjectFactory()
	{
		CachingTreeObjectFactory factory = new CachingTreeObjectFactory("DeployInterfaceTreeNode");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.deploy.components.DeployProjectData", "mit.cadlab.dome3.gui.deploy.components.DeployProjectIModelTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData", "mit.cadlab.dome3.gui.deploy.components.DeployInterfaceTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter", "mit.cadlab.dome3.gui.deploy.components.DeployFilterTreeObject",
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