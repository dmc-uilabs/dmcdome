package DispatchModelGUIRenderer;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;

import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;

import java.awt.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import DispatchModelGUIRenderer.DispatchModelGUIPanels.EditableLineXYChartPanel;
import DispatchModelGUIRenderer.DispatchModelGUIPanels.InputDataSpecificationPanel;
import DispatchModelGUIRenderer.DispatchModelGUIPanels.PowerGenerationAndFuelInformationPanel;
import DispatchModelGUIRenderer.DispatchModelGUIPanels.ModelResultsPanel;


/**
 * Created by IntelliJ IDEA.
 * Name: DispatchModelGUIRenderer
 * User: jacob
 * Date: Jul 5, 2003
 * Time: 2:40:58 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class DispatchModelGUIRenderer
                            extends JPanel
{
	// JFreeChart properties
	public static final String MODEL_RESULTS = "model results";
	public static final String DATA_DEFINITION = "input data";
	public static final String POWER_FUEL_DEFINITION = "power and fuel type";

	// panel titles
	public static final String TABLE_DATA = "power demand for ";

	public static final String POWER_GENERATION = "power generation types";
	public static final String FUEL_TYPES = "fuel types";

	public static final String EMPTY_STRING = "";

	public static final Dimension EDITABLE_PANEL_SIZE = new Dimension(500, 375);

	public static final GridBagConstraints gbc = null;

	protected ModelInterfaceBase _iface;

	private JTabbedPane _customTabs;

	// editable chart class members
	private EditableLineXYChartPanel _editableChart;
	private PowerGenerationAndFuelInformationPanel _powerGenerationAndFuelInformationPanel;
    private InputDataSpecificationPanel _inputDataSpecificationPanel;
    private ModelResultsPanel _modelResultsPanel;

	private JPanel _editableChartPanel;
	private JPanel _dataInformationEditableChartComboBoxPanel;

	public DispatchModelGUIRenderer(ModelInterfaceBase iface)
	{
		_iface = iface;

		_dataInformationEditableChartComboBoxPanel = new JPanel();

		makeInputDataSpecificationPanel();
		makePowerGenerationAndFuelTypePanel();
		makeModelResultsPanel();

		this._customTabs = Templates.makeTabbedPane();
		this._customTabs.setTabPlacement(JTabbedPane.TOP);
		this._customTabs.addTab(DATA_DEFINITION, this._dataInformationEditableChartComboBoxPanel);
		this._customTabs.addTab(POWER_FUEL_DEFINITION, this._powerGenerationAndFuelInformationPanel);
		this._customTabs.addTab(MODEL_RESULTS, _modelResultsPanel);

		JComponent[] comps = {this._customTabs};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs =
		{
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(this, comps, gbcs);

	}

	protected void makeInputDataSpecificationPanel()
	{
		this._inputDataSpecificationPanel =  new InputDataSpecificationPanel(_iface);


		this._editableChart = new EditableLineXYChartPanel(this._inputDataSpecificationPanel.getCurrentDayDataObject().getXYSeriesCollection());
		this._editableChartPanel = this._editableChart.getChartPanel();
		this._editableChartPanel.setBorder(BorderFactory.createTitledBorder(
		        null, TABLE_DATA + this._inputDataSpecificationPanel.getCurrentDayDataObject().getDayName(), 0, 0, Templates.FONT11B));
		this._editableChartPanel.setMinimumSize(EDITABLE_PANEL_SIZE);

		this.layoutInputDataSpecificationPanel();
		this.registerEditableChartPanelListeners();
		this.registerInputDataSpecificationPanelListeners();
	}

	protected void makePowerGenerationAndFuelTypePanel()
	{
		this._powerGenerationAndFuelInformationPanel = new PowerGenerationAndFuelInformationPanel(_iface);
		this.registerPowerGenerationAndFuelInformationPanelListeners();
	}

    protected void makeModelResultsPanel()
    {
	    _modelResultsPanel = new ModelResultsPanel(_iface);
    }





	private void registerInputDataSpecificationPanelListeners()
	{
		NumberOfDaysHasChangedListener l = new NumberOfDaysHasChangedListener();
		this._inputDataSpecificationPanel.addPropertyChangeListener(InputDataSpecificationPanel.NUMBER_OF_DAYS_HAS_CHANGED, l);

		CurrentSelectedDayListener m = new CurrentSelectedDayListener();
		this._inputDataSpecificationPanel.addPropertyChangeListener(InputDataSpecificationPanel.EDITABLE_CHART_DATA_HAS_CHANGED, m);

		NameHasChangedListener n = new NameHasChangedListener();
		this._inputDataSpecificationPanel.addPropertyChangeListener(InputDataSpecificationPanel.DAY_NAME_TEXTFIELD_HAS_CHANGED, n);

		UpdateNumberOfDaysAccountedForListener o = new UpdateNumberOfDaysAccountedForListener();
		this._inputDataSpecificationPanel.addPropertyChangeListener(InputDataSpecificationPanel.UPDATE_NUMBER_OF_DAYS_ACCOUNTED_FOR, o);
	}

	// --------------- class listeners listening for property changes ---------------------------------

	// this listener is activated when a day is added, it will increment the number of days showed in the text field
	// it will also update the combo box to show the new available selection
	// it will also create a new DayDataObject to store the new data structure

	class NumberOfDaysHasChangedListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			_inputDataSpecificationPanel.getNumberOfDaysHolder().setText(
                    _inputDataSpecificationPanel.getNumberOfDays().toString());
			_inputDataSpecificationPanel.updateNumberOfDaysComboBox();
			_inputDataSpecificationPanel.updateNumberOfDaysList();
            _modelResultsPanel.updateDaySelectComboBox(
                    _inputDataSpecificationPanel.getDayNameComboBox().getItemCount(),
                        _inputDataSpecificationPanel.getDayNameComboBox().getSelectedIndex());
		}
	}

	class CurrentSelectedDayListener implements PropertyChangeListener
	{
        public void propertyChange(PropertyChangeEvent e)
        {
	        _editableChart = new EditableLineXYChartPanel(_inputDataSpecificationPanel.getCurrentDayDataObject().getXYSeriesCollection());
			_editableChartPanel = _editableChart.getChartPanel();
	        _editableChartPanel.setBorder(BorderFactory.createTitledBorder( null, TABLE_DATA +
                    _inputDataSpecificationPanel.getCurrentDayDataObject().getDayName(), 0, 0, Templates.FONT11B));
	        registerEditableChartPanelListeners();
	        layoutInputDataSpecificationPanel();
        }
	}

	class NameHasChangedListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			_inputDataSpecificationPanel.swapNameInComboBox();
            _modelResultsPanel.swapDaySelectComboBox(_inputDataSpecificationPanel.getDayNameComboBox().getSelectedIndex(),
                    _inputDataSpecificationPanel.getEditableNameField().getText());
		}
	}

	class UpdateNumberOfDaysAccountedForListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			DispatchModelGUIRenderer.this._inputDataSpecificationPanel.setNumberOfDaysInYearAccountedForLabel(
			        DispatchModelGUIRenderer.this._inputDataSpecificationPanel.getTotalNumberOfDaysAccountedFor().toString());
		}
	}

	private void registerEditableChartPanelListeners()
	{
		XYDataCollectionChangedListener z = new XYDataCollectionChangedListener();
		this._editableChart.addPropertyChangeListener(EditableLineXYChartPanel.XY_DATA_CHANGED, z);
	}

	// ---------- class listeners for property changes in the EditableLineXYChartPanel class -------------------

	class XYDataCollectionChangedListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			DispatchModelGUIRenderer.this._inputDataSpecificationPanel.getCurrentDayDataObject().updatePowerDemandData(
			                        DispatchModelGUIRenderer.this._editableChart.getActivePointX()-1,
			                                        DispatchModelGUIRenderer.this._editableChart.getActivePointY());
			DispatchModelGUIRenderer.this._inputDataSpecificationPanel.updatePowerDemandDataMatrix(
			            DispatchModelGUIRenderer.this._editableChart.getActivePointX(),
											  DispatchModelGUIRenderer.this._editableChart.getActivePointY());
		}
	}

	private void registerPowerGenerationAndFuelInformationPanelListeners()
	{
		NumberOfPowerSourcesListener a = new NumberOfPowerSourcesListener();
		this._powerGenerationAndFuelInformationPanel.addPropertyChangeListener(PowerGenerationAndFuelInformationPanel.NUMBER_OF_POWER_SOURCE_CHANGED, a);

		PowerSourceNameChangeListener b = new PowerSourceNameChangeListener();
		this._powerGenerationAndFuelInformationPanel.addPropertyChangeListener(PowerGenerationAndFuelInformationPanel.POWER_SOURCE_NAME_HAS_CHANGED, b);

		NumberOfFuelTypesListener d = new NumberOfFuelTypesListener();
		this._powerGenerationAndFuelInformationPanel.addPropertyChangeListener(PowerGenerationAndFuelInformationPanel.NUMBER_OF_FUEL_TYPES_CHANGED, d);

		FuelTypeNameChangeListener f = new FuelTypeNameChangeListener();
		this._powerGenerationAndFuelInformationPanel.addPropertyChangeListener(PowerGenerationAndFuelInformationPanel.FUEL_TYPE_NAME_HAS_CHANGED, f);

	}

	// -------- class listeners for property changes in the PowerGenerationAndFuelInformationPanel class ---------------

	class NumberOfPowerSourcesListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			_powerGenerationAndFuelInformationPanel.getNumberPowerSourcesTextField().
			        setText(_powerGenerationAndFuelInformationPanel.getNumberOfPowerSources().toString());
			_powerGenerationAndFuelInformationPanel.updateNumberOfPowerSourcesComboBox();
			_powerGenerationAndFuelInformationPanel.updatePowerGenerationTypesList();
		}
	}

	class PowerSourceNameChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			_powerGenerationAndFuelInformationPanel.swapPowerGenerationTypeNameInComboBox();
            _modelResultsPanel.swapPowerGenerationTypeNameInStackedAreaChart
                    (_powerGenerationAndFuelInformationPanel.getPowerGenerationTypesComboBox().getSelectedIndex(),
                            _powerGenerationAndFuelInformationPanel.getCurrentPowerGenerationTypeDataObject().getPowerGenerationTypeName());
		}
	}

	class NumberOfFuelTypesListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			_powerGenerationAndFuelInformationPanel.getNumberOfFuelTypesTextField().setText(
                    _powerGenerationAndFuelInformationPanel.getNumberOfFuelTypes().toString());
			_powerGenerationAndFuelInformationPanel.updateNumberOfFuelTypesComboBox();
			_powerGenerationAndFuelInformationPanel.updateFuelTypesList();
		}
	}

	class FuelTypeNameChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
            _powerGenerationAndFuelInformationPanel.swapFuelTypeNameInComboBox();
		}
	}
	// --------------- layouts for the different tab panels ---------------------------------

	protected void layoutInputDataSpecificationPanel()
	{
		this._dataInformationEditableChartComboBoxPanel.removeAll();

		JComponent[] comps =
		        {
			        // collected data for power generation demand and market data
			        this._inputDataSpecificationPanel,

			        // power generation demand chart
			        this._editableChartPanel,

		        };

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs =
		        {
			        // daySpecification
			        new GridBagConstraints(0, 0, 1, 1, 0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),

			        // chartPanel
			        new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),

		        };

		Templates.layoutGridBag(this._dataInformationEditableChartComboBoxPanel, comps, gbcs);
	}


}
