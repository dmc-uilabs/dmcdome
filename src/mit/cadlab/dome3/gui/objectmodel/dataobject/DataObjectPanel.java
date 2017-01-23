// DataObjectPanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;

import java.awt.GridBagConstraints;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

public abstract class DataObjectPanel extends JPanel
{

	// need default constructor for run mode guis to support serialization
	// do not need default constructors for build guis

	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class

	public abstract void setDataObject(DataObject data);

	protected abstract PropertyChangeListener createPropertyListener();

}
