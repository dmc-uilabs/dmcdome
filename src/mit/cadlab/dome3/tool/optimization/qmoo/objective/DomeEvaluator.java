package mit.cadlab.dome3.tool.optimization.qmoo.objective;

import org.goof.rts.AnyMap;
import org.goof.qmoo.EvaluatorData;
import org.goof.qmoo.Individual;
import org.apache.xmlrpc.XmlRpcException;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.api.DomeProject;
import mit.cadlab.dome3.api.DomeFolder;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.db.DbErrors;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 25, 2004
 * Time: 1:32:46 PM
 * To change this template use Options | File Templates.
 */
public class DomeEvaluator
{

    private OptimizationToolRuntime _analysisTool;

    private List _inputParametersList;  // list of input parameters from the underlying project
    private List _outputParametersList; // list of output parameters from the underlying project
    private List _variableParameters;   // list of optimisation variable parameters
    private List _objectiveParameters;  // list of optimisation objective parameters

    private List _sortedVariables;
    private List _sortedObjectives;

    private JFrame _controlGUI;

    private int _processCount = 0;

    private ProjectThread _projectThread = null;

    public DomeEvaluator(OptimizationToolRuntime analysisTool)
    {
        _analysisTool = analysisTool;
        createControlGUI();
        obtainVariablesAndObjectives();
    }

    public void start(AnyMap root, AnyMap objective, AnyMap space)
    {
        ListIterator iterator = _variableParameters.listIterator();

        objective.setInt("nobjs", _objectiveParameters.size());
        objective.setInt("nvars", _variableParameters.size());

        for (int i = 0; iterator.hasNext(); i++)
        {
            VariableParameter parameter = (VariableParameter) iterator.next();
            String parameterType = null;

            if (parameter.getParameter().getCurrentDataObject().getTypeName().equals(DomeReal.TYPE_INFO.getTypeName()))
                parameterType = "real";
            else if (parameter.getParameter().getCurrentDataObject().getTypeName().equals(DomeInteger.TYPE_INFO.getTypeName()))
                parameterType = "integer";

            space.setString("[" + i + "].type", parameterType);
            space.setDouble("[" + i + "].low", parameter.getLowerLimit().getValue());
            space.setDouble("[" + i + "].high", parameter.getUpperLimit().getValue());
            space.setInt("[" + i + "].index", i);
        }
    }

    public void process(EvaluatorData e)
    {
        if (_projectThread == null)
        {
            if (e.count() > 0)
            {
                _projectThread = new ProjectThread(e.next());
                Thread thread = new Thread(_projectThread);
                thread.start();
            }
        }
        else
        {
            if(_projectThread.isProjectThreadDone())
            {
                e.done(_projectThread.getCurrentIndividual(), _projectThread.isIndividualFeasible());
                _projectThread = null;
            }
        }
    }

    protected void createControlGUI()
    {
        _controlGUI = new JFrame("Manual Project Abort");
        _controlGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _controlGUI.getContentPane().add(new JLabel("Click to manually abort project"), BorderLayout.NORTH);

        JButton b = new JButton("OK");
        b.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                _projectThread.markProjectIsDone();
            }
        });

        _controlGUI.getContentPane().add(b, BorderLayout.SOUTH);

        _controlGUI.pack();
        _controlGUI.show();
    }

    public void deleteControlGUI()
    {
        _controlGUI.dispose();
    }

    protected void obtainVariablesAndObjectives()
    {
        ConnectionMappingManager mgr = _analysisTool.getMappingManager();

        _variableParameters = new ArrayList(_analysisTool.getOptimizationToolVariableParameterMap().values());
        List variableParameterKeys = new ArrayList(_analysisTool.getOptimizationToolVariableParameterMap().keyList());
        ListIterator variableIterator = variableParameterKeys.listIterator();
        _inputParametersList = new ArrayList();

        while (variableIterator.hasNext())
        {
            Object obj = variableIterator.next();
            if (obj instanceof ModelParameterRuntime)
            {
                VariableParameter variableParameter = (VariableParameter)_analysisTool.getOptimizationToolVariableParameterMap().get(obj);
                if (_analysisTool.isVariableParameterInInterface(variableParameter) && variableParameter.getIsActive().getValue())
                {
                    Collection mappings = mgr.getMappingsForParameter((Parameter) obj);
                    if (!mappings.isEmpty())
                    {
                        Parameter targetParameter = (Parameter) mappings.toArray()[0];
                        if (targetParameter.getScope() instanceof DefaultSubscription)
                        {
                            DefaultSubscription def = (DefaultSubscription) targetParameter.getScope();
                            targetParameter = def.getInterfaceParameter(targetParameter.getId().toString());
                        }
                        _inputParametersList.add(targetParameter);
                    }
                }
                else
                    _variableParameters.remove(variableParameter);
            }
            else
                OneButton1Msg.showError(null, "run mode", "error: unknown parameter type " + ClassUtils.getClassName(obj), "ok", OneButton1Msg.DEFAULT_SIZE);
        }

        _objectiveParameters = new ArrayList(_analysisTool.getOptimizationToolObjectiveParameterMap().values());
        List objectiveParameterKeys = new ArrayList(_analysisTool.getOptimizationToolObjectiveParameterMap().keyList());
        ListIterator objectiveIterator = objectiveParameterKeys.listIterator();
        _outputParametersList = new ArrayList();

        while (objectiveIterator.hasNext())
        {
            Object obj = objectiveIterator.next();
            if (obj instanceof ModelParameterRuntime)
            {
                ObjectiveParameter objectiveParameter = (ObjectiveParameter)_analysisTool.getOptimizationToolObjectiveParameterMap().get(obj);
                if (_analysisTool.isObjectiveParameterInInterface(objectiveParameter) && objectiveParameter.getIsActive().getValue())
                {
                    Collection mappings = mgr.getMappingsForParameter((Parameter) obj);
                    if (!mappings.isEmpty())
                    {
                        Parameter targetParameter = (Parameter) mappings.toArray()[0];
                        if (targetParameter.getScope() instanceof DefaultSubscription)
                        {
                            DefaultSubscription def = (DefaultSubscription) targetParameter.getScope();
                            targetParameter = def.getInterfaceParameter(targetParameter.getId().toString());
                        }
                        _outputParametersList.add(targetParameter);
                    }
                }
                else
                    _objectiveParameters.remove(objectiveParameter);
            }
            else
                OneButton1Msg.showError(null, "run mode", "error: unknown parameter type " + ClassUtils.getClassName(obj), "ok", OneButton1Msg.DEFAULT_SIZE);
        }

    }

    public void sortModelInterfaceParameters()
    {
        _sortedVariables = new ArrayList();
        _sortedObjectives = new ArrayList();
        ListIterator vIter = _inputParametersList.listIterator();

        while (vIter.hasNext())
        {
            Object obj = vIter.next();
            if (obj instanceof Parameter)
            {
                Parameter p = interfaceParameterFromProjectParameter((Parameter)obj);
                if (p != null)
                    _sortedVariables.add(p);
            }
        }

        ListIterator oIter = _outputParametersList.listIterator();

        while (oIter.hasNext())
        {
            Object obj = oIter.next();
            if (obj instanceof Parameter)
            {
                Parameter p = interfaceParameterFromProjectParameter((Parameter)obj);
                if (p != null)
                    _sortedObjectives.add(p);
            }
        }
    }

    private Parameter interfaceParameterFromProjectParameter(Parameter p)
    {
        Collection c = null;
        Object obj = null;
        ConnectionMappingManager mgr = _analysisTool.getMappingManager();

        if (p.getScope() instanceof SubscriptionInterface)
        {
            Subscription s = ((SubscriptionInterface)p.getScope()).getSubscription();
            if (s instanceof DefaultSubscription)
            {
                DefaultSubscription def = (DefaultSubscription) s;
                Iterator subscriptionParameterIds = def.getParamIdMap().keySet().iterator();
                while (subscriptionParameterIds.hasNext())
                {
                    String subscriptionParameterId = (String) subscriptionParameterIds.next();
                    if(def.getParamIdMap().get(subscriptionParameterId).equals(p.getId().toString()))
                    {
                        p = (Parameter)def.getModelObjectById(new Id(subscriptionParameterId));
                        c = mgr.getMappingsForParameter(p);
                        continue;
                    }
                }
            }
        }
        else
            c = mgr.getMappingsForParameter(p);

        List l = new ArrayList(c);
        for (Iterator iter = l.iterator(); iter.hasNext();)
        {
            obj = iter.next();
            if (obj instanceof Parameter)
            {
                Parameter p1 = (Parameter) obj;
                Collection c1 = mgr.getInterfaceConnections(p1);
                List interfaceMappings = new ArrayList(c1);
                for (Iterator iterator = interfaceMappings.iterator(); iterator.hasNext();)
                {
                    obj = iterator.next();
                    if (obj instanceof Parameter)
                    {
                        return (Parameter) obj;
                    }
                }
            }
        }

        return null;
    }

    public List getObjectives()
    {
        return _objectiveParameters;
    }

    public List getVariables()
    {
        return _variableParameters;
    }

    public List getInputParameters()
    {
        return _inputParametersList;
    }

    public List getOutputParameters()
    {
        return _outputParametersList;
    }

    public List getSortedVariables()
    {
        return _sortedVariables;
    }

    public List getSortedObjectives()
    {
        return _sortedObjectives;
    }

    public int getProcessCount()
    {
        return _processCount;
    }

    class ProjectThread implements Runnable
    {
        private boolean _isProjectDone = false;
        private boolean _isIndividualFeasible = false;
        private String _analysisToolPath;
        private Individual _currentIndividual;
        private ProjectStatusListener _listener;

        public ProjectThread(Individual ind)
        {
            System.out.println("*********** processing individual " + ++_processCount + " ***********");
            _currentIndividual = ind;
            _listener = new ProjectStatusListener();
        }

        public void run()
        {

            IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) _analysisTool.getIntegrationProject();
            project.addPropertyChangeListener(ModelRuntime.RUN_STATUS, _listener);

            ListIterator variableIterator = _inputParametersList.listIterator();

            for (int i = 0; variableIterator.hasNext(); i++)
            {
                Object obj = variableIterator.next();
                if (obj instanceof ModelParameterRuntime)
                {
                    DataObject domeObject = ((ModelParameterRuntime) obj).getCurrentDataObject();

                    if (domeObject instanceof DomeReal)
                    {
                        System.out.println("variable " + i + " (" + ((ModelParameterRuntime) obj).getName() +"): " + _currentIndividual.getVariable(i));
                        DomeReal dr = new RealData(_currentIndividual.getVariable(i), domeObject.getUnit());
                        ((ModelParameterRuntime)obj).setInitialValue(dr);
                    }
                }
            }
            project.startProject();
        }

        public void markProjectIsDone()
        {
            _isProjectDone = true;
        }

        public boolean isProjectThreadDone()
        {
            return _isProjectDone;
        }

        public Individual getCurrentIndividual()
        {
            return _currentIndividual;
        }

        public boolean isIndividualFeasible()
        {
            return _isIndividualFeasible;
        }

        class ProjectStatusListener implements PropertyChangeListener
        {
            public void propertyChange(PropertyChangeEvent e)
            {
                String projectStatus = (String)e.getNewValue();
                if (projectStatus.equals(ModelRuntime.STATUS_DONE))
                    setObjectivesInIndividual();
                else if (projectStatus.equals(ModelRuntime.STATUS_ABORTED))
                {
                    IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime)_analysisTool.getIntegrationProject();
                    project.removePropertyChangeListener(ModelRuntime.RUN_STATUS, _listener);
                    _isProjectDone = true;
                }
            }

            public void setObjectivesInIndividual()
            {
                ListIterator objectiveIterator = _outputParametersList.listIterator();

                for (int j = 0; objectiveIterator.hasNext(); j++)
                {
                    Object obj = objectiveIterator.next();
                    Object object = _objectiveParameters.get(j);
                    String maxOrMin = ((ObjectiveParameter)object).getIsMaxOrMinForEditor();
                    if (obj instanceof ModelParameterRuntime)
                    {
                        DataObject domeObject = ((ModelParameterRuntime) obj).getCurrentDataObject();
                        if (domeObject instanceof DomeReal)
                        {
                            System.out.println("objective " + j + " (" + ((ModelParameterRuntime) obj).getName() + "): " + ((DomeReal) domeObject).getValue());
                            if (maxOrMin != null && maxOrMin.equals(ObjectiveParameter.MAXIMIZE))
                                _currentIndividual.setObjective(j,(-1.0)*((DomeReal) domeObject).getValue());
                            else
                                _currentIndividual.setObjective(j, ((DomeReal)domeObject).getValue());
                        }
                    }
                }

                IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime)_analysisTool.getIntegrationProject();
                project.removePropertyChangeListener(ModelRuntime.RUN_STATUS, _listener);
                _isIndividualFeasible = true;
                _isProjectDone = true;
            }
        }
    }
}
