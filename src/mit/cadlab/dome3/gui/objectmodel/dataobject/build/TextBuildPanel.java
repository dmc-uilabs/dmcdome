// TextBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText;
import mit.cadlab.dome3.swing.DTextArea;
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
import javax.swing.JScrollPane;

public class TextBuildPanel extends DataObjectPanel
{

	protected PropertyChangeListener propertyListener;
	protected DTextArea valueTextArea;
	protected JButton constraintsButton;
	protected mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText dataModel;
	public static final TypeInfo TYPE_INFO = new TypeInfo("TextBuildPanel");
	public static final String XML_TAG = "textbuildpanel";

	public TextBuildPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText txt)
	{
		if (txt == null)
			throw new IllegalArgumentException("DomeText gui - null DomeText");
		dataModel = txt;
		propertyListener = createPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		layoutPanel();
	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new TextPropertyChangeListener();
	}

	protected void layoutPanel()
	{
		valueTextArea = Templates.makeDTextArea(dataModel.getValue());
		constraintsButton = Templates.makeButton("constraints");
		JComponent[] comps = {new JScrollPane(valueTextArea),
		                      constraintsButton,
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0), // valueTextArea
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, -2), // constraintsButton
		};
		Templates.layoutGridBagB(this, comps, gbcs);

		valueTextArea.addActionListener(new ActionListener()
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

		if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText)
			setModel((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText) data);
		else
			throw new IllegalArgumentException("DomeText gui - non-DomeText parameter");
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


	public void setModel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText model)
	{
		if (model == null)
			throw new IllegalArgumentException("DomeText gui - null DomeText");
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
		valueTextArea.setCurrent();
	}

	protected void setModelData()
	{
		dataModel.setValue(getValue());
	}

	// DString javabean support
	public String getValue()
	{
		return valueTextArea.getText();
	}

	public void setValue(String value)
	{
		valueTextArea.setText(value);
	}

	protected class TextPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText.VALUE)) {
				setValue(newValue.toString());
				valueTextArea.setCurrent();
			}
		}
	}

}
