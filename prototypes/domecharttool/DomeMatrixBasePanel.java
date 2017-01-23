// DomeMatrixBasePanel
//  ver 0.1 04/13/02  originally MatrixPanel.java 
//  ver 0.2 05/27/02
//      add in 1) bean support, directly operating on the data model
//             2) change into a base panel with all gui layout




import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;

import mit.cadlab.dome.gui.components.shared.*;
import mit.cadlab.dome.swing.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * DomeMatrixBasePanel: as the base panel for build and run DomeMatrix GUI
 *   as a property listener listen to the DomeVector property changes except those caught by tablemodel
 *   
 *
 */
public class DomeMatrixBasePanel extends DataObjectPanel{
  
  protected PropertyChangeListener propertyListener;
  DomeMatrixData dataMatrix=null; 
  DomeMatrixTableModel matrixTableModel;
   
  // define components here
  JTextField rowsTextInput;
  JTextField columnsTextInput;
  
  JComboBox unitComboBox;
  JComboBox flavorComboBox;
  JComboBox visualizationComboBox; 
  JCheckBox fixSizeBox;
  JButton constraintsButton;
  JButton addButton;
  JButton deleteButton;
  JButton fillButton;

  JLabel rowsLabel;
  JLabel columnsLabel;

   
  JScrollPane matrixScrollPane;

  MyTable matrixTable;
  JList rowHeader;
    
 
 
  String[] unitModel= {"no units","options ..."};  
  String[] flavourModel= {"real","integer"}; 
  String[] visualizationModel= { "table view"};                       
   
    
    
   
   
  /*
   * Constructors
   */

  public DomeMatrixBasePanel(DomeMatrixData dataVect){
    this(500,300,dataVect);
  }
    
  public DomeMatrixBasePanel(){
    this(500,300);
  }
    
  public DomeMatrixBasePanel(int PanelWidth,int PanelHeight){
    this(PanelWidth,PanelHeight,null);
      
  }

   
   

  public DomeMatrixBasePanel(int PanelWidth,int PanelHeight,DomeMatrixData dataVect){
    setPreferredSize(new Dimension(PanelWidth,PanelHeight));
	
    
    propertyListener = getPropertyListener();
     
      
    layoutComponents();
    if(dataVect!=null)   setDataModel_GUI(dataVect); //load dataVector into the GUI
      
    configureComponents();  
      
      
  }

  protected PropertyChangeListener getPropertyListener() {
	return new MatrixPanelPropertyChangeListener();
  }

  //to be overwriten by subclass
  protected void configureComponents(){

  }
  

  public void setDataObject(DataObject data) {
    if (data instanceof DomeMatrixData)
      setDataModel_GUI((DomeMatrixData)data);
    else
      throw new IllegalArgumentException("DomeMatrix gui - bad parameter");
  }

  public DomeMatrixData getDataObject()
  {
    return dataMatrix;
  }
  
  protected void layoutComponents()
  {
    // create components
    constraintsButton=Templates.makeButton("contraints...");
    addButton=Templates.makeButton("add...");
     
    deleteButton=Templates.makeButton("delete...");
    fillButton=Templates.makeButton("fill...");

    unitComboBox=Templates.makeComboBox(unitModel);
    flavorComboBox=Templates.makeComboBox(flavourModel);
      
    visualizationComboBox=Templates.makeComboBox(visualizationModel);
    visualizationComboBox.setSelectedIndex(0);//for now bcz there is only one item in it..

    
    fixSizeBox=Templates.makeCheckBox("fix size");
    fixSizeBox.setHorizontalTextPosition(SwingConstants.LEFT);

    rowsTextInput=Templates.makeTextField();
    columnsTextInput=Templates.makeTextField();
     
      
    rowsLabel=Templates.makeLabel("rows");
    columnsLabel=Templates.makeLabel("columns");
      
          
    JPanel sizePanel= new JPanel();
    sizePanel.setLayout(new GridLayout(0,4,0,0));
    sizePanel.add(rowsLabel);
    sizePanel.add(rowsTextInput);
    sizePanel.add(columnsLabel);
    sizePanel.add(columnsTextInput);

    JPanel topRowFillerPanel=new JPanel();
    topRowFillerPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));

    JPanel buttonsPanel=new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    buttonsPanel.setLayout(gridbag);
    buttonsPanel.add(deleteButton, new GridBagConstraints(2,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,0,0,10),0,0));
    buttonsPanel.add(addButton,new GridBagConstraints(1,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,2,0,0),0,0));
    buttonsPanel.add(constraintsButton,new GridBagConstraints(4,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,0,0,10),0,0));
    buttonsPanel.add(fillButton,new GridBagConstraints(3,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,2,0,0),0,0));
    buttonsPanel.add(visualizationComboBox,new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,0,0,20),0,0));
   
	
    JPanel comboboxPanel=new JPanel();
    comboboxPanel.setLayout(new GridLayout(0,2,0,0));
    comboboxPanel.add(unitComboBox);
    comboboxPanel.add(flavorComboBox);
 
    matrixScrollPane=new JScrollPane();
    matrixScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    matrixScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,new Corner());
   
    JComponent[] comps = {
      sizePanel,
      fixSizeBox,
      comboboxPanel,
      topRowFillerPanel,
      matrixScrollPane,
      buttonsPanel,
		
    };
			     
    // do layout
    GridBagConstraints[] gbcs = {
      new GridBagConstraints(0,0,1,1,1.0,0.0,java.awt.GridBagConstraints.WEST,java.awt.GridBagConstraints.NONE,new Insets(5,10,0,0),0,0),
      new GridBagConstraints(1,0,1,1,1.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(5,10,0,5),0,0),
      new GridBagConstraints(2,0,1,1,1.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.HORIZONTAL,new Insets(5,10,0,5),0,0),
      new GridBagConstraints(3,0,1,1,1.0,0.0,java.awt.GridBagConstraints.NORTHEAST,java.awt.GridBagConstraints.HORIZONTAL,new Insets(5,5,0,5),0,0),
      new GridBagConstraints(0,1,4,1,1.0,1.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.BOTH,new Insets(5,5,0,5),5,0),
      new GridBagConstraints(0,3,4,1,0.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.NONE,new Insets(5,5,10,5),0,0),
    };
      
      
    gridbag = new GridBagLayout();
    setLayout(gridbag);
    for (int i=0; i<gbcs.length; ++i){
      gridbag.setConstraints(comps[i],gbcs[i]);
      this.add(comps[i]);
    }
	   
  }

  protected void setDataModel_GUI(DomeMatrixData d){
    this.dataMatrix=new DomeMatrixData(d); //not directly change the data
    debug(dataMatrix.toString());
    debug(dataMatrix.getValueType());
    debug(d.getValueType());
    dataMatrix.addPropertyChangeListener(propertyListener);

    //load the table
    matrixTableModel= new DomeMatrixTableModel(dataMatrix);
	
    matrixTable = new MyTable(matrixTableModel);
     
    matrixTable.setRowSelectionAllowed(true);
    matrixTable.getColumnModel().setColumnSelectionAllowed(true);
    matrixTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
    
    matrixTable.setPreferredScrollableViewportSize(new Dimension(453, 190));

    matrixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    MyListModel listModel = new MyListModel(matrixTableModel);
        
    // Create single component to add to scrollpane
    rowHeader = new JList(listModel);
    rowHeader.setFixedCellWidth(50);
    rowHeader.setFixedCellHeight(matrixTable.getRowHeight());
    rowHeader.setCellRenderer(new RowHeaderRenderer(matrixTable));
      
    matrixScrollPane.setViewportView(matrixTable);
    matrixScrollPane.getViewport().setBackground(Color.white);

    rowHeader.setAutoscrolls(false);
    matrixScrollPane.setRowHeaderView(rowHeader); // Adds row-list left of the table
    matrixScrollPane.getRowHeader().setBackground(Color.black);

    //configure other components
    setSize_GUI();
	
    setFixedSize_GUI();
    setValueType_GUI();
    setUnit_GUI();
  }
  
 
  
  protected void setSize_GUI(){
    this.rowsTextInput.setText(String.valueOf(dataMatrix.getRowCount()));
    this.columnsTextInput.setText(String.valueOf(dataMatrix.getColumnCount()));
  }
    
      
  protected void setFixedSize_GUI(){
    fixSizeBox.setSelected(dataMatrix.isFixedSize());
  }
    
  protected void setValueType_GUI(){
    if(dataMatrix.getValueType().toLowerCase().equals("real"))
      flavorComboBox.setSelectedIndex(0);
    else
      flavorComboBox.setSelectedIndex(1);
      
  }

  protected void setUnit_GUI(){
    if(dataMatrix.getUnit().equals(""))
      unitComboBox.setSelectedIndex(0);
    else
      unitComboBox.setSelectedIndex(1);
  }
     
  protected void RepaintTableHeader(){
    matrixTable.repaint();
    matrixTable.getTableHeader().repaint();
    rowHeader.repaint();
    matrixScrollPane.repaint();
  }
  

  protected void convertToNotEditable(){
	
    rowsTextInput.setEnabled(!fixSizeBox.isSelected());
    columnsTextInput.setEnabled(!fixSizeBox.isSelected());
    flavorComboBox.setEnabled(false);
    addButton.setEnabled(!fixSizeBox.isSelected());
    deleteButton.setEnabled(!fixSizeBox.isSelected());
    fixSizeBox.setEnabled(false);
    rowsLabel.setEnabled(!fixSizeBox.isSelected());
    columnsLabel.setEnabled(!fixSizeBox.isSelected());
  }

 
  protected Vector convertToVector(int[] anArray) {
    if (anArray == null)
      return null;

    Vector v = new Vector(anArray.length);
    for (int i=0; i < anArray.length; i++) {
      v.addElement(new Integer(anArray[i]));
    }
    return v;
  }

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Matrix Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new DomeMatrixBasePanel(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }
  
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

  private void debug(String msg){
    boolean debug=false;
    if(debug)
      System.out.println("MatrixPanel: "+msg);
  }

  protected class MatrixPanelPropertyChangeListener implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String property = e.getPropertyName();
	    Object newValue = e.getNewValue();
	    if (property.equals(DomeMatrix.SIZE)) {
		 setSize_GUI();
	    }
	    else if (property.equals(DomeMatrix.ITEMS)) {
		RepaintTableHeader();//  table changes are taken care by tablemodel
	    }
	    else if (property.equals(DomeMatrix.FIXEDSIZE)) {
		 setFixedSize_GUI();
	    }
	    else if (property.equals(DomeMatrix.VALUETYPE)) {
		 setValueType_GUI();
	    }
	    else if (property.equals(DomeMatrix.UNIT)) {
		 setUnit_GUI();
	    }
	    else if (property.equals(DomeMatrix.DATA)) {//in case DomeMatrixData.setData() called
		 setDataModel_GUI(dataMatrix);
	    }
	}
  }

    class MyListModel extends AbstractListModel {

    TableModel tableModel;
    public MyListModel(TableModel tableModel) {
      this.tableModel = tableModel;
      tableModel.addTableModelListener(new TableModelListener() {
	public void tableChanged(TableModelEvent e) {
	  switch(e.getType()) {
	    case TableModelEvent.INSERT:
	      fireIntervalAdded(MyListModel.this,e.getFirstRow(),e.getLastRow());
	      break;
	    case TableModelEvent.DELETE:
	      fireIntervalRemoved(MyListModel.this,e.getFirstRow(),e.getLastRow());
	      break;
	    case TableModelEvent.UPDATE:
	      return;
	  }
	  fireContentsChanged(MyListModel.this,0,getSize());
	}
	});
    }
    public int getSize() { return tableModel.getRowCount(); }
    public Object getElementAt(int index) { return Integer.toString(index); }
  }

 


class Corner extends JComponent {
    public void paintComponent(Graphics g) {
      g.setColor(Color.lightGray);
      g.fill3DRect(0, 0, getWidth(), getHeight(),true);
       
      g.setColor(Color.gray);
      g.drawLine(0, 0, getWidth(), getHeight());
    }
}
}
