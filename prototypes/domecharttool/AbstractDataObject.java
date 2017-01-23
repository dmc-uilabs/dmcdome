// AbstractDataObject.java

import mit.cadlab.dome.util.DomeJavaBean;

public abstract class AbstractDataObject extends DomeJavaBean
  implements DataObject {
    
  public DataObject getDataObject(){
    return this;
  }
  
  public String toString(){
    return getType();
  }

}
