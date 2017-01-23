// WindowTracker.java
package mit.cadlab.dome3.swing;

import java.awt.Window;
import java.util.List;

/**
 * This interface is for Windows which manage child Windows.
 * java.awt.Window has internal window tracking which is
 * limited to package-only access.
 */
public interface WindowTracker
{

	public List getChildren();

	public void notifyInFront(Window w);

	public void removeChildWindow(Window w);

	public void hideAll();

	public void showAll();

	public void closeAll();

}
