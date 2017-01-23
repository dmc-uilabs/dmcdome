/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Aug 30, 2002
 * Time: 1:48:19 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package mit.cadlab.dome3.plugin.excel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ExcelNativeCaller;
import mit.cadlab.dome3.plugin.excel.ExcelPlugin;

import java.util.List;

public class ExcelMatrix extends AbstractPluginData
{
	public static final String CLASS = "ExcelMatrix";
	public static final String GTDIM = "ExcelMatrix::getDimension";
	public static final String STDIM = "ExcelMatrix::setDimension";
	public static final String GTROWS = "ExcelMatrix::getRows";
	public static final String STROWS = "ExcelMatrix::setRows";
	public static final String GTCOLS = "ExcelMatrix::getColumns";
	public static final String STCOLS = "ExcelMatrix::setColumns";
	public static final String GTVAL = "ExcelMatrix::getValues";
	public static final String STVAL = "ExcelMatrix::setValues";
	public static final String STELE = "ExcelMatrix::setElement";
	public static final String GTELE = "ExcelMatrix::getElement";

	private ExcelNativeCaller caller;
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

	public ExcelMatrix(ExcelNativeCaller caller, long modelPtr, String sheet, String range)
	{
		this(caller, modelPtr, sheet, range, null);
	}

	public ExcelMatrix(ExcelNativeCaller caller, long modelPtr, String sheet, String range, Parameter matrixParam)
	{
		this.caller = caller;
		this.parameter = matrixParam;
		String[] arr = new String[2];
		arr[0] = sheet;
		arr[1] = range;
		exMatrixObj = caller.callObjectFunc(ExcelPlugin.MODEL,
		                                    modelPtr, ExcelPlugin.CRTMTX, arr);
		if (exMatrixObj == -1) {
			throw new RuntimeException("Argument number mismatch");
		} else if (exMatrixObj == -2) {
			throw new RuntimeException("Argument type mismatch");
		}

		if (parameter == null) {
			data = new DomeMatrixData();
			//for now all the values are double
			data.setInitialValue(new Double(0));
		} else
			data = (DomeMatrixData)parameter.getCurrentDataObject();
		isResult = false;
	}

	// destroy native object if not already done
	public void destroy()
	{
		if (exMatrixObj != 0) {
			caller.callDestructor(CLASS, exMatrixObj);
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
		return caller.callIntArrayFunc(CLASS, exMatrixObj, GTDIM, null);
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
		caller.callVoidFunc(CLASS, exMatrixObj, STDIM, arr);
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
		return caller.callIntFunc(CLASS, exMatrixObj, GTROWS, null);
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
		caller.callVoidFunc(CLASS, exMatrixObj, STROWS, arr);
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
		return caller.callIntFunc(CLASS, exMatrixObj, GTCOLS, null);
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
		caller.callVoidFunc(CLASS, exMatrixObj, STCOLS, arr);
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
		return caller.call2DimDoubleArrayFunc(CLASS, exMatrixObj, GTVAL, null);
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
		Object[] arr = new Object[1];
		arr[0] = values;
		caller.callVoidFunc(CLASS, exMatrixObj, STVAL, arr);
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
		return caller.callDoubleFunc(CLASS, exMatrixObj, GTELE, arr);
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
		caller.callVoidFunc(CLASS, exMatrixObj, STELE, arr);
	}


	public void loadNativeData()
	{
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getColumns(); j++) {
				setElement(i, j, getElement(i, j), true);
			}
		}
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

	public void resetObjectPointer() {
		exMatrixObj = 0;
	}

	public String toString()
	{
		return ("ExcelMatrix: " + exMatrixObj);
	}
}