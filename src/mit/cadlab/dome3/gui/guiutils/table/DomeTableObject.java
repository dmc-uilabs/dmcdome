// DomeTableObject.java
package mit.cadlab.dome3.gui.guiutils.table;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.swing.tree.TreeObjectEvent;
import mit.cadlab.dome3.swing.tree.TreeObjectListener;

public abstract class DomeTableObject extends AbstractTableObject
{
	protected static final String NOTHING = "";
	// to do: fix things so that once can listen to tree objects
	// not managed by model for model objects (filters, in particular)

	public DomeTableObject(DomeObject obj)
	{
		super(obj);
		addTableTreeObjectListener(obj);
	}

	public DomeTableObject(DomeObject obj, boolean[] isEditable)
	{
		super(obj, isEditable);
		addTableTreeObjectListener(obj);
	}

	public abstract void addTableTreeObjectListener(DomeObject obj);
	// getTreeObject(obj).addTreeObjectListener(new TableTreeObjectListener());

	public Object getValueAt(int column)
	{
		if (column == 0)
			return ((DomeObject) object).getName();
		else
			return NOTHING;
	}

	public void setValueAt(Object value, int column)
	{
		if (column == 0) // name
			((DomeObject) object).setName(value.toString());
	}

	public class TableTreeObjectListener implements TreeObjectListener
	{
		public void nodeValueChanged(TreeObjectEvent event)
		{
			fireTableObjectChanged(0);
		}

		public void nodeStructureChanged(TreeObjectEvent event)
		{
		}

		public void childrenChanged(TreeObjectEvent event)
		{
		}

		public void childrenAdded(TreeObjectEvent event)
		{
		}

		public void childrenRemoved(TreeObjectEvent event)
		{
		}
	}

}
