// CausalityInfoEditorDialog.java
package mit.cadlab.dome3.gui.objectmodel.causality;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildPanel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.LoopException;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.MultipleErrorsException;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class CausalityInfoEditorDialog extends JPanel {

    protected static GridBagConstraints gbc;
    protected static final String title = "Edit Causality Information";
    protected static final String instructions = "Check box if object in row depends on object in column.";
    protected static final Dimension WARNING_SIZE1 = new Dimension(280, 100);
    protected static final Dimension WARNING_SIZE2 = new Dimension(250, 100);

    protected static final int MAX_COUNT = 40;


    protected TableChangeListener tableChangeListener;
    protected DependencyEditorTableModel dataModel;
    protected JTable dataTable;
    protected JTable rowTable;
    protected DependencyInfo dependencyInfo = null; // answer
    protected Component parent;

    public static DependencyInfo showEditor(Component parent, List objects) {
        CausalityInfoEditorDialog editor = new CausalityInfoEditorDialog(parent, objects);
        JDialog d = DialogFactory.createDialog(parent, title, editor, true, true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.show();
        return editor.dependencyInfo;
    }

    public static DependencyInfo showEditor(Component parent, List objects, DependencyInfo table) {
        if (table == null)
            return showEditor(parent, objects);
        CausalityInfoEditorDialog editor = new CausalityInfoEditorDialog(parent, objects, table);
        JDialog d = DialogFactory.createDialog(parent, title, editor, true, true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.show();
        return editor.dependencyInfo;
    }

    private CausalityInfoEditorDialog(Component parent, List objects) {
        this.parent = parent;
        dataModel = new DependencyEditorTableModel(objects);
        tableChangeListener = new TableChangeListener();
        dataModel.addTableModelListener(tableChangeListener);
        dependencyInfo = new DependencyInfo(objects);
        configureLargeEditor();
    }

    private CausalityInfoEditorDialog(Component parent, List objects, DependencyInfo table) {
        this.parent = parent;
        dependencyInfo = table;
        dataModel = new DependencyEditorTableModel(objects, table);
        configureLargeEditor();
    }

    private void configureLargeEditor() {
        JComponent[] comps = {createBigTableEditor(),
                              makeCheckButtonPanel(),
                              Templates.makeLabel(instructions),
                              makeButtonPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0),
        };
        Templates.layoutGridBagB(this, comps, gbcs);
    }


    private JScrollPane createBigTableEditor() {
        JScrollPane sp = new JScrollPane();
        dataTable = CausalityInfoTableBase.createDataTable(dataModel);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rowTable = CausalityInfoTableBase.createRowHeaderTable(dataModel.getColumnNames());
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataTable.setSelectionModel(rowTable.getSelectionModel());

        dataTable.setCellSelectionEnabled(true);
        dataTable.setColumnSelectionAllowed(true);

        JViewport dataViewport = new JViewport();
        dataViewport.setView(dataTable);
        dataViewport.setSize(dataTable.getPreferredSize());
        dataViewport.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        sp.setViewport(dataViewport);

        JViewport viewport = new JViewport();
        viewport.setView(rowTable);
        viewport.setPreferredSize(rowTable.getPreferredSize());
        viewport.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        sp.setRowHeaderView(viewport);

        sp.setCorner(JScrollPane.UPPER_LEFT_CORNER, new CausalityInfoTableBase.DarkerBackgroundPanel());
        Dimension dataDim = dataTable.getPreferredSize();
        Dimension rowDim = rowTable.getPreferredSize();
        Dimension headerDim = dataTable.getTableHeader().getPreferredSize();

        if (dataModel.getColumnCount() < MAX_COUNT) {
            Dimension dim = new Dimension(dataDim.width+rowDim.width +10,
                    dataDim.height+headerDim.height+10);
            sp.setPreferredSize(dim);
        }
        return sp;
    }

    public DependencyInfo getDependencyInfo() {
        return dataModel.getDependencyInfo();
    }

    private JPanel makeCheckButtonPanel() {
       JPanel p = new JPanel();

       JButton checkButton = Templates.makeButton("check selection", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //WaitCursorUtils.showWaitCursor(true, CausalityInfoEditorDialog.this);
                dataModel.fill(dataTable.getSelectedRows(), dataTable.getSelectedColumns(), Boolean.TRUE);
                //WaitCursorUtils.showWaitCursor(false, CausalityInfoEditorDialog.this);
            }
       });

       JButton unCheckButton = Templates.makeButton("uncheck selection", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //WaitCursorUtils.showWaitCursor(true, CausalityInfoEditorDialog.this);
                dataModel.fill(dataTable.getSelectedRows(), dataTable.getSelectedColumns(), Boolean.FALSE);
                //WaitCursorUtils.showWaitCursor(false, CausalityInfoEditorDialog.this);
            }
       });
       p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
       p.add(Box.createHorizontalGlue());
       p.add(checkButton);
       p.add(Box.createHorizontalStrut(5));
       p.add(unCheckButton);
       return p;
    }

    private JPanel makeButtonPanel() {
        JButton okButton = Templates.makeButton("ok", new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    DependencyInfo newDependencyInfo = dataModel.getDependencyInfo();
                    newDependencyInfo.validate(); // if invalid, throws exceptions
                    dependencyInfo = newDependencyInfo; // set answer
                    if (parent instanceof ProceduralRelationBuildPanel) {
                        ProceduralRelationBuildPanel parentpane = (ProceduralRelationBuildPanel) parent;
                        ProceduralRelation r = (ProceduralRelation) parentpane.getRelation();
                        r.setDependencyInfo(newDependencyInfo);
                    }
                    if (parent instanceof EqualRelationBuildPanel) {
                        EqualRelationBuildPanel parentpane = (EqualRelationBuildPanel) parent;
                        EqualRelation r = (EqualRelation) parentpane.getRelation();
                        r.setDependencyInfo(newDependencyInfo);
                    }
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (ex instanceof LoopException) {
                        OneButton2Msg.showWarning(CausalityInfoEditorDialog.this, "Causality warning",
                                "form a loop.\nPlease remove the loop.", ex.getMessage(), "OK", WARNING_SIZE1);
                    } else {
                        if (ex instanceof MultipleErrorsException) {
                            int nonLoopErrors = 0;
                            Iterator errors = ((MultipleErrorsException) ex).getErrorList().iterator();
                            while (errors.hasNext()) {
                                Exception error = (Exception) errors.next();
                                if (error instanceof LoopException) {
                                    OneButton2Msg.showWarning(CausalityInfoEditorDialog.this, "Causality warning",
                                            "form a loop.\nPlease remove the loop.", error.getMessage(), "OK", WARNING_SIZE1);
                                } else {
                                    System.err.println(error);
                                    nonLoopErrors++;
                                }
                            }
                            if (nonLoopErrors > 0)
                                OneButton1Msg.showWarning(CausalityInfoEditorDialog.this, "Warning: causality",
                                        "Exception specifying causality.\nSee message log for details.",
                                        "OK", WARNING_SIZE2);
                        } else {
                            System.err.println(ex);
                            OneButton1Msg.showWarning(CausalityInfoEditorDialog.this, "Warning: causality",
                                    "Exception specifying causality.\nSee message log for details.",
                                    "OK", WARNING_SIZE2);

                        }
                    }
                }
            }
        });
        JButton cancelButton = Templates.makeButton("cancel", new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        p.add(okButton);
        p.add(Box.createHorizontalStrut(5));
        p.add(cancelButton);
        return p;
    }

    private void dispose() {
        SwingUtilities.windowForComponent(this).dispose();
    }

    public class DependencyEditorTableModel extends DefaultTableModel
            implements CausalityInfoTableBase.DependencyTableModel {
        // assumes that modelObjects vector will not change at all during editing

        protected List objects;
        protected Vector columnNames;

        /**
         * Initialize with list of model objects
         */
        public DependencyEditorTableModel(List objects) {
            this.objects = objects;
            columnNames = createColumnNames();
            Vector starterData = createInitialDataVector();
            setDataVector(starterData, columnNames);
        }

        /**
         * Initialize with list of model objects and dependency table
         */
        public DependencyEditorTableModel(List objects, DependencyInfo table) {
            this.objects = objects;
            columnNames = createColumnNames();
            Vector starterData = createInitialDataVector(table);
            setDataVector(starterData, columnNames);
        }

        public void fill(int[] rows, int[] cols, Boolean b) {
            for (int r = 0; r < rows.length; r++) {
                int row = rows[r];
                for (int c = 0; c < cols.length; c++) {
                    int col = cols[c];
                    if (row != col)
                        this.setValueAt(b, row, col);
                }
            }
        }

        public Vector getColumnNames() {
            return (Vector) columnNames.clone();
        }

        private Vector createColumnNames() {
            Vector v = new Vector();
            Iterator objects = this.objects.iterator();
            while (objects.hasNext()) {
                v.add(getNameForObject(objects.next()));
            }
            return v;
        }

        private Vector createInitialDataVector() {
            Vector templateRow = new Vector();
            for (int i = 0; i < objects.size(); ++i)
                templateRow.add(Boolean.FALSE);
            Vector rows = new Vector();
            for (int i = 0; i < objects.size(); ++i) {
                rows.add(templateRow.clone());
            }
            return rows;
        }

        private Vector createInitialDataVector(DependencyInfo table) {
            Vector rows = createInitialDataVector();
            Iterator rowObjects = table.getOutputs().iterator();
            while (rowObjects.hasNext()) {
                Object rowObject = rowObjects.next();
                int rowIndex = objects.indexOf(rowObject);
                Iterator dependents = table.getDependentsForObject(rowObject).iterator();
                while (dependents.hasNext()) {
                    Object dependent = dependents.next();
                    int columnIndex = objects.indexOf(dependent);
                    Vector rowVector = (Vector) rows.get(rowIndex);
                    rowVector.set(columnIndex, Boolean.TRUE);
                }
            }
            return rows;
        }

        public Class getColumnClass(int columnIndex) {
            return Boolean.class;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return !isRowColumnSame(rowIndex, columnIndex);
        }

        public DependencyInfo getDependencyInfo() { // not validated
            DependencyInfo table = new DependencyInfo(objects);
            for (int i = 0; i < getRowCount(); ++i) {
                List rowData = getDataForRow(i);
                if (rowData != null) {
                    Object obj = rowData.remove(0);
                    table.setDependency(obj, rowData);
                }
            }
            return table;
        }

        public boolean isRowColumnSame(int row, int column) {
            return (row == column);
        }

        private List getDataForRow(int row) {
            // format = rowobject, dependents
            Object rowObject = objects.get(row);
            List rowDependents = new ArrayList();
            for (int i = 0; i < getColumnCount(); ++i) {
                if (getValueAt(row, i).equals(Boolean.TRUE))
                    rowDependents.add(objects.get(i));
            }
            if (rowDependents.isEmpty())
                return null;
            else {
                rowDependents.add(0, rowObject);
                return rowDependents;
            }
        }

        private String getNameForObject(Object obj) {
            if (obj instanceof ModelObject)
                return ((ModelObject) obj).getName();
            else
                return obj.toString();
        }

    }

    public class TableChangeListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            int startRow = e.getFirstRow();
            int endRow = e.getLastRow();
            int column = e.getColumn();
            int type = e.getType();

            if (startRow == endRow && type == TableModelEvent.UPDATE) {
                type = type;
            }
        }
    }

}
