// AbstractDomeObject.java
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DomeException;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * To do by subclasses:
 * public TypeId getTypeId();
 */
public abstract class AbstractDomeObject extends DomeJavaBean implements DomeObject
{

	//private static ExceptionHandler eh = new PrintExceptionHandler();
	private Id id;
	protected String name = "";
	protected List deletionListeners = new ArrayList();
	protected boolean isDeleted = false;

	protected AbstractDomeObject()
	{
		// for subclasses
	}

	public AbstractDomeObject(Id id)
	{
		this(id, (String) null);
	}

	public AbstractDomeObject(Id id, DomeObject dObj)
	{
		this(id, dObj.getName());
	}

	protected AbstractDomeObject(Id id, String name)
	{
		if (id == null)
			throw new DomeObjectException("constructor", "null id");
		this.id = id;
		if (name == null || name.trim().equals(""))
			setName(this.getTypeName());
		else
			setName(name);
	}

	public AbstractDomeObject(Element xmlElement)
	{
		if (xmlElement == null)
			throw new NullPointerException(getClass().getName() + " constructor - xmlElement is null!");
		XMLUtils.makeRootElement(xmlElement); // necessary to use XPath locally
		parseHeaderElement(xmlElement);
	}

	protected void parseHeaderElement(Element xmlElement)
	{
		if (xmlElement.getQName().getName().equals(getXmlTag())) {
			String type = xmlElement.attributeValue("type");
			if (type != null && !type.equals("") && !type.equals(getXmlType()))
				throw new IllegalArgumentException(getTypeName() + " - invalid xml type: " +
				                                   type);
			String id = xmlElement.attributeValue("id");
			if (id == null)
				throw new IllegalArgumentException(getTypeName() + " - no xml id");
			this.id = new Id(id);
			String name = xmlElement.attributeValue("name");
			if (name == null)
				throw new IllegalArgumentException(getTypeName() + " - no xml name");
			setName(name);
		} else {
			throw new IllegalArgumentException(getTypeName() + " - illegal xmlElement: " + xmlElement.asXML());
		}
	}

	// DomeObject interface

	protected abstract TypeInfo getTypeInfo();

	public String getTypeName()
	{
		return getTypeInfo().getTypeName();
	}

	public String getXmlType()
	{
		return getTypeInfo().getXmlType();
	}

	// RegistrySupport interface
	public String getRegistryKey()
	{
		String xmlType = getXmlType();
		if (xmlType.equals(""))
			return getXmlTag();
		else
			return getXmlTag() + "." + xmlType;
	}

	public DomeObject getDomeObject()
	{
		return this;
	}

	public Id getId()
	{
		return id;
	}

	/**
	 * This should be used by constructors of subclasses.
	 * An id of an object should never be changed!
	 */
	protected void setId(String idString)
	{
		this.id = new Id(idString);
	}

	//for model save as
	protected void changeId()
	{
		id = new Id(UUIDGenerator.create());
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (name == null || name.trim().equals("")) {
			return; // do not change
		}

		// Qing May 18: move this code elsewhere, like to the parameter and relation classes
		/*
		if (this instanceof Parameter && ((Parameter) this).getScope() instanceof Relation) {
			List pythonTypes = Registry.getPythonDataTypes();
			for (Iterator i = pythonTypes.iterator(); i.hasNext();) {
				if (name.equals(i.next())) {
					//pop up dialog
					Component comp = BuildFocusTracker.getCurrentComponent();
					String title = "Parameter Name Error: " + this.name;
					String msg = "Error: parameter name cannot be same as data type name " + name;
					OneButton1Msg.showError(comp, title, msg, "OK", OneButton1Msg.DEFAULT_SIZE, false);
					return;
				}
			}

			// check for duplicate name
			AbstractRelation rel = (AbstractRelation) ((Parameter) this).getScope();
			if (!rel.isValidParameterNameInRelation((ModelObject) this, name)) {
				//pop up dialog
				Component comp = BuildFocusTracker.getCurrentComponent();
				String title = "Parameter Name Error: " + name;
				String msg = "Error: the parameter name '" + name;
				msg += "' is already being used in the relation. Please use another name.";
				OneButton1Msg.showError(comp, title, msg, "OK", OneButton1Msg.DEFAULT_SIZE, false);
				return;
			}
		}
 */
		String oldName = this.name;
		this.name = name;
		firePropertyChange("name", oldName, this.name);
	}

	public String getNameIdString()
	{
		return getName() + "[" + getId() + "]";
	}

	// Destructor interface
	// Default implementation. Subclasses may want custom behavior.

	public void delete(DeletionListener notifier)
	{
		if (isDeleted) return; // can't delete twice
		isDeleted = true;
		removeDeletionListener(notifier);
		fireObjectDeleted();
	}

	public synchronized void addDeletionListener(DeletionListener l)
	{
		if (l == null) return;
		if (!deletionListeners.contains(l)) deletionListeners.add(l);
	}

	public synchronized void removeDeletionListener(DeletionListener l)
	{
		if (l == null) return;
		deletionListeners.remove(l);
	}

	protected synchronized void fireObjectDeleted()
	{
		if (deletionListeners.isEmpty()) return;
		DomeObjectDeletionEvent event = new DomeObjectDeletionEvent();
		// notify deletionListeners in reverse order
		ArrayList errors = new ArrayList();
		for (int i = deletionListeners.size() - 1; i >= 0; --i) {
			try {
				((DeletionListener) deletionListeners.get(i)).objectDeleted(event);
			} catch (Exception ex) {
				errors.add(ex);
			}
		}
		deletionListeners.clear();
		//if (errors.size() > 0)
		//eh.handleExceptions("fireObjectDeleted",errors);
	}

	public String toString()
	{
		return getNameIdString();
	}

	public Element headerToXmlElement()
	{
		Element xml = DocumentHelper.createElement(getXmlTag());
		String xmlType = getXmlType();
		if (xmlType != null && !xmlType.equals(""))
			xml.addAttribute("type", xmlType);
		xml.addAttribute("id", getId().getIdString());
		xml.addAttribute("name", name);
		return xml;
	}

	public Element toXmlElement()
	{
		return headerToXmlElement();
	}

	public Element toXmlRef()
	{
		Element xmlElement = DocumentHelper.createElement(getXmlTag());
		xmlElement.addAttribute("name", getName());
		xmlElement.addAttribute("idRef", getId().getIdString());

		return xmlElement;
	}

	public static Id parseXmlRef(Element xmlElement)
	{
		String idString = xmlElement.attributeValue("idRef");
		if (idString == null)
			throw new IllegalArgumentException("no xml idRef: " + xmlElement.asXML());
		return new Id(idString);
	}

	public static Id parseXmlModelRef(Element xmlElement)
	{
		String idString = xmlElement.attributeValue("idModelRef");
		if (idString == null)
			throw new IllegalArgumentException("no xml idModelRef: " + xmlElement.asXML());
		return new Id(idString);
	}

	// convenience class for invoking property change when list changes
	protected class PropertyDListListener implements DListListener
	{
		protected String property;

		public PropertyDListListener(String property)
		{
			this.property = property;
		}

		public void intervalChanged(DListEvent e)
		{
			firePropertyChange(property);
		}

		public void intervalAdded(DListEvent e)
		{
			firePropertyChange(property);
		}

		public void intervalRemoved(DListEvent e)
		{
			firePropertyChange(property);
		}

		public void itemsRemoved(DListEvent e)
		{
			firePropertyChange(property);
		}

		public void itemsReplaced(DListEvent e)
		{
			firePropertyChange(property);
		}

	}

	protected class DomeObjectDeletionEvent extends DeletionEvent
	{
		public DomeObjectDeletionEvent()
		{
			super(AbstractDomeObject.this);
		}

		public String toString()
		{
			return "DomeObjectDeletionEvent for " + ((AbstractDomeObject) getSource()).getNameIdString();
		}
	}

	public class DomeObjectException extends DomeException
	{

		public DomeObjectException()
		{
			super();
		}

		public DomeObjectException(String msg)
		{
			super(msg);
		}

		public DomeObjectException(String method, String msg)
		{
			super(AbstractDomeObject.this, method, msg);
		}

		public DomeObjectException(String method, Exception exception)
		{
			super(AbstractDomeObject.this, method, exception);
		}

		public DomeObjectException(String method, Exception exception, Object methodResult)
		{
			super(AbstractDomeObject.this, method, exception, methodResult);
		}

		public DomeObject getDomeObject()
		{
			return (DomeObject) getSource();
		}

	}

}
