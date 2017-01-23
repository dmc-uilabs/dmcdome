// RealBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.gui.guiutils.units.UnitChooser;
import mit.cadlab.dome3.gui.guiutils.units.UnitComboBoxModel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DataObjectPanel;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.units.Quantity;
import mit.cadlab.dome3.util.FormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RealBuildPanel extends DataObjectPanel
{

	protected PropertyChangeListener propertyListener;
	protected DTextField valueTextField;
	protected DComboBox unitComboBox;
	protected JButton constraintsButton;
	protected mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal dataModel;
	public static final TypeInfo TYPE_INFO = new TypeInfo("RealBuildPanel");
	public static final String XML_TAG = "realbuildpanel";

	public RealBuildPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal real)
	{
		if (real == null)
			throw new IllegalArgumentException("DomeReal gui - null DomeReal");
		dataModel = real;
		propertyListener = createPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		layoutComponents(createComponents());
		configureComponents();
	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new RealPropertyChangeListener();
	}

	protected JComponent[] createComponents()
	{
        valueTextField = Templates.makeDTextField(FormatUtils.formatSigFig(dataModel.getRealValue().doubleValue()), 10);
		//valueTextField = Templates.makeDTextField(dataModel.getRealValue().toString(), 10);
		UnitComboBoxModel unitModel = new UnitComboBoxModel(dataModel.getUnit(), true);
		unitComboBox = Templates.makeDComboBox(unitModel);
		constraintsButton = Templates.makeButton("constraints");
		return new JComponent[]{valueTextField,
		                        unitComboBox,
		                        new JPanel(),
		                        constraintsButton,
		};
	}

	protected void layoutComponents(JComponent[] comps)
	{
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected void configureComponents()
	{
		valueTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					dataModel.setValue(getValue());
				} catch (NumberFormatException ex) {
					System.err.println("invalid real");
				}
			}
		});
		unitComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object choice = unitComboBox.getSelectedItem();
				if (choice instanceof String && UnitComboBoxModel.CHANGE_LIST.equals(choice))
					changeListAction();
				else
					dataModel.setUnit(getUnit());
			}
		});
	}

	protected void changeListAction()
	{
		Unit choice = UnitChooser.showDialog(null, dataModel.getUnit());
		if (choice == null)
			unitComboBox.setSelectedObject(dataModel.getUnit()); // back to initial choice
		else
			dataModel.setUnit(choice);
	}

	// connect to data model
	public void setDataObject(DataObject data)
	{
        // sangmok: added code to fix memory problem
        // setDataObject(null) is invoked during the executino of releaseDataObjectReferenceOfDataObjectPanel() in DataObjectCards class
        // when DomeMatrixData is null, codes like setDataModel_GUI() should be skipped
        // instead setDataModel_Null() should be invoked
        if (data == null) {
            setDataModel_Null();
            return;
        }
        // sangmok: added code ends

		if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal)
			setModel((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal) data);
		else
			throw new IllegalArgumentException("DomeReal gui - non-DomeReal parameter");
	}

    /**
     * sangmok : a new method to fix memory leakage problem
     * set data object reference (=dataMatrix) as null
     * also release data object reference in TableModel object
     */
	protected void setDataModel_Null()
	{
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = null;
	}

	public void setModel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal model)
	{
		if (model == null)
			throw new IllegalArgumentException("DomeReal gui - null DomeReal");
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = model;
		dataModel.addPropertyChangeListener(propertyListener);
		getModelData();
	}

	protected void getModelData()
	{
		setQuantity(dataModel.getQuantity());
		valueTextField.setCurrent();
	}

	protected void setModelData()
	{
		dataModel.setQuantity(getQuantity());
	}

	// DomeReal javabean support
	public double getValue()
	{
		return Double.parseDouble(valueTextField.getText());
	}

	public void setValue(double value)
	{
		valueTextField.setText(FormatUtils.formatSigFig(value));
        //valueTextField.setText(Double.toString(value));
	}

	public Double getRealValue()
	{
		return new Double(getValue());
	}

	public void setRealValue(Double value)
	{
        valueTextField.setText(FormatUtils.formatSigFig(value.doubleValue()));
		//valueTextField.setText(value.toString());
	}

	public Unit getUnit()
	{
		return (Unit) unitComboBox.getSelectedItem();
	}

	public void setUnit(Unit unit)
	{
		unitComboBox.setModel(new UnitComboBoxModel(unit, true));
	}

	public Quantity getQuantity()
	{
		return new Quantity(getRealValue(), getUnit());
	}

	public void setQuantity(Quantity q)
	{
		setValue(q.getMagnitude().doubleValue());
		setUnit(q.getUnit());
	}

	protected class RealPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal.VALUE)) {
				setRealValue((Double) newValue);
				valueTextField.setCurrent();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal.UNIT)) {
				setUnit((Unit) newValue);
			}
		}
	}

}
