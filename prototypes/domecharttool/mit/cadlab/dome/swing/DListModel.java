// DListModel.java
package mit.cadlab.dome.swing;

import javax.swing.ListModel;
import javax.swing.Icon;

public interface DListModel extends ListModel {
  // Extension to support custom icon and text for each object in list.

  public Icon getIcon(int index);
  public String getListText(int index);

}
