// InterfaceParameterClient.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.api.ParameterStatusChangeEvent;
import mit.cadlab.dome3.api.ParameterStatusChangeListener;
import mit.cadlab.dome3.api.ParameterValueChangeEvent;
import mit.cadlab.dome3.api.ParameterValueChangeListener;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Version of interface parameter for interface parameters on the client
 * Changes from GUI/external are sent to server listeners
 * Changes from server are sent to datatype without bouncing back.
 */
public class InterfaceParameterClient extends ParameterRuntime
{

	protected InterfaceParameterClientChangeSupport changeListeners = new InterfaceParameterClientChangeSupport(this);
	protected ServerParameterChangeListener serverParamListener;
    protected Vector parameterValueChangeListeners = new Vector();
    protected Vector parameterStatusChangeListeners = new Vector();
	private int lastChangedId = -1;

	public InterfaceParameterClient(ModelObjectScope scope, Id id, String dataType)
	{
		super(scope, id, dataType);
	}

	public InterfaceParameterClient(ModelObjectScope scope, Id id)
	{
		super(scope, id, (String) null);
	}

	public InterfaceParameterClient(ModelObjectScope scope, Id id, Parameter param)
	{
		super(scope, id, param);
	}

	public InterfaceParameterClient(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
	}

	protected ParameterRuntime.InternalDataObjectListener createInternalDataObjectListener()
	{
		return new InterfaceParameterClientInternalDataObjectListener();
	}

	protected ServerParameterChangeListener getServerParameterChangeListener()
	{
		if (serverParamListener == null)
			serverParamListener = new ServerParameterChangeListener();
		return serverParamListener;
	}

	public void addServerParameterListener(DataObjectChangeListener listener)
	{
		changeListeners.addServerParameterListener(listener);
	}

	public void removeServerParameterListener(DataObjectChangeListener listener)
	{
		changeListeners.removeServerParameterListener(listener);
	}

	public void addClientListener(DataObjectChangeListener listener)
	{
		changeListeners.addClientListener(listener);
	}

	public void removeClientListener(DataObjectChangeListener listener)
	{
		changeListeners.removeClientListener(listener);
	}

	protected class InterfaceParameterClientInternalDataObjectListener extends InternalDataObjectListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			changeListeners.fireDataObjectChange(evt);
		}
	}

	protected class ServerParameterChangeListener extends DataObjectChangeListener
	{
		public ServerParameterChangeListener()
		{
			super(InterfaceParameterClient.this);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			synchronized (changeListeners) {
				changeListeners.setServerParameterChanged();
				PropertyChangeEvent event = e.getEvent();
				if (event.getPropertyName().equals(Parameter.VALUE_STATUS))
					setValueStatus((String) event.getNewValue());
				else
					InterfaceParameterClient.this.getCurrentDataObject().getValueShadowListener().propertyChange(e.getEvent());
				changeListeners.clearServerParameterChanged();
			}
		}
	}

	/**
	 * To be called from server changes only!
	 * @param values
	 */
	public void setValues(List values)
	{
		synchronized (changeListeners) {
			changeListeners.setServerParameterChanged();
            List oldValues = InterfaceParameterClient.this.getCurrentDataObject().getValues();
            InterfaceParameterClient.this.getCurrentDataObject().setValues(values);
			changeListeners.clearServerParameterChanged();

            /* notifies listenters */
            ParameterValueChangeEvent valuesChangeEvent = new ParameterValueChangeEvent(this, oldValues, values);
            synchronized (parameterValueChangeListeners) {
                for (Iterator i = parameterValueChangeListeners.iterator(); i.hasNext(); ) {
                    ((ParameterValueChangeListener) i.next()).valueChanged(valuesChangeEvent);
                }
            }
		}
	}

	public void setValueStatus(int changedId, String status)
	{
		if (changedId > lastChangedId) {
			lastChangedId = changedId;
			String oldStatus = getValueStatus();
            setValueStatus(status);

            /* notifies listenters */
            ParameterStatusChangeEvent runtimeStatusChangeEvent = new ParameterStatusChangeEvent(this, oldStatus, status);
            synchronized (parameterStatusChangeListeners) {
                for (Iterator i = parameterStatusChangeListeners.iterator(); i.hasNext(); ) {
                    ((ParameterStatusChangeListener) i.next()).statusChanged(runtimeStatusChangeEvent);
                }
            }
		}
	}

    /**
     * add PropertyChangeListener who want to be notified when the value of this parameter changes
     */
    public void addParameterValueChangeListener(ParameterValueChangeListener listener) {
        if (! parameterValueChangeListeners.contains(listener)) {
            parameterValueChangeListeners.add(listener);
        }
        //System.out.println("size of value change listener: " + parameterValueChangeListeners.size());
    }

    /**
     * add PropertyChangeListener who want to be notified when the status(=color of param) of this parameter changes
     */
    public void addParameterStatusChangeListener(ParameterStatusChangeListener listener) {
        if (! parameterStatusChangeListeners.contains(listener)) {
            parameterStatusChangeListeners.add(listener);
        }
        //System.out.println("size of status change listener: " + parameterStatusChangeListeners.size());
    }

    /**
     * remove PropertyChangeListener who no more want to be notified when the value of this parameter changes
     */
    public void removeParameterValueChangeListener(ParameterValueChangeListener listener) {
        parameterValueChangeListeners.remove(listener);
    }

    /**
     * remove PropertyChangeListener who no more want to be notified when the status(=color of param) of this parameter changes
     */
    public void removeParameterStatusChangeListener(ParameterStatusChangeListener listener) {
        parameterStatusChangeListeners.remove(listener);
    }

    /**
     * remove all (=clear) PropertyChangeListener
     */
    public void clearParameterValueChangeListener() {
        synchronized (parameterValueChangeListeners) {
            parameterValueChangeListeners.clear();
        }
    }

    /**
     * remove all (=clear) PropertyChangeListener
     */
    public void clearParameterStatusChangeListener() {
        synchronized (parameterStatusChangeListeners) {
            parameterStatusChangeListeners.clear();
        }
    }
}
