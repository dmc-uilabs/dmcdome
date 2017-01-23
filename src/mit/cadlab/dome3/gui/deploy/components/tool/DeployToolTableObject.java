package mit.cadlab.dome3.gui.deploy.components.tool;

import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;
import mit.cadlab.dome3.swing.table.AbstractTableObject;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 17, 2003
 * Time: 9:20:45 PM
 * To change this template use Options | File Templates.
 */
public class DeployToolTableObject extends AbstractTableObject
{
	protected DeployAnalysisToolData _toolObject;
	protected static TextCellEditor _description = new TextCellEditor();

	public DeployToolTableObject(DeployAnalysisToolData toolObject)
	{
		super(toolObject);
		_toolObject = toolObject;
	}

	public Object getValueAt(int column)
	{
		if (column == 0)
			return _toolObject.getName();
		else if (column == 1) {
			return null;
		} else if (column == 2)
			return null;
		else if (column == 3)
			return _toolObject.getDescription();
		else
			return null;
	}

	public boolean isEditableAt(int column)
	{
		return false;
	}


}