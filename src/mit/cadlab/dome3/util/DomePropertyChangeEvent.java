// DomePropertyChangeEvent.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.beans.PropertyChangeEvent;

/**
 * This subclass of PropertyChangeEvent allows events to be tagged with an eventId which
 * can be used to order events in time.
 */
public class DomePropertyChangeEvent extends PropertyChangeEvent
{
	private Integer eventId;

	public DomePropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue, Integer eventId)
	{
		super(source, propertyName, oldValue, newValue);
		this.eventId = eventId;
	}

	public Integer getEventId()
	{
		return eventId;
	}

}
