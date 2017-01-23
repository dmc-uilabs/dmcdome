package buildinglcagui.buildinglcaguipanels;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import buildinglcagui.BuildingLCAGUIConstants;

/**
 * Created by IntelliJ IDEA.
 * Name: BuildingLCAInputSpecificationPanel
 * User: jacob
 * Date: Aug 13, 2003
 * Time: 2:44:39 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class BuildingLCAInputSpecificationPanel extends JPanel
{
	private static final GridBagConstraints gbc = null;

	private JComboBox _buildingTypeComboBox, _constructionTypeComboBox, _baseTypeComboBox,
						_optionTypeComboBox, _componentTypeComboBox;

	private JTextField _floorAreaTextField, _evaluationPeriodTextField, _baseLifetimeTextField,
						   _modifiedLifetimeTextField, _baseTextField, _modifiedTextField, _baseEquipmentTextField,
								_modifiedEquipmentTextField, _baseLightingTextField, _modifiedLightingTextField,
									_areaType1TextField, _areaType2TextField;

	private JPanel _baseCharacteristicsPanel = new JPanel(),  _buildingConstructionInformationPanel = new JPanel(),
					_energyConsumptionCalculationPanel = new JPanel();

    private ModelInterfaceBase _iface;

	private Parameter _buildingTypeId, _constructionTypeId, _floorArea, _evaluationPeriod,
						_baseLifetime, _modifiedLifetime, _baseAirCirculation, _modifiedAirCirculation,
							_baseLighting, _modifiedLighting, _baseConstructionData, _modifiedConstructionData;

	public BuildingLCAInputSpecificationPanel(ModelInterfaceBase iface)
	{
		_iface = iface;

		initializeParameters();
    	makeComponents();
		layoutComponents();
		registerParameterListeners();
	}

	protected void initializeParameters()
	{
		_buildingTypeId = getParameterByName(BuildingLCAGUIConstants.BUILDING_TYPE_ID_PARAMETER);
		_constructionTypeId = getParameterByName(BuildingLCAGUIConstants.CONSTRUCTION_TYPE_ID_PARAMETER);
		_floorArea = getParameterByName(BuildingLCAGUIConstants.TOTAL_FLOOR_AREA_PARAMETER);
		_evaluationPeriod = getParameterByName(BuildingLCAGUIConstants.EVALUATION_PERIOD_PARAMETER);
		_baseLifetime = getParameterByName(BuildingLCAGUIConstants.BASE_DESIGN_LIFETIME_PARAMETER);
		_modifiedLifetime = getParameterByName(BuildingLCAGUIConstants.MODIFIED_DESIGN_LIFETIME_PARAMETER);
		_baseAirCirculation = getParameterByName(BuildingLCAGUIConstants.BASE_AIR_CIRCULATION_PARAMETER);
		_modifiedAirCirculation = getParameterByName(BuildingLCAGUIConstants.MODIFIED_AIR_CIRCULATION_PARAMETER);
		_baseLighting = getParameterByName(BuildingLCAGUIConstants.BASE_LIGHTING_PARAMETER);
		_modifiedLighting = getParameterByName(BuildingLCAGUIConstants.MODIFIED_LIGHTING_PARAMETER);
		_baseConstructionData = getParameterByName(BuildingLCAGUIConstants.BASE_CONSTRUCTION_DATA_PARAMETER);
		_modifiedConstructionData = getParameterByName(BuildingLCAGUIConstants.MODIFIED_CONSTRUCTION_DATA_PARAMETER);
	}

	protected void makeComponents()
	{
		makeBaseCharacteristicsPanel();
        makeBuildingConstructionInformationPanel();
        makeEnergyConsumptionCalculationPanel();
	}

	protected void registerParameterListeners()
	{
		_buildingTypeId.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealDataComboBoxListener(_buildingTypeComboBox));
		_constructionTypeId.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealDataComboBoxListener(_constructionTypeComboBox));
	}

	protected void layoutComponents()
	{
    	JComponent[] comps = {

		    _baseCharacteristicsPanel,

		    _buildingConstructionInformationPanel,

		    _energyConsumptionCalculationPanel
	    };

		GridBagConstraints[] gbcs = {

			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private void makeBaseCharacteristicsPanel()
	{
		_baseCharacteristicsPanel.setBorder(BorderFactory.
			createTitledBorder(null, BuildingLCAGUIConstants.BASE_CHARACTERISTICS_BORDER, 0, 0, Templates.FONT11B));

		_buildingTypeComboBox = Templates.makeComboBox(BuildingLCAGUIConstants.buildingTypeOptions);
		_buildingTypeComboBox.addActionListener(new RealComboBoxActionListener(_buildingTypeId, _buildingTypeComboBox));
		_constructionTypeComboBox = Templates.makeComboBox(BuildingLCAGUIConstants.constructionTypeOptions);
		_constructionTypeComboBox.addActionListener(new RealComboBoxActionListener(_constructionTypeId, _constructionTypeComboBox));

		_floorAreaTextField = makeParameterTextField(_floorArea, true);
		_evaluationPeriodTextField = makeParameterTextField(_evaluationPeriod, true);
		_baseLifetimeTextField = makeParameterTextField(_baseLifetime, true);
		_modifiedLifetimeTextField = makeParameterTextField(_modifiedLifetime, true);

		JComponent[] comps = {

			BuildingLCAGUIConstants.buildingTypeLabel,  _buildingTypeComboBox,

			BuildingLCAGUIConstants.constructionTypeLabel, _constructionTypeComboBox,

			BuildingLCAGUIConstants.floorAreaLabel, _floorAreaTextField, BuildingLCAGUIConstants.m2UnitLabel1,

            BuildingLCAGUIConstants.evaluationPeriodLabel, _evaluationPeriodTextField, BuildingLCAGUIConstants.yearUnitLabel1,

            BuildingLCAGUIConstants.baseLifetimeLabel, _baseLifetimeTextField, BuildingLCAGUIConstants.yearUnitLabel2,

            BuildingLCAGUIConstants.modifiedLifetimeLabel, _modifiedLifetimeTextField, BuildingLCAGUIConstants.yearUnitLabel3,

            new JPanel()

		};

		GridBagConstraints[] gbcs = {

			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 45), 0, 0),

			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 45), 0, 0),

			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			new GridBagConstraints(0, 6, 3, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(_baseCharacteristicsPanel, comps, gbcs);

	}

	private void makeBuildingConstructionInformationPanel()
	{
    	_buildingConstructionInformationPanel.setBorder
	            (BorderFactory.createTitledBorder(null,
						  BuildingLCAGUIConstants.BUILDING_CONSTRUCTION_INFORMATION_BORDER, 0, 0, Templates.FONT11B));

        _baseTypeComboBox = Templates.makeComboBox(BuildingLCAGUIConstants.componentTypeOptions);
		_baseTypeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				_optionTypeComboBox.setModel((ComboBoxModel)BuildingLCAGUIConstants.componentsComboBoxList.get(_baseTypeComboBox.getSelectedIndex()));
				_optionTypeComboBox.setSelectedIndex(0);
			}
		});

		_optionTypeComboBox = Templates.makeComboBox(BuildingLCAGUIConstants.engineeringOptionsComboBoxModel);
		_optionTypeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				_componentTypeComboBox.setModel((DefaultComboBoxModel)
				        ((List) BuildingLCAGUIConstants.comboBoxes.get(_baseTypeComboBox.
									getSelectedIndex())).get(_optionTypeComboBox.getSelectedIndex()));
				_componentTypeComboBox.setSelectedIndex(0);
			}
		});

		_componentTypeComboBox = Templates.makeComboBox(BuildingLCAGUIConstants.earthComponentsComboBoxModel);
		_componentTypeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				_baseTextField.setText(getMatrixElementValue((DomeMatrix)_baseConstructionData.getCurrentDataObject()).toString());
				_modifiedTextField.setText(getMatrixElementValue((DomeMatrix)_modifiedConstructionData.getCurrentDataObject()).toString());
			}
		});

		_baseTextField = makeParameterTextField(_baseConstructionData, true);
		_modifiedTextField = makeParameterTextField(_modifiedConstructionData, true);

		JComponent[] comps = {

			_baseTypeComboBox, _optionTypeComboBox, _componentTypeComboBox,

			BuildingLCAGUIConstants.baseLabel, _baseTextField, BuildingLCAGUIConstants.m2kgUnitLabel1,

			BuildingLCAGUIConstants.modifiedLabel, _modifiedTextField, BuildingLCAGUIConstants.m2kgUnitLabel2,

			new JPanel()
		};

		GridBagConstraints[] gbcs = {

			new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(3, 0, 3, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(6, 0, 3, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

			new GridBagConstraints(0, 1, 2, 1, 0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 1, 9, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(11, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			new GridBagConstraints(0, 2, 2, 1, 0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 2, 9, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(11, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			new GridBagConstraints(0, 3, 12, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(_buildingConstructionInformationPanel, comps, gbcs);
	}

	private void makeEnergyConsumptionCalculationPanel()
	{
    	_energyConsumptionCalculationPanel.setBorder(
	            BorderFactory.createTitledBorder(null,
						 BuildingLCAGUIConstants.ENERGY_CONSUMPTION_CALCULATION_BORDER, 0, 0, Templates.FONT11B));

        _baseEquipmentTextField = makeParameterTextField(_baseAirCirculation, true);
		_modifiedEquipmentTextField = makeParameterTextField(_modifiedAirCirculation, true);
		_baseLightingTextField = makeParameterTextField(_baseLighting, true);
		_modifiedLightingTextField = makeParameterTextField(_modifiedLighting, true);
        _areaType1TextField = Templates.makeTextField(BuildingLCAGUIConstants.EMPTY_STRING);
		_areaType2TextField = Templates.makeTextField(BuildingLCAGUIConstants.EMPTY_STRING);

        JComponent[] comps = {

        	BuildingLCAGUIConstants.baseAirEquipmentLabel, _baseEquipmentTextField,
					BuildingLCAGUIConstants.KwhPerYearUnitLabel1,

	        BuildingLCAGUIConstants.modifiedAirEquipmentLabel, _modifiedEquipmentTextField,
	                BuildingLCAGUIConstants.KwhPerYearUnitLabel2,

	        BuildingLCAGUIConstants.baseLightingLabel, _baseLightingTextField,
	                BuildingLCAGUIConstants.KwhPerYearUnitLabel3,

	        BuildingLCAGUIConstants.modifiedLightingLabel, _modifiedLightingTextField,
	                BuildingLCAGUIConstants.KwhPerYearUnitLabel4,

	        BuildingLCAGUIConstants.areaPerRoomType1Label, _areaType1TextField, BuildingLCAGUIConstants.m2UnitLabel4,

	        BuildingLCAGUIConstants.areaPerRoomType2Label, _areaType2TextField, BuildingLCAGUIConstants.m2UnitLabel5,

	        new JPanel()
        };

		GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(2, 4, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(2, 5, 1, 1, 0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 10), 0, 0),

            new GridBagConstraints(0, 6, 3, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(_energyConsumptionCalculationPanel, comps, gbcs);
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
	 * Makes textfield for parameter and registers listeners between parameter and textfield.
	 * @param p
	 * @param isEditable or in other words is it an input
	 * @return
	 */
	protected JTextField makeParameterTextField(Parameter p, boolean isEditable)
	{
		JTextField tf = new JTextField();
		p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
		tf.setEditable(isEditable);
		tf.setHorizontalAlignment(SwingConstants.RIGHT);
		if(p.getCurrentDataObject().getTypeName().equals(DomeReal.TYPE_INFO.getTypeName()))
		{
			tf.setText(getRealValue(p));
			tf.addActionListener(new RealTextFieldActionListener(p, tf));
			p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealDataTextFieldListener(tf));
		}
		else if(p.getCurrentDataObject().getTypeName().equals(DomeMatrix.TYPE_INFO.getTypeName()))
		{
			tf.setText(getMatrixElementValue((DomeMatrix)p.getCurrentDataObject()).toString());
			tf.addActionListener(new MatrixDataTextFieldActionListener(p, tf));
			p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new MatrixDataTextFieldListener(tf));
		}
		return tf;
	}

	class RealTextFieldActionListener implements ActionListener
	{
		JTextField txtField;
		Parameter p;

		public RealTextFieldActionListener(Parameter p, JTextField txtField)
		{
			this.p = p;
			this.txtField = txtField;
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				double newValue = Double.parseDouble(txtField.getText());
				if(newValue < 0.0)
				{
					OneButton1Msg.showWarning(null, "warning", "number entered must be positive", "OK", new Dimension(150, 75));
					txtField.setText(new Double(0.0).toString());
				}
				else
					setRealValue(p, newValue);
			}
			catch (NumberFormatException ex)
			{
				OneButton1Msg.showWarning(null, "warning", "you must enter a real number", "OK", new Dimension(150, 75));
				txtField.setText(new Double(0.0).toString());
			}
		}
	}

	class MatrixDataTextFieldActionListener implements ActionListener
	{
		JTextField _txtField;
		Parameter _p;

		public MatrixDataTextFieldActionListener(Parameter p, JTextField txtField)
		{
			_p = p;
			_txtField = txtField;
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
            	Double newValue = new Double(_txtField.getText());
				if(newValue.doubleValue() < 0.0)
				{
					OneButton1Msg.showWarning(null, "warning", "number entered must be positive", "OK", new Dimension(150, 75));
					_txtField.setText(new Double(0.0).toString());
				}
				else
					setMatrixElementValue(_p, newValue);
			}
			catch (NumberFormatException ex)
			{
				OneButton1Msg.showWarning(null, "warning", "you must enter a real number", "OK", new Dimension(150, 75));
				_txtField.setText(new Double(0.0).toString());
			}
		}
	}

	public void setMatrixElementValue(Parameter p, Double value)
	{
		((DomeMatrix)p.getCurrentDataObject()).setItem(calculateCurrentIndex(), 0, value);
	}

	public Double getMatrixElementValue(DomeMatrix matrix)
	{
		return (Double)matrix.getItem(calculateCurrentIndex(), 0);
	}

	public int calculateCurrentIndex()
	{
		int secondIndex = _optionTypeComboBox.getSelectedIndex();
		switch(_baseTypeComboBox.getSelectedIndex())
		{
			case 0:
				if(secondIndex == 0)
					return 0;
				else
					return _componentTypeComboBox.getSelectedIndex() + 1;
			case 1:
				return _componentTypeComboBox.getSelectedIndex() + 4;
			case 2:
				if (_optionTypeComboBox.getSelectedIndex() == 0)
					return _componentTypeComboBox.getSelectedIndex() + 9;
				else if (_optionTypeComboBox.getSelectedIndex() == 1)
					return _componentTypeComboBox.getSelectedIndex() + 14;
				else
					return _componentTypeComboBox.getSelectedIndex() + 19;
			case 3:
				if (_optionTypeComboBox.getSelectedIndex() == 0)
					return _componentTypeComboBox.getSelectedIndex() + 22;
				else if (_optionTypeComboBox.getSelectedIndex() == 1)
					return _componentTypeComboBox.getSelectedIndex() + 30;
				else if (_optionTypeComboBox.getSelectedIndex() == 2)
					return _componentTypeComboBox.getSelectedIndex() + 37;
				else if (_optionTypeComboBox.getSelectedIndex() == 3)
					return _componentTypeComboBox.getSelectedIndex() + 40;
				else
					return _componentTypeComboBox.getSelectedIndex() + 44;
		}
		return 0;
	}

	class RealComboBoxActionListener implements ActionListener
	{
		JComboBox _comboBox;
		Parameter _p;

		public RealComboBoxActionListener(Parameter p, JComboBox comboBox)
		{
			_comboBox = comboBox;
			_p = p;
		}

		public void actionPerformed(ActionEvent e)
		{
			setRealValue(_p, _comboBox.getSelectedIndex());
		}
	}

	/**
	 * When value changes in parameter, value is set in textfield.
	 */
	class RealDataTextFieldListener implements PropertyChangeListener
	{
		JTextField txtField;

		public RealDataTextFieldListener(JTextField txtField)
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

	class MatrixDataTextFieldListener implements PropertyChangeListener
	{
		JTextField _txtField;

		public MatrixDataTextFieldListener(JTextField txtField)
		{
			_txtField = txtField;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
            if(evt.getPropertyName().equals(DataObject.VALUE))
            {
	            _txtField.setText(getMatrixElementValue((DomeMatrix)evt.getSource()).toString());
            }
		}
	}

	/**
	 ** this class is a listener for parameters that control the combo box selections
	 **/
	class RealDataComboBoxListener implements PropertyChangeListener
	{
		JComboBox _comboBox;

		public RealDataComboBoxListener(JComboBox comboBox)
		{
			_comboBox = comboBox;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if(evt.getPropertyName().equals(DataObject.VALUE))
			{
				Double value = (Double)evt.getNewValue();
				if(value.intValue() < 0.0 || value.intValue() > _comboBox.getItemCount())
				{
					OneButton1Msg.showWarning(null, "warning", "you are attempting to set a value that does not exist", "OK", new Dimension(150, 75));
				}
				else
					_comboBox.setSelectedIndex(value.intValue());
			}
		}
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

	protected void setRealValue(Parameter p, double value)
	{
		((DomeReal) p.getCurrentDataObject()).setValue(value);
	}

	protected String getRealValue(Parameter p)
	{
		return Double.toString(((DomeReal) p.getCurrentDataObject()).getValue());
	}





}
