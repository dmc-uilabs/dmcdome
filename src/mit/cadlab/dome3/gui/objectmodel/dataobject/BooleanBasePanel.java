// BooleanBasePanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.Templates;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class BooleanBasePanel extends DataObjectPanel
{

	protected static Object[] booleanChoices = {Boolean.FALSE,
	                                            Boolean.TRUE};
	protected PropertyChangeListener propertyListener;
	protected DComboBox valueComboBox;
	protected mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean dataModel;

	public BooleanBasePanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean bool)
	{
		if (bool == null)
			throw new IllegalArgumentException("DomeBoolean gui - null DomeBoolean");
		dataModel = bool;
		propertyListener = createPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		layoutComponents(createComponents());
		configureComponents();
	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new BooleanPropertyChangeListener();
	}

	/**
	 * Method may be overridden. Call super.createComponents().
	 */
	protected JComponent[] createComponents()
	{
		valueComboBox = Templates.makeDComboBox(booleanChoices);
		valueComboBox.setSelectedObject(dataModel.getBooleanValue());
		JPanel fillerPanel = new JPanel();
		return new JComponent[]{valueComboBox,
		                        fillerPanel,
		};
	}

	protected void layoutComponents(JComponent[] comps)
	{
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0), // valueComboBox
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0) // fillerPanel
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	// to be overridden by subclasses
	protected void configureComponents()
	{
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

		if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean)
			setModel((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean) data);
		else
			throw new IllegalArgumentException("DomeBoolean gui - non-DomeBoolean parameter");
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

	public void setModel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean model)
	{
		if (model == null)
			throw new IllegalArgumentException("DomeBoolean gui - null DomeBoolean");
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = model;
		dataModel.addPropertyChangeListener(propertyListener);
		getModelData();
	}

	protected void getModelData()
	{
		valueComboBox.setSelectedObject(dataModel.getBooleanValue());
	}

	protected void setModelData()
	{
		dataModel.setBooleanValue(getValue());
	}

	// DomeBoolean javabean support
	public Boolean getValue()
	{
		return (Boolean) valueComboBox.getSelectedItem();
	}

	public void setValue(Boolean value)
	{
		valueComboBox.setSelectedObject(value);
	}

	protected class BooleanPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean.VALUE)) {
				setValue((Boolean) newValue);
			}
		}
	}

}
