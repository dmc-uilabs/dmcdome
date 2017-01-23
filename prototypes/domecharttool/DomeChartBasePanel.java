// DomeChartBasePanel.java   
//   5/21/02
//   ver 0.1: loading dome vector and show as xy chart

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;

import javax.swing.table.AbstractTableModel;

import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;

import com.jrefinery.chart.*;

/**
 * DomeChartBasePanel:
 *   
 *
 */




public class DomeChartBasePanel extends JPanel{
   
    JScrollPane chartScrollPane;
    JScrollPane tableScrollPane;
    JScrollPane listScrollPane;
    JList DataSetList;
    ChartDataTableModel tm;
  DefaultListModel lm;
  JTable ChartTable;
  JFreeChartPanel ChartPanel;
 
    
    
    /*
     * Constructors
     */

    public DomeChartBasePanel(DomeVectorData[] data){
	this(800,500,data);
    }
    
    public DomeChartBasePanel(){
	this(800,500);
    }
    
    public DomeChartBasePanel(int PanelWidth,int PanelHeight){
        this(PanelWidth,PanelHeight,null);
      
    }

   
   

    public DomeChartBasePanel(int PanelWidth,int PanelHeight,DomeVectorData[] data){
      setPreferredSize(new Dimension(PanelWidth,PanelHeight));
	
    
          
      layoutComponents(createComponents());
      if(data!=null)   setDataModel_GUI(data); //load dataVect into the GUI
      
     
      
      
    }

    protected JComponent[] createComponents(){
        JFreeChart chart=ChartFactory.createXYChart("XY chart","x","y",null,true);
	ChartPanel=new JFreeChartPanel(chart);

	chartScrollPane=new JScrollPane(ChartPanel); 

	tm=new ChartDataTableModel();
	
	ChartTable=new JTable(tm);
	tm.setTable(ChartTable);
	ChartTable.setRowSelectionAllowed(tm.isConvert());
	ChartTable.setColumnSelectionAllowed(tm.isConvert());
	ChartTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	ChartTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);    
	tableScrollPane=new JScrollPane(ChartTable); 
	
	
	lm=new DefaultListModel();
	loadData();
	DataSetList=new JList(lm);
	DataSetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
	listScrollPane=new JScrollPane(DataSetList); 

	JButton addButton=Templates.makeButton("add to table");
	addButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		    //if selected vector can be added into the table then okay
		  if(lm.get(DataSetList.getSelectedIndex()) instanceof DomeVectorData)
		  tm.add((DomeVectorData)lm.get(DataSetList.getSelectedIndex()));
		    //else beep
		}
	    });
	JButton deleteButton=Templates.makeButton("delete from table");
	deleteButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		    //delete the selected domevector in the table
		  tm.remove(ChartTable.getSelectedRow(),ChartTable.getSelectedColumn());
		    //else beep
		}
	    });

	JButton drawButton=Templates.makeButton("draw");
	drawButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		    //get data from table and draw chart
		  if(tm.getDataModel()==null) //nothing in the table
		    return;
		  
		 
		  else{
		    Object[] d=tm.getDataModel();
		    if(d[0] instanceof DomeVectorData){
		      DomeVectorData[] dv=new DomeVectorData[d.length];
		      for(int i=0;i<d.length;i++)
			{
			  dv[i]=(DomeVectorData)d[i];
			}

		       JFreeChart chart=ChartFactory.createAreaXYChart("XY chart","x","y",new DomeXYDataset(dv),true);
		      // JFreeChart chart=ChartFactory.createXYChart("XY chart","x","y",new DomeXYDataset(dv),true);
		       //chartScrollPane.setViewportView(new JFreeChartPanel(chart));
		       ChartPanel.setChart(chart);
		     }
		  }
		}
	    });

	

	return new JComponent[] {chartScrollPane,listScrollPane,tableScrollPane,addButton,deleteButton,drawButton};
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
  
    
 
    protected void setDataModel_GUI(DomeVectorData[] d){
	
    }


	   

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Chart/DomeVector Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new DomeChartBasePanel(), BorderLayout.CENTER);
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

  //this function is to generate some test data
  protected void loadData(){
    Vector v=new Vector();
    int size=8;
    double step=0.5;
    for(double i=0;i<size;i+=step)
      v.add(new Double(i));
    DomeVectorData d1=new DomeVectorData(v,"cm",true,true,new Double(10.0));
    lm.addElement(d1);
    DomeVectorData d4=new DomeVectorData(v,"cm",false,true,new Double(10.0));
    lm.addElement(d4);
    
    v.clear();
    for(double i=0;i<size;i+=step)
      v.add(new Double(java.lang.Math.cos((double)i)));
    DomeVectorData d2=new DomeVectorData(v,"cm",true,true,new Double(10.0));
    lm.addElement(d2);

    v.clear();
    for(double i=0;i<size;i+=step)
      v.add(new Double(java.lang.Math.sin((double)i)));
    DomeVectorData d3=new DomeVectorData(v,"cm",false,true,new Double(10.0));
    lm.addElement(d3);

    v=null;
  }
}



class ChartDataTableModel extends AbstractTableModel{
    
    protected Vector data=new Vector();
    protected boolean isConvert=false;//decide whether to show at the right way (input determin total column count ) or row/column flipped(input determin total row count)
    //for domevector case: isRowVector means right way; isColumnVector means row/column flipped
    
  protected JTable table;
    
  
  
    public ChartDataTableModel(){
	

	
    }

  public void setTable(JTable t){
    table=t;
  }

   

  public boolean isConvert(){
    return isConvert;
  }

  public void setConvert(boolean isC){
    this.isConvert=isC;
   
    table.setRowSelectionAllowed(isConvert);
    table.setColumnSelectionAllowed(isConvert);
    
    fireTableStructureChanged();
 
 }
  
  public int getRowCount() {
	if(data.size()==0) return 0;
	if(isConvert) //element size determined row count
	  //    {
	  //if(data.get(0) instanceof DomeVectorData)
		  return ((DomeVectorData)data.get(0)).getSize();
	//else if(data.get(0) instanceof DomeMatrixData)
	//  return -1;//needs change

	//  }
	else
	  return data.size();
     }

    public int getColumnCount() {
	if(data.size()==0) return 0;
	if(!isConvert) //element size determined column count
	  // {
	  //if(data.get(0) instanceof DomeVectorData)
		  return ((DomeVectorData)data.get(0)).getSize();
	//else if(data.get(0) instanceof DomeMatrixData)
	//  return -1;//needs change

	//  }
	else
	  return data.size();
	
       
    }

    public Object getValueAt(int row, int column) {
	if(data.size()==0) return null;
	if(!isConvert) //element size determined column count
	  // {
	  //if(data.get(0) instanceof DomeVectorData)
		  return ((DomeVectorData)data.get(row)).getItem(column);
	//else if(data.get(0) instanceof DomeMatrixData)
	//  return null;  //needs change

	//  }
	else{
	  // if(data.get(0) instanceof DomeVectorData)
		  return ((DomeVectorData)data.get(column)).getItem(row);
		//else if(data.get(0) instanceof DomeMatrixData)
		//  return ...;
	}
    }

    public void setValueAt(Object value, int row, int column) {
    }

    public void add(DomeVectorData d)
    {
	//if empty them this is the first element, decide the whole thing
	if(data.size()==0)
	    {
		this.isConvert=!d.isRowVector();
		data.add(d);
		table.setRowSelectionAllowed(isConvert);
		table.setColumnSelectionAllowed(isConvert);

		fireTableStructureChanged();
		
	     }
        else{
	    //decide if this data match the pattern
	  if(data.get(0) instanceof DomeVectorData){
	    if(d.isRowVector()==!isConvert)
	    {  
	      data.add(d);
	      fireTableStructureChanged();
	    }
	    //   else
	    //Message.Show("DomeChartGUI---Error: DomeVectorData doesn't match the pattern expected");
	  }
	  // else
	    //not add into 
	    //Message.Show("DomeChartGUI---Error: DomeMatrixData expected");

	}
    }
  
  public void remove(int row, int column){
    if(data.size()==0) return;
    if(data.get(0) instanceof DomeVectorData){
    //for domevector us only
    if(isConvert)
      data.remove(column);
    else
      data.remove(row);
    
    fireTableStructureChanged();
    }
  } 
  
  public Object[] getDataModel(){
    int count =data.size();
    if(count>0&&data.get(0) instanceof DomeVectorData)
    {
      DomeVectorData[] result = new DomeVectorData[count];
      for(int i=0;i<count;i++){
	result[i]=(DomeVectorData)data.get(i);
      }

      return result;
    }
    return null;
  }

    public boolean isCellEditable(int rowIndex,int columnIndex)
    {
	return false;
    }
    
    public String getColumnName(int column) {
	
	return String.valueOf(column);
    }

    }
