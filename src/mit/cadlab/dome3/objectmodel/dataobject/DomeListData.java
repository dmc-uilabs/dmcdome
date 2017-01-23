package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.AbstractModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 10, 2003
 * Time: 4:07:12 PM
 * To change this template use Options | File Templates.
 */
public class DomeListData extends AbstractDataObject
        implements DomeList
{
    protected DataList data = new DataList();
    protected String itemDataType = "Real";
	ModelObjectScope scope;
    List paramsXml;
    ParameterDeletionListener myDeletionListener = new ParameterDeletionListener();
	protected Number initialValue = new Double(0.0); // determines type

    public DomeListData() {
        data.addDListListener(new DataListListener());
    }

    public DomeListData(ModelObjectScope scope, DomeList list) {
        if (list == null)
            throw new IllegalArgumentException("DomeList - null parameter");
        setScope(scope);
        Object topScope = getObjectTopScope(scope);
        if (topScope instanceof ModelInterface || topScope instanceof InterfaceModelView) {
            addAndMap(0, list.getValues());
        } else
            addCopies(list.getValues());
        data.addDListListener(new DataListListener());
    }

    public DomeListData(DomeList list) {
        throw new UnsupportedOperationException();
    }

    public DomeListData(List data) {
        if (data == null)
            throw new IllegalArgumentException("DomeList - null parameter");
        this.data = new DataList(data);
        this.data.addDListListener(new DataListListener());
    }

    public DomeListData(Element xmlElement) {
        super(xmlElement);
        itemDataType = xmlElement.elementText("itemType");
	    paramsXml = xmlElement.selectNodes("/" + getXmlTag() + "/parameters/" + Parameter.XML_TAG);
        data.addDListListener(new DataListListener());
    }

    public void loadXmlData() {
        Parameter param;
        Element element;
        for (Iterator iter = paramsXml.iterator(); iter.hasNext();) {
            element = (Element) iter.next();
            Id paramId = AbstractModelObject.parseXmlRef(element);
            param = (Parameter) scope.getModelObjectById(paramId);
            if (param != null) {
                this.data.add(param);
            }
        }

        // load data
        /*Element dataElement = (Element) xmlElement.selectSingleNode("parameters");
        List items = dataElement.selectNodes("parameter");
        for (Iterator itemiter = items.iterator(); itemiter.hasNext();) {
            ConcreteParameter item = new ConcreteParameter((Element) itemiter.next());
            this.data.add(itemiter.next());
        }*/
    }

    public List getContainedParametersXml() {
        return paramsXml;
    }

    public void setScope(ModelObjectScope scope) {
        this.scope = scope;
    }

    public boolean isCompatibleType(DataObject newObj) {
        return (newObj instanceof DomeListData);
    }

    public DomeList getDomeList() {
        return this;
    }

    protected TypeInfo getTypeInfo() {
        return DomeList.TYPE_INFO;
    }

	public String getItemType(){
		return itemDataType;
	}

	public void setItemType(String type) {
        itemDataType = type;
		firePropertyChange(ITEMTYPE, null, type);
	}

    public int getSize() {
        return data.size();
    }

    public void setSize(int newSize) {
        int currentsize = getSize();
        if (currentsize == newSize) return;
        if (currentsize < newSize)  //append
        {
            addItems(newSize - currentsize);
        } else //truncate from end
        {
            data.removeRange(newSize, currentsize);
        }

        firePropertyChange(SIZE, null, new Integer(data.size()));
    }

    public void setValues(List values) {
        if (values.size() > 0) {
            data.clear();
            data.addAll(values);
            firePropertyChange(LIST, null, data);
        }
    }

    public List getValues() {
        return data;
    }

	public Object getValuesForXmlRpcUse ()
	{
		return data;
	}

    public Collection getContents() {
        return Collections.unmodifiableCollection(data);
    }

    public Object getElementValue(int index) {
        return data.get(index);
    }

    public void setElementValue(int index, Object newValue) {
        if (!(newValue instanceof Parameter))
            return;
        if (index < 0 || index > data.size()) {
            return;
        } else {
            data.set(index, newValue);
            firePropertyChange(ITEM, null, new Integer(index));
        }
    }

    public void removeElementAt(int index) {
        if (index < 0 || index > data.size()) {
            return;
        } else {
            data.remove(index);
        }

        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void removeAll(int[] indices) {
        data.removeAll(indices);
        firePropertyChange(SIZE, null, new Integer(getSize()));

    }

    public void removeAll() {
        if (data.size() > 0) {
            data.removeRange(0, data.size());
            firePropertyChange(SIZE, null, new Integer(getSize()));
        }

    }

    public void removeObjects(List l) {
        data.removeAll(l);
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public Parameter addItem(String type) {
        Parameter p = createParameter(type);
        data.add(p);
        firePropertyChange(SIZE, null, new Integer(getSize()));
        return p;
    }

    public void removeItem(Parameter p) {
        if (data.contains(p))
            data.remove(p);
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public Parameter addItem(int index, String type) {
        if (index < 0 || index > data.size()) {
            return null;
        } else {
            Parameter p = createParameter(type);
            if (p != null) data.add(index, p);
            firePropertyChange(SIZE, null, new Integer(getSize()));
            return p;
        }
    }

    /**
     * to be called by plugin file context file list only!!
     * @param p
     */
    public void addItemReference(Parameter p){
        if(p!=null) data.add(p);
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void addItems(int Howmany) {
        for (int i = 0; i < Howmany; i++) {
            data.add(createParameter("Real"));
        }
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void addItems(int index, int Howmany) {
        for (int i = 0; i < Howmany; i++) {
            this.addItem(index, "Real");
            index++;
        }
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void addItems(List l) {
        data.addAll(l);
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void addItems(int index, List l) {
        data.addAll(index, l);
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void addCopies(List l) {
        Collection newObj = scope.newModelObjects(l);
        data.addAll(newObj);
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void addCopies(int index, List l) {
        Collection newObj = scope.newModelObjects(l);
        data.addAll(index, newObj);
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public Parameter deleteElementAt(int index) {
        if (index < 0 || index > data.size()) {
            return null;
        } else {
            Parameter p = (Parameter) data.remove(index);
            if (p != null) p.delete(myDeletionListener);
            return p;
        }
    }


    public int getParameterIndex(Parameter p) {
        return data.indexOf(p);
    }

    public DataObject duplicate() {
        return new DomeListData(scope, this);
    }

    protected PropertyChangeListener createValueUnitShadowListener() {
        return new ListValueShadowListener();
    }

    protected PropertyChangeListener createValueShadowListener() {
        return new ListValueShadowListener();
    }

    public class ListValueShadowListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            Object obj = evt.getSource();
            if (obj instanceof DomeListData) {
                if (property.equals(DomeList.LIST)) {
                    processListChange((DListEvent) evt.getNewValue());
                } else {
	                processChange(evt);
                }
            }
        }
    }

    protected void processListChange(DListEvent e) {
        switch (e.getType()) {
            case DListEvent.INTERVAL_ADDED:
                addAndMap(e.getFirstIndex(), e.getItems());
                break;
            case DListEvent.INTERVAL_CHANGED:

                break;
            case DListEvent.INTERVAL_REMOVED:
                deleteAndRemoveMapping(e.getIndices());
                break;
            case DListEvent.ITEMS_REMOVED:
                deleteAndRemoveMapping(e.getIndices());
                break;
            case DListEvent.ITEMS_REPLACED:
                break;
        }

    }

	protected void processChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		Object obj = evt.getSource();
		if (property.equals(DomeList.ITEMTYPE)) {
			setItemType((String)evt.getNewValue());
		}
	}

    protected void addAndMap(int index, Parameter p) {
        Parameter newObj = (Parameter) scope.newModelObject(p);
        data.add(index, newObj);
        if (getObjectTopScope(newObj) instanceof ModelInterface || getObjectTopScope(newObj) instanceof InterfaceModelView) {
            if (getObjectTopScope(p) instanceof Model) {
                ((AbstractDomeModel) scope.getModel()).getMappingManager().addInterfaceMapping(newObj, p);
            } else if (getObjectTopScope(p) instanceof ModelInterface || getObjectTopScope(p) instanceof InterfaceModelView) { //when interface is duplicated
                ConnectionMappingManager mgr = ((DomeModel) p.getModel()).getMappingManager();
                Collection mappings = mgr.getMappingsForParameter(p);
                mgr.addMappings(newObj, mappings);
            }
        }
    }

    protected void addAndMap(int index, List l) {
        for (int i = 0; i < l.size(); i++) {
            addAndMap(index + i, (Parameter) l.get(i));
        }
    }

    protected void deleteAndRemoveMapping(int[] indices) {
        Arrays.sort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            Parameter p = this.deleteElementAt(indices[i]);
            if (p != null)
                ((AbstractDomeModel) scope.getModel()).getMappingManager().removeAllMappings(p);
        }
    }

    class DataListListener implements DListListener {
        public void intervalChanged(DListEvent e) {
            firePropertyChange(DomeList.LIST, null, e);
        }

        public void intervalAdded(DListEvent e) {
            firePropertyChange(DomeList.LIST, null, e);
        }

        public void intervalRemoved(DListEvent e) {
            firePropertyChange(DomeList.LIST, null, e);
        }

        public void itemsRemoved(DListEvent e) {
            firePropertyChange(DomeList.LIST, null, e);
        }

        public void itemsReplaced(DListEvent e) {
            firePropertyChange(DomeList.LIST, null, e);
        }
    }

    public Element toXmlElement() {
        Element xml = super.toXmlElement();

	    if (itemDataType != null) {
	        xml.addElement("itemType").setText(itemDataType);
	    }

        Element paramElement;
        paramElement = DocumentHelper.createElement("parameters");

        xml.add(paramElement);

        // store data
        for (int i = 0; i < data.size(); i++) {
            Object obj = data.get(i);
            if (obj instanceof Parameter) {
                Element param = ((Parameter) obj).toXmlRef();
                paramElement.add(param);
            }
        }
        return xml;
    }

    protected Parameter createParameter(String type) {
        ModelObject obj = scope.newModelObject(type);
        if (obj instanceof Parameter) {
            return (Parameter) obj;
        } else {
            scope.deleteModelObject(obj);
            return null;
        }
    }

    public void deleteContentsFromScope() {
        scope.deleteModelObjects(data);
    }

    class ParameterDeletionListener implements DeletionListener {
        public void objectDeleted(DeletionEvent e) {
            data.remove(e.getSource());
            firePropertyChange(SIZE, null, new Integer(getSize()));
        }
    }

    public List getView() {
        return Collections.unmodifiableList(data);
    }

    public void addViewListener(DListListener l) {
    }

    public void removeViewListener(DListListener l) {
    }

    class DataList extends DArrayList {
        public DataList() {
        }

        public DataList(int initialCapacity) {
            super(initialCapacity);
        }

        public DataList(Collection objs) {
            super(objs);
        }

        public DataList(Object[] objs) {
            super(objs);
        }

        // Hooks into add/remove operations on List
        // assumption: list is not changed by add/remove Hooks!
        // hooks for doing something before adding/removing objects from List
        // returns true if add/remove should proceed, false otherwise
        protected boolean addHookBefore(Object obj) {
            return obj instanceof Parameter;
        }

        // hooks for doing something after adding/removing objects from List
        protected void addHookAfter(Object obj) {
            ((Parameter) obj).setContainerCollection(DomeListData.this);
            ((Parameter) obj).addDeletionListener(myDeletionListener);
        }

        protected boolean removeHookBefore(Object obj) {
            return true;
        }

        protected void removeHookAfter(Object obj) {
            ((Parameter) obj).setContainerCollection(null);
            ((Parameter) obj).removeDeletionListener(myDeletionListener);
        }
    }

    public static ModelObjectScope getObjectTopScope(ModelObjectScope s) {
        if (s instanceof ModelInterface || s instanceof Model) {
            return s;
        } else if (s instanceof Relation) {
            return ((Relation) s).getScope();
        } else if (s instanceof Subscription) {
            return ((Subscription) s).getScope();
        } else
            return s;
    }

    public static ModelObjectScope getObjectTopScope(ModelObject obj) {
        if (obj.getScope() instanceof SubscriptionInterface) {
            return ((SubscriptionInterface) obj.getScope()).getSubscription().getScope();
        }
        while (!(obj.getScope() instanceof ModelInterface) && !(obj.getScope() instanceof Model) &&
                !(obj.getScope() instanceof InterfaceModelView)) {
            obj = (ModelObject) obj.getScope();
        }
        return obj.getScope();
    }

}
