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
public class DeployIntegrationModelTableObject extends AbstractTableObject
{
	protected DeployModelData _modelObject;
	protected static TextCellEditor _description = new TextCellEditor();

	public DeployIntegrationModelTableObject(DeployModelData modelObject)
	{
		super(modelObject);
		this._modelObject = modelObject;
	}

	public Object getValueAt(int column)
	{
		if (column == 0)
			return this._modelObject.getName();
		else if (column == 1) {
			return null;
		} else if (column == 2)
			return null;
		else if (column == 3)
			return _modelObject.getModelDescription();
		else
			return null;
	}

	public boolean isEditableAt(int column)
	{
		if (column == 3)
			return true;
		else
			return false;
	}

	public Class getClassAt(int column)
	{
		if (column == 3) {
			return String.class;
		} else
			return null;
	}

	public void setValueAt(Object value, int column)
	{
		if (column == 3) {
			_modelObject.setModelDescription((String) value);
		}
	}

	public TableCellRenderer getCellRenderer(int row, int column)
	{

		return new DefaultTableCellRenderer();
	}

	public TableCellEditor getEditorAt(int column)
	{

		if (column == 3) {
			return _description;
		}
		return super.getEditorAt(column);
	}
}


