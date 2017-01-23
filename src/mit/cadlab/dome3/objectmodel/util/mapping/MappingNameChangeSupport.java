// MappingChangeSupport.java
package mit.cadlab.dome3.objectmodel.util.mapping;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.util.AbstractEventChangeSupport;

/**
 * Class for managing MappingNameChangeListeners
 */
public class MappingNameChangeSupport extends AbstractEventChangeSupport
{

	/**
	 * is sourceBean a MappingManager or the model?
	 */
	public MappingNameChangeSupport(Model m)
	{
		super("[" + m.getNameIdString() + "]" + "MappingNameChangeSupport", "MappingNameChange", m);
	}

	public synchronized void addMappingNameChangeListener(MappingNameChangeListener listener)
	{
		this.addEventChangeListener(listener);
	}

	public synchronized void removeMappingNameChangeListener(MappingNameChangeListener listener)
	{
		this.removeEventChangeListener(listener);
	}

	public synchronized void addMappingNameChangeListener(Parameter param, MappingNameChangeListener listener)
	{
		this.addEventChangeListener(param, listener);
	}

	public synchronized void removeMappingNameChangeListener(Parameter param, MappingNameChangeListener listener)
	{
		this.removeEventChangeListener(param, listener);
	}

	public void fireMappingNameChange(Parameter parameter)
	{
		if (parameter == null)
			throw new NullPointerException(name + ".fireMappingNameChange - null parameter");
		fireMappingNameChange(new MappingNameChangeEvent(source, parameter));
	}

	public void fireMappingNameChange(Visualization vis)
	{
		if (vis == null)
			throw new NullPointerException(name + ".fireMappingNameChange - null visualization");
		fireMappingNameChange(new MappingNameChangeEvent(source, vis),vis);
	}

	protected void fireMappingNameChange(MappingNameChangeEvent event)
	{
		Object[] genericListeners = null;
		MappingNameChangeSupport paramListeners = null;
		synchronized (this) {
			genericListeners = getGenericListenersArray();
			if (specificChangeListeners != null) {
				paramListeners = (MappingNameChangeSupport) specificChangeListeners.get(event.getParameter());
			}
		}
		if (paramListeners != null) // do parameter listeners first, last to first
			paramListeners.fireMappingNameChange(event);
		if (genericListeners != null) { // then generic listeners, last to first
			for (int i = genericListeners.length - 1; i >= 0; --i)
				((MappingNameChangeListener) genericListeners[i]).mappingNameChanged(event);
		}
	}

		protected void fireMappingNameChange(MappingNameChangeEvent event, Visualization vis)
	{
		Object[] genericListeners = null;
		MappingNameChangeSupport visListeners = null;
		synchronized (this) {
			genericListeners = getGenericListenersArray();
			if (specificChangeListeners != null) {
				visListeners = (MappingNameChangeSupport) specificChangeListeners.get(event.getVisualization());
			}
		}
		if (visListeners != null) // do parameter listeners first, last to first
			visListeners.fireMappingNameChange(event,vis);
		if (genericListeners != null) { // then generic listeners, last to first
			for (int i = genericListeners.length - 1; i >= 0; --i)
				((MappingNameChangeListener) genericListeners[i]).mappingNameChanged(event);
		}
	}
	// method to create instances of MappingNameChangeSupport for this model
	protected AbstractEventChangeSupport createEventChangeSupport()
	{
		return new MappingNameChangeSupport((Model) this.source);
	}

}

