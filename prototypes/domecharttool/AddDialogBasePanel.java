// AddDialog.java
//package mit.cadlab.dome.gui.components;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;
import javax.swing.table.*;
import java.util.*;

public class AddDialogBasePanel extends JPanel {
  // define components here
  JTextField sizeTextField;
  JTextField afterTextField;
  JRadioButton rowRadioButton;
  JRadioButton columnRadioButton;
  JRadioButton startRadioButton;
  JRadioButton endRadioButton;
  JRadioButton beforeRadioButton;

  JButton OkButton;
  JButton cancleButton;


 
  public AddDialogBasePanel()
  {
    layoutComponents(createComponents());
    setPreferredSize(new Dimension(150,220));
  
  }
  
  private JComponent[] createComponents(){
    Font font11=new Font("Dialog", Font.PLAIN, 11);
    Font bold= new Font("Dialog",Font.BOLD, 11);

    OkButton=Templates.makeButton("OK",font11);
    cancleButton=Templates.makeButton("Cancel",font11);
    JLabel addLabel=Templates.makeLabel("add",bold);
    JLabel afterLabel=Templates.makeLabel("value:",bold);
    
    sizeTextField=Templates.makeTextField("1");
    afterTextField=Templates.makeTextField("",5);
   
	
    rowRadioButton=Templates.makeRadioButton("row(s)");
    columnRadioButton=Templates.makeRadioButton("column(s)");
    // Group the radio buttons.
    ButtonGroup group1 = new ButtonGroup();
    group1.add(rowRadioButton);
    group1.add(columnRadioButton);
        
    startRadioButton=Templates.makeRadioButton("at start");
    endRadioButton=Templates.makeRadioButton("at end",true);
    beforeRadioButton=Templates.makeRadioButton("before selection");  
    // Group the radio buttons.
    ButtonGroup group2 = new ButtonGroup();
    group2.add(startRadioButton);
    group2.add(endRadioButton);
    group2.add(beforeRadioButton);
	

    JPanel rowColButtonGroupPanel= new JPanel();
    GridLayout grid = new GridLayout(2,0,0,0);
    rowColButtonGroupPanel.setLayout(grid);
    rowRadioButton.setFont(font11);
    rowColButtonGroupPanel.add(rowRadioButton);
    columnRadioButton.setFont(font11);
    rowColButtonGroupPanel.add(columnRadioButton);
    
    JPanel addPanel= new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    addPanel.setLayout(gridbag);
    addPanel.add(addLabel,new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.HORIZONTAL,new Insets(0,0,0,5),0,0));
    addPanel.add(sizeTextField,new GridBagConstraints(1,0,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(3,3,0,0),20,0));
    addPanel.add(rowColButtonGroupPanel,new GridBagConstraints(2,0,1,1,0.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.BOTH,new Insets(0,3,0,0),0,0));

    
   
    JPanel OkPanel=new JPanel();
    gridbag = new GridBagLayout();
    OkPanel.setLayout(gridbag);
    OkPanel.add(OkButton,new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.NONE,new Insets(0,0,0,5),0,0));
    OkPanel.add(cancleButton,new GridBagConstraints(1,0,1,1,0.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));

    JPanel locationButtonGroupPanel= new JPanel();
    gridbag = new GridBagLayout();
    locationButtonGroupPanel.setLayout(gridbag);
    startRadioButton.setFont(font11);
    locationButtonGroupPanel.add(startRadioButton,new GridBagConstraints(0,0,1,1,1.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
    endRadioButton.setFont(font11);
    locationButtonGroupPanel.add(endRadioButton,new GridBagConstraints(0,1,1,1,1.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
    beforeRadioButton.setFont(font11);
    locationButtonGroupPanel.add(beforeRadioButton,new GridBagConstraints(0,2,1,1,1.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
 
	
    JPanel valuePanel=new JPanel();
    gridbag = new GridBagLayout();
    valuePanel.setLayout(gridbag);
    afterLabel.setFont(font11);
    valuePanel.add(afterLabel,new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(0,0,0,5),0,0));
    afterTextField.setFont(font11);
    valuePanel.add(afterTextField,new GridBagConstraints(1,0,1,1,1.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));

  
    
  return new JComponent[] {addPanel,locationButtonGroupPanel,valuePanel,OkPanel};
  }
 
  
   protected void layoutComponents(JComponent[] comps){
     // do layout
     GridBagConstraints[] gbcs = {        
	  new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(5,0,5,0),0,0),
	  new GridBagConstraints(0,1,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(5,0,5,0),0,0),
	  new GridBagConstraints(0,2,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(5,0,5,0),0,0),
	  new GridBagConstraints(0,3,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(5,0,5,0),0,0),
	};
      
       
	GridBagLayout gridbag = new GridBagLayout();
	this.setLayout(gridbag);
	for (int i=0; i<gbcs.length; ++i){
	  gridbag.setConstraints(comps[i],gbcs[i]);
	  this.add(comps[i]);
	}
     
	
  }

 
  
  
 public void setRowColumnMode(boolean isRow,boolean isColumn){
    rowRadioButton.setSelected(isRow);
    rowRadioButton.setEnabled(isRow);
    columnRadioButton.setSelected(isColumn);
    columnRadioButton.setEnabled(isColumn);
    }
 

public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Add Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new AddDialogBasePanel(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }  

 protected int min(int[] indices){
    Arrays.sort(indices);
    if(indices.length>0)
	return indices[0];
    else //not selected
	return -1;
  }
  
 protected void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

 private void debug(String msg){
	boolean debug=true;
	if(debug)
	    System.out.println("AddDialogBasePanel: "+msg);
 }

}
