package mit.cadlab.dome3.plugin.mathematica.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.mathematica.MathematicaPluginCaller;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 6, 2005
 * Time: 4:55:19 PM
 * To change this template use Options | File Templates.
 */
public class MathematicaMatrixNew extends AbstractPluginData
{
	private MathematicaPluginCaller caller;
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

	public MathematicaMatrixNew(MathematicaPluginCaller caller, long modelPtr, String name, int rows, int cols)
	{
		this(caller, modelPtr, name, rows, cols, null);
	}

	public MathematicaMatrixNew(MathematicaPluginCaller caller, long modelPtr, String name, int rows, int cols, Parameter dMatrix)
	{
		this.caller = caller;
		parameter = dMatrix;
		Object[] arr = new Object[3];
		arr[0] = name;
		arr[1] = new Integer(rows);
		arr[2] = new Integer(cols);
		exMatrixObj = caller.callObjectFunc(modelPtr, MathematicaPluginCaller.MODEL_CREATE_MATRIX, arr);
		if (exMatrixObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exMatrixObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}
		//for now all the values are double
		if (dMatrix == null) {
			data = new DomeMatrixData(rows, cols, false, new Double(0));
			//for now all the values are double
		} else
			data = (DomeMatrixData)parameter.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exMatrixObj != 0) {
			//caller.callDestructor(CLASS, exMatrixObj);
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
		return (int[]) caller.callIntArrayFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_GET_DIM, null);
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
		caller.callVoidFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_SET_DIM, arr);
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
		return caller.callIntFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_GET_ROWS, null);
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
		caller.callVoidFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_SET_ROWS, arr);
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
		return caller.callIntFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_GET_COLS, null);
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
		caller.callVoidFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_SET_COLS, arr);
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
		return caller.call2DimDoubleArrayFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_GET_VALUES, null);
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
		caller.callVoidFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_SET_VALUES, arr);
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
		return caller.callDoubleFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_GET_ELEMENT, arr);
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
		caller.callVoidFunc(exMatrixObj, MathematicaPluginCaller.MATRIX_SET_ELEMENT, arr);
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
		double[][] arr = new double[getRows()][getColumns()];
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getColumns(); j++) {
				arr[i][j] = getElement(i, j, true);
			}
		}
		setValues(arr);
	}

	// destroy native object if it's still around
	// when finalize called from garbage collection
	public void finalize()
	{
		destroy();
	}

	public void resetObjectPointer() {
		exMatrixObj = 0;
	}

	public String toString()
	{
		return ("MathematicaMatrixNew: " + exMatrixObj);
	}
}