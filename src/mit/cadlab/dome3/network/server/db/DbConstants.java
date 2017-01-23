// DbConstants.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.db;

import mit.cadlab.dome3.plugin.excel.ExcelConfiguration;
import mit.cadlab.dome3.plugin.matlab.MatlabConfiguration;
import mit.cadlab.dome3.plugin.mathematica.MathematicaConfiguration;
import mit.cadlab.dome3.plugin.Solidworks.SolidworksConfiguration;
import mit.cadlab.dome3.plugin.ideas.IdeasConfiguration;
import mit.cadlab.dome3.plugin.ug.UgConfiguration;
import mit.cadlab.dome3.plugin.catia.CATIAConfiguration;
import mit.cadlab.dome3.plugin.abaqus.AbaqusConfiguration;
import mit.cadlab.dome3.plugin.batch.nameValue.NameValueConfiguration;
import mit.cadlab.dome3.plugin.adams.AdamsConfiguration;
import mit.cadlab.dome3.plugin.nastran.NastranConfiguration;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Configuration;
import mit.cadlab.dome3.plugin.mathcad.MathcadConfiguration;
import mit.cadlab.dome3.plugin.groovy.GroovyConfiguration;
import mit.cadlab.dome3.plugin.vensim.VensimConfiguration;
import mit.cadlab.dome3.plugin.catalog.CatalogConfiguration;
import mit.cadlab.dome3.plugin.extendsim.ExtendSimConfiguration;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import java.util.Vector;

/**
 * This interface defines string constants representing services provided
 * by the Dome Server.
 */
public class DbConstants
{

	public static final Vector EMPTY_VECTOR = new Vector();

	// redefinition of NULL -- used because XML-RPC does not support
	// the NULL value and we would like a NULL representation that is
	// consistent across the client, server and database modules
	public static final int NULL = -1;

	public static final int GUEST_USER_ID = 2;
	public static final int ADMIN_GROUP_ID = 1;

    public static final Integer NULL_FOLDER_ID = new Integer(-1);

	// constants that map to object types for retrieving object xml descriptions
	public static final int MODEL = 0;
	public static final int PROJECT = 1;
	public static final int INTERFACE = 2;
	public static final int INTEGRATION_MODEL = 3;
	public static final int PLAYSPACE = 4;
	public static final String GET_OBJECT_XML_DESCRIPTION = "getObjectDescription";

	// interface parent types
	public static final String IFACE_PARENT_TYPE_MODEL = "MODELS";
	public static final String IFACE_PARENT_TYPE_PROJECT = "PROJECTS";
	public static final String IFACE_PARENT_TYPE_IMODEL = "INTEGRATION_MODELS";
    public static final String IFACE_PARENT_TYPE_ANALYSIS_TOOL = "ANALYSIS_TOOLS";

	// user/group types
	public static final String USER_TYPE = "U";
	public static final String GROUP_TYPE = "G";
	public static final String USER_GROUP_STATUS_ACTIVE = "ACTIVE";
	public static final String USER_GROUP_STATUS_INACTIVE = "INACTIVE";

	// playspace/model types
	public static final String MODEL_TYPE = "MODEL";
	public static final String PLAYSPACE_TYPE = "PLAYSPACE";
	public static final String PROJECT_TYPE = "PROJECT";
    public static final String ANALYSIS_TOOL_TYPE = "ANALYSIS_TOOL";
	public static final String INTEGRATION_MODEL_TYPE = "INTEGRATION_MODEL";
	public static final String INTERFACE_TYPE = "INTERFACE";
	public static final String AUX_FILE_TYPE = "AUX_FILE";

	// model types
	public static final String MODEL_TYPE_DOME = "DOME";
	public static final String MODEL_TYPE_CATIA = CATIAConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_EXCEL = ExcelConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_MATLAB = MatlabConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_MATHEMATICA = MathematicaConfiguration.TYPE_INFO.getXmlType();
	//public static final String MODEL_TYPE_MAXWELL = MaxwellConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_SOLIDWORKS = SolidworksConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_IDEAS8 = IdeasConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_UNIGRAPHICS = UgConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_ABAQUS = AbaqusConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_NAMEVALUE = NameValueConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_ADAMS = AdamsConfiguration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_NASTRAN = NastranConfiguration.TYPE_INFO.getXmlType();
    public static final String MODEL_TYPE_OPTIMIZATION = QMOOConfiguration.TYPE_INFO.getXmlType();
    public static final String MODEL_TYPE_IDEAS10 = Ideas10Configuration.TYPE_INFO.getXmlType();
	public static final String MODEL_TYPE_MATHCAD = MathcadConfiguration.TYPE_INFO.getXmlType();
    public static final String MODEL_TYPE_GROOVY = GroovyConfiguration.TYPE_INFO.getXmlType();
    public static final String MODEL_TYPE_CATALOG = CatalogConfiguration.TYPE_INFO.getXmlType();
    public static final String MODEL_TYPE_VENSIM = VensimConfiguration.TYPE_INFO.getXmlType();
    public static final String MODEL_TYPE_EXTENDSIM = ExtendSimConfiguration.TYPE_INFO.getXmlType();

	// permission types
	public static final String MODEL_PROJECT_EDIT = "MODEL OR iPROJECT EDITING PRIVILEGES";
	public static final String INTERFACE_USE = "INTERFACE USE PRIVILEGES";
	public static final String PLAYSPACE_EDIT = "PLAYSPACE EDITING PRIVILEGES";
	public static final String PLAYSPACE_USE = "PLAYSPACE USE PRIVILEGES";
	public static final String PROJECT_VISIBILITY = "PROJECT VISIBILITY PRIVILEGES";
    public static final String ANALYSIS_TOOL_EDIT = "ANALYSIS TOOL EDITING PRIVILEGES";

	// session status types
	public static final String SESSION_STATUS_ACTIVE = "ACTIVE";
	public static final String SESSION_STATUS_LOGGED_OUT = "LOGGED_OUT";
	public static final String SESSION_STATUS_EXPIRED = "EXPIRED";

	// login types
	public static final String LOGIN_TYPE_ADMIN = "ADMIN";
	public static final String LOGIN_TYPE_USER = "USER";
	public static final String LOGIN_TYPE_GUEST = "GUEST";

	// interface status types
	public static final String INTERFACE_STATUS_AVAILABLE = "AVAILABLE";
	public static final String INTERFACE_STATUS_UNAVAILABLE = "UNAVAILABLE";
	public static final String INTERFACE_STATUS_DELETE = "DELETE";

	// file event types
	public static final String FILE_EVENT_UPLOAD = "UPLOAD";
	public static final String FILE_EVENT_DOWNLOAD = "DOWNLOAD";

	// folder types
	public static final String MODEL_FOLDER_TYPE = "MODEL_FOLDER";
	public static final String PLAYSPACE_FOLDER_TYPE = "PLAYSPACE_FOLDER";

	// function handlers
	public static final String FUNC_TYPE_CLIENT = "Client";
	public static final String FUNC_TYPE_USER_GROUP = "UserGroup";
	public static final String FUNC_TYPE_FILESYSTEM = "FileSystem";
	public static final String FUNC_TYPE_DEPLOYFILESYSTEM = "DeployFileSystem";
	public static final String FUNC_TYPE_BROWSEFILESYSTEM = "BrowseFileSystem";
	public static final String FUNC_TYPE_SERVER_ADMIN = "ServerAdmin";
	public static final String FUNC_TYPE_DB_MAINTENANCE = "DbMaintenance";
	public static final String FUNC_TYPE_PERMISSION = "Permission";
	public static final String FUNC_TYPE_DEPLOY_FILES = "DeployFiles";
	public static final String FUNC_TYPE_CHECKOUT_FILES = "CheckOut";
	public static final String FUNC_TYPE_CONFERENCE_SERVER = "ConferenceServer";
	public static final String FUNC_TYPE_CONFERENCE_CLIENT = "ConferenceClient";
	public static final String FUNC_TYPE_SERVER_PEER = "ServerPeer";

	// server administration methods
	public static final String SHUTDOWN = "shutdown";

	// client methods
	public static final String LOGIN_ADMIN = "loginAdmin";
	public static final String LOGIN_USER = "loginUser";
	public static final String LOGIN_GUEST = "loginGuest";
	public static final String LOGOUT = "logout";

	//user and group methods
	public static final String GET_SIMPLE_ACTIVE_USERS_AND_GROUPS_LIST = "getSimpleActiveUsersAndGroupsList";
	public static final String GET_SIMPLE_USER_LIST = "getSimpleUserList";
	public static final String GET_DETAILED_USER_LIST = "getDetailedUserList";
	public static final String GET_SIMPLE_GROUP_LIST = "getSimpleGroupList";
	public static final String GET_DETAILED_GROUP_LIST = "getDetailedGroupList";
	public static final String GET_USER_GROUP_INFO = "getUserGroupInfo";
	public static final String GET_GROUPS_FOR_USER = "getGroupsForUser";
	public static final String DELETE_USER_GROUP = "deleteUserGroup";
	public static final String CREATE_NEW_GROUP = "createNewGroup";
	public static final String CREATE_NEW_USER = "createNewUser";
	public static final String EDIT_USER_GROUP_INFO = "editUserGroupInfo";
	public static final String EDIT_GROUPS_FOR_USER = "editUserGroups";
	public static final String GET_MEMBERS_FOR_GROUP = "getMembersForGroup";
	public static final String EDIT_MEMBERS_FOR_GROUP = "editMembersForGroup";
	public static final String CHANGE_USER_PASSWORD = "changeUserPassword";
	public static final String GET_USER_ID_FROM_SESSION = "getUserIdFromSession";
	public static final String SESSION_USER_HAS_SAVE_PERMISSION = "sessionUserHasSavePermission";
	public static final String USER_OR_GROUP_HAS_SAVE_PERMISSION = "userOrGroupHasSavePermission";
	public static final String USER_IN_GROUP_WITH_SAVE_PERMISSION = "userInGroupWithSavePermission";

	// folder methods
	public static final String GET_PARENT_ID = "getParentDbId";
	public static final String CREATE_FOLDER = "createFolder";
	public static final String DELETE_FOLDER = "deleteFolder";
	public static final String RENAME_FOLDER = "renameFolder";
	public static final String MOVE_FOLDER = "moveFolder";
	public static final String GET_USER_GROUP_FOLDERS = "getUserGroupFolders";

	public static final String GET_PLAYSPACE_FOLDER_CONTENTS = "getPlayspaceFolderContents";
	public static final String GET_MODEL_FOLDER_CONTENTS = "getModelFolderContents";
	public static final String GET_PROJECTS_FOLDER_CONTENTS = "getProjectsFolderContents";
	public static final String GET_SUBSCRIPTION_MODEL_FOLDER_CONTENTS = "getSubscriptionModelFolderContents";
    public static final String GET_ANALYSIS_TOOL_CONTENTS = "getAnalysisToolContents";
    public static final String GET_PROJECT_INSIDE_ANALYSIS_TOOL = "getProjectInsideAnalysisTool";
    public static final String GET_PROJECT_ID_INSIDE_ANALYSIS_TOOL = "getProjectIdInsideAnalysisTool";

	// model and interface methods
	public static final String UPDATE_MODEL = "updateModel";
	public static final String UPDATE_INTERFACE = "updateInterface";
	public static final String ADD_NEW_MODEL = "addNewModel";
	public static final String REDEPLOY_MODEL = "redeployModel";
	public static final String DELETE_MODEL = "deleteModel";
    public static final String DELETE_ANALYSIS_TOOL = "deleteAnalysisTool";
	public static final String DELETE_PLAYSPACE = "deletePlayspace";
	public static final String GET_LAST_MODIFIED = "getLastModified";
	public static final String GET_INTERFACES = "getInterfaces";
	public static final String GET_AVAILABLE_INTERFACES = "getAvailableInterfaces";
	public static final String GET_AVAILABLE_INTERFACES_INFO = "getAvailableInterfacesInfo";
	public static final String GET_AVAILABLE_INTERFACE_IDS = "getAvailableInterfaceIds";
	public static final String GET_INTERFACE_DESCRIPTION = "getInterfaceDescription";
	public static final String GET_INTERFACE_VERSION = "getInterfaceVersion";
	public static final String GET_PLAYSPACE_DESCRIPTION = "getPlayspaceDescription";
	public static final String GET_PLAYSPACE_INFO = "getPlayspaceInfo";
	public static final String GET_PLAYSPACE_VERSION_INFO = "getPlayspaceVersionInfo";
	public static final String GET_PROJECT_DESCRIPTION = "getProjectDescription";
	public static final String GET_MODEL_DESCRIPTION = "getModelDescription";
	public static final String GET_INTEGRATION_MODEL_DESCRIPTION = "getProjectDescription";
	public static final String GET_MODEL_VERSION_DATA = "getModelVersionData";
	public static final String GET_PROJECT_VERSION_DATA = "getProjectVersionData";
    public static final String GET_ANALYSIS_TOOL_VERSION_DATA = "getAnalysisToolVersionData";
	public static final String GET_MODEL_INFO = "getModelInfo";
	public static final String UPLOAD_AUXFILE = "auxiliary file upload";
    public static final String GET_AUXFILE_INFO ="get auxfile info";
    public static final String GET_CUSTOMGUIFILE_INFO ="get customGuiFile info";
    public static final String UPLOAD_CUSTOMGUI_FILE="upload customGuiFiles";

	// playspace methods
	public static final String UPDATE_PLAYSPACE = "updatePlayspace";
	public static final String ADD_NEW_PLAYSPACE = "addNewPlayspace";
	public static final String REDEPLOY_PLAYSPACE = "redeployPlayspace";

	// project methods
	public static final String UPDATE_PROJECT = "updateProject";
	public static final String ADD_NEW_PROJECT = "addNewProject";
	public static final String REDEPLOY_PROJECT = "redeployProject";
    public static final String REDEPLOY_ANALYSIS_TOOL = "redeployAnalysisTool";
	public static final String GET_AVAILABLE_PROJECT_CONTENTS = "getAvailableProjectContents";
	public static final String GET_PROJECT_INFO = "getProjectInfo";
    public static final String GET_ANALYSIS_TOOL_INFO = "getAnalysisToolInfo";
	public static final String DELETE_PROJECT = "deleteProject";

    // analysis tool methods
    public static final String ADD_NEW_ANALYSIS_TOOL = "addNewAnalysisTool";
    public static final String INSERT_ANALYSIS_TOOL_PROJECT_ID = "insertAnalysisToolProjectId";
    public static final String HAS_ANALYSIS_TOOL_PROTOTYPE_FOLDER_BEEN_CREATED = "hasAnalysisToolPrototypeFolderBeenCreated";

	// space methods
	public static final String GET_USER_SPACE = "getUserSpace";
	public static final String GET_USER_SPACE_FOR_SESSION = "getUserSpaceForSession";
	public static final String GET_USER_PLAYSPACE_FOR_SESSION = "getUserPlayspaceForSession";
	public static final String GET_GROUP_SPACE = "getGroupSpace";
	public static final String GET_SERVER_SPACE = "getServerSpace";
	public static final String GET_USER_GROUP_SPACES_LIST = "getUserGroupSpacesList";
	public static final String GET_GROUP_SPACE_NO_MEMBERSHIP_CHECK = "getGroupSpaceNoMembershipCheck";
	public static final String GET_SERVER_SPACE_NO_MEMBERSHIP_CHECK = "getServerSpaceNoMembershipCheck";
	public static final String GET_PATH_FOR_MODEL = "getPathForModel";
	public static final String GET_PATH_FOR_PROJECT = "getPathForProject";
	public static final String GET_PATH_FOR_ANALYSIS_TOOL = "getPathForAnalysisTool";
    public static final String GET_ROOT_FOR_MODEL = "getRootForModel";
	public static final String GET_PATH_FOR_PLAYSPACE = "getPathForPlayspace";
	public static final String GET_ROOT_FOR_PLAYSPACE = "getRootForPlayspace";
	public static final String GET_ROOT_FOR_PROJECT = "getRootForProject";
    public static final String GET_ROOT_FOR_ANALYSIS_TOOL = "getRootForAnalysisTool";

	//permissions
	public static final String GET_PERMISSION_CATEGORY_INFO = "getPermissionCategoryInfo";
	public static final String GET_OBJECT_PERMISSION_INFO = "getObjectPermissionInfo";
	public static final String SET_OBJECT_PERMISSIONS = "setObjectPermissions";
	public static final String USER_HAS_PERMISSION = "userHasPermission";
	public static final String SESSION_USER_HAS_PERMISSION = "sessionUserHasPermission";
	public static final String GET_PLAYSPACE_MEMBER = "getPlayspaceMembers";
	public static final String GET_PLAYSPACE_USER_MEMBER = "getPlayspaceUserMembers";


	//DOME mit.cadlab.dome3.network.conference methods passed from a mit.cadlab.dome3.network.conference member to the server
	public static final String JOIN_CONFERENCE = "joinConference";
	public static final String LEAVE_CONFERENCE = "leaveConference";
	public static final String WHO_IS_IN_THE_CONFERENCE_ROOM = "whoIsInTheConferenceRoom";
	public static final String SEND_MESSAGE_TO_EVERYONE = "sendMessageToEveryone";
	public static final String SEND_MESSAGE_TO_MEMBER = "sendMessageToMember";

	//DOME mit.cadlab.dome3.network.conference methods passed from the server to a mit.cadlab.dome3.network.conference member
	public static final String INFORM_CONFERENCE_OF_NEW_MEMBER = "informConferenceOfNewMember";
	public static final String SEND_MESSAGE_TO_CONFERENCE_MEMBER = "sendMessageToConferenceMembers";
	public static final String INFORM_CONFERENCE_OF_OLD_MEMBER = "informConferenceOfOldMember";

	// checkout methods
	public static final String CHECKOUT_MODEL = "checkoutModel";
    public static final String CHECKOUT_AUXFILE_FOR_MODEL = "checkoutAuxFileForModel";
	public static final String CHECKOUT_PLAYSPACE = "checkoutPlayspace";
	public static final String CHECKOUT_PROJECT = "checkoutProject";
	// message
	public static final String MSG_ISALIVE = "IsAlive";

	// database message
	public static final String TABLE_EXISTS = "S0001";
	public static final String SINGLE_VALUE = "37000";  // single value expected in a select statement
	// error occurs if 0 or >1 values provided
	public static final String UNIQUE_INDEX = "23000";   // trying to insert a record that already exists

	// filesystem modes
	public static final String FILESYSTEM_BROWSE = "filesystem browse";
	public static final String FILESYSTEM_SUBSCRIBE = "filesystem subscribe";



}
