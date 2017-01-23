package listDataType;

import mit.cadlab.dome.DomeInit;
import mit.cadlab.dome.config.Registry;
import mit.cadlab.dome.gui.swing.tree.DomeTree;
import mit.cadlab.dome.gui.swing.tree.build.BuildListObjectTreeNode;
import mit.cadlab.dome.gui.swing.treetable.BuildTreeTable;
import mit.cadlab.dome.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome.objectmodel.util.id.Id;
import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.util.DArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Apr 2, 2003
 * Time: 7:51:41 PM
 * To change this template use Options | File Templates.
 */
public class DefinitionPanel extends JPanel
{
	public static final GridBagConstraints gbc = null;
	private static final Dimension DEFAULT_SIZE = new Dimension(400, 300);

	JTextField itemField;
	JLabel itemLabel;
	JComboBox typeCombo;
	JButton addButton;
	JButton deleteButton;
	JButton constraintsButton;
	List dataTypes;
	DArrayList objectArray = new DArrayList();
	DomeTree tree;
	BuildTreeTable treetable;
	int idNo = 0;
	Integer objectNo = new Integer(0);

	public DefinitionPanel()
	{
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		itemField = Templates.makeTextField("");
		itemField.setEditable(false);
		itemLabel = Templates.makeLabel("items");
		dataTypes = Registry.getDataObjectTypes();
		typeCombo = Templates.makeComboBox(dataTypes.toArray());   //(new Object[] {"real", "list dome datatypes"});
		tree = new DomeTree(new BuildListObjectTreeNode(objectArray), true);
		treetable = new BuildTreeTable(tree, 2, new String[]{"name","value" }, new int[] {150,200});
		JScrollPane tablePane = new JScrollPane(treetable);
		addButton = Templates.makeButton("add",new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				idNo++;
				objectArray.add(new ConcreteParameter(null,new Id("parameter"+idNo), dataTypes.get(typeCombo.getSelectedIndex()).toString()));
				objectNo = new Integer(objectArray.size());
				itemField.setText(objectNo.toString());
			}
		});
		deleteButton = Templates.makeButton("delete",new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//System.out.println(tree.getSelectionCount() );
				if(tree.getSelectionCount()!=0){
					int[] selected = tree.getSelectionRows();
					objectArray.removeAll(selected);
					objectNo = new Integer(objectArray.size());
					itemField.setText(objectNo.toString());
				}
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent e){
				 if(tree.getSelectionCount() == 0) deleteButton.setEnabled(false);
				 else deleteButton.setEnabled(true);
			}
		});

		deleteButton.setEnabled(false);
		constraintsButton = Templates.makeButton("constraints...");

		JComponent[] comps = {itemField, itemLabel, typeCombo, tablePane, makeAddDelete(), constraintsButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 30, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeAddDelete()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {addButton, deleteButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		JFrame f = new JFrame("List datatype definition panel");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DefinitionPanel());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
