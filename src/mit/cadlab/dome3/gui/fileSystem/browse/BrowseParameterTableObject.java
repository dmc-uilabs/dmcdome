package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomePreference;
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
public class BrowseParameterTableObject extends AbstractTableObject
{
	protected ParameterRuntime _parameter;

/*	protected Renderers.DefaultRenderer defaultRenderer = new Renderers.DefaultRenderer();
	protected Renderers.DomeRealValueRenderer realRenderer = new Renderers.DomeRealValueRenderer();
	protected Renderers.DomeIntegerValueRenderer integerRenderer = new Renderers.DomeIntegerValueRenderer();
	protected Renderers.DomeStringValueRenderer stringRenderer = new Renderers.DomeStringValueRenderer();
	protected Renderers.DomeVectorValueRenderer vectorRenderer = new Renderers.DomeVectorValueRenderer();
	protected Renderers.DomeMatrixRenderer matrixRenderer = new Renderers.DomeMatrixRenderer();
	protected Renderers.DomeEnumerationRenderer enumerationRenderer = new Renderers.DomeEnumerationRenderer();*/

	public BrowseParameterTableObject(ParameterRuntime parameter)
	{
		super(parameter);
		this._parameter = parameter;
	}


	protected static final String NOTHING = "";

	public Object getValueAt(int column)
	{
		if (column == 0)
			return _parameter.getName();
/*		else if (column == 1) { // value
			DataObject data = _parameter.getCurrentDataObject();
			if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean)
				return ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean) data).getBooleanValue();
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal)
				return data;
			//return ((DomeReal)data).getRealValue();
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger)
				return data;
			//return ((DomeInteger)data).getIntegerValue();
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString)
				return data;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector)
				return data;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix)
				return data;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration)
				return data;
		}*/
		return NOTHING;
	}

/*	public TableCellRenderer getRendererAt(int column)
	{
		if (column == 1) {
			DataObject data = _parameter.getCurrentDataObject();
			if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean)
				return defaultRenderer;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal)
				return realRenderer;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger)
				return integerRenderer;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString)
				return stringRenderer;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector)
				return vectorRenderer;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix)
				return matrixRenderer;
			else if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration)
				return enumerationRenderer;
		}
		return null;
	}*/
}
