// DomeVectorBuildPanel.java   
//   based on VectorPanel of 04/11/02
//   ver 0.1

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
 * DomeVectorBuildPanel: extends basepanel, 
 *  add listener to the radiobuttons and textinput and buttons.
 *  allow user edit size and property types
 *   
 *
 */




public class DomeVectorBuildPanel extends DomeVectorBasePanel{
    // define components here
    //same components as basepanel
  
        
    static DomeVectorData answer=null; 
 
    public static DomeVectorData showDialog(Component parent,DomeVectorData data) {
   
	DomeVectorBuildPanel editor = new DomeVectorBuildPanel(data);
   
	JDialog d = DialogFactory.createDialog(parent,"Dome Vector Build Panel",editor,true,true);
   
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	d.pack();
 			     
	d.show();

	return editor.answer;  //return it
    }    

    /*
     * Constructors
     */

    public DomeVectorBuildPanel(){
	 super(new DomeVectorData());
     }

    public DomeVectorBuildPanel(DomeVectorData v){
	super(v);
	if(v==null) setDataModel_GUI(new DomeVectorData());
    }

    protected void configureComponents() {
     	constraintsButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		   
		}
	    });
	addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    VectorAddDialog.showDialog(vectorScrollPane,vectorTableModel,vectorTable.getSelectedRows(),vectorTable.getSelectedColumns());
		   
		}
	    });
	OkButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    answer=dataVector;
		    dispose();
		}
	    });
	deleteButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		   if(dataVector.isRowVector()){//columns
		       dataVector.removeItems(vectorTable.getSelectedColumns());
     
		   }
		   else{//rows
		       dataVector.removeItems(vectorTable.getSelectedRows());
     
		   }
		   
		
		}
	    });
	fillButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    Double value=fillDialog.showValueInput(vectorScrollPane,dataVector.initialValue);
		    if(value==null) return;
		    if(dataVector.isRowVector()){//columns
			dataVector.fillItems(vectorTable.getSelectedColumns(),value);
		    }
		    else{//rows
			dataVector.fillItems(vectorTable.getSelectedRows(),value);
		    }
		}
	    });
	fixSizeBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		   dataVector.setFixedSize(fixSizeBox.isSelected());
		}
	    });
	sizeTextInput.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    int size=Integer.parseInt(sizeTextInput.getText());
		    dataVector.setSize(size);
 		}
	    });
	rowRadioButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    if(rowRadioButton.isSelected()) {
			dataVector.setRowVector(true);
		    }
		}
	    });
	columnRadioButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    if(columnRadioButton.isSelected()) {
			dataVector.setRowVector(false);
		    }
		}
	    });
	unitComboBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    if(unitComboBox.getSelectedIndex()==0){//"no unit"
			dataVector.setUnit("");
			debug("set to no unit");
		    }
		    else  {
			dataVector.setUnit("units");
			debug("set to units");  //later change to unit option..
		    }
		}
	    });
	flavorComboBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
		    if(flavorComboBox.getSelectedIndex()==0){//"real"
			dataVector.setValueType("real");
			debug("type set to real");
		    }
		    else   {
			dataVector.setValueType("integer");
			debug("type set to integer");}
		
		}
	    });
       
    }
       
	

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Vector build Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new DomeVectorBuildPanel(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }
  
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

  private void debug(String msg){
     boolean debug=true;
     if(debug)
	 System.out.println("DomeVectorBuildPanel: "+msg);
    }

  
 
}


