// AbstractFilterTreeSelectionModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing.tree;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

/**
 * TreeSelectionModel that accepts/rejects selections
 * default behavior is to keep the last valid selection
 */
public abstract class AbstractFilterTreeSelectionModel extends DefaultTreeSelectionModel
{
	protected boolean keepLastValidSelection = false;

	public AbstractFilterTreeSelectionModel()
	{
		this(true);
	}

	public AbstractFilterTreeSelectionModel(boolean keepLastValidSelection)
	{
		this.keepLastValidSelection = keepLastValidSelection;
	}

	public void setSelectionPaths(TreePath[] paths)
	{
		Vector validPaths = new Vector();
		for (int i = 0; i < paths.length; ++i) {
			if (isValidSelectionPath(paths[i]))
				validPaths.add(paths[i]);
		}
		if (validPaths.isEmpty() && keepLastValidSelection)
			return;
		super.setSelectionPaths((TreePath[]) validPaths.toArray(new TreePath[validPaths.size()]));
	}

	public void addSelectionPaths(TreePath[] paths)
	{
		Vector validPaths = new Vector();
		for (int i = 0; i < paths.length; ++i) {
			if (isValidSelectionPath(paths[i]))
				validPaths.add(paths[i]);
		}
		if (validPaths.isEmpty() && keepLastValidSelection)
			return;
		super.addSelectionPaths((TreePath[]) validPaths.toArray(new TreePath[validPaths.size()]));
	}

	protected abstract boolean isValidSelectionPath(TreePath path);

	public boolean isKeepLastValidSelection()
	{
		return keepLastValidSelection;
	}

	public void setKeepLastValidSelection(boolean keepLastValidSelection)
	{
		this.keepLastValidSelection = keepLastValidSelection;
	}
}
