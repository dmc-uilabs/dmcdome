// DomeMatrixData.java
//  ver 0.1  May 24, 02

import java.util.Vector;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.awt.*;

/**
 * Data structure for a Dome Matrix.
 */
public class DomeMatrixData extends AbstractDataObject
  implements DomeMatrix {

  protected Vector data = new Vector();
  protected String unit = "";
  protected boolean isFixedSize = false;
  protected Number initialValue = new Integer(0); // determines type


  public DomeMatrixData() {  
    
  }
  
  public DomeMatrixData(int row, int col, boolean isFixed, Number initV) {
    
    
    isFixedSize=isFixed;
    
    initialValue=initV;

    for(int i=0;i<row;i++)
      {
	Vector therow = new Vector(col);
	//fillRow(therow,initialValue);
	data.add(therow);
      }
    //otherwise the columncount will be zero;
    setColumnCount(col);
  }
  
  public DomeMatrixData(int row, int col) {
    
    this(row,col,false,new Integer(0));
   
   }

  
 

  public DomeMatrixData(DomeMatrixData v) {
    data = (Vector)v.data.clone();
  
    unit = v.unit;
    initialValue = v.initialValue;
    isFixedSize = v.isFixedSize;
  }

  public DataObject duplicate() {
    return new DomeMatrixData(this);
  }
  
  public String toString(){
    return "DomeMatrix("+getRowCount()+"*"+getColumnCount()+"):"+ data.toString();
  }
  
  public void setData(DomeMatrix v){
    if (v == null)
      throw new IllegalArgumentException("DomeMatrix - null parameter");
    if(!(v instanceof DomeMatrixData))
      throw new IllegalArgumentException("DomeMatrix - DomeMatrixData type parameter required");
    
    data = (Vector)((DomeMatrixData)v).data.clone();
  
    unit = ((DomeMatrixData)v).unit;
    initialValue = ((DomeMatrixData)v).initialValue;
    isFixedSize = ((DomeMatrixData)v).isFixedSize;
     
    firePropertyChange(DATA,null,this);//pass the data vector as a whole
   
   
    
  }


  public List getData() {
    return Collections.unmodifiableList(data);
  }

 
  public int getRowCount() {
    return data.size();
  }
  
  public int getColumnCount() {
    if(data.size()==0) return 0;
    else 
      return ((Vector)data.get(0)).size();
 
  }
  
  public void setColumnCount(int columnCount) {
    if(columnCount==getColumnCount()) return;
    int oldSize=getColumnCount();
    for (int r = 0; r < getRowCount(); r++) { 
	    Vector row = (Vector)data.elementAt(r);
	   
	    if(oldSize>columnCount)  
	      {
		row.setSize(columnCount); //being truncated;
	      }
	    else
	      appendColumnItems(row,oldSize,columnCount);
    }

    //fire both items and size change
    if(oldSize>columnCount)
      {
	Vector info=new Vector();
	info.add("delcol");
	for(int i=oldSize;i>=columnCount;i--)
	  info.add(new Integer(i));
	firePropertyChange(ITEMS,null,info);
	firePropertyChange(SIZE,null,new String("delcol"));
      }
    else
      {
	
	Vector info=new Vector();
	info.add("addcol");
	for(int i=oldSize;i<columnCount;i++)
	  info.add(new Integer(i));
	firePropertyChange(ITEMS,null,info);
	firePropertyChange(SIZE,null,new String("addcol"));
      }
  }
   
  public void setRowCount(int newSize) {
    if(isFixedSize) {
      debug("Warning! dataMatrix size is fixed, can't change row count");
      return ;
    }
    if ((newSize < 0) || (newSize == getRowCount()))
      return;
    
   
    int oldNumRows = getRowCount();
    if (newSize <= getRowCount()) {
      // newSize is smaller than our current size, so we can just
      // let Vector discard the extra rows
      data.setSize(newSize);
      
     }
    else {
      int columnCount = getColumnCount();
      // We are adding rows to the model
      while(getRowCount() < newSize) {
	Vector newRow = new Vector(columnCount);
	//fill with initial value
	fillRow(newRow,initialValue);
	data.addElement(newRow);
      }
    }
     //fire both items and size change
    if(oldNumRows>newSize)
      {
	Vector info=new Vector();
	info.add("delrow");
	for(int i=oldNumRows-1;i>=newSize;i--)
	  info.add(new Integer(i));
	firePropertyChange(ITEMS,null,info);
	firePropertyChange(SIZE,null,new String("delrow"));
      }
    else
      {
	
	Vector info=new Vector();
	info.add("addrow");
	for(int i=oldNumRows;i<newSize;i++)
	  info.add(new Integer(i));
	firePropertyChange(ITEMS,null,info);
	firePropertyChange(SIZE,null,new String("addrow"));
      }
   
  }

 
  public void  setFixedSize(boolean isFixed){
    this.isFixedSize=isFixed;
    firePropertyChange(FIXEDSIZE,null,new Boolean(isFixed));
  }

  public boolean isFixedSize(){
    return this.isFixedSize;
  }

  public Number getItem(int row,int col) {
    if(getRowCount()==0 ||getColumnCount()==0) return null;
    Vector rowVector = (Vector)data.elementAt(row);
    return (Number)rowVector.elementAt(col);
  }
  
  public void setItem(int row,int col, Number n) {
    if (n.getClass() != initialValue.getClass()) {
      if (initialValue instanceof Double)
	n = new Double(n.doubleValue());
      else if (initialValue instanceof Integer)
	n = new Integer(n.intValue());
    }
    
    Vector rowVector = (Vector)data.elementAt(row);
    rowVector.setElementAt(n, col);
    firePropertyChange(ITEM,null,new Point(row,col));
  }
    
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    if (unit == null) return;
    this.unit = unit;
    firePropertyChange(UNIT,null,unit);
  }

 
  public String getType(){
    return TYPE;
  }

 
  public String getValueType(){
    if(initialValue instanceof Double) return "Real";
    return "Integer";
  }

  public void setValueType(String type) {
    if (type.toLowerCase().equals("real")) {
      if (initialValue instanceof Integer)
	changeClass(false);
    } else if (type.toLowerCase().equals("integer")) {
      if (initialValue instanceof Double)
	changeClass(true);
    }
    firePropertyChange(VALUETYPE,null,type);
  }

  public Number getInitialValue() {
    return initialValue;
  }

  //not fire property change, should be only inner use
  private void changeClass(boolean isInteger) {
    if (isInteger) {
      initialValue = new Integer(initialValue.intValue());
      for (int i=0; i<data.size(); i++) 
	{
	  Vector rowData=(Vector)data.get(i);
	  for(int j=0;j<rowData.size();j++){
	    Number n=(Number)rowData.elementAt(j);
	    rowData.set(j,new Integer(n.intValue()));
	  }
	}
    } else { // must be Double
      initialValue = new Double(initialValue.doubleValue());
      for (int i=0; i<data.size(); i++)
	{
	  Vector rowData=(Vector)data.get(i);
	  for(int j=0;j<rowData.size();j++){
	    Number n=(Number)rowData.elementAt(j);
	    rowData.set(j,new Double(n.doubleValue()));
	  }
    }
  }
  }

  public void setInitialValue(Number n) {
    if (n == null) return;
    if (initialValue.getClass() != n.getClass()) { // wrong type...convert
      if (initialValue instanceof Integer) {
	initialValue = new Integer(n.intValue());
      } else { // must be Double
	initialValue = new Double(n.doubleValue());
      }
    } else { // correct type
      initialValue = n;
    }
  }

  public void addRowItems(int index, int howMany, Number newInitialValue) {
    if(isFixedSize) {
      debug("Warning! dataVector size is fixed, can't add...");
      return;
    }
    if (index<0 || index>data.size())
      return;
    setInitialValue(newInitialValue);
    //stores information to pass when propertychange is fired
    Vector info=new Vector();
    info.add("addrow");//put {0} as a add/update/del identification

    for (int i=0; i<howMany; ++i)
     { 
       Vector row=new Vector(getColumnCount());
       fillRow(row,initialValue);
       data.insertElementAt(row,index); 
       info.add(new Integer(index+i));
      } 
   
    firePropertyChange(ITEMS,null,info);
    firePropertyChange(SIZE,null,new String("addrow"));
  }


  public void removeRowItems(int[] indices) {
    if(isFixedSize) {
      debug("Warning! dataVector size is fixed, can't delete...");
      return;
    }
    // assume indices are all good
   
    //stores information to pass when propertychange is fired
    Vector info=new Vector();
    info.add("delrow");//put {0} as a add/update/del identification
    Arrays.sort(indices);
    for (int i = indices.length-1; i>=0; --i)
     	{
	  data.remove(indices[i]);
	  info.add(new Integer(indices[i]));
	}
  
    firePropertyChange(ITEMS,null,info);
    firePropertyChange(SIZE,null,new String("delrow"));
 }


  public void  addColumnItems(int index, int howMany, Number newInitialValue){
   
    if(isFixedSize) {
      debug("Warning! dataMatrix size is fixed, can't add...");
      return;
    }
    if (index<0 || index>data.size())
      return;
  
    setInitialValue(newInitialValue);
    Vector info=new Vector();
    info.add("addcol");

    for(int j=0;j<getRowCount();j++){ 
      //for each row add columns into it
      Vector row=(Vector)(data.elementAt(j));
      for(int i=0;i<howMany;i++){
	row.insertElementAt(initialValue,index);
        }
    }
    
    for(int i=0;i<howMany;i++){
      info.add(new Integer(index+i));
    }
   
    firePropertyChange(ITEMS,null,info);
    firePropertyChange(SIZE,null,new String("addcol"));
  }
  
  public void removeColumnItems(int[] indices) {
    if(isFixedSize) {
      debug("Warning! dataVector size is fixed, can't delete...");
      return;
    }
    // assume indices are all good
    Arrays.sort(indices);
    Vector info=new Vector();
    info.add("delcol");

    for(int j=0;j<getRowCount();j++){ 
      //for each row delete columns 
      Vector row=(Vector)(data.elementAt(j));
      for (int i = indices.length-1; i>=0; --i){
     	 row.remove(indices[i]);
        }
    }
    
    for(int i=0;i<indices.length;i++){
      info.add(new Integer(indices[i]));
    }
   
    firePropertyChange(ITEMS,null,info);
    firePropertyChange(SIZE,null,new String("delcol"));
	   
  }
  

  //startIndex is inclusive and endIndex is exclusive
  public void removeRange(int startIndex,int endIndex,boolean isForRow){
     if(isFixedSize) {
      debug("Warning! dataVector size is fixed, can't delete...");
      return;
    }
    
    if(isForRow) //removing rows
    {
      if(startIndex<0||endIndex>=getRowCount()) return;
      Vector info=new Vector();
      info.add("delrow");
      for(int i=endIndex-1;i>=startIndex;i--)
    	{
	  data.removeElementAt(i);
	  info.add(new Integer(i));
	}

      firePropertyChange(ITEMS,null,info);
      firePropertyChange(SIZE,null,new String("delrow"));

    }
    else{//removing columns
       if(startIndex<0||endIndex>=getColumnCount()) return;
       Vector info=new Vector();
       info.add("delcol");
       for(int j=0;j<getRowCount();j++){
	 //for each row delete columns 
	 Vector row=(Vector)(data.elementAt(j));
	 for(int i=endIndex-1;i>=startIndex;i--)
	   row.removeElementAt(i);
       }
       for(int i=endIndex-1;i>startIndex;i--)
	 info.add(new Integer(i));
       
       firePropertyChange(ITEMS,null,info);
       firePropertyChange(SIZE,null,new String("delcol"));
    }
   
   
  }
 
  public void fillItems(Vector selectedPoints, Number n) {
    //selectedPoints: vector of java.awt.Point, with x:row, y:column
    // assume indices are all good
    setInitialValue(n);
    for (int i=0; i<selectedPoints.size(); ++i){
      Point p=(Point)selectedPoints.elementAt(i);
    
      setItem(p.x,p.y,initialValue); //will fire property change in this function
    
    }
    return;
  }

  //not fire propertychange, should be used by this function itself
  private void fillRow(Vector row, Number n) {
   
    // assume indices are all good
    setInitialValue(n);

    for (int i=0; i<getColumnCount(); ++i)
      row.add(i,initialValue);  
    return;
  }

  //not fire propertychange, should be used by this function itself
  private void appendColumnItems(Vector row,int oldSize,int newSize){
    if(oldSize>=newSize) return;
    for (int i=0; i<(newSize-oldSize); i++)
      row.add(initialValue);  
    }

  //static function for use
   
  //then check is they are all same type with the first domeMatrix
	//this has two cases: one is all the rowCount is the same, the other is all the colCount is the same, depends on how to append then together
	//if all rowCount is the same, then append then horizontally and colCount doesn't need to be same
	//else if all colCount is the same, then append then vertically and rowCount doesn't need to be same

  // Matrix +/-/*
 
  /**
   * matrix plus, must be same dimension, also the first domematrix data determines the result matrix property
   */ 

  public static DomeMatrixData plus(DomeMatrixData m1, DomeMatrixData m2)
  {
    //first check dimension is right
    if(m1.getRowCount()==m2.getRowCount()&&m1.getColumnCount()==m2.getColumnCount())
      {
	if(m1.getValueType().equals(m2.getValueType()))
	  {
	    DomeMatrixData result=new DomeMatrixData(m1);
	    for(int i=0;i<result.getRowCount();i++)
	      for(int j=0;j<result.getColumnCount();j++)
	      {
		result.setItem(i,j,new Double(m1.getItem(i,j).doubleValue()+m2.getItem(i,j).doubleValue()));
	      }
	    return result;
	  }
	else //should all changed to the valuetype of the first Matrix
	  {
	    DomeMatrixData result=new DomeMatrixData(m1);
	    for(int i=0;i<result.getRowCount();i++)
	      for(int j=0;j<result.getColumnCount();j++)
	      {
		
		result.setItem(i,j,new Double(m1.getItem(i,j).doubleValue()+m2.getItem(i,j).doubleValue()));//it automatically takes type conversion(integer/double)
	      }
	    return result;
	  }
      }
    else 
      return null;

  }

  
  /**
   * matrix minus, 
   * must be same dimension, also the first domematrix data determines the result matrix property
   */ 
  
  public static DomeMatrixData minus(DomeMatrixData m1, DomeMatrixData m2)
  {
 //first check dimension is right
    if(m1.getRowCount()==m2.getRowCount()&&m1.getColumnCount()==m2.getColumnCount())
      {
	if(m1.getValueType().equals(m2.getValueType()))
	  {
	    DomeMatrixData result=new DomeMatrixData(m1);
	    for(int i=0;i<result.getRowCount();i++)
	      for(int j=0;j<result.getColumnCount();j++)
	      {
		result.setItem(i,j,new Double(m1.getItem(i,j).doubleValue()-m2.getItem(i,j).doubleValue()));
	      }
	    return result;
	  }
	else 
	  {
	    DomeMatrixData result=new DomeMatrixData(m1);
	    for(int i=0;i<result.getRowCount();i++)
	      for(int j=0;j<result.getColumnCount();j++)
	      {
		
		result.setItem(i,j, new Double(m1.getItem(i,j).doubleValue()-m2.getItem(i,j).doubleValue()));//it automatically takes type conversion(integer/double)
	      }
	    return result;
	  }
	
      }
    else 
      return null;

  }

 /**
   * matrix multiply, 
   * must be match dimension--which means they have flipped row/col count, also the first domematrix data determines the result matrix property
   */ 

  public static DomeMatrixData multiply(DomeMatrixData m1, DomeMatrixData m2)
  {
    if(m1.getColumnCount()==m2.getRowCount())
      {
	DomeMatrixData result=new DomeMatrixData(m1.getRowCount(),m2.getColumnCount());
	result.setInitialValue(m1.getInitialValue());
	for(int i=0;i<m1.getRowCount();i++)
	  for(int j=0;j<m2.getColumnCount();j++)
	    {
	      double n=0;
	      for(int k=0;k<m1.getColumnCount();k++)
		n+= m1.getItem(i,k).doubleValue()*m2.getItem(k,j).doubleValue();
	      result.setItem(i,j,new Double(n));
	    }
	return result;
	    
      }
    else 
      return null;
    
    
  }


  // Matrix append vertically /horizontally
  
  /*
   *  Matrix appendHorizontally
   *  must be same row count
   *  the first matrix determins property
   */

  public static DomeMatrixData appendHorizontally(DomeMatrixData m1, DomeMatrixData m2)
  {
    if(m1.getRowCount()==m2.getRowCount())
      {
	DomeMatrixData result=new DomeMatrixData(m1.getRowCount(),m1.getColumnCount()+m2.getColumnCount(),m1.isFixedSize(),m1.getInitialValue());
	
	for(int i=0;i<result.getRowCount();i++)
	  {
	    for(int j=0;j<m1.getColumnCount();j++)
	      result.setItem(i,j,m1.getItem(i,j));
	    for(int k=0;k<m2.getColumnCount();k++)
	      result.setItem(i,m1.getColumnCount()+k,m2.getItem(i,k));
	  }
	
	return result;
      }
    else
      return null;

  }

  /*
   *  Matrix appendHorizontally
   *  must be same column count
   *  the first matrix determins property
   */

  public static DomeMatrixData appendVertically(DomeMatrixData m1, DomeMatrixData m2)
  {
      if(m1.getColumnCount()==m2.getColumnCount())
      {
	DomeMatrixData result=new DomeMatrixData(m1.getRowCount()+m2.getRowCount(),m2.getColumnCount(),m1.isFixedSize(),m1.getInitialValue());
	
	for(int j=0;j<result.getColumnCount();j++)
	  {
	    for(int i=0;i<result.getRowCount();i++)
	     {
	       if(i<m1.getRowCount())
		 result.setItem(i,j,m1.getItem(i,j));
	       else  
		 result.setItem(i,j,m2.getItem(i-m1.getRowCount(),j));
	   }
	  }
	
	return result;
	
      }
    else
      return null;
  }

 private void debug(String msg){
     boolean debug=true;
     if(debug)
	 System.out.println("DomeMatrixData: "+msg);
    }
}
