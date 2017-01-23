/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 26, 2003
 * Time: 3:19:04 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.gui.guiutils.treetable.Editors;
import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class DeployProjectInterfaceTableObject extends AbstractTableObject
{
	protected DeployProjectInterfaceData _interfaceObject;
	protected static Editors.BooleanCheckBoxEditor _isDeployed = new Editors.BooleanCheckBoxEditor();
	protected static TextCellEditor _description = new TextCellEditor();

	public DeployProjectInterfaceTableObject(DeployProjectInterfaceData interfaceObject)
	{
		super(interfaceObject);
		this._interfaceObject = interfaceObject;
	}

	public Object getValueAt(int column)
	{
		if (column == 0)
			return this._interfaceObject.getName();
		else if (column == 1) {
			return null;
		} else if (column == 2)
			return this._interfaceObject.getIsAvailable();
		else if (column == 3)
			return _interfaceObject.getDescription();
		else
			return null;
	}

	public void setValueAt(Object value, int column)
	{
		if (column == 2) {
			_interfaceObject.setIsAvailable((Boolean) value);
		}
		if (column == 3) {
			_interfaceObject.setDescription((String) value);
		}
	}

	public TableCellEditor getEditorAt(int column)
	{
		if (column == 2) {
			return _isDeployed;
		}
		if (column == 3) {
			return _description;
		}
		return super.getEditorAt(column);
	}

	public TableCellRenderer getCellRenderer(int row, int column)
	{
		//if(column==1)
		//    return null;
		return new DefaultTableCellRenderer();
	}

	public Class getClassAt(int column)
	{
		if (column == 2) {
			return Boolean.class;
		} else if (column == 3) {
			return String.class;
		} else
			return null;

	}

	// TableObject interface
	public boolean isEditableAt(int column)
	{
		if (column == 2)
			return true;
		else if (column == 3)
			return true;
		else
			return false;
	}
}
