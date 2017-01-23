package mit.cadlab.dome3.objectmodel.toolinterface.optimization.run;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.AbstractOptimizationToolInterfaceRuntime;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.util.AggregatorMap;
import org.dom4j.Element;

import java.util.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 25, 2003
 * Time: 7:01:20 PM
 * To change this template use Options | File Templates.
 */

/**
 * OptimizationToolInterfaceRuntimeServer
 * This class is used to construct a run time object of the optimization tool
 * interface on the server side.
 */

public class OptimizationToolInterfaceRuntimeServer extends AbstractOptimizationToolInterfaceRuntime
{
    private int referenceCount = 0;

    private AggregatorMap _modelParameterToInterfaceParameterMap = null;

	public OptimizationToolInterfaceRuntimeServer(Model m, Id id)
	{
		super(m, id);
		_runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
	}

	public OptimizationToolInterfaceRuntimeServer(Model m, String id)
	{
		super(m, id);
		_runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
	}

	public OptimizationToolInterfaceRuntimeServer(Model m, Id id, ModelObjectScope mObjScope)
	{
		super(m, id, mObjScope);
		_runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
	}

	public OptimizationToolInterfaceRuntimeServer(Model m, Element xmlContent)
	{
		this(null, m, xmlContent, null);
		_runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
	}

	public OptimizationToolInterfaceRuntimeServer(CompoundId parentId, Model m, Element xmlContent, Element xmlMappings)
	{
		super(m, xmlContent, xmlMappings);
		if (parentId != null)
			_runtimeId = new CompoundId(parentId);
		_runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
	}

	public void capture ()
	{
		++referenceCount;
	}

	public int release ()
	{
		return --referenceCount;
	}

    public Hashtable getParameterSystemCausality()
    {
        Hashtable ht = new Hashtable();
        return ht;
    }

    /**
	 * Set one or more parameters data object values.
	 * @param objectId Id of the parameter
	 * @param args Argument list
	 */
	public void setItem(Id objectId, List args)
	{
		Parameter mObj = (Parameter) _interfaceObjectsFlatMap.get(objectId);
		DataObject dObj = mObj.getCurrentDataObject();
		dObj.setValues(args);
	}

	public void setItemStatus(Id objectId, String newStatus)
	{
		Parameter mObj = (Parameter) _interfaceObjectsFlatMap.get(objectId);
		mObj.setValueStatus(newStatus);
	}

	protected Parameter getModelParam(Parameter p)
	{
		return p;
	}

    public AggregatorMap getModelParameterToInterfaceParameterMap()
    {
        if (_modelParameterToInterfaceParameterMap != null)
            return _modelParameterToInterfaceParameterMap;
        else
        {
            _modelParameterToInterfaceParameterMap = new AggregatorMap();
            Model model = getModel();
            // get model objects mapping manager
            ConnectionMappingManager mManager = null;
            mManager = ((AnalysisToolBase) model).getMappingManager();

            // create hash map btn objects in model and model objects in interface
            Collection intefaceObj = this.getModelObjects();
            for (Iterator iterator = intefaceObj.iterator(); iterator.hasNext();)
            {
                Object iObj = iterator.next();
                if (iObj instanceof Parameter)
                {
                    List mapping = new ArrayList(mManager.getMappingsForParameter((Parameter) iObj));

                    if (mapping.size() > 1)
                        throw new IllegalArgumentException("Error getting mapping for parameter " + iObj);
                    else if (!(mapping == null || mapping.size() == 0))
                    {
                        Object modelParam = mapping.get(0);
                        _modelParameterToInterfaceParameterMap.put(modelParam, iObj);
                    }
                }
            }
            return _modelParameterToInterfaceParameterMap;
        }
    }

    public void setParameterAttributes(Hashtable parameterTable)
    {
        Iterator parameterIds = parameterTable.keySet().iterator();
        while (parameterIds.hasNext())
        {
            Id parameterId = new Id((String) parameterIds.next());
            Vector v = (Vector) parameterTable.get(parameterId.toString());
            if (_interfaceObjectsFlatMap.containsKey(parameterId))
            {
                Parameter p = (Parameter) _interfaceObjectsFlatMap.get(parameterId);
                ConnectionMappingManager mgr = ((OptimizationToolRuntime)_model).getMappingManager();
                Collection c  = mgr.getInterfaceConnections(p);
                Collection c2 = mgr.getMappingsForParameter(p);
                Iterator modelParameters = mgr.getMappingsForParameter(p).iterator();
                while (modelParameters.hasNext())
                {
                    Parameter modelParameter = (Parameter) modelParameters.next();
                    if (((OptimizationToolRuntime)_model).getOptimizationToolVariableParameterMap().containsKey(modelParameter))
                    {
                        VariableParameter vp = (VariableParameter) ((OptimizationToolRuntime)_model).getOptimizationToolVariableParameterMap().get(modelParameter);
                        vp.setUpperLimit((Double)v.get(0));
                        vp.setLowerLimit((Double)v.get(1));
                        vp.setIsActive(new BooleanData((Boolean)v.get(2)));
                    }
                    else if (((OptimizationToolRuntime)_model).getOptimizationToolObjectiveParameterMap().containsKey(modelParameter))
                    {
                        ObjectiveParameter op = (ObjectiveParameter) ((OptimizationToolRuntime)_model).getOptimizationToolObjectiveParameterMap().get(modelParameter);
                        op.setIsActive(new BooleanData((Boolean)v.get(0)));
                    }
                }
            }
        }
    }

    public boolean isInterfaceParameterMappedToModelParameter(Parameter parameter)
    {
        return getModelParameterToInterfaceParameterMap().containsKey(parameter);
    }


}
