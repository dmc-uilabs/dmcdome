package mit.cadlab.dome3.objectmodel.toolinterface.optimization;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.OptimizationToolBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.FilterFunction;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.FunctionFilter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceConfiguration;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import org.dom4j.Element;

import java.util.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Oct 22, 2003
 * Time: 8:47:45 PM
 * To change this template use Options | File Templates.
 */
public abstract class OptimizationToolInterfaceBase extends AnalysisToolInterfaceBase
{
    public static final String QMOO_INTERFACE_BUILD_VIEW_PARAMETER = "qmoo interface build panel parameter";

    protected HashMap _interfaceVariableMap;
    protected HashMap _interfaceObjectiveMap;
    protected HashMap _interfaceOptimizationParametersMap;

    protected OptimizationInterfaceConfiguration _optimizationInterfaceConfiguration;

    protected Filter _variablesFilter, _objectivesFilter;

    public OptimizationToolInterfaceBase(Model m, Id id)
    {
        super(m, id);
    }

    public OptimizationToolInterfaceBase(ToolInterface ti, Id id)
    {
        super(ti, id);
    }

    public OptimizationToolInterfaceBase(Model m, Element xmlContent, Element xmlMappings)
    {
        super(m, xmlContent, xmlMappings);
    }

    public OptimizationToolInterfaceBase(Model m, Id id, ModelObjectScope mObjScope)
    {
        super(m, id, mObjScope);
    }

    protected CausalityManager createInternalCausalityManager(Element xmlElement)
    {
        return null;
    }

    protected void createViews()
    {
        // create build view
        views.put(BUILD_VIEW, Collections.unmodifiableList(_buildViewContext.getModelObjectReferences()));

        // create run view
        List interfaceCausalityView = new ArrayList();
        interfaceCausalityView.add(_variablesFilter);
        interfaceCausalityView.add(_objectivesFilter);
        views.put(INTERFACE_CAUSALITY_VIEW, Collections.unmodifiableList(interfaceCausalityView));
    }

    public void loadXmlElement(Element xmlContent)
    {
        initModel();
        loadVariableParameters(xmlContent);
        loadObjectiveParameters(xmlContent);
        loadCustomGuis(xmlContent);
        loadBuildContext(xmlContent);
        createQMOOInterfaceFilters();
        createQMOOInterfaceConfiguration(xmlContent);
    }

    protected void initModel()
    {
        _interfaceVariableMap = new HashMap();
        _interfaceObjectiveMap = new HashMap();
        _interfaceOptimizationParametersMap = new HashMap();
    }

    protected void loadVariableParameters(Element xmlContent)
    {
        Element element = null;
        VariableParameter parameter = null;
        List params = xmlContent.selectNodes("/" + getXmlTag() + "/variables/" + Parameter.XML_TAG);
        Model m = getModel();
        for (Iterator iter = params.iterator(); iter.hasNext();)
		{
			element = (Element) iter.next();
			parameter = new VariableParameter(this, element);
			if (parameter != null)
			{
				modelObjects.add(parameter.getParameter());
				_interfaceVariableMap.put(parameter.getParameter(), parameter);
                _interfaceOptimizationParametersMap.put(parameter.getParameter(), parameter);
			}
		}

    }

    protected void loadObjectiveParameters(Element xmlContent)
    {
        Element element = null;
        ObjectiveParameter parameter = null;
        List params = xmlContent.selectNodes("/" + getXmlTag() + "/objectives/" + Parameter.XML_TAG);
        for (Iterator iter = params.iterator(); iter.hasNext();)
        {
            element = (Element) iter.next();
            parameter = new ObjectiveParameter(this, element);
            if(parameter != null)
            {
                modelObjects.add(parameter.getParameter());
                _interfaceObjectiveMap.put(parameter.getParameter(), parameter);
                _interfaceOptimizationParametersMap.put(parameter.getParameter(), parameter);
            }
        }

    }

    protected void loadCustomGuis(Element xmlContent)
    {
        Element customGUIElement = (Element) xmlContent.selectSingleNode("customGUIs");
		if (customGUIElement != null) {
			//loads the customGUI info
			List element = customGUIElement.selectNodes(CustomGuiInfo.XML_TAG);
			for (Iterator iter = element.iterator(); iter.hasNext();) {
				Element e = (Element) iter.next();
				_customGUIList.add(new CustomGuiInfo(e));
			}
		}
    }

    protected void loadBuildContext(Element xmlContent)
    {
        Context cxt;
        Element buildContextElement = null;
        List viewList = xmlContent.selectNodes("/" + getXmlTag() + "/" + ToolInterface.VIEWS + "/" + ToolInterface.VIEW);
        for (Iterator i = viewList.iterator(); i.hasNext();)
        {
            Element viewElement = (Element) i.next();
            String viewName = viewElement.attributeValue(ToolInterface.NAME);
            if (viewName.equals(ToolInterface.BUILD_VIEW))
            {
                buildContextElement = (Element) viewElement.elements().get(0);
            }
        }
        if (buildContextElement != null)
        {
            cxt = (Context) getModelObjectFactory().newInstance(buildContextElement, new Object[]{this, buildContextElement});
            if (cxt != null)
            {
                _buildViewContext = cxt;
                modelObjects.add(_buildViewContext);
            }
        }
    }

    protected static final Id VARIABLES_FILTER = new Id("VARIABLES_FILTER");
    protected static final Id OBJECTIVES_FILTER = new Id("OBJECTIVES_FILTER");

    protected void createQMOOInterfaceFilters()
    {
        _variablesFilter = new ToolInterfaceFilter(new QMOOVariableCheck(),VARIABLES_FILTER);
        _objectivesFilter = new ToolInterfaceFilter(new QMOOObjectiveCheck(), OBJECTIVES_FILTER);
    }

    protected void createQMOOInterfaceConfiguration(Element xmlContent)
    {
        _optimizationInterfaceConfiguration = new OptimizationInterfaceConfiguration(this, xmlContent);
    }

    public OptimizationInterfaceConfiguration getInterfaceConfiguration()
    {
        return _optimizationInterfaceConfiguration;
    }

    protected class ToolInterfaceFilter extends FunctionFilter implements ViewSupport
    {
        //generates xml for filter
        public Element toXmlElement()
        {
            return super.toXmlElement();
        }

        public ToolInterfaceFilter(FilterFunction filterFunction, Id id)
        {
            super(OptimizationToolInterfaceBase.this._model, id, filterFunction, true);
            addListToFilter(OptimizationToolInterfaceBase.this);
        }
        //ViewSupport funcs implemented already implemented in superclass AbstractFilter
    }

    protected class QMOOVariableCheck implements FilterFunction
    {
        protected static final String VARIABLES = "variables";

        public boolean keepInFilter(Object obj)
        {
            return _interfaceVariableMap.containsKey(obj);
        }

        public String getName()
        {
            return VARIABLES;
        }
    }

    protected class QMOOObjectiveCheck implements FilterFunction
    {
        protected static final String OBJECTIVE = "objectives";

        public boolean keepInFilter(Object obj)
        {
            return _interfaceObjectiveMap.containsKey(obj);
        }

        public String getName()
        {
            return OBJECTIVE;
        }
    }

    public List getView(String viewName)
    {
        if (viewNames.contains(viewName))
        {
            List view = (List) views.get(viewName);
            return (view == null) ? Collections.EMPTY_LIST : view;
        }
        return Collections.EMPTY_LIST;
    }

    public Element toXmlElement()
    {
        //todo: move this lower down the tree
        Element xml = headerToXmlElement();
        xml.addAttribute("toolType", QMOOConfiguration.TYPE_INFO.getXmlType());

        // creates the modelinfo element
        xml.add(createInterfaceInfoElement());

        getInterfaceConfiguration().toXmlElement(xml);

        Element variableElement = xml.addElement("variables");
        Element objectiveElement = xml.addElement("objectives");
        Element viewsElement = xml.addElement(ToolInterface.VIEWS);

        Iterator listIterator = modelObjects.listIterator();
        while (listIterator.hasNext())
        {
            Object obj = listIterator.next();
            if (obj instanceof Parameter)
            {
                Element parameter = null;
                if (getInterfaceVariableMap().containsKey(obj))
                {
                    parameter = ((VariableParameter) this.getInterfaceVariableMap().get(obj)).toXmlElement();
                    if (parameter != null)
                        variableElement.add(parameter);
                }
                else
                {
                    parameter = ((ObjectiveParameter) this.getInterfaceObjectiveMap().get(obj)).toXmlElement();
                    if (parameter != null)
                        objectiveElement.add(parameter);
                }
            }
        }

        if (_customGUIList.size() != 0)
            XMLUtils.addCollection(xml, "customGUIs", _customGUIList);


        Element buildViewElement = viewsElement.addElement(ToolInterface.VIEW);
        buildViewElement.addAttribute(ToolInterface.NAME, ToolInterface.BUILD_VIEW);

        Element buildconElement = getBuildContext().toXmlElement();
        buildViewElement.add(buildconElement);

        Element runViewElement = viewsElement.addElement(ToolInterface.VIEW);
        runViewElement.addAttribute(ToolInterface.NAME, ToolInterface.INTERFACE_CAUSALITY_VIEW);
        Element variablesFilterElement = _variablesFilter.toXmlElement();
        runViewElement.add(variablesFilterElement);
        Element objectivesFilterElement = _objectivesFilter.toXmlElement();
        runViewElement.add(objectivesFilterElement);

        return xml;


    }

    public Filter getVariablesFilter()
    {
        return _variablesFilter;
    }

    public Filter getObjectivesFilter()
    {
        return _objectivesFilter;
    }

    public HashMap getInterfaceVariableMap()
    {
        return _interfaceVariableMap;
    }

    public HashMap getInterfaceObjectiveMap()
    {
        return _interfaceObjectiveMap;
    }

    public HashMap getInterfaceOptimizationMap()
    {
        return _interfaceOptimizationParametersMap;
    }

    public OptimizationInterfaceConfiguration getOptimizationInterfaceConfiguration()
    {
        return _optimizationInterfaceConfiguration;
    }

    public boolean getUserPermission(String permissionType)
    {
        return ((DomeBoolean)_optimizationInterfaceConfiguration.getSetupParameter
                    (permissionType).getCurrentDataObject()).getValue();
    }

    public void addViewListener(String viewName, DListListener l)
    {
    }

    public void removeViewListener(String viewName, DListListener l)
    {
    }

    public Collection removeItemsToFilterListener(DListListener l)
    {
        return null;
    }

    protected void copyModelObjects(ToolInterface iface){}

}
