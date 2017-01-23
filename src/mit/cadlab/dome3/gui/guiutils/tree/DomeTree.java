// DomeTree.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.plaf.DTreeUI;
import mit.cadlab.dome3.swing.tree.GuiObjectTree;
import mit.cadlab.dome3.swing.tree.ObjectTreeModel;
import mit.cadlab.dome3.swing.tree.ObjectTreeNode;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.ImageIcon;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreeCellEditor;

public class DomeTree extends GuiObjectTree
{

	public DomeTree(ObjectTreeModel model, boolean isEditable)
	{
		super(model);
		customizeTree(this);
		setEditable(isEditable);
	}

	public DomeTree(ObjectTreeNode root, boolean isEditable)
	{
		this(new ObjectTreeModel(root), isEditable);
	}

	public static void customizeTree(GuiObjectTree tree)
	{
		if (tree instanceof DomeTree)
			tree.setUI(((DomeTree) tree).getCustomTreeUI());
		else
			tree.setUI(new DTreeUI());
		tree.setRowHeight(22);
		((javax.swing.plaf.basic.BasicTreeUI) (tree.getUI())).setCollapsedIcon(DomeIcons.getIcon(DomeIcons.TREE_COLLAPSED));
		((javax.swing.plaf.basic.BasicTreeUI) (tree.getUI())).setExpandedIcon(DomeIcons.getIcon(DomeIcons.TREE_EXPANDED));
	}

	protected boolean areNewNodesExpanded()
	{
		return false;
	}

	protected TreeUI getCustomTreeUI()
	{
		if (areNewNodesExpanded()) {
			return new DTreeUI()
			{
				protected TreeModelListener createTreeModelListener()
				{
					return new ExpandNewNodeTreeModelHandler(); // new items are expanded
				}
			};
		} else {
			return new DTreeUI();
		}
	}

	protected TreeCellEditor getTreeCellEditor()
	{
		// right-click for text edit popup menu
		return new TextCellEditor();
	}

}
