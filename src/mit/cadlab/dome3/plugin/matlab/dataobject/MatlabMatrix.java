/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Aug 30, 2002
 * Time: 1:48:19 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package mit.cadlab.dome3.plugin.matlab.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.MatlabNativeCaller;
import mit.cadlab.dome3.plugin.matlab.MatlabPlugin;

import java.util.List;

public class MatlabMatrix extends AbstractPluginData
{
    // see MatlabNativeWorker.h for integer values
    public static final int SET_VALUES	= 12; //"MatlabMatrix::setValues";
    public static final int SET_ELEMENT = 13; //"MatlabMatrix::setElement";
    public static final int SET_ROWS	= 14; //"MatlabMatrix::setRows";
    public static final int SET_COLS	= 15; //"MatlabMatrix::setColumns";
    public static final int SET_DIM		= 16; //"MatlabMatrix::setDimension";
    public static final int GET_VAL		= 17; //"MatlabMatrix::getValues";
    public static final int GET_ELEMENT = 18; //"MatlabMatrix::getElement";
    public static final int GET_ROWS	= 19; //"MatlabMatrix::getRows";
    public static final int GET_COLS	= 20; //"MatlabMatrix::getColumns";
    public static final int GET_DIM		= 21; //"MatlabMatrix::getDimension";

	private MatlabNativeCaller caller;
	private long exMatrixObj;   //corresponding C++ object
	private DomeMatrixData data;
	private boolean isResult;

	public boolean getIsResult()
	{
		return isResult;
	}

	public void setIsResult(boolean val)
	{
		isResult = val;
	}

	public MatlabMatrix(MatlabNativeCaller caller, long modelPtr, String name, int rows, int cols)
	{
		this(caller, modelPtr, name, rows, cols, null);
	}

	public MatlabMatrix(MatlabNativeCaller caller, long modelPtr, String name, int rows, int cols, Parameter matrixParam)
	{
		this.caller = caller;
		this.parameter = matrixParam;
		Object[] arr = new Object[3];
		arr[0] = name;
		arr[1] = new Integer(rows);
		arr[2] = new Integer(cols);
		exMatrixObj = caller.callObjectFunc(MatlabDataObjects.CLASS_MODEL,
		                                    modelPtr, MatlabPlugin.MODEL_CREATE_MATRIX, arr);
		if (exMatrixObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exMatrixObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		if (parameter == null) {
			data = new DomeMatrixData(rows, cols, false, new Double(0));
			//for now all the values are double
		} else
			data = (DomeMatrixData)matrixParam.getCurrentDataObject();

		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exMatrixObj != 0) {
			caller.callDestructor(MatlabDataObjects.CLASS_MATRIX, exMatrixObj);
			exMatrixObj = 0;
		}
	}

	// get dimension from the java object
	public int[] getDimension()
	{
		int[] dimension = new int[2];
		dimension[0] = data.getRowCount();
		dimension[1] = data.getColumnCount();
		return dimension;
	}

	// get dimension from the C++ object - actual spreadsheet
	public int[] getDimension(boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("getDimension called on destroyed object");
		}
		//   return (int[]) caller.callArrayFunc(CLASS, exMatrixObj, GTDIM, null);
		return null;
	}

	// set dimension of java object
	public void setDimension(int rows, int cols)
	{
		data.setRowCount(rows);
		data.setColumnCount(cols);
	}

	// set dimension of the C++ object - actual spreadsheet
	public void setDimension(int rows, int cols, boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("setDimension called on destroyed object");
		}
		Object[] arr = new Integer[2];
		arr[0] = new Integer(rows);
		arr[1] = new Integer(cols);
		caller.callVoidFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj, SET_DIM, arr);
	}

	// get rows from java object
	public int getRows()
	{
		return data.getRowCount();
	}

	// get rows from native object
	public int getRows(boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("getRows called on destroyed object");
		}
		return caller.callIntFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj, GET_ROWS, null);
	}

	// set rows of java object
	public void setRows(int rows)
	{
		data.setRowCount(rows);
	}

	// set rows of native object
	public void setRows(int rows, boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("setRows called on destroyed object");
		}
		Object[] arr = new Integer[1];
		arr[0] = new Integer(rows);
		caller.callVoidFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj, SET_ROWS, arr);
	}

	// get columns from java object
	public int getColumns()
	{
		return data.getColumnCount();
	}

	// get columns from native object
	public int getColumns(boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("getColumns called on destroyed object");
		}
		return caller.callIntFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj, GET_COLS, null);
	}

	// set columns of native object
	public void setColumns(int cols)
	{
		data.setColumnCount(cols);
	}

	// set columns of java object
	public void setColumns(int cols, boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("setColumns called on destroyed object");
		}
		Object[] arr = new Integer[1];
		arr[0] = new Integer(cols);
		caller.callVoidFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj, SET_COLS, arr);
	}

	// get values from java object
	public List getValues()
	{
		return data.getData();
	}

	// get values from native object
	public double[][] getValues(boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("getValues called on destroyed object");
		}
		return caller.call2DimDoubleArrayFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj,
		                                      GET_VAL,
		                                      null);
	}

	// set values of java object
	public void setValues(double[][] values)
	{
		data.setValues(values);
	}

	// set values of native object
	public void setValues(double[][] values, boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("setValues called on destroyed object");
		}
		Object[] arr = new Object[3];
		arr[0] = values;
		arr[1] = new Integer(values.length);
		arr[2] = new Integer(values[0].length);
		caller.callVoidFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj, SET_VALUES, arr);
	}

	// get values from java object
	public double getElement(int row, int col)
	{
		Number num = data.getItem(row, col);
		return num.doubleValue();
	}

	// get values from native object
	public double getElement(int row, int col, boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("getElement called on destroyed object");
		}
		Object[] arr = new Integer[2];
		arr[0] = new Integer(row);
		arr[1] = new Integer(col);
		double result =  caller.callDoubleFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj,
		                                       GET_ELEMENT,
		                                       arr);
		return result;
	}

	// set values of java object
	public void setElement(int row, int col, double value)
	{
		data.setItem(row, col, new Double(value));
	}

	// set values of native object
	public void setElement(int row, int col, double value, boolean isNativeCall)
	{
		if (exMatrixObj == 0) {
			throw new IllegalStateException("setElement called on destroyed object");
		}
		Object[] arr = new Object[3];
		arr[0] = new Integer(row);
		arr[1] = new Integer(col);
		arr[2] = new Double(value);
		caller.callVoidFunc(MatlabDataObjects.CLASS_MATRIX, exMatrixObj, SET_ELEMENT, arr);
	}


	public void loadNativeData()
	{
		int rows = getRows();
		int cols = getColumns();
		double[][] args = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				args[i][j] = getElement(i, j);
			}
		}
		setValues(args, true);
	}

	public void loadJavaData()
	{
		double[][] arr = getValues(true);
		setValues(arr);
	}

	// destroy native object if it's still around
	// when finalize called from garbage collection
	public void finalize()
	{
		destroy();
	}

	public void resetObjectPointer()
	{
		exMatrixObj = 0;
	}

	public String toString()
	{
		return ("MatlabMatrix: " + exMatrixObj);
	}
}