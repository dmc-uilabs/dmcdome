package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 1:53:39 PM
 * To change this template use Options | File Templates.
 */
public class DeployProjectTableObject extends AbstractTableObject
{
	protected DeployProjectData projectObject;
	protected static TextCellEditor _description = new TextCellEditor();

	public DeployProjectTableObject(DeployProjectData projectObject)
	{
		super(projectObject);
		this.projectObject = projectObject;
	}

	public Object getValueAt(int column)
	{
		if (column == 0)
			return this.projectObject.getName();
		else if (column == 1) {
			return null;
		} else if (column == 2)
			return null;
		else if (column == 3)
			return projectObject.getDescription();
		else
			return null;
	}

	public boolean isEditableAt(int column)
	{
		return false;
	}


}


