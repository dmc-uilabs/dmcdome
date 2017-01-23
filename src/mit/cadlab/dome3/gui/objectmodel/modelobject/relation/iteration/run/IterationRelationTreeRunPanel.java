// ProceduralRelationTreeRunPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 10, 2003
 * Time: 6:23:49 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.run;

import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.RunProceduralRelationTree;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class IterationRelationTreeRunPanel extends JPanel
{

	public static GridBagConstraints gbc;
	protected ConditionIterationRelation relationBuilder;
	protected RunIterationRelationTree tree;
	protected RunTreeTable treeTable;

	public IterationRelationTreeRunPanel(ConditionIterationRelation relationBuilder,
	                                      String view)
	{
		this.relationBuilder = relationBuilder;
		tree = new RunIterationRelationTree(relationBuilder, view);
		treeTable = new RunTreeTable(tree);
		layoutComponent(treeTable);
		treeTable.setEnabled(false);
	}

	protected void layoutComponent(Component treeTable)
	{
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		JComponent[] comps = {scrollPane};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
//			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}
}
