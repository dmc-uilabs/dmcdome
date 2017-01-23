// BuildProceduralRelationTree.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 10, 2003
 * Time: 4:41:43 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.run;

import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunObjectTreeNode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.ProceduralRelationTree;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.IterationRelationTree;

import javax.swing.tree.TreePath;

public class RunIterationRelationTree extends IterationRelationTree
{

	public RunIterationRelationTree(IterationRelation relation, String view)
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
