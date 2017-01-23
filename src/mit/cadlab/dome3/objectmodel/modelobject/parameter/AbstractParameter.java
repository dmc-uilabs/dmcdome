// AbstractParameter.java
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.treetable.BuildTreeTable;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.AbstractDataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeCollection;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.AbstractModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.AbstractContext;
import mit.cadlab.dome3.objectmodel.modelobject.relation.AbstractRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.OrderedHashMap;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.network.server.Debug;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

/**
 * protected DataObjectFactory getDataOjbectFactory();
 */
public abstract class AbstractParameter extends AbstractModelObject
        implements Parameter
{

	protected static String isConstantXmlTag = "isConstant";
	protected static String currentTypeXmlTag = "currentType";

	protected OrderedHashMap dataObjects;
	protected String currentType;
	protected boolean isConstant = false;
	protected transient Parameter.DataTypeSelection dataTypeSelection;
	protected String valueStatus;
    protected DomeCollection container;  //will be null if param is not inside a DomeCollection i.e. DomeList for now
	protected InternalDataObjectListener dataObjectListener;

    protected CurrentTypeChangeListener currentTypeChangeListener;

	public AbstractParameter(ModelObjectScope scope, Id id)
	{
		this(scope, id, (String) null);
	}

	public AbstractParameter(ModelObjectScope m, Id id, String dataType)
	{
		super(m, id);

		dataObjects = new OrderedHashMap();

		if (dataType != null && Registry.isValidDataObjectType(dataType)) {
			setDataTypeSelection(new Parameter.DataTypeSelection(dataType));
		} else {
			setDataTypeSelection(new Parameter.DataTypeSelection("Real")); // for now; later do all datatypes
		}

		// set the name, avoiding duplicates in a relation
		String name = currentType + "Parameter";
		if (m instanceof Relation) {
			AbstractRelation rel = (AbstractRelation) m;
			name = rel.getUniqueName(this, name);
		}
		setName(name);
	}

	public AbstractParameter(ModelObjectScope scope, Id id, Parameter param)
	{
		super(scope, id, param);
		dataObjects = new OrderedHashMap();

        Iterator keys = param.getDataObjects().iterator();
        while (keys.hasNext()) {
            DataObject keyData = (DataObject) keys.next();
            Object dupData;
			if (keyData instanceof DomeList) {
				dupData = new DomeListData(getScope(), (DomeList) keyData);
			} else
				dupData = getDataObjectFactory().newInstance(keyData); // way to make duplicate of data
			this.dataObjects.put(keyData.getTypeName(), dupData);
			if (dupData instanceof DomeList) {
				((DomeListData) dupData).setScope(getScope());
			}
		}
		currentType = param.getCurrentType();
		isConstant = param.isConstant();
		synchronizeDataTypeSelection();
	}

	public AbstractParameter(ModelObjectScope scope, Element xmlElement)
    {
        super(scope, xmlElement);
        dataObjects = new OrderedHashMap();

        if (xmlElement.getQName().getName().equals(getXmlTag())) {
            Element isConstantNode = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + isConstantXmlTag);
            if (isConstantNode != null)
                isConstant = ("true".equals(isConstantNode.attributeValue("value")));
            // load data types
            List dataNodes = xmlElement.selectNodes("/" + getXmlTag() + "/data/dataobject");
			Iterator it = dataNodes.iterator();
			while (it.hasNext()) {
				Element paramElement = (Element) it.next();
				DataObject data = getDataObjectFactory().newInstance(paramElement);
				dataObjects.put(data.getTypeName(), data);
				if (data instanceof DomeList) {
					((DomeListData) data).setScope(getScope());
				}

			}
			Element currentType = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + currentTypeXmlTag);
			setCurrentType(currentType.attributeValue("value"));
			// validate?
		} else {
			throw new IllegalArgumentException(getTypeName() + " - illegal xmlElement: " + xmlElement.asXML());
		}
	}

	private void synchronizeDataTypeSelection()
	{
		dataTypeSelection = new Parameter.DataTypeSelection(dataObjects.keyList(), currentType);
	}

	protected abstract DataObjectFactory getDataObjectFactory();

	// Parameter interface
	public boolean supportsType(String type)
	{
		return dataObjects.containsKey(type);
	}

	public Parameter.DataTypeSelection getDataTypeSelection()
	{
		return dataTypeSelection;
	}

	/*
	* validate all names first
	* only will do if at least one valid type
	* removes all types and sets types to
	* types given
	* should keep defaults for types if already exists
	* delete defaults not in list and add new ones
	*/
	public void setDataTypeSelection(Parameter.DataTypeSelection dtSel)
	{
		// validate types
		if (dtSel == null || dtSel.equals(dataTypeSelection))
			return;
		ArrayList types = new ArrayList();
		Iterator it1 = dtSel.getValidDataTypes().iterator();
		while (it1.hasNext()) {
			String type = (String) it1.next();
			if (Registry.isValidDataObjectType(type))
				types.add(type);
		}
		if (types.isEmpty()) return; // do not set

		// create new list of objects, copying from old list if it existed
		OrderedHashMap newDataObjects = new OrderedHashMap();
		Iterator it2 = types.iterator();
		while (it2.hasNext()) {
			String type = (String) it2.next();
			if (dataObjects.containsKey(type)) {
				newDataObjects.put(type, dataObjects.get(type));
			} else {
				DataObject obj = getDataObjectFactory().newInstance(type);
				newDataObjects.put(type, obj);
				if (obj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList) {
					((DomeListData) obj).setScope(getScope());
				}
			}
		}

		// set values
		Parameter.DataTypeSelection oldDataTypeSelection = dataTypeSelection;
		String oldCurrentType = currentType;

        // sangmok : to prevent memory leakage remove
        dataObjects.clear();

        dataObjects = newDataObjects;
		String newCurrentType = dtSel.getSelectedDataType();
		if (!types.contains(newCurrentType))
			newCurrentType = (String) types.get(0); // first element default
		currentType = newCurrentType;
		synchronizeDataTypeSelection();

		firePropertyChange(DATATYPESELECTION, oldDataTypeSelection, dataTypeSelection);
		firePropertyChange(CURRENT_TYPE, oldCurrentType, currentType);
	}

	public String getCurrentType()
	{
		return currentType;
	}

	public void setCurrentType(String type)
	{
		if (dataObjects.containsKey(type)) {
			if (currentType != null && !currentType.equals(type))
				return;
			String oldType = currentType;
			currentType = type;
			synchronizeDataTypeSelection();
			firePropertyChange(CURRENT_TYPE, oldType, currentType);
		}
	}

	public String getValueStatus()
	{
		return valueStatus;
	}

	public void setValueStatus(String status)
	{
		String oldOne = valueStatus;
		valueStatus = status;
		firePropertyChange(VALUE_STATUS, oldOne, valueStatus);
	}

	public DomeCollection getContainerCollection() {
		return container;
	}

	public void setContainerCollection(DomeCollection container) {
		this.container = container;
	}

	public boolean isConstant()
	{
		return isConstant;
	}

	public void setConstant(boolean isConstant)
	{
		Boolean oldIsConstant = new Boolean(this.isConstant);
		this.isConstant = isConstant;
		firePropertyChange(CONSTANT, oldIsConstant, new Boolean(this.isConstant));
	}

	public DataObject getCurrentDataObject()
	{
		return getDataObjectForType(currentType);
	}

	public DataObject getDataObjectForType(String type)
	{
		return (DataObject) dataObjects.get(type);
	}

	public List getDataObjects()
	{
		return new ArrayList(dataObjects.values());
	}

	public String[] getDataObjectTypes()
	{
		return (String[]) dataObjects.keyList().toArray(new String[]{});
	}

	// DomeObject interface
	protected TypeInfo getTypeInfo()
	{
		return Parameter.TYPE_INFO;
	}

	public void setName(String name)
	{
	  // Qing May 18: move this code from AbstractDomeObject

		if (getScope() instanceof Relation) {
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
            // check for " " space inside name,will take care of duplicate name
			AbstractRelation rel = (AbstractRelation) getScope();
			name=rel.getUniqueName(this, name);
		}

		super.setName(name);
	}

	public void delete(DeletionListener notifier)
	{
		//check if this is a Listdata data parameter and do it differently
		if (currentType.equals("List")) {
			DomeListData listObj = (DomeListData) this.getDataObjectForType("List");
			listObj.deleteContentsFromScope();
			if (listObj != null && listObj.getSize() != 0) {
				//delete all stuff inside.
				for (int i = 0; i < listObj.getSize(); i++) {
					Parameter item = (Parameter) listObj.getElementValue(i);
					item.delete(notifier);
				}
			}

		}

        // sangmok : if there is a ParameterTableObject associated with this Parameter, release the data object reference in it.
//        BuildTreeTable.ParameterTableObject paramTableObj = (BuildTreeTable.ParameterTableObject) BuildTreeTable.getTableObjectFactory().getTableObject(this);
//        if (paramTableObj != null) {
//            paramTableObj.releaseDataObjectReferenceOfEditors();
//        }

        // sangmok : memory debugging
        int numberOfListeners = 0;
        for (Iterator i = dataObjects.values().iterator(); i.hasNext(); ) {
            DataObject aDataObject = (DataObject) i.next();
            if (aDataObject instanceof AbstractDataObject) {
                /* removes all property listeners associated with this AbstractDataObject */
                ((AbstractDataObject) aDataObject).clearPropertyListeners();
                numberOfListeners++;
            }
        }
        //Debug.trace(Debug.ALL, "parameter '" + getName() + "' data object listener cleared / No of data objects = " + numberOfListeners);

        dataObjects.clear(); // This is the much-be-done. It contains data objects as keys.
        if (dataObjectListener != null)
        {
            dataObjectListener.removeDataObject(); // to make sure target is set to null;
            dataObjectListener = null; // This will release InternalListner, and as a result it will make InternalListener's reference to data object cleared.
        }

		super.delete(notifier);
	}

	protected String contentToString()
	{
		return "  current type: " + currentType + "\n  is constant: " + isConstant +
		        "\n  " + dataObjects;
	}

	public String getXmlTag()
	{
		return Parameter.XML_TAG;
	}

	public String getXmlMappedTag()
	{
		return Parameter.XML_MAPPED_TAG;
	}

	public Element toXmlMappedRef()
	{
		Element xmlElement = DocumentHelper.createElement(getXmlMappedTag());
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

	protected void addXmlContent(Element xmlElement)
	{
		if (isConstant)
			xmlElement.addElement(isConstantXmlTag).addAttribute("value", "true");
		xmlElement.addElement(currentTypeXmlTag).addAttribute("value", currentType);
		XMLUtils.addCollection(xmlElement, "data", dataObjects.values());
	}

    /**
     * This method was overwritten and given public access
     * (previously protected) to allow Optimization Parameters
     * to fire property change events when their lower or upper
     * limits change.
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

	protected void initParameter()
	{
		dataObjectListener = createInternalDataObjectListener();
		DataObject dObj = getCurrentDataObject();
		if (dObj != null)
			dataObjectListener.addToDataObject(dObj);
		currentTypeChangeListener = new CurrentTypeChangeListener();
        addPropertyChangeListener(CURRENT_TYPE, currentTypeChangeListener);
	}

	protected abstract InternalDataObjectListener createInternalDataObjectListener();

	protected abstract class InternalDataObjectListener implements PropertyChangeListener
	{
		protected DataObject target;

		protected void addToDataObject(DataObject dObj)
		{
			target = dObj;
            String property = DataObject.VALUE;
            if (target instanceof DomeEnumeration)
                property = DomeEnumeration.LASTSELECTION;
			target.addPropertyModifiedListener(property, this);
		}

		protected void removeDataObject()
		{
            String property = DataObject.VALUE;
            if (target instanceof DomeEnumeration)
                property = DomeEnumeration.LASTSELECTION;
			target.removePropertyModifiedListener(property, this);
            target = null;
		}

		public abstract void propertyChange(PropertyChangeEvent evt);
	}

	protected class CurrentTypeChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(CURRENT_TYPE)) {
				dataObjectListener.removeDataObject();
				dataObjectListener.addToDataObject(getCurrentDataObject());
			}
		}
	}
}
