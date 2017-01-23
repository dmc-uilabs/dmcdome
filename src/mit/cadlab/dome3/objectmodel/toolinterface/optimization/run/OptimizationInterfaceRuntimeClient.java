package mit.cadlab.dome3.objectmodel.toolinterface.optimization.run;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.AbstractOptimizationToolInterfaceRuntime;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.tool.AnalysisToolUtils;
import mit.cadlab.dome3.util.Regex;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Oct 22, 2003
 * Time: 8:48:59 PM
 * To change this template use Options | File Templates.
 */

/**
 * OptmizationInterfaceRuntimeClient
 * This class is used to construct a run - time object of the optimization tool
 * interface on the client side.
 */
public class OptimizationInterfaceRuntimeClient extends AbstractOptimizationToolInterfaceRuntime
{
    public static final String _EMPTY_STRING = "";
    public static final String PROPERTY_CLOSED = "analysisToolInterfaceClientClosed";
    public static final String INTERFACEMODELSTATUS = "interfaceModelStatus";
    public static final String INTERFACEINPUT = "interfaceInput";
    public static final String NEW_INDIVIDUAL_ADDED = "newIndividualAdded";
    public static final String RESULTS_LOADED = "resultsLoaded";
    public static final String RESET_PLOT = "resetPlot";
    public static final String INTERFACE_RESULTS = "interfaceresults";
    public static final String OPTIMIZATION_ANALYSIS_COMPLETE = "optimizationAnalysisIsComplete";

    public static final int NO_PARAMETERS_ACTIVE = 0;
    public static final int VARIABLES_OR_OBJECTIVES_ACTIVE = 1;
    public static final int VARIABLES_AND_OBJECTIVES_ACTIVE = 2;

    protected List _clientSideChangeList = new ArrayList();   //used in client side playspace

    protected DataObjectChangeListener internalParameterChangeListener = new InternalParameterChangeListener();

    private ServerConnection _serverConnection;
    private boolean _shouldSaveResultsFile;
    private int _numberOfListeners = 0;
    private HashMap _parameterHistory;
    private Vector _individualRanks;
    private Vector _variableMaps, _objectiveMaps;
    private String _resultsFileName = ""; // file name for the results file

    private boolean _ifaceLoaded = false;

    public OptimizationInterfaceRuntimeClient(Element xmlElement)
    {
        super(null, xmlElement, null);
        init(new CompoundId(), null, xmlElement);
    }

    public OptimizationInterfaceRuntimeClient(CompoundId id, ServerConnection svrConn,
	                                   ClientModelRuntime model, Element xmlElement)
	{
		super(null, xmlElement, null); // no mappings for runtime client
        if (model != null)
            model.addPropertyChangeListener(new ModelStatusChangeListener());

        init(id, svrConn, xmlElement);
	}

    protected void init(CompoundId id,
                          ServerConnection svrConn, Element xmlElement)
    {
        _serverConnection = svrConn;
        if (_serverConnection != null)
            _serverConnection.addReference();

        _runtimeId = new CompoundId(id);
        _parameterHistory = new HashMap();
        _individualRanks = new Vector();
        _variableMaps = new Vector();
        _objectiveMaps = new Vector();

        populateInterfaceObjectFlatMap();
        populateParameterHistoryMap();
        createViews();
        registerInternalParameterChangeListeners();
    }

    public ModelObjectFactory getModelObjectFactory()
	{
		if (m_moFactory == null)
			m_moFactory = new RuntimeInterfaceClientObjectFactory();
		return m_moFactory;
	}

    protected void registerInternalParameterChangeListeners()
	{
		Iterator it = _interfaceObjectsFlatMap.values().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof InterfaceParameterClient) {
				((InterfaceParameterClient) o).addServerParameterListener(internalParameterChangeListener);
			} else if (o instanceof Parameter)
				System.err.println(((Parameter) o).getName() + " is not instance of InterfaceParameterClient!");
		}
	}

    protected void populateParameterHistoryMap()
    {
        Iterator it = _interfaceObjectsFlatMap.values().iterator();
        while (it.hasNext())
        {
            Object o = it.next();
            if (o instanceof InterfaceParameterClient)
            {
                String parameterId = ((Parameter) o).getId().toString();
                _parameterHistory.put(parameterId, new Vector());
            }
        }
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

    // to detect change in inputs
	class InternalParameterChangeListener extends DataObjectChangeListener
	{
		public InternalParameterChangeListener()
		{
			super(OptimizationInterfaceRuntimeClient.this);
		}

		public void dataObjectChanged(DataObjectChangeEvent e)
		{
			Parameter p = e.getParameter();
			CompoundId objectCompoundId = new CompoundId(_runtimeId);
			objectCompoundId.setDataObjectStaticId(p.getId().toString());
			Object oldValue = e.getEvent().getOldValue();
			Object newValue = e.getEvent().getNewValue();
			if (oldValue == null || !oldValue.equals(newValue))
            {
				_clientSideChangeList.add(p);
				p.setValueStatus(Parameter.VALUE_STATUS_STALE);
			}
		}
	}

    class ModelStatusChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent pe)
		{
			String propName = pe.getPropertyName();
			if (propName.equals(ClientModelRuntime.MODELSTATUS))
            {
				//fire another property change which will bew captured by the interface gui
				Object oldValue = pe.getOldValue();
				Object newValue = pe.getNewValue();
				firePropertyChange(INTERFACEMODELSTATUS, oldValue, newValue);
			}
		}
	}

    public int activeCondition()
    {
        int activeCondition = 0;
        Iterator iter = _interfaceVariableMap.values().iterator();
        while (iter.hasNext())
        {
            VariableParameter varParameter = (VariableParameter) iter.next();
            if (varParameter.getIsActive().getValue())
            {
                activeCondition++;
                break;
            }
        }

        iter = _interfaceObjectiveMap.values().iterator();
        while (iter.hasNext())
        {
            ObjectiveParameter objParameter = (ObjectiveParameter) iter.next();
            if (objParameter.getIsActive().getValue())
            {
                activeCondition++;
                break;
            }
        }
        return activeCondition;
    }

    public void startOptimization()
    {
        RuntimeFunctionsClient.startAnalysisToolSolving(_serverConnection, _runtimeId, prepareParameterAttributeTable(), true);
        _clientSideChangeList.clear();
    }

    public void addIndividualToClient(Vector v)
    {
        System.out.println("***** Adding Individuals To Client: *****");
        Vector isRankOne = (Vector) v.get(0);
        Hashtable variableTable = (Hashtable) v.get(1);
        Hashtable objectiveTable = (Hashtable) v.get(2);
        System.out.println("Size: " + isRankOne.size());
        
        synchronized (_parameterHistory)
        {
            Iterator iterator = _interfaceObjectsFlatMap.values().iterator();
            while (iterator.hasNext())
            {
                Object obj = iterator.next();
                if (obj instanceof InterfaceParameterClient)
                {
                    String parameterId = ((Parameter) obj).getId().toString();
                    Vector vec1 = (Vector) _parameterHistory.get(parameterId);
                    if (variableTable.containsKey(parameterId))
                    {
                        vec1.add(variableTable.get(parameterId));
                    }
                    else if (objectiveTable.containsKey(parameterId))
                    {
                        vec1.add(objectiveTable.get(parameterId));
                    }
                }
            }
            _individualRanks.add(isRankOne.get(0));
            _variableMaps.add(variableTable);
            _objectiveMaps.add(objectiveTable);
            firePropertyChange(NEW_INDIVIDUAL_ADDED, null, Vectors.create(isRankOne.get(0), variableTable, objectiveTable));
        }
    }

    public void preparePlotForNewData()
    {
        synchronized (_parameterHistory)
        {
            _individualRanks.removeAllElements();
            _variableMaps.removeAllElements();
            _objectiveMaps.removeAllElements();

            Iterator vectors = _parameterHistory.values().iterator();
            while (vectors.hasNext())
            {
                Vector v = (Vector) vectors.next();
                v.removeAllElements();
            }
            firePropertyChange(RESET_PLOT, null, null);
        }
    }

    public void optimizationAnalysisIsComplete()
    {
        synchronized (_parameterHistory)
        {
            firePropertyChange(OPTIMIZATION_ANALYSIS_COMPLETE, null, null);
        }
    }

    public HashMap getParameterHistory()
    {
        return _parameterHistory;
    }

    public Vector getIndividualRanks()
    {
        return _individualRanks;
    }

    public ServerConnection getServerConnection()
	{
		return _serverConnection;
	}

    private Hashtable prepareParameterAttributeTable()
    {
        Hashtable t = new Hashtable();

        Iterator iter = _interfaceOptimizationParametersMap.values().iterator();
        while (iter.hasNext())
        {
            Object obj = iter.next();
            if (obj instanceof VariableParameter)
            {
                VariableParameter vp = (VariableParameter) obj;
                Vector v = new Vector();
                v.add(vp.getUpperLimit().getRealValue()); // upper limit
                v.add(vp.getLowerLimit().getRealValue()); // lower limit
                v.add(vp.getIsActive().getBooleanValue()); // is active
                t.put(vp.getParameter().getId().toString(), v);
            }
            else if (obj instanceof ObjectiveParameter)
            {
                ObjectiveParameter op = (ObjectiveParameter) obj;
                Vector v = new Vector();
                v.add(op.getIsActive().getBooleanValue()); // is active
                t.put(op.getParameter().getId().toString(), v);
            }
        }
        return t;
    }

    public void handleParetoPlotMouseClick(Vector v)
    {
        Hashtable variables = (Hashtable) v.get(1);
        Hashtable objectives = (Hashtable) v.get(2);

        Iterator iterator = _interfaceObjectsFlatMap.values().iterator();
        while (iterator.hasNext())
        {
            Object obj = iterator.next();
            if (obj instanceof InterfaceParameterClient)
            {
                String parameterId = ((Parameter) obj).getId().toString();
                if (variables.containsKey(parameterId))
                {
                    Double newValue = (Double) variables.get(parameterId);
                    VariableParameter variableParameter = (VariableParameter) _interfaceVariableMap.get(obj);
                    if (variableParameter.getParameter().getCurrentDataObject().getTypeName().equals(DomeReal.TYPE_INFO.getTypeName()))
                        ((DomeReal)variableParameter.getParameter().getCurrentDataObject()).setRealValue(newValue);
                }
                else if (objectives.containsKey(parameterId))
                {
                    Double newValue = (Double) objectives.get(parameterId);
                    ObjectiveParameter objectiveParameter = (ObjectiveParameter) _interfaceObjectiveMap.get(obj);
                    if (objectiveParameter.getParameter().getCurrentDataObject().getTypeName().equals(DomeReal.TYPE_INFO.getTypeName()))
                        ((DomeReal)objectiveParameter.getParameter().getCurrentDataObject()).setRealValue(newValue);
                }
            }
        }
        RuntimeFunctionsClient.setProjectInterfaceInsideAnalysisToolItems(_serverConnection, _runtimeId, v);
    }

    public void handleDesignSpacePlotMouseClick (Vector v)
    {
        Hashtable variables = (Hashtable) v.get(1);

        Iterator iterator = _interfaceObjectsFlatMap.values().iterator();
        while (iterator.hasNext())
        {
            Object obj = iterator.next();
            if (obj instanceof InterfaceParameterClient)
            {
                String parameterId = ((Parameter) obj).getId().toString();
                if (variables.containsKey(parameterId))
                {
                    Double newValue = (Double) variables.get(parameterId);
                    VariableParameter variableParameter = (VariableParameter) _interfaceVariableMap.get(obj);
                    if (variableParameter.getParameter().getCurrentDataObject().getTypeName().equals(DomeReal.TYPE_INFO.getTypeName()))
                        ((DomeReal)variableParameter.getParameter().getCurrentDataObject()).setRealValue(newValue);
                }
            }
        }
        RuntimeFunctionsClient.setProjectInterfaceInsideAnalysisToolItems(_serverConnection, _runtimeId, v);
    }

    public void loadResults(String resultsFileName)
    {
        _resultsFileName = resultsFileName;
        clearDataVectors();
        parseXmlElement();
    }

    protected void clearDataVectors()
    {
        _individualRanks.removeAllElements();
        _variableMaps.removeAllElements();
        _objectiveMaps.removeAllElements();

        Iterator vectors = _parameterHistory.values().iterator();
        while (vectors.hasNext())
        {
            Vector v = (Vector) vectors.next();
            v.removeAllElements();
        }
    }

    protected void parseXmlElement()
    {
        int vectorSize = 0;
        Element modelElement = XMLUtils.fileToXmlElement(_resultsFileName);

        if (modelElement.getQName().getName().equals(INTERFACE_RESULTS))
        {
            String id = modelElement.attributeValue("id");
			if (id == null)
				throw new IllegalArgumentException(getTypeName() + " - no xml id in interface results file");
            if (id.equals(getRuntimeId().getInterfaceStaticId()))
            {
                List variables = modelElement.selectNodes("/" + INTERFACE_RESULTS + "/variables/" + Parameter.XML_TAG);
                Iterator variableIterator = variables.iterator();
                while (variableIterator.hasNext())
                {
                    Element element = (Element) variableIterator.next();
                    String variableId = element.attributeValue("id");
                    if (_parameterHistory.containsKey(variableId))
                    {
                        ((Vector) _parameterHistory.get(variableId)).removeAllElements();
                        String vectorInString = element.element("values").getText();
                        String vectorInStringTrim = vectorInString.substring(vectorInString.indexOf("[") + 1, vectorInString.indexOf("]"));
                        List values = Regex.split(", ", vectorInStringTrim);
                        vectorSize = values.size();
                        Iterator valueIterator = values.iterator();
                        while (valueIterator.hasNext())
                        {
                            String value = (String) valueIterator.next();
                            ((Vector)_parameterHistory.get(variableId)).add(new Double(value));
                        }
                    }
                }

                List objectives = modelElement.selectNodes("/" + INTERFACE_RESULTS + "/objectives/" + Parameter.XML_TAG);
                Iterator objectiveIterator = objectives.iterator();
                while (objectiveIterator.hasNext())
                {
                    Element element = (Element) objectiveIterator.next();
                    String objectiveId = element.attributeValue("id");
                    if (_parameterHistory.containsKey(objectiveId))
                    {
                        ((Vector) _parameterHistory.get(objectiveId)).removeAllElements();
                        String vectorInString = element.element("values").getText();
                        String vectorInStringTrim = vectorInString.substring(vectorInString.indexOf("[") + 1, vectorInString.indexOf("]"));
                        List values = Regex.split(", ", vectorInStringTrim);
                        Iterator valueIterator = values.iterator();
                        while (valueIterator.hasNext())
                        {
                            String value = (String) valueIterator.next();
                            if (value != null && (!value.equals(_EMPTY_STRING)))
                                ((Vector)_parameterHistory.get(objectiveId)).add(new Double(value));
                        }
                    }
                }

                for (int i=0; i < vectorSize; i++)
                {
                    Hashtable variableTable = new Hashtable();
                    Hashtable objectiveTable = new Hashtable();

                    Iterator paramIter = _parameterHistory.keySet().iterator();
                    while (paramIter.hasNext())
                    {
                        String parameterId = (String) paramIter.next();

                        Iterator varIter = _interfaceVariableMap.keySet().iterator();
                        while (varIter.hasNext())
                        {
                            Parameter p = (Parameter) varIter.next();
                            if (p.getId().toString().equals(parameterId))
                            {
                                Vector v = (Vector) _parameterHistory.get(parameterId);
                                if (v != null && (!v.isEmpty()))
                                    variableTable.put(parameterId, v.get(i));
                                break;
                            }
                        }

                        Iterator objIter = _interfaceObjectiveMap.keySet().iterator();
                        while (objIter.hasNext())
                        {
                            Parameter p = (Parameter) objIter.next();
                            if (p.getId().toString().equals(parameterId))
                            {
                                Vector v = (Vector) _parameterHistory.get(parameterId);
                                if (v != null && (!v.isEmpty()))
                                    objectiveTable.put(parameterId, v.get(i));
                                break;
                            }
                        }
                    }
                    _variableMaps.add(variableTable);
                    _objectiveMaps.add(objectiveTable);
                }

                String ranksInString = modelElement.element("ranks").getText();
                String ranksInStringTrim = ranksInString.substring(ranksInString.indexOf("[") + 1, ranksInString.indexOf("]"));
                List ranks = Regex.split(", ", ranksInStringTrim);
                Iterator rankIterator = ranks.iterator();
                while (rankIterator.hasNext())
                {
                    String value = (String) rankIterator.next();
                    _individualRanks.add(new Boolean(value));
                }

                firePropertyChange(RESULTS_LOADED, null, null);
            }
            else
            {
                OneButton1Msg.showError(null, "load results error", "results file does not belong to this interface", "ok", OneButton1Msg.DEFAULT_SIZE);
                return;
            }
		}
        else
        {
			throw new IllegalArgumentException(getTypeName() + " - illegal xmlElement: " + modelElement.asXML());
		}
    }

    public void saveResults()
    {
        if (_resultsFileName.equals(""))
        {
            saveAsResults();
        }
    }

    public boolean isLoaded() {
		return _ifaceLoaded;
	}
    // asks server what the proper interface state is
	public void synchronizeInterfaceState() {
		String ifaceState = RuntimeFunctionsClient.getInterfaceStatus(_serverConnection, _runtimeId);
		if (ModelRuntime.STATUS_IFACE_CREATED.equals(ifaceState)) {
			_ifaceLoaded = true;
			Debug.trace(Debug.ALL, "interface loaded: " + getName());
		} else {
			Debug.trace(Debug.ALL, getName() + " state: " + ifaceState);
		}
		firePropertyChange(INTERFACEMODELSTATUS, null, ifaceState);
	}

    public void saveAsResults()
    {
        String newFileName = RunMode.runFileChooser.showSaveDialog(RunMode.getCurrentModelFrame(),
                AnalysisToolUtils.INTERFACE_RESULTS_FILE, new File(_resultsFileName));
        if (newFileName == null)
			return;

		newFileName = fixFileName(newFileName, getInterfaceResultsExtension());

        if (newFileName.equalsIgnoreCase(_resultsFileName))
        { // same file as before
            String msg = "Can not save the new results file in the original file.  Choose a different file name.";
            OneButton1Msg.showWarning(null, "Warning: Save As", msg, "OK", new Dimension(230, 80));
            return;
        }
		else
        {
            // check if file already exists
            File file = new File(newFileName);
            if (file.exists())
            {
                String msg = null;
                msg = "File <" + file.getName() + "> already exists. Replace it?";
                int button = TwoButton1Msg.showOption(null,
                        "Warning: File exists", msg, "Replace",
                        "Cancel", new Dimension(230, 80));
                if (button == 0) return;
            }
            if (_resultsFileName.equals(""))
                saveResultsFile(newFileName);
            else
                setShouldSaveResultsFile(true);
        }
    }

    private String getInterfaceResultsExtension()
    {
        return "RESULTS";
    }

    private void saveResultsFile(String newFileName)
    {
        Document doc = DocumentFactory.getInstance().createDocument();
		Element xmlElement = resultsToXmlElement();
		doc.add(xmlElement);

        try
        {
            save(doc, newFileName);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

    }

    private Element resultsToXmlElement()
    {
        Element xml = DocumentHelper.createElement(INTERFACE_RESULTS);
        xml.addAttribute("name", getName());
        xml.addAttribute("id", getRuntimeId().getInterfaceStaticId());

        Element variables = DocumentHelper.createElement("variables");
        Element objectives = DocumentHelper.createElement("objectives");
        Element ranks = DocumentHelper.createElement("ranks");

        xml.add(variables);
        xml.add(objectives);
        xml.add(ranks);

        Iterator varIter = _interfaceVariableMap.keySet().iterator();
        while (varIter.hasNext())
        {
            Object object = varIter.next();
            if (object instanceof InterfaceParameterClient)
            {
                Parameter p = (Parameter) object;
                if (_parameterHistory.containsKey(p.getId().toString()))
                {
                    Element parameterElement = DocumentHelper.createElement("parameter");
                    variables.add(parameterElement);
                    parameterElement.addAttribute("id", p.getId().toString());
                    parameterElement.addAttribute("name", p.getName());
                    Element values = DocumentHelper.createElement("values");
                    parameterElement.add(values);
                    Vector vector = (Vector) _parameterHistory.get(p.getId().toString());
                    values.addText(vector.toString());
                }
            }
        }

        Iterator objIter = _interfaceObjectiveMap.keySet().iterator();
        while (objIter.hasNext())
        {
            Object object = objIter.next();
            if (object instanceof InterfaceParameterClient)
            {
                Parameter p = (Parameter) object;
                if (_parameterHistory.containsKey(p.getId().toString()))
                {
                    Element parameterElement = DocumentHelper.createElement("parameter");
                    objectives.add(parameterElement);
                    parameterElement.addAttribute("id", p.getId().toString());
                    parameterElement.addAttribute("name", p.getName());
                    Element values = DocumentHelper.createElement("values");
                    parameterElement.add(values);
                    Vector vector = (Vector) _parameterHistory.get(p.getId().toString());
                    values.addText(vector.toString());
                }
            }
        }

        ranks.addText(_individualRanks.toString());

        return xml;
    }

    public static String fixFileName(String fileName, String resExt)
	{
		// validate and fix the file name
		String properModelExt = "-" + resExt + ".rti";
		String fileModelExt = DomeFileChooser.getAnalysisToolInterfaceResultsExtension(fileName);
		if (fileModelExt == null) {
			String fileExt = DomeFileChooser.getExtension(fileName);
			if (fileExt == null)
				fileName = fileName + properModelExt;
			else if (fileExt.equals("rti"))
				fileName = fileName.substring(0, fileName.length() - 4) + properModelExt;
			else
				fileName = fileName + properModelExt;
		} else if (!fileModelExt.equalsIgnoreCase(properModelExt)) {
			fileName = fileName.substring(0, fileName.length() - (fileModelExt.length() + 1)) + properModelExt;
		}
		return fileName;
	}

    public int getNumberOfListeners()
    {
        return _numberOfListeners;
    }

    public void listenerAdded()
    {
        _numberOfListeners++;
    }

    public void listenerRemoved()
    {
        _numberOfListeners--;
    }

    public Vector getVariableMaps()
    {
        return _variableMaps;
    }

    public Vector getObjectiveMaps()
    {
        return _objectiveMaps;
    }

    public void setShouldSaveResultsFile(boolean shouldSave)
	{
		_shouldSaveResultsFile = shouldSave;
	}

	public boolean getShouldSaveResultsFile()
	{
		return _shouldSaveResultsFile;
	}
}
