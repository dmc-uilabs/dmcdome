package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.EventObject;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * User: Sangmok Han
 * Date: Oct 4, 2006
 */
public class MatrixPopupEditor extends JFrame {
    private ComponentReference compRef;
    private String qualifiedParamName;
    private JTable table;
    private JTextField rowCountFd;
    private JTextField columnCountFd;
    private MatrixValueEditorPane matrixEditorPane;
    public static final int COLUMN_WIDTH = 50;

    private MatrixPopupTableModel tableModel;

    public MatrixPopupEditor(String qualifiedParamName, MatrixValueEditorPane matrixEditorPane, List rowList, ComponentReference compRef) {
        super("Matrix: " + qualifiedParamName);
        this.qualifiedParamName = qualifiedParamName;
        this.compRef = compRef;
        this.matrixEditorPane = matrixEditorPane;
        this.init(rowList);
    }

    protected void resetColumnWidth() {
        for (Enumeration e = table.getColumnModel().getColumns(); e.hasMoreElements(); ) {
            TableColumn column = (TableColumn) e.nextElement();
            column.setPreferredWidth(COLUMN_WIDTH);
        }
        table.revalidate();
    }

    public void setMatrixValue(List rowList) {
        tableModel.rowList = rowList;
        rowCountFd.setText(Integer.toString(tableModel.getRowCount()));
        columnCountFd.setText(Integer.toString(tableModel.getColumnCount() - 1));
        tableModel.fireTableStructureChanged();
        tableModel.fireTableDataChanged();
//        tableModel.fireTableRowsUpdated(0, tableModel.getRowCount() - 1);
        this.resetColumnWidth();
    }

    public void init(List rowList) {
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        tableModel = new MatrixPopupTableModel(false);
        int rowCount = 0;
        int columnCount = 0;
        if (rowList != null) {
            tableModel.rowList = rowList;
    //        tableModel.fireTableDataChanged();
            System.out.println("rowList = " + rowList);
            rowCount = tableModel.getRowCount();
            columnCount = tableModel.getColumnCount() - 1;
        }

        rowCountFd = UIUtil.createTextField();
        rowCountFd.setText(Integer.toString(rowCount));
        rowCountFd.setMinimumSize(new Dimension(30, 1));
        rowCountFd.setPreferredSize(new Dimension(30, 20));
        rowCountFd.setMaximumSize(new Dimension(30, 20));
        rowCountFd.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                rowCountFd.selectAll();
            }
            public void focusLost(FocusEvent e) {
                updateMatrixSize();
            }
        });
        rowCountFd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateMatrixSize();
            }
        });
        columnCountFd = UIUtil.createTextField();
        columnCountFd.setText(Integer.toString(columnCount));
        columnCountFd.setMinimumSize(new Dimension(30, 1));
        columnCountFd.setPreferredSize(new Dimension(30, 20));
        columnCountFd.setMaximumSize(new Dimension(30, 20));
        columnCountFd.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                columnCountFd.selectAll();
            }
            public void focusLost(FocusEvent e) {
                updateMatrixSize();
            }
        });
        columnCountFd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateMatrixSize();
            }
        });
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));
        //configPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel paramNameLb = UIUtil.createLabel(" name: " + qualifiedParamName);
        paramNameLb.setMinimumSize(new Dimension(80, 25));
        paramNameLb.setPreferredSize(new Dimension(80, 25));
        paramNameLb.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        configPanel.add(paramNameLb);
        configPanel.add(Box.createHorizontalGlue());

        configPanel.add(UIUtil.createLabel(" rows: "));
        configPanel.add(rowCountFd);
        configPanel.add(UIUtil.createLabel("   columns: "));
        configPanel.add(columnCountFd);
        configPanel.add(Box.createHorizontalStrut(2));
        JButton updateBt = UIUtil.createButton("set");
        updateBt.setPreferredSize(new Dimension(45, 21));
        updateBt.setMaximumSize(new Dimension(45, 21));
        updateBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowCount = Integer.parseInt(rowCountFd.getText());
                int columnCount = Integer.parseInt(columnCountFd.getText());
                tableModel.setMatrixSize(rowCount, columnCount);
                tableModel.fireTableStructureChanged();
                table.revalidate();
            }
        });
        configPanel.add(updateBt);
        configPanel.add(Box.createHorizontalStrut(10));
        //configPanel.add(Box.createHorizontalStrut(30));
        configPanel.add(UIUtil.createLabel(" data type:  "));
        JComboBox dataTypeCb = UIUtil.createComboBox(new String[] { "Real", "Integer" });
        dataTypeCb.setMaximumSize(new Dimension(60, 21));
        configPanel.add(dataTypeCb);
        configPanel.setMinimumSize(new Dimension(300, 40));

        table = new JTable(tableModel);
        tableModel.setTable(table);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        TableCellRenderer renderer = new MatrixPopupTableRenderer();
        table.getTableHeader().setFont(UIUtil.DIALOG_FONT);
        for (Enumeration e = table.getColumnModel().getColumns(); e.hasMoreElements(); ) {
            TableColumn column = (TableColumn) e.nextElement();
            column.setPreferredWidth(COLUMN_WIDTH);
        }
        table.setDefaultRenderer(Number.class, renderer);
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultEditor(Number.class, new NumberCellEditor());

        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setPreferredSize(new Dimension(Short.MAX_VALUE, 15));

        //table.setPreferredScrollableViewportSize(new Dimension(table.getColumnModel().getTotalColumnWidth(), 300));
        //table.setPreferredSize(new Dimension(600, 300));
        //TableColumn column = table.getColumnModel().getColumn(2);

        //JPanel scrollPaneContainer = new JPanel();
        //JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setPreferredSize(new Dimension(300, 200));
        //scrollPaneContainer.add(scrollPane, BorderLayout.CENTER);
        //contentPane.add(scrollPaneContainer, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton applyBt = UIUtil.createButton("apply");
        applyBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                matrixEditorPane.setMatrixValue(tableModel.getRowList());
                matrixEditorPane.acceptEditorValue();
                matrixEditorPane.getComponentReference().getImplementationEditor().requestFocusInWindow();
            }
        } );
        JButton okayBt = UIUtil.createButton("okay");
        okayBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                matrixEditorPane.setMatrixValue(tableModel.getRowList());
                matrixEditorPane.acceptEditorValue();
                matrixEditorPane.getComponentReference().getImplementationEditor().requestFocusInWindow();
                MatrixPopupEditor.this.dispose();
            }
        } );
        JButton cancelBt = UIUtil.createButton("cancel");
        cancelBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MatrixPopupEditor.this.dispose();
            }
        } );
        buttonPanel.add(applyBt);
        buttonPanel.add(okayBt);
        buttonPanel.add(cancelBt);
        buttonPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 30));

        JPanel spreadsheetPanel = new JPanel();
        spreadsheetPanel.setLayout(new BorderLayout());
        spreadsheetPanel.add(scrollPane, BorderLayout.CENTER);
        spreadsheetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        //scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)));
        JPanel toolButtonPanel = new JPanel();
        toolButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        JButton insertBt = UIUtil.createButton("insert...");
        JButton deleteBt = UIUtil.createButton("delete...");
        JButton fillBt = UIUtil.createButton("fill...");

        insertBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MatrixPopupEditorInsertDialog dialog = new MatrixPopupEditorInsertDialog(MatrixPopupEditor.this);
                dialog.setVisible(true);
            }
        });

        toolButtonPanel.add(insertBt);
        toolButtonPanel.add(deleteBt);
        toolButtonPanel.add(fillBt);
//        table.setBackground(Color.WHITE);
//        scrollPane.setBackground(Color.WHITE);
        table.setOpaque(false);
        spreadsheetPanel.add(toolButtonPanel, BorderLayout.PAGE_END);

//        contentPane.add(paramNamePanel);
        contentPane.add(configPanel);
        contentPane.add(spreadsheetPanel);
        contentPane.add(buttonPanel);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        }, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

//        int x = owner.getX() + (owner.getWidth() - this.getWidth()) / 2;
//        int y = owner.getY() + (owner.getHeight() - this.getHeight()) / 2;
//        if (x < 0) x = 0;
//        if (y < 0) y = 0;
//        this.setLocation(x, y);

        //this.pack();
    }

    public List getMatrixValue() {
        return tableModel.getRowList();
    }

    public String getQualifiedParamName() {
        return qualifiedParamName;
    }

    public void updateMatrixSize() {
        int rowCount = 0;
        try {
            rowCount = Integer.parseInt(rowCountFd.getText());
        } catch (NumberFormatException e) { }

        int columnCount = 0;
        try {
            columnCount = Integer.parseInt(columnCountFd.getText());
        } catch (NumberFormatException e) { }

        tableModel.setMatrixSize(rowCount, columnCount);
    }

    public static void main(String[] args) {
//        try {
//            String laf = System.getProperty("swing.defaultlaf");
//            if(laf == null) {
//                laf = UIManager.getSystemLookAndFeelClassName();
//            }
//            UIManager.setLookAndFeel(laf);
//            UIManager.put("ToolTip.font", UIUtil.PARAM_NAME_FONT);
//        } catch (Exception ex) {
//            System.err.println(ex.getMessage());
//        }

        JFrame frame = new MatrixPopupEditor("table table table table table  ", null, null, null);
        frame.setSize(430, 280);
        frame.setVisible(true);
    }

}

class MatrixPopupTableRenderer implements TableCellRenderer {
    JButton labelRenderComp;
    JLabel numberRenderComp;
    Border EMPTY_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    NumberFormat numFormat;

    public MatrixPopupTableRenderer() {
        labelRenderComp = new JButton();
        //labelRenderComp.setBorder(BorderFactory.createRaisedBevelBorder());
        Border headerCellBorder = (Border) UIManager.getDefaults().get("TableHeader.cellBorder");
        labelRenderComp.setBorder(headerCellBorder);
        //labelRenderComp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        labelRenderComp.setFont(UIUtil.DIALOG_FONT);
        labelRenderComp.setForeground(Color.BLACK);
        labelRenderComp.setOpaque(true);
        numberRenderComp = new JLabel();
        numberRenderComp.setHorizontalAlignment(SwingConstants.RIGHT);
        numberRenderComp.setBorder(null);
        numberRenderComp.setFont(UIUtil.DIALOG_FONT);

        labelRenderComp.setOpaque(true);
        numberRenderComp.setOpaque(true);

        numberRenderComp.setBorder(EMPTY_BORDER);

        numFormat = NumberFormat.getInstance();
        numFormat.setGroupingUsed(true);
        numFormat.setMaximumFractionDigits(6);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
            labelRenderComp.setText((String) value);
            return labelRenderComp;
        } else {
            String formatted = numFormat.format((Number) value);
            numberRenderComp.setText(formatted);

            if (isSelected) {
                numberRenderComp.setForeground(table.getSelectionForeground());
                numberRenderComp.setBackground(table.getSelectionBackground());
            } else {
                numberRenderComp.setForeground(table.getForeground());
                numberRenderComp.setBackground(table.getBackground());
            }

            return numberRenderComp;
        }
    }

}

class MatrixPopupTableModel extends AbstractTableModel {

    java.util.List rowList;
    boolean isIntegerType;
    JTable table;
    //String[] columnNames = { "Param Name", "Data Type", "Unit", "Default Value" };

    public MatrixPopupTableModel(boolean isIntegerType) {
        rowList = new ArrayList();
        this.isIntegerType = isIntegerType;
        //populate();
    }

    public void populate() {
        setMatrixSize(1, 1);
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public List getRowList() {
        return rowList;
    }

    private void adjustColumnSize(int newColumnSize) {
        for (int i = 0; i < rowList.size(); i++) {
            List row = (List) rowList.get(i);
            while (row.size() > newColumnSize) {
                row.remove(row.size() - 1);
            }

            while (row.size() < newColumnSize) {
                if (isIntegerType) {
                    row.add(new Integer(0));
                } else {
                    row.add(new Double(0));
                }
            }
        }
        System.out.println("column size changed: rowList = " + rowList);
    }

    private void adjustRowSize(int newRowSize, int currentColumnSize) {
        while (getRowCount() > newRowSize) {
            rowList.remove(rowList.size() - 1);
        }

        while (getRowCount() < newRowSize) {
            rowList.add(createZeroFilledList(currentColumnSize, isIntegerType));
        }
    }

    private static List createZeroFilledList(int size, boolean isIntegerType) {
        List ret = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            if (isIntegerType) {
                ret.add(new Integer(0));
            } else {
                ret.add(new Double(0));
            }
        }
        return ret;
    }

    /** to leave column size or row size unchanged, give -1 instead of specifying their new sizes */
    public void setMatrixSize(int newRowSize, int newColumnSize) {
        int currentRowSize = getRowCount();
        int currentColumnSize = getColumnCount() - 1;

        if (newColumnSize == -1) {
            newColumnSize = currentColumnSize;
        }

        if (newRowSize == -1) {
            newRowSize = currentRowSize;
        }

        adjustColumnSize(newColumnSize);
        adjustRowSize(newRowSize, newColumnSize);

        if (newRowSize != -1 && currentRowSize > newRowSize) {
            fireTableRowsDeleted(newRowSize, currentRowSize - 1);
        } else if (currentRowSize < newRowSize) {
            fireTableRowsInserted(currentRowSize, newRowSize - 1);
        }

        if (newColumnSize != -1 && currentColumnSize > newColumnSize) {
            fireTableStructureChanged();

            for (Enumeration e = table.getColumnModel().getColumns(); e.hasMoreElements(); ) {
                TableColumn column = (TableColumn) e.nextElement();
                //column.setPreferredWidth(50);
                column.setPreferredWidth(MatrixPopupEditor.COLUMN_WIDTH);
            }
        } else if (currentColumnSize < newColumnSize) {
            fireTableStructureChanged();

            for (Enumeration e = table.getColumnModel().getColumns(); e.hasMoreElements(); ) {
                TableColumn column = (TableColumn) e.nextElement();
                //column.setPreferredWidth(50);
                column.setPreferredWidth(MatrixPopupEditor.COLUMN_WIDTH);
            }
        }
    }

    public int getRowCount() {
        return rowList.size();
    }

    public int getColumnCount() {
        if (rowList.isEmpty()) {
            return 1;
        } else {
            return ((java.util.List) rowList.get(0)).size() + 1;
        }
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        } else {
            return Integer.toString(column);
        }
    }

    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return Integer.toString(row + 1); // String
        } else {
            if (rowList.isEmpty()) {
                if (isIntegerType) {
                    return new Integer(0);
                } else {
                    return new Double(0);
                }
            } else {
                return ((java.util.List) rowList.get(row)).get(col - 1);
            }
        }
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return false;
        }
        return true;
    }


    public Class getColumnClass(int col) {
        if (col == 0) {
            return String.class;
        } else {
            return Number.class;
        }
    }

    public void setValueAt(Object value, int row, int col) {
        Number num;
        if (isIntegerType) {
            num = new Integer(((Number) value).intValue());
        } else {
            num = new Double(((Number) value).doubleValue());
        }
        ((List) rowList.get(row)).set(col - 1, num);
        fireTableCellUpdated(row, col);
    }
}

class NumberCellEditor extends DefaultCellEditor {
    JTextField tf;
    Number currentValue;
    NumberFormat numFormat;
    NumberFormat numParser;
    public static final Border NORMAL_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    public static final Border ERROR_BORDER = BorderFactory.createLineBorder(Color.RED, 1);

    public NumberCellEditor() {
        super(UIUtil.createTextField());

        numFormat = NumberFormat.getInstance();
        numFormat.setGroupingUsed(false);
        numFormat.setMaximumFractionDigits(10);

        numParser = NumberFormat.getInstance();
        numParser.setGroupingUsed(true);
        numParser.setMaximumFractionDigits(10);

        tf = (JTextField) getComponent();

        tf.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                //System.out.println("current value:" + currentValue);
            }
        });

        tf.setBorder(NORMAL_BORDER);

        tf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //System.out.println("action performed is called");
            }
        });

        tf.setHorizontalAlignment(JTextField.RIGHT);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        tf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        tf.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tf.postActionEvent(); //inform the editor
            }
        });

        tf.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "no-escape");
        tf.getActionMap().put("no-escape", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                /* retrieve original value */
                tf.setBorder(NORMAL_BORDER);
                if (currentValue != null) {
                    tf.setText(numFormat.format(currentValue));
                } else {
                    tf.setText("0");
                }
                /* accept original value */
                if (isEditValid()) {
                    tf.postActionEvent(); //inform the editor
                }
            }
        });
    }

    public boolean isEditValid() {
        String editedValue = tf.getText();
        //tf.setText(editedName);
        try {
            if ("".equalsIgnoreCase(editedValue)) {
                editedValue = "0";
            }
            Number parsed = numParser.parse(editedValue);
            this.
            currentValue = parsed;
            tf.setText(numFormat.format(parsed));
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    //Override to invoke setValue on the formatted text field.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = (Number) value;
        tf.setText(numFormat.format(currentValue));
//        tf.setSelectionStart(0);
//        tf.setSelectionEnd(tf.getText().length());
        tf.selectAll();
        return tf;
    }

    public boolean stopCellEditing() {
        JTextField tf = (JTextField) getComponent();
        if (isEditValid()) {
            tf.setBorder(NORMAL_BORDER);
            //System.out.println("do commit and stop cell editing");
        } else { //text is invalid
            tf.setBorder(ERROR_BORDER);
            tf.requestFocusInWindow();
            return false; //don't let the editor go away

// below code gives revert option
//            if (! userSaysRevert()) { //user wants to edit
//                tf.setBorder(ERROR_BORDER);
//                return false; //don't let the editor go away
//            } else {
//                System.out.println("now normal");
//                tf.setBorder(NORMAL_BORDER);
//            }
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
            tf.setText(numFormat.format(currentValue));
            return true;
        }
        return false;
    }

    public Object getCellEditorValue() {
        return currentValue;
    }
}

class MatrixPopupEditorInsertDialog extends JDialog {

    ButtonGroup rowOrColRdGp;
    ButtonGroup insertAtRdGp;
    SpringLayout springLayout;

    public MatrixPopupEditorInsertDialog(JFrame owner) {
        super(owner, "insert row or column");
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        this.setSize(200, 215);
        setContentPane(mainPanel);
        springLayout = new SpringLayout();
        mainPanel.setLayout(springLayout);
        JLabel addLb = UIUtil.createLabel("add");
        JTextField rowOrColNumFd = UIUtil.createTextField("1", 4);
        JRadioButton[] rowOrColRd = new JRadioButton[2];
        rowOrColRd [0] = UIUtil.createRadioButton("row(s)");
        rowOrColRd [0].setSelected(true);
        rowOrColRd [1] = UIUtil.createRadioButton("column(s)");
        rowOrColRdGp = UIUtil.groupButtons(rowOrColRd);
        JPanel rowOrColPn = new JPanel();
        BoxLayout rowOrColPnLayout = new BoxLayout(rowOrColPn, BoxLayout.Y_AXIS);
        rowOrColPn.setLayout(rowOrColPnLayout);
        rowOrColPn.add(rowOrColRd [0]);
        rowOrColPn.add(rowOrColRd [1]);

        JRadioButton[] insertAtRd = new JRadioButton[3];
        insertAtRd [0] = UIUtil.createRadioButton("at start");
        insertAtRd [1] = UIUtil.createRadioButton("at end");
        insertAtRd [1].setSelected(true);
        insertAtRd [2] = UIUtil.createRadioButton("before selection");
        insertAtRdGp = UIUtil.groupButtons(insertAtRd);
        JPanel insertAtPn = new JPanel();
        BoxLayout insertAtPnLayout = new BoxLayout(insertAtPn, BoxLayout.Y_AXIS);
        insertAtPn.setLayout(insertAtPnLayout);
        insertAtPn.add(insertAtRd [0]);
        insertAtPn.add(insertAtRd [1]);
        insertAtPn.add(insertAtRd [2]);

        JLabel initValueLb = UIUtil.createLabel("initial value");
        JTextField initValueFd = UIUtil.createTextField("0", 8);

        JButton okBt = UIUtil.createButton("ok");
        JButton cancelBt = UIUtil.createButton("cancel");
        JPanel buttonPn = UIUtil.createButtonPanel(new Component[] { okBt, cancelBt });

        mainPanel.add(addLb);
        mainPanel.add(rowOrColNumFd);
        mainPanel.add(rowOrColPn);
        mainPanel.add(insertAtPn);
        mainPanel.add(initValueLb);
        mainPanel.add(initValueFd);
        mainPanel.add(buttonPn);

        SpringLayout.Constraints panelCons = springLayout.getConstraints(mainPanel);
        SpringLayout.Constraints addLbCons = springLayout.getConstraints(addLb);
        SpringLayout.Constraints rowOrColNumFdCons = springLayout.getConstraints(rowOrColNumFd);
        SpringLayout.Constraints rowOrColPnCons = springLayout.getConstraints(rowOrColPn);
        SpringLayout.Constraints insertAtPnCons = springLayout.getConstraints(insertAtPn);
        SpringLayout.Constraints initValueLbCons = springLayout.getConstraints(initValueLb);
        SpringLayout.Constraints initValueFdCons = springLayout.getConstraints(initValueFd);
        SpringLayout.Constraints buttonPnCons = springLayout.getConstraints(buttonPn);

        addLbCons.setConstraint(SpringLayout.WEST, Spring.sum(Spring.constant(5), panelCons.getConstraint(SpringLayout.EAST)));
        addLbCons.setConstraint(SpringLayout.NORTH, Spring.sum(Spring.constant(5), panelCons.getConstraint(SpringLayout.NORTH)));

        rowOrColNumFdCons.setConstraint(SpringLayout.WEST, Spring.sum(Spring.constant(3), addLbCons.getConstraint(SpringLayout.EAST)));
        rowOrColNumFdCons.setConstraint(SpringLayout.NORTH, Spring.sum(Spring.constant(5), panelCons.getConstraint(SpringLayout.NORTH)));

        rowOrColPnCons.setConstraint(SpringLayout.WEST, Spring.sum(Spring.constant(0), rowOrColPnCons.getConstraint(SpringLayout.EAST)));
        rowOrColPnCons.setConstraint(SpringLayout.NORTH, Spring.sum(Spring.constant(0), panelCons.getConstraint(SpringLayout.NORTH)));

        insertAtPnCons.setConstraint(SpringLayout.WEST, Spring.sum(Spring.constant(0), panelCons.getConstraint(SpringLayout.EAST)));
        insertAtPnCons.setConstraint(SpringLayout.NORTH, Spring.sum(Spring.constant(0), rowOrColPnCons.getConstraint(SpringLayout.SOUTH)));

        initValueLbCons.setConstraint(SpringLayout.WEST, Spring.sum(Spring.constant(5), panelCons.getConstraint(SpringLayout.EAST)));
        initValueLbCons.setConstraint(SpringLayout.NORTH, Spring.sum(Spring.constant(5), insertAtPnCons.getConstraint(SpringLayout.SOUTH)));

        initValueFdCons.setConstraint(SpringLayout.WEST, Spring.sum(Spring.constant(3), initValueLbCons.getConstraint(SpringLayout.EAST)));
        //initValueFdCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(3), panelCons.getConstraint(SpringLayout.EAST)));
        initValueFdCons.setConstraint(SpringLayout.NORTH, Spring.sum(Spring.constant(5), insertAtPnCons.getConstraint(SpringLayout.SOUTH)));

//        System.out.println(panelCons.getWidth());
//        System.out.println(buttonPnCons.getWidth());
//        System.out.println(Spring.sum(panelCons.getWidth(), Spring.minus(buttonPnCons.getWidth())));
        //buttonPnCons.setX(Spring.sum(panelCons.getWidth(), Spring.minus(buttonPnCons.getWidth())));
        //buttonPnCons.setConstraint(SpringLayout.EAST, panelCons.getWidth());
        //buttonPnCons.setConstraint(SpringLayout.WEST, panelCons.getConstraint(SpringLayout.WEST));
        buttonPnCons.setConstraint(SpringLayout.WEST, Spring.sum(Spring.constant(80), panelCons.getConstraint(SpringLayout.EAST)));
        buttonPnCons.setConstraint(SpringLayout.NORTH, Spring.sum(Spring.constant(5), initValueFdCons.getConstraint(SpringLayout.SOUTH)));

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        }, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void openEditor() {
        this.setVisible(true);
    }

    public void closeEditor() {
        this.setVisible(false);
    }

}