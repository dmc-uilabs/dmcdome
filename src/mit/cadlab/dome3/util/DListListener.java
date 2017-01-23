// DListListener.java
package mit.cadlab.dome3.util;

import java.util.EventListener;

public interface DListListener extends EventListener
{

	public void intervalChanged(DListEvent e);

	public void intervalAdded(DListEvent e);

	public void intervalRemoved(DListEvent e);

	public void itemsRemoved(DListEvent e);

	public void itemsReplaced(DListEvent e);

}
