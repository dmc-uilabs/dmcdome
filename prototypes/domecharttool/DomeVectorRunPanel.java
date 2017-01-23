// DomeVectorRunPanel.java   
//   based on VectorPanel of 04/11/02
//   ver 0.1

import java.awt.*;
import javax.swing.*;
import mit.cadlab.dome.swing.*;

/**
 * DomeVectorRunPanel:
 *  not editable with the structure of DomeVectorData
 *  but size and value can be changed.
 *  ---extends buildPanel(can do this bcz it functions same as build but buttons related to structure changes are unabled...)
 */




public class DomeVectorRunPanel extends DomeVectorBuildPanel{

      
   public static DomeVectorData showDialog(Component parent,DomeVectorData data) {
   
	DomeVectorRunPanel editor = new DomeVectorRunPanel(data);
   
	JDialog d = DialogFactory.createDialog(parent,"Dome Vector Run Panel",editor,true,true);
   
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	d.pack();
 			     
	d.show();

	return editor.answer;  //return it
    }    

   
    /*
     * Constructors
     */
 
    public DomeVectorRunPanel(){
	 super();
	 convertToNotEditable();
     }

    public DomeVectorRunPanel(DomeVectorData v){
	super(v);
	//setDataModel_GUI(v);
	convertToNotEditable();
    }
   
 

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("DomeVector Run Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new DomeVectorRunPanel(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }
  
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

 
 
}
