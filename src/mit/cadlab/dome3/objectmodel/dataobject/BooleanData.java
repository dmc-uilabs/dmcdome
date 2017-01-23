// BooleanData.java
package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

public class BooleanData extends AbstractDataObject
        implements DomeBoolean
{

	protected Boolean value;

	public BooleanData()
	{
		value = Boolean.FALSE;
	}

	public BooleanData(String v)
	{
		value = new Boolean(v);
	}

	public BooleanData(boolean v)
	{
		value = v ? Boolean.TRUE : Boolean.FALSE;
	}

	public BooleanData(Boolean v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeBoolean - null parameter");
		value = v;
	}

	public BooleanData(DomeBoolean v)
	{
		if (v == null)
			throw new IllegalArgumentException("DomeBoolean - null parameter");
		value = v.getBooleanValue();
	}

	public BooleanData(Element xmlElement)
	{
		super(xmlElement);
		//Element b = (Element) xmlElement.selectSingleNode("/dataobject/value");
		//String text = b.getText();
		String text = xmlElement.getText();
		value = new Boolean(text);
		if (value == null)
			throw new IllegalArgumentException("DomeBoolean - invalid xml value: " + xmlElement.getText());
	}

	protected PropertyChangeListener createValueShadowListener()
	{
		return new BooleanShadowListener();
	}

	protected PropertyChangeListener createValueUnitShadowListener()
	{
		return new BooleanShadowListener();
	}

	// DataObject interface
	public boolean isCompatibleType(DataObject newObj)
	{
		return (newObj instanceof BooleanData);
	}

	protected TypeInfo getTypeInfo()
	{
		return DomeBoolean.TYPE_INFO;
	}

	public DataObject duplicate()
	{
		return new BooleanData(this);
	}

	// DomeBoolean interface
	public DomeBoolean getDomeBoolean()
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
			if (value instanceof Double)
				setValue(((Double) value).doubleValue() > 0);
			else if (value instanceof Integer)
				setValue(((Integer) value).doubleValue() > 0);
			else if (value instanceof Boolean)
				setValue(((Boolean) value).booleanValue());
		}
	}

	public void setValues(DataObject newObj)
	{
		boolean value = false;

		if (newObj instanceof RealData)
			value = ((RealData) newObj).getValue() > 0;
		else if (newObj instanceof IntegerData)
			value = ((IntegerData) newObj).getValue() > 0;
		else if (newObj instanceof BooleanData)
			value = ((BooleanData) newObj).getBooleanValue().booleanValue();

		setValue(value);
	}

	public boolean getValue()
	{
		return value.booleanValue();
	}

	public void setValue(boolean value)
	{
		setBooleanValue(value ? Boolean.TRUE : Boolean.FALSE);
	}

	public Boolean getBooleanValue()
	{
		return value;
	}

	public void setBooleanValue(Boolean value)
	{
		if (value == null) return;
		Boolean oldValue = this.value;
		this.value = value;
		firePropertyChange(VALUE, oldValue, this.value);
	}

	public String toString()
	{
		return value.toString();
	}

	public Element toXmlElement()
	{
		return super.toXmlElement().addText(value.toString());
	}

	protected class BooleanShadowListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			String property = evt.getPropertyName();
			Object obj = evt.getSource();
			if (obj instanceof DomeBoolean) {
				if (property.equals(DomeBoolean.VALUE)) {
					setValue(((DomeBoolean) obj).getValue());
				}
			}
		}
	}

	public double __double__()
	{
		return getValue() ? (new Double(1.0)).doubleValue() : (new Double(0.0)).doubleValue();
	}

	public int __int__()
	{
		return getValue() ? 1 : 0;
	}

    public int __nonzero__()
    {
         return getValue() ? 1 : 0;
    }
}
