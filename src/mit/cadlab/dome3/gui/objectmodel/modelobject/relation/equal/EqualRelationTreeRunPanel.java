// ProceduralRelationTreeRunPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 10, 2003
 * Time: 8:33:43 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal;

import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.run.RunEqualRelationTree;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class EqualRelationTreeRunPanel extends JPanel
{

	protected ConcreteEqualRelation relationBuilder;
	protected RunEqualRelationTree tree;
	protected RunTreeTable treeTable;


	public EqualRelationTreeRunPanel(ConcreteEqualRelation relationBuilder,
	                                 String view)
	{
		this.relationBuilder = relationBuilder;
		tree = new RunEqualRelationTree(relationBuilder, view);
		treeTable = new RunTreeTable(tree);
		layoutComponent(treeTable);
		treeTable.setEnabled(false);
	}

	//slightly changed the layoutComponent function to get rid of the up and down panel
	protected void layoutComponent(Component treeTable)
	{
		JScrollPane scrollPane = new JScrollPane(treeTable);
		scrollPane.getViewport().setBackground(Color.white);
		Dimension d = treeTable.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width, 200));
		JComponent[] comps = {scrollPane};
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(this, comps, gbcs);

	}
}
