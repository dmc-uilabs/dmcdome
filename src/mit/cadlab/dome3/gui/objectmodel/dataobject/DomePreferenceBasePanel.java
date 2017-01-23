// DomePreferenceBasePanel

package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomePreference;
import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
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
 * DomePreferenceBasePanel: as the base panel for build and run DomePreference GUI
 *   as a property listener listen to the DomeVector property changes except those caught by tablemodel
 *
 *
 */
public class DomePreferenceBasePanel extends DataObjectPanel
{

	protected GridBagConstraints gbc;
	protected PropertyChangeListener propertyListener;
	protected DomePreferenceData dataPreference = null;

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


	protected JScrollPane preferenceScrollPane;

	protected TableSelection preferenceTable;
	protected JList rowHeader;


	//String[] unitModel = {"no units", "options ..."};
	protected String[] flavourModel = {"real", "integer"};

	/*
	 * Constructors
	 */

	public DomePreferenceBasePanel(DomePreferenceData dataVect)
	{
		this(450, 200, dataVect);
	}

	public DomePreferenceBasePanel()
	{
		this(450, 200);
	}

	public DomePreferenceBasePanel(int PanelWidth, int PanelHeight)
	{
		this(PanelWidth, PanelHeight, null);

	}


	public DomePreferenceBasePanel(int PanelWidth, int PanelHeight, DomePreferenceData dataVect)
	{
		dataPreference = dataVect;
		setPreferredSize(new Dimension(PanelWidth, PanelHeight));


		propertyListener = getPropertyListener();
		dataPreference.addPropertyChangeListener(propertyListener);

		layoutComponents();
		//if (dataVect != null) setDataModel_GUI(dataVect); //load dataVector into the GUI

		configureComponents();


	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new PreferencePanelPropertyChangeListener();
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new PreferencePanelPropertyChangeListener();
	}

	//to be overwriten by subclass
	protected void configureComponents()
	{

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

		if (data instanceof DomePreferenceData)
			setDataModel_GUI((DomePreferenceData) data);
		else
			throw new IllegalArgumentException("DomePreference gui - bad parameter");
	}

	public DomePreferenceData getDataObject()
	{
		return dataPreference;
	}

	protected void layoutComponents()
	{
		// create components
		constraintsButton = Templates.makeButton("constraints...");
		addButton = Templates.makeButton("add...");

		deleteButton = Templates.makeButton("delete...");
		fillButton = Templates.makeButton("fill...");

		UnitComboBoxModel unitModel = new UnitComboBoxModel(dataPreference.getUnit(), true);
		unitComboBox = Templates.makeDComboBox(unitModel);

		flavorComboBox = Templates.makeDComboBox(flavourModel);

		fixSizeBox = Templates.makeCheckBox("fix size", dataPreference.isFixedSize());
		fixSizeBox.setHorizontalTextPosition(SwingConstants.LEFT);

		rowsTextInput = Templates.makeDTextField(String.valueOf(dataPreference.getRowCount()));
		columnsTextInput = Templates.makeDTextField(String.valueOf(dataPreference.getColumnCount()));


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

		//tablepreference
		preferenceTable = new TableSelection(new DomePreferenceTableModel(dataPreference));
		//add here for font
		preferenceTable.setFont(Templates.FONT11);

		preferenceTable.setRowSelectionAllowed(true);
		preferenceTable.getColumnModel().setColumnSelectionAllowed(true);
		preferenceTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


		preferenceTable.setPreferredScrollableViewportSize(new Dimension(453, 190));

		preferenceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		MyListModel listModel = new MyListModel(preferenceTable.getModel());

		// Create single component to add to scrollpane
		rowHeader = new JList(listModel);
		rowHeader.setFixedCellWidth(50);
		rowHeader.setFixedCellHeight(preferenceTable.getRowHeight());
		rowHeader.setCellRenderer(new RowHeaderRenderer(preferenceTable));

		preferenceScrollPane = new JScrollPane();
		preferenceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		preferenceScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, new Corner());

		preferenceScrollPane.setViewportView(preferenceTable);
		preferenceScrollPane.getViewport().setBackground(Color.white);

		rowHeader.setAutoscrolls(false);
		preferenceScrollPane.setRowHeaderView(rowHeader); // Adds row-list left of the table
		preferenceScrollPane.getRowHeader().setBackground(Color.black);

		DomeTable.customizeTable(preferenceTable);
		JComponent[] comps = {
			sizePanel,
			fixSizeBox,
			comboboxPanel,
			preferenceScrollPane,
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
     * set data object reference (=dataMatrix) as null
     * also release data object reference in TableModel object
     */
	protected void setDataModel_Null()
	{
		if (dataPreference != null) {
			dataPreference.removePropertyChangeListener(propertyListener);
		}
		dataPreference = null;
        ((DomePreferenceTableModel) preferenceTable.getModel()).removePropertyChangeListener();
		((DomePreferenceTableModel) preferenceTable.getModel()).releaseReferenceToDataObject();
	}

	protected void setDataModel_GUI(DomePreferenceData d)
	{
		if (dataPreference != null) {
			dataPreference.removePropertyChangeListener(propertyListener);
		}

		dataPreference = d;
		dataPreference.addPropertyChangeListener(propertyListener);
		//load the table
		preferenceTable.setModel(new DomePreferenceTableModel(dataPreference));
		//configure other components

        // sangmok : fix starts
        //load the table
		// vectorTable.setModel(new DomeVectorTableModel(dataVector));

        // sangmok: final fix starts. using a new added method setData() update the data in TableModel with new dataMatrix
        ((DomePreferenceTableModel) preferenceTable.getModel()).setData(dataPreference);
        // also update matrixTableModel in rowHeader JList
        ((MyListModel) rowHeader.getModel()).setTableModel(preferenceTable.getModel());
        // sangmok : fix end

		setSize_GUI();

		setFixedSize_GUI();
		setValueType_GUI();
		setUnit_GUI();


	}


	protected void setSize_GUI()
	{
		rowsTextInput.setText(String.valueOf(dataPreference.getRowCount()));
		columnsTextInput.setText(String.valueOf(dataPreference.getColumnCount()));
		rowsTextInput.setCurrent();
		columnsTextInput.setCurrent();
	}


	protected void setFixedSize_GUI()
	{
		fixSizeBox.setSelected(dataPreference.isFixedSize());
	}

	protected void setValueType_GUI()
	{
		if (dataPreference.getValueType().toLowerCase().equals("real"))
			flavorComboBox.setSelectedIndex(0);
		else
			flavorComboBox.setSelectedIndex(1);

	}

	protected void setUnit_GUI()
	{
		unitComboBox.setModel(new UnitComboBoxModel(dataPreference.getUnit(), true));
	}

	protected void RepaintTableHeader()
	{
		preferenceTable.repaint();
		preferenceTable.getTableHeader().repaint();
		rowHeader.repaint();
		preferenceScrollPane.repaint();
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
		JFrame f = Templates.makeTestFrame("Preference Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		f.getContentPane().add(new DomePreferenceBasePanel(), BorderLayout.CENTER);
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
			System.out.println("PreferencePanel: " + msg);
	}

	protected class PreferencePanelPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(DomePreference.SIZE)) {
				String info = (String) newValue;
				if (info.startsWith("add")) {
					if (dataPreference.getRowCount() == 0 && dataPreference.getColumnCount() != 0)
						dataPreference.setRowCount(1);
					else if (dataPreference.getRowCount() != 0 && dataPreference.getColumnCount() == 0) dataPreference.setColumnCount(1);
				}
				if (info.startsWith("del")) {
					if (dataPreference.getRowCount() == 0 && dataPreference.getColumnCount() != 0)
						dataPreference.setColumnCount(0);
					else if (dataPreference.getRowCount() != 0 && dataPreference.getColumnCount() == 0) dataPreference.setRowCount(0);

				}
				setSize_GUI();
			} else if (property.equals(DomePreference.ITEMS)) {
				RepaintTableHeader();//  table changes are taken care by tablemodel
			} else if (property.equals(DomePreference.FIXEDSIZE)) {
				setFixedSize_GUI();
			} else if (property.equals(DomePreference.VALUETYPE)) {
				setValueType_GUI();
			} else if (property.equals(DomePreference.UNIT)) {
				setUnit_GUI();
			} else if (property.equals(DomePreference.DATA)) {//in case DomePreferenceData.setData() called
				setDataModel_GUI(dataPreference);
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
