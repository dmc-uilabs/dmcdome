// AbstractContext.java
package mit.cadlab.dome3.objectmodel.modelobject.context;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.exceptions.DuplicateContentException;
import mit.cadlab.dome3.objectmodel.exceptions.IllegalCrossModelPasteException;
import mit.cadlab.dome3.objectmodel.exceptions.IllegalRecursiveContextException;
import mit.cadlab.dome3.objectmodel.exceptions.IllegalTypeException;
import mit.cadlab.dome3.objectmodel.exceptions.NoReferenceException;
import mit.cadlab.dome3.objectmodel.modelobject.AbstractModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AbstractContext extends AbstractModelObject
        implements Context
{

	protected static String mObjsXmlTag = "modelobjects";
	protected static final int PROMPT_NO_REFERENCES = 0;
	protected static final int DELETE_NO_REFERENCES = 1;
	protected static final int KEEP_NO_REFERENCES = 2;
	protected static final int RECURSIVE_DELETE = 3;

	protected static final String COPY = "Copy";
	protected static final String NON_MODELOBJECT = "non-ModelObject";

	protected DArrayList modelObjectReferences;
	protected ModelObject.ModelReference modelReference = new AbstractModelObject.DefaultModelReference();
	protected int deleteType = PROMPT_NO_REFERENCES;
	protected transient Element modelObjectsElement = null;

	public AbstractContext(ModelObjectScope scope, Id id)
	{
		super(scope, id);
		init();
	}

	public AbstractContext(ModelObjectScope scope, Id id, Context context)
	{
		super(scope, id, context);
		init();
		if ((context.getModel() == null && this.getModel() != null) ||
		        (context.getModel() != null && !context.getModel().equals(this.getModel())))
			throw new AbstractDomeObject.DomeObjectException("CopyConstructor", "can not copy context into different model");
		List refs = context.getModelObjectReferences();
		modelObjectReferences.addAll(refs);
	}

	// todo: change code throughout DOME so that this constructor does not load references
	// Then the following constructor can be deleted/merged with this one.
	// That is, the default behavior should be that contexts are loaded from xml but do not load their references right away.
	public AbstractContext(ModelObjectScope scope, Element xmlElement)
	{
		this(scope, xmlElement, true);
	}

	public AbstractContext(ModelObjectScope scope, Element xmlElement, boolean loadReferences)
	{
		super(scope, xmlElement);
		init();
		String xmlTag = getXmlTag();
		if (xmlElement.getQName().getName().equals(xmlTag)) {
			modelObjectsElement = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + mObjsXmlTag);
			if (loadReferences)
			    loadReferencesFromXml();
		}
		else {
			throw new IllegalArgumentException(getTypeName() + " - illegal xmlElement: " + xmlElement.asXML());
		}
	}

	public synchronized void loadReferencesFromXml()
	{
		if (modelObjectsElement == null)
			return;
		List mObjRefNodes = modelObjectsElement.elements();
		Iterator it = mObjRefNodes.iterator();
		while (it.hasNext()) {
			Element mObjRefNode = (Element) it.next();
			ModelObjectScope scope = getScope();
			Id modelObjId = parseXmlRef(mObjRefNode);
			ModelObject mObj = scope.getModelObjectById(modelObjId);
			if (mObj == null)
				throw new IllegalArgumentException(getTypeName() + " - can't find: " + mObjRefNode.asXML());
			else
				modelObjectReferences.add(mObj);
		}
		modelObjectsElement = null;
	}

	private void init()
	{
		modelObjectReferences = createModelObjectReferencesList();
	}

	public boolean isEmpty()
	{
		return modelObjectReferences.isEmpty();
	}

	public void clear()
	{
		modelObjectReferences.clear();
	}

	public boolean containsReference(ModelObject mObj)
	{
		return modelObjectReferences.contains(mObj);
	}

	public Collection getFlattenedContentSet()
	{
		DSet flattenedContents = new DSet(modelObjectReferences);
		Iterator it = modelObjectReferences.iterator();
		while (it.hasNext()) {
			Object mObjRef = it.next();
			if (mObjRef instanceof Context) {
				flattenedContents.addAll(((Context) mObjRef).getFlattenedContentSet());
			}
		}
		return flattenedContents;
	}

	/**
	 * Subclasses can override this method to return custom DList.
	 */
	protected DArrayList createModelObjectReferencesList()
	{
		return new ModelObjectReferencesList();
	}

	// Context interface
	public List getModelObjectReferences()
	{
		return Collections.unmodifiableList(modelObjectReferences);
	}

	public boolean isContextDescendant(ModelObject mObj)
	{
		if (mObj.equals(this)) return true;
		if (modelObjectReferences.contains(mObj)) return true;
		synchronized (modelObjectReferences) {
			for (int i = 0; i < modelObjectReferences.size(); ++i) {
				Object obj = modelObjectReferences.get(i);
				if (obj instanceof Context) {
					boolean answer = ((Context) obj).isContextDescendant(mObj);
					if (answer) return answer;
				}
			}
		}
		return false;
	}

	public void addModelObjectReference(ModelObject mObj)
	{
		try {
			modelObjectReferences.add(mObj);
		} catch (Exception ex) {
			handleAddReferenceErrors(ex);
		}
	}


	public void addModelObjectReferences(Collection modelObjects)
	{
		try {
			modelObjectReferences.addAll(modelObjects);
		} catch (Exception ex) {
			handleAddReferenceErrors(ex);
		}
	}

	protected void handleAddReferenceErrors(Exception ex)
	{
		throw new AbstractDomeObject.DomeObjectException("addModelObjectReference", ex);
	}

	public void removeModelObjectReference(ModelObject mObj)
	{
		modelObjectReferences.remove(mObj);
	}

	public void removeModelObjectReferences(Collection modelObjects)
	{
		modelObjectReferences.removeAll(modelObjects);
	}

	public void addModelObjectReferencesListener(DListListener l)
	{
		modelObjectReferences.addDListListener(l);
	}

	public void removeModelObjectReferencesListener(DListListener l)
	{
		modelObjectReferences.removeDListListener(l);
	}

	protected boolean validateAddModelObjectConditions(Object obj)
	{
		if (obj == null) return false;
		if (!(obj instanceof ModelObject))
			throw new IllegalTypeException(this, NON_MODELOBJECT);
		ModelObject mObj = (ModelObject) obj;
		if (!((mObj.getModel() == null && getModel() == null) ||
		        (mObj.getModel() != null && getModel() != null && mObj.getModel().equals(getModel())))) {
			if ((mObj.getModel() instanceof Relation) && (mObj.getModel()).getModel().equals(getModel())) {
				// ok,
			} else {
				throw new IllegalCrossModelPasteException("ModelObject's Model is not this Context's Model", this, obj);
			}
		}
		if (modelObjectReferences.contains(obj))
			throw new DuplicateContentException("ModelObject already referenced in Context", this, obj);
		if (obj instanceof Context && ((Context) obj).isContextDescendant(this))
			throw new IllegalRecursiveContextException("ModelObject is parent of this Context", this, obj);
		return true;
	}

	protected boolean validateRemoveModelObjectConditions(Object obj)
	{
		if (obj == null) return false;
		return true;
	}

	// Destructor overriding
	public synchronized void delete(DeletionListener notifier)
	{
		if (!isDeleted) {
			super.delete(notifier); // should not throw any exceptions
			deleteType = PROMPT_NO_REFERENCES;
			modelObjectReferences.clear(); // remove references to modelObjects
			// any exceptions thrown are sent to caller
			// subclasses should process exceptions
		}
	}

	public synchronized void delete(DeletionListener notifier, boolean deleteNoReferenceObjects)
	{
		if (!isDeleted) {
			super.delete(notifier);
			if (deleteNoReferenceObjects)
				deleteType = DELETE_NO_REFERENCES;
			else
				deleteType = KEEP_NO_REFERENCES;
			try {
				modelObjectReferences.clear(); // remove references to modelObjects
			} catch (Exception ex) {
				throw new AbstractDomeObject.DomeObjectException("delete", ex);
			}
		}
	}

	public synchronized void recursiveDelete(DeletionListener notifier)
	{
		if (!isDeleted) {
			super.delete(notifier);
			deleteType = RECURSIVE_DELETE;
			try {
				modelObjectReferences.clear(); // delete modelObjects
			} catch (Exception ex) {
				throw new AbstractDomeObject.DomeObjectException("recursiveDelete", ex);
			}
		}
	}

	protected List getNoReferenceWarnings(MultipleErrorsException ex)
	{
		ArrayList noReferenceWarnings = new ArrayList();
		List errors = ex.getErrorList();
		for (int i = errors.size() - 1; i >= 0; --i) {
			Object obj = errors.get(i);
			if (obj instanceof MultipleErrorsException) {
				MultipleErrorsException meex = (MultipleErrorsException) obj;
				noReferenceWarnings.addAll(getNoReferenceWarnings(meex));
				if (meex.getErrorList().isEmpty())
					errors.remove(obj);
			} else if (obj instanceof DArrayList.AfterHookException) {
				DArrayList.AfterHookException ahex = (DArrayList.AfterHookException) obj;
				noReferenceWarnings.addAll(getNoReferenceWarnings(ahex));
				if (ahex.exception == null)
					errors.remove(obj);
			}
		}
		return noReferenceWarnings;
	}

	public List getNoReferenceWarnings(DArrayList.AfterHookException ex)
	{
		ArrayList noReferenceWarnings = new ArrayList();
		if (ex.exception instanceof NoReferenceException) {
			noReferenceWarnings.add(ex.exception);
			ex.exception = null;
		} else if (ex.exception instanceof MultipleErrorsException) {
			MultipleErrorsException meex = (MultipleErrorsException) ex.exception;
			noReferenceWarnings.addAll(getNoReferenceWarnings(meex));
			if (meex.getErrorList().isEmpty())
				ex.exception = null;
		}
		return noReferenceWarnings;
	}

	protected List getObjectsFromNoReferenceWarnings(List noReferenceWarnings)
	{
		List objs = new ArrayList();
		Iterator it = noReferenceWarnings.iterator();
		while (it.hasNext()) {
			objs.add(((NoReferenceException) it.next()).modelObject);
		}
		return objs;
	}

	protected void objectDeletedAction(ModelObject mObj)
	{
		removeModelObjectReference(mObj);
	}

	protected class ModelObjectReferencesList extends DArrayList
	{
		protected boolean addHookBefore(Object obj)
		{
			if (validateAddModelObjectConditions(obj)) {
				((ModelObject) obj).addDeletionListener(modelReference);
				return true;
			}
			return false;
		}

		protected boolean removeHookBefore(Object obj)
		{
			return validateRemoveModelObjectConditions(obj);
		}

		protected void removeHookAfter(Object obj)
		{
			switch (deleteType) {
				case RECURSIVE_DELETE:
					if (obj instanceof Context)
						((Context) obj).recursiveDelete(modelReference);
					else
						((ModelObject) obj).delete(modelReference);
					break;
				case KEEP_NO_REFERENCES:
					if (obj instanceof Context)
						try { // contexts with no references always deleted
							((ModelObject) obj).removeDeletionListener(modelReference);
						} catch (NoReferenceException ex) {
							((Context) obj).delete(null, false);
						}
					else
						((ModelObject) obj).removeDeletionListener(modelReference, false);
					break;
				case DELETE_NO_REFERENCES:
					if (obj instanceof Context)
						try {
							((ModelObject) obj).removeDeletionListener(modelReference);
						} catch (NoReferenceException ex) {
							((Context) obj).delete(null, true);
						}
					else
						((ModelObject) obj).removeDeletionListener(modelReference, true);
					break;
				default:
					((ModelObject) obj).removeDeletionListener(modelReference);
			}
		}

	}

	// DomeObject interface
	protected TypeInfo getTypeInfo()
	{
		return Context.TYPE_INFO;
	}

	protected String contentToString()
	{
		return "\n  modelObjRefs: " + Names.getNameIds(modelObjectReferences);
	}

	public String getXmlTag()
	{
		return Context.XML_TAG;
	}

	// XmlSupport interface
	public String getXmlMappedTag()
	{
		return Context.XML_MAPPED_TAG;
	}

	protected void addXmlContent(Element xmlElement)
	{
		XMLUtils.addCollectionRef(xmlElement, mObjsXmlTag, modelObjectReferences);
	}

}
