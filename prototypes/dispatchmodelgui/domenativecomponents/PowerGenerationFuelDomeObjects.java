package dispatchmodelgui.domenativecomponents;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import dispatchmodelgui.DispatchModelGUIDomeObjectManager;
import dispatchmodelgui.GUIConstants;

import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 30, 2003
 * Time: 12:11:48 AM
 * To change this template use Options | File Templates.
 */
public class PowerGenerationFuelDomeObjects
{
    private ModelInterfaceBase _iface;
    private DispatchModelGUIDomeObjectManager _mgr;

    private Parameter _conversionEfficiency, _installationCost, _runningCost,
                            _upperLimit, _lowerLimit;

    public PowerGenerationFuelDomeObjects(ModelInterfaceBase iface,
                                          DispatchModelGUIDomeObjectManager mgr)
    {
        _iface = iface;
        _mgr = mgr;

        createPowerGenerationFuelParameters();
    }

    protected void createPowerGenerationFuelParameters()
    {
        _conversionEfficiency = getParameterByName(GUIConstants.CONVERSION_EFFICIENCY_PARAMETER);
        _conversionEfficiency.getCurrentDataObject().addPropertyChangeListener
                        (DataObject.VALUE, new DataListener(_conversionEfficiency));

        _installationCost = getParameterByName(GUIConstants.INSTALLATION_COST_PARAMETER);
        _installationCost.getCurrentDataObject().addPropertyChangeListener(
                        DataObject.VALUE, new DataListener(_installationCost));

        _runningCost = getParameterByName(GUIConstants.RUNNING_COST_PARAMETER);
        _runningCost.getCurrentDataObject().addPropertyChangeListener(
                        DataObject.VALUE, new DataListener(_runningCost));

        _upperLimit = getParameterByName(GUIConstants.UPPER_LIMIT_PARAMETER);
        _upperLimit.getCurrentDataObject().addPropertyChangeListener(
                        DataObject.VALUE, new DataListener(_upperLimit));

        _lowerLimit = getParameterByName(GUIConstants.LOWER_LIMIT_PARAMETER);
        _lowerLimit.getCurrentDataObject().addPropertyChangeListener(
                        DataObject.VALUE, new DataListener(_lowerLimit));
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

    /**
	 * When value changes in parameter, value is set in textfield.
	 */
	class DataListener implements PropertyChangeListener
	{
        Parameter _p;
        public DataListener(Parameter p)
		{
            _p = p;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(DataObject.VALUE))
			{
                Object source = evt.getSource();
                if (source instanceof DomeMatrix)
                {
                    Number[] newValue = ((DomeMatrixData)source).getRow(0).getValuesArray();

                    if(_p.getName().equals(GUIConstants.CONVERSION_EFFICIENCY_PARAMETER))
                    {
                        _mgr.handlePowerGenerationAndFuelParameterChange(
                                GUIConstants.CONVERSION_EFFICIENCY_PARAMETER, newValue);
                    }
                    else if (_p.getName().equals(GUIConstants.INSTALLATION_COST_PARAMETER))
                    {
                        _mgr.handlePowerGenerationAndFuelParameterChange(
                                GUIConstants.INSTALLATION_COST_PARAMETER, newValue);
                    }
                    else if (_p.getName().equals(GUIConstants.RUNNING_COST_PARAMETER))
                    {
                        _mgr.handlePowerGenerationAndFuelParameterChange(
                                GUIConstants.RUNNING_COST_PARAMETER, newValue);
                    }
                    else if (_p.getName().equals(GUIConstants.UPPER_LIMIT_PARAMETER))
                    {
                        _mgr.handlePowerGenerationAndFuelParameterChange(
                                GUIConstants.UPPER_LIMIT_PARAMETER, newValue);
                    }
                    else if (_p.getName().equals(GUIConstants.LOWER_LIMIT_PARAMETER))
                    {
                        _mgr.handlePowerGenerationAndFuelParameterChange(
                                GUIConstants.LOWER_LIMIT_PARAMETER, newValue);
                    }
                }
			}
		}
	}

    public Parameter getConversionEfficiency()
    {
        return _conversionEfficiency;
    }

    public Parameter getInstallationCost()
    {
        return _installationCost;
    }

    public Parameter getRunningCost()
    {
        return _runningCost;
    }

    public Parameter getUpperLimit()
    {
        return _upperLimit;
    }

    public Parameter getLowerLimit()
    {
        return _lowerLimit;
    }
}
