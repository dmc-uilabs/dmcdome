// EqualRelationDefinitionRunPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 10, 2003
 * Time: 7:37:00 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.EqualRelationTreeRunPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class EqualRelationDefinitionRunPanel extends JPanel
{
	protected static GridBagConstraints gbc;
	protected static int[] columnWidths = {150, 150, 125};

	protected ConcreteEqualRelation relation;
	protected CardLayout2 relationViewsCards;
	protected JPanel relationViewsPanel;
	protected EqualRelationTreeRunPanel inputOutputViewPanel;
	protected EqualRelationTreeRunPanel modelCausalityViewPanel;
	protected DefaultComboBoxModel cbModel;
	protected JComboBox viewComboBox;


	public EqualRelationDefinitionRunPanel(ConcreteEqualRelation relation)
	{
		this.relation = relation;
		createComponents();
	}

	protected ComboBoxModel makeDataTypeComboBoxModel()
	{
		List dataTypes = Registry.getDataObjectTypes();
		return new DefaultComboBoxModel(dataTypes.toArray()); // one, for now
	}

	protected void createComponents()
	{
		relationViewsCards = new CardLayout2();
		relationViewsPanel = new JPanel();
		relationViewsPanel.setLayout(relationViewsCards);
		inputOutputViewPanel = new EqualRelationTreeRunPanel(relation,
		                                                     EqualRelation.INPUT_OUTPUT_VIEW);
		relationViewsPanel.add(EqualRelation.INPUT_OUTPUT_VIEW, inputOutputViewPanel);
		modelCausalityViewPanel = new EqualRelationTreeRunPanel(relation,
		                                                        EqualRelation.MODEL_CAUSALITY_VIEW);
		relationViewsPanel.add(EqualRelation.MODEL_CAUSALITY_VIEW, modelCausalityViewPanel);
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


		JComponent[] comps1 = {new JPanel(),
		                       viewComboBox,
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs1 = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps1, gbcs1);
		return p;
	}

	protected void switchView()
	{
		String newView = cbModel.getSelectedItem().toString();
		relationViewsCards.show(relationViewsPanel, newView);
	}
}
