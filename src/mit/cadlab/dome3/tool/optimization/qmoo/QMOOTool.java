package mit.cadlab.dome3.tool.optimization.qmoo;

import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.tool.AnalysisTool;
import mit.cadlab.dome3.tool.optimization.qmoo.monitor.DomeMonitor;
import mit.cadlab.dome3.tool.optimization.qmoo.objective.DomeEvaluator;
import org.goof.qmoo.Optimiser;
import org.goof.rts.AnyMap;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Feb 27, 2003
 * Time: 4:09:41 PM
 * To change this template use Options | File Templates.
 */


public class QMOOTool implements AnalysisTool
{
    private OptimizationToolRuntime _analysisTool;
    private AnyMap _qMap;
    private DomeEvaluator _objective;
    private DomeMonitor _monitor;
    private Optimiser _o;
    private Vector _variableIds, _objectiveIds;

    private Integer _monitorInterval = null;

    private boolean _isModelLoaded = false;


    public QMOOTool(OptimizationToolRuntime analysisTool)
    {
        _analysisTool = analysisTool;
        _variableIds = new Vector();
        _objectiveIds = new Vector();
    }

    public void createModel()
    {
        _monitor = new DomeMonitor(this);
    }

    public void loadModel()
    {
        _objective = new DomeEvaluator(_analysisTool);
        orderVariablesAndObjectives();
        _qMap = ((QMOOConfiguration) _analysisTool.getToolConfiguration()).createMap();
        specifyJavaObjects();
        _o = new Optimiser(_qMap);

        _isModelLoaded = true;
    }

    public void execute()
    {
        _o.run();
    }

    public boolean isModelLoaded()
    {
        return _isModelLoaded;
    }

    public void unloadModel()
    {
        _isModelLoaded = false;
    }

    public void deleteModel()
    {
        if (_objective != null)
        {
            _objective.deleteControlGUI();
        }
    }

    private void specifyJavaObjects()
    {
        _qMap.setObject("objective.java.object", _objective);
        _qMap.setObject("monitors[1].java.object", _monitor);

        if (_monitorInterval != null)
            _qMap.setInt("monitors[1].interval", _monitorInterval.intValue());
    }

    public void importInterfaceConfiguration(Integer value)
    {
        _monitorInterval = value;
    }

    public DomeEvaluator getDomeObjectiveForQMOO()
    {
        return _objective;
    }

    public void sendIndividualToClient(Vector isRankOne, Vector variables, Vector objectives)
    {
        Hashtable variableTable = new Hashtable();
        Hashtable objectiveTable = new Hashtable();

        List sortedVars = _objective.getSortedVariables();
        List sortedObjs = _objective.getSortedObjectives();

        for (int i = 0; i < sortedVars.size(); i++)
        {
            Parameter p = (Parameter) sortedVars.get(i);
            variableTable.put(p.getId().toString(), variables.get(i));
        }

        for (int i = 0; i < sortedObjs.size(); i++)
        {
            Parameter p = (Parameter) sortedObjs.get(i);
            objectiveTable.put(p.getId().toString(), objectives.get(i));
        }

        _analysisTool.sendIndividualToClient(Vectors.create(isRankOne, variableTable, objectiveTable));

        /**
         * this method call will tell the client that the optimization has finished executing
         * this will be useful (for example) for the API to determine when to close the server connection
         */
        if (_objective.getProcessCount() == ((DomeInteger)_analysisTool.getToolConfiguration().
                getSetupParameter(QMOOConfiguration.NUMBER_OF_EVALUATIONS).getCurrentDataObject()).getValue())
            signalClientOptimizationAnalysisIsComplete();
    }

    protected void signalClientOptimizationAnalysisIsComplete()
    {
        _analysisTool.signalClientOptimizationAnalysisIsComplete();
    }

    public void startNewIterationRecord()
    {
        _analysisTool.preparePlotForNextGeneration();
    }

    protected void orderVariablesAndObjectives()
    {
        for (Iterator iter = _objective.getVariables().listIterator(); iter.hasNext();)
        {
            VariableParameter variable = (VariableParameter) iter.next();
            _variableIds.add(variable.getParameter().getId().toString());
        }

        for (Iterator iter = _objective.getObjectives().listIterator(); iter.hasNext();)
        {
            ObjectiveParameter objective = (ObjectiveParameter) iter.next();
            _objectiveIds.add(objective.getParameter().getId().toString());
        }
    }
}
