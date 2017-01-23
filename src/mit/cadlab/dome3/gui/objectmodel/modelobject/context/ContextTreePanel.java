//ModelInterfaceTreeBuilderPanel.java

package mit.cadlab.dome3.gui.objectmodel.modelobject.context;

import mit.cadlab.dome3.gui.guiutils.treetable.RunTreeTable;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.run.RunContextTree;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContext;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ContextTreePanel extends JPanel
{
	protected RunContextTree tree;
	protected RunTreeTable treeTable;

	public ContextTreePanel(DefaultContext context)
	{
		tree = new RunContextTree(context);
		treeTable = new RunTreeTable(tree);
		layoutComponent(treeTable);
	}

	public ContextTreePanel(DefaultContext context,
	                        boolean isTableEditable)
	{
		this(context);
		treeTable.setEnabled(isTableEditable);
	}

    //for optimization models
	public ContextTreePanel(DefaultContext context, String optimizationParameterType,
	                               int noCols, String[] colNames, int[] colWidths)
	{
        tree = new RunContextTree(context);
        treeTable = new RunTreeTable(tree, optimizationParameterType, noCols, colNames, colWidths);
		layoutComponent(treeTable);
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
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}


	public RunContextTree getContextTree()
	{
		return tree;
	}
}
