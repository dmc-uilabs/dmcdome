// PluginModelRuntime.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.functions.DeployFilesDbFunctions;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.*;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.FileUtils;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class PluginModelRuntime extends PluginModelBuilder implements ModelRuntime {

	private static Integer lastRuntimeInstanceNumber = new Integer(0);

    protected HashMap parametersFlatMap = new HashMap();    // flat list of all model objects, including those in relations
    protected CompoundId runtimeId = new CompoundId();
	private boolean isProjectResource;
	private String modelRunStatus = ModelRuntime.STATUS_DONE; // default;

	protected PluginModelExecutionManager solver;
    protected boolean isRemoteModel = false;
	protected CompoundId parentProjectId = null;
	protected String parentServerSessionId = null;
	protected File modelDirectory, workingDirectory;
	protected Boolean waitingToDie = Boolean.FALSE;

	public PluginModelRuntime(String file, Element xml)
	{
		super(file, xml);
	}

	public PluginModelRuntime(Id id, PluginModelBuilder model)
	{
		super(id, model);
	}

	public PluginModelRuntime(String id, String pluginType)
	{
		super(id, pluginType);
	}

	public PluginModelRuntime(Element xml)
	{
		super(xml);
		populateParametersFlatMap();
	}

	public PluginModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource)
	{
		super(xml);

		runtimeId = new CompoundId(parentId);
		if (runtimeId.getModelRuntimeId() == null)
			runtimeId.setModelRuntimeId(UUIDGenerator.create());
		populateParametersFlatMap(); // fix file path is called here
		this.isProjectResource = isProjectResource;
		solver = new PluginModelExecutionManager(this); // solver created after model loaded
		solver.addPropertyChangeListener(DomeModelExecutionManager.SOLVING_STATUS, new SolverStatusListener());
	}

    /**
     * added Sep, 2005 by sangmok
     * FileParameter needs to know in which directory it runs.
     * ParameterRuntime.getScope().getWorkingDirectory() will give the answer.
     */
    public File getWorkingDirectory() {
        if (workingDirectory == null) {
            createAuxFilesDirectory();
        }
        return workingDirectory;
    }

	protected void createAuxFilesDirectory()
	{
		if (modelDirectory == null) {
			modelDirectory = new File(DomeServer.getServerAuxFileRoot(), getRuntimeId().getModelStaticId()); // uses deploy id
			if (!modelDirectory.exists())
				modelDirectory.mkdir(); // assume it works for now
			synchronized (lastRuntimeInstanceNumber) {
				int newValue = lastRuntimeInstanceNumber.intValue() + 1;
				workingDirectory = new File(modelDirectory, "Run_" + newValue);
				try {
					if (!workingDirectory.exists() && !workingDirectory.mkdir()) {
						String msg = "unable to create working directory: " + workingDirectory.getAbsolutePath();
						throw new RuntimeException(msg);
					}
				}
				catch (RuntimeException e) {
					throw e;
				}
				catch (Exception e) {
					String msg = "unable to create working directory: " + workingDirectory.getAbsolutePath() + "\n\t" + e;
					throw new RuntimeException(msg);
				}
				lastRuntimeInstanceNumber = new Integer(newValue);
			}
			Debug.trace(Debug.ALL, "modelDirectory=" + modelDirectory.getAbsolutePath());
			Debug.trace(Debug.ALL, "workingDirectory=" + workingDirectory.getAbsolutePath());
		}
	}

    public void setRemoteModel(String parentServerSessionId, CompoundId parentProjectId) {
        this.parentServerSessionId = parentServerSessionId;
        parentProjectId = new CompoundId(parentProjectId);
        isRemoteModel = true;
    }

    public String getParentServerSessiondId() {
        return parentServerSessionId;
    }

    public CompoundId getRootProjectId() {
        return parentProjectId;
    }

    public boolean isRemoteModel() {
        return isRemoteModel;
    }

	public DirectedGraph createModelGraph()
	{
		if (modelGraph == null)
			super.createModelGraph();
		return modelGraph;
	}

    protected boolean getFilesCanBeExecuted() {
        for (int i = 0; i < AuxFiles.size(); i++) {
            AbstractAuxFile auxFile = (AbstractAuxFile) AuxFiles.get(i);
            return auxFile.isExecuteOnServer();
        }
        return false;
    }

    //Qing : add for auxiliary files
    protected String getMainModelFileName() {
        if (AuxFiles.size() == 0)
            return null;
        else {
            for (int i = 0; i < AuxFiles.size(); i++) {
                AbstractAuxFile auxFile = (AbstractAuxFile) AuxFiles.get(i);
                if (auxFile.isMainModelFile()) {
                	System.out.println("PluginModelRunTime:getMainModelFileName: Is Main Model File");
                 	System.out.println("AuxFileId" + auxFile.getId().toString());
                	System.out.println("runtimeId.getModelStaticId()=" + runtimeId.getModelStaticId());

                    if (auxFile.isExecuteOnServer()) {
                        try {
                            Vector auxInfo = DeployFilesDbFunctions.getMostRecentAuxFileInfo(auxFile.getId().toString(), runtimeId.getModelStaticId());
                            if (auxInfo == null || auxInfo.size() == 0) {
                                System.out.println("error getting auxfile information from server for auxiliary file " + auxFile.getId());
                                return null;
                            }
                            String filename = (String) auxInfo.get(0);
                           	System.out.println("filename=" + filename);

                            String serverLocation = (String) auxInfo.get(1);
                            if (File.separatorChar == '/') {
                            	serverLocation = serverLocation.replace('\\', File.separatorChar);	
                            }
                            else {
                            	serverLocation = serverLocation.replace('/', File.separatorChar);
                            }
                            System.out.println("Server Located at " + serverLocation.toString());
                            
                            if (serverLocation.equals("")) {
                                System.out.println("not available on server");
                                return null;
                            }
                            return DomeServer.getServerAuxFileRoot() + File.separator + serverLocation;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else //using local add
                    	System.out.println("Model not executed at server ");
                        Debug.trace(Debug.ALL, "Auxfile =" + auxFile.getFile().getPath().toString());

                        return auxFile.getFile().getPath();
                }
            }
            return null;
        }
    }

    public String getAuxFileDomeName(int index) {
        if (AuxFiles.size() == 0)
            return null;
        if (index < 0 || index > AuxFiles.size()) return null;

        AbstractAuxFile auxFile = (AbstractAuxFile) AuxFiles.get(index);
	    return auxFile.getName();
    }

    public String getAuxFilePathName(int index) {
        if (AuxFiles.size() == 0)
            return null;
        if (index < 0 || index > AuxFiles.size()) return null;

        AbstractAuxFile auxFile = (AbstractAuxFile) AuxFiles.get(index);
        try {
            Vector auxInfo = DeployFilesDbFunctions.getMostRecentAuxFileInfo(auxFile.getId().toString(), runtimeId.getModelStaticId());
            if (auxInfo == null || auxInfo.size() == 0) {
                System.out.println("error getting auxfile information from server for auxiliary file " + auxFile.getId());
                return null;
            }
            String filename = (String) auxInfo.get(0);
            String serverLocation = (String) auxInfo.get(1);
            if (serverLocation.equals("")) {
                System.out.println("not available on server");
                return null;
            }
            return DomeServer.getServerAuxFileRoot() + File.separator + serverLocation;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

	public boolean isWaitingToDie()
	{
		return waitingToDie.booleanValue();
	}

	public void startModel() {
	    solver.startSolving();
    }

	public void pauseModel() {
		// default implementations do nothing
	}

	public void resumeModel() {
		// default implementations do nothing
	}

	public void stopModel() {
		// default implementations do nothing
	}

    public void deleteModel() {
        cleanup();
    }

    public void startModelAndWait() {
	    solver.startSolving();
    }

	/**
	 * Call this method to tell plugin to execute
	 * @param outputs list of outputs that will be affected by this execution based on list of changed inputs
	 */
    public void execute(List outputs) {
		try {
			executeNativePlugin(outputs);
		} catch (RuntimeException ex) {
			throw ex; // bounce it up to the solver
		} catch (Exception e) { // don't let the server die, convert to a RuntimeException
			throw new RuntimeException(e.getMessage(),e);
		}
	}

    /**
     * Subclasses should implement this method for how the plugin is executed.
     * @param affectedOutputParams list of outputs affected by the changed inputs
     */
    protected abstract void executeNativePlugin(List affectedOutputParams);

    // copied from DomeModelRuntime

    protected ConnectionMappingManager createConnectionMappingManager() {
        return new ConnectionMappingManagerRuntime(this);
    }

    protected ModelInterfaceManager createInterfacesManager() {
        return new ModelInterfaceManagerRuntime(this);
    }

    /**
     * Create a flat list (hashmap) of all parameters, including relation parameters.
     */
    private void populateParametersFlatMap() {
        for (Iterator mObjIter = modelObjects.iterator(); mObjIter.hasNext();) {
            ModelObject mObj = (ModelObject) mObjIter.next();
            if (mObj instanceof Parameter) {
                parametersFlatMap.put(mObj.getId(), mObj);
	            DataObject dobj = ((Parameter) mObj).getCurrentDataObject();
	            if (dobj instanceof DomeFile) {
		            fixFilePath((Parameter) mObj);
	            }
            }
        }
    }

	private void fixFilePath(Parameter p)
	{
		if (modelDirectory == null)
			createAuxFilesDirectory();

		DataObject dobj = p.getCurrentDataObject();
		FileData dfile = (FileData) dobj;
		String filepath = dfile.getFilePath();
		File origFile = new File(filepath);  // undeployed file
		File mdlFile = new File(modelDirectory, origFile.getName()); // deployed file
		File newFile = new File(workingDirectory, origFile.getName()); // todo: support directories within directory
		dfile.setFilePath(newFile.getAbsolutePath());
		if (mdlFile.exists()) {
			FileUtils.copyFile(mdlFile, newFile);
		}
		else {
			try {
				newFile.createNewFile(); //create an empty file to avoid pop up dialog for o/p files
			}
			catch (IOException e) {
				System.err.println("error creating dummy file: " + newFile.getAbsolutePath());
			}
		}
	}

	protected void cleanup()
	{
		solver.cleanup();
		//delete the output files before this object gets garbage collected
		if (workingDirectory != null)
			try {
				FileUtils.deleteDirectoryContents(workingDirectory, true);
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
		if (modelDirectory != null && modelDirectory.listFiles().length == 0) // no other model is using this
			try {
				FileUtils.deleteDirectoryContents(modelDirectory, true); // warning! synchronization problems may occur!
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
		firePropertyChange(MODEL_KILLED);
		Debug.trace(Debug.ALL, "plugin '" + getName() + "' killed");

		//qing---release the memory
		//clean the model parameters as well
		Collection allmodelobject = this.getModelObjects();

		//clean the collections
		for (Iterator i = allmodelobject.iterator(); i.hasNext();) {
			Object modelobj = i.next();
			if (modelobj != null) modelobj = null;
		}
		allmodelobject = null;
		System.gc();
		Debug.trace(Debug.ALL, "plugin model memory set free");
	}

    public CompoundId getRuntimeId() {
        return runtimeId;
    }

	public boolean isProjectResource()
	{
		return isProjectResource;
	}

	public void markModelWaitingToBeExecuted()
	{
		if (!ModelRuntime.STATUS_RUNNING.equals(modelRunStatus))
			setModelRunStatus(ModelRuntime.STATUS_WAITING_TO_BE_EXECUTED);
	}

	public void suspendStatusPropagation()
	{
		solver.suspendStatusPropagation();
	}

	public void resumeStatusPropagation()
	{
		solver.resumeStatusPropagation();
	}

	public boolean hasAtLeastOneInterfaceLoaded()
	{
		return getModelInterfacesManager().countInterfaces() > 0;
	}

    /**
     * Set a parameter's data object values using reflection.
     * @param objectId Id of the parameter
     * @param methodName Set method name
     * @param args Argument list
     */
    public void setItem(String objectId, String methodName, List args) {
        Id objId = new Id(objectId);
        Parameter mObj = (Parameter) parametersFlatMap.get(objId);
        DataObject dObj = mObj.getCurrentDataObject();

        if (dObj == null)
            ;// todo: throw exception
        else {
            // get new parameter types (some types may be converted to their superclasses)
            Class[] paramClassTypes = getMethodArgTypes(methodName, dObj, args);

            if (paramClassTypes != null) {
                // call the set method
                Method method;
                Class objClass = dObj.getClass();
                Object[] arguments = args.toArray();
                try {
                    method = objClass.getMethod(methodName, paramClassTypes);
                    method.invoke(dObj, arguments);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                } catch (IllegalAccessException e) {
                    System.out.println(e);
                } catch (InvocationTargetException e) {
                    System.out.println(e);
                }
            }
        }
    }

    /**
     * Get a parameter's data object values using reflection.
     * @param objectId Id of the parameter
     * @param methodName Get item method name
     * @param args Argument list
     * @return Data values
     */
    public Object getItem(String objectId, String methodName, List args) {
        Id objId = new Id(objectId);
        Parameter mObj = (Parameter) parametersFlatMap.get(objId);
        DataObject dObj = mObj.getCurrentDataObject();
        Object results = null;

        if (dObj == null)
            ;// todo: throw exception
        else {
            // get new parameter types (some types may be converted to their superclasses or primitives)
            Class[] paramClassTypes = getMethodArgTypes(methodName, dObj, args);

            if (paramClassTypes != null) {
                // call the get method
                Method method;
                Object result = null;
                Class objClass = dObj.getClass();
                Object[] arguments = args.toArray();
                try {
                    method = objClass.getMethod(methodName, paramClassTypes);
                    results = method.invoke(dObj, arguments);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    System.out.println(e);
                } catch (InvocationTargetException e) {
                    System.out.println(e);
                }
            }
        }

        return results;
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
    private Class[] getMethodArgTypes(String methodName, Object obj, List args) {
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

    public ModelObjectFactory getModelObjectFactory() {
        if (moFactory == null)
            moFactory = new DomeModelRuntime.RuntimeModelObjectFactory();
        return moFactory;
    }

	/**
	 * project uses this method to add external graph to remote resource
	 * @param graphXml must contain sourceId in it
	 */
	public void addExternalGraph(String graphXml)
	{
		Element xmlElement = XMLUtils.stringToXmlElement(graphXml);
		String sourceId = DirectedGraph.getGraphIdFromXml(xmlElement);
		DirectedGraph externalInfoGraph = new DirectedGraph(xmlElement);
		DirectedGraph externalGraph = convertGraphFromIdsToParameters(externalInfoGraph);
		solver.addExternalGraph(sourceId, externalGraph);
	}

	public String getRunStatus() {
		return this.modelRunStatus;
	}
	
	private void setModelRunStatus(String modelRunStatus)
	{
		String oldModelRunStatus = this.modelRunStatus;
		this.modelRunStatus = modelRunStatus;
		firePropertyChangeInSeparateThread(ModelRuntime.RUN_STATUS, oldModelRunStatus, this.modelRunStatus);
	}

	class SolverStatusListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (waitingToDie.booleanValue()) {
				deleteModel();
				return;
			}
			Object solverStatus = evt.getNewValue();
			if (AbstractModelExecutionManager.SOLVING_STARTED.equals(solverStatus))
				setModelRunStatus(STATUS_RUNNING);
			else if (AbstractModelExecutionManager.SOLVING_PAUSED.equals(solverStatus))
				setModelRunStatus(STATUS_PAUSED);
			else if (AbstractModelExecutionManager.SOLVING_ABORTED.equals(solverStatus) ||
			        AbstractModelExecutionManager.SOLVING_INCOMPLETE.equals(solverStatus))
				setModelRunStatus(STATUS_ABORTED);
			else if (AbstractModelExecutionManager.SOLVING_STOPPED.equals(solverStatus)) {
				if (isProjectResource())
					setModelRunStatus(STATUS_WAITING_FOR_CONFIRMATION);
				else {
					solver.markChangesConsistent();
					setModelRunStatus(STATUS_DONE);
				}
			}
		}
	}

	/**
	 * To be used by project to tell imodels and resources that the project run is complete
	 */
	public void notifyProjectRunComplete()
	{
		solver.markChangesConsistent();
		setModelRunStatus(STATUS_DONE);
	}

	public DirectedGraph getGraph()
	{
		return solver.getCompleteGraph();
	}

	protected DirectedGraph getSystemCausalityGraph()
	{
		return solver.getCompleteGraph();
	}

}
