// StringBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DataObjectPanel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class StringBuildPanel extends DataObjectPanel
{

	protected PropertyChangeListener propertyListener;
	protected DTextField valueTextField;
	protected JButton constraintsButton;
	protected mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString dataModel;
	public static final TypeInfo TYPE_INFO = new TypeInfo("StringBuildPanel");
	public static final String XML_TAG = "stringbuildpanel";

	public StringBuildPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString str)
	{
		if (str == null)
			throw new IllegalArgumentException("DomeString gui - null DomeString");
		dataModel = str;
		propertyListener = createPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		layoutPanel();
	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new StringPropertyChangeListener();
	}

	protected void layoutPanel()
	{
		valueTextField = Templates.makeDTextField(dataModel.getValue());
		constraintsButton = Templates.makeButton("constraints");
		JComponent[] comps = {valueTextField,
		                      new JPanel(), // filler
		                      constraintsButton,
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0), // valueTextField
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0), // fillerPanel
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, -2), // constraintsButton
		};
		Templates.layoutGridBagB(this, comps, gbcs);

		valueTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				setModelData();
			}
		});
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

		if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString)
			setModel((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString) data);
		else
			throw new IllegalArgumentException("DomeString gui - non-DomeString parameter");
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


	public void setModel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString model)
	{
		if (model == null)
			throw new IllegalArgumentException("DomeString gui - null DomeString");
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = model;
		dataModel.addPropertyChangeListener(propertyListener);
		getModelData();
	}

	protected void getModelData()
	{
		setValue(dataModel.getValue());
		valueTextField.setCurrent();
	}

	protected void setModelData()
	{
		dataModel.setValue(getValue());
	}

	// DString javabean support
	public String getValue()
	{
		return valueTextField.getText();
	}

	public void setValue(String value)
	{
		valueTextField.setText(value);
	}

	protected class StringPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString.VALUE)) {
				setValue(newValue.toString());
				valueTextField.setCurrent();
			}
		}
	}

}
