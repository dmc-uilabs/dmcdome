package mit.cadlab.dome3.tool.optimization.qmoo;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.tool.AnalysisToolConfiguration;
import mit.cadlab.dome3.tool.optimization.qmoo.monitor.DomeMonitor;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolClientRuntime;
import mit.cadlab.dome3.gui.guiutils.treetable.Editors;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.optimization.OptimizationToolBuildPanel;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.units.Quantity;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.goof.rts.AnyMap;
import org.goof.qmoo.monitor.ParetoMonitor;

import java.util.List;

import edu.iupui.rg.ucum.units.Unit;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 10, 2003
 * Time: 3:53:43 PM
 * To change this template use Options | File Templates.
 */
public class QMOOConfiguration extends AnalysisToolConfiguration
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("Optimization", "OPTIMIZATION");

	public static final String ID = "id";

    public static final String VARIABLE = "Variable";
    public static final String OBJECTIVE = "Objective";
    public static final String MODEL_VARIABLE = "model variable";
    public static final String MODEL_OBJECTIVE = "model objective";
	public static final String INTERFACE_VARIABLE = "interface variable";
    public static final String INTERFACE_OBJECTIVE = "interface objective";

    public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName()};

	// configuration context folders
	public static final String SYSTEM_CONTEXT = "system configuration";
	public static final String ALGORITHM_CONFIGURATION = "algorithm configuration";
    public static final String ALGORITHM = "algorithm";

    // system configuration
    public static final String PLATFORM_COMPILED = "platform: ";

	// platform enumeration menu
	public static final String WINDOWS_2000 = "Windows 2000";
    public static final String WINDOWS_XP = "Windows XP";
    public static final String MAC_OS_X = "MAC OSX";

    // algorithm
    public static final String NAME = "name: ";

    // algorithm enumeration menu
    public static final String QMOO = "Q.M.O.O.";

    // algorithm configuration
    public static final String POPULATION_SIZE = "population size: ";
    public static final String PERCENTAGE_OF_UNRANKED_POPULATION = "percentage of unranked population: ";
    public static final String NUMBER_OF_EVALUATIONS = "number of evaluations: ";

    public static final String MAPPING_COLUMN_NAME = "mapping";

	public static final int MAPPING_COLUMN_SIZE = 150;
    public static final Integer DEFAULT_POPULATION_SIZE = new Integer(50);
    public static final Double DEFAULT_UNRANKED_PERCENTAGE = new Double(0.30);
    public static final Integer DEFAULT_NUMBER_OF_EVALUATIONS = new Integer(1000);

    public static final String FILE_LOCATION = "";

//    public static final Unit INDIVIDUAL = new Unit("individual");
//    public static final Unit PERCENT = new Unit("%");

    public static final String RUN_DIRECTORY_PATH = System.getProperty("QMOOROOT") + System.getProperty("file.separator") + "results";
    public static final String STOP_MONITOR = "stop_monitor";
    public static final String SIMPLE_MONITOR = "simple_monitor";

	public QMOOConfiguration(AnalysisTool model)
	{
        Context systemConfiguration = new DefaultContextBuilder(model, new Id(SYSTEM_CONTEXT));
        systemConfiguration.setName(SYSTEM_CONTEXT);
        Parameter platformCompiled = new ConcreteParameter(model, new Id(PLATFORM_COMPILED), DomeEnumeration.TYPE_INFO.getTypeName());
        platformCompiled.setName(PLATFORM_COMPILED);
        EnumerationData platform = (EnumerationData)platformCompiled.getCurrentDataObject();
		platform.addElement(WINDOWS_2000, "");
        platform.addElement(WINDOWS_XP, "");
        platform.addElement(MAC_OS_X, "");
        platform.setLastSelection(0);
		addSetupParameter(platformCompiled);
        systemConfiguration.addModelObjectReference(platformCompiled);
        addSetupContextFolder(systemConfiguration);

        Context algorithmType = new DefaultContextBuilder(model, new Id(ALGORITHM));
        algorithmType.setName(ALGORITHM);
        Parameter algorithmTypes = new ConcreteParameter(model, new Id(NAME), DomeEnumeration.TYPE_INFO.getTypeName());
        algorithmTypes.setName(NAME);
        EnumerationData algorithmNames = (EnumerationData) algorithmTypes.getCurrentDataObject();
        algorithmNames.addElement(QMOO, "");
        algorithmNames.setLastSelection(0);
        addSetupParameter(algorithmTypes);
        algorithmType.addModelObjectReference(algorithmTypes);
        addSetupContextFolder(algorithmType);

        Context algorithmConfiguration = new DefaultContextBuilder(model, new Id(ALGORITHM_CONFIGURATION));
        algorithmConfiguration.setName(ALGORITHM_CONFIGURATION);

        Parameter populationSize = new ConcreteParameter(model, new Id(POPULATION_SIZE), DomeInteger.TYPE_INFO.getTypeName());
        populationSize.setName(QMOOConfiguration.POPULATION_SIZE);
		((DomeInteger) populationSize.getCurrentDataObject()).setIntegerValue(DEFAULT_POPULATION_SIZE);
		((DomeInteger) populationSize.getCurrentDataObject()).setUnit(new Unit("individual"));
		this.addSetupParameter(populationSize);
        algorithmConfiguration.addModelObjectReference(populationSize);

        Parameter percentageOfUnrankedPopulation = new ConcreteParameter(model, new Id(PERCENTAGE_OF_UNRANKED_POPULATION), DomeReal.TYPE_INFO.getTypeName());
        percentageOfUnrankedPopulation.setName(PERCENTAGE_OF_UNRANKED_POPULATION);
        ((DomeReal) percentageOfUnrankedPopulation.getCurrentDataObject()).setRealValue(DEFAULT_UNRANKED_PERCENTAGE);
        ((DomeReal) percentageOfUnrankedPopulation.getCurrentDataObject()).setUnit(new Unit("%"));
        addSetupParameter(percentageOfUnrankedPopulation);
        algorithmConfiguration.addModelObjectReference(percentageOfUnrankedPopulation);

        Parameter numberOfIterations = new ConcreteParameter(model, new Id(NUMBER_OF_EVALUATIONS), DomeInteger.TYPE_INFO.getTypeName());
        numberOfIterations.setName(NUMBER_OF_EVALUATIONS);
        ((DomeInteger) numberOfIterations.getCurrentDataObject()).setIntegerValue(DEFAULT_NUMBER_OF_EVALUATIONS);
        ((DomeInteger) numberOfIterations.getCurrentDataObject()).setUnit(new Unit("individual"));
        addSetupParameter(numberOfIterations);
        algorithmConfiguration.addModelObjectReference(numberOfIterations);

        this.addSetupContextFolder(algorithmConfiguration);

	}

	public QMOOConfiguration(AnalysisTool model, Element xmlElement)
	{
		this(model);
		parseConfigurationXml(xmlElement);
	}

	public Object[] createParameter(AnalysisTool model, Id id, String type)
	{
		if (DomeReal.TYPE_INFO.getTypeName().equals(type) && OptimizationToolBuildPanel._currentView.equals(OptimizationToolBuildPanel.VARIABLES))
        {
            VariableParameter qmooVariable = new VariableParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
            qmooVariable.getParameter().setName(VARIABLE);
            return new Object[]{qmooVariable, VARIABLE};
        }
        if (DomeReal.TYPE_INFO.getTypeName().equals(type) && OptimizationToolBuildPanel._currentView.equals(OptimizationToolBuildPanel.OBJECTIVES))
        {
            ObjectiveParameter qmooObjective = new ObjectiveParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
            qmooObjective.getParameter().setName(OBJECTIVE);
            return new Object[]{qmooObjective, OBJECTIVE};
        }
		return null;
	}

	public TypeInfo getTypeInfo()
	{
		return TYPE_INFO;
	}

	public String getMappingColumnName()
	{
		return MAPPING_COLUMN_NAME;
	}

	public int getMappingColumnSize()
	{
		return MAPPING_COLUMN_SIZE;
	}

    public boolean useCustomDatatype()
	{
		return true;
	}

	private void parseConfigurationXml(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml configuration info");

		String selectedSystemConfiguration = xmlElement.selectSingleNode("configuration/systemconfiguration/platform").getText();
        EnumerationData platformOptions = (EnumerationData)this.getSetupParameter(QMOOConfiguration.PLATFORM_COMPILED).getCurrentDataObject();
        for(int i=0; i<platformOptions.getSize(); i++)
	        if(platformOptions.getNames()[i].toString().equals(selectedSystemConfiguration))
		        platformOptions.setLastSelection(i);

        String selectedAlgorithmType = xmlElement.selectSingleNode("configuration/algorithm/name").getText();
        EnumerationData algorithmTypes = (EnumerationData)getSetupParameter(NAME).getCurrentDataObject();
        for (int i=0; i < algorithmTypes.getSize(); i++)
            if (algorithmTypes.getNames()[i].toString().equals(selectedAlgorithmType))
                algorithmTypes.setLastSelection(i);

		Element populationSize = (Element)xmlElement.selectSingleNode("configuration/algorithmconfiguration/populationSize");
		((DomeInteger)this.getSetupParameter(POPULATION_SIZE).getCurrentDataObject()).setIntegerValue(new Integer(populationSize.getText()));

        Element percentageUnranked = (Element)xmlElement.selectSingleNode("configuration/algorithmconfiguration/unrankedPercentage");
        ((DomeReal)getSetupParameter(PERCENTAGE_OF_UNRANKED_POPULATION).getCurrentDataObject()).setRealValue(new Double(percentageUnranked.getText()));

        Element numberOfEvaluations = (Element)xmlElement.selectSingleNode("configuration/algorithmconfiguration/numberOfEvaluations");
        ((DomeInteger)getSetupParameter(NUMBER_OF_EVALUATIONS).getCurrentDataObject()).setIntegerValue(new Integer(numberOfEvaluations.getText()));

	}

    public Element createConfigurationXml()
    {
        Element configurationXml = DocumentHelper.createElement("configuration");
        Element configurationSystem = configurationXml.addElement("systemconfiguration");
        String platform = ((DomeEnumeration) getSetupParameter(PLATFORM_COMPILED).getCurrentDataObject()).getElementName(
                ((DomeEnumeration) getSetupParameter(PLATFORM_COMPILED).getCurrentDataObject()).getLastSelection());
        configurationSystem.addElement("platform").addText(platform);

        Element algorithm = configurationXml.addElement("algorithm");
        String type = ((DomeEnumeration) getSetupParameter(NAME).getCurrentDataObject()).getElementName(
                ((DomeEnumeration)getSetupParameter(NAME).getCurrentDataObject()).getLastSelection());
        algorithm.addElement("name").addText(type);

        Element configurationAlgorithm = configurationXml.addElement("algorithmconfiguration");

        Integer populationSize = ((DomeInteger) getSetupParameter(POPULATION_SIZE).getCurrentDataObject()).getIntegerValue();
        configurationAlgorithm.addElement("populationSize").addText(populationSize.toString());

        Double percentageUnranked = ((DomeReal) getSetupParameter(PERCENTAGE_OF_UNRANKED_POPULATION).getCurrentDataObject()).getRealValue();
        configurationAlgorithm.addElement("unrankedPercentage").addText(percentageUnranked.toString());

        Integer numberOfEvaluations = ((DomeInteger)getSetupParameter(NUMBER_OF_EVALUATIONS).getCurrentDataObject()).getIntegerValue();
        configurationAlgorithm.addElement("numberOfEvaluations").addText(numberOfEvaluations.toString());

        return configurationXml;
    }

    public AnyMap createMap()
    {
        Integer population = ((DomeInteger)getSetupParameter(POPULATION_SIZE).getCurrentDataObject()).getIntegerValue();
        Double percentageUnranked = ((DomeReal)getSetupParameter(PERCENTAGE_OF_UNRANKED_POPULATION).getCurrentDataObject()).getRealValue();

        int unrankedPopulation = (int) (percentageUnranked.doubleValue()*population.intValue());

        AnyMap m = new AnyMap();	// defaults come from text file

        m.setString("java.classpath", System.getProperty("QMOOROOT"));
        m.setString("evaluator.name", "external_evaluator");
        m.setString("run.name", "optimisationresults");
        m.setString("run.results_dir", RUN_DIRECTORY_PATH);
        m.setString("objective.eename", "evaluators/jni_evaluator");
        m.setEnvironment("java.environment");
        m.setString("random.starter", "timer");

        // monitors are being specified
        m.setString("monitors[0].name", STOP_MONITOR);
        m.setInt("monitors[0].start", ((DomeInteger)getSetupParameter(NUMBER_OF_EVALUATIONS).getCurrentDataObject()).getIntegerValue().intValue());
        m.setString("monitors[1].name", "external_monitor");
        m.setString("monitors[1].plugin_name", "monitors/jni_monitor");
//        m.setInt("monitors[1].start", 10);

        m.setString("ranker.front_type", "list_front");
        m.setString("ranker.cruft_type", "birthday_cruft");
        m.setDouble("ranker.first_rank_prob", 0.666);
        m.setInt("ranker.unranked_size", unrankedPopulation);
        m.setInt("ranker.first_rank_size", population.intValue() - unrankedPopulation);

        m.setInt("assigner.initial_pop", population.intValue());

        m.setString("assigner.initial[0].name", "sobol_assigner");
        m.setInt("assigner.initial[0].start_poly", 0);
        m.setInt("assigner.initial[0].warmup", 0);
        m.setString("assigner.main[0].name", "parent_chooser");
        m.setString("assigner.main[1].name", "cloner");
        m.setString("assigner.main[2].name", "sbx_crossover");
        m.setString("assigner.main[3].name", "eoc_op");
        m.setString("assigner.main[3].type", "mutate");
        m.setDouble("assigner.main[3].op_inherit_prob", 0.900000);
        m.setString("assigner.main[3].ops[0].name", "no_op");
        m.setString("assigner.main[3].ops[1].name", "uniform_mutate");
        m.setString("assigner.main[3].ops[2].name", "normal_mutate");
        m.setString("assigner.main[3].ops[3].name", "global_mutate");
        m.setString("assigner.main[4].name", "bounds_checker");
        m.setString("assigner.main[5].name", "parent_checker");

        m.setString("thinner.name", "quadratic_thin");
        m.setDouble("thinner.overrun", 1.20000);

        m.setString("grouper.name", "no_groups");

        return m;
    }


}
