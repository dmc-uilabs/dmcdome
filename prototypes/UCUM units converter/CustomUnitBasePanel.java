/*
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Oct 1, 2002
 * Time: 4:49:02 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import mit.cadlab.dome.swing.DTable;
import mit.cadlab.dome.swing.Templates;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import units.UnitTab;
import units.ParseException;

public class CustomUnitBasePanel extends DataObjectPanel {

    protected JButton addButton;
    protected JButton removeButton;
    protected JScrollPane scrollPane;
    protected DTable table;
    protected DefaultTableModel tableDataModel;
    protected UnitDatabase unitDataModel;
    protected boolean isDuplicated;
    protected boolean isEditable;

    public CustomUnitBasePanel() {
        tableDataModel = new DefaultTableModel(makeColumnNames(),1);
        unitDataModel = new UnitDatabase();
        layoutComponents(createComponents());
    }

    public Object[] makeColumnNames() {
               Object[] colNames = {};
        return colNames;
    }

    protected JComponent[] createComponents() {

        addButton = Templates.makeButton("Add");
        addButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
                //when a row is selected
                if (table.getSelectedRow()>=0) {
                    if (!scanEmptyCell()) {
                        scanDuplicate();
                        if (isEditable) {
                            // don't need to add a row
                            // need to update the converter
                            saveSelectedRowData();
                        }
                        if (!isDuplicated) {
                            // new unit
                            addTableRow();
                            saveSelectedRowData();
                            //UnitDatabase.printData(UnitDatabase.unitData);
                            //UnitDatabase.printKeywords(UnitDatabase.getKeywords());
                        }
                        if(!isEditable && isDuplicated) {
                            JOptionPane.showMessageDialog(scrollPane, "Please enter a new abbreviation"
                                    , "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(scrollPane, "Data not entered in one or more cells",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    addTableRow();
                }
            }
	    });

        removeButton = Templates.makeButton("Remove");
        removeButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
                //when a row is selected
                if (table.getSelectedRow()>=0) {
                    deleteSelectedRowData();
                    removeTableRow();
                    //UnitDatabase.printData(UnitDatabase.unitData);
                    //UnitDatabase.printKeywords(UnitDatabase.getKeywords());
                }
                else {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(scrollPane, "Please select a row to remove.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
	    });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        table = createTable(tableDataModel);
        addColumnListener();
        scrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));

        return new JComponent[]{buttonPanel, scrollPane};
    }

    // to be overwritten by subclass
    protected void addColumnListener() {
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {}
        });
    }

    // to be overwritten by subclass
    protected DTable createTable(DefaultTableModel tableModel) {
       DTable aTable = new DTable(tableModel);
       return aTable;
    }

    protected void layoutComponents(JComponent[] comps) {
    // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
        new GridBagConstraints(0,0,1,1,0.0,0.0,gbc.EAST,gbc.NONE,new Insets(0,0,0,0),0,0), // buttonPanel
        new GridBagConstraints(0,1,1,1,0.0,0.0,gbc.NORTH,gbc.BOTH,new Insets(0,0,0,0),0,0), // scrollPanel
	    };
        Templates.layoutGridBagB(this,comps,gbcs);
    }

    public void scanDuplicate() {
        String newAbb = String.valueOf(table.getValueAt(table.getSelectedRow(),1));
        String newName = String.valueOf(table.getValueAt(table.getSelectedRow(),0));
        isDuplicated = unitDataModel.scanAbbDuplicate(newAbb);
        isEditable = unitDataModel.isEditable(newAbb,newName);
    }

    public boolean scanEmptyCell() {
        boolean hasEmptyCell = false;
        for (int col=0; col<table.getColumnCount();col++) {
            if (table.getValueAt(table.getSelectedRow(),col)==null ||
                    String.valueOf(table.getValueAt(table.getSelectedRow(),col))=="") {
                hasEmptyCell = true;
                //break;
            }
        }
        return hasEmptyCell;
    }

    public void saveSelectedRowData() {
        String[] newRow = {"","","",""};
        for (int col=0; col<table.getColumnCount();col++) {
            newRow[col] = String.valueOf(table.getValueAt(table.getSelectedRow(),col));
        }
        String newKeyword = newRow[1];
        String newName = newRow[0];
        String newDef = newRow[2];
        String newClass = newRow[3];
        String newUnit = new String(newRow[1]+ " = "+ newRow[2]+ " "+newRow[3]+ " #"+ newRow[0]);

        // editing: need to delete converter's old definition first
        if (isEditable) {
            String oldDef = unitDataModel.getOldDef(newKeyword);
            String oldClass = unitDataModel.getOldClass(newKeyword);
            String oldUnit = new String(newRow[1]+ " = "+ oldDef+ " "+oldClass+ " #"+ newRow[0]);

            // remove the primer's old unit
            try{
                UnitTab.remove(oldUnit,newKeyword);
            }catch(IOException x){
			    JOptionPane.showMessageDialog(table, "Error: "+x.getMessage());
		    }catch(ParseException x){
			    JOptionPane.showMessageDialog(table, "Error: "+x.getMessage());
		    }

            //remove the editor database old's unit
            unitDataModel.removeRow(newKeyword,newName,oldDef,oldClass);
            unitDataModel.addRow(newKeyword,newName,newDef,newClass);
        }

        if (!isEditable) unitDataModel.addRow(newKeyword,newName,newDef,newClass);

        try{
            System.out.println("new unit = " + newUnit);
            UnitTab.insert(newUnit,unitDataModel.unitData, tableDataModel);
        }catch(IOException x){
			JOptionPane.showMessageDialog(table, "Error: "+x.getMessage());
		}catch(ParseException x){
			JOptionPane.showMessageDialog(table, "Error: "+x.getMessage());
		}
        unitDataModel.makeCopy();
    }

    public void addTableRow() {
        String[] newTableRow = {"","","","Other"};
        tableDataModel.addRow(newTableRow);
    }

    public void removeTableRow() {
        int selectedRowIndex = table.getSelectedRow();
        tableDataModel.removeRow(table.getSelectedRow());
        if (selectedRowIndex==0 && tableDataModel.getRowCount()==0) {addTableRow();
        }
    }

    public void deleteSelectedRowData() {
        String[] selectedRow = {"","","",""};
        for (int col=0; col<table.getColumnCount();col++) {
            selectedRow[col] = String.valueOf(table.getValueAt(table.getSelectedRow(),col));
        }
        String keyword = selectedRow[1];
        String name = selectedRow[0];
        String def = selectedRow[2];
        String aClass = selectedRow[3];
        String selectedUnit = new String(keyword+ " = "
                + def+ " "+aClass+ " #"+ name);

        unitDataModel.removeRow(keyword,name,def,aClass);

        try{
            UnitTab.remove(selectedUnit,keyword);
        }catch(IOException x){
			JOptionPane.showMessageDialog(table, "Error: "+x.getMessage());
		}catch(ParseException x){
			JOptionPane.showMessageDialog(table, "Error: "+x.getMessage());
		}
    }

    public void setDataObject(DataObject data) {
    }

    protected PropertyChangeListener getPropertyListener() {
        return null;
    }
}
