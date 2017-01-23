// ConnectionMappingManagerBuild.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.mapping;

import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.exceptions.BuildMappingValuePropagationException;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelinterface.InterfaceModelView;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.DataObjectChangeListener;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.util.DSet;

import java.util.Collection;
import java.awt.Dimension;

public class ConnectionMappingManagerBuild extends ConnectionMappingManager
{
	// Note: ToolInterface mappings are still done the old way using ValueShadowListeners

	// structures for making mapping "live" in build mode
	private ModelObjectListener mObjListener; // propagates value changes to mapped parameters

	public ConnectionMappingManagerBuild(Model model)
	{
		super(model);
		mObjListener = new ModelObjectListener();
	}

	/**
	 * Implement this method to process the mapping after it has been added successfully
	 * @param p Model object
	 * @param target Model object
	 */
	protected void addModelMappingHook(Parameter p, Parameter target)
	{
		// add shadow listeners
		((ConcreteParameter)p).addChangeListener(mObjListener);
		((ConcreteParameter) target).addChangeListener(mObjListener);

		// add other listeners
		String key = p.getId().getIdString() + target.getId().getIdString();
		if (listenermap.get(key) == null) {
			//when target name changes change mapping column of p
			NameListener nlSource = new ParameterNameListener(p);
			target.addPropertyChangeListener(NameListener.NAME, nlSource);
			//and vice versa
			NameListener nlTarget = new ParameterNameListener(target);
			p.addPropertyChangeListener(NameListener.NAME, nlTarget);
			Object[] listeners = new Object[]{nlSource, nlTarget};
			listenermap.put(key, listeners);
		}
	}

	protected void addModelMappingHook(Visualization p, Visualization target)
	{
		// add shadow listeners
        p.addPropertyChangeListener(target.getValueShadowListener());
		target.addPropertyChangeListener(p.getValueShadowListener());

		// add other listeners
		String key = p.getId().getIdString() + target.getId().getIdString();
		if (listenermap.get(key) == null) {
			//when target name changes change mapping column of p
			NameListener nlSource = new VisualizationNameListener(p);
			target.addPropertyChangeListener(NameListener.NAME, nlSource);
			//and vice versa
			NameListener nlTarget = new VisualizationNameListener(target);
			p.addPropertyChangeListener(NameListener.NAME, nlTarget);
			Object[] listeners = new Object[]{nlSource, nlTarget};
			listenermap.put(key, listeners);
		}
	}

	/**
	 * Implement this method to process the mapping after it has been added successfully
	 * @param p Interface object
	 * @param target Model object
	 */
	protected void addInterfaceMappingHook(Parameter p, Parameter target)
	{
		// add shadow listener
		((ConcreteParameter) target).addChangeListener(mObjListener);

		DataObject pObj = p.getCurrentDataObject();
		DataObject tgtObj = target.getCurrentDataObject();
		ModelObjectScope scope = getObjectTopScope(p);
		if (scope instanceof ModelInterfaceBuilder)
        {
            addNameListener(p, target);
        }
        else if (scope instanceof AnalysisToolInterfaceBase)
        {
            AnalysisToolInterfaceBase iface = (AnalysisToolInterfaceBase) scope;
            tgtObj.addPropertyChangeListener(pObj.getValueUnitShadowListener());
            addNameListener(p, target);
            addOptimizationParameterListeners(p, target);
        }
		else if (scope instanceof InterfaceModelView)
        {
            addNameListener(p, target);
        }
	}

	protected void addInterfaceMappingHook(Visualization p, Visualization target)
	{
		// add shadow listener
		ModelObjectScope scope = getObjectTopScope(p);
		if (scope instanceof ModelInterfaceBuilder)
        {
            ModelInterfaceBuilder iface = (ModelInterfaceBuilder) scope;
            if (iface.isDefaultInterface())
            {
                target.addPropertyChangeListener(p.getValueUnitShadowListener());
            }
            else
                target.addPropertyChangeListener(p.getValueShadowListener());
            addNameListener(p, target);
        }
        else if (scope instanceof AnalysisToolInterfaceBase)
        {
            AnalysisToolInterfaceBase iface = (AnalysisToolInterfaceBase) scope;
            target.addPropertyChangeListener(p.getValueUnitShadowListener());
            addNameListener(p, target);
        }
		else if (scope instanceof InterfaceModelView)
        {
            InterfaceModelView iface = (InterfaceModelView) scope;
            if (iface.isDefaultInterface())
            {
                target.addPropertyChangeListener(p.getValueUnitShadowListener());
            }
            else
                target.addPropertyChangeListener(p.getValueShadowListener());
            addNameListener(p, target);
        }
	}

	private void addNameListener(Parameter p, Parameter target) {
			String key = p.getId().getIdString() + target.getId().getIdString();
			if (listenermap.get(key) == null) {
				//when target name changes change mapping column of p
				NameListener l = new ParameterNameListener(p);
				target.addPropertyChangeListener(NameListener.NAME, l);
				Object[] listeners = new Object[]{l};
				listenermap.put(key, listeners);
			}
	}

	private void addNameListener(Visualization p, Visualization target) {
			String key = p.getId().getIdString() + target.getId().getIdString();
			if (listenermap.get(key) == null) {
				//when target name changes change mapping column of p
				NameListener l = new VisualizationNameListener(p);
				target.addPropertyChangeListener(NameListener.NAME, l);
				Object[] listeners = new Object[]{l};
				listenermap.put(key, listeners);
			}
	}

    /**
     * method used to add optimization parameter listeners
     * @param iP (interface parameter)
     * @param tP (analysis tool  parameter)
     */
    private void addOptimizationParameterListeners(Parameter iP, Parameter tP)
    {
        // assumes that the parameters are of optimization tool scope
        //todo: do the scope checking before this method is called
        OptimizationInterfaceBuild tface = (OptimizationInterfaceBuild)iP.getScope();
        OptimizationToolBuild tool = (OptimizationToolBuild) tP.getScope();

        if(tool.getOptimizationToolVariableParameterMap().containsKey(tP))
        {
            VariableParameter v = (VariableParameter)tool.getOptimizationToolVariableParameterMap().get(tP);
            VariableParameter iV = (VariableParameter)tface.getInterfaceVariableMap().get(iP);
            v.getUpperLimit().addPropertyChangeListener(iV.getUpperLimit().getValueUnitShadowListener());
            v.getLowerLimit().addPropertyChangeListener(iV.getLowerLimit().getValueUnitShadowListener());
        }
        else if (tool.getOptimizationToolObjectiveParameterMap().containsKey(tP))
        {
            ObjectiveParameter o = (ObjectiveParameter)tool.getOptimizationToolObjectiveParameterMap().get(tP);
            ObjectiveParameter iO = (ObjectiveParameter)tface.getInterfaceObjectiveMap().get(iP);
            o.getIsMaxOrMin().addPropertyChangeListener(iO.getIsMaxOrMin().getValueUnitShadowListener());
        }
    }



	/**
	 * Implement this method to process the mapping after it has been removed successfully
	 * @param p
	 * @param target
	 */
	protected void removeMappingHook(Parameter p, Parameter target)
	{
		removeModelObjectListener(p);
		removeModelObjectListener(target);

		//remove mapping name change listeners
		String key = p.getId().getIdString() + target.getId().getIdString();
		Object obj = listenermap.get(key);
		if(obj == null) {
			key = target.getId().getIdString() + p.getId().getIdString();
			obj = listenermap.get(key);
		}
		if (obj != null) {
			// remove other listeners
			Object[] listeners = (Object[]) obj;
			if (listeners.length == 2) {
				//remove target name listener
				NameListener l = (NameListener) listeners[0];
				target.removePropertyChangeListener(NameListener.NAME, l);
				//and then listener on p
				NameListener nl = (NameListener) listeners[1];
				p.removePropertyChangeListener(NameListener.NAME, nl);
			} else { //only 2 listeners
				NameListener l = (NameListener) listeners[0];
				target.removePropertyChangeListener(NameListener.NAME, l);
			}
			listenermap.remove(key);
		}
	}

	protected void removeModelObjectListener(Parameter p) {
		if (p.getScope() instanceof ModelInterface) // listeners only added to model (rel/sub) parameters
			return;
		Collection modelParamMappings = getMappingsForParameter(p);
		Collection ifaceParamMappings = getInterfaceConnections(p);
		// only remove listener if there are no more mappings for this object
		if ((modelParamMappings == null || modelParamMappings.isEmpty()) &&
		        (ifaceParamMappings == null || ifaceParamMappings.isEmpty()))
			((ConcreteParameter) p).removeChangeListener(mObjListener);
	}

	protected void removeMappingHook(Visualization p, Visualization target)
	{
		//remove mapping name change listeners
		String key = p.getId().getIdString() + target.getId().getIdString();
		Object obj = listenermap.get(key);
		if(obj == null) {
			key = target.getId().getIdString() + p.getId().getIdString();
			obj = listenermap.get(key);
		}
		if (obj != null) {
			// remove shadow listener from target
            target.removePropertyChangeListener(p.getValueShadowListener());

			// remove other listeners
			Object[] listeners = (Object[]) obj;
			if (listeners.length == 2) {
				// remove shadow listener from source
                p.removePropertyChangeListener(target.getValueShadowListener());

				//remove target name listener
				NameListener l = (NameListener) listeners[0];
				target.removePropertyChangeListener(NameListener.NAME, l);
				//and then listener on p
				NameListener nl = (NameListener) listeners[1];
				p.removePropertyChangeListener(NameListener.NAME, nl);
			} else { //only 2 listeners
				NameListener l = (NameListener) listeners[0];
				target.removePropertyChangeListener(NameListener.NAME, l);
			}
			listenermap.remove(key);
		}
	}

	class ModelObjectListener extends DataObjectChangeListener {
		public ModelObjectListener()
		{
			super(ConnectionMappingManagerBuild.this);
		}
		public void dataObjectChanged(DataObjectChangeEvent event)
		{
			Parameter modelParam = (Parameter) event.getSource();
			Parameter causeParam = event.getCause();
			DSet mappedModelParams = new DSet();
			Collection c = getMappingsForParameter(modelParam);
			if (c!=null) {
				mappedModelParams.addAll(c);
				mappedModelParams.remove(causeParam); // do not send change back to parameter that caused this change
			}
			c = getInterfaceConnections(modelParam);
			if (c!=null) {
				mappedModelParams.addAll(c);
			}

			ConcreteParameter mappedParam;
			for (int i = 0; i < mappedModelParams.size(); i++) {
				mappedParam = (ConcreteParameter) mappedModelParams.get(i);
				try {
					mappedParam.shadowDataChange(event);
				}
				catch (BuildMappingValuePropagationException ex) {
					System.err.println(ex);
					OneButton2Msg.showBuildMappingValuePropagationError(ex);
				}
			}
		}
	}

}
