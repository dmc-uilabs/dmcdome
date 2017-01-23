package dispatchmodelgui.domenativecomponents;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import dispatchmodelgui.DispatchModelGUIDomeObjectManager;
import dispatchmodelgui.GUIConstants;

import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 30, 2003
 * Time: 12:12:08 AM
 * To change this template use Options | File Templates.
 */
public class ModelResultsDomeObjects
{
    private ModelInterfaceBase _iface;
    private DispatchModelGUIDomeObjectManager _mgr;

    private Parameter _generatedPower;

    public ModelResultsDomeObjects(ModelInterfaceBase iface, DispatchModelGUIDomeObjectManager mgr)
    {
        _iface = iface;
        _mgr = mgr;

        createModelResultsParameters();
        createModelResultsParametersListeners();
    }

    protected void createModelResultsParameters()
    {
        _generatedPower = getParameterByName(GUIConstants.GENERATED_POWER_SUPPLY_PARAMETER);
    }

    protected void createModelResultsParametersListeners()
    {
        _generatedPower.getCurrentDataObject().addPropertyChangeListener(
                DataObject.VALUE, new DataResultsListener(_generatedPower));
    }

    protected Parameter getParameterByName(String paramName)
	{
		Iterator it = this._iface.getModelObjectParameters().iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Parameter)
			{
				if (((Parameter) o).getName().equals(paramName))
					return (Parameter) o;
			}
		}
		throw new RuntimeException("unable to find parameter " + paramName);
	}

    protected class DataResultsListener implements PropertyChangeListener
    {
        private Parameter _p;

        public DataResultsListener(Parameter p)
        {
            _p = p;
        }

        public void propertyChange(PropertyChangeEvent e)
        {
            Object source = e.getSource();
            if (source instanceof DomeMatrix)
            {
                if (_p.getName().equals(GUIConstants.GENERATED_POWER_SUPPLY_PARAMETER))
                {
                    _mgr.handleModelResultsParametersChange(_p.getName(), source);
                }
            }
        }
    }
}
