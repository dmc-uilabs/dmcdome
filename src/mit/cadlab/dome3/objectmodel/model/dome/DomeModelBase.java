// DomeModelBase.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.model.dome;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.CommonAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.AbstractModelInterfaceRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeServer;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.Parameters;
import mit.cadlab.dome3.objectmodel.util.solving.MultiItemNode;
import mit.cadlab.dome3.objectmodel.util.solving.NameIdNode;
import mit.cadlab.dome3.util.AggregatorMap;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;

import java.util.*;

public abstract class DomeModelBase extends AbstractDomeModel {
    protected HashMap relationGraphs = new HashMap(); // key is relation, value is graph


    public DomeModelBase(Id id) {
        super(id);
        initModel();
    }

    public DomeModelBase(Id id, DomeModel model) {
        super(id, model);
        initModel();
        // not implemented here
    }

    public DomeModelBase(String file, Element xmlElement) {
        super(file, xmlElement);
        loadXml(xmlElement);
    }

    //used by iProject to load iModel
    public DomeModelBase(IntegrationProject project, ProjectIntegrationModelInfo info,
                         String file, Element xmlElement) {
        super(file, xmlElement);
        this.iProject = project;
        info.setModel(this);
        loadXml(xmlElement);
    }

    public DomeModelBase(Element xmlElement) {
        super(xmlElement);
        loadXml(xmlElement);
    }

    protected void loadXml(Element xmlElement) {
        initModel(); // create default objects
        loadParameters(xmlElement);
        loadRelations(xmlElement);
        loadVisualizations(xmlElement);
        loadSubscriptions(xmlElement);
        loadContexts(xmlElement);
        loadMappings(xmlElement);
        //add for auxFiles
        loadAuxFiles(xmlElement);
        storeXmlAndUpdateDefaultInterface();
    }

    protected void loadSubscriptions(Element xmlElement) {
        Element element;
        Subscription sub;
        List params = xmlElement.selectNodes("/" + getXmlTag() + "/subscriptions/subscription");
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            element = (Element) iter.next();
            sub = (Subscription) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (sub != null) {
                modelObjects.add(sub);
                if (sub instanceof Filterable)
                    this.addListToCausalFilters(sub);
            }
        }
    }

    protected void loadParameters(Element xmlElement) {
        // read model parameters
        Parameter param;
        Element element;
        List params = xmlElement.selectNodes("/" + getXmlTag() + "/parameters/" + Parameter.XML_TAG);
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            element = (Element) iter.next();
            param = (Parameter) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (param != null) {
                modelObjects.add(param);
            }
        }
        // add list parameter references
        params = getFilter(DomeModel.PARAMETERS_FILTER).getItems();
        for (int i = 0; i < params.size(); i++) {
            param = (Parameter) params.get(i);
            Object obj = param.getDataObjectForType("List");
            if (obj != null) {
                ((DomeListData) obj).loadXmlData();
            }
        }
    }

    protected void loadRelations(Element xmlElement) {
        // read relations
        Relation rel;
        Element element;
        List relations = xmlElement.selectNodes("/" + getXmlTag() + "/relations/" + Relation.XML_TAG);
        for (Iterator iter = relations.iterator(); iter.hasNext();) {
            rel = null;
            element = (Element) iter.next();
            rel = (Relation) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (rel != null) {
                modelObjects.add(rel);
                if (rel instanceof Filterable)
                    this.addListToCausalFilters((Filterable) rel);
            }
        }
        // add list parameter references
        Parameter param;
        List rels = getFilter(DomeModel.RELATIONS_FILTER).getItems();
        for (int i = 0; i < rels.size(); i++) {
            rel = (Relation) rels.get(i);
            Collection relParams = rel.getModelObjects();
            for (Iterator iterator = relParams.iterator(); iterator.hasNext();) {
                param = (Parameter) iterator.next();
                Object obj = param.getDataObjectForType("List");
                if (obj != null) {
                    ((DomeListData) obj).loadXmlData();
                }
            }
        }
    }

    protected void loadVisualizations(Element xmlElement) {
        //add for visualization
        Visualization vis;
        Element element;
        List visualizations = xmlElement.selectNodes("/" + getXmlTag() + "/visualizations/" + Visualization.XML_TAG);
        for (Iterator iter = visualizations.iterator(); iter.hasNext();) {
            vis = null;
            element = (Element) iter.next();
            vis = (Visualization) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (vis != null) {
                modelObjects.add(vis);
            }
        }
    }

	protected void loadContexts(Element xmlElement)
	{
		// read contexts
		Context cxt;
		Element element;
		Element BuidContextElement = null;
		Element FileContextElement = null;
		Vector cxtObjects = new Vector();
		List contexts = xmlElement.selectNodes("/" + getXmlTag() + "/contexts/" + Context.XML_TAG);
		for (ListIterator iter = contexts.listIterator(contexts.size()); iter.hasPrevious();) {  //so that nested contexts are loaded properly
			element = (Element) iter.previous();
			String conId = element.attributeValue("id");
			if (conId.equals(BUILD_CONTEXT_ID.getIdString())) {  //skip build context
				BuidContextElement = element;
				continue;
			} else if (conId.equals(FILE_CONTEXT_ID.getIdString())) {  //skip file context
				FileContextElement = element;
				continue;
			}
			cxt = (Context) getModelObjectFactory().newInstance(element, new Object[]{this, element});
			if (cxt != null) {
				modelObjects.add(0, cxt);                      //so that nested contexts are loaded properly
				// build a collection of all context objects
				Collection cxtObjectList = cxt.getModelObjectReferences();
				for (Iterator contextIter = cxtObjectList.iterator(); contextIter.hasNext();) {
					ModelObject cxtObject = (ModelObject) contextIter.next();
					cxtObjects.addElement(cxtObject.getId());
				}
			}
		}

		if (BuidContextElement != null) {
			cxt = (Context) getModelObjectFactory().newInstance(BuidContextElement, new Object[]{this, BuidContextElement});
			if (cxt != null) {
				//populate build context to preserve order
				Context buildContext = (Context) modelObjectsById.get(BUILD_CONTEXT_ID);
				if (buildContext != null) {
					modelObjects.remove(buildContext);  //remove empty build context created initially
				}
				modelObjects.add(cxt); //add the build context with right order of modelobjects
			}
		} else {
//TODO remove all the code that belongs to this else statement.  This is in place so that the old models
//TODO which didn't save the build context can be loaded back
			Context buildContext = (Context) getModelObjectById(BUILD_CONTEXT_ID);
			// revisit the model objects and put them in the build context
			// only if they are not in any other context
			for (Iterator iter = modelObjects.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof ModelObject) {
					ModelObject modelObj = (ModelObject) obj;
					if (modelObj != null && modelObj != buildContext && !cxtObjects.contains(modelObj.getId())) {
						buildContext.addModelObjectReference(modelObj);
					}
				}
			}
		}

		if (FileContextElement != null) {
			cxt = (Context) getModelObjectFactory().newInstance(FileContextElement, new Object[]{this, FileContextElement});
			if (cxt != null) {
				//populate file context to preserve order
				Context FileContext = (Context) modelObjectsById.get(FILE_CONTEXT_ID);
				if (FileContext != null) {
					modelObjects.remove(FileContext);  //remove empty file context created initially
				}
				modelObjects.add(cxt); //add the file context with right order of modelobjects
			}
		}
	}


    protected void loadMappings(Element xmlElement) {
        // read mappings
        Element mappings = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/mappings/modelMappings");
        if (mappings != null)
            mappingManager.addMappings(mappings);
    }

    protected void loadAuxFiles(Element xmlElement) {
        // read auxFiles
        List auxFiles = xmlElement.selectNodes("/" + getXmlTag() + "/auxfiles/" + AuxFile.XML_TAG);
        for (Iterator iter = auxFiles.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            if (element != null) {//loadAuxFiles;
                AuxFiles.add(new CommonAuxFile(element));
            }
        }
    }

    protected void storeXmlAndUpdateDefaultInterface() {
        // store the xml as a string
        lastSavedXml = createXmlDocument().asXML();
        if (defaultIface != null)
            defaultIface.updateSavedStatus();
    }

    public DomeModelBase(String id, String name) {
        super(new Id(id));
        initModel();
        setName(name);
    }
	protected void initModel()
	{
		mappingManager = createConnectionMappingManager();
		modelObjects.add(createBuildContext());
		modelObjects.add(createFileContext());
		createModelObjectView();
		interfaces = createInterfacesManager();
		modelCausalityManager = createCausalityManager();
		createCausalView();
	}


	public ModelObject newModelObject(String modelObjectType)
	{
		ModelObject obj = super.newModelObject(modelObjectType);
		if (obj instanceof Parameter) {
			DataObject dObj = ((Parameter) obj).getCurrentDataObject();
			if (dObj instanceof DomeFile) {
				getFileContext().addModelObjectReference(obj);
			}
		}
		return obj;
	}

	//override so that file parameters can be added to "Files" context in the
	//setup panel on paste copy action
	public Collection newModelObjects(Collection mObjs, boolean deepCopy) {
		Collection copies = super.newModelObjects(mObjs, deepCopy);
		for (Iterator iterator = copies.iterator(); iterator.hasNext();) {
			ModelObject obj = (ModelObject)iterator.next();
			if (obj instanceof Parameter) {
				DataObject dObj = ((Parameter) obj).getCurrentDataObject();
				if (dObj instanceof DomeFile) {
					getFileContext().addModelObjectReference(obj);
				}
			}
		}
		return copies;
	}

	protected abstract ConnectionMappingManager createConnectionMappingManager();

	protected abstract ModelInterfaceManager createInterfacesManager();

	public List getValidModelObjectTypes()
	{
		List types = Registry.getDataObjectTypes();
		types.add("parameter");
		types.add("visualization");
		types.add(Registry.getRelationTypes());
		return types;
	}

//To show relation parameters in model causality view
    public void addListToCausalFilters(Filterable filterable) {
        independentFilter.addListToFilter(filterable);
        intermediateFilter.addListToFilter(filterable);
        resultFilter.addListToFilter(filterable);
        indeterminateFilter.addListToFilter(filterable);
    }

//To remove relation parameters from model causality view
    public void removeListFromCausalFilters(Filterable filterable) {
        independentFilter.removeListToFilter(filterable);
        intermediateFilter.removeListToFilter(filterable);
        resultFilter.removeListToFilter(filterable);
        indeterminateFilter.removeListToFilter(filterable);
    }

    public Document createXmlDocument() {
        Document doc = DocumentFactory.getInstance().createDocument();
        Element xmlElement = toXmlElement();
        doc.add(xmlElement);
        return doc;
    }

    public TypeInfo getTypeInfo() {
        return TYPE_INFO;
    }


    protected void registerRelationGraphs() {
        //System.out.println("type: "+getTypeName());
        modelGraph = new DirectedGraph();
        Iterator it = this.getModelObjects().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof ProceduralRelation) {
                ProceduralRelation rel = (ProceduralRelation) obj;
                DependencyInfo dInfo = rel.getDependencyInfo();
                if (dInfo != null) {
                    relationGraphs.put(rel, createMappedDirectedGraph(dInfo, rel.getModelObjects()));
                }
                // todo register listener on changes to dependency info
            }
        }
    }

	// todo: change build mode graph to match run mode graph algorithm
	// how to deal with disconnected nodes?
    /**
     * mapped directed graph extends outputs until it reaches relation inputs
     * @param dInfo
     * @return
     */
    protected DirectedGraph createMappedDirectedGraph(DependencyInfo dInfo, Collection relParams) {
        DirectedGraph dg = new DirectedGraph(dInfo);
        dg.addNodes(relParams);
        List outputs = dg.getOutputs();
        // two mapping scenarios possible
        // 1. output is mapped to relation input
        // 2. output is mapped to model parameter; model parameter is mapped to relation input
        ConnectionMappingManager mManager = this.getMappingManager();
        for (int i = 0; i < outputs.size(); i++) {
            Parameter p = (Parameter) outputs.get(i);
            Collection mappedParams = mManager.getMappingsForParameter(p);
            for (Iterator iterator = mappedParams.iterator(); iterator.hasNext();) {
                Parameter mappedParam = (Parameter) iterator.next();
                if (Parameters.isRelationInput(mappedParam) || Parameters.isSubscriptionInput(mappedParam)
                        || Parameters.isInterfaceInput(mappedParam)) {
                    dg.addNode(mappedParam);
                    dg.addArc(p, mappedParam);
                } else if (Parameters.isModelParameter(mappedParam)) {
                    dg.addNode(mappedParam);
                    dg.addArc(p, mappedParam);
                    Collection mappedParams2 = new ArrayList(mManager.getMappingsForParameter(mappedParam));
                    mappedParams2.remove(p);
                    for (Iterator it = mappedParams2.iterator(); it.hasNext();) {
                        Parameter parameter = (Parameter) it.next();
                        if (Parameters.isRelationInput(parameter) || Parameters.isSubscriptionInput(parameter)
                                || Parameters.isInterfaceInput(parameter)) {
                            dg.addNode(parameter);
                            dg.addArc(mappedParam, parameter);
                        } else
                            System.err.println("DomeModelBase.createMappedDirectedGraph (1): unknown mapping type with " +
                                    Names.getName(p) + ", " + Names.getName(parameter));
                    }
                } else {
                    System.err.println("DomeModelBase.createMappedDirectedGraph (2): unknown mapping type with " +
                            Names.getName(p) + ", " + Names.getName(mappedParam));
                }
            }
        }

        List inputs = dg.getInputs();
        // two mapping scenarios possible
        // 1. input is mapped to relation output
        // 2. input is mapped to model parameter; model parameter is mapped to relation input
        for (int i = 0; i < inputs.size(); i++) {
            Parameter p = (Parameter) inputs.get(i);
            Collection mappedParams = mManager.getMappingsForParameter(p);
            for (Iterator iterator = mappedParams.iterator(); iterator.hasNext();) {
                Parameter mappedParam = (Parameter) iterator.next();
                if (Parameters.isRelationOutput(mappedParam) || Parameters.isSubscriptionOutput(mappedParam) ||
                        Parameters.isInterfaceOutput(mappedParam)) {
                    dg.addNode(mappedParam);
                    dg.addArc(mappedParam, p);
                } else if (Parameters.isModelParameter(mappedParam)) {
                    dg.addNode(mappedParam);
                    dg.addArc(mappedParam, p);

                } else {
                    System.err.println("DomeModelBase.createMappedDirectedGraph (3): unknown mapping type with " +
                            Names.getName(p) + ", " + Names.getName(mappedParam));
                }
            }
        }
        modelGraph.addGraph(dg);
        return dg;
    }

    protected void addSubscriptionGraph() {
        Iterator it = this.getSubscriptions().iterator();
        while (it.hasNext()) {
            Subscription sub = (Subscription) it.next();
            modelGraph.addGraph(sub.getGraph());

            // mapping between subscription parameters
            ConnectionMappingManager mgr = getMappingManager();
            Collection subParams = sub.getModelObjects();

            for (Iterator it3 = subParams.iterator(); it3.hasNext();) {
	            Object obj = it3.next();
	            if (obj instanceof Parameter) {
		            Parameter subParam = (Parameter) obj;
		            Collection subMap = mgr.getMappingsForParameter(subParam);
		            if (!subMap.isEmpty()) {
		                for (Iterator it4 = subMap.iterator(); it4.hasNext();) {
		                    Object o = it4.next();
		                    if (o instanceof Parameter) { // might be model param, or subscription param
		                        Parameter mappedSubParam = (Parameter) o;
		                        if (modelGraph.getNodes().contains(subParam) &&
		                                modelGraph.getNodes().contains(mappedSubParam)) {
		                            if (Parameters.isSubscriptionInput(subParam)) {
		                                modelGraph.addArc(mappedSubParam, subParam);
		                            } else if (Parameters.isSubscriptionOutput(subParam)) {
		                                modelGraph.addArc(subParam, mappedSubParam);
		                            }
		                        }
		                    }
		                }
		            }
	            }
            }
        }
    }

    public DirectedGraph createModelGraph() { // todo: remove usages not in build mode or change run graph calculation
        if (modelGraph == null || (DomeClientApplication.getMode() == Modes.BUILD_MODE && GraphHasChanged))
            calculateModelGraph();   //if build mode, update
        //for run mode, won't change
        return modelGraph;
    }

    public void calculateModelGraph() {
        registerRelationGraphs();
        // add model parameters
        modelGraph.addNodes(new ArrayList(getFilter(DomeModel.PARAMETERS_FILTER).getItems()));
        if (this.isIntegrationModel())
            addSubscriptionGraph();

        setGraphHasChanged(false);
    }

	public List getAffectedParams(Parameter changedParam)
	{
		createModelGraph();
		return modelGraph.getAffectedNodes(changedParam);
	}

	public List getAffectedParams(List changedParameters)
	{
		createModelGraph();
		return modelGraph.getAffectedNodes(changedParameters);
	}

	public void markAffectedParameters(Parameter p)
	{
		List affectedParams = getAffectedParams(p);
		for (Iterator iterator = affectedParams.iterator(); iterator.hasNext();) {
			((Parameter) iterator.next()).setValueStatus(Parameter.VALUE_STATUS_INCONSISTENT);
		}
	}

	public void markAffectedParameters(List parameters)
	{
		List affectedParams = getAffectedParams(parameters);
		for (Iterator iterator = affectedParams.iterator(); iterator.hasNext();) {
			((Parameter) iterator.next()).setValueStatus(Parameter.VALUE_STATUS_INCONSISTENT);
		}
	}

	/**
	 * Returns graph for each interface and information about which interface parameters
	 * are related to which other interface parameters. Placed in DomeModelBase so that
	 * methods are available to both Dome and Plugin models.
	 * @param interfaceIds
	 * @return Vector of <vector multiItemNodes, hashtable of <interfaceid,graphXML>>
	 * multiItemNodes have modelParamId as Id and ifaceParamIds<ifaceId.paramId> as items
	 */
	public Vector getAggregatedInterfaceInfo(List interfaceIds)
	{
		AggregatorMap mpToIp = new AggregatorMap();
		Hashtable interfaceInfo = new Hashtable(); // ifaceId, ifaceGraphXml

		for (Iterator iterator = interfaces.getInterfaces().iterator(); iterator.hasNext();) {
			ModelInterfaceBase iface = (ModelInterfaceBase) iterator.next();
			String ifaceId = iface.getId().getIdString();
			if (iface instanceof AbstractModelInterfaceRuntime) {
				ifaceId = ((AbstractModelInterfaceRuntime) iface).getRuntimeId().getInterfaceStaticId();
			}
			if (!interfaceIds.contains(ifaceId))
				continue;
			interfaceInfo.put(ifaceId, iface.getInterfaceGraph().toXmlElement(ifaceId).asXML());
			mpToIp.putAll(iface.getModelParamToInterfaceParamMap());
		}

		// remove model parameters mapped to only one interface parameter
		Vector multiItemNodes = new Vector();
		Collection modelParams = mpToIp.keySet();
		for (Iterator iterator = modelParams.iterator(); iterator.hasNext();) {
			Parameter modelParam = (Parameter) iterator.next();
			List ifaceParams = (List) mpToIp.get(modelParam);
			if (ifaceParams == null || ifaceParams.isEmpty() || ifaceParams.size() == 1)
				continue; // skip
			List ifaceParamIds = new ArrayList();
			for (int i = 0; i < ifaceParamIds.size(); i++) {
				ifaceParamIds.add(getInterfaceParamId((Parameter) ifaceParamIds.get(i)));
			}
			multiItemNodes.add(new MultiItemNode(getModelParamId(modelParam), ifaceParamIds).toXmlElement().asXML());
		}

		return Vectors.create(multiItemNodes, interfaceInfo);
	}

	/**
	 * Converts a graph in the format created by getAggregatedInterfaceInfo
	 * to a graph in actual model parameters. Each MultiItemNode is mapped to the
	 * model parameter and each interface parameter is mapped to its corresponding
	 * model parameter.
	 * @param idGraph composed of ifaceId.paramId and MultiItemNodes(modelParamId)
	 * @return
	 */
	protected DirectedGraph convertGraphFromIdsToParameters(DirectedGraph idGraph)
	{
		List nodes = idGraph.getNodes();
		HashMap idToParamMap = new HashMap();
		Object p;
		for (int i = 0; i < nodes.size(); i++) {
			Object o = nodes.get(i);
			if (o instanceof NameIdNode) { // project node, leave it alone
				p = o;
			}
			else if (o instanceof MultiItemNode) { // use model param
				p = getModelParameterFromModelParamId(((MultiItemNode) o).getId());
			}
			else if (o instanceof String) {
				p = getModelParameterFromInterfaceParamId((String) o);
			}
			else {
				System.err.println("DomeModelBase.convertGraphFromIdsToParameters: unknown node type - " + o);
				continue;
			}
			if (p != null)
				idToParamMap.put(o, p);
		}
		return new DirectedGraph(idGraph, idToParamMap);
	}

	/**
	 * Returns parameter from modelParamId generated by getModelParamId
	 * @param modelParamId
	 * @return
	 */
	private Parameter getModelParameterFromModelParamId(String modelParamId)
	{
		int separatorIndex = modelParamId.indexOf(".");
		if (separatorIndex == -1) // not in subscription
			return (Parameter) this.getModelObjectById(new Id(modelParamId));
		else {
			Subscription sub = (Subscription) this.getModelObjectById(new Id(modelParamId.substring(0, separatorIndex)));
			if (sub == null) // can't find it
				return null;
			return (Parameter) sub.getModelObjectById(new Id(modelParamId.substring(++separatorIndex)));
		}
	}

	/**
	 * Returns parameter from modelParamId generated by getModelParamId
	 * @param ifaceParamId interfaceId.paramId
	 * @return
	 */
	private Parameter getModelParameterFromInterfaceParamId(String ifaceParamId)
	{
		int separatorIndex = ifaceParamId.indexOf(".");
		if (separatorIndex == -1) { // invalid parameter id
			System.err.println("DomeModelBase.getModelParameterFromInterfaceParamId - invalid ifaceParamId: " + ifaceParamId);
			return null;
		}
		String ifaceId = ifaceParamId.substring(0, separatorIndex);
		String paramId = ifaceParamId.substring(++separatorIndex);
		ModelInterfaceBase mi = (ModelInterfaceBase) getModelInterfacesManager().getById(ifaceId);
		if (mi == null) {
			System.err.println("DomeModelBase.getModelParameterFromInterfaceParamId - invalid ifaceId: " + ifaceId);
			return null;
		}
		Parameter ifaceParam = (Parameter) mi.getModelObjectById(new Id(paramId));
		return ModelInterfaceBase.getModelParameterForInterfaceParameter(ifaceParam, getMappingManager());
	}

	public static String getModelParamId(Parameter p)
	{
		String paramId = p.getId().getIdString();
		if (p.getScope() instanceof Subscription) {
			return p.getScope().getId().getIdString() + "." + paramId;
		}
		else if (p.getScope() instanceof SubscriptionInterface) {
			return ((SubscriptionInterface) p.getScope()).getSubscription().getId().getIdString() + "." + paramId;
		}
		else {
			return paramId;
		}
	}

	public static String getInterfaceParamId(Parameter p)
	{
		String paramId = p.getId().getIdString();
		if (p.getScope() instanceof AbstractModelInterfaceRuntime) {
			return ((AbstractModelInterfaceRuntime) p.getScope()).getRuntimeId().getInterfaceStaticId() + "." + paramId;
		}
		else
			return p.getScope().getId().getIdString() + "." + paramId;
	}

	public ModelInterfaceRuntimeServer loadRuntimeInterface(CompoundId interfaceId, String xmlContent,
	                                                        String xmlMappings)
	{
		ModelInterfaceRuntimeServer iface;
		iface = ((ModelInterfaceManagerRuntime) interfaces).loadInterface(interfaceId, xmlContent, xmlMappings);
		return iface;
	}

	
	//todo: get rid of these methods; should be calculated by a system causality graph manager
	
	protected abstract DirectedGraph getSystemCausalityGraph();

	/**
	 * Returns hashtable of each interface param to system causality status
	 * @param paramMap interface param to model param
	 * @return
	 */
	public Hashtable getSystemCausality(Map paramMap)
	{
		DirectedGraph systemCausalityGraph = getSystemCausalityGraph();
		List disconnectedNodes = systemCausalityGraph.getDisconnectedNodes();
		List independents = systemCausalityGraph.getInputs();
		List intermediates = systemCausalityGraph.getIntermediates();
		List results = systemCausalityGraph.getResults();
		Hashtable paramCausality = new Hashtable();

		for (Iterator iterator = paramMap.keySet().iterator(); iterator.hasNext();) {
			Parameter ifaceParam = (Parameter) iterator.next();
			if (independents.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.INDEPENDENT);
			else if (intermediates.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.INTERMEDIATE);
			else if (results.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.RESULT);
			else if (disconnectedNodes.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.INDETERMINATE);
			else
				throw new RuntimeException("DomeModelBuilder:getSystemCausality: invalid interface parameter: " +
				                           ifaceParam.getName() + " in " + getName());
		}
		return paramCausality;
	}

}