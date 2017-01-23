package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 3.
 */
public class ParameterDefinitionPanel extends JScrollPane {
    private JTable table;
    private ParamDefTableModel tableModel;
    private boolean isDefiningInputParam;

    /**
     * isDefiningInputParam specifies if the param definition panel would be used for defining input param or for defining output param
     */
    public ParameterDefinitionPanel (boolean isDefiningInputParam) {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.isDefiningInputParam = isDefiningInputParam;
        initComponents();
    }

    public void reset() {
        tableModel.removeAll();
    }

    public JTable getTable() {
        return table;
    }

    private void initComponents() {
        tableModel = new ParamDefTableModel();
        //table = new JTable(new Object[0][0], columnNames);
        table = new JTable(tableModel);
        this.setViewportView(table);
        table.setFont(UIUtil.DIALOG_FONT);
        table.getTableHeader().setFont(UIUtil.DIALOG_FONT);
        table.setCellSelectionEnabled(true);

//        table.getColumnModel().getColumn(0).setPreferredWidth(100);
//        table.getColumnModel().getColumn(1).setPreferredWidth(50);
//        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        JComboBox comboBox = new JComboBox();
        comboBox.addItem(CConstant.REAL_DATA_TYPE);
        comboBox.addItem(CConstant.INTEGER_DATA_TYPE);
        comboBox.addItem(CConstant.BOOLEAN_DATA_TYPE);
        comboBox.addItem(CConstant.STRING_DATA_TYPE);
        comboBox.addItem(CConstant.VECTOR_DATA_TYPE);
        comboBox.addItem(CConstant.MATRIX_DATA_TYPE);
        comboBox.addItem(CConstant.ENUM_DATA_TYPE);
        comboBox.addItem(CConstant.FILE_DATA_TYPE);

        //Set up renderer and editor for the Favorite Color column.
        table.setDefaultRenderer(CUnit.class, new UnitRenderer(true));
        table.setDefaultEditor(CUnit.class, new UnitEditor());
        table.setDefaultRenderer(ParamName.class, new ParamNameRenderer(true));
        table.setDefaultEditor(ParamName.class, new ParamNameEditor(table.getModel(), isDefiningInputParam, this));
        table.setDefaultEditor(DataType.class, new DefaultCellEditor(comboBox));
        table.setDefaultRenderer(DefaultValue.class, new DefaultValueRenderer());
//        JTextField tf = new JTextField();
//        Border NORMAL_BORDER = BorderFactory.createEmptyBorder(0, 1, 1, 0);
//        tf.setBorder(NORMAL_BORDER);
//        table.setDefaultEditor(DefaultValue.class, new DefaultCellEditor(tf));
        table.setDefaultEditor(DefaultValue.class, new DefaultValueEditor());




//        TableColumn typeColumn = table.getColumnModel().getColumn(1);
//
//        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //table.setRowSelectionAllowed(false);
        //table.setCellSelectionEnabled(false);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    //System.out.println("empty");
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                }
            }
        });

        addBindings();
    }

    /* add binding to make alt-enter and ctrl-enter work as enter */
    protected void addBindings() {
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), "copyRow");
        table.getActionMap().put("copyRow", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("copy : " + table.getSelectedRow());
            }
        });

        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), "pasteRow");
        table.getActionMap().put("pasteRow", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("paste: " + table.getSelectedRow());
            }
        });
    }

    /** returns ParamDefTableModel of a table. you can modify table data using the returned object */
    ParamDefTableModel getParamDefTableModel() {
        return (ParamDefTableModel) table.getModel();
    }

    /** check if param definition panel has a compared param name */
    public boolean containsParamName(String compared) {
        compared = compared.trim();
        TableModel tableModel = this.getTable().getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String paramName = (String) tableModel.getValueAt(i, 0);
            String editedName = compared;
            if (paramName.equals(editedName)) {
                return true;
            }
        }
        return false;
    }

    class DataType { }
    class ParamName { }
    class DefaultValue { }

    class ParamDefRow {
        public ParamDefRow(String paramName, String dataType, CUnit unit, String defaultValue) {
            //this.paramName = new ParamName(paramName);
            this.paramName = paramName;
            this.dataType = dataType;
            this.unit = unit;
            this.defaultValue = defaultValue;
        }

        //ParamName paramName;
        String paramName;
        String dataType;
        CUnit unit;
        String defaultValue;
    }

    class ParamDefTableModel extends AbstractTableModel {

        java.util.List rowList;
        String[] columnNames = { "Param Name", "Data Type", "Unit", "Default Value" };

        public ParamDefTableModel() {
            rowList = new ArrayList();
            //populate();
        }

//        public void populate() {
//            add(0, "12dd", "Real", new CUnit("No_Unit"));
//            add(0, "13cc", "Integer", new CUnit("No_Unit"));
//            add(0, "14aa", "Real", new CUnit("No_Unit"));
//        }

        public void add(int insertRow, String paramName, String dataType, CUnit unit, String defaultValue) {
            rowList.add(new ParamDefRow(paramName, dataType, unit, defaultValue));
            fireTableRowsInserted(insertRow, insertRow);
        }

        public void remove(int rowIdx) {
            rowList.remove(rowIdx);
            fireTableRowsDeleted(rowIdx, rowIdx);
        }

        public void removeAll() {
            int size = rowList.size();
            if (size > 0) {
                rowList.clear();
                fireTableRowsDeleted(0, size - 1);
            }
        }

        public int getRowCount() {
            return rowList.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int column) {
            return columnNames[column];
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) return ((ParamDefRow) rowList.get(row)).paramName;
            if (col == 1) return ((ParamDefRow) rowList.get(row)).dataType;
            if (col == 2) return ((ParamDefRow) rowList.get(row)).unit;
            if (col == 3) return ((ParamDefRow) rowList.get(row)).defaultValue;
            return null;
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }


        public Class getColumnClass(int col) {
            if (col == 0) return ParamName.class;
            if (col == 1) return DataType.class;
            if (col == 2) return CUnit.class;
            if (col == 3) return DefaultValue.class;
            return String.class;
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 0) ((ParamDefRow) rowList.get(row)).paramName = (String) value;
            if (col == 1) ((ParamDefRow) rowList.get(row)).dataType = (String) value;
            if (col == 2) ((ParamDefRow) rowList.get(row)).unit = (CUnit) value;
            if (col == 3) ((ParamDefRow) rowList.get(row)).defaultValue = (String) value;
            fireTableCellUpdated(row, col);
        }

        public void addValue(String[] paramInfo) {
            int insertRowIdx = getRowCount();
            rowList.add(new ParamDefRow(paramInfo[0], paramInfo[1], new CUnit(paramInfo[2]), paramInfo[3]));
            fireTableCellUpdated(insertRowIdx, 0);
            fireTableCellUpdated(insertRowIdx, 1);
            fireTableCellUpdated(insertRowIdx, 2);
            fireTableCellUpdated(insertRowIdx, 3);
        }
    }
}

class ParamNameEditor extends DefaultCellEditor {
    //JFormattedTextField ftf;
    JTextField tf;
    String currentValue;
    public static final Border NORMAL_BORDER = BorderFactory.createEmptyBorder(0, 1, 1, 0);
    public static final Border ERROR_BORDER = BorderFactory.createLineBorder(Color.RED, 1);
    private TableModel model;
    private boolean isDefiningInputParam;
    ParameterDefinitionPanel definitionPanel;
    private int skipValidationRow;

    /** isDefiningInputParam is used by isEditValid() method */
    public ParamNameEditor(TableModel model, boolean isDefiningInputParam, ParameterDefinitionPanel definitionPanel) {
        super(UIUtil.createTextField());
        this.isDefiningInputParam = isDefiningInputParam;
        this.definitionPanel = definitionPanel;
        this.model = model;
        tf = (JTextField) getComponent();

        tf.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                System.out.println("current value:" + currentValue);

            }
        });

        tf.setBorder(NORMAL_BORDER);

//        tf.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                System.out.println("action performed is called");
//            }
//        });

        tf.setHorizontalAlignment(JTextField.LEADING);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        tf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        tf.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tf.postActionEvent(); //inform the editor
            }
        });

        tf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "no-escape");
        tf.getActionMap().put("no-escape", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                /* no escape = does nothing */
                if (isEditValid()) {
                    tf.postActionEvent(); //inform the editor
                }
            }
        });

    }

    public boolean isEditValid() {
        String editedName = null;
        if (definitionPanel.getRootPane().getParent() instanceof RelationDialog) {
            editedName = VarNameConverter.getFixedName(tf.getText().trim());
        } else {
            editedName = tf.getText().trim();
        }

        tf.setText(editedName);

        for (int i = 0; i < ParamNameEditor.this.model.getRowCount(); i++) {
            String paramName = (String) ParamNameEditor.this.model.getValueAt(i, 0);

            if (i == skipValidationRow) {
                continue;
            }
            if (paramName.equals(editedName)) {
                return false;
            }
        }

        /* TopLevelAncestor of this class is either InterfaceDialog or RelationDialog */
        ParameterDefinitionPanel iParamDefPanel = null;
        ParameterDefinitionPanel oParamDefPanel = null;
        if (definitionPanel.getTopLevelAncestor() instanceof RelationDialog) {
            iParamDefPanel = ((RelationDialog) definitionPanel.getTopLevelAncestor()).iParamDefPanel;
            oParamDefPanel = ((RelationDialog) definitionPanel.getTopLevelAncestor()).oParamDefPanel;
        } else if (definitionPanel.getTopLevelAncestor() instanceof InterfaceDialog) {
            iParamDefPanel = ((InterfaceDialog) definitionPanel.getTopLevelAncestor()).iParamDefPanel;
            oParamDefPanel = ((InterfaceDialog) definitionPanel.getTopLevelAncestor()).oParamDefPanel;
        }

        if (isDefiningInputParam) {
            return ! oParamDefPanel.containsParamName(tf.getText());
        } else {
            return ! iParamDefPanel.containsParamName(tf.getText());
        }
    }

    //Override to invoke setValue on the formatted text field.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.skipValidationRow = row;
        currentValue = (String) value;
        tf.setText((String) value);
        tf.setSelectionStart(0);
        tf.setSelectionEnd(tf.getText().length());
        return tf;
    }

    public boolean stopCellEditing() {
        JTextField tf = (JTextField) getComponent();
        if (isEditValid()) {
            tf.setBorder(NORMAL_BORDER);
        } else { //text is invalid
            if (! userSaysRevert()) { //user wants to edit
                tf.setBorder(ERROR_BORDER);
                return false; //don't let the editor go away
            } else {
                tf.setBorder(NORMAL_BORDER);
            }
        }
        return super.stopCellEditing();
    }

    public boolean isCellEditable(EventObject event) {
//          // to enable one-click editing
//        if (event instanceof MouseEvent && ((MouseEvent) event).getClickCount() > 0) {
//            return true;
//        }
        return super.isCellEditable(event);
    }

    /**
     * Lets the user know that the text they entered is bad. Returns true if the user elects to revert to the last good value.
     * Otherwise, returns false, indicating that the user wants to continue editing.
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        tf.selectAll();
        Object[] options = {"edit again", "revert changes"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(tf), "UNIQUE PARAMETER NAME NEEDED!\nYou have other parameter with the same name as '" + tf.getText() + "' in this relation.\nYou can either continue editing or revert to the last valid value.", "Invalid Text Entered", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

        if (answer == 1) {
            /* Revert! */
            tf.setText(currentValue);
            return true;
        }
        return false;
    }
}


class DefaultValueEditor extends DefaultCellEditor {
    JTextField tf;
    String currentValue;
    public static final Border NORMAL_BORDER = BorderFactory.createEmptyBorder(0, 1, 1, 0);
    public static final Border ERROR_BORDER = BorderFactory.createLineBorder(Color.RED, 1);

    public DefaultValueEditor() {
        super(UIUtil.createTextField());
        tf = (JTextField) getComponent();

        tf.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                System.out.println("current value:" + currentValue);

            }
        });

        tf.setBorder(NORMAL_BORDER);

//        tf.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                System.out.println("action performed is called");
//            }
//        });

        tf.setHorizontalAlignment(JTextField.LEADING);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        tf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        tf.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tf.postActionEvent(); //inform the editor
            }
        });

        tf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "no-escape");
        tf.getActionMap().put("no-escape", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                /* no escape = does nothing */
                if (isEditValid()) {
                    tf.postActionEvent(); //inform the editor
                }
            }
        });

//        tf.addKeyListener(new KeyAdapter() {
//            public void keyPressed(KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
//                    stopCellEditing();
//                    event.consume();
//                }
//            }
//        });


    }

    public boolean isEditValid() {
//        String editedName = tf.getText();
//        tf.setText(editedName);
        return true;
    }

    //Override to invoke setValue on the formatted text field.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = (String) value;
        tf.setText((String) value);
        tf.setSelectionStart(0);
        tf.setSelectionEnd(tf.getText().length());
        return tf;
    }

    public boolean stopCellEditing() {
        JTextField tf = (JTextField) getComponent();
        if (isEditValid()) {
            tf.setBorder(NORMAL_BORDER);
            System.out.println("do commit and stop cell editing");
        } else { //text is invalid
            if (! userSaysRevert()) { //user wants to edit
                tf.setBorder(ERROR_BORDER);
                return false; //don't let the editor go away
            } else {
                System.out.println("now normal");
                tf.setBorder(NORMAL_BORDER);
            }
        }
        return super.stopCellEditing();
    }

    public boolean isCellEditable(EventObject event) {
//          // to enable one-click editing
//        if (event instanceof MouseEvent && ((MouseEvent) event).getClickCount() > 0) {
//            return true;
//        }
        return super.isCellEditable(event);
    }

    /**
     * Lets the user know that the text they entered is bad. Returns true if the user elects to revert to the last good value.
     * Otherwise, returns false, indicating that the user wants to continue editing.
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        tf.selectAll();
        Object[] options = {"edit again", "revert changes"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(tf), "INVALID DEFAULT VALUE!\nIt should be a form of 'something'.", "Invalid Text Entered", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

        if (answer == 1) {
            /* Revert! */
            tf.setText(currentValue);
            return true;
        }
        return false;
    }
}


class ParamNameRenderer extends JLabel implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public ParamNameRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
//        Color newColor = (Color)color;
//        setBackground(newColor);
        String newStr = (String) obj;
        setText(newStr);
        setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 0));

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        return this;
    }
}


class DefaultValueRenderer extends JLabel implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;

    public DefaultValueRenderer() {
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
//        Color newColor = (Color)color;
//        setBackground(newColor);
        String newStr = (String) obj;
        setText(newStr);
        setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 0));

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        return this;
    }
}
