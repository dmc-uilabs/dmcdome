package mit.cadlab.dome3.plugin.catalog.ui;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 3.
 */
import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.plugin.catalog.core.CUnit;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnitEditor extends AbstractCellEditor implements TableCellEditor {

    //Color currentColor;
    CUnit currentUnit;
    JButton button;
    //JColorChooser colorChooser;
    //UnitChooserDialog unitChooser;
    //JDialog dialog;
    protected static final String EDIT = "edit";

    public UnitEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton();
        //button.setActionCommand(EDIT);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //if (EDIT.equals(event.getActionCommand())) {
                //The user has clicked the cell, so bring up the dialog.
                //button.setBackground(currentColor);
                button.setText(currentUnit.getDescription());
                //colorChooser.setColor(currentColor);


                //Unit unit = unitChooser.setVisible(true);
                Unit selectedUnit = UnitChooserDialog.showDialog(null, currentUnit.getUnit());

                if (selectedUnit != null) {
                    currentUnit = new CUnit(selectedUnit);
                }

                //Make the renderer reappear.
                fireEditingStopped();

                //}
            }
        });

        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 0));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        //button.setBorderPainted(true);

        //unitChooser = new UnitChooserDialog(null, CUnit.NO_UNIT_STR.getUnit());
        //unitChooser.setVisible(false);

        //Set up the dialog that the button brings up.
//        colorChooser = new JColorChooser();
//        dialog = JColorChooser.createDialog(button,
//                                        "Pick a Color",
//                                        true,  //modal
//                                        colorChooser,
//                                        this,  //OK button handler
//                                        null); //no CANCEL button handler
    }

//    /**
//     * Handles events from the editor button and from
//     * the dialog's OK button.
//     */
//    public void actionPerformed(ActionEvent e) {
//        if (EDIT.equals(e.getActionCommand())) {
//            //The user has clicked the cell, so bring up the dialog.
//            //button.setBackground(currentColor);
//            button.setText(currentUnit.toString());
//            //colorChooser.setColor(currentColor);
//
//
//            unitChooser.setVisible(true);
//            System.out.println("am i waiting");
//
//            if (unitChooser.getUnitSelection() != null) {
//                currentUnit = new CUnit(unitChooser.getUnitSelection());
//            }
//
//            //Make the renderer reappear.
//            fireEditingStopped();
//
//        }
////        else { //User pressed dialog's "OK" button.
////            System.out.println("unitChooser.getUnitSelection(): " + unitChooser.getUnitSelection());
////            currentUnit = new CUnit(unitChooser.getUnitSelection());
////
////        }
//    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentUnit;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        //currentColor = (Color) value;
        currentUnit = (CUnit) value;
        return button;
    }
}

