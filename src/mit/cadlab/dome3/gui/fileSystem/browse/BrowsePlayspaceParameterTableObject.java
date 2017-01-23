package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime;
import mit.cadlab.dome3.swing.table.AbstractTableObject;

import javax.swing.table.TableCellRenderer;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 11:29:30 PM
 * To change this template use Options | File Templates.
 */
public class BrowsePlayspaceParameterTableObject extends AbstractTableObject
{
	protected ParameterRuntime _parameter;

	protected Renderers.DefaultRenderer defaultRenderer = new Renderers.DefaultRenderer();
	protected Renderers.DomeRealValueRenderer realRenderer = new Renderers.DomeRealValueRenderer();
	protected Renderers.DomeIntegerValueRenderer integerRenderer = new Renderers.DomeIntegerValueRenderer();
	protected Renderers.DomeStringValueRenderer stringRenderer = new Renderers.DomeStringValueRenderer();
	protected Renderers.DomeVectorValueRenderer vectorRenderer = new Renderers.DomeVectorValueRenderer();
	protected Renderers.DomeMatrixRenderer matrixRenderer = new Renderers.DomeMatrixRenderer();
	protected Renderers.DomePreferenceRenderer preferenceRenderer = new Renderers.DomePreferenceRenderer();
	protected Renderers.DomeEnumerationRenderer enumerationRenderer = new Renderers.DomeEnumerationRenderer();

	public BrowsePlayspaceParameterTableObject(ParameterRuntime parameter)
	{
		super(parameter);
		this._parameter = parameter;
	}


	protected static final String NOTHING = "";

	public Object getValueAt(int column)
	{
		if (column == 0)
			return _parameter.getName();
		else if (column == 1) {
		}
		return NOTHING;
	}

	public TableCellRenderer getRendererAt(int column)
	{
		if (column == 1) {
		}
		return null;
	}
}
