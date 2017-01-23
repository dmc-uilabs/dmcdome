package dispatchmodelgui.components;

import dispatchmodelgui.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 19, 2003
 * Time: 12:34:29 AM
 * To change this template use Options | File Templates.
 */
public class PowerSourceAndFuelInformationPanel extends JPanel
                                                                implements ActionListener
{
    private static final GridBagConstraints gbc = null;

    private JLabel _attributesForPowerTypeLabel = GUIConstants.makeLabel(GUIConstants.POWER_GENERATION_TYPE_ATTRIBUTES);
    private JLabel _limitDoesNotExistLabel = GUIConstants.makeLabel(GUIConstants.LIMIT_DOES_NOT_EXIST);
    private JLabel _attributesForFuelTypeLabel = GUIConstants.makeLabel(GUIConstants.ATTRIBUTE_FUEL_TYPE);

    private JTextField _numberPowerGenerationTypes, _editablePowerSourceName, _conversionEfficiencyTextField,
                            _installationCostTextField, _runningCostTextField, _increasingConstraintTextField,
                                _decreasingConstraintTextField, _upperLimitTextField, _lowerLimitTextField, _inspectionRatioTextField,
                                    _depreciationPeriodTextField, _residualValueTextField, _numberFuelTypesTextField, _fuelNameTextField,
                                        _fuelCostTextField, _maximumAnnualFlowTextField, _maximumHourlyFlowTextField, _energeticValueTextField;


    private JButton _addPowerSource, _removePowerSource, _addFuelType, _removeFuelType;

    private JComboBox _powerSourceComboBox, _fuelTypesForPowerSource, _fuelTypesComboBox, _fuelUnitOptions;

    private JRadioButton _noUpperLimitButton;

    private PropertyChangeSupport _listener;

    private Integer _numberPowerSource = GUIConstants.DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES;
    private Integer _numberFuelType = GUIConstants.DEFAULT_NUMBER_OF_FUEL_TYPES;

    public void actionPerformed(ActionEvent e)
    {
        if(_addPowerSource.equals(e.getSource()))
        {
            Integer powerSource = _numberPowerSource;
            _numberPowerSource = new Integer(powerSource.intValue() + 1);
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_SOURCE_ADDED, powerSource, _numberPowerSource);
        }
        else if(_removePowerSource.equals(e.getSource()))
        {
            Integer powerSource = _numberPowerSource;
            _numberPowerSource = new Integer(powerSource.intValue() - 1);
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_SOURCE_REMOVED, powerSource, _numberPowerSource);
        }
        else if(_powerSourceComboBox.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_SOURCE_COMBO_BOX,
                        null, new Integer(_powerSourceComboBox.getSelectedIndex()));
        }
        else if(_editablePowerSourceName.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_SOURCE_NAME,
                                        null, _editablePowerSourceName.getText());
        }
        else if(_fuelTypesForPowerSource.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_FUEL_TYPE,
                                        null, _fuelTypesForPowerSource.getSelectedItem());
        }
        else if(_conversionEfficiencyTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_CONVERSION_EFFICIENCY,
                                        null, _conversionEfficiencyTextField.getText());
        }
        else if(_installationCostTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_INSTALLATION_COST, null, _installationCostTextField.getText());
        }
        else if(_runningCostTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_RUNNING_COST, null, _runningCostTextField.getText());
        }
        else if(_increasingConstraintTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_INCREASING_CONSTRAINT, null,  _increasingConstraintTextField.getText());
        }
        else if(_decreasingConstraintTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_DECREASING_CONSTRAINT, null, _decreasingConstraintTextField.getText());
        }
        else if(_upperLimitTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_UPPER_LIMIT, null, _upperLimitTextField.getText());
        }
        else if(_lowerLimitTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_LOWER_LIMIT, null, _lowerLimitTextField.getText());
        }
        else if(_noUpperLimitButton.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_NO_UPPER_LIMIT, null, new Boolean(_noUpperLimitButton.isSelected()));
        }
        else if(_inspectionRatioTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_INSPECTION_RATIO, null, _inspectionRatioTextField.getText());
        }
        else if(_depreciationPeriodTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_DEPRECIATION_PERIOD, null, _depreciationPeriodTextField.getText());
        }
        else if(_residualValueTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_POWER_RESIDUAL_VALUE, null, _residualValueTextField.getText());
        }
        else if(_numberFuelTypesTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_NUMBER, null, _numberFuelTypesTextField.getText());
        }
        else if(_addFuelType.equals(e.getSource()))
        {
            Integer oldNumberFuelType = _numberFuelType;
            _numberFuelType = new Integer(oldNumberFuelType.intValue() + 1);
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_ADDED, null, _numberFuelType);
        }
        else if(_removeFuelType.equals(e.getSource()))
        {
            Integer oldNumberFuelType = _numberFuelType;
            _numberFuelType = new Integer(oldNumberFuelType.intValue() - 1);
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_REMOVED, null, _numberFuelType);
        }
        else if(_fuelTypesComboBox.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_COMBO_BOX, null, _fuelTypesComboBox.getSelectedItem());
        }
        else if(_fuelUnitOptions.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_UNIT_OPTIONS, null, _fuelUnitOptions.getSelectedItem());
        }
        else if(_fuelNameTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_NAME, null, _fuelNameTextField.getText());
        }
        else if(_fuelCostTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_COST, null, _fuelCostTextField.getText());
        }
        else if(_maximumAnnualFlowTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_ANNUAL_FLOW, null, _maximumAnnualFlowTextField.getText());
        }
        else if(_maximumHourlyFlowTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_HOURLY_FLOW, null, _maximumHourlyFlowTextField.getText());
        }
        else if(_energeticValueTextField.equals(e.getSource()))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_FUEL_ENERGY_VALUE, null, _energeticValueTextField.getText());
        }
    }

    public PowerSourceAndFuelInformationPanel()
    {
        super();

        configurePanel();

        JComponent[] comps = {

            makePowerGenerationPanel(),
            makeFuelTypePanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        GUIConstants.layoutGridBag(this, comps, gbcs);

        _listener = new PropertyChangeSupport(this);
    }

    protected void configurePanel()
    {
        setSize(GUIConstants.DEFAULT_SIZE);
    }

    protected JPanel makePowerGenerationPanel()
    {
        makePowerGenerationComponents();
        return layoutPowerGenerationComponents();
    }

    protected JPanel makeFuelTypePanel()
    {
        makeFuelTypeComponents();
        return layoutFuelTypeComponents();
    }

    private void makePowerGenerationComponents()
    {
        _numberPowerGenerationTypes = GUIConstants.makeTextField();
        _numberPowerGenerationTypes.setText(GUIConstants.DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES.toString());
        _numberPowerGenerationTypes.setEditable(false);

        _addPowerSource = GUIConstants.makeButton(GUIConstants.ADD);
        _addPowerSource.addActionListener(this);
        _addPowerSource.setEnabled(false);
        _removePowerSource = GUIConstants.makeButton(GUIConstants.REMOVE);
        _removePowerSource.addActionListener(this);
        _removePowerSource.setEnabled(false);

        _powerSourceComboBox = GUIConstants.makeComboBox(GUIConstants.seriesNames);
        _powerSourceComboBox.addActionListener(this);

		_editablePowerSourceName = GUIConstants.makeTextField();
        _editablePowerSourceName.addActionListener(this);

        _fuelTypesForPowerSource = GUIConstants.makeComboBox(GUIConstants.fuelTypeOptions);
        _fuelTypesForPowerSource.addActionListener(this);

        _conversionEfficiencyTextField = GUIConstants.makeTextField();
        _conversionEfficiencyTextField.addActionListener(this);

        _installationCostTextField = GUIConstants.makeTextField();
        _installationCostTextField.addActionListener(this);

        _runningCostTextField = GUIConstants.makeTextField();
        _runningCostTextField.addActionListener(this);

        _increasingConstraintTextField = GUIConstants.makeTextField();
        _increasingConstraintTextField.addActionListener(this);

        _decreasingConstraintTextField = GUIConstants.makeTextField();
        _decreasingConstraintTextField.addActionListener(this);

        _limitDoesNotExistLabel.setEnabled(false);

        _upperLimitTextField = GUIConstants.makeTextField();
        _upperLimitTextField.addActionListener(this);
        _lowerLimitTextField = GUIConstants.makeTextField();
        _lowerLimitTextField.addActionListener(this);

        _noUpperLimitButton = GUIConstants.makeRadioButton();
        _noUpperLimitButton.addActionListener(this);

        _inspectionRatioTextField = GUIConstants.makeTextField();
        _inspectionRatioTextField.addActionListener(this);

        _depreciationPeriodTextField = GUIConstants.makeTextField();
        _depreciationPeriodTextField.addActionListener(this);

        _residualValueTextField = GUIConstants.makeTextField();
        _residualValueTextField.addActionListener(this);
    }

    private void makeFuelTypeComponents()
    {
        _numberFuelTypesTextField = GUIConstants.makeTextField();
        _numberFuelTypesTextField.setText(GUIConstants.DEFAULT_NUMBER_OF_FUEL_TYPES.toString());
        _numberFuelTypesTextField.setEditable(false);

        _addFuelType = GUIConstants.makeButton(GUIConstants.ADD);
        _addFuelType.setEnabled(false);
        _addFuelType.addActionListener(this);

        _removeFuelType = GUIConstants.makeButton(GUIConstants.REMOVE);
        _removeFuelType.setEnabled(false);
        _removeFuelType.addActionListener(this);

        _fuelTypesComboBox = GUIConstants.makeComboBox(GUIConstants.fuelTypeOptions);
        _fuelTypesComboBox.addActionListener(this);

        _fuelUnitOptions = GUIConstants.makeComboBox(GUIConstants.fuelUnitOptions);
        _fuelUnitOptions.addActionListener(this);

        _fuelNameTextField = GUIConstants.makeTextField();
        _fuelNameTextField.addActionListener(this);

        _fuelCostTextField = GUIConstants.makeTextField();
        _fuelCostTextField.addActionListener(this);

        _maximumAnnualFlowTextField = GUIConstants.makeTextField();
        _maximumAnnualFlowTextField.addActionListener(this);

        _maximumHourlyFlowTextField = GUIConstants.makeTextField();
        _maximumHourlyFlowTextField.addActionListener(this);

        _energeticValueTextField = GUIConstants.makeTextField();
        _energeticValueTextField.addActionListener(this);

        takeNoFuelTypeAction();
    }

    private JPanel layoutPowerGenerationComponents()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, GUIConstants.POWER_GENERATION, 0, 0, GUIConstants.BOLD_FONT));

        JComponent[] comps = {

			// number of power sources  (text field) add  remove
			GUIConstants.numberPowerSourcesLabel, _numberPowerGenerationTypes, _addPowerSource, _removePowerSource,

			// power generation types combo box
			_powerSourceComboBox,

			// simple label for power generation attributes
            _attributesForPowerTypeLabel,

            // name: (text field)
            GUIConstants.powerGenerationNameLabel, _editablePowerSourceName,

            // fuel type: (text field)
			GUIConstants.fuelTypeForPowerSourceLabel, _fuelTypesForPowerSource,

            // efficiency (text field) %
            GUIConstants.conversionEfficiencyLabel, _conversionEfficiencyTextField, GUIConstants.percentLabel,

            // installation cost (text field) Yen / kW
            GUIConstants.installationCostLabel, _installationCostTextField, GUIConstants.yenPerKw,

            GUIConstants.runningCostLabel, _runningCostTextField, GUIConstants.yenPerKwh,

            GUIConstants.loadFollowingCharacteristicsLabel,

            makeConstraintsPanel(),

            // capacity
            GUIConstants.capacityLabel,

            // upperLimit __________________ GW  (RB) limit does not exist
            GUIConstants.upperLimitLabel, _upperLimitTextField, GUIConstants.gwUpperLabel, _noUpperLimitButton, _limitDoesNotExistLabel,

            // lowerLimit __________________ GW
            GUIConstants.lowerLimitLabel, this._lowerLimitTextField, GUIConstants.gwLowerLabel,

			//energy plan parameters for each centralized power source
			GUIConstants.energyPlanLabel,

            //inspection ratio ________________ %
            GUIConstants.inspectionRatioLabel, this._inspectionRatioTextField, GUIConstants.inspectionPercentLabel,

            //depreciation period _______________ years
            GUIConstants.depreciationLabel, this._depreciationPeriodTextField, GUIConstants.yearsLabel,

            // residual value ___________________ %
            GUIConstants.residualValueLabel, this._residualValueTextField, GUIConstants.residualPercentLabel,

            new JPanel()

		};

		GridBagConstraints[] gbcs = {

			// this._numberPowerGenerationTypes
			new GridBagConstraints(0, 1, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 2, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 100, 0),
			new GridBagConstraints(3, 1, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(4, 1, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),

			// this._powerSourceComboBox
			new GridBagConstraints(0, 2, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 20, 0, 20), 0, 0),

			// this._attributesForPowerTypeLabel
			new GridBagConstraints(0, 3, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			// this.powerGenerationNameLabel, this._editablePowerSourceName
			new GridBagConstraints(0, 4, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 20), 0, 0),

			// fuelTypeForPowerSourceLabel, this._fuelTypesForPowerSource,
			new GridBagConstraints(0, 5, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 5, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 20), 0, 0),

			// conversionEfficiencyLabel, this._conversionEfficiencyTextField, percentLabel,
			new GridBagConstraints(0, 6, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 6, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			new GridBagConstraints(0, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 7, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			new GridBagConstraints(0, 8, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 8, 3, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 8, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			new GridBagConstraints(0, 9, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 10, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			// capacity label
			new GridBagConstraints(0, 11, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			// upperLimitLabel, this._upperLimitTextField, gwLabel, this._noUpperLimitButton, _limitDoesNotExistLabel,
			new GridBagConstraints(0, 12, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 12, 1, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 12, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 12, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 25, 0, 0), 0, 0),
			new GridBagConstraints(4, 12, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 0, 0, 20), 0, 0),

			// lowerLimitLable, this._lowerLimitTextField, gwLabel
			new GridBagConstraints(0, 13, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 13, 1, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 13, 3, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// energyPlanLabel
			new GridBagConstraints(0, 14, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0),

			//inspectionRatioLabel, this._inspectionRatioTextField, inspectionPercentLabel,
			new GridBagConstraints(0, 15, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0),0 ,0),
			new GridBagConstraints(1, 15, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 15, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// depreciationLabel, this._depreciationValue, yearsLabel,
			new GridBagConstraints(0, 16, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 16, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 16, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// residualValueLabel, this._residualValueTextField, residualPercentLabel,
			new GridBagConstraints(0, 17, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 17, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 17, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// JPanel last spacer
			new GridBagConstraints(0, 18, 5, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

        GUIConstants.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeConstraintsPanel()
    {
        JPanel p = new JPanel();

        JComponent[] comps = {

            GUIConstants.increasingConstraintLabel, _increasingConstraintTextField, GUIConstants.decreasingConstraintLabel, _decreasingConstraintTextField
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 5), 100, 0),
            new GridBagConstraints(2, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 100, 0),

        };

        GUIConstants.layoutGridBag(p, comps, gbcs);
        return p;
    }

    private JPanel layoutFuelTypeComponents()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, GUIConstants.FUEL_TYPES,  0, 0, GUIConstants.BOLD_FONT));

        JComponent[] comps = {

			// fuel types _________________ add remove
			GUIConstants.numberFuelTypesLabel, _numberFuelTypesTextField, this._addFuelType, this._removeFuelType,

			// JComboBox fuel types selection
			_fuelTypesComboBox,

			// fuel attributes:
			_attributesForFuelTypeLabel,

			// name _____________________
			GUIConstants.fuelNameLabel, _fuelNameTextField,

			// cost of fuel __________________ Yen / unit
			GUIConstants.costOfFuelLabel, _fuelCostTextField, GUIConstants.yenPerUnitLabel,

			// maximum annual fuel supply rate ___________________________  units / year
			GUIConstants.maximumAnnualFuelSupplyRateLabel, _maximumAnnualFlowTextField, GUIConstants.unitsPerYearLabel,

            // maximum hourly fuel supply rate ___________________________  units / hour
			GUIConstants.maximumHourlyFuelSupplyRateLabel, _maximumHourlyFlowTextField, GUIConstants.unitsPerHourLabel,

			// energetic value ________________________ Joules / unit
			GUIConstants.energeticValueLabel, _energeticValueTextField, GUIConstants.joulesPerUnitLabel,

			// fuel units _______________________
			GUIConstants.fuelUnitsLabel, _fuelUnitOptions,

			new JPanel()

		};

		GridBagConstraints[] gbcs = {

			// this._numberPowerGenerationTypes
			new GridBagConstraints(0, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 2, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 100, 0),
			new GridBagConstraints(3, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(4, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),

			// this._fuelTypesForPowerSource
			new GridBagConstraints(0, 1, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 20, 0, 20), 0, 0),

			// fuelAttributes
			new GridBagConstraints(0, 2, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			// fuelNameLabel, this._fuelNameTextField
			new GridBagConstraints(0, 3, 1, 1, 0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 20), 0, 0),

			// costOfFuelLabel, this._fuelCostTextField, yenPerUnitLabel,
			new GridBagConstraints(0, 4, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 4, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0 , 20), 0, 0),

			// maximumAnnualFuelSupplyRateLabel, this._maximumAnnualFlowTextField, unitsPerYearLabel,
			new GridBagConstraints(0, 5, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 5, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 5, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			// maximumHourlyFuelSupplyRateLabel, this._maximumHourlyFlowTextField, unitsPerHourLabel,
			new GridBagConstraints(0, 6, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 6, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			// energeticValueLabel, this._energeticValueTextField, joulesPerUnitLabel,
			new GridBagConstraints(0, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 7, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			// fuelUnitsLabel, this._fuelUnitOptions,
			new GridBagConstraints(0, 8, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 8, 4, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 20), 0, 0),

			// JPanel last spacer
			new GridBagConstraints(0, 9, 5, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)

		};

        GUIConstants.layoutGridBag(p, comps, gbcs);

        return p;

    }

    /**
     * get/set methods
     */

    public JTextField getNumberPowerSourceTextField()
    {
        return _numberPowerGenerationTypes;
    }

    public JTextField getPowerSourceNameTextField()
    {
        return _editablePowerSourceName;
    }

    public JComboBox getPowerSourceComboBox()
    {
        return _powerSourceComboBox;
    }

    public JComboBox getFuelComboBox()
    {
        return _fuelTypesComboBox;
    }

    public JComboBox getFuelForPowerSourceComboBox()
    {
        return _fuelTypesForPowerSource;
    }

    public JTextField getConversionEfficiencyTextField()
    {
        return _conversionEfficiencyTextField;
    }

    public JTextField getInstallationCostTextField()
    {
        return _installationCostTextField;
    }

    public JTextField getRunningCostTextField()
    {
        return _runningCostTextField;
    }

    public JTextField getIncreasingConstraintTextField()
    {
        return _increasingConstraintTextField;
    }

    public JTextField getDecreasingConstraintTextField()
    {
        return _decreasingConstraintTextField;
    }

    public JTextField getUpperLimitTextField()
    {
        return _upperLimitTextField;
    }

    public JTextField getLowerLimitTextField()
    {
        return _lowerLimitTextField;
    }

    public JTextField getInspectionRatioTextField()
    {
        return _inspectionRatioTextField;
    }

    public JTextField getDepreciationPeriodTextField()
    {
        return _depreciationPeriodTextField;
    }

    public JTextField getResidualValueTextField()
    {
        return _residualValueTextField;
    }

    public JLabel getLimitDoesNotExistLabel()
    {
        return _limitDoesNotExistLabel;
    }

    public JRadioButton getNoUpperLimitButton()
    {
        return _noUpperLimitButton;
    }

    public JTextField getNumberFuelTypeTextField()
    {
        return _numberFuelTypesTextField;
    }

    public JComboBox getFuelUnitOptions()
    {
        return _fuelUnitOptions;
    }

    public JTextField getFuelNameTextField()
    {
        return _fuelNameTextField;
    }

    public JTextField getCostOfFuelTextField()
    {
        return _fuelCostTextField;
    }

    public JTextField getAnnualFlowTextField()
    {
        return _maximumAnnualFlowTextField;
    }

    public JTextField getHourlyFlowTextField()
    {
        return _maximumHourlyFlowTextField;
    }

    public JTextField getEnergeticValueTextField()
    {
        return _energeticValueTextField;
    }


    /**
     * This method will replace the combo box selection
     * With the new name as soon as it changes.
     */
    public void swapPowerNameInComboBox()
	{
		int index = _powerSourceComboBox.getSelectedIndex();
		if(_powerSourceComboBox.getItemCount() == 1)
		{
			_powerSourceComboBox.removeAllItems();
			_powerSourceComboBox.addItem(_editablePowerSourceName.getText());
		}
		else
		{
			_powerSourceComboBox.insertItemAt(_editablePowerSourceName.getText(), index);
			_powerSourceComboBox.removeItemAt(index+1);
			_powerSourceComboBox.setSelectedIndex(index);
		}
	}

    public void swapFuelNameInComboBox()
	{
		int index = this._fuelTypesComboBox.getSelectedIndex();
		if(this._fuelTypesComboBox.getItemCount() == 1)
		{
			this._fuelTypesComboBox.removeAllItems();
			this._fuelTypesForPowerSource.removeAllItems();
			this._fuelTypesComboBox.addItem(this._fuelNameTextField.getText());
			this._fuelTypesForPowerSource.addItem(this._fuelNameTextField.getText());
		}
		else
		{
			this._fuelTypesComboBox.insertItemAt(this._fuelNameTextField.getText(), index);
			this._fuelTypesForPowerSource.insertItemAt(this._fuelNameTextField.getText(), index);
			this._fuelTypesComboBox.removeItemAt(index+1);
			this._fuelTypesForPowerSource.removeItemAt(index+1);
			this._fuelTypesComboBox.setSelectedIndex(index);
		}
	}

    public void takeNoFuelTypeAction()
    {
        if (_fuelTypesComboBox.getSelectedItem().equals(GUIConstants.NONE))
        {
            _fuelNameTextField.setText(GUIConstants.EMPTY_STRING);
            _fuelNameTextField.setEditable(false);
            _fuelCostTextField.setText(GUIConstants.EMPTY_STRING);
            _fuelCostTextField.setEditable(false);
            _maximumAnnualFlowTextField.setEditable(false);
            _maximumAnnualFlowTextField.setText(GUIConstants.EMPTY_STRING);
            _maximumHourlyFlowTextField.setEditable(false);
            _maximumHourlyFlowTextField.setText(GUIConstants.EMPTY_STRING);
            _energeticValueTextField.setEditable(false);
            _energeticValueTextField.setText(GUIConstants.EMPTY_STRING);
            _fuelUnitOptions.setEnabled(false);
        }
        else
        {
            _fuelNameTextField.setEditable(true);
            _fuelCostTextField.setEditable(true);
            _maximumAnnualFlowTextField.setEditable(true);
            _maximumHourlyFlowTextField.setEditable(true);
            _energeticValueTextField.setEditable(true);
            _fuelUnitOptions.setEnabled(true);
        }
    }

    /**
     *   PropertyChangeSupport methods
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        _listener.addPropertyChangeListener(propertyChangeListener);
    }

    public void addPropertyChangeListener(String s, PropertyChangeListener propertyChangeListener)
    {
        _listener.addPropertyChangeListener(s, propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        _listener.removePropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(String s, PropertyChangeListener propertyChangeListener)
    {
        _listener.removePropertyChangeListener(s, propertyChangeListener);
    }

}

