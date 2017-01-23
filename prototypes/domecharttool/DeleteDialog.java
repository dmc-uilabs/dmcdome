// DeleteDialog.java  
//    ver 0.1  4/14/02
//    ver 0.2  5/28/02  
//             ver history: changed static function, make it directly operate on the data object
//

//package mit.cadlab.dome.gui.components;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;
import javax.swing.table.*;
import java.util.*;

public class DeleteDialog extends JPanel implements ActionListener{
  // define components here
 
  JRadioButton rowRadioButton;
  JRadioButton columnRadioButton;
  JButton OkButton;
  JButton cancleButton;
  JPanel contentPanel;
 
  Font font11=new Font("Dialog", Font.PLAIN, 11);
  Font bold= new Font("Dialog",Font.BOLD, 11);

  int[] selectedRows;
  int[] selectedColumns;

  DomeMatrixData Model;
  ButtonGroup group1;

  public static void showDialog(Component parent,DomeMatrixData model,int[] selectedcolumns,int[] selectedrows) {
   
    DeleteDialog editor = new DeleteDialog(model,selectedrows,selectedcolumns);
   
    JDialog d = DialogFactory.createDialog(parent,"DeleteElement",editor,true,false);
   
    d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
 			     
    d.show();
   
    
     
  }     
 

  public DeleteDialog(DomeMatrixData Model,int[] selectedRows,int[] selectedColumns){
      super();
      this.Model=Model;
      this.selectedRows=selectedRows;
      this.selectedColumns=selectedColumns;
     
     
      // create components
      OkButton=Templates.makeButton("OK",font11,this);
      cancleButton=Templates.makeButton("Cancel",font11,this);
     
      rowRadioButton=Templates.makeRadioButton("delete selected rows",true);
      columnRadioButton=Templates.makeRadioButton("delete selected column",false);
    
    
      // Group the radio buttons.
      group1 = new ButtonGroup();
      group1.add(rowRadioButton);
      group1.add(columnRadioButton);
      
      contentPanel=new JPanel();
   
      
      JComponent[] comps = { rowRadioButton,
			     columnRadioButton,
			     OkButton,
			     cancleButton,
			     };
			     
      // do layout
      GridBagConstraints[] gbcs = {        
        new GridBagConstraints(0,0,1,1,1.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(0,5,0,5),0,0),
        new GridBagConstraints(0,1,1,1,1.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(0,5,0,5),0,0),
        new GridBagConstraints(0,2,1,1,1.0,1.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.NONE,new Insets(5,5,5,0),0,0),
	new GridBagConstraints(1,2,1,1,0.0,1.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.NONE,new Insets(5,5,5,5),0,0),
        };
       
      GridBagLayout gridbag = new GridBagLayout();
      contentPanel.setLayout(gridbag);
      for (int i=0; i<gbcs.length; ++i){
	  gridbag.setConstraints(comps[i],gbcs[i]);
	  contentPanel.add(comps[i]);
      }
     
      //Dimension d = new Dimension(180, 110);
      this.setLayout(new BorderLayout());
      this.add(contentPanel);
      //this.setPreferredSize(d);
  }
 
  
 public void actionPerformed(ActionEvent e){
  if(e.getSource()==OkButton){
     if(rowRadioButton.isSelected()){
       Model.removeRowItems(selectedRows);
     }
     else if(columnRadioButton.isSelected()){
       Model.removeColumnItems(selectedColumns);
     }
     this.dispose();
       
   } 
  else if(e.getSource()==cancleButton){
    this.dispose();
        
  }
    
 }

  
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

    private void debug(String msg){
	boolean debug=true;
	if(debug)
	    System.out.println("DeleteDialog: "+msg);
    }
}
