// VectorAddDialog.java
//  ver 0.1 May 23,2002
//   inherit from the basepanel which already has layout the GUI

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;
import javax.swing.table.*;
import java.util.*;

public class VectorAddDialog extends AddDialogBasePanel{

  
  DomeVectorTableModel tableModel;
  int[] selectedRows;
  int[] selectedColumns;
 
   
  public static void showDialog(Component parent,DomeVectorTableModel tablemodel,int[] rows,int[] columns) {
   
    VectorAddDialog editor = new VectorAddDialog(tablemodel,rows,columns);
   
    JDialog d = DialogFactory.createDialog(parent,"Add Element",editor,true,false);
   
    d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
 			     
    d.show();
   
    
     
  }     

 
  public VectorAddDialog(DomeVectorTableModel tableModel,int[] rows,int[] columns)
  {
     super();
     this.tableModel=tableModel;
     this.selectedRows=rows;
     this.selectedColumns=columns;
    
     configureComponents();
     
   }


  protected void configureComponents(){

 
      OkButton.addActionListener(new ActionListener(){
	  public void actionPerformed(ActionEvent e){
	    if(tableModel instanceof DomeVectorTableModel)
	      {
		Double value;
		int size;
		try{
       
		  value=new Double(Double.parseDouble(afterTextField.getText()));
		  size=Integer.parseInt(sizeTextField.getText());
		}catch(Exception ee){
		  return;//just return
		}
		addValue(size,value);
	      }
    
	    dispose();
	  }
	});
      cancleButton.addActionListener(new ActionListener(){
	  public void actionPerformed(ActionEvent e){
	 
	    dispose();
	  }
	});
				     
     
      if(tableModel instanceof DomeVectorTableModel){
	String s=((DomeVectorTableModel)tableModel).data.getInitialValue().toString();
	
	afterTextField.setText(s);
	
	boolean isRowVector=((DomeVectorTableModel)tableModel).isRowVector();
	
	rowRadioButton.setSelected(!isRowVector);
	columnRadioButton.setSelected(isRowVector);
	
	setRowColumnMode(!isRowVector,isRowVector);
      }
      
      afterTextField.addKeyListener(new KeyAdapter(){
	public void keyPressed(KeyEvent e) {
	  if(e.getKeyCode()==KeyEvent.VK_ENTER){
	    
	    //same as okay
	     if(tableModel instanceof DomeVectorTableModel)
	      {
		Double value;
		int size;
		try{
       
		  value=new Double(Double.parseDouble(afterTextField.getText()));
		  size=Integer.parseInt(sizeTextField.getText());
		}catch(Exception ee){
		  return;//just return
		}
		addValue(size,value);
	      }
    
	    dispose();
	  }
	}
       });
   
  }
 


  private void addValue(int size,Double value){ 
       if(startRadioButton.isSelected()) {//add to the start
	 tableModel.addRowsOrColumns(0,size,value);
       }
       else if(endRadioButton.isSelected()){//add to the end
	 tableModel.addRowsOrColumns(tableModel.data.getSize(),size,value);
       }
       else if(beforeRadioButton.isSelected()){//add to the least selection
	 if(tableModel.isRowVector())
	   if(min(selectedColumns)==-1)
	     tableModel.addRowsOrColumns(tableModel.getColumnCount(),size,value); //add to end by default
	   else
	     tableModel.addRowsOrColumns(min(selectedColumns),size,value);
	 else
	   if(min(selectedRows)==-1)
	     tableModel.addRowsOrColumns(tableModel.getRowCount(),size,value); //add to end by default
	   else
	     tableModel.addRowsOrColumns(min(selectedRows),size,value);	
        }
  }




    private void debug(String msg){
	boolean debug=true;
	if(debug)
	    System.out.println("AddDialog: "+msg);
    }
}
