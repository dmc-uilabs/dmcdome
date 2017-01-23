// AuxFilesPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelcomponent.auxfile;

import mit.cadlab.dome3.gui.guiutils.table.DomeTable;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.GuiConstants;
import mit.cadlab.dome3.util.FileUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 */
public class AuxFilesPanel extends JPanel {
    protected Model model;
    protected JTable auxFilesTable;
    protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
	protected JLabel toplabel;

    public AuxFilesPanel(Model m) {
        super();
        this.model = m;

        auxFilesTable = new JTable(new AuxFilesTableModel());
        auxFilesTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        auxFilesTable.setFont(GuiConstants.FONT11);

        //make first column very slim
        TableColumn column = auxFilesTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(20);


        auxFilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(auxFilesTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        //layout
        toplabel = Templates.makeLabel("Auxilary Files for model:" + model.getName());
        model.addPropertyChangeListener(NameListener.NAME, new NameListener(){
			public void nameChanged(String newName)
			{
				toplabel.setText("Auxilary Files for model:" + newName);
			}
		});
	    setLayout(new GridBagLayout());
        JComponent[] comp = {toplabel,
                             scrollPane
        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] cons = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 5), 0, 0),
        };

        Templates.layoutGridBagB(this, comp, cons);
    }

    public void addSelectionListenerForTable(ListSelectionListener l) {
        ListSelectionModel rowSM = auxFilesTable.getSelectionModel();
        rowSM.addListSelectionListener(l);
    }

    class AuxFilesTableModel extends AbstractTableModel implements PropertyChangeListener {
        public String[] columnNames = {"#", "ID", "Name", "Location", "Size", "ExcuteOnServer"};

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == DomeModel.AUXFILES_MODIFIED) {
                fireTableDataChanged();
            }
        }

        public AuxFilesTableModel() {
            model.addPropertyChangeListener(this);
        }


        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return ((AbstractDomeModel) model).getAuxFiles().size();
        }

        public int getColumnCount() {
            return 6;
        }

        public AbstractAuxFile getAuxFileAt(int rowIndex) {
            if (rowIndex <= (((AbstractDomeModel) model).getAuxFiles().size())) {
                return (AbstractAuxFile) ((AbstractDomeModel) model).getAuxFiles().get(rowIndex);
            }
            return null;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return new Integer(rowIndex);
            }
            if (columnIndex == 1) {
                return getAuxFileAt(rowIndex).getId();
            }
            if (columnIndex == 2) {
                return getAuxFileAt(rowIndex).getName();
            }
            if (columnIndex == 3) {
                return getAuxFileAt(rowIndex).getFile().getPath();
            }
            if (columnIndex == 4) {
                return FileUtils.getApproxFileSize(getAuxFileAt(rowIndex).getFile().getPath());
            }
            if (columnIndex == 5) {
                if (getAuxFileAt(rowIndex).isExecuteOnServer())
                    return "Y";
                else
                    return "N";
            }
            return null;
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

    }
}
