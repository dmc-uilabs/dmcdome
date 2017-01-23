// AbstractCausalityManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.DCollections;
import mit.cadlab.dome3.util.DomeException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractCausalityManager implements CausalityManager
{

	protected HashMap objectCausality; // key=object; value=CausalityStatus
	protected HashMap causalityLists; // key = CausalityStatus, value=list of objects
	protected CausalityChangeSupport causalityChangeListeners;

    public AbstractCausalityManager() { //for system causality view

    }

	public AbstractCausalityManager(ModelObjectScope m)
	{
		if (m == null)
			throw new IllegalArgumentException(ClassUtils.getClassName(this) + " - null ModelObjectScope");
		causalityChangeListeners = new CausalityChangeSupport(m);
		objectCausality = new HashMap();
		causalityLists = new HashMap();
	}

	public AbstractCausalityManager(ModelObjectScope m, Element xmlElement)
	{
		this(m);

		List paramList = xmlElement.selectNodes("parameter");
		for (Iterator iter = paramList.iterator(); iter.hasNext();) {
			// get model object
			Element paramElement = (Element) iter.next();
			Id paramId = AbstractDomeObject.parseXmlRef(paramElement);
			if (paramId == null) {
				throw new IllegalArgumentException("causality parameter has no id");
			}
			Parameter p = (Parameter) m.getModelObjectById(paramId);
			if (p == null) {
				throw new IllegalArgumentException("causality manager: parameter not found");
			}
			// get status and create the causality map
			CausalityStatus status = CausalityStatus.parseXml(paramElement);
			addObject(p, status);
		}
	}

	protected void addObject(Object obj)
	{
		addObject(obj, getInitialCausality(obj));
	}

	protected abstract CausalityStatus getInitialCausality(Object obj);

	protected void addObject(Object obj, CausalityStatus objCausality)
	{
		if (objCausality == null)
			objCausality = this.getInitialCausality(obj);
		if (objectCausality.containsKey(obj))
			throw new DomeException(this, "addObject", Names.getNameId(obj) + " already exists!");
		objectCausality.put(obj, objCausality);
		addToCausalityList(obj, objCausality);
		causalityChangeListeners.fireCausalityChange(obj, null, objCausality);
	}

	public void changeCausality(Object obj, CausalityStatus newCausality)
	{
		CausalityStatus oldCausality = (CausalityStatus) objectCausality.get(obj);
		if (oldCausality == null)
			return;
		if (newCausality == null)
			return; // no new information; keep it the same
		if (newCausality.equals(oldCausality))
			return; // no change
		objectCausality.put(obj, newCausality);
		removeFromCausalityList(obj, oldCausality);
		addToCausalityList(obj, newCausality);
		causalityChangeListeners.fireCausalityChange(obj, oldCausality, newCausality);
	}

	protected void removeObject(Object obj)
	{
		CausalityStatus causality = (CausalityStatus) objectCausality.remove(obj);
		if (causality == null) // object not here
			return;
		removeFromCausalityList(obj, causality);
		causalityChangeListeners.fireCausalityChange(obj, causality, null);
	}

	protected void addToCausalityList(Object obj, CausalityStatus objCausality)
	{
		List cList = (List) causalityLists.get(objCausality);
		if (cList == null) {
			cList = new ArrayList();
			causalityLists.put(objCausality, cList);
		}
		cList.add(obj);
	}

	protected void removeFromCausalityList(Object obj, CausalityStatus objCausality)
	{
		List cList = (List) causalityLists.get(objCausality);
		if (cList == null)
			return;
		cList.remove(obj);
		if (cList.isEmpty())
			causalityLists.remove(objCausality);
	}

	// CausalitySupport interface
	public List getItems(CausalityStatus causality)
	{
		List l = (List) causalityLists.get(causality);
		if (l == null)
			return Collections.EMPTY_LIST;
		return Collections.unmodifiableList(l);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		if (causality == null)
			return getCausality(obj) == null; // or should it always return false?
		return causality.equals(getCausality(obj));
	}

	public CausalityStatus getCausality(Object obj)
	{
		return (CausalityStatus) objectCausality.get(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		causalityChangeListeners.addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		causalityChangeListeners.removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		causalityChangeListeners.addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		causalityChangeListeners.removeCausalityChangeListener(obj, l);
	}

	// CausalityManager interface
	public List getIndependents()
	{
		return getItems(CausalityStatus.INDEPENDENT);
	}

	public List getIntermediates()
	{
		return getItems(CausalityStatus.INTERMEDIATE);
	}

	public List getResults()
	{
		return getItems(CausalityStatus.RESULT);
	}

	public List getIndeterminates()
	{
		return getItems(CausalityStatus.INDETERMINATE);
	}

	public List getDrivers()
	{ // Independents and Intermediates
		return DCollections.unmodifiableList(getIndependents(), getIntermediates());
	}

	public List getOutputs()
	{ // Intermediates and Results
		return DCollections.unmodifiableList(getIntermediates(), getResults());
	}

	public boolean isIndependent(Object obj)
	{
		return CausalityStatus.INDEPENDENT.equals(getCausality(obj));
	}

	public boolean isIntermediate(Object obj)
	{
		return CausalityStatus.INTERMEDIATE.equals(getCausality(obj));
	}

	public boolean isResult(Object obj)
	{
		return CausalityStatus.RESULT.equals(getCausality(obj));
	}

	public boolean isIndeterminate(Object obj)
	{
		return CausalityStatus.INDETERMINATE.equals(getCausality(obj));
	}

	public boolean isDriver(Object obj)
	{
		return isIndependent(obj) || isIntermediate(obj);
	}

	public boolean isOutput(Object obj)
	{
		return isIntermediate(obj) || isResult(obj);
	}

	/**
	 * Generate an XML description of the causality manager
	 * @return dom4j Element
	 */
	public Element toXmlElement()
	{
		Element causalityElement = DocumentHelper.createElement("causality");

		// generate the list of parameter causalities
		Set objectList = objectCausality.keySet();
		for (Iterator iter = objectList.iterator(); iter.hasNext();) {
			// get the parameter reference element
			Parameter p = (Parameter) iter.next();
			Element paramElement = p.toXmlMappedRef();
			// get the causality status
			CausalityStatus status = (CausalityStatus) objectCausality.get(p);
			paramElement.add(CausalityStatus.toXmlElement(status));
			// add to the list
			causalityElement.add(paramElement);
		}

		return causalityElement;
	}
}
