package mit.cadlab.dome3.objectmodel.util.mapping;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 4, 2003
 * Time: 11:09:57 AM
 * To change this template use Options | File Templates.
 */

public class VisualizationMapping extends Mapping
{

	public VisualizationMapping(Visualization p)
	{
		super(p);
	}

	public VisualizationMapping(Visualization p, Collection mappings)
	{
		this(p);
		addMappings(mappings);
	}

	public boolean addMapping(Visualization p)
	{
		return super.addMapping(p);
	}

	public boolean removeMapping(Visualization p)
	{
		return super.removeMapping(p);
	}

	public Collection addMappings(Collection vises)
	{
		return super.addMappings(vises, Visualization.class);
	}

	public Collection removeMappings(Collection vises)
	{
		return super.removeMappings(vises, Visualization.class);
	}

	public Visualization getVisualization()
	{
		return (Visualization) super.getMappedObject();
	}

	public boolean isMappedTo(Visualization p)
	{
		return super.isMappedTo(p);
	}

}
