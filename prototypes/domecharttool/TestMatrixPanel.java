// TestMatrixPanel.java   
//   5/30/02 ver 0.1
//   based on domechartbasepanel layout
//   

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;




import mit.cadlab.dome.swing.*;

import com.jrefinery.chart.*;

/**
 * TestMatrixPanel:
 *  1)List of DomeMatrixData
 *  2)+/-/multiply/append function test
 *  3) using DomeMatrixBuildPanel as visulization
 *  4) using JFreechartPanel to show xy chart
 *   
 *
 */




public class TestMatrixPanel extends JPanel{
   
  JScrollPane chartScrollPane;
  JScrollPane tableScrollPane;
  JScrollPane listScrollPane;

  JList DataSetList;
  DefaultListModel lm;
 
  DomeMatrixBuildPanel tablePanel;
  JFreeChartPanel ChartPanel;
 
  //for functions
  final String ADD="+";
  final String MINUS="-";
  final String MULTIPLY="*";
  final String APPENDH="append horizontally";
  final String APPENDV="append vertically";
  final String EQUAL="=";
 
  Vector opers=new Vector();;
    
    /*
     * Constructors
     */

  
    public TestMatrixPanel(){
	this(800,500);
    }
    
   
    public TestMatrixPanel(int PanelWidth,int PanelHeight){
      setPreferredSize(new Dimension(PanelWidth,PanelHeight));
	      
      layoutComponents(createComponents());
      
    }

    protected JComponent[] createComponents(){
        JFreeChart chart=ChartFactory.createXYChart("XY chart","x","y",null,true);
	ChartPanel=new JFreeChartPanel(chart);

	chartScrollPane=new JScrollPane(ChartPanel); 

	tablePanel=new DomeMatrixBuildPanel();
	tableScrollPane=new JScrollPane(tablePanel); 
	
	
	lm=new DefaultListModel();
	loadData();
	DataSetList=new JList(lm);
	DataSetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	DataSetList.addListSelectionListener(new ListSelectionListener() {
	    public void valueChanged(ListSelectionEvent e) {
	      //Ignore extra messages.
	      if (e.getValueIsAdjusting()) return;
	      if(DataSetList.getSelectionModel().isSelectionEmpty())
		    return;
	      int selectedRow = DataSetList.getMinSelectionIndex();
	      tablePanel.setDataObject((DomeMatrixData)(lm.getElementAt(selectedRow)));
	     
	    }
	  });
	listScrollPane=new JScrollPane(DataSetList); 
	//change should restart here
	//do button for +,-, *,append horizontal, append verticall
	JButton addButton=Templates.makeButton("+");
	addButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		  if(tablePanel.getDataObject()==null) 
		    //should beep 
		    return;
		  DomeMatrixData m1=tablePanel.getDataObject();
		  if(m1.getRowCount()==0||m1.getColumnCount()==0) //means it's the empty matrix after u click "clear"
		    //should beep
		    return;
		  
		  //now take the one showing in table to be the first arg for minus function DomeMatrixData.minus(m1,m2);
		  opers.add(m1);
		  opers.add(ADD);
      
		}
	    });
	JButton minusButton=Templates.makeButton("-");
	minusButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		  if(tablePanel.getDataObject()==null) 
		    //should beep 
		    return;
		  DomeMatrixData m1=tablePanel.getDataObject();
		  if(m1.getRowCount()==0||m1.getColumnCount()==0) //means it's the empty matrix after u click "clear"
		    //should beep
		    return;
		  
		  //now take the one showing in table to be the first arg for minus function DomeMatrixData.minus(m1,m2);
		  opers.add(m1);
		  opers.add(MINUS);
      
		}
	    });
	JButton multiButton=Templates.makeButton("*");
	multiButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		  if(tablePanel.getDataObject()==null) 
		    //should beep 
		    return;
		  DomeMatrixData m1=tablePanel.getDataObject();
		  if(m1.getRowCount()==0||m1.getColumnCount()==0) //means it's the empty matrix after u click "clear"
		    //should beep
		    return;
		  
		  //now take the one showing in table to be the first arg for minus function DomeMatrixData.minus(m1,m2);
		  opers.add(m1);
		  opers.add(MULTIPLY);
      
		}
	    });
	JButton appendHButton=Templates.makeButton("Append H");
	appendHButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		   
		  if(tablePanel.getDataObject()==null) 
		    //should beep 
		    return;
		  DomeMatrixData m1=tablePanel.getDataObject();
		  if(m1.getRowCount()==0||m1.getColumnCount()==0) //means it's the empty matrix after u click "clear"
		    //should beep
		    return;
		  
		  //now take the one showing in table to be the first arg for minus function DomeMatrixData.minus(m1,m2);
		  opers.add(m1);
		  opers.add(APPENDH);
      
		}
	    });
	JButton appendVButton=Templates.makeButton("Append V");
	appendVButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		  if(tablePanel.getDataObject()==null) 
		    //should beep 
		    return;
		  DomeMatrixData m1=tablePanel.getDataObject();
		  if(m1.getRowCount()==0||m1.getColumnCount()==0) //means it's the empty matrix after u click "clear"
		    //should beep
		    return;
		  
		  //now take the one showing in table to be the first arg for minus function DomeMatrixData.minus(m1,m2);
		  opers.add(m1);
		  opers.add(APPENDV);
		}
	  });
	JButton resultButton=Templates.makeButton("=");
	resultButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		   if(tablePanel.getDataObject()==null) 
		    //should beep 
		    return;
		  DomeMatrixData m1=tablePanel.getDataObject();
		  if(m1.getRowCount()==0||m1.getColumnCount()==0) //means it's the empty matrix after u click "clear"
		    //should beep
		    return;
		  
		  //now take the one showing in table to be the first arg for minus function DomeMatrixData.minus(m1,m2);
		  opers.add(m1);
		  opers.add(EQUAL);
		 
		  //clear list selection
		  DataSetList.clearSelection();
		  //calculate
		  DomeMatrixData result=Calculate(opers);
		  if(result!=null)
		    tablePanel.setDataObject(result); 
		  else
		    tablePanel.setDataObject(new DomeMatrixData());
		    
		}
	  });
	JButton clearButton=Templates.makeButton("clear");
	clearButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		  //clear list selection
		  DataSetList.clearSelection();
		  //empty table
		  tablePanel.setDataObject(new DomeMatrixData());
		  //empty vector
		  opers.clear();
		   
		}
	  });
	
	//still need draw
	JButton drawButton=Templates.makeButton("draw");
	drawButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		 
		    //get data from table and draw chart
		  if(tablePanel.getDataObject()==null) //nothing in the table
		    return;
		  
		 
		  else{
		   
		    DomeMatrixData dm=tablePanel.getDataObject();
		    if(dm.getRowCount()==0||dm.getColumnCount()==0) return;
		    else
		     {
		       // JFreeChart chart=ChartFactory.createAreaXYChart("XY chart","x","y",new DomeXYDataset(dm),true);
		       JFreeChart chart=ChartFactory.createXYChart("XY chart","x","y",new DomeXYDataset(dm),true);
		       //chartScrollPane.setViewportView(new JFreeChartPanel(chart));
		       ChartPanel.setChart(chart);
		     }
		  }
		}
	    });

	JPanel functionPanel=new JPanel();
	functionPanel.setLayout(new GridLayout(6,0,0,0));
	functionPanel.add(addButton);
	functionPanel.add(minusButton);
	functionPanel.add(multiButton);
	functionPanel.add(appendHButton);
	functionPanel.add(appendVButton);
	functionPanel.add(resultButton);

  
	return new JComponent[] {chartScrollPane,listScrollPane,tableScrollPane,functionPanel,clearButton,drawButton};
    }

    protected void layoutComponents(JComponent[] comps){
      
     
     // do layout
      GridBagConstraints[] gbcs = {
        new GridBagConstraints(0,0,5,2,0.0,1.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.BOTH,new Insets(5,5,0,0),0,0),
        new GridBagConstraints(0,2,2,2,1.0,1.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.BOTH,new Insets(5,5,0,0),0,0),
        new GridBagConstraints(3,2,2,2,1.0,1.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.BOTH,new Insets(5,5,0,0),0,0),
        new GridBagConstraints(2,2,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(5,5,0,5),0,0),
        new GridBagConstraints(4,4,1,1,1.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.NONE,new Insets(5,5,0,5),0,0),
	new GridBagConstraints(5,4,1,1,0.0,0.0,java.awt.GridBagConstraints.SOUTHEAST,java.awt.GridBagConstraints.NONE,new Insets(5,5,0,5),0,0),
       	   };
      
      
      GridBagLayout gridbag = new GridBagLayout();
      setLayout(gridbag);
      for (int i=0; i<gbcs.length; ++i){
	  gridbag.setConstraints(comps[i],gbcs[i]);
	  this.add(comps[i]);
      }
      
    
  }
  
    

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Chart/DomeVector Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new TestMatrixPanel(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }
  
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

  private void debug(String msg){
     boolean debug=true;
     if(debug)
	 System.out.println("DomeChartBasePanel: "+msg);
    }


  protected DomeMatrixData Calculate(Vector v){
    //assume info inside v are good
    //then it should have similar sequence like (matrix,oper,matrix,oper,...)
    //and ends with a "="
    //so first check if info vector is like that, or just return null

    //if okay, do calculation
    
    //return null if any step gos wrong
    
    debug(v.toString());
    
    int i=0;
    DomeMatrixData tempresult=null;

    DomeMatrixData m1= (DomeMatrixData)v.get(0);
   
    while(i<v.size()-1) //bcz last item is '='
      {
       
	String op=(String)v.get(i+1);
	DomeMatrixData m2=(DomeMatrixData)v.get(i+2);
	if(op.equals(ADD))
	  {
	    if(DomeMatrixData.plus(m1,m2)==null) return null;
	    else tempresult=DomeMatrixData.plus(m1,m2);
	  }
	else if(op.equals(MINUS))
	  {
	    if(DomeMatrixData.minus(m1,m2)==null) return null;
	    else tempresult=DomeMatrixData.minus(m1,m2);
	  }
	  
	else if(op.equals(MULTIPLY))
	  {
	    if(DomeMatrixData.multiply(m1,m2)==null) return null;
	    else tempresult=DomeMatrixData.multiply(m1,m2);
	  }
	else if(op.equals(APPENDH))
	  {
	    if(DomeMatrixData.appendHorizontally(m1,m2)==null) return null;
	    else tempresult=DomeMatrixData.appendHorizontally(m1,m2);
	  }
	else if(op.equals(APPENDV))
	  {
	    if(DomeMatrixData.appendVertically(m1,m2)==null) return null;
	    else tempresult=DomeMatrixData.appendVertically(m1,m2);
	  }
	  
	i=i+3;
	m1=tempresult;

      }
    
    return m1;
  }


  //this function is to generate some test data
  protected void loadData(){
    DomeMatrixData d1=new DomeMatrixData(3,3,false,new Double(0));
    DomeMatrixData d2=new DomeMatrixData(3,3,false,new Double(0));
    DomeMatrixData d3=new DomeMatrixData(5,3,false,new Integer(0));
    DomeMatrixData d4=new DomeMatrixData(5,3,false,new Integer(0));

    for(int i=0;i<3;i++)
      for(int j=0;j<3;j++)
	{
	  d1.setItem(i,j,new Double(i-0.1*j));
	  d2.setItem(i,j,new Double(i+0.5*j));
	}
    for(int i=0;i<5;i++)
      for(int j=0;j<3;j++)
	{
	  d3.setItem(i,j,new Integer(i+1*j));
	  d4.setItem(i,j,new Integer(i-5*j));
	}
    
    lm.addElement(d1);
    lm.addElement(d2);
    lm.addElement(d3);
    lm.addElement(d4);
  }
  
  
}


