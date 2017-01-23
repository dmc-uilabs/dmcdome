// CausalityChangeSupport.java
package mit.cadlab.dome3.objectmodel.util.causality;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.util.AbstractEventChangeSupport;

/**
 * Class for managing CausalityChangeListeners
 */
public class CausalityChangeSupport extends AbstractEventChangeSupport
{

	/**
	 * is sourceBean a CausalityManager or the relation/model?
	 */
	public CausalityChangeSupport(ModelObjectScope m)
	{
		super("[" + m.getNameIdString() + "]" + "CausalityChangeSupport", "CausalityChange", m);
	}

	public synchronized void addCausalityChangeListener(CausalityChangeListener listener)
	{
		this.addEventChangeListener(listener);
	}

	public synchronized void removeCausalityChangeListener(CausalityChangeListener listener)
	{
		this.removeEventChangeListener(listener);
	}

	public synchronized void addCausalityChangeListener(Object obj, CausalityChangeListener listener)
	{
		this.addEventChangeListener(obj, listener);
	}

	public synchronized void removeCausalityChangeListener(Object obj, CausalityChangeListener listener)
	{
		this.removeEventChangeListener(obj, listener);
	}

	public void fireCausalityChange(Object obj, CausalityStatus oldValue, CausalityStatus newValue)
	{
		if (obj == null)
			throw new NullPointerException(name + ".fireCausalityChange - null parameter");
		if ((oldValue == null && newValue == null) || (oldValue != null && newValue != null && oldValue.equals(newValue)))
			return;  // no change
		fireCausalityChange(new CausalityChangeEvent(source, obj, oldValue, newValue));
	}

	protected void fireCausalityChange(CausalityChangeEvent event)
	{
		Object[] genericListeners = null;
		CausalityChangeSupport paramListeners = null;
		synchronized (this) {
			genericListeners = getGenericListenersArray();
			if (specificChangeListeners != null) {
				paramListeners = (CausalityChangeSupport) specificChangeListeners.get(event.getParameter());
			}
		}
		if (paramListeners != null) // do parameter listeners first, last to first
			paramListeners.fireCausalityChange(event);
		if (genericListeners != null) { // then generic listeners, last to first
			for (int i = genericListeners.length - 1; i >= 0; --i)
				((CausalityChangeListener) genericListeners[i]).causalityChanged(event);
		}
	}

	// method to create instances of CausalityChangeSupport for this ModelObjectScope
	protected AbstractEventChangeSupport createEventChangeSupport()
	{
		return new CausalityChangeSupport((ModelObjectScope) this.source);
	}

}
