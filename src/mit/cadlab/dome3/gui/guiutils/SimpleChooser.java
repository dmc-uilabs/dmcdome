// SimpleChooser.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils;

import mit.cadlab.dome3.swing.DList;
import mit.cadlab.dome3.swing.DListModel;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleChooser extends JPanel
{
	protected static final GridBagConstraints gbc = null;

	protected DList objectList;
	protected Object[] selectedItems = null;

	public static Object[] showChooser(Component parent, String title, String okText, String cancelText, DListModel listModel, int listSelectionMode)
	{
		return showChooser(parent, title, okText, cancelText, listModel, listSelectionMode, null);
	}

	public static Object[] showChooser(Component parent, String title, String okText, String cancelText, DListModel listModel, int listSelectionMode,
	                                   Dimension size)
	{
		SimpleChooser chooser = new SimpleChooser(okText, cancelText, listModel, listSelectionMode);
		JDialog d = DialogFactory.createDialog(parent, title, chooser, true, true);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		if (size != null)
			d.setSize(size);
		d.show();
		return chooser.selectedItems;
	}

	public static Object[] showChooser(Component parent, String title, String okText, String cancelText, DListModel listModel)
	{
		return showChooser(parent, title, okText, cancelText, listModel, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	public static Object[] showChooser(Component parent, String title, String okText, String cancelText, DListModel listModel,
	                                   Dimension size)
	{
		return showChooser(parent, title, okText, cancelText, listModel, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, size);
	}

	public SimpleChooser(String okText, String cancelText, DListModel listModel, int listSelectionMode)
	{
		objectList = Templates.makeDList(listModel);
		objectList.setSelectionMode(listSelectionMode);
		JButton okButton = Templates.makeButton(okText, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedItems = objectList.getSelectedValues();
				dispose();
			}
		});
		JButton cancelButton = Templates.makeButton(cancelText, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		JComponent[] comps = {new JScrollPane(objectList),
		                      okButton,
		                      cancelButton
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}


}
