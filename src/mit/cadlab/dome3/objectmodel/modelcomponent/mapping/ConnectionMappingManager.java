// ConnectionMappingManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.mapping;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton3Msg;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeCollection;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.OptimizationToolBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.AbstractIntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.mapping.*;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class ConnectionMappingManager implements ModelComponent {
    // to do:
    // need to listen to relations come and go; parameters come and go;
    // delete mappings for parameter when they are removed

    protected Model model;
    protected HashMap byObjMappings;    // hierarchical collection of mappings, indexed by the
    // objects to which they belong.
    // key = model/interface object, value = HashMap (mappings for the object)
    protected HashMap flatMappings; // all mappings in a flat list
    protected MappingChangeSupport mappingChangeListeners;
    protected MappingNameChangeSupport mappingNameChangeListeners;
    protected ConnectionManager connectionMgr;
    protected HashMap listenermap = new HashMap();

    public ConnectionMappingManager(Model model) {
        if (model == null)
            throw new IllegalArgumentException("MappingManager - null model");
        this.model = model;
        byObjMappings = new HashMap();
        flatMappings = new HashMap();
        mappingChangeListeners = new MappingChangeSupport(model);
        mappingNameChangeListeners = new MappingNameChangeSupport(model);
        connectionMgr = new ConnectionManager();
    }

    public ConnectionMappingManager(Model model, Element xmlElement) {
        this(model);
        addMappings(xmlElement);
    }

    public Model getModel() {
        return model;
    }

    public Set getMappedParameters() {
        return Collections.unmodifiableSet(flatMappings.keySet());
    }

    public Collection getInterfaceConnections(Parameter p) {
        return connectionMgr.getInterfaceConnections(p);
    }

    public Collection getInterfaceConnections(Relation modelrel) {
        return connectionMgr.getInterfaceConnections(modelrel);
    }

    public Object getModelConnection(Relation ifacerel) {
        return connectionMgr.getModelConnection(ifacerel);
    }

//	public boolean addConnection(Relation modelRel, Relation ifaceRel) {
//		return connectionMgr.addConnection(modelRel, ifaceRel);
//	}

//	public void removeAllConnections(Relation modelRel) {
//		connectionMgr.removeConnections(modelRel);
//	}

//	public void removeConnection(Relation modelRel, Relation ifaceRel) {
//		connectionMgr.removeConnection(modelRel, ifaceRel);
//	}

//	public void removeConnection(Relation ifaceRel) {
//		connectionMgr.removeConnection(ifaceRel);
//	}

    public Collection getParameterMappings() {
        return Collections.unmodifiableCollection(flatMappings.values());
    }

    public void removeAllMappings(Parameter p) {
        // remove parameter mappings
        Collection mappingsReadOnly = getMappingsForParameter(p);
        Collection mappings = new ArrayList(mappingsReadOnly);
        for (Iterator params = mappings.iterator(); params.hasNext();) {
            Parameter target = (Parameter) params.next();
            removeMapping(p, target);
        }

        //if this is a model parameter, get connections to interface parameters
        //and remove any mappings between those parameters and p
        connectionMgr.removeConnectionsAndMappings(p);
    }

    public void removeAllMappings(Visualization p) {
        // remove parameter mappings
        Collection mappingsReadOnly = getMappingsForVisualization(p);
        Collection mappings = new ArrayList(mappingsReadOnly);
        for (Iterator params = mappings.iterator(); params.hasNext();) {
            Visualization target = (Visualization) params.next();
            removeMapping(p, target);
        }

        //if this is a model parameter, get connections to interface parameters
        //and remove any mappings between those parameters and p
        connectionMgr.removeConnectionsAndMappings(p);
    }

    /*
    public void removeAllIncomingMappings (Parameter p)
    {
        // remove parameter mappings
        Collection mappingsReadOnly = getMappingsForParameter (p);
        Collection mappings = new ArrayList(mappingsReadOnly);
        for (Iterator params = mappings.iterator(); params.hasNext();) {
            Parameter target = (Parameter) params.next();
            removeIncomingMapping (p, target);
        }

        //if this is a model parameter, get connections to interface parameters
        //and remove any mappings between those parameters and p
        connectionMgr.removeConnectionsAndMappings(p);
    }
    */

    public Collection getMappingsForParameter(Parameter p) {
        if (p == null)
            return Collections.EMPTY_LIST;
        ParameterMapping pMap = null;
        pMap = (ParameterMapping) flatMappings.get(p);
        return (pMap == null) ? Collections.EMPTY_LIST : pMap.getMappings();
    }

    public Collection getMappingsForVisualization(Visualization p) {
        if (p == null)
            return Collections.EMPTY_LIST;
        VisualizationMapping pMap = null;
        pMap = (VisualizationMapping) flatMappings.get(p);
        return (pMap == null) ? Collections.EMPTY_LIST : pMap.getMappings();
    }

    public Element getMappingsXMLForParameter(Parameter p) {
        ParameterMapping pMap = (ParameterMapping) flatMappings.get(p);
        if (pMap == null)
            return null;
        return pMap.toXmlElement();
    }

    public Element getMappingsXMLForVisualization(Visualization p) {
        VisualizationMapping pMap = (VisualizationMapping) flatMappings.get(p);
        if (pMap == null)
            return null;
        return pMap.toXmlElement();
    }

    public boolean mappingExists(Parameter p1, Parameter p2) {
        return getMappingsForParameter(p1).contains(p2);
    }

    public boolean mappingExists(Visualization p1, Visualization p2) {
        return getMappingsForVisualization(p1).contains(p2);
    }

    public void addMappings(Element xmlElement) {
        addMappings(model, xmlElement);
    }

    //Qing when it reads the mapping from a saved model/interface file, not need to check valid mapping or not
    public void addMappings(ModelObjectScope scope, Element xmlElement) {
        Id paramId;
        Relation rel = null;
        Parameter p1, p2;
        String relationId;
        List mappedObjects;
        Element paramElement;

        XMLUtils.makeRootElement(xmlElement); // necessary to use XPath locally
        List params = xmlElement.selectNodes("/" + xmlElement.getQName().getName()
                + "/" + Parameter.XML_MAPPED_TAG);

        List errors = new ArrayList();
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            try {
                // get the mapping element
                paramElement = (Element) iter.next();
                if (paramElement == null) {
                    throw new IllegalArgumentException("no mapping parameter");
                }

                // get the primary parameter
                relationId = paramElement.attributeValue("idRelationRef");
                if (relationId != null && !relationId.equals("")) {
                    // the parameter is inside a relation
                    rel = (Relation) scope.getModelObjectById(new Id(relationId));
                    if (rel == null) {
                        throw new IllegalArgumentException("relation does not exist");
                    }
                    paramId = AbstractDomeObject.parseXmlRef(paramElement);
                    if (paramId == null) {
                        throw new IllegalArgumentException("no relation parameter Id in mapping");
                    }
                    p1 = (Parameter) rel.getModelObjectById(paramId);
                    if (p1 == null) {
                        throw new IllegalArgumentException("mapping parameter not found in relation");
                    }
                } else {
                    // the parameter is not inside a relation
                    paramId = AbstractDomeObject.parseXmlRef(paramElement);
                    if (paramId == null) {
                        throw new IllegalArgumentException("no parameter Id in mapping");
                    }
                    p1 = (Parameter) scope.getModelObjectById(paramId);
                    if (p1 == null) {
                        if (scope instanceof DomeModelInterface) {
                            if (((DomeModelInterface) scope).isDefaultInterface()) {
                                p1 = (Parameter) ((DomeModelInterface) scope).getTempModelObjectById(paramId);
                            }
                        }
                        if (scope instanceof AnalysisToolBase) {
                            // search in all iModels
                            List iModels = ((AnalysisToolBase) model).getIntegrationProject().getIntegrationModels();
                            for (Iterator i = iModels.iterator(); i.hasNext();) {
                                ProjectIntegrationModelInfo info = (ProjectIntegrationModelInfo) i.next();
                                DomeModel imod = info.getModel();
                                p1 = (Parameter) imod.getModelObjectById(paramId);
                                if (p1 != null) break;
                            }
                        }
                        if (p1 == null) {
                            throw new IllegalArgumentException(paramId + " mapping parameter not found in scope");
                        }
                    }
                }

                // get the mapped parameters and establish the mappings
                mappedObjects = paramElement.selectNodes("parameter");
                if (mappedObjects == null) {
                    throw new IllegalArgumentException("no target parameters in mapping");
                }
                for (Iterator objs = mappedObjects.iterator(); objs.hasNext();) {
                    try {
                        // get model parameter
                        Element nextObj = (Element) objs.next();
                        paramId = AbstractDomeObject.parseXmlRef(nextObj);
                        if (paramId == null) {
                            throw new IllegalArgumentException("no target parameter Id in mapping");
                        }
                        relationId = nextObj.attributeValue("idRelationRef");
                        if (relationId == null || relationId.equals("")) {
                            // the parameter is not inside a relation
                            p2 = (Parameter) model.getModelObjectById(paramId);
                            if (p2 != null) {
                                addMapping(p1, p2, false);
                                continue;
                            }
                            if (model instanceof IntegrationProject) {
                                //search in all iModels
                                List iModels = ((IntegrationProject) model).getIntegrationModels();
                                for (Iterator i = iModels.iterator(); i.hasNext();) {
                                    ProjectIntegrationModelInfo info = (ProjectIntegrationModelInfo) i.next();
                                    DomeModel imod = info.getModel();
                                    p2 = (Parameter) imod.getModelObjectById(paramId);
                                    if (p2 != null) {
                                        addMapping(p1, p2, false);
                                        continue;
                                    }
                                }
                            }
                            if (model instanceof AnalysisToolBase) {
                                // search in all iModels
                                List iModels = ((AnalysisToolBase) model).getIntegrationProject().getIntegrationModels();
                                for (Iterator i = iModels.iterator(); i.hasNext();) {
                                    ProjectIntegrationModelInfo info = (ProjectIntegrationModelInfo) i.next();
                                    DomeModel imod = info.getModel();
                                    p2 = (Parameter) imod.getModelObjectById(paramId);
                                    if (p2 != null) {
                                        addMapping(p1, p2, false);
                                        continue;
                                    }
                                }
                            }
                            if (p2 == null) {
                                throw new IllegalArgumentException("target parameter not found in scope");
                            }
                        } else {
                            // the parameter is inside a relation
                            if (model instanceof DomeModel && !(model instanceof AnalysisTool))
                                rel = (Relation) model.getModelObjectById(new Id(relationId));
                            else if (model instanceof IntegrationProject) {
                                List imodels = ((AbstractIntegrationProject) model).getIntegrationModels();
                                for (int i = 0; i < imodels.size(); i++) {
                                    DomeModel model = ((ProjectIntegrationModelInfo) imodels.get(i)).getModel();
                                    rel = (Relation) model.getModelObjectById(new Id(relationId));
                                    if (rel != null)
                                        break;
                                }
                            }
                            else if (model instanceof AnalysisTool)
                            {
                                // the parameter is inside a relation of the project inside the anlaysis tool
                                List imodels = ((AnalysisToolBase)model).getIntegrationProject().getIntegrationModels();
                                for (int i = 0; i < imodels.size(); i++)
                                {
                                    DomeModel model = ((ProjectIntegrationModelInfo) imodels.get(i)).getModel();
                                    rel = (Relation) model.getModelObjectById(new Id(relationId));
                                    if (rel != null)
                                        break;
                                }
                            }
                            if (rel == null)
                                throw new IllegalArgumentException("target relation does not exist");
                            p2 = (Parameter) rel.getModelObjectById(paramId);
                            if (p2 == null) {
                                throw new IllegalArgumentException("target parameter not found in relation");
                            }
                        }

                        // create mapping
                        addMapping(p1, p2, false);
                    } catch (Exception e) {
                        e.printStackTrace();
//						errors.add(e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//				errors.add(e);
            }
        }
        if (!errors.isEmpty()) {
            MultipleErrorsException ex = new MultipleErrorsException(getModel().getName() + " load mappings from xml error",
                    errors);
            System.err.println(ex); // todo: figure out how to get this message to client
        }

        addVisualizationMappings(scope, xmlElement);
    }

    public void addVisualizationMappings(ModelObjectScope scope, Element xmlElement) {
        Id paramId;
        Relation rel = null;
        Visualization p1, p2;
        String relationId;
        List mappedObjects;
        Element paramElement;

        XMLUtils.makeRootElement(xmlElement); // necessary to use XPath locally
        List params = xmlElement.selectNodes("/" + xmlElement.getQName().getName()
                + "/" + Visualization.XML_MAPPED_TAG);

        List errors = new ArrayList();
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            try {
                // get the mapping element
                paramElement = (Element) iter.next();
                if (paramElement == null) {
                    throw new IllegalArgumentException("no mapping visualization");
                }

                // get the primary parameter
                relationId = paramElement.attributeValue("idRelationRef");
                if (relationId != null && !relationId.equals("")) {
                    // the parameter is inside a relation
                    rel = (Relation) scope.getModelObjectById(new Id(relationId));
                    if (rel == null) {
                        throw new IllegalArgumentException("relation does not exist");
                    }
                    paramId = AbstractDomeObject.parseXmlRef(paramElement);
                    if (paramId == null) {
                        throw new IllegalArgumentException("no relation visualization Id in mapping");
                    }
                    p1 = (Visualization) rel.getModelObjectById(paramId);
                    if (p1 == null) {
                        throw new IllegalArgumentException("mapping visualization not found in relation");
                    }
                } else {
                    // the parameter is not inside a relation
                    paramId = AbstractDomeObject.parseXmlRef(paramElement);
                    if (paramId == null) {
                        throw new IllegalArgumentException("no visualization Id in mapping");
                    }
                    p1 = (Visualization) scope.getModelObjectById(paramId);
                    if (p1 == null) {
                        if (scope instanceof DomeModelInterface) {
                            if (((DomeModelInterface) scope).isDefaultInterface()) {
                                p1 = (Visualization) ((DomeModelInterface) scope).getTempModelObjectById(paramId);
                            }
                        }
                        if (scope instanceof AnalysisToolBase) {
// search in all iModels
                            List iModels = ((AnalysisToolBase) model).getIntegrationProject().getIntegrationModels();
                            for (Iterator i = iModels.iterator(); i.hasNext();) {
                                ProjectIntegrationModelInfo info = (ProjectIntegrationModelInfo) i.next();
                                DomeModel imod = info.getModel();
                                p1 = (Visualization) imod.getModelObjectById(paramId);
                                if (p1 != null) break;
                            }
                        }
                        if (p1 == null) {
                            throw new IllegalArgumentException(paramId + " mapping visualization not found in scope");
                        }
                    }
                }

                // get the mapped parameters and establish the mappings
                mappedObjects = paramElement.selectNodes("visualization");
                if (mappedObjects == null) {
                    throw new IllegalArgumentException("no target visualization s in mapping");
                }
                for (Iterator objs = mappedObjects.iterator(); objs.hasNext();) {
                    try {
                        // get model parameter
                        Element nextObj = (Element) objs.next();
                        paramId = AbstractDomeObject.parseXmlRef(nextObj);
                        if (paramId == null) {
                            throw new IllegalArgumentException("no target visualization Id in mapping");
                        }
                        relationId = nextObj.attributeValue("idRelationRef");
                        if (relationId == null || relationId.equals("")) {
                            // the parameter is not inside a relation
                            p2 = (Visualization) model.getModelObjectById(paramId);
                            if (p2 != null) {
                                addMapping(p1, p2);
                                break;
                            }
                            if (model instanceof IntegrationProject) {
                                //search in all iModels
                                List iModels = ((IntegrationProject) model).getIntegrationModels();
                                for (Iterator i = iModels.iterator(); i.hasNext();) {
                                    ProjectIntegrationModelInfo info = (ProjectIntegrationModelInfo) i.next();
                                    DomeModel imod = info.getModel();
                                    p2 = (Visualization) imod.getModelObjectById(paramId);
                                    if (p2 != null) break;
                                }
                            }
                            if (model instanceof AnalysisToolBase) {
                                // search in all iModels
                                List iModels = ((AnalysisToolBase) model).getIntegrationProject().getIntegrationModels();
                                for (Iterator i = iModels.iterator(); i.hasNext();) {
                                    ProjectIntegrationModelInfo info = (ProjectIntegrationModelInfo) i.next();
                                    DomeModel imod = info.getModel();
                                    p2 = (Visualization) imod.getModelObjectById(paramId);
                                    if (p2 != null) break;
                                }
                            }
                            if (p2 == null) {
                                throw new IllegalArgumentException("target visualization not found in scope");
                            }
                        } else {
                            // the parameter is inside a relation
                            if (model instanceof DomeModel)
                                rel = (Relation) model.getModelObjectById(new Id(relationId));
                            else if (model instanceof IntegrationProject) {
                                List imodels = ((AbstractIntegrationProject) model).getIntegrationModels();
                                for (int i = 0; i < imodels.size(); i++) {
                                    DomeModel model = ((ProjectIntegrationModelInfo) imodels.get(i)).getModel();
                                    rel = (Relation) model.getModelObjectById(new Id(relationId));
                                    if (rel != null)
                                        break;
                                }
                            }
                            if (rel == null)
                                throw new IllegalArgumentException("target relation does not exist");
                            p2 = (Visualization) rel.getModelObjectById(paramId);
                            if (p2 == null) {
                                throw new IllegalArgumentException("target visualization not found in relation");
                            }
                        }

                        // create mapping
                        addMapping(p1, p2);
                    } catch (Exception e) {
                        e.printStackTrace();
//						errors.add(e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//				errors.add(e);
            }
        }
        if (!errors.isEmpty()) {
            MultipleErrorsException ex = new MultipleErrorsException(getModel().getName() + " load visualization mappings from xml error",
                    errors);
            System.err.println(ex); // todo: figure out how to get this message to client
        }

    }

    public HashMap getMappings(ModelObjectScope scope) {
        HashMap mappings;
        mappings = (HashMap) byObjMappings.get(scope);
        if (mappings == null) {
            mappings = new HashMap();
            byObjMappings.put(scope, mappings);
        }
        return mappings;
    }

    public static ModelObjectScope getObjectTopScope(ModelObject obj) {
        if (obj.getScope() instanceof SubscriptionInterface) {
            return ((SubscriptionInterface) obj.getScope()).getSubscription().getScope();
        }
        while (!(obj.getScope() instanceof ModelInterface) && !(obj.getScope() instanceof Model) &&
                !(obj.getScope() instanceof InterfaceModelView) && !(obj.getScope() instanceof ToolInterface)) {
            obj = (ModelObject) obj.getScope();
        }
        return obj.getScope();
    }

    private HashMap getMappings(ModelObject obj) {
        // locate the mappings for this object
        ModelObjectScope scope = getObjectTopScope(obj);
        return getMappings(scope);
    }

    private HashMap getMappings(ModelObject objA, ModelObject objB) {
        ModelObjectScope scopeA = getObjectTopScope(objA);
        ModelObjectScope scopeB = getObjectTopScope(objB);
        boolean isInterfaceScope = scopeA instanceof ModelInterface || scopeA instanceof InterfaceModelView ||
                scopeA instanceof ToolInterface;
        HashMap mappings = (isInterfaceScope) ? getMappings(scopeA) : getMappings(scopeB);
        return mappings;
    }


    public boolean addMapping(Parameter p, Parameter target, boolean toCheckValid) {
        // determine whether it's a model or interface mapping
        ModelObjectScope scopeP = getObjectTopScope(p);
        ModelObjectScope scopeTarget = getObjectTopScope(target);
        if ((scopeP instanceof ModelInterface || scopeP instanceof InterfaceModelView) ||
                (scopeTarget instanceof ModelInterface || scopeTarget instanceof InterfaceModelView ||
                (scopeTarget instanceof ToolInterface) || (scopeP instanceof ToolInterface)))
            return addInterfaceMapping(p, target, toCheckValid);
        else if (scopeTarget instanceof OptimizationToolBase) {
            return addQMOOMappings(p, target, toCheckValid);
        } else
            return addModelMapping(p, target, toCheckValid);
    }

    public boolean addMapping(Parameter p, Parameter target) {
        // determine whether it's a model or interface mapping
        ModelObjectScope scopeP = getObjectTopScope(p);
        ModelObjectScope scopeTarget = getObjectTopScope(target);
        if ((scopeP instanceof ModelInterface || scopeP instanceof InterfaceModelView) ||
                (scopeTarget instanceof ModelInterface || scopeTarget instanceof InterfaceModelView) ||
                (scopeTarget instanceof ToolInterface) || (scopeP instanceof ToolInterface))
            return addInterfaceMapping(p, target);
        else if (scopeTarget instanceof OptimizationToolBuild) {
            return addQMOOMappings(p, target);
        } else
            return addModelMapping(p, target);
    }

    public boolean addMapping(Visualization p, Visualization target) {
        // determine whether it's a model or interface mapping
        ModelObjectScope scopeP = getObjectTopScope(p);
        ModelObjectScope scopeTarget = getObjectTopScope(target);
        if ((scopeP instanceof ModelInterface || scopeP instanceof InterfaceModelView) ||
                (scopeTarget instanceof ModelInterface || scopeTarget instanceof InterfaceModelView))
            return addInterfaceMapping(p, target);
        else
            return addModelMapping(p, target);
    }

    /**
     * Create a bidirectional mapping between two model objects.
     * @param p Model object
     * @param target Model object
     */
    public boolean addModelMapping(Parameter p, Parameter target) {
        if (isValidMapping(p, Collections.singletonList(target), false, true)) {
            if (_addMapping(p, target)) {
                if (_addMapping(target, p)) { // bidirectional
                    addModelMappingHook(p, target);
                    fireMappingChange(p);
                    fireMappingChange(target);
                }
            }
            return true;
        }
        return false;
    }

    public boolean addModelMapping(Visualization p, Visualization target) {
        if (isValidMapping(p, Collections.singletonList(target), false)) {
            if (_addMapping(p, target)) {
                if (_addMapping(target, p)) { // bidirectional
                    addModelMappingHook(p, target);
                    fireMappingChange(p);
                    fireMappingChange(target);
                }
            }
            return true;
        }
        return false;
    }


    public boolean addModelMapping(Parameter p, Parameter target, boolean toCheckValid) {
        if (toCheckValid) {
            if (isValidMapping(p, Collections.singletonList(target), false, true)) {
                if (_addMapping(p, target)) {
                    if (_addMapping(target, p)) { // bidirectional
                        addModelMappingHook(p, target);
                        fireMappingChange(p);
                        fireMappingChange(target);
                    }
                }
                return true;
            }
        } else {
            if (_addMapping(p, target)) {
                if (_addMapping(target, p)) { // bidirectional
                    addModelMappingHook(p, target);
                    fireMappingChange(p);
                    fireMappingChange(target);
                }
            }
            return true;
        }
        return false;
    }


    public boolean addQMOOMappings(Parameter p, Parameter target)
    {
        if (target.getScope() instanceof DefaultSubscription)
        {
            target = ((DefaultSubscription) target.getScope()).getInterfaceParameter(target.getId().getIdString());
        }
        if (isValidMapping(p, Collections.singletonList(target), true, true))
        {
            if (_addQMOOMapping(p, target))
            {
                if (_addQMOOMapping(target, p))
                {
                    if (p.getScope() instanceof AnalysisToolBase)
                        target.addDeletionListener(((AnalysisToolBase) p.getScope()).getAnalysisToolDeletionListener());
                    else
                        p.addDeletionListener(((AnalysisToolBase) target.getScope()).getAnalysisToolDeletionListener());
                    addModelMappingHook(p, target);
                    fireMappingChange(p);
                    fireMappingChange(target);
                }

            }
            return true;
        }
        return false;
    }

    public boolean addQMOOMappings(Parameter p, Parameter target, boolean toCheckValid)
    {
        if (toCheckValid)
        {
            if (isValidMapping(p, Collections.singletonList(target), true, true))
            {
                if (_addQMOOMapping(p, target))
                {
                    if (_addQMOOMapping(target, p))
                    {
                        p.addDeletionListener(((AnalysisToolBase) target.getScope()).getAnalysisToolDeletionListener());
                        addModelMappingHook(p, target);
                        fireMappingChange(p);
                        fireMappingChange(target);
                    }

                }
                return true;
            }
        }
        else
        {
            if (_addQMOOMapping(p, target))
            {
                if (_addQMOOMapping(target, p))
                {
                    ((Parameter) p).addDeletionListener(((AnalysisToolBase) target.getScope()).getAnalysisToolDeletionListener());
                    addModelMappingHook(p, target);
                    fireMappingChange(p);
                    fireMappingChange(target);
                }

            }
            return true;
        }
        return false;
    }


    /**
     * Create a bidirectional mapping between two model objects.
     * Relax the valid mapping constraints -- parameters in the same model is ok.
     * @param p Model object
     * @param target Model object
     */
    public boolean addRuntimeMapping(Parameter p, Parameter target) {
        if (isValidMapping(p, Collections.singletonList(target), true, true)) {
            if (_addMapping(p, target)) {
                if (_addMapping(target, p)) { // bidirectional
                    addModelMappingHook(p, target);
                    fireMappingChange(p);
                    fireMappingChange(target);
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Implement this method to process the mapping after it has been added successfully
     * @param p Model object
     * @param target Model object
     */
    protected abstract void addModelMappingHook(Parameter p, Parameter target);

    protected abstract void addModelMappingHook(Visualization p, Visualization target);

    protected class ParameterNameListener extends NameListener {
        private Parameter p;

        protected ParameterNameListener(Parameter p) {
            this.p = p;
        }

        public void nameChanged(String newName) {
            fireMappingNameChange(p);
        }
    }

    protected class VisualizationNameListener extends NameListener {
        private Visualization v;

        protected VisualizationNameListener(Visualization v) {
            this.v = v;
        }

        public void nameChanged(String newName) {
            fireMappingNameChange(v);
        }
    }


    public void updateMappingListeners(Parameter p, DataObject oldObject) {
        // TODO
        // p's data object changed -- if the new type is compatible
        // get the shadow listener from the old object, remove
        // it from the old object, and add it to the new one
        // get the owner of the shadow listener (look in mappings?),
        // and add the new object's shadow listener to the owner
    }


    /**
     * Qing:-- June 09 05,
     * add the check to see if p is an interface parameter and target is a model parameter or if it is the opposite way
     *
     */
    public boolean addInterfaceMapping(Parameter p, Parameter target)
    {
       if((getObjectTopScope(target) instanceof Model) &&
                    ((getObjectTopScope(p) instanceof ModelInterface) ||
                    (getObjectTopScope(p) instanceof InterfaceModelView) ||
                    (getObjectTopScope(p) instanceof ToolInterface))) {
                return _addInterfaceMapping(p, target);
            }
        else if((getObjectTopScope(p) instanceof Model) &&
                    ((getObjectTopScope(target) instanceof ModelInterface) ||
                    (getObjectTopScope(target) instanceof InterfaceModelView) ||
                    (getObjectTopScope(target) instanceof ToolInterface))){
                 //System.out.println("interface parameter:"+target.getName()+"  model parameter:"+p.getName());
                 return _addInterfaceMapping(target, p);
        }
        return false;
    }

    /**
     * Create a unidirectional mapping between a model object and an interface object.
     * Creates a separate hidden mapping (a "connection") to keep track of the reverse relationship.
     * Check for loop mapping performed in addInterfaceMapping(Parameter,Parameter,toCheckValid)
     * Note: this method is overridden in ConnectionMappingManagerRuntime
     * @param p Interface object
     * @param target Model object
     */
    public boolean _addInterfaceMapping(Parameter p, Parameter target)
    {
      if (isValidMapping(p, Collections.singletonList(target), false))
        {
            if (_addMapping(p, target) && connectionMgr.addConnection(target, p))
            {
                //add a connection between model and interface relation
                //if the parameters are in relation
                ModelObjectScope scopeP = p.getScope();   //interface relation
                ModelObjectScope scopeTarget = target.getScope(); //model relation
                if ((scopeP instanceof Relation) && (scopeTarget instanceof Relation))
                {
                    Object modelRel = connectionMgr.getModelConnection((Relation) scopeP);
                    if (modelRel == null)
                    { //if the connection does not exist already
                        connectionMgr.addConnection((Relation) scopeTarget, (Relation) scopeP);
                    }
                }
                addInterfaceMappingHook(p, target);
                fireMappingChange(p);
            }
            return true;
        }
        return false;
    }

    /**
     * Qing:-- June 09 05,
     * add the check to see if p is an interface parameter and target is a model parameter or if it is the opposite way
     *
     */
    public boolean addInterfaceMapping(Visualization p, Visualization target) {
       if((getObjectTopScope(target) instanceof Model) &&
                    ((getObjectTopScope(p) instanceof ModelInterface) ||
                    (getObjectTopScope(p) instanceof InterfaceModelView) ||
                    (getObjectTopScope(p) instanceof ToolInterface))) {
                return _addInterfaceMapping(p, target);
            }
        else if((getObjectTopScope(p) instanceof Model) &&
                    ((getObjectTopScope(target) instanceof ModelInterface) ||
                    (getObjectTopScope(target) instanceof InterfaceModelView) ||
                    (getObjectTopScope(target) instanceof ToolInterface))){
                 //System.out.println("interface parameter:"+target.getName()+"  model parameter:"+p.getName());
                 return _addInterfaceMapping(target, p);
        }
        return false;
    }
    /**
     *
     * @param p: interface visualization
     * @param target: model visualization
     * @return
     */
    public boolean _addInterfaceMapping(Visualization p, Visualization target) {
        if (isValidMapping(p, Collections.singletonList(target), false)) {
            if (_addMapping(p, target) && connectionMgr.addConnection(target, p)) {
                //add a connection between model and interface relation
                //if the parameters are in relation
                ModelObjectScope scopeP = p.getScope();   //interface relation
                ModelObjectScope scopeTarget = target.getScope(); //model relation
                if ((scopeP instanceof Relation) && (scopeTarget instanceof Relation)) {
                    Object modelRel = connectionMgr.getModelConnection((Relation) scopeP);
                    if (modelRel == null) { //if the connection does not exist already
                        connectionMgr.addConnection((Relation) scopeTarget, (Relation) scopeP);
                    }
                }
                addInterfaceMappingHook(p, target);
                fireMappingChange(p);
            }
            return true;
        }
        return false;
    }

    /**
     * Create a unidirectional mapping between a model object and an interface object.
     * Creates a separate hidden mapping (a "connection") to keep track of the reverse relationship.
     * Qing--- Nov 17th, in this case, no need to check loop mapping
     * @param p Interface object
     * @param target Model object
     */
    public boolean addInterfaceMapping(Parameter p, Parameter target, boolean toCheckValid) {
        if (!toCheckValid) {
            return addInterfaceMapping(p, target);
        } else {
            if (isValidMapping(p, Collections.singletonList(target), false, false)) {
                return addInterfaceMapping(p, target);
            }
        }
        return false;
    }


    protected boolean _addMapping(Parameter p, Parameter target) {
        ParameterMapping pMap = (ParameterMapping) flatMappings.get(p);
        if (pMap == null) {
            pMap = new ParameterMapping(p);
            HashMap mappings = getMappings(p, target);
            mappings.put(p, pMap);
            flatMappings.put(p, pMap);
        }
        return pMap.addMapping(target);
    }

    protected boolean _addMapping(Visualization p, Visualization target) {
        VisualizationMapping pMap = (VisualizationMapping) flatMappings.get(p);
        if (pMap == null) {
            pMap = new VisualizationMapping(p);
            HashMap mappings = getMappings(p, target);
            mappings.put(p, pMap);
            flatMappings.put(p, pMap);
        }
        return pMap.addMapping(target);
    }

    protected boolean _addQMOOMapping(Parameter p, Parameter target) {
        ParameterMapping pMap = (ParameterMapping) flatMappings.get(p);
        if (pMap == null) {
            pMap = new ParameterMapping(p);
            HashMap mappings = getMappings(p);
            mappings.put(p, pMap);
            flatMappings.put(p, pMap);
        }
        return pMap.addMapping(target);
    }

    /**
     * Implement this method to process the mapping after it has been added successfully
     * @param p Interface object
     * @param target Model object
     */
    protected abstract void addInterfaceMappingHook(Parameter p, Parameter target);

    protected abstract void addInterfaceMappingHook(Visualization p, Visualization target);

    public void removeMapping(Parameter p, Parameter target) {
        if (_removeMapping(p, target)) {
            fireMappingChange(p);
            boolean success = _removeMapping(target, p);
            if (success) {
                fireMappingChange(target);
            }
            removeMappingHook(p, target);
        }
    }

    public void removeMapping(Visualization p, Visualization target) {
        if (_removeMapping(p, target)) {
            fireMappingChange(p);
            boolean success = _removeMapping(target, p);
            if (success) {
                fireMappingChange(target);
            }
            removeMappingHook(p, target);
        }
    }

    /**
     * Implement this method to process the mapping after it has been removed successfully
     * @param p
     * @param target
     */
    protected abstract void removeMappingHook(Parameter p, Parameter target);

    protected abstract void removeMappingHook(Visualization p, Visualization target);

    /*
    public void removeIncomingMapping(Parameter p, Parameter target)
    {
        if (_removeMapping(target, p))
        {
            fireMappingChange(target);

            //remove mapping name change listeners
            String key = p.getId().getIdString() + target.getId().getIdString();
            Object obj = listenermap.get(key);
            if(obj != null) {
                Object[] listeners = (Object[])obj;
                if(listeners.length == 2) {
                    //remove only the listener on p (it doesn't need to listen to changes to itself)
                    NameListener nl = (NameListener)listeners[1];
                    p.removePropertyChangeListener(NameListener.NAME, nl);
                }
                listenermap.remove(key);
            }
        }
    }
    */

    private boolean _removeMapping(Parameter p, Parameter target) {
        ParameterMapping pMap = (ParameterMapping) flatMappings.get(p);
        if (pMap == null)
            return false;
        if (pMap.removeMapping(target)) {
            if (pMap.isEmpty()) {
                HashMap mappings = getMappings(p, target);
                mappings.put(p, pMap);
                flatMappings.remove(p);
            }
            //remove mappings for contained parameters in a DomeCollection such as DomeList
            if (p.getCurrentDataObject() instanceof DomeCollection &&
                    target.getCurrentDataObject() instanceof DomeCollection) {
                Collection pContents = ((DomeCollection) p.getCurrentDataObject()).getContents();
                Collection targetContents = ((DomeCollection) target.getCurrentDataObject()).getContents();
                for (Iterator i = pContents.iterator(); i.hasNext();) {
                    Parameter param = (Parameter) i.next();
                    Collection mappings = new ArrayList(getMappingsForParameter(param));
                    for (Iterator j = mappings.iterator(); j.hasNext();) {
                        Parameter pr = (Parameter) j.next();
                        if (targetContents.contains(pr)) {
                            removeMapping(param, pr);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean _removeMapping(Visualization p, Visualization target) {
        VisualizationMapping pMap = (VisualizationMapping) flatMappings.get(p);
        if (pMap == null)
            return false;
        if (pMap.removeMapping(target)) {
            if (pMap.isEmpty()) {
                HashMap mappings = getMappings(p, target);
                mappings.put(p, pMap);
                flatMappings.remove(p);
            }
            //remove mappings for contained parameters in a DomeCollection such as DomeList
            /* if(p.getCurrentDataObject() instanceof DomeCollection &&
                    target.getCurrentDataObject() instanceof DomeCollection) {
                Collection pContents =  ((DomeCollection)p.getCurrentDataObject()).getContents();
                Collection targetContents =  ((DomeCollection)target.getCurrentDataObject()).getContents();
                for(Iterator i = pContents.iterator(); i.hasNext(); ) {
                    Parameter param = (Parameter)i.next();
                    Collection mappings = new ArrayList(getMappingsForParameter(param));
                    for(Iterator j = mappings.iterator(); j.hasNext(); ) {
                        Parameter pr = (Parameter)j.next();
                        if(targetContents.contains(pr)) {
                            removeMapping(param, pr);
                        }
                    }
                }
            } */
            return true;
        }
        return false;
    }


// assumes Collection is list of parameters...will die otherwise!
    public void addMappings(Parameter p, Collection targets)
    {
        Iterator it = targets.iterator();
        while (it.hasNext())
        {
            Parameter target = (Parameter) it.next();
            ModelObjectScope scopeP = getObjectTopScope(p);
            ModelObjectScope scopeTarget = getObjectTopScope(target);
            if ((scopeP instanceof ModelInterface || scopeP instanceof InterfaceModelView ||
                    scopeP instanceof ToolInterface) || (scopeTarget instanceof ModelInterface ||
                    scopeTarget instanceof InterfaceModelView))
                addInterfaceMapping(p, target);
            else if (scopeP instanceof OptimizationToolBuild)
            {
                addQMOOMappings(p, target);
            }
            else
                addModelMapping(p, target);
        }
    }

    public void addMappings(Visualization p, Collection targets) {
        Iterator it = targets.iterator();
        while (it.hasNext()) {
            Visualization target = (Visualization) it.next();
            ModelObjectScope scopeP = getObjectTopScope(p);
            ModelObjectScope scopeTarget = getObjectTopScope(target);
            if ((scopeP instanceof ModelInterface || scopeP instanceof InterfaceModelView ||
                    scopeP instanceof ToolInterface) || (scopeTarget instanceof ModelInterface ||
                    scopeTarget instanceof InterfaceModelView))
                addInterfaceMapping(p, target);
            else if (scopeP instanceof OptimizationToolBuild) {
                //**addQMOOMapping(p, target);
            } else
                addModelMapping(p, target);
        }
    }


    // assumes Collection is list of parameters...will die otherwise!
    private void addModelMappings(Parameter p, Collection targets) {
        if (isValidMapping(p, targets, false, true)) {
            HashMap mappings = getMappings(p);

            ParameterMapping pMap = (ParameterMapping) mappings.get(p);
            if (pMap == null) {
                pMap = new ParameterMapping(p);
                mappings.put(p, pMap);
                flatMappings.put(p, pMap);
            }

            Collection addedParams = pMap.addMappings(targets);
            if (!addedParams.isEmpty()) {
                Iterator it = addedParams.iterator();
                while (it.hasNext()) {
                    _addMapping((Parameter) it.next(), p);
                }
                fireMappingChange(p);
                fireMappingChanges(addedParams);
            }
        }
    }

    public void removeMappings(Parameter p, Collection targets) {
        HashMap mappings = getMappings(p);

        ParameterMapping pMap = (ParameterMapping) mappings.get(p);
        if (pMap == null) return;
        Collection removedParams = pMap.removeMappings(targets);
        if (!removedParams.isEmpty()) {
            if (pMap.isEmpty()) {
                mappings.remove(p);
                flatMappings.remove(p);
            }
            Iterator it = removedParams.iterator();
            while (it.hasNext()) {
                Parameter p1 = (Parameter) it.next();
                _removeMapping(p1, p);
                removeMappingHook(p1, p);
            }
            fireMappingChange(p);
            fireMappingChanges(removedParams);
        }
    }

    public void removeMappings(Visualization p, Collection targets) {
        HashMap mappings = getMappings(p);

        VisualizationMapping pMap = (VisualizationMapping) mappings.get(p);
        if (pMap == null) return;
        Collection removedParams = pMap.removeMappings(targets);
        if (!removedParams.isEmpty()) {
            if (pMap.isEmpty()) {
                mappings.remove(p);
                flatMappings.remove(p);
            }
            Iterator it = removedParams.iterator();
            while (it.hasNext()) {
                Visualization p1 = (Visualization) it.next();
                _removeMapping(p1, p);
                removeMappingHook(p1, p);
            }
            fireMappingChange(p);
            fireMappingChanges(removedParams);
        }
    }

    public void addMappingChangeListener(MappingChangeListener l) {
        mappingChangeListeners.addMappingChangeListener(l);
    }

    public void removeMappingChangeListener(MappingChangeListener l) {
        mappingChangeListeners.removeMappingChangeListener(l);
    }

    public void addMappingChangeListener(Parameter param, MappingChangeListener l) {
        mappingChangeListeners.addMappingChangeListener(param, l);
    }

    public void removeMappingChangeListener(Parameter param, MappingChangeListener l) {
        mappingChangeListeners.removeMappingChangeListener(param, l);
    }

    protected void fireMappingChange(Parameter p) {
        mappingChangeListeners.fireMappingChange(p, getMappingsForParameter(p));
    }

    protected void fireMappingChange(Visualization p) {
        mappingChangeListeners.fireMappingChange(p, getMappingsForVisualization(p));
    }

    private void fireMappingChanges(Collection params) {
        Iterator it = params.iterator();
        while (it.hasNext())
            fireMappingChange((Parameter) it.next());
    }

    public void addMappingNameChangeListener(MappingNameChangeListener l) {
        mappingNameChangeListeners.addMappingNameChangeListener(l);
    }

    public void removeMappingNameChangeListener(MappingNameChangeListener l) {
        mappingNameChangeListeners.removeMappingNameChangeListener(l);
    }

    public void addMappingNameChangeListener(Parameter param, MappingNameChangeListener l) {
        mappingNameChangeListeners.addMappingNameChangeListener(param, l);
    }

    public void removeMappingChangeListener(Parameter param, MappingNameChangeListener l) {
        mappingNameChangeListeners.removeMappingNameChangeListener(param, l);
    }

    private void fireMappingNameChange(Parameter p) {
        mappingNameChangeListeners.fireMappingNameChange(p);
    }

    private void fireMappingNameChange(Visualization v) {
        mappingNameChangeListeners.fireMappingNameChange(v);
    }

    /**
     * Check if the mappings are valid.
     * @param p Dome parameter
     * @param targets Dome parameters
     * @return Mapping validity status (true = valid, false = invalid)
     */
    private boolean isValidMapping(Parameter p, Collection targets, boolean relaxDifferentModelConstraint, boolean checkLoopMapping) {
        if (p == null || targets == null) return false;

        for (Iterator params = targets.iterator(); params.hasNext();) {
            Parameter param = (Parameter) params.next();

            // check unit compatibility
            DataObject pObj = p.getCurrentDataObject();
            DataObject tgtObj = param.getCurrentDataObject();
            if (this instanceof ConnectionMappingManagerRuntime) {
                // do not set pObj in runtime as this is done via other mechanisms in runtime
                // of course, isValidMapping shouldn't be changing the state of the parameters in the first place!
                // fix by doing the to do below
            } else {
                // change made by sittha starts..
                if (pObj.getUnit() != null && tgtObj.getUnit() != null)
                    if (!pObj.getUnit().equivalent(tgtObj.getUnit())) {
                        String pName = p.getName();
                        String pscopeName = p.getScope().getName();
                        String paramName = param.getName();
                        String paramscopeName = param.getScope().getName();
                        String msg = pName + " in " + pscopeName + " and " + paramName + " in " + paramscopeName + " are incompatible.";
                        OneButton1Msg.showError(null, "Unit assignment error",
                                "Input and output units are incompatible. Cannot perform mapping.\n" + msg,
                                "ok", new Dimension());
                        return false;
                    }
/*				try {
					if (pObj.getUnit() != null)
						pObj.setValues(tgtObj);
				} catch (UnitsException e) {
					String pName = p.getName();
					String pscopeName = p.getScope().getName();
					String paramName = param.getName();
					String paramscopeName = param.getScope().getName();
					String msg = pName + " in " + pscopeName + " and " + paramName + " in " + paramscopeName + " are incompatible.";
					OneButton1Msg.showError(null, "Unit assignment error",
							"Input and output units are incompatible. Cannot perform mapping.\n" + msg,
							"ok", new Dimension());
					return false;
				}*/
                //end of change made by sittha
            }

            // check relation mappings
            //Qing Change here: check both ways for relation mapping!
            if (checkRelationMapping(p, param) == false || checkRelationMapping(param, p) == false)
                return false;

            if (checkSubscriptionMapping(p, param) == false)
                return false;

            // check model mappings
            if (checkModelMapping(p, param) == false)
                return false;

            // check duplicate mappings
            if (isDuplicateMapping(p, param) == true)
                return false;

            // check compatible parameters
            if (isCompatibleMapping(p, param, relaxDifferentModelConstraint) == false)
                return false;

            // check parameter being mapped to itself
            if (p.equals(param)) {
                String msg = "cannot be mapped to itself.";
                OneButton2Msg.showWarning(null, "Warning: mapping", msg, p.getName(),
                        "OK", new Dimension(1, 1));
                return false;
            }

            boolean shouldCheckloopMapping = checkLoopMapping;
            if (model instanceof DomeModelBuilder)
                shouldCheckloopMapping = shouldCheckloopMapping && (((DomeModelBuilder) model).isLoopChecking());
            if (shouldCheckloopMapping) {
                //check mapping circle in a model scope   -- Qing
                if (isLoopMapping(p, param) == true || isLoopMapping(param, p) == true)
                    return false;
            }
        }

        return true;
    }

    private boolean isValidMapping(Visualization p, Collection targets, boolean relaxDifferentModelConstraint) {
        if (p == null || targets == null) return false;

        for (Iterator vises = targets.iterator(); vises.hasNext();) {
            Visualization vis = (Visualization) vises.next();

            // check parameter being mapped to itself
            if (p.equals(vis)) {
                String msg = "cannot be mapped to itself.";
                OneButton2Msg.showWarning(null, "Warning: mapping", msg, p.getName(),
                        "OK", new Dimension(1, 1));
                return false;
            }
        }

        return true;
    }

    private boolean isValidMapping(Parameter p, Collection targets, boolean relaxDifferentModelConstraint)
    {
        if (p == null || targets == null )
            return false;

        for (Iterator iter = targets.iterator(); iter.hasNext();)
        {
            Parameter parameter = (Parameter) iter.next();

            // check if an imodel parameter is being mapped to an optimisation interface parameter
            if (!isValidAnalysisToolInterfaceMapping(p, parameter))
                return false;
            if (!isFirstAnalysisToolInterfaceMapping(p, parameter))
                return false;
        }
        return true;
    }


    /**
     * (1) Check whether a relation parameter can be mapped to another parameter.
     * Mapping is not possible when the relation parameter is independent
     * (i.e., an input parameter) and is already mapped to another object.
     * (2) Check whether a parameter is being mapped to two relation result
     * parameters. THis is not allowed.
     * (3) Check whether mapping creates a loop e.g. parameter mapping
     * in the input as well as output.
     *    Qing Oct 17th 03--- this only check inside a specific relation
     * (4) Cannot map input parameters in two different relations.
     * (5) Cannot map output parameters in two different relations.
     * (6) Cannot map input of a relation to input of a subscription.
     *
     * @param pa Dome parameter
     * @param pb Dome parameter
     * @return Mapping validity status (true = valid, false = invalid)
     */
    private boolean checkRelationMapping(Parameter pa, Parameter pb) {
        Parameter[] params = new Parameter[]{pa, pb};

        for (int i = 0; i < 2; i++) {
            ModelObjectScope scopeA = params[i].getScope();
            if (scopeA instanceof Relation) {
                CausalityStatus causalityA = ((Relation) scopeA).getCausality(params[i]);
                Collection mappings = getMappingsForParameter(params[i]);

                // check condition (1)
                if (causalityA.equals(CausalityStatus.INDEPENDENT) && mappings.size() > 0 &&
                        getObjectTopScope(pa) instanceof Model && getObjectTopScope(pb) instanceof Model) {
                    String msg = "can only be mapped to one item. Therefore it cannot be mapped to";
                    OneButton3Msg.showWarning(null, "Warning: mapping", msg, params[i].getName(),
                            params[1 - i].getName(), "OK", new Dimension(1, 1));
                    return false;
                }

                // check condition (2)
                if (causalityA.equals(CausalityStatus.RESULT))   // this parameter is a relation result
                {
                    mappings = getMappingsForParameter(params[1 - i]); // get mappings for other parameter
                    for (Iterator mapIter = mappings.iterator(); mapIter.hasNext();) {
                        ModelObject mObj = (ModelObject) mapIter.next();
                        ModelObjectScope mObjScope = mObj.getScope();
                        // fail it those mappings are also relation results
                        if (mObjScope instanceof Relation &&
                                ((Relation) mObjScope).getCausality(mObj).equals(CausalityStatus.RESULT)) {
                            String msg = "is already driven by another mapping.\n";
                            msg += "Therefore it cannot be mapped to";
                            OneButton3Msg.showWarning(null, "Warning: mapping", msg, params[1 - i].getName(),
                                    params[i].getName(), "OK", new Dimension(1, 1));
                            return false;
                        }
                    }
                }

                ModelObjectScope scopeB = params[1 - i].getScope();
                if (scopeB instanceof Relation) {
                    // check condition (4)
                    CausalityStatus causalityB = ((Relation) params[1 - i].getScope()).getCausality(params[1 - i]);
                    if (causalityA.equals(CausalityStatus.INDEPENDENT) &&
                            causalityB.equals(CausalityStatus.INDEPENDENT) &&
                            getObjectTopScope(pa) instanceof Model && getObjectTopScope(pb) instanceof Model) {
                        String msg = "The input of one relation cannot be mapped to the input of\n";
                        msg += "another relation. If you want to make the inputs the same, you\n";
                        msg += "should add a model parameter and map it to both input parameters.\n";
                        msg += "You can also use a twin relation to make the two inputs the same.";
                        OneButton1Msg.showWarning(null, "Warning: mapping", msg, "OK", new Dimension(1, 1));
                        return false;
                    }

                    // check condition (5)
                    if (causalityA.equals(CausalityStatus.RESULT) &&
                            causalityB.equals(CausalityStatus.RESULT) &&
                            getObjectTopScope(pa) instanceof Model && getObjectTopScope(pb) instanceof Model) {
                        String msg = "cannot be mapped. It is the output of a relation\n";
                        msg += "and therefore cannot be mapped to the output of another\n";
                        msg += "relation. This would create an invalid model.";
                        OneButton2Msg.showWarning(null, "Warning: mapping", msg, params[i].getName(),
                                "OK", new Dimension(1, 1));
                        return false;
                    }
                } else if (scopeB instanceof Subscription) {
                    // check condition (6)
                    CausalityStatus causalityB = ((Subscription) params[1 - i].getScope()).getCausality(params[1 - i]);
                    if (causalityA.equals(CausalityStatus.INDEPENDENT) &&
                            causalityB.equals(CausalityStatus.INDEPENDENT) &&
                            getObjectTopScope(pa) instanceof Model && getObjectTopScope(pb) instanceof Model) {
                        String msg = "The input of a relation cannot be mapped to the input in\n";
                        msg += "a subscription. If you want to make the inputs the same, you\n";
                        msg += "should add a model parameter and map it to both input parameters.\n";
                        msg += "You can also use a twin relation to make the two inputs the same.";
                        OneButton1Msg.showWarning(null, "Warning: mapping", msg, "OK", new Dimension(1, 1));
                        return false;
                    }
                }
            }
        }

        // check condition (3)
//Qing : this only check the loop within a specific relation scope

        ModelObjectScope scopea = pa.getScope();
        if (scopea instanceof Relation) {
            CausalityStatus causality = ((Relation) scopea).getCausality(pa);
            if (causality.equals(CausalityStatus.INDEPENDENT)) { //input
                //then cannot add and map to output
                Collection outputs = ((Relation) scopea).getCausalityManager().getOutputs();
                for (Iterator i = outputs.iterator(); i.hasNext();) {
                    Parameter p = (Parameter) i.next();
                    Collection outputmap = this.getMappingsForParameter(p);
                    if (outputmap.contains(pb)) {
                        //show warning dialog
                        String msg = "is already mapped to an output parameter in the relation.\n";
                        msg += "Therefore it cannot be mapped to an input parameter.";
                        OneButton3Msg.showWarning(null, "Warning: mapping", msg, pb.getName(),
                                pa.getName(), "OK", new Dimension(1, 1));
                        //delete pa from relation
                        //((Relation)scopea).deleteModelObject(pa);
                        return false;
                    }
                }
            } else if (causality.equals(CausalityStatus.INTERMEDIATE) ||
                    causality.equals(CausalityStatus.RESULT)) { //output
                //then cannot add and map to input
                Collection inputs = ((Relation) scopea).getCausalityManager().getIndependents();
                for (Iterator i = inputs.iterator(); i.hasNext();) {
                    Parameter p = (Parameter) i.next();
                    Collection inputmap = this.getMappingsForParameter(p);
                    if (inputmap.contains(pb)) {
                        //show warning dialog
                        String msg = "is already mapped to an input parameter in the relation.\n";
                        msg += "Therefore it cannot be mapped to an output parameter.";
                        OneButton3Msg.showWarning(null, "Warning: mapping", msg, pb.getName(),
                                pa.getName(), "OK", new Dimension(1, 1));
                        //delete pa from relation
                        //((Relation)scopea).deleteModelObject(pa);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Checks whether two parameters are linked to be a circle if adding the mapping
     * Only allows when there is at least one node are from iteration relation
     * @param pa
     * @param pb
     * @return
     */
    private boolean isLoopMapping(Parameter pa, Parameter pb) {
        ModelObjectScope scopea = pa.getScope();
        ModelObjectScope scopeb = pb.getScope();
        if (scopea.getModel() instanceof DomeModelBuilder &&
                scopeb.getModel() instanceof DomeModelBuilder &&
                scopea.getModel() == scopeb.getModel()
        )   //in same model
        {
            DomeModelBase dataModel = (DomeModelBase) scopea.getModel();
            DirectedGraph dg = (dataModel).createModelGraph();
            java.util.List nodes = dg.getNodes();
            if (nodes.indexOf(pa) == -1 || nodes.indexOf(pb) == -1)
                return false;

            // if (dg.areReachableNodes(pa, pb)&&!isLinkedViaLoopRelation(pa,pb))

            if (dg.areReachableNodes(pa, pb))
            // if(dg.getPath(pa,pb)!=null &&dg.getPath(pa,pb).size()>0)
            {
                Vector path = dg.getPath(pa, pb);
                Vector nodes_in_iteration_relation = new Vector();

                if (path != null) {

                    for (int i = 0; i < path.size(); i++) {
                        if (path.get(i) instanceof Parameter) {
                            ConcreteParameter param = (ConcreteParameter) path.get(i);
                            if (param.getScope() instanceof IterationRelation) {
                                nodes_in_iteration_relation.add(param);
                            }
                        }
                    }
                }

                if (nodes_in_iteration_relation.size() == 0) {
                    String msg = "the mapping will create a loop in model " + dataModel.getName() + ".\n";
                    msg += "Please use a loop relation to connect them instead.";
                    OneButton1Msg.showWarning(null, "Warning: mapping", msg, "OK", new Dimension(1, 1));
                    return true;
                } else {
                    String msg = "the mapping will create a loop in model " + dataModel.getName() + ".\n";
                    msg += "This is allowed since there is at least a node in the circle is in an iteration relation \nPlease carefully specify the termination condition to prevent an infinite loop.";
                    OneButton1Msg.showWarning(null, "Warning: mapping", msg, "OK", new Dimension(1, 1));
                    return false;
                }

            }
        }
        return false;
    }

    /**
     * If two subscription parameters with input causality are mapped, this results in
     * invalid mapping, mapping is valid otherwise.
     * @param pa Dome parameter
     * @param pb Dome parameter
     * @return Mapping validity status (true = valid, false = invalid)
     */
    public boolean checkSubscriptionMapping(Parameter pa, Parameter pb) {
        ModelObjectScope sa = pa.getScope();
        ModelObjectScope sb = pb.getScope();
        if (sa instanceof Subscription && sb instanceof Subscription) {
            CausalityStatus ca = ((Subscription) sa).getCausality(pa);
            CausalityStatus cb = ((Subscription) sb).getCausality(pb);
            if (ca.equals(CausalityStatus.INDEPENDENT) &&
                    cb.equals(CausalityStatus.INDEPENDENT)) {
                String msg = "The input of one subscription cannot be mapped to the input of\n";
                msg += "another subscription. If you want to make the inputs the same, you\n";
                msg += "should add an iModel parameter and map it to both input parameters.\n";
                msg += "You can also use a twin relation to make the two inputs the same.";
                OneButton1Msg.showWarning(null, "Warning: mapping", msg, "OK", new Dimension(1, 1));
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether a model parameter is being mapped to a relation parameter or interface parameter.
     * Mapping is invalid otherwise.
     * @param pa Dome parameter
     * @param pb Dome parameter
     * @return Mapping validity status (true = valid, false = invalid)
     */
    private boolean checkModelMapping(Parameter pa, Parameter pb) {
        Parameter[] params = new Parameter[]{pa, pb};


        for (int i = 0; i < 2; i++) {
            if ((params[i].getScope() instanceof Model)) {
                if (!(params[i].getScope() instanceof AnalysisTool)) // added this line to allow mapping between analysis tool and i model
                {
                    ModelObjectScope otherScope = params[1 - i].getScope();
                    if (!(otherScope instanceof Relation || otherScope instanceof ModelInterface || otherScope instanceof Subscription ||
                            otherScope instanceof InterfaceModelView || otherScope instanceof ToolInterface || otherScope instanceof AnalysisTool)) {
                        String msg = "is a model parameter and can only be mapped to relation/interface/subscription/analysis tool parameters.\n";
                        msg += "A twin or equal relation may be used to equate it with the parameter";
                        OneButton3Msg.showWarning(null, "Warning: mapping", msg, params[i].getName(),
                                params[1 - i].getName(), "OK", new Dimension(1, 1));
                        return false;
                    }
                }
            }
        }
        /*change to allow vise versa
        for (int i = 0; i < 2; i++) {
            if ((params[i].getScope() instanceof Relation)||(params[i].getScope() instanceof ModelInterface)||(params[i].getScope() instanceof Subscription)) {
                ModelObjectScope otherScope = params[1 - i].getScope();
                if (!(otherScope instanceof Model||otherScope instanceof Relation || otherScope instanceof ModelInterface||otherScope instanceof Subscription)) {
                    String msg = "is a model parameter and can only be mapped to relation/interface/subscription parameters.\n";
                    msg += "A twin or equal relation may be used to equate it with the parameter";
                    OneButton3Msg.showWarning(null, "Warning: mapping", msg, params[i].getName(),
                            params[1 - i].getName(), "OK", new Dimension(1, 1));
                    return false;
                }
            }
        }*/

        return true;
    }

    /**
     * Check whether a mapping is valid between two objects. Mapping is invalid
     * when the objects are already mapped to one another.
     * @param pa Dome parameter
     * @param pb Dome parameter
     * @return Mapping validity status (true = valid, false = invalid)
     */
    private boolean isDuplicateMapping(Parameter pa, Parameter pb) {
        Collection mappings = getMappingsForParameter(pa);

        Iterator mappedParams = mappings.iterator();
        while (mappedParams.hasNext()) {
            Parameter mappedParam = (Parameter) mappedParams.next();
            if (mappedParam.equals(pb)) {
                String msg = "is already mapped to";
                OneButton3Msg.showWarning(null, "Warning: mapping", msg, pa.getName(),
                        pb.getName(), "OK", new Dimension(1, 1));
                return true;
            }
        }

        return false;
    }

    /**
     * Check that the mapped data types and the models are compatible.
     * @param pa Dome parameter
     * @param pb Dome parameter
     * @return Mapping validity status (true = valid, false = invalid)
     */
    private boolean isCompatibleMapping(Parameter pa, Parameter pb, boolean relaxDifferentModelConstraint) {
        // check data types
        if (isCompatibleType(pa, pb) == false) {
            String msg = "cannot be mapped because they have incompatible data types.";
            String item = pa.getName() + " and " + pb.getName();
            OneButton2Msg.showWarning(null, "Warning: mapping", msg, item, "OK", new Dimension(1, 1));
            return false;
        }

//TODO  following code is specific to mapping between project interface and iModel parameter
//TODO do we want to keep it or instead use isCompatibleMapping with the	relaxDifferentModelConstraint flag?
        Model paModel = pa.getModel();
        Model pbModel = pb.getModel();
        if (paModel != pbModel) {
            if (paModel instanceof IntegrationProject && pbModel instanceof DomeModel) {
                if (((DomeModel) pbModel).getIntegrationProject().equals(paModel))
                    return true;
            } else if (pbModel instanceof IntegrationProject && paModel instanceof DomeModel) {
                if (((DomeModel) paModel).getIntegrationProject().equals(pbModel))
                    return true;
            }
        }
//TODO end

        // check models
        if (!relaxDifferentModelConstraint && pa.getModel() != pb.getModel()) {
            String msg = "are in different models. Mapping can only be performed within\n";
            msg += "the same model. Use subscribe between models.";
            String item = pa.getName() + " and " + pb.getName();
            OneButton2Msg.showWarning(null, "Warning: mapping", msg, item, "OK", new Dimension(1, 1));
            return false;
        }

        return true;
    }

    /**
     * Determine whether two dome parameters are compatible.
     * @return Compatibility status (true = compatible, false = incompatible)
     */
    private boolean isCompatibleType(Parameter p1, Parameter p2) {
        DataObject p1Type = p1.getCurrentDataObject();
        DataObject p2Type = p2.getCurrentDataObject();

        return p1Type.isCompatibleType(p2Type);
    }

    /**
     * Determine whether mapping operation is valid for analysis tools.
     * @return compatiblitiy status (true = compatible, false = incompatible)
     */
    private boolean isValidAnalysisToolInterfaceMapping (Parameter p1, Parameter p2)
    {
        if (p1.getScope() instanceof ToolInterface && p2.getScope() instanceof DomeModelBuilder)
        {
            OneButton1Msg.showWarning(null, "Warning: Map Operation", "You are not allowed to map an integration " +
                    "model parameter to an analysis tool interface parameter.", "OK", OneButton1Msg.DEFAULT_SIZE);
            return false;
        }
        return true;
    }

    private boolean isFirstAnalysisToolInterfaceMapping (Parameter p1, Parameter p2)
    {
        Collection items = getInterfaceConnections(p2);
        for (Iterator iter = items.iterator(); iter.hasNext();)
        {
            Object obj = iter.next();
            if (obj instanceof Parameter)
            {
                if (((Parameter)obj).getScope() instanceof OptimizationInterfaceBuild &&
                        ((Parameter)obj).getScope().getId().toString().equals(p1.getScope().getId().toString()))
                {
                    OneButton1Msg.showWarning(null, "Warning: Map Operation", "You are attempting to map to a parameter, " +
                            "which is already referenced in this model.", "OK", OneButton1Msg.DEFAULT_SIZE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Create an XML representation of the mapping manager
     * @return dom4j XML element
     */
    public Element toXmlElement(ModelObjectScope scope, String elementName) {
        Element xmlElement = DocumentHelper.createElement(elementName);
        return toXmlElement(scope, xmlElement);
    }


    /**
     * Create an XML representation of the mapping manager
     * @return dom4j XML element
     */
    public Element toXmlElement(ModelObjectScope scope, Element xmlElement) {
        HashMap mappings = getMappings(scope);
        HashMap reciprocalMappings = new HashMap();

        // traverse the list of mappings
        Collection keys = Collections.unmodifiableSet(mappings.keySet());
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            // add mapped parameter heading
            Object obj = iter.next();
            if (obj instanceof Parameter) {
                Parameter p = (Parameter) obj;
                //*Parameter p = (Parameter) iter.next();
                Element mappedParam = p.toXmlMappedRef();

                // add mapped parameters
                ParameterMapping map = (ParameterMapping) mappings.get(p);
                Collection mappingsList = map.getMappings();
                for (Iterator maps = mappingsList.iterator(); maps.hasNext();) {
                    // get mapped object and its reciprocal
                    Parameter target = (Parameter) maps.next();
                    DSet mappedParams = (DSet) reciprocalMappings.get(p);

                    // If we haven't recorded the mapping between p and target, then this is
                    // the first time the mapping has been seen. in that case, create the mapping
                    // xml and update the reciprocal mappings list.
                    // Otherwise, this is a reciprocal mapping and should be skipped.
                    if (mappedParams == null || !mappedParams.contains(target)) {
                        // create the xml mapping element
                        Element paramElement = target.toXmlRef();
                        mappedParam.add(paramElement);

                        // maintain the list of reciprocal mappings --
                        //    if the target maps back to the source, store this mapping so that
                        //    it can be avoided when it is encountered later.
                        ParameterMapping targetMap = (ParameterMapping) mappings.get(target);
                        if (targetMap != null && targetMap.getMappings().contains(p)) {
                            DSet recipParams = (DSet) reciprocalMappings.get(target);
                            if (recipParams == null) {
                                recipParams = new DSet();
                                reciprocalMappings.put(target, recipParams);
                            }
                            recipParams.add(p);
                        }
                    }
                }

                // finally, add the new mappings to the xml structure
                if (mappedParam.nodeCount() > 0)
                    xmlElement.add(mappedParam);
            } else if (obj instanceof Visualization) {
                Visualization p = (Visualization) obj;
                //*Parameter p = (Parameter) iter.next();
                Element mappedParam = p.toXmlMappedRef();

                // add mapped parameters
                VisualizationMapping map = (VisualizationMapping) mappings.get(p);
                Collection mappingsList = map.getMappings();
                for (Iterator maps = mappingsList.iterator(); maps.hasNext();) {
                    // get mapped object and its reciprocal
                    Visualization target = (Visualization) maps.next();
                    DSet mappedParams = (DSet) reciprocalMappings.get(p);

                    // If we haven't recorded the mapping between p and target, then this is
                    // the first time the mapping has been seen. in that case, create the mapping
                    // xml and update the reciprocal mappings list.
                    // Otherwise, this is a reciprocal mapping and should be skipped.
                    if (mappedParams == null || !mappedParams.contains(target)) {
                        // create the xml mapping element
                        Element paramElement = target.toXmlRef();
                        mappedParam.add(paramElement);

                        // maintain the list of reciprocal mappings --
                        //    if the target maps back to the source, store this mapping so that
                        //    it can be avoided when it is encountered later.
                        VisualizationMapping targetMap = (VisualizationMapping) mappings.get(target);
                        if (targetMap != null && targetMap.getMappings().contains(p)) {
                            DSet recipParams = (DSet) reciprocalMappings.get(target);
                            if (recipParams == null) {
                                recipParams = new DSet();
                                reciprocalMappings.put(target, recipParams);
                            }
                            recipParams.add(p);
                        }
                    }
                }

                // finally, add the new mappings to the xml structure
                if (mappedParam.nodeCount() > 0)
                    xmlElement.add(mappedParam);

            }

        }

        return (xmlElement.elements().isEmpty() ? null : xmlElement);
    }


    /**
     * Manages connections between model parameters and interface paramters. Connections
     * are hidden mappings that allow interface parameters to be identified from
     * the model parameters to which they are mapped.
     */
    protected class ConnectionManager {
        // model relation --------> interface relation
        //       one        to			many
        // model relation <-------- interface relation
        //       one        to          one
        protected HashMap modelToInterfaceConnections = new HashMap();
        // "connections" (hidden mappings) between model parameters
        // and interface parameters or
        // model relations and interface relations
        protected HashMap interfaceToModelConnections = new HashMap();
        // "connections" (hidden mappings) between
        // interface relations and model relations
        //relation connections are stored bidirectionally so that searching for and
        //removing individual connection is fast.

        /**
         * Get the interface parameter connections for the given model parameter
         * @param p Model parameter
         * @return List of connections
         */
        public Collection getInterfaceConnections(Parameter p) {
            if (getObjectTopScope(p) instanceof Model)
                return Collections.unmodifiableCollection(getConnections(p));
            else
                return Collections.EMPTY_LIST;
        }

        /**
         * Get the interface relation connections for the given model relation
         * @param rel Model relation
         * @return Collection of connections
         */
        public Collection getInterfaceConnections(Relation rel) {
            if (getObjectTopScope(rel) instanceof Model) {
                Collection con = getConnections(rel);
                if (con != null) {
                    return Collections.unmodifiableCollection(con);
                } else {
                    return Collections.EMPTY_LIST;
                }
            } else
                return Collections.EMPTY_LIST;
        }

        /**
         * Get the model relation connections for the given interface relation
         * @param rel Model relation
         * @return Model Relation or <code>null</code>
         */
        public Object getModelConnection(Relation rel) {
            if (getObjectTopScope(rel) instanceof InterfaceModelView) {
                Object obj = interfaceToModelConnections.get(rel);
                return obj;
            } else
                return null;
        }

        /**
         * Get the interface parameter connections for the given model parameter
         * @param p Model parameter
         * @return List of connections
         */
        private Collection getConnections(Parameter p) {
            ParameterMapping pMap = null;
            if ((getObjectTopScope(p) instanceof Model)) {
                if (p == null)
                    return Collections.EMPTY_LIST;
                pMap = (ParameterMapping) modelToInterfaceConnections.get(p);
            }
            return (pMap == null) ? Collections.EMPTY_LIST : pMap.getMappings();
        }

        private Collection getConnections(Visualization p) {
            VisualizationMapping pMap = null;
            if ((getObjectTopScope(p) instanceof Model)) {
                if (p == null)
                    return Collections.EMPTY_LIST;
                pMap = (VisualizationMapping) modelToInterfaceConnections.get(p);
            }
            return (pMap == null) ? Collections.EMPTY_LIST : pMap.getMappings();
        }

        /**
         * Get the interface relation connections for the given model relation
         * @param rel Model relation
         * @return Collection of connections
         */
        private Collection getConnections(Relation rel) {
            List connections = null;
            if ((getObjectTopScope(rel) instanceof Model)) {
                if (rel == null)
                    return Collections.EMPTY_LIST;
                connections = (List) modelToInterfaceConnections.get(rel);
                return connections;
            } else {
                return Collections.EMPTY_LIST;
            }
        }

        /**
         * If the input parametr is a model parameter, traverse its connections to interface parameters
         * and remove any mappings between those parameters and itself.
         * @param p Model paramter
         */
        public void removeConnectionsAndMappings(Parameter p) {
            if (getObjectTopScope(p) instanceof Model) {
                Collection connections = getConnections(p);
                for (Iterator ifaceParams = connections.iterator(); ifaceParams.hasNext();) {
                    Parameter ifaceParam = (Parameter) ifaceParams.next();
                    removeMapping(ifaceParam, p);
                }

                //if this is a model relation parameter, remove all relation connections
                if (p.getScope() instanceof Relation) {
                    Relation modelRel = (Relation) p.getScope();
                    removeConnections(modelRel);
                }
            } else if (getObjectTopScope(p) instanceof ModelInterface ||
                    getObjectTopScope(p) instanceof InterfaceModelView) {
                //if this is a interface relation parameter, remove the model relation connection
                if (p.getScope() instanceof Relation) {
                    Relation ifaceRel = (Relation) p.getScope();
                    removeConnection(ifaceRel);
                }
            }
            else if (getObjectTopScope(p) instanceof ToolInterface)
            {
                /**
                 * For an optimization interface, a model parameter cannot be mapped to more than
                 * one interface parameter.  To allow for the mappings, the connection must be
                 * removed whenever an interface parameter is deleted.  This is done below.
                 */
                Collection modelInterfaceCollections = modelToInterfaceConnections.values();
                for (Iterator iter = modelInterfaceCollections.iterator(); iter.hasNext(); )
                {
                    Object object = iter.next();
                    if (object instanceof ParameterMapping)
                    {
                        ParameterMapping pm = (ParameterMapping) object;
                        Collection mappings = ((ParameterMapping)object).getMappings();
                        List mappingsList = new ArrayList(mappings);
                        for (int i = mappingsList.size() - 1; i >= 0; i--)
                        {
                            Object mappedObject = mappingsList.get(i);
                            if (mappedObject instanceof Parameter)
                            {
                                Parameter mappedParameter = (Parameter) mappedObject;
                                if (mappedParameter.getId().toString().equals(p.getId().toString()))
                                {
                                    pm.removeMapping(p);
                                }
                            }
                        }
                    }
                }
            }
        }

        public void removeConnectionsAndMappings(Visualization p) {
            if (getObjectTopScope(p) instanceof Model) {
                Collection connections = getConnections(p);
                for (Iterator ifaceParams = connections.iterator(); ifaceParams.hasNext();) {
                    Visualization ifaceParam = (Visualization) ifaceParams.next();
                    removeMapping(ifaceParam, p);
                }

                //if this is a model relation parameter, remove all relation connections
                if (p.getScope() instanceof Relation) {
                    Relation modelRel = (Relation) p.getScope();
                    removeConnections(modelRel);
                }
            } else if (getObjectTopScope(p) instanceof ModelInterface ||
                    getObjectTopScope(p) instanceof InterfaceModelView) {
                //if this is a interface relation parameter, remove the model relation connection
                if (p.getScope() instanceof Relation) {
                    Relation ifaceRel = (Relation) p.getScope();
                    removeConnection(ifaceRel);
                }
            }
        }

        /**
         * If the input is a model relation, delete its connections to interface relations
         * @param modelrel Model relation
         */
        public void removeConnections(Relation modelrel) {
            if (getObjectTopScope(modelrel) instanceof Model) {
                Object list = modelToInterfaceConnections.remove(modelrel);
                if (list != null) {
                    Iterator i = ((List) list).iterator();
                    while (i.hasNext()) {
                        Object ifacerel = i.next();
                        interfaceToModelConnections.remove(ifacerel);
                    }
                }
            }
        }

        //remove one connection between interface and model relations
        public void removeConnection(Relation modelRel, Relation ifaceRel) {
            if (getObjectTopScope(modelRel) instanceof Model &&
                    (getObjectTopScope(ifaceRel) instanceof InterfaceModelView)) {
                Object modRel = interfaceToModelConnections.get(ifaceRel);
                if (modelRel.equals(modRel)) {
                    removeConnection(ifaceRel);
                }
            }
        }

        //remove one connection between interface and model relations
        public void removeConnection(Relation ifaceRel) {
            if (getObjectTopScope(ifaceRel) instanceof InterfaceModelView) {
                Object modelRel = interfaceToModelConnections.remove(ifaceRel);
                List con = (List) modelToInterfaceConnections.get(modelRel);
                if (con != null) {
                    con.remove(ifaceRel);
                }
            }
        }

        /**
         * Add a connection between a model parameter and an interface parameter.
         * @param modelParam Model parameter
         * @param ifaceParam Interface parameter
         * @return successful connection indicator
         */
        public boolean addConnection(Parameter modelParam, Parameter ifaceParam) {
            if (!(getObjectTopScope(modelParam) instanceof Model) ||
                    !((getObjectTopScope(ifaceParam) instanceof ModelInterface) ||
                    (getObjectTopScope(ifaceParam) instanceof InterfaceModelView) ||
                    (getObjectTopScope(ifaceParam) instanceof ToolInterface))) {
                return false;
            }

            ParameterMapping pMap = (ParameterMapping) modelToInterfaceConnections.get(modelParam);
            if (pMap == null) {
                pMap = new ParameterMapping(modelParam);
                modelToInterfaceConnections.put(modelParam, pMap);
            }
            return pMap.addMapping(ifaceParam);
        }

        public boolean addConnection(Visualization modelParam, Visualization ifaceParam) {
            if (!(getObjectTopScope(modelParam) instanceof Model) ||
                    !((getObjectTopScope(ifaceParam) instanceof ModelInterface) ||
                    (getObjectTopScope(ifaceParam) instanceof InterfaceModelView) ||
                    (getObjectTopScope(ifaceParam) instanceof ToolInterface))) {
                return false;
            }

            VisualizationMapping pMap = (VisualizationMapping) modelToInterfaceConnections.get(modelParam);
            if (pMap == null) {
                pMap = new VisualizationMapping(modelParam);
                modelToInterfaceConnections.put(modelParam, pMap);
            }
            return pMap.addMapping(ifaceParam);
        }

        /**
         * Add a connection between a model parameter and an interface parameter.
         * @param modelRel Model Relation
         * @param ifaceRel Interface Relation
         * @return successful connection indicator
         */
        public boolean addConnection(Relation modelRel, Relation ifaceRel) {
            if (!(getObjectTopScope(modelRel) instanceof Model) ||
                    !(getObjectTopScope(ifaceRel) instanceof InterfaceModelView)) {
                return false;
            }

            // model relation --------> interface relation
            //       one        to			many
            // model relation <-------- interface relation
            //       one        to          one
            Object obj = interfaceToModelConnections.get(ifaceRel);
            if (obj != null && !obj.equals(modelRel)) {
                return false;   //already connected to another relation
            }
            interfaceToModelConnections.put(ifaceRel, modelRel);
            List connections = (List) modelToInterfaceConnections.get(modelRel);
            if (connections == null) {
                connections = new ArrayList();
                connections.add(ifaceRel);
                modelToInterfaceConnections.put(modelRel, connections);
            } else {
                connections.add(ifaceRel);
            }
            return true;
        }
    }
}
