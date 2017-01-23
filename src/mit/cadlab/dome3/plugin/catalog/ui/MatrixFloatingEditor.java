package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import java.util.List;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.System;

/**
 * User: Sangmok Han
 * Date: Feb 15, 2007
 */
public class MatrixFloatingEditor extends JPanel {
    private ComponentReference compRef;
    private String qualifiedParamName;
    private JTable table;
    private JTextField rowCountFd;
    private JTextField columnCountFd;
    private MatrixValueEditorPane matrixEditorPane;
    protected JComponent titlePanel;
    public static final int COLUMN_WIDTH = 50;
    private FloatingWindowListener floatWindowListener;
    public static int RUBBER_BAND_SIZE = 19;

    private MatrixPopupTableModel tableModel;

    public MatrixFloatingEditor(String qualifiedParamName, ComponentReference compRef) {
        this.qualifiedParamName = qualifiedParamName;
        this.compRef = compRef;

        BaseCell cell = compRef.getCell(qualifiedParamName);
        MatrixValueEditorPane matrixValueEditorPane = (MatrixValueEditorPane) cell.getValueEditorPane();
        List rowList = DataObjectUtil.cloneRowList(matrixValueEditorPane.getMatrixValue());
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

    public void paint(Graphics g) {
        super.paint(g);
        floatWindowListener.setOrigin(this.getX(), this.getY());
    }

    public void init(List rowList) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        //titlePanel.setBackground(Color.LIGHT_GRAY);
        titlePanel.setOpaque(true);
        titlePanel.setMinimumSize(new Dimension(Short.MAX_VALUE, 19));
        titlePanel.setBorder(null);
        JLabel matrixParamNameLb = UIUtil.createSmallLabel(" name: " + qualifiedParamName);
        matrixParamNameLb.setPreferredSize(new Dimension(150, 17));
        matrixParamNameLb.setMinimumSize(new Dimension(150, 17));
        matrixParamNameLb.setMaximumSize(new Dimension(190, 17));
        titlePanel.add(matrixParamNameLb);

        tableModel = new MatrixPopupTableModel(false);
        int rowCount = 0;
        int columnCount = 0;
        if (rowList != null) {
            tableModel.rowList = rowList;
            rowCount = tableModel.getRowCount();
            columnCount = tableModel.getColumnCount() - 1;
        }

        rowCountFd = UIUtil.createTextField();
        rowCountFd.setText(Integer.toString(rowCount));
        rowCountFd.setBorder(UIUtil.FLOATING_EDITOR_BUTTON_BORDER);
        rowCountFd.setFont(UIUtil.SMALL_DIALOG_FONT);
        rowCountFd.setMinimumSize(new Dimension(20, 1));
        rowCountFd.setPreferredSize(new Dimension(20, 17));
        rowCountFd.setMaximumSize(new Dimension(20, 17));
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
        columnCountFd.setBorder(UIUtil.FLOATING_EDITOR_BUTTON_BORDER);
        columnCountFd.setFont(UIUtil.SMALL_DIALOG_FONT);
        columnCountFd.setMinimumSize(new Dimension(20, 1));
        columnCountFd.setPreferredSize(new Dimension(20, 17));
        columnCountFd.setMaximumSize(new Dimension(20, 17));
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

        titlePanel.add(UIUtil.createSmallLabel("size: "));
        titlePanel.add(rowCountFd);
        titlePanel.add(UIUtil.createSmallLabel(" x "));
        titlePanel.add(columnCountFd);

        titlePanel.add(UIUtil.createSmallLabel("  "));

        JButton updateBt = UIUtil.createButton("set");
        updateBt.setForeground(Color.BLACK);
        updateBt.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        updateBt.setFont(UIUtil.SMALL_DIALOG_FONT);
        updateBt.setBorder(UIUtil.FLOATING_EDITOR_BUTTON_BORDER);
        updateBt.setPreferredSize(new Dimension(25, 17));
        updateBt.setMaximumSize(new Dimension(25, 17));
        updateBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowCount = Integer.parseInt(rowCountFd.getText());
                int columnCount = Integer.parseInt(columnCountFd.getText());
                tableModel.setMatrixSize(rowCount, columnCount);
                tableModel.fireTableStructureChanged();
                resetColumnWidth();
                table.revalidate();
            }
        });
        titlePanel.add(updateBt);

//        titlePanel.setPreferredSize(new Dimension(430, 30));
//        titlePanel.setMaximumSize(new Dimension(430, 30));
//        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        titlePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        floatWindowListener = new FloatingWindowListener(this);
        titlePanel.addMouseListener(floatWindowListener);
        titlePanel.addMouseMotionListener(floatWindowListener);

        this.setBorder(UIUtil.FLOATING_EDITOR_BORDER);
        this.setBounds(0, 0, 300, 200);
        this.setVisible(false);


//        JPanel configPanel = new JPanel();
//        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));
//        //configPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//        JLabel paramNameLb = UIUtil.createLabel(" name: " + qualifiedParamName);
//        paramNameLb.setMinimumSize(new Dimension(80, 25));
//        paramNameLb.setPreferredSize(new Dimension(80, 25));
//        paramNameLb.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
//        configPanel.add(paramNameLb);
//        configPanel.add(Box.createHorizontalGlue());
//
//        configPanel.add(UIUtil.createLabel(" rows: "));
//        configPanel.add(rowCountFd);
//        configPanel.add(UIUtil.createLabel("   columns: "));
//        configPanel.add(columnCountFd);
//        configPanel.add(Box.createHorizontalStrut(2));
//        JButton updateBt = UIUtil.createButton("set");
//        updateBt.setPreferredSize(new Dimension(45, 21));
//        updateBt.setMaximumSize(new Dimension(45, 21));
//        updateBt.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                int rowCount = Integer.parseInt(rowCountFd.getText());
//                int columnCount = Integer.parseInt(columnCountFd.getText());
//                tableModel.setMatrixSize(rowCount, columnCount);
//                tableModel.fireTableStructureChanged();
//                table.revalidate();
//            }
//        });
//        configPanel.add(updateBt);
//        configPanel.add(Box.createHorizontalStrut(10));
//        //configPanel.add(Box.createHorizontalStrut(30));
//        configPanel.add(UIUtil.createLabel(" data type:  "));
//        JComboBox dataTypeCb = UIUtil.createComboBox(new String[] { "Real", "Integer" });
//        dataTypeCb.setMaximumSize(new Dimension(60, 21));
//        configPanel.add(dataTypeCb);
//        configPanel.setMinimumSize(new Dimension(300, 40));

        table = new JTable(tableModel);
        tableModel.setTable(table);
        table.setFont(UIUtil.SMALL_DIALOG_FONT);
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
        //buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));


        JButton applyBt = UIUtil.createSmallButton("apply");
        applyBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                matrixEditorPane.setMatrixValue(tableModel.getRowList());
                matrixEditorPane.acceptEditorValue();
                matrixEditorPane.getComponentReference().getImplementationEditor().requestFocusInWindow();
            }
        } );
        JButton okayBt = UIUtil.createSmallButton("okay");
        okayBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                matrixEditorPane.setMatrixValue(tableModel.getRowList());
                matrixEditorPane.acceptEditorValue();
                matrixEditorPane.getComponentReference().getImplementationEditor().requestFocusInWindow();
                MatrixFloatingEditor.this.setVisible(false);
            }
        } );
        JButton cancelBt = UIUtil.createSmallButton("cancel");
        cancelBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MatrixFloatingEditor.this.setVisible(false);
            }
        } );

        applyBt.setBorder(UIUtil.FLOATING_EDITOR_BUTTON_BORDER);
        okayBt.setBorder(UIUtil.FLOATING_EDITOR_BUTTON_BORDER);
        cancelBt.setBorder(UIUtil.FLOATING_EDITOR_BUTTON_BORDER);

        applyBt.setPreferredSize(new Dimension(36, 17));
        okayBt.setPreferredSize(new Dimension(36, 17));
        cancelBt.setPreferredSize(new Dimension(40, 17));

        JLabel dragLabel = new JLabel(UIUtil.createImageIcon("images/drag_rubber.gif", "drag here to resize"));
        dragLabel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));

        buttonPanel.add(applyBt);
        buttonPanel.add(okayBt);
        buttonPanel.add(cancelBt);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(dragLabel);


        //buttonPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 30));
        buttonPanel.setMinimumSize(new Dimension(Short.MAX_VALUE, 17));
        buttonPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 17));
        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 17));
        buttonPanel.setOpaque(true);

//        JPanel spreadsheetPanel = new JPanel();
//        spreadsheetPanel.setLayout(new BorderLayout());
//        spreadsheetPanel.add(scrollPane, BorderLayout.CENTER);
//        spreadsheetPanel.setBorder(null);
////        spreadsheetPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
////        spreadsheetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
//        //scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)));
//        JPanel toolButtonPanel = new JPanel();
//        toolButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
//        JButton insertBt = UIUtil.createButton("insert...");
//        JButton deleteBt = UIUtil.createButton("delete...");
//        JButton fillBt = UIUtil.createButton("fill...");
//
//        insertBt.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                MatrixPopupEditorInsertDialog dialog = new MatrixPopupEditorInsertDialog(ModelEditorFrame.getFrame());
//                dialog.setVisible(true);
//            }
//        });
//
//        toolButtonPanel.add(insertBt);
//        toolButtonPanel.add(deleteBt);
//        toolButtonPanel.add(fillBt);
//        table.setBackground(Color.WHITE);
//        scrollPane.setBackground(Color.WHITE);
        table.setOpaque(false);
//        spreadsheetPanel.add(toolButtonPanel, BorderLayout.PAGE_END);

//        contentPane.add(paramNamePanel);
        this.add(titlePanel);
//        this.add(configPanel);
        this.add(scrollPane);
        this.add(buttonPanel);

//        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
//        getRootPane().registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                MatrixFloatingEditor.this.setVisible(false);
//            }
//        }, "ESCAPE", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

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

class FloatingWindowListener extends MouseAdapter implements MouseMotionListener {
    boolean isMoving = false;
    int dragInsetX;
    int dragInsetY;
    int originX;
    int originY;

    MatrixFloatingEditor floatingEditor;

    public FloatingWindowListener(MatrixFloatingEditor floatingEditor) {
        this.floatingEditor = floatingEditor;
    }

    public void mouseReleased(MouseEvent event) {
        isMoving = false;
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
        //floatingEditor.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseMoved(MouseEvent event) {
        if (event.getY() <= MatrixFloatingEditor.RUBBER_BAND_SIZE) {
            floatingEditor.titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else {
            floatingEditor.titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
//        if (event.getX() > (getWidth() - RUBBER_BAND_SIZE) && event.getY() > (getHeight() - RUBBER_BAND_SIZE)) {
//            setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
//        } else {
//            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//        }
    }

    public void mouseDragged(MouseEvent event) {
        if (event.getY() < MatrixFloatingEditor.RUBBER_BAND_SIZE) {
            if (! isMoving) {
                isMoving = true;
                dragInsetX = event.getX();
                dragInsetY = event.getY();
                originX = floatingEditor.getX();
                originY = floatingEditor.getY();
            }
        }

        if (isMoving) {
            floatingEditor.setBounds(event.getX() - dragInsetX + originX, event.getY() - dragInsetY + originY, floatingEditor.getWidth(), floatingEditor.getHeight());
            floatingEditor.validate();
        }
    }

    protected void setOrigin(int originX, int originY) {
        this.originX = originX;
        this.originY = originY;
    }
}