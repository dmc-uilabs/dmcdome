// MatrixAddDialog.java
//  ver 0.1 May 23,2002
//   inherit from the basepanel which already has layout the GUI

package mit.cadlab.dome3.gui.guiutils;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.gui.objectmodel.dataobject.AddDialogBasePanel;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MatrixAddDialog extends AddDialogBasePanel
{


	DomeMatrixData Model;
	int[] selectedRows;
	int[] selectedColumns;
	Class m_modelDataClass;


	public static void showDialog(Component parent, DomeMatrixData model, int[] rows, int[] columns)
	{

		MatrixAddDialog editor = new MatrixAddDialog(model, rows, columns);

		JDialog d = DialogFactory.createDialog(parent, "Add", editor, true, false);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		d.show();


	}


	public MatrixAddDialog(DomeMatrixData Model, int[] rows, int[] columns)
	{
		super(Model.getInitialValue().getClass());
		this.Model = Model;
		this.selectedRows = rows;
		this.selectedColumns = columns;
		m_modelDataClass = Model.getInitialValue().getClass();

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


		String s = Model.getInitialValue().toString();
		valueTextField.setText(s);

		rowRadioButton.setSelected(true);
		columnRadioButton.setSelected(false);


		valueTextField.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

	private void addValue(int size, Number value)
	{
		if (rowRadioButton.isSelected()) {
			if (startRadioButton.isSelected()) {//add to the start
				Model.addRowItems(0, size, value);
			} else if (endRadioButton.isSelected()) {//add to the end
				Model.addRowItems(Model.getRowCount(), size, value);
			} else if (beforeRadioButton.isSelected()) {//add to the least selection
				if (min(selectedRows) == -1) //not selected any row or column
					Model.addRowItems(Model.getRowCount(), size, value); //add to end by default
				else
					Model.addRowItems(min(selectedRows), size, value);
			}
		} else if (columnRadioButton.isSelected()) {

			if (startRadioButton.isSelected()) {//add to the start
				Model.addColumnItems(0, size, value);
			} else if (endRadioButton.isSelected()) {//add to the end
				Model.addColumnItems(Model.getColumnCount(), size, value);
			} else if (beforeRadioButton.isSelected()) {//add to the least selection
				if (min(selectedColumns) == -1) //not selected any row or column
					Model.addColumnItems(Model.getColumnCount(), size, value); //add to end by default
				else
					Model.addColumnItems(min(selectedColumns), size, value);
			}
		}

	}


	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("Matrix AddDialog: " + msg);
	}
}
