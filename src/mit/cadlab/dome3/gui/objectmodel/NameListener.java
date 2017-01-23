// NameListener.java
package mit.cadlab.dome3.gui.objectmodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class NameListener implements PropertyChangeListener
{

	public static final String NAME = "name";

	public void propertyChange(PropertyChangeEvent e)
	{
		String property = e.getPropertyName();
		Object newValue = e.getNewValue();
		if (property.equals(NAME)) {
			nameChanged((newValue == null) ? "" : newValue.toString());
		}
	}

	public abstract void nameChanged(String newName);

}
