// AuxFilesManagerPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelcomponent.auxfile;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.CommonAuxFile;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 */
public class AuxFilesManagerPanel extends JPanel
{
	protected Model model;
	protected int selectedRow = -1; //initial value
	protected boolean isAdd = false,isEdit = false;

	AuxFilesPanel filePanel;
	JButton addButton,editButton,removeButton;
	JButton selectButton,okButton,cancelButton;
	JTextField nameField,filelocationField;
	JPanel blankPanel,editPanel,bottomCardPanel;
	protected CardLayout2 cardview;

	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class


	public AuxFilesManagerPanel(Model m)
	{
		this.model = m;
		filePanel = new AuxFilesPanel(model);
		filePanel.addSelectionListenerForTable(createSelectionListener());
		model.addPropertyChangeListener(NameListener.NAME, new NameListener(){
			public void nameChanged(String newName)
			{
				//for later use
			}
		});

		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				if (source == removeButton) {
					if (getAuxFile(selectedRow) != null)
						((AbstractDomeModel) model).removeAuxFile(getAuxFile(selectedRow));
				}
				//else if (source == doneButton) {
				//dispose();
				//}
				else if (source == addButton) {
					isAdd = true;
					showadd();
				}
				else if (source == editButton) {
					isEdit = true;
					showedit();
				}
			}
		};

		addButton = Templates.makeButton("add", actionListener);
		removeButton = Templates.makeButton("remove", actionListener);
		editButton = Templates.makeButton("edit", actionListener);
		//doneButton = Templates.makeButton("done", actionListener);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());

		JComponent[] comps = {
			addButton,
			removeButton,
			editButton,
			//		doneButton
		};

		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//		new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
		};

		Templates.layoutGridBagB(buttonPanel, comps, gbcs);
		buttonPanel.setPreferredSize(new Dimension(450, 70));

		blankPanel = new JPanel();
		editPanel = makeEditPanel();
		cardview = new CardLayout2();
		bottomCardPanel = new JPanel();
		bottomCardPanel.setLayout(cardview);

		bottomCardPanel.add("blank", blankPanel);
		bottomCardPanel.add("edit", editPanel);

		JComponent[] comp2s = {
			filePanel,
			buttonPanel,
			bottomCardPanel,
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbc2s = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.SOUTH, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.SOUTH, gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0),
		};

		Templates.layoutGridBagB(this, comp2s, gbc2s);
	}


	protected JPanel makeEditPanel()
	{
		JPanel p = new JPanel();
		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				if (source == okButton) {
					if (isAdd) {
						addNewAuxFile();
						cardview.show(bottomCardPanel, "blank");
						isAdd = false;
					}
					else if (isEdit) {
						if (getAuxFile(selectedRow) != null) {
							editAuxFile(getAuxFile(selectedRow));
						}

						cardview.show(bottomCardPanel, "blank");
						isEdit = false;
					}
				}
				else if (source == cancelButton) {
					if (isAdd) {
						cardview.show(bottomCardPanel, "blank");
						isAdd = false;
					}
					else if (isEdit) {
						cardview.show(bottomCardPanel, "blank");
						isEdit = false;
					}
				}
				else if (source == selectButton) {
					//pop up a file chooser
					JFileChooser chooser = new JFileChooser();
					if (chooser.showDialog(AuxFilesManagerPanel.this, "choose") != JFileChooser.APPROVE_OPTION)
						return;
					else {
						//if some file that is not that type is selected, we will assume people want other filetype
						String newFileName = chooser.getSelectedFile().getAbsolutePath(); // never empty
						filelocationField.setText(newFileName);
						nameField.setText(chooser.getSelectedFile().getName());
					}
				}
			}
		};
		okButton = Templates.makeButton("ok", actionListener);
		cancelButton = Templates.makeButton("cancel", actionListener);
		selectButton = Templates.makeButton("select", actionListener);

		JLabel nameLabel = Templates.makeLabel("name:");
		JLabel location = Templates.makeLabel("location:");

		nameField = Templates.makeTextField("");
		filelocationField = Templates.makeTextField("");
		filelocationField.setEditable(false);
		JPanel fillerPanel = new JPanel();

		JComponent[] comps = {
			nameLabel,
			nameField,
			location,
			filelocationField,
			selectButton,
			fillerPanel,
			okButton,
			cancelButton,
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 6, 3, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
		};

		Templates.layoutGridBagB(p, comps, gbcs);
		return p;
	}


	protected ListSelectionListener createSelectionListener()
	{
		return new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;

				ListSelectionModel lsm =
				        (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					selectedRow = -1;
					//no rows are selected
					removeButton.setEnabled(false);
					editButton.setEnabled(false);
				}
				else {
					selectedRow = lsm.getMinSelectionIndex();
					//selectedRow is selected
					removeButton.setEnabled(true);
					editButton.setEnabled(true);
				}
			}
		};
	}


	protected AbstractAuxFile getAuxFile(int index)
	{
		if (index >= 0 && index <= (((AbstractDomeModel) model).getAuxFiles().size())) {
			return (AbstractAuxFile) ((AbstractDomeModel) model).getAuxFiles().get(index);
		}
		return null;
	}

	protected void showedit()
	{
		AbstractAuxFile f = getAuxFile(selectedRow);
		nameField.setText(f.getName());
		filelocationField.setText(f.getFile().getPath());
		//setInfo
		cardview.show(bottomCardPanel, "edit");
	}

	protected void showadd()
	{
		//clear
		nameField.setText("");
		filelocationField.setText("");
		cardview.show(bottomCardPanel, "edit");
	}

	protected void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}

	protected void addNewAuxFile()
	{
		((AbstractDomeModel) model).addAuxFile(new CommonAuxFile(model, getNextId(), nameField.getText(), new File(filelocationField.getText())));
	}

	protected Id getNextId()
	{
		return new Id(UUIDGenerator.create());
	}

	protected void editAuxFile(AbstractAuxFile f)
	{
		f.setName(nameField.getText());
		f.setFile(new File(filelocationField.getText()));
		((AbstractDomeModel) model).setAuxFile(selectedRow, f);
	}


}
