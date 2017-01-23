// SimpleData.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package test.jython;

public class SimpleData {

    private String name;
    private double value;

    public SimpleData() {
        System.out.println("SimpleData constructor");
        name = "no name";
        value = 0;
    }

    public SimpleData(String name, int value) {
        this.name = name;
        this.value = (double)value;
    }

    public SimpleData(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String toString() {
        return name+": "+value;
    }

	public Object __add__(Object obj)
	{
		if (obj instanceof SimpleData) {
			SimpleData that = (SimpleData) obj;
			return new SimpleData(this.name+that.name,this.value+that.value);
		} else if (obj instanceof Number) {
			Number that = (Number) obj;
			return new SimpleData(this.name+that,this.value+that.doubleValue());
		} else {
			throw new IllegalArgumentException("Can't add "+this+" to "+ obj);
		}
	}
}
