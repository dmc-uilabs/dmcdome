// MappingChangeEvent.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.mapping;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.util.Names;

import java.util.Collection;
import java.util.EventObject;

public class MappingChangeEvent extends EventObject
{

	protected Parameter parameter;
    protected Visualization visualization;
	protected Collection mappings;

	/**
	 * source is a MappingManager/Model/Relation
	 */
	public MappingChangeEvent(Object source, Parameter parameter, Collection mappings)
	{
		super(source);
		if (parameter == null)
			throw new NullPointerException("MappingChangeEvent - null parameter");
		this.parameter = parameter;
		this.mappings = mappings;
	}

	public MappingChangeEvent(Object source, Visualization visualization, Collection mappings)
	{
		super(source);
		if (visualization == null)
			throw new NullPointerException("MappingChangeEvent - null visualization");
		this.visualization = visualization;
		this.mappings = mappings;
	}

	public Parameter getParameter()
	{
		return parameter;
	}

	public Visualization getVisualization()
	{
		return visualization;
	}

	public Collection getMappings()
	{
		return mappings;
	}

	public String toString()
	{
		return "MappingChangeEvent from " + Names.getNameId(getSource()) +
		        " for " + Names.getNameId(parameter) + ": " + Names.getNameIds(mappings);
	}

}
