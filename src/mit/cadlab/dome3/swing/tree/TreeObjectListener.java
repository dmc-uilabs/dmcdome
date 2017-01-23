// TreeObjectListener.java
package mit.cadlab.dome3.swing.tree;

import java.util.EventListener;

public interface TreeObjectListener extends EventListener
{

	public void nodeValueChanged(TreeObjectEvent event);

	public void nodeStructureChanged(TreeObjectEvent event);

	public void childrenChanged(TreeObjectEvent event);

	public void childrenAdded(TreeObjectEvent event);

	public void childrenRemoved(TreeObjectEvent event);

}
