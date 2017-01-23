// DomeMatrixBuildPanel.java
//   based on MatrixPanel of 04/11/02
//   ver 0.1
//   ver 0.2  inherit GUI features from the base Panel

package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.gui.guiutils.DeleteDialog;
import mit.cadlab.dome3.gui.guiutils.FillDialog;
import mit.cadlab.dome3.gui.guiutils.MatrixAddDialog;
import mit.cadlab.dome3.gui.guiutils.units.UnitChooser;
import mit.cadlab.dome3.gui.guiutils.units.UnitComboBoxModel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DomeMatrixBasePanel;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * DomeMatrixBuildPanel: extends basepanel,
 *  add listener to the radiobuttons and textinput and buttons.
 *  allow user edit size and property types
 *
 *
 */

public class DomeMatrixBuildPanel extends DomeMatrixBasePanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("MatrixBuildPanel");
	public static final String XML_TAG = "matrixbuildpanel";

	// define components here
	//same components as basepanel

	protected static DomeMatrixData answer = null;

	public static DomeMatrixData showDialog(Component parent, DomeMatrixData data)
	{
        // sangmok : added 'final' to be accessible from inner class. seems not to create any problem, becuase it is never changed even after the editor instance is passed to createDialog()
		final DomeMatrixBuildPanel editor = new DomeMatrixBuildPanel(data);

		JDialog d = DialogFactory.createDialog(parent, "Dome Matrix Build Panel", editor, true, true);

		// sangmok: when window closes there is oen more thing to do: releasing data object
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		d.pack();

		d.show();

		return editor.answer;  //return it

	}

	/*
	 * Constructors
	 */

	public DomeMatrixBuildPanel(DomeMatrixData v)
	{
		super(v);
		if (v == null) setDataModel_GUI(new DomeMatrixData());
	}

	/* MAK: public DomeMatrixBuildPanel()
	{
		super(new DomeMatrixData());
	}
*/
	
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
				MatrixAddDialog.showDialog(matrixScrollPane, dataMatrix, matrixTable.getSelectedRows(), matrixTable.getSelectedColumns());

			}
		});

		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DeleteDialog.showDialog(matrixScrollPane, dataMatrix, matrixTable.getSelectedColumns(), matrixTable.getSelectedRows());

			}
		});

		fillButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Double value = FillDialog.showValueInput(matrixScrollPane, dataMatrix.getInitialValue());
				if (value == null) return;
				dataMatrix.fillItems(matrixTable.getSelectedPoints(), value);
			}
		});

		fixSizeBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dataMatrix.setFixedSize(fixSizeBox.isSelected());

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
				dataMatrix.setRowCount(row);

				dataMatrix.setColumnCount(column);
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
				dataMatrix.setRowCount(row);

				dataMatrix.setColumnCount(column);
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
					dataMatrix.setUnit(getUnit());
			}
		});

		flavorComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (flavorComboBox.getSelectedIndex() == 0) {//"real"
					dataMatrix.setValueType("real");
					debug("type set to real");
				} else {
					dataMatrix.setValueType("integer");
					debug("type set to integer");
				}

			}
		});

	}


	protected void changeListAction()
	{
		Unit choice = UnitChooser.showDialog(null, dataMatrix.getUnit());
		if (choice == null)
			unitComboBox.setSelectedObject(dataMatrix.getUnit()); // back to initial choice
		else
			dataMatrix.setUnit(choice);
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
		JFrame f = Templates.makeTestFrame("Matrix build Panel");
		DomeMatrixData d4 = new DomeMatrixData(5, 3, false, new Double(0));

		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 3; j++) {
				d4.setItem(i, j, new Double(i - 0.1));

			}
		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		// MAK: remove "new DomeMatrixData()" below
		DomeMatrixBuildPanel p = new DomeMatrixBuildPanel(new DomeMatrixData());
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
			System.out.println("DomeMatrixBuildPanel: " + msg);
	}

}

