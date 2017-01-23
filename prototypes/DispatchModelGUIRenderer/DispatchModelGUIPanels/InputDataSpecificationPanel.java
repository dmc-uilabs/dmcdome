package DispatchModelGUIRenderer.DispatchModelGUIPanels;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import DispatchModelGUIRenderer.DispatchModelGUIDataObjects.DayDataObject;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

/**
 * Created by IntelliJ IDEA.
 * Name: InputDataSpecificationPanel
 * User: jacob
 * Date: Jul 22, 2003
 * Time: 2:45:30 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class InputDataSpecificationPanel extends JPanel
{
	// JPanel titles, JLabel text, JButton text
	public static final String DAILY_DATA = "daily data collected";
	public static final String MARKET_DATA = "social situation parameters";
	public static final String DAY_PANEL = "days of the year";
	public static final String DAY_ATTRIBUTE_LABEL = "attributes for ";
	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	public static final String DAY = "day ";
	public static final String TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR = "days in the year accounted for    ";

	public static final String EMPTY_STRING = "";

	public static int NUMBER_OF_HOURS = 24;

	public static final GridBagConstraints gbc = null;

	public static final String NUMBER_OF_DAYS = "number of days: ";
	public static final String INTEREST_RATE = "interest rate: ";
	public static final String DEFAULT_OF_SEVEN_DAYS = "7";
	public static final String PERCENT = "%";
	public static final String PROPERTY_TAX = "property tax: ";
	public static final String YEN = "Yen";

	public static final Boolean TRUE = new Boolean(true);
	public static final Boolean FALSE = new Boolean(false);

	// name of changed property
	public static final String ITEM = "item";
	public static final String NUMBER_OF_DAYS_HAS_CHANGED = "number of days has changed";
	public static final String EDITABLE_CHART_DATA_HAS_CHANGED = "editable chart data has changed";
	public static final String DAY_NAME_TEXTFIELD_HAS_CHANGED = "name textfield has changed";
	public static final String UPDATE_NUMBER_OF_DAYS_ACCOUNTED_FOR = "update number of days accounted for";
	public static final String XY_SERIES_COLLECTION_HAS_CHANGED = "xy series has changed";
	public static final String POWER_DEMAND_DATA_PARAMETER_CHANGED = "power demand data parameter changed";

	protected static final JLabel numberOfDaysLabel = Templates.makeLabel(NUMBER_OF_DAYS);

	protected static final JLabel interestRateLabel = Templates.makeLabel(INTEREST_RATE);
	protected static final JLabel percentLabel = Templates.makeLabel(PERCENT);
	protected static final JLabel propertyTaxLabel = Templates.makeLabel(PROPERTY_TAX);
	protected static final JLabel yenLabel = Templates.makeLabel(YEN);

	// parameter names
	public static final String INTEREST_RATE_PARAMETER = "interest rate";
	public static final String PROPERTY_TAX_PARAMETER = "property tax";
	public static final String POWER_DEMAND_DATA_PARAMETER = "power demand data";
	public static final String NUMBER_OF_DAYS_REPRESENTED = "number of days represented";
	public static final String NAMES_OF_DAYS = "names of days";

    public PropertyChangeSupport _listeners;

	private ModelInterfaceBase _iface;

	private JTextField _numberOfDaysHolder, _editableNameOfDayField,
				_interestRateTextField, _propertyTaxTextField, _numberOfDaysRepresentedTextField;

	private JButton _addADay, _removeADay;

	private JComboBox _dayComboBox;

	private JLabel _nameLabel = Templates.makeLabel("name: ");
	private JLabel _numberOfDaysRepresentedLabel = Templates.makeLabel("number of days represented: ");
	private JLabel _days = Templates.makeLabel(" days");
	private JLabel _dayAttributesLabel, _numberOfDaysInYearAccountedForLabel;

	private Integer _numberOfDays = null;
	private Integer _numberOfDaysInYearAccountedFor;
	private Integer _dayObjectToRemoveInList;

	private Integer _comboBoxDayNameUniqueTag;

	private List _dayDataObjectList = new ArrayList();

	private Boolean _listenerDirectionFlag = FALSE;

	Parameter _interestRate, _propertyTax, _powerDemandData, _numberOfDaysRepresented;

	public InputDataSpecificationPanel(ModelInterfaceBase iface)
	{
		_iface = iface;

		initializeParameters();
		makeInputDataSpecificationPanelComponents();
		makeInputDataSpecificationPanel();
        this._listeners = new PropertyChangeSupport(this);
	}

	private void initializeParameters()
	{
    	_interestRate = getParameterByName(INTEREST_RATE_PARAMETER);
		_propertyTax = getParameterByName(PROPERTY_TAX_PARAMETER);
		_powerDemandData = getParameterByName(POWER_DEMAND_DATA_PARAMETER);
		_numberOfDaysRepresented = getParameterByName(NUMBER_OF_DAYS_REPRESENTED);
	}

	private void makeInputDataSpecificationPanelComponents()
	{
		_comboBoxDayNameUniqueTag = new Integer(7);

		// default value for number of days is 7
		_numberOfDays = new Integer(DEFAULT_OF_SEVEN_DAYS);

		_numberOfDaysHolder = Templates.makeTextField(this._numberOfDays.toString());
		_numberOfDaysHolder.setEditable(false);
		_numberOfDaysHolder.setPreferredSize(new Dimension(100, 20));
		_numberOfDaysHolder.setHorizontalAlignment(SwingConstants.RIGHT);

		// add button to add a day
		this._addADay = Templates.makeButton(ADD);
		this._addADay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addOneDay();
			}
		});

		// remove button to remove a day
		this._removeADay = Templates.makeButton(REMOVE);
		this._removeADay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeOneDay();
			}
		});

		// combo box for the 7 days with default names; day1 ... day7
		this._dayComboBox = Templates.makeComboBox(ModelResultsPanel.comboBoxChoice);
		this._dayComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				bringCurrentDayDataObjectToFront();
				editableChartDataHasChanged();
			}
		});

		// create the 7 DayDataObject - the remaining objects will be created/destroyed as more days are added/removed

		for(int i=0; i < this._dayComboBox.getItemCount(); i++)
		{
			_dayDataObjectList.add(i, new DayDataObject((String)_dayComboBox.getItemAt(i)));
		}

		this._dayAttributesLabel = Templates.makeLabel();
		this.setDayAttributeLabel(this.getCurrentDayDataObject().getDayName());

		this._editableNameOfDayField = Templates.makeTextField(this.getCurrentDayDataObject().getDayName());
		this._editableNameOfDayField.setEditable(true);
		this._editableNameOfDayField.setHorizontalAlignment(SwingConstants.RIGHT);
		this._editableNameOfDayField.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if(!_editableNameOfDayField.getText().equals(InputDataSpecificationPanel.this.getCurrentDayDataObject().getDayName()))
					{
						getCurrentDayDataObject().setDayName((InputDataSpecificationPanel.this._editableNameOfDayField.getText()));
						_listeners.firePropertyChange(DAY_NAME_TEXTFIELD_HAS_CHANGED, null, _editableNameOfDayField.getText());
					}
				}
			}

			public void keyReleased(KeyEvent e)	{}

			public void keyTyped(KeyEvent e) {}

		});
		this._numberOfDaysRepresentedTextField = makeParameterTextField(_numberOfDaysRepresented, true);
		this._numberOfDaysRepresentedTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		_numberOfDaysInYearAccountedFor = calculateSumOfMatrix(((DomeMatrixData)_numberOfDaysRepresented.getCurrentDataObject()).getRow(0).getValuesArray());

		if(_numberOfDaysInYearAccountedFor.intValue() > 365)
		{
			for(int i=0; i < _dayComboBox.getItemCount(); i++)
				((DomeMatrix)_numberOfDaysRepresented.getCurrentDataObject()).setItem(0, i, new Double(0.0));

			OneButton1Msg.showError(null, "warning", "total number of days accounted for is greater than 365 days \n" +
			                                         "number of days represented for each element will be set to 0 \n" +
			                                         "please re-enter the required data", "OK", new Dimension(150, 75));

			_numberOfDaysInYearAccountedFor = new Integer(0);
		}

		this._numberOfDaysInYearAccountedForLabel = Templates.makeLabel(TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR + this._numberOfDaysInYearAccountedFor.toString());
		this._interestRateTextField = makeParameterTextField(_interestRate, true);
		this._interestRateTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		this._propertyTaxTextField = makeParameterTextField(_propertyTax, true);
		this._propertyTaxTextField.setHorizontalAlignment(SwingConstants.RIGHT);

		DomeMatrix powerDemandData = (DomeMatrix)_powerDemandData.getCurrentDataObject();
		powerDemandData.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				if(!_listenerDirectionFlag.booleanValue())
				{
					if (e.getPropertyName().equals(ITEM))
					{
						Point newPoint = (Point) e.getNewValue();
						((DayDataObject) InputDataSpecificationPanel.this._dayDataObjectList.get(newPoint.y)).
						        updatePowerDemandData(newPoint.x, (((DomeMatrix) InputDataSpecificationPanel.this._powerDemandData.getCurrentDataObject())
						                                          .getItem(newPoint.x, newPoint.y)).doubleValue());
						if(newPoint.y == InputDataSpecificationPanel.this._dayComboBox.getSelectedIndex())
							InputDataSpecificationPanel.this.editableChartDataHasChanged();
					}
				}
			}
		});

		getCurrentDayDataObject().setPowerDemandData(((DomeMatrixData) _powerDemandData.getCurrentDataObject()).getCol(_dayComboBox.getSelectedIndex()).getValuesArray());
	}

	private void setTotalNumberOfDaysAccountedFor(Integer newValue)
	{
		Integer oldValue = this._numberOfDaysInYearAccountedFor;
		this._numberOfDaysInYearAccountedFor = newValue;
		_listeners.firePropertyChange(UPDATE_NUMBER_OF_DAYS_ACCOUNTED_FOR, oldValue, this._numberOfDaysInYearAccountedFor);
	}

	public Integer getTotalNumberOfDaysAccountedFor()
	{
		return this._numberOfDaysInYearAccountedFor;
	}

	private void addOneDay()
	{
		int i = this._numberOfDays.intValue();
		setNumberOfDays(new Integer(++i));
	}

	private void removeOneDay()
	{
		int i = _numberOfDays.intValue();
		this.setNumberOfDays(new Integer(--i));
	}

	public void setNumberOfDaysInYearAccountedForLabel(String value)
	{
		this._numberOfDaysInYearAccountedForLabel.setText(TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR + value);
	}

	public JComboBox getDayNameComboBox()
	{
		return this._dayComboBox;
	}

	public void setDayAttributeLabel(String newLabel)
	{
		this._dayAttributesLabel.setText(DAY_ATTRIBUTE_LABEL + newLabel + ":");
	}

	public void setNumberOfDays(Integer newValue)
	{
        Integer oldValue = _numberOfDays;
		_numberOfDays = newValue;
		_listeners.firePropertyChange(NUMBER_OF_DAYS_HAS_CHANGED, oldValue, this._numberOfDays);
	}

	public Integer getNumberOfDays()
	{
		return this._numberOfDays;
	}

	public void editableChartDataHasChanged()
	{
		getCurrentDayDataObject().setPowerDemandData(((DomeMatrixData)_powerDemandData.getCurrentDataObject()).getCol(_dayComboBox.getSelectedIndex()).getValuesArray());
		_listeners.firePropertyChange(EDITABLE_CHART_DATA_HAS_CHANGED, null, this.getCurrentDayDataObject().getDayName());
	}

	public void swapNameInComboBox()
	{
		int index = this._dayComboBox.getSelectedIndex();
		if(this._dayComboBox.getItemCount() == 1)
		{
			this._dayComboBox.removeAllItems();
			this._dayComboBox.addItem(this._editableNameOfDayField.getText());
		}
		else
		{
			this._dayComboBox.insertItemAt(this._editableNameOfDayField.getText(), index);
			this._dayComboBox.removeItemAt(index+1);
			this._dayComboBox.setSelectedIndex(index);
		}
	}


	public void setEditableNameField(String newName)
	{
		this._editableNameOfDayField.setText(newName);
	}

    public JTextField getEditableNameField()
    {
        return _editableNameOfDayField;
    }

	public JTextField getNumberOfDaysHolder()
	{
		return this._numberOfDaysHolder;
	}

	public void updateNumberOfDaysComboBox()
	{
		if (this._numberOfDays.intValue() > this._dayComboBox.getItemCount())
		{
			this._comboBoxDayNameUniqueTag = new Integer(this._comboBoxDayNameUniqueTag.intValue() + 1);
			this._dayComboBox.addItem(DAY + this._comboBoxDayNameUniqueTag.toString());
		}
		else
		{
			this._dayObjectToRemoveInList = new Integer(this._dayComboBox.getSelectedIndex());
			this._dayComboBox.removeItemAt(this._dayObjectToRemoveInList.intValue());
		}
	}

	public void updateNumberOfDaysList()
	{
		if (this._numberOfDays.intValue() > this._dayDataObjectList.size())
			this._dayDataObjectList.add(this._numberOfDays.intValue() - 1, new DayDataObject(DAY + this._comboBoxDayNameUniqueTag.toString()));
		else if (this._numberOfDays.intValue() < this._dayDataObjectList.size())
			this._dayDataObjectList.remove(this._dayObjectToRemoveInList.intValue());
		else
			return;
	}

	public DayDataObject getCurrentDayDataObject()
	{
		return (DayDataObject)this._dayDataObjectList.get(this._dayComboBox.getSelectedIndex());
	}

	public void bringCurrentDayDataObjectToFront()
	{
		// important note:  this gui assumes that all the day data attribute information is stored in a 1 x n matrix
		// where n is the number of days in the DOME model.  The information that populates the gui is obtained
		// from the DOME model directly.

		int _FIRST_ROW = 0;

		DayDataObject dayDataObject = this.getCurrentDayDataObject();
		this._editableNameOfDayField.setText(dayDataObject.getDayName());

		this._numberOfDaysRepresentedTextField.setText(((DomeMatrix)_numberOfDaysRepresented.getCurrentDataObject()).getItem(_FIRST_ROW, _dayComboBox.getSelectedIndex()).toString());

		this.setDayAttributeLabel(dayDataObject.getDayName());
	}

	public void updatePowerDemandDataMatrix(int x, double y)
	{
		_listenerDirectionFlag = TRUE;

		((DomeMatrix)_powerDemandData.getCurrentDataObject()).setItem(x-1, this._dayComboBox.getSelectedIndex(), new Double(y));

		_listenerDirectionFlag = FALSE;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_listeners.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		this._listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		this._listeners.removePropertyChangeListener(propertyName, listener);
	}

	protected JPanel makeDayInformationPanel()
	{
		JPanel p = new JPanel();

		JComponent[] comps =
		        {
			        numberOfDaysLabel, _numberOfDaysHolder, this._addADay, this._removeADay, new JPanel(),

			        // combo box used to select specific day
			        this._dayComboBox,

			        // JPanel() for spacing
			        new JPanel(),

			        // day label for attributes
			        this._dayAttributesLabel,

			        this._nameLabel, this._editableNameOfDayField,

			        this._numberOfDaysRepresentedLabel, this._numberOfDaysRepresentedTextField, this._days,

			        this._numberOfDaysInYearAccountedForLabel,

			        // JPanel spacer
//			        new JPanel()

		        };

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs =
		        {
			        // numberOfDaysLabel, _numberOfDaysUneditable, addADay, removeADay
			        new GridBagConstraints(0, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			        new GridBagConstraints(1, 0, 2, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0,10, 0, 0), 0, 0),
			        new GridBagConstraints(3, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
					new GridBagConstraints(4, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
                    new GridBagConstraints(5, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 20), 0, 0),
			        // this._dayComboBox
			        new GridBagConstraints(0, 1, 6, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 20, 0, 20), 0, 0),

			        // JPanel spacer
			        new GridBagConstraints(0, 2, 6, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 20), 0, 0),

			        // this._dayAttributesLabel
			        new GridBagConstraints(0, 3, 6, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 0, 0, 20), 0, 0),

			        // this._nameLabel, this._editableNameOfDayField
			        new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			        new GridBagConstraints(1, 4, 5, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 20), 0, 0),

			        // this._numberOfDaysRepresentedLabel, this._numberOfDaysRepresentedTextField, this._days
			        new GridBagConstraints(0, 5, 2, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			        new GridBagConstraints(2, 5, 3, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 10, 0, 0), 0, 0),
			        new GridBagConstraints(5, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 20), 0, 0),

			        // this._numberOfDaysinYearAccountedForLabel
			        new GridBagConstraints(0, 6, 6, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(20, 10, 20, 20), 0, 0),

			        // JPanel spacer
//			        new GridBagConstraints(0, 7, 4, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)

		        };

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public JPanel makeMarketInformationPanel()
	{
		JPanel p = new JPanel();

		JComponent[] comps = {

			// interest rate = ______________ %
			interestRateLabel, this._interestRateTextField, percentLabel,

			// property tax: _____________________ Yen
			propertyTaxLabel, this._propertyTaxTextField, yenLabel,

			// JPanel spacer
			new JPanel()

		};

		GridBagConstraints[] gbcs = {

			// interesteRateLabel, this._interestRateTextField, percentLabel
			new GridBagConstraints(0, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 20), 0, 0),

			// proerptyTaxLabel, this._propertyTaxTextField, yenLabel
			new GridBagConstraints(0, 2, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 1.0, 0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 0, 0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 20), 0, 0),

			// JPanel spacer
			new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 20), 0, 0)

		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;

	}

	public void makeInputDataSpecificationPanel()
	{
		JPanel dayInformationPanel = this.makeDayInformationPanel();
		dayInformationPanel.setBorder(BorderFactory.createTitledBorder(null, DAILY_DATA, 0, 0, Templates.FONT11B));

		JPanel marketInformationPanel = this.makeMarketInformationPanel();
		marketInformationPanel.setBorder(BorderFactory.createTitledBorder(null, MARKET_DATA, 0, 0, Templates.FONT11B));

		JComponent[] comps = {

			// dayInformationPanel
			dayInformationPanel,

			// marketInformationPanel
			marketInformationPanel

		};

        GridBagConstraints[] gbcs = {

	        /// dayInformationPanel
	        new GridBagConstraints(0, 0, 1, 1, 0, 0, gbc.NORTH, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),

            // marketInformationPanel
	        new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)

        };

		Templates.layoutGridBag(this, comps, gbcs);
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
		JTextField tf = Templates.makeTextField(EMPTY_STRING);
		tf.setEditable(isEditable);

		p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));

		if(p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName()))
		{
			tf.setText(getRealValue(p));
			tf.addActionListener(new RealTextFieldActionListener(p, tf));
			p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealDataListener(tf));
		}
		else if(p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName()))
		{
			if(p.getName().equals(NUMBER_OF_DAYS_REPRESENTED))
			{
				tf.setText(getNumberOfDaysRepresentedElement(p).toString());
				tf.addActionListener(new NumberOfDaysRepresentedTextFieldActionListener(p, tf));
				p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new NumberOfDaysRepresentedDataListener(tf));
			}
		}
		return tf;
	}

	/**
	 * When textfield is clicked, value is sent to parameter.
	 */
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
				if (p.getName().equals(INTEREST_RATE_PARAMETER))
					if (newValue < 0)
					{
						OneButton1Msg.showWarning(null, "warning", "intereste rate must be a positive value", "OK", new Dimension(150, 75));
						this.txtField.setText(((DomeReal) p.getCurrentDataObject()).getDomeReal().toString());
					}
					else
						setRealValue(p, newValue);
				if(p.getName().equals(PROPERTY_TAX_PARAMETER))
					if (newValue < 0)
					{
						OneButton1Msg.showWarning(null, "warning", "property tax parameter must be a positive value", "OK", new Dimension(150, 75));
						this.txtField.setText(((DomeReal) p.getCurrentDataObject()).getDomeReal().toString());
					}
					else
						setRealValue(p, newValue);
			}
			catch (NumberFormatException exception)
			{
				OneButton1Msg.showWarning(null, "number format warning", "invalid value entered", "OK", new Dimension(150, 75));
				this.txtField.setText(((DomeReal)p.getCurrentDataObject()).getDomeReal().toString());
			}
		}
	}

	class NumberOfDaysRepresentedTextFieldActionListener implements ActionListener
	{
		JTextField _txtField;
		Parameter _p;

		public NumberOfDaysRepresentedTextFieldActionListener(Parameter p, JTextField txtField)
		{
			_txtField = txtField;
			_p = p;
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				int newValue = Integer.parseInt(_txtField.getText());
				if (newValue >= 0)
				{
					Integer oldValue = getNumberOfDaysRepresentedElement(_p);
					setNumberOfDaysRepresentedElement(_p, newValue);

					Integer totalNumberOfDays = InputDataSpecificationPanel.calculateSumOfMatrix(((DomeMatrixData) _p.getCurrentDataObject()).getRow(0).getValuesArray());

					if (totalNumberOfDays.intValue() > 365)
					{
						OneButton1Msg.showWarning(null, "warning", "total number of days accounted for exceeds 365", "OK", new Dimension(150, 75));
						_txtField.setText(oldValue.toString());
						setNumberOfDaysRepresentedElement(_p, oldValue.intValue());
					}
					else
						InputDataSpecificationPanel.this.setTotalNumberOfDaysAccountedFor(totalNumberOfDays);
				}
				else
				{
					OneButton1Msg.showError(null, "warning", "number of days must be positive", "OK", new Dimension(150, 75));
					_txtField.setText(InputDataSpecificationPanel.this.getCurrentDayDataObject().getNumberOfDaysAccounting().toString());
				}
			}
			catch (NumberFormatException exception)
			{
				OneButton1Msg.showWarning(null, "number format warning", "invalid value entered", "OK", new Dimension(150, 75));
				_txtField.setText(InputDataSpecificationPanel.this.getCurrentDayDataObject().getNumberOfDaysAccounting().toString());
			}
		}
	}

	/**
	 * When value changes in parameter, value is set in textfield.
	 */
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

	class NumberOfDaysRepresentedDataListener implements PropertyChangeListener
	{
		JTextField _txtField;

		public NumberOfDaysRepresentedDataListener(JTextField txtField)
		{
			 _txtField = txtField;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if(evt.getPropertyName().equals(DataObject.VALUE))
			{
				Double newValue = (Double) ((DomeMatrix)evt.getSource()).getItem(0, InputDataSpecificationPanel.this._dayComboBox.getSelectedIndex());
				_txtField.setText(new Integer(newValue.intValue()).toString());
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

	protected Integer getNumberOfDaysRepresentedElement(Parameter p)
	{
		return new Integer((int)(((DomeMatrix)p.getCurrentDataObject()).getItem(0, _dayComboBox.getSelectedIndex())).doubleValue());
	}

	protected void setNumberOfDaysRepresentedElement(Parameter p, int newValue)
	{
		((DomeMatrix)p.getCurrentDataObject()).setItem(0, _dayComboBox.getSelectedIndex(), new Double(newValue));
	}

	protected static Integer calculateSumOfMatrix(Number[] matrix)
	{
		int sum = 0;
		for(int i=0; i < matrix.length; i++)
			sum += matrix[i].intValue();

		return new Integer(sum);
	}

}
