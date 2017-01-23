/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//package mit.cadlab.dome.gui.components;

import javax.swing.*;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.accessibility.*;

import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.table.*;
import javax.swing.border.*;

import java.text.NumberFormat; 
import java.text.DateFormat;

public class MyTable extends JTable {
  Vector points = new Vector();
  Point startpoint = new Point(-1, -1);

    public MyTable() {
        super(null, null, null);
	setUI(new MyTableUI());
    }

    public MyTable(TableModel dm) {
        super(dm, null, null);
	setUI(new MyTableUI());
    }

    public MyTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm, null);
	setUI(new MyTableUI());
    
    }

    public MyTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm,cm,sm);
	setUI(new MyTableUI());
    }

    public MyTable(int numRows, int numColumns) {
      super(numRows,numColumns);
      setUI(new MyTableUI());
    }

    public MyTable(final Vector rowData, final Vector columnNames) {
      super(rowData,columnNames);
      setUI(new MyTableUI());
    }

    public MyTable(final Object[][] rowData, final Object[] columnNames) {
      super(rowData,columnNames);
      setUI(new MyTableUI());
    }

    public boolean isCellSelected(int row, int column) { 
      Point p = new Point(row,column);
      return points.contains(p);
    }



  public void setMySelectedPoint(int r, int c) {
    Point p = new Point(r,c);
    if (points.contains(p)) {
      //System.out.println("unselect: "+p);
      points.remove(p);
    } else { // select it
      //System.out.println("select: "+p);
      points.add(p);
    }
    //tableChanged(new TableModelEvent(getModel(),r,r,c));
    repaint();
  }

  public void setMyStartPoint(int r, int c) {
    startpoint = new Point(r,c);
  }


  public void clearPoints() {
    if (points!=null && !points.isEmpty()) {
      points.clear();
      repaint();
    }
  }

  private void adjustPoints() {
    if(points.contains(startpoint)) {
      int i=points.indexOf(startpoint);
      for(int j=i+1;j<points.size();) {
	points.removeElementAt(j);
      }
    }
  }

  public Vector getSelectedPoints(){
    return this.points;
      }
  
  public void setMySelectedPointRect(int r, int c) {
    if (startpoint != new Point(-1, -1)) {
      adjustPoints();
      int x = startpoint.x;
      int y = startpoint.y;
      int z;
      if (r>x) { z=r;r=x;x=z; }
      if (c>y) { z=c;c=y;y=z; }
      for(int i=r; i<=x; i++)
	for(int j=c; j<=y; j++) {
	  Point p = new Point(i,j);
	  if(!points.contains(p)) {
	    points.add(p);
	    repaint();
	  }
	}
    }
  }

public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Object value = getValueAt(row, column);
	boolean isSelected = isCellSelected(row, column);
	boolean rowIsAnchor = (selectionModel.getAnchorSelectionIndex() == row);
	boolean colIsAnchor = 
	    (columnModel.getSelectionModel().getAnchorSelectionIndex() == column);
	boolean hasFocus= false;
	if(startpoint.x==row && startpoint.y==column) {
	  hasFocus = true;
	} else {
	  hasFocus = false; // (rowIsAnchor && colIsAnchor) && hasFocus();
	  }
	return renderer.getTableCellRendererComponent(this, value,
	                                              isSelected, hasFocus,
	                                              row, column);
    }


  public int[] getSelectedRows() {
    Vector rows=new Vector();
    for(int i=0;i<points.size();i++)
      {
	Point p=(Point)points.elementAt(i);
	Integer row=new Integer(p.x);
	if(!rows.contains(row)) rows.add(row);
      }
    debug(rows.toString());
    
    int[] rowArray=new int[rows.size()];
    for(int i=0;i<rows.size();i++){
      rowArray[i]=((Integer)rows.elementAt(i)).intValue();
    }
    
    return rowArray;
  }

  public int[] getSelectedColumns(){
    Vector columns=new Vector();
    for(int i=0;i<points.size();i++)
      {
	Point p=(Point)points.elementAt(i);
	Integer column=new Integer(p.y);
	if(!columns.contains(column)) columns.add(column);
      }
    debug(columns.toString());
    
    int[] columnArray=new int[columns.size()];
    for(int i=0;i<columns.size();i++){
      columnArray[i]=((Integer)columns.elementAt(i)).intValue();
    }
    
    return columnArray;

  }


  public void tableChanged(TableModelEvent e) {
    if (e.getType()==TableModelEvent.INSERT || e.getType()==TableModelEvent.DELETE)
      clearPoints();
    else if (e.getFirstRow()==TableModelEvent.HEADER_ROW) // columns change
      clearPoints();
    super.tableChanged(e);
  }

  private void debug(String msg){
     boolean debug=true;
     if(debug)
	 System.out.println("MyTable: "+msg);
    }
}
