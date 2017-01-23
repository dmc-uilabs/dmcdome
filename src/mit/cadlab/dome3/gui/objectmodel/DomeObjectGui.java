// DomeObjectGui.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.objectmodel.DomeObject;

/**
 * Objects which implement this interface should also
 * derive from JComponent in order for ModelObjectFrame
 * to be able to use it.
 */
public interface DomeObjectGui extends DomeGui
{

	// need default constructor for serialization
	// need constructor which takes in parameter for normal uses
	public DomeObject getDomeObject();

	public String getTitlePrefix();

}
