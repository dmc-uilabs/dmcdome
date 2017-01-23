package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 8, 2003
 * Time: 3:58:32 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractModelInterfaceRuntime extends ModelInterfaceBase
{
	CompoundId runtimeId = new CompoundId();
	protected HashMap interfaceObjectsFlatMap = new HashMap();  // flat list of all model objects, including those in relations

	public AbstractModelInterfaceRuntime(Model m, Id id)
	{
		super(m, id);
	}

	public AbstractModelInterfaceRuntime(Model m, String id)
	{
		super(m, new Id(id));
	}

	public AbstractModelInterfaceRuntime(Model m, Id id, ModelObjectScope mObjScope)
	{
		super(m, id, mObjScope);
	}

	public AbstractModelInterfaceRuntime(Model m, Element xmlContent, Element xmlMapping)
	{
		super(m, xmlContent, xmlMapping);
	}

	public Map getInterfaceObjectsFlatMap()
	{
		return Collections.unmodifiableMap(interfaceObjectsFlatMap);
	}

    /**
     * do clean up 'interfaceObjectsFlatMap' and 'data object' in this interface
     */
    public void cleanup() {
        super.cleanup();
        interfaceObjectsFlatMap.clear();
    }

	/**
	 * Create a flat list (hashmap) of all interface objects, including relation parameters.
	 */
	protected void populateInterfaceObjectFlatMap()
	{
//TODO add parameters in contexts to the map
		for (Iterator mObjIter = modelObjects.iterator(); mObjIter.hasNext();) {
			ModelObject mObj = (ModelObject) mObjIter.next();
			if (mObj instanceof Parameter)
				interfaceObjectsFlatMap.put(mObj.getId(), mObj);
			else if (mObj instanceof Relation) {
				interfaceObjectsFlatMap.put(mObj.getId(), mObj);
				Collection params = ((Relation) mObj).getModelObjects();
				for (Iterator paramIter = params.iterator(); paramIter.hasNext();) {
					Parameter p = (Parameter) paramIter.next();
					interfaceObjectsFlatMap.put(p.getId(), p);
				}
			}
			else if(mObj instanceof Visualization)
				interfaceObjectsFlatMap.put(mObj.getId(), mObj);

		}
		for (Iterator mObjIter = getModelViewObjects().iterator(); mObjIter.hasNext();) {
			ModelObject mObj = (ModelObject) mObjIter.next();
			if (mObj instanceof Parameter)
				interfaceObjectsFlatMap.put(mObj.getId(), mObj);
			else if (mObj instanceof Relation) {
				interfaceObjectsFlatMap.put(mObj.getId(), mObj);
				Collection params = ((Relation) mObj).getModelObjects();
				for (Iterator paramIter = params.iterator(); paramIter.hasNext();) {
					Parameter p = (Parameter) paramIter.next();
					interfaceObjectsFlatMap.put(p.getId(), p);
				}
			}
			else if (mObj instanceof Visualization)
				interfaceObjectsFlatMap.put(mObj.getId(), mObj);

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
		Object obj = interfaceObjectsFlatMap.get(objId);
		if (obj instanceof Parameter) {
			Parameter mObj = (Parameter) obj;
			DataObject dObj = mObj.getCurrentDataObject();

			return dObj.getValues();
		} else
			return null;
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
		return runtimeId;
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
