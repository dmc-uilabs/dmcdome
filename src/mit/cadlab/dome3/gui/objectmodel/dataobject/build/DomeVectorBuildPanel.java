// DomeVectorBuildPanel.java
//   based on VectorPanel of 04/11/02
//   ver 0.1
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

//import mit.cadlab.dome3.gui.components.shared.*;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.gui.guiutils.FillDialog;
import mit.cadlab.dome3.gui.guiutils.VectorAddDialog;
import mit.cadlab.dome3.gui.guiutils.units.UnitChooser;
import mit.cadlab.dome3.gui.guiutils.units.UnitComboBoxModel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DomeVectorBasePanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DomeVectorTableModel;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * DomeVectorBuildPanel: extends basepanel,
 *  add listener to the radiobuttons and textinput and buttons.
 *  allow user edit size and property types
 *
 *
 */


public class DomeVectorBuildPanel extends DomeVectorBasePanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("VectorBuildPanel");
	public static final String XML_TAG = "vectorbuildpanel";

	// define components here
	//same components as basepanel

	protected static DomeVectorData answer = null;

	public static DomeVectorData showDialog(Component parent, DomeVectorData data)
	{

		DomeVectorBuildPanel editor = new DomeVectorBuildPanel(data);

		JDialog d = DialogFactory.createDialog(parent, "Dome Vector Build Panel", editor, true, true);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.pack();

		d.show();

		return editor.answer;  //return it
	}


	/*
	 * Constructors
	 */

	public DomeVectorBuildPanel(DomeVectorData v)
	{
		super(v);
		if (v == null) setDataModel_GUI(new DomeVectorData());
		propertyListener = createPropertyListener();
		dataVector.addPropertyChangeListener(propertyListener);
	}

	/* MAK: empty constructors cause COnstructor.newInstance() errors in DOME
	public DomeVectorBuildPanel()
	{
		super(new DomeVectorData());
		propertyListener = createPropertyListener();
		dataVector.addPropertyChangeListener(propertyListener);
	}
	*/
	
	protected PropertyChangeListener createPropertyListener()
	{
		return new VectorPropertyChangeListener();
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
				VectorAddDialog.showDialog(vectorScrollPane, (DomeVectorTableModel) vectorTable.getModel(), vectorTable.getSelectedRows(), vectorTable.getSelectedColumns());

			}
		});
		/*OkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				answer=dataVector;
				dispose();
			}
			});*/
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (dataVector.isRowVector()) {//columns
					dataVector.removeItems(vectorTable.getSelectedColumns());

				} else {//rows
					dataVector.removeItems(vectorTable.getSelectedRows());

				}


			}
		});
		fillButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Double value = FillDialog.showValueInput(vectorScrollPane, dataVector.getInitialValue());
				if (value == null) return;
				if (dataVector.isRowVector()) {//columns
					dataVector.fillItems(vectorTable.getSelectedColumns(), value);
				} else {//rows
					dataVector.fillItems(vectorTable.getSelectedRows(), value);
				}
			}
		});
		fixSizeBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dataVector.setFixedSize(fixSizeBox.isSelected());
				addButton.setEnabled(!fixSizeBox.isSelected());
				deleteButton.setEnabled(!fixSizeBox.isSelected());

			}
		});
		sizeTextInput.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int size = Integer.parseInt(sizeTextInput.getText());
				dataVector.setSize(size);
				sizeTextInput.setCurrent();
			}
		});
		rowRadioButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (rowRadioButton.isSelected()) {
					dataVector.setRowVector(true);
				}
			}
		});
		columnRadioButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (columnRadioButton.isSelected()) {
					dataVector.setRowVector(false);
				}
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
					dataVector.setUnit(getUnit());
			}
		});

		flavorComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (flavorComboBox.getSelectedIndex() == 0) {//"real"
					dataVector.setValueType("real");
					debug("type set to real");
				} else {
					dataVector.setValueType("integer");
					debug("type set to integer");
				}

			}
		});
		if(dataVector.getInitialValue() instanceof Double)
			flavorComboBox.setSelectedIndex(0);
		else
			flavorComboBox.setSelectedIndex(1);
	}

	protected void changeListAction()
	{
		Unit choice = UnitChooser.showDialog(null, dataVector.getUnit());
		if (choice == null)
			unitComboBox.setSelectedObject(dataVector.getUnit()); // back to initial choice
		else
			dataVector.setUnit(choice);
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
		JFrame f = Templates.makeTestFrame("Vector build Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		// MAK: remove "new DomeVectorData()" to invoke empty constructor
		f.getContentPane().add(new DomeVectorBuildPanel(new DomeVectorData()), BorderLayout.CENTER);
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
			System.out.println("DomeVectorBuildPanel: " + msg);
	}


	protected class VectorPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.SIZE)) {
				sizeTextInput.setText(String.valueOf(newValue));
				sizeTextInput.setCurrent();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.UNIT)) {
				setUnit((Unit) newValue);
			}
		}
	}

}


