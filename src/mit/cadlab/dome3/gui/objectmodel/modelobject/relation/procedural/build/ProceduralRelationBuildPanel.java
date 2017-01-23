// ProceduralRelationBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoEditorDialog;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoRendererTable;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;

import mit.cadlab.dome3.swing.PythonEditor;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.LineNumberedScrollPane;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.plugin.PluginModel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ProceduralRelationBuildPanel extends AbstractDomeObjectGui
{

	protected static GridBagConstraints gbc;

	protected ConcreteProceduralRelation relation;
	protected PropertyChangeListener propertyListener;
	protected ModelObjectNameListener nameListener;
	protected NameTextField nameField;
	protected JTabbedPane contentTabs, definitionTabs;
	protected ProceduralRelationDefinitionBuildPanel defPanel;
	protected CausalityInfoRendererTable dependencyTable;
	protected JButton editCausalityButton;
	protected DocumentationBuildPanel docPanel;
	protected PythonEditor pyEditor;

	public ProceduralRelationBuildPanel(ConcreteProceduralRelation relation)
	{
		super(relation);
		if (relation == null)
			throw new IllegalArgumentException("ProceduralRelation gui - null ProceduralRelation");
		this.relation = relation;
		pyEditor = relation.getPythonEditor();
		pyEditor.setParentPanel(this);
		propertyListener = getPropertyListener();
		relation.addPropertyChangeListener(propertyListener);
		relation.addModelObjectsListener(new RelationListener());
		nameListener = new ModelObjectNameListener();
		createComponents();
		Iterator rObjs = relation.getModelObjects().iterator();
		while (rObjs.hasNext()) {
			Object obj = rObjs.next();
			if (obj instanceof Parameter) {
				Parameter rp = (Parameter) obj;
				rp.addPropertyChangeListener(ModelObject.NAME, nameListener);
			}
		}
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new ProceduralRelationPropertyChangeListener();
	}

	class ProceduralRelationPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(ProceduralRelation.DEPENDENCY_INFO)) {
				dependencyTable.setDependencyInfo(new ArrayList(relation.getModelObjects()), (DependencyInfo) newValue);
			}
		}
	}

	class ModelObjectNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			if (property.equals(ModelObject.NAME)) {
				// update causality table
				Object obj = e.getSource();
				DependencyInfo dInfo = relation.getDependencyInfo();
				if (dInfo.getCausality(obj) == CausalityStatus.INDEPENDENT ||
				        dInfo.getCausality(obj) == CausalityStatus.RESULT) {
					dependencyTable.setDependencyInfo(new ArrayList(relation.getModelObjects()), dInfo);
				}
			}
		}
	}

	protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(relation);
		contentTabs = Templates.makeTabbedPane();
		docPanel = new DocumentationBuildPanel(relation.getDocumentation());
		contentTabs.addTab("definition", makeDefinitionPanel());
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
		JComponent[] comps = {makeNamePanel(), contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeNamePanel()
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

	protected JSplitPane makeDefinitionPanel()
	{
		definitionTabs = Templates.makeTabbedPaneTop();
		defPanel = new ProceduralRelationDefinitionBuildPanel(relation);
		definitionTabs.addTab("interface parameters", defPanel);
		definitionTabs.addTab("causality", makeCausalityPanel());
		definitionTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setMenuContext();
			}
		});
		JPanel editorPanel = makeEditorPanel();
		JSplitPane spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, definitionTabs, editorPanel);
		spane.setDividerLocation(200);
		return spane;
	}

	protected JPanel makeCausalityPanel()
	{
		JPanel p = new JPanel();
		dependencyTable = new CausalityInfoRendererTable(new ArrayList(relation.getModelObjects()),
		                                                 relation.getDependencyInfo());
        editCausalityButton = Templates.makeButton("Edit causality information",
		                                           new ActionListener()
		                                           {
			                                           public void actionPerformed(ActionEvent event)
			                                           {
				                                           DependencyInfo newInfo = CausalityInfoEditorDialog.showEditor(ProceduralRelationBuildPanel.this,
				                                                                                                         new ArrayList(relation.getModelObjects()), relation.getDependencyInfo());
				                                           if (newInfo != null)
					                                           relation.setDependencyInfo(newInfo);
			                                           }
		                                           });
		if (relation.getParameterCount() < 2)
			editCausalityButton.setEnabled(false);
		JComponent[] comps = {dependencyTable,
		                      editCausalityButton
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBagB(p, comps, gbcs);
		return p;
	}

	protected JPanel makeEditorPanel()
	{
		JPanel p = new JPanel();

		pyEditor.setMinimumSize(new Dimension(300, 150));

		LineNumberedScrollPane spane = new LineNumberedScrollPane(pyEditor);
		JComponent[] comps = {Templates.makeLabel("body:"),
		                      spane
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public Relation getRelation()
	{
		return this.relation;
	}

	public String getTextFromPyEditor()
	{
		return pyEditor.getText().trim();
	}

	public void addNotify()
	{
		super.addNotify();
		SwingUtilities.windowForComponent(this).addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				docPanel.setModelText(); // hack since it doesn't seem to update properly
			}
		});
	}

	class RelationListener implements DListListener
	{
		public void intervalChanged(DListEvent e)
		{
			// do nothing
		}

		public void intervalAdded(DListEvent e)
		{
			addItems(e.getItems());
		}

		public void intervalRemoved(DListEvent e)
		{
			removeItems(e.getItems());
		}

		public void itemsRemoved(DListEvent e)
		{
			removeItems(e.getItems());
		}

		public void itemsReplaced(DListEvent e)
		{
			// not supported
		}

		protected void addItems(List items)
		{
			Iterator rObjs = items.iterator();
			while (rObjs.hasNext()) {
				Object obj = rObjs.next();
				if (obj instanceof Parameter) {
					Parameter rp = (Parameter) obj;
					rp.addPropertyChangeListener(ModelObject.NAME, nameListener);
				}
			}
			if (relation.getModelObjects().size() > 1 && !editCausalityButton.isEnabled())
				editCausalityButton.setEnabled(true);
		}

		protected void removeItems(List items)
		{
			Iterator rObjs = items.iterator();
			while (rObjs.hasNext()) {
				Object obj = rObjs.next();
				if (obj instanceof Parameter) {
					Parameter rp = (Parameter) obj;
					rp.removePropertyChangeListener(ModelObject.NAME, nameListener);
				}
			}
			if (relation.getModelObjects().size() < 2 && editCausalityButton.isEnabled())
				editCausalityButton.setEnabled(false);
		}
	}

	// DomeObjectGui interface
	public String getTitlePrefix()
	{
		return "ProceduralRelation: ";
	}

	public String getHelpContext()
	{
		return null;
	}

	public void setMenuContext()
	{
		switch (contentTabs.getSelectedIndex()) {
			case 0: // definition
				switch (definitionTabs.getSelectedIndex()) {
					case 0: // parameters
						defPanel.setMenuContext();
						return;
					default: // causality
						if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
							MenuManager.setContext(ModeContexts.BUILD_PROJECT);
						} else if (relation.getModel() instanceof PluginModel)
							MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
						else
							MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
				}
				break;
			case 1: // documentation
				if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
					MenuManager.setContext(ModeContexts.BUILD_PROJECT_DOCUMENTATION);
				} else if (relation.getModel() instanceof PluginModel)
					MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL_DOCUMENTATION);
				else
					MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION);
				break;
			default: // default for other tabs
				if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
					MenuManager.setContext(ModeContexts.BUILD_PROJECT);
				} else if (relation.getModel() instanceof PluginModel)
					MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
				else
					MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);

		}
		BuildFocusTracker.notifyInFocus(this, relation.getModel());
	}

	public void test()
	{
		System.out.println(relation.getName() + ":test2");
	}

}
