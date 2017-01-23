// ProceduralRelationDefinitionRunPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 10, 2003
 * Time: 5:57:07 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.run;

import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ProceduralRelationDefinitionRunPanel extends JPanel
{
	protected static GridBagConstraints gbc;
	protected static int[] columnWidths = {150, 150, 125};

	protected ConcreteProceduralRelation relation;
	protected CardLayout2 relationViewsCards;
	protected JPanel relationViewsPanel;
	protected ProceduralRelationTreeRunPanel inputOutputViewPanel;
	protected ProceduralRelationTreeRunPanel modelCausalityViewPanel;
	protected DefaultComboBoxModel cbModel;
	protected JComboBox viewComboBox;


	public ProceduralRelationDefinitionRunPanel(ConcreteProceduralRelation relation)
	{
		this.relation = relation;
		createComponents();
	}

	protected void createComponents()
	{
		relationViewsCards = new CardLayout2();
		relationViewsPanel = new JPanel();
		relationViewsPanel.setLayout(relationViewsCards);
		inputOutputViewPanel = new ProceduralRelationTreeRunPanel(relation,
		                                                          ProceduralRelation.INPUT_OUTPUT_VIEW);
		relationViewsPanel.add(ProceduralRelation.INPUT_OUTPUT_VIEW, inputOutputViewPanel);
		modelCausalityViewPanel = new ProceduralRelationTreeRunPanel(relation,
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

		JComponent[] comps = {new JPanel(),
		                      viewComboBox,
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	protected void switchView()
	{
		String newView = cbModel.getSelectedItem().toString();
		relationViewsCards.show(relationViewsPanel, newView);
	}
}
