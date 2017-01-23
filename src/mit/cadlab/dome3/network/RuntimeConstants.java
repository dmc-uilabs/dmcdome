package mit.cadlab.dome3.network;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 28, 2003
 * Time: 4:29:32 PM
 * To change this template use Options | File Templates.
 */
public class RuntimeConstants
{
	// function handlers
	public static final String FUNC_TYPE_RUNTIME = "Runtime"; // both a client- and server-side handler

	// methods
	public static final String GET_MEMBERS = "getMembers";
	public static final String JOIN_PLAYSPACE = "joinPlayspace";
	public static final String CREATE_INTERFACE = "createInterface";
	public static final String CREATE_INTERFACE_QUICK = "createInterfaceQuick";
	public static final String START_INTERFACE_PARENT = "startInterfaceParent";
	public static final String GET_INTERFACE_STATUS = "getInterfaceStatus";
	public static final String CREATE_INTERFACE_AND_PLAYSPACE = "createInterfaceAndPlayspace";
    public static final String CREATE_PROJECT = "createProject";
    public static final String CREATE_ANALYSIS_TOOL = "createAnalysisTool";
	public static final String GET_TOOL_PROJECT_INFO = "getAnalysisToolProjectInfo";
	public static final String CREATE_DISTRIBUTED_PLAYSPACE_AND_IFACE = "createDistPlayspaceAndIface";
	public static final String RUN_MODEL = "runModel";
	public static final String SET_ITEM = "setItem";
	public static final String SET_ITEMS = "setItems";
    public static final String SET_ITEM_STATUS = "setItemStatus";
	public static final String GET_ITEM = "getItem";
	public static final String START_SOLVING = "startSolving";
	public static final String PAUSE_SOLVING = "pauseSolving";
	public static final String RESUME_SOLVING = "resumeSolving";
	public static final String KILL_SOLVING = "killSolving";
	public static final String LEAVE_PLAYSPACE = "leavePlayspace";
	public static final String CLOSE_PROJECT = "closeProject";
	public static final String KILL_INTERFACE_PARENT = "killInterfaceParent";
	public static final String KILL_PROJECT = "killProject";
	public static final String KILL_TOOL = "killTool";
    public static final String GET_FILE_CONTENT = "getFileContent";
    public static final String GET_PARAMETER_SYSTEM_CAUSALITY = "getParameterSystemCausality";
	public static final String GET_CUSTOM_GUI_JAR = "getCustomGuiJar";
	public static final String GET_FILE = "getFile";
	
	// server peer methods
	public static final String CREATE_REMOTE_MODEL = "createRemoteModel";
	public static final String CREATE_REMOTE_PROJECT = "createRemoteProject";

	// messages
	public static final String MESSAGE_ITEM_VALUE_CHANGED = "messageItemValueChanged";
	public static final String MESSAGE_ITEM_STATUS_CHANGED = "messageItemStatusChanged";
	public static final String MESSAGE_PARAMETERS_INCONSISTENT = "messageParametersInconsistent";
	public static final String MESSAGE_MODEL_STATUS_CHANGED = "messageModelStatusChanged";
    public static final String MESSAGE_MODEL_EXECUTION_ERROR = "messageModelExecutionError";
	public static final String MESSAGE_MODEL_STARTUP_ERROR = "messageModelStartupError";
	public static final String MESSAGE_GET_FILE_REQUEST = "messageGetFileRequest";
	

    // analysis tool methods
    public static final String CREATE_ANALYSIS_TOOL_INTERFACE = "createAnalysisToolInterface";
    public static final String START_ANALYSIS_TOOL_SOLVING = "startAnalysisToolSolving";
    public static final String SET_PROJECT_INSIDE_ANALYSIS_TOOL_ITEMS = "setProjectInsideAnalysisToolItems";
    public static final String PASS_INDIVIDUAL_TO_CLIENT = "passIndividualToClient";
    public static final String PREPARE_PLOT_FOR_NEXT_GENERATION = "preparePlotForNextGeneration";
    public static final String OPTIMIZATION_ANALYSIS_IS_COMPLETE = "optimizationAnalysisIsComplete";

	public static final Vector NO_VECTOR = new Vector();
}
