package editPlayspace;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelListener;
import javax.swing.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 13, 2003
 * Time: 7:51:43 PM
 * To change this template use Options | File Templates.
 */
public class EditPlayspaceTableModel extends DefaultTableModel {

	private Vector ModelsAndProjects = new Vector();

	public EditPlayspaceTableModel() {
	}

	public EditPlayspaceTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	/**
	 * That function overwrite the one in DefaultTableModel, at the moment the behaviour is the same but
	 * depending how we store the data in the <rowVector> it may have to be modified.
	 * @param row
	 * @param column
	 * @return
	 */
	public Object getValueAt(int row, int column) {
		Vector rowVector = (Vector) dataVector.elementAt(row);
		return rowVector.elementAt(column);
	}

	public Vector getRow(int row){
		return (Vector) dataVector.elementAt(row);
	}

	public String getType(int i){
		return (String)((Vector) ModelsAndProjects.get(i)).get(4);
	}

	public String getId(int i) {
		return (String) ((Vector) ModelsAndProjects.get(i)).get(3);
	}

	public void addRow(Object[] rowData) {
		Vector v = new Vector();
		for(int i=0; i<rowData.length; i++){
			v.add(rowData[i]);
		}
		ModelsAndProjects.add(v);
		super.addRow(rowData);
	}

	public void removeRow(int row) {
		ModelsAndProjects.remove(row);
		super.removeRow(row);
	}

	public Class getColumnClass(int columnIndex) {
		if(columnIndex == 0) return Icon.class;
		return String.class;
	}


}
