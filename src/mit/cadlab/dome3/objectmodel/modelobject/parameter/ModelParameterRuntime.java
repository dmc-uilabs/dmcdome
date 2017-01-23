// ModelParameterRuntime.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;

import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class ModelParameterRuntime extends ParameterRuntime
{
	protected ModelParameterRuntimeChangeSupport changeListeners = new ModelParameterRuntimeChangeSupport(this);
	protected DataObjectChangeListener ifaceParamListener = null;
	protected DataObjectChangeListener solvingQueueListener = null;

	public void delete(DeletionListener notifier) {
	    super.delete(notifier);

        changeListeners.clearInterfaceListener();
        ifaceParamListener = null; // this will make ifaceParamListener be gabage-collected to release its reference to this object
    }

	public ModelParameterRuntime(ModelObjectScope scope, Id id, String dataType)
	{
		super(scope, id, dataType);
	}

	public ModelParameterRuntime(ModelObjectScope scope, Id id)
	{
		super(scope, id);
	}

	public ModelParameterRuntime(ModelObjectScope scope, Id id, Parameter param)
	{
		super(scope, id, param);
	}

	public ModelParameterRuntime(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
	}

	protected ParameterRuntime.InternalDataObjectListener createInternalDataObjectListener()
	{
		return new ModelParameterInternalDataObjectChangeListener();
	}

	protected void initParameter()
	{
		super.initParameter();
		addPropertyChangeListener(Parameter.VALUE_STATUS, dataObjectListener);
	}

	/**
	 * @return listener for interface parameters
	 */
	public DataObjectChangeListener getInterfaceParameterListener()
	{
		if (ifaceParamListener == null)
			ifaceParamListener = new InterfaceParameterListener();
		return ifaceParamListener;
	}

	/**
	 * @return listener for the model solving queue
	 */
	public DataObjectChangeListener getSolvingQueueListener()
	{
		if (solvingQueueListener == null)
			solvingQueueListener = new SolvingQueueListener();
		return solvingQueueListener;
	}

	public void addModelQueueListener(DataObjectChangeListener mqListener)
	{
		changeListeners.addModelQueueListener(mqListener);
	}

	public void removeModelQueueListener()
	{
		changeListeners.removeModelQueueListener();
	}

	public void addSubscriptionInterfaceListener(DataObjectChangeListener mqListener)
	{
		changeListeners.addSubscriptionInterfaceListener(mqListener);
	}

	public void addChangeListener(DataObjectChangeListener listener)
	{
		changeListeners.addChangeListener(listener);
	}

	public void removeChangeListener(DataObjectChangeListener listener)
	{
		changeListeners.removeChangeListener(listener);
	}

	public void addInterfaceListener(DataObjectChangeListener listener)
	{
		changeListeners.addInterfaceListener(listener);
	}

	public void removeInterfaceListener(DataObjectChangeListener listener)
	{
		changeListeners.removeInterfaceListener(listener);
	}

	public void setValues(List args)
	{
		// from server
		if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(getValueStatus()) ||
				Parameter.VALUE_STATUS_CONSISTENT.equals(getValueStatus()))
		{
			if ((this.scope instanceof SubscriptionInterface) &&
					(CausalityStatus.INDEPENDENT.equals(((SubscriptionInterface) scope).getCausality(this))))
			{
				// shouldn't be getting new values unless perhaps from a resource interface
				// so process these changes instead of ignoring them, that is, skip the part in the else
				// change colors now, to try to avoid problems later where new values arrive before
				// parameter is marked inconsistent
				((DomeModelBase) this.getModel()).markAffectedParameters(this);
			}
			else
			{ // otherwise, warn and ignore, as before
				System.err.println("PARAMETER CHANGE IGNORED: [" + getName() + " (" + getValueStatus() + ")] of model \"" +
								   getModel().getName() + "\"");
				return;

			}
		}
		synchronized (changeListeners) {
			changeListeners.setServerParameterChanged();
			getCurrentDataObject().setValues(args);
			changeListeners.clearServerParameterChanged();
		}
		((ModelRuntime) getModel()).startModel();
	}

	/**
	 * For initialization from interfaces and subscriptions only
	 * @param dObj
	 */
	public void setInitialValue(DataObject dObj)
	{
		synchronized (changeListeners) {
			changeListeners.setChangeTrigger(this); // that way the value will be used in imodel solving
			getCurrentDataObject().setValues(dObj);
			changeListeners.clearChangeTrigger();
		}
	}

	protected class ModelParameterInternalDataObjectChangeListener extends InternalDataObjectListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			changeListeners.fireDataObjectChange(evt);
		}
	}

	protected class SolvingQueueListener extends DataObjectChangeListener
	{
		public SolvingQueueListener()
		{
			super(ModelParameterRuntime.this);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			synchronized (changeListeners) {
				changeListeners.setChangeTrigger(e.getParameter());
				getCurrentDataObject().getValueShadowListener().propertyChange(e.getEvent());
				changeListeners.clearChangeTrigger();
			}
		}
	}

	protected class InterfaceParameterListener extends DataObjectChangeListener
	{
		public InterfaceParameterListener()
		{
			super(ModelParameterRuntime.this);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			PropertyChangeEvent event = e.getEvent();
			if (Parameter.VALUE_STATUS.equals(event.getPropertyName())) {
				// do advanced notification of status to resource models
				if (Parameter.VALUE_STATUS_INCONSISTENT.equals(event.getNewValue())) {
					synchronized (changeListeners) {
						ModelParameterRuntime.this.setValueStatus(Parameter.VALUE_STATUS_INCONSISTENT);
					}
					((DomeModelBase)ModelParameterRuntime.this.getModel()).markAffectedParameters(ModelParameterRuntime.this);
				}
			} else {
				synchronized (changeListeners) {
					changeListeners.setChangeTrigger(e.getParameter());
					getCurrentDataObject().getValueShadowListener().propertyChange(event);
					changeListeners.clearChangeTrigger();
				}
			}
		}
	}

}
