// AbstractGenericRelation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.relation;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.exceptions.NoReferenceException;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.ImmutableCausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;


public abstract class AbstractRelation extends AbstractModelObjectScope
        implements Relation
{

	protected CausalityManager internalCausalityManager;

	// ModelObject support
	protected ModelObjectScope scope;
	protected int modelReferenceCount = 0;

	public AbstractRelation(ModelObjectScope scope, Id id)
	{
		super(id);
		setScope(scope);
		internalCausalityManager = createInternalCausalityManager();
	}

	public AbstractRelation(ModelObjectScope scope, Id id, Relation relation)
	{
		super(id, relation, false);
		setScope(scope);
		internalCausalityManager = createInternalCausalityManager();
	}

	public AbstractRelation(ModelObjectScope scope, Element xmlElement)
	{
		super(xmlElement);
		setScope(scope);
		internalCausalityManager = createInternalCausalityManager();
	}

	// Relation interface
	protected abstract CausalityManager createInternalCausalityManager();

	public CausalityManager getCausalityManager()
	{
		return new ImmutableCausalityManager(internalCausalityManager);
	}

	// ModelComponent interface
	public Model getModel()
	{
		Model m = scope.getModel();
		return m;
	}

	// ModelObject interface
	public ModelObjectScope getScope()
	{
		return scope;
	}

	private void setScope(ModelObjectScope scope)
	{
		if (scope == null)
			System.out.println("Warning--modelObjectScope is null"); // do not enforce non-null scope for testing
		// of individual ModelObject guis
		//throw new NullPointerException("AbstractModelObject.setScope - null scope");
		if (this.scope != null)
			throw new AbstractDomeObject.DomeObjectException("setScope", "can not change scope of ModelObject!");
		this.scope = scope;
	}

	public ModelObjectFactory getModelObjectFactory()
	{
		return ((AbstractModelObjectScope) this.getScope()).getModelObjectFactory();
	}

	public boolean isValidModelObjectType(String modelObjectType)
	{
		return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter");
	}

	public boolean isValidModelObjectType(ModelObject modelObject)
	{
		return modelObject instanceof Parameter;
	}

	protected DArrayList createModelObjectList()
	{
		return new RelationObjectList();
	}

	// Destructor interface
	// Customized to keep track of listeners from within model (ModelReferences)

	public synchronized void addDeletionListener(DeletionListener l)
	{
		if (!deletionListeners.contains(l)) {
			deletionListeners.add(l);
			if (isSameModelReference(l)) // should be function insteac
				modelReferenceCount++;
		}
	}

	public synchronized void removeDeletionListener(DeletionListener l)
	        throws NoReferenceException
	{
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

	// ModelObject interface -- doesn't apply!
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

	protected class RelationObjectList extends AbstractModelObjectScope.ModelObjectList
	{
		protected void addHookAfter(Object obj)
		{
			super.addHookAfter(obj);
		}

		protected void removeHookAfter(Object obj)
		{
			super.removeHookAfter(obj);
			ModelObject mObj = (ModelObject) obj;
		}
	}

	protected static final char UNDERSCORE = '_';

	// fixes individual parameter name
	protected String fixParameterName(String name)
	{
		String validName = "";
		int errors = 0;
		if (name == null || name.length() == 0) {
			validName = "var";
		} else {
			StringBuffer goodName = new StringBuffer("");
			char nameChar = name.charAt(0);
			// validate first character
			if (Character.isLetter(nameChar) || nameChar == UNDERSCORE) {
				goodName.append(nameChar);
			} else {
				goodName.append(UNDERSCORE);
				errors++;
			}
			for (int i = 1; i < name.length(); ++i) {
				nameChar = name.charAt(i);
				if (Character.isLetter(nameChar) || Character.isDigit(nameChar) ||
				        nameChar == UNDERSCORE) {
					goodName.append(nameChar);
				} else {
					goodName.append(UNDERSCORE);
					errors++;
				}
			}
			validName = goodName.toString();
		}
		return validName;
		//if (errors > 0)
		//System.out.println("invalid name '"+name+"' has been fixed");
	}

	/**
	 * Given a candidate name, determine whether it is unique. If not,
	 * make it unique and return the result.
     * @param param
	 * @param name
	 */
	public String getUniqueName(Parameter param, String name)
	{
		// fix individual name
		String validName = fixParameterName(name);
		if (!isValidParameterNameInRelation(param, validName)) {
			int xx = 1;
			while (!isValidParameterNameInRelation(param, validName + xx)) {
				xx++;
			}
			validName = validName + xx;
		}

		return validName;
	}

    //TODO unused method - should this be removed?
	protected void fixName(ModelObject mObj)
	{
		if (!(mObj instanceof Parameter))
			return; // need only fix Parameter names
		// fix individual name
		String name = mObj.getName();
		String validName = fixParameterName(name);
		if (!isValidParameterNameInRelation(mObj, validName)) {
			int xx = 1;
			while (!isValidParameterNameInRelation(mObj, validName + xx)) {
				xx++;
			}
			validName = validName + xx;
		}
		if (!name.equals(validName))
			mObj.setName(validName);
	}

   //TODO unused method - should this be removed?
	// check for duplicate names before parameter has been added
	protected boolean isValidParameterNameInRelation(String paramName)
	{
		// check for duplicate name
		for (int i = 0; i < modelObjects.size(); ++i) {
			ModelObject mObj = (ModelObject) modelObjects.get(i);
			if (!(mObj instanceof Parameter)) continue;
			if (mObj.getName().equals(paramName)) { // duplicate names found
				return false;
			}
		}

		// check for clash with python data type names
		return true;
	}

	// check for duplicate names after parameter has been added
	public boolean isValidParameterNameInRelation(ModelObject param, String paramName)
	{
		// check for duplicate name
		for (int i = 0; i < modelObjects.size(); ++i) {
			ModelObject mObj = (ModelObject) modelObjects.get(i);
			if (!(mObj instanceof Parameter)) continue;
			if (param != mObj) {
				if (mObj.getName().equals(paramName)) { // duplicate names found
					return false;
				}
			} // skip itself
		}

		// check for clash with python data type names
		return true;
	}

	// CausalitySupport interface

	public List getItems(CausalityStatus causality)
	{
		return getCausalityManager().getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return getCausalityManager().isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return getCausalityManager().getCausality(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(obj, l);
	}

	// XmlSupport interface
	public String getXmlTag()
	{
		return Relation.XML_TAG;
	}

	// XmlSupport interface
	public String getXmlMappedTag()
	{
		return Relation.XML_MAPPED_TAG;
	}
}
