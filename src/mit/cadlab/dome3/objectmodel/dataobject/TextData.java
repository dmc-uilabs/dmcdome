// TextData.java
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

public class TextData extends AbstractDataObject
        implements DomeText
{

	protected String value;

	public TextData()
	{
		value = "";
	}

	public TextData(String v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeText - null parameter");
		value = v;
	}

	public TextData(DomeText v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeText - null parameter");
		value = v.getValue();
	}

	public TextData(Element xmlElement)
	{
		super(xmlElement);
		value = xmlElement.getText();
		if (value == null)
			throw new IllegalArgumentException("DomeText - invalid xml value: " + xmlElement.asXML());
	}

	protected PropertyChangeListener createValueShadowListener()
	{
		return new TextShadowListener();
	}

	protected PropertyChangeListener createValueUnitShadowListener()
	{
		return new TextShadowListener();
	}

	// DataObject interface
	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof DomeString || newObj instanceof DomeText);
	}

	protected TypeInfo getTypeInfo()
	{
		return DomeText.TYPE_INFO;
	}

	public DataObject duplicate()
	{
		return new TextData(this);
	}

	// DomeText interface
	public DomeText getDomeText()
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
			setValue(((DomeString)newObj).getValue());
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
		return super.toXmlElement().addCDATA(value);
	}

    public Object __eq__(Object o)
	{
		return new BooleanData(equals(o));
	}

    public Object __ne__(Object o)
    {
        return new BooleanData(!equals(o));
    }

	public boolean equals(Object anothertext)
	{
		if (anothertext instanceof TextData)
        {
            String v1=  this.getValue();
            String v2=  ((TextData)anothertext).getValue();
            return v1.trim().equals(v2.trim());
        }
		else
			return false;
	}

	protected class TextShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof TextData) {
				if (property.equals(TextData.VALUE)) {
					setValue(((TextData) obj).getValue());
				}
			}
		}
	}
}
