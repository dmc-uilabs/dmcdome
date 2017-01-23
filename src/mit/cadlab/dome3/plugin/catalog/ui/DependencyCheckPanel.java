package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 4.
 *
 * DependencyCheckPanel works in two mode: dynamic and static
 *
 * dynamic mode is used when parameters are added/removed like when we define local relation or interface.
 * static mode is for displaying dependency information of implementation
 *
 * why use dynamic mode?
 * sometimes DepedencyCheckPanel provided with ParameterDefinitionPanels for editing input params and output params.
 * in this case, we hope this dependency check panels input/output parameter names are automatically reflect the
 * changes maded in ParameterDefinitionPanels. to achieve this, in dynamic mode, input/output parameter names is
 * listening to changes in ParameterDefinitionPanels, and they will be automatically respond to addition or removal of parameters
 *
 * why use static mode?
 * sometimes DepedencyCheckPanel provided without ParameterDefinitionPanels for editing input params and output params.
 * We just want to display depedency between a certain set of input and output parameters, so what we need to do should
 * giving DependencyCheckPanel the set of input and output parameters (using setInputParameterNames() and setOutputParameterNames())
 * and populate DepCheckTableModel as it needs to be displayed.
 *
 * to make check panel dynamic, set constructor with argument two TableModel argument iParamModel and oParamModel, this CheckPanel's column names and row names will be listening to changes in those two models.
 * to make check panel static, set constructor with no argument, and call setInputParameterNames() and setOutputParameterNames() when input and output parameters are known. for usage, please refer InterfaceDialog.setImplementationDependency()
 */
public class DependencyCheckPanel extends JScrollPane {

    private TableModel oParamModel = null;
    private TableModel iParamModel = null;

    private List oParamNames = new ArrayList();
    private List iParamNames = new ArrayList();

    private DepCheckTableModel tableModel;
    private JTable table;

    /** constructor for dynamic mode */
    public DependencyCheckPanel(TableModel iParamModel, TableModel oParamModel) {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.iParamModel = iParamModel;
        this.oParamModel = oParamModel;

        updateInputParamNames();
        updateOutputParamNames();

        initComponents();
    }

    /** constructor for static mode */
    public DependencyCheckPanel() {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        initComponents();
    }

    public void setEditable(boolean isFreezed) {
        tableModel.setFreeze(true);
    }

    /** [for dynamic mode] should be called when input parameters added or removed */
    protected void updateInputParamNames() {
        iParamNames.clear();
        for (int i = 0; i < iParamModel.getRowCount(); i++) {
            iParamNames.add((String) iParamModel.getValueAt(i, 0));
        }
    }

    /** [for dynamic mode] should be called when input parameters added or removed */
    protected void updateOutputParamNames() {
        oParamNames.clear();
        for (int i = 0; i < oParamModel.getRowCount(); i++) {
            oParamNames.add((String) oParamModel.getValueAt(i, 0));
        }
    }

    /** update input and output parameter names and uncheck all dependencies */
    protected void reset() {
        updateInputParamNames();
        updateOutputParamNames();
        tableModel.setUncheckedAll();
    }

    /** [for static mode] called when input param names are changed */
    public void setInputParamNames(List iParamNames) {
        this.iParamNames = iParamNames;
        tableModel.fireTableStructureChanged();

    }

    /** [for static mode] called when input param names are changed */
    public void setOutputParamNames(List oParamNames) {
        this.oParamNames = oParamNames;
        tableModel.fireTableStructureChanged();
    }

    public JTable getTable() {
        return table;
    }

    private void initComponents() {
        tableModel = new DepCheckTableModel();
        table = new JTable();
        this.setViewportView(table);

        //TableColumnModel colModel = table.getColumnModel();

        table.setFont(UIUtil.DIALOG_FONT);
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableCellRenderer renderer = new DependencyCheckTableRenderer();
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(Boolean.class, renderer);
        table.getTableHeader().setReorderingAllowed(false);

        /* listener is added only in dynamic mode (oParamModel is not null and iParamModel is not null) */
        if (oParamModel != null) {
            oParamModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent event) {
                    updateOutputParamNames();
                    tableModel.fireTableStructureChanged();
                }
            });
        }

        if (iParamModel != null) {
            iParamModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent event) {
                    updateInputParamNames();
                    tableModel.fireTableStructureChanged();
                }
            });
        }

        tableModel.fireTableStructureChanged();
    }

    class DepCheckTableModel extends DefaultTableModel {
        java.util.List rowList;
        boolean isFreezed = false; // used for displaying dependency purpose

        /**
         *   col0          col3
         *   [ ]  [ ]  [ ]  [ ]  [ ]
         *   [X]  [X]  [X]  [X]  [ ] // row 1
         *   [X]  [X]  [X]  [X]  [ ] // row 2
         *   [ ]  [ ]  [ ]  [ ]  [ ]
         *
         * To check dependency table like above, setChecked(1, 0, 4, 2);
         * 1 is the row index of the upper left corner, and 0 is the column index of the upper left corner
         * 4 is the width of rows to be checked, and 2 is the height of columns to be checked.
         * To check all cells, you may use setCheckedAll();
         * row idx and column idx start from zero.
         */
        public void setChecked(int upperLeftRowIdx, int upperLeftColumnIdx, int heightOfRowsToBeChecked, int widthOfColumnsToBeChecked) {
            for (int i = upperLeftRowIdx; i < upperLeftRowIdx + heightOfRowsToBeChecked; i++) {
                for (int j = upperLeftColumnIdx; j < upperLeftColumnIdx + widthOfColumnsToBeChecked; j++) {
                    if (getValueAt(i, j + 1).equals(Boolean.FALSE)) {
                        setValueAt(Boolean.TRUE, i, j + 1);
                        fireTableCellUpdated(i, j + 1);
                    }
                }
            }
        }

        protected List getValueRowList() {
            return rowList;
        }

        /** make all cells checked */
        public void setCheckedAll() {
            setChecked(0, 0, oParamNames.size(), iParamNames.size());
        }

        /** make all cells unchecked */
        public void setUncheckedAll() {
            int size = rowList.size();
            if (size > 0) {
                rowList.clear();
                fireTableRowsUpdated(0, size - 1);
            }
        }

        public DepCheckTableModel() {
            rowList = new ArrayList();
        }

        public void add(int insertRow, Boolean[] rowData) {
            rowList.add(rowData);
            fireTableRowsInserted(insertRow, insertRow);
        }

        public void remove(int rowIdx) {
            rowList.remove(rowIdx);
            fireTableRowsDeleted(rowIdx, rowIdx);
        }

        public int getRowCount() {
            if (rowList == null) {
                return 0;
            }
            return oParamNames.size();
        }

        public int getColumnCount() {
            return iParamNames.size() + 1;
        }

        public String getColumnName(int col) {
            if (col == 0) return "below output depends on:";
            return (String) iParamNames.get(col - 1);
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return oParamNames.get(row);
            }
            if (row >= rowList.size()) {
                return Boolean.FALSE;
                //return Boolean.TRUE; // make it checked as default
            } else if ((col - 1) >= ((Boolean[]) rowList.get(row)).length) {
                return Boolean.FALSE;
                //return Boolean.TRUE; // make it checked as default
            }
            return ((Boolean[]) rowList.get(row)) [col - 1];
        }

        public boolean isCellEditable(int row, int col) {
            if (isFreezed) {
                return false;
            }

            if (col == 0) return false;
            return true;
        }


        public Class getColumnClass(int col) {
            if (col == 0) return String.class;
            return Boolean.class;
        }

        public void setValueAt(Object value, int row, int col) {
            if (row >= rowList.size()) {
                while (rowList.size() < row + 1) {
                    Boolean[] newRow = new Boolean[getColumnCount() - 1];
                    for (int i = 0; i < newRow.length; i++) {
                        newRow[i] = Boolean.FALSE;
                        //newRow[i] = Boolean.TRUE; // make it checked as default
                    }
                    rowList.add(newRow);
                }
            } else if ((col - 1) >= ((Boolean[]) rowList.get(row)).length) {
                Boolean[] oldRow = ((Boolean[]) rowList.get(row));
                Boolean[] newRow = new Boolean[getColumnCount() - 1];
                for (int i = 0; i < newRow.length; i++) {
                    if (i < oldRow.length) {
                        newRow[i] = oldRow[i];
                    } else {
                        newRow[i] = Boolean.FALSE;
                        //newRow[i] = Boolean.TRUE; // make it checked as default
                    }
                }
                rowList.set(row, newRow);
            }
            ((Boolean[]) rowList.get(row)) [col - 1] = (Boolean) value;
            fireTableCellUpdated(row, col);
        }

        public void setFreeze(boolean isFreezed) {
            this.isFreezed = isFreezed;
        }
    }
}

class DependencyCheckTableRenderer implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    JButton labelRenderComp;
    JCheckBox checkRenderComp;

    public DependencyCheckTableRenderer() {
        labelRenderComp = new JButton();
        labelRenderComp.setFont(UIUtil.DIALOG_FONT);
        Border headerCellBorder = (Border) UIManager.getDefaults().get("TableHeader.cellBorder");
        labelRenderComp.setBorder(headerCellBorder);
        labelRenderComp.setOpaque(true);
        labelRenderComp.setForeground(Color.BLACK);
        //labelRenderComp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        //labelRenderComp.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        //labelRenderComp.setBackground(Color.LIGHT_GRAY);
        labelRenderComp.setHorizontalAlignment(SwingConstants.CENTER);
        labelRenderComp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
            }
        });

        checkRenderComp = new JCheckBox();
        checkRenderComp.setOpaque(false);
        checkRenderComp.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
            labelRenderComp.setText((String) obj);
            return labelRenderComp;
        } else {
            if (obj != null) {
                checkRenderComp.setSelected(((Boolean) obj).booleanValue());
            } else {
                checkRenderComp.setSelected(false);
            }
            return checkRenderComp;
        }
    }
}
