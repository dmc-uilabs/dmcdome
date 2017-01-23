// ContextBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ContextBuildPanel extends AbstractDomeObjectGui
{

	protected static GridBagConstraints gbc;

	//protected static Dimension DEFAULT_SIZE = new Dimension(400,300);

	protected DefaultContextBuilder contextBuilder;
	protected NameTextField nameField;
	protected JTabbedPane contentTabs;
	protected ContextDefinitionBuildPanel defPanel;
	protected DocumentationBuildPanel docPanel;

	public ContextBuildPanel(DefaultContextBuilder contextBuilder)
	{
		super(contextBuilder);
		this.contextBuilder = contextBuilder;
		createComponents();
	}

	protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(contextBuilder);
		contentTabs = Templates.makeTabbedPane();
		defPanel = new ContextDefinitionBuildPanel(contextBuilder);
		docPanel = new DocumentationBuildPanel(contextBuilder.getDocumentation());
		contentTabs.addTab("definition", defPanel);
		contentTabs.addTab("documentation", docPanel);
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setMenuContext();
			}
		});
		layoutComponent();
	}

	protected void layoutComponent()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {makeControlPanel(), contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {Templates.makeLabel("name:"),
		                      nameField
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	// DomeObjectGui interface

	public String getTitlePrefix()
	{
		return "Context: ";
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		switch (contentTabs.getSelectedIndex()) {
			case 0: // definition
				defPanel.setMenuContext();
//	MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL_DEFINITION);
//	BuildFocusTracker.notifyInFocus(defPanel,contextBuilder);
				return;
			case 1: // documentation
				if (((DomeModel) contextBuilder.getModel()).isIntegrationModel()) {
					//set project menu
					MenuManager.setContext(ModeContexts.BUILD_PROJECT_DOCUMENTATION);
				} else if (contextBuilder.getModel() instanceof PluginModel)
					MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL_DOCUMENTATION);
				else
					MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION);
				break;
			default:
				if (((DomeModel) contextBuilder.getModel()).isIntegrationModel()) {
					//set project menu
					MenuManager.setContext(ModeContexts.BUILD_PROJECT);
				} else if (contextBuilder.getModel() instanceof PluginModel)
					MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
				else
					MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
		}
		BuildFocusTracker.notifyInFocus(this, contextBuilder);
	}

	public void addNotify()
	{
		super.addNotify();
	}
}
