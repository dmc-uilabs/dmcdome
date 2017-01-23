// AbstractModelObjectScope.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.dataobject.DocumentationData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeCollectionUtils;
import mit.cadlab.dome3.objectmodel.exceptions.CopyFailedException;
import mit.cadlab.dome3.objectmodel.exceptions.DuplicateContentException;
import mit.cadlab.dome3.objectmodel.exceptions.DuplicateIdException;
import mit.cadlab.dome3.objectmodel.exceptions.IllegalObjectModelOperationException;
import mit.cadlab.dome3.objectmodel.exceptions.IllegalTypeException;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.MultipleErrorsException;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of a model as a collection of ModelObjects.
 * Provides DeletionListener support.
 * Subclasses must implement:
 * protected ModelObjectFactory getModelObjectFactory();
 */
public abstract class AbstractModelObjectScope extends AbstractDomeObject implements ModelObjectScope
{

	protected static final String GENERIC = "Generic";

	protected static final String NON_MODELOBJECT = "non-ModelObject";
	protected static final String NON_PARAMETER = "non-Parameter";

	protected DArrayList modelObjects;
	protected transient HashMap modelObjectsById;
	protected Documentation doc;
	protected DeletionListener deletionListener = new DeletionListener()
	{
		public void objectDeleted(DeletionEvent e)
		{
			objectDeletedAction((ModelObject) e.getSource());
		}
	};

	public AbstractModelObjectScope(Id id)
	{
		super(id);
		init();
		doc = new DocumentationData();
	}

	public AbstractModelObjectScope(Id id, ModelObjectScope mObjScope)
	{
		this(id, mObjScope, true);
	}

	public AbstractModelObjectScope(Id id, ModelObjectScope mObjScope, boolean copyObjects)
	{
		super(id, mObjScope);
		init();
		if (copyObjects)
			copyModelObjects(mObjScope.getModelObjects());
		doc = new DocumentationData(mObjScope.getDocumentation());
	}

	public AbstractModelObjectScope(Element xmlElement)
	{
		super(xmlElement);
		init();
		Element docElement = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/documentation");
		if (docElement == null)
			doc = new DocumentationData(); // need to get mode version of this!
		else
			doc = new DocumentationData(docElement);
	}

	private void init()
	{
		modelObjects = createModelObjectList();
		modelObjectsById = new HashMap();
	}

	protected Id getNextId()
	{
		return new Id(UUIDGenerator.create());
	}

	protected HashMap copyModelObjects(Collection origModelObjects)
	{
		HashMap idMap = new HashMap(); // key = oldId, value = newObj
		List contexts = new ArrayList();
		Iterator it = origModelObjects.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			ModelObject origModelObject = (ModelObject) obj;
			ModelObject newModelObject = null;
			if (obj instanceof Context) {
				if (((Context) obj).getId().equals(DomeModelInterface.BUILD_CONTEXT_ID)) {
					continue;
				}
				if (((Context) obj).getId().equals(DomeModelInterface.MODEL_CONTEXT_ID)) {
					continue;
				}
				newModelObject = this.newModelObject("Context");
				newModelObject.setName(origModelObject.getName());
				contexts.add(obj);
			}
			else if(!DomeCollectionUtils.isCollectionParameter(origModelObject)){
				//do not copy params inside collections such as DomeList as that creates duplicate objects.
				newModelObject = this.newModelObject(origModelObject);
			}
			if(newModelObject != null) {
				idMap.put(origModelObject.getId(), newModelObject);
			}
		}
		// copy contexts, map old items to new items
		Iterator cit1 = contexts.iterator();
		while (cit1.hasNext()) {
			Context origCxt = (Context) cit1.next();
			Context dupCxt = (Context) idMap.get(origCxt.getId());
			Iterator contents = origCxt.getModelObjectReferences().iterator();
			while (contents.hasNext()) {
				ModelObject mObj = (ModelObject) contents.next();
				dupCxt.addModelObjectReference((ModelObject) idMap.get(mObj.getId()));
			}
		}
		return idMap; // so subclasses can do something
	}

	/**
	 * Subclasses can override this method to return custom DList.
	 */
	protected DArrayList createModelObjectList()
	{
		return new ModelObjectList();
	}

	public Documentation getDocumentation()
	{
		return doc;
	}

	/**
	 * Gets modelobject with specified id
	 * Returns null if object not found.
	 */
	public ModelObject getModelObjectById(Id modelObjectId)
	{
		return (ModelObject) modelObjectsById.get(modelObjectId);
	}

	public int countModelObjects()
	{
		return modelObjects.size();
	}

	public Collection getModelObjects()
	{
		return Collections.unmodifiableList(modelObjects);
	}

	/*
	* ModelObject creation methods can throw exceptions for following reasons:
	* from modelObjectFactory (object creation)
	* null result from modelObjectFactory
	* exception adding to list - BeforeHookException/AfterHookException
	*   or other type (if insertion)
	*/

	public ModelObject newModelObject(String modelObjectType)
	{
		if (isValidModelObjectType(modelObjectType)) {
			ModelObject mObj = null;
			try {
				Object[] ctrParams = new Object[]{this, getNextId()};
				mObj = getModelObjectFactory().newInstance(modelObjectType, ctrParams);
				if (mObj == null)
					throw new DomeObjectException("newModelObject", "object is null");
				if (!modelObjects.add(mObj)) // add failed
					return null;
			} catch (Exception ex) {
				ex.printStackTrace();
				if (!(ex instanceof DArrayList.AfterHookException))
					mObj = null; // remind me why?
				throw new DomeObjectException("newModelObject", ex, mObj);
			}
			return mObj;
		} else { // throw exception for invalid type
			throw new DomeObjectException("newModelObject", "invalid type: " + modelObjectType);
		}
	}

	public boolean isValidModelObjectType(String modelObjectType)
	{
		return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter")
		        || modelObjectType.equals("Context") || modelObjectType.equals("Procedural Relation") || modelObjectType.equals("Equal Relation") || modelObjectType.equals("Visualization")|| modelObjectType.equals("Iteration Relation");
	}

	public boolean isValidModelObjectType(ModelObject modelObject)
	{
		return true;
	}

	public ModelObject newModelObject(ModelObject modelObject)
	{
		return newModelObject(modelObject, false);
	}

	public ModelObject newModelObject(ModelObject modelObject, boolean deepCopy)
	{
		if (isValidModelObjectType(modelObject)) {
			ModelObject mObj = null;
			if ((modelObject instanceof Context) && deepCopy) {
				mObj = deepCopyContext((Context) modelObject);
				if (mObj == null)
					throw new DomeObjectException("newModelObject", "object is null");
			} else {
				try {
					Object[] ctrParams = new Object[]{this, getNextId(), modelObject};
					mObj = getModelObjectFactory().newInstance(modelObject, ctrParams);
					if (mObj == null)
						throw new DomeObjectException("newModelObject", "object is null");
					if (!modelObjects.add(mObj)) // add failed
						return null;
				} catch (Exception ex) {
					if (!(ex instanceof DArrayList.AfterHookException))
						mObj = null;
					ex.printStackTrace();
					throw new DomeObjectException("newModelObject", ex, mObj);
				}
			}
			return mObj;
		} else { // throw exception for invalid type
			throw new DomeObjectException("newModelObject", "invalid type: " + modelObject);
		}
	}

	public Collection newModelObjects(Collection mObjs)
	{
		return newModelObjects(mObjs, false);
	}

	public Collection newModelObjects(Collection mObjs, boolean deepCopy)
	{
		Object[] copyResult = createModelObjectCopies(mObjs, deepCopy);
		List copies = (List) copyResult[0];
		DArrayList errors = new DArrayList((List) copyResult[1]);
		boolean copiesAdded = ((Boolean) copyResult[2]).booleanValue();
		if (!copiesAdded)
		{
			try
			{
				modelObjects.addAll(copies);
			}
			catch (MultipleErrorsException ex)
			{
				errors.addAll(ex.getErrorList());
			}
			catch (Exception ex)
			{
				errors.add(ex);
			}
		}
		if (errors.size() == 1)
			throw new DomeObjectException("newModelObjects", (Exception) errors.get(0), copies);
		else if (errors.size() > 1)
			throw new DomeObjectException("newModelObjects", new MultipleErrorsException(errors), copies);
		return copies;
	}

	public void delete(DeletionListener notifier)
	{
		super.delete(notifier);
		deleteAllModelObjects();
	}

	public void deleteModelObject(ModelObject mObj)
	{
		try {
			if (mObj != null) {
				mObj.delete(null);
			}
		} catch (Exception ex) {
			throw new DomeObjectException("deleteModelObject", ex);
		}
	}

	public void deleteModelObjects(Collection mObjs)
	{
		if (mObjs == null)
			return;
		List errors = new ArrayList();
		ModelObject[] mObjsArray = (ModelObject[]) mObjs.toArray(new ModelObject[]{});
		for (int i = mObjsArray.length - 1; i >= 0; --i) {
			try {
                mObjsArray[i].delete(null);
			} catch (Exception ex) {
				errors.add(ex);
			}
		}
		if (errors.size() == 1)
			throw new DomeObjectException("deleteModelObjects", (Exception) errors.get(0));
		else if (errors.size() > 1)
			throw new DomeObjectException("deleteModelObjects", new MultipleErrorsException(errors));
	}

	public void deleteAllModelObjects()
	{
		deleteModelObjects(modelObjects);
	}

	public void addModelObjectsListener(DListListener listener)
	{
		modelObjects.addDListListener(listener);
	}

	public void removeModelObjectsListener(DListListener listener)
	{
		modelObjects.removeDListListener(listener);
	}

	protected boolean validateAddModelObjectConditions(Object obj)
	{
		if (obj == null) return false;
		if (!(obj instanceof ModelObject))
			throw new IllegalTypeException(this, NON_MODELOBJECT);
		ModelObjectScope scope = ((ModelObject) obj).getScope();
		if (!scope.equals(this))
			throw new IllegalObjectModelOperationException("ModelObject's Model is not this Model", this, obj);
		if (modelObjects.contains(obj)) {
			System.err.println("duplicate content: " + obj);
			throw new DuplicateContentException("ModelObject already exists in Model", this, obj);
		}
		if (modelObjectsById.containsKey(((ModelObject) obj).getId())) {
			System.err.println("duplicate id: " + obj);
			throw new DuplicateIdException("ModelObject id already exists in Model", this, obj);
		}
		return true;
	}

	protected boolean validateDeleteModelObjectConditions(Object obj)
	{
		if (obj == null) return false;
		return true;
	}

	protected void objectDeletedAction(ModelObject mObj)
	{
		try {
			modelObjects.remove(mObj);
		} catch (Exception ex) {
			throw new DomeObjectException("objectDeletedAction", ex);
		}
	}

	public String toString()
	{
		String docString = getDocumentation().toString();
		String contentString = contentToString();
		return super.toString() + (contentString.equals("") ? "" : "\n" + contentString) +
		        (docString.equals("") ? "" : "\n" + docString);
	}

	protected abstract String contentToString();

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		addXmlContent(xml);
		if (!doc.isEmpty())
			xml.add(doc.toXmlElement());
		return xml;
	}

	protected abstract void addXmlContent(Element xmlElement);

	protected class ModelObjectList extends DArrayList
	{
		// subclasses should add behavior in the addHookAfter/removeHookAfter methods
		protected boolean addHookBefore(Object obj)
		{
			if (validateAddModelObjectConditions(obj)) {
				((ModelObject) obj).addDeletionListener(deletionListener);
				return true;
			}
			return false;
		}

		protected void addHookAfter(Object obj)
		{
			modelObjectsById.put(((ModelObject) obj).getId(), obj);
		}

		protected boolean removeHookBefore(Object obj)
		{
			return validateDeleteModelObjectConditions(obj);
		}

		protected void removeHookAfter(Object obj)
		{
            modelObjectsById.remove(((ModelObject) obj).getId());
		}

	}

	/**
	 * Result is an array of two Objects
	 * Object[0] is List of copies; always non-null; may be empty
	 * Object[1] is List of CopyFailedErrors generated during copying
	 *   process; always non-null; may be empty
	 * Copies are assumed to be valid for model...ModelObjectFactory should guarantee that.
	 */
	protected Object[] createModelObjectCopies(Collection mObjs)
	{
		return createModelObjectCopies(mObjs, false);
	}

	protected Object[] createModelObjectCopies(Collection mObjs, boolean deepCopy)
	{
		List copies = new ArrayList();
		List copyErrors = new ArrayList();
		//deep copied objects are already added to "modelObjects" list
		//so there is no need to add again, actually that throws an exception
		Boolean copiesAdded = new Boolean(deepCopy);

		Iterator it = mObjs.iterator();
		while (it.hasNext())
		{
			Object obj = it.next();
			try
			{
				if ((obj instanceof Context) && deepCopy)
				{
					Object mObj = deepCopyContext((Context) obj);
					if (mObj == null)
						throw new NullPointerException("copy is null");
					else
						copies.add(mObj);
				}
				else if (obj instanceof ModelObject)
				{
					ModelObject origObj = (ModelObject) obj;
					Object[] ctrParams = new Object[]{this, getNextId(), origObj};
					ModelObject mObj = getModelObjectFactory().newInstance(origObj, ctrParams);
					if (mObj == null)
						throw new NullPointerException("copy is null");
					else
					{
						copies.add(mObj);
						if (deepCopy)
						{
							if (!modelObjects.add(mObj))
							{
								// add failed
								throw new DomeObjectException("createModelObjectCopies", "add to modelobjects list failed");
							}
						}
					}
				}
				else
				{
					throw new IllegalTypeException(this, NON_MODELOBJECT);
				}
			}
			catch (Exception ex)
			{
				copyErrors.add(new CopyFailedException(ex, this, obj));
			}
		}
		return new Object[]{copies, copyErrors, copiesAdded};
	}

	protected Context deepCopyContext(Context cxt)
	{
		return (Context)deepCopyContext(cxt, false);
	}

	/**
	 * Copys the context and returns the idmap (oldId-->newObject)
	 * @param cxt the context to deep copy
	 * @param returnIdMap true if want to have id map returned
	 * @return Object[]{newContext, idMap} if returnIdMap is true;
	 *   otherwise, returns newContext
	 */
	protected Object deepCopyContext(Context cxt, boolean returnIdMap) {
		Context newContext = (Context) this.newModelObject("Context");
		newContext.setName(cxt.getName());
		Collection flattenedContents = cxt.getFlattenedContentSet();
		HashMap idMap = copyContextObjects(flattenedContents);
		Iterator contents = cxt.getModelObjectReferences().iterator();
		while (contents.hasNext()) {
			ModelObject mObj = (ModelObject) contents.next();
			ModelObject newObj = (ModelObject) idMap.get(mObj.getId());
			newContext.addModelObjectReference(newObj);
		}
		if (returnIdMap)
			return new Object[]{newContext, idMap};
		else
			return newContext;
	}

	protected HashMap copyContextObjects(Collection contextObjects)
	{
		HashMap idMap = new HashMap(); // key = oldId, value = newObj
		List contexts = new ArrayList();
		Iterator it = contextObjects.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			ModelObject origModelObject = (ModelObject) obj;
			ModelObject newModelObject;
			if (obj instanceof Context) {
				newModelObject = this.newModelObject("Context");
				newModelObject.setName(origModelObject.getName());
				contexts.add(obj);
			} else {
				newModelObject = this.newModelObject(origModelObject);
			}
			idMap.put(origModelObject.getId(), newModelObject);
		}
		// copy contexts, map old items to new items
		Iterator cit1 = contexts.iterator();
		while (cit1.hasNext()) {
			Context origCxt = (Context) cit1.next();
			Context dupCxt = (Context) idMap.get(origCxt.getId());
			Iterator contents = origCxt.getModelObjectReferences().iterator();
			while (contents.hasNext()) {
				ModelObject mObj = (ModelObject) contents.next();
				dupCxt.addModelObjectReference((ModelObject) idMap.get(mObj.getId()));
			}
		}
		return idMap; // so subclasses can do something
	}

	protected abstract class AbstractInternalCausalityManager extends AbstractCausalityManager
	{
		public AbstractInternalCausalityManager()
		{
			super(AbstractModelObjectScope.this);
			modelObjects.addDListListener(createModelObjectsListListener());
		}

		public AbstractInternalCausalityManager(Element xmlElement)
		{
			super(AbstractModelObjectScope.this, xmlElement);
			modelObjects.addDListListener(createModelObjectsListListener());
		}

		protected DListListener createModelObjectsListListener()
		{
			return new ModelScopeModelObjectsListListener();
		}


		public class ModelScopeModelObjectsListListener implements DListListener
		{
			public void intervalChanged(DListEvent e)
			{
			}

			public void intervalAdded(DListEvent e)
			{
				addItems(e.getItems());
 			}

			public void intervalRemoved(DListEvent e)
			{
				removeItems(e.getItems());
			}

			public void itemsRemoved(DListEvent e)
			{
				removeItems(e.getItems());
			}

			public void itemsReplaced(DListEvent e)
			{
			}

			protected void addItems(List items)
			{
				Iterator it = items.iterator();
				while (it.hasNext()) {
					Object obj = it.next();
					if (obj instanceof Parameter)
						addObject(obj);
				}
			}

			protected void removeItems(List items)
			{
				Iterator it = items.iterator();
				while (it.hasNext()) {
					Object obj = it.next();
					if (obj instanceof Parameter)
						removeObject(obj);
				}
			}
		}
	}
}