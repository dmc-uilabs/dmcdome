package mit.cadlab.dome3.objectmodel.toolinterface.optimization.build;

import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.OptimizationToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import edu.iupui.rg.ucum.units.Unit;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Sep 16, 2003
 * Time: 1:36:01 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationInterfaceConfiguration
{
    public static final TypeInfo TYPE_INFO = new TypeInfo("Optimization Interface", "Optimization");
    public static final String VARIABLES_CONTEXT = "design variables";
    public static final String OBJECTIVES_CONTEXT = "design objectives";
    public static final String PLOT_CONFIGURATION = "plot configuration";

    // design variable constants
    public static final String IS_VARIABLE_ACTIVE = "allow users to set variables active/inactive: ";
    public static final String ALLOW_VARIABLE_SEARCH = "allow users to set search limits: ";

    // design objective constants
    public static final String IS_OBJECTIVE_ACTIVE = "allow users to set objectives active/inactive: ";
    public static final String MAXIMUM_NUMBER_OBJECTIVES = "maximum number of objectives: ";

    // solution monitor (plot configuration)
    public static final String SOLUTION_UPDATE_INTERVAL = "update pareto plot every: ";

    public static final Integer DEFAULT_NUMBER_OF_OBJECTIVES = new Integer(2);
    public static final Integer DEFAULT_UPDATE_INTERVAL = new Integer(10);

    public static final Unit OBJECTIVE_UNIT = new Unit("objective");
    public static final Unit INDIVIDUAL_UNIT = new Unit("individual");

    protected List _setupParameters = new ArrayList();
    protected HashMap _paramsByName = new HashMap();

    protected List _setupContextFolders = new ArrayList();
    protected HashMap _contextFolderByName = new HashMap();


    public OptimizationInterfaceConfiguration(OptimizationToolInterfaceBase optimizationInterface)
    {
        Context designVariables = new DefaultContextBuilder(optimizationInterface, new Id(VARIABLES_CONTEXT));
        designVariables.setName(VARIABLES_CONTEXT);

        Parameter isVariableActive = new ConcreteParameter(optimizationInterface, new Id(IS_VARIABLE_ACTIVE), DomeBoolean.TYPE_INFO.getTypeName());
        isVariableActive.setName(IS_VARIABLE_ACTIVE);
        ((DomeBoolean)isVariableActive.getCurrentDataObject()).setValue(true);
        addSetupParameter(isVariableActive);
        designVariables.addModelObjectReference(isVariableActive);

        Parameter allowVariableSearch = new ConcreteParameter(optimizationInterface, new Id(ALLOW_VARIABLE_SEARCH), DomeBoolean.TYPE_INFO.getTypeName());
        allowVariableSearch.setName(ALLOW_VARIABLE_SEARCH);
        ((DomeBoolean)allowVariableSearch.getCurrentDataObject()).setValue(true);
        addSetupParameter(allowVariableSearch);
        designVariables.addModelObjectReference(allowVariableSearch);

        addSetupContextFolder(designVariables);

        Context designObjectives = new DefaultContextBuilder(optimizationInterface, new Id(OBJECTIVES_CONTEXT));
        designObjectives.setName(OBJECTIVES_CONTEXT);

        Parameter isObjectiveActive = new ConcreteParameter(optimizationInterface, new Id(IS_OBJECTIVE_ACTIVE), DomeBoolean.TYPE_INFO.getTypeName());
        isObjectiveActive.setName(IS_OBJECTIVE_ACTIVE);
        ((DomeBoolean)isObjectiveActive.getCurrentDataObject()).setValue(true);
        addSetupParameter(isObjectiveActive);
        designObjectives.addModelObjectReference(isObjectiveActive);

        Parameter maxNumberObjectives = new ConcreteParameter(optimizationInterface, new Id(MAXIMUM_NUMBER_OBJECTIVES), DomeInteger.TYPE_INFO.getTypeName());
        maxNumberObjectives.setName(MAXIMUM_NUMBER_OBJECTIVES);
        ((DomeInteger) maxNumberObjectives.getCurrentDataObject()).setIntegerValue(DEFAULT_NUMBER_OF_OBJECTIVES);
        ((DomeInteger) maxNumberObjectives.getCurrentDataObject()).setUnit(OBJECTIVE_UNIT);
        addSetupParameter(maxNumberObjectives);
        designObjectives.addModelObjectReference(maxNumberObjectives);

        addSetupContextFolder(designObjectives);

        Context plotConfiguration = new DefaultContextBuilder(optimizationInterface, new Id(PLOT_CONFIGURATION));
        plotConfiguration.setName(PLOT_CONFIGURATION);

        Parameter updateInterval = new ConcreteParameter(optimizationInterface, new Id(SOLUTION_UPDATE_INTERVAL), DomeInteger.TYPE_INFO.getTypeName());
        updateInterval.setName(SOLUTION_UPDATE_INTERVAL);
        ((DomeInteger)updateInterval.getCurrentDataObject()).setIntegerValue(DEFAULT_UPDATE_INTERVAL);
        ((DomeInteger)updateInterval.getCurrentDataObject()).setUnit(INDIVIDUAL_UNIT);

        addSetupParameter(updateInterval);
        plotConfiguration.addModelObjectReference(updateInterval);

        addSetupContextFolder(plotConfiguration);
    }

    public OptimizationInterfaceConfiguration(OptimizationToolInterfaceBase optimizationInterface, Element xmlContent)
    {
        this(optimizationInterface);
        parseConfigurationXml(xmlContent);
    }

    public OptimizationInterfaceConfiguration(OptimizationToolInterfaceBase optimizationInterface, OptimizationInterfaceConfiguration configuration)
    {
        Context designVariables = new DefaultContextBuilder(optimizationInterface, new Id(VARIABLES_CONTEXT));
        designVariables.setName(VARIABLES_CONTEXT);

        Parameter isVariableActive = new ConcreteParameter(optimizationInterface, new Id(IS_VARIABLE_ACTIVE), DomeBoolean.TYPE_INFO.getTypeName());
        isVariableActive.setName(IS_VARIABLE_ACTIVE);
        ((DomeBoolean) isVariableActive.getCurrentDataObject()).setValue(((DomeBoolean)configuration.getSetupParameter(IS_VARIABLE_ACTIVE).getCurrentDataObject()).getValue());
        addSetupParameter(isVariableActive);
        designVariables.addModelObjectReference(isVariableActive);

        Parameter allowVariableSearch = new ConcreteParameter(optimizationInterface, new Id(ALLOW_VARIABLE_SEARCH), DomeBoolean.TYPE_INFO.getTypeName());
        allowVariableSearch.setName(ALLOW_VARIABLE_SEARCH);
        ((DomeBoolean) allowVariableSearch.getCurrentDataObject()).setValue(((DomeBoolean)configuration.getSetupParameter(ALLOW_VARIABLE_SEARCH).getCurrentDataObject()).getValue());
        addSetupParameter(allowVariableSearch);
        designVariables.addModelObjectReference(allowVariableSearch);

        addSetupContextFolder(designVariables);

        Context designObjectives = new DefaultContextBuilder(optimizationInterface, new Id(OBJECTIVES_CONTEXT));
        designObjectives.setName(OBJECTIVES_CONTEXT);

        Parameter isObjectiveActive = new ConcreteParameter(optimizationInterface, new Id(IS_OBJECTIVE_ACTIVE), DomeBoolean.TYPE_INFO.getTypeName());
        isObjectiveActive.setName(IS_OBJECTIVE_ACTIVE);
        ((DomeBoolean) isObjectiveActive.getCurrentDataObject()).setValue(((DomeBoolean)configuration.getSetupParameter(IS_OBJECTIVE_ACTIVE).getCurrentDataObject()).getValue());
        addSetupParameter(isObjectiveActive);
        designObjectives.addModelObjectReference(isObjectiveActive);

        Parameter maxNumberObjectives = new ConcreteParameter(optimizationInterface, new Id(MAXIMUM_NUMBER_OBJECTIVES), DomeInteger.TYPE_INFO.getTypeName());
        maxNumberObjectives.setName(MAXIMUM_NUMBER_OBJECTIVES);
        ((DomeInteger) maxNumberObjectives.getCurrentDataObject()).setIntegerValue(((DomeInteger)configuration.getSetupParameter(MAXIMUM_NUMBER_OBJECTIVES).getCurrentDataObject()).getIntegerValue());
        ((DomeInteger) maxNumberObjectives.getCurrentDataObject()).setUnit(OBJECTIVE_UNIT);
        addSetupParameter(maxNumberObjectives);
        designObjectives.addModelObjectReference(maxNumberObjectives);

        addSetupContextFolder(designObjectives);

        Context plotConfiguration = new DefaultContextBuilder(optimizationInterface, new Id(PLOT_CONFIGURATION));
        plotConfiguration.setName(PLOT_CONFIGURATION);

        Parameter updateInterval = new ConcreteParameter(optimizationInterface, new Id(SOLUTION_UPDATE_INTERVAL), DomeInteger.TYPE_INFO.getTypeName());
        updateInterval.setName(SOLUTION_UPDATE_INTERVAL);
        ((DomeInteger) updateInterval.getCurrentDataObject()).setIntegerValue(((DomeInteger)configuration.getSetupParameter(SOLUTION_UPDATE_INTERVAL).getCurrentDataObject()).getIntegerValue());
        ((DomeInteger) updateInterval.getCurrentDataObject()).setUnit(INDIVIDUAL_UNIT);

        addSetupParameter(updateInterval);
        plotConfiguration.addModelObjectReference(updateInterval);

        addSetupContextFolder(plotConfiguration);
    }

    private void parseConfigurationXml(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml configuration info");

		Element allowSetVariableState = (Element)xmlElement.selectSingleNode("configuration/variables/allowSetState");
        ((DomeBoolean)getSetupParameter(IS_VARIABLE_ACTIVE).getCurrentDataObject()).setBooleanValue(new Boolean(allowSetVariableState.getText()));

        Element allowSetVariableLimits = (Element)xmlElement.selectSingleNode("configuration/variables/allowSetLimits");
        ((DomeBoolean)getSetupParameter(ALLOW_VARIABLE_SEARCH).getCurrentDataObject()).setBooleanValue(new Boolean(allowSetVariableLimits.getText()));

        Element allowSetObjectiveState = (Element)xmlElement.selectSingleNode("configuration/objectives/allowSetState");
        ((DomeBoolean)getSetupParameter(IS_OBJECTIVE_ACTIVE).getCurrentDataObject()).setBooleanValue(new Boolean(allowSetObjectiveState.getText()));

        Element defaultNumberOfObjectives = (Element)xmlElement.selectSingleNode("configuration/objectives/maximumObjectives");
        ((DomeInteger)getSetupParameter(MAXIMUM_NUMBER_OBJECTIVES).getCurrentDataObject()).setIntegerValue(new Integer(defaultNumberOfObjectives.getText()));
        ((DomeInteger)getSetupParameter(MAXIMUM_NUMBER_OBJECTIVES).getCurrentDataObject()).setUnit(OBJECTIVE_UNIT);

        Element updateInterval = (Element)xmlElement.selectSingleNode("configuration/plot/updateInterval");
        ((DomeInteger)getSetupParameter(SOLUTION_UPDATE_INTERVAL).getCurrentDataObject()).setIntegerValue(new Integer(updateInterval.getText()));
	}

    private String getTypeName()
    {
        return getTypeInfo().getTypeName();
    }

    private TypeInfo getTypeInfo()
    {
        return TYPE_INFO;
    }

    protected void addSetupParameter(Parameter param)
	{
		_setupParameters.add(param);
		_paramsByName.put(param.getName(), param);
	}

    protected void addSetupContextFolder(Context context)
    {
        this._setupContextFolders.add(context);
        this._contextFolderByName.put(context.getName(), context);
    }

    public List getSetupParameters()
	{
		return Collections.unmodifiableList(this._setupParameters);
	}

    public List getSetupContextFolders()
    {
        return Collections.unmodifiableList(this._setupContextFolders);
    }

    public Parameter getSetupParameter(String name)
	{
		return (Parameter) _paramsByName.get(name);
	}

    public void toXmlElement(Element xmlElement)
    {
        Element configurationElement = xmlElement.addElement("configuration");

        Element variablesConfiguration = configurationElement.addElement("variables");

        String isVariableActive = getSetupParameter(IS_VARIABLE_ACTIVE).getCurrentDataObject().toString();
        variablesConfiguration.addElement("allowSetState").addText(isVariableActive);

        String allowSearchLimits = getSetupParameter(ALLOW_VARIABLE_SEARCH).getCurrentDataObject().toString();
        variablesConfiguration.addElement("allowSetLimits").addText(allowSearchLimits);

        Element objectivesConfiguration = configurationElement.addElement("objectives");

        String isObjectiveActive = getSetupParameter(IS_OBJECTIVE_ACTIVE).getCurrentDataObject().toString();
        objectivesConfiguration.addElement("allowSetState").addText(isObjectiveActive);

        Integer maxNumberObjectives = ((DomeInteger)getSetupParameter(MAXIMUM_NUMBER_OBJECTIVES).getCurrentDataObject()).getIntegerValue();
        objectivesConfiguration.addElement("maximumObjectives").addText(maxNumberObjectives.toString());

        Element plotConfiguration = configurationElement.addElement("plot");
        Integer updateInterval = ((DomeInteger)getSetupParameter(SOLUTION_UPDATE_INTERVAL).getCurrentDataObject()).getIntegerValue();
        plotConfiguration.addElement("updateInterval").addText(updateInterval.toString());
    }
}
