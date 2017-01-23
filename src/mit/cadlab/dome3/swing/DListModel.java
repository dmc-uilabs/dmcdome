// DListModel.java
package mit.cadlab.dome3.swing;

import javax.swing.Icon;
import javax.swing.ListModel;

public interface DListModel extends ListModel
{
	// Extension to support custom icon and text for each object in list.

	public Icon getIcon(int index);

	public String getListText(int index);

}
