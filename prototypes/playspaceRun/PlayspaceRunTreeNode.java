// FileSystemTreeNode.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package playspaceRun;

import mit.cadlab.dome.swing.tree.AbstractTreeObjectFactoryTreeNode;
import mit.cadlab.dome.swing.tree.TreeObjectFactory;
import mit.cadlab.dome.swing.tree.CachingTreeObjectFactory;

import javax.swing.tree.MutableTreeNode;

public class PlayspaceRunTreeNode extends AbstractTreeObjectFactoryTreeNode
{

	private static TreeObjectFactory treeObjFactory = createTreeObjectFactory();

	public PlayspaceRunTreeNode(Object fileSystemObject)
	{
		super(fileSystemObject);
	}

	// use factory for specific tree
	protected TreeObjectFactory getTreeObjectFactory()
	{
		return treeObjFactory;
	}

	private static TreeObjectFactory createTreeObjectFactory() {
		CachingTreeObjectFactory factory = new CachingTreeObjectFactory("PlayspaceRunTreeObjectFactory");
		factory.registerTreeObjectInfo("mit.cadlab.dome.network.client.ClientPlayspaceRecord","mit.cadlab.dome.gui.playspace.treeobject.PlayspaceTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome.network.client.ClientModelRecord","mit.cadlab.dome.gui.playspace.treeobject.PlayspaceModelTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome.network.client.ClientInterfaceRecord", "mit.cadlab.dome.gui.playspace.treeobject.PlayspaceInterfaceTreeObject");

		factory.registerTreeObjectInfo("mit.cadlab.dome.objectmodel.modelcomponent.filter.AbstractEventFilter", "mit.cadlab.dome.gui.deploy.components.DeployFilterTreeObject",
		                               "mit.cadlab.dome.objectmodel.modelcomponent.filter.Filter");
		factory.registerTreeObjectInfo("mit.cadlab.dome.objectmodel.modelcomponent.filter.FunctionFilter", "mit.cadlab.dome.gui.deploy.components.DeployFilterTreeObject",
		                               "mit.cadlab.dome.objectmodel.modelcomponent.filter.Filter");
		//for DefaultContextBuilder
		factory.registerTreeObjectInfo("mit.cadlab.dome.gui.objectmodel.modelobject.context.DefaultContextBuilder",
		                               "mit.cadlab.dome.gui.objectmodel.modelobject.context.ContextBuilderTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome.objectmodel.modelobject.parameter.ParameterRuntime",
		                               "mit.cadlab.dome.gui.deploy.components.DeployParameterTreeObject");
		//factory.registerTreeObjectInfo("mit.cadlab.dome.gui.fileSystem.DomeFile","mit.cadlab.dome.gui.fileSystem.DomeFileTreeObject");

		return factory;
	}

    // implemented to create instance of self
    // override to create different types of tree nodes, if desired
    protected MutableTreeNode makeTreeNode(Object obj) {
        return super.makeTreeNode(obj);
    }
}
