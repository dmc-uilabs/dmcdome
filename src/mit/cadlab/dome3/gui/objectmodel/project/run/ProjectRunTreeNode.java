// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.project.run;

import mit.cadlab.dome3.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.swing.tree.CachingTreeObjectFactory;

import javax.swing.tree.MutableTreeNode;

public class ProjectRunTreeNode extends AbstractTreeObjectFactoryTreeNode
{

	private static TreeObjectFactory treeObjFactory = createTreeObjectFactory();

	public ProjectRunTreeNode(Object fileSystemObject)
	{
		super(fileSystemObject);
	}

	// use factory for specific tree
	protected TreeObjectFactory getTreeObjectFactory()
	{
		return treeObjFactory;
	}

	private static TreeObjectFactory createTreeObjectFactory()
	{
		CachingTreeObjectFactory factory = new CachingTreeObjectFactory("PlayspaceRunTreeObjectFactory");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRecord",
		                               "mit.cadlab.dome3.gui.playspace.treeobject.PlayspaceTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord",
		                               "mit.cadlab.dome3.gui.playspace.treeobject.PlayspaceModelTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord",
		                               "mit.cadlab.dome3.gui.playspace.treeobject.PlayspaceProjectTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord",
		                               "mit.cadlab.dome3.gui.playspace.treeobject.PlayspaceInterfaceTreeObject");

		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractEventFilter",
		                               "mit.cadlab.dome3.gui.deploy.components.DeployFilterTreeObject",
		                               "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");

		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelcomponent.filter.FunctionFilter",
		                               "mit.cadlab.dome3.gui.deploy.components.DeployFilterTreeObject",
		                               "mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter");
		//for DefaultContextBuilder
		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder",
		                               "mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextBuilderTreeObject");

		factory.registerTreeObjectInfo("mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime",
		                               "mit.cadlab.dome3.gui.deploy.components.DeployParameterTreeObject");

		// for folders in projects
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.fileSystem.Folder", "mit.cadlab.dome3.gui.fileSystem.FolderTreeObject");
		return factory;
	}

	// implemented to create instance of self
	// override to create different types of tree nodes, if desired
	protected MutableTreeNode makeTreeNode(Object obj)
	{
		return super.makeTreeNode(obj);
	}
}
