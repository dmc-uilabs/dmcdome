package mit.cadlab.dome3.network.server.db;

/**
 * Created by IntelliJ IDEA.
 * Name: DbErrors
 * User: thorek
 * Date: Mar 18, 2003
 * Time: 11:11:25 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class DbErrors
{
	// XML-RPC error codes
	public static final int XMLRPC_DB_ERROR = 0;
	public static final int XMLRPC_NO_SUCH_METHOD = 1;
	// argument lists
	public static final int XMLRPC_INVALID_ARGUMENT_LIST = 100;
	public static final int XMLRPC_INVALID_NUMBER_OF_ARGUMENTS = 101;
	// users
	public static final int XMLRPC_NO_USERS = 200;
	public static final int XMLRPC_NO_SUCH_USER_GROUP = 201;
	public static final int XMLRPC_DUPLICATE_USER = 202;
	public static final int XMLRPC_EMPTY_PASSWORD = 203;
	public static final int XMLRPC_EMPTY_CLIENT_URL = 204;
	public static final int XMLRPC_LOGIN_FAILED = 205;
	public static final int XMLRPC_INCORRECT_PASSWORD = 206;
	public static final int XMLRPC_BAD_SESSION_ID = 207;
	public static final int XMLRPC_NO_SUCH_USER_GROUP_OR_HAS_NO_FOLDER = 208;
	// groups
	public static final int XMLRPC_NO_GROUPS = 300;
	public static final int XMLRPC_DUPLICATE_GROUP = 301;
	// folders
	public static final int XMLRPC_NO_SUCH_FOLDER = 400;
	public static final int XMLRPC_EMPTY_FOLDER_NAME = 401;
	public static final int XMLRPC_FOLDER_EXISTS = 402;
	public static final int XMLRPC_FOLDER_NOT_EMPTY = 403;
	public static final int XMLRPC_CAN_NOT_RENAME_PUBLIC_OR_PRIVATE_FOLDER = 404;
	// interfaces
	public static final int XMLRPC_NO_SUCH_INTERFACE = 500;
	public static final int XMLRPC_INTERFACE_INVOCATION_ERROR = 501;
	public static final int XMLRPC_NO_INTERFACE_PARENT = 502;
	// models
	public static final int XMLRPC_NO_SUCH_MODEL = 600;
	public static final int XMLRPC_MODEL_INVOCATION_ERROR = 601;
	// playspaces
	public static final int XMLRPC_NO_SUCH_PLAYSPACE = 700;
	public static final int XMLRPC_PLAYSPACE_INVOCATION_ERROR = 701;
	// connections
	public static final int XMLRPC_CLIENT_CONNECTION_ERROR = 800;
	// permissions
	public static final int XMLRPC_SET_PERMISSION_ERROR = 900;
	// projects
	public static final int XMLRPC_NO_SUCH_PROJECT = 1000;
	public static final int XMLRPC_PROJECT_INVOCATION_ERROR = 1001;
	// integration models
	public static final int XMLRPC_NO_SUCH_INTEGRATION_MODEL = 1100;
	// deploy
	public static final int XMLRPC_DEPLOY_ERROR = 1200;
	// content
	public static final int XMLRPC_NO_XML_DESCRIPTION = 1300;
	public static final int XMLRPC_NO_STATIC_DESCRIPTION = 1301;
    // analysis tool
    public static final int XMLRPC_NO_SUCH_ANALYSIS_TOOL = 1400;
    public static final int XMLRPC_ANALYSIS_TOOL_INVOCATION_ERROR = 1401;




	// XML-RPC error messages
	public static final String XMLRPC_NO_SUCH_USER_GROUP_MSG = "No such user or group";
	public static final String XMLRPC_NO_SUCH_GROUP_MSG = "No such group";
	public static final String XMLRPC_DB_ERROR_MSG = "Database error";
	public static final String XMLRPC_NO_SUCH_METHOD_MSG = "No such method";
	public static final String XMLRPC_INVALID_ARGUMENT_LIST_MSG = "Invalid argument list";
	public static final String XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG = "Invalid number of arguments";
	public static final String XMLRPC_NO_USERS_MSG = "No users exist";
	public static final String XMLRPC_DUPLICATE_USER_MSG = "Duplicate user";
	public static final String XMLRPC_INCORRECT_PASSWORD_MSG = "Incorrect password";
	public static final String XMLRPC_EMPTY_PASSWORD_MSG = "Password is empty";
	public static final String XMLRPC_EMPTY_CLIENT_URL_MSG = "Client URL is empty";
	public static final String XMLRPC_LOGIN_FAILED_MSG = "Login failed";
	public static final String XMLRPC_BAD_SESSION_ID_MSG = "Invalid sessions ID";
	public static final String XMLRPC_NO_SUCH_USER_GROUP_OR_HAS_NO_FOLDER_MSG = "No such user or group / user or group has no folder";
	public static final String XMLRPC_NO_SUCH_FOLDER_MSG = "No such folder";
	public static final String XMLRPC_EMPTY_FOLDER_NAME_MSG = "Unknown folder name";
	public static final String XMLRPC_FOLDER_EXISTS_MSG = "Folder exists";
	public static final String XMLRPC_FOLDER_NOT_EMPTY_MSG = "Folder is not empty";
	public static final String XMLRPC_DUPLICATE_GROUP_MSG = "Duplicate group";
	public static final String XMLRPC_NO_GROUPS_MSG = "No groups exist";
	public static final String XMLRPC_CAN_NOT_RENAME_PUBLIC_OR_PRIVATE_FOLDER_MSG = "Can't rename public or privaye folder";
	public static final String XMLRPC_NO_SUCH_INTERFACE_MSG = "No such interface";
	public static final String XMLRPC_INTERFACE_INVOCATION_MSG = "Interface invocation error";
	public static final String XMLRPC_NO_SUCH_MODEL_MSG = "No such model";
	public static final String XMLRPC_MODEL_INVOCATION_MSG = "Model invocation error";
	public static final String XMLRPC_NO_SUCH_PLAYSPACE_MSG = "No such playspace";
	public static final String XMLRPC_PLAYSPACE_INVOCATION_MSG = "ServerPlayspace invocation error";
	public static final String XMLRPC_NO_SUCH_PROJECT_MSG = "No such project";
	public static final String XMLRPC_PROJECT_INVOCATION_MSG = "Project invocation error";
    public static final String XMLRPC_ANALYSIS_TOOL_INVOCATION_MSG = "analysis tool invocation error";
	public static final String XMLRPC_NO_SUCH_INTEGRATION_MODEL_MSG = "No such integration model";
	public static final String XMLRPC_INTEGRATION_MODEL_INVOCATION_MSG = "Integration model invocation error";
	public static final String XMLRPC_NO_INTERFACE_PARENT_MSG = "Interface has no parent object";
	public static final String XMLRPC_NO_XML_DESCRIPTION_MSG = "No xml description";
	public static final String XMLRPC_NO_STATIC_DESCRIPTION_MSG = "No static description";
    public static final String XMLRPC_NO_SUCH_ANALYSIS_TOOL_MSG = "No such analysis tool";
}
