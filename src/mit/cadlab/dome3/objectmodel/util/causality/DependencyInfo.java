// DependencyInfo.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.causality;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Dependency information for a set of objects
 * objects are associated with list of objects they are dependent on (driven by)
 * supports non-DomeObjects for testing purposes only
 */
public class DependencyInfo implements XMLSupport
{
	public static String XML_TAG = "dependencies";

	protected HashMap dependencies = new HashMap(); // key is output, value is List
	protected boolean validated = false;

	// following variables are created when information is validated (made consistent)
	protected DSet nodes;
	protected HashMap adjacencyList;
	protected List independents, intermediates, results;
	protected HashMap objCausalities;
	protected List loops;

	public DependencyInfo()
	{
	}

	/**
	 * Duplicate a dependency info structure using copies of the node set.
	 * The nodeMap structure defines the mappings between the old and new
	 * set of dome objects implicated in the dependencies.
	 * @param dt Old dependency info structure
	 * @param nodeMap Mapping from old dome objects to copies of those objects
	 */
	public DependencyInfo(DependencyInfo dt, HashMap nodeMap)
	{
		// nodeMap is old DomeObject node id->new node
		// or old object->new object (if old object not a DomeObject)
		this.dependencies = new HashMap();
		Iterator keys = dt.getDependencyKeys().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			Object newKey = nodeMap.get(((DomeObject) key).getId());
			Iterator deps = dt.getDependentsForObject(key).iterator();
			List newDeps = new ArrayList();
			while (deps.hasNext()) {
				Object dependent = deps.next();
				if (dependent instanceof DomeObject)
					dependent = ((DomeObject) dependent).getId();
				newDeps.add(nodeMap.get(dependent));
			}
			this.dependencies.put(newKey, newDeps);
		}
	}

	public DependencyInfo(List nodes)
	{
		this.nodes = new DSet(nodes);
	}

	//copy constructor
	/**
	 * Copies the given causality info and sets the newNodes as the node list
	 * also removes dependencies for any nodes that were removed
	 */
	public DependencyInfo(DependencyInfo dt, List newNodes, boolean toRemove)
	{
		// copies the information
		this.nodes = new DSet(dt.getNodes());
		Iterator keys = dt.getDependencyKeys().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			this.dependencies.put(key, new ArrayList(dt.getDependentsForObject(key)));
		}
		if (toRemove) { //remove objects in newNodes from this
			//note here collection newNodes is removed from collection of
			//all the nodes
			Collection removedNodes = DSet.removeSet(this.nodes, newNodes);
			removeObjects(newNodes);
			//so remaining is a collection all nodes after removal is complete
			this.nodes = new DSet(removedNodes);
		} else {  //add objects in newNodes to this
			Collection addNodes = DSet.union(this.nodes, newNodes);
			this.nodes = new DSet(addNodes);
		}
		validated = false;
	}

	// only supports xml for DomeObjects; strings for everything else
	public DependencyInfo(ModelObjectScope scope, Element xmlElement)
	{
		String xmlTag = getXmlTag();
		if (xmlElement.getQName().getName().equals(xmlTag)) {
			Iterator xmlDependencies = xmlElement.elements().iterator();
			while (xmlDependencies.hasNext()) {
				createDependencyFromXml(scope, (Element) xmlDependencies.next());
			}
		} else {
			throw new IllegalArgumentException("DependencyInfo - illegal xmlElement: " + xmlElement.asXML());
		}
	}

	protected void createDependencyFromXml(ModelObjectScope scope, Element xmlDependency)
	{
		if (xmlDependency.getQName().getName().equals("dependency")) {
			List independObjRefs = xmlDependency.elements();
			String dependObjId = xmlDependency.attributeValue("idRef");
			Object dependObjRef;
			if (dependObjId == null) { // object
				Element objXml = (Element) independObjRefs.remove(0);
				dependObjRef = objXml.getText();
			} else {
				dependObjRef = scope.getModelObjectById(new Id(dependObjId));
			}
			List independObjs = new ArrayList();
			Iterator it = independObjRefs.iterator();
			while (it.hasNext()) {
				Object independObj = createInputVariable(scope, (Element) it.next());
				independObjs.add(independObj);
			}
			setDependency(dependObjRef, independObjs);
		} else {
			throw new IllegalArgumentException("DependencyInfo - illegal xmlDependency: " +
			                                   xmlDependency.asXML());
		}
	}

	protected Object createInputVariable(ModelObjectScope scope, Element xmlElement)
	{
		if (xmlElement.getQName().getName().equals("object")) {
			return xmlElement.getText();
		} else {
			String idRef = xmlElement.attributeValue("idRef");
			if (idRef == null)
				throw new IllegalArgumentException("DependencyInfo - illegal xmlInputObject: " +
				                                   xmlElement.asXML());
			return scope.getModelObjectById(new Id(idRef)); // error if null?
		}
	}

	public List getItems(CausalityStatus causality)
	{
		if (!validated) validate();
		if (CausalityStatus.INDEPENDENT.equals(causality))
			return independents;
		else if (CausalityStatus.INTERMEDIATE.equals(causality))
			return intermediates;
		else if (CausalityStatus.RESULT.equals(causality))
			return results;
		else
			return Collections.EMPTY_LIST;
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		if (!validated) validate();
		if (causality == null)
			return getCausality(obj) == null;
		return causality.equals(getCausality(obj));
	}

	public CausalityStatus getCausality(Object obj)
	{
		if (!validated) validate();
		return (CausalityStatus) objCausalities.get(obj);
	}

	public List getNodes()
	{
		if (!validated) validate();
		return Collections.unmodifiableList(nodes);
	}

	public List getOutputs()
	{
		if (intermediates == null) {
			if (results == null) {
				return Collections.EMPTY_LIST;
			} else
				return results;
		} else {
			if (results == null) {
				return intermediates;
			} else
				return new ArrayList(DSet.union(intermediates, results));
		}
	}

	public List getTriggers()
	{
		if (independents == null) {
			if (intermediates == null) {
				return Collections.EMPTY_LIST;
			} else
				return intermediates;
		} else {
			if (intermediates == null) {
				return independents;
			} else
				return new ArrayList(DSet.union(independents, intermediates));
		}
	}

	public boolean isEmpty()
	{
		return dependencies.isEmpty();
	}

	public Set getDependencyKeys()
	{
		return Collections.unmodifiableSet(dependencies.keySet());
	}

	public Collection getDependentsForObject(Object object)
	{
		Collection dependents = (Collection) dependencies.get(object);
        if(dependents==null) return null;
		return Collections.unmodifiableCollection(dependents);
	}

	public void setDependency(Object object, Collection objectDependencies)
	{
		if (object == null || objectDependencies == null)
			throw new IllegalArgumentException("DependencyCausalityInfo.setDependency - null parameter");
		if (objectDependencies.contains(object))
			throw new UnsupportedOperationException("Object can not depend on itself: " + object);
		if (dependencies.containsKey(object))
			throw new UnsupportedOperationException("DependencyCausalityInfo.setDependency - object already set in table: " + object);
		if (objectDependencies.isEmpty()) return; // no internalCausality to add
		dependencies.put(object, new ArrayList(objectDependencies));
		validated = false;
	}

    public void clearDependency(Object driven) {
        dependencies.remove(driven);
        validated = false;
    }

	public void addDependency(Object object, Object target)
	{
		Collection objDependencies = (Collection) dependencies.get(object);
		if (objDependencies == null) {
			objDependencies = new ArrayList();
			dependencies.put(object, objDependencies);
		} else if (objDependencies.contains(target)) {
			return; // already added
		}
		objDependencies.add(target);
		validated = false;
	}

	public void removeDependency(Object object, Object target)
	{
		Collection objDependencies = (Collection) dependencies.get(object);
		if (objDependencies == null || !objDependencies.contains(target)) return; // doesn't exist
		objDependencies.remove(target);
		if (objDependencies.isEmpty())
			dependencies.remove(object);
		validated = false;
	}

	public void removeObjects(Collection objects)
	{
		Iterator it = objects.iterator();
		while (it.hasNext())
			removeObject(it.next());
	}

	public void removeObject(Object object)
	{
		boolean changed = false;
		if (dependencies.remove(object) != null)
			changed = true;
		List keys = new ArrayList(dependencies.keySet()); // to avoid ConcurrentModificationException
		Iterator keysIterator = keys.iterator();
		while (keysIterator.hasNext()) {
			Object key = keysIterator.next();
			Collection keyDependencies = (Collection) dependencies.get(key);
			if (keyDependencies.remove(object) == true)
				changed = true;
			if (keyDependencies.isEmpty())
				dependencies.remove(key);
		}
		if (changed)
			validated = false;
	}

	public boolean isValidated()
	{
		return validated;
	}

	public void validate()
	{
		if (validated) return;
		createNodeAndAdjacencyList();
		calculateIO();
		List loops = new ArrayList();
		ArrayList nodesLeft = new ArrayList(intermediates);
		int numberNodes = nodesLeft.size();
		for (int i = 0; i < numberNodes - 1; ++i) {
			List loopStart = new ArrayList();
			loopStart.add(nodesLeft.remove(0));
			loops.addAll(findLoopsStartingWith(loopStart, nodesLeft));
		}
		this.loops = loops;
		validated = true;
		if (!loops.isEmpty()) {
			if (loops.size() == 1) {
				throw new LoopException((List) loops.get(0));
			}
			List exceptions = new ArrayList();
			for (int i = 0; i < loops.size(); ++i) {
				exceptions.add(new LoopException((List) loops.get(i)));
			}
			throw new MultipleErrorsException(exceptions);
		}
	}

	protected void createNodeAndAdjacencyList()
	{
		nodes = new DSet();
		adjacencyList = new HashMap();
		Iterator keys = dependencies.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			nodes.add(key);
			List keyDependents = (List) dependencies.get(key);
			Iterator dependents = keyDependents.iterator();
			while (dependents.hasNext()) {
				Object dependent = dependents.next();
				nodes.add(dependent);
				List dependentTargets = (List) adjacencyList.get(dependent);
				if (dependentTargets == null) {
					dependentTargets = new ArrayList();
					adjacencyList.put(dependent, dependentTargets);
				}
				dependentTargets.add(key);
			}
		}
	}

	protected void calculateIO()
	{
		// assume non-connected items are independents
		Collection x = dependencies.keySet();
		independents = new ArrayList(DSet.removeSet(nodes, dependencies.keySet()));
		intermediates = new ArrayList(DSet.intersection(dependencies.keySet(),
		                                                adjacencyList.keySet()));
		results = new ArrayList(DSet.removeSet(dependencies.keySet(), intermediates));
		objCausalities = new HashMap();
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (independents.contains(obj))
				objCausalities.put(obj, CausalityStatus.INDEPENDENT);
			else if (intermediates.contains(obj))
				objCausalities.put(obj, CausalityStatus.INTERMEDIATE);
			else if (results.contains(obj))
				objCausalities.put(obj, CausalityStatus.RESULT);
			else
				System.err.println("DependencyInfo.calculateIO error - unknown causality for " +
				                   Names.getNameId(obj));
		}
	}

	protected void printHashMap(HashMap table)
	{
		Iterator keys = table.keySet().iterator();
		while (keys.hasNext()) {
			Object obj = keys.next();
			System.out.println(obj + "\t" + table.get(obj));
		}
	}

	protected List findLoopsStartingWith(List loopStart, List nodesLeft)
	{
		List loops = new ArrayList();
		List nodesFromLastNode = (List) adjacencyList.get(loopStart.get(loopStart.size() - 1)); // guaranteed to be non-Empty
		if (nodesFromLastNode.contains(loopStart.get(0))) { // found loop
			loops.add(new ArrayList(loopStart));
		}
		Collection nextNodes = DSet.intersection(nodesFromLastNode, nodesLeft);
		Iterator it = nextNodes.iterator();
		while (it.hasNext()) {
			Object nextNode = it.next();
			if (loopStart.contains(nextNode))
				continue; // skip
			List newLoopStart = new ArrayList(loopStart);
			newLoopStart.add(nextNode);
			List newNodesLeft = new ArrayList(nodesLeft);
			newNodesLeft.remove(nextNode);
			loops.addAll(findLoopsStartingWith(newLoopStart, newNodesLeft));
		}
		return loops;
	}

	public void loadDependenciesFromArrays(Object[] keys, Object[][] targets)
	{
		if (keys == null || targets == null || keys.length != targets.length)
			return; // error!
		for (int i = 0; i < keys.length; ++i) {
			setDependency(keys[i], Arrays.asList(targets[i]));
		}
		validate();
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer("DependencyInfo: ");
		if (validated) {
			sb.append(Names.getNameIds(nodes));
		}
		Iterator keys = dependencies.keySet().iterator();
		while (keys.hasNext()) {
			Object obj = keys.next();
			sb.append("\n  " + Names.getNameId(obj) + "\t" + Names.getNameIds((Collection) dependencies.get(obj)));
		}
		if (validated) {
			sb.append("\nindependents: " + Names.getNameIds(independents));
			sb.append("\nintermediates: " + Names.getNameIds(intermediates));
			sb.append("\nresults: " + Names.getNameIds(results));
		} else {
			sb.append("\nnot consistent");
		}
		return sb.toString();
	}

	public String getXmlTag()
	{
		return DependencyInfo.XML_TAG;
	}

	public Element toXmlElement()
	{
		Element xml = DocumentHelper.createElement(getXmlTag());
		Iterator it = dependencies.keySet().iterator();
		while (it.hasNext()) {
			xml.add(dependencyToXmlElement(it.next()));
		}
		return xml;
	}

	protected Element dependencyToXmlElement(Object obj)
	{
		Collection objDependencies = (Collection) dependencies.get(obj);
		if (objDependencies == null)
			return null;
		Element xml = DocumentHelper.createElement("dependency");
		if (obj instanceof DomeObject)
			xml.addAttribute("idRef", ((DomeObject) obj).getId().getIdString());
		else
			xml.addElement("object").setText(obj.toString());
		Iterator it = objDependencies.iterator();
		while (it.hasNext()) {
			Object dep = it.next();
			if (dep instanceof DomeObject)
				xml.add(((DomeObject) dep).toXmlRef());
			else
				xml.addElement("object").setText(dep.toString());
		}
		return xml;
	}

	public static void main(String[] args)
	{
		String[] nodes = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
		String[] dependencyKeys = {"b", "c", "d", "f", "g", "h", "i", "k"};
		String[][] dependencies = {{"a"}, //b
		                           {"a", "i"}, //c
		                           {"b"}, //d
		                           {"c"}, //f
		                           {"c", "k"}, //g
		                           {"g"}, //h
		                           {"h"}, //i
		                           {"h"}  //k
		};
		DependencyInfo dt = new DependencyInfo();
		try {
			dt.loadDependenciesFromArrays(dependencyKeys, dependencies);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		System.out.println(dt);
		Element xml = dt.toXmlElement();
		XMLUtils.print(xml);
		DependencyInfo xmlDt = new DependencyInfo(null, xml);
		System.out.println(xmlDt);
	}

}
