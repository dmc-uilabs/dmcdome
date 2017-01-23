package mit.cadlab.dome3.objectmodel.toolinterface.optimization.build;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerBuild;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.FunctionFilter;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.OptimizationParameter;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.OptimizationToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityFilter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeEvent;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.util.ShiftSupport;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Sep 4, 2003
 * Time: 2:41:40 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationInterfaceBuild extends OptimizationToolInterfaceBase
{
    public static final String OPTIMIZATION_INTERFACE = "Optimization Interface";
    public static final String VARIABLES = "Variables";
    public static final String OBJECTIVES = "Objectives";

    public static final Id INTERFACE_VARIABLE_CONTEXT_ID = new Id("interface design variable context");
    public static final Id INTERFACE_OBJECTIVE_CONTEXT_ID = new Id("interface design objective context");

    //key -object in model view, value - object in causal views also referenced in the build view (in default interface)
    protected HashMap _listenerMap = new HashMap();

    protected boolean _isDuplicateOperation;   //used during interface duplication - initailly false

    /**
     * HashMap that connects interface parameters to analysis tool parameters
     * key - analysis tool parameter
     * value - interface parameter
     */
    protected HashMap _interfaceOptimizationToolMap = new HashMap();

    protected HashMap _sharedListenerMap = new HashMap();

    protected HashMap _modelObjectMap = new HashMap();

    protected ModelObjectsListener listener;

    public OptimizationInterfaceBuild(Model m, Id id)
    {
        super(m, id);
        setName(OPTIMIZATION_INTERFACE);
        initModel();
        _optimizationInterfaceConfiguration = new OptimizationInterfaceConfiguration(this);
        addContextListeners();
        createQMOOInterfaceFilters();
        createViews();
    }

    public OptimizationInterfaceBuild(Model m, Id id, ModelObjectScope modelObjectScope)
    {
        this (m, id, modelObjectScope, true);
    }

    public OptimizationInterfaceBuild(Model m, Id id, ModelObjectScope mObjScope, boolean changeName)
	{
		super(m, id, mObjScope);

        String name = getName();
		if (changeName)
			name = "Copy Of " + name;
		setName(name);
        initModel();
        if (mObjScope instanceof OptimizationInterfaceBuild)
        {
            copyModelObjects((ToolInterface) mObjScope);
            _optimizationInterfaceConfiguration = new OptimizationInterfaceConfiguration(this, ((OptimizationInterfaceBuild) mObjScope).getInterfaceConfiguration());

            Context oldBuildContext = ((OptimizationInterfaceBuild) mObjScope).getBuildContext();
            for (Iterator i = oldBuildContext.getModelObjectReferences().iterator(); i.hasNext();)
            {
                ModelObject mObj = (ModelObject) i.next();
                ModelObject obj = (ModelObject) _modelObjectMap.get(mObj.getId());
                if (obj != null)
                {
                    _buildViewContext.addModelObjectReference(obj);
                }
            }
        }
        addContextListeners();
        createQMOOInterfaceFilters();
        createViews();
	}

    public OptimizationInterfaceBuild(OptimizationToolBuild m, Element xmlContent, Element xmlMappings)
    {
        super(m, xmlContent, xmlMappings);

        loadLookUpInfo(xmlMappings);

        Object objectiveCxt = m.getDesignObjectiveContext();
        Object variableContext = m.getDesignVariableContext();
        Collection objs = m.getModelObjects();
        for (Iterator i = objs.iterator(); i.hasNext();)
        {
            Object obj = i.next();
            if (obj.equals(objectiveCxt) || obj.equals(variableContext))
                continue;
            if (!distinctModelObjs.contains(obj))
                distinctModelObjs.add(obj);
        }
    }

    public void loadXmlElement(Element xmlContent)
    {
        super.loadXmlElement(xmlContent);

        addContextListeners();
    }

    private void addContextListeners()
    {
        listener = new ModelObjectsListener((AnalysisTool)_model);

        ((OptimizationToolBuild) _model).getDesignVariableContext().addModelObjectReferencesListener(listener);
        ((OptimizationToolBuild) _model).getDesignObjectiveContext().addModelObjectReferencesListener(listener);
    }

    protected abstract class InterfaceInternalCausalityFilter extends AbstractCausalityFilter
	        implements ShiftSupport
	{

		//for run mode client - Model m is null
		public InterfaceInternalCausalityFilter(Model m, String idString, String name)
		{
			super(m, new Id(idString), name);
			_internalCausalityManager.addCausalityChangeListener(new AbstractCausalityFilter.CausalityFilterCausalityChangeListener());
		}

		public InterfaceInternalCausalityFilter(String idString, String name)
		{
			super(OptimizationInterfaceBuild.this.getModel(), new Id(idString), name);
			_internalCausalityManager.addCausalityChangeListener(new AbstractCausalityFilter.CausalityFilterCausalityChangeListener());
		}

		// ShiftSupport interface
		public void shiftLeft(int[] indices)
		{
			filteredItems.shiftLeft(indices);
		}

		public void shiftRight(int[] indices)
		{
			filteredItems.shiftRight(indices);
		}

	}

    public Collection addAndMapModelObjects(Collection origModelObjects)
    {
        Collection objs = null;
        objs = newModelObjects(origModelObjects);
        if (objs != null)
            mapModelObjects(origModelObjects, objs);
        return objs;
    }

    public Collection addAndMapModelObjects(Collection origModelObjects, int index)
    {
        Collection objs = null;
        objs = newModelObjects(origModelObjects, index);
        if (objs != null)
            mapModelObjects(origModelObjects, objs);
        return objs;
    }

    public Collection newModelObjects(Collection mObjs)
    {
        if (!mObjs.isEmpty())
        {
            List mobjs = new ArrayList();
            if (_isDuplicateOperation)
            {
            }
            else
            {
                currentInsertionIndex = getFilterItemCount();
                currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
                Iterator iter = mObjs.iterator();
                while (iter.hasNext())
                {
                    Object obj = iter.next();
                    if (obj instanceof Parameter)
                    {
                        Parameter parameter = (Parameter) obj;
                        if (isADoubleReferenceToAModelParameter(parameter))
                        {
                            OneButton1Msg.showWarning(null, "Warning: Add and Map", "The parameter you are attempting to add is already referenced" +
                                    " in this interface.", "OK", OneButton1Msg.DEFAULT_SIZE);
                            return null;
                        }
                        OptimizationToolBuild model = (OptimizationToolBuild) getModel();
                        if (model.getOptimizationToolVariableParameterMap().containsKey(parameter))
                            mobjs.add(newModelObject(parameter, QMOOConfiguration.VARIABLE));
                        else if (model.getOptimizationToolObjectiveParameterMap().containsKey(parameter))
                            mobjs.add(newModelObject(parameter, QMOOConfiguration.OBJECTIVE));
                        else
                        {
                            OneButton1Msg.showWarning(null, "Warning: Add and Map", "You are attempting to add " +
                                    "a parameter, which is not an optimization variable or objective.", "OK", OneButton1Msg.DEFAULT_SIZE);
                            return null;
                        }
                    }
                }
                addToContexts(mobjs);
            }
            return mobjs;
        }
        return mObjs;
    }

    public Collection newModelObjects(Collection c, boolean flag)
    {
        return null;
    }

	public Collection newModelObjects(Collection modelObjects, int index)
    {
        if (!modelObjects.isEmpty())
        {
            if (index < 0 || index > getFilterItemCount())
                currentInsertionIndex = getFilterItemCount();
            else
                currentInsertionIndex = index;
            currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
            Collection mobjs = newModelObjects(modelObjects);
            return mobjs;
        }
        return modelObjects;
    }

    public ModelObject newModelObject(ModelObject parameter, String modelObjectType)
    {
        Parameter o = null;
        if (parameter != null)
        {
            if (modelObjectType.equals(QMOOConfiguration.VARIABLE))
            {
                VariableParameter variableParameter =
                        (VariableParameter) ((OptimizationToolBuild) _model).getOptimizationToolVariableParameterMap().get(parameter);
                VariableParameter interfaceVariableParameter = new VariableParameter(this, getNextId(), variableParameter);
                o = interfaceVariableParameter.getParameter();
                _interfaceVariableMap.put(o, interfaceVariableParameter);
                _interfaceOptimizationParametersMap.put(o, interfaceVariableParameter);
            }
            else if (modelObjectType.equals(QMOOConfiguration.OBJECTIVE))
            {
                ObjectiveParameter objectiveParameter =
                        (ObjectiveParameter) ((OptimizationToolBuild) _model).getOptimizationToolObjectiveParameterMap().get(parameter);
                ObjectiveParameter interfaceObjectiveParameter = new ObjectiveParameter(this, getNextId(), objectiveParameter);
                o = interfaceObjectiveParameter.getParameter();
                _interfaceObjectiveMap.put(o, interfaceObjectiveParameter);
                _interfaceOptimizationParametersMap.put(o, interfaceObjectiveParameter);
            }
            modelObjects.add(o);
            return o;
        }
        return super.newModelObject(modelObjectType);
    }

    public ModelObject newModelObject(OptimizationParameter parameter)
    {
        Parameter o = null;
        if (parameter != null)
        {
            if (parameter instanceof VariableParameter)
            {
                VariableParameter variableParameter = new VariableParameter(this, getNextId(), (VariableParameter) parameter);
                o = variableParameter.getParameter();
                _interfaceVariableMap.put(o, variableParameter);
                _interfaceOptimizationParametersMap.put(o, variableParameter);
            }
            else if (parameter instanceof ObjectiveParameter)
            {
                ObjectiveParameter objectiveParameter = new ObjectiveParameter(this, getNextId(), (ObjectiveParameter) parameter);
                o = objectiveParameter.getParameter();
                _interfaceObjectiveMap.put(o, objectiveParameter);
                _interfaceOptimizationParametersMap.put(o, objectiveParameter);
            }
            modelObjects.add(o);
            return o;
        }
        return super.newModelObject(DomeReal.TYPE_INFO.getTypeName());
    }

    public ModelObject newModelObject(String modelObjectType)
    {
        Parameter mobj = null;

        //interface is being duplicated
        if (_isDuplicateOperation)
        {

        }
        else
        {
            if (modelObjectType.equals(QMOOConfiguration.VARIABLE))
            {
                VariableParameter interfaceVariableParameter = new VariableParameter(this, getNextId(), modelObjectType);
                mobj = interfaceVariableParameter.getParameter();
                _interfaceVariableMap.put(mobj, interfaceVariableParameter);
                _interfaceOptimizationParametersMap.put(mobj, interfaceVariableParameter);
            }
            else if (modelObjectType.equals(QMOOConfiguration.OBJECTIVE))
            {
                ObjectiveParameter interfaceObjectiveParameter = new ObjectiveParameter(this, getNextId(), modelObjectType);
                mobj = interfaceObjectiveParameter.getParameter();
                _interfaceObjectiveMap.put(mobj, interfaceObjectiveParameter);
                _interfaceOptimizationParametersMap.put(mobj, interfaceObjectiveParameter);
            }
            else
            {
                OneButton1Msg.showError(null, "interface build mode error", "unknown parameter type", "ok", OneButton1Msg.DEFAULT_SIZE);
                return null;
            }
            modelObjects.add(mobj);
            addToContexts(mobj);
            return mobj;
        }
        return super.newModelObject(modelObjectType);
    }

    private void addToContexts(ModelObject mobj)
    {
        if (!currentView.equals(ToolInterface.BUILD_VIEW))
            _buildViewContext.addModelObjectReference(mobj);
    }

    private void addToContexts(Collection mObjs)
    {
        if (!currentView.equals(ToolInterface.BUILD_VIEW))
        {
            _buildViewContext.addModelObjectReferences(mObjs);
        }
    }

    protected void mapModelObjects(Collection parameters, Collection iterfaceParameters)
    {
        if (parameters.size() != iterfaceParameters.size())
        {
            System.err.println("some objects not copied correctly -- no automapping");
            return;
        }
        ConnectionMappingManager mgr = ((OptimizationToolBuild)getModel()).getMappingManager();
        Iterator interfaceParams = iterfaceParameters.iterator();
        Iterator params = parameters.iterator();
        List errors = new ArrayList();
        while (interfaceParams.hasNext())
        {
            Object objrp = interfaceParams.next();
            Object objp = params.next();
            if (objrp instanceof Parameter && objp instanceof Parameter)
            {
                Parameter rp = (Parameter) objrp;
                Parameter p = (Parameter) objp;
                if (isADoubleReferenceToAModelParameter(p))
                {
                    OneButton1Msg.showWarning(null, "Warning: Map", "You are attempting to map a parameter that is already" +
                            " referenced in this interface.", "OK", OneButton1Msg.DEFAULT_SIZE);
                    return;
                }
                try
                {
                    mgr.addMapping(rp, p);
                    addCausalityListener(p, rp); //for no-default interface only
                }
                catch (RuntimeException ex)
                {
                    /**/System.out.println(ex);
                    errors.add(ex);
                }
            }
        }
        if (errors.size() == 1)
            throw (RuntimeException) errors.get(0);
        else if (errors.size() > 1)
            throw new MultipleErrorsException(errors);
    }

    protected void copyModelObjects(ToolInterface iface)
    {
        ConnectionMappingManager mmgr = ((OptimizationToolBuild)_model).getMappingManager();

		//Copy modelObjects - causal views
		Collection origModelObjects = iface.getModelObjects();
		ArrayList mObjs = new ArrayList(origModelObjects); // mutable list

        if (iface instanceof OptimizationInterfaceBuild)
        {
            for (Iterator i = mObjs.iterator(); i.hasNext();)
            {
                ModelObject mObj = (ModelObject) i.next();
                if (mObj instanceof Parameter)
                {
                    ModelObject newParameter = null;
                    if (((OptimizationInterfaceBuild) iface).getInterfaceVariableMap().containsKey(mObj))
                    {
                        VariableParameter originalVariableParameter = (VariableParameter)((OptimizationInterfaceBuild)iface).getInterfaceVariableMap().get(mObj);
                        newParameter = newModelObject(originalVariableParameter);
                    }
                    else if (((OptimizationInterfaceBuild)iface).getInterfaceObjectiveMap().containsKey(mObj))
                    {
                        ObjectiveParameter originalObjectiveParameter = (ObjectiveParameter)((OptimizationInterfaceBuild)iface).getInterfaceObjectiveMap().get(mObj);
                        newParameter = newModelObject(originalObjectiveParameter);
                    }
                    _modelObjectMap.put(mObj.getId(), newParameter);
                    Collection mapping = mmgr.getMappingsForParameter((Parameter) mObj);
                    if (newParameter != null && newParameter instanceof Parameter)
                        mmgr.addMappings((Parameter) newParameter, mapping);
                    addToContexts(newParameter);
                }
            }
        }
    }

    private void addCausalityListener(Object source, Object target)
    {
        if (source instanceof Parameter && target instanceof Parameter)
        {
            Model m = ((Parameter) source).getModel();
            CausalityChangeListener cl = new ParameterCausalityListener((Parameter) target);
            m.addCausalityChangeListener(source, cl);
            _sharedListenerMap.put(target, cl);
        }

    }

    protected int getFilterItemCount()
    {
        CausalityStatus stat = getNewObjectCausality();
        if (stat == null)
        {
            return 0;
        }
        if (stat.equals(CausalityStatus.INDEPENDENT))
        {
            return _independentFilter.getItemCount();
        }
        else if (stat.equals(CausalityStatus.RESULT))
        {
            return _resultFilter.getItemCount();
        }
        return _independentFilter.getItemCount();
    }

    protected class ModelObjectsListener implements DListListener
    {
        HashMap listenermap = new HashMap(); //key -iface obj, value - listeners
        AnalysisTool model;
        ConnectionMappingManager mgr;

        protected ModelObjectsListener(AnalysisTool model)
        {
            this.model = model;
            this.mgr = model.getMappingManager();
        }

        //add listeners on pre-existing objects
        protected void addListenersToExistingObjects()
        {

        }

        //obj - model object, newobj - interface model view object
        public void addListeners(ModelObject obj, ModelObject ifaceobj)
        {

        }

        public void intervalAdded(DListEvent e)
        {

        }

        public void intervalChanged(DListEvent e)
        {

        }

        public void itemsRemoved(DListEvent e)
        {
            intervalRemoved(e);
        }

        public void intervalRemoved(DListEvent e)
        {
            List items = e.getItems();
            for (Iterator i = items.iterator(); i.hasNext();)
            {
                ModelObject obj = (ModelObject) i.next();

                boolean isReference = !distinctModelObjs.remove(obj);
                if (isReference)
                {
                    //do not proceed if it is a reference
                    return;
                }
                if (obj instanceof Parameter)
                {
                    //remove obj from build and causal views
                    Parameter parameterToBeRemoved = (Parameter) _interfaceOptimizationToolMap.get(obj);
                    OptimizationInterfaceBuild.this.deleteModelObject(parameterToBeRemoved);
                    DefaultContextBuilder bv = (DefaultContextBuilder) OptimizationInterfaceBuild.this._buildViewContext;
                    bv.removeModelObjectReference(parameterToBeRemoved);
                    mgr.removeMapping(parameterToBeRemoved, (Parameter) obj);
                    Object[] listeners = (Object[]) listenermap.get(parameterToBeRemoved);
                    if (listeners != null)
                    {
                        PropertyChangeListener nameListen = (PropertyChangeListener) listeners[0];
                        obj.removePropertyChangeListener(NameListener.NAME, nameListen);
                    }
                    listenermap.remove(parameterToBeRemoved);
                }
            }
        }

        public void itemsReplaced(DListEvent e)
        {
            // System.out.println("in itemsReplaced");
            throw new UnsupportedOperationException();
        }


    }

     protected class ParameterCausalityListener implements CausalityChangeListener
	{
		Parameter newParam;
		Parameter source; //null in case of default interface

		protected ParameterCausalityListener(Parameter newParam)
		{
			this.newParam = newParam;
		}

		protected ParameterCausalityListener(Parameter source, Parameter target)
		{
			this.source = source;
			this.newParam = target;
		}

        public void causalityChanged(CausalityChangeEvent e)
        {
            CausalityStatus newstat = e.getNewCausalityStatus();
            OptimizationInterfaceBuild.this.changeCausality(newParam, newstat);
            CausalityStatus oldstat = e.getOldCausalityStatus();
            if (oldstat == null || !(oldstat.equals(CausalityStatus.INTERMEDIATE)))
            {
                if (newstat != null && newstat.equals(CausalityStatus.INTERMEDIATE))
                {
                    modelObjects.remove(newParam);
                    _buildViewContext.removeModelObjectReference(newParam);
                    _tempModelObjects.add(newParam);
                    _tempModelObjectsMap.put(newParam.getId(), newParam);
                }
            }
            else if (newstat == null || !(newstat.equals(CausalityStatus.INTERMEDIATE)))
            {
                if (oldstat != null && oldstat.equals(CausalityStatus.INTERMEDIATE))
                {
                    modelObjects.add(newParam);
                    _buildViewContext.addModelObjectReference(newParam);
                    OptimizationInterfaceBuild.this.changeCausality(newParam, newstat);
                    _tempModelObjects.remove(newParam);
                    _tempModelObjectsMap.remove(newParam.getId());
                }
            }
        }

		public Parameter getSource()
		{
			return source;
		}
	}

    protected static class ModelObjectNameListener extends NameListener
    {
        ModelObject newobj;

        protected ModelObjectNameListener(ModelObject newobj)
        {
            this.newobj = newobj;
        }

        public void nameChanged(String newName)
        {
            newobj.setName(newName);
        }
    }

    public void save(String fileName) throws IOException
	{
		String oldFileName = this.fileName;
		this.fileName = fileName;
		saveFile(fileName);
		firePropertyChange(FILENAME, oldFileName, fileName);
	}

    protected void saveFile(String fileName) throws IOException
	{
		Document xmlDoc = createXmlDocument();
        save(xmlDoc, fileName);

        if (version.compareTo(lastSavedVersion) == 1)
        { // version is newer
            save(xmlDoc, fileName);
            lastSavedVersion = version.duplicate();
        }
        else
        { // change version if content is different
            if (hasChanged(xmlDoc))
            {
                save(xmlDoc, fileName);
            }
            else
            { // increment version
                version.revSaveVersion();
                Element versionNode = (Element) xmlDoc.selectSingleNode("/model/modelinfo/version");
                if (versionNode != null)
                    versionNode.setText(version.toString());
                xmlDoc = createXmlDocument();
                save(xmlDoc, fileName);
                lastSavedVersion = version.duplicate();
            }
        }

        //save the mappings in a separate file
        Element mappingRootElement = headerToXmlElement();
        addXmlContent(mappingRootElement); //add interface version info
        Element mappingElement = createMappingElement();
        mappingRootElement.add(mappingElement);

        Element lookupElement = createDefaultLookupElement();
        mappingRootElement.add(lookupElement);

        int extensionIndex = fileName.indexOf(".dti");
        StringBuffer mappingFilename = new StringBuffer(fileName.substring(0, extensionIndex));
		mappingFilename.append("-mappings");
		Document mappingdoc = DocumentFactory.getInstance().createDocument();
		mappingdoc.add(mappingRootElement);
		save(mappingdoc, mappingFilename.toString());
	}

    //returns null for non-default interface
	public Element createDefaultLookupElement()
	{
	    Element topXmlElement = DocumentHelper.createElement("lookupdata");
		Element interfaceOptimizationToolMap = DocumentHelper.createElement("interfaceOptimizationToolMap");

        Set objectKeySet = _interfaceOptimizationToolMap.keySet();
        for (Iterator keys = objectKeySet.iterator(); keys.hasNext();)
        {
            ModelObject analysisToolObject = (ModelObject) keys.next();
            ModelObject interfaceObject = (ModelObject) _interfaceOptimizationToolMap.get(analysisToolObject);
            Element keyElement = analysisToolObject.toXmlRef();
            keyElement.addAttribute("description", "key");
            interfaceOptimizationToolMap.add(keyElement);
            Element valueElement = interfaceObject.toXmlRef();
            valueElement.addAttribute("description", "value");
            interfaceOptimizationToolMap.add(valueElement);
        }
		topXmlElement.add(interfaceOptimizationToolMap);
        return topXmlElement;
	}

    public void loadLookUpInfo(Element xmlMappings)
    {
        if (xmlMappings != null)
        {
            Element interfaceOptimizationToolMapElement = (Element) xmlMappings.selectSingleNode("/" + getXmlTag() + "/lookupdata/interfaceOptimizationToolMap");
            if (interfaceOptimizationToolMapElement == null)
                return;
            Model m = getModel();
            List pairs = interfaceOptimizationToolMapElement.elements();
            for (Iterator p = pairs.iterator(); p.hasNext();)
            {
                Element key = (Element) p.next();
                Id modelObjectId = parseXmlRef(key);
                ModelObject modelObject = m.getModelObjectById(modelObjectId);
                if (modelObject != null)
                { //object belongs directly to the model
                    Element value = (Element) p.next();
                    Id interfaceObjectId = parseXmlRef(value);
                    ModelObject interfaceObject = getModelObjectById(interfaceObjectId);
                    _interfaceOptimizationToolMap.put(modelObject, interfaceObject);
                }
            }
        }
    }

    public void deleteModelObjects(Collection objectsToBeDeleted)
    {
        for (Iterator i = objectsToBeDeleted.iterator(); i.hasNext();)
        {
            removeCausalityListener(i.next());
        }
        for (Iterator iter = objectsToBeDeleted.iterator(); iter.hasNext();)
        {
            Object obj = iter.next();
            if (obj instanceof Parameter)
            {
                if (_interfaceVariableMap.containsKey(obj))
                {
                    _interfaceVariableMap.remove(obj);
                    _interfaceOptimizationParametersMap.remove(obj);
                }
                else if (_interfaceObjectiveMap.containsKey(obj))
                {
                    _interfaceObjectiveMap.remove(obj);
                    _interfaceOptimizationParametersMap.remove(obj);
                }
            }
        }
        removeModelObjectsFromBuildContext(objectsToBeDeleted);
        super.deleteModelObjects(objectsToBeDeleted);
		ConnectionMappingManager mgr = ((OptimizationToolBuild)_model).getMappingManager();
		for (Iterator i = objectsToBeDeleted.iterator(); i.hasNext();)
        {
            ModelObject obj = (ModelObject) i.next();
            if (obj instanceof Parameter)
            {
                mgr.removeAllMappings((Parameter) obj);
            }
        }
    }

    protected void removeAllModelReferences(Parameter parameter)
    {
        ConnectionMappingManager mgr = ((OptimizationToolBuild)_model).getMappingManager();
        for (Iterator iter = _model.getModelObjects().iterator(); iter.hasNext();)
        {
            Object object = iter.next();
            if (object instanceof Parameter)
            {
                Parameter modelParameter = (Parameter) object;
                Collection interfaceMappings = mgr.getInterfaceConnections(modelParameter);
                if (interfaceMappings != null && interfaceMappings.size() > 0)
                {
                    for (Iterator i = interfaceMappings.iterator(); i.hasNext(); )
                    {
                        Object interfaceObject = i.next();
                        if (interfaceObject instanceof Parameter)
                        {
                            Parameter interfaceParameter = (Parameter) interfaceObject;
                            if (interfaceParameter.getId().toString().equals(parameter.getId().toString()))
                            {
                            }
                        }
                    }
                }
            }
        }
    }
    public boolean removeModelObjects(Collection mObjects)
    {
        boolean ret = true;
        for (Iterator i = mObjects.iterator(); i.hasNext();)
        {
            removeCausalityListener(i.next());
        }
        removeModelObjectsFromBuildContext(mObjects);
        ret = modelObjects.removeAll(mObjects);

        ConnectionMappingManager mgr = ((AnalysisTool) _model).getMappingManager();
        for (Iterator i = mObjects.iterator(); i.hasNext();)
        {
            ModelObject obj = (ModelObject) i.next();
            if (obj instanceof Parameter)
            {
                mgr.removeAllMappings((Parameter) obj);
            }
        }
        return ret;
    }

    private void removeModelObjectsFromBuildContext(Collection mObjects)
    {
        for (Iterator i = mObjects.iterator(); i.hasNext();)
        {
            ModelObject mObject = (ModelObject) i.next();
            try
            {
                _buildViewContext.removeModelObjectReference(mObject); //removes all parameters
            }
            catch (Exception ex)
            {
            }
        }
    }

    private void removeCausalityListener(Object ob)
    {
        if (ob instanceof Parameter)
        {
            Parameter p = (Parameter) ob;
            Object obj = _sharedListenerMap.get(p);
            if (obj != null)
            {
                ParameterCausalityListener cl = (ParameterCausalityListener) obj;
                Parameter source = cl.getSource();
                if (source != null)
                    source.getModel().removeCausalityChangeListener(cl);
                _sharedListenerMap.remove(p);
            }
        }
    }

    private boolean isADoubleReferenceToAModelParameter(Parameter p)
    {
        Collection c = ((OptimizationToolBuild)_model).getMappingManager().getInterfaceConnections(p);
        for (Iterator iter = c.iterator(); iter.hasNext();)
        {
            Object obj = iter.next();
            if (obj instanceof Parameter)
            {
                if (((Parameter)obj).getScope() instanceof OptimizationInterfaceBuild)
                    return true;
            }
        }
        return false;
    }
}
