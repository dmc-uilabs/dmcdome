package dispatchmodelgui;

import dispatchmodelgui.domenativecomponents.InputDataDomeObjects;
import dispatchmodelgui.domenativecomponents.PowerGenerationFuelDomeObjects;
import dispatchmodelgui.domenativecomponents.ModelResultsDomeObjects;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 30, 2003
 * Time: 12:07:52 AM
 * To change this template use Options | File Templates.
 */
public class DispatchModelGUIDomeObjectManager
{
    private DispatchModelGUIListeners _listeners;
    private ModelInterfaceBase _iface;

    private InputDataDomeObjects _iO;
    private PowerGenerationFuelDomeObjects _pO;
    private ModelResultsDomeObjects _rO;

    public DispatchModelGUIDomeObjectManager(DispatchModelGUIListeners listeners,
                                             ModelInterfaceBase iface)
    {
        _listeners = listeners;
        _iface = iface;

        createInputDataObjectManager();
        createPowerAndFuelObjectManager();
        createModelResultsObjectManager();

        registerInputDataListeners();
        registerPowerAndFuelObjectManager();
    }

    protected void createInputDataObjectManager()
    {
        _iO = new InputDataDomeObjects(_iface, this);
    }

    protected void createPowerAndFuelObjectManager()
    {
        _pO = new PowerGenerationFuelDomeObjects(_iface, this);
    }

    protected void createModelResultsObjectManager()
    {
        _rO = new ModelResultsDomeObjects(_iface, this);
    }

    protected void registerInputDataListeners()
    {
        InterestRateListener a = new InterestRateListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_INTEREST_RATE, a);

        PropertyTaxListener b = new PropertyTaxListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_PROPERTY_TAX, b);

        EditableChartListener c = new EditableChartListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_EDITABLE_CHART, c);

        DaysRepresentedListener d = new DaysRepresentedListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_DAYS_REPRESENTED, d);
    }

    protected void registerPowerAndFuelObjectManager()
    {
        ConversionEfficiencyListener a = new ConversionEfficiencyListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_CONVERSION_EFFICIENCY, a);

        InstallationCostListener b = new InstallationCostListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_INSTALLATION_COST, b);

        RunningCostListener c = new RunningCostListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_RUNNING_COST, c);

        UpperLimitListener d = new UpperLimitListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_UPPER_LIMIT, d);

        LowerLimitListener e = new LowerLimitListener();
        _listeners.addPropertyChangeListener(GUIConstants.DOME_CHANGE_LOWER_LIMIT, e);
    }

    class InterestRateListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            Double newValue = (Double) e.getNewValue();
            ((DomeReal)_iO.getInterestRateParameter().getCurrentDataObject()).setRealValue(newValue);
        }
    }

    class PropertyTaxListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            Double newValue = (Double) e.getNewValue();
            ((DomeReal)_iO.getPropertyTaxParameter().getCurrentDataObject()).setRealValue(newValue);
        }
    }

    class EditableChartListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            Double[] newValue = (Double[]) e.getNewValue();

            for (int i = 0; i < newValue.length; i++)
                ((DomeMatrix)_iO.getPowerDemandData().getCurrentDataObject())
                        .setItem(i, _listeners.getTabbedPane().getInputPanel()
                                .getDayComboBox().getSelectedIndex(), newValue[i]);
        }
    }

    class DaysRepresentedListener implements PropertyChangeListener
    {
        public void propertyChange (PropertyChangeEvent e)
        {
            Integer newValue = (Integer) e.getNewValue();

            ((DomeMatrix)_iO.getNumberOfDaysRepresented().getCurrentDataObject())
                    .setItem(0, _listeners.getTabbedPane().getInputPanel()
                            .getDayComboBox().getSelectedIndex(), newValue);
        }
    }

    class ConversionEfficiencyListener implements PropertyChangeListener
    {
        public void propertyChange (PropertyChangeEvent e)
        {
            Double newValue = (Double) e.getNewValue();

            ((DomeMatrix)_pO.getConversionEfficiency().getCurrentDataObject())
                    .setItem(0, _listeners.getTabbedPane().getPowerAndFuelPanel()
                            .getPowerSourceComboBox().getSelectedIndex(), newValue);
        }
    }

    class InstallationCostListener implements PropertyChangeListener
    {
        public void propertyChange (PropertyChangeEvent e)
        {
            Double newValue = (Double) e.getNewValue();

            ((DomeMatrix)_pO.getInstallationCost().getCurrentDataObject())
                    .setItem(0, _listeners.getTabbedPane().getPowerAndFuelPanel()
                            .getPowerSourceComboBox().getSelectedIndex(), newValue);
        }
    }

    class RunningCostListener implements PropertyChangeListener
    {
        public void propertyChange (PropertyChangeEvent e)
        {
            Double newValue = (Double) e.getNewValue();

            ((DomeMatrix)_pO.getRunningCost().getCurrentDataObject())
                    .setItem(0, _listeners.getTabbedPane().getPowerAndFuelPanel()
                            .getPowerSourceComboBox().getSelectedIndex(), newValue);
        }
    }

    class UpperLimitListener implements PropertyChangeListener
    {
        public void propertyChange (PropertyChangeEvent e)
        {
            Double newValue = (Double) e.getNewValue();
            ((DomeMatrix)_pO.getUpperLimit().getCurrentDataObject())
                    .setItem(0, _listeners.getTabbedPane().getPowerAndFuelPanel()
                            .getPowerSourceComboBox().getSelectedIndex(), newValue);
        }
    }

    class LowerLimitListener implements PropertyChangeListener
    {
        public void propertyChange (PropertyChangeEvent e)
        {
            Double newValue = (Double) e.getNewValue();
            ((DomeMatrix)_pO.getLowerLimit().getCurrentDataObject())
                    .setItem(0, _listeners.getTabbedPane().getPowerAndFuelPanel()
                            .getPowerSourceComboBox().getSelectedIndex(), newValue);
        }
    }

    public void handleInputPanelParameterChange(String parameterName, Object value)
    {
        if(parameterName.equals(GUIConstants.PROPERTY_TAX_PARAMETER))
        {
            _listeners.getDataObjectManager().setPropertyTax((Double)value);
            _listeners.getTabbedPane().getInputPanel().getPropertyTaxTextField().setText(value.toString());
        }
        else if(parameterName.equals(GUIConstants.INTEREST_RATE_PARAMETER))
        {
            _listeners.getDataObjectManager().setInterestRate((Double)value);
            _listeners.getTabbedPane().getInputPanel().getInterestRateTextField().setText(value.toString());
        }
        else if(parameterName.equals(GUIConstants.NUMBER_OF_DAYS_REPRESENTED_PARAMETER))
        {
            Number[] d = (Number[])value;
            for(int i = 0; i < d.length; i++)
                _listeners.getDataObjectManager().getDayDataObject(i)
                    .setDaysRepresented(new Integer(d[i].intValue()));
            _listeners.getTabbedPane().getInputPanel().getDaysRepresentedTextField()
                    .setText(_listeners.getCurrentDayDataObject().getDaysRepresented().toString());
            _listeners.getTabbedPane().getInputPanel().getNumberOfDaysAccountedFor()
                    .setText(GUIConstants.TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR +
                            _listeners.getDataObjectManager().
                                    getTotalNumberOfDaysRepresented().toString());
        }
        else if (parameterName.equals(GUIConstants.POWER_DEMAND_DATA_PARAMETER))
        {
            Double[][] d = (Double[][])value;

            for (int i = 0; i < d.length; i++)
                _listeners.getDataObjectManager().getDayDataObject(i)
                        .setPowerDemandData(d[i]);

            Double[] a = _listeners.getCurrentDayDataObject().getPowerDemandData();

            Point p = new Point();

            for (int i = 0; i < a.length; i++)
            {
                p.setLocation(i + 1, a[i].doubleValue());
                _listeners.getTabbedPane().getInputPanel().getEditableChartPanel().updateXYDataset(p);
            }

        }
    }

    public void handlePowerGenerationAndFuelParameterChange(String parameterName, Object value)
    {
        Number[] newValue = (Number[]) value;
        if (parameterName.equals(GUIConstants.CONVERSION_EFFICIENCY_PARAMETER))
        {

            for(int i = 0; i < newValue.length; i++)
            {
                _listeners.getDataObjectManager().getPowerSourceDataObject(i).
                        setConversionEfficiency(new Double(newValue[i].doubleValue()));
                if(i == _listeners.getTabbedPane().getPowerAndFuelPanel().getPowerSourceComboBox().getSelectedIndex())
                    _listeners.getTabbedPane().getPowerAndFuelPanel().
                            getConversionEfficiencyTextField().setText(newValue[i].toString());
            }

        }
        else if (parameterName.equals(GUIConstants.INSTALLATION_COST_PARAMETER))
        {
            for(int i = 0; i < newValue.length; i++)
            {
                _listeners.getDataObjectManager().getPowerSourceDataObject(i).
                        setInstallationCost(new Double(newValue[i].doubleValue()));
                if(i == _listeners.getTabbedPane().getPowerAndFuelPanel().getPowerSourceComboBox().getSelectedIndex())
                    _listeners.getTabbedPane().getPowerAndFuelPanel().
                            getInstallationCostTextField().setText(newValue[i].toString());
            }
        }
        else if (parameterName.equals(GUIConstants.RUNNING_COST_PARAMETER))
        {
            for(int i = 0; i < newValue.length; i++)
            {
                _listeners.getDataObjectManager().getPowerSourceDataObject(i).
                        setRunningCost(new Double(newValue[i].doubleValue()));
                if(i == _listeners.getTabbedPane().getPowerAndFuelPanel().getPowerSourceComboBox().getSelectedIndex())
                    _listeners.getTabbedPane().getPowerAndFuelPanel().
                            getRunningCostTextField().setText(newValue[i].toString());
            }
        }
        else if (parameterName.equals(GUIConstants.UPPER_LIMIT_PARAMETER))
        {
            for(int i = 0; i < newValue.length; i++)
            {
                _listeners.getDataObjectManager().getPowerSourceDataObject(i).
                        setUpperLimit(new Double(newValue[i].doubleValue()));
                if(i == _listeners.getTabbedPane().getPowerAndFuelPanel().getPowerSourceComboBox().getSelectedIndex())
                    _listeners.getTabbedPane().getPowerAndFuelPanel().
                            getUpperLimitTextField().setText(newValue[i].toString());
            }
        }
        else if (parameterName.equals(GUIConstants.LOWER_LIMIT_PARAMETER))
        {
            for(int i = 0; i < newValue.length; i++)
            {
                _listeners.getDataObjectManager().getPowerSourceDataObject(i).
                        setLowerLimit(new Double(newValue[i].doubleValue()));
                if(i == _listeners.getTabbedPane().getPowerAndFuelPanel().getPowerSourceComboBox().getSelectedIndex())
                    _listeners.getTabbedPane().getPowerAndFuelPanel().
                            getLowerLimitTextField().setText(newValue[i].toString());
            }
        }
    }

    public void handleModelResultsParametersChange(String parameterName, Object value)
    {
        if (parameterName.equals(GUIConstants.GENERATED_POWER_SUPPLY_PARAMETER))
        {
            Number[][] data = ((DomeMatrixData)value).getNumberArrayData();
            _listeners.getTabbedPane().getResultsPanel().updateData(data);
        }
    }
}
