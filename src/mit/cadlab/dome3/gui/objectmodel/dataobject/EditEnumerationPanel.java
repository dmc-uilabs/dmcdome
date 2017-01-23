package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class EditEnumerationPanel extends DataObjectPanel {

    public static final TypeInfo TYPE_INFO = new TypeInfo("EditEnumerationPanel");
    public static final String XML_TAG = "editenumerationpanel";

    protected PropertyChangeListener propertyListener;

    DTextField sizeTextField;
    DComboBox typeComboBox;

    JButton upButton;
    JButton downButton;

    JButton addButton;
    JButton deleteButton;
    JButton okButton;

    JTable enumerationTable;
    EditEnumerationTableModel enumerationTableModel;
    JScrollPane enumerationScrollPane;

    EnumerationData dataModel;
    EnumerationData answer = null;

    String[] typeModelText = {"integer", "real", "boolean", "string"};
    TextCellEditor textCellEditor = new TextCellEditor();


    public static EnumerationData showEditor(Component parent, EnumerationData data) {

        EditEnumerationPanel editor = new EditEnumerationPanel(data);
        JDialog d = DialogFactory.createDialog(parent, "Edit Enumeration", editor, true, true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.show();

        return editor.answer;
    }


    public EditEnumerationPanel() {
        this(new EnumerationData());
    }

    public EditEnumerationPanel(EnumerationData data) {
        //here--notice must do a copy of data to make sure the editing is not affecting original
        //and make sure when cancel editing , all change are discarded..

        typeComboBox = Templates.makeDComboBox(typeModelText);

        dataModel = new EnumerationData(data);
        propertyListener = createPropertyListener();
        dataModel.addPropertyChangeListener(propertyListener);
        layoutComponenets(createComponents());


        Dimension d = new Dimension(320, 200);
        this.setPreferredSize(d);
    }

    // connect to data model
    public void setDataObject(DataObject data) {
        if (data instanceof EnumerationData)
            setModel_GUI((EnumerationData) data);
        else
            throw new IllegalArgumentException("DomeEnumeration gui - non-DomeEnumeration parameter");
    }

    protected PropertyChangeListener createPropertyListener() {
        return new EditEnumerationPropertyChangeListener();
    }

    protected void setModel_GUI(EnumerationData data) {
        //dataModel.removePropertyChangeListener(propertyListener);
        dataModel.setEnumeration(data);
        //dataModel.addPropertyChangeListener(propertyListener);

        //update Table
        //enumerationTableModel.setDataModel(dataModel);
        //update Other GUI component
        updateGUI();

    }

    /**
     * Method may be overridden. Call super.createComponents().
     */
    protected JComponent[] createComponents() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonListener listener = new buttonListener();
        addButton = Templates.makeButton("add", listener);

        deleteButton = Templates.makeButton("delete", listener);

        okButton = Templates.makeButton("OK", listener);
        initTable();

        JComponent[] bComps = {addButton, typeComboBox, deleteButton, okButton};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        // in the order of the comps array
        GridBagConstraints[] bGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)};
        Templates.layoutGridBag(buttonPanel, bComps, bGbcs);

        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new GridBagLayout());
        sizeTextField = Templates.makeDTextField(String.valueOf(enumerationTableModel.getRowCount()), 2);
        sizeTextField.setEditable(false);
        sizeTextField.setEnabled(false);
        JLabel sizeLabel = Templates.makeLabel("items");
        JLabel typeLabel = Templates.makeLabel("enumeration type:");

        JComponent[] sComps = {sizeTextField, sizeLabel};
        //JComponent[] sComps = {sizeTextField, sizeLabel, typeLabel, typeComboBox};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        // in the order of the comps array
        GridBagConstraints[] sGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 20, 0),
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 3, 0, 0), 0, 0),
            //		new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            //		new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 3, 0, 0), 0, 0)};
        };
        Templates.layoutGridBag(sizePanel, sComps, sGbcs);


        upButton = Templates.makeListArrowButton("up", listener);

        downButton = Templates.makeListArrowButton("down", listener);


        return new JComponent[]{sizePanel, enumerationScrollPane, upButton, downButton, buttonPanel};
    }

    protected void layoutComponenets(JComponent[] comps) {
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        // in the order of the comps array


        GridBagConstraints[] eGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 2, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 0.0, 1.0, gbc.NORTHWEST, gbc.NONE, new Insets(2, 0, 0, 5), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0)};
        Templates.layoutGridBag(this, comps, eGbcs);
    }

    protected void updateGUI() {
        sizeTextField.setText(String.valueOf(enumerationTableModel.getRowCount()));
        sizeTextField.setCurrent();


        enumerationScrollPane.repaint();
    }

    public void addButton_clicked() {
        //add oneline at the bottom

        switch (typeComboBox.getSelectedIndex()) {
            case 0:
                enumerationTableModel.addRow(new Integer(0));
                break;
            case 1:
                enumerationTableModel.addRow(new Double(0));
                break;
            case 2:
                enumerationTableModel.addRow(new Boolean(true));
                break;
            case 3:
                enumerationTableModel.addRow(new String("a string"));
                break;
            default:
                enumerationTableModel.addRow(new Object());

        }

    }

    public void deleteButton_clicked() {
        //directly delete that row if any row selected

        int selectedRow = enumerationTable.getSelectedRow();
        if (selectedRow != -1) {
            enumerationTableModel.removeRow(selectedRow);
        }
        //else delete the last row
        else {
            enumerationTableModel.removeRow(enumerationTableModel.getRowCount() - 1);
        }

    }

    public void okButton_clicked() {
        //add the following line to work as if a 'enter' key-pressed
        if (enumerationTable.getCellEditor() != null) enumerationTable.getCellEditor().stopCellEditing();
        answer = dataModel;
        dispose();
    }

    public void upButton_clicked() {
        //add the following line to work as if a 'enter' key-pressed
        if (enumerationTable.getCellEditor() != null) enumerationTable.getCellEditor().stopCellEditing();

        //int startIndex = EnumerationTable.getSelectionModel().getMinSelectionIndex();
        int startIndex = enumerationTable.getSelectedRows()[0];
        if (startIndex > 0) {
            enumerationTableModel.moveRow(startIndex, startIndex, startIndex - 1);
            //keep that row being selected
            enumerationTable.getSelectionModel().setSelectionInterval(startIndex - 1, startIndex - 1);

        } else {
            //beep
            Toolkit.getDefaultToolkit().beep();

            return; //already at the top ==0 or nothing selected ==-1
        }
    }

    public void downButton_clicked() {
        //add the following line to work as if a 'enter' key-pressed
        if (enumerationTable.getCellEditor() != null) enumerationTable.getCellEditor().stopCellEditing();


        int startIndex = enumerationTable.getSelectionModel().getMinSelectionIndex();
        if (startIndex >= 0 && startIndex < (enumerationTable.getRowCount() - 1)) {
            enumerationTableModel.moveRow(startIndex, startIndex, startIndex + 1);
            //keep that row being selected
            enumerationTable.getSelectionModel().setSelectionInterval(startIndex + 1, startIndex + 1);
        } else {
            //beep
            Toolkit.getDefaultToolkit().beep();

            return; //already at the end== getRowCount() or nothing selected ==-1
        }
    }

    private void dispose() {

        SwingUtilities.windowForComponent(this).dispose();
    }

    private void initTable() {
        enumerationTableModel = new EditEnumerationTableModel(dataModel);
        enumerationTable = new EnumerationTable(enumerationTableModel);

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
                    //int selectedRow = lsm.getMinSelectionIndex();
                    int selectedRow = enumerationTable.getSelectedRow();
                   //Qing: July 25th, here !! selection shouldn't affect the original model selection! the last selection should be defined in EnumerationBuildPanel only
                    //dataModel.setLastSelection(selectedRow);
                    //adjusting comboBox selection
                    if (enumerationTable.getValueAt(selectedRow, 1).getClass().getName().toLowerCase().equals("java.lang.integer"))
                        typeComboBox.setSelectedIndex(0);
                    else if (enumerationTable.getValueAt(selectedRow, 1).getClass().getName().toLowerCase().equals("java.lang.double"))
                        typeComboBox.setSelectedIndex(1);
                    else if (enumerationTable.getValueAt(selectedRow, 1).getClass().getName().toLowerCase().equals("java.lang.boolean"))
                        typeComboBox.setSelectedIndex(2);
                    else if (enumerationTable.getValueAt(selectedRow, 1).getClass().getName().toLowerCase().equals("java.lang.string"))
                        typeComboBox.setSelectedIndex(3);
                }
            }
        });
        DomeTable.customizeTable(enumerationTable);
        enumerationScrollPane = new JScrollPane();
        enumerationScrollPane.add(enumerationTable);
        enumerationScrollPane.setViewportView(enumerationTable);
    }


    public static void main(String[] args) {
        JDialog d = Templates.makeTestDialog("Enumerated Edit Panel");

        d.getContentPane().add(new EditEnumerationPanel());
        d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        d.setSize(d.getPreferredSize());
        d.show();
    }

    protected class EditEnumerationPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            Object newValue = e.getNewValue();
            //note table changes are taken care of by tablemodelpropertylistener,
            //so here only have to take care of other components change
            if (property.equals(DomeEnumeration.SIZE)) {
                updateGUI();
            } else if (property.equals(DomeEnumeration.ENUMERATION)) {
                updateGUI();
            } else if (property.equals(DomeEnumeration.LASTSELECTION)) {
               ListSelectionModel rowSM = enumerationTable.getSelectionModel();
               if (dataModel.getLastSelection() != -1)
                    rowSM.setSelectionInterval(dataModel.getLastSelection(), dataModel.getLastSelection());
            }
        }
    }

    class buttonListener implements ActionListener {
        public buttonListener() {
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addButton) {
                addButton_clicked();
            } else if (e.getSource() == deleteButton) {
                deleteButton_clicked();
            } else if (e.getSource() == okButton) {
                okButton_clicked();
            } else if (e.getSource() == upButton) {
                upButton_clicked();
            } else if (e.getSource() == downButton) {
                downButton_clicked();
            }
        }

    }


}



