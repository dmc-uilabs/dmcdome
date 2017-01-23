package mit.cadlab.dome3.plugin.groovy.dataobject;

import mit.cadlab.dome3.plugin.excel.ExcelPluginCaller;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.network.server.Debug;

import java.util.List;

/**
 * User: Sangmok Han
 * Date: Aug 15, 2005
 */
public class GroovyMatrix extends GroovyDataObject  {
    private DomeMatrixData data;
    private boolean isResult;
	private DomeMatrixData nativeData;

    public GroovyMatrix() {
        Debug.trace(Debug.ALL, "Catalog Matrix with no argument is called");
	    new GroovyMatrix((Parameter) null);
    }

    public GroovyMatrix(Parameter matrixParam) {
        this.parameter = matrixParam;

        if (parameter == null) {
            data = new DomeMatrixData();
            //for now all the values are double
            data.setInitialValue(new Double(0));
        } else {
            data = (DomeMatrixData) parameter.getCurrentDataObject();
        }
	    /* initialize native data */
	    nativeData = new DomeMatrixData((DomeMatrixData) parameter.getCurrentDataObject());
        isResult = false;
    }

    public GroovyMatrix(DomeMatrixData data) {
        this.data = data;

	    /* initialize native data */
	    nativeData = new DomeMatrixData(data);
        isResult = false;
    }

    public DataObject getData() {
        return nativeData;
    }

    public boolean getIsResult() {
        return isResult;
    }

    public void setIsResult(boolean val) {
        isResult = val;
    }

    // destroy native object if not already done
    public void destroy() {
    }

    // get dimension from the java object
    public int[] getDimension() {
        int[] dimension = new int[2];
        dimension[0] = data.getRowCount();
        dimension[1] = data.getColumnCount();
        return dimension;
    }

    // get dimension from the C++ object - actual spreadsheet
    public int[] getDimension(boolean isNativeCall) {
        int[] dimension = new int[2];
        dimension[0] = nativeData.getRowCount();
        dimension[1] = nativeData.getColumnCount();
        return dimension;
    }

    // set dimension of java object
    public void setDimension(int rows, int cols) {
        data.setRowCount(rows);
        data.setColumnCount(cols);
    }

    // set dimension of the C++ object - actual spreadsheet
    public void setDimension(int rows, int cols, boolean isNativeCall) {
        nativeData.setRowCount(rows);
        nativeData.setColumnCount(cols);
    }

    // get rows from java object
    public int getRows() {
        return data.getRowCount();
    }

    // get rows from native object
    public int getRows(boolean isNativeCall) {
        return nativeData.getRowCount();
    }

    // set rows of java object
    public void setRows(int rows) {
        data.setRowCount(rows);
    }

    // set rows of native object
    public void setRows(int rows, boolean isNativeCall) {
	    nativeData.setRowCount(rows);
    }

    // get columns from java object
    public int getColumns() {
        return data.getColumnCount();
    }

    // get columns from native object
    public int getColumns(boolean isNativeCall) {
		return nativeData.getColumnCount();
    }

    // set columns of native object
    public void setColumns(int cols) {
        data.setColumnCount(cols);
    }

    // set columns of java object
    public void setColumns(int cols, boolean isNativeCall) {
		nativeData.setColumnCount(cols);
    }

    // get values from java object
    public List getValues() {
        return data.getData();
    }

    // get values from native object
    public double[][] getValues(boolean isNativeCall) {
        return data.getDoubleArrayData();
    }

    // set values of java object
    public void setValues(double[][] values) {
        data.setValues(values);
    }

    // set values of native object
    public void setValues(double[][] values, boolean isNativeCall) {
		nativeData.setValues(values);
    }

    // get values from java object
    public double getElement(int row, int col) {
        Number num = data.getItem(row, col);
        return num.doubleValue();
    }

    // get values from native object
    public double getElement(int row, int col, boolean isNativeCall) {
        Number num = nativeData.getItem(row, col);
        return num.doubleValue();
    }

    // set values of java object
    public void setElement(int row, int col, double value) {
        data.setItem(row, col, new Double(value));
    }

    // set values of native object
    public void setElement(int row, int col, double value, boolean isNativeCall) {
        nativeData.setItem(row, col, new Double(value));
    }


    public void loadNativeData() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                setElement(i, j, getElement(i, j), true);
            }
        }
    }

    public void loadJavaData() {
        double[][] arr = getValues(true);
        setValues(arr);
    }

    public void finalize() {
        destroy();
    }

    public void resetObjectPointer() {
    }

    public String toString() {
        return ("GroovyMatrix: row size = " + nativeData.getRowCount() + ", col size = " + nativeData.getColumnCount());
    }
}
