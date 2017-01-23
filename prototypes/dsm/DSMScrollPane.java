package dsm;

import mit.cadlab.dome.swing.CompositeIcon;
import mit.cadlab.dome.swing.VTextIcon;
import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.swing.table.ObjectTable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Component;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class DSMScrollPane extends JScrollPane implements ListSelectionListener
{

	int n;                    //dsm Size
	int[][] originalMatrix;  //Original Matrix
	Object[][] sortedMatrix;  //Matrix that dsm currently show--change to object for show

	String[] sortedColumnNumbers;    //Name list of each element
	String[] originalColumnNumbers;   //Name list of each element
	String[] _originalActivities;
	String[] _sortedActivities;
	int x_selected = -1;
	int y_selected = -1;
	JTable rowTable, tableView, columnTable;

	public static final GridBagConstraints gbc = null;


	public DSMScrollPane(int[][] nMatrix, String[] columns, String[] activities)
	{
		super();
		//this.setOriginal(nMatrix, columns, activities);
		//initialize
		n = nMatrix.length;

		// check possibly input error here:
		if (nMatrix.length != columns.length)
			System.out.println("incompatiable!!!" + nMatrix.length + "," + columns.length);

		// initialize memory for arrays
		originalMatrix = new int[n][n];
		sortedMatrix = new Object[n][n];
		originalColumnNumbers = new String[n];
		sortedColumnNumbers = new String[n];
		_originalActivities = new String[n];
		_sortedActivities = new String[n];

		//read data into arrays
		int i,j;
		for (i = 0; i < n; i++)
		{
			for (j = 0; j < n; j++)
			{
				originalMatrix[i][j] = nMatrix[i][j];
			}
			this.originalColumnNumbers[i] = columns[i];
			this._originalActivities[i] = activities[i];
		}

	}

	/*public void setOriginal(int[][] nMatrix, String[] columns, String[] activities)
	{

		// draw the scrollpane
		drawDSM(this.originalMatrix, this.originalColumnNumbers, this._originalActivities);

	}*/

	public void drawDSM(int[][] matrixData, String[] columnNumbers, String[] activityNames)
	{
		n = matrixData.length;
		//initialize
		int i,j;
		for (i = 0; i < n; i++)
		{
			for (j = 0; j < n; j++)
			{
				sortedMatrix[i][j] = new Integer(matrixData[i][j]);
			}
			this.sortedColumnNumbers[i] = columnNumbers[i];
			this._sortedActivities[i] = activityNames[i];
		}

		this.createMainTable();
		this.createRowHeaderTable();
		this.createColumnHeaderTable();
	}

	protected void createMainTable()
	{
		AbstractTableModel dataModel = new AbstractTableModel()
		{
			public int getColumnCount()
			{
				return DSMScrollPane.this.sortedMatrix.length;
			}

			public int getRowCount()
			{
				return getColumnCount();
			}

			public Object getValueAt(int row, int col)
			{
				return DSMScrollPane.this.sortedMatrix[row][col];
			}

			public String getColumnName(int column)
			{
				return DSMScrollPane.this.originalColumnNumbers[column];
			}

			public Class getColumnClass(int c)
			{
				return Integer.class;
			}

			public boolean isCellEditable(int row, int col)
			{
				return false;
			}
		};

		tableView = new JTable(dataModel);

		//  tableView.setPreferredScrollableViewportSize(new Dimension(250, 250));
		tableView.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		//adjust size
		tableView.setFont(Templates.FONT11);
		tableView.setGridColor(Color.LIGHT_GRAY);
		tableView.setShowGrid(false);
		tableView.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		tableView.setRowHeight(20);
		tableView.setOpaque(true);
		TableColumn column = null;
		for (int i = 0; i < tableView.getColumnModel().getColumnCount(); i++)
		{
			column = tableView.getColumnModel().getColumn(i);
			column.setMaxWidth(tableView.getRowHeight());
			column.setMinWidth(2);
		}
		tableView.setPreferredSize(new Dimension(tableView.getRowHeight() * tableView.getRowCount(), tableView.getRowHeight() * tableView.getRowCount()));
		tableView.setDefaultRenderer(Integer.class, new DSMCellRenderer(this.getBackground()));

		//create selection cross-hairs
		tableView.setRowSelectionAllowed(true);
		tableView.setColumnSelectionAllowed(true);
		tableView.setSelectionForeground(Color.white);
		tableView.setSelectionBackground(Color.DARK_GRAY);
		tableView.getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		tableView.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

		//add selection listener
		tableView.getSelectionModel().addListSelectionListener(this);
		tableView.getColumnModel().getSelectionModel().addListSelectionListener(this);
		tableView.setIntercellSpacing(new Dimension(0, 0));
		this.setViewportView(tableView);
	}

	protected void createRowHeaderTable()
	{
		//Create a model of the project names.
		try
		{
			rowTable = new JTable(new RowHeaderModel());
			rowTable.setShowGrid(false); rowTable.setFont(Templates.FONT11);
			rowTable.setRowHeight(20);
			rowTable.setIntercellSpacing(new Dimension(0, 0));
			JTableHeader rowTableHeader = rowTable.getTableHeader();
			rowTableHeader.setResizingAllowed(false);
			TableColumn column = rowTable.getColumnModel().getColumn(0);
			column.setCellRenderer(new ActivitiesCellRenderer(this.getBackground(), rowTable));
			int maxWidth = 0;
			JLabel l = new JLabel("Activities");
			l.setHorizontalAlignment(JLabel.RIGHT);
			maxWidth = Math.max(maxWidth, l.getPreferredSize().width);
			for(int i=0; i<this._sortedActivities.length; i++)
			{
				l = new JLabel(this._sortedActivities[i]);
				maxWidth = Math.max(maxWidth, l.getPreferredSize().width);
			}

			column.setPreferredWidth(maxWidth+10);
			rowTable.setBackground(this.getBackground());

			TableColumn numberColumn = rowTable.getColumnModel().getColumn(1);
			numberColumn.setPreferredWidth(20);
			numberColumn.setWidth(20);
			numberColumn.setCellRenderer(new ActivitiesCellRenderer(this.getBackground(), rowTable));

			JViewport viewport = new JViewport();
			viewport.setView(rowTable);
			viewport.setPreferredSize(rowTable.getPreferredSize());
			viewport.setBackground(this.getBackground());
			this.setRowHeaderView(viewport);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected void createColumnHeaderTable()
	{
		try
		{
			Object[][] testData = null;
			Object[] activityNames = new Object[this.sortedColumnNumbers.length];
			Object[] columnNames = new Object[this.sortedColumnNumbers.length];
			for(int i=0; i<this.sortedColumnNumbers.length; i++)
			{
				activityNames[i] = this._sortedActivities[i];
				columnNames[i] = this.originalColumnNumbers[i];
			}
			testData = new Object[][]{activityNames,columnNames};
			columnTable = new ObjectTable(new DsmDataObjectTableModel(testData, this._sortedActivities.length, this.getBackground()));
			for(int i=0; i<this.sortedColumnNumbers.length; i++)
			{
				columnTable.getColumnModel().getColumn(i).setPreferredWidth(20);
				columnTable.getColumnModel().getColumn(i).setWidth(20);
				columnTable.getColumnModel().getColumn(i).setMaxWidth(20);
			}
			columnTable.setShowGrid(false);
			columnTable.setFont(Templates.FONT11);
			columnTable.setIntercellSpacing(new Dimension(0, 0));
			JTableHeader columnTableHeader = columnTable.getTableHeader();
			columnTableHeader.setResizingAllowed(false);
			for(int i=0; i<this.sortedColumnNumbers.length; i++)
			{
				columnTableHeader.getColumnModel().getColumn(i).setHeaderRenderer(new VerticalCompositeRenderer(this.getBackground(),this.originalColumnNumbers[i],this._sortedActivities[i]));
			}
			int maxHeight = 0;
			JLabel l = new JLabel("Activities");
			l.setHorizontalAlignment(JLabel.RIGHT);
			maxHeight = Math.max(maxHeight, l.getPreferredSize().width);
			for (int i = 0; i < this._sortedActivities.length; i++)
			{
				l = new JLabel(this._sortedActivities[i]);
				maxHeight = Math.max(maxHeight, l.getPreferredSize().width);
			}
			columnTable.setRowHeight(0,maxHeight+20); columnTable.setRowHeight(1,20);
			columnTable.setMaximumSize(new Dimension(this._sortedActivities.length * 20, maxHeight + 40));
			columnTable.setBackground(this.getBackground()); columnTable.setOpaque(true);
			JViewport viewport = new JViewport();
			viewport.setView(columnTable);
			viewport.setPreferredSize(columnTable.getPreferredSize());
			viewport.setBackground(this.getBackground());
			this.setColumnHeaderView(viewport);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 *   catch table selection change here
	 */

	public void valueChanged(ListSelectionEvent e)
	{
		try
		{
			if (e.getSource() == tableView.getSelectionModel())
			{
				this.y_selected = tableView.getSelectedRow();
			}
			else
			{
				this.x_selected = tableView.getSelectedColumn();
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public int getSelectedX()
	{
		return this.x_selected;
	}

	public int getSelectedY()
	{
		return this.y_selected;
	}

	public static class VerticalCompositeRenderer extends DefaultTableCellRenderer
	{
		protected Color headerColor;
		VerticalCompositeRenderer(Color rowTableColor, String columnNumber, String activity)
		{
			headerColor = rowTableColor;
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.BOTTOM);
			setValue(columnNumber, activity);

		}
		protected void setValue(Object columnValue, Object value)
		{
			VTextIcon textIcon = new VTextIcon(this, (value == null) ? "" : value.toString(), VTextIcon.ROTATE_LEFT);
			if(columnValue == null)
				return;
			VTextIcon columnIcon = new VTextIcon(this,columnValue.toString());
			CompositeIcon compositeIcon = new CompositeIcon(columnIcon, textIcon);
			setIcon(compositeIcon);
		}

	}
	public static class ActivitiesCellRenderer extends DefaultTableCellRenderer
	{
		protected Color headerColor;

		ActivitiesCellRenderer(Color rowTableColor, JTable table)
		{
			JTableHeader header = table.getTableHeader();
			headerColor = rowTableColor;
			setHorizontalAlignment(JLabel.RIGHT);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setBackground(headerColor);
		}
	}
	class RowHeaderModel extends AbstractTableModel
	{
		public int getRowCount()
		{
			return n;
		}

		public int getColumnCount()
		{
			return 2;
		}

		public Object getValueAt(int row, int column)
		{
			if(column == 0)
			{
				return DSMScrollPane.this._sortedActivities[row];
			}
			else if(column == 1)
			{
				return DSMScrollPane.this.originalColumnNumbers[row];
			}
			else return "";
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public String getColumnName(int column)
        {
	        if(column == 0)
	            return "Activities";
            else
		        return "";
        }
	}

	class ColumnHeaderModel extends AbstractTableModel
	{
		public int getRowCount()
		{
			return 2;
		}
		public int getColumnCount()
		{
			return n;
		}
		public Object getValueAt(int row, int column)
		{
			if(row == 0)
			{
				return DSMScrollPane.this._sortedActivities[column];
			}
			else if(row == 1)
			{
				return DSMScrollPane.this.originalColumnNumbers[column];
			}
			else return "";
		}
		public Class getRowClass(int rowIndex)
		{
			return String.class;
		}
		public String getColumnName(int row)
		{
			if(row == 0)
			{
				return "Activities";
			}
			else
				return "";
		}

	}
}