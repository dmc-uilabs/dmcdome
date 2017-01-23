/*
* Created by IntelliJ IDEA.
* User: administrator
* Date: Oct 31, 2002
* Time: 1:01:08 PM
* To change this template use Options | File Templates.
*/
package mit.cadlab.dome3.objectmodel;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ModelObjectBaseFactory implements ModelObjectFactory
{
	Object[] argument;

	public ModelObject newInstance(Object obj, Object[] args)
	{
		if ((obj instanceof String) && Registry.isValidDataObjectType((String) obj)) {
			try {
				argument = new Object[args.length + 1];
				System.arraycopy(args, 0, argument, 0, args.length);
				argument[args.length] = obj;
				return (ModelObject) Registry.getConstructor("Parameter", Registry.BASE_CLS).newInstance(argument);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (IllegalArgumentException e) {
				Class claz = null;
				try {
					claz = Class.forName("mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter");
				} catch (ClassNotFoundException cne) {
				}
				Object mObject = null;
				Constructor[] cons = claz.getConstructors();
				for (int i = 0; i < cons.length; i++) {
					try {
						mObject = cons[i].newInstance(argument);
					} catch (InstantiationException ie) {
						e.printStackTrace();
					} catch (IllegalAccessException ile) {
					} catch (IllegalArgumentException ilae) {
						continue;
					} catch (InvocationTargetException ive) {
					}
					return (ModelObject) mObject;
				}
			}
			return null;
		}
		//if not a DataOject, do the following
		try {
			return (ModelObject) Registry.getConstructor(obj, Registry.BASE_CLS).newInstance(args);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}