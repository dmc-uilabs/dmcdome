package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ClientRuntimeScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeSupport;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.ImmutableCausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.NameIdNode;
import mit.cadlab.dome3.objectmodel.util.solving.MultiItemNode;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 6, 2003
 * Time: 7:53:43 PM
 * To change this template use Options | File Templates.
 */
public class ModelInterfaceRuntimeClient extends AbstractModelInterfaceRuntime {
    public static final String PROPERTY_CLOSED = "interfaceClientClosed";
    public static final String INTERFACEMODELSTATUS = "interfaceModelStatus";
    public static final String INTERFACEINPUT = "interfaceInput";
	public static final String SUBMITTING_CHANGES = "submitting changes";

    //manages system or model level causality of parameters in the interface
    //static info loaded from xml at this point //TODO how to listen to causality changes?
    //  ModelInterfaceRuntimeCausalityManager systemCausalityManager;

    protected DataObjectChangeListener internalParameterChangeListener = new InternalParameterChangeListener();
    private ServerConnection serverConnection;
	private boolean ifaceLoaded = false;
	private boolean changesWaiting = false;

    // maps ids to values list for changed items
    protected Hashtable changedParameters = new Hashtable();
    protected ClientRuntimeScope model;   //can be ClientModelRuntime or IntegrationProjectRunTime

    protected DirectedGraph interfaceGraph;
    protected HashMap relParamToIfaceMap;
    private boolean isSystemCausalityInitialized;
    private DArrayList systemCausalityList;
    private boolean notCalculatingSystemCausality;


    public ModelInterfaceRuntimeClient(Id id) {
        super(null, id);
        populateInterfaceObjectFlatMap();
    }

    public ModelInterfaceRuntimeClient(String id) {
        super(null, id);
        populateInterfaceObjectFlatMap();
    }

    public ModelInterfaceRuntimeClient(Id id, ModelObjectScope mObjScope) {
        super(null, id, mObjScope);
        populateInterfaceObjectFlatMap();
    }

    /**
     * Server connection not needed during deploy.
     * @param xmlElement
     */
    public ModelInterfaceRuntimeClient(Element xmlElement, boolean notCalculatingSystemCausality) {
        super(null, xmlElement, null); // no mappings for runtime client
        this.notCalculatingSystemCausality = notCalculatingSystemCausality;
        init(new CompoundId(), null, xmlElement);
    }

    /**
     * Constructor for browsing an interface via its XML
     * // todo: create a browse representation of an interface
     * @param deployId
     * @param xmlElement
     */
    public ModelInterfaceRuntimeClient(String deployId, Element xmlElement) {
        this(new CompoundId(), null, (ClientModelRuntime) null, xmlElement);
        this.setId(deployId);
    }

    public ModelInterfaceRuntimeClient(CompoundId id, ServerConnection svrConn,
                                       IntegrationProjectClientRuntime project, Element xmlElement) {
        this(id, svrConn, project, xmlElement, true);
    }

	public ModelInterfaceRuntimeClient(CompoundId id, ServerConnection svrConn,
	                                   IntegrationProjectClientRuntime project, Element xmlElement, boolean ifaceCreatedOnServer)
	{
		super(null, xmlElement, null); // no mappings for runtime client
		this.model = project;
		this.ifaceLoaded = ifaceCreatedOnServer;
		if (model != null)
			((IntegrationProjectClientRuntime) this.model).addPropertyChangeListener(new ModelStatusChangeListener());
		init(id, svrConn, xmlElement);
		if (!ifaceLoaded)
			markParametersInconsistent();
	}

    public ModelInterfaceRuntimeClient(CompoundId id, ServerConnection svrConn,
                                       ClientModelRuntime model, Element xmlElement) {
	    this(id, svrConn, model, xmlElement, true);
    }

	public ModelInterfaceRuntimeClient(CompoundId id, ServerConnection svrConn,
	                                   ClientModelRuntime model, Element xmlElement, boolean ifaceCreatedOnServer)
	{
		super(null, xmlElement, null); // no mappings for runtime client
		this.model = model;
		this.ifaceLoaded = ifaceCreatedOnServer;
		if (model != null)
			((ClientModelRuntime) this.model).addPropertyChangeListener(new ModelStatusChangeListener());
		init(id, svrConn, xmlElement);
		if (!ifaceLoaded)
			markParametersInconsistent();
	}

    private void init(CompoundId id,
                      ServerConnection svrConn, Element xmlElement) {
        serverConnection = svrConn;
        if (serverConnection != null)
            serverConnection.addReference();
        populateInterfaceObjectFlatMap();
        createViews();

        runtimeId = new CompoundId(id);

        //populate the interface causality and system causality views
        //build context and thus build view is already populated in the ModelInterfaceBase class
        List viewList = xmlElement.selectNodes("/" + getXmlTag() + "/" + DomeModelInterface.VIEWS + "/" + DomeModelInterface.VIEW);
        for (Iterator i = viewList.iterator(); i.hasNext();) {
            Element viewElement = (Element) i.next();
            String viewName = viewElement.attributeValue(DomeModelInterface.NAME);
            if (viewName.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
                List filterList = viewElement.selectNodes(Filter.XML_TAG);
                for (Iterator fiter = filterList.iterator(); fiter.hasNext();) {
                    Element fElement = (Element) fiter.next();
                    String filtername = fElement.attributeValue(DomeModelInterface.NAME);
                    Element paramsElement = (Element) fElement.selectSingleNode("parameters");
                    if (paramsElement == null) {
                        continue; //filter is empty
                    }
                    List paramElements = paramsElement.elements();
                    if (paramElements == null || paramElements.isEmpty()) {
                        continue; //filter is empty
                    }
                    for (Iterator piter = paramElements.iterator(); piter.hasNext();) {
                        Element pElement = (Element) piter.next();
                        Id modelObjId = parseXmlRef(pElement);
                        Parameter param = (Parameter) interfaceObjectsFlatMap.get(modelObjId);
                        if (filtername.equals(DomeModelInterface.INPUTS)) {
                            changeCausality(param, CausalityStatus.INDEPENDENT);
//System.out.println(param.getName() + " interface causality = independent");
                        } else if (filtername.equals(DomeModelInterface.OUTPUTS)) {
                            String causality = pElement.attributeValue("causality");
                            if (causality.equals(CausalityStatus.RESULT.toString())) {
                                changeCausality(param, CausalityStatus.RESULT);
//System.out.println(param.getName() + " interface causality = result");
                            } else {
                                changeCausality(param, CausalityStatus.INTERMEDIATE);
//System.out.println(param.getName() + " interface causality = intermediate");
                            }
                        } else if (filtername.equals(DomeModelInterface.INDETERMINATES)) {
                            changeCausality(param, CausalityStatus.INDETERMINATE);
//System.out.println(param.getName() + " interface causality = indeterminate");
                        }
                    }
                }
            } /*else if (viewName.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
                List filterList = viewElement.selectNodes(Filter.XML_TAG);
                for (Iterator fiter = filterList.iterator(); fiter.hasNext();) {
                    Element fElement = (Element) fiter.next();
                    String filtername = fElement.attributeValue(DomeModelInterface.NAME);
                    Element paramsElement = (Element) fElement.selectSingleNode("parameters");
                    if (paramsElement == null) {
                        continue; //filter is empty
                    }
                    List paramElements = paramsElement.elements();
                    if (paramElements == null || paramElements.isEmpty()) {
                        continue; //filter is empty
                    }
                    for (Iterator piter = paramElements.iterator(); piter.hasNext();) {
                        Element pElement = (Element) piter.next();
                        Id modelObjId = parseXmlRef(pElement);
                        Parameter param = (Parameter) interfaceObjectsFlatMap.get(modelObjId);
                        if (filtername.equals(CausalityStatus.INDEPENDENT.toString())) {
                            changeSystemCausality(param, CausalityStatus.INDEPENDENT);
        //System.out.println(param.getName() + " system causality = independent");
                        } else if (filtername.equals(CausalityStatus.INTERMEDIATE.toString())) {
                            changeSystemCausality(param, CausalityStatus.INTERMEDIATE);
        //System.out.println(param.getName() + " system causality = intermediate");
                        } else if (filtername.equals(CausalityStatus.RESULT.toString())) {
                            changeSystemCausality(param, CausalityStatus.RESULT);
        //System.out.println(param.getName() + " system causality = result");
                        } else if (filtername.equals(CausalityStatus.INDETERMINATE.toString())) {
                            changeSystemCausality(param, CausalityStatus.INDETERMINATE);
        //System.out.println(param.getName() + " system causality = indeterminate");
                        }
                    }
                }
            }*/
        }
        registerInternalParameterChangeListeners();

        Element e = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "directedGraph");
        if (e == null) {
            interfaceGraph = new DirectedGraph();
            return;
        }
        interfaceGraph = new DirectedGraph(e);
        List nodes = interfaceGraph.getNodes();
        HashMap hm = new HashMap();
        Iterator i = nodes.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof NameIdNode || o instanceof MultiItemNode)
                hm.put(o, o);
            else
                hm.put(o, interfaceObjectsFlatMap.get(new Id((String) o)));
        }
        interfaceGraph = new DirectedGraph(interfaceGraph, hm);

        e = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "interfaceToRelParamMap");
        relParamToIfaceMap = XMLUtils.parseStringMap(e, "interfaceToRelParamMap", "iToRpMap", "relParamId", "ifaceId");
    }

	private void markParametersInconsistent()
	{
		Iterator inputs = inputFilter.getItems().iterator();
		while (inputs.hasNext()) {
			((Parameter) inputs.next()).setValueStatus(Parameter.VALUE_STATUS_WAITING_VALIDATION);
		}
		Iterator outputs = outputFilter.getItems().iterator();
		while (outputs.hasNext()) {
			((Parameter) outputs.next()).setValueStatus(Parameter.VALUE_STATUS_INCONSISTENT);
		}
	}

	public boolean isLoaded() {
		return ifaceLoaded;
	}

	public DirectedGraph getInterfaceGraph() {
        return interfaceGraph;
    }

    public HashMap getRelParamToIfaceMap() {
        return relParamToIfaceMap;
    }

    protected void registerInternalParameterChangeListeners() {
        Iterator it = interfaceObjectsFlatMap.values().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof InterfaceParameterClient) {
                ((InterfaceParameterClient) o).addServerParameterListener(internalParameterChangeListener);
            } else if (o instanceof Parameter)
                System.err.println(((Parameter) o).getName() + " is not instance of InterfaceParameterClient!");
        }
    }

    // add BUILD_VIEW to list of views so it can be obtained via getView(viewName)
    // implementation assumes BUILD_VIEW does not change
    // todo (should change build context to actual view object)
    protected void createViews() {
        super.createViews(); // interface causality view & system causality view
        views.put(BUILD_VIEW, Collections.unmodifiableList(getBuildContext().getModelObjectReferences()));
    }

    protected CausalityManager createInternalCausalityManager() {
        return new ModelInterfaceRuntimeCausalityManager();
    }

    protected CausalityManager createInternalCausalityManager(Element xmlElement) {
        return new ModelInterfaceRuntimeCausalityManager(xmlElement);
    }

    // Filters
    protected void createInternalCausalityFilters() {
        inputFilter = new InputFilter(null);   //null model
        outputFilter = new OutputFilter(null);  //null model
        indeterFilter = new IndeterminateFilter(null); //null model
    }

    protected void createSystemCausalityFilters() {
        systemCausalityList = new DArrayList();
        systemCausalityManager = new ModelInterfaceRuntimeCausalityManager();
        independentFilter = new SystemCausalityFilter(null, CausalityStatus.INDEPENDENT);  //null model
        intermediateFilter = new SystemCausalityFilter(null, CausalityStatus.INTERMEDIATE); //null model
        resultFilter = new SystemCausalityFilter(null, CausalityStatus.RESULT);  //null model
    }

    public boolean isItemOfSystemCausality(Object obj, CausalityStatus cause) {
/*        if (!(obj instanceof Parameter))
            return false;
        CausalityStatus stat = systemCausalityManager.getCausality(obj);*/
        Hashtable parameterSystemCausality = null; // key=parameter id, value=CausalityStatus.name
        if (!isSystemCausalityInitialized) {
            parameterSystemCausality = RuntimeFunctionsClient.getParameterSystemCausality(serverConnection, getRuntimeId()); // on the server
            isSystemCausalityInitialized = true;
            //call setdata on sys causality manager
            ((ModelInterfaceRuntimeCausalityManager) systemCausalityManager).setData(parameterSystemCausality);
        }
        CausalityStatus stat = systemCausalityManager.getCausality(obj);
        return (cause.equals(stat));
    }

    public ClientRuntimeScope getModelRuntime() {
        return model;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public String getXmlDescription() {
        // not implemented for now -- must overload the toXml() method first
        return null;
    }

    public ModelObjectFactory getModelObjectFactory() {
        if (m_moFactory == null)
            m_moFactory = new RuntimeInterfaceClientObjectFactory();
        return m_moFactory;
    }

    protected class RuntimeInterfaceClientObjectFactory implements ModelObjectFactory {
        /**
         * obj is either object type name, type symbol, or object instance
         * params must be the parameters to the constructor method in correct order
         */
        public ModelObject newInstance(Object obj, Object[] params) {
            try {
                Constructor ctr = null;
                try {
                    ctr = Registry.getConstructor(obj, Registry.CLIENT_CLS);
                } catch (Exception e) {
                    ctr = Registry.getConstructor(obj, Registry.BASE_CLS);
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

    protected class ModelInterfaceRuntimeCausalityManager
            extends AbstractModelObjectScope.AbstractInternalCausalityManager {

        public ModelInterfaceRuntimeCausalityManager() {
            causalityChangeListeners = new CausalityChangeSupport(ModelInterfaceRuntimeClient.this);
        }

        public ModelInterfaceRuntimeCausalityManager(Element xmlElement) {
            super(xmlElement);
        }

        protected CausalityStatus getInitialCausality(Object obj) {
            return getNewObjectCausality();
        }

        public void setData(Hashtable causal) {
            causalityLists.put(CausalityStatus.INDEPENDENT, new ArrayList()); // key = CausalityStatus, value=list of objects
            causalityLists.put(CausalityStatus.INTERMEDIATE, new ArrayList());
            causalityLists.put(CausalityStatus.RESULT, new ArrayList());
            causalityLists.put(CausalityStatus.INDETERMINATE, new ArrayList());
            for (Iterator iterator = causal.keySet().iterator(); iterator.hasNext();) {
                String o = (String) iterator.next();
                Id id = new Id(o);
                Object param = getModelObjectById(id);
                if ((causal.get(o)).equals(CausalityStatus.INDEPENDENT.toString())) {
                    objectCausality.put(param, CausalityStatus.INDEPENDENT); // key=object; value=CausalityStatus
                    ((List) causalityLists.get(CausalityStatus.INDEPENDENT)).add(param);
                } else if ((causal.get(o)).equals(CausalityStatus.INTERMEDIATE.toString())) {
                    objectCausality.put(param, CausalityStatus.INTERMEDIATE);
                    ((List) causalityLists.get(CausalityStatus.INTERMEDIATE)).add(param);
                } else if ((causal.get(o)).equals(CausalityStatus.RESULT.toString())) {
                    objectCausality.put(param, CausalityStatus.RESULT);
                    ((List) causalityLists.get(CausalityStatus.RESULT)).add(param);
                } else if ((causal.get(o)).equals(CausalityStatus.INDETERMINATE.toString())) {
                    objectCausality.put(param, CausalityStatus.INDETERMINATE);
                    ((List) causalityLists.get(CausalityStatus.INDETERMINATE)).add(param);
                }
            }
        }
    }

    protected void changeCausality(Object obj, CausalityStatus cause) {
        ((ModelInterfaceRuntimeCausalityManager) internalCausalityManager).changeCausality(
                obj, cause);
    }

    protected void changeSystemCausality(Object obj, CausalityStatus cause) {
        ((ModelInterfaceRuntimeCausalityManager) systemCausalityManager).changeCausality(obj, cause);
    }

    public CausalityManager getSystemCausalityManager() {
        return new ImmutableCausalityManager(systemCausalityManager);
    }

    //for testing
    public void printViewContents() {
        System.out.println("Build View");
        for (Iterator i = buildViewContext.getModelObjectReferences().iterator(); i.hasNext();) {
            ModelObject obj = (ModelObject) i.next();
            System.out.println(obj.getName());
            if (obj instanceof Context) {
                for (Iterator j = ((Context) obj).getModelObjectReferences().iterator(); j.hasNext();) {
                    ModelObject object = (ModelObject) j.next();
                    System.out.println("\t" + object.getName());
                }
            }
        }

        System.out.println("Interface causality view");
        System.out.println("Input filter contains: ");
        for (Iterator i = inputFilter.getItems().iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            System.out.println(param.getName());
        }
        System.out.println("Output filter contains: ");
        for (Iterator i = outputFilter.getItems().iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            System.out.println(param.getName());
        }
        System.out.println("Indeterminate filter contains: ");
        for (Iterator i = indeterFilter.getItems().iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            System.out.println(param.getName());
        }

        System.out.println("System causality view");
        System.out.println("Independent filter contains: ");
        for (Iterator i = independentFilter.getItems().iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            System.out.println(param.getName());
        }
        System.out.println("Intermediate filter contains: ");
        for (Iterator i = intermediateFilter.getItems().iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            System.out.println(param.getName());
        }
        System.out.println("Result filter contains: ");
        for (Iterator i = resultFilter.getItems().iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            System.out.println(param.getName());
        }
    }

    public void printViews() {
        Iterator it = views.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry viewEntry = (Map.Entry) it.next();
            System.out.println("view: " + viewEntry.getKey());
            Iterator items = ((List) viewEntry.getValue()).iterator();
            while (items.hasNext()) {
                Object o = items.next();
                System.out.println("  " + Names.getName(o));
                if (o instanceof Filter)
                    printFilter((Filter) o);
            }
        }
    }

    public void printFilter(Filter f) {
        Iterator it = f.getItems().iterator();
        while (it.hasNext()) {
            System.out.println("    " + Names.getName(it.next()));
        }
    }

    public Hashtable getAndResetChangeMap() {
        synchronized (changedParameters) {
            Hashtable table = changedParameters;
            changedParameters = new Hashtable();
            return table;
        }
    }

    //to detect change in inputs
    class InternalParameterChangeListener extends DataObjectChangeListener {
        public InternalParameterChangeListener() {
            super(ModelInterfaceRuntimeClient.this);
        }

        public void dataObjectChanged(DataObjectChangeEvent e) {
            Parameter p = e.getParameter();
            CompoundId objectCompoundId = new CompoundId(runtimeId);
            objectCompoundId.setDataObjectStaticId(p.getId().toString());
            Object oldValue = e.getEvent().getOldValue();
            Object newValue = e.getEvent().getNewValue();
            synchronized (changedParameters) {
                if (oldValue == null || !oldValue.equals(newValue)) { // todo: move this check elsewhere
                    changedParameters.put(objectCompoundId.toString(), Collections.singletonList(newValue));
                    p.setValueStatus(Parameter.VALUE_STATUS_STALE);
                }
                if (changedParameters.size() > 0) {
                    firePropertyChange(INTERFACEINPUT, null, null);
                }
            }
        }
    }

    public static void main(String args[]) {
        //test2 code for xml load constructor
        SAXReader xmlReader = new SAXReader();
        Document doc = null;
        try {
            doc = xmlReader.read("C:\\test2\\runtest\\test2.dmi");
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        if (doc == null) {
            System.out.println("document is null");
            System.exit(1);
        }
        Element root = doc.getRootElement();
        DomeInit.initializeDOME();
        ModelInterfaceRuntimeClient client;
        client = new ModelInterfaceRuntimeClient(root, false);
        client.printViewContents();
    }


    /**
     * Indicate the parameter status by changing the background color of the edit field.
     * @param objectId Object id
     * @param status Status
     */
    public void setStatus(Id objectId, int changedId, String status) {
        InterfaceParameterClient mObj = (InterfaceParameterClient) interfaceObjectsFlatMap.get(objectId);
        mObj.setValueStatus(changedId, status);
    }


    /**
     * Set a parameter's data object values.
     * @param objectId Id of the parameter
     * @param args Argument list
     */
    public void setItem(Id objectId, List args) {
        InterfaceParameterClient mObj = (InterfaceParameterClient) interfaceObjectsFlatMap.get(objectId);
        mObj.setValues(args);
    }

	// asks server what the proper interface state is
	public void synchronizeInterfaceState() {
		String ifaceState = RuntimeFunctionsClient.getInterfaceStatus(this.serverConnection, runtimeId);
		if (ModelRuntime.STATUS_IFACE_CREATED.equals(ifaceState)) {
			ifaceLoaded = true;
			Debug.trace(Debug.ALL, "interface loaded: " + getName());
		} else {
			Debug.trace(Debug.ALL, getName() + " state: " + ifaceState);
		}
		firePropertyChange(INTERFACEMODELSTATUS, null, ifaceState);
	}

	public boolean areChangesWaiting()
	{
		return changesWaiting;
	}

    public void submitChanges() {
	    if (ifaceLoaded) {
		    // submit all changes
		    Debug.trace(Debug.ALL, "submitting changes for interface: " + getName());
		    firePropertyChange(INTERFACEMODELSTATUS, null, SUBMITTING_CHANGES);
		    synchronized (changedParameters) {
		        if (changedParameters.isEmpty())
		            changedParameters.put(this.getRuntimeId().toString(), Collections.EMPTY_LIST);
		        RuntimeFunctionsClient.setItems(serverConnection, changedParameters, true);
		        changedParameters = new Hashtable(); // create a new one so that xml-rpc call is not mangled!
		    }
	    } else {
		    Debug.trace(Debug.ALL, "caching submitted changes for interface: " + getName());
		    changesWaiting = true;
	    }
    }

    public void pauseSolving() {
        // pause the solving
        CompoundId cid = this.getRuntimeId();
        RuntimeFunctionsClient.pauseSolving(serverConnection, cid);
    }

    public void resumeSolving() {
        // resume the solving
        CompoundId cid = this.getRuntimeId();
        RuntimeFunctionsClient.resumeSolving(serverConnection, cid);
    }

    public void killSolving() {
        // kill the solving process
        CompoundId cid = this.getRuntimeId();
        RuntimeFunctionsClient.killSolving(serverConnection, cid);
    }

    class ModelStatusChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pe) {
            String propName = pe.getPropertyName();
            if (propName.equals(ClientModelRuntime.MODELSTATUS) ||
                    propName.equals(IntegrationProjectClientRuntime.PROJECTSTATUS)) {
                //fire another property change which will bew captured by the interface gui
                Object oldValue = pe.getOldValue();
                Object newValue = pe.getNewValue();
	            Debug.trace(Debug.ALL, getName() + " state: " + newValue);
	            firePropertyChange(INTERFACEMODELSTATUS, oldValue, newValue);
	            if (!ifaceLoaded && ModelRuntime.STATUS_IFACE_CREATED.equals(newValue)) {
		            ifaceLoaded = true;
		            Debug.trace(Debug.ALL, "interface loaded: " + getName());
		            if (changesWaiting) {
			            changesWaiting = false;
			            submitChanges();
		            }
	            }
            }
        }
    }

    public int getNumberOfChangedParameters() {
        return changedParameters.size();
    }

    public boolean isInterfaceConsistent() {
        Iterator mObjs = getModelObjects().iterator();
        ModelObject mObj;
        while (mObjs.hasNext()) {
            mObj = (ModelObject) mObjs.next();
            if (mObj instanceof Parameter)
                if (!Parameter.VALUE_STATUS_CONSISTENT.equals(((Parameter) mObj).getValueStatus()))
                    return false;
        }
        return true;
    }

    public boolean isProjectinterface() {
        return (model instanceof IntegrationProject);
    }

    public Collection addItemsToFilterListener(DListListener l) {
        systemCausalityList.addDListListener(l);
        return Collections.unmodifiableList(systemCausalityList);
    }

    public Collection removeItemsToFilterListener(DListListener l) {
        systemCausalityList.removeDListListener(l);
        return Collections.unmodifiableList(systemCausalityList);
    }
    //don't need to override delete(DeletionListener notifier), addDeletionListener(DeletionListener l),
    //and removeDeletionListener(DeletionListener l) because it's not gonna happen in runtime anyway

    public void setCurrentView(String view) {
        super.setCurrentView(view);
        if (currentView == DomeModelInterface.SYSTEM_CAUSALITY_VIEW) {
            populateSystemCausalityList();
        }
    }

    private void populateSystemCausalityList() {
        Collection params = getModelObjectParameters();
        systemCausalityList.clear();
        systemCausalityList.addAll(params);
        isSystemCausalityInitialized = false; // needs to recalculate the whole system causality when the view is reselected
    }
}