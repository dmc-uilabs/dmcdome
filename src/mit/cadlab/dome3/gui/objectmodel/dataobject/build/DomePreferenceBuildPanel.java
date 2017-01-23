// DomePreferenceBuildPanel.java
//   based on MatrixBuildPanel of _______
//   ver 0.1

package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.gui.guiutils.FillDialog;
import mit.cadlab.dome3.gui.guiutils.PreferenceAddDialog;
import mit.cadlab.dome3.gui.guiutils.DeletePreferenceDialog;
import mit.cadlab.dome3.gui.guiutils.units.UnitChooser;
import mit.cadlab.dome3.gui.guiutils.units.UnitComboBoxModel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DomePreferenceBasePanel;
import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DomePreferenceBuildPanel: extends basepanel,
 *  add listener to the radiobuttons and textinput and buttons.
 *  allow user edit size and property types
 *
 *
 */

public class DomePreferenceBuildPanel extends DomePreferenceBasePanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("PreferenceBuildPanel");
	public static final String XML_TAG = "preferencebuildpanel";

	// define components here
	//same components as basepanel

	protected static DomePreferenceData answer = null;

	public static DomePreferenceData showDialog(Component parent, DomePreferenceData data)
	{
		DomePreferenceBuildPanel editor = new DomePreferenceBuildPanel(data);

		JDialog d = DialogFactory.createDialog(parent, "Dome Preference Build Panel", editor, true, true);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.pack();

		d.show();

		return editor.answer;  //return it
	}

	/*
	 * Constructors
	 */

	public DomePreferenceBuildPanel(DomePreferenceData v)
	{
		super(v);
		if (v == null) setDataModel_GUI(new DomePreferenceData());
	}

	public DomePreferenceBuildPanel()
	{
		super(new DomePreferenceData());
	}

	protected void configureComponents()
	{
		constraintsButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

			}
		});

		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PreferenceAddDialog.showDialog(preferenceScrollPane, dataPreference, preferenceTable.getSelectedRows(), preferenceTable.getSelectedColumns());

			}
		});

		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DeletePreferenceDialog.showDialog(preferenceScrollPane, dataPreference, preferenceTable.getSelectedColumns(), preferenceTable.getSelectedRows());

			}
		});

		fillButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Double value = FillDialog.showValueInput(preferenceScrollPane, dataPreference.getInitialValue());
				if (value == null) return;
				dataPreference.fillItems(preferenceTable.getSelectedPoints(), value);
			}
		});

		fixSizeBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dataPreference.setFixedSize(fixSizeBox.isSelected());

				addButton.setEnabled(!fixSizeBox.isSelected());
				deleteButton.setEnabled(!fixSizeBox.isSelected());

			}
		});

		rowsTextInput.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = Integer.parseInt(rowsTextInput.getText());
				int column = Integer.parseInt(columnsTextInput.getText());
				if (row != 0 && column == 0) column = 1;
				dataPreference.setRowCount(row);

				dataPreference.setColumnCount(column);
				columnsTextInput.setCurrent();
				rowsTextInput.setCurrent();
			}
		});

		columnsTextInput.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = Integer.parseInt(rowsTextInput.getText());
				int column = Integer.parseInt(columnsTextInput.getText());
				if (row == 0 && column != 0) row = 1;
				dataPreference.setRowCount(row);

				dataPreference.setColumnCount(column);
				columnsTextInput.setCurrent();
				rowsTextInput.setCurrent();
			}
		});

		unitComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object choice = unitComboBox.getSelectedItem();
				if (choice instanceof String && UnitComboBoxModel.CHANGE_LIST.equals(choice))
					changeListAction();
				else
					dataPreference.setUnit(getUnit());
			}
		});

		flavorComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (flavorComboBox.getSelectedIndex() == 0) {//"real"
					dataPreference.setValueType("real");
					debug("type set to real");
				} else {
					dataPreference.setValueType("integer");
					debug("type set to integer");
				}

			}
		});

	}


	protected void changeListAction()
	{
		Unit choice = UnitChooser.showDialog(null, dataPreference.getUnit());
		if (choice == null)
			unitComboBox.setSelectedObject(dataPreference.getUnit()); // back to initial choice
		else
			dataPreference.setUnit(choice);
	}

	public Unit getUnit()
	{
		return (Unit) unitComboBox.getSelectedItem();
	}

	public void setUnit(Unit unit)
	{
		unitComboBox.setModel(new UnitComboBoxModel(unit, true));
	}


	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("Preference build Panel");
		DomePreferenceData d4 = new DomePreferenceData(5, 3, false, new Double(0));

		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 3; j++) {
				d4.setItem(i, j, new Double(i - 0.1));

			}
		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		DomePreferenceBuildPanel p = new DomePreferenceBuildPanel();
		f.getContentPane().add(p, BorderLayout.CENTER);
		p.setDataObject(d4);
		f.pack();
		f.setVisible(true);
	}

	private void dispose()
	{
		SwingUtilities.windowForComponent(this).dispose();
	}

	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("DomePreferenceBuildPanel: " + msg);
	}

}

