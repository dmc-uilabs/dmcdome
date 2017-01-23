// ParameterBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DocumentationBasePanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.ParameterBasePanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.DataTypeComboBoxModel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.DataObjectCards;
import mit.cadlab.dome3.gui.guiutils.msg.SingleOptionChooser;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.plugin.PluginModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ParameterBuildPanel extends ParameterBasePanel
{

	public ParameterBuildPanel(Parameter param)
	{
		super(param);
	}

	protected ComboBoxModel makeDataTypeComboBoxModel()
	{
		return new DataTypeComboBoxModel(dataModel.getCurrentType(), true); // one, for now
	}

	protected void configureComponents()
	{
		nameField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dataModel.setName(nameField.getText());
			}
		});
		valuePanel = createDataObjectCards();
		valuePanel.setDataObjects(dataModel.getDataObjects(), dataModel.getCurrentType());
		docPanel = createDocumentationPanel();
		contentTabs.addTab("definition", valuePanel);
		contentTabs.addTab("documentation", docPanel);
        contentTabs.addChangeListener(new ChangeListener()
		{
            public void stateChanged(ChangeEvent e)
            {
                MenuManager.setContext(getMenuContext());
            }
        });
		dataTypeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String choice = (String) dataTypeComboBox.getSelectedItem();
				if (DataTypeComboBoxModel.CHANGE_LIST.equals(choice)) {
					changeListAction();
				} else {
					setModelCurrentType();
				}
			}
		});
		constantCheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				dataModel.setConstant(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
	}

	protected DataObjectCards createDataObjectCards()
	{
		return new DataObjectCards(0); // build
	}

	protected DocumentationBasePanel createDocumentationPanel()
	{
		return new DocumentationBuildPanel(dataModel.getDocumentation());
	}

	protected void changeListAction()
	{
		String selectedType = SingleOptionChooser.showDialog(this,
		                                                     "Data type chooser",
		                                                     dataModel.getCurrentType(),
		                                                     Registry.getDataObjectTypes());
		if (selectedType != null) {
			setModelDataTypeSelection(new Parameter.DataTypeSelection(selectedType));
		} else { // reset combo box
			dataTypeComboBox.setSelectedObject(dataModel.getCurrentType());
		}
	}

	// DomeObjectGui interface
	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		MenuManager.setContext(getMenuContext());
		BuildFocusTracker.notifyInFocus(this, dataModel);
	}

	protected String getMenuContext()
	{
		switch (contentTabs.getSelectedIndex()) {
			case 0: // definition
				if (dataModel.getScope() instanceof DomeModel && ((DomeModel) dataModel.getScope()).isIntegrationModel()) {
					//set project menu
					return ModeContexts.BUILD_PROJECT;
				} else if (dataModel.getScope() instanceof PluginModel)
					return ModeContexts.BUILD_PLUGINMODEL;
				else
					return ModeContexts.BUILD_DOMEMODEL;

			case 1: // documentation
				if (dataModel.getScope() instanceof DomeModel && ((DomeModel) dataModel.getScope()).isIntegrationModel()) {
					//set project menu
					return ModeContexts.BUILD_PROJECT_DOCUMENTATION;
				} else if (dataModel.getScope() instanceof PluginModel)
					return ModeContexts.BUILD_PLUGINMODEL_DOCUMENTATION;
				else
					return ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION;

			default:
				if (dataModel.getScope() instanceof DomeModel && ((DomeModel) dataModel.getScope()).isIntegrationModel()) {
					//set project menu
					return ModeContexts.BUILD_PROJECT;
				} else if (dataModel.getScope() instanceof PluginModel)
					return ModeContexts.BUILD_PLUGINMODEL;
				else
					return ModeContexts.BUILD_DOMEMODEL;
		}
	}

}
