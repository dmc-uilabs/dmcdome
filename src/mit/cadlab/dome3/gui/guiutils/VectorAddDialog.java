// VectorAddDialog.java
//  ver 0.1 May 23,2002
//   inherit from the basepanel which already has layout the GUI

package mit.cadlab.dome3.gui.guiutils;

import mit.cadlab.dome3.gui.objectmodel.dataobject.AddDialogBasePanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DomeVectorTableModel;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class VectorAddDialog extends AddDialogBasePanel
{
	DomeVectorTableModel tableModel;
	int[] selectedRows;
	int[] selectedColumns;
	Class m_modelDataClass;


	public static void showDialog(Component parent, DomeVectorTableModel tablemodel, int[] rows, int[] columns)
	{
		VectorAddDialog editor = new VectorAddDialog(tablemodel, rows, columns);

		JDialog d = DialogFactory.createDialog(parent, "Add", editor, true, false);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		d.show();
	}


	public VectorAddDialog(DomeVectorTableModel tableModel, int[] rows, int[] columns)
	{
		super(tableModel.getData().getInitialValue().getClass());
		this.tableModel = tableModel;
		this.selectedRows = rows;
		this.selectedColumns = columns;
		m_modelDataClass = tableModel.getData().getInitialValue().getClass();

		configureComponents();
	}


	protected void configureComponents()
	{
		OkButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				processValue();
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				dispose();
			}
		});


		if (tableModel instanceof DomeVectorTableModel) {
			String s = ((DomeVectorTableModel) tableModel).getData().getInitialValue().toString();

			valueTextField.setText(s);

			boolean isRowVector = ((DomeVectorTableModel) tableModel).isRowVector();

			rowRadioButton.setSelected(!isRowVector);
			columnRadioButton.setSelected(isRowVector);

			setRowColumnMode(!isRowVector, isRowVector);
		}

		valueTextField.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					//same as okay
					processValue();
					dispose();
				}
			}
		});

	}

	/**
	 * Read the new value and update the model.
	 */
	private void processValue()
	{
		if (tableModel instanceof DomeVectorTableModel) {
			Number value;
			int size;
			try {
				if (m_modelDataClass == Double.class)
					value = new Double(Double.parseDouble(valueTextField.getText()));
				else
					value = new Integer(Integer.parseInt(valueTextField.getText()));
				size = Integer.parseInt(sizeTextField.getText());
			} catch (Exception ee) {
				return;//just return
			}
			addValue(size, value);
		}
	}


	private void addValue(int size, Number value)
	{
		if (startRadioButton.isSelected()) {//add to the start
			tableModel.addRowsOrColumns(0, size, value);
		} else if (endRadioButton.isSelected()) {//add to the end
			tableModel.addRowsOrColumns(tableModel.getData().getSize(), size, value);
		} else if (beforeRadioButton.isSelected()) {//add to the least selection
			if (tableModel.isRowVector())
				if (min(selectedColumns) == -1)
					tableModel.addRowsOrColumns(tableModel.getColumnCount(), size, value); //add to end by default
				else
					tableModel.addRowsOrColumns(min(selectedColumns), size, value);
			else if (min(selectedRows) == -1)
				tableModel.addRowsOrColumns(tableModel.getRowCount(), size, value); //add to end by default
			else
				tableModel.addRowsOrColumns(min(selectedRows), size, value);
		}
	}


	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("AddDialog: " + msg);
	}
}
