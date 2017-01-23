// RealIterationVariable.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.IterationVariable;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import edu.iupui.rg.ucum.units.Unit;
import org.dom4j.Element;

public class RealIterationVariable extends RealData implements IterationVariable
{
	public static final String INCREMENT = "increment";
	public static final String LIMIT = "limit";

	protected double increment = 1.0;
	protected double limit = 10.0;

	public RealIterationVariable()
	{
		this("s");
	}

	public RealIterationVariable(String unit)
	{
		super(unit);
	}

	public RealIterationVariable(IterationVariable v)
	{
		super((RealIterationVariable)v);
		this.increment = ((RealIterationVariable)v).getIncrement();
		this.limit = ((RealIterationVariable) v).getLimit();
	}

	public RealIterationVariable(double v)
	{
		super(v);
	}

	public RealIterationVariable(Double v)
	{
		super(v);
	}

	public RealIterationVariable(double v, String unit)
	{
		super(v, unit);
	}

	public RealIterationVariable(double v, Unit unit)
	{
		super(v, unit);
	}

	public RealIterationVariable(Element xmlElement)
	{
		super(xmlElement);
		Element incXml = (Element) xmlElement.selectSingleNode("/dataobject/"+INCREMENT);
		this.increment = Double.parseDouble(incXml.getText());
		Element limitXml = (Element) xmlElement.selectSingleNode("/dataobject/" + LIMIT);
		this.limit = Double.parseDouble(limitXml.getText());
	}

	public double getIncrement()
	{
		return increment;
	}

	public void setIncrement(double increment)
	{
		Double oldIncrement = new Double(this.increment);
		this.increment = increment;
		firePropertyChange(INCREMENT, oldIncrement, new Double(increment));
	}

	public double getLimit()
	{
		return limit;
	}

	public void setLimit(double limit)
	{
		Double oldLimit = new Double(this.limit);
		this.limit = limit;
		firePropertyChange(LIMIT, oldLimit, new Double(limit));
	}

	public boolean tick() {
		if (this.getValue() < limit) { // only tick if limit hasn't been reached yet
			this.setValue(this.getValue() + this.increment);
			firePropertyChange(TICK);
			return true;
		}
		return false;
	}

	protected TypeInfo getTypeInfo()
	{
		return IterationVariable.TYPE_INFO;
	}

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		xml.addElement(INCREMENT).addText(String.valueOf(this.increment));
		xml.addElement(LIMIT).addText(String.valueOf(this.limit));
		return xml;
	}

	public String toString()
	{
		return super.toString()+"\tincrement: "+this.increment+"\tlimit: "+this.limit;
	}

}
