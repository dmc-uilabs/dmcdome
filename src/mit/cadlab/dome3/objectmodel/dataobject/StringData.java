// StringData.java
package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

public class StringData extends AbstractDataObject
        implements DomeString
{

	protected String value;

	public StringData()
	{
		value = "";
	}

	public StringData(String v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeString - null parameter");
		value = v;
	}

	public StringData(DomeString v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeString - null parameter");
		value = v.getValue();
	}

	public StringData(Element xmlElement)
	{
		super(xmlElement);
		value = xmlElement.getText();
		if (value == null)
			throw new IllegalArgumentException("DomeString - invalid xml value: " + xmlElement.asXML());
	}

	protected PropertyChangeListener createValueShadowListener()
	{
		return new StringShadowListener();
	}

	protected PropertyChangeListener createValueUnitShadowListener()
	{
		return new StringShadowListener();
	}

	// DataObject interface
	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof DomeString || newObj instanceof DomeText);
	}

	protected TypeInfo getTypeInfo()
	{
		return DomeString.TYPE_INFO;
	}

	public DataObject duplicate()
	{
		return new StringData(this);
	}

	// DomeString interface
	public DomeString getDomeString()
	{
		return this;
	}

	public List getValues()
	{
		return Collections.singletonList(value);
	}

	public Object getValuesForXmlRpcUse ()
	{
		return value;
	}

	public void setValues(List values)
	{
		if (values.size() > 0) {
			Object value = values.get(0);
			setValue(value.toString());
		}
	}

	public void setValues(DataObject newObj)
	{
		if (newObj instanceof DomeString)
			setValue(((DomeString) newObj).getValue());
		else if (newObj instanceof DomeText)
			setValue(((DomeText) newObj).getValue());
		else
			setValue(newObj.toString());
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		if (value == null) return;
		if (value.indexOf("\n") != -1)
			throw new IllegalArgumentException("DomeString can not contain newlines");
		String oldValue = this.value;
		this.value = value;
		firePropertyChange(VALUE, oldValue, this.value);
	}

	public String toString()
	{
		return value;
	}

	public Element toXmlElement()
	{
		return super.toXmlElement().addText(value);
	}

    public Object __eq__(Object o)
	{
		return new BooleanData(equals(o));
	}

    public Object __ne__(Object o)
    {
        return new BooleanData(!equals(o));
    }

	public boolean equals(Object anotherstr)
	{
		if (anotherstr instanceof StringData)
        {
            return this.getValue().equals(((StringData)anotherstr).getValue());
        }
		else
			return false;
	}

	protected class StringShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof StringData) {
				if (property.equals(StringData.VALUE)) {
					setValue(((StringData) obj).getValue());
				}
			}
		}
	}
}
