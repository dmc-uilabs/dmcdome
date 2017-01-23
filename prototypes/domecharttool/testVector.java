// testVector.java
//package mit.cadlab.dome.gui.components;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.event.*;




import mit.cadlab.dome.swing.*;


public class testVector extends JPanel implements ListSelectionListener {
    // define components here


    DefaultListModel listModel1,listModel2;
    JList DataSetTemplateList;
    JList DataSetList;
    DomeVectorBasePanel vis;

    public testVector(){
	super();
	listModel1=new DefaultListModel();
	listModel2=new DefaultListModel();
	vis=new DomeVectorBasePanel();
	vis.setReadOnly();
	clearVis();
      
	//Random data
	DomeVectorData t1=new DomeVectorData();
	t1.setInitialValue(new Double(0.8));
	t1.setSize(4);
	t1.setRowVector(true);

	DomeVectorData t2=new DomeVectorData();
	t2.setInitialValue(new Integer(14));
	t2.setSize(7);
      
	listModel1.addElement(t1);
	listModel1.addElement(t2);

	// create components
	//defineButton=Templates.makeButton("define...",font11,this);
	//runButton=Templates.makeButton("run...",font11,this);
     
	DataSetTemplateList=new JList(listModel1);
	DataSetTemplateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	DataSetTemplateList.addListSelectionListener(this);


	DataSetList=new JList(listModel2);
	DataSetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
	JScrollPane scrollPane1 = new JScrollPane(DataSetTemplateList);
	JScrollPane scrollPane2 = new JScrollPane(DataSetList);
	JScrollPane scrollPane3=  new JScrollPane(vis);
     
	GridLayout gridbag = new GridLayout(3,2,0,0);	     
	setLayout(gridbag);      
	add(scrollPane1);
	add(scrollPane2);
	add(scrollPane3);

     
     
	Dimension d = new Dimension(450, 550);
       
	this.setPreferredSize(d);
    }
 
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("DataSet");
        menu.setMnemonic(KeyEvent.VK_D);
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    //DataSetTemplateList.clearSelection() ;
		    BuildDataSet(-2);
		}
	    });
        menu.add(menuItem);
        menuItem = new JMenuItem("Build Selected DomeVector");
        menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    BuildDataSet(DataSetTemplateList.getSelectedIndex());
		}
	    });
        menu.add(menuItem);
	menuItem = new JMenuItem("Run Selected DomeVector");
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    RunDataSet(DataSetTemplateList.getSelectedIndex());
		}
	    });
        menu.add(menuItem);
        menuBar.add(menu);
	
	menu = new JMenu("Chart");
        menu.setMnemonic(KeyEvent.VK_C);
        menuItem = new JMenuItem("Point");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
               
		}
	    });
        menu.add(menuItem);
	menuItem = new JMenuItem("Line");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
               
		}
	    });
        menu.add(menuItem);
	menuItem = new JMenuItem("Area");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
               
		}
	    });
        menu.add(menuItem);
        menuBar.add(menu);

        return menuBar;
    }
  
    public static void main(String[] args){
	JFrame f = Templates.makeTestFrame("Test Dome Vector");
 
	f.getContentPane().setLayout(new GridLayout(1,1,0,0));
	testVector t=new testVector();
	f.getContentPane().add(t, BorderLayout.CENTER);
	f.setJMenuBar(t.createMenuBar());
	f.pack();
	f.setVisible(true);
    } 
 
    //Listener method for list selection changes.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
	    if (DataSetTemplateList.getSelectedIndex() == -1) {
		clearVis();
            } 
	    else {
		//Single selection: 
		refreshVis();
            }
	  
	  
	}
    }
    
    protected void clearVis(){
	vis.setVisible(false);

    }
    protected void refreshVis(){
	clearVis();
	vis.setDataObject((DomeVectorData)listModel1.get(DataSetTemplateList.getSelectedIndex()));
	vis.setVisible(true);
    }
    public void BuildDataSet(int index){
	if(index==-2) //new one
	    {
		DomeVectorData answer=DomeVectorBuildPanel.showDialog(this.getParent(),null);
		if(answer==null)//change was discarded ..
		    {
			return;
		    }
		else   listModel1.addElement(new DomeVectorData(answer));
	    }
	else if(index==-1) //new one
	    {
	        DomeVectorData answer=DomeVectorBuildPanel.showDialog(this.getParent(),null);
		if(answer==null)//change was discarded ..
		    {
			return;
		    }
		else   listModel1.addElement(new DomeVectorData(answer));
	
	    }
	else {  //modify one
	    DomeVectorData answer=DomeVectorBuildPanel.showDialog(this.getParent(),(DomeVectorData)listModel1.get(index));
	    if(answer==null)//change was discarded ..
		{
		    return;
		}
	    else   {
		listModel1.set(index,new DomeVectorData(answer));
		refreshVis();
	    }
	}
     
    }

    public void RunDataSet(int index){
	if(index==-1) //new one
	    {
		return;
	
	    }
	else {  //modify one
	    DomeVectorData answer=DomeVectorRunPanel.showDialog(this.getParent(),(DomeVectorData)listModel1.get(index));
	    if(answer==null)//change was discarded ..
		{
		    return;
		}
	    else   listModel2.addElement(new DomeVectorData(answer));

	}
    }
   
 
}

 
 

