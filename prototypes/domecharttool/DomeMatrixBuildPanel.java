// DomeMatrixBuildPanel.java   
//   based on MatrixPanel of 04/11/02
//   ver 0.1
//   ver 0.2  inherit GUI features from the base Panel

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;

/**
 * DomeMatrixBuildPanel: extends basepanel, 
 *  add listener to the radiobuttons and textinput and buttons.
 *  allow user edit size and property types
 *   
 *
 */




public class DomeMatrixBuildPanel extends DomeMatrixBasePanel{
    // define components here
    //same components as basepanel
  
        
    static DomeMatrixData answer=null; 
 
    public static DomeMatrixData showDialog(Component parent,DomeMatrixData data) {
   
	DomeMatrixBuildPanel editor = new DomeMatrixBuildPanel(data);
   
	JDialog d = DialogFactory.createDialog(parent,"Dome Matrix Build Panel",editor,true,true);
   
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	d.pack();
 			     
	d.show();

	return editor.answer;  //return it
    }    

    /*
     * Constructors
     */

    public DomeMatrixBuildPanel(){
	 super(new DomeMatrixData());
     }

    public DomeMatrixBuildPanel(DomeMatrixData v){
	super(v);
	if(v==null) setDataModel_GUI(new DomeMatrixData());
    }

    protected void configureComponents() {
     	constraintsButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		   
		}
	    });
	addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    MatrixAddDialog.showDialog(matrixScrollPane,dataMatrix,matrixTable.getSelectedRows(),matrixTable.getSelectedColumns());
		   
		}
	    });
	
	deleteButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		  DeleteDialog.showDialog(matrixScrollPane,dataMatrix,matrixTable.getSelectedColumns(),matrixTable.getSelectedRows());
		 
		}
	    });
	fillButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    Double value=fillDialog.showValueInput(matrixScrollPane,dataMatrix.initialValue);
		    if(value==null) return;
		    dataMatrix.fillItems(matrixTable.getSelectedPoints(),value);
		}
	    });
	fixSizeBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		   dataMatrix.setFixedSize(fixSizeBox.isSelected());
		}
	    });
	rowsTextInput.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		  int row=Integer.parseInt(rowsTextInput.getText());
		  int column=Integer.parseInt(columnsTextInput.getText());
		  
		  dataMatrix.setRowCount(row);
		    
		  dataMatrix.setColumnCount(column);
 		}
	    });
	columnsTextInput.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		  int row=Integer.parseInt(rowsTextInput.getText());
		  int column=Integer.parseInt(columnsTextInput.getText());
		  
		  dataMatrix.setRowCount(row);
		    
		  dataMatrix.setColumnCount(column);
		   
 		}
	    });	
	unitComboBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    if(unitComboBox.getSelectedIndex()==0){//"no unit"
			dataMatrix.setUnit("");
			debug("set to no unit");
		    }
		    else  {
			dataMatrix.setUnit("units");
			debug("set to units");  //later change to unit option..
		    }
		}
	    });
	flavorComboBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    if(flavorComboBox.getSelectedIndex()==0){//"real"
			dataMatrix.setValueType("real");
			debug("type set to real");
		    }
		    else   {
			dataMatrix.setValueType("integer");
			debug("type set to integer");}
		
		}
	    });
       
    }
       
	

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Matrix build Panel");
    DomeMatrixData d4=new DomeMatrixData(5,3,false,new Double(0));

    for(int i=0;i<5;i++)
      for(int j=0;j<3;j++)
	{
	  d4.setItem(i,j,new Double(i-0.1));
	
	}
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    DomeMatrixBuildPanel p=new DomeMatrixBuildPanel();
    f.getContentPane().add(p, BorderLayout.CENTER);
    p.setDataObject(d4);
    f.pack();
    f.setVisible(true);
  }
  
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

  private void debug(String msg){
     boolean debug=false;
     if(debug)
	 System.out.println("DomeMatrixBuildPanel: "+msg);
    }

  
 
}


