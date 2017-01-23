/*
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Oct 1, 2002
 * Time: 4:48:20 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import mit.cadlab.dome.swing.DialogFactory;
import mit.cadlab.dome.swing.Templates;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class CustomUnitBuildPanel extends CustomUnitBasePanel {

    protected int DefinitionColumnIndex = 2;
    protected UnitEditorBuildPanel editor;
    public static JDialog unitEditorDialog;
    protected JComboBox combo;

    public CustomUnitBuildPanel() {
        super();
        setComboColumn(comboColumn(), 3);
        tableDataModel.setValueAt("Other",0,3);
    }

    public Object[] makeColumnNames() {
        Object[] colNames = {"Name",
                             "Abbreviation",
                             "Definition",
                             "Class"};
        return colNames;
    }

    protected void addColumnListener() {
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (table.getSelectedColumn() == DefinitionColumnIndex) {
                    String[] testKeywords = UnitDatabase.getKeywords();
                    editor = new UnitEditorBuildPanel(table,testKeywords);
                    String unitName = String.valueOf(table.getValueAt(table.getSelectedRow(),0));
                    String existingDef = String.valueOf(table.getValueAt(
                            table.getSelectedRow(),DefinitionColumnIndex));
                    if (existingDef!="null") editor.valueTextField.setText(existingDef);
                    unitEditorDialog = DialogFactory.createDialog(table,"Unit Editor: "+unitName,editor,true,false);
                    unitEditorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    unitEditorDialog.setSize(300,120);
                    unitEditorDialog.show();
                }
            }
        });
    }

    public JComboBox comboColumn() {
        Object[] choices = {"Metric", "Custom", "Other"};
        combo = Templates.makeComboBox(choices);
        combo.setSelectedIndex(2);
        return combo;
    }

    protected void setComboColumn(JComboBox combo, int col) {
        TableColumn classColumn = table.getColumnModel().getColumn(col);
        classColumn.setCellEditor(new DefaultCellEditor(combo));
    }

}
