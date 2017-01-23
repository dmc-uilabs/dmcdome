// AbstractModelObject.java
package mit.cadlab.dome3.objectmodel.modelobject;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.dataobject.DocumentationData;
import mit.cadlab.dome3.objectmodel.exceptions.NoReferenceException;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.context.AbstractContext;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * An implementation of a ModelObject which keeps track of
 * model references within the deletion listeners.
 *
 * To do by subclasses:
 * public TypeId getTypeId();
 */
public abstract class AbstractModelObject extends AbstractDomeObject
        implements ModelObject
{

	protected ModelObjectScope scope;
	protected Documentation doc;
	protected int modelReferenceCount = 0;

	public AbstractModelObject(ModelObjectScope scope, Id id)
	{
		super(id);
		setScope(scope);
		doc = new DocumentationData();
	}

	public AbstractModelObject(ModelObjectScope scope, Id id, ModelObject mObj)
	{
		super(id, mObj);
		setScope(scope);
		doc = new DocumentationData(mObj.getDocumentation());
	}

	public AbstractModelObject(ModelObjectScope scope, Element xmlElement)
	{
		super(xmlElement); // loads type info
		setScope(scope);
		Element docElement = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/documentation");
		if (docElement == null)
			doc = new DocumentationData(); // need to get mode version of this!
		else
			doc = new DocumentationData(docElement);
	}

	// ModelComponent interface
	public Model getModel()
	{
		if (scope == null)
			return null;
		return scope.getModel();
	}

	// ModelObject interface
	public ModelObjectScope getScope()
	{
		return scope;
	}

	private void setScope(ModelObjectScope scope)
	{
		// of individual ModelObject guis
		//throw new NullPointerException("AbstractModelObject.setScope - null scope");
		if (this.scope != null)
			throw new AbstractDomeObject.DomeObjectException("setScope", "can not change scope of ModelObject!");
		this.scope = scope;
	}

	// DocumentationSupport interface
	public Documentation getDocumentation()
	{
		return doc;
	}

	// Destructor interface
	// Customized to keep track of listeners from within model (ModelReferences)

	public synchronized void addDeletionListener(DeletionListener l)
	{
		if (l == null) return;
		if (!deletionListeners.contains(l)) {
			deletionListeners.add(l);
			if (isSameModelReference(l)) // should be function insteac
				modelReferenceCount++;
		}
	}

	public synchronized void removeDeletionListener(DeletionListener l)
	        throws NoReferenceException
	{
		if (l == null) return;
		if (deletionListeners.remove(l)) {
			if (isSameModelReference(l)) {
				modelReferenceCount--;
				if (!isDeleted && modelReferenceCount == 0)
					throw new NoReferenceException(this);
			}
		}
	}

	protected boolean isSameModelReference(DeletionListener l)
	{
		if (getModel() == null) // no model for this object
			return false;
		if (l instanceof ModelObject.ModelReference) {
			return getModel().equals(((ModelObject.ModelReference) l).getModel());
		} else {
			return false;
		}
	}

	// ModelObject interface
	public synchronized void removeDeletionListener(DeletionListener l, boolean deleteIfNoReference)
	{
		if (deletionListeners.remove(l)) {
			if (l instanceof ModelObject.ModelReference) {
				modelReferenceCount--;
				if (!isDeleted && modelReferenceCount == 0 && deleteIfNoReference)
					delete(null);
			}
		}
	}

	protected synchronized void fireObjectDeleted()
	{
		super.fireObjectDeleted();
		modelReferenceCount = 0;
	}

	/**
	 * To be used by subclasses to process information about objects deleted.
	 */
	protected void objectDeletedAction(ModelObject mObj)
	{
	}

	/**
	 * Must be used by ModelObjects to create references to other
	 * ModelObjects.
	 */
	protected class DefaultModelReference implements ModelObject.ModelReference
	{
		public DefaultModelReference()
		{
		}

		public Model getModel()
		{
			return AbstractModelObject.this.getModel();
		}

		public void objectDeleted(DeletionEvent e)
		{
			objectDeletedAction((ModelObject) e.getSource());
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

	public String getXmlMappedTag()
	{
		return this.getXmlTag();
	}

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		addXmlContent(xml);
		if (!doc.isEmpty())
			xml.add(doc.toXmlElement());
		return xml;
	}

	public Element toXmlRef()
	{
		Element xmlElement = DocumentHelper.createElement(getXmlTag());
		createXmlRef(xmlElement);
		return xmlElement;
	}

	protected void createXmlRef(Element xmlElement)
	{
		xmlElement.addAttribute("name", getName());
		xmlElement.addAttribute("idRef", getId().getIdString());

		if (scope instanceof Relation)
			xmlElement.addAttribute("idRelationRef", scope.getId().toString());
		else if (scope instanceof Model)
			xmlElement.addAttribute("idModelRef", scope.getId().toString());
	}

	protected abstract void addXmlContent(Element xmlElement);

	public void delete(DeletionListener notifier) {
		if (getScope() != null && getScope() instanceof AbstractContext) {
			((AbstractContext) getScope()).removeModelObjectReference(this);
		}
		super.delete(notifier);
	}
}

