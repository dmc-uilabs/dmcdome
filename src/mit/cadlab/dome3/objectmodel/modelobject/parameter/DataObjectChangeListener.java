// DataObjectChangeListener.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import java.util.EventListener;

public abstract class DataObjectChangeListener implements EventListener
{

	private Object listenerSource;

	protected DataObjectChangeListener(Object listenerSource)
	{
		this.listenerSource = listenerSource;
	}

	public Object getListenerSource()
	{
		return listenerSource;
	}

	public abstract void dataObjectChanged(DataObjectChangeEvent e);

}

