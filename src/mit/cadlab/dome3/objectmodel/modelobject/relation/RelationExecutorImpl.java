/*
* Created by IntelliJ IDEA.
* @Author Renu Fondeker
* Date: Aug 9, 2002
*/
/*
TODO: later on load this class with properties file from which we read the java to python class mappings.
*/
package mit.cadlab.dome3.objectmodel.modelobject.relation;

import mit.cadlab.dome3.objectmodel.dataobject.*;
import mit.cadlab.dome3.objectmodel.modelobject.relation.RelationExecutor;
import mit.cadlab.dome3.objectmodel.exceptions.RelationExecutionException;
import mit.cadlab.dome3.util.ClassUtils;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;


public class RelationExecutorImpl implements RelationExecutor
{
	private static final String sep = System.getProperty("file.separator");
	private static final String domeRoot = System.getProperty("DOMEROOT");
	private static final String filePath = domeRoot + sep + "python" + sep + "dataTypes" + sep;
	private static final String filePath2 = domeRoot + sep + "python" + sep + "util" + sep;
	private static final String pyClassPath = "\"" + domeRoot + "\\python\\dataTypes\"";
	private static final String pyClassPath2 = "\"" + domeRoot + "\\python\\util\"";
	private static final String pyClassPath3 = "\"" + domeRoot + "\\python\\3rd-party\\XlsxWriter0.6.5\"";
		
//    private static final String pyRealDivPath = "\"/Python22/lib/\""; //real div requires python 2.2
	private PythonInterpreter interp;
	private String code;
	private HashMap classMap;
	private String relationName;

	/**
	 * @param relationName relation name is used in RelationExecutionException thrown during execution of code
	 */
	public RelationExecutorImpl(String relationName)
	{
		this.relationName = (relationName==null)?"":relationName;
		this.interp = new PythonInterpreter();
		this.code = "";
		loadLib();
        //Qing added Oct 20, to make the python output to message log
        interp.setOut(System.out);
	}

	/**
	 * Clears the variable namespace in the relation.
	 */
	public void clearVariables()
	{
		interp.cleanup();
		classMap.clear();
	}

	/**
	 * Loads the values of the variables into the namespace
	 * Values of old variables with the same name are overridden
	 * The variable namespace is not cleared during this method
	 *
	 * IMPORTANT: The RelationExecutor guarantees that these variable values
	 * are never changed by any action of this class.
	 */
	public void loadVariables(HashMap variables)
	{
		classMap = new HashMap(variables.size());
		for (Iterator i = variables.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			String var = (String) e.getKey();
			Object val = e.getValue();
			classMap.put(var, val.getClass());
			loadInterpreter(var, val);
		}
	}

	/**
	 * Sets the code for this relation (optional)
	 */
	public void setCode(String code)
	{
		code.trim();
//	   System.out.println("code = " + code);
		this.code = code;
	}

	/**
	 * Runs the relation
	 */
	public void run() throws RelationExecutionException
	{
		try {
			interp.exec(code);
		}
		catch (PyException e) {
			handlePythonExecutionException(e);
		}
	}

	/**
	 * Returns the specified variable from the relation
	 * Used to retrieve output variables
	 */
	public Object getVariable(String name)
	{
		PyObject pyObj = interp.get(name);
		Class pyClass = pyObj.getClass();
		Class varClass = (Class) classMap.get(name);
		Object obj = null;
		// get the resultant data object
		if (pyClass.equals(PyInstance.class) || pyClass.equals(PyObjectDerived.class)) {
			// it's a Java object: get the object directly
			obj = interp.get(name, Object.class);
		} else {
			// it's a python object and needs to be converted
			if (pyClass.equals(PyString.class)) { // python string
				String val = pyObj.toString();
				if (varClass.equals(StringData.class))
					obj = new StringData(val);
				else if (varClass.equals(TextData.class))
					obj = new TextData(val);
				else
					throw new UnsupportedOperationException("cannot convert python string to "+ClassUtils.getClassName(varClass));
			}
			if (pyClass.equals(PyInteger.class) || pyClass.equals(PyFloat.class)) { // python number
				double val = 0;
				if (pyClass.equals(PyInteger.class))
					val = ((PyInteger) pyObj).getValue();
				else if (pyClass.equals(PyFloat.class))
					val = ((PyFloat) pyObj).getValue();
				// create a new instance matching the relation parameter's class type
				if (varClass.equals(RealData.class))
					obj = new RealData(val);
				else if (varClass.equals(IntegerData.class))
					obj = new IntegerData((int) val);
				else if (varClass.equals(BooleanData.class))
					obj = new BooleanData(val == 1 ? true : false);
				else
					throw new UnsupportedOperationException("cannot convert python number to " + ClassUtils.getClassName(varClass));
			}
			if (obj == null)
				throw new UnsupportedOperationException("cannot convert "+ClassUtils.getClassName(pyClass)+" to " + ClassUtils.getClassName(varClass));
		}
		return obj;
	}

	private void loadLib()
	{
		try {
			
			interp.exec("import sys");
			interp.exec("sys.path.append(" + pyClassPath.replace('\\', '/') + ")");
			interp.exec("sys.path.append(" + pyClassPath2.replace('\\', '/') + ")");
			interp.exec("sys.path.append(" + pyClassPath3.replace('\\', '/') + ")");
//			interp.exec("sys.path.append(" + "\"" + domeRoot + "/python/3rd-party/XlsxWriter0.6.5\"" + ")");
			
			
//        interp.exec("sys.path.append(" +  pyRealDivPath + ")");
			//interp.exec("print sys.path");
			interp.execfile(filePath2 + "__future__.py");
			interp.execfile(filePath2 + "copy.py");
			interp.execfile(filePath2 + "stat.py");
			System.out.println("execed Stat.py");
			interp.execfile(filePath2 + "linecache.py");
			System.out.println("execed linecache.py");
			interp.execfile(filePath2 + "traceback.py");
			interp.execfile(filePath + "DataType.py");
			//interp.execfile(filePath + "Numeric.py");
			//interp.execfile(filePath + "Number.py");
			//interp.execfile(filePath + "Real.py");
			//interp.execfile(filePath + "Integer.py");
			interp.execfile(filePath + "Boolean.py");
			//interp.execfile(filePath + "Enumerated.py");
			//interp.execfile(filePath + "Matrix.py");
			interp.execfile(filePath + "String.py");
			//interp.execfile(filePath + "Vector.py");
			//interp.execfile(filePath2 + "DFunctionsEnum.py");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import IntegerData");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import RealData");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import DomeVectorData");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import DomeMatrixData");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import DomePreferenceData");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import TextData");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import EnumerationData");
			interp.exec("from mit.cadlab.dome3.objectmodel.dataobject import DomeMath");
			interp.execfile(filePath2 + "DMath.py");
			interp.exec("from DMath import *");
			
		} catch (PyException e) {
			handlePythonLoadingException(e);
		}
	}

	private void loadInterpreter(String var, Object val)
	{
		if (val.getClass() == RealData.class) {
			RealData data = (RealData) val;
			//Reset infinity values to zero as they throw error message in python
			//This is ok since we get infinity value for an output when we divide by
			//zero and the relation does not work the next time
			//so we reset the value of the output below which later gets overwritten
			//by the result obtained from the python interpreter
			if(Double.isInfinite(data.getValue())) {
				data.setValue(0);
			}
			interp.exec(var + " = RealData(" + data.getValue() + ", '" + data.getUnit() + "')");
			return;
		}
		if (val.getClass() == IntegerData.class) {
			IntegerData data = (IntegerData) val;
			interp.exec(var + " = IntegerData(" + data.getValue() + ", '" + data.getUnit() + "')");
			return;
		}
		if (val.getClass() == BooleanData.class) {
			Object cmd = interp.eval("Boolean()");
			BooleanData data = null;
			if (cmd instanceof PyInstance) {
				try {
					data = (BooleanData) ((PyInstance) cmd).__tojava__(
					        Class.forName(
					                "mit.cadlab.dome3.objectmodel.dataobject.BooleanData"));
				} catch (ClassNotFoundException ce) {
					ce.printStackTrace();
				}
			}
			data.setValue(((BooleanData) val).getValue());
			interp.set(var, cmd);
			return;
		}
		if (val.getClass() == EnumerationData.class) {
            EnumerationData data = new EnumerationData((EnumerationData) val);
/*			Object cmd = interp.eval("Enumerated()");
			EnumerationData data = null;
			if (cmd instanceof PyInstance) {
				try {
					data = (EnumerationData) ((PyInstance) cmd).__tojava__(
					        Class.forName(
					                "mit.cadlab.dome3.objectmodel.dataobject.EnumerationData"));
				} catch (ClassNotFoundException ce) {
					ce.printStackTrace();
				}
			}
			data.setEnumeration((EnumerationData) val);*/
			interp.set(var, data);
			return;
		}
		if (val.getClass() == DomeMatrixData.class) {
			DomeMatrixData data = new DomeMatrixData((DomeMatrixData) val);
			/*Object cmd = interp.eval("Matrix()");
			DomeMatrixData data = null;
			if (cmd instanceof PyInstance) {
				try {
					data = (DomeMatrixData) ((PyInstance) cmd).__tojava__(
					        Class.forName(
					                "mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData"));
				}
				catch (ClassNotFoundException ce) {
					ce.printStackTrace();
				}
			}
			data.setData((DomeMatrixData) val);*/
			interp.set(var, data);
			return;
		}
		if (val.getClass() == DomePreferenceData.class) {
			DomePreferenceData data = new DomePreferenceData((DomePreferenceData) val);

			interp.set(var, data);
			return;
		}	
		if (val.getClass() == StringData.class) {
			interp.exec(var + " = String('" + val + "')");
			return;
		}
        if (val.getClass() == TextData.class) {
			interp.set(var, val);
			return;
		}
		if (val.getClass() == DomeVectorData.class) {
			DomeVectorData data = new DomeVectorData((DomeVectorData) val);
			/*Object cmd = interp.eval("Vector()");
			DomeVectorData data = null;
			if (cmd instanceof PyInstance) {
				try {
					data = (DomeVectorData) ((PyInstance) cmd).__tojava__(
					        Class.forName(
					                "mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData"));
				}
				catch (ClassNotFoundException ce) {
					ce.printStackTrace();
				}
			}
			data.setData((DomeVectorData) val);*/
			interp.set(var, data);
			return;
		}
		if (val.getClass() == FileData.class) {
			interp.set(var, val);
			return;
		}
	}

	private void handlePythonLoadingException(PyException e) {
		String msg = "";
		Exception ex = e;
		if (e instanceof PySyntaxError) { // syntax error in loaded files
			try {
				PyObject[] list = ((PyTuple) e.value).getArray();// .list;
				PyObject[] errInfo = ((PyTuple) list[1]).getArray();// .list;
				String fileName = errInfo[0].toString();
				int lineNumber = ((PyInteger) errInfo[1]).getValue();
				msg += "in line " + lineNumber + ": " + fileName + "\n";
				msg += list[0].toString() + ": " + errInfo[3].toString(); // name of error and line
			}
			catch (Exception e1) {
			}
		} else if ((e.type instanceof PyClass) && (e.value instanceof PyString)) { // python error
			String type = ((PyClass) e.type).__name__;
			if (type.equals("IOError")) {
				msg += "Be sure your DOME installation includes a python directory at ";
				msg += domeRoot + sep + "python\n";
				msg += e.value.toString();
			} else { // error in file being loaded
				String fileName = e.traceback.tb_frame.f_code.co_filename;
				int lineNumber = e.traceback.tb_lineno;
				msg += "in line " + lineNumber + ": " + fileName + "\n";
				if ((e.type instanceof PyJavaType)) { // java exception
					ex = (Exception) e.value.__tojava__(Exception.class);
					msg += ex.getMessage();
				}
				else if ((e.type instanceof PyClass) && (e.value instanceof PyString)) { // python error
					msg += ((PyClass) e.type).__name__ + ": ";
					msg += e.value.toString();
				}
			}
		}
		if (msg.equals("")) { // all other errors
			String exMsg = ex.getMessage();
			if (exMsg == null || exMsg.trim().equals(""))
				exMsg = ex.toString();
			msg += exMsg;
		}
		throw new RelationExecutionException(relationName, "Error loading python libraries:\n" + msg, ex);
	}

	private void handlePythonExecutionException(PyException e) {
		boolean useLineNumber = true;
		int lineNumber = e.traceback.tb_lineno;
		String msg;
		Exception ex = e;
		if (e instanceof PySyntaxError) { // syntax error
			try {
				PyObject[] list = ((PyTuple) e.value).getArray();// .list;
				msg = list[0].toString() + ": ";
				PyObject[] errInfo = ((PyTuple) list[1]).getArray();// .list;
				lineNumber = ((PyInteger) errInfo[1]).getValue();
				msg += errInfo[3].toString();
			}
			catch (Exception e1) {
				msg = ex.getMessage();
				if (msg == null || msg.trim().equals(""))
					msg = ex.toString();
			}
		}
		else {
			if ((e.type instanceof PyJavaType)) { // java exception/error
				Object obj = e.value.__tojava__(Object.class);
				if (obj instanceof Throwable) {
					msg = ((Throwable)obj).getMessage();
					if (msg == null)
						msg = obj.toString();
					if (obj instanceof Exception)
						ex = (Exception)obj;
					else { // Error
						useLineNumber = false;
						ex = new RuntimeException((Throwable)obj);
					}
				}
				else {
					msg = e.toString();
				}
			}
			else if ((e.type instanceof PyClass) && (e.value instanceof PyInstance)) { // python exception (e.g. ValueError)
				msg = ((PyClass) e.type).__name__;
				PyTuple args = (PyTuple) e.value.__findattr__("args");
				if (args != null && args.getArray().length>0) {
					msg += ": ";
					msg += tupleToString(args);
				}
			}
			else if ((e.type instanceof PyClass) && (e.value instanceof PyString)) { // python error
				msg = ((PyClass) e.type).__name__ + ": ";
				msg += e.value.toString();
			}
			else if ((e.type instanceof PyString) && (e.value instanceof PyNone)) { // raise string as error
				useLineNumber = false; // don't print line number since these are meant as messages to users of model
				msg = e.type.toString();
			}
			else { // other errors
				msg = ex.getMessage();
				if (msg == null || msg.trim().equals(""))
					msg = ex.toString();
			}
		}
		if (useLineNumber)
			msg = "Error in line " + lineNumber + ":\n" + msg;
		throw new RelationExecutionException(relationName, msg, ex);
	}

	private String tupleToString(PyTuple t) {
		if (t==null)
			return "";
		PyObject[] items = t.getArray();// .list;
		if (items.length==0)
			return "";
		StringBuffer sb = new StringBuffer(items[0].toString());
		for (int i = 1; i < items.length; i++) {
			sb.append(" "+items[i].toString());
		}
		return sb.toString();
	}

}
