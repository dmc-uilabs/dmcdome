package dispatchmodelgui.domenativecomponents;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import dispatchmodelgui.GUIConstants;
import dispatchmodelgui.DispatchModelGUIDomeObjectManager;

import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 30, 2003
 * Time: 12:11:27 AM
 * To change this template use Options | File Templates.
 */
public class InputDataDomeObjects
{
    private ModelInterfaceBase _iface;
    private DispatchModelGUIDomeObjectManager _mgr;

    private Parameter _interestRate, _propertyTax, _numberOfDaysRepresented, _powerDemandData;

    public InputDataDomeObjects(ModelInterfaceBase iface, DispatchModelGUIDomeObjectManager mgr)
    {
        _iface = iface;
        _mgr = mgr;

        createMarketAttributeParameters();
    }

    protected void createMarketAttributeParameters()
    {
        _interestRate = getParameterByName(GUIConstants.INTEREST_RATE_PARAMETER);
        _interestRate.getCurrentDataObject()
                .addPropertyChangeListener(DataObject.VALUE, new DataListener(_interestRate));

        _propertyTax = getParameterByName(GUIConstants.PROPERTY_TAX_PARAMETER);
        _propertyTax.getCurrentDataObject()
                .addPropertyChangeListener(DataObject.VALUE, new DataListener(_propertyTax));

        _numberOfDaysRepresented = getParameterByName(GUIConstants.NUMBER_OF_DAYS_REPRESENTED_PARAMETER);
        _numberOfDaysRepresented.getCurrentDataObject()
                .addPropertyChangeListener(DataObject.VALUE, new DataListener(_numberOfDaysRepresented));

        _powerDemandData = getParameterByName(GUIConstants.POWER_DEMAND_DATA_PARAMETER);
        _powerDemandData.getCurrentDataObject()
                .addPropertyChangeListener(DataObject.VALUE, new DataListener(_powerDemandData));
    }

    public Parameter getInterestRateParameter()
    {
        return _interestRate;
    }

    public Parameter getPropertyTaxParameter()
    {
        return _propertyTax;
    }

    public Parameter getPowerDemandData()
    {
        return _powerDemandData;
    }

    public Parameter getNumberOfDaysRepresented()
    {
        return _numberOfDaysRepresented;
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
                if(source instanceof DomeReal)
                {
                    Double newValue = ((DomeReal)source).getRealValue();
                    if(_p.getName().equals(GUIConstants.PROPERTY_TAX_PARAMETER))
                    {
                        _mgr.handleInputPanelParameterChange(
                                GUIConstants.PROPERTY_TAX_PARAMETER, newValue);
                    }
                    else if(_p.getName().equals(GUIConstants.INTEREST_RATE_PARAMETER))
                    {
                        _mgr.handleInputPanelParameterChange(
                                GUIConstants.INTEREST_RATE_PARAMETER, newValue);
                    }
                }
                else if (source instanceof DomeMatrix)
                {
                    if(_p.getName().equals(GUIConstants.NUMBER_OF_DAYS_REPRESENTED_PARAMETER))
                    {
                        Number[] newValue = ((DomeMatrixData)source).getRow(0).getValuesArray();
                        _mgr.handleInputPanelParameterChange(
                                GUIConstants.NUMBER_OF_DAYS_REPRESENTED_PARAMETER, newValue);
                    }
                    else if (_p.getName().equals(GUIConstants.POWER_DEMAND_DATA_PARAMETER))
                    {
                        Double[][] newValue = new Double[7][24];
                        double [][] d = ((DomeMatrixData)source).getDoubleArrayData();
                        for (int i = 0; i < d.length; i++)
                            for (int j = 0; j < d[i].length; j++)
                                    newValue[j][i] = new Double(d[i][j]);
                        _mgr.handleInputPanelParameterChange(
                                GUIConstants.POWER_DEMAND_DATA_PARAMETER, newValue);
                    }
                }
			}
		}
	}


}
