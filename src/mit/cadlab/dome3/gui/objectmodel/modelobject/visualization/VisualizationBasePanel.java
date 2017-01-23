// VisualizationBasePanel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.gui.objectmodel.DomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.DataObjectCards;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DocumentationBasePanel;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.DomeObjectSet;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.LayeredCenterLayout;
import mit.cadlab.dome3.swing.Templates;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
//import com.jrefinery.chart.demo.EmptyXYDataset;
//import org.jfree.data.XYDataset;

public abstract class VisualizationBasePanel extends JLayeredPane
        implements DomeObjectGui
{

	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
	protected PropertyChangeListener propertyListener;
	protected DTextField nameField;
	protected JTabbedPane contentTabs;
	protected ChartVisualizationPanel valuePanel;
	protected DocumentationBasePanel docPanel;
	protected EditPanel editPanel;
	protected DataPanel dataPanel;
	protected JCheckBox constantCheckBox;
	protected JComboBox chartTypeComboBox;
	protected JComboBox chartTypeSubCombBox;
	protected JRadioButton verticalButton;
	protected JRadioButton horizontalButton;


	protected Visualization dataModel;

	public VisualizationBasePanel(Visualization param)
	{
		if (param == null)
			throw new IllegalArgumentException("Visualization gui - null Visualization");
		dataModel = param;
		propertyListener = getPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		createComponents();
		layoutComponents();
		configureComponents();
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return null;
	}

	protected void createComponents()
	{
		nameField = Templates.makeDTextField(dataModel.getName());
		chartTypeComboBox = Templates.makeDComboBox(makeChartTypeComboBoxModel());
		chartTypeSubCombBox = Templates.makeDComboBox(makeChartTypeSubComboBoxModel());
		contentTabs = Templates.makeTabbedPane();
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				repaint();
			}
		});
		constantCheckBox = Templates.makeCheckBox("constant:", dataModel.isConstant(), true);
		chartTypeComboBox.setEnabled(!dataModel.isConstant());
		chartTypeSubCombBox.setEnabled(!dataModel.isConstant());
        verticalButton = Templates.makeRadioButton("vertical", dataModel.isVertical());
		horizontalButton = Templates.makeRadioButton("horizontal", !dataModel.isVertical());
	}

	protected abstract ComboBoxModel makeChartTypeComboBoxModel();

	protected abstract ComboBoxModel makeChartTypeSubComboBoxModel();

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
		                      chartTypeComboBox,
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
	public void setModel(Visualization model)
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
		//setDataTypeSelection();
		setConstant(dataModel.isConstant());
		docPanel.setModel(dataModel.getDocumentation());

		if (dataModel.getChart() != null) {
			valuePanel.setChart(dataModel.getChart());
			//**valuePanel.setModelChartTypes();
		}
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

	// protected void setModelDataTypeSelection(Parameter.DataTypeSelection dtSel) {
	//     dataModel.setChartTypeSelection(dtSel);
	// }

	// protected void setModelCurrentType() {
	//     dataModel.setCurrentType((String) chartTypeComboBox.getSelectedItem());
	// }

	public void setModelConstant()
	{
		dataModel.setConstant(constantCheckBox.isSelected());
	}

	// Parameter javabean support
	public void setName(String value)
	{
		nameField.setText(value);
	}

	//  protected void setDataTypeSelection() {
	//      chartTypeComboBox.setModel(makeChartTypeComboBoxModel());
	//      valuePanel.setDataObjects(dataModel.getDataObjects(), dataModel.getCurrentType());
	//  }

	public void setCurrentType(String value)
	{
		if (!value.equals((String) chartTypeComboBox.getSelectedItem()))
			chartTypeComboBox.setSelectedItem(value);
	}

	public void setCurrentSubType(String value)
	{
		if (!value.equals((String) chartTypeSubCombBox.getSelectedItem()))
			chartTypeSubCombBox.setSelectedItem(value);
	}

	public void setConstant(boolean constant)
	{
		constantCheckBox.setSelected(constant);
	}

	public void setVertical(boolean vertical)
	{
		if(vertical == true) verticalButton.setSelected(true);
		else horizontalButton.setSelected(true);
	}

	//protected void showCurrentValue() {
	//valuePanel.showChart((String)dataTypeComboBox.getSelectedItem());
	//}

	/* public void loadChart(){
	       int selectedIndex=0;//for testing only
	       if(dataModel.getSetsList().size()==0)
	             return;
	       DomeObjectSet set=(DomeObjectSet)dataModel.getSetsList().get(selectedIndex);
	       JFreeChart chart=valuePanel.getChart();
	       chart.setDataset(set.portIntoChartDataset((String)chartTypeComboBox.getSelectedItem()));
	     }

	*/

	protected void convertToNotEditable()
	{

	}

	// DomeObjectGui interface
	public DomeObject getDomeObject()
	{
		return dataModel;
	}

	public String getTitlePrefix()
	{
		return "Visualization: ";
	}

	public String getTitle()
	{
		return getTitlePrefix() + getDomeObject().getName();
	}

	public Object getGuiObject()
	{
		return dataModel;
	}

	public ChartVisualizationPanel getValuePanel()
	{
		return valuePanel;
	}

	public void close()
	{
		//save chart into datamodel to keep a copy of the chart properties

		dataModel.setChart(valuePanel.getChart());
		// get data?
	}

}
