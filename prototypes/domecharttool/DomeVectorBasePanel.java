// DomeVectorBasePanel.java   
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
 * DomeVectorBasePanel: as the base panel for build and run DomeVector GUI
 *   as a property listener listen to the DomeVector property changes except those caught by tablemodel
 *   
 *
 */




public class DomeVectorBasePanel extends DataObjectPanel{
   
  
    protected PropertyChangeListener propertyListener;
    DomeVectorData dataVector=null; 

   // define components here
    JTextField sizeTextInput;
    JLabel sizeLabel;
    JPanel comboboxPanel;
    JComboBox unitComboBox;
    JComboBox flavorComboBox;
    JCheckBox fixSizeBox;
    JButton constraintsButton;
    JButton addButton;
    JButton deleteButton;
    JButton OkButton;
    JPanel sizePanel;
    JButton fillButton;
    JPanel rowColButtonGroupPanel;
    JPanel topRowFillerPanel;
    JScrollPane vectorScrollPane;
    JTable vectorTable;
    JList rowHeader;
    JViewport viewport;
    JPanel buttonsPanel;
    DomeVectorTableModel vectorTableModel;
    
  
    JRadioButton rowRadioButton;
    JRadioButton columnRadioButton;
    ButtonGroup group;

   
 
    String[] unitModel= {"no units","options ..."};  
    String[] flavourModel= {"real","integer"}; 
       
   
    
   

    /*
     * Constructors
     */

    public DomeVectorBasePanel(DomeVectorData dataVect){
	this(500,300,dataVect);
    }
    
    public DomeVectorBasePanel(){
	this(500,300);
    }
    
    public DomeVectorBasePanel(int PanelWidth,int PanelHeight){
        this(PanelWidth,PanelHeight,null);
      
    }

   
   

    public DomeVectorBasePanel(int PanelWidth,int PanelHeight,DomeVectorData dataVect){
      setPreferredSize(new Dimension(PanelWidth,PanelHeight));
	
    
      propertyListener = getPropertyListener();
     
      
      layoutComponents();
      if(dataVect!=null)   setDataModel_GUI(dataVect); //load dataVector into the GUI
      
      configureComponents();  
      
      
    }

    public void setDataObject(DataObject data) {
	if (data instanceof DomeVectorData)
	    setDataModel_GUI((DomeVectorData)data);
	else
	    throw new IllegalArgumentException("DomeVector gui - bad parameter");
    }

  public DomeVectorData getDataObject()
  {
    return dataVector;
  }
    protected void layoutComponents(){
      
     
      // create components
      constraintsButton=Templates.makeButton("contraints...");
      addButton=Templates.makeButton("add...");
      OkButton=Templates.makeButton("OK");
      deleteButton=Templates.makeButton("delete");
      fillButton=Templates.makeButton("fill...");
      unitComboBox=Templates.makeComboBox(unitModel);
      flavorComboBox=Templates.makeComboBox(flavourModel);
     
    
    
      fixSizeBox=Templates.makeCheckBox("fix size");
      fixSizeBox.setHorizontalTextPosition(SwingConstants.LEFT);

     
      sizeTextInput=Templates.makeTextField();
      sizeLabel=Templates.makeLabel(" elements,");
      
      rowRadioButton=Templates.makeRadioButton("row vector");
     
      columnRadioButton=Templates.makeRadioButton("column vector");
      
      
      group=new ButtonGroup();
      group.add(rowRadioButton);
      group.add(columnRadioButton);
    
      
      rowColButtonGroupPanel=new JPanel();
      sizePanel= new JPanel();
      topRowFillerPanel=new JPanel();
     
      buttonsPanel=new JPanel();
      comboboxPanel=new JPanel();

     
      vectorScrollPane=new JScrollPane();
     
     
      vectorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

     
      topRowFillerPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
		
      
      makerowColButtonGroupPanel();
      makesizePanel();
      makebuttonPanel();
      makeComboBoxPanel();
      
      JComponent[] comps = {
                 rowColButtonGroupPanel,
                 sizePanel,
		 comboboxPanel,
                 topRowFillerPanel,
		 vectorScrollPane,
		 buttonsPanel,
		
      };
			     
      // do layout
      GridBagConstraints[] gbcs = {
        new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.BOTH,new Insets(5,5,0,0),0,0),
        new GridBagConstraints(1,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.HORIZONTAL,new Insets(5,10,0,0),0,0),
        new GridBagConstraints(2,0,1,1,1.0,0.0,java.awt.GridBagConstraints.EAST,java.awt.GridBagConstraints.NONE,new Insets(5,10,0,5),0,0),
        new GridBagConstraints(3,0,1,1,0.0,0.0,java.awt.GridBagConstraints.NORTHEAST,java.awt.GridBagConstraints.HORIZONTAL,new Insets(5,5,0,5),0,0),
        new GridBagConstraints(0,1,3,1,1.0,1.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.BOTH,new Insets(5,5,0,5),0,0),
	new GridBagConstraints(0,3,4,1,0.0,0.0,java.awt.GridBagConstraints.SOUTHEAST,java.awt.GridBagConstraints.NONE,new Insets(5,5,10,5),0,0),
       	   };
      
      
      GridBagLayout gridbag = new GridBagLayout();
      setLayout(gridbag);
      for (int i=0; i<gbcs.length; ++i){
	  gridbag.setConstraints(comps[i],gbcs[i]);
	  this.add(comps[i]);
      }
      
    
  }
  
   // to be overridden by subclasses
  protected void configureComponents() {}

   private void makesizePanel(){
   sizePanel.setLayout(new GridLayout(0,3,0,0));
   sizePanel.add(sizeTextInput);
   sizePanel.add(sizeLabel);
   sizePanel.add(fixSizeBox);
  
 }
    
 
  private void  makeComboBoxPanel(){
    comboboxPanel.setLayout(new GridLayout(0,2,0,0));
    // comboboxPanel.add(fixSizeBox);
    comboboxPanel.add(unitComboBox);
    comboboxPanel.add(flavorComboBox);

 
		 

  }
  private void makerowColButtonGroupPanel(){
    rowColButtonGroupPanel.setLayout(new GridLayout(0,1,0,0));
    rowColButtonGroupPanel.add(rowRadioButton);
    rowColButtonGroupPanel.add(columnRadioButton);
    }

  private void makebuttonPanel(){
    JComponent[] comps = {
     deleteButton, addButton, constraintsButton, fillButton, OkButton,
    };
			     
    // do layout
    GridBagConstraints[] gbcs = {        
     new GridBagConstraints(1,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,0,0,10),0,0),
     new GridBagConstraints(0,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,2,0,0),0,0),
     new GridBagConstraints(3,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,0,0,10),0,0),
     new GridBagConstraints(2,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,2,0,0),0,0), 
     new GridBagConstraints(4,0,1,1,0.0,0.0,java.awt.GridBagConstraints.CENTER,java.awt.GridBagConstraints.NONE,new Insets(2,0,0,0),0,0),
     };
     GridBagLayout gridbag = new GridBagLayout();
     buttonsPanel.setLayout(gridbag);
     for (int i=0; i<gbcs.length; ++i){
	 gridbag.setConstraints(comps[i],gbcs[i]);
	 buttonsPanel.add(comps[i]);
     }

  }

    protected PropertyChangeListener getPropertyListener() {
	return new VectorPanelPropertyChangeListener();
    }
    protected void convertToNotEditable(){
	rowRadioButton.setEnabled(false);
	columnRadioButton.setEnabled(false);
	sizeTextInput.setEnabled(!fixSizeBox.isSelected());
	flavorComboBox.setEnabled(false);
	addButton.setEnabled(!fixSizeBox.isSelected());
	deleteButton.setEnabled(!fixSizeBox.isSelected());
	fixSizeBox.setEnabled(false);
	sizeLabel.setEnabled(!fixSizeBox.isSelected());
    }
    
    public void setReadOnly(){
	rowRadioButton.setEnabled(false);
	columnRadioButton.setEnabled(false);
	sizeTextInput.setEnabled(false);
	flavorComboBox.setEnabled(false);
	unitComboBox.setEnabled(false);
	addButton.setEnabled(false);
	deleteButton.setEnabled(false);
	fixSizeBox.setEnabled(false);
	sizeLabel.setEnabled(false);
	OkButton.setEnabled(false);
	fillButton.setEnabled(false);
		
    }

    protected void setDataModel_GUI(DomeVectorData d){
	this.dataVector=new DomeVectorData(d); //not directly change the data
	dataVector.addPropertyChangeListener(propertyListener);
	//load the table
	vectorTableModel= new DomeVectorTableModel(dataVector);
	
	vectorTable = new javax.swing.JTable(vectorTableModel);
     
	vectorTable.setRowSelectionAllowed(true);
	vectorTable.getColumnModel().setColumnSelectionAllowed(true);
	vectorTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
    
	vectorTable.setPreferredScrollableViewportSize(new Dimension(453, 190));

	vectorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	MyListModel listModel = new MyListModel(vectorTableModel);
        
	// Create single component to add to scrollpane
	rowHeader = new JList(listModel);
	rowHeader.setFixedCellWidth(50);
	rowHeader.setFixedCellHeight(vectorTable.getRowHeight());
	rowHeader.setCellRenderer(new RowHeaderRenderer(vectorTable));
      
	vectorScrollPane.setViewportView(vectorTable);
	vectorScrollPane.getViewport().setBackground(Color.white);

	rowHeader.setAutoscrolls(false);
	vectorScrollPane.setRowHeaderView(rowHeader); // Adds row-list left of the table
	vectorScrollPane.getRowHeader().setBackground(Color.black);

	//configure other components
	setSize_GUI();
	setRowVector_GUI();
	setFixedSize_GUI();
	setValueType_GUI();
	setUnit_GUI();
    }

    protected void setSize_GUI(){
	 this.sizeTextInput.setText(String.valueOf(dataVector.getSize()));
	 }
    
    protected void setRowVector_GUI(){
	 rowRadioButton.setSelected(dataVector.isRowVector());
	 columnRadioButton.setSelected(!dataVector.isRowVector());
     }
    
    protected void setFixedSize_GUI(){
	fixSizeBox.setSelected(dataVector.isFixedSize());
      }
    
    protected void setValueType_GUI(){
      if(dataVector.getValueType().toLowerCase().equals("real"))
	flavorComboBox.setSelectedIndex(0);
      else
	flavorComboBox.setSelectedIndex(1);
      
    }

    protected void setUnit_GUI(){
        if(dataVector.getUnit().equals(""))
	unitComboBox.setSelectedIndex(0);
      else
	unitComboBox.setSelectedIndex(1);
    }
     
    protected void RepaintTableHeader(){
	vectorTable.repaint();
	rowHeader.repaint();
	vectorScrollPane.repaint();
    }
	   

  public static void main(String[] args){
    JFrame f = Templates.makeTestFrame("Vector Panel");
    
    f.getContentPane().setLayout(new GridLayout(1,1,0,0));
    f.getContentPane().add(new DomeVectorBasePanel(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }
  
  private void dispose() {
   
    SwingUtilities.windowForComponent(this).dispose();
  } 

  private void debug(String msg){
     boolean debug=true;
     if(debug)
	 System.out.println("DomeVectorBasePanel: "+msg);
    }
  protected class VectorPanelPropertyChangeListener implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String property = e.getPropertyName();
	    Object newValue = e.getNewValue();
	    if (property.equals(DomeVector.SIZE)) {
		 setSize_GUI();
	    }
	    else if (property.equals(DomeVector.ITEMS)) {
		RepaintTableHeader();//  table changes are taken care by tablemodel
	    }
	    else if (property.equals(DomeVector.ROWVECTOR)) {
		 setRowVector_GUI();
		 RepaintTableHeader();
	    }
	    else if (property.equals(DomeVector.FIXEDSIZE)) {
		 setFixedSize_GUI();
	    }
	    else if (property.equals(DomeVector.VALUETYPE)) {
		 setValueType_GUI();
	    }
	    else if (property.equals(DomeVector.UNIT)) {
		 setUnit_GUI();
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

}
class RowHeaderRenderer extends JLabel implements ListCellRenderer {
    
    /**
     * Constructor creates all cells the same
     * To change look for individual cells put code in
     * getListCellRendererComponent method
     **/
    RowHeaderRenderer(JTable table) {
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(CENTER);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
    }
    
    /**
     * Returns the JLabel after setting the text of the cell
     **/
    public Component getListCellRendererComponent( JList list,
    Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        setText((value == null) ? "" : value.toString());
        return this;
    }
}
