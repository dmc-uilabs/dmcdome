// TestUnits.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.jython.DataType;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;

import org.python.util.PythonInterpreter;
import edu.iupui.rg.ucum.units.Unit;

public class TestUnits
{

	public static void multiplyUnits() {
        RealData x = new RealData(1, new Unit("[Ft_p_s_p_degF]"));
        RealData y = new RealData(70, new Unit("[degF]"));
/*		IntegerData w = new IntegerData(5720);
		IntegerData n = new IntegerData(7590);
		IntegerData e = new IntegerData(11440);*/
		PythonInterpreter interp = new PythonInterpreter();
		interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import IntegerData");
		interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import RealData");
		interp.set("x", x);
		interp.set("y", y);
/*		interp.set("n", n);
		interp.set("e", e)*/;
		interp.exec("d1 = x*y");
		interp.exec("print d1");
/*		interp.exec("d2 = s-n");
		interp.exec("print d2");
		interp.exec("p1 = d1*d2");
		interp.exec("print p1");
		interp.exec("f = p1/1000000");
		interp.exec("print f");
		Object obj = interp.get("f",Object.class);
		System.out.println(obj.getClass().getName());
		System.out.println(obj);*/
	}

	public static void badConversion() {
		IntegerData s = new IntegerData(3,"IN");
		IntegerData w = new IntegerData(2,"IN");
		PythonInterpreter interp = new PythonInterpreter();
		interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import IntegerData");
		interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import RealData");
		interp.set("s", s);
		interp.set("w", w);
		interp.exec("p = s*w");
		interp.exec("print p");
		Object obj = interp.get("p", Object.class);
		System.out.println(obj.getClass().getName());
		IntegerData p1 = (IntegerData)obj;
		IntegerData p2 = new IntegerData(0,"FT");
		System.out.println(p1);
		double newValue = p2.getUnit().convertFrom(p1.getValue(),p1.getUnit());
		System.out.println(newValue);
		System.out.println(p1.getUnit().dim());
		System.out.println(p2.getUnit().dim());
	}

	public static void main(String[] args)
	{
		DomeInit.loadUnits();
		multiplyUnits();
		//badConversion();
	}

}
