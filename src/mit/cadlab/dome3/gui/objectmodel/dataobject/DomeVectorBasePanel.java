// DomeVectorBasePanel.java
//   based on VectorPanel of 04/11/02
//   ver 0.1
package mit.cadlab.dome3.gui.objectmodel.dataobject;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
//import mit.cadlab.dome3.gui.components.shared.*;
import mit.cadlab.dome3.swing.*;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.gui.guiutils.table.TableSelection;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.units.UnitComboBoxModel;
import mit.cadlab.dome3.gui.guiutils.treetable.Editors;
import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;


/**
 * DomeVectorBasePanel: as the base panel for build and run DomeVector GUI
 *   as a property listener listen to the DomeVector property changes except those caught by tablemodel
 *
 *
 */


public class DomeVectorBasePanel extends DataObjectPanel
{

	GridBagConstraints gbc;

	protected PropertyChangeListener propertyListener;
	protected DomeVectorData dataVector = null;

	// define components here
	protected DTextField sizeTextInput;
	protected JLabel sizeLabel;
	protected JPanel comboboxPanel;
	protected DComboBox unitComboBox;
	protected DComboBox flavorComboBox;
	protected JCheckBox fixSizeBox;
	protected JButton constraintsButton;
	protected JButton addButton;
	protected JButton deleteButton;
	//JButton OkButton; not needed as the main wrapper for the parameter provides OK and cancel
	//JButton cancelButton; not needed as the main wrapper for the parameter provides OK and cancel
	protected JPanel sizePanel;
	protected JButton fillButton;
	protected JPanel rowColButtonGroupPanel;
	protected JPanel topRowFillerPanel;
	protected JScrollPane vectorScrollPane;
	protected JTable vectorTable;
	protected JList rowHeader;
	protected JViewport viewport;
	protected JPanel buttonsPanel;

    // sangmok : fix start
    // protected DomeVectorTableModel vectorTableModel;
    // make vectorTableModel no more a member variable of this class
    // current TableModel is accessible through matrixTable.getModel()
    // if we leave matrixTableModel as a member variable, it should be reassigned whenever we assign new matrixTable.setModel(newTableModel)
    // sangmok : fix end


	protected JRadioButton rowRadioButton;
	protected JRadioButton columnRadioButton;
	protected ButtonGroup group;


	//String[] unitModel = {"no units", "options ..."};
	protected String[] flavourModel = {"real", "integer"};





	/*
	 * Constructors
	 */

	public DomeVectorBasePanel(DomeVectorData dataVect)
	{
//        this(500, 150, dataVect);
		this(450, 250, dataVect);
	}

	public DomeVectorBasePanel()
	{
//        this(500, 150);
		this(450, 250);
	}

	public DomeVectorBasePanel(int PanelWidth, int PanelHeight)
	{
		this(PanelWidth, PanelHeight, null);

	}


	public DomeVectorBasePanel(int PanelWidth, int PanelHeight, DomeVectorData dataVect)
	{
		dataVector = dataVect;
		setPreferredSize(new Dimension(PanelWidth, PanelHeight));


		propertyListener = getPropertyListener();
		dataVector.addPropertyChangeListener(propertyListener);

		layoutComponents();
		// if (dataVect != null) setDataModel_GUI(dataVect); //load dataVector into the GUI

		configureComponents();


	}

	public void setDataObject(DataObject data)
	{
        // sangmok: added code to fix memory problem
        // setDataObject(null) is invoked during the executino of releaseDataObjectReferenceOfDataObjectPanel() in DataObjectCards class
        // when DomeMatrixData is null, codes like setDataModel_GUI() should be skipped
        // instead setDataModel_Null() should be invoked
        if (data == null) {
            setDataModel_Null();
            return;
        }
        // sangmok: added code ends

		if (data instanceof DomeVectorData)
			setDataModel_GUI((DomeVectorData) data);
		else
			throw new IllegalArgumentException("DomeVector gui - bad parameter");
	}

	public DomeVectorData getDataObject()
	{
		return dataVector;
	}

	protected void layoutComponents()
	{


		// create components
		constraintsButton = Templates.makeButton("constraints...");
		addButton = Templates.makeButton("add...");
		deleteButton = Templates.makeButton("delete");
		fillButton = Templates.makeButton("fill...");
		UnitComboBoxModel unitModel = new UnitComboBoxModel(dataVector.getUnit(), true);
		unitComboBox = Templates.makeDComboBox(unitModel);
		// unitComboBox = Templates.makeDComboBox(unitModel);
		flavorComboBox = Templates.makeDComboBox(flavourModel);


		fixSizeBox = Templates.makeCheckBox("fix size", dataVector.isFixedSize());
		fixSizeBox.setHorizontalTextPosition(SwingConstants.LEFT);


		sizeTextInput = Templates.makeDTextField(String.valueOf(dataVector.getSize()));
		sizeLabel = Templates.makeLabel(" elements,");

		rowRadioButton = Templates.makeRadioButton("row vector", dataVector.isRowVector());

		columnRadioButton = Templates.makeRadioButton("column vector", !dataVector.isRowVector());


		group = new ButtonGroup();
		group.add(rowRadioButton);
		group.add(columnRadioButton);


		rowColButtonGroupPanel = new JPanel();
		sizePanel = new JPanel();

		buttonsPanel = new JPanel();
		comboboxPanel = new JPanel();

		//table

        // memory fix starts
		// vectorTableModel = new DomeVectorTableModel(dataVector);
		// vectorTable = new TableSelection(vectorTableModel);
        vectorTable = new TableSelection(new DomeVectorTableModel(dataVector));
        // memory fix ends

		vectorTable.setRowSelectionAllowed(true);
		vectorTable.getColumnModel().setColumnSelectionAllowed(true);
		vectorTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		vectorTable.setFont(Templates.FONT11);

		vectorTable.setPreferredScrollableViewportSize(new Dimension(470, 100));
        DomeTable.customizeTable(vectorTable);
		vectorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


		MyListModel listModel = new MyListModel(vectorTable.getModel());
		// Create single component to add to scrollpane
		rowHeader = new JList(listModel);
		rowHeader.setFixedCellWidth(50);
		rowHeader.setFixedCellHeight(vectorTable.getRowHeight());
		rowHeader.setCellRenderer(new RowHeaderRenderer(vectorTable));

		vectorScrollPane = new JScrollPane();
		vectorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		vectorScrollPane.setViewportView(vectorTable);
		vectorScrollPane.getViewport().setBackground(Color.white);

		rowHeader.setAutoscrolls(false);
		vectorScrollPane.setRowHeaderView(rowHeader); // Adds row-list left of the table
		vectorScrollPane.getRowHeader().setBackground(Color.black);




		makerowColButtonGroupPanel();
		makesizePanel();
		makebuttonPanel();
		makeComboBoxPanel();

		JComponent[] comps = {
			rowColButtonGroupPanel,
			sizePanel,
			comboboxPanel,
			vectorScrollPane,
			buttonsPanel,

		};

		// do layout
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(7, 15, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 15, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
		};


		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		for (int i = 0; i < gbcs.length; ++i) {
			gridbag.setConstraints(comps[i], gbcs[i]);
			this.add(comps[i]);
		}


	}

	// to be overridden by subclasses
	protected void configureComponents()
	{
	}

	private void makesizePanel()
	{
		sizePanel.setLayout(new GridLayout(0, 3, 0, 0));
		sizePanel.add(sizeTextInput);
		sizePanel.add(sizeLabel);
		sizePanel.add(fixSizeBox);

	}


	private void makeComboBoxPanel()
	{

		comboboxPanel.setLayout(new GridBagLayout());
		JComponent[] comps = {unitComboBox, flavorComboBox};

		// do layout
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(comboboxPanel, comps, gbcs);

	}

	private void makerowColButtonGroupPanel()
	{
		rowColButtonGroupPanel.setLayout(new GridLayout(0, 1, 0, 0));
		rowColButtonGroupPanel.add(rowRadioButton);
		rowColButtonGroupPanel.add(columnRadioButton);
	}

	private void makebuttonPanel()
	{
		JComponent[] comps = {
			addButton, deleteButton, fillButton, constraintsButton};

		// do layout
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
			new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0)
		};
		GridBagLayout gridbag = new GridBagLayout();
		buttonsPanel.setLayout(gridbag);
		for (int i = 0; i < gbcs.length; ++i) {
			gridbag.setConstraints(comps[i], gbcs[i]);
			buttonsPanel.add(comps[i]);
		}

	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new VectorPanelPropertyChangeListener();
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new VectorPanelPropertyChangeListener();
	}

	protected void convertToNotEditable()
	{
		rowRadioButton.setEnabled(false);
		columnRadioButton.setEnabled(false);
		sizeTextInput.setEnabled(!fixSizeBox.isSelected());
		flavorComboBox.setEnabled(false);
		addButton.setEnabled(!fixSizeBox.isSelected());
		deleteButton.setEnabled(!fixSizeBox.isSelected());
		fixSizeBox.setEnabled(false);
		sizeLabel.setEnabled(!fixSizeBox.isSelected());
	}

	public void setReadOnly()
	{
		rowRadioButton.setEnabled(false);
		columnRadioButton.setEnabled(false);
		sizeTextInput.setEnabled(false);
		flavorComboBox.setEnabled(false);
		unitComboBox.setEnabled(false);
		addButton.setEnabled(false);
		deleteButton.setEnabled(false);
		fixSizeBox.setEnabled(false);
		sizeLabel.setEnabled(false);
		fillButton.setEnabled(false);

	}

    /**
     * sangmok : a new method to fix memory leakage problem
     * This method is called just before user closes data object panel window.
     * 1) set data object reference (=dataMatrix) as null
     * 2) make DataModel no more fire change to this already-closed Panel (= remove Listener)
     * 3) release data object reference in TableModel object
     */
	protected void setDataModel_Null()
	{
		if (dataVector != null) {
			dataVector.removePropertyChangeListener(propertyListener);
		}
		dataVector = null;
        ((DomeVectorTableModel) vectorTable.getModel()).removePropertyChangeListener();
        ((DomeVectorTableModel) vectorTable.getModel()).releaseReferenceToDataObject();
	}

	protected void setDataModel_GUI(DomeVectorData d)
	{
		if (dataVector != null) {
			dataVector.removePropertyChangeListener(propertyListener);
		}

		dataVector = d;
		dataVector.addPropertyChangeListener(propertyListener);


        // sangmok : fix starts
        //load the table
		// vectorTable.setModel(new DomeVectorTableModel(dataVector));

        // sangmok: final fix starts. using a new added method setData() update the data in TableModel with new dataMatrix
        ((DomeVectorTableModel) vectorTable.getModel()).setData(dataVector);
        // also update matrixTableModel in rowHeader JList
        ((MyListModel) rowHeader.getModel()).setTableModel(vectorTable.getModel());
        // sangmok : fix end

		//configure other components
		setSize_GUI();
		setRowVector_GUI();
		setFixedSize_GUI();
		setValueType_GUI();
		setUnit_GUI();
	}

	protected void setSize_GUI()
	{
		this.sizeTextInput.setText(String.valueOf(dataVector.getSize()));
		sizeTextInput.setCurrent();
	}

	protected void setRowVector_GUI()
	{
		rowRadioButton.setSelected(dataVector.isRowVector());
		columnRadioButton.setSelected(!dataVector.isRowVector());
	}

	protected void setFixedSize_GUI()
	{
		fixSizeBox.setSelected(dataVector.isFixedSize());
	}

	protected void setValueType_GUI()
	{
		if (dataVector.getValueType().toLowerCase().equals("real"))
			flavorComboBox.setSelectedIndex(0);
		else
			flavorComboBox.setSelectedIndex(1);

	}

	protected void setUnit_GUI()
	{
		unitComboBox.setModel(new UnitComboBoxModel(dataVector.getUnit(), true));
	}

	protected void RepaintTableHeader()
	{
		vectorTable.repaint();
		rowHeader.repaint();
		vectorScrollPane.repaint();
	}


	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("Vector Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		f.getContentPane().add(new DomeVectorBasePanel(), BorderLayout.CENTER);
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
			System.out.println("DomeVectorBasePanel: " + msg);
	}

	protected class VectorPanelPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();

			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.ITEMS)) {
				RepaintTableHeader();//  table changes are taken care by tablemodel
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.ROWVECTOR)) {
				setRowVector_GUI();
				RepaintTableHeader();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.FIXEDSIZE)) {
				setFixedSize_GUI();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.VALUETYPE)) {
				setValueType_GUI();
			}
		}
	}


	class MyListModel extends AbstractListModel
	{

		TableModel tableModel;

		public MyListModel(TableModel tableModel)
		{
			this.tableModel = tableModel;
			tableModel.addTableModelListener(new TableModelListener()
			{
				public void tableChanged(TableModelEvent e)
				{
					switch (e.getType()) {
						case TableModelEvent.INSERT:
							fireIntervalAdded(MyListModel.this, e.getFirstRow(), e.getLastRow());
							break;
						case TableModelEvent.DELETE:
							fireIntervalRemoved(MyListModel.this, e.getFirstRow(), e.getLastRow());
							break;
						case TableModelEvent.UPDATE:
							return;
					}
					fireContentsChanged(MyListModel.this, 0, getSize());
				}
			});
		}

		public int getSize()
		{
			return tableModel.getRowCount();
		}

		public Object getElementAt(int index)
		{
			return Integer.toString(index);
		}

        public void setTableModel(TableModel tableModel) {
            this.tableModel = tableModel;
        }
	}

}


class RowHeaderRenderer extends JLabel implements ListCellRenderer
{

	/**
	 * Constructor creates all cells the same
	 * To change look for individual cells put code in
	 * getListCellRendererComponent method
	 **/
	RowHeaderRenderer(JTable table)
	{
		JTableHeader header = table.getTableHeader();
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		setFont(Templates.FONT11);
	}

	/**
	 * Returns the JLabel after setting the text of the cell
	 **/
	public Component getListCellRendererComponent(JList list,
	                                              Object value, int index, boolean isSelected, boolean cellHasFocus)
	{

		setText((value == null) ? "" : value.toString());
		return this;
	}


}
