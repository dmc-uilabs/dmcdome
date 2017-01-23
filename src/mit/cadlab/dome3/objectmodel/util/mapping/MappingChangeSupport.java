// MappingChangeSupport.java
package mit.cadlab.dome3.objectmodel.util.mapping;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.util.AbstractEventChangeSupport;

import java.util.Collection;

/**
 * Class for managing MappingChangeListeners
 */
public class MappingChangeSupport extends AbstractEventChangeSupport
{

	/**
	 * is sourceBean a MappingManager or the model?
	 */
	public MappingChangeSupport(Model m)
	{
		super("[" + m.getNameIdString() + "]" + "MappingChangeSupport", "MappingChange", m);
	}

	public synchronized void addMappingChangeListener(MappingChangeListener listener)
	{
		this.addEventChangeListener(listener);
	}

	public synchronized void removeMappingChangeListener(MappingChangeListener listener)
	{
		this.removeEventChangeListener(listener);
	}

	public synchronized void addMappingChangeListener(Parameter param, MappingChangeListener listener)
	{
		this.addEventChangeListener(param, listener);
	}

	public synchronized void removeMappingChangeListener(Parameter param, MappingChangeListener listener)
	{
		this.removeEventChangeListener(param, listener);
	}

	public void fireMappingChange(Parameter parameter, Collection newMappings)
	{
		if (parameter == null)
			throw new NullPointerException(name + ".fireMappingChange - null parameter");
		fireMappingChange(new MappingChangeEvent(source, parameter, newMappings));
	}

	public void fireMappingChange(Visualization visualization, Collection newMappings)
	{
		if (visualization == null)
			throw new NullPointerException(name + ".fireMappingChange - null visualization");
		fireMappingChange(new MappingChangeEvent(source, visualization, newMappings),visualization);
	}

	protected void fireMappingChange(MappingChangeEvent event)
	{
		Object[] genericListeners = null;
		MappingChangeSupport paramListeners = null;
		synchronized (this) {
			genericListeners = getGenericListenersArray();
			if (specificChangeListeners != null) {
				paramListeners = (MappingChangeSupport) specificChangeListeners.get(event.getParameter());
			}
		}
		if (paramListeners != null) // do parameter listeners first, last to first
			paramListeners.fireMappingChange(event);
		if (genericListeners != null) { // then generic listeners, last to first
			for (int i = genericListeners.length - 1; i >= 0; --i)
				((MappingChangeListener) genericListeners[i]).mappingChanged(event);
		}
	}

	protected void fireMappingChange(MappingChangeEvent event, Visualization visualization)
	{
		Object[] genericListeners = null;
		MappingChangeSupport visListeners = null;
		synchronized (this) {
			genericListeners = getGenericListenersArray();
			if (specificChangeListeners != null) {
				visListeners = (MappingChangeSupport) specificChangeListeners.get(event.getVisualization());
			}
		}
		if (visListeners != null) // do parameter listeners first, last to first
			visListeners.fireMappingChange(event, visualization);
		if (genericListeners != null) { // then generic listeners, last to first
			for (int i = genericListeners.length - 1; i >= 0; --i)
				((MappingChangeListener) genericListeners[i]).mappingChanged(event);
		}
	}

	// method to create instances of MappingChangeSupport for this model
	protected AbstractEventChangeSupport createEventChangeSupport()
	{
		return new MappingChangeSupport((Model) this.source);
	}

}
