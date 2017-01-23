package mit.cadlab.dome3.objectmodel.model.tool.optimization;

import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;
import mit.cadlab.dome3.util.OrderedHashMap;

import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 26, 2003
 * Time: 11:59:29 AM
 * To change this template use Options | File Templates.
 */
public abstract class OptimizationToolBase extends AnalysisToolBase
{
    protected OrderedHashMap _variableParameters;
    protected OrderedHashMap _objectiveParameters;

    protected Context _designVariableObjects;
    protected Context _designObjectiveObjects;

    public OptimizationToolBase(Id id)
    {
        super(id);
    }

    public OptimizationToolBase(Id id, AnalysisTool model)
	{
		super(id, model);
    }

    public OptimizationToolBase(String id)
    {
        super(id, QMOOConfiguration.TYPE_INFO.getTypeName());
    }

    public OptimizationToolBase(String file, Element xml)
    {
        super(file, xml);
    }

    public OptimizationToolBase(Element xml)
    {
        super(xml);
    }

    protected void loadXml(Element xmlElement)
    {
        initModel();

        loadVariableParameters(xmlElement);
        loadObjectiveParameters(xmlElement);
        loadContexts(xmlElement);

        _designVariableObjects = (Context) this.getModelObjectById(OptimizationToolBuild.VARIABLE_CONTEXT_ID);
        _designObjectiveObjects = (Context) this.getModelObjectById(OptimizationToolBuild.OBJECTIVE_CONTEXT_ID);

        modelObjects.remove(getModelObjectById(OptimizationToolBuild.VARIABLE_CONTEXT_ID));
        modelObjects.remove(getModelObjectById(OptimizationToolBuild.OBJECTIVE_CONTEXT_ID));

        // read config stuff
        this._toolConfiguration = new QMOOConfiguration(this, xmlElement);
        this._toolMappingManager = this._toolConfiguration.createToolMappingManager(this);

    }

    protected void initModel()
	{
		super.initModel();
        this._variableParameters = new OrderedHashMap();
		this._objectiveParameters = new OrderedHashMap();
	}

    protected void loadVariableParameters(Element xmlElement)
	{
		VariableParameter parameter;
		Element element;
		List params = xmlElement.selectNodes("/" + getXmlTag() + "/variables/" + Parameter.XML_TAG);
		for (Iterator iter = params.iterator(); iter.hasNext();)
		{
			element = (Element) iter.next();
			parameter = new VariableParameter(this, element);
			if (parameter != null)
			{
				_variableParameters.put(parameter.getParameter(), parameter);
                modelObjects.add(parameter.getParameter());
			}
		}
	}

	protected void loadObjectiveParameters(Element xmlElement)
	{
		ObjectiveParameter parameter;
		Element element;
		List params = xmlElement.selectNodes("/" + getXmlTag() + "/objectives/" + Parameter.XML_TAG);
		for (Iterator iter = params.iterator(); iter.hasNext();)
		{
			element = (Element) iter.next();
			parameter = new ObjectiveParameter(this, element);
			if(parameter != null)
			{
				_objectiveParameters.put(parameter.getParameter(), parameter);
                modelObjects.add(parameter.getParameter());
			}
		}
	}

    public OrderedHashMap getOptimizationToolVariableParameterMap()
    {
        return _variableParameters;
    }

    public OrderedHashMap getOptimizationToolObjectiveParameterMap()
    {
        return _objectiveParameters;
    }

    protected AbstractCausalityManager createCausalityManager()
    {
        return new OptimizationToolCausalityManager();
    }

    protected class OptimizationToolCausalityManager
            extends AbstractModelObjectScope.AbstractInternalCausalityManager
    {
        public OptimizationToolCausalityManager()
        {
            addPropertyChangeListener(AnalysisTool.DEPENDENCY_INFO, new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    processDependencyInfoChange();
                }
            });
        }

        protected CausalityStatus getInitialCausality(Object obj)
        {
            if (_variableParameters.containsKey(obj))
                return CausalityStatus.INDEPENDENT;
            else if (_objectiveParameters.containsKey(obj))
                return CausalityStatus.RESULT;
            else
                return CausalityStatus.INDEPENDENT;
        }

        protected void processDependencyInfoChange()
        {
            Iterator parameters = OptimizationToolBase.this.getModelObjects().iterator();
            while (parameters.hasNext())
            {
                Object obj = parameters.next();
                if (obj instanceof Parameter)
                {

                }
            }
        }
    }


}
