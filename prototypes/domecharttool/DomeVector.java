// DomeVector.java
import java.util.*;

public interface DomeVector extends DataObject {
  
  public static final String TYPE = "DomeVector";
  public static final String VALUETYPE = "valuetype";
  public static final String SIZE = "size";
  public static final String ITEM = "item";
  public static final String ITEMS = "items";
  public static final String DATA = "data"; 
  public static final String FIXEDSIZE = "fixedsize";
  public static final String ROWVECTOR = "rowvector";
  public static final String UNIT = "unit";
  
  

 
  public String getValueType();

  public void setValueType(String value);

  public int getSize();

  public void setSize(int size);
  
  public Number getItem(int index);

  public void setItem(int index,Number n);
    
  public List getData();

  public void setData(DomeVectorData v);

  public boolean isFixedSize();

  public void setFixedSize(boolean yesOrno);
  
  public boolean isRowVector();

  public void setRowVector(boolean yesOrno);

  public String getUnit();

  public void setUnit(String unit);



}
