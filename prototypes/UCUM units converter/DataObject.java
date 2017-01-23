// DataObject.java

import mit.cadlab.dome.util.JavaBeanSupport;

public interface DataObject extends JavaBeanSupport {

  public String getType();
  public DataObject getDataObject();
  public DataObject duplicate();

}
