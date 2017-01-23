// DomeMatrixBasePanel
//  ver 0.1 04/13/02  originally MatrixPanel.java
//  ver 0.2 05/27/02
//      add in 1) bean support, directly operating on the data model
//             2) change into a base panel with all gui layout

package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.gui.guiutils.table.TableSelection;
import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.units.UnitComboBoxModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

/**
 * DomeMatrixBasePanel: as the base panel for build and run DomeMatrix GUI
 *   as a property listener listen to the DomeVector property changes except those caught by tablemodel
 *
 *
 */
public class DomeMatrixBasePanel extends DataObjectPanel
{

	protected GridBagConstraints gbc;
	protected PropertyChangeListener propertyListener;
	protected DomeMatrixData dataMatrix = null;

    // sangmok : fix start
    // protected DomeMatrixTableModel matrixTableModel;
    // make matrixTableModel no more a member variable of this class
    // current TableModel is accessible through matrixTable.getModel()
    // if we leave matrixTableModel as a member variable, it should be reassigned whenever we assign new matrixTable.setModel(newTableModel)
    // sangmok : fix end

	// define components here
	protected DTextField rowsTextInput;
	protected DTextField columnsTextInput;

	protected DComboBox unitComboBox;
	protected DComboBox flavorComboBox;
	protected JCheckBox fixSizeBox;
	protected JButton constraintsButton;
	protected JButton addButton;
	protected JButton deleteButton;
	protected JButton fillButton;

	protected JLabel rowsLabel;
	protected JLabel columnsLabel;


	protected JScrollPane matrixScrollPane;

	protected TableSelection matrixTable;
	protected JList rowHeader;


	//String[] unitModel = {"no units", "options ..."};
	protected String[] flavourModel = {"real", "integer"};

	/*
	 * Constructors
	 */

	public DomeMatrixBasePanel(DomeMatrixData dataVect)
	{
		this(450, 200, dataVect);
	}

	public DomeMatrixBasePanel()
	{
		this(450, 200);
    }

	public DomeMatrixBasePanel(int PanelWidth, int PanelHeight)
	{
		this(PanelWidth, PanelHeight, null);
	}


	public DomeMatrixBasePanel(int PanelWidth, int PanelHeight, DomeMatrixData dataVect)
	{
		dataMatrix = dataVect;
		setPreferredSize(new Dimension(PanelWidth, PanelHeight));


		propertyListener = getPropertyListener();
		dataMatrix.addPropertyChangeListener(propertyListener);

		layoutComponents();
		//if (dataVect != null) setDataModel_GUI(dataVect); //load dataVector into the GUI

		configureComponents();


	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new MatrixPanelPropertyChangeListener();
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new MatrixPanelPropertyChangeListener();
	}

	//to be overwriten by subclass
	protected void configureComponents()
	{

	}


	public void setDataObject(DataObject data)
	{
		// sangmok: memory fix starts
        // setDataObject(null) is invoked during the executino of releaseDataObjectReferenceOfDataObjectPanel() in DataObjectCards class
        // when DomeMatrixData is null, codes like setDataModel_GUI() should be skipped
        // instead setDataModel_Null() should be invoked
        if (data == null) {
            setDataModel_Null();
            return;
        }
        // sangmok: memory fix ends

        if (data instanceof DomeMatrixData)
			setDataModel_GUI((DomeMatrixData) data);
		else
			throw new IllegalArgumentException("DomeMatrix gui - bad parameter");
	}

	public DomeMatrixData getDataObject()
	{
		return dataMatrix;
	}

	protected void layoutComponents()
	{
		// create components
		constraintsButton = Templates.makeButton("constraints...");
		addButton = Templates.makeButton("add...");

		deleteButton = Templates.makeButton("delete...");
		fillButton = Templates.makeButton("fill...");


		UnitComboBoxModel unitModel = new UnitComboBoxModel(dataMatrix.getUnit(), true);
		unitComboBox = Templates.makeDComboBox(unitModel);

		flavorComboBox = Templates.makeDComboBox(flavourModel);

		fixSizeBox = Templates.makeCheckBox("fix size", dataMatrix.isFixedSize());
		fixSizeBox.setHorizontalTextPosition(SwingConstants.LEFT);

		rowsTextInput = Templates.makeDTextField(String.valueOf(dataMatrix.getRowCount()));
		columnsTextInput = Templates.makeDTextField(String.valueOf(dataMatrix.getColumnCount()));


		rowsLabel = Templates.makeLabel("rows:");
		columnsLabel = Templates.makeLabel("columns:");


		JPanel sizePanel = new JPanel();
		sizePanel.setLayout(new GridBagLayout());
		sizePanel.add(rowsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0));
		sizePanel.add(rowsTextInput, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 20, 0));
		sizePanel.add(columnsLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0));
		sizePanel.add(columnsTextInput, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 20, 0));

		JPanel buttonsPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		buttonsPanel.setLayout(gridbag);
		buttonsPanel.add(addButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonsPanel.add(deleteButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0));
		buttonsPanel.add(fillButton, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0));
        buttonsPanel.add(constraintsButton, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));

		JPanel comboboxPanel = new JPanel();
		comboboxPanel.setLayout(new GridBagLayout());
		JComponent[] cComps = {unitComboBox, flavorComboBox};
		// do layout
		GridBagConstraints[] cGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(comboboxPanel, cComps, cGbcs);

		//table
		//matrixTableModel = new DomeMatrixTableModel(dataMatrix);
		//matrixTable = new TableSelection(matrixTableModel);


        // sangmok: fix start
        // make matrixTableModel no more a member variable of this class
        // current TableModel is accessible through matrixTable.getModel()
        // if we leave matrixTableModel as a member variable, it should be reassigned whenever we assign new matrixTable.setModel(newTableModel)
        matrixTable = new TableSelection(new DomeMatrixTableModel(dataMatrix));


        // update dataMatrix with
        //((DomeMatrixTableModel) matrixTable.getModel()).setData(dataMatrix);
        // sangmok: fix end

        //add here for font
		matrixTable.setFont(Templates.FONT11);

		matrixTable.setRowSelectionAllowed(true);
		matrixTable.getColumnModel().setColumnSelectionAllowed(true);
		matrixTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


		matrixTable.setPreferredScrollableViewportSize(new Dimension(453, 190));
        DomeTable.customizeTable(matrixTable);
		matrixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		MyListModel listModel = new MyListModel(matrixTable.getModel());

		// Create single component to add to scrollpane
		rowHeader = new JList(listModel);
		rowHeader.setFixedCellWidth(50);
		rowHeader.setFixedCellHeight(matrixTable.getRowHeight());
		rowHeader.setCellRenderer(new RowHeaderRenderer(matrixTable));

		matrixScrollPane = new JScrollPane(matrixTable);
        matrixScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, new Corner());

		matrixScrollPane.setViewportView(matrixTable);

		matrixScrollPane.getViewport().setBackground(Color.white);

		rowHeader.setAutoscrolls(false);

        matrixScrollPane.setRowHeaderView(rowHeader); // Adds row-list left of the table
        matrixScrollPane.getRowHeader().setBackground(Color.black);


		JComponent[] comps = {
			sizePanel,
			fixSizeBox,
			comboboxPanel,
			matrixScrollPane,
			buttonsPanel,

		};

		// do layout
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
		};


		gridbag = new GridBagLayout();
		setLayout(gridbag);
		for (int i = 0; i < gbcs.length; ++i) {
			gridbag.setConstraints(comps[i], gbcs[i]);
			this.add(comps[i]);
		}

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
		if (dataMatrix != null) {
			dataMatrix.removePropertyChangeListener(propertyListener);
		}
		dataMatrix = null;
        ((DomeMatrixTableModel) matrixTable.getModel()).removePropertyChangeListener();
        ((DomeMatrixTableModel) matrixTable.getModel()).releaseReferenceToDataObject();
	}

	protected void setDataModel_GUI(DomeMatrixData d)
	{
		if (dataMatrix != null) {
			dataMatrix.removePropertyChangeListener(propertyListener);
		}

		dataMatrix = d;
		dataMatrix.addPropertyChangeListener(propertyListener);

        // sangmok : fix starts
        //load the table
		//matrixTable.setModel(new DomeMatrixTableModel(dataMatrix));

        // sangmok: final fix starts. using a new added method setData() update the data in TableModel with new dataMatrix
        ((DomeMatrixTableModel) matrixTable.getModel()).setData(dataMatrix);
        // also update matrixTableModel in rowHeader JList
        ((MyListModel) rowHeader.getModel()).setTableModel(matrixTable.getModel());
        // sangmok : fix end

		//configure other components
		setSize_GUI();

		setFixedSize_GUI();
		setValueType_GUI();
		setUnit_GUI();
	}


	protected void setSize_GUI()
	{
		rowsTextInput.setText(String.valueOf(dataMatrix.getRowCount()));
		columnsTextInput.setText(String.valueOf(dataMatrix.getColumnCount()));
		rowsTextInput.setCurrent();
		columnsTextInput.setCurrent();
	}


	protected void setFixedSize_GUI()
	{
		fixSizeBox.setSelected(dataMatrix.isFixedSize());
		if(dataMatrix.isFixedSize()) {
			addButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
		else {
			addButton.setEnabled(true);
			deleteButton.setEnabled(true);
		}
	}

	protected void setValueType_GUI()
	{
		if (dataMatrix.getValueType().toLowerCase().equals("real"))
			flavorComboBox.setSelectedIndex(0);
		else
			flavorComboBox.setSelectedIndex(1);

	}

	protected void setUnit_GUI()
	{
		unitComboBox.setModel(new UnitComboBoxModel(dataMatrix.getUnit(), true));
	}

	protected void RepaintTableHeader()
	{
		matrixTable.repaint();
		matrixTable.getTableHeader().repaint();
		rowHeader.repaint();
		matrixScrollPane.repaint();
	}


	protected void convertToNotEditable()
	{

		rowsTextInput.setEnabled(!fixSizeBox.isSelected());
		columnsTextInput.setEnabled(!fixSizeBox.isSelected());
		flavorComboBox.setEnabled(false);
		addButton.setEnabled(!fixSizeBox.isSelected());
		deleteButton.setEnabled(!fixSizeBox.isSelected());
		fixSizeBox.setEnabled(false);
		rowsLabel.setEnabled(!fixSizeBox.isSelected());
		columnsLabel.setEnabled(!fixSizeBox.isSelected());
	}


	protected Vector convertToVector(int[] anArray)
	{
		if (anArray == null)
			return null;

		Vector v = new Vector(anArray.length);
		for (int i = 0; i < anArray.length; i++) {
			v.addElement(new Integer(anArray[i]));
		}
		return v;
	}

	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("Matrix Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		f.getContentPane().add(new DomeMatrixBasePanel(), BorderLayout.CENTER);
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
			System.out.println("MatrixPanel: " + msg);
	}

	protected class MatrixPanelPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.SIZE)) {
				String info = (String) newValue;
				if (info.startsWith("add")) {
					if (dataMatrix.getRowCount() == 0 && dataMatrix.getColumnCount() != 0)
						dataMatrix.setRowCount(1);
					else if (dataMatrix.getRowCount() != 0 && dataMatrix.getColumnCount() == 0) dataMatrix.setColumnCount(1);
				}
				if (info.startsWith("del")) {
					if (dataMatrix.getRowCount() == 0 && dataMatrix.getColumnCount() != 0)
						dataMatrix.setColumnCount(0);
					else if (dataMatrix.getRowCount() != 0 && dataMatrix.getColumnCount() == 0) dataMatrix.setRowCount(0);

				}
				setSize_GUI();
    		} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.ITEMS)) {
				RepaintTableHeader();//  table changes are taken care by tablemodel
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.FIXEDSIZE)) {
				setFixedSize_GUI();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.VALUETYPE)) {
				setValueType_GUI();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.UNIT)) {
				setUnit_GUI();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.DATA)) {//in case DomeMatrixData.setData() called
				setDataModel_GUI(dataMatrix);
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


	class Corner extends JComponent
	{
		public void paintComponent(Graphics g)
		{
			g.setColor(Color.lightGray);
			g.fill3DRect(0, 0, getWidth(), getHeight(), true);

			g.setColor(Color.gray);
			g.drawLine(0, 0, getWidth(), getHeight());
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

}
