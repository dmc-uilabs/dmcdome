// TreeHistoryListener.java
package mit.cadlab.dome3.swing.tree;

import java.util.EventListener;

/**
 * Listeners for TreeHistoryEvents.
 */
public interface TreeHistoryListener extends EventListener
{

	public void historyChanged(TreeHistoryEvent event);

}
