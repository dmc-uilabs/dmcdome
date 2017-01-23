/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Dec 18, 2002
 * Time: 12:39:25 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.plugin.mathematica.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.MathematicaNativeCaller;
import mit.cadlab.dome3.plugin.mathematica.MathematicaPlugin;

import java.util.List;

public class MathematicaVector extends AbstractPluginData
{
	public static final String CLASS = "MathematicaVector";
	public static final String GTVTRCOLS = "MathematicaVector::getLength";
	public static final String GTVTRELE = "MathematicaVector::getElement";
	public static final String STVTRELE = "MathematicaVector::setElement";
	public static final String STVTRVAL = "MathematicaVector::setValues";
	public static final String GTVTRVALS = "MathematicaVector::getValues";

	private MathematicaNativeCaller caller;
	private long exVectorObj;   //corresponding C++ object
	private DomeVectorData data;
	private boolean isResult;

	public boolean getIsResult()
	{
		return isResult;
	}

	public void setIsResult(boolean val)
	{
		isResult = val;
	}

	public MathematicaVector(MathematicaNativeCaller caller, long modelPtr, String name, int cols)
	{
		this(caller, modelPtr, name, cols, null);
	}

	public MathematicaVector(MathematicaNativeCaller caller, long modelPtr, String name, int cols, Parameter dVector)
	{
		this.caller = caller;
		parameter = dVector;
		Object[] arr = new Object[2];
		arr[0] = name;
		arr[1] = new Integer(cols);
		exVectorObj = caller.callObjectFunc(MathematicaPlugin.MODEL,
		                                    modelPtr, MathematicaPlugin.CRTVTR, arr);
		if (exVectorObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exVectorObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		//for now all the values are double
		if (dVector == null) {
			data = new DomeVectorData(cols);
			data.setInitialValue(new Double(0.0));
		} else
			data = (DomeVectorData)parameter.getCurrentDataObject();

		isResult = false;
	}

	public void loadNativeData()
	{
		int cols = getSize();
		double[] args = new double[cols];
		for (int j = 0; j < cols; j++) {
			args[j] = getElement(j);
		}
		setValues(args, true);
	}

	public void loadJavaData()
	{
		for (int j = 0; j < getSize(); j++) {
			setElement(j, getElement(j, true));

		}
	}

	// destroy native object if it's still around
	// when finalize called from garbage collection
	public void finalize()
	{
		destroy();
	}

	public String toString()
	{
		return ("MathematicaVector: " + exVectorObj);
	}

	// get columns from java object
	public int getSize()
	{
		return data.getSize();
	}

	// get columns from native object
	public int getSize(boolean isNativeCall)
	{
		if (exVectorObj == 0) {
			throw new IllegalStateException("getSize called on destroyed object");
		}
		return caller.callIntFunc(CLASS, exVectorObj, GTVTRCOLS, null);
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exVectorObj != 0) {
			caller.callDestructor(CLASS, exVectorObj);
			exVectorObj = 0;
		}
	}

	// get values from java object
	public double getElement(int col)
	{
		Number num = data.getItem(col);
		return num.doubleValue();
	}

	// get values from native object
	public double getElement(int col, boolean isNativeCall)
	{
		if (exVectorObj == 0) {
			throw new IllegalStateException("getElement called on destroyed object");
		}
		Object[] arr = new Integer[1];
		arr[0] = new Integer(col);
		return caller.callDoubleFunc(CLASS, exVectorObj, GTVTRELE, arr);
	}

	// set values of java object
	public void setElement(int col, double value)
	{
		data.setItem(col, new Double(value));
	}
/*
 // set values of java object
 public void setElement(int col, double value) {
              System.out.println("setElement value = " + value);
    data.setItem(col, new Double(value));
     System.out.println("setElement data.value = ");
     List val = data.getData();
     for(Iterator i = val.iterator(); i.hasNext();) {
         System.out.println((Double)i.next());
     }
 }
 */
	// set values of native object
	public void setElement(int col, double value, boolean isNativeCall)
	{
		if (exVectorObj == 0) {
			throw new IllegalStateException("setElement called on destroyed object");
		}
		Object[] arr = new Object[2];
		arr[0] = new Integer(col);
		arr[1] = new Double(value);
		caller.callVoidFunc(CLASS, exVectorObj, STVTRELE, arr);
	}

	// set values of java object
	public void setValues(double[] values)
	{
		for (int j = 0; j < values.length; j++) {
			data.setItem(j, new Double(values[j]));
		}
	}

	// set values of native object
	public void setValues(double[] values, boolean isNativeCall)
	{
		if (exVectorObj == 0) {
			throw new IllegalStateException("setValues called on destroyed object");
		}
		Object[] arr = new Object[2];
		arr[0] = values;
		arr[1] = new Integer(values.length);
		caller.callVoidFunc(CLASS, exVectorObj, STVTRVAL, arr);
	}

	// get values from java object
	public List getValues()
	{
		return data.getData();
	}

	// get values from native object
	public double[] getValues(boolean isNativeCall)
	{
		if (exVectorObj == 0) {
			throw new IllegalStateException("getValues called on destroyed object");
		}
		return caller.callDoubleArrayFunc(CLASS, exVectorObj, GTVTRVALS, null);
	}

	public void resetObjectPointer()
	{
		exVectorObj = 0;
	}
}
