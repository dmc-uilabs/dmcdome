// DomeMatrix.java
import java.util.*;

public interface DomeMatrix extends DataObject {
  
  public static final String TYPE = "DomeMatrix";
  public static final String VALUETYPE = "valuetype";
  public static final String SIZE = "size";
  public static final String ITEM = "item";
  public static final String ITEMS = "items";
  public static final String DATA = "data"; 
  public static final String FIXEDSIZE = "fixedsize";
  
  public static final String UNIT = "unit";
  
  

 
  public String getValueType();

  public void setValueType(String value);

  
  
  public Number getItem(int index1,int index2);

  public void setItem(int index1,int index2,Number n);
    
  public List getData();
  
  public void setData(DomeMatrix v);

  public boolean isFixedSize();

  public void setFixedSize(boolean yesOrno);
  
  public String getUnit();

  public void setUnit(String unit);



}
