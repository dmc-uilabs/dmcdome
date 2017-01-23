package mit.cadlab.dome3.plugin.mathematica.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.mathematica.MathematicaPluginCaller;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 6, 2005
 * Time: 4:30:46 PM
 * To change this template use Options | File Templates.
 */
public class MathematicaVectorNew extends AbstractPluginData
{
	private MathematicaPluginCaller caller;
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

	public MathematicaVectorNew(MathematicaPluginCaller caller, long modelPtr, String name, int cols)
	{
		this(caller, modelPtr, name, cols, null);
	}

	public MathematicaVectorNew(MathematicaPluginCaller caller, long modelPtr, String name, int cols, Parameter dVector)
	{
		this.caller = caller;
		parameter = dVector;
		Object[] arr = new Object[2];
		arr[0] = name;
		arr[1] = new Integer(cols);
		exVectorObj = caller.callObjectFunc(modelPtr, MathematicaPluginCaller.MODEL_CREATE_VECTOR, arr);
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
		return ("MathematicaVectorNew: " + exVectorObj);
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
		return caller.callIntFunc(exVectorObj, MathematicaPluginCaller.VECTOR_GET_LENGTH, null);
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exVectorObj != 0) {
			//caller.callDestructor(CLASS, exVectorObj);
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
		return caller.callDoubleFunc(exVectorObj, MathematicaPluginCaller.VECTOR_GET_ELEMENT, arr);
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
		caller.callVoidFunc(exVectorObj, MathematicaPluginCaller.VECTOR_SET_ELEMENT, arr);
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
		arr[0] = new Integer(values.length);
		arr[1] = values;
		caller.callVoidFunc(exVectorObj, MathematicaPluginCaller.VECTOR_SET_VALUES, arr);
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
		return caller.callDoubleArrayFunc(exVectorObj, MathematicaPluginCaller.VECTOR_GET_VALUES, null);
	}

	public void resetObjectPointer()
	{
		exVectorObj = 0;
	}
}
