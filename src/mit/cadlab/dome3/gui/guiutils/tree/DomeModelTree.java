// DomeModelTree.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.swing.tree.DefaultObjectTreeNode;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.swing.tree.TreeObject;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

public class DomeModelTree extends DomeTree
{

	public DomeModelTree(ObjectTreeModel model, boolean isEditable)
	{
		super(model, isEditable);
		addTreeExpansionListener(new DomeTreeExpansionListener());
	}

	public DomeModelTree(ObjectTreeNode root, boolean isEditable)
	{
		this(new ObjectTreeModel(root), isEditable);
	}

	// note: filter nodes should register for expansion events on parent nodes
	// and act accordingly
	protected class DomeTreeExpansionListener implements TreeExpansionListener
	{
		public void treeExpanded(TreeExpansionEvent event)
		{
			// if a Procedural Relation is expanded, expand its filters, too
			TreePath expandedPath = event.getPath();
			DefaultObjectTreeNode node = (DefaultObjectTreeNode) expandedPath.getLastPathComponent();
			TreeObject tObj = node.getTreeObject();
			if (tObj instanceof DomeTreeObject) {
				DomeObject dObj = ((DomeTreeObject) tObj).getDomeObject();
				if (dObj instanceof ProceduralRelation) {
					if (node.getChildCount() > 0) {
						expandPath(expandedPath.pathByAddingChild(node.getChildAt(1))); // output filter
						expandPath(expandedPath.pathByAddingChild(node.getChildAt(0))); // input filter
					}
				}
			}
		}

		public void treeCollapsed(TreeExpansionEvent event)
		{
			// do nothing
		}
	}

}
