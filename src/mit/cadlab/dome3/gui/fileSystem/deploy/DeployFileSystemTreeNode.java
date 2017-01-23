// DeployFileSystemTreeNode.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.swing.tree.TreeObjectFactory;
import mit.cadlab.dome3.swing.tree.CachingTreeObjectFactory;
import mit.cadlab.dome3.gui.fileSystem.FileSystemTreeNode;

import javax.swing.tree.MutableTreeNode;

public class DeployFileSystemTreeNode extends FileSystemTreeNode
{
	private static TreeObjectFactory treeObjFactory = createTreeObjectFactory();

	public DeployFileSystemTreeNode(Object fileSystemObject)
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
		CachingTreeObjectFactory factory = new CachingTreeObjectFactory("DeployFileSystemTreeObjectFactory");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.fileSystem.FileSystemObject", "mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.fileSystem.Folder", "mit.cadlab.dome3.gui.fileSystem.FolderTreeObject");
		factory.registerTreeObjectInfo("mit.cadlab.dome3.gui.fileSystem.DomeFile", "mit.cadlab.dome3.gui.fileSystem.deploy.DeployDomeFileTreeObject");
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
