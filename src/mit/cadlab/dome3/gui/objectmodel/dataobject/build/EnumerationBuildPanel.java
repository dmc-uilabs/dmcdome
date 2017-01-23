// EnumerationBuildPanel.java

package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DataObjectPanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.EditEnumerationPanel;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationItem;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

public class EnumerationBuildPanel extends DataObjectPanel {

    public static final TypeInfo TYPE_INFO = new TypeInfo("EnumerationBuildPanel");
    public static final String XML_TAG = "enumerationbuildpanel";

    protected static Dimension preferredSize = new Dimension(320, 200);
    protected PropertyChangeListener propertyListener;

    protected DComboBox selectionComboBox;
    protected EnumerationTableModel enumerationTableModel;
    protected JTable enumerationTable;
    protected JButton editButton;
    protected JButton constraintButton;
    protected JScrollPane enumerationScrollPane;

    protected EnumerationData dataModel;

    /*
public EnumerationBuildPanel() {
this(new EnumerationData());

}
    */

    public EnumerationBuildPanel(EnumerationData enm) {
        if (enm == null)
            throw new IllegalArgumentException("DomeEnumeration gui - null DomeEnumeration");
        dataModel = enm;
        propertyListener = getPropertyListener();
        dataModel.addPropertyChangeListener(propertyListener);
        layoutComponents(createComponents());
        configureComponents();

        setPreferredSize(preferredSize);

    }

    /**
     * Method may be overridden. Call super.createComponents().
     */
    protected JComponent[] createComponents() {

        editButton = Templates.makeButton("edit enumeration");

        constraintButton = Templates.makeButton("constraints");


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(editButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(constraintButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0));

        enumerationTableModel = new EnumerationTableModel();


        DefaultComboBoxModel cm;
        if (dataModel.getSize() == 0)
            cm = new DefaultComboBoxModel(); //instaniate comboBox after table
        else
            cm = new DefaultComboBoxModel(dataModel.getNames());//instaniate comboBox after table
        selectionComboBox = Templates.makeDComboBox(cm);
        selectionComboBox.setMinimumSize(new Dimension(100, 20));

        selectionComboBox.setSelectedIndex(dataModel.getLastSelection());


        enumerationTable = new JTable(enumerationTableModel);
        enumerationTable.setBackground(Color.lightGray);
        //setTableBehaviour
        enumerationTable.setRowSelectionAllowed(true);
        enumerationTable.setColumnSelectionAllowed(false);
        enumerationTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enumerationTable.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        //detecting row selection
        ListSelectionModel rowSM = enumerationTable.getSelectionModel();
        if (dataModel.getLastSelection() != -1)
            rowSM.setSelectionInterval(dataModel.getLastSelection(), dataModel.getLastSelection());

        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm =
                        (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    //no rows are selected
                } else {
                    int selectedRow = enumerationTable.getSelectedRow();
                    //adjusting comboBox selection
                    dataModel.setLastSelection(selectedRow);
                }
            }
        });


        enumerationScrollPane = new JScrollPane(enumerationTable);


        DomeTable.customizeTable(enumerationTable);

        return new JComponent[]{selectionComboBox, enumerationScrollPane, buttonPanel};
    }

    protected void layoutComponents(JComponent[] comps) {
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady

        GridBagConstraints[] dGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0)};
        Templates.layoutGridBag(this, comps, dGbcs);


    }

    protected PropertyChangeListener createPropertyListener() {
        return new EnumerationPropertyChangeListener();
    }

    protected PropertyChangeListener getPropertyListener() {
        return new EnumerationPropertyChangeListener();
    }

    // connect to data model
    public void setDataObject(DataObject data) {
        // sangmok: added code to fix memory problem
        // setDataObject(null) is invoked during the executino of releaseDataObjectReferenceOfDataObjectPanel() in DataObjectCards class
        // when DomeMatrixData is null, codes like setDataModel_GUI() should be skipped
        // instead setDataModel_Null() should be invoked
        if (data == null) {
            setDataModel_Null();
            return;
        }
        // sangmok: added code ends

        if (data instanceof EnumerationData)
            setModel_GUI((EnumerationData) data);
        else
            throw new IllegalArgumentException("DomeEnumeration gui - non-DomeEnumeration parameter");
    }

    /**
     * sangmok : a new method to fix memory leakage problem
     * set data object reference (=dataMatrix) as null
     * also release data object reference in TableModel object
     */
	protected void setDataModel_Null()
	{
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = null;
	}

    public void setModel_GUI(EnumerationData d) {

        if (d == null)
            throw new IllegalArgumentException("DomeEnumeration gui - null parameter");
        else {
            //dataModel.removePropertyChangeListener(propertyListener);
            //dataModel = d;
            //dataModel.addPropertyChangeListener(propertyListener);
            dataModel.setEnumeration(d);
        }
        //update Table
        //enumerationTableModel.setDataModel(dataModel);
        //update Other GUI component
        DefaultComboBoxModel cm = new DefaultComboBoxModel(dataModel.getNames());//instaniate comboBox after table
        selectionComboBox.setModel(cm);
        selectionComboBox.setSelectedIndex(dataModel.getLastSelection());
        enumerationTable.getSelectionModel().setSelectionInterval(dataModel.getLastSelection(), dataModel.getLastSelection());
        updateGUI();

    }


    protected void configureComponents() {
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                EnumerationData answer = EditEnumerationPanel.showEditor(editButton.getParent(), dataModel);

                if (answer == null)
                    return;
                else {
                    setModel_GUI(answer);
                }
            }
        });
        constraintButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //do sth here for constraints button
            }
        });
        selectionComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectionComboBox.getSelectedIndex() < 0) return;
                //EnumerationTable.setRowSelectionInterval(selectionComboBox.getSelectedIndex(), selectionComboBox.getSelectedIndex());
                if (selectionComboBox.getSelectedIndex() != dataModel.getLastSelection())
                    dataModel.setLastSelection(selectionComboBox.getSelectedIndex());
            }
        });

    }


    public void updateGUI() {
        //called when size is changed

        enumerationTable.repaint();

        enumerationScrollPane.repaint();
    }


    public static void main(String[] args) {
        JDialog d = Templates.makeTestDialog("Enumerated Edit Panel");
        Vector fakeData = new Vector();
        EnumerationItem item1 = new EnumerationItem("apple", new Integer(1));
        EnumerationItem item2 = new EnumerationItem("Pear", new Boolean(true));
        EnumerationItem item3 = new EnumerationItem("Orange", new String("Orange juice is bad for teeth"));
        fakeData.add(item1);
        fakeData.add(item2);
        fakeData.add(item3);
        d.getContentPane().add(new EnumerationBuildPanel(new EnumerationData(fakeData)));
        d.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        d.setSize(d.getPreferredSize());
        d.show();
    }


    protected class EnumerationPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            Object newValue = e.getNewValue();
            //note table changes are taken care of by tablemodelpropertylistener,
            //so here only have to take care of other components change
            if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration.SIZE)) {
                DefaultComboBoxModel cm = new DefaultComboBoxModel(dataModel.getNames());//instaniate comboBox after table
                selectionComboBox.setModel(cm);
                selectionComboBox.setSelectedIndex(dataModel.getLastSelection());
                enumerationTable.getSelectionModel().setSelectionInterval(dataModel.getLastSelection(), dataModel.getLastSelection());
                updateGUI();
            } else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration.ELEMENTNAME)) {
                selectionComboBox.setModel(new DefaultComboBoxModel(dataModel.getNames()));
                selectionComboBox.setSelectedIndex(dataModel.getLastSelection());
                updateGUI();
            } else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration.ENUMERATION)) {
                DefaultComboBoxModel cm = new DefaultComboBoxModel(dataModel.getNames());//instaniate comboBox after table
                selectionComboBox.setModel(cm);
                selectionComboBox.setSelectedIndex(dataModel.getLastSelection());
                enumerationTable.getSelectionModel().setSelectionInterval(dataModel.getLastSelection(), dataModel.getLastSelection());
                updateGUI();
            } else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration.LASTSELECTION)) {
                if (selectionComboBox.getSelectedIndex() != dataModel.getLastSelection())
                    selectionComboBox.setSelectedIndex(dataModel.getLastSelection());
                if (enumerationTable.getSelectedRow() != dataModel.getLastSelection())
                    enumerationTable.getSelectionModel().setSelectionInterval(dataModel.getLastSelection(), dataModel.getLastSelection());
                updateGUI();
            }
        }
    }


    class EnumerationTableModel extends AbstractTableModel {

        //EnumerationData dataEnum;
        protected PropertyChangeListener propertyListener = getPropertyListener();

        public EnumerationTableModel() {
            if (dataModel != null)
                dataModel.addPropertyChangeListener(propertyListener);
        }

        public int getColumnCount() {
            return 2;
        }

        protected PropertyChangeListener getPropertyListener() {
            return new EnumerationTablePropertyChangeListener();
        }

        public int getRowCount() {

            return dataModel.getSize();
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) //name col
            {
                return dataModel.getElementName(row);
            } else if (col == 1)
                return dataModel.getElementValue(row);
            return null;
        }

        public boolean isCellEditable(int row, int col) {

            return false;

        }

        public String getColumnName(int column) {
            if (column == 0)
                return "name";
            else if (column == 1)
                return "value";
            else
                return null;
        }


        protected class EnumerationTablePropertyChangeListener implements PropertyChangeListener {
            public void propertyChange(PropertyChangeEvent e) {
                String property = e.getPropertyName();
                Object newValue = e.getNewValue();
                if (property.equals(DomeEnumeration.SIZE)) {//repaint whole table
                    fireTableStructureChanged();
                } else if (property.equals(DomeEnumeration.ELEMENTNAME)) {//repaint that cell
                    int index = ((Integer) newValue).intValue();
                    fireTableCellUpdated(index, 0);
                } else if (property.equals(DomeEnumeration.ELEMENTVALUE)) {//repaint that cell
                    int index = ((Integer) newValue).intValue();
                    fireTableCellUpdated(index, 1);
                } else if (property.equals(DomeEnumeration.ENUMERATION)) {//repaint the whole table
                    fireTableStructureChanged();
                }
            }
        }
    }
}
