// RunContextTree.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.run;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.gui.guiutils.tree.run.RunObjectTreeNode;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.ContextTree;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContext;

import javax.swing.tree.TreePath;

public class RunContextTree extends ContextTree
{

	public RunContextTree(DefaultContext context)
	{
		super(new RunObjectTreeNode(context), false);
	}

}
