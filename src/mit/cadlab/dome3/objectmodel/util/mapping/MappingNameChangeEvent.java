// MappingNameChangeEvent.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.mapping;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.util.Names;

import java.util.EventObject;

public class MappingNameChangeEvent extends EventObject
{

	protected Parameter parameter;
    protected Visualization visualization;
//  protected Collection mappings;

	/**
	 * source is a MappingManager/Model/Relation
	 */
	public MappingNameChangeEvent(Object source, Parameter parameter) //, Collection mappings) {
	{
		super(source);
		if (parameter == null)
			throw new NullPointerException("MappingNameChangeEvent - null parameter");
		this.parameter = parameter;
//        this.mappings = mappings;
	}

	public MappingNameChangeEvent(Object source, Visualization vis) //, Collection mappings) {
	{
		super(source);
		if (vis == null)
			throw new NullPointerException("MappingNameChangeEvent - null visualization");
		this.visualization = vis;
//        this.mappings = mappings;
	}

	public Parameter getParameter()
	{
		return parameter;
	}

	public Visualization getVisualization()
	{
		return visualization;
	}

//    public Collection getMappings() {
//        return mappings;
//    }

	public String toString()
	{
		return "MappingNameChangeEvent from " + Names.getNameId(getSource()) +
		        " for " + Names.getNameId(parameter); // + ": " + Names.getNameIds(mappings);
	}

}
