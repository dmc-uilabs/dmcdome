package buildPlayspace;

import mit.cadlab.dome.swing.CardLayout2;
import mit.cadlab.dome.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


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
public class BuildPlayspaceGui extends JPanel
{

	public static final GridBagConstraints gbc = null;

	BuildPlayspace data;

	private JButton addButton;
	private JButton removeButton;
	private JPanel tableCard = new JPanel();
	private JTextField nameField;
	private JTextField filePathField;

	/**
	 *
	 * @param t pass the EditPlayspaceTable to show in the GUI
	 */
	public void setTable(editPlayspace.EditPlayspaceTable t)
	{
		tableCard.add("editTable",t);
		((CardLayout2)(tableCard.getLayout())).last(tableCard);

	}

	/**
	 * use to set the file path once the PS has been saved, or on an open
	 * @param txt
	 */
	public void setFilePathField(String txt)
	{
		filePathField.setText(txt);
	}

	public BuildPlayspaceGui(BuildPlayspace data)
	{
		this.data = data;

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel nameLabel = Templates.makeLabel("name:");
		nameField = Templates.makeDTextField("");
		nameField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				data.setPlayspaceName(nameField.getText());
				nameField.setBackground(Color.white);
				//todo should also set the window title
			}
		});

		addButton = Templates.makeButton("add");
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//todo may need to change this when Charles updates the edit code
				data.getTable().showAddDialog();
				tableCard.validate();
			}
		});
		removeButton = Templates.makeButton("remove");
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				data.getTable().deleteRow();
				tableCard.validate();
			}
		});
		tableCard.setLayout(new CardLayout2());
		filePathField = Templates.makeTextField("");
		filePathField.setEditable(false);

		JComponent[] comps = {nameLabel, nameField, addButton, removeButton, tableCard, filePathField};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

}
