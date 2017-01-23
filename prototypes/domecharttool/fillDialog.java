// fillDialog.java


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;
import javax.swing.table.*;
import java.util.*;

public class fillDialog extends JPanel implements ActionListener{
  // define components here
 
  JTextField valueTextField;
 
  JButton OkButton;
  JButton cancleButton;

  
  static Double answer=null;

  public static Double showValueInput(Component parent,Number initValue) {
   
    fillDialog editor = new fillDialog(initValue);
   
    JDialog d = DialogFactory.createDialog(parent,"Fill with value:",editor,true,false);
   
    d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
 			     
    d.show();
   
    return answer;
     
  }     
  public fillDialog(Number initValue){
      super();

      Font font11=new Font("Dialog", Font.PLAIN, 11);
      Font bold= new Font("Dialog",Font.BOLD, 11);
      // create components
      OkButton=Templates.makeButton("OK",font11,this);
      cancleButton=Templates.makeButton("Cancel",font11,this);
      JLabel textLabel=Templates.makeLabel("Fill with value:",bold);
      valueTextField=Templates.makeTextField(initValue.toString());
      valueTextField.addKeyListener(new KeyAdapter(){
	public void keyPressed(KeyEvent e) {
	  if(e.getKeyCode()==KeyEvent.VK_ENTER){
	    Double value;
	    try{
      
	      value=new Double(Double.parseDouble(valueTextField.getText()));
     
	    }catch(Exception ee){
	      return;//just return
	    }
	    answer=value;
	    dispose();
	  }
	}
	});
  
    
     
      JComponent[] comps = {OkButton,cancleButton,textLabel,valueTextField};
			     
      // do layout
      GridBagConstraints[] gbcs = {        
        new GridBagConstraints(0,1,1,1,0.0,0.0,java.awt.GridBagConstraints.SOUTHEAST,java.awt.GridBagConstraints.NONE,new Insets(0,5,0,5),0,0),
        new GridBagConstraints(1,1,1,1,0.0,0.0,java.awt.GridBagConstraints.SOUTHEAST,java.awt.GridBagConstraints.NONE,new Insets(5,5,0,5),0,0),
        new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(0,5,5,5),0,0),
	new GridBagConstraints(1,0,1,1,0.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0),
       };
      
       
      GridBagLayout gridbag = new GridBagLayout();
      this.setLayout(gridbag);
      for (int i=0; i<gbcs.length; ++i){
	gridbag.setConstraints(comps[i],gbcs[i]);
	this.add(comps[i]);
      }
     
      Dimension d = new Dimension(180, 70);
       
      this.setPreferredSize(d);
  }
 
  
  
 
 public void actionPerformed(ActionEvent e){
  
   if(e.getSource()==OkButton){
     Double value;
     try{
      
       value=new Double(Double.parseDouble(valueTextField.getText()));
     
     }catch(Exception ee){
       return;//just return
     }
    this.answer=value;
    this.dispose();
        }
        
    else if(e.getSource()==cancleButton){
         this.dispose();
        
        }
    
    }

 
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 
  

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Vector build Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new fillDialog(new Double(1.0)), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }
}
