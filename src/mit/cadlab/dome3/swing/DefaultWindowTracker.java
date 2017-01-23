// DefaultWindowTracker.java
package mit.cadlab.dome3.swing;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation of WindowTracker interface.
 */
public class DefaultWindowTracker implements WindowTracker
{

	protected List children;

	public DefaultWindowTracker()
	{
		children = new ArrayList();
	}

	public List getChildren()
	{
		return Collections.unmodifiableList(children);
	}

	public void notifyInFront(Window w)
	{
		children.remove(w); // if in queue
		children.add(w);
	}

	public void removeChildWindow(Window w)
	{
		children.remove(w);
	}

	public void hideAll()
	{
		// assumes hiding children is non-destructive
		Iterator it = children.iterator();
		while (it.hasNext()) {
			((Window) it.next()).hide();
		}
	}

	public void showAll()
	{
		Iterator it = children.iterator();
		while (it.hasNext()) {
			((Window) it.next()).show();
		}
	}

	public void closeAll()
	{
		// traverse vector backwards since closing may be destructive to vector
		int i = children.size() - 1;
		for (; i >= 0; --i) {
			Object obj = children.get(i);
			if (obj instanceof Closeable)
				((Closeable) obj).close();
		}
	}

}
