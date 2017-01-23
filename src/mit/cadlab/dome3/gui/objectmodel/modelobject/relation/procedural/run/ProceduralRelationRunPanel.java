// ProceduralRelationBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 10, 2003
 * Time: 5:31:25 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.run;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.run.ProceduralRelationDefinitionRunPanel;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoRendererTable;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.swing.LineNumberedScrollPane;
import mit.cadlab.dome3.swing.PythonEditor;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;

public class ProceduralRelationRunPanel extends AbstractDomeObjectGui
{

	protected static GridBagConstraints gbc;

	protected ConcreteProceduralRelation relation;
	protected NameTextField nameField;
	protected JTabbedPane contentTabs, definitionTabs;
	protected ProceduralRelationDefinitionRunPanel defPanel;
	protected CausalityInfoRendererTable dependencyTable;
	protected JButton editCausalityButton;
	protected DocumentationRunPanel docPanel;
	protected PythonEditor pyEditor;

	public ProceduralRelationRunPanel(ConcreteProceduralRelation relation)
	{
		super(relation);
		if (relation == null)
			throw new IllegalArgumentException("ProceduralRelation gui - null ProceduralRelation");
		this.relation = relation;
		pyEditor = relation.getPythonEditor();
		pyEditor.setParentPanel(this);
		pyEditor.setEditable(false);
		createComponents();
	}

	protected void createComponents()
	{
		nameField = new NameTextField();
		nameField.setDomeObject(relation);
		nameField.setEditable(false);
		contentTabs = Templates.makeTabbedPane();
		docPanel = new DocumentationRunPanel(relation.getDocumentation());
		contentTabs.addTab("definition", makeDefinitionPanel());
		contentTabs.addTab("documentation", docPanel);
		layoutComponent();
	}

	protected void layoutComponent()
	{
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
		JComponent[] comps = {Templates.makeLabel("name:"), nameField};
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
		defPanel = new ProceduralRelationDefinitionRunPanel(relation);
		definitionTabs.addTab("interface parameters", defPanel);
		definitionTabs.addTab("causality", makeCausalityPanel());
//todo do we need this listener?
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
		JComponent[] comps = {dependencyTable};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 5, 0), 0, 0),
//			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
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

	// DomeGui interface
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
		MenuManager.setContext(ModeContexts.RUN_MODE);
	}

	public String getMenuContext()
	{
		return ModeContexts.RUN_MODE;
	}

}
