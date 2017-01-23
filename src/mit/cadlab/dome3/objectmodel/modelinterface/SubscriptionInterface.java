package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.UnreachableServerException;
import mit.cadlab.dome3.network.client.connection.ServerMethodException;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.FileTransport;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.ImmutableCausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.NameIdNode;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 6, 2003
 * Time: 7:53:43 PM
 * To change this template use Options | File Templates.
 */
public class SubscriptionInterface extends AbstractModelInterfaceRuntime
{
	public static final String PROPERTY_REMOVED = "removed";

	ModelInterfaceRuntimeCausalityManager systemCausalityManager; //manages system or model level causality of parameters in the interface
	//static info loaded from xml at this point //TODO how to listen to causality changes?

	protected DataObjectChangeListener internalParameterChangeListener = new InternalParameterChangeListener();
	protected PropertyChangeListener internalParameterStatusChangeListener = new InternalParameterStatusChangeListener();
	private ServerConnection serverConnection;
	public static final String INTERFACEMODELSTATUS = "interfaceModelStatus";
	public static final String INTERFACEINPUT = "interfaceInput";

	protected DirectedGraph interfaceGraph;
	protected HashMap ifaceToRelParamMap;
	protected Subscription subscription;
	protected Hashtable changedParameters = new Hashtable();
	protected Vector parametersWithInconsistentStatus = new Vector();
	protected Integer numberPendingStatusNotifications = new Integer(0);

	public SubscriptionInterface(Id id)
	{
		super(null, id);
		populateInterfaceObjectFlatMap();
	}

	public SubscriptionInterface(String id)
	{
		super(null, id);
		populateInterfaceObjectFlatMap();
	}

	public SubscriptionInterface(Model m , Id id, ModelObjectScope mObjScope)
	{
		super(m, id, mObjScope);
		populateInterfaceObjectFlatMap();
	}

	/**
	 * Server connection not needed during deploy.
	 * @param xmlElement
	 */
	public SubscriptionInterface(Element xmlElement)
	{
		this(new CompoundId(), null, xmlElement);
	}

	/**
	 * Constructor for browsing an interface via its XML
	 * // todo: create a browse representation of an interface
	 * @param deployId
	 * @param xmlElement
	 */
	public SubscriptionInterface(String deployId, Element xmlElement)
	{
		this(new CompoundId(), null, xmlElement);
		this.setId(deployId);
	}

	public SubscriptionInterface(CompoundId id, ServerConnection svrConn, Element xmlElement)
	{
		super(null, xmlElement, null); // no mappings for runtime client
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
			} else if (viewName.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
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
			}
		}

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
			if (o instanceof NameIdNode)
				hm.put(o, o);
			/*if (o.equals(MODEL_NODE))
				hm.put(MODEL_NODE, MODEL_NODE);*/
			else
				hm.put(o, interfaceObjectsFlatMap.get(new Id((String) o)));
		}

		interfaceGraph = new DirectedGraph(interfaceGraph, hm);

		e = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/" + "interfaceToRelParamMap");
		ifaceToRelParamMap = XMLUtils.parseStringMap(e, "interfaceToRelParamMap", "iToRpMap", "relParamId", "ifaceId");

		registerInternalParameterChangeListeners();
	}

	public void setSubscription(Subscription sub)
	{ // for use by Subscription
		if (subscription != null)
			throw new UnsupportedOperationException("SubscriptionInterface.setSubscription - subscription not null");
		this.subscription = sub;
		this.model = this.subscription.getModel();
	}


	public Subscription getSubscription()
	{
		return subscription;
	}

	public DirectedGraph getInterfaceGraph()
	{
		return interfaceGraph;
	}

	public HashMap getIfaceToRelParamMap()
	{
		return ifaceToRelParamMap;
	}

	protected void registerInternalParameterChangeListeners()
	{
		Iterator it = interfaceObjectsFlatMap.values().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof ModelParameterRuntime) {
				if (CausalityStatus.INDEPENDENT.equals(getCausality(o))) { // todo: how to handle disconnected parameters?
					((ModelParameterRuntime) o).addSubscriptionInterfaceListener(internalParameterChangeListener);
					((ModelParameterRuntime) o).addPropertyChangeListener(Parameter.VALUE_STATUS, internalParameterStatusChangeListener);
				}
			} else if (o instanceof Parameter)
				System.err.println(((Parameter) o).getName() + " is not instance of ModelParameterRuntime!");
		}
	}

	// add BUILD_VIEW to list of views so it can be obtained via getView(viewName)
	// implementation assumes BUILD_VIEW does not change
	// todo (should change build context to actual view object)
	protected void createViews()
	{
		super.createViews(); // interface causality view & system causality view
		views.put(BUILD_VIEW, Collections.unmodifiableList(getBuildContext().getModelObjectReferences()));
	}

	protected CausalityManager createInternalCausalityManager()
	{
		return new ModelInterfaceRuntimeCausalityManager();
	}

	protected CausalityManager createInternalCausalityManager(Element xmlElement)
	{
		return new ModelInterfaceRuntimeCausalityManager(xmlElement);
	}

	// Filters
	protected void createInternalCausalityFilters()
	{
		inputFilter = new InputFilter(null);   //null model
		outputFilter = new OutputFilter(null);  //null model
		indeterFilter = new IndeterminateFilter(null); //null model
	}

	protected void createSystemCausalityFilters()
	{
		systemCausalityManager = new ModelInterfaceRuntimeCausalityManager();
		independentFilter = new SystemCausalityFilter(null, CausalityStatus.INDEPENDENT);  //null model
		intermediateFilter = new SystemCausalityFilter(null, CausalityStatus.INTERMEDIATE); //null model
		resultFilter = new SystemCausalityFilter(null, CausalityStatus.RESULT);  //null model
	}

	public boolean isItemOfSystemCausality(Object obj, CausalityStatus cause)
	{
		if (!(obj instanceof Parameter))
			return false;
		CausalityStatus stat = systemCausalityManager.getCausality(obj);
		return (cause.equals(stat));
	}


	public ServerConnection getServerConnection()
	{
		return serverConnection;
	}

	public CompoundId getRuntimeId()
	{
		return runtimeId;
	}

	public String getXmlDescription()
	{
		// not implemented for now -- must overload the toXml() method first
		return null;
	}

	public ModelObjectFactory getModelObjectFactory()
	{
		if (m_moFactory == null)
			m_moFactory = new RuntimeInterfaceClientObjectFactory();
		return m_moFactory;
	}

	protected class RuntimeInterfaceClientObjectFactory implements ModelObjectFactory
	{
		/**
		 * obj is either object type name, type symbol, or object instance
		 * params must be the parameters to the constructor method in correct order
		 */
		public ModelObject newInstance(Object obj, Object[] params)
		{
			try {
				Constructor ctr = null;
				try {
					ctr = Registry.getConstructor(obj, Registry.SERVER_CLS);
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
				System.err.println("newInstance: " + e.getTargetException() + "\t" + obj);
			}
			return null;
		}
	}

	protected class ModelInterfaceRuntimeCausalityManager
	        extends AbstractModelObjectScope.AbstractInternalCausalityManager
	{

		public ModelInterfaceRuntimeCausalityManager()
		{
		}

		public ModelInterfaceRuntimeCausalityManager(Element xmlElement)
		{
			super(xmlElement);
		}

		protected CausalityStatus getInitialCausality(Object obj)
		{
			return getNewObjectCausality();
		}

	}

	protected void changeCausality(Object obj, CausalityStatus cause)
	{
		((ModelInterfaceRuntimeCausalityManager) internalCausalityManager).changeCausality(
		        obj, cause);
	}

	protected void changeSystemCausality(Object obj, CausalityStatus cause)
	{
		systemCausalityManager.changeCausality(obj, cause);
	}

	public CausalityManager getSystemCausalityManager()
	{
		return new ImmutableCausalityManager(systemCausalityManager);
	}

	//for testing
	public void printViewContents()
	{
		System.out.println("Build View");
		for (Iterator i = buildViewContext.getModelObjectReferences().iterator(); i.hasNext();) {
			ModelObject obj = (ModelObject) i.next();
			//System.out.println(obj.getName());
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

	public void printViews()
	{
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

	public void printFilter(Filter f)
	{
		Iterator it = f.getItems().iterator();
		while (it.hasNext()) {
			System.out.println("    " + Names.getName(it.next()));
		}
	}

	class InternalParameterStatusChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			Parameter p = (Parameter) evt.getSource();
			CompoundId objectCompoundId = new CompoundId(runtimeId);
			objectCompoundId.setDataObjectStaticId(p.getId().toString());
			Object oldValue = evt.getOldValue();
			Object newValue = evt.getNewValue();
			if (!newValue.equals(oldValue) &&
			        (Parameter.VALUE_STATUS_INCONSISTENT.equals(newValue) || Parameter.VALUE_STATUS_STALE.equals(newValue))) {
				synchronized (parametersWithInconsistentStatus) {
					parametersWithInconsistentStatus.add(objectCompoundId.toString());
				}
			}
		}
	}

	// to detect change in inputs
	class InternalParameterChangeListener extends DataObjectChangeListener
	{
		public InternalParameterChangeListener()
		{
			super(SubscriptionInterface.this);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			Parameter p = e.getParameter();
			CompoundId objectCompoundId = new CompoundId(runtimeId);
			objectCompoundId.setDataObjectStaticId(p.getId().toString());
			Object value = e.getEvent().getNewValue();
			if (value == null) {
				Object currentDataObject = p.getCurrentDataObject();
				if (currentDataObject instanceof FileData) {
					FileData fileDataObj = (FileData) currentDataObject;
					Object fileValue = fileDataObj.getValuesForXmlRpcUse();
					
					//value = ((FileData)p.getCurrentDataObject()).getValuesForXmlRpcUse();
					// MAK: <value> is the file contents. Create a FileTransport instance that contains
					//      the contents AND filename, and pass that instance into the changemaps for RuntimeFunctions setItems() processing
					if (fileValue instanceof byte[]) {
						byte[] fileContents = (byte[]) fileValue;
						FileTransport fileTransportObj = new FileTransport(fileDataObj.getActualFilename(), fileContents);
						// reset <value> to be the newly created fileTransportObj so it's added to the changedParameters list below
						value = fileTransportObj;
					} else if (fileValue instanceof URL) {
						value = fileValue;
					}
				}
			}
			
			if (value == null)
				System.err.println(p.getName()+" change ignored: null new value for property "+e.getEvent().getPropertyName());
			else {
				synchronized (changedParameters) {
					//MAK: <value> is file contents. Should FileTransport obj be "put" instead?
					changedParameters.put(objectCompoundId.toString(), Collections.singletonList(value));
				}
				if (!((DomeModelRuntime)getModel()).isSolving()) // don't change to yellow during solving!
					p.setValueStatus(Parameter.VALUE_STATUS_STALE);
			}
		}
	}

	public void submitChanges()
	{
		// wait until value changes are done before submitting values (or else there are problems!)
		int numPendingValueChanges = -1;
		synchronized (numberPendingStatusNotifications) {
			numPendingValueChanges = numberPendingStatusNotifications.intValue();
		}
		while (numPendingValueChanges != 0) {
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
			}
			synchronized (numberPendingStatusNotifications) {
				numPendingValueChanges = numberPendingStatusNotifications.intValue();
			}
		}
		// submit all changes
		if (getModel() instanceof DomeModelRuntime && ((DomeModelRuntime) getModel()).isWaitingToDie())
			return; // don't set outputs if waiting to die
		synchronized (changedParameters) {
			if (!changedParameters.isEmpty()) {
				RuntimeFunctionsClient.setItems(serverConnection, changedParameters, true);
				changedParameters = new Hashtable(); // create a new one so that xml-rpc call is not mangled!
			}
		}
	}

	public void submitInconsistentParameters() {

		synchronized (parametersWithInconsistentStatus) {
			if (!parametersWithInconsistentStatus.isEmpty()) {
				Vector params = parametersWithInconsistentStatus;
				parametersWithInconsistentStatus = new Vector();
				new Thread(new StatusChangeThread(params)).start();
			}
		}

	}

	public static void main(String args[])
	{
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
		SubscriptionInterface client;
		client = new SubscriptionInterface(root);
		client.printViewContents();
	}

	/**
	 * Set a parameter's data object values.
	 * @param objectId Id of the parameter
	 * @param args Argument list
	 */
	public void setItem(Id objectId, List args)
	{
		ModelParameterRuntime mObj = (ModelParameterRuntime) interfaceObjectsFlatMap.get(objectId);
		mObj.setValues(args);
	}

	class StatusChangeThread implements Runnable {
		Vector paramsIds;

		public StatusChangeThread(Vector inconsistentParamIds)
		{
			synchronized (numberPendingStatusNotifications) {
				numberPendingStatusNotifications = new Integer(numberPendingStatusNotifications.intValue() + 1);
			}
			this.paramsIds = inconsistentParamIds;
		}

		public void run()
		{
			try {
				serverConnection.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "."
				                   + RuntimeConstants.MESSAGE_PARAMETERS_INCONSISTENT, Vectors.create(paramsIds));
			}
			catch (UnreachableServerException e) {
				System.err.println("SubscriptionInterface.StatusChangeThread: "+SubscriptionInterface.this.getName() +
				                   " at " + serverConnection.getServerPort() + "\n\t" + e);
			}
			catch (ServerMethodException e) {
				System.err.println(e);
			}

			synchronized (numberPendingStatusNotifications) {
				numberPendingStatusNotifications = new Integer(numberPendingStatusNotifications.intValue() - 1);
			}
		}
	}

	public void pauseSolving()
	{
		// pause the solving
		CompoundId cid = this.getRuntimeId();
		if (serverConnection != null)
			RuntimeFunctionsClient.pauseSolving(serverConnection, cid);
	}

	public void resumeSolving()
	{
		// resume the solving
		CompoundId cid = this.getRuntimeId();
		if (serverConnection != null)
			RuntimeFunctionsClient.resumeSolving(serverConnection, cid);
	}

	public void killSolving()
	{
		// kill the solving process
		CompoundId cid = this.getRuntimeId();
		if (serverConnection != null)
			RuntimeFunctionsClient.killSolving(serverConnection, cid);
	}

	/**
	 * Tell anyone who's listening that this interface is being killed.
	 */
	public void notifyRemovalEvent()
	{
		if (serverConnection != null)
			serverConnection.removeReference();
		firePropertyChange(PROPERTY_REMOVED);
	}


	/**
	 * Cause a units incompatible message to be sent to an interface GUI.
	 * @param targetParameterId Target parameter id
	 * @param sourceUnit Source unit string
	 * @param targetUnit Target unit string
	 */
	public void messageUnitsIncompatible(CompoundId targetParameterId,
	                                     String sourceUnit, String targetUnit)
	{
//		Id targetId = new Id(targetParameterId.getDataObjectStaticId());
//		Parameter p = (Parameter) interfaceObjectsFlatMap.get(targetId);
//		String targetParamName = p.getName();

		OneButton1Msg.showError(null, "Unit assignment error",
		                        "Input unit (" + sourceUnit + ") and output unit (" + targetUnit + ") are incompatible",
		                        "ok", new Dimension());

		// todo: send a message to the interface gui (firePropertyChange?)
	}

	class ModelStatusChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent pe)
		{
			String propName = pe.getPropertyName();
			if (propName.equals(ClientModelRuntime.MODELSTATUS)) {
				//fire another property change which will bew captured by the interface gui
				Object oldValue = pe.getOldValue();
				Object newValue = pe.getNewValue();
				firePropertyChange(INTERFACEMODELSTATUS, oldValue, newValue);
			}
		}
	}

}