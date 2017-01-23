// DataObjectChangeListener.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import java.beans.PropertyChangeEvent;
import java.util.EventObject;

public class DataObjectChangeEvent extends EventObject
{

	protected Parameter paramSource;
	protected Parameter paramCause;
	protected PropertyChangeEvent event;

	public DataObjectChangeEvent(Parameter source, PropertyChangeEvent event)
	{
		this(source, null, event);
	}

	public DataObjectChangeEvent(Parameter source, Parameter cause, PropertyChangeEvent event)
	{
		super(source);
		this.paramSource = source;
		this.paramCause = cause;
		this.event = event;
	}

	public Parameter getParameter()
	{
		return paramSource;
	}

	public Parameter getCause()
	{
		return paramCause;
	}

	public PropertyChangeEvent getEvent()
	{
		return event;
	}

	public String toString()
	{
		return "DataObjectChangeEvent from " + paramSource.getName() + ": " + event.getNewValue();
	}

}
