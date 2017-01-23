package mit.cadlab.dome3.objectmodel.modelobject.relation.iteration;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.exceptions.RelationExecutionException;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.relation.RelationExecutorImpl;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.swing.PythonEditor;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.util.UnitsException;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Sep 15, 2003
 * Time: 1:18:25 PM
 * To change this template use Options | File Templates.
 */
public class ConditionIterationRelation extends AbstractProceduralRelation implements MultiViewSupport, ViewSupport, IterationRelation {

    protected String initial_condition = "";
    protected String condition = "";
    protected PythonEditor pyEditor,pyEditor_cond,pyEditor_init_cond;
    protected String iteration_type = "WHILE_LOOP";//default
    protected boolean isInIteration = false;  //used for execute_normal method()... to keep in the memory whether the execution is in iteration process or not
    protected boolean isSettingIteratorsFinalValue = false;//used for execute_lock_in_this_relation method()... to keep in memory whether the execution method is triggered again by setting the iterators final value..in this case, we don't want to local loop to run again.
    protected boolean toExit = false;//used for do while loop to determine the last round

    protected static final String CONDITION_BOOL_VALUE = "ConditionBooleanValue";
    protected ModelObjectNameListener nameListener;
    protected int currentInsertionIndex = -1;
    protected List currentItems = Collections.EMPTY_LIST;
    protected ArrayList iteratorItems = new ArrayList();
    protected HashMap views; // keyed by view name
    boolean broadcasting_eachloop = true;//default






//Constructors
    public ConditionIterationRelation(ModelObjectScope scope, Id id) {
        super(scope, id);
        modelObjects.addDListListener(new RelationObjectsListener());
        views = new HashMap();
        nameListener = new ModelObjectNameListener();
        createViews();
        createPythonEditor();

    }

    public ConditionIterationRelation(ModelObjectScope scope, Id id, IterationRelation relation) {
        super(scope, id, relation);
        //setbody
        setBody(relation.getBody());
        //setcondition
        setCondition(relation.getCondition());

        setInitial_condition(relation.getInitial_condition());
        //setIterationType
        setIterationType(relation.getIterationType());
        //setBraodcasting
        setBroadcasting_eachloop(relation.isBroadcasting_eachloop());

        modelObjects.addDListListener(new RelationObjectsListener());
        views = new HashMap();
        nameListener = new ModelObjectNameListener();
        createViews();
        createPythonEditor();


    }

    public ConditionIterationRelation(ModelObjectScope scope, Element xmlElement) {
        super(scope, xmlElement);

        parseRelationAdditionalInfoElement(xmlElement);
        modelObjects.addDListListener(new RelationObjectsListener());
        views = new HashMap();
        nameListener = new ModelObjectNameListener();
        createViews();
        createPythonEditor();

    }

// Overwrite AbstractProcedureRelation methods
    protected String contentToString() {
        StringBuffer sb = new StringBuffer();
        sb.append("inputs: " + Names.getNameIds(modelObjects));
        //sb.append("\noutputs: "+Names.getNameIds());
        sb.append("\niteration type:\n" + getIterationType());
        sb.append("\ninitial condition:\n" + getInitial_condition());
        sb.append("\ncondition:\n" + getCondition());
        sb.append("\nbody:\n" + getBody());
        if (isBroadcasting_eachloop())
            sb.append("\nbroadcasting:\n" + "at the end of each loop");
        else
            sb.append("\nbroadcasting:\n" + "when condition meets");
        sb.append("\n" + dependencyInfo.toString());
        return sb.toString();
    }

    protected void addXmlContent(Element xmlElement) {
        XMLUtils.addCollection(xmlElement, "parameters", modelObjects);
        xmlElement.addElement("initcondition").addCDATA(getInitial_condition());
        xmlElement.addElement("condition").addCDATA(getCondition());
        xmlElement.addElement("body").addCDATA(getBody());
        xmlElement.addElement("iterationtype").addText(getIterationType());
        xmlElement.addElement("broadcasting").addAttribute("value", new Boolean(isBroadcasting_eachloop()).toString());
        xmlElement.add(dependencyInfo.toXmlElement());
        XMLUtils.addCollectionRef(xmlElement, "iterationparameters", iteratorItems);
    }


    protected void parseRelationAdditionalInfoElement(Element xmlElement) {
        super.parseRelationInfoElement(xmlElement);

        //read condition
        Element initconditionElement = (Element) xmlElement.selectSingleNode("initcondition");
        String initcondition = initconditionElement.getText();
        this.initial_condition = initcondition.trim();
        firePropertyChange(INIT_CONDITION, initcondition, this.initial_condition);


        //read condition
        Element conditionElement = (Element) xmlElement.selectSingleNode("condition");
        String condition = conditionElement.getText();
        this.condition = condition.trim();
        firePropertyChange(CONDITION, condition, this.condition);

        //read iteration type
        Element iterationtypeElement = (Element) xmlElement.selectSingleNode("iterationtype");
        String iterationtype = iterationtypeElement.getText();
        this.iteration_type = iterationtype.trim();
        firePropertyChange(ITERATIONTYPE, iterationtype, this.iteration_type);

        //read iteration type
        Element broadcastingElement = (Element) xmlElement.selectSingleNode("broadcasting");
        boolean is_broadcasting_each_loop = new Boolean(broadcastingElement.attributeValue("value")).booleanValue();
        this.setBroadcasting_eachloop(is_broadcasting_each_loop);

        // read parameters
        Id paramId;
        Parameter p1;
        Element paramElement;
        List params = xmlElement.selectNodes("iterationparameters/" + Parameter.XML_TAG);
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            p1 = null;
            paramElement = (Element) iter.next();
            paramId = AbstractDomeObject.parseXmlRef(paramElement);
            p1 = (Parameter) getModelObjectById(paramId);
            if (p1 == null) {
                throw new IllegalArgumentException("iterators parameter not found in relation");
            } else {
                iteratorItems.add(p1);
            }
        }
    }


    public List getIteratorItems() {
        return iteratorItems;
    }

    public void setIteratorItems(List params) {
        //clean all before adding
        iteratorItems.clear();
        for (Iterator i = params.iterator(); i.hasNext();) {
            Object p = i.next();
            if (p instanceof Parameter) {
                addIteratorItem((Parameter) p);
            }
        }
        firePropertyChange(ITERATORS, null, iteratorItems);
    }

    public void addIteratorItem(Parameter p) {
        //fist validate this parameter
        if (p == null) return;
        if (!getModelObjects().contains(p)) return;
        if (getCausality(p).equals(CausalityStatus.INDEPENDENT)) {
            //then p is valid for this purpose
            iteratorItems.add(p);
            firePropertyChange(ITERATOR, null, p);
        }
    }

    public boolean isIterator(Parameter p) {
        if (p == null) return false;
        if (iteratorItems.contains(p))
            return true;
        else
            return false;
    }

    public void removeIteratorItem(Parameter p) {
        if (p == null) return;
        if (iteratorItems.contains(p)) {
            iteratorItems.remove(p);
            firePropertyChange(ITERATOR, p, null);
        }
    }

    public void removeAllIteratorItems() {
        List oldIterators = new ArrayList(iteratorItems);
        iteratorItems.clear();
        firePropertyChange(ITERATORS, oldIterators, iteratorItems);

    }


//Overwrite ConcreteProcedureRelation methods
    public void handleDependencyInfoException(Exception ex) {
        //for now keeps the same as ConcreteProcedureRelation
        System.out.println("ConcreteProceduralRelation: DependencyInfo Exception thrown");
        ex.printStackTrace();
        throw new RuntimeException();
    }

    protected void createPythonEditor() {
        String[] keywords = new String[getParameterCount()];
        Collection params = getModelObjects();
        Iterator it = params.iterator();
        for (int i = 0; i < getParameterCount(); i++) {
            Object obj = it.next();
            if (obj instanceof Parameter) {
                keywords[i] = ((Parameter) obj).getName();
                ((Parameter) obj).addPropertyChangeListener(ModelObject.NAME, nameListener);
            }
        }
        pyEditor = new PythonEditor(null, keywords);
        pyEditor.setText(getBody());
        pyEditor.addPropertyChangeListener(PythonEditor.BODY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        String property = e.getPropertyName();
                        if (property.equals(PythonEditor.BODY)) {
                            String text = pyEditor.getText().trim();
                            setBody(text);
                        }
                    }
                });


        pyEditor_cond = new PythonEditor(null, keywords);
        pyEditor_cond.setText(getCondition());
        pyEditor_cond.addPropertyChangeListener(PythonEditor.BODY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        String property = e.getPropertyName();
                        if (property.equals(PythonEditor.BODY)) {
                            String text = pyEditor_cond.getText().trim();
                            setCondition(text);
                        }
                    }
                });

        pyEditor_init_cond = new PythonEditor(null, keywords);
        pyEditor_init_cond.setText(getInitial_condition());
        pyEditor_init_cond.addPropertyChangeListener(PythonEditor.BODY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        String property = e.getPropertyName();
                        if (property.equals(PythonEditor.BODY)) {
                            String text = pyEditor_init_cond.getText().trim();
                            setInitial_condition(text);
                        }
                    }
                });

    }

    public PythonEditor getPythonEditor_cond() {
        return pyEditor_cond;
    }

    public PythonEditor getPythonEditor_init_cond() {
        return pyEditor_init_cond;
    }

    public PythonEditor getPythonEditor() {
        return pyEditor;
    }

//Implement methods from IterationRelation interface
    public String getCondition() {
        if (condition == null) condition = "";
        return condition;
    }

    public void setCondition(String condition) {
        if (this.condition == null) this.condition = "";
        String oldcondition = this.condition;
        if (oldcondition.equals(condition))
            return;
        this.condition = condition;
        firePropertyChange(CONDITION, oldcondition, this.condition);
    }

    public String getInitial_condition() {
        if (initial_condition == null) condition = "";
        return initial_condition;
    }

    public void setInitial_condition(String initial_condition) {
        if (initial_condition == null) initial_condition = "";
        this.initial_condition = initial_condition;
        String oldcondition = this.initial_condition;
        if (oldcondition.equals(initial_condition))
            return;
        this.initial_condition = initial_condition;
        firePropertyChange(CONDITION, oldcondition, this.initial_condition);
    }

    public String getIterationType() {
        if (iteration_type == null) iteration_type = WHILE_LOOP;
        return iteration_type;
    }

    public void setIterationType(String iterationType) {
        String olditeration_type = getIterationType();
        if (olditeration_type.equals(iterationType))
            return;

        if (!isValidConditonIterationType(iterationType))
            return;

        this.iteration_type = iterationType;
        firePropertyChange(ITERATIONTYPE, olditeration_type, this.iteration_type);
    }

    public String[] getValidConditionIterationTypes() {
        return new String[]{WHILE_LOOP, DO_WHILE_LOOP, Timestep_LOOP};
    }

    public boolean isValidConditonIterationType(String it) {
        return it.equalsIgnoreCase(WHILE_LOOP) || it.equalsIgnoreCase(DO_WHILE_LOOP) || it.equalsIgnoreCase(Timestep_LOOP);
    }


    public boolean isBroadcasting_eachloop() {
        return broadcasting_eachloop;
    }

    public void setBroadcasting_eachloop(boolean broadcasting_eachloop) {
        boolean old_broadcasting_eachloop = this.broadcasting_eachloop;
        if (old_broadcasting_eachloop == broadcasting_eachloop) return;
        this.broadcasting_eachloop = broadcasting_eachloop;
        firePropertyChange(BROADCASTINGTYPE, new Boolean(old_broadcasting_eachloop), new Boolean(broadcasting_eachloop));
    }


    protected TypeInfo getTypeInfo() {
        return IterationRelation.TYPE_INFO;
    }

    class ModelObjectNameListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            if (property.equals(ModelObject.NAME)) {
                // update code editor
                Object oldValue = e.getOldValue();
                Object newValue = e.getNewValue();
                pyEditor.replaceKeyword((String) oldValue, (String) newValue);
                String text = pyEditor.getText();
                text.trim();
                setBody(text);
                pyEditor_cond.replaceKeyword((String) oldValue, (String) newValue);
                String text_cond = pyEditor_cond.getText();
                text_cond.trim();
                setCondition(text_cond);
                pyEditor_init_cond.replaceKeyword((String) oldValue, (String) newValue);
                String text_init_cond = pyEditor_init_cond.getText();
                text_init_cond.trim();
                setInitial_condition(text_init_cond);
            }
        }
    }

    //copy from concreteprocedural relation
    public boolean isInputFilter(Object obj) {
        return inputFilter.equals(obj);
    }


    public int getParameterCount() {
        return modelObjects.size();
    }


    protected void createViews() {
        // create model object type view
        List inputOutputView = new ArrayList();
        inputOutputView.add(inputFilter);
        inputOutputView.add(outputFilter);
        views.put(ProceduralRelation.INPUT_OUTPUT_VIEW, Collections.unmodifiableList(inputOutputView));
        // create model causality view
        List modelCausalityView = new ArrayList();
        modelCausalityView.add(independentFilter);
        modelCausalityView.add(intermediateFilter);
        modelCausalityView.add(resultFilter);
        views.put(ProceduralRelation.MODEL_CAUSALITY_VIEW, Collections.unmodifiableList(modelCausalityView));
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

    // ViewSupport interface (override)

    public List getView() {
        return getView(ProceduralRelation.INPUT_OUTPUT_VIEW);
    }

    public void addViewListener(DListListener l) {
        // do nothing
    }

    public void removeViewListener(DListListener l) {
        // do nothing
    }

    public List getValidModelObjectTypes() {
        List types = Registry.getDataObjectTypes();
        types.add("parameter");
        return types;
    }

    public ModelObject newModelObject(String modelObjectType) {
        currentInsertionIndex = inputFilter.getItemCount();
        currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
        ModelObject obj = super.newModelObject(modelObjectType);
        //Qing: May 18th, add for not allow parameter has a space in name
        if (obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter) obj, obj.getName()));
        return obj;
    }

    public ModelObject newModelObject(ModelObject modelObject) {
        currentInsertionIndex = inputFilter.getItemCount();
        currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
        ModelObject obj = super.newModelObject(modelObject);
        //Qing: May 18th, add for not allow parameter has a space in name
        if (obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter) obj, obj.getName()));
        return obj;
    }

    public Collection newModelObjects(Collection mObjs) {
        currentInsertionIndex = inputFilter.getItemCount();
        currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
        Collection objs = super.newModelObjects(mObjs);
//Qing: May 18th, add for not allow parameter has a space in name

        for (Iterator i = objs.iterator(); i.hasNext();) {
            ModelObject obj = (ModelObject) i.next();
            if (obj instanceof Parameter)
                obj.setName(getUniqueName((Parameter) obj, obj.getName()));
        }
        return objs;
    }

    public ModelObject newModelObject(String modelObjectType, int index) {
        if (index < 0 || index > inputFilter.getItemCount())
            currentInsertionIndex = inputFilter.getItemCount();
        else
            currentInsertionIndex = index;
        currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
        ModelObject obj = super.newModelObject(modelObjectType);
        //Qing: May 18th, add for not allow parameter has a space in name
        if (obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter) obj, obj.getName()));
        return obj;
    }

    public ModelObject newModelObject(ModelObject modelObject, int index) {
        if (index < 0 || index > inputFilter.getItemCount())
            currentInsertionIndex = inputFilter.getItemCount();
        else
            currentInsertionIndex = index;
        currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
        ModelObject obj = super.newModelObject(modelObject);
        //Qing: May 18th, add for not allow parameter has a space in name
        if (obj instanceof Parameter)
            obj.setName(getUniqueName((Parameter) obj, obj.getName()));
        return obj;
    }

    public Collection newModelObjects(Collection modelObjects, int index) {
        if (index < 0 || index > inputFilter.getItemCount())
            currentInsertionIndex = inputFilter.getItemCount();
        else
            currentInsertionIndex = index;
        currentItems = new ArrayList(this.getModelObjects()); // inputs, then outputs
        Collection objs = super.newModelObjects(modelObjects);
//Qing: May 18th, add for not allow parameter has a space in name

        for (Iterator i = objs.iterator(); i.hasNext();) {
            ModelObject obj = (ModelObject) i.next();
            if (obj instanceof Parameter)
                obj.setName(getUniqueName((Parameter) obj, obj.getName()));
        }
        return objs;
    }

    // mapping shortcuts support
    public void addAndMapModelObjects(Collection origModelObjects) {
//		System.out.println("params: " + Names.getNameIds(origModelObjects));
        mapModelObjects(origModelObjects, newModelObjects(origModelObjects));
    }

    public void addAndMapModelObjects(Collection origModelObjects, int index) {
//		System.out.println("index: " + index + "\tparams: " + Names.getNameIds(origModelObjects));
        mapModelObjects(origModelObjects, newModelObjects(origModelObjects, index));
    }

    protected void mapModelObjects(Collection parameters, Collection relationParameters) {
//		System.out.println("relParmams: " + Names.getNameIds(relationParameters));
        if (parameters.size() != relationParameters.size()) {
            System.err.println("some objects not copied correctly -- no automapping");
            return;
        }
        ConnectionMappingManager mm = ((DomeModel) getModel()).getMappingManager();
        Iterator relParams = relationParameters.iterator();
        Iterator params = parameters.iterator();
        List errors = new ArrayList();
        while (relParams.hasNext()) {
            Parameter rp = (Parameter) relParams.next();
            Parameter p = (Parameter) params.next();
            try {
//				System.out.println("addMapping:" + Names.getNameId(rp) + " -> " + Names.getNameId(p));
                mm.addMapping(rp, p);
            } catch (RuntimeException ex) {
                /**/System.out.println(ex);
                errors.add(ex);
            }
        }
        if (errors.size() == 1)
            throw (RuntimeException) errors.get(0);
        else if (errors.size() > 1)
            throw new MultipleErrorsException(errors);
    }


    // change causality as a result
    public class RelationObjectsListener implements DListListener {
        public void intervalChanged(DListEvent e) {
        }

        public void intervalAdded(DListEvent e) {
            addItems(e.getItems());
        }

        public void intervalRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsReplaced(DListEvent e) {
            throw new UnsupportedOperationException("can not set objects in Procedural Relation!");
        }
    }

    protected void addItems(List items) {
        currentItems.addAll(items);
        this.setDependencyInfo((new DependencyInfo(dependencyInfo, items, false)));
        currentInsertionIndex = -1; // important to reset so other filter activities are not changed
        currentItems = Collections.EMPTY_LIST;

        // insert in python editor and add object listeners
        for (Iterator objIter = items.iterator(); objIter.hasNext();) {
            ModelObject obj = (ModelObject) objIter.next();
            if (obj instanceof Parameter) {
                pyEditor.addKeyword(obj.getName());
                obj.addPropertyChangeListener(ModelObject.NAME, nameListener);
                pyEditor_cond.addKeyword(obj.getName());
                pyEditor_init_cond.addKeyword(obj.getName());
            }
        }
    }

    protected void removeItems(List items) {
        currentItems.removeAll(items);
        this.setDependencyInfo(new DependencyInfo(dependencyInfo, items, true));
        currentInsertionIndex = -1; // important to reset!
        currentItems = Collections.EMPTY_LIST;

        // remove from python editor and remove object listeners
        for (Iterator objIter = items.iterator(); objIter.hasNext();) {
            ModelObject obj = (ModelObject) objIter.next();
            if (obj instanceof Parameter) {
                pyEditor.removeKeyword(obj.getName());
                pyEditor_cond.removeKeyword(obj.getName());
                pyEditor_init_cond.removeKeyword(obj.getName());
                obj.removePropertyChangeListener(ModelObject.NAME, nameListener);
                //here also if it is in iterators list remove it
                if (isIterator((Parameter) obj))
                    removeIteratorItem((Parameter) obj);
            }
        }
    }


    /**
     * this method is called by buildPanel for testing, leave it here
     * Completely different implentation--- Qing Nov3rd
     *  Qing Nov 17th, change to execute normal
     */
    public void execute() {
        execute_normal();
    }


    /**
     * this method is called by solver
     * Ver 0.1  Nov 3rd  2003
     *  Ver 0.2  Nov 17th 2003
     *  Ver 0.3: Jan 25th 2004
     *  Ver 0.4  July 24 2004
     *  Ver 0.5  July 27 2004
     */
    public void execute(List changedParams) {
        if (getIterationType().equals(IterationRelation.WHILE_LOOP))
            execute_while_loop(changedParams);
        else if (getIterationType().equals(IterationRelation.DO_WHILE_LOOP))
            execute_do_while_loop(changedParams);
        else {
            //ToDo to be implemented for timestep

        }
    }


    //Qing July 27th:
    //algorithm: do condition first, satisfy, do body, not satisfy, exit
    public void execute_while_loop(List changedParams) {
        boolean should_exit = false;

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

        //because condition is always should get a boolean value
        BooleanData condition_value = new BooleanData(true);//default is true
        ht.put(CONDITION_BOOL_VALUE, condition_value);

        re.loadVariables(ht);

        // execute the relation code
        String conditioncode = getCondition();
        String bodycode = getBody();

        if (!isInIteration) { //first time
            String initConditioncode = getInitial_condition();
            if (!initConditioncode.trim().equals("")) {
                re.setCode(initConditioncode);
                re.run(); // throws RelationExecutionException if anything goes wrong
            }
            isInIteration = true;
        }

        if (!conditioncode.trim().equals("")) {
            //check condition
            re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
            re.run(); // throws RelationExecutionException if anything goes wrong
            condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
            if (condition_value.getValue())//true
            {
                should_exit = false;
                //do body
                re.setCode(bodycode);
                re.run(); // throws RelationExecutionException if anything goes wrong
            } else {   //false
                should_exit = true;
                //not do anything
            }
        } else {
            //pop up warning
            System.out.println("condition not defined, the relation will only run body once!");
            //do body first
            re.setCode(bodycode);
            re.run(); // throws RelationExecutionException if anything goes wrong
            should_exit = true;
        }

        //Qing-- should set value at the end, bcz otherwise if a change not satisfy condition, it won't get status update

        //then set the value of input parameters according whether to fire the next round or not
        if (!should_exit) {
            setValueAndDoAdvancedNotification(changedParams, params);
        } else {
            //should reset isInIteration
            isInIteration = false;
            //then next time it will run the initiate code
            doAdvancedNotificationAtTheEnd(changedParams, params);
        }
    }


    public void execute_do_while_loop(List changedParams) {
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

        //because condition is always should get a boolean value
        BooleanData condition_value = new BooleanData(true);//default is true
        ht.put(CONDITION_BOOL_VALUE, condition_value);

        re.loadVariables(ht);

        // execute the relation code
        String conditioncode = getCondition();
        String bodycode = getBody();

        if (!isInIteration) { //first time
            String initConditioncode = getInitial_condition();
            if (!initConditioncode.trim().equals("")) {
                re.setCode(initConditioncode);
                re.run(); // throws RelationExecutionException if anything goes wrong
            }
            isInIteration = true;

            toExit=false;
        }

        if (toExit) {//mean in the previoud round already satisfied, so this iteration is only to do adavanced notification
            //should reset isInIteration
            isInIteration = false;
            //then next time it will run the initiate code
            doAdvancedNotificationAtTheEnd(changedParams, params);
            return;
        }

        if (!conditioncode.trim().equals("")) {

            //do body first
            re.setCode(bodycode);
            re.run(); // throws RelationExecutionException if anything goes wrong
            //check condition
            re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
            re.run(); // throws RelationExecutionException if anything goes wrong
            condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
            if (condition_value.getValue())//true
            {
                toExit = false;
            } else {   //false
                toExit = true;
            }
        } else {
            //pop up warning
            System.out.println("condition not defined, the relation will only run body once!");
            //do body first
            re.setCode(bodycode);
            re.run(); // throws RelationExecutionException if anything goes wrong
            toExit = true;
        }

        setValueAndDoAdvancedNotification(changedParams, params);

    }


    /**
     *
     * @param changedParams
     * @param params :modelobject in this relation
     */
    protected void setValueAndDoAdvancedNotification(List changedParams, Collection params) {
        //---the following piece of code are setting the parameter values and status----
        // get the results from the interpreter
        List affectedParams = getAffectedParams(changedParams);
        if (!(affectedParams.size() == 0)) {
            for (Iterator i = params.iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof Parameter) {
                    CausalityStatus cs = getCausality(object);
                    if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                        Parameter originalParam = (Parameter) object;
                        DataObject originalParamData = originalParam.getCurrentDataObject();
                        DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                        try {
                            originalParamData.setValues(newParamData);
                        } catch (UnitsException e) {
                            throw new RelationExecutionException(getName(),
                                    e.getMessage(originalParam.getName()),
                                    e);
                        }
                    }
                }
            }
        }

        for (Iterator i = this.getIteratorItems().iterator(); i.hasNext();) {
            Object object = i.next();
            if (object instanceof Parameter) {
                CausalityStatus cs = getCausality(object);
                if (cs.equals(CausalityStatus.INDEPENDENT)) {//double check
                    Parameter originalParam = (Parameter) object;
                    DataObject originalParamData = originalParam.getCurrentDataObject();
                    DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                    try {
                        originalParamData.setValues(newParamData);
                    } catch (UnitsException e) {
                        throw new RelationExecutionException(getName(),
                                e.getMessage(originalParam.getName()),
                                e);
                    }
                }
            }
        }
        //---end of the piece of code are setting the parameter values and status----
    }


    public boolean isInIteration() {
        return isInIteration;
    }

    public void setInIteration(boolean inIteration) {
        isInIteration = inIteration;
    }

    /**
     * At the end of execution, the changed parameters should be set to status_waiting_validation,
     * for the while loop,
     *  since if the condition exit, no need to update value
     *  for example: if b+1<10 then do body
     *              when b comes to 9. the condition not satisfy, then exit
     *
     * for the do while loop,
     *  since if the condition exit, need to update value
     *  for example: do body when b+1<10
     *              when b comes to 10. the condition not satisfy, then exit, but the b is set to 10 now
     * @param changedParams
     * @param params
     */
    protected void doAdvancedNotificationAtTheEnd(List changedParams, Collection params) {
        for (Iterator i = this.getIteratorItems().iterator(); i.hasNext();) {
            Object object = i.next();
            if (object instanceof ModelParameterRuntime) {
                CausalityStatus cs = getCausality(object);
                if (cs.equals(CausalityStatus.INDEPENDENT)) {//double check
                    if (((ModelParameterRuntime) object).getValueStatus().equals(Parameter.VALUE_STATUS_INCONSISTENT))
                        ((ModelParameterRuntime) object).setValueStatus(Parameter.VALUE_STATUS_WAITING_VALIDATION);
                }
            }
        }
        List affectedParams = getAffectedParams(changedParams);
        if (!(affectedParams.size() == 0)) {
            for (Iterator i = params.iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof ModelParameterRuntime) {

                    CausalityStatus cs = getCausality(object);
                    if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                        //---the following piece of code are setting the parameter values and status----
                        // get the results from the interpreter
                        //if its driven parameters are in the loop, it is marked consistent,
                        //if it has outside parameters it should be marked waited to be confirmed
                        Parameter originalParam = (Parameter) object;
                        DataObject originalParamData = originalParam.getCurrentDataObject();
                        //DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                        try {
                            originalParamData.setValues(originalParamData);
                        } catch (UnitsException e) {
                            throw new RelationExecutionException(getName(),
                                    e.getMessage(originalParam.getName()),
                                    e);
                        }
                    }
                }
            }
        }

        //---end of the piece of code are setting the parameter values and status----
    }

    //--------------------backup code--------------------to be removed---------------------

    //Qing July 26th,
    public void execute_old(List changedParams) {
        boolean should_exit = false;

        if (re == null) {
            re = new RelationExecutorImpl(getName());
            //so that we do not waste time loading the pyhon classes
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

        //because condition is always should get a boolean value
        BooleanData condition_value = new BooleanData(true);//default is true
        ht.put(CONDITION_BOOL_VALUE, condition_value);

        re.loadVariables(ht);

        // execute the relation code
        String conditioncode = getCondition();
        String bodycode = getBody();

        if (!isInIteration) { //first time
            String initConditioncode = getInitial_condition();
            if (!initConditioncode.trim().equals("")) {
                re.setCode(initConditioncode);
                re.run(); // throws RelationExecutionException if anything goes wrong
            }
            isInIteration = true;
            //for do while need to run one more time

            //the do while case has one more execution than while
            if (getIterationType().equals(IterationRelation.DO_WHILE_LOOP)) {
                //do body first
                re.setCode(bodycode);
                re.run();
            }
        }

        if (!conditioncode.trim().equals("")) {
            //check condition
            re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
            re.run(); // throws RelationExecutionException if anything goes wrong
            condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
            if (condition_value.getValue())//true
            {
                should_exit = false;
                //do body
                re.setCode(bodycode);
                re.run(); // throws RelationExecutionException if anything goes wrong
            } else {   //false
                should_exit = true;
                //not do anything
            }
        } else {
            //pop up warning
            System.out.println("condition not defined, the relation will only run body once!");
            //do body first
            re.setCode(bodycode);
            re.run(); // throws RelationExecutionException if anything goes wrong
            should_exit = true;
        }

        //Qing-- should set value at the end, bcz otherwise if a change not satisfy condition, it won't get status update


        //then set the value of input parameters according whether to fire the next round or not
        if (!should_exit) {
            //---the following piece of code are setting the parameter values and status----
            // get the results from the interpreter
            List affectedParams = getAffectedParams(changedParams);
            if (!(affectedParams.size() == 0)) {
                for (Iterator i = params.iterator(); i.hasNext();) {
                    Object object = i.next();
                    if (object instanceof Parameter) {
                        CausalityStatus cs = getCausality(object);
                        if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                            Parameter originalParam = (Parameter) object;
                            DataObject originalParamData = originalParam.getCurrentDataObject();
                            DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                            try {
                                originalParamData.setValues(newParamData);
                            } catch (UnitsException e) {
                                throw new RelationExecutionException(getName(),
                                        e.getMessage(originalParam.getName()),
                                        e);
                            }
                        }
                    }
                }
            }

            for (Iterator i = this.getIteratorItems().iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof Parameter) {
                    CausalityStatus cs = getCausality(object);
                    if (cs.equals(CausalityStatus.INDEPENDENT)) {//double check
                        Parameter originalParam = (Parameter) object;
                        DataObject originalParamData = originalParam.getCurrentDataObject();
                        DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                        try {
                            originalParamData.setValues(newParamData);
                        } catch (UnitsException e) {
                            throw new RelationExecutionException(getName(),
                                    e.getMessage(originalParam.getName()),
                                    e);
                        }
                    }
                }
            }
            //---end of the piece of code are setting the parameter values and status----
        } else {
            //should reset isInIteration
            isInIteration = false;
            //then next time it will run the initiate code

            //Qing change here July 21, still need to set parameter status
            for (Iterator i = this.getIteratorItems().iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof ModelParameterRuntime) {
                    CausalityStatus cs = getCausality(object);
                    if (cs.equals(CausalityStatus.INDEPENDENT)) {//double check
                        if (((ModelParameterRuntime) object).getValueStatus().equals(Parameter.VALUE_STATUS_INCONSISTENT))
                            ((ModelParameterRuntime) object).setValueStatus(Parameter.VALUE_STATUS_WAITING_VALIDATION);
                    }
                }
            }
            List affectedParams = getAffectedParams(changedParams);
            if (!(affectedParams.size() == 0)) {
                for (Iterator i = params.iterator(); i.hasNext();) {
                    Object object = i.next();
                    if (object instanceof ModelParameterRuntime) {

                        CausalityStatus cs = getCausality(object);
                        if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                            //---the following piece of code are setting the parameter values and status----
                            // get the results from the interpreter
                            //if its driven parameters are in the loop, it is marked consistent,
                            //if it has outside parameters it should be marked waited to be confirmed
                            Parameter originalParam = (Parameter) object;
                            DataObject originalParamData = originalParam.getCurrentDataObject();
                            //DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                            try {
                                originalParamData.setValues(originalParamData);
                            } catch (UnitsException e) {
                                throw new RelationExecutionException(getName(),
                                        e.getMessage(originalParam.getName()),
                                        e);
                            }
                            //  ((ModelParameterRuntime) object).setValueStatus(Parameter.VALUE_STATUS_WAITING_VALIDATION);
                        }

                    }
                }
            }

            //---end of the piece of code are setting the parameter values and status----
        }
    }

    public void execute_normal() {
        boolean should_exit = false;

        if (re == null) {
            re = new RelationExecutorImpl(getName());
            //so that we do not waste time loading the pyhon classes
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

        //because condition is always should get a boolean value
        BooleanData condition_value = new BooleanData(true);//default is true
        ht.put(CONDITION_BOOL_VALUE, condition_value);

        re.loadVariables(ht);

        if (!isInIteration) { //first time
            String initConditioncode = getInitial_condition();
            if (!initConditioncode.trim().equals("")) {
                re.setCode(initConditioncode);
                re.run(); // throws RelationExecutionException if anything goes wrong
            }
            isInIteration = true;
        }


        // execute the relation code
        String conditioncode = getCondition();
        String bodycode = getBody();

        if (!conditioncode.trim().equals("")) {
            //the while case
            if (getIterationType().equals(IterationRelation.WHILE_LOOP)) {
                //check condition first
                re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
                re.run(); // throws RelationExecutionException if anything goes wrong
                condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
                if (condition_value.getValue())//true
                {
                    should_exit = false;
                    //do body
                    re.setCode(bodycode);
                    re.run(); // throws RelationExecutionException if anything goes wrong
                } else {   //false
                    should_exit = true;
                    //not do anything
                }
            } else if (getIterationType().equals(IterationRelation.DO_WHILE_LOOP)) {
                //do body first
                re.setCode(bodycode);
                re.run(); // throws RelationExecutionException if anything goes wrong
                //check condition then
                re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
                re.run(); // throws RelationExecutionException if anything goes wrong
                condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
                if (condition_value.getValue())//true
                {
                    should_exit = false;
                } else {
                    should_exit = true;
                }
            } else if (getIterationType().equals(IterationRelation.Timestep_LOOP)) {
                //todo: to be implemented
            }
        } else {
            //pop up warning
            System.out.println("condition not defined, the relation will only run body once!");
            //do body first
            re.setCode(bodycode);
            re.run(); // throws RelationExecutionException if anything goes wrong
            should_exit = true;
        }

        //Qing-- should set value at the end, bcz otherwise if a change not satisfy condition, it won't get status update


        //then set the value of input parameters according whether to fire the next round or not
        if (!should_exit) {
            // get the results from the interpreter
            for (Iterator i = params.iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof Parameter) {
                    CausalityStatus cs = getCausality(object);
                    if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                        Parameter originalParam = (Parameter) object;
                        DataObject originalParamData = originalParam.getCurrentDataObject();
                        DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                        try {
                            originalParamData.setValues(newParamData);
                        } catch (UnitsException e) {
                            throw new RelationExecutionException(getName(),
                                    e.getMessage(originalParam.getName()),
                                    e);
                        }
                    }
                }
            }

            for (Iterator i = this.getIteratorItems().iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof Parameter) {
                    CausalityStatus cs = getCausality(object);
                    if (cs.equals(CausalityStatus.INDEPENDENT)) {//double check
                        Parameter originalParam = (Parameter) object;
                        DataObject originalParamData = originalParam.getCurrentDataObject();
                        DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                        try {
                            originalParamData.setValues(newParamData);
                        } catch (UnitsException e) {
                            throw new RelationExecutionException(getName(),
                                    e.getMessage(originalParam.getName()),
                                    e);
                        }
                    }

                }
            }
        } else {
            //should reset isInIteration
            isInIteration = false;
            //then next time it will run the initiate code
        }
    }

    /*
    public void execute_lock_in_this_relation() {
        //do nothing is this method is triggered by setting the iterators final value
        if (isSettingIteratorsFinalValue) {
            isSettingIteratorsFinalValue = false;
            return;
        }

        if (re == null) {
            re = new RelationExecutorImpl();
            //so that we do not waste time loading the pyhon classes
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

        //because condition is always should get a boolean value
        BooleanData condition_value = new BooleanData(true);//default is true
        ht.put(CONDITION_BOOL_VALUE, condition_value);

        re.loadVariables(ht);

        //initial
        String initConditioncode = getInitial_condition();
        if (!initConditioncode.trim().equals("")) {
            re.setCode(initConditioncode);
            try {
                re.run();
            } catch (UnitsException e) {
                // todo: throw a meaningful units exception
                e.printStackTrace();
            }
        }
        boolean should_exit = false;
        // execute the relation code
        String conditioncode = getCondition();
        String bodycode = getBody();

        if (!conditioncode.trim().equals("")) {
            do {
                //the while case
                if (getIterationType().equals(IterationRelation.WHILE_LOOP)) {
                    //check condition first
                    re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
                    try {
                        re.run();
                    } catch (UnitsException e) {
                        // todo: throw a meaningful units exception
                        e.printStackTrace();
                    }
                    condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
                    if (condition_value.getValue())//true
                    {
                        should_exit = false;
                        //do body
                        re.setCode(bodycode);
                        try {
                            re.run();
                        } catch (UnitsException e) {
                            // todo: throw a meaningful units exception
                            e.printStackTrace();
                        }

                    } else {   //false
                        should_exit = true;
                        //not do anything
                    }
                } else if (getIterationType().equals(IterationRelation.DO_WHILE_LOOP)) {
                    //do body first
                    re.setCode(bodycode);
                    try {
                        re.run();
                    } catch (UnitsException e) {
                        // todo: throw a meaningful units exception
                        e.printStackTrace();
                    }

                    //check condition then
                    re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
                    try {
                        re.run();
                    } catch (UnitsException e) {
                        // todo: throw a meaningful units exception
                        e.printStackTrace();
                    }
                    condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
                    if (condition_value.getValue())//true
                    {
                        should_exit = false;
                    } else {
                        should_exit = true;
                    }
                } else if (getIterationType().equals(IterationRelation.Timestep_LOOP)) {
                    //todo: to be implemented
                }
            } while (!should_exit);
        } else {
            System.out.println("condition not defined, the relation will only run body once!");
            //do body first
            re.setCode(bodycode);
            try {
                re.run();
            } catch (UnitsException e) {
                // todo: throw a meaningful units exception
                e.printStackTrace();
            }

        }

        // get the results from the interpreter
        for (Iterator i = params.iterator(); i.hasNext();) {
            Object object = i.next();
            if (object instanceof Parameter) {
                CausalityStatus cs = getCausality(object);
                if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                    Parameter originalParam = (Parameter) object;
                    DataObject originalParamData = originalParam.getCurrentDataObject();
                    DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());

                    try {
                        originalParamData.setValues(newParamData);
                    } catch (UnitsException e) {
                        throw new UnitsException(originalParam,
                                originalParamData.getUnit().toString(),
                                newParamData.getUnit().toString());
                    }

                }
            }
        }
        //setting the iterators
        for (Iterator i = this.getIteratorItems().iterator(); i.hasNext();) {
            Object object = i.next();
            if (object instanceof Parameter) {
                CausalityStatus cs = getCausality(object);
                if (cs.equals(CausalityStatus.INDEPENDENT)) {//double check
                    Parameter originalParam = (Parameter) object;
                    DataObject originalParamData = originalParam.getCurrentDataObject();
                    DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());

                    try {
                        originalParamData.setValues(newParamData);
                    } catch (UnitsException e) {
                        throw new UnitsException(originalParam,
                                originalParamData.getUnit().toString(),
                                newParamData.getUnit().toString());
                    }
                }

                if (!isSettingIteratorsFinalValue) isSettingIteratorsFinalValue = true;
            }

        }


    }
   */
    /*
    //1: only set intermediate and result parameters to prevent solver gets infinite loop
    //2: for while loop, if the condition is not satisfied, logically no parameter should get update, but because in run mode the parameter status need to be taken careof, so need to add setvalue code there
    public void execute() {
        if (re == null) {
            re = new RelationExecutorImpl();
            //so that we do not waste time loading the pyhon classes
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

        //because condition is always should get a boolean value
        BooleanData condition_value = new BooleanData(true);//default is true
        ht.put(CONDITION_BOOL_VALUE, condition_value);

        re.loadVariables(ht);



        // execute the relation code
        String conditioncode = getCondition();
        String bodycode = getBody();

        //First, if condition is empty, the body will run only once
        if (!conditioncode.trim().equals("")) {
            //the while case
            if (getIterationType().equals(IterationRelation.WHILE_LOOP)) {
                boolean firstrun = true;
                do {
                    //first do condition
                    re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
                    try {
                        re.run();
                    } catch (UnitsException e) {
                        // todo: throw a meaningful units exception
                        e.printStackTrace();
                    }
                    condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
                    if (condition_value.getValue())//true
                    {
                        firstrun = false; //this means it's passed the condition test and coming to body code
                        //do body
                        re.setCode(bodycode);
                        try {
                            re.run();
                        } catch (UnitsException e) {
                            // todo: throw a meaningful units exception
                            e.printStackTrace();
                        }
                        if (isBroadcasting_eachloop()) {
                            // get the results from the interpreter
                            for (Iterator i = params.iterator(); i.hasNext();) {
                                Object object = i.next();
                                if (object instanceof Parameter) {
                                    CausalityStatus cs = getCausality(object);
                                    if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                                        Parameter originalParam = (Parameter) object;
                                        DataObject originalParamData = originalParam.getCurrentDataObject();
                                        DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                                        try {
                                            originalParamData.setValues(newParamData);
                                        } catch (UnitsException e) {
                                            throw new UnitsException(originalParam,
                                                    originalParamData.getUnit().toString(),
                                                    newParamData.getUnit().toString());
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        //stop
                        break;
                    }
                } while (true);
                if (!isBroadcasting_eachloop() || firstrun) {
                    // get the results from the interpreter
                    for (Iterator i = params.iterator(); i.hasNext();) {
                        Object object = i.next();
                        if (object instanceof Parameter) {
                            CausalityStatus cs = getCausality(object);
                            if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                                Parameter originalParam = (Parameter) object;
                                DataObject originalParamData = originalParam.getCurrentDataObject();
                                DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                                try {
                                    originalParamData.setValues(newParamData);
                                } catch (UnitsException e) {
                                    throw new UnitsException(originalParam,
                                            originalParamData.getUnit().toString(),
                                            newParamData.getUnit().toString());
                                }
                            }
                        }
                    }
                }
            }
//the do while case
            else if (getIterationType().equals(IterationRelation.DO_WHILE_LOOP)) {
                boolean firstrun = true;
                do {
                    //first do body
                    re.setCode(bodycode);
                    try {
                        re.run();
                    } catch (UnitsException e) {
                        // todo: throw a meaningful units exception
                        e.printStackTrace();
                    }

                    //then decide whether the end condition meets
                    re.setCode(CONDITION_BOOL_VALUE + "=(" + conditioncode + ")");
                    try {
                        re.run();
                    } catch (UnitsException e) {
                        // todo: throw a meaningful units exception
                        e.printStackTrace();
                    }

                    //broadcasting change
                    if (isBroadcasting_eachloop()) {
                        // get the results from the interpreter
                        for (Iterator i = params.iterator(); i.hasNext();) {
                            Object object = i.next();
                            if (object instanceof Parameter) {
                                CausalityStatus cs = getCausality(object);
                                if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                                    Parameter originalParam = (Parameter) object;
                                    DataObject originalParamData = originalParam.getCurrentDataObject();
                                    DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                                    try {
                                        originalParamData.setValues(newParamData);
                                    } catch (UnitsException e) {
                                        throw new UnitsException(originalParam,
                                                originalParamData.getUnit().toString(),
                                                newParamData.getUnit().toString());
                                    }
                                }
                            }
                        }
                    }

                    condition_value = (BooleanData) re.getVariable(CONDITION_BOOL_VALUE);
                    if (condition_value.getValue())//true
                    {
                        firstrun = false; //this means it's passed the condition test and coming to body code
                        continue;
                    } else {
                        //stop
                        break;
                    }
                } while (true);
                if (!isBroadcasting_eachloop() || firstrun) {
                    // get the results from the interpreter
                    for (Iterator i = params.iterator(); i.hasNext();) {
                        Object object = i.next();
                        if (object instanceof Parameter) {
                            CausalityStatus cs = getCausality(object);
                            if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                                Parameter originalParam = (Parameter) object;
                                DataObject originalParamData = originalParam.getCurrentDataObject();
                                DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                                try {
                                    originalParamData.setValues(newParamData);
                                } catch (UnitsException e) {
                                    throw new UnitsException(originalParam,
                                            originalParamData.getUnit().toString(),
                                            newParamData.getUnit().toString());
                                }
                            }
                        }
                    }
                }
            } else if (getIterationType().equals(IterationRelation.Timestep_LOOP)) {

            }

        } else {
            System.out.println("the body code will run only once since no condition is defined for this case");
            //do body
            re.setCode(bodycode);
            try {
                re.run();
            } catch (UnitsException e) {
                // todo: throw a meaningful units exception
                e.printStackTrace();
            }
            // get the results from the interpreter
            for (Iterator i = params.iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof Parameter) {
                    CausalityStatus cs = getCausality(object);
                    if (cs.equals(CausalityStatus.INTERMEDIATE) || cs.equals(CausalityStatus.RESULT)) {
                        Parameter originalParam = (Parameter) object;
                        DataObject originalParamData = originalParam.getCurrentDataObject();
                        DataObject newParamData = (DataObject) re.getVariable(originalParam.getName());
                        try {
                            originalParamData.setValues(newParamData);
                        } catch (UnitsException e) {
                            throw new UnitsException(originalParam,
                                    originalParamData.getUnit().toString(),
                                    newParamData.getUnit().toString());
                        }
                    }
                }
            }
        }

        re.clearVariables();
    }

    */
}



