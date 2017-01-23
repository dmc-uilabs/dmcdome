// EditPlayspaceTableModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.guiutils.treetable.Renderers;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceBuild;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.util.Vector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 *
 */
public class EditPlayspaceTableModel extends DefaultTableModel
{

	private Vector ModelsAndProjects = new Vector();


	public EditPlayspaceTableModel()
	{
	}

	public EditPlayspaceTableModel(Object[] columnNames, int rowCount)
	{
		super(columnNames, rowCount);
	}

	/**
	 * That function overwrite the one in DefaultTableModel, at the moment the behaviour is the same but
	 * depending how we store the data in the <rowVector> it may have to be modified.
	 * @param row
	 * @param column
	 * @return
	 */
	public Object getValueAt(int row, int column)
	{
		Vector rowVector = (Vector) dataVector.elementAt(row);
		return rowVector.elementAt(column);
	}

	public Vector getRow(int row)
	{
		return (Vector) dataVector.elementAt(row);
	}

	public String getType(int i)
	{
		return (String) ((Vector) ModelsAndProjects.get(i)).get(5);
	}

	public String getId(int i)
	{
		return (String) ((Vector) ModelsAndProjects.get(i)).get(4);
	}

	public void addRow(Object[] rowData)
	{
		Vector v = new Vector();
		for (int i = 0; i < rowData.length; i++) {
			v.add(rowData[i]);
		}
		ModelsAndProjects.add(v);
		super.addRow(rowData);
	}

	public void removeRow(int row)
	{
		ModelsAndProjects.remove(row);
		super.removeRow(row);
	}

	public Class getColumnClass(int columnIndex)
	{
		if (columnIndex == 0) return Icon.class;
		return String.class;
	}

	public boolean isCellEditable(int row, int column)
	{
		if (column == 2)
			return true;
		else
			return false;
	}

	public boolean isInPlayspace(DomeFile so)
	{

		Vector v;
		for (int i = 0; i < ModelsAndProjects.size(); i++) {
			v = (Vector) ModelsAndProjects.get(i);
			if ((so.getId().toString()).equals(v.get(4)))
				return true;
		}
		return false;
	}


}

