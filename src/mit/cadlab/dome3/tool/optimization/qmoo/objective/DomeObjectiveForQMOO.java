package mit.cadlab.dome3.tool.optimization.qmoo.objective;

import org.goof.rts.AnyMap;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.util.ClassUtils;

import java.util.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Dec 25, 2003
 * Time: 2:29:56 PM
 * To change this template use Options | File Templates.
 */
public class DomeObjectiveForQMOO
{
    private OptimizationToolRuntime _analysisTool;

    private List _inputParametersList;  // list of input parameters from the underlying project
    private List _outputParametersList; // list of output parameters from the underlying project
    private List _variableParameters;   // list of optimisation variable parameters
    private List _objectiveParameters;  // list of optimisation objective parameters

    private List _sortedVariables;
    private List _sortedObjectives;

    public DomeObjectiveForQMOO(OptimizationToolRuntime analysisTool)
    {
        _analysisTool = analysisTool;
        obtainVariablesAndObjectives();

    }

    public void start(AnyMap obj, AnyMap space)
    {
        ListIterator iterator = _variableParameters.listIterator();

        obj.setInt("nobjs", _objectiveParameters.size());
        obj.setInt("nvars", _variableParameters.size());

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

    public boolean evaluate(double[] vars, double[] objs)
    {

//        ListIterator variableIterator = _inputParametersList.listIterator();
//        ListIterator objectiveIterator = _outputParametersList.listIterator();
//
//        for (int i=0; variableIterator.hasNext(); i++)
//        {
//            Object obj = variableIterator.next();
//            if (obj instanceof ModelParameterRuntime)
//            {
//                DataObject domeObject = ((ModelParameterRuntime)obj).getCurrentDataObject();
//
//                if (domeObject instanceof DomeReal)
//                    ((DomeReal)domeObject).setValue(vars[i]);
//                else if (domeObject instanceof DomeInteger)
//                    ((DomeInteger)domeObject).setValue((int)vars[i]);
//            }
//        }
//
//        List iModels = _analysisTool.getIntegrationProject().getIntegrationModels();
//        DomeModelRuntime model = (DomeModelRuntime) ((ProjectIntegrationModelInfoRuntime) iModels.get(0)).getModel();
//        model.startModel(solvingLog);
//
//        JOptionPane.showMessageDialog(null, "Is it ok to continue?");

//        for (int j = 0; j < objs.length; j++)
//        {
//            Object obj = objectiveIterator.next();
//            if (obj instanceof ModelParameterRuntime)
//            {
//                DataObject domeObject = ((ModelParameterRuntime) obj).getCurrentDataObject();
//                if (domeObject instanceof DomeReal)
//                    objs[j] = ((DomeReal) domeObject).getValue();
//                else if (domeObject instanceof DomeInteger)
//                    objs[j] = ((DomeInteger) domeObject).getValue();
//            }
//        }

        objs[0] = vars[0] * vars[0];
        objs[1] = (vars[0] - 2.0) * (vars[0] - 2.0);
        return true;
    }

    protected void obtainVariablesAndObjectives()
    {
        ConnectionMappingManager mgr = _analysisTool.getMappingManager();

        _variableParameters = new ArrayList(_analysisTool.getOptimizationToolVariableParameterMap().values());
        List variableParameterKeys = new ArrayList(_analysisTool.getOptimizationToolVariableParameterMap().keySet());
        ListIterator variableIterator = variableParameterKeys.listIterator();
        _inputParametersList = new ArrayList();

        while (variableIterator.hasNext())
        {
            Object obj = variableIterator.next();
            if (obj instanceof ModelParameterRuntime)
            {
                Collection mappings = mgr.getMappingsForParameter((Parameter)obj);
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
                OneButton1Msg.showError(null, "run mode", "error: unknow parameter type " + ClassUtils.getClassName(obj), "ok", OneButton1Msg.DEFAULT_SIZE);
        }

        _objectiveParameters = new ArrayList(_analysisTool.getOptimizationToolObjectiveParameterMap().values());
        List objectiveParameterKeys = new ArrayList(_analysisTool.getOptimizationToolObjectiveParameterMap().keySet());
        ListIterator objectiveIterator = objectiveParameterKeys.listIterator();
        _outputParametersList = new ArrayList();

        while (objectiveIterator.hasNext())
        {
            Object obj = objectiveIterator.next();
            if (obj instanceof ModelParameterRuntime)
            {
                Collection mappings = mgr.getMappingsForParameter((Parameter)obj);
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
                OneButton1Msg.showError(null, "run mode", "error: unknow parameter type " + ClassUtils.getClassName(obj), "ok", OneButton1Msg.DEFAULT_SIZE);
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

    private Parameter interfaceParameterFromProjectParameter(Parameter p)
    {
        Object obj = null;
        ConnectionMappingManager mgr = _analysisTool.getMappingManager();

        Collection c = mgr.getMappingsForParameter(p);
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

    public List getSortedVariables()
    {
        return _sortedVariables;
    }

    public List getSortedObjectives()
    {
        return _sortedObjectives;
    }
}
