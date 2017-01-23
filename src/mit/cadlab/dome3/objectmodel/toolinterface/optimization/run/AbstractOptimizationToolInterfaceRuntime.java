package mit.cadlab.dome3.objectmodel.toolinterface.optimization.run;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.OptimizationToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.config.Registry;

import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.dom4j.Element;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 25, 2003
 * Time: 6:58:50 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractOptimizationToolInterfaceRuntime extends OptimizationToolInterfaceBase
{
    CompoundId _runtimeId = new CompoundId();
    protected HashMap _interfaceObjectsFlatMap = new HashMap();  // flat list of all model objects, including those in relations
    protected boolean _m_makeConsistent = true;  // interface runs the model when opened;

    public AbstractOptimizationToolInterfaceRuntime(Model m, Id id)
	{
		super(m, id);
	}

	public AbstractOptimizationToolInterfaceRuntime(Model m, String id)
	{
		super(m, new Id(id));
	}

	public AbstractOptimizationToolInterfaceRuntime(Model m, Id id, ModelObjectScope mObjScope)
	{
		super(m, id, mObjScope);
	}

	public AbstractOptimizationToolInterfaceRuntime(Model m, Element xmlContent, Element xmlMapping)
	{
		super(m, xmlContent, xmlMapping);
	}

	public Map getInterfaceObjectsFlatMap()
	{
		return Collections.unmodifiableMap(_interfaceObjectsFlatMap);
	}

	public boolean getMakeConsistent ()
	{
        return _m_makeConsistent;
	}

	public void resetMakeConsistent ()
	{
        _m_makeConsistent = false;
	}

	/**
	 * Create a flat list (hashmap) of all interface objects, including relation parameters.
	 */
	protected void populateInterfaceObjectFlatMap()
	{
		for (Iterator mObjIter = modelObjects.iterator(); mObjIter.hasNext();)
        {
            ModelObject mObj = (ModelObject) mObjIter.next();
            if (mObj instanceof Parameter)
                _interfaceObjectsFlatMap.put(mObj.getId(), mObj);
        }
	}

	public String getXmlDescription()
	{
		return lastSavedXml;
	}

	public String getLiveXmlDescription()
	{
		String xml = toXmlElement().asXML();
		return xml;
	}

	/**
	 * Get a parameter's data object values using reflection.
	 * @param objectId Id of the parameter
	 * @return Data values
	 */
	public List getItem(String objectId)
	{
		Id objId = new Id(objectId);
		Parameter mObj = (Parameter) _interfaceObjectsFlatMap.get(objId);
		DataObject dObj = mObj.getCurrentDataObject();

		return dObj.getValues();
	}

	/**
	 * Prepare an argument list for reflection. This is necessary because the argument lists
	 * passed to setItem and getItem will only contain types that can be handled by XML-RPC.
	 * Some types will have to be converted from objects to primitives (e.g., Integer to int)
	 * and some types will have to be converted to superclasses (e.g., Integer to Number).
	 * @param methodName Method name to be invoked
	 * @param obj Object on which to invoke the method
	 * @param args Argument list
	 * @return New set of parameter types compatible with the method to be invoked
	 */
	protected Class[] getMethodArgTypes(String methodName, Object obj, List args)
	{
		// store the argument class types
		int i = 0;
		Class[] paramClassTypes = new Class[args.size()];
		for (Iterator typesIter = args.iterator(); typesIter.hasNext(); i++) {
			Object type = typesIter.next();
			paramClassTypes[i] = type.getClass();
		}

		// try to find the matching method
		boolean methodMatch = false;
		Class[] newParamClassTypes = new Class[paramClassTypes.length];
		Method[] dObjMethods = obj.getClass().getMethods();
		for (i = 0; i < dObjMethods.length; i++) {
			// does the method name match?
			String name = dObjMethods[i].getName();
			if (name.equals(methodName)) {
				// yes, now what about the parameter types?
				int j;
				Class[] argTypes = dObjMethods[i].getParameterTypes();
				for (j = 0; j < argTypes.length; j++) {
					// check primitive types
					if (argTypes[j].isPrimitive()) {
						if (argTypes[j].equals(int.class) &&
						        paramClassTypes[j].equals(Integer.class))
							newParamClassTypes[j] = int.class;
						else if (argTypes[j].equals(double.class) &&
						        paramClassTypes[j].equals(Double.class))
							newParamClassTypes[j] = double.class;
						else if (argTypes[j].equals(boolean.class) &&
						        paramClassTypes[j].equals(Boolean.class))
							newParamClassTypes[j] = boolean.class;
						else
							break; // no match
					} else
					// does the parameter class type match?
						if (argTypes[j].equals(paramClassTypes[j]))
							newParamClassTypes[j] = paramClassTypes[j];
						else
						// parameter class doesn't match; how about its superclass?
							if (argTypes[j].equals(paramClassTypes[j].getSuperclass()))
								newParamClassTypes[j] = paramClassTypes[j].getSuperclass();
							else
								break;
				}
				// the method matches if all the parametersm match
				if (j == argTypes.length)
					methodMatch = true;
			}

			// we found a matching method, so stop
			if (methodMatch) break;
		}

		return (methodMatch ? newParamClassTypes : null);
	}

	public ModelObjectFactory getModelObjectFactory()
	{
		if (m_moFactory == null)
			m_moFactory = new RuntimeInterfaceObjectFactory();
		return m_moFactory;
	}

	public CompoundId getRuntimeId()
	{
		return _runtimeId;
	}

    protected class RuntimeInterfaceObjectFactory implements ModelObjectFactory
	{
		/**
		 * obj is either object type name, type symbol, or object instance
		 * params must be the parameters to the constructor method in correct order
		 */
		public ModelObject newInstance(Object obj, Object[] params)
		{
			try {
				Constructor ctr = null;
				try {
					ctr = Registry.getConstructor(obj, Registry.SERVER_INTERFACE_CLS);
				} catch (Exception e) {
					try {
						ctr = Registry.getConstructor(obj, Registry.SERVER_CLS);
					} catch (Exception e1) {
						ctr = Registry.getConstructor(obj, Registry.BASE_CLS);
					}
				}
				return (ModelObject) ctr.newInstance(params);
			} catch (InstantiationException e) {
				System.err.println("newInstance: " + e + "\t" + obj);
			} catch (IllegalAccessException e) {
				System.err.println("newInstance: " + e + "\t" + obj);
			} catch (IllegalArgumentException e) {
				System.err.println("newInstance: " + e + "\t" + obj);
			} catch (InvocationTargetException e) {
				System.err.println("newInstance: " + e + "\t" + obj);
			}
			return null;
		}
	}

}
