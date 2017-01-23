// AbstractDomeModel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.model.dome;

import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.IterationVariable;
import mit.cadlab.dome3.objectmodel.model.AbstractModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.CommonAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilterFunction;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractPropertyChangeFilter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.FilterFunction;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.FilterFunctions;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.FunctionFilter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeEvent;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.CausalitySupport;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.Parameters;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// to do: move filterable up a class?
// move views up hierarchy
// model causality manager
// read/write xml

public abstract class AbstractDomeModel extends AbstractModel implements DomeModel {
    protected Filter subscriptionsFilter;
    protected Filter independentFilter;
    protected Filter intermediateFilter;
    protected Filter resultFilter;
    protected Filter indeterminateFilter;
    protected String fileName = "";
    protected IntegrationProject iProject = null;
    protected boolean isIntegrationModel;
    protected ModelObjectFactory moFactory;
    protected AbstractCausalityManager modelCausalityManager;
    protected HashMap views = new HashMap(); // keyed by view name
    protected HashMap filters = new HashMap(); // keyed by filter name; later make it immutable
    protected ConnectionMappingManager mappingManager;
    protected ModelInterfaceManager interfaces;
    protected DomeModelInterface defaultIface;      //default interface for this model
    protected ArrayList AuxFiles = new ArrayList();
    protected DirectedGraph modelGraph;
    protected boolean GraphHasChanged=false;

    public AbstractDomeModel(Id id) {
        super(id);
    }

    public AbstractDomeModel(Id id, DomeModel model) {
        super(id, model, false);    // false = do not copy model objects yet
    }

    public AbstractDomeModel(Element xmlElement) {
        super(xmlElement);

        if (xmlElement == null)
            throw new IllegalArgumentException(getTypeName() + " - no xml model info");
    }

    public AbstractDomeModel(String file, Element xmlElement) {
        this(xmlElement);
        this.fileName = file;
    }


    public ModelObject getModelObjectById(Id modelObjectId) {

        ModelObject mo = super.getModelObjectById(modelObjectId);
        if (mo != null) return mo;

        Collection everything = getModelObjects();
        for (Iterator it = everything.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof ModelObjectScope) {
                mo = ((ModelObjectScope) o).getModelObjectById(modelObjectId);
                if (mo != null) return mo;
            }
        }

	    // if the model is an instance of OptimizationToolBuild it is possible that the parameter
	    // is inside one of the qmoo project integration models and will not be found at this point
	    // therefore the message below is misleading, as it indicates that the model object
	    // does not exist at all, if the model is an instance of OptimizationToolBuild the message
	    // below will not be displayed to the user

	    System.out.println(getName() + " could not find: " + modelObjectId);
        return null;

    }


    protected Element createModelInfoElement() {
        Element e = super.createModelInfoElement();
        if (isIntegrationModel())
            e.addElement("imodel");
        return e;
    }

    protected void parseModelInfoElement(Element xmlElement) {
        super.parseModelInfoElement(xmlElement);
        Element imodelXml = (Element) xmlElement.selectSingleNode("/modelinfo/imodel");
        isIntegrationModel = (imodelXml != null);
    }


    protected HashMap copyModelObjects(Collection origModelObjects) {
        return copyModelObjects(origModelObjects, true);
    }

    protected HashMap copyModelObjects(Collection origModelObjects, boolean createBuildContext) {
        if (createBuildContext)
            modelObjects.add(createBuildContext());
        ArrayList mObjs = new ArrayList(origModelObjects); // mutable list
        Context origBuildContext = null;
        Iterator it = origModelObjects.iterator();
        while (it.hasNext()) {
            ModelObject mObj = (ModelObject) it.next();
            if (mObj.getId().equals(DomeModel.BUILD_CONTEXT_ID)) {
                origBuildContext = (Context) mObj;
                mObjs.remove(mObj);
                break;
            }
        }
        HashMap idMap = super.copyModelObjects(mObjs);
        // fill build context
        Context buildCxt = this.getBuildContext();
        if (origBuildContext == null) { // no build context so fill with modelobjects
            List allButBuildCxt = new ArrayList(modelObjects);
            allButBuildCxt.remove(buildCxt);
            buildCxt.addModelObjectReferences(allButBuildCxt);
        } else { // exists, so copy
            Iterator contents = origBuildContext.getModelObjectReferences().iterator();
            while (contents.hasNext()) {
                ModelObject mObj = (ModelObject) contents.next();
                buildCxt.addModelObjectReference((ModelObject) idMap.get(mObj.getId()));
            }
        }
        return idMap; // does not include build context, should it?
    }

    public IntegrationProject getIntegrationProject() {
        return iProject;
    }

    public boolean isIntegrationModel() {
        return isIntegrationModel || getIntegrationProject() != null;
    }

    public ModelInterfaceManager getModelInterfacesManager() {
        return interfaces;
    }

    public Collection getModelInterfaces() {
        return interfaces.getInterfaces();
    }

    public ConnectionMappingManager getMappingManager() {
        return mappingManager;
    }

    protected Context createBuildContext() {
        Object[] ctrParams = new Object[]{this, BUILD_CONTEXT_ID};
        Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
        if (cxt == null)
            throw new DomeObjectException("createBuildContext failed");
        cxt.setName(BUILD_VIEW);
        return cxt;
    }

    public Context getBuildContext() {
        return (Context) getModelObjectById(BUILD_CONTEXT_ID);
    }

	protected Context createFileContext()
	{
		Object[] ctrParams = new Object[]{this, FILE_CONTEXT_ID};
		Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
		if (cxt == null)
			throw new DomeObjectException("createFileContext failed");
		cxt.setName(FILES_CONTEXT);
		return cxt;
	}

	public Context getFileContext()
	{
		return (Context) getModelObjectById(FILE_CONTEXT_ID);
	}

    // causality support
    protected AbstractCausalityManager createCausalityManager() {
        return new DomeModelCausalityManager();
    }

    protected CausalityManager getCausalityManager() {
        return modelCausalityManager;
    }

    protected void changeCausality(Object obj, CausalityStatus cause) {
        modelCausalityManager.changeCausality(obj, cause);
    }

    protected class DomeModelCausalityManager
            extends AbstractModelObjectScope.AbstractInternalCausalityManager {
        protected DListListener relationObjectsListListener;
        protected CausalityChangeListener relationCausalityChangesListener;
        protected DListListener interfaceObjectsListListener;
        protected CausalityChangeListener interfaceCausalityChangesListener;
        private MappingChangeListener mappingChangeListener;

        public DomeModelCausalityManager() {
            relationObjectsListListener = new ModelScopeModelObjectsListListener();
            relationCausalityChangesListener = new RelationCausalityChangesListener();
            interfaceObjectsListListener = new ModelScopeModelObjectsListListener();
            interfaceCausalityChangesListener = new InterfaceCausalityChangesListener();
            mappingChangeListener = new ModelMappingChangesListener();
            mappingManager.addMappingChangeListener(mappingChangeListener);
            interfaces.addInterfacesListener(
                    new DListListener() {
                        public void intervalChanged(DListEvent e) {
                        }

                        public void intervalAdded(DListEvent e) {
                            Collection items = e.getItems();
                            for (Iterator i = items.iterator(); i.hasNext();) {
                                addInterface((ModelInterface) i.next());
                            }
                        }

                        public void intervalRemoved(DListEvent e) {
                            Collection items = e.getItems();
                            for (Iterator i = items.iterator(); i.hasNext();) {
                                removeInterface((ModelInterface) i.next());
                            }
                        }

                        public void itemsRemoved(DListEvent e) {
                            Collection items = e.getItems();
                            for (Iterator i = items.iterator(); i.hasNext();) {
                                removeInterface((ModelInterface) i.next());
                            }
                        }

                        public void itemsReplaced(DListEvent e) {
                            throw new UnsupportedOperationException("can not set objects in Model Interface Manager!");
                        }
                    }
            );
            if (isIntegrationModel()) {
                addSubscriptionListener();
            }
        }

        protected void addSubscriptionListener() {
            subscriptionsFilter.addFilterListener(new SubscriptionListener());
        }

        protected void addInterface(ModelInterface iface) {
            Iterator it = iface.getModelObjects().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Parameter)
                    addObject(obj);
            }
            iface.addModelObjectsListener(interfaceObjectsListListener);
            iface.addCausalityChangeListener(interfaceCausalityChangesListener);

        }

        protected void removeInterface(ModelInterface iface) {
            iface.removeCausalityChangeListener(interfaceCausalityChangesListener);
            iface.removeModelObjectsListener(interfaceObjectsListListener);
            Iterator it = iface.getModelObjects().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Parameter)
                    removeObject(obj);
            }
        }

        protected DListListener createModelObjectsListListener() {
            return new DomeModelModelObjectsListListener();
        }

        protected class DomeModelModelObjectsListListener extends ModelScopeModelObjectsListListener {
            protected void addItems(List items) {
                Iterator it = items.iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (obj instanceof Parameter)
                        addObject(obj);
                    else if (obj instanceof Relation)
                        addRelation((Relation) obj);
                }
            }

            protected void removeItems(List items) {
                Iterator it = items.iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (obj instanceof Parameter)
                        removeObject(obj);
                    else if (obj instanceof Relation)
                        removeRelation((Relation) obj);
                }
            }

            protected void addRelation(Relation rel) {

                Iterator it = rel.getModelObjects().iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    //	System.out.println("addRelation in AbstractDomeModel.java:" + obj);
                    if (obj instanceof Parameter)
                        addObject(obj);
                }

                rel.addModelObjectsListener(relationObjectsListListener);
                rel.addCausalityChangeListener(relationCausalityChangesListener);

            }

            protected void removeRelation(Relation rel) {
                rel.removeCausalityChangeListener(relationCausalityChangesListener);
                rel.removeModelObjectsListener(relationObjectsListListener);
                Iterator it = rel.getModelObjects().iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (obj instanceof Parameter)
                        removeObject(obj);
                }
            }
        }

        /**
         * listens to all causality changes in model relations
         */
        class RelationCausalityChangesListener implements CausalityChangeListener {
            public void causalityChanged(CausalityChangeEvent event) {
                processCausalityChange((Parameter) event.getParameter());
                setGraphHasChanged(true);
            }
        }

        class InterfaceCausalityChangesListener implements CausalityChangeListener {
            public void causalityChanged(CausalityChangeEvent event) {
                processCausalityChange((Parameter) event.getParameter());
                // interface causality does not affect model graph
            }
        }

        class SubscriptionListener implements DListListener {
            public void intervalAdded(DListEvent e) {
                processNewSubscriptions(e.getItems());
                setGraphHasChanged(true);
            }

            public void intervalChanged(DListEvent e) {
            }

            public void intervalRemoved(DListEvent e) {
                processSubscriptionsRemoval(e.getItems());
                setGraphHasChanged(true);
            }

            public void itemsRemoved(DListEvent e) {
                processSubscriptionsRemoval(e.getItems());
                setGraphHasChanged(true);
            }

            public void itemsReplaced(DListEvent e) {
            }
        }

        protected void processNewSubscriptions(List items) {
            for (int i = 0; i < items.size(); i++) {
                processNewSubscription((Subscription) items.get(i));
            }
        }

        protected void processNewSubscription(Subscription sub) {
            Iterator subObjs = sub.getModelObjects().iterator();
	        Object obj;
            while (subObjs.hasNext()) {
                obj = subObjs.next();
	            if (obj instanceof Parameter)
                    this.addObject(obj, sub.getCausality(obj));
            }

            independentFilter.addListToFilter(sub);
            indeterminateFilter.addListToFilter(sub);
            intermediateFilter.addListToFilter(sub);
            resultFilter.addListToFilter(sub);
        }

        public void processNewSubscriptionInterface(SubscriptionInterface subIface, DefaultSubscription sub) {
            Iterator subObjs = sub.getModelObjects().iterator();
            while (subObjs.hasNext()) {
                Object obj = subObjs.next();
	            if (obj instanceof Parameter) {
		            Parameter param = (Parameter)obj;
		            Parameter ifaceParam = sub.getInterfaceParameter(param);
		            this.addObject(ifaceParam, sub.getCausality(param));
	            }
            }
            //todo the original subscription is left in the casaulity manager and ther subscription interface is not added to the filter --> make sure this is ok later
            independentFilter.addListToFilter(subIface);
            indeterminateFilter.addListToFilter(subIface);
            intermediateFilter.addListToFilter(subIface);
            resultFilter.addListToFilter(subIface);
            processSubscriptionRemoval(sub);
        }

        protected void processSubscriptionsRemoval(List items) {
            for (int i = 0; i < items.size(); i++) {
                processSubscriptionRemoval((Subscription) items.get(i));
            }
        }

        protected void processSubscriptionRemoval(Subscription sub) {
            Iterator subObjs = sub.getModelObjects().iterator();
            while (subObjs.hasNext()) {
                Object obj = subObjs.next();
	            if (obj instanceof Parameter)
                    this.removeObject(obj);
            }

            independentFilter.removeListToFilter(sub);
            indeterminateFilter.removeListToFilter(sub);
            intermediateFilter.removeListToFilter(sub);
            resultFilter.removeListToFilter(sub);
        }

        /**
         * listens to all mapping changes for model parameters
         */
        class ModelMappingChangesListener implements MappingChangeListener {
            public void mappingChanged(MappingChangeEvent event) {
	            if(event.getParameter() != null) processMappingChange(event.getParameter());
	            if(event.getVisualization() != null) processMappingChange(event.getVisualization());
                setGraphHasChanged(true);
            }
        }

        /**
         * reevaluate model causality effects for all model parameters
         * mapped to relation parameter
         */
        private void processCausalityChange(Parameter relParam) {
            Collection mappedParams = mappingManager.getMappingsForParameter(relParam);
            Iterator it = mappedParams.iterator();
            while (it.hasNext()) {
                Parameter p = (Parameter) it.next();
//				if (AbstractDomeModel.this.equals(p.getModel())) { // is this necessary?
                if (AbstractDomeModel.this.equals(p.getScope())) { // is this necessary?
                    updateModelParameterCausalityStatus(p);
                }
            }
            if (relParam.getScope() instanceof Relation) {
                updateRelationParameterCausalityStatus(relParam);
            } else if (relParam.getScope() instanceof ModelInterface) {
                updateInterfaceParameterCausalityStatus(relParam);
            }
        }

        private void updateRelationParameterCausalityStatus(Parameter relParam) {
            CausalityStatus newCS = ((Relation) relParam.getScope()).getCausality(relParam);
            if ((newCS == CausalityStatus.INDEPENDENT) || (newCS == CausalityStatus.RESULT)) {
                Collection mappedParams = mappingManager.getMappingsForParameter(relParam);
                if (!mappedParams.isEmpty())
                    newCS = CausalityStatus.INTERMEDIATE;
            }
            changeCausality(relParam, newCS);
        }

        private void updateInterfaceParameterCausalityStatus(Parameter ifaceParam) {
            CausalityStatus newCS = ((ModelInterface) ifaceParam.getScope()).getCausality(ifaceParam);
            if (getCausality(ifaceParam) == null) {
                //item does not exist in causality map
                //it will be added by AbstractModelObjectScope
                return;
            }
            changeCausality(ifaceParam, newCS);
        }

        private void updateSubscriptionParameterCausalityStatus(Parameter subParam) {
            CausalityStatus newCS = ((CausalitySupport) subParam.getScope()).getCausality(subParam);
            if ((newCS == CausalityStatus.INDEPENDENT) || (newCS == CausalityStatus.RESULT)) {
                Collection mappedParams = mappingManager.getMappingsForParameter(subParam);
                if (!mappedParams.isEmpty())
                    newCS = CausalityStatus.INTERMEDIATE;
            }
            changeCausality(subParam, newCS);
        }

        private void updateModelParameterCausalityStatus(Parameter p) {
            CausalityStatus newCS = CausalityStatus.INDETERMINATE;
            Collection mappedParams = mappingManager.getMappingsForParameter(p);
            if (mappedParams.isEmpty()) {
                newCS = CausalityStatus.INDEPENDENT;
            } else if (mappedParams.size() == 1) {
                Parameter relParam = (Parameter) mappedParams.iterator().next();
                ModelObjectScope scope = relParam.getScope();
                CausalityStatus rpCS = null;
                if (scope instanceof Relation || scope instanceof Subscription
                        || scope instanceof SubscriptionInterface) {
                    rpCS = ((CausalitySupport) scope).getCausality(relParam);
                }
                if (scope instanceof ModelInterface) {
                    //we change interface parameter causality based on model parameter
                    //so do not change the model parameter causality due to
                    //interface parameter causality change
                }
                if (CausalityStatus.INDEPENDENT.equals(rpCS)) {
                    newCS = CausalityStatus.INDEPENDENT;
                } else if (CausalityStatus.RESULT.equals(rpCS)) {
                    newCS = CausalityStatus.RESULT;
                } else if (CausalityStatus.INTERMEDIATE.equals(rpCS)) {
                    newCS = CausalityStatus.INTERMEDIATE;
                } else {
                    newCS = CausalityStatus.INDETERMINATE; // what to do here?
                }
            } else { // more than one mappedParams
                boolean drivenByOutput = false;
                boolean drivingInput = false;
                boolean interfaceMapping = false;
                Iterator it = mappedParams.iterator();
                while (it.hasNext()) {
                    Parameter relParam = (Parameter) it.next();
                    ModelObjectScope scope = relParam.getScope();
                    CausalityStatus rpCS = null;
                    if (scope instanceof Relation || scope instanceof Subscription
                            || scope instanceof SubscriptionInterface) {
                        rpCS = ((CausalitySupport) scope).getCausality(relParam);
                    }
                    if (scope instanceof ModelInterface) {
                        //we change interface parameter causality based on model parameter
                        //so do not change the model parameter causality due to
                        //interface parameter causality change
                        interfaceMapping = true;
                    }
                    if (CausalityStatus.INDEPENDENT.equals(rpCS)) {
                        drivingInput = true;
                    } else if (CausalityStatus.RESULT.equals(rpCS) || CausalityStatus.INTERMEDIATE.equals(rpCS)) {
                        drivenByOutput = true;
                    }
                }
                if (drivingInput && drivenByOutput)
                    newCS = CausalityStatus.INTERMEDIATE;
                else if (drivingInput)
                    newCS = CausalityStatus.INDEPENDENT;
                else if (drivenByOutput) { // impossible to be driven by more than one item!
                    if (!interfaceMapping) {
                        throw new IllegalStateException("found parameter driven by more than one item: " + Names.getNameId(p) +
                                " --> " + Names.getNameIds(mappedParams));
                    } else {
                        newCS = CausalityStatus.RESULT;
                    }
                } else { // mapped but not driven or driving
                    newCS = CausalityStatus.INDEPENDENT;
                }
            }
            changeCausality(p, newCS); // should we do this if it's null?
        }

        /**
         * reevaluates parameter causality based on mapping change
         */
        private void processMappingChange(Parameter p) {
            if (Parameters.isRelationParameter(p))
                updateRelationParameterCausalityStatus(p);
            else if (Parameters.isSubscriptionParameter(p))
                updateSubscriptionParameterCausalityStatus(p);
            else if (Parameters.isInterfaceParameter(p))
                updateInterfaceParameterCausalityStatus(p);
            else
                updateModelParameterCausalityStatus(p);
        }

	    private void processMappingChange(Visualization p) {
/*	        if (Parameters.isRelationParameter(p))
	            updateRelationParameterCausalityStatus(p);
	        else if (Parameters.isSubscriptionParameter(p))
	            updateSubscriptionParameterCausalityStatus(p);
	        else if (Parameters.isSubscriptionInterfaceParameter(p))
	            updateSubscriptionInterfaceParameterCausalityStatus(p);
	        else if (Parameters.isInterfaceParameter(p))
	            updateInterfaceParameterCausalityStatus(p);
	        else
	            updateModelParameterCausalityStatus(p);
*/	    }

        protected CausalityStatus getInitialCausality(Object obj) {
            if (obj instanceof Parameter) {
                Parameter param = (Parameter) obj;
	            if (param.getCurrentDataObject() instanceof IterationVariable)
	                return CausalityStatus.INDEPENDENT;
	            if (param.getScope() instanceof CausalitySupport) {
		            CausalityStatus cs = ((CausalitySupport)param.getScope()).getCausality(param);
		            if (cs == null)
			            return CausalityStatus.INDETERMINATE;
		            else
		                return cs;
	            }
                else
                    return CausalityStatus.INDETERMINATE;
            } else {
                throw new IllegalStateException("can not get initial causality of " + Names.getNameId(obj));
            }
        }
    }

    protected void createModelObjectView() {
        // create model object type view
        List modelObjectTypeView = new ArrayList();
        Filter paramFilter = new DomeModelFilter(new FilterFunctions.Parameters());
        Filter relFilter = new DomeModelFilter(new FilterFunctions.Relations());
        Filter cxtFilter = new DomeModelFilter(new DomeModelContexts());
        filters.put(DomeModel.PARAMETERS_FILTER, paramFilter);
        filters.put(DomeModel.RELATIONS_FILTER, relFilter);
        filters.put(DomeModel.CONTEXTS_FILTER, cxtFilter);
        modelObjectTypeView.add(paramFilter);
        modelObjectTypeView.add(relFilter);
        modelObjectTypeView.add(cxtFilter);
        views.put(DomeModel.OBJECT_TYPE_VIEW, Collections.unmodifiableList(modelObjectTypeView));
        if (isIntegrationModel())
            addSubscriptionFilter();
    }

    protected void addSubscriptionFilter() { // must be called after createModelObjectView // todo: find a more elegant way
        if (subscriptionsFilter != null)
            return;
        List modelObjectTypeView = new ArrayList((List) views.get(DomeModel.OBJECT_TYPE_VIEW));
        subscriptionsFilter = new DomeModelFilter(new FilterFunctions.Subscriptions());
        subscriptionsFilter.addFilterListener(new SubscriptionProcessingListener());
        if (modelCausalityManager != null)
            ((DomeModelCausalityManager) modelCausalityManager).addSubscriptionListener();
        filters.put(DomeModel.SUBSCRIPTIONS_FILTER, subscriptionsFilter);
        modelObjectTypeView.add(subscriptionsFilter);
        views.put(DomeModel.OBJECT_TYPE_VIEW, Collections.unmodifiableList(modelObjectTypeView));
    }


    protected void createCausalView() {
        // create causal view
        independentFilter = new CausalityFilter(CausalityStatus.INDEPENDENT);
        intermediateFilter = new CausalityFilter(CausalityStatus.INTERMEDIATE);
        resultFilter = new CausalityFilter(CausalityStatus.RESULT);
        indeterminateFilter = new CausalityFilter(CausalityStatus.INDETERMINATE);
        List modelCausalityView = new ArrayList();
        modelCausalityView.add(independentFilter);
        modelCausalityView.add(intermediateFilter);
        modelCausalityView.add(resultFilter);
        modelCausalityView.add(indeterminateFilter);
        views.put(DomeModel.CAUSAL_VIEW, Collections.unmodifiableList(modelCausalityView));
    }

    public Filter getFilter(String filterName) {
        return (Filter) filters.get(filterName);
    }

    public List getSubscriptions() {
        if (!isIntegrationModel())
            return Collections.EMPTY_LIST;
        return subscriptionsFilter.getItems();
    }

    public void addSubscriptionsListener(DListListener l) {
        if (!isIntegrationModel())
            return;
        subscriptionsFilter.addFilterListener(l);
    }

    public void removeSubscriptionsListener(DListListener l) {
        if (!isIntegrationModel())
            return;
        subscriptionsFilter.removeFilterListener(l);
    }

    // Filterable support
    public Collection addItemsToFilterListener(DListListener l) {
        modelObjects.addDListListener(l);
        return Collections.unmodifiableList(modelObjects);
    }

    public Collection removeItemsToFilterListener(DListListener l) {
        modelObjects.removeDListListener(l);
        return Collections.unmodifiableList(modelObjects);
    }

    protected class DomeModelFilter extends FunctionFilter implements ViewSupport {
        public DomeModelFilter(FilterFunction filterFunction) {
            super(AbstractDomeModel.this, AbstractDomeModel.this.getNextId(),
                    filterFunction, true);
            addListToFilter(AbstractDomeModel.this);
        }
        //ViewSupport funcs implemented already implemented in superclass AbstractFilter
    }

    protected class DomeModelContexts extends FilterFunctions.Contexts {
        public DomeModelContexts() {
        }

        public boolean keepInFilter(Object obj) {
            return (super.keepInFilter(obj) && !getBuildContext().equals(obj));
        }
    }

    protected class CausalityFilter extends AbstractPropertyChangeFilter implements ViewSupport {
        private CausalityStatus causality;

        public CausalityFilter(CausalityStatus cs) {
            super(AbstractDomeModel.this, AbstractDomeModel.this.getNextId(), true);
            causality = cs;
            setName(cs.toString());
            addListToFilter(AbstractDomeModel.this);
        }

        protected AbstractPropertyChangeFilter.PropertyChangeFilterFunction createFilterFunction() {
            return new CausalityFilterFunction("Causality Filter");
        }

        protected void processCausalityChange(CausalityChangeEvent event) {
            CausalityStatus oldCS = event.getOldCausalityStatus();
            CausalityStatus newCS = event.getNewCausalityStatus();
            if (oldCS == newCS) return;
            if (causality.equals(newCS)) {
                filteredItems.add(event.getParameter());
            } else if (causality.equals(oldCS)) {
                filteredItems.remove(event.getParameter());
            }
        }

        protected boolean keepInFilter(Object obj) {
            return !filteredItems.contains(obj) && modelCausalityManager.isItemOfCausality(obj, causality);
        }

        protected class CausalityFilterFunction extends AbstractFilterFunction
                implements AbstractPropertyChangeFilter.PropertyChangeFilterFunction {
            CausalityChangeListener ccListener;

            public CausalityFilterFunction(String name) {
                super(name);
                ccListener = new CausalityFilterCausalityChangeListener();
            }

            public boolean keepInFilter(Object obj) {
                return CausalityFilter.this.keepInFilter(obj);
            }

            public void addListenerTo(Object obj) {
                modelCausalityManager.addCausalityChangeListener(obj, ccListener);
            }

            public void removeListenerFrom(Object obj) {
                modelCausalityManager.removeCausalityChangeListener(obj, ccListener);
            }

            protected class CausalityFilterCausalityChangeListener implements CausalityChangeListener {
                public void causalityChanged(CausalityChangeEvent event) {
                    processCausalityChange(event);
                }
            }
        }

    }

    // MultiViewSupport interface
    public List getViewNames() {
        return viewNames;
    }

    public List getView(String viewName) {
        if (viewNames.contains(viewName)) {
            List view = (List) views.get(viewName);
            return (view == null) ? Collections.EMPTY_LIST : view;
        }
        return Collections.EMPTY_LIST;
    }

    public void addViewListener(String viewName, DListListener l) {
        // do nothing, views do not change
    }

    public void removeViewListener(String viewName, DListListener l) {
        // do nothing, views do not change
    }

    public Element headerToXmlElement() {
        return super.headerToXmlElement();
    }

    public Element toXmlElement() {
        Element xml = super.toXmlElement();

        Element paramElement, relElement, cxtElement, mapElement,visElement;
        paramElement = DocumentHelper.createElement("parameters");
        relElement = DocumentHelper.createElement("relations");
        visElement = DocumentHelper.createElement("visualizations");
        cxtElement = DocumentHelper.createElement("contexts");

        xml.add(paramElement);
        xml.add(relElement);
        xml.add(visElement);

        // add parameters, relations and contexts
        for (Iterator iter = modelObjects.listIterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof Parameter) {
                Element param = ((Parameter) obj).toXmlElement();
                paramElement.add(param);
            } else if (obj instanceof Relation) {
                Element rel = ((Relation) obj).toXmlElement();
                relElement.add(rel);
            } else if (obj instanceof Context) {
                Element cxt = ((Context) obj).toXmlElement();
                cxtElement.add(cxt);
            }
            //add for visualizations
            else if (obj instanceof Visualization) {
                Element vis = ((Visualization) obj).toXmlElement();
                visElement.add(vis);
            }
        }

        if (isIntegrationModel() && subscriptionsFilter != null && subscriptionsFilter.getItemCount() != 0) {
            Element subscriptionElement = DocumentHelper.createElement("subscriptions");
            xml.add(subscriptionElement);
            List subscriptions = subscriptionsFilter.getItems();
            for (int i = 0; i < subscriptions.size(); i++) {
                subscriptionElement.add(((Subscription) subscriptions.get(i)).toXmlElement());
            }
        }

        xml.add(cxtElement);

        mapElement = DocumentHelper.createElement("mappings");
        xml.add(mapElement);

        // add mappings
        Element mapSubElements = mappingManager.toXmlElement(this, "modelMappings");
        if (mapSubElements != null)
            mapElement.add(mapSubElements);

	    //add AuxFiles
	    Context fileCon = getFileContext();
	    Collection fileConObjects = fileCon.getModelObjectReferences();
	    for (Iterator iterator = fileConObjects.iterator(); iterator.hasNext();) {
		    Parameter p = (Parameter) iterator.next();
		    CausalityStatus stat = modelCausalityManager.getCausality(p);
//		    if (CausalityStatus.INDEPENDENT.equals(stat)) {
			    DomeFile df = (DomeFile) p.getCurrentDataObject();
			    String f = df.getFilePath();
			    Id id = this.getAuxFileIdForFile(f);
			    if(id == null) {
					CommonAuxFile auxFile = new CommonAuxFile(this, new Id(UUIDGenerator.create()),
															  new File(f));
					AuxFiles.add(auxFile);
			    }
//		    }
/*		    else {
			    DomeFile df = (DomeFile) p.getCurrentDataObject();
			    String f = df.getFilePath();
			    Id id = this.getAuxFileIdForFile(f);
			    if (id != null) {
				    AbstractAuxFile auxFile = this.getAuxFileForId(id);
				    AuxFiles.remove(auxFile);
			    }
		    }*/
	    }
        Element auxFileElement = DocumentHelper.createElement("auxfiles");
        for (Iterator itor = AuxFiles.iterator(); itor.hasNext();) {
            AbstractAuxFile f = (AbstractAuxFile) itor.next();
            auxFileElement.add(f.toXmlElement());
        }
        xml.add(auxFileElement);
        return xml;
    }

    public class SubscriptionProcessingListener implements DListListener {
        public void intervalAdded(DListEvent e) {
        }

        public void intervalChanged(DListEvent e) {
        }

        public void intervalRemoved(DListEvent e) {
            fireInterfaceDeletionEvent(e.getItems());
        }

        public void itemsRemoved(DListEvent e) {
            fireInterfaceDeletionEvent(e.getItems());
        }

        public void itemsReplaced(DListEvent e) {
        }

        protected void fireInterfaceDeletionEvent(List items) {
            for (int i = 0; i < items.size(); i++) {
                Subscription subscription = (Subscription) items.get(i);
                firePropertyChange(SUBSCRIPTION_DELETED, null, subscription);
            }
        }
    }

    public boolean hasChanged(Document modelDoc) {
        String xml = modelDoc.asXML();
        if (lastSavedXml.equals(xml) && !interfaces.hasChanged())
            return false;
        return true;
    }

    protected void save(Document xmlDoc, String fileName) throws IOException {
            // save interfaces
            ((ModelInterfaceManagerBuilder) interfaces).save(fileName);
            // save the model
            super.save(xmlDoc, fileName);
    }

    public void printFilters() {
        System.out.println("model: " + getName());
        Filter[] filters = {independentFilter, intermediateFilter, resultFilter, indeterminateFilter};
        for (int i = 0; i < filters.length; i++) {
            Filter filter = filters[i];
            System.out.println("name: " + filter.getName());
            List items = filter.getItems();
            for (int j = 0; j < items.size(); j++) {
                Object o = (Object) items.get(j);
                System.out.println("   " + Names.getNameId(o));
            }
        }
    }

    //Aux File Utils
    public ArrayList getAuxFiles() {
        return AuxFiles;
    }

    public Id addAuxFile(AbstractAuxFile f) {

        if (getAuxFileIdForFile(f.getFile().getPath()) != null) {
            System.out.println("already exist");
            return null;
        } else if (!checkDuplicateName(f.getFile().getName()))
        {
            System.out.println("duplicate auxiliary file name are not permitted in one model");
            return null;
        }

        AuxFiles.add(f);
        firePropertyChange(DomeModel.AUXFILES_MODIFIED, null, f);
        return f.getId();

    }


    /**
     * this method is to check redudant file path
     * @param _filepath
     * @return
     */
    public Id getAuxFileIdForFile(String _filepath) {
        for (Iterator i = AuxFiles.iterator(); i.hasNext();) {
            AbstractAuxFile f = (AbstractAuxFile) i.next();
            if (f.getFile().getPath().equals(_filepath))
                return f.getId();
        }
        return null;
    }


    public String getAuxFilePathForId(Id _id) {
        for (Iterator i = AuxFiles.iterator(); i.hasNext();) {
            AbstractAuxFile f = (AbstractAuxFile) i.next();
            if (f.getId().equals(_id))
                return f.getFile().getPath();
        }
        return null;
    }

    public AbstractAuxFile getAuxFileForId(Id _id) {
        for (Iterator i = AuxFiles.iterator(); i.hasNext();) {
            AbstractAuxFile f = (AbstractAuxFile) i.next();
            if (f.getId().equals(_id))
                return f;
        }
        return null;
    }


    public boolean checkDuplicateName(String filename) {
        for (Iterator i = AuxFiles.iterator(); i.hasNext();) {
            AbstractAuxFile f = (AbstractAuxFile) i.next();
            if (f.getFile().getName().equals(filename))
                return false;
        }
        return true;
    }

    public void setAuxFile(int index, AbstractAuxFile f) {
        if (index >= 0 && index < AuxFiles.size()) {
            AuxFiles.set(index, f);
            firePropertyChange(DomeModel.AUXFILES_MODIFIED, null, f);
        }
    }

    public void removeAuxFile(AbstractAuxFile f) {
        if (AuxFiles.contains(f)) {
            AuxFiles.remove(f);
            firePropertyChange(DomeModel.AUXFILES_MODIFIED, null, f);
        }
    }

    public void clearAuxFiles(){
        AuxFiles.clear();
        firePropertyChange(DomeModel.AUXFILES_MODIFIED, null, null);
    }

    public void setGraphHasChanged(boolean trueorfalse){
       GraphHasChanged=trueorfalse;
    }
}