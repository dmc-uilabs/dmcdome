// DomeModelRuntime.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.model.dome;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.functions.DeployFilesDbFunctions;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.functions.RuntimeFunctionsServer;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.playspace.ServerPlayspace;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.IterationVariable;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.*;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Latch;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.AbstractModelExecutionManager;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.DomeModelExecutionManager;
import mit.cadlab.dome3.objectmodel.util.solving.NameIdNode;
import mit.cadlab.dome3.objectmodel.util.solving.Parameters;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Version of DomeModel for running on server.
 */
public class DomeModelRuntime extends DomeModelBase implements ModelRuntime
 {
    private static Integer lastRuntimeInstanceNumber = new Integer(0);
    private HashMap modelObjectsFlatMap = new HashMap();    // flat list of all model objects, including those in relations
    private CompoundId runtimeId = null;
    private boolean isProjectResource;
    private String modelRunStatus = ModelRuntime.STATUS_DONE; // default;

    private List clocks;
    private List latches;

    private DomeModelExecutionManager solver;
    volatile private Boolean lockModel = new Boolean(false);
    protected boolean isRemoteModel = false;
    protected CompoundId parentProjectId = null;
    protected String parentServerSessionId = null;
    protected File modelDirectory, workingDirectory; // do not initialize values here; otherwise, they will overwrite values set in initModel!
    protected Boolean waitingToDie = Boolean.FALSE;

    public DomeModelRuntime(Id id) {
        super(id);
    }

    public DomeModelRuntime(Id id, DomeModel model) {
        super(id, model);
        // not really supported; not implemented
    }

    public DomeModelRuntime(CompoundId parentId, Element xmlElement, boolean isProjectResource) {
        super(xmlElement);
        runtimeId = new CompoundId(parentId);
        if (runtimeId.getModelRuntimeId() == null)
            runtimeId.setModelRuntimeId(UUIDGenerator.create());
        this.isProjectResource = isProjectResource;
        populateModelObjectFlatMap(); // fix file path is called here
        createLatches();
        solver = new DomeModelExecutionManager(this);
        solver.addPropertyChangeListener(DomeModelExecutionManager.SOLVING_STATUS, new SolverStatusListener());
    }

    public DomeModelRuntime(CompoundId parentId, Element xmlElement, IntegrationProjectServerRuntime project) {
        super(xmlElement);
        this.iProject = project;
        runtimeId = new CompoundId(parentId);
        if (runtimeId.getModelRuntimeId() == null)
            runtimeId.setModelRuntimeId(UUIDGenerator.create());
        // add subscription interface and convert mappings
        loadSubscriptionInterfaces();
        convertMappings();
        populateModelObjectFlatMap(); // fix file path is called here
        createLatches(); // after subscription interfaces are loaded!
        solver = new DomeModelExecutionManager(this);
        solver.addPropertyChangeListener(DomeModelExecutionManager.SOLVING_STATUS, new SolverStatusListener());
        loadSubscriptionParameterValues(); // do this after solver is created
    }

    //not used now _i
    public DomeModelRuntime(CompoundId parentId, Element xmlElement, IntegrationProjectServerRuntime project, boolean isStarting) {
        super(xmlElement);
        if (isStarting) {
            this.iProject = project;
            runtimeId = new CompoundId(parentId);
            if (runtimeId.getModelRuntimeId() == null)
                runtimeId.setModelRuntimeId(UUIDGenerator.create());
            loadSubscriptionInterfaces();
            convertMappings();
            populateModelObjectFlatMap(); // fix file path is called here
            createLatches(); // after subscription interfaces are loaded!
            solver = new DomeModelExecutionManager(this);
            solver.addPropertyChangeListener(DomeModelExecutionManager.SOLVING_STATUS, new SolverStatusListener());
            loadSubscriptionParameterValues(); // do this after solver is created
        }
    }

    //loads the subscription interfaces after all i-models have been loaded _i
    public void finishImodelLoading() {
        // add subscription interface and convert mappings
        loadSubscriptionInterfaces();
        convertMappings();
        populateModelObjectFlatMap(); // fix file path is called here
        createLatches(); // after subscription interfaces are loaded!
        solver = new DomeModelExecutionManager(this);
        solver.addPropertyChangeListener(DomeModelExecutionManager.SOLVING_STATUS, new SolverStatusListener());
        loadSubscriptionParameterValues(); // do this after solver is created
    }

    public DomeModelRuntime(String id, String name) {
        super(id, name);
    }

    protected void initModel() {
        super.initModel();
        clocks = new ArrayList();
        latches = new ArrayList();
    }

    /**
     * added Sep, 2005 by sangmok
     * FileParameter needs to know in which directory it runs.
     * ParameterRuntime.getScope().getWorkingDirectory() will give the answer. 
     */
    public File getWorkingDirectory() {
    	createAuxFilesDirectory();
        return workingDirectory;
    }
    /**
     * 
     * ParameterRuntime.getScope().getModelDirectory() will give the answer. 
     */
    public File getModelDirectory() {
    	createAuxFilesDirectory();
        return modelDirectory;
    }
    
    protected void createAuxFilesDirectory() {
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
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    String msg = "unable to create working directory: " + workingDirectory.getAbsolutePath() + "\n\t" + e;
                    throw new RuntimeException(msg);
                }
                lastRuntimeInstanceNumber = new Integer(newValue);
            }
            Debug.trace(Debug.ALL, "modelDirectory=" + modelDirectory.getAbsolutePath());
            Debug.trace(Debug.ALL, "workingDirectory=" + workingDirectory.getAbsolutePath());
        }
    }

    protected void loadSubscriptionInterfaces() {
        List subscriptions = getFilter(SUBSCRIPTIONS_FILTER).getItems();
        DefaultSubscription sub;
        ProjectResourceInfoRuntime prir;
        for (int i = 0; i < subscriptions.size(); i++) {
            sub = (DefaultSubscription) subscriptions.get(i);
            prir = (ProjectResourceInfoRuntime) iProject.getResource(sub.getResourceId());
            try {
                prir.loadSubscription(sub);
            } catch (XmlRpcException e) {
                System.err.println("error loading subscription interface for " + sub.getName());
                System.err.println("\t" + e);
                throw new RuntimeException("XmlRpcException: error loading subscription interface for " + sub.getName());
            } catch (UnreachableServerException e) {
                System.err.println("error loading subscription interface for " + sub.getName());
                System.err.println("\t" + e);
                throw new RuntimeException("UnreachableServerException: error loading subscription interface for " + sub.getName()
                        + " from " + prir.getName());
            }
        }
    }

    /**
     * Parameters in resource models can be mapped to integration model parameters
     * or to other resource model parameters. We want to move the mappings from the
     * resource models to the resource model interfaces. Mappings should always be
     * be of the form: [model interface parameter] -> [subscription model interface parameter]
     * or [subscription model interface parameter] -> [integration model parameter].
     */
    private void convertMappings() {
        // cycle through the mappings for this model
        ConnectionMappingManagerRuntime mgr = (ConnectionMappingManagerRuntime) getMappingManager();
        HashMap mappings = mgr.getMappings(this);
        Object[] sourceParams = mappings.keySet().toArray();
        for (int i = 0; i < sourceParams.length; i++) {
            // get the source parameter and its target mappings
            ModelParameterRuntime sourceParam = (ModelParameterRuntime) sourceParams[i];
            ModelObjectScope sourceScope = sourceParam.getScope();
            // get the underlying source parameter
            ParameterRuntime newSourceParam = null;
            if (sourceScope instanceof DefaultSubscription) { // parameter is in the subscription
                newSourceParam = (ParameterRuntime) ((DefaultSubscription) sourceScope).getInterfaceParameter(sourceParam);
            } else {
                // parameter is in the model
                newSourceParam = sourceParam;
            }

            Object[] targetParams = mgr.getMappingsForParameter(sourceParam).toArray();
            ParameterRuntime targetParam;
            ModelObjectScope targetScope;
            ParameterRuntime newTargetParam;
            for (int j = 0; j < targetParams.length; j++) {
                targetParam = (ParameterRuntime) targetParams[j];
                targetScope = targetParam.getScope();

                // get the underlying target parameter
                if (targetScope instanceof DefaultSubscription) { // parameter is in the subscription
                    newTargetParam = (ParameterRuntime) ((DefaultSubscription) targetScope).getInterfaceParameter(targetParam);
                } else {
                    // parameter is in the model
                    newTargetParam = targetParam;
                }

                // remove the old mapping and add the new one; this does not need
                // to be done if the new parameters and the original parameters are the same
                if (newSourceParam != null && newTargetParam != null &&
                        (!newSourceParam.equals(sourceParam) || !newTargetParam.equals(targetParam))) {
                    mgr.removeMapping(sourceParam, targetParam);
                    if (!mgr.mappingExists(newSourceParam, newTargetParam))
                        mgr.addRuntimeMapping(newSourceParam, newTargetParam);
                }
            }
        }
    }

    /**
     * Copy the subscription parameter values to the subscription interface parameters.
     */
    private void loadSubscriptionParameterValues() {
        Iterator subscriptions = getSubscriptions().iterator();
        DefaultSubscription subscription;
        while (subscriptions.hasNext()) {
            // copy subscription parameter value to subscription interface parameter
            // if parameter is a model input; otherwise, it will be changed later by the model
            subscription = (DefaultSubscription) subscriptions.next();
            Iterator mObjs = subscription.getModelObjects().iterator();
            ModelObject mObj;
            ModelParameterRuntime subIfaceParam;
            CausalityStatus paramCS;
            while (mObjs.hasNext()) {
                mObj = (ModelObject) mObjs.next();
                if (mObj instanceof Parameter) {
                    subIfaceParam = (ModelParameterRuntime) subscription.getInterfaceParameter((Parameter) mObj);
                    paramCS = getCausality(subIfaceParam);
                    if (CausalityStatus.INDEPENDENT.equals(paramCS)) {
                        subIfaceParam.setInitialValue(((Parameter) mObj).getCurrentDataObject()); // do it this way so solver will accept value
                    }
                }
            }
        }
    }

    public boolean isWaitingToDie() {
        return waitingToDie.booleanValue();
    }

    public void doAdvancedNotification() {
        Iterator subscriptions = getSubscriptions().iterator();
        DefaultSubscription subscription;
        SubscriptionInterface sIface;
        while (subscriptions.hasNext()) {
            subscription = (DefaultSubscription) subscriptions.next();
            sIface = subscription.getInterface();
            sIface.submitInconsistentParameters();
        }
    }

    public void setRemoteModel(String parentServerSessionId, CompoundId parentProjectId) {
        this.parentServerSessionId = parentServerSessionId;
        this.parentProjectId = new CompoundId(parentProjectId);
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

    protected ConnectionMappingManager createConnectionMappingManager() {
        return new ConnectionMappingManagerRuntime(this);
    }

    protected DomeModelInterface createDefaultModelInterface() {
        return new ModelInterfaceRuntimeServer(this, ModelInterface.DEFAULT_IFACE_TAG);
    }

    protected ModelInterfaceManager createInterfacesManager() {
        return new ModelInterfaceManagerRuntime(this);
    }

    /**
     * Create a flat list (hashmap) of all model objects, including relation parameters.
     */
    private void populateModelObjectFlatMap() {
        for (Iterator mObjIter = modelObjects.iterator(); mObjIter.hasNext();) {
            ModelObject mObj = (ModelObject) mObjIter.next();
            if (mObj instanceof Parameter) {
                modelObjectsFlatMap.put(mObj.getId(), mObj);
                DataObject dobj = ((Parameter) mObj).getCurrentDataObject();
                if (dobj instanceof DomeFile) {
                    fixFilePath((Parameter) mObj);
                }
            } else if (mObj instanceof Subscription) {
                modelObjectsFlatMap.put(mObj.getId(), mObj);
                Iterator subParams = ((Subscription) mObj).getModelObjects().iterator();
                ModelObject subObject;
                while (subParams.hasNext()) {
                    subObject = (ModelObject) subParams.next();
                    if (subObject instanceof Parameter) { // ignore everything else
                        DataObject dobj = ((Parameter) subObject).getCurrentDataObject();
                        if (dobj instanceof DomeFile) {
                            Parameter subIfaceObject = ((DefaultSubscription) subObject.getScope()).getInterfaceParameter((Parameter) subObject);
                            ((DomeFile) subIfaceObject.getCurrentDataObject()).setFilePath(((DomeFile) dobj).getFilePath());
                            fixFilePath(subIfaceObject);
                        }
                    }
                    modelObjectsFlatMap.put(subObject.getId(), subObject); // doesn't work well in runtime if interface is used twice
                    // this map isn't currently used in runtime anyways
                }
            } else if (mObj instanceof Relation) {
                Collection params = ((Relation) mObj).getModelObjects();
                for (Iterator paramIter = params.iterator(); paramIter.hasNext();) {
                    Parameter p = (Parameter) paramIter.next();
                    modelObjectsFlatMap.put(p.getId(), p);
                    DataObject dobj = p.getCurrentDataObject();
                    if (dobj instanceof DomeFile) {
                        fixFilePath(p);
                    }
                }
            }
        }
    }

    private void fixFilePath(Parameter p) {
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
        } else {
            try {
                newFile.createNewFile(); //create an empty file to avoid pop up dialog for o/p files
            } catch (IOException e) {
                System.err.println("error creating dummy file: " + newFile.getAbsolutePath());
            }
        }
    }

    protected String getAuxFilePathOnServer(String filepath) {
        if (AuxFiles.size() == 0)
            return null;

        int index = -1;
        for (Iterator iterator = AuxFiles.iterator(); iterator.hasNext();) {
            AbstractAuxFile aux = (AbstractAuxFile) iterator.next();
            if (aux.getFile().getAbsolutePath().equals(filepath)) {
                break;
            } else {
                index++;
            }
        }
        index++; //since index is initialized to -1
        AbstractAuxFile auxFile = (AbstractAuxFile) AuxFiles.get(index);
        try {
            Vector auxInfo = DeployFilesDbFunctions.getMostRecentAuxFileInfo(auxFile.getId().toString(), runtimeId.getModelStaticId());
            if (auxInfo == null || auxInfo.size() == 0) {
                System.out.println("error getting auxfile information from server for auxiliary file " + auxFile.getId());
                return null;
            }
            String serverLocation = (String) auxInfo.get(1);
            if (serverLocation.equals("")) {
                System.out.println("not available on server");
                return null;
            }
            int nameind = serverLocation.indexOf(File.separator);
            String serverPath = serverLocation.substring(0, nameind);
            return DomeServer.getServerAuxFileRoot() + File.separator + serverPath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void cleanup() {

        // memory fix starts. this should be done before interfaces get empty
        interfaces.cleanup();
        // memory fix ends

        solver.cleanup();

        //delete the output files before this object gets garbage collected
        if (workingDirectory != null)
            try {
                FileUtils.deleteDirectoryContents(workingDirectory, true);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        if (modelDirectory != null && modelDirectory.listFiles().length == 0) // no other model is using this
            try {
                FileUtils.deleteDirectoryContents(modelDirectory, true); // warning! synchronization problems may occur!
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        firePropertyChange(MODEL_KILLED);
        Debug.trace(Debug.ALL, "model '" + getName() + "' killed");

        // sangmok : memory leak debug starts

        Set deletedObjectSet = new HashSet();
        Set deletedModelObjectScopeSet = new HashSet();


        for (int i = 0; i < modelObjects.size(); i++) {
            ModelObject mObj = (ModelObject) modelObjects.get(i);
            if (mObj instanceof Parameter) {
                deletedObjectSet.add(mObj);
            } else if (mObj instanceof Subscription) {
                deletedModelObjectScopeSet.add(mObj);
            } else if (mObj instanceof Relation) {
                deletedModelObjectScopeSet.add(mObj);
            } else if (mObj instanceof DefaultContextBuilder) {
                List mObjReferences = ((DefaultContextBuilder) mObj).getModelObjectReferences();
                for (int j = 0; j < mObjReferences.size(); j++) {
                    ModelObject mObjInBuilder = (ModelObject) mObjReferences.get(j);

                    if (mObjInBuilder instanceof Parameter) {
                        deletedObjectSet.add(mObjInBuilder);
                    } else if (mObjInBuilder instanceof Subscription) {
                        deletedModelObjectScopeSet.add(mObjInBuilder);

                    } else if (mObjInBuilder instanceof Relation) {
                        if (mObjInBuilder instanceof AbstractProceduralRelation) {
                            /* this removes python object to be released, which finally results in releasing data object referred by it */  
                            ((AbstractProceduralRelation) mObjInBuilder).clearRelationExecutor();
                        }
                        deletedModelObjectScopeSet.add(mObjInBuilder);
                    }
                }
            }
        }

        for (Iterator j = deletedObjectSet.iterator(); j.hasNext(); ) {
            try {
                ((ModelObject) j.next()).delete(null);
                //System.out.println("DELETE OF MODEL : ");
            } catch (NoSuchElementException e) { System.err.println(e); }
        }

//        for (Iterator j = deletedModelObjectScopeSet.iterator(); j.hasNext(); ) {
//            try {
//                ModelObjectScope scope = (ModelObjectScope) j.next();
//                scope.deleteAllModelObjects();
//                //scope.delete(null);
//                //System.out.println("DELETE ALL OF SUBSCRIPTION OR RELATION : ");
//            } catch (NoSuchElementException e) { System.err.println(e); }
//        }


        // sangmok : memory leak debug ends
    }

    public CompoundId getRuntimeId() {
        return runtimeId;
    }

    public boolean isProjectResource() {
        return isProjectResource;
    }

    public void markModelWaitingToBeExecuted() {
        if (!ModelRuntime.STATUS_RUNNING.equals(modelRunStatus))
            setModelRunStatus(ModelRuntime.STATUS_WAITING_TO_BE_EXECUTED);
    }

    public void suspendStatusPropagation() {
        solver.suspendStatusPropagation();
    }

    public void resumeStatusPropagation() {
        solver.resumeStatusPropagation();
    }

    public boolean hasAtLeastOneInterfaceLoaded() {
        return getModelInterfacesManager().countInterfaces() > 0;
    }

    public String getXmlDescription() {
        return lastSavedXml;
    }

    public DirectedGraph getGraph() {
        return solver.getCompleteGraph();
    }

    // for run
    protected void addSubscriptionGraph() {
        Vector linkMap = new Vector();
        ConnectionMappingManager mgr = getMappingManager();

        List nodes;
        Parameter subParam, ifaceParam;
        Iterator it = this.getSubscriptions().iterator();
        while (it.hasNext()) {
            DefaultSubscription sub = (DefaultSubscription) it.next();
            DirectedGraph dg = sub.getGraph();
            dg.removeNode(ModelInterfaceBase.MODEL_NODE);
            nodes = dg.getNodes();
            HashMap subParamToIfaceParamMap = new HashMap();
            for (int i = 0; i < nodes.size(); i++) {
                Object obj = nodes.get(i);
                if (obj instanceof Parameter) {
                    subParam = (Parameter) obj;
                    ifaceParam = sub.getInterfaceParameter(subParam);
                    subParamToIfaceParamMap.put(subParam, ifaceParam);

                    Collection map = mgr.getMappingsForParameter(ifaceParam);
                    for (Iterator it2 = map.iterator(); it2.hasNext();) {
                        Object o = it2.next();
                        if (Parameters.isSubscriptionOutput(subParam))
                            linkMap.add(Vectors.create(ifaceParam, o));
                        else if (Parameters.isSubscriptionInput(subParam))
                            linkMap.add(Vectors.create(o, ifaceParam));
                    }
                } else if (obj instanceof NameIdNode)
                    dg.removeNode(obj);
            }
            modelGraph.addGraph(new DirectedGraph(dg, subParamToIfaceParamMap));
        }
        for (int i = 0; i < linkMap.size(); i++) {
            Vector pair = (Vector) linkMap.get(i);
            modelGraph.addArc(pair.get(0), pair.get(1));
        }
    }

    /**
     * Set a parameter's data object values using reflection.
     * @param objectId Id of the parameter
     * @param methodName Set method name
     * @param args Argument list
     */
    public void setItem(String objectId, String methodName, List args) {
        Id objId = new Id(objectId);
        Parameter mObj = (Parameter) modelObjectsFlatMap.get(objId);
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
        Parameter mObj = (Parameter) modelObjectsFlatMap.get(objId);
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


    /**
     * Prohibit the model from starting its solving process.
     */
    public void lockModel() {
        synchronized (lockModel) {
            lockModel = new Boolean(true);
        }
    }

    /**
     * Allow the model to start its solving process.
     */
    public void unlockModel() {
        synchronized (lockModel) {
            lockModel = new Boolean(false);
        }
    }


    /**
     * Run the solving process on the model's relations.
     * Starts the solving in a separate thread.
     */
    public void startModel() {
        synchronized (lockModel) {
            if (lockModel.booleanValue() == false)
                solver.startSolving();
        }
    }

    /**
     * Halt the solving process.
     */
    public void pauseModel() {
        solver.stopSolving();
        //todo: tell all the other servers to pause
    }

    /**
     * Resume the solving process.
     */
    public void resumeModel() {
        synchronized (lockModel) {
            if (lockModel.booleanValue() == false)
                solver.startSolving();
        }
    }

    /**
     * Kill the model. Behavior to be defined.
     */
    public void stopModel() {
        throw new UnsupportedOperationException();
    }

    public void deleteModel() {
        if (solver.isSolving()) {
            solver.stopSolving();
            waitingToDie = Boolean.TRUE;
            return;
        }
        cleanup();
    }

    /**
     * Run the solving process in the same thread.
     */
    public void startModelAndWait() {
        synchronized (lockModel) {
            if (lockModel.booleanValue() == false)
                solver.runModel();
        }
    }

    public boolean isSolving() {
        return solver.isSolving();
    }

    public ModelObjectFactory getModelObjectFactory() {
        if (moFactory == null)
            moFactory = new RuntimeModelObjectFactory();
        return moFactory;
    }

    public static class RuntimeModelObjectFactory implements ModelObjectFactory {
        /**
         * obj is either object type name, type symbol, or object instance
         * params must be the parameters to the constructor method in correct order
         */
        public ModelObject newInstance(Object obj, Object[] params) {
            try {
                Constructor ctr = null;
                try {
                    ctr = Registry.getConstructor(obj, Registry.SERVER_CLS);
                } catch (Exception e) {
                    try {
                        ctr = Registry.getConstructor(obj, Registry.BUILDER_CLS);
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

    public String getRunStatus() {
        return this.modelRunStatus;
    }

    private void setModelRunStatus(String modelRunStatus) {
        String oldModelRunStatus = this.modelRunStatus;
        this.modelRunStatus = modelRunStatus;
        firePropertyChangeInSeparateThread(ModelRuntime.RUN_STATUS, oldModelRunStatus, this.modelRunStatus);
    }

    class SolverStatusListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
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
                else if (isIntegrationModel()) {
                    if (!((IntegrationProjectServerRuntime) iProject).isProjectResource())
                        if (advanceClocks()) {
                            return;
                        }
                    setModelRunStatus(STATUS_WAITING_FOR_CONFIRMATION);
                } else {
                    if (advanceClocks()) {
                        return;
                    }
                    solver.markChangesConsistent();
                    setModelRunStatus(STATUS_DONE);
                }
            }
        }
    }

    public boolean advanceClocks() {
        boolean clockTicked = false;
        if (!clocks.isEmpty()) {
            Parameter parameter;
            for (int i = 0; i < clocks.size(); i++) {
                parameter = (Parameter) clocks.get(i);
                clockTicked = ((IterationVariable) parameter.getCurrentDataObject()).tick() || clockTicked;
            }
            if (clockTicked)
                DomeModelRuntime.this.startModel();
        }
        return clockTicked;
    }

    /**
     * To be used by project to tell imodels and resources that the project run is complete
     */
    public void notifyProjectRunComplete() {
        solver.markChangesConsistent();
        setModelRunStatus(STATUS_DONE);
    }

    public void processNewSubscriptionInterface(SubscriptionInterface subIface, DefaultSubscription sub) {
        ((DomeModelCausalityManager) modelCausalityManager).processNewSubscriptionInterface(subIface, sub);
    }

    /**
     * project uses this method to add external graph to remote resource
     * @param graphXml must contain sourceId in it
     */
    public void addExternalGraph(String graphXml) {
        Element xmlElement = XMLUtils.stringToXmlElement(graphXml);
        String sourceId = DirectedGraph.getGraphIdFromXml(xmlElement);
        DirectedGraph externalInfoGraph = new DirectedGraph(xmlElement);
        addExternalGraph(sourceId, externalInfoGraph, true);
    }

    /**
     * project uses this method to add external graphs to imodels models
     * and imodels
     * @param sourceId
     * @param graph
     * @param shouldConvertNodes set to true if graph nodes are ids instead of actual parameters
     */
    public void addExternalGraph(String sourceId, DirectedGraph graph, boolean shouldConvertNodes) {
        if (shouldConvertNodes) {
            graph = convertGraphFromIdsToParameters(graph);
        }
        solver.addExternalGraph(sourceId, graph);
    }

    protected DirectedGraph getSystemCausalityGraph() {
        return solver.getCompleteGraph();
    }

    protected void loadParameters(Element xmlElement) {
        super.loadParameters(xmlElement);
        Iterator parameters = getFilter(PARAMETERS_FILTER).getItems().iterator();
        Parameter param;
        while (parameters.hasNext()) {
            param = (Parameter) parameters.next();
            if (param.getCurrentDataObject() instanceof IterationVariable) {
                clocks.add(param);
            }
        }
    }

    protected void loadContexts(Element xmlElement) {
        super.loadContexts(xmlElement);
        Iterator contexts = getFilter(CONTEXTS_FILTER).getItems().iterator();
        Context context;
        while (contexts.hasNext()) {
            context = (Context) contexts.next();
            if (context.getName().equals(Latch.NAME))
                latches.add(context);
        }
    }

    protected void createLatches() {
        Context latchContext;
        for (int i = 0; i < latches.size(); i++) {
            latchContext = (Context) latches.get(i);
            latches.set(i, new Latch(latchContext, this));
        }
    }

    // iason - elaine
    public Vector loadImodelInterface(String ifaceStaticId) {
        try {
            String[] xmlContentMappings
                    = DeployFilesDbFunctions.getMostRecentInterfaceXmlDefinitionAndMappings(ifaceStaticId);
            if (xmlContentMappings.length == 2) {
                String xmlContent = xmlContentMappings[0];
                String xmlMappings = xmlContentMappings[1];

                CompoundId interfaceId = new CompoundId(getRuntimeId());
                interfaceId.setInterfaceStaticId(ifaceStaticId);
                CompoundId ifaceParentId = this.getRuntimeId();
                String ifaceParentRuntimeId = ifaceParentId.getModelRuntimeId();
                interfaceId.setModelStaticId(ifaceParentId.getModelStaticId());
                interfaceId.setModelRuntimeId(ifaceParentRuntimeId);

                ModelInterfaceRuntimeServer iface = loadRuntimeInterface(interfaceId, xmlContent, xmlMappings);

                CompoundId ifaceId = iface.getRuntimeId();
                interfaceId.setInterfaceRuntimeId(ifaceId.getInterfaceRuntimeId());


                String xml = iface.getLiveXmlDescription();
                Integer version = FileSystemDbFunctions.getInterfaceVersion(interfaceId.getInterfaceStaticId());
                return Vectors.create(null, null, interfaceId, version.toString(), xml);
            }
        } catch (XmlRpcException e) {
            return null;
        }
        return null;
    }


    //setting isProjectResource value for nested i-model subscriptions _i
    //todo check if this causes solving problems in other cases!
    public void setIsProjectResource(boolean isProjRes) {
        this.isProjectResource = isProjRes;
    }

}
