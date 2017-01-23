package deployPlayspaceRev2;

import mit.cadlab.dome.gui.projectPlayspaceAddModel.ProjectPlayspaceAddModel;
import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.swing.CardLayout2;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import editPlayspace.EditPlayspaceTable;
import deployPlayspace.DeployPlayspace;
import deployPlayspace.DeployPlayspaceGui;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 4:26:11 PM
 * To change this template use Options | File Templates.
 */


/**
 * Card for adding a removing items from a playspace during deployment
 */
public class EditCard extends JPanel
{


	public static final GridBagConstraints gbc = null;

	DeployPlayspace data;
	DeployPlayspaceGui deployGui;

	private JButton addButton;
	private JButton removeButton;
	private JPanel tableCard = new JPanel();

	public void setEditTable()
	{
		EditPlayspaceTable t= null;
		if (data.isNewDeployment())
			t = new EditPlayspaceTable(null);
		else
			;//do the same as above but calling the svr based version on of the constructor
		tableCard.add("temp", t);
		((CardLayout2) tableCard.getLayout()).last(tableCard);
		data.setPlayspaceTable(t);
	}


	public EditCard(DeployPlayspace deployData, DeployPlayspaceGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setEditCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Which models or iProjects are in the playspace?", Templates.FONT12B);

		addButton = Templates.makeButton("add");
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				data.getPlayspaceTable().addRow();
				tableCard.validate();
			}
		});

		removeButton = Templates.makeButton("remove");
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				data.getPlayspaceTable().deleteRow();
				tableCard.validate();
			}
		});
		tableCard.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, addButton, removeButton, tableCard};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("edit playspace content card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new EditCard(null, null));
		f.show();
	}
}
