// IconImageWindowTracker.java
package mit.cadlab.dome3.swing;

import java.awt.Image;

/**
 * Extends the WindowTracker to provide support for
 * passing different Images from parent window to child window.
 */
public interface IconImageWindowTracker extends WindowTracker
{

	public Image getChildIconImage();

}
