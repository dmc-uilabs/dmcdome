// ImmutableDependencyInfo.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import org.dom4j.Element;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Wrapper class for passing back dependencyInfo without creating new class.
 */
public class ImmutableDependencyInfo extends DependencyInfo
{

	private DependencyInfo dInfo;

	public ImmutableDependencyInfo(DependencyInfo dInfo)
	{
		this.dInfo = dInfo;
	}

	public List getItems(CausalityStatus causality)
	{
		return dInfo.getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return dInfo.isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return dInfo.getCausality(obj);
	}

	public List getNodes()
	{
		return dInfo.getNodes();
	}

	public List getOutputs()
	{
		return dInfo.getOutputs();
	}

	public List getTriggers()
	{
		return dInfo.getTriggers();
	}

	public boolean isEmpty()
	{
		return dInfo.isEmpty();
	}

	public Set getDependencyKeys()
	{
		return dInfo.getDependencyKeys();
	}

	public Collection getDependentsForObject(Object object)
	{
		return dInfo.getDependentsForObject(object);
	}

	public void setDependency(Object object, Collection objectDependencies)
	{
		throw new UnsupportedOperationException();
	}

	public void addDependency(Object object, Object target)
	{
		throw new UnsupportedOperationException();
	}

	public void removeDependency(Object object, Object target)
	{
		throw new UnsupportedOperationException();
	}

	public void removeObjects(Collection objects)
	{
		throw new UnsupportedOperationException();
	}

	public void removeObject(Object object)
	{
		throw new UnsupportedOperationException();
	}

	public boolean isValidated()
	{
		return dInfo.isValidated();
	}

	public void validate()
	{
		dInfo.validate();
	}

	public void loadDependenciesFromArrays(Object[] keys, Object[][] targets)
	{
		throw new UnsupportedOperationException();
	}

	public String toString()
	{
		return dInfo.toString();
	}

	public Element toXmlElement()
	{
		return dInfo.toXmlElement();
	}

    public DependencyInfo getMutableDependencyInfo() {
        return dInfo;
    }

}
