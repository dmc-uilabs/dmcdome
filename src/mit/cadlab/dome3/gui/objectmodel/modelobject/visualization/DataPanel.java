// DataPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.DomeObjectSet;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
import mit.cadlab.dome3.gui.guiutils.ModelObjectsComboBoxListModel;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import edu.iupui.rg.ucum.units.Unit;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Vector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 *
 */
public class DataPanel extends JPanel implements ActionListener
{

	static GridBagConstraints gbc;
	protected PropertyChangeListener propertyListener;

	protected JTable dataSourceTable;
	protected JButton upButton;
	protected JButton downButton;

	protected JButton addButton;
	protected JButton removeButton;
	protected JButton doneButton;
	protected JButton cancelButton;

	//configuration area components
	protected JComboBox dataSetComboBox;
	protected JButton editSetButton;
	protected JRadioButton rowButton;
	protected JRadioButton colButton;
	protected JComboBox horizontalAxisComboBox;
	protected JCheckBox domeNamesCheckBox;
	protected JComboBox seriesNamesComboBox;
	//protected JButton closeButton;
	protected JButton changeSeriesNameButton;

	protected Visualization vis;
	protected Vector sets;
	protected int formerIndex;

	public static JDialog makeDataDialog(Visualization v)
	{

		JDialog d = Templates.makeTestDialog("Data Panel");
		d.getContentPane().add(new DataPanel(v));
		d.pack();
		return d;
	}

	public DataPanel(Visualization v)
	{
		//load data
		this.vis = v;
		propertyListener = getPropertyListener();
		//**v.addPropertyChangeListener(propertyListener);
		//**sets = (Vector) vis.getSetsList().clone();
		sets = new Vector();
		for (int i = 0; i < vis.getSetsList().size(); i ++) {
			DomeObjectSet newSet = new DomeObjectSet((DomeObjectSet)vis.getSetsList().get(i));
            newSet.addPropertyChangeListener(propertyListener);
            sets.addElement(newSet);
		}

		formerIndex = vis.getSelectedSetIndex();
		//make GUI
		makeGUI();


	}

	protected void makeGUI()
	{
		JLabel sourceLabel = Templates.makeLabel("data series in data set:");

		JLabel configurationLabel = Templates.makeLabel("data set name:");

		addButton = Templates.makeButton("Add");
        addButton.addActionListener(this);
		removeButton = Templates.makeButton("Remove");
        removeButton.addActionListener(this);
		doneButton = Templates.makeButton("Done");
        doneButton.addActionListener(this);
		cancelButton = Templates.makeButton("Cancel");
        cancelButton.addActionListener(this);

		dataSetComboBox = Templates.makeDComboBox(makeDataSetComboBoxModel());
		dataSetComboBox.addActionListener(this);

		//editSetButton = Templates.makeButton("edit");
		//editSetButton.addActionListener(this);

		editSetButton = Templates.makeButton("Edit...");
		editSetButton.addActionListener(this);

		JPanel configurationPanel = new JPanel();
		configurationPanel.setBackground(Color.lightGray);
		configurationPanel.setLayout(new GridBagLayout());
		JLabel seriesLabel = Templates.makeLabel("data is in:");
		rowButton = Templates.makeRadioButton("rows", false);
		rowButton.addActionListener(this);
		//**rowButton.setBackground(Color.lightGray);
		rowButton.setEnabled(false);
		colButton = Templates.makeRadioButton("columns", false);
		colButton.addActionListener(this);
		//**colButton.setBackground(Color.lightGray);
		colButton.setEnabled(false);

		//ButtonGroup rowColGroup = new ButtonGroup();
		//rowColGroup.add(rowButton);
		//rowColGroup.add(colButton);


		setRowColButton();

		JPanel rowColPanel = new JPanel();
		rowColPanel.setLayout(new GridBagLayout());
		//**rowColPanel.setBackground(Color.lightGray);

		JComponent[] rcComps = {seriesLabel, rowButton, colButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] rcGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(rowColPanel, rcComps, rcGbcs);

		dataSourceTable = new JTable(makeDataSourceTableModel());
		dataSourceTable.setRowSelectionAllowed(true);
		dataSourceTable.setColumnSelectionAllowed(false);
		dataSourceTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataSourceTable.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane sourceScrollPane = new JScrollPane(dataSourceTable);

        JPanel addRemovePanel = new JPanel();
        addRemovePanel.setLayout(new GridBagLayout());

        JComponent[] arComps = {addButton, removeButton};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        // in the order of the comps array
        GridBagConstraints[] arGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
        Templates.layoutGridBag(addRemovePanel, arComps, arGbcs);

        JPanel doneCancelPanel = new JPanel();
        doneCancelPanel.setLayout(new GridBagLayout());

        JComponent[] dcComps = {doneButton, cancelButton};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        // in the order of the comps array
        GridBagConstraints[] dcGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
        Templates.layoutGridBag(doneCancelPanel, dcComps, dcGbcs);

		JComponent[] cComps = {addRemovePanel, configurationLabel, dataSetComboBox, rowColPanel,sourceLabel,
		                       editSetButton, sourceScrollPane, doneCancelPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] cGbcs = {
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 0, 0, 10), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 0, 0, 10), 0, 0),
			new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 0, 0, 10), 0, 0),
			new GridBagConstraints(0, 4, 3, 2, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 10, 0, 10), 0, 0),
			new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 10, 10), 0, 0)
		};
		Templates.layoutGridBag(this, cComps, cGbcs);

		Dimension d = new Dimension(650, 450);
		this.setPreferredSize(d);

		dataSetComboBox.setSelectedIndex(formerIndex);


	}


	public ComboBoxModel makeDataSetComboBoxModel()
	{
		return new DefaultComboBoxModel(sets);
	}

	public ComboBoxModel makeHorizontalAxisComboBoxModel()
	{

		int index = dataSetComboBox.getSelectedIndex();
		if (index == -1)
			return new ModelObjectsComboBoxListModel();
		else {
			DomeObjectSet dos = (DomeObjectSet) sets.get(index);
			return new ModelObjectsComboBoxListModel(dos.getSelectedSetItems());
		}
	}

	public ComboBoxModel makeSeriesNamesComboBoxModel()
	{
		int index = dataSetComboBox.getSelectedIndex();
		if (index == -1)
			return new DefaultComboBoxModel();
		else {
			DomeObjectSet dos = (DomeObjectSet) sets.get(index);


			DomeObject[] alldata = dos.getSelectedSetItems();

			ArrayList yaxis = new ArrayList();
			DomeObject xaxis = (DomeObject) horizontalAxisComboBox.getSelectedItem();


			if (dos.isUsingDomeName()) {
				for (int i = 0; i < alldata.length; i++) {
					if (!alldata[i].equals(xaxis)) yaxis.add(alldata[i]);
				}

				return new ModelObjectsComboBoxListModel(yaxis.toArray());
			} else {
				for (int i = 0; i < alldata.length; i++) {
					if (!alldata[i].equals(xaxis)) yaxis.add(dos.getAlias(alldata[i]));
				}

				return new DefaultComboBoxModel(yaxis.toArray());
			}

		}
	}


	/**
	 *
	 * @return    0: not choose 1: choose row, -1: choose column
	 */
	protected int isRowOrColumn()
	{
		int index = dataSetComboBox.getSelectedIndex();
		if (index == -1) return 0;
		DomeObjectSet dos = (DomeObjectSet) sets.get(index);

		return dos.getRowsOrColumns();
	}


	protected void setRowColButton()
	{

		int choice;
		if (dataSetComboBox.getSelectedIndex() == -1) {
			rowButton.setSelected(false);
			colButton.setSelected(false);
			rowButton.setEnabled(false);
			colButton.setEnabled(false);
		}
		else {
			DomeObjectSet dos = (DomeObjectSet) sets.get(dataSetComboBox.getSelectedIndex());
            if(dos.getRowsOrColumns() == 0) {
	            rowButton.setSelected(false);
	            colButton.setSelected(false);
            }
			if(dos.getRowsOrColumns() == 1) {
				rowButton.setSelected(true);
				colButton.setSelected(false);
			}
			if(dos.getRowsOrColumns() == 2) {
				rowButton.setSelected(false);
				colButton.setSelected(true);
			}

            if(dos.isRowsAndColumns() == true) {
	            rowButton.setEnabled(true);
	            colButton.setEnabled(true);
            } else {
	            rowButton.setEnabled(false);
	            colButton.setEnabled(false);
            }
		}

	}

	public void actionPerformed(ActionEvent e)
	{
        if (e.getSource() == dataSetComboBox) {
			dataSetComboBox_clicked();
		} else if (e.getSource() == addButton) {
			addSetButton_clicked();
		} else if (e.getSource() == removeButton) {
			removeSetButton_clicked();
		} else if (e.getSource() == editSetButton) {
			editSetButton_clicked();
		} else if (e.getSource() == doneButton) {
			doneButton_clicked();
		} else if (e.getSource() == cancelButton) {
			cancelButton_clicked();
		} else if (e.getSource() == rowButton) {
			if (dataSetComboBox.getSelectedIndex() != -1) {
				DomeObjectSet dos = (DomeObjectSet) sets.get(dataSetComboBox.getSelectedIndex());
				dos.setRowsOrColumns(1);
				dataSourceTable.setModel(makeDataSourceTableModel());
				colButton.setSelected(false);
			}
		} else if (e.getSource() == colButton) {
			if (dataSetComboBox.getSelectedIndex() != -1) {
				DomeObjectSet dos = (DomeObjectSet) sets.get(dataSetComboBox.getSelectedIndex());
				dos.setRowsOrColumns(2);
				dataSourceTable.setModel(makeDataSourceTableModel());
				rowButton.setSelected(false);
			}
		}
	}

	protected void addSetButton_clicked()
	{
		String name = "set " + sets.size();
		sets.add(new DomeObjectSet(name, propertyListener));
		dataSetComboBox.setModel(makeDataSetComboBoxModel());
        formerIndex = sets.size() - 1;
		dataSetComboBox.setSelectedIndex(sets.size() - 1);//put it to the latest one
	}

	protected void removeSetButton_clicked()
	{
		int index = dataSetComboBox.getSelectedIndex();
		if (index != -1) {
			sets.removeElementAt(index);
			dataSetComboBox.setModel(makeDataSetComboBoxModel());
			if (index < sets.size()) {
                formerIndex = index;
				dataSetComboBox.setSelectedIndex(index);
			}
			else {
			    formerIndex = sets.size() - 1;
				dataSetComboBox.setSelectedIndex(sets.size() - 1);
			}
		}


	}

	protected void editSetButton_clicked()
	{
		JDialog dialog = DialogFactory.createDialog(this, "Edit data for data set: "+ ((DomeObjectSet)sets.get(dataSetComboBox.getSelectedIndex())).getName(), new EditPanel(vis,(DomeObjectSet)sets.get(dataSetComboBox.getSelectedIndex())), true, true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.show();
	}

    protected void doneButton_clicked()
    {
        vis.setSetsList(sets);
	    vis.setSelectedSet(formerIndex);
        this.dispose();
    }

    protected void cancelButton_clicked()
    {
       this.dispose();
    }

    private void dispose()
    {
        SwingUtilities.windowForComponent(this).dispose();
    }

	protected void dataSetComboBox_clicked()
	{
		//reload combobox model
		//**horizontalAxisComboBox.setModel(makeHorizontalAxisComboBoxModel());
		//**seriesNamesComboBox.setModel(makeSeriesNamesComboBoxModel());


		//make checkbox
		int index = dataSetComboBox.getSelectedIndex();
		if (index == -1 && index != formerIndex) {        // after rename!!
		    if (!dataSetComboBox.getSelectedItem().toString().trim().equals("") ) {
                ((DomeObjectSet) sets.get(formerIndex)).setName(dataSetComboBox.getSelectedItem().toString());
			    dataSetComboBox.setModel(makeDataSetComboBoxModel());
		    }
			dataSetComboBox.setSelectedIndex(formerIndex);
            return;
		}

        if (index == -1) {
	        dataSetComboBox.setEditable(false);
            removeButton.setEnabled(false);
	        editSetButton.setEnabled(false);
        }
		else {
	        dataSetComboBox.setEditable(true);
	        removeButton.setEnabled(true);
	        editSetButton.setEnabled(true);
        }

		dataSourceTable.setModel(makeDataSourceTableModel());
        formerIndex = index;
		setRowColButton();
	}

	public AbstractTableModel makeDataSourceTableModel()
	{
		int index = dataSetComboBox.getSelectedIndex();
		if (index == -1)
			return new DefaultTableModel();
		else {
			DomeObjectSet dos = (DomeObjectSet) sets.get(index);
			return new visTableModel(dos);
		}
	}

	private void debug(String msg)
	{
		boolean debug = true;
		if (debug)
			System.out.println("Data Dialog: " + msg);
	}

	public void repaintToSetChange(String changeoption)
	{
		if (changeoption.equals(DomeObjectSet.SELECTIONCHANGED)) {
			//repaint set
			dataSetComboBox_clicked();
		} else if (changeoption.equals(DomeObjectSet.SERIESNAMECHANGED)) {
			//do nothing
		} else if (changeoption.equals(DomeObjectSet.SETCHANGED)) {
			//repaint set
			dataSetComboBox_clicked();
		}


	}


	protected PropertyChangeListener getPropertyListener()
	{
		return new DataPanel.DataPanelPropertyChangeListener();
	}

	class DataPanelPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
            System.out.println("get in!!!");
/**			if (property.equals(Visualization.SETSCHANGED)) {
				debug("data panel catch changes");
				dataSetComboBox.setModel(makeDataSetComboBoxModel());


				//repaint table
				dataSourceTable.setModel(makeDataSourceTableModel());
				//repaint x-axis combobox
				horizontalAxisComboBox.setModel(makeHorizontalAxisComboBoxModel());
				//repaint y-axis combobox
				seriesNamesComboBox.setModel(makeSeriesNamesComboBoxModel());


				if (dataSetComboBox.getModel().getSize() > 0)
					dataSetComboBox.setSelectedIndex(0); //by defualt pick the first one
			} else if (property.equals(Visualization.SETSELECTIONCHANGED)) {
				if (vis.getSelectedSet() == (DomeObjectSet) dataSetComboBox.getSelectedItem()) {

				} else {
					dataSetComboBox.setSelectedItem(vis.getSelectedSet());
				}

			} **/
            if (property.equals(DomeObjectSet.SETCONTENTCHANGED)) {
                if (((DomeObjectSet)sets.get(dataSetComboBox.getSelectedIndex())).getHorizontalIndex() > ((DomeObjectSet)sets.get(dataSetComboBox.getSelectedIndex())).getSeriesSize() -1)
                {    if (((DomeObjectSet)sets.get(dataSetComboBox.getSelectedIndex())).getSeriesSize() == 0)
                        ((DomeObjectSet)sets.get(dataSetComboBox.getSelectedIndex())).setHorizontalIndex(-1);
                    else ((DomeObjectSet)sets.get(dataSetComboBox.getSelectedIndex())).setHorizontalIndex(0);
                }
                dataSourceTable.setModel(makeDataSourceTableModel());
		       setRowColButton();
               dataSetComboBox.getParent().repaint();
            }
		}
	}

	class visTableModel extends AbstractTableModel
	{
		DomeObjectSet dataSet;
		//**protected visTableModel.ModelObjectListener nameListener = new visTableModel.ModelObjectListener();

		public visTableModel(DomeObjectSet obj)
		{
            dataSet = obj;
		}

		public int getColumnCount()
		{
			return 4;
		}

		public int getRowCount()
		{
			return dataSet.getSeriesSize();
		}

		public String getColumnName(int column)
		{
			if (column == 0)
				return "Source (Dome Name)";
			else if (column == 1)
				return "Series Name";
			else if (column == 2)
				return "Value";
			else if (column == 3)
				return "Horizontal Axis";
			else
				return null;
		}

		public Object getValueAt(int row, int col)
		{
			if (col == 0)  return dataSet.getSeriesName(row);
            if (col == 1)  return dataSet.getSeriesAlias(row);
			if (col == 2) {
				DomeObject obj =  dataSet.getBySeries(row);
				if ((obj instanceof Parameter) && (((Parameter) obj).getDataObjectForType("Vector") != null)) {
					DomeVectorData d = (DomeVectorData) (((Parameter) obj).getDataObjectForType("Vector"));
					int count = d.getSize();
					Unit unit = d.getUnit();
					return count + " elements    " + "unit: " + unit;
				} else if ((obj instanceof Parameter) && (((Parameter) obj).getDataObjectForType("Matrix") != null)) {
					DomeMatrixData d = (DomeMatrixData) (((Parameter) obj).getDataObjectForType("Matrix"));
                    int count = 0;
                    if(dataSet.getRowsOrColumns() == 1) count = d.getColumnCount();
					if(dataSet.getRowsOrColumns() == 2) count = d.getRowCount();
					Unit unit = d.getUnit();
                    return count + " elements    " + "unit: " + unit;
                } else if ((obj instanceof Parameter) && (((Parameter) obj).getDataObjectForType("Preference") != null)) {
	                DomePreferenceData d = (DomePreferenceData) (((Parameter) obj).getDataObjectForType("Preference"));
	                int count = 0;
	                if (dataSet.getRowsOrColumns() == 1) count = d.getColumnCount();
	                if (dataSet.getRowsOrColumns() == 2) count = d.getRowCount();
	                Unit unit = d.getUnit();
	                return count + " elements    " + "unit: " + unit;
                } else  return null;
			}
            if (col == 3)  {
                if (row == dataSet.getHorizontalIndex()) return (new Boolean(true));
                else return (new Boolean(false));
            }
			return null;
		}

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col == 0) return false;
            if (col == 1) return true;
            if (col == 2) return false;
            if (col == 3) return true;
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 1) {
	            dataSet.setSeriesAlias((String)value, row);
	            //((ConcreteVisualization)vis).setRowName(dataSetComboBox.getSelectedIndex(),row,(String)value);
            }
            if (col == 3 ) {
                if(((Boolean)value).booleanValue() == true) {
                    int former = dataSet.getHorizontalIndex();
                    dataSet.setHorizontalIndex(row);
                    fireTableCellUpdated(former, col);
                } else dataSet.setHorizontalIndex(-1);

            }
            fireTableCellUpdated(row, col);

        }

	}


}


