// RunProceduralRelationTree.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 10, 2003
 * Time: 7:15:18 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run;

import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunObjectTreeNode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.EqualRelationTree;

import javax.swing.tree.TreePath;

public class RunEqualRelationTree extends EqualRelationTree
{

	public RunEqualRelationTree(EqualRelation relation, String view)
	{
		super(new RunObjectTreeNode(relation, view), true);
	}

	public boolean isPathEditable(TreePath path)
	{
		// can not edit system filter names
		if (!isEditable()) return false;
		RunObjectTreeNode node = (RunObjectTreeNode) path.getLastPathComponent();
		return !(((DomeTreeObject) node.getTreeObject()).getDomeObject() instanceof Filter);
	}

}
