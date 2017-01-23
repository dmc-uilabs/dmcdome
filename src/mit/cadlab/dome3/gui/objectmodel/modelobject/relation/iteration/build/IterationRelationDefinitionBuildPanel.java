// ProceduralRelationDefinitionBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class IterationRelationDefinitionBuildPanel extends JPanel
{
	protected static GridBagConstraints gbc;
	protected static int[] columnWidths = {150, 150, 125};

	protected ConditionIterationRelation relation;
	protected CardLayout2 relationViewsCards;
	protected JPanel relationViewsPanel;
	protected IterationRelationTreeBuilderPanel inputOutputViewPanel;
	protected IterationRelationTreeBuilderPanel modelCausalityViewPanel;
	protected DefaultComboBoxModel cbModel;
	protected JComboBox viewComboBox;


	public IterationRelationDefinitionBuildPanel(ConditionIterationRelation relation)
	{
		this.relation = relation;
		createComponents();
	}

	protected void createComponents()
	{
		relationViewsCards = new CardLayout2();
		relationViewsPanel = new JPanel();
		relationViewsPanel.setLayout(relationViewsCards);
		inputOutputViewPanel = new IterationRelationTreeBuilderPanel(relation,
		                                                              ProceduralRelation.INPUT_OUTPUT_VIEW);
		relationViewsPanel.add(ProceduralRelation.INPUT_OUTPUT_VIEW, inputOutputViewPanel);
		modelCausalityViewPanel = new IterationRelationTreeBuilderPanel(relation,
		                                                                 ProceduralRelation.MODEL_CAUSALITY_VIEW);
		relationViewsPanel.add(ProceduralRelation.MODEL_CAUSALITY_VIEW, modelCausalityViewPanel);

		layoutComponent();
	}

	protected void layoutComponent()
	{
		JComponent[] comps = {makeControlPanel(), relationViewsPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// 25 inset
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
		cbModel = new DefaultComboBoxModel(relation.getViewNames().toArray());
		viewComboBox = Templates.makeComboBox(cbModel);
		viewComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				switchView();
			}
		});

		// filler panel size of button
		JButton listButton = Templates.makeListArrowButton("up");
		JPanel fillerPanel = new JPanel();
		Templates.setFixedSize(fillerPanel, listButton.getPreferredSize());

		JComponent[] comps = {new JPanel(),
		                      viewComboBox,
		                      fillerPanel
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public void setMenuContext()
	{
		MenuManager.setContext(getMenuContext());
		JComponent comp = (JComponent) relationViewsCards.getActiveComponent();
		BuildFocusTracker.notifyInFocus(comp, (ModelObject) relation);
		if (comp.equals(inputOutputViewPanel))
			inputOutputViewPanel.setEditMenusForSelection();
	}

	protected String getMenuContext()
	{
		String currentView = relationViewsCards.getActiveName();
		if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
			//setting the projext menu
			if (currentView.equals(IterationRelation.INPUT_OUTPUT_VIEW)) {
				return ModeContexts.BUILD_PROJECT_ITERATIONRELATION_DEFINITION;
			} else if (currentView.equals(IterationRelation.MODEL_CAUSALITY_VIEW)) {
				return ModeContexts.BUILD_PROJECT_ITERATIONRELATION_DEFINITION;
			} else {
				return ModeContexts.BUILD_PROJECT_STANDARD_VIEW;
			}
		} else {
			if (currentView.equals(IterationRelation.INPUT_OUTPUT_VIEW)) {
				return ModeContexts.BUILD_ITERATIONRELATION_DEFINITION;
			} else if (currentView.equals(IterationRelation.MODEL_CAUSALITY_VIEW)) {
				return ModeContexts.BUILD_ITERATIONRELATION_DEFINITION;
			} else {
				return ModeContexts.BUILD_STANDARD_VIEW;
			}
		}
	}

	protected void switchView()
	{
		String newView = cbModel.getSelectedItem().toString();
		relationViewsCards.show(relationViewsPanel, newView);
		setMenuContext(); // do this before the next line!
		synchronizeViewControls(); // needs correct menu showing
	}

	protected void synchronizeViewControls()
	{
		String currentView = relationViewsCards.getActiveName();
		if (currentView.equals(ProceduralRelation.INPUT_OUTPUT_VIEW)) {
			inputOutputViewPanel.setEditMenusForSelection();
		}
		if (currentView.equals(ProceduralRelation.MODEL_CAUSALITY_VIEW)) {
			modelCausalityViewPanel.setEditMenusForSelection();
		}
	}

}
