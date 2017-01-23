// CausalityInfoRendererTable.java
package mit.cadlab.dome3.gui.objectmodel.causality;

import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

public class CausalityInfoRendererTable extends JScrollPane
{

	protected DependencyRendererTableModel dataModel;
	protected TableModelListener tableChangeListener;
	protected JTable dataTable;
	protected JTable rowTable;

	// for initialization
	protected boolean needsInitialization = false;
	protected List i_objects;
	protected DependencyInfo i_table;

	public CausalityInfoRendererTable()
	{
	}

	public CausalityInfoRendererTable(List objects, DependencyInfo table)
	{
		setDependencyInfo(objects, table);
		setMinimumSize(new Dimension(380, 80));
		setPreferredSize(new Dimension(380, 100));
	}

	public void setDependencyInfo(List objects, DependencyInfo table)
	{
		if (!isValid()) {
			i_objects = objects;
			i_table = table;
			needsInitialization = true;
		}
		configureViewport(objects, table);
		configureRowView();
		dataTable.setSelectionModel(rowTable.getSelectionModel());
		setCorner(JScrollPane.UPPER_LEFT_CORNER, new CausalityInfoTableBase.DarkerBackgroundPanel());
	}

	public boolean containsName(String name)
	{
		return dataModel.containsName(name);
	}

	public void addNotify()
	{
		super.addNotify();
		if (needsInitialization) {
			setDependencyInfo(i_objects, i_table);
			i_objects = null;
			i_table = null;
		}
	}

	private void configureViewport(List objects, DependencyInfo table)
	{
		dataModel = new DependencyRendererTableModel(objects, table);
		tableChangeListener = new TableChangeListener();
		dataModel.addTableModelListener(tableChangeListener);
		dataTable = CausalityInfoTableBase.createDataTable(dataModel);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JViewport dataViewport = new JViewport();
		dataViewport.setView(dataTable);
		dataViewport.setSize(dataTable.getPreferredSize());
		dataViewport.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		setViewport(dataViewport);
	}

	private void configureRowView()
	{
		rowTable = CausalityInfoTableBase.createRowHeaderTable(dataModel.getRowNames());
		JViewport viewport = new JViewport();
		viewport.setView(rowTable);
		viewport.setPreferredSize(rowTable.getPreferredSize());
		viewport.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		setRowHeaderView(viewport);
	}

	// DependencyRendererTableModel.java
	public class DependencyRendererTableModel extends DefaultTableModel
	        implements CausalityInfoTableBase.DependencyTableModel
	{

		// assumes that modelObjects vector will not change at all during editing
		protected List objects;
		protected Vector columnNames; // backward compatibility
		protected List rowNames;
		protected DependencyInfo table;

		/**
		 * Initialize with list of model objects and dependency table
		 */
		public DependencyRendererTableModel(List objects, DependencyInfo table)
		{
			columnNames = createColumnNames(objects, table);
			Vector starterData = createInitialDataVector(objects, table);
			this.table=table;
			setDataVector(starterData, columnNames);
		}

		public boolean isRowColumnSame(int row, int column)
		{
			//Qing May 22: this is not correct for plugin model since shouldn't need the parameter has the same names
			//return rowNames.get(row).equals(columnNames.get(column));
			//should compare object instead
			return getObjectAt(true,row)==(getObjectAt(false,column));
		}

		public Object getObjectAt(boolean isRow,int index){
			if(isRow){
				return table.getOutputs().get(index);
			}
			else{
				return objects.get(index);

			}
		}

		public boolean containsName(String name)
		{
			return columnNames.contains(name) || rowNames.contains(name);
		}

		public Vector getColumnNames()
		{
			return columnNames;
		}

		public List getRowNames()
		{
			return rowNames;
		}

		private Vector createColumnNames(List allObjects, DependencyInfo table)
		{
			// create columns
			objects = new ArrayList();
			List triggers = table.getTriggers();
			for (int i = 0; i < allObjects.size(); ++i)
				if (triggers.contains(allObjects.get(i)))
					objects.add(allObjects.get(i));
			Vector v = new Vector();
			Iterator objects = this.objects.iterator();
			while (objects.hasNext()) {
				Object obj = objects.next();
				if (obj instanceof ModelObject)
					v.add(((ModelObject) obj).getName());
				else
					v.add(obj.toString());
			}
			return v;
		}

		private Vector createInitialDataVector()
		{
			Vector templateRow = new Vector();
			for (int i = 0; i < objects.size(); ++i)
				templateRow.add(Boolean.FALSE);
			Vector rows = new Vector();
			for (int i = 0; i < rowNames.size(); ++i) {
				rows.add(new Vector(templateRow));
			}
			return rows;
		}

		private Vector createInitialDataVector(List allObjects, DependencyInfo table)
		{
			rowNames = new Vector();
			List tableOutputs = table.getOutputs();
			List outputs = new ArrayList();
			for (int i = 0; i < allObjects.size(); ++i) {
				Object obj = allObjects.get(i);
				if (tableOutputs.contains(obj)) {
					outputs.add(obj);
					rowNames.add(getNameForObject(obj));
				}
			}
			if (rowNames.size() == 0) { // no data
				columnNames = new Vector(); // clear all columns
				return new Vector();
			}
			Vector rows = createInitialDataVector();
			Iterator rowObjects = outputs.iterator();
			while (rowObjects.hasNext()) {
				Object rowObject = rowObjects.next();
				//Qing May 22th: shouldn't use name to index parameter, otherwise will have problem
				int rowIndex = outputs.indexOf(rowObject);
				//int rowIndex = rowNames.indexOf(getNameForObject(rowObject));
				Iterator dependents = table.getDependentsForObject(rowObject).iterator();
				while (dependents.hasNext()) {
					Object dependent = dependents.next();
					int columnIndex = objects.indexOf(dependent);
					List rowList = (List) rows.get(rowIndex);
					rowList.set(columnIndex, Boolean.TRUE);
				}
			}
			return rows;
		}

		public Class getColumnClass(int columnIndex)
		{
			return Boolean.class;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}

		private String getNameForObject(Object obj)
		{
			if (obj instanceof ModelObject)
				return ((ModelObject) obj).getName();
			else
				return obj.toString();
		}

	}

	public class TableChangeListener implements TableModelListener
	{
		public void tableChanged(TableModelEvent e)
		{
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
