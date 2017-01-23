// MatrixAddDialog.java
//  ver 0.1 May 23,2002
//   inherit from the basepanel which already has layout the GUI

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;
import javax.swing.table.*;
import java.util.*;

public class MatrixAddDialog extends AddDialogBasePanel{

  
  DomeMatrixData Model;
  int[] selectedRows;
  int[] selectedColumns;
 
   
  public static void showDialog(Component parent,DomeMatrixData model,int[] rows,int[] columns) {
   
    MatrixAddDialog editor = new MatrixAddDialog(model,rows,columns);
   
    JDialog d = DialogFactory.createDialog(parent,"Add Element",editor,true,false);
   
    d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
 			     
    d.show();
   
    
     
  }     

 
  public MatrixAddDialog(DomeMatrixData Model,int[] rows,int[] columns)
  {
     super();
     this.Model=Model;
     this.selectedRows=rows;
     this.selectedColumns=columns;
    
     configureComponents();
     
   }


  protected void configureComponents(){

 
      OkButton.addActionListener(new ActionListener(){
	  public void actionPerformed(ActionEvent e){
	   
	    Double value;
	    int size;
	    try{
       
	      value=new Double(Double.parseDouble(afterTextField.getText()));
	      size=Integer.parseInt(sizeTextField.getText());
	    }catch(Exception ee){
	      return;//just return
	    }
	    addValue(size,value);
	     
    
	    dispose();
	  }
	});
      cancleButton.addActionListener(new ActionListener(){
	  public void actionPerformed(ActionEvent e){
	 
	    dispose();
	  }
	});
				     
      
      String s=Model.getInitialValue().toString();
      afterTextField.setText(s);
     
      rowRadioButton.setSelected(true);
      columnRadioButton.setSelected(false);
	
      
     
      afterTextField.addKeyListener(new KeyAdapter(){
	public void keyPressed(KeyEvent e) {
	  if(e.getKeyCode()==KeyEvent.VK_ENTER){
	    
	    
	    Double value;
	    int size;
	    try{
       
	      value=new Double(Double.parseDouble(afterTextField.getText()));
	      size=Integer.parseInt(sizeTextField.getText());
	    }catch(Exception ee){
	      return;//just return
	    }
	    addValue(size,value);
	     
    
	    dispose();
	  }
	}
       });
   
  }
 


  private void addValue(int size,Double value){ 
    if(rowRadioButton.isSelected()){
      if(startRadioButton.isSelected()) {//add to the start
	Model.addRowItems(0,size,value);
      }
      else if(endRadioButton.isSelected()){//add to the end
	Model.addRowItems(Model.getRowCount(),size,value);
      }
      else if(beforeRadioButton.isSelected()){//add to the least selection
	if(min(selectedRows)==-1) //not selected any row or column
	  Model.addRowItems(Model.getRowCount(),size,value); //add to end by default
	else
	  Model.addRowItems(min(selectedRows),size,value);
      }
    }
    else if(columnRadioButton.isSelected()){
	  
      if(startRadioButton.isSelected()) {//add to the start
	Model.addColumnItems(0,size,value);
      }
      else if(endRadioButton.isSelected()){//add to the end
	Model.addColumnItems(Model.getColumnCount(),size,value);
      }
      else if(beforeRadioButton.isSelected()){//add to the least selection
	if(min(selectedColumns)==-1) //not selected any row or column
	  Model.addColumnItems(Model.getColumnCount(),size,value); //add to end by default
	else
	  Model.addColumnItems(min(selectedColumns),size,value);
      }
    }
   
  }




    private void debug(String msg){
	boolean debug=true;
	if(debug)
	    System.out.println("Matrix AddDialog: "+msg);
    }
}
