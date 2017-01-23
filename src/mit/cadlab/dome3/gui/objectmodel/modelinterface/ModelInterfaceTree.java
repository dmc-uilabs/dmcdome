// ModelInterfaceTree.java
package mit.cadlab.dome3.gui.objectmodel.modelinterface;

import mit.cadlab.dome3.gui.guiutils.tree.DomeObjectTreeNode;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTree;
import mit.cadlab.dome3.gui.guiutils.tree.build.BuildObjectTreeNode;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;

import javax.swing.tree.TreePath;

public class ModelInterfaceTree extends DomeTree
{

	protected TreePath inputFilterPath, outputFilterPath; //indeterFilterPath;
	protected TreePath indepFilterPath, intermediateFilterPath, resultFilterPath;
	protected DomeObjectTreeNode ifaceNode;

	public ModelInterfaceTree(DomeObjectTreeNode ifaceNode, String view, boolean isEditable)
	{
		super(ifaceNode, isEditable);
		this.ifaceNode = ifaceNode;
		setPaths(view);
		expandAllVisibleRows();
	}

	public TreePath getInputFilterPath()
	{
		return inputFilterPath;
	}

	public TreePath getOutputFilterPath()
	{
		return outputFilterPath;
	}

//	public TreePath getIndeterminateFilterPath()
//	{
//		return indeterFilterPath;
//	}

	public TreePath getIndependentFilterPath()
	{
		return indepFilterPath;
	}

	public TreePath getIntermediateFilterPath()
	{
		return intermediateFilterPath;
	}

	public TreePath getResultFilterPath()
	{
		return resultFilterPath;
	}

	public void setPaths(String view)
	{
		if (view.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
			inputFilterPath = new TreePath(((DomeObjectTreeNode) ifaceNode.getChildAt(0)).getPath());
			outputFilterPath = new TreePath(((DomeObjectTreeNode) ifaceNode.getChildAt(1)).getPath());
//don't show unoccupied filters in interface causality view for now
//			indeterFilterPath = new TreePath(((DomeObjectTreeNode) ifaceNode.getChildAt(2)).getPath());
			indepFilterPath = null;
			intermediateFilterPath = null;
			resultFilterPath = null;
		} else {
			indepFilterPath = new TreePath(((DomeObjectTreeNode) ifaceNode.getChildAt(0)).getPath());
			intermediateFilterPath = new TreePath(((DomeObjectTreeNode) ifaceNode.getChildAt(1)).getPath());
			resultFilterPath = new TreePath(((DomeObjectTreeNode) ifaceNode.getChildAt(2)).getPath());
			inputFilterPath = null;
			outputFilterPath = null;
//don't show unoccupied filters in interface causality view for now
//			indeterFilterPath = null;
		}
	}


}
