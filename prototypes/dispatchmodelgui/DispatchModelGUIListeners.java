package dispatchmodelgui;

import dispatchmodelgui.dataobjects.DayDataObject;
import dispatchmodelgui.dataobjects.PowerSourceDataObject;
import dispatchmodelgui.dataobjects.FuelDataObject;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 20, 2003
 * Time: 11:48:05 PM
 * To change this template use Options | File Templates.
 */

/**
 * DispatchModelGUIListeners.java
 *
 * This class listens to changes in the DispatchModelGUITabbedPane class
 * and handles those changes accordingly.  It is used for communciation
 * between the gui and the gui data objects as well as the DOME data
 * objects.  The listener works in the following way:
 *
 * The gui components all implement the ActionListener interface.  When
 * a gui component experiences an event, it fires the appropriate
 * property change event to which this class is listening.  This class
 * catches those property change events and takes the appropriate action.
 *
 */

public class DispatchModelGUIListeners
{
    // size of the warning box is declared
    private static final Dimension WARNING_BOX = new Dimension(250, 75);

    // gui class
    private DispatchModelGUITabbedPane _tp;

    // manager of gui data objects
    private DispatchModelGUIDataObjectManager _mgr;

    // property change listeners
    private PropertyChangeSupport _listeners;

    /**
     * DispatchModelGUIListeners constructor
     * Takes in two arguments, the gui object and
     * the data object maanger.  The listener is used
     * to communicate between these objects.
     * @param tp
     * @param mgr
     */
	public DispatchModelGUIListeners(DispatchModelGUITabbedPane tp,
                                     DispatchModelGUIDataObjectManager mgr)
    {
        // assigning arguments to member variables of the class
        _tp = tp;
        _mgr = mgr;

        // assigning a property change listener to this class
        _listeners = new PropertyChangeSupport(this);

        // creates all of the required day and editable chart panel listeners
        createInputPanelListeners();

        // creates all of the power and fuel types listeners
        createPowerGenerationAndFuelTypeListeners();

        // populates the gui with default values of data objects
        initializeGUI();
    }

    /**
     * This method creates all of the input panel
     * listeners.  When an event takes place on this
     * panel, the appropriate listeners are notified
     * and they pass the change in information to the
     * appropriate gui data objects.
     */
    protected void createInputPanelListeners()
    {
        /**
         * NumberOfDaysListener
         * Listens for changes to the number of
         * days text field.  Notified when GUIConstants.
         * CHANGE_NUMBER_OF_DAYS property change is fired.
         */
        NumberOfDaysListener a = new NumberOfDaysListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_NUMBER_OF_DAYS, a);

        /**
         * DayAddedListener
         * Listens for actions on the add button in
         * the gui.  Notified when GUIConstants.
         * CHANGE_DAY_ADDED property change is fired.
         */
        DayAddedListener b = new DayAddedListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_DAY_ADDED, b);

        /**
         * DayRemovedListener
         * Listens for actions on the remove button
         * in the gui.  Notified when GUIConstants.
         * CHANGE_DAY_REMOVED property change is fired.
         */
        DayRemovedListener c = new DayRemovedListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_DAY_REMOVED, c);

        /**
         * ComboBoxListener
         * Listens for actions on the combo box button
         * in the gui.  Notified when GUIConstants.
         * CHANGE_DAY_COMBO_BOX property change is fired.
         */
        ComboBoxListener d = new ComboBoxListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_DAY_COMBO_BOX, d);

        /**
         * NameChangeListener
         * Listens for actions on the name text field.
         * Notified when GUIConstants.CHANGE_NAME_OF_DAY
         * property change is fired.
         */
        NameChangeListener e = new NameChangeListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_NAME_OF_DAY, e);

        /**
         * DaysrepresentedListener
         * Listens for actions on the days represented
         * label.  Notified when GUIConstants.CHANGE
         * _DAYS_REPRESENTED property change is fired.
         */
        DaysRepresentedListener f = new DaysRepresentedListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_DAYS_REPRESENTED, f);

        /**
         * InterestRateListener
         * Listens for actions on the interest rate
         * text field.  Notified when GUIConstants.CHANGE_INTEREST_RATE
         * property change is fired.
         */
        InterestRateListener g = new InterestRateListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_INTEREST_RATE, g);

        /**
         * PropertyTaxListener
         * Listens for actions on the property tax text field.
         * Notified when GUIConstants.CHANGE_PROPERTY_TAX property
         * change is fired.
         */
        PropertyTaxListener h = new PropertyTaxListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_PROPERTY_TAX, h);

        /**
         * EditableChartListener
         * Listens for actions on the editable chart.
         * Notified when GUIConstants.CHANGE_EDIT_X_Y property change is fired.
         */
        EditableChartListener i = new EditableChartListener();
        _tp.getInputPanel().addPropertyChangeListener(GUIConstants.CHANGE_EDIT_X_Y, i);
    }

    protected void createPowerGenerationAndFuelTypeListeners()
    {
        NumberOfPowerSourceListener a = new NumberOfPowerSourceListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_SOURCE, a);

        PowerSourceAddedListener b = new PowerSourceAddedListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_SOURCE_ADDED, b);

        PowerSourceRemovedListener c = new PowerSourceRemovedListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_SOURCE_REMOVED, c);

        PowerSourceComboBoxListener d = new PowerSourceComboBoxListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_SOURCE_COMBO_BOX, d);

        PowerSourceNameListener e = new PowerSourceNameListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_SOURCE_NAME, e);

        PowerSourceFuelListener f = new PowerSourceFuelListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_FUEL_TYPE, f);

        PowerSourceConversionEfficiencyListener g = new PowerSourceConversionEfficiencyListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_CONVERSION_EFFICIENCY, g);

        PowerSourceInstallationCostListener h = new PowerSourceInstallationCostListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_INSTALLATION_COST, h);

        PowerSourceRunningCostListener i = new PowerSourceRunningCostListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_RUNNING_COST, i);

        PowerSourceIncreasingConstraintListener j = new PowerSourceIncreasingConstraintListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_INCREASING_CONSTRAINT, j);

        PowerSourceDecreasingConstraintListener k = new PowerSourceDecreasingConstraintListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_DECREASING_CONSTRAINT, k);

        PowerSourceUpperLimitListener l = new PowerSourceUpperLimitListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_UPPER_LIMIT, l);

        PowerSourceLowerLimitListener m = new PowerSourceLowerLimitListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_LOWER_LIMIT, m);

        PowerSourceNoUpperLimitListener n = new PowerSourceNoUpperLimitListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_NO_UPPER_LIMIT, n);

        PowerSourceInspectionRatioListener o = new PowerSourceInspectionRatioListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_INSPECTION_RATIO, o);

        PowerSourceDepreciationPeriodListener p = new PowerSourceDepreciationPeriodListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_DEPRECIATION_PERIOD, p);

        PowerSourceResidualValueListener q = new PowerSourceResidualValueListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_POWER_RESIDUAL_VALUE, q);

        FuelChangeListener r = new FuelChangeListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_NUMBER, r);

        FuelAddedListener s = new FuelAddedListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_ADDED, s);

        FuelRemovedListener t = new FuelRemovedListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_REMOVED, t);

        FuelComboBoxListener u = new FuelComboBoxListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_COMBO_BOX, u);

        FuelUnitListener v = new FuelUnitListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_UNIT_OPTIONS, v);

        FuelNameListener w = new FuelNameListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_NAME, w);

        FuelCostListener x = new FuelCostListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_COST, x);

        FuelAnnualFlowListener y = new FuelAnnualFlowListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_ANNUAL_FLOW, y);

        FuelHourlyFlowListener z = new FuelHourlyFlowListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_HOURLY_FLOW, z);

        FuelEnergeticValueListener aa = new FuelEnergeticValueListener();
        _tp.getPowerAndFuelPanel().addPropertyChangeListener(GUIConstants.CHANGE_FUEL_ENERGY_VALUE, aa);
    }

    protected void initializeGUI()
    {
        bringCurrentDayObjectToFront();
        bringCurrentPowerSourceDataObjectToFront();
        bringCurrentFuelDataObjectToFront();
    }

    /**
     * listeners for the InputDataSpecficiationPanel
     */

    class NumberOfDaysListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {

        }
    }

    class DayAddedListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            _tp.getInputPanel().getNumberOfDaysTextField().setText(e.getNewValue().toString());
        }
    }

    class DayRemovedListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            _tp.getInputPanel().getNumberOfDaysTextField().setText(e.getNewValue().toString());
        }
    }

    class ComboBoxListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            bringCurrentDayObjectToFront();
        }
    }

    class NameChangeListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String newName = (String) e.getNewValue();
            DayDataObject d = getCurrentDayDataObject();
            if(!newName.equals(d)) d.setName(newName);
            _tp.getInputPanel().swapNameInComboBox();
            _tp.getResultsPanel().swapDaySelectComboBox(_tp.getInputPanel()
                    .getDayComboBox().getSelectedIndex(), newName);
        }
    }

    class DaysRepresentedListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String days = (String) e.getNewValue();
            if(NumberFormatHandler.isPositiveInteger(days))
            {
                getCurrentDayDataObject().setDaysRepresented(Integer.decode(days));
                _tp.getInputPanel().getNumberOfDaysAccountedFor().setText(
                        GUIConstants.TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR +
                            _mgr.getTotalNumberOfDaysRepresented().toString());

                _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_DAYS_REPRESENTED,
                        null, getCurrentDayDataObject().getDaysRepresented());
            }
            else
                _tp.getInputPanel().getDaysRepresentedTextField().setText(
                        getCurrentDayDataObject().getDaysRepresented().toString());
        }
    }

    class InterestRateListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double newValue = Double.valueOf((String) e.getNewValue());
                _mgr.setInterestRate(newValue);

                _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_INTEREST_RATE, null, _mgr.getInterestRate());
            }
            else
                _tp.getInputPanel().getInterestRateTextField().setText(GUIConstants.EMPTY_STRING);
        }
    }

    class PropertyTaxListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double newValue = Double.valueOf((String)e.getNewValue());
                _mgr.setPropertyTax(newValue);

                _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_PROPERTY_TAX, null, _mgr.getPropertyTax());
            }
            else
                _tp.getInputPanel().getPropertyTaxTextField().setText(GUIConstants.EMPTY_STRING);
        }
    }

    class EditableChartListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            Double[] d = (Double[]) e.getNewValue();
            getCurrentDayDataObject().setPowerDemandData(d);

            _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_EDITABLE_CHART, null,
                    getCurrentDayDataObject().getPowerDemandData());
        }
    }

    /**
     * listeners for PowerGenerationAndFuelTypePanel
     */

    class NumberOfPowerSourceListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {

        }
    }

    class PowerSourceAddedListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            _tp.getPowerAndFuelPanel().getNumberPowerSourceTextField().setText(e.getNewValue().toString());
        }
    }

    class PowerSourceRemovedListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            _tp.getPowerAndFuelPanel().getNumberPowerSourceTextField().
                                            setText(e.getNewValue().toString());
        }
    }

    class PowerSourceComboBoxListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            bringCurrentPowerSourceDataObjectToFront();
        }
    }

    class PowerSourceNameListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String newName = (String) e.getNewValue();
            PowerSourceDataObject p = getCurrentPowerSourceDataObject();
            if(!newName.equals(p)) p.setName(newName);
            _tp.getPowerAndFuelPanel().swapPowerNameInComboBox();
            _tp.getResultsPanel().swapPowerGenerationTypeNameInStackedAreaChart(
                    _tp.getPowerAndFuelPanel().getPowerSourceComboBox().getSelectedIndex(), p.getName());
        }
    }

    class PowerSourceFuelListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            int index = _tp.getPowerAndFuelPanel().getFuelForPowerSourceComboBox().getSelectedIndex();
            FuelDataObject f = _mgr.getFuelDataObject(index);
            getCurrentPowerSourceDataObject().setFuelDataObject(f);
        }
    }

    class PowerSourceConversionEfficiencyListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                {
                    getCurrentPowerSourceDataObject().setConversionEfficiency(c);
                    _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_CONVERSION_EFFICIENCY, null, c);

                }
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "conversion efficiency " +
                                                "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getConversionEfficiencyTextField().setText(
                            getCurrentPowerSourceDataObject().getConversionEfficiency().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getConversionEfficiencyTextField().setText(
                        getCurrentPowerSourceDataObject().getConversionEfficiency().toString());
        }
    }

    class PowerSourceInstallationCostListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                {
                    getCurrentPowerSourceDataObject().setInstallationCost(c);

                    _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_INSTALLATION_COST, null, c);
                }
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "installation cost " +
                                            "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getInstallationCostTextField().setText(
                            getCurrentPowerSourceDataObject().getInstallationCost().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getInstallationCostTextField().setText(
                        getCurrentPowerSourceDataObject().getInstallationCost().toString());
        }
    }

    class PowerSourceRunningCostListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                {
                    getCurrentPowerSourceDataObject().setRunningCost(c);

                    _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_RUNNING_COST, null, c);
                }
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "running cost " +
                                            "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getRunningCostTextField().setText(
                            getCurrentPowerSourceDataObject().getRunningCost().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getRunningCostTextField().setText(
                        getCurrentPowerSourceDataObject().getRunningCost().toString());
        }
    }

    class PowerSourceIncreasingConstraintListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentPowerSourceDataObject().setIncreasingConstraint(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "increasing constraint " +
                                                "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getIncreasingConstraintTextField().setText(
                            getCurrentPowerSourceDataObject().getIncreasingConstraint().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getIncreasingConstraintTextField().setText(
                        getCurrentPowerSourceDataObject().getIncreasingConstraint().toString());
        }
    }

    class PowerSourceDecreasingConstraintListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentPowerSourceDataObject().setDecreasingConstraint(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "decreasing constraint " +
                                                "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getDecreasingConstraintTextField().setText(
                            getCurrentPowerSourceDataObject().getDecreasingConstraint().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getDecreasingConstraintTextField().setText(
                        getCurrentPowerSourceDataObject().getDecreasingConstraint().toString());
        }
    }

    class PowerSourceUpperLimitListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                {
                    getCurrentPowerSourceDataObject().setUpperLimit(c);

                    _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_UPPER_LIMIT, null, c);
                }
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "upper limit " +
                                        "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getUpperLimitTextField().setText(
                            getCurrentPowerSourceDataObject().getUpperLimit().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getUpperLimitTextField().setText(
                        getCurrentPowerSourceDataObject().getUpperLimit().toString());
        }
    }

    class PowerSourceLowerLimitListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                {
                    getCurrentPowerSourceDataObject().setLowerLimit(c);

                    _listeners.firePropertyChange(GUIConstants.DOME_CHANGE_LOWER_LIMIT, null, c);
                }
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "lower limit " +
                                        "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getLowerLimitTextField().setText(
                            getCurrentPowerSourceDataObject().getLowerLimit().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getLowerLimitTextField().setText(
                        getCurrentPowerSourceDataObject().getLowerLimit().toString());
        }
    }

    class PowerSourceNoUpperLimitListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            Boolean b = (Boolean)e.getNewValue();
            if (b.booleanValue())
            {
                _tp.getPowerAndFuelPanel().getLimitDoesNotExistLabel().setEnabled(true);
                _tp.getPowerAndFuelPanel().getUpperLimitTextField().setText(GUIConstants.EMPTY_STRING);
                _tp.getPowerAndFuelPanel().getUpperLimitTextField().setEditable(false);
                getCurrentPowerSourceDataObject().setUpperLimit(new Double(10E04));
            }
            else
            {
                _tp.getPowerAndFuelPanel().getLimitDoesNotExistLabel().setEnabled(false);
                _tp.getPowerAndFuelPanel().getUpperLimitTextField().setEditable(true);
                getCurrentPowerSourceDataObject().setUpperLimit(new Double(0.0));
                _tp.getPowerAndFuelPanel().getUpperLimitTextField().setText(
                        getCurrentPowerSourceDataObject().getUpperLimit().toString());
            }

        }
    }

    class PowerSourceInspectionRatioListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentPowerSourceDataObject().setInspectionRatio(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "inspection ratio " +
                            "                       must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getInspectionRatioTextField().setText(
                            getCurrentPowerSourceDataObject().getInspectionRatio().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getInspectionRatioTextField().setText(
                        getCurrentPowerSourceDataObject().getInspectionRatio().toString());
        }
    }

    class PowerSourceDepreciationPeriodListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentPowerSourceDataObject().setDepreciationPeriod(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "depreciation period " +
                            "                       must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getDepreciationPeriodTextField().setText(
                            getCurrentPowerSourceDataObject().getDepreciationPeriod().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getDepreciationPeriodTextField().setText(
                        getCurrentPowerSourceDataObject().getDepreciationPeriod().toString());
        }
    }

    class PowerSourceResidualValueListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentPowerSourceDataObject().setResidualValue(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "residual value " +
                            "                       must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getResidualValueTextField().setText(
                            getCurrentPowerSourceDataObject().getResidualValue().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getResidualValueTextField().setText(
                        getCurrentPowerSourceDataObject().getResidualValue().toString());
        }
    }

    class FuelChangeListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {

        }
    }

    class FuelAddedListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            _tp.getPowerAndFuelPanel().getNumberFuelTypeTextField().setText(e.getNewValue().toString());
        }
    }

    class FuelRemovedListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            _tp.getPowerAndFuelPanel().getNumberFuelTypeTextField().setText(e.getNewValue().toString());
        }
    }

    class FuelComboBoxListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            bringCurrentFuelDataObjectToFront();
            _tp.getPowerAndFuelPanel().takeNoFuelTypeAction();
        }
    }

    class FuelUnitListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            getCurrentFuelDataObject().setUnit((String)_tp.getPowerAndFuelPanel().getFuelUnitOptions().getSelectedItem());
        }
    }

    class FuelNameListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String newName = (String) e.getNewValue();
            FuelDataObject f = getCurrentFuelDataObject();
            if(!newName.equals(f)) f.setName(newName);
            _tp.getPowerAndFuelPanel().swapFuelNameInComboBox();
        }
    }

    class FuelCostListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentFuelDataObject().setCost(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "fuel cost " +
                                        "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getCostOfFuelTextField().setText(
                            getCurrentFuelDataObject().getCost().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getCostOfFuelTextField().setText(
                        getCurrentFuelDataObject().getCost().toString());
        }
    }

    class FuelAnnualFlowListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentFuelDataObject().setAnnualFlow(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "maximum annual flow " +
                                        "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getAnnualFlowTextField().setText(
                            getCurrentFuelDataObject().getAnnualFlow().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getAnnualFlowTextField().setText(
                        getCurrentFuelDataObject().getAnnualFlow().toString());
        }
    }

    class FuelHourlyFlowListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentFuelDataObject().setHourlyFlow(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "maximum hourly flow " +
                                        "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getHourlyFlowTextField().setText(
                            getCurrentFuelDataObject().getHourlyFlow().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getHourlyFlowTextField().setText(
                            getCurrentFuelDataObject().getHourlyFlow().toString());
        }
    }

    class FuelEnergeticValueListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            if(NumberFormatHandler.isDouble((String)e.getNewValue()))
            {
                Double c = Double.valueOf((String)e.getNewValue());
                if(c.doubleValue() > 0.0)
                    getCurrentFuelDataObject().setEnergeticValue(c);
                else
                {
                    OneButton1Msg.showWarning(null, "warning", "energetic value " +
                                        "must be greater than 0.0", "OK", WARNING_BOX);
                    _tp.getPowerAndFuelPanel().getEnergeticValueTextField().setText(
                            getCurrentFuelDataObject().getEnergeticValue().toString());
                }
            }
            else
                _tp.getPowerAndFuelPanel().getEnergeticValueTextField().setText(
                            getCurrentFuelDataObject().getEnergeticValue().toString());
        }
    }



    /**
     * returns the current day data object selected
     * in the combo box
     * @return
     */
    public DayDataObject getCurrentDayDataObject()
    {
        return _mgr.getDayDataObject(_tp.getInputPanel().getDayComboBox().getSelectedIndex());
    }

    /**
     * returns the current power source data object
     * current referring to the combo box selection
     * @return
     */
    public PowerSourceDataObject getCurrentPowerSourceDataObject()
    {
        return _mgr.getPowerSourceDataObject(_tp.getPowerAndFuelPanel().getPowerSourceComboBox().getSelectedIndex());
    }

    /**
     * return the current fuel data object
     * current referring to the combo box selection
     * @return FuelDataObject
     */
    public FuelDataObject getCurrentFuelDataObject()
    {
        return _mgr.getFuelDataObject(_tp.getPowerAndFuelPanel().getFuelComboBox().getSelectedIndex());
    }

    /**
     * class that ensures that number is of appropriate format
     */

    protected static class NumberFormatHandler
    {
        protected static boolean isDouble(String text)
        {
            try
            {
                Double.parseDouble(text);
                return true;
            }
            catch(NumberFormatException e)
            {
                OneButton1Msg.showWarning(null, "number format warning", "value must be a real value", "OK", WARNING_BOX);
                return false;
            }
        }

        protected static boolean isPositiveInteger(String text)
        {
            try
            {
                if(Integer.parseInt(text) < 0)
                {
                    OneButton1Msg.showWarning(null, "warning", "value must be a postive integer", "OK", WARNING_BOX);
                    return false;
                }
                else return
                            true;
            }
            catch(NumberFormatException e)
            {
                OneButton1Msg.showWarning(null, "number format warning", "value must be an integer number", "OK", WARNING_BOX);
                return false;
            }
        }
    }

    /**
     * This method will bring the current day data object
     * to the gui.
     *      Current data object - one who's name
     * corresponds to the currently selected item in the
     * day name combo box.
     */

    private void bringCurrentDayObjectToFront()
    {
        /**
         * important note: this gui assumes that all the day data
         * attribute information is stored in a 1 x n matrix
         * where n is the number of day sin the DOME model.  The
         * information that populates the gui is obtained from the
         * DOME model directly.
         */
        DayDataObject d = getCurrentDayDataObject();

        _tp.getInputPanel().getEditableNameOfDayTextField().setText(d.getName());
        _tp.getInputPanel().getDaysRepresentedTextField().setText(d.getDaysRepresented().toString());

        Double[] db = d.getPowerDemandData();

        Point p = new Point();

        for(int i = 0; i < db.length; i++)
        {
            p.setLocation(i+1, db[i].doubleValue());
            _tp.getInputPanel().getEditableChartPanel().updateXYDataset(p);
        }

        _tp.getInputPanel().getNumberOfDaysAccountedFor().setText(
                GUIConstants.TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR + _mgr.getTotalNumberOfDaysRepresented().toString());

        _tp.getInputPanel().getInterestRateTextField().setText(_mgr.getInterestRate().toString());
        _tp.getInputPanel().getPropertyTaxTextField().setText(_mgr.getPropertyTax().toString());
    }

    /**
     * This mehtod will bring the current power source
     * data object to the gui.
     *      Current power source data object - one which
     * corresponds to the currently selected item in the power
     * source combo box.
     */

    private void bringCurrentPowerSourceDataObjectToFront()
    {
        PowerSourceDataObject p = getCurrentPowerSourceDataObject();

        _tp.getPowerAndFuelPanel().getPowerSourceNameTextField().setText(p.getName());

        _tp.getPowerAndFuelPanel().getConversionEfficiencyTextField().setText(p.getConversionEfficiency().toString());

        _tp.getPowerAndFuelPanel().getInstallationCostTextField().setText(p.getInstallationCost().toString());

        _tp.getPowerAndFuelPanel().getRunningCostTextField().setText(p.getRunningCost().toString());

        _tp.getPowerAndFuelPanel().getIncreasingConstraintTextField().setText(p.getIncreasingConstraint().toString());

        _tp.getPowerAndFuelPanel().getDecreasingConstraintTextField().setText(p.getDecreasingConstraint().toString());

        if(p.getUpperLimit().doubleValue() == 10E04)
        {
			_tp.getPowerAndFuelPanel().getUpperLimitTextField().setText(GUIConstants.EMPTY_STRING);
			_tp.getPowerAndFuelPanel().getUpperLimitTextField().setEditable(false);
			_tp.getPowerAndFuelPanel().getLimitDoesNotExistLabel().setEnabled(true);
			_tp.getPowerAndFuelPanel().getNoUpperLimitButton().setSelected(true);
		}
		else
		{
			_tp.getPowerAndFuelPanel().getUpperLimitTextField().setEditable(true);
			_tp.getPowerAndFuelPanel().getUpperLimitTextField().setText(p.getUpperLimit().toString());
			_tp.getPowerAndFuelPanel().getLimitDoesNotExistLabel().setEnabled(false);
			_tp.getPowerAndFuelPanel().getNoUpperLimitButton().setSelected(false);
		}

        _tp.getPowerAndFuelPanel().getLowerLimitTextField().setText(p.getLowerLimit().toString());

        _tp.getPowerAndFuelPanel().getInspectionRatioTextField().setText(p.getInspectionRatio().toString());

        _tp.getPowerAndFuelPanel().getDepreciationPeriodTextField().setText(p.getDepreciationPeriod().toString());

        _tp.getPowerAndFuelPanel().getResidualValueTextField().setText(p.getResidualValue().toString());
    }

    /**
     * This method will bring the object represented
     * by the current fuel types combo box selection
     * to the gui.
     */

    private void bringCurrentFuelDataObjectToFront()
    {
        FuelDataObject f = getCurrentFuelDataObject();

        if (!(_tp.getPowerAndFuelPanel().getFuelComboBox().getSelectedIndex() == 0))
        {
            _tp.getPowerAndFuelPanel().getFuelNameTextField().setText(f.getName());
            _tp.getPowerAndFuelPanel().getCostOfFuelTextField().setText(f.getCost().toString());
            _tp.getPowerAndFuelPanel().getAnnualFlowTextField().setText(f.getAnnualFlow().toString());
            _tp.getPowerAndFuelPanel().getHourlyFlowTextField().setText(f.getHourlyFlow().toString());
            _tp.getPowerAndFuelPanel().getEnergeticValueTextField().setText(f.getEnergeticValue().toString());
        }
    }

    public DispatchModelGUIDataObjectManager getDataObjectManager()
    {
        return _mgr;
    }

    public DispatchModelGUITabbedPane getTabbedPane()
    {
        return _tp;
    }

    /**
     * PropertyChangeSupport listeners
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        _listeners.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        _listeners.removePropertyChangeListener(propertyChangeListener);
    }

    public void addPropertyChangeListener(String s, PropertyChangeListener propertyChangeListener)
    {
        _listeners.addPropertyChangeListener(s, propertyChangeListener);
    }

    public void removePropertyChangeListener(String s, PropertyChangeListener propertyChangeListener)
    {
        _listeners.removePropertyChangeListener(s, propertyChangeListener);
    }


}
