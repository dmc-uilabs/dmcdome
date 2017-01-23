// AbstractProceduralRelation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.relation.procedural;

import edu.iupui.rg.ucum.units.Unit;
import mit.cadlab.dome3.objectmodel.*;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.exceptions.RelationExecutionException;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilterFunction;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractPropertyChangeFilter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.AbstractRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.RelationExecutor;
import mit.cadlab.dome3.objectmodel.modelobject.relation.RelationExecutorImpl;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityFilter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeEvent;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.ImmutableDependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.InvalidObjectsException;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.ShiftSupport;
import mit.cadlab.dome3.util.UnitsException;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractProceduralRelation extends AbstractRelation implements ProceduralRelation {

    protected String body = "";
    protected DependencyInfo dependencyInfo = new ImmutableDependencyInfo(new DependencyInfo());
    // internal causality support
    protected transient CausalityStatus newObjectCausality = CausalityStatus.INDEPENDENT; // default
    protected Filter inputFilter, outputFilter;
    protected Filter independentFilter, intermediateFilter, resultFilter;
    protected HashMap modelObjectMap = new HashMap();
    protected RelationExecutor re;


    public AbstractProceduralRelation(ModelObjectScope scope, Id id) {
        super(scope, id);
        createInternalCausalityFilters();
        createModelScopeCausalityFilters();
    }

    public AbstractProceduralRelation(ModelObjectScope scope, Id id, Relation relation) {
        super(scope, id, relation);
        createInternalCausalityFilters();
        createModelScopeCausalityFilters();
        modelObjectMap = copyModelObjects(relation);
        DependencyInfo dependencyInfo = ((AbstractProceduralRelation) relation).getDependencyInfo();
        dependencyInfo = new DependencyInfo(dependencyInfo, modelObjectMap);
        setDependencyInfo(dependencyInfo);
    }

    public AbstractProceduralRelation(ModelObjectScope scope, Element xmlElement) {
        super(scope, xmlElement);
        createInternalCausalityFilters();
        createModelScopeCausalityFilters();
        parseRelationInfoElement(xmlElement);
    }

    /**
     * added for memory leakage problem
     * @param notifier
     */
    public void delete(DeletionListener notifier) {
        super.delete(notifier);
        re = null;
    }

    protected TypeInfo getTypeInfo() {
        return ProceduralRelation.TYPE_INFO;
    }

    public HashMap getModelObjectMap() {
        return modelObjectMap;
    }

    // ProceduralRelation interface
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        String oldBody = this.body;
        if (body == null) body = "";
        if (oldBody.equals(body))
            return;
        this.body = body;
        firePropertyChange(BODY, oldBody, this.body);
    }

    public DependencyInfo getDependencyInfo() {
        return dependencyInfo;
    }

    public void setDependencyInfo(DependencyInfo dInfo) {
        if (dInfo == null)
            dInfo = new DependencyInfo();
        if (validateDependencyInfo(dInfo)) {
            DependencyInfo oldDInfo = dependencyInfo;
            dependencyInfo = new ImmutableDependencyInfo(dInfo);
            firePropertyChange(DEPENDENCY_INFO, oldDInfo, dependencyInfo);
        }
    }

    protected boolean validateDependencyInfo(DependencyInfo dInfo) {
        try {
            dInfo.validate();
            List nodes = dInfo.getNodes();
//for(int i = 0; i < nodes.size(); i++) {
//System.out.println("node " + i + " = " + nodes.get(i));
//}
            Collection badNodes = DSet.removeSet(nodes, modelObjects);

/*if(badNodes.size() > 0)	{
System.out.println("relation scope = " + scope.getName());
for(int i = 0; i < badNodes.size(); i++) {
System.out.println("badNode " + i + " = " + ((List)badNodes).get(i));
}
}*/
            if (badNodes.isEmpty())
                return true;
            else if (badNodes.size() == 1 && badNodes.iterator().next() == null) { //TODO: fix this kludge later
                return true;
            } else
                throw new InvalidObjectsException(badNodes);
        } catch (Exception ex) {
            handleDependencyInfoException(ex);
            return false;
        }
    }

    protected abstract void handleDependencyInfoException(Exception ex);

    protected HashMap copyModelObjects(Relation rel) {
        CausalityManager cmgr = rel.getCausalityManager();
        Collection origModelObjects = rel.getModelObjects();
        ArrayList mObjs = new ArrayList(origModelObjects); // mutable list
        HashMap idMap = super.copyModelObjects(mObjs);
        for (Iterator i = mObjs.iterator(); i.hasNext();) {
            ModelObject mObj = (ModelObject) i.next();
            if (mObj instanceof Parameter) {
                CausalityStatus cause = cmgr.getCausality(mObj);
                ((ProceduralRelationInternalCausalityManager) internalCausalityManager).changeCausality(
                        idMap.get(mObj.getId()), cause);
            }
        }
        this.setBody(((ProceduralRelation) rel).getBody());
        return idMap;
    }


    // Relation interface
    protected CausalityManager createInternalCausalityManager() {
        return new ProceduralRelationInternalCausalityManager();
    }

    protected CausalityManager createModelCausalityManager() {
        return new ProceduralRelationInternalCausalityManager();
    }

    protected class ProceduralRelationInternalCausalityManager
            extends AbstractModelObjectScope.AbstractInternalCausalityManager {
        public ProceduralRelationInternalCausalityManager() {
            addPropertyChangeListener(ProceduralRelation.DEPENDENCY_INFO, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    processDependencyInfoChange();
                }
            });
        }

        protected CausalityStatus getInitialCausality(Object obj) {
            return getNewObjectCausality();
        }

        protected void processDependencyInfoChange() {
            Iterator parameters = AbstractProceduralRelation.this.getModelObjects().iterator();
            while (parameters.hasNext()) {
                Object param = parameters.next();
                CausalityStatus oldstat = internalCausalityManager.getCausality(param);
                CausalityStatus newstat = dependencyInfo.getCausality(param);
                //in relation gui, user wants to move relation param from output
                //filter back to input filter - bug fix
                if (CausalityStatus.RESULT.equals(oldstat) && newstat == null) {
                    newstat = CausalityStatus.INDEPENDENT;
                }
                changeCausality(param, newstat);
            }
        }
    }

    protected CausalityStatus getNewObjectCausality() {
        return newObjectCausality;
    }

    protected void setNewObjectCausality(CausalityStatus causality) {
        newObjectCausality = causality;
    }

    protected void resetNewObjectCausality() {
        newObjectCausality = CausalityStatus.INDEPENDENT; // default
    }

    // Filterable interface
    public Collection addItemsToFilterListener(DListListener l) {
        modelObjects.addDListListener(l);
        return Collections.unmodifiableList(modelObjects);
    }

    public Collection removeItemsToFilterListener(DListListener l) {
        modelObjects.removeDListListener(l);
        return Collections.unmodifiableList(modelObjects);
    }

    // Filters
    protected void createInternalCausalityFilters() {
        inputFilter = new InputFilter();
        outputFilter = new OutputFilter();
    }

    protected void createModelScopeCausalityFilters() {
        //independentFilter = new InputFilter();
        //intermediateFilter = new InputFilter();
        //resultFilter = new InputFilter();
        independentFilter = new ModelScopeCausalityFilter(CausalityStatus.INDEPENDENT);
        intermediateFilter = new ModelScopeCausalityFilter(CausalityStatus.INTERMEDIATE);
        resultFilter = new ModelScopeCausalityFilter(CausalityStatus.RESULT);
    }


    protected class InputFilter extends RelationInternalCausalityFilter implements ViewSupport {
        public InputFilter() {
            super("INPUTS_FILTER", "Inputs");
        }

        protected boolean isCausalityOfInterest(CausalityStatus cs) {
            return cs != null && cs.equals(CausalityStatus.INDEPENDENT);
        }
    }


    protected class OutputFilter extends RelationInternalCausalityFilter implements ViewSupport {
        public OutputFilter() {
            super("OUTPUTS_FILTER", "Outputs");
        }

        protected boolean isCausalityOfInterest(CausalityStatus cs) {
            return cs != null && (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT));
        }
    }

    protected abstract class RelationInternalCausalityFilter extends AbstractCausalityFilter
            implements ShiftSupport {

        public RelationInternalCausalityFilter(String idString, String name) {
            super(AbstractProceduralRelation.this.getModel(), new Id(idString), name);
            internalCausalityManager.addCausalityChangeListener(new CausalityFilterCausalityChangeListener());
        }

        // ShiftSupport interface
        public void shiftLeft(int[] indices) {
            filteredItems.shiftLeft(indices);
        }

        public void shiftRight(int[] indices) {
            filteredItems.shiftRight(indices);
        }

    }


    protected class ModelScopeCausalityFilter extends AbstractPropertyChangeFilter implements ViewSupport {
        private CausalityStatus causality;

        public ModelScopeCausalityFilter(CausalityStatus cs) {
            super(AbstractProceduralRelation.this.getModel(),
                    AbstractProceduralRelation.this.getNextId(), true);
            causality = cs;
            setName(cs.toString());
            addListToFilter(AbstractProceduralRelation.this);
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
            if (getModel() != null) {
                return !filteredItems.contains(obj) && getModel().isItemOfCausality(obj, causality);
            } else {
                ModelObjectScope relScope = getScope();
                if (relScope instanceof ModelInterfaceRuntimeClient) {
                    return !filteredItems.contains(obj) && ((ModelInterfaceRuntimeClient) relScope).isItemOfSystemCausality(obj, causality);
                } else
                    return false;
            }
        }

        protected class CausalityFilterFunction extends AbstractFilterFunction
                implements AbstractPropertyChangeFilter.PropertyChangeFilterFunction {
            CausalityChangeListener ccListener;

            public CausalityFilterFunction(String name) {
                super(name);
                ccListener = new CausalityFilterCausalityChangeListener();
            }

            public boolean keepInFilter(Object obj) {
                return ModelScopeCausalityFilter.this.keepInFilter(obj);
            }

            public void addListenerTo(Object obj) {
                if (getModel() != null) {
                    getModel().addCausalityChangeListener(obj, ccListener);
                }
            }

            public void removeListenerFrom(Object obj) {
                if (getModel() != null) {
                    getModel().removeCausalityChangeListener(obj, ccListener);
                }
            }

            protected class CausalityFilterCausalityChangeListener implements CausalityChangeListener {
                public void causalityChanged(CausalityChangeEvent event) {
                    processCausalityChange(event);
                }
            }
        }

    }

    protected void parseRelationInfoElement(Element xmlElement) {
        if (xmlElement == null)
            throw new IllegalArgumentException(getTypeName() + " - no xml relation info");
        XMLUtils.makeRootElement(xmlElement);

        // read parameters
        Parameter param;
        Element element;
        List params = xmlElement.selectNodes("/" + getXmlTag() + "/parameters/parameter");
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            param = null;
            element = (Element) iter.next();
            ModelObjectScope scope = getScope();
            ModelObjectFactory factory = scope.getModelObjectFactory();
            param = (Parameter) factory.newInstance(element, new Object[]{this, element});
            if (param != null) {
                modelObjects.add(param);
            }
        }

        // read body
        Element bodyElement = (Element) xmlElement.selectSingleNode("body");
        String body = bodyElement.getText();
        this.body = body.trim();
        firePropertyChange(BODY, body, this.body);

        // read dependency
        Element dependencies = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/dependencies");
        DependencyInfo dependInfo = new DependencyInfo(this, dependencies);
        setDependencyInfo(dependInfo);

        //inputsFromXml((Element)xmlElement.selectSingleNode("/relation/inputs"));
        //outputsFromXml((Element)xmlElement.selectSingleNode("/relation/outputs"));
        // body from XML
        // dependency info from XML
    }

    protected String contentToString() {
        StringBuffer sb = new StringBuffer();
        sb.append("inputs: " + Names.getNameIds(modelObjects));
        //sb.append("\noutputs: "+Names.getNameIds());
        sb.append("\nbody:\n" + body);
        sb.append("\n" + dependencyInfo.toString());
        return sb.toString();
    }

    protected void addXmlContent(Element xmlElement) {
        XMLUtils.addCollection(xmlElement, "parameters", modelObjects);
        xmlElement.addElement("body").addCDATA(body);
        xmlElement.add(dependencyInfo.toXmlElement());
    }

    protected void createXmlRef(Element xmlElement) {
        xmlElement.addAttribute("name", getName());
        xmlElement.addAttribute("idRef", getId().getIdString());

        if (scope instanceof Relation)
            xmlElement.addAttribute("idRelationRef", scope.getId().toString());
        else if (scope instanceof Model)
            xmlElement.addAttribute("idModelRef", scope.getId().toString());
    }

    public void execute() throws RelationExecutionException {
		execute(null);
    }

    public boolean cmpValue(DataObject oldObj, DataObject newObj) {
        if (oldObj.getClass() == RealData.class) {
            double value = ((RealData) newObj).getValue();
            Unit u = newObj.getUnit();
            return (((RealData) oldObj).getValue() == value) && (oldObj.getUnit().equivalent(u));
        }
        if (oldObj.getClass() == IntegerData.class) {
            int value = ((IntegerData) newObj).getValue();
            Unit u = newObj.getUnit();
            return (((IntegerData) oldObj).getValue() == value) && (oldObj.getUnit().equivalent(u));

        }
        if (oldObj.getClass() == BooleanData.class) {
            boolean value = ((BooleanData) newObj).getValue();

            return ((BooleanData) oldObj).getValue() == value;
        }
        if (oldObj.getClass() == EnumerationData.class) {
            EnumerationData value = (EnumerationData) newObj.getDataObject();

            return oldObj.equals(value);
        }
        if (oldObj.getClass() == DomeMatrixData.class) {
            DomeMatrixData value = (DomeMatrixData) newObj.getDataObject();

            return oldObj.equals(value);
        }
        if (oldObj.getClass() == DomePreferenceData.class) {
		        DomePreferenceData value = (DomePreferenceData) newObj.getDataObject();

		        return oldObj.equals(value);
        }
        if (oldObj.getClass() == StringData.class) {
            StringData value = (StringData) newObj.getDataObject();

            return oldObj.equals(value);
        }
        if (oldObj.getClass() == DomeVectorData.class) {
            DomeVectorData value = (DomeVectorData) newObj.getDataObject();

            return oldObj.equals(value);
        }
        return false;
    }

    //qing added Jan25th
    public void execute(List changedParams) throws RelationExecutionException {
	    if (getDependencyInfo() == null || getDependencyInfo().isEmpty()) {
		    throw new RelationExecutionException(getName(), "Causality must be defined before executing this relation", null);
	    }

        if (re == null) {
            re = new RelationExecutorImpl(getName());
            //so that we do not waste time loading the python classes
            //every time this method is called
        }
        HashMap ht = new HashMap();
        Collection params = getModelObjects();

        // load variables into python interpreter
        for (Iterator i = params.iterator(); i.hasNext();) {
            Object obj = i.next();
            if (obj instanceof Parameter) {
                Parameter param = (Parameter) obj;
                ht.put(param.getName(), param.getCurrentDataObject());
            }
        }
        re.loadVariables(ht);

        // execute the relation code
        String code = getBody();
        String workingDir = null;
        Object runtimeThingy = this.getScope();
        if (runtimeThingy instanceof DomeModelRuntime) {
        	File wDir = ((DomeModelRuntime)runtimeThingy).getWorkingDirectory();
        	if (wDir != null) 
        		workingDir = wDir.getAbsolutePath();
        	else
        		workingDir = ((DomeModelRuntime)runtimeThingy).getModelDirectory().getAbsolutePath();
        }
        else 
        	workingDir = new File(((DomeModelBuilder)runtimeThingy).getFileName()).getParent();
        String runCode = "modelWorkingDirectory = '" + workingDir.replace('\\', '/') + "'\n" + code;
        re.setCode(runCode);
	    re.run(); // throws RelationExecutionException if anything goes wrong

	    if (getModel() instanceof DomeModelRuntime && ((DomeModelRuntime)getModel()).isWaitingToDie())
	        return; // don't set outputs if waiting to die

        // get the results from the interpreter
        //Qing change here Jan25th: the changed parameter should be only those parameters that are affected
		List affectedParams = getAffectedParams(changedParams);
	    Parameter originalParam;
	    for (int i = 0; i < affectedParams.size(); i++) {
		    originalParam = (Parameter) affectedParams.get(i);
		    DataObject originalParamData = originalParam.getCurrentDataObject();
		    try {
		        DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
			    originalParamData.setValues(newParamData); // triggers change messages
		    }
		    catch (UnitsException e) {
			    throw new RelationExecutionException(getName(),
			                                         "Exiting relation:\n" + e.getMessage(originalParam.getName()),
			                                         e);
		    } catch (Exception ex) {
			    throw new RelationExecutionException(getName(),
			                                         "Exiting relation: error setting value of " + originalParam.getName() +
			                                         "\n" + ex.getMessage(),
			                                         ex);
		    }
	    }
        re.clearVariables();
    }

    /**
     * add for memory leak debug
     * clearVariables() clears the Python class's reference to Data Object.
     */
    public void clearRelationExecutor() {
        if (re != null) {
            re.clearVariables();
        }
    }

	/**
	 * If the list of changedParams is null or empty, return list of all intermediates and results
	 * else return list of parameters that are affected by the changed parameters.
	 * @param changedParams
	 * @return
	 */
    public List getAffectedParams(List changedParams) {
		DSet affectedParams = new DSet();
		DirectedGraph relGraph = new DirectedGraph(this.getDependencyInfo());
		if (changedParams == null || changedParams.isEmpty()) {
			affectedParams.addAll(relGraph.getOutputs());
		}
		else {
			for (Iterator i = changedParams.iterator(); i.hasNext();) {
				affectedParams.addAll(relGraph.getAffectedNodes(i.next()));
			}
		}
        return affectedParams;
    }

	//to add file parameters in a relation to the file contexts in the model
	protected void chekckForFileType(ModelObject obj)
	{
		if (this.scope instanceof DomeModelBuilder) {
			DataObject dobj = ((Parameter) obj).getCurrentDataObject();
			if (dobj instanceof DomeFile) {
				Context filecon = ((DomeModelBuilder) scope).getFileContext();
				filecon.addModelObjectReference(obj);
			}
		}
	}
}
