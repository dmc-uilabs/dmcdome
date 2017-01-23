// ParameterRuntime.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.ServerInterfaceFileData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;


public class InterfaceParameterRuntime extends ParameterRuntime
{

	protected InterfaceParameterRuntimeChangeSupport changeListeners = new InterfaceParameterRuntimeChangeSupport(this);
	protected ModelParameterChangeListener modelParamListener;
	protected boolean isIndependent = false;

	public void delete(DeletionListener notifier) {
	    super.delete(notifier);

        changeListeners.clearListeners();
        changeListeners.removeModelParameterListener();
        modelParamListener = null; // this will make modelParamListener be gabage-collected to release its reference to this object
    }


	public InterfaceParameterRuntime(ModelObjectScope scope, Id id, String dataType)
	{
		super(scope, id, dataType);
	}

	public InterfaceParameterRuntime(ModelObjectScope scope, Id id)
	{
		super(scope, id, (String) null);
	}

	public InterfaceParameterRuntime(ModelObjectScope scope, Id id, Parameter param)
	{
		super(scope, id, param);
	}

	public InterfaceParameterRuntime(ModelObjectScope scope, Element xmlElement)
	{
		super(scope, xmlElement);
	}

	protected void initParameter()
	{
		super.initParameter();
		addPropertyModifiedListener(Parameter.VALUE_STATUS, dataObjectListener);
	}

	protected DataObjectFactory createDataObjectFactory()
	{
		return new DataObjectSvrInterfaceFactory();
	}

	protected ParameterRuntime.InternalDataObjectListener createInternalDataObjectListener()
	{
		return new InterfaceParameterInternalDataObjectListener();
	}

	/**
	 * should only be able to call this once; undefined behavior if called multiple times
	 * @param mdlParam
	 */
	public void setModelParameter(ModelParameterRuntime mdlParam)
	{
		if (CausalityStatus.INDEPENDENT.equals(mdlParam.getModel().getCausality(mdlParam))) {
			this.isIndependent = true;
		}
		if (mdlParam.getCurrentDataObject() instanceof DomeFile) { // make the interface parameter have the same path as the model parameter
			ServerInterfaceFileData ifaceDta = (ServerInterfaceFileData) getCurrentDataObject();
			File origIfaceFile = new File(ifaceDta.getFilePath());

            ifaceDta.setFileName(origIfaceFile.getName()); // keep original interface file name for use in clients

            /* patch code for unexpected behavior
               above code is written under assumption that
               ifaceDta.getFilePath() returns full file path(dir name+file name).
               however, the return of ifaceDta.getFilePath() contains only file name
               or 'wrong directoy name (the dir used when it was built)  + file name.'
               below code will fix this problem using working directory information
               obtained from DomeModelRuntime.getWorkingDirectory() */

            // before patching
            // ((DomeFile) this.getCurrentDataObject()).setFilePath(((DomeFile) mdlParam.getCurrentDataObject()).getFilePath());

            // after patching starts
            if (ifaceDta.getFilePath().equals(origIfaceFile.getName()) || ! origIfaceFile.exists()) {
                // when file path does not contain directory information or when ifaceDta.getFilePath() does not exist
                String workingDir = null;
                if (mdlParam.getScope() instanceof DomeModelRuntime) {
                    workingDir = ((DomeModelRuntime) mdlParam.getScope()).getWorkingDirectory().getAbsolutePath();
                } if (mdlParam.getScope() instanceof PluginModelRuntime) {
                    workingDir = ((PluginModelRuntime) mdlParam.getScope()).getWorkingDirectory().getAbsolutePath();
                } else if (mdlParam.getScope() instanceof ProceduralRelation) {
                    ProceduralRelation relationScope = (ProceduralRelation) mdlParam.getScope();
                    workingDir = ((DomeModelRuntime) relationScope.getScope()).getWorkingDirectory().getAbsolutePath();
                } else if (mdlParam.getScope() instanceof SubscriptionInterface) {
                    SubscriptionInterface subsItf = (SubscriptionInterface) mdlParam.getScope();
                    ModelObjectScope modelObjScope = subsItf.getSubscription().getScope();
                    workingDir = ((DomeModelRuntime) modelObjScope).getWorkingDirectory().getAbsolutePath();
                }

                if (workingDir != null) {
                    String dirSeparator = ((workingDir.indexOf("\\") != -1) ? "\\" : "/");
                    ((DomeFile) this.getCurrentDataObject()).setFilePath(workingDir + dirSeparator + origIfaceFile.getName());
                    ((DomeFile) mdlParam.getCurrentDataObject()).setFilePath(workingDir + dirSeparator + origIfaceFile.getName());
                } else {
                    ((DomeFile) this.getCurrentDataObject()).setFilePath(((DomeFile) mdlParam.getCurrentDataObject()).getFilePath());
                    ((DomeFile) mdlParam.getCurrentDataObject()).setFilePath(((DomeFile) mdlParam.getCurrentDataObject()).getFilePath());
                }
            } else {
                ((DomeFile) this.getCurrentDataObject()).setFilePath(((DomeFile) mdlParam.getCurrentDataObject()).getFilePath());
            }
            // after patching ends



		}
		changeListeners.addModelParameterListener(mdlParam.getInterfaceParameterListener());
		mdlParam.addInterfaceListener(getModelParameterChangeListener());
	}

	/**
	 * assumes mdlParam is the model parameter that was previously set via setModelParameter
	 * @param mdlParam
	 */
	public void removeModelParameter(ModelParameterRuntime mdlParam)
	{
		changeListeners.removeModelParameterListener();
		mdlParam.removeInterfaceListener(getModelParameterChangeListener());
	}

	protected ModelParameterChangeListener getModelParameterChangeListener()
	{
		if (modelParamListener == null)
			modelParamListener = new ModelParameterChangeListener();
		return modelParamListener;
	}

	public void addSubscriptionListener(DataObjectChangeListener listener)
	{
		changeListeners.addSubscriptionListener(listener);
	}

	public void removeSubscriptionListener(DataObjectChangeListener listener)
	{
		changeListeners.removeSubscriptionListener(listener);
	}

	public void addClientListener(DataObjectChangeListener listener)
	{
		changeListeners.addClientListener(listener);
	}

	public void removeClientListener(DataObjectChangeListener listener)
	{
		changeListeners.removeClientListener(listener);
	}

	public void clearListeners() {
		changeListeners.clearListeners();
	}

	protected class InterfaceParameterInternalDataObjectListener extends InternalDataObjectListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			changeListeners.fireDataObjectChange(evt);
		}
	}

	protected class ModelParameterChangeListener extends DataObjectChangeListener
	{
		public ModelParameterChangeListener()
		{
			super(InterfaceParameterRuntime.this);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			synchronized (changeListeners) {
				changeListeners.setModelParameterChanged();
				PropertyChangeEvent event = e.getEvent();
				if (event.getPropertyName().equals(Parameter.VALUE_STATUS))
					setValueStatus((String) event.getNewValue());
				else
					InterfaceParameterRuntime.this.getCurrentDataObject().getValueShadowListener().propertyChange(event);
				changeListeners.clearModelParameterChanged();
			}
		}
	}

	/**
	 * To be called from server for changes from clients only!
	 * @param args
	 */
	public void setValues(List args)
	{
		if (isIndependent)
			getCurrentDataObject().setValues(args);
		else {
			// ignore changes since they are not valid
			// todo: error handling for invalid changes
		}
	}

	/**
	 * To be called from model changes only!
	 */
	public void setValues(DataObject values)
	{
		synchronized (changeListeners) {
			changeListeners.setModelParameterChanged();
			DataObject dobj = InterfaceParameterRuntime.this.getCurrentDataObject();
			if (dobj instanceof FileData)
				((FileData)dobj).notifyFileChanged();
			else
				dobj.setValues(values);
			changeListeners.clearModelParameterChanged();
		}
	}

	class DataObjectSvrInterfaceFactory implements DataObjectFactory
	{
		public DataObject newInstance(String dObjType)
		{
			Constructor ctr = null;
			try {
				try {
					ctr = Registry.getConstructor(dObjType, Registry.SERVER_INTERFACE_CLS);
				}
				catch (Exception e) {
					ctr = Registry.getConstructor(dObjType, Registry.BASE_CLS);
				}
				return (DataObject) ctr.newInstance(null);
			}
			catch (InstantiationException e) {
				System.err.println("newInstance: " + e + "\t" + dObjType);
			}
			catch (IllegalAccessException e) {
				System.err.println("newInstance: " + e + "\t" + dObjType);
			}
			catch (IllegalArgumentException e) {
				System.err.println("newInstance: " + e + "\t" + dObjType);
			}
			catch (InvocationTargetException e) {
				System.err.println("newInstance: " + e + "\t" + dObjType);
			}
			return null;
		}

		public DataObject newInstance(DataObject dObj)
		{
			Constructor ctr = null;
			try {
				try {
					ctr = Registry.getConstructor(dObj, Registry.SERVER_INTERFACE_CLS);
				}
				catch (Exception e) {
					ctr = Registry.getConstructor(dObj, Registry.BASE_CLS);
				}
				return (DataObject) ctr.newInstance(new Object[]{dObj});
			}
			catch (InstantiationException e) {
				System.err.println("newInstance: " + e + "\t" + dObj);
			}
			catch (IllegalAccessException e) {
				System.err.println("newInstance: " + e + "\t" + dObj);
			}
			catch (IllegalArgumentException e) {
				System.err.println("newInstance: " + e + "\t" + dObj);
			}
			catch (InvocationTargetException e) {
				System.err.println("newInstance: " + e + "\t" + dObj);
			}
			return null;
		}

		public DataObject newInstance(Element xml)
		{
			Constructor ctr = null;
			try {
				try {
					ctr = Registry.getConstructor(xml, Registry.SERVER_INTERFACE_CLS);
				}
				catch (Exception e) {
					ctr = Registry.getConstructor(xml, Registry.BASE_CLS);
				}
				return (DataObject) ctr.newInstance(new Object[]{xml});
			}
			catch (InstantiationException e) {
				System.err.println("newInstance: " + e + "\t" + xml);
			}
			catch (IllegalAccessException e) {
				System.err.println("newInstance: " + e + "\t" + xml);
			}
			catch (IllegalArgumentException e) {
				System.err.println("newInstance: " + e + "\t" + xml);
			}
			catch (InvocationTargetException e) {
				System.err.println("newInstance: " + e + "\t" + xml);
			}
			return null;
		}
	}

}
