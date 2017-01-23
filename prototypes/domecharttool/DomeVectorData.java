// DomeVectorData.java
// Version history:
//  0.1
//  0.2 4/14/02
//  0.3 5/10/02: add javabean support
//         policy:
//          1.for now, if a row is changed, fire single element(item) change, if multiple rows changed,fire items change.
//          2.if size change, fire size change.
//  0.4 5/21/02: add a constructor directly takes in all parameters
//

import java.util.Vector;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class DomeVectorData extends AbstractDataObject
  implements DomeVector {
  
  protected Vector data = new Vector();
  protected String unit = "";
  protected boolean isRowVector = false;
  protected boolean isFixedSize = false;
  protected Number initialValue = new Integer(0); // determines type

  /**
   *  contructor
   */
    public DomeVectorData() {    
    }
    
    public DomeVectorData(Vector v,String u,boolean isRow, boolean isFix,Number initialV) 
    {
	data=(Vector)v.clone();
	unit=u;
	isRowVector=isRow;
	isFixedSize=isFix;
	initialValue=initialV;
	//if the initialvalue has differient type with data vector, make them coherent
	changeClass(initialValue instanceof Integer); 

	
    }

  public DomeVectorData(DomeVectorData v) {
    if (v == null)
      throw new IllegalArgumentException("DomeVector - null parameter");
   
    this.data = (Vector)v.data.clone();
  
    this.unit = v.unit;
    this.isRowVector = v.isRowVector;
    this.initialValue = v.initialValue;
    this.isFixedSize = v.isFixedSize;

    
  }
 
  public DomeVector getDomeVector(){
    return this;
  }

  public DataObject duplicate() {
    return new DomeVectorData(this);
  }
  
  public String toString(){
    return data.toString();
  }
  
  public String getType(){
    return TYPE;
  }

  //inherited from domeVector interface
  public String getValueType()
  {
   if(initialValue instanceof Double) return "real";
    return "integer";
  }

  public void setValueType(String type)
  { if (type.equals("real")) {
      if (initialValue instanceof Integer)
	changeClass(false);
    } else if (type.equals("integer")) {
      if (initialValue instanceof Double)
	changeClass(true);
    }
    firePropertyChange(VALUETYPE,null,type);
    firePropertyChange(DATA,null,data);
  }

  public int getSize(){
    return data.size();
  }

  public void setSize(int size){
    int currentsize=getSize();
    if(currentsize==size) return;
    if(currentsize<size)  //append
      {
	  addItems(currentsize,size-currentsize,this.initialValue);
	
  
      }
    else //truncate from end
      {
   
	removeRange(size,currentsize);
	
      }
	
  
  }
  

  public List getData() {
    return Collections.unmodifiableList(data);;
  }
 
  public void setData(DomeVectorData v){
     if (v == null)
      throw new IllegalArgumentException("DomeVector - null parameter");
    data = (Vector)v.data.clone();
  
    unit = v.unit;
    isRowVector = v.isRowVector;
    initialValue = v.initialValue;
    isFixedSize = v.isFixedSize;
     
    firePropertyChange(DATA,null,data);//pass the data vector as a whole
    firePropertyChange(SIZE,null,new Integer(data.size())); 
    firePropertyChange(FIXEDSIZE,null,new Boolean(isFixedSize));
    firePropertyChange(UNIT,null,new String(unit));
    firePropertyChange(ROWVECTOR,null,new Boolean(isRowVector));
    
  }

  public boolean isFixedSize(){
    return this.isFixedSize;
  }

  public void setFixedSize(boolean yesOrno){
    Boolean oldProp = new Boolean(isFixedSize);
    this.isFixedSize=yesOrno;   
    firePropertyChange(FIXEDSIZE,oldProp,new Boolean(yesOrno));
  }

  public Number getItem(int index) {
    return (Number)data.get(index);
  }
  
  public void setItem(int index, Number n) {
   
    if (n.getClass() != initialValue.getClass()) {
      if (initialValue instanceof Double)
	n = new Double(n.doubleValue());
      else if (initialValue instanceof Integer)
	n = new Integer(n.intValue());
    }
    setInitialValue(n);
    data.set(index,this.initialValue);

    position p;
    if(isRowVector)
      p=new position(0,index);
    else
      p=new position(index,0);
    firePropertyChange(ITEM,null,p);
  
  }
    
  public String getUnit() {
    return unit;
  }

  public void setUnit(String u) {
    if (u == null) return;

    String old=this.unit;
    
    this.unit = u;

    firePropertyChange(UNIT,old,u);

  }

  public boolean isRowVector() {
    return isRowVector;
  }

  public void setRowVector(boolean rowVector) {

    
    isRowVector = rowVector;

    firePropertyChange(ROWVECTOR,null,new Boolean(rowVector));
  }

 
 

 public void changeClass(boolean isInteger) {
    if (isInteger) {
      initialValue = new Integer(initialValue.intValue());
      for (int i=0; i<data.size(); i++)
	data.set(i,new Integer(((Number)data.get(i)).intValue()));
    } else { // must be Double
      initialValue = new Double(initialValue.doubleValue());
      for (int i=0; i<data.size(); i++)
	data.set(i,new Double(((Number)data.get(i)).doubleValue()));
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

    public Number getInitialValue() {
	return initialValue;
    }

   public boolean addItems(int index, int howMany, Number newInitialValue) {
    if(isFixedSize) {
      debug("DomeVector: Warning! dataVector size is fixed, can't add...");
      return false;
    }
    if (index<0 || index>data.size())
      return false;
    setInitialValue(newInitialValue);
    for (int i=0; i<howMany; ++i)
       data.add(index,this.initialValue);
	 
    firePropertyChange(ITEMS,null,new positions(positions.ADD,index,howMany));
    firePropertyChange(SIZE,null,new Integer(data.size()));  
    return true;
  }

  public void removeItems(int[] indices) {
    if(isFixedSize) {
      debug("DomeVector: Warning! dataVector size is fixed, can't delete...");
      return;
    }
    // assume indices are all good
    Arrays.sort(indices);
    for (int i = indices.length-1; i>=0; --i)
     	 data.remove(indices[i]);

    
    firePropertyChange(ITEMS,null,new positions(positions.DEL,indices.length));
    firePropertyChange(SIZE,null,new Integer(data.size()));  

 	   
  }

  
  public void fillItems(int[] indices, Number n) {
    // assume indices are all good
    setInitialValue(n);
    for (int i=0; i<indices.length; ++i)
      data.set(indices[i],initialValue);
    
    firePropertyChange(ITEMS,null,new positions(positions.UPDATE,indices.length));
    
  
   
  }

  //startIndex is inclusive and endIndex is exclusive
 
  public void removeRange(int startIndex,int endIndex){
     if(isFixedSize) {
      debug("Warning! dataVector size is fixed, can't delete...");
      return;
    }
    //assume the index para are good
    for(int i=endIndex-1;i>=startIndex;i--)
    	 data.removeElementAt(i);
	
    firePropertyChange(ITEMS,null,new positions(positions.DEL,startIndex,endIndex-startIndex));
    firePropertyChange(SIZE,null,new Integer(data.size()));  
  }

 

 //for debug

 private void debug(String msg){
     boolean debug=true;
     if(debug)
	 System.out.println("DomeVectorData: "+msg);
    }

  
}
 class position 
    
  {
    public int col;
    public int row;
        
    public position(int r,int c){
      this.col=c;
      this.row=r;
    }
      
  }
  class positions 
    
  {
      public static final String ADD="add";
      public static final String DEL="remove";
      public static final String UPDATE="update";

      protected String change="";  
      protected int start=-1;
      protected int howmany=-1;

      public positions(String changetype,int start,int howmany){
	  this.start=start;
	  this.howmany=howmany;
	  
	  change=changetype;
	  int end=getEndIndex();
	  if(start>=end)
	      throw new IllegalArgumentException("Positions - Bad index parameters");
	 
      }
      
      public positions(String changetype,int howmany){//for multiple indiecs, start will remain -1
	  this.howmany=howmany;
	  
	  change=changetype;
	  
	 
      }

      
      
      public int getStartIndex(){
	  return this.start;
      }
       
      public int getEndIndex(){//note endindex is exclusive
	  if((start==-1)&&(howmany==-1)) return -1;
	  return this.start+this.howmany;
      }
      
      public String getChangeType(){
	  return this.change;
      }

         
  }
