// ProceduralRelationRunPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 10, 2003
 * Time: 7:23:12 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run.EqualRelationDefinitionRunPanel;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoRendererTable;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;

public class EqualRelationRunPanel extends AbstractDomeObjectGui
{
	protected static GridBagConstraints gbc;

	protected ConcreteEqualRelation relation;
	protected NameTextField nameField;
	protected JTabbedPane contentTabs, definitionTabs;
	protected EqualRelationDefinitionRunPanel defPanel;
	protected CausalityInfoRendererTable dependencyTable;
	protected DocumentationRunPanel docPanel;


	public EqualRelationRunPanel(ConcreteEqualRelation relation)
	{
		super(relation);
		if (relation == null)
			throw new IllegalArgumentException("EqualRelation gui - null EqualRelation");
		this.relation = relation;
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

	//protected JSplitPane makeDefinitionPanel()
	protected JPanel makeDefinitionPanel()
	{
		JPanel p = new JPanel();
		definitionTabs = Templates.makeTabbedPaneTop();
		defPanel = new EqualRelationDefinitionRunPanel(relation);
		definitionTabs.addTab("interface parameters", defPanel);
		definitionTabs.addTab("causality", makeCausalityPanel());
		JComponent[] comps = {definitionTabs};
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
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
		};
		Templates.layoutGridBagB(p, comps, gbcs);
		return p;
	}

	// DomeGui interface
	public String getTitlePrefix()
	{
		return "EqualRelation: ";
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
