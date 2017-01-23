package DispatchModelGUIRenderer.DispatchModelGUIPanels;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Vector;

import DispatchModelGUIRenderer.DispatchModelGUIDataObjects.PowerGenerationTypeDataObject;
import DispatchModelGUIRenderer.DispatchModelGUIDataObjects.FuelTypeDataObject;

/**
 * Created by IntelliJ IDEA.
 * Name: PowerGenerationAndFuelInformationPanel
 * User: jacob
 * Date: Jul 26, 2003
 * Time: 2:39:48 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class PowerGenerationAndFuelInformationPanel extends JPanel
{
    public GridBagConstraints gbc = null;

	public static final String POWER_GENERATION = "power generation types";
	public static final String FUEL_TYPES = "fuel types";

	public static Integer DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES = new Integer(11);

    public static final String POWER_GENERATION_DESCRIPTION = "description: ";
	public static final String NUMBER_OF_POWER_SOURCES = "number of power generation types ";
	public static final String POWER_GENERATION_TYPE_ATTRIBUTES = "attributes for ";
	public static final String FUEL_TYPE_POWER_SOURCE = "fuel type ";
	public static final String COMBO_BOX_CHOICE_DEFAULT = "power generation type ";
	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	public static final String NAME = "name: ";
	public static final String EMPTY_STRING = "";
	public static final String CONVERSION_EFFICIENCY = "conversion efficiency ";
	public static final String PERCENT = "%";
	public static final String INSTALLATION_COST = "installation cost / kW capacity ";
	public static final String YEN_PER_KW = "Yen / kW";
	public static final String RUNNING_COST = "running cost / kWh of power generation";
	public static final String YEN_PER_KWH = "Yen / kWh";
	public static final String LOAD_FOLLOWING_CHARACTERISTICS = "load following characteristics: ";
	public static final String INCREASING_CONSTRAINT = "increasing constraint ";
	public static final String DECREASING_CONSTRAINT = "decreasing constraint ";
	public static final String CAPACITY = "capacity: ";
	public static final String UPPER_LIMIT = "upper limit";
	public static final String LOWER_LIMIT = "lower limit";
	public static final String LIMIT_DOES_NOT_EXIST = "limit does not exist";
	public static final String GW = "GW";
	public static final String ENERGY_PLAN_PARAMETERS = "energy plan parameters for each centralized power source: ";
	public static final String INSPECTION_RATIO = "inspection ratio";
    public static final String DEPRECIATION_PERIOD = "depreciation period";
	public static final String YEARS = "years";
    public static final String RESIDUAL_VALUE = "residual value";

	// fuel types constants
	public static final String NUMBER_FUEL_TYPES = "number of fuel types";
	public static final String FUEL_ATTRIBUTES_FOR = "attributes for ";
	public static final String COST_OF_FUEL = "cost of fuel";
	public static final String YEN_PER_UNIT = "yen / unit";
	public static final String MAXIMUM_ANNUAL_FUEL_RATE = "maximum annual fuel supply rate";
	public static final String UNITS_PER_YEAR = "units / year";
	public static final String MAXIMUM_HOURLY_FUEL_RATE = "maximum hourly fuel supply rate";
	public static final String UNITS_PER_HOUR = "units / hour";
    public static final String ENERGETIC_VALUE = "energetic value";
	public static final String JOULES_PER_UNIT = "Joules / unit";
    public static final String FUEL_UNITS = "fuel unit";
	public static final String LITERS = "liters";
	public static final String TONS = "tons";
	public static final String CUBIC_METERS = "cubic meters";

	public static final String[] fuelUnitOptions = {
													LITERS,
													TONS,
													CUBIC_METERS
	};

	// types of fuel
	public static final String NONE = "no fuel type";
	public static final String URANIUM = "uranium";
	public static final String COAL = "coal";
	public static final String OIL = "oil";
	public static final String HYDROGEN = "hydrogen";

	public static final String[] fuelTypeOptions = {

		NONE,
		URANIUM,
		COAL,
		OIL,
		HYDROGEN

	};

	public static Integer DEFAULT_NUMBER_OF_FUEL_TYPES = new Integer(fuelTypeOptions.length);

	// property change support constants
	public static final String NUMBER_OF_POWER_SOURCE_CHANGED = "number of power sources changed";
	public static final String NUMBER_OF_FUEL_TYPES_CHANGED = "number of fuel types changed";
	public static final String POWER_SOURCE_NAME_HAS_CHANGED = "power source name has changed";
	public static final String FUEL_TYPE_NAME_HAS_CHANGED = "fuel type name has changed";
	public static final String POWER_SOURCE_COMBO_BOX_SELECTION_CHANGED = "power source combo box selection has changed";
	public static final String FUEL_TYPE_COMBO_BOX_SELECTION_CHANGED = "fuel type combo box selection has changed";

	// parameter names
	public static final String CONVERSION_EFFICIENCY_PARAMETER = "conversion efficiency";
	public static final String INSTALLATION_COST_PARAMETER = "installation cost";
	public static final String RUNNING_COST_PARAMETER = "running cost";
	public static final String INCREASING_CONSTRAINT_PARAMETER = "increasing constraint";
	public static final String DECREASING_CONSTRAINT_PARAMETER = "decreasing constraint";
	public static final String UPPER_LIMIT_PARAMETER = "upper limit";
	public static final String LOWER_LIMIT_PARAMETER = "lower limit";
	public static final String INSPECTION_RATIO_PARAMETER = "inspection ratio";
	public static final String DEPRECIATION_PERIOD_PARAMETER = "depreciation period";
	public static final String RESIDUAL_VALUE_PARAMETER = "residual value";
	public static final String FUEL_COST_PARAMETER = "fuel cost";
	public static final String MAXIMUM_ANNUAL_FLOW_PARAMETER = "maximum annual flow";
	public static final String MAXIMUM_HOURLY_FLOW_PARAMETER = "maximum hourly flow";
	public static final String ENERGETIC_VALUE_PARAMETER = "energetic value";
	public static final String TYPE_OF_FUEL_FOR_POWER_GENERATION = "type of fuel for power generation";

	public PropertyChangeSupport _listeners;

	public static final int _ONE_ROW = 0;

	private static JLabel descriptionLabel = Templates.makeLabel(POWER_GENERATION_DESCRIPTION);
	private static JLabel numberPowerSourcesLabel = Templates.makeLabel(NUMBER_OF_POWER_SOURCES);
	private static JLabel powerGenerationNameLabel = Templates.makeLabel(NAME);
	private static JLabel fuelTypeForPowerSourceLabel = Templates.makeLabel(FUEL_TYPE_POWER_SOURCE);
	private static JLabel conversionEfficiencyLabel = Templates.makeLabel(CONVERSION_EFFICIENCY);
	private static JLabel percentLabel = Templates.makeLabel(PERCENT);
	private static JLabel installationCostLabel = Templates.makeLabel(INSTALLATION_COST);
	private static JLabel yenPerKw = Templates.makeLabel(YEN_PER_KW);
	private static JLabel runningCostLabel = Templates.makeLabel(RUNNING_COST);
	private static JLabel yenPerKwh = Templates.makeLabel(YEN_PER_KWH);
	private static JLabel loadFollowingCharacteristicsLabel = Templates.makeLabel(LOAD_FOLLOWING_CHARACTERISTICS);
	private static JLabel increasingConstraintLabel = Templates.makeLabel(INCREASING_CONSTRAINT);
	private static JLabel decreasingConstraintLabel = Templates.makeLabel(DECREASING_CONSTRAINT);
	private static JLabel capacityLabel = Templates.makeLabel(CAPACITY);
	private static JLabel upperLimitLabel = Templates.makeLabel(UPPER_LIMIT);
	private static JLabel lowerLimitLabel = Templates.makeLabel(LOWER_LIMIT);
	private static JLabel gwUpperLabel = Templates.makeLabel(GW);
	private static JLabel gwLowerLabel = Templates.makeLabel(GW);
	private static JLabel energyPlanLabel = Templates.makeLabel(ENERGY_PLAN_PARAMETERS);
	private static JLabel inspectionRatioLabel = Templates.makeLabel(INSPECTION_RATIO);
	private static JLabel inspectionPercentLabel = Templates.makeLabel(PERCENT);
	private static JLabel depreciationLabel = Templates.makeLabel(DEPRECIATION_PERIOD);
	private static JLabel yearsLabel = Templates.makeLabel(YEARS);
	private static JLabel residualValueLabel = Templates.makeLabel(RESIDUAL_VALUE);
	private static JLabel residualPercentLabel = Templates.makeLabel(PERCENT);


	// fuel types labels
	private static JLabel numberFuelTypesLabel = Templates.makeLabel(NUMBER_FUEL_TYPES);
	private static JLabel fuelNameLabel = Templates.makeLabel(NAME);
	private static JLabel costOfFuelLabel = Templates.makeLabel(COST_OF_FUEL);
	private static JLabel yenPerUnitLabel = Templates.makeLabel(YEN_PER_UNIT);
	private static JLabel maximumAnnualFuelSupplyRateLabel = Templates.makeLabel(MAXIMUM_ANNUAL_FUEL_RATE);
	private static JLabel unitsPerYearLabel = Templates.makeLabel(UNITS_PER_YEAR);
    private static JLabel maximumHourlyFuelSupplyRateLabel = Templates.makeLabel(MAXIMUM_HOURLY_FUEL_RATE);
	private static JLabel unitsPerHourLabel = Templates.makeLabel(UNITS_PER_HOUR);
	private static JLabel energeticValueLabel = Templates.makeLabel(ENERGETIC_VALUE);
	private static JLabel joulesPerUnitLabel = Templates.makeLabel(JOULES_PER_UNIT);
	private static JLabel fuelUnitsLabel = Templates.makeLabel(FUEL_UNITS);

	private JLabel _attributesForPowerTypeLabel = Templates.makeLabel(POWER_GENERATION_TYPE_ATTRIBUTES);
	private JLabel _attributesForFuelTypeLabel = Templates.makeLabel(FUEL_ATTRIBUTES_FOR);
	private JLabel _limitDoesNotExistLabel = Templates.makeLabel(LIMIT_DOES_NOT_EXIST);

	private Integer _powerGenerationTypesCount;
	private Integer _fuelTypeCount;

	private Integer _powerGenerationTypeListItemToRemove;
	private Integer _fuelTypeListItemToRemove;

	private JTextField _numberPowerGenerationTypes, _editablePowerGenerationSourceName,
				            _conversionEfficiencyTextField, _installationCostTextField, _runningCostTextField,
								_increasingConstraintTextField, _decreasingConstraintTextField, _upperLimitTextField, _lowerLimitTextField,
									 _inspectionRatioTextField, _depreciationPeriodTextField, _residualValueTextField, _numberFuelTypes, _fuelNameTextField,
										_fuelCostTextField, _maximumAnnualFlowTextField, _maximumHourlyFlowTextField, _energeticValueTextField;

	private JButton _addPowerSource, _removePowerSource, _addFuelType, _removeFuelType;

	private JComboBox _powerGenerationTypesComboBox, _fuelTypesForPowerSource, _fuelTypesComboBox, _fuelUnitOptions;

	private JRadioButton _noUpperLimitButton;

	private List _powerGenerationList = new ArrayList();

	private List _fuelList = new ArrayList();

	private Integer _comboBoxPowerGenerationNameUniqueTag;
	private Integer _comboBoxFuelTypeNameUniqueTag;

	private ModelInterfaceBase _iface;

	private Parameter _conversionEfficiency, _installationCost,
						_runningCost, _increasingConstraint, _decreasingConstraint,
							_upperLimit, _lowerLimit, _inspectionRatio, _depreciationPeriod,
								_residualValue, _fuelCost, _maximumAnnualFlow, _maximumHourlyFlow, _energeticValue,
									_typeOfFuelForPowerGeneration;

    private boolean _comingFromComboBox = false;

	public PowerGenerationAndFuelInformationPanel(ModelInterfaceBase iface)
    {
	    _iface = iface;

	    initializeParameters();

		makePowerGenerationTypesComponents();
	    makeFuelTypeComponents();

	    makePowerGenerationAndFuelInformationPanel();

	    this._listeners = new PropertyChangeSupport(this);
    }

	protected void initializeParameters()
	{
		_conversionEfficiency = getParameterByName(CONVERSION_EFFICIENCY_PARAMETER);
		_installationCost = getParameterByName(INSTALLATION_COST_PARAMETER);
		_runningCost = getParameterByName(RUNNING_COST_PARAMETER);
		_increasingConstraint = getParameterByName(INCREASING_CONSTRAINT_PARAMETER);
		_decreasingConstraint = getParameterByName(DECREASING_CONSTRAINT_PARAMETER);
		_upperLimit = getParameterByName(UPPER_LIMIT_PARAMETER);
		_lowerLimit = getParameterByName(LOWER_LIMIT_PARAMETER);
		_inspectionRatio = getParameterByName(INSPECTION_RATIO_PARAMETER);
		_depreciationPeriod = getParameterByName(DEPRECIATION_PERIOD_PARAMETER);
		_residualValue = getParameterByName(RESIDUAL_VALUE_PARAMETER);
		_fuelCost = getParameterByName(FUEL_COST_PARAMETER);
		_maximumAnnualFlow = getParameterByName(MAXIMUM_ANNUAL_FLOW_PARAMETER);
		_maximumHourlyFlow = getParameterByName(MAXIMUM_HOURLY_FLOW_PARAMETER);
		_energeticValue = getParameterByName(ENERGETIC_VALUE_PARAMETER);
		_typeOfFuelForPowerGeneration = getParameterByName(TYPE_OF_FUEL_FOR_POWER_GENERATION);
	}

	protected void makePowerGenerationTypesComponents()
	{
		_powerGenerationTypesCount = DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES;
		_comboBoxPowerGenerationNameUniqueTag = DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES;
		_attributesForPowerTypeLabel.setText(POWER_GENERATION_TYPE_ATTRIBUTES + ModelResultsPanel.seriesNames[0]+ ":");

		_numberPowerGenerationTypes = Templates.makeTextField(DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES.toString());
		_numberPowerGenerationTypes.setEditable(false);
		_numberPowerGenerationTypes.setHorizontalAlignment(SwingConstants.RIGHT);

		_addPowerSource = Templates.makeButton(ADD);
		_addPowerSource.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PowerGenerationAndFuelInformationPanel.this.addPowerSource();
			}
		});
		this._removePowerSource = Templates.makeButton(REMOVE);
		this._removePowerSource.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PowerGenerationAndFuelInformationPanel.this.removePowerSource();
			}
		});

		this._powerGenerationTypesComboBox = Templates.makeComboBox(ModelResultsPanel.seriesNames);
		this._powerGenerationTypesComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                bringCurrentPowerGenerationTypeDataObjectToFront();
			}
		});

		for (int i = 0; i < DEFAULT_NUMBER_OF_POWER_GENERATION_TYPES.intValue(); i++)
			this._powerGenerationList.add(i, new PowerGenerationTypeDataObject((String)_powerGenerationTypesComboBox.getItemAt(i)));

		this._editablePowerGenerationSourceName = Templates.makeTextField(this.getCurrentPowerGenerationTypeDataObject().getPowerGenerationTypeName());
		this._editablePowerGenerationSourceName.setHorizontalAlignment(SwingConstants.RIGHT);
		this._editablePowerGenerationSourceName.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if(!_editablePowerGenerationSourceName.getText().equals(getCurrentPowerGenerationTypeDataObject().getPowerGenerationTypeName()))
					{
						getCurrentPowerGenerationTypeDataObject().setPowerGenerationTypeName(_editablePowerGenerationSourceName.getText());
						powerGenerationSourceNameChanged();
					}
				}
			}

			public void keyReleased(KeyEvent e){}

			public void keyTyped(KeyEvent e) {}

		});

		_comboBoxFuelTypeNameUniqueTag = DEFAULT_NUMBER_OF_FUEL_TYPES;

		_typeOfFuelForPowerGeneration.getCurrentDataObject().
		        addPropertyChangeListener(DataObject.VALUE, new FuelTypeForPowerGenerationListener(_typeOfFuelForPowerGeneration));

		createFuelTypeForPowerSourceSelectionComboBox();

		this._conversionEfficiencyTextField = makeParameterTextField(_conversionEfficiency, true);
		this._conversionEfficiencyTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._installationCostTextField = makeParameterTextField(_installationCost, true);
		this._installationCostTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._runningCostTextField = makeParameterTextField(_runningCost, true);
		this._runningCostTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._increasingConstraintTextField = makeParameterTextField(_increasingConstraint, true);
		this._increasingConstraintTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._decreasingConstraintTextField = makeParameterTextField(_decreasingConstraint, true);
		this._decreasingConstraintTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._limitDoesNotExistLabel.setEnabled(false);

		this._upperLimitTextField = makeParameterTextField(_upperLimit, true);
		this._upperLimitTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._lowerLimitTextField = makeParameterTextField(_lowerLimit, true);
		this._lowerLimitTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._noUpperLimitButton = Templates.makeRadioButton();
        this._noUpperLimitButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent e)
	        {
		        if(((JRadioButton)e.getSource()).isSelected())
		        {
                    PowerGenerationAndFuelInformationPanel.this._limitDoesNotExistLabel.setEnabled(true);
			        PowerGenerationAndFuelInformationPanel.this._upperLimitTextField.setText(EMPTY_STRING);
			        PowerGenerationAndFuelInformationPanel.this._upperLimitTextField.setEditable(false);
			        setMatrixElementValue(_upperLimit, new Double(10E09));
		        }
		        else
		        {
			        PowerGenerationAndFuelInformationPanel.this._limitDoesNotExistLabel.setEnabled(false);
			        PowerGenerationAndFuelInformationPanel.this._upperLimitTextField.setEditable(true);
			        setMatrixElementValue(_upperLimit, new Double(0.0));
		            PowerGenerationAndFuelInformationPanel.this._upperLimitTextField.setText(getMatrixElementValue(_upperLimit).toString());
		        }

	        }
        });

		this._inspectionRatioTextField = makeParameterTextField(_inspectionRatio, true);
		this._inspectionRatioTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._depreciationPeriodTextField = makeParameterTextField(_depreciationPeriod, true);
		this._depreciationPeriodTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		this._residualValueTextField = makeParameterTextField(_residualValue, true);
		this._residualValueTextField.setHorizontalAlignment(SwingConstants.RIGHT);

	}

	protected void makeFuelTypeComponents()
	{
		this._fuelTypeCount = DEFAULT_NUMBER_OF_FUEL_TYPES;
		for(int i=0; i < _fuelTypeCount.intValue(); i++)
			_fuelList.add(i, new FuelTypeDataObject(fuelTypeOptions[i]));
		this._numberFuelTypes = Templates.makeTextField(this._fuelTypeCount.toString());
		this._numberFuelTypes.setEditable(false);
		this._numberFuelTypes.setHorizontalAlignment(SwingConstants.RIGHT);

		this._addFuelType = Templates.makeButton(ADD);
		this._addFuelType.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PowerGenerationAndFuelInformationPanel.this.addFuelType();
			}
		});

		this._removeFuelType = Templates.makeButton(REMOVE);
		this._removeFuelType.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeFuelType();
			}
		});

		createFuelTypeSelectionComboBox();

		_attributesForFuelTypeLabel.setText(FUEL_ATTRIBUTES_FOR + _fuelTypesComboBox.getItemAt(0) + ": ");

		this._fuelUnitOptions = Templates.makeComboBox(fuelUnitOptions);
		this._fuelUnitOptions.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				getCurrentFuelTypeDataObject().setFuelUnit((String)PowerGenerationAndFuelInformationPanel.this._fuelUnitOptions.getSelectedItem());
			}
		});

		this._fuelNameTextField = Templates.makeTextField(this.getCurrentFuelTypeDataObject().getFuelName());
		this._fuelNameTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        this._fuelNameTextField.addKeyListener(new KeyListener()
        {
	        public void keyPressed(KeyEvent e)
	        {
		        if (e.getKeyCode() == KeyEvent.VK_ENTER)
		        {
			        if (!_fuelNameTextField.getText().equals(getCurrentFuelTypeDataObject().getFuelName()))
			        {
				        getCurrentFuelTypeDataObject().setFuelName(_fuelNameTextField.getText());
				        fuelTypeNameChanged();
			        }
		        }
	        }

	        public void keyReleased(KeyEvent e){}

	        public void keyTyped(KeyEvent e) {}

		});

        _fuelCostTextField = Templates.makeTextField(getCurrentFuelTypeDataObject().getCostOfFuel().toString());
		_fuelCostTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        _fuelCostTextField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        Double newValue = Double.valueOf(_fuelCostTextField.getText());
                        if(newValue.doubleValue() < 0.0)
                            OneButton1Msg.showWarning(null, "warning", "cost of fuel must be greater than 0.0", "OK", new Dimension(150, 75));
                        else
                            getCurrentFuelTypeDataObject().setCostOfFuel(newValue);
                    }
                    catch (NumberFormatException exception)
                    {
                        OneButton1Msg.showWarning(null, "number format warning", "invalid value entered", "OK", new Dimension(150, 75));
                        _fuelCostTextField.setText(getCurrentFuelTypeDataObject().getCostOfFuel().toString());
                    }
                }
            }

            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });

        _maximumAnnualFlowTextField = Templates.makeTextField(getCurrentFuelTypeDataObject().getMaximumAnnualFlow().toString());
        _maximumAnnualFlowTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        _maximumAnnualFlowTextField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        Double newValue = Double.valueOf(_maximumAnnualFlowTextField.getText());
                        if(newValue.doubleValue() < 0.0)
                            OneButton1Msg.showWarning(null, "warning", "maximum annual flow must be greater than 0.0", "OK", new Dimension(150, 75));
                        else
                            getCurrentFuelTypeDataObject().setMaximumAnnualFlow(newValue);
                    }
                    catch (NumberFormatException exception)
                    {
                        OneButton1Msg.showWarning(null, "number format warning", "value entered must be real", "OK", new Dimension(150, 75));
                        _maximumAnnualFlowTextField.setText(getCurrentFuelTypeDataObject().getCostOfFuel().toString());
                    }
                }
            }

            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });

        _maximumHourlyFlowTextField = Templates.makeTextField(getCurrentFuelTypeDataObject().getMaximumHourlyFlow().toString());
		_maximumHourlyFlowTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        _maximumHourlyFlowTextField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        Double newValue = Double.valueOf(_maximumHourlyFlowTextField.getText());
                        if(newValue.doubleValue() < 0.0)
                            OneButton1Msg.showWarning(null, "warning", "maximum hourly flow must be greater than 0.0", "OK", new Dimension(150, 75));
                        else
                            getCurrentFuelTypeDataObject().setMaximumHourlyFlow(newValue);
                    }
                    catch(NumberFormatException excpetion)
                    {
                        OneButton1Msg.showWarning(null, "number format warning", "value entered must be a real", "OK", new Dimension(150, 75));
                        _maximumHourlyFlowTextField.setText(getCurrentFuelTypeDataObject().getCostOfFuel().toString());
                    }
                }
            }

            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });

        _energeticValueTextField = Templates.makeTextField(getCurrentFuelTypeDataObject().getEnergeticValue().toString());
		_energeticValueTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        _energeticValueTextField.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        Double newValue = Double.valueOf(_energeticValueTextField.getText());
                        if(newValue.doubleValue() < 0.0)
                            OneButton1Msg.showWarning(null, "warning", "energetic value must be greater than 0.0", "OK", new Dimension(150, 75));
                        else
                            getCurrentFuelTypeDataObject().setEnergeticValue(newValue);
                    }
                    catch(NumberFormatException excpetion)
                    {
                        OneButton1Msg.showWarning(null, "number format warning", "value entered must be a real", "OK", new Dimension(150, 75));
                        _energeticValueTextField.setText(getCurrentFuelTypeDataObject().getEnergeticValue().toString());
                    }
                }
            }
            public void keyReleased(KeyEvent e){}
            public void keyTyped(KeyEvent e){}

        });

        takeNoFuelTypeAction();
	}

	protected void createFuelTypeForPowerSourceSelectionComboBox()
	{
		  this._fuelTypesForPowerSource = Templates.makeComboBox(fuelTypeOptions);
		_fuelTypesForPowerSource.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                _comingFromComboBox = true;
				setMatrixElementValue(_typeOfFuelForPowerGeneration, new Double(_fuelTypesForPowerSource.getSelectedIndex()));
			}
		});
	}

	protected void createFuelTypeSelectionComboBox()
	{
		_fuelTypesComboBox = Templates.makeComboBox(fuelTypeOptions);
		_fuelTypesComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                takeNoFuelTypeAction();
				bringCurrentFuelTypeDataObjectToFront();
			}
		});
	}

	protected void addPowerSource()
	{
		int i = this._powerGenerationTypesCount.intValue();
		this.setNumberOfPowerSources(new Integer(++i));
	}

	protected void removePowerSource()
	{
		int i = this._powerGenerationTypesCount.intValue();
		if(i == 1)
			OneButton1Msg.showWarning(null, "warning", "number of power sources must be at least 1", "OK", new Dimension(150,75));
		else
			this.setNumberOfPowerSources(new Integer(--i));
	}

	protected void addFuelType()
	{
        int i = this._fuelTypeCount.intValue();
		this.setNumberOfFuelTypes(new Integer(++i));
	}

	protected void removeFuelType()
	{
    	int i = this._fuelTypeCount.intValue();
		if(i == 1)
			OneButton1Msg.showWarning(null, "warning", "number of available fuels must be at least 1", "OK", new Dimension(150, 75));
		else
			this.setNumberOfFuelTypes(new Integer(--i));
	}

	public void powerGenerationSourceNameChanged()
	{
		this._listeners.firePropertyChange(POWER_SOURCE_NAME_HAS_CHANGED, null, this.getCurrentPowerGenerationTypeDataObject().getPowerGenerationTypeName());
	}

	public void fuelTypeNameChanged()
	{
		this._listeners.firePropertyChange(FUEL_TYPE_NAME_HAS_CHANGED, null, this.getCurrentFuelTypeDataObject().getFuelName());
	}

	public PowerGenerationTypeDataObject getCurrentPowerGenerationTypeDataObject()
	{
		return (PowerGenerationTypeDataObject) this._powerGenerationList.get(this._powerGenerationTypesComboBox.getSelectedIndex());
	}

	public FuelTypeDataObject getCurrentFuelTypeDataObject()
	{
		return (FuelTypeDataObject) this._fuelList.get(this._fuelTypesComboBox.getSelectedIndex());
	}

	public void setNumberOfPowerSources(Integer newValue)
	{
		Integer oldValue = this._powerGenerationTypesCount;
		this._powerGenerationTypesCount = newValue;
		this._listeners.firePropertyChange(NUMBER_OF_POWER_SOURCE_CHANGED, oldValue, this._powerGenerationTypesCount);
	}

	protected void setNumberOfFuelTypes(Integer newValue)
	{
		Integer oldValue = this._fuelTypeCount;
		this._fuelTypeCount = newValue;
		this._listeners.firePropertyChange(NUMBER_OF_FUEL_TYPES_CHANGED, oldValue, this._fuelTypeCount);
	}

	public Integer getNumberOfPowerSources()
	{
		return this._powerGenerationTypesCount;
	}

	public Integer getNumberOfFuelTypes()
	{
		return this._fuelTypeCount;
	}

	public void updateNumberOfPowerSourcesComboBox()
	{
    	if(this._powerGenerationTypesCount.intValue() > this._powerGenerationTypesComboBox.getItemCount())
	    {
			_comboBoxPowerGenerationNameUniqueTag = new Integer(_comboBoxPowerGenerationNameUniqueTag.intValue() + 1);
		    this._powerGenerationTypesComboBox.addItem(COMBO_BOX_CHOICE_DEFAULT + _comboBoxPowerGenerationNameUniqueTag.toString());
	    }
		else
	    {
		    this._powerGenerationTypeListItemToRemove = new Integer(this._powerGenerationTypesComboBox.getSelectedIndex());
		    this._powerGenerationTypesComboBox.removeItemAt(this._powerGenerationTypeListItemToRemove.intValue());
	    }
	}

	public void updatePowerGenerationTypesList()
	{
		if(this._powerGenerationTypesCount.intValue() > this._powerGenerationList.size())
			this._powerGenerationList.add(this._powerGenerationTypesCount.intValue() - 1,
			                              new PowerGenerationTypeDataObject(COMBO_BOX_CHOICE_DEFAULT + _comboBoxPowerGenerationNameUniqueTag.toString()));
		else if(this._powerGenerationTypesCount.intValue() < this._powerGenerationList.size())
			this._powerGenerationList.remove(this._powerGenerationTypeListItemToRemove.intValue());
		else
			return;
	}

	public void updateNumberOfFuelTypesComboBox()
	{
    	if(this._fuelTypeCount.intValue() > this._fuelTypesComboBox.getItemCount())
	    {
		    this._comboBoxFuelTypeNameUniqueTag = new Integer(this._comboBoxFuelTypeNameUniqueTag.intValue() + 1);
		    this._fuelTypesComboBox.addItem(FUEL_TYPE_POWER_SOURCE + _comboBoxFuelTypeNameUniqueTag.toString());
		    this._fuelTypesForPowerSource.addItem(FUEL_TYPE_POWER_SOURCE + _comboBoxFuelTypeNameUniqueTag.toString());
	    }
		else
	    {
		    this._fuelTypeListItemToRemove = new Integer(this._fuelTypesComboBox.getSelectedIndex());
		    this._fuelTypesComboBox.removeItemAt(this._fuelTypeListItemToRemove.intValue());
		    this._fuelTypesForPowerSource.removeItemAt(this._fuelTypeListItemToRemove.intValue());
	    }
	}

	public void updateFuelTypesList()
	{
		if(this._fuelTypeCount.intValue() > this._fuelList.size())
			this._fuelList.add(this._fuelTypeCount.intValue() - 1, new FuelTypeDataObject(FUEL_TYPE_POWER_SOURCE + _comboBoxFuelTypeNameUniqueTag.toString()));
		else if(this._fuelTypeCount.intValue() < this._fuelList.size())
			this._fuelList.remove(this._fuelTypeListItemToRemove.intValue());
		else
			return;
	}

	public JTextField getNumberPowerSourcesTextField()
	{
		return this._numberPowerGenerationTypes;
	}

	public JTextField getNumberOfFuelTypesTextField()
	{
		return this._numberFuelTypes;
	}

	public void swapPowerGenerationTypeNameInComboBox()
	{
		int index = this._powerGenerationTypesComboBox.getSelectedIndex();
		if (this._powerGenerationTypesComboBox.getItemCount() == 1)
		{
			this._powerGenerationTypesComboBox.removeAllItems();
			this._powerGenerationTypesComboBox.addItem(this._editablePowerGenerationSourceName.getText());
		}
		else
		{
			this._powerGenerationTypesComboBox.insertItemAt(this._editablePowerGenerationSourceName.getText(), index);
			this._powerGenerationTypesComboBox.removeItemAt(index+1);
			this._powerGenerationTypesComboBox.setSelectedIndex(index);
		}
	}

	public void swapFuelTypeNameInComboBox()
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
        if (_fuelTypesComboBox.getSelectedItem().equals(NONE))
        {
            _fuelNameTextField.setText(EMPTY_STRING);
            _fuelNameTextField.setEditable(false);
            _fuelCostTextField.setText(EMPTY_STRING);
            _fuelCostTextField.setEditable(false);
            _maximumAnnualFlowTextField.setEditable(false);
            _maximumAnnualFlowTextField.setText(EMPTY_STRING);
            _maximumHourlyFlowTextField.setEditable(false);
            _maximumHourlyFlowTextField.setText(EMPTY_STRING);
            _energeticValueTextField.setEditable(false);
            _energeticValueTextField.setText(EMPTY_STRING);
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

    public void bringCurrentPowerGenerationTypeDataObjectToFront()
	{
		// this method will populate the data objects in the gui with the current values in the DOME model
		// important:  the code here will work ONLY if the data in the DOME model is stored in a
		//  1 x n data matrix, where n is the number of power generation types !!!!

		int _FIRST_ROW = 0;

		PowerGenerationTypeDataObject pg = (PowerGenerationTypeDataObject) _powerGenerationList.get(_powerGenerationTypesComboBox.getSelectedIndex());
		_editablePowerGenerationSourceName.setText(pg.getPowerGenerationTypeName());

		_fuelTypesForPowerSource.setSelectedIndex(getMatrixElementValue(_typeOfFuelForPowerGeneration).intValue());

		_conversionEfficiencyTextField.setText(getMatrixElementValue(_conversionEfficiency).toString());

		_installationCostTextField.setText(getMatrixElementValue(_installationCost).toString());
		_runningCostTextField.setText(getMatrixElementValue(_runningCost).toString());
		_increasingConstraintTextField.setText(getMatrixElementValue(_increasingConstraint).toString());
		_decreasingConstraintTextField.setText(getMatrixElementValue(_decreasingConstraint).toString());

		if(((DomeMatrix) _upperLimit.getCurrentDataObject()).getItem(_FIRST_ROW, _powerGenerationTypesComboBox.getSelectedIndex()).doubleValue() == 10E09)
		{
			_upperLimitTextField.setText(EMPTY_STRING);
			_upperLimitTextField.setEditable(false);
			_limitDoesNotExistLabel.setEnabled(true);
			_noUpperLimitButton.setSelected(true);
		}
		else
		{
			_upperLimitTextField.setEditable(true);
			_upperLimitTextField.setText(getMatrixElementValue(_upperLimit).toString());
			_limitDoesNotExistLabel.setEnabled(false);
			_noUpperLimitButton.setSelected(false);
		}

		_lowerLimitTextField.setText(getMatrixElementValue(_lowerLimit).toString());
        _inspectionRatioTextField.setText(getMatrixElementValue(_inspectionRatio).toString());
		_depreciationPeriodTextField.setText(getMatrixElementValue(_depreciationPeriod).toString());
		_residualValueTextField.setText(getMatrixElementValue(_residualValue).toString());

		_attributesForPowerTypeLabel.setText(POWER_GENERATION_TYPE_ATTRIBUTES + pg.getPowerGenerationTypeName() + ": ");
	}

	public void bringCurrentFuelTypeDataObjectToFront()
	{
		FuelTypeDataObject ft = (FuelTypeDataObject) _fuelList.get(_fuelTypesComboBox.getSelectedIndex());
        _attributesForFuelTypeLabel.setText(FUEL_ATTRIBUTES_FOR + ft.getFuelName() + ": ");

        if((ft.getFuelName().equals(NONE)))
            return;
        else
        {
            _fuelNameTextField.setText(ft.getFuelName());
            for (int i = 0; i < _fuelUnitOptions.getItemCount(); i++)
            {
                if (((String) _fuelUnitOptions.getItemAt(i)).equals(ft.getFuelUnit()))
                {
                    _fuelUnitOptions.setSelectedIndex(i);
                    break;
                }
            }

            _fuelCostTextField.setText(ft.getCostOfFuel().toString());
            _maximumAnnualFlowTextField.setText(ft.getMaximumAnnualFlow().toString());
            _maximumHourlyFlowTextField.setText(ft.getMaximumHourlyFlow().toString());
            _energeticValueTextField.setText(ft.getEnergeticValue().toString());
        }
	}

	protected JPanel makePowerGenerationTypesPanel()
	{
		JPanel p = new JPanel();

		JComponent[] comps = {

			// description:
			descriptionLabel,

			// number of power sources  _____________ add  remove
			numberPowerSourcesLabel, _numberPowerGenerationTypes, _addPowerSource, _removePowerSource,

			// power generation types combo box
			_powerGenerationTypesComboBox,

			// JPanel spacer
			new JPanel(),

			// simple label for power generation attributes
            _attributesForPowerTypeLabel,

            // name: _________________________
            powerGenerationNameLabel, _editablePowerGenerationSourceName,

            // fuel type: ___________________
			fuelTypeForPowerSourceLabel, _fuelTypesForPowerSource,

            // efficiency _________________ %
            conversionEfficiencyLabel, _conversionEfficiencyTextField, percentLabel,

            // installation cost _________________________ Yen / kW
            installationCostLabel, _installationCostTextField, yenPerKw,

            runningCostLabel, _runningCostTextField, yenPerKwh,

            new JPanel(),

            loadFollowingCharacteristicsLabel,

			increasingConstraintLabel, _increasingConstraintTextField,

            decreasingConstraintLabel, _decreasingConstraintTextField,

            // JPanel spacer
            new JPanel(),

            // capacity
            capacityLabel,

            // upperLimit __________________ GW  (RB) limit does not exist
            upperLimitLabel, _upperLimitTextField, gwUpperLabel, _noUpperLimitButton, _limitDoesNotExistLabel,

            // lowerLimit __________________ GW
            lowerLimitLabel, this._lowerLimitTextField, gwLowerLabel,

			//JPanel spacer
            new JPanel(),

            //energy plan parameters for each centralized power source
			energyPlanLabel,

            //inspection ratio ________________ %
            inspectionRatioLabel, this._inspectionRatioTextField, inspectionPercentLabel,

            //depreciation period _______________ years
            depreciationLabel, this._depreciationPeriodTextField, yearsLabel,

            // residual value ___________________ %
            residualValueLabel, this._residualValueTextField, residualPercentLabel,

            new JPanel()

		};

		GridBagConstraints[] gbcs = {

			// description
			new GridBagConstraints(0, 0, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			// this._numberPowerGenerationTypes
			new GridBagConstraints(0, 1, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 2, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(3, 1, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(4, 1, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),

			// this._powerGenerationTypesComboBox
			new GridBagConstraints(0, 2, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 20, 0, 20), 0, 0),

			// JPanel spacer
			new GridBagConstraints(0, 3, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			// this._attributesForPowerTypeLabel
			new GridBagConstraints(0, 4, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			// this.powerGenerationNameLabel, this._editablePowerGenerationSourceName
			new GridBagConstraints(0, 5, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 5, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 20), 0, 0),

			// fuelTypeForPowerSourceLabel, this._fuelTypesForPowerSource,
			new GridBagConstraints(0, 6, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 20), 0, 0),

			// conversionEfficiencyLabel, this._conversionEfficiencyTextField, percentLabel,
			new GridBagConstraints(0, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 7, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			new GridBagConstraints(0, 8, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 8, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 8, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			new GridBagConstraints(0, 9, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 9, 3, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 9, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// JPanel spacer
			new GridBagConstraints(0, 10, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 11, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 12, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 12, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 20), 0, 0),

			new GridBagConstraints(0, 13, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 13, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 20), 0,0),

			// JPanel spacer
			new GridBagConstraints(0, 14, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			// capacity label
			new GridBagConstraints(0, 15, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			// upperLimitLabel, this._upperLimitTextField, gwLabel, this._noUpperLimitButton, _limitDoesNotExistLabel,
			new GridBagConstraints(0, 16, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 16, 1, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 16, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 16, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 25, 0, 0), 0, 0),
			new GridBagConstraints(4, 16, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 0, 0, 5), 0, 0),

			// lowerLimitLable, this._lowerLimitTextField, gwLabel
			new GridBagConstraints(0, 17, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 17, 1, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 17, 3, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// JPanel spacer
			new GridBagConstraints(0, 18, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			// energyPlanLabel
			new GridBagConstraints(0, 19, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0),

			//inspectionRatioLabel, this._inspectionRatioTextField, inspectionPercentLabel,
			new GridBagConstraints(0, 20, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 10, 0, 0),0 ,0),
			new GridBagConstraints(1, 20, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 20, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),

			// depreciationLabel, this._depreciationValue, yearsLabel,
			new GridBagConstraints(0, 21, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 21, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 21, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// residualValueLabel, this._residualValueTextField, residualPercentLabel,
			new GridBagConstraints(0, 22, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 22, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 22, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// JPanel last spacer
			new GridBagConstraints(0, 23, 5, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	protected JPanel makeFuelTypesPanel()
	{
		JPanel p = new JPanel();

		JComponent[] comps = {

			// fuel types _________________ add remove
			numberFuelTypesLabel, this._numberFuelTypes, this._addFuelType, this._removeFuelType,

			// JComboBox fuel types selection
			this._fuelTypesComboBox,

			// JPanel spacer
			new JPanel(),

			// fuel attributes:
			_attributesForFuelTypeLabel,

			// name _____________________
			fuelNameLabel, this._fuelNameTextField,

			// cost of fuel __________________ Yen / unit
			costOfFuelLabel, this._fuelCostTextField, yenPerUnitLabel,

			// maximum annual fuel supply rate ___________________________  units / year
			maximumAnnualFuelSupplyRateLabel, this._maximumAnnualFlowTextField, unitsPerYearLabel,

            // maximum hourly fuel supply rate ___________________________  units / hour
			maximumHourlyFuelSupplyRateLabel, this._maximumHourlyFlowTextField, unitsPerHourLabel,

			// energetic value ________________________ Joules / unit
			energeticValueLabel, this._energeticValueTextField, joulesPerUnitLabel,

			// fuel units _______________________
			fuelUnitsLabel, this._fuelUnitOptions,

			new JPanel()

		};

		GridBagConstraints[] gbcs = {

			// this._numberPowerGenerationTypes
			new GridBagConstraints(0, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 2, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0),
			new GridBagConstraints(3, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(4, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),

			// this._fuelTypesForPowerSource
			new GridBagConstraints(0, 1, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 20, 0, 20), 0, 0),

			// JPanel spacer
			new GridBagConstraints(0, 2, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			// fuelAttributes
			new GridBagConstraints(0, 3, 5, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),

			// fuelNameLabel, this._fuelNameTextField
			new GridBagConstraints(0, 4, 1, 1, 0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 4, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 20), 0, 0),

			// costOfFuelLabel, this._fuelCostTextField, yenPerUnitLabel,
			new GridBagConstraints(0, 5, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 5, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 5, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0 , 20), 0, 0),

			// maximumAnnualFuelSupplyRateLabel, this._maximumAnnualFlowTextField, unitsPerYearLabel,
			new GridBagConstraints(0, 6, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 6, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			// maximumHourlyFuelSupplyRateLabel, this._maximumHourlyFlowTextField, unitsPerHourLabel,
			new GridBagConstraints(0, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 7, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 7, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			// energeticValueLabel, this._energeticValueTextField, joulesPerUnitLabel,
			new GridBagConstraints(0, 8, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 8, 3, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(4, 8, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			// fuelUnitsLabel, this._fuelUnitOptions,
			new GridBagConstraints(0, 9, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 9, 4, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 20), 0, 0),

			// JPanel last spacer
			new GridBagConstraints(0, 10, 5, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)

		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	protected void makePowerGenerationAndFuelInformationPanel()
	{
		JPanel powerGenerationPanel = this.makePowerGenerationTypesPanel();
		powerGenerationPanel.setBorder(BorderFactory.createTitledBorder(null, POWER_GENERATION, 0, 0, Templates.FONT11B));

		JPanel fuelTypesPanel = this.makeFuelTypesPanel();
		fuelTypesPanel.setBorder(BorderFactory.createTitledBorder(null, FUEL_TYPES, 0, 0, Templates.FONT11B));

		JComponent[] comps = {powerGenerationPanel, fuelTypesPanel};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs =
		        {
			        // daySpecification
			        new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 0), 0, 0),

			        // tablePanel
			        new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 5), 0, 0)

		        };

		Templates.layoutGridBag(this, comps, gbcs);
	}

	public void addPropertyChangeListener(
	        PropertyChangeListener listener)
	{
		_listeners.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(
	        String propertyName,
	        PropertyChangeListener listener)
	{
		_listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(
					PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(
					String propertyName,
					PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Makes textfield for parameter and registers listeners between parameter and textfield.
	 * @param p
	 * @param isEditable or in other words is it an input
	 * @return
	 */
	protected JTextField makeParameterTextField(Parameter p, boolean isEditable)
	{
		JTextField tf = Templates.makeTextField(EMPTY_STRING);
		tf.setEditable(isEditable);

		p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));

		if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName()))
		{
			tf.setText(getMatrixElementValue(p).toString());
			tf.addActionListener(new MatrixElementTextFieldActionListener(p, tf));
			p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new MatrixElementDataListener(tf, p));
		}
		return tf;
	}

	protected class ParameterStatusChangeListener implements PropertyChangeListener
	{
		Parameter p;
		JComponent comp;

		public ParameterStatusChangeListener(Parameter p, JComponent comp)
		{
			this.p = p;
			this.comp = comp;
		}

		public void propertyChange(PropertyChangeEvent e)
		{
			if (e.getPropertyName().equals(Parameter.VALUE_STATUS))
			{
				String valueStatus = p.getValueStatus();
				if (Parameter.VALUE_STATUS_STALE.equals(valueStatus))
					comp.setBackground(Templates.STALE_COLOR);
				else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(valueStatus))
					comp.setBackground(Templates.INCONSISTENT_COLOR);
				else if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(valueStatus))
					comp.setBackground(Templates.WAITING_VALIDATION_COLOR);
				else if (Parameter.VALUE_STATUS_CONSISTENT.equals(valueStatus))
					comp.setBackground(Templates.CONSISTENT_COLOR);
			}
		}
	}


	class MatrixElementTextFieldActionListener implements ActionListener
	{

		JTextField _txtField;
		Parameter _p;

		public MatrixElementTextFieldActionListener(Parameter p, JTextField txtField)
		{
			_p = p;
			_txtField = txtField;
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Double newValue = Double.valueOf(_txtField.getText());
				if(_p.getName().equals(CONVERSION_EFFICIENCY_PARAMETER) || _p.getName().equals(INCREASING_CONSTRAINT_PARAMETER) || _p.getName().equals(DECREASING_CONSTRAINT_PARAMETER))
				{
					if (newValue.doubleValue() < 0 || newValue.doubleValue() > 1.0)
					{
						OneButton1Msg.showWarning(null, "warning", _p.getName() + " must be a value between 0 and 1.0", "OK", new Dimension(150, 75));
						_txtField.setText(getMatrixElementValue(_p).toString());
					}
					else
						setMatrixElementValue(_p, newValue);
				}
				else if(_p.getName().equals(INSTALLATION_COST_PARAMETER) || _p.getName().equals(RUNNING_COST_PARAMETER) || _p.getName().equals(UPPER_LIMIT_PARAMETER)
					|| _p.getName().equals(LOWER_LIMIT_PARAMETER) || _p.getName().equals(DEPRECIATION_PERIOD_PARAMETER))
				{
					if (newValue.doubleValue() < 0)
					{
						OneButton1Msg.showWarning(null, "warning", _p.getName() + " must be greater than 0", "OK", new Dimension(150, 75));
						_txtField.setText(getMatrixElementValue(_p).toString());

					}
					else
						setMatrixElementValue(_p, newValue);
				}
				else if(_p.getName().equals(INSPECTION_RATIO_PARAMETER) || _p.getName().equals(RESIDUAL_VALUE_PARAMETER))
				{
					if (newValue.doubleValue() < 0.0 || newValue.doubleValue() > 100.0)
					{
						OneButton1Msg.showWarning(null, "warning", _p.getName() + " must be a value between 0.0 and 100%", "OK", new Dimension(150, 75));
					}
					else
					{
						setMatrixElementValue(_p, newValue);
					}
				}
                else if(_p.getName().equals(COST_OF_FUEL))
                {
                    if(newValue.doubleValue() < 0.0)
                    {
                        OneButton1Msg.showWarning(null, "warning", "cost of fuel must be a value greater than 0.0", "OK", new Dimension(150, 75));
                        _txtField.setText(PowerGenerationAndFuelInformationPanel.this.getCurrentFuelTypeDataObject().getCostOfFuel().toString());
                    }
                    else
                    {
                        for (int i = 0; i < _powerGenerationList.size(); i++)
                        {
                            if (((DomeMatrix) _typeOfFuelForPowerGeneration.getCurrentDataObject()).getItem(0, i).intValue()
                                    == _fuelTypesComboBox.getSelectedIndex())
                            {
                                ((DomeMatrix) _p.getCurrentDataObject()).setItem(0, i, newValue);
                            }
                        }
                        getCurrentFuelTypeDataObject().setCostOfFuel(newValue);
                    }
                }
				else if(_p.getName().equals(MAXIMUM_ANNUAL_FLOW_PARAMETER))
				{
					if (newValue.doubleValue() < 0.0)
					{
						OneButton1Msg.showWarning(null, "warning", "annual flow must be a value greater than 0.0", "OK", new Dimension(150, 75));
						_txtField.setText(PowerGenerationAndFuelInformationPanel.this.getCurrentFuelTypeDataObject().getMaximumAnnualFlow().toString());
					}
					else
					{
                        for (int i = 0; i < _powerGenerationList.size(); i++)
                        {
                            if (((DomeMatrix) _typeOfFuelForPowerGeneration.getCurrentDataObject()).getItem(0, i).intValue()
                                    == _fuelTypesComboBox.getSelectedIndex())
                            {
                                ((DomeMatrix) _p.getCurrentDataObject()).setItem(0, i, newValue);
                            }
                        }
                        PowerGenerationAndFuelInformationPanel.this.getCurrentFuelTypeDataObject().setMaximumAnnualFlow(newValue);
					}
				}
				else if(_p.getName().equals(MAXIMUM_HOURLY_FLOW_PARAMETER))
				{
					if (newValue.doubleValue() < 0.0)
					{
						OneButton1Msg.showWarning(null, "warning", "hourly flow must be a value greater than 0.0", "OK", new Dimension(150, 75));
						_txtField.setText(PowerGenerationAndFuelInformationPanel.this.getCurrentFuelTypeDataObject().getMaximumHourlyFlow().toString());
					}
					else
					{
						for (int i = 0; i < _powerGenerationList.size(); i++)
                        {
                            if(((DomeMatrix)_typeOfFuelForPowerGeneration.getCurrentDataObject()).getItem(0, i).intValue()
                                                                                        == _fuelTypesComboBox.getSelectedIndex())
                            {
                                ((DomeMatrix)_p.getCurrentDataObject()).setItem(0, i, newValue);
                            }
                        }
						PowerGenerationAndFuelInformationPanel.this.getCurrentFuelTypeDataObject().setMaximumHourlyFlow(newValue);
					}
				}
				else if(_p.getName().equals(ENERGETIC_VALUE_PARAMETER))
				{
					if (newValue.doubleValue() < 0.0)
					{
						OneButton1Msg.showWarning(null, "warning", "energetic value must be a value greater than 0.0", "OK", new Dimension(150, 75));
						_txtField.setText(PowerGenerationAndFuelInformationPanel.this.getCurrentFuelTypeDataObject().getEnergeticValue().toString());
					}
					else
					{
						for (int i = 0; i < _powerGenerationList.size(); i++)
                        {
                            if(((DomeMatrix)_typeOfFuelForPowerGeneration.getCurrentDataObject()).getItem(0, i).intValue()
                                                                                        == _fuelTypesComboBox.getSelectedIndex())
                            {
                                ((DomeMatrix)_p.getCurrentDataObject()).setItem(0, i, newValue);
                            }
                        }
                        PowerGenerationAndFuelInformationPanel.this.getCurrentFuelTypeDataObject().setEnergeticValue(newValue);
					}
				}
				else
					OneButton1Msg.showWarning(null, "warning", "parameter does not exist in this model", "OK", new Dimension(150, 75));
			}
			catch (NumberFormatException exception)
			{
				OneButton1Msg.showWarning(null, "number format warning", "invalid value entered", "OK", new Dimension(150, 75));
				_txtField.setText(getMatrixElementValue(_p).toString());
			}
		}
	}

	class RealDataListener implements PropertyChangeListener
	{
		JTextField txtField;

		public RealDataListener(JTextField txtField)
		{
			this.txtField = txtField;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(DataObject.VALUE))
			{
				txtField.setText(evt.getNewValue().toString());
			}
		}
	}

	class FuelTypeForPowerGenerationListener implements PropertyChangeListener
	{
		Parameter _p;

		public FuelTypeForPowerGenerationListener(Parameter p)
		{
			_p = p;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
            if(evt.getPropertyName().equals(DataObject.VALUE))
            {
                if (_comingFromComboBox)
                {
                    FuelTypeDataObject ft = (FuelTypeDataObject) _fuelList.get(_fuelTypesForPowerSource.getSelectedIndex());
                    ((DomeMatrix) _fuelCost.getCurrentDataObject()).setItem(0, _powerGenerationTypesComboBox.getSelectedIndex(), ft.getCostOfFuel());
                    ((DomeMatrix) _maximumAnnualFlow.getCurrentDataObject()).setItem(0, _powerGenerationTypesComboBox.getSelectedIndex(), ft.getMaximumAnnualFlow());
                    ((DomeMatrix) _maximumHourlyFlow.getCurrentDataObject()).setItem(0, _powerGenerationTypesComboBox.getSelectedIndex(), ft.getMaximumHourlyFlow());
                    ((DomeMatrix) _energeticValue.getCurrentDataObject()).setItem(0, _powerGenerationTypesComboBox.getSelectedIndex(), ft.getEnergeticValue());
                    _comingFromComboBox = false;
                }
                else
                {
                }
            }
		}
	}

	class MatrixElementDataListener implements PropertyChangeListener
	{
		JTextField _txtField;
		Parameter _p;

		public MatrixElementDataListener(JTextField txtField, Parameter p)
		{
			_txtField = txtField;
			_p = p;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if(evt.getPropertyName().equals(DataObject.VALUE))
			{
				Double newValue = (Double) ((DomeMatrix) evt.getSource()).getItem(0, _powerGenerationTypesComboBox.getSelectedIndex());
				if(_p.getName().equals(UPPER_LIMIT_PARAMETER) && newValue.doubleValue() == 10E09)
				    _txtField.setText(EMPTY_STRING);
				else
					_txtField.setText(newValue.toString());
			}
		}
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

	protected void setRealValue(Parameter p, double value)
	{
		((DomeReal) p.getCurrentDataObject()).setValue(value);
	}

	protected String getRealValue(Parameter p)
	{
		return Double.toString(((DomeReal) p.getCurrentDataObject()).getValue());
	}

	protected Double getMatrixElementValue(Parameter p)
	{
		return new Double(((DomeMatrix)p.getCurrentDataObject()).getItem(_ONE_ROW, this._powerGenerationTypesComboBox.getSelectedIndex()).doubleValue());
	}

	protected void setMatrixElementValue(Parameter p, Double value)
	{
		((DomeMatrix)p.getCurrentDataObject()).setItem(_ONE_ROW, this._powerGenerationTypesComboBox.getSelectedIndex(), value);
	}

    public JComboBox getPowerGenerationTypesComboBox()
    {
        return _powerGenerationTypesComboBox;
    }

}
