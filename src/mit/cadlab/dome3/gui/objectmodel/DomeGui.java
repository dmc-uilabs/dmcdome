// DomeObjectGui.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.objectmodel.DomeObject;

/**
 * Objects which implement this interface should also
 * derive from JComponent in order for Frame
 * to be able to use it.
 */
public interface DomeGui
{

	public String getTitle();

	public String getHelpContext();

	public void setMenuContext();

	public void close();

	public Object getGuiObject();

}
