// ParameterMapping.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.mapping;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.Collection;


public class ParameterMapping extends Mapping
{

	public ParameterMapping(Parameter p)
	{
		super(p);
	}

	public ParameterMapping(Parameter p, Collection mappings)
	{
		this(p);
		addMappings(mappings);
	}

	public boolean addMapping(Parameter p)
	{
		return super.addMapping(p);
	}

	public boolean removeMapping(Parameter p)
	{
		return super.removeMapping(p);
	}

	public Collection addMappings(Collection params)
	{
		return super.addMappings(params, Parameter.class);
	}

	public Collection removeMappings(Collection params)
	{
		return super.removeMappings(params, Parameter.class);
	}

	public Parameter getParameter()
	{
		return (Parameter) super.getMappedObject();
	}

	public boolean isMappedTo(Parameter p)
	{
		return super.isMappedTo(p);
	}

}
