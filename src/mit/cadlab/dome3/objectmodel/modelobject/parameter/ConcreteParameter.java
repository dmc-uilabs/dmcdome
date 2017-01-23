/*
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Oct 22, 2002
 * Time: 4:36:53 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.exceptions.BuildMappingValuePropagationException;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DSet;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.util.List;

/**
 * This has elements of the basic Parameter as well as elements for Build mode.
 * These two functionalities should be split apart and this one renamed to BuildParameter
 */
public class ConcreteParameter extends AbstractParameter
{
	private DataObjectFactory factory;
	private Parameter changeCause; // used to prohibit back flow of data
	private List changeListeners = new DSet(); // build connection mapping managers for making mappings live

	public ConcreteParameter(ModelObjectScope scope, Id id, String dataType)
	{
		super(scope, id, dataType);
		initParameter();
	}

	public ConcreteParameter(ModelObjectScope scope, Id id)
	{
		super(scope, id, (String) null);
		initParameter();
	}

	public ConcreteParameter(ModelObjectScope scope, Id id, Parameter param)
	{
		super(scope, id, param);
		if (scope instanceof ModelInterface || scope instanceof ToolInterface) {
			DataObject dObj = getCurrentDataObject();
			if (dObj instanceof FileData) { // strip file data in interfaces of file path
				File dFile = new File(((FileData)dObj).getFilePath());
				((FileData)dObj).setFilePath(dFile.getName());
			}
		}
		initParameter();
	}

	public ConcreteParameter(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
		initParameter();
	}

	public DataObjectFactory getDataObjectFactory()
	{
		if (factory == null) {
			factory = new DataObjectBaseFactory();
		}
		return factory;
	}

	protected AbstractParameter.InternalDataObjectListener createInternalDataObjectListener()
	{
		return new ConcreteParameterInternalDataObjectListener();
	}

	class DataObjectBaseFactory implements DataObjectFactory
	{
		public DataObject newInstance(String dObjType)
		{
			try {
				return (DataObject) Registry.getConstructor(dObjType, Registry.BASE_CLS).newInstance(null);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}

		public DataObject newInstance(DataObject dObj)
		{
			try {
				return (DataObject) Registry.getConstructor(dObj, Registry.BASE_CLS).newInstance(new Object[]{dObj});
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}

		public DataObject newInstance(Element xml)
		{
			try {
				Constructor ctr = Registry.getConstructor(xml, Registry.BASE_CLS);
				return (DataObject) ctr.newInstance(new Object[]{xml});
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}
	}

	public void addChangeListener(DataObjectChangeListener changeListener)
	{
		synchronized (changeListeners) {
			changeListeners.add(changeListener);
		}
	}

	public void removeChangeListener(DataObjectChangeListener changeListener)
	{
		synchronized (changeListeners) {
			changeListeners.remove(changeListener);
		}
	}

	// used for making mappings "live" in build mode
	protected class ConcreteParameterInternalDataObjectListener extends InternalDataObjectListener
	{
		protected void addToDataObject(DataObject dObj)
		{
			target = dObj;
			target.addPropertyChangeListener(this);
		}

		protected void removeDataObject()
		{
			target.removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			DataObjectChangeEvent event = new DataObjectChangeEvent(ConcreteParameter.this, changeCause, evt);
			synchronized(changeListeners) {
				for (int i = 0; i < changeListeners.size(); i++)
					((DataObjectChangeListener) changeListeners.get(i)).dataObjectChanged(event);
			}
		}
	}

	/**
	 * This method is to be called for making mappings live.
	 * This method can only be called by one thread at a time to preserve value of "cause".
	 */
	public synchronized void shadowDataChange(DataObjectChangeEvent event) {
		this.changeCause = (Parameter)event.getSource(); // the source of the event is the cause for why this parameter is changing
		PropertyChangeListener shadowListener = null;
		ModelObjectScope scope = getScope();
		if (scope instanceof ModelObject)
			scope = ((ModelObject)scope).getScope(); // should be the top level scope!
		if (scope instanceof ModelInterfaceBuilder) { // interface mapping
			ModelInterfaceBuilder iface = (ModelInterfaceBuilder) scope;
			if (iface.isDefaultInterface() || getCurrentDataObject() instanceof DomeFile) {
				shadowListener = getCurrentDataObject().getValueUnitShadowListener();
			}
			else
				shadowListener = getCurrentDataObject().getValueShadowListener();
		}
		else if (scope instanceof InterfaceModelView) {
			shadowListener = getCurrentDataObject().getValueUnitShadowListener();
		}
		else { // model mapping
			shadowListener = getCurrentDataObject().getValueShadowListener();
		}
		try {
			shadowListener.propertyChange(event.getEvent());
		}
		catch (Exception e) {
			throw new BuildMappingValuePropagationException(changeCause, this, event.getEvent(), e);
		}
	}

}
