// ModeContexts.java
package mit.cadlab.dome3.gui.mode;

public interface ModeContexts
{

	public static final String BUILD_MODE = "Build Mode";
	public static final String BUILD_DOMEMODEL = "Build DOME Model";
	public static final String BUILD_PLUGINMODEL = "Build Plugin Model";
    public static final String BUILD_TOOLMODEL = "Build Tool Model";
	public static final String BUILD_QMOOMODEL = "Build QMOO Model";
	public static final String BUILD_DOMEMODEL_DEFINITION = "Build DOME Model Definition";
	public static final String BUILD_MODELCAUSALITY_VIEW = "Build Model Causality View";
	public static final String BUILD_PROJECTCAUSALITY_VIEW = "Build Project Causality View";
	public static final String BUILD_PLUGINMODEL_DEFINITION = "Build Plugin Model Definition";
    public static final String BUILD_TOOLMODEL_DEFINITION = "Build Tool Model Definition";
    public static final String BUILD_TOOL_PROJECT_DEFINITION = "Build Tool Project Defintion";
    public static final String BUILD_QMOOMODEL_VARIABLE_DEFINITION = "Build QMOO Model Variable Definition";
    public static final String BUILD_QMOOMODEL_OBJECTIVE_DEFINITION = "Build QMOO Model Objective Definition";
	public static final String BUILD_DOMEMODEL_SETUP = "Build DOME Model Setup";
	public static final String BUILD_DOMEMODEL_DOCUMENTATION = "Define Model Documentation";
	public static final String BUILD_PLUGINMODEL_DOCUMENTATION = "Build Plugin Model Documentation";
    public static final String BUILD_QMOOMODEL_DOCUMENTATION = "Build QMOO Model Documentation";
	public static final String BUILD_DOMEMODEL_INTERFACES = "Build Model Interfaces";
	public static final String BUILD_PLUGINMODEL_INTERFACES = "Build Plugin Model Interfaces";
    public static final String BUILD_TOOLMODEL_INTERFACES = "Build Tool Interfaces";
	public static final String BUILD_DOMEMODEL_INTERFACE_CAUSALVIEW = "Build Interface CausalView";
	public static final String BUILD_DOMEMODEL_INTERFACE_BUILDVIEW = "Build Interface BuildView";
	public static final String BUILD_DOMEMODEL_INTERFACE_MODELVIEW = "Build Interface ModelView";
	public static final String BUILD_PLUGINMODEL_INTERFACE_CAUSALVIEW = "Build Plugin Interface CausalView";
	public static final String BUILD_PLUGINMODEL_INTERFACE_BUILDVIEW = "Build Plugin Interface BuildView";
	public static final String BUILD_PLUGINMODEL_INTERFACE_MODELVIEW = "Build Plugin Interface ModelView";
    public static final String BUILD_TOOLMODEL_INTERFACE_BUILDVIEW = "Build Tool Interface BuildView";


	public static final String BUILD_PROCEDURALRELATION_DEFINITION = "Build Procedural Relation";
	public static final String BUILD_EQUALRELATION_DEFINITION = "Build Equal Relation";
    public static final String BUILD_ITERATIONRELATION_DEFINITION = "Build Iteration Relation";
	public static final String BUILD_PROCEDURALRELATION_DOCUMENTATION = "Define Relation Documentation";
	public static final String BUILD_PARAMETER = "Build Parameter";
	public static final String BUILD_FILTER = "Build Filter";
	public static final String BUILD_STANDARD_VIEW = "Build Standard View";
	public static final String BUILD_PLUGIN_STANDARD_VIEW = "Build Plugin Standard View";
    public static final String BUILD_TOOL_STANDARD_VIEW = "Build Tool Standard View";
	public static final String BUILD_MAPPING_TOOL = "Build Mapping Tool";
	public static final String BUILD_PLUGIN_MAPPING_TOOL = "Build plugin Mapping Tool";
	public static final String BUILD_VISUALIZATION = "Build Visualization";
	public static final String BUILD_PLUGIN_VISUALIZATION = "Build Plugin Visualization";
	public static final String BUILD_DOMEMODEL_OTHERVISUALIZATION_VIEW = "Build Dome model other visualization view";
	public static final String BUILD_PLUGINMODEL_OTHERVISUALIZATION_VIEW = "Build Plugin model other visualization view";
    public static final String BUILD_TOOL_OTHERVISUALIZATION_VIEW = "Build Tool model other visualization view";

	public static final String BUILD_PLAYSPACE = "Build Playspace";
	public static final String BUILD_PROJECT = "Build Project";
	public static final String BUILD_PROJECT_DEFINITION = "Build Project Definition";
	public static final String BUILD_PROJECT_DOCUMENTATION = "Build Project Documentation";
	public static final String BUILD_PROJECT_MAPPING_TOOL = "Build Project Mapping Tool";
    public static final String BUILD_ANALYSIS_TOOL_MAPPING_TOOL = "Build Analysis Tool Mapping Tool";

	public static final String BUILD_PROJECT_DOMEMODEL_DEFINITION = "Build Project DOME Model Definition";
    public static final String BUILD_TOOL_PROJECT_DOMEMODEL_DEFINITION = "Build Tool Project DOME Model Definition";
    public static final String BUILD_TOOL_PROJECT_STANDARD_VIEW = "Build Tool Project Standard View";
    public static final String BUILD_TOOL_PROJECT_OTHERVISUALIZATION_VIEW = "Build Tool Project Other Visualization View";
	public static final String BUILD_PROJECT_DOMEMODEL_OTHERVISUALIZATION_VIEW = "Build Project Dome model other visualization view";
	public static final String BUILD_PROJECT_PROCEDURALRELATION_DEFINITION = "Build Project Procedural Relation";
	public static final String BUILD_PROJECT_EQUALRELATION_DEFINITION = "Build Project Equal Relation";
    public static final String BUILD_PROJECT_ITERATIONRELATION_DEFINITION = "Build Project Iteration Relation";

	public static final String BUILD_PROJECT_STANDARD_VIEW = "Build Project Standard View";
	public static final String BUILD_PROJECT_VISUALIZATION = "Build Project Visualization";
	public static final String BUILD_PROJECT_INTERFACES = "Build Project Interfaces";
	public static final String BUILD_TOOL_PROJECT_INTERFACES = "Build Tool Project Interfaces";
	public static final String BUILD_PROJECTMODEL_INTERFACES = "Build Project Model Interfaces";
	public static final String BUILD_PROJECT_INTERFACE_CAUSALVIEW = "Build Project Interface CausalView";
	public static final String BUILD_PROJECT_INTERFACE_BUILDVIEW = "Build Project Interface BuildView";
	public static final String BUILD_PROJECT_INTERFACE_MODELVIEW = "Build Project Interface ModelView";
	public static final String BUILD_PROJECT_INTERFACE = "Build Project Interface";
    public static final String BUILD_ANALYSIS_TOOL_PROJECT_INTERFACE = "Build Analysis Tool Project Interface";
    public static final String BUILD_TOOL_INTERFACE = "Build Tool Interface";
    public static final String BUILD_DOME_TOOL_QMOO = "Build QMOO Model";

	public static final String DEPLOY_MODE = "Deploy Mode";
	public static final String DEPLOY_PLAYSPACE = "Deploy PlaySpace";
	public static final String DEPLOY_MODEL = "Deploy Model";

	public static final String RUN_DOMEMODEL_INTERFACE = "Run Interface";
	public static final String RUN_DOMEMODEL_INTERFACE_CAUSALVIEW = "Run Interface CausalView";
	public static final String RUN_DOMEMODEL_INTERFACE_BUILDVIEW = "Run Interface BuildView";
	public static final String RUN_DOMEMODEL_INTERFACE_MODELVIEW = "Run Interface ModelView";

    public static final String RUN_ANALYSIS_TOOL_INTERFACE = "Run Analysis Tool Interface";
    public static final String RUN_ANALYSIS_TOOL_INTERFACE_BUILDVIEW = "Run Analysis Tool Interface BuildView";
    public static final String RUN_ANALYSIS_TOOL_INTERFACE_CAUSALVIEW = "Run Analysis Tool Interface CausalView";
    public static final String RUN_ANALYSIS_TOOL_INTERFACE_RESULTS_VIEW = "Run Analysis Tool Interface Results View";

	public static final String RUN_MODE = "Run Mode";
	public static final String RUN_BROWSER = "Browse";
	public static final String RUN_BROWSER_MODEL_VIEW = "Browser in Model view";
	public static final String RUN_BROWSER_PLAYSPACE_VIEW = "Browser in Playspace view";
	public static final String RUN_PLAYSPACE = "Run Playspace";
	public static final String RUN_PROJECT = "Run Project";
    public static final String RUN_ANALYSIS_TOOL = "Run Analysis Tool";

	public static final String SERVER_MODE = "Server Mode";
	public static final String SERVER_ADMIN = "Administrator Server Options";
	public static final String SERVER_USER = "User Server Options";
	public static final String LOGGED_OFF = "Logged Off";

	public static final String RUN_VISUALIZATION = "Run Visualization";

}
