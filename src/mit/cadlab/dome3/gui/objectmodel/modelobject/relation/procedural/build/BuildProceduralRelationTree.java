// BuildProceduralRelationTree.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.ProceduralRelationTree;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;

import javax.swing.tree.TreePath;

public class BuildProceduralRelationTree extends ProceduralRelationTree
{

	public BuildProceduralRelationTree(ProceduralRelation relation, String view)
	{
		super(new BuildObjectTreeNode(relation, view), true);
	}

	public boolean isPathEditable(TreePath path)
	{
		// can not edit system filter names
		if (!isEditable()) return false;
		BuildObjectTreeNode node = (BuildObjectTreeNode) path.getLastPathComponent();
		return !(((DomeTreeObject) node.getTreeObject()).getDomeObject() instanceof Filter);
	}

}