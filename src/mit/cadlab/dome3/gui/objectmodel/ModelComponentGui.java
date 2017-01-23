// ModelComponentGui.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;

/**
 * Objects which implement this interface should also
 * derive from JComponent in order for ModelComponentFrame
 * to be able to use it.
 */
public interface ModelComponentGui extends DomeGui
{

	public ModelComponent getModelComponent();

}
