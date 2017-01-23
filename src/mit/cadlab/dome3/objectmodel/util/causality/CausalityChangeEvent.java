// CausalityChangeEvent.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import mit.cadlab.dome3.objectmodel.DomeObject;

import java.util.EventObject;

public class CausalityChangeEvent extends EventObject
{

	protected Object parameter;
	protected CausalityStatus oldStatus, newStatus;

	/**
	 * source is a CausalityManager
	 */
	public CausalityChangeEvent(Object source, Object param, CausalityStatus oldStatus, CausalityStatus newStatus)
	{
		super(source);
		this.parameter = param;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}

	public Object getParameter()
	{
		return parameter;
	}

	public CausalityStatus getOldCausalityStatus()
	{
		return oldStatus;
	}

	public CausalityStatus getNewCausalityStatus()
	{
		return newStatus;
	}

	public String toString()
	{
		return "CausalityChangeEvent from " + getSource() + ": [" + oldStatus + " -> " + newStatus + "] for " +
		        ((parameter instanceof DomeObject) ? ((DomeObject) parameter).getNameIdString() : parameter.toString());
	}

}
