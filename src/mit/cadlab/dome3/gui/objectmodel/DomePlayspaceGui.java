// DomeObjectGui.java
package mit.cadlab.dome3.gui.objectmodel;


/**
 * Objects which implement this interface should also
 * derive from JComponent in order for Frame
 * to be able to use it.
 */
public interface DomePlayspaceGui
{

	public String getTitle();

	public String getHelpContext();

	public void setMenuContext();

	public void close();


}
