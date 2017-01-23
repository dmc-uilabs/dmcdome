// EnumerationData.java
// version 2: July 24th, add a name check so no redandunt name is allowed in enumeration

package mit.cadlab.dome3.objectmodel.dataobject;


import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class EnumerationData extends AbstractDataObject  implements DomeEnumeration {

    protected Vector data = new Vector();
    protected int lastSelectionIndex = -1; //to remember selected row

    protected HashMap Name_Value_hashmap = new HashMap();//name is the key,

    public EnumerationData() {

    }

    public EnumerationData(Vector v) {
        if (v == null)
            throw new IllegalArgumentException("DomeEnumeration - null parameter");
        else
            for (int i = 0; i < v.size(); i++) {
                if (v.get(i) instanceof EnumerationItem) {
                    data.add(v.get(i));
                } else {
                    //use id generator to generate unique name instead
//data.add(new EnumerationItem("item" + i, v.get(i)));
                    addElement(v.get(i));
                }
            }
    }

    public EnumerationData(DomeEnumeration enm) {
        if (enm == null)
            throw new IllegalArgumentException("EnumerationData - null parameter");
        EnumerationData v = (EnumerationData) enm;
        //data = (Vector) v.data.clone();  //clone is not corect, since it is not deep copy
        for (Iterator i = v.data.iterator(); i.hasNext();) {
            addElement(i.next());
        }

        this.lastSelectionIndex = enm.getLastSelection();
    }

    public EnumerationData(EnumerationData v) {
        if (v == null)
            throw new IllegalArgumentException("EnumerationData - null parameter");
        for (Iterator i = v.data.iterator(); i.hasNext();) {
            addElement(i.next());
        }
        //data = (Vector) v.data.clone();
        this.lastSelectionIndex = v.getLastSelection();
    }

    public EnumerationData(Element xmlElement) {
        super(xmlElement);

        // load data
        Element dataElement = (Element) xmlElement.selectSingleNode("data");
        List items = dataElement.selectNodes("dataobject");
        for (Iterator itemiter = items.iterator(); itemiter.hasNext();) {
            EnumerationItem item = new EnumerationItem((Element) itemiter.next());
            addElement(item);
        }
        Element indexElement = (Element) xmlElement.selectSingleNode("lastSelectionIndex");
        String lastSel = indexElement.getText();
        lastSelectionIndex = new Integer(lastSel).intValue();
    }

//already exists in AbstractDataObject see getDataObject
//    public DomeEnumeration getEnumeration() {
//        return this;
//    }



    protected PropertyChangeListener createValueShadowListener() {
        return new EnumerationShadowListener();
    }

    protected PropertyChangeListener createValueUnitShadowListener() {
        return new EnumerationShadowListener();
    }

    public void setEnumeration(EnumerationData newData) {
        if (newData == null)
            throw new IllegalArgumentException("EnumerationData - null parameter");

        clear();

        for (Iterator itemiter = newData.data.iterator(); itemiter.hasNext();) {
            EnumerationItem item = (EnumerationItem) itemiter.next();
            addElement(item);
        }
        lastSelectionIndex = newData.getLastSelection();
        firePropertyChange(ENUMERATION);
    }

    // only need this method when not using DOME GUI, e.g. when using DOME API
    // normally, this firePropertyChange is done automatically when the Edit Enumeration GUI is OK'ed
    public void fireEnumerationPropertyChange() {
        firePropertyChange(ENUMERATION);
    }

    public DataObject duplicate() {
        return new EnumerationData(this);
    }

    public EnumerationData dup() {
        return new EnumerationData(this);
    }

    public boolean isCompatibleType(DataObject newObj) {
        return (newObj instanceof EnumerationData);
    }

    public List getValues() {
        return data;
    }

	public Object getValuesForXmlRpcUse ()
	{
		return data;
	}

    public void setValues(List values) {
      /*  if (values.size() == 1)
            setLastSelection(((Integer) values.get(0)).intValue());
        */
        if (values.size() > 0) {
			Object value = values.get(0);
          //  System.out.println(value.getClass());
            if(value instanceof Integer)
                setLastSelection(((Integer) values.get(0)).intValue());
            else if(value instanceof Double)
                setLastSelection(((Double) values.get(0)).intValue());
        }
    }

    public void setValues(DataObject newObj) {
        clear();
        if (newObj instanceof DomeVectorData) {
            Vector newdata = ((DomeVectorData) newObj).data;
            //  this.lastSelectionIndex = 0;
            for (Iterator i = newdata.iterator(); i.hasNext();) {
                addElement(i.next());
            }
            lastSelectionIndex=-1;
        } else if (newObj instanceof EnumerationData) {
            EnumerationData en = (EnumerationData) newObj;
           // data = en.data;
            for (Iterator i = en.getValues().iterator(); i.hasNext();) {
                addElement(i.next());
            }
            this.lastSelectionIndex = en.getLastSelection();
        }
        firePropertyChange(ENUMERATION);
        firePropertyChange(LASTSELECTION, null, new Integer(this.lastSelectionIndex));
    }

    public int getSize() {
        return data.size();
    }

    public void setSize(int newSize) {
        if (newSize == getSize()) return;
        if (newSize < getSize())//truncate
        {
            for (int i = getSize() - 1; i >= newSize; i--)
                removeElementAt(i);

            if (getLastSelection() > newSize) setLastSelection(-1);

            firePropertyChange(SIZE, null, new Integer(newSize));
        }
        else  //append
        {
            for (int i = getSize(); i < newSize; i++)
                data.add(new EnumerationItem());
            firePropertyChange(SIZE, null, new Integer(newSize));
        }
//		System.out.println("Size = " + data.size());
    }


    public String toString() {
        return data.toString();
    }

    public TypeInfo getTypeInfo() {
        return DomeEnumeration.TYPE_INFO;
    }


    public EnumerationItem[] getNames() {
        EnumerationItem[] result = new EnumerationItem[getSize()];
        for (int i = 0; i < getSize(); i++) {
            EnumerationItem v = (EnumerationItem) (data.elementAt(i));
            result[i] = v;
        }
        return result;
    }

    public String[] getNamesArray() {
        String[] result = new String[getSize()];
        for (int i = 0; i < getSize(); i++) {
            result[i] = ((EnumerationItem) (data.elementAt(i))).name;
        }
        return result;
    }

    public BooleanData hasName(String name) {
        BooleanData boo = new BooleanData(false);
        for (int i = 0; i < getSize(); i++) {
            if (((EnumerationItem) (data.elementAt(i))).name.equals(name)) {
                boo.setValue(true);
                break;
            }
        }
        return boo;
    }

    public BooleanData hasNameCI(String name) {
        BooleanData boo = new BooleanData(false);
        for (int i = 0; i < getSize(); i++) {
            if (((EnumerationItem) (data.elementAt(i))).name.equalsIgnoreCase(name)) {
                boo.setValue(true);
                break;
            }
        }
        return boo;
    }

    public Object getElementValue(int index) {
        if (index == -1) return null;
        EnumerationItem v = (EnumerationItem) (data.elementAt(index));
        return v.obj;
    }

     /**
     * should assuming no redudant name
     * @param name
     * @return
     */
    public Object getElementValue(String name) {
        return Name_Value_hashmap.get(name);
    }

    public void setElementValue(int index, Object newValue) {
        if (index == -1) return;
        EnumerationItem v = (EnumerationItem) (data.elementAt(index));
        String n = v.name;
        EnumerationItem newV = new EnumerationItem(n, newValue);
        data.set(index, newV);

        Name_Value_hashmap.put(n,newValue);
        firePropertyChange(ELEMENTVALUE, null, new Integer(index));

    }

    public void setElementValue(String name, Object newValue) {
        if (!Name_Value_hashmap.containsKey(name)) return;
        int index=-1;
        for (int i = 0; i < getSize(); i++) {
            if (((EnumerationItem) (data.elementAt(i))).name.equals(name)) {
                index=i;
                break;
            }
        }
        setElementValue(index,newValue);

    }

    public String getElementName(int index) {
        if (index == -1) return null;
        EnumerationItem v = (EnumerationItem) (data.elementAt(index));
        return v.name;
    }

    public void setElementName(int index, String newName) {
        if (index == -1) return;
        EnumerationItem v = (EnumerationItem) (data.elementAt(index));
        String oldName=v.getName();
        Object newValue=v.getValue();

        Name_Value_hashmap.remove(oldName);

        EnumerationItem newV = new EnumerationItem(newName, newValue);
        int number_to_append = 2;
        while (Name_Value_hashmap.containsKey(newV.name)) {
            newV.name = newName + number_to_append++;
        }
        Name_Value_hashmap.put(newV.name, newValue);
        data.set(index, newV);
        firePropertyChange(ELEMENTNAME, null, new Integer(index));
    }

    public void setLastSelectionToName(String newName) {
        for (int i = 0; i < data.size(); i++) {
            EnumerationItem v = (EnumerationItem) (data.elementAt(i));
            if (v.getName().equals(newName)) {
                this.setLastSelection(i);
                //break; // debugging : break; is incorrect here. return; should be used.
                return;
            }
        }
        throw new IllegalArgumentException("EnumerationData.setLastSelectionToName: can't find the name " + newName);
    }

    /**
     *
     * @param value can be String or Dome Data Type or object with same toString value
     */
    public void setLastSelectionToValue(Object value) {
        for (int i = 0; i < data.size(); i++) {
            Object v = ((EnumerationItem) (data.elementAt(i))).getValue();
            if (v.toString().equals(value.toString())) {
                this.setLastSelection(i);
                    return;
            }
            if (v instanceof Number) {
                if (value instanceof Number) {
                    if (((Number) v).doubleValue() == ((Number) value).doubleValue()) {
                        this.setLastSelection(i);
                        return;
                    }
                } else if (value instanceof String) {
                    try {
                        double d = Double.parseDouble((String)value);
                        if (((Number) v).doubleValue() == d) {
                            this.setLastSelection(i);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                }
            }
        }
    }

    public void addElement(String valueName, Object newValue) {
        EnumerationItem newItem = new EnumerationItem(valueName, newValue);
        int number_to_append = 2;
        while (Name_Value_hashmap.containsKey(newItem.getName())) {
            newItem.name = valueName + number_to_append++;
        }
        data.add(newItem);
        Name_Value_hashmap.put(newItem.getName(), newItem.getValue());
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void addElement(Object newValue) {
        if (newValue instanceof EnumerationItem) {
            EnumerationItem newItem = (EnumerationItem) newValue;
            int number_to_append = 2;
            String _newName=newItem.getName();
            while (Name_Value_hashmap.containsKey(newItem.getName())) {
                newItem.name = _newName + number_to_append++;
            }
            data.add(newItem);
            Name_Value_hashmap.put(newItem.getName(), newItem.getValue());
        } else {
            EnumerationItem newItem = new EnumerationItem(newValue);
            int number_to_append = 2;
            String _newName=newItem.getName();
            while (Name_Value_hashmap.containsKey(newItem.getName())) {
                newItem.name = _newName + number_to_append++;
            }
            data.add(newItem);
            Name_Value_hashmap.put(newItem.getName(), newItem.getValue());
        }
        firePropertyChange(SIZE, null, new Integer(getSize()));
    }

    public void insertElementAt(int index, String valueName, Object newValue) {
        EnumerationItem newItem = new EnumerationItem(valueName, newValue);
        int number_to_append = 2;
        while (Name_Value_hashmap.containsKey(newItem.getName())) {
            newItem.name = valueName + number_to_append++;
        }
        data.insertElementAt(newItem, index);
        Name_Value_hashmap.put(newItem.getName(), newItem.getValue());

        int oldLastSelection = this.lastSelectionIndex;
        if(lastSelectionIndex==index)  lastSelectionIndex=lastSelectionIndex+1;   //keep the old selection

        firePropertyChange(SIZE, null, new Integer(getSize()));
        firePropertyChange(LASTSELECTION, new Integer(oldLastSelection), new Integer(this.lastSelectionIndex));

    }

    public void insertElementAt(int index, Object newValue) {
        EnumerationItem newItem = new EnumerationItem(newValue);
        int number_to_append = 2;
        String _newName=newItem.getName();
        while (Name_Value_hashmap.containsKey(newItem.getName())) {
            newItem.name = _newName + number_to_append++;
        }
        data.insertElementAt(newItem, index);
        Name_Value_hashmap.put(newItem.getName(), newItem.getValue());

        int oldLastSelection = this.lastSelectionIndex;
        if(lastSelectionIndex==index)  lastSelectionIndex=lastSelectionIndex+1;   //keep the old selection

        firePropertyChange(SIZE, null, new Integer(getSize()));
        firePropertyChange(LASTSELECTION, new Integer(oldLastSelection), new Integer(this.lastSelectionIndex));
       }



    public void removeElementAt(int index) {

        EnumerationItem element_to_be_removed = (EnumerationItem) data.get(index);

        int oldLastSelection = this.lastSelectionIndex;
        if(lastSelectionIndex==index)  lastSelectionIndex=-1;

        Name_Value_hashmap.remove(element_to_be_removed.getName());

        data.removeElementAt(index);

        firePropertyChange(SIZE, null, new Integer(getSize()));
        firePropertyChange(LASTSELECTION, new Integer(oldLastSelection), new Integer(this.lastSelectionIndex));
     }


    public Element toXmlElement() {
        Element xml = super.toXmlElement();

        // store data
        Element dataElement = xml.addElement("data");
        for (int i = 0; i < data.size(); i++) {
            EnumerationItem item = (EnumerationItem) data.elementAt(i);
            dataElement.add(item.toXmlElement());
        }
        Element indexElement = xml.addElement("lastSelectionIndex");
        indexElement.addText(new Integer(lastSelectionIndex).toString());
        return xml;
    }

    public int getLastSelection() {
        //if (lastSelectionIndex >= getSize()) return -1;
        return lastSelectionIndex;
    }

    public void setLastSelection(int lastSelectionIndex) {

        int oldLastSelection = this.lastSelectionIndex;
        if (lastSelectionIndex >= getSize())
            throw new IllegalArgumentException("EnumerationData.setLastSelection: index out of bound");
        if (lastSelectionIndex == -1) {
            this.lastSelectionIndex = lastSelectionIndex;

        } else {
            this.lastSelectionIndex = lastSelectionIndex;
        }
        firePropertyChange(LASTSELECTION, new Integer(oldLastSelection), new Integer(this.lastSelectionIndex));
    }

    public void clear() {
        data.clear();
        Name_Value_hashmap.clear();
        //this.lastSelectionIndex = -1;
        firePropertyChange(SIZE, null, new Integer(0));
        //firePropertyChange(LASTSELECTION, null, new Integer(this.lastSelectionIndex));
    }



    public Object __eq__(Object o) {
        return new BooleanData(equals(o));
    }

    public Object __ne__(Object o) {
        return new BooleanData(!equals(o));
    }

    /**
     * Compares the LHS parameter the RHS parameter.
     * Boolean parameter result is true if: The two parameters contain the same elements (name-value pairs) and have the same selection.
     * Elements do not have to be in the same order.
     * @param anotherEnumeration
     * @return
     */
    public boolean equals(Object anotherEnumeration) {
        if (anotherEnumeration instanceof EnumerationData) {
            //first check whether they have same name-value pair
            EnumerationData that = (EnumerationData) anotherEnumeration;
            if (this.getSize() != that.getSize()) return false;
            if (this.getSize() == 0) return true;

            for (int i = 0; i < this.getSize(); i++) {
                String a_name = this.getElementName(i);
                Object this_value = this.getElementValue(i);

                if (!that.hasName(a_name).getValue()) return false;
                Object that_value = that.getElementValue(a_name);

                //for immutable object, it's okay , they are comparign the value!
                if(!this_value.equals(that_value))  return false;
            }

            //then if there selected value is the same
            if(this.lastSelectionIndex!=-1&&that.lastSelectionIndex!=-1)
            {
                Object this_value=this.getElementValue(this.lastSelectionIndex) ;
                Object that_value=that.getElementValue(that.lastSelectionIndex) ;

                if(!this_value.equals(that_value))  return false;
            }

            return true;
        } else
            return false;
    }

    protected class EnumerationShadowListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            Object obj = evt.getSource();
            if (obj instanceof EnumerationData) {
                if (property.equals(EnumerationData.VALUE)||property.equals(EnumerationData.ENUMERATION)) {
                    setEnumeration((EnumerationData) obj);
                } else if (property.equals(LASTSELECTION)) {
                    setLastSelection(((EnumerationData) obj).getLastSelection());
                }
            }
        }
    }
}
