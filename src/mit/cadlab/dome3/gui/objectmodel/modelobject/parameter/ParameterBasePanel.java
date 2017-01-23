// ParameterBasePanel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.gui.objectmodel.DomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DocumentationBasePanel;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.LayeredCenterLayout;
import mit.cadlab.dome3.swing.Templates;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class ParameterBasePanel extends JLayeredPane
        implements DomeObjectGui
{

	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
	protected PropertyChangeListener propertyListener;
	protected DTextField nameField;
	protected DComboBox dataTypeComboBox;
	protected DataObjectCards valuePanel;
	protected DocumentationBasePanel docPanel;
	protected JTabbedPane contentTabs;
	protected JCheckBox constantCheckBox;
	protected Parameter dataModel;

	public ParameterBasePanel(Parameter param)
	{
		if (param == null)
			throw new IllegalArgumentException("Parameter gui - null Parameter");
		dataModel = param;
		propertyListener = getPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		createComponents();
		layoutComponents();
		configureComponents();
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new GenericParameterPropertyChangeListener();
	}

	protected void createComponents()
	{
		nameField = Templates.makeDTextField(dataModel.getName());
		dataTypeComboBox = Templates.makeDComboBox(makeDataTypeComboBoxModel());
		contentTabs = Templates.makeTabbedPane();
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				repaint();
			}
		});
		constantCheckBox = Templates.makeCheckBox("constant:", dataModel.isConstant(), true);
	}

	protected abstract ComboBoxModel makeDataTypeComboBoxModel();

	protected void layoutComponents()
	{
		setLayout(new LayeredCenterLayout());
		add(makeConstantPanel());
		add(makeMainPanel());
	}

	protected void configureComponents()
	{
	}  // to be overridden by subclasses

	public JPanel makeConstantPanel()
	{
		JPanel p = new JPanel();
		p.setOpaque(false);
		JPanel filler1 = new JPanel();
		filler1.setOpaque(false);
		JComponent[] comps = {filler1,
		                      constantCheckBox};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0), // center filler
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 8), 0, -2), // constant check box
		};
		Templates.layoutGridBagB(p, comps, gbcs);
		return p;
	}

	public JPanel makeMainPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {Templates.makeLabel("name:"),
		                      nameField,
		                      dataTypeComboBox,
		                      contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0), // name label
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0), // name field
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0), // data type chooser
			new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0), // tabbed pane
		};
		Templates.layoutGridBagB(p, comps, gbcs);
		return p;
	}

	// connect to data model
	public void setModel(Parameter model)
	{
		if (model == null)
			throw new IllegalArgumentException("Parameter gui - null Parameter");
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = model;
		dataModel.addPropertyChangeListener(propertyListener);
		getModelData();
	}

	protected void getModelData()
	{
		setName(dataModel.getName());
		nameField.setCurrent();
		setDataTypeSelection();
		setConstant(dataModel.isConstant());
		docPanel.setModel(dataModel.getDocumentation());
	}

	protected void setModelData()
	{ // when does this get used?
		setModelName();
		//setModelDataTypeSelection(); // doesn't make sense
		setModelConstant();
	}

	protected void setModelName()
	{
		dataModel.setName(nameField.getText());
	}

	protected void setModelDataTypeSelection(Parameter.DataTypeSelection dtSel)
	{
		dataModel.setDataTypeSelection(dtSel);
	}

	protected void setModelCurrentType()
	{
		dataModel.setCurrentType((String) dataTypeComboBox.getSelectedItem());
	}

	public void setModelConstant()
	{
		dataModel.setConstant(constantCheckBox.isSelected());
	}

	// Parameter javabean support
	public void setName(String value)
	{
		nameField.setText(value);
	}

	protected void setDataTypeSelection()
	{
		dataTypeComboBox.setModel(makeDataTypeComboBoxModel());
		valuePanel.setDataObjects(dataModel.getDataObjects(), dataModel.getCurrentType());
	}

	public void setCurrentType(String value)
	{
		dataTypeComboBox.setSelectedItem(value); //??
		//valuePanel.showType(value); // assume it works
		//dataTypeComboBox.setBackground(currentBgColor);
	}

	public void setConstant(boolean constant)
	{
		constantCheckBox.setSelected(constant);
	}

	//protected void showCurrentValue() {
	//valuePanel.showType((String)dataTypeComboBox.getSelectedItem());
	//}

	class GenericParameterPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(Parameter.NAME)) {
				setName((String) newValue);
				nameField.setCurrent();
			} else if (property.equals(Parameter.DATATYPESELECTION)) {
				setDataTypeSelection();
			} else if (property.equals(Parameter.CURRENT_TYPE)) {
				dataTypeComboBox.setSelectedObject(newValue);
			} else if (property.equals(Parameter.CONSTANT)) {
				setConstant(((Boolean) newValue).booleanValue());
			}
		}
	}

	// DomeObjectGui interface
	public DomeObject getDomeObject()
	{
		return dataModel;
	}

	public String getTitlePrefix()
	{
		return "Parameter: ";
	}

	public String getTitle()
	{
		return getTitlePrefix() + getDomeObject().getName();
	}

	public Object getGuiObject()
	{
		return dataModel;
	}

	public void close()
	{
		// get data?

        // sangmok: memory problem fix. valuePanel is DataObjectCards instance which contains DataObjectPanel instances.
        // releaseDataObjectReferenceOfDataObjectPanel() method of DataObjectCards releases references to data object.
        valuePanel.releaseDataObjectReferenceOfDataObjectPanel();
	}

}
