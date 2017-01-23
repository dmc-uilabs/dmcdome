// DbInit.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.db;

import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.functions.UserGroupDbFunctions;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.util.FileUtils;
import org.apache.xmlrpc.XmlRpcException;

import java.awt.Dimension;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class initialises the database.
 * This gets called only once, typically when the DOME server is installed
 *
 * Notes:
 * Permissions for objects must be removed deliberately when object is deleted.
 */
public class DbInit
{
	private static String m_dbFile = "DOMEdB"; // default value

	private static String createConstantsTables[] = new String[]
	{
		// interface parent types
		"create table INTERFACE_PARENT_TABLE_NAMES (TABLE_NAME varchar primary key)",
		"insert into INTERFACE_PARENT_TABLE_NAMES values ('" + DbConstants.IFACE_PARENT_TYPE_MODEL + "')",
		"insert into INTERFACE_PARENT_TABLE_NAMES values ('" + DbConstants.IFACE_PARENT_TYPE_PROJECT + "')",
		"insert into INTERFACE_PARENT_TABLE_NAMES values ('" + DbConstants.IFACE_PARENT_TYPE_IMODEL + "')",
        "insert into INTERFACE_PARENT_TABLE_NAMES values ('" + DbConstants.IFACE_PARENT_TYPE_ANALYSIS_TOOL + "')",

		// user group types
		"create table USERS_GROUPS_TYPES (TYPE varchar primary key)",
		"insert into USERS_GROUPS_TYPES values ('" + DbConstants.USER_TYPE + "')",
		"insert into USERS_GROUPS_TYPES values ('" + DbConstants.GROUP_TYPE + "')",

		// create table for valid status types for users and groups
		"create table USERS_GROUPS_STATUS_TYPES (STATUS varchar primary key)",
		"insert into USERS_GROUPS_STATUS_TYPES values ('" + DbConstants.USER_GROUP_STATUS_ACTIVE + "')",
		"insert into USERS_GROUPS_STATUS_TYPES values ('" + DbConstants.USER_GROUP_STATUS_INACTIVE + "')",

		// create table for valid model types
		"create table MODEL_TYPES (TYPES varchar primary key)",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_CATIA + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_DOME + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_EXCEL + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_MATLAB + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_MATHCAD + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_MATHEMATICA + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_SOLIDWORKS + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_IDEAS8 + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_IDEAS10 + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_UNIGRAPHICS + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_ABAQUS + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_NAMEVALUE + "')",
		"insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_ADAMS + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_NASTRAN + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_OPTIMIZATION + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_GROOVY + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_CATALOG + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_VENSIM + "')",
        "insert into MODEL_TYPES values ('" + DbConstants.MODEL_TYPE_EXTENDSIM + "')",


		// Note: the category names are used in constants in PermissionUtils. Edit there if changed here.
		"create table PERMISSION_CATEGORIES (ID int identity,NAME varchar not null)",
		"insert into PERMISSION_CATEGORIES (ID, NAME) values (0,'" + DbConstants.MODEL_PROJECT_EDIT + "')",
		"insert into PERMISSION_CATEGORIES (ID, NAME) values (1,'" + DbConstants.INTERFACE_USE + "')",
		"insert into PERMISSION_CATEGORIES (ID, NAME) values (2,'" + DbConstants.PLAYSPACE_EDIT + "')",
		"insert into PERMISSION_CATEGORIES (ID, NAME) values (3,'" + DbConstants.PLAYSPACE_USE + "')",
		"insert into PERMISSION_CATEGORIES (ID, NAME) values (4,'" + DbConstants.PROJECT_VISIBILITY + "')",
        "insert into PERMISSION_CATEGORIES (ID, NAME) values (5,'" + DbConstants.ANALYSIS_TOOL_EDIT + "')",

		// create table for valid permission types
		"create table PERMISSION_TYPES (ID int identity,NAME varchar not null,CATEGORY_ID int not null)",
		"alter table PERMISSION_TYPES add constraint VALID_CATEGORY_ID foreign key(CATEGORY_ID) references PERMISSION_CATEGORIES(ID) on delete cascade",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Set model or iProject edit permissions',0)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Delete model or iProject',0)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Modify model or iProject',0)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Copy model or iProject',0)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Set interface use privilege',0)",

		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Subscribe to interface',1)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('View and run in a new playspace',1)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Save in a new playspace',1)",

		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Set playspace editing permissions',2)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Delete playspace',2)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Modify playspace',2)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Copy playspace',2)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Set playspace use privilege',2)",

		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Change values',3)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Save state versions',3)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Open as separate playspace',3)",

		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('May set iProject content visibility permissions',4)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('May set interface use permissions for iModels in project',4)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('May see contents while subscribing',4)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('May see contents while in run mode',4)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('May see contents and run iProject in new playspace',4)",
		"insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('May see contents and save iProject in new playspace',4)",

        "insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('set analysis tool edit permissions',5)",
        "insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('delete analysis tool',5)",
        "insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('modify analysis tool',5)",
        "insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('copy analysis tool',5)",
        "insert into PERMISSION_TYPES (NAME,CATEGORY_ID)values ('Set interface use privilege',5)",

		//(a,b) if a is allowed, b must be allowed,
		"create table PERMISSION_LINKS (PERMISSION_ID int not null,PERMISSION_DEPENDENT_ID int not null)",
		"alter table PERMISSION_LINKS add constraint VALID_PERMISSION_ID foreign key(PERMISSION_ID) references PERMISSION_TYPES(ID) on delete cascade",
		"alter table PERMISSION_LINKS add constraint VALID_PERMISSION_DEPENDENT_ID foreign key(PERMISSION_DEPENDENT_ID) references PERMISSION_TYPES(ID) on delete cascade",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (4,2)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (4,3)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (2,3)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (7,6)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (12,10)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (12,11)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (10,11)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (20,19)",
		"insert into PERMISSION_LINKS (PERMISSION_ID,PERMISSION_DEPENDENT_ID) values (21,19)",

		// create table for valid session status types
		"create table SESSION_STATUS_TYPES (STATUS varchar primary key)",
		"insert into SESSION_STATUS_TYPES values('" + DbConstants.SESSION_STATUS_ACTIVE + "')",
		"insert into SESSION_STATUS_TYPES values('" + DbConstants.SESSION_STATUS_LOGGED_OUT + "')",
		"insert into SESSION_STATUS_TYPES values('" + DbConstants.SESSION_STATUS_EXPIRED + "')",

		// create table for valid login types
		"create table LOGIN_TYPES (TYPE varchar primary key)",
		"insert into LOGIN_TYPES values('" + DbConstants.LOGIN_TYPE_ADMIN + "')",
		"insert into LOGIN_TYPES values('" + DbConstants.LOGIN_TYPE_USER + "')",
		"insert into LOGIN_TYPES values('" + DbConstants.LOGIN_TYPE_GUEST + "')",

		// create table for possible interface status types
		"create table INTERFACE_STATUS_TYPES (TYPE varchar primary key)",
		"insert into INTERFACE_STATUS_TYPES values('" + DbConstants.INTERFACE_STATUS_AVAILABLE + "')",
		"insert into INTERFACE_STATUS_TYPES values('" + DbConstants.INTERFACE_STATUS_UNAVAILABLE + "')",
		"insert into INTERFACE_STATUS_TYPES values('" + DbConstants.INTERFACE_STATUS_DELETE + "')",

		// create table for valid file event types
		"create table FILE_EVENT_TYPES (TYPE varchar primary key)",
		"insert into FILE_EVENT_TYPES values('" + DbConstants.FILE_EVENT_UPLOAD + "')",
		"insert into FILE_EVENT_TYPES values('" + DbConstants.FILE_EVENT_DOWNLOAD + "')",
	};

	private static String createDataTables[] = new String[]
	{
		// users and groups
		"create table USERS_GROUPS (ID int identity, TYPE varchar not null, STATUS varchar default '" + DbConstants.USER_GROUP_STATUS_ACTIVE
	        + "' not null, NAME varchar not null, DESCRIPTION varchar default '', PASSWORD varbinary, CAN_SAVE_MODEL bit default false, CAN_SAVE_PLAYSPACE bit default false)",
		"alter table USERS_GROUPS add constraint UNIQUE_NAME unique(NAME)",
		"alter table USERS_GROUPS add constraint VALID_USER_GROUP_TYPE foreign key(TYPE) references USERS_GROUPS_TYPES",
		"alter table USERS_GROUPS add constraint VALID_USER_GROUP_STATUS foreign key(STATUS) references USERS_GROUPS_STATUS_TYPES",

		// group membership
		"create table GROUP_MEMBERSHIP (GROUP_ID int not null, MEMBER_ID int not null)",
		"alter table GROUP_MEMBERSHIP add constraint UNIQUE_MEMBERS unique (GROUP_ID,MEMBER_ID)",
		"alter table GROUP_MEMBERSHIP add constraint VALID_GROUP_ID foreign key(GROUP_ID) references USERS_GROUPS(ID) on delete cascade",
		"alter table GROUP_MEMBERSHIP add constraint VALID_MEMBER_ID foreign key(MEMBER_ID) references USERS_GROUPS(ID) on delete cascade",

		// sessions
		"create table SESSIONS (ID varchar primary key, USER_ID int, URL varchar not null, TYPE varchar not null, STATUS varchar default '" + DbConstants.SESSION_STATUS_ACTIVE
	        + "' not null, START_TIME timestamp default 'now', LAST_ACTIVITY timestamp default 'now')",
		"alter table SESSIONS add constraint VALID_USER_ID foreign key(USER_ID) references USERS_GROUPS(ID) on delete cascade",
		"alter table SESSIONS add constraint VALID_SESSION_STATUS foreign key(STATUS) references SESSION_STATUS_TYPES",
		"alter table SESSIONS add constraint VALID_LOGIN_TYPE foreign key(TYPE) references LOGIN_TYPES",

		// model folders
		"create table MODEL_FOLDERS (ID int identity, NAME varchar not null, PARENT_ID int default '" + DbConstants.NULL + "')",
		"alter table MODEL_FOLDERS add constraint UNIQUE_NAME unique(NAME,PARENT_ID)",
		"insert into MODEL_FOLDERS (ID, NAME) values ('" + DbConstants.NULL + "', 'NullFolder')",

		//playspace folders
		"create table PLAYSPACE_FOLDERS (ID int identity, NAME varchar not null, PARENT_ID int default '" + DbConstants.NULL + "')",
		"alter table PLAYSPACE_FOLDERS add constraint UNIQUE_NAME unique(NAME,PARENT_ID)",
		"insert into PLAYSPACE_FOLDERS (ID, NAME) values ('" + DbConstants.NULL + "', 'NullFolder')",

		// file events
		"create table FILE_EVENTS (EVENT_ID int identity, EVENT_TYPE varchar not null, SESSION_ID varchar not null, DATE timestamp default 'now' not null)",
		"alter table FILE_EVENTS add constraint VALID_EVENT_TYPE foreign key(EVENT_TYPE) references FILE_EVENT_TYPES",
		"alter table FILE_EVENTS add constraint VALID_SESSION_ID foreign key(SESSION_ID) references SESSIONS(ID) on delete cascade",

		// file download log
		"create table FILE_DOWNLOADS (EVENT_ID int identity, FILE_ID varchar not null, VERSION_ID int not null)",
		"alter table FILE_DOWNLOADS add constraint VALID_FILE_EVENT_ID foreign key(EVENT_ID) references FILE_EVENTS(EVENT_ID) on delete cascade",

		// models and integration models
		"create table MODELS (ID varchar not null primary key, TYPE varchar not null, NAME varchar not null, DESCRIPTION varchar default '' not null, FOLDER_ID int not null, LAST_VERSION_ID int default '" + DbConstants.NULL + "')",
		"alter table MODELS add constraint UNIQUE_NAME unique(NAME,FOLDER_ID)",
		"alter table MODELS add constraint VALID_MODEL_TYPE foreign key(TYPE) references MODEL_TYPES",
		"alter table MODELS add constraint VALID_FOLDER foreign key(FOLDER_ID) references MODEL_FOLDERS(ID)",
		// model versions
		"create table MODEL_VERSIONS (ID int identity, MODEL_ID varchar not null, VERSION int default '1' not null, BUILD_ID varchar not null, BUILD_VERSION varchar not null, CONTENT varchar not null, FILE_EVENT_ID int not null)",
		"alter table MODEL_VERSIONS add constraint UNIQUE_MODEL_VERSIONS unique(MODEL_ID,VERSION)",
		"alter table MODEL_VERSIONS add constraint VALID_MODEL_ID foreign key(MODEL_ID) references MODELS(ID) on delete cascade",
		//"alter table MODEL_VERSIONS add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

		// playspaces
		"create table PLAYSPACES (ID varchar not null primary key, NAME varchar not null, DESCRIPTION varchar default '' not null, FOLDER_ID int not null, LAST_VERSION_ID int default '" + DbConstants.NULL + "')",
		"alter table PLAYSPACES add constraint UNIQUE_NAME unique(NAME,FOLDER_ID)",
		"alter table PLAYSPACES add constraint VALID_FOLDER foreign key(FOLDER_ID) references PLAYSPACE_FOLDERS(ID)",
		// playspace versions
		"create table PLAYSPACE_VERSIONS (ID int identity, PLAYSPACE_ID varchar not null, VERSION int default '1' not null, BUILD_ID varchar not null, BUILD_VERSION varchar not null, CONTENT varchar not null, FILE_EVENT_ID int not null)",
		"alter table PLAYSPACE_VERSIONS add constraint UNIQUE_PLAYSPACE_VERSIONS unique(PLAYSPACE_ID,VERSION)",
		"alter table PLAYSPACE_VERSIONS add constraint VALID_PLAYSPACE_ID foreign key(PLAYSPACE_ID) references PLAYSPACES(ID) on delete cascade",
		//"alter table PLAYSPACE_VERSIONS add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

		// integration projects
		"create table PROJECTS (ID varchar not null primary key, NAME varchar not null, DESCRIPTION varchar default '' not null, FOLDER_ID int not null, LAST_VERSION_ID int default '" + DbConstants.NULL + "')",

        /**
         * the constraint below has been relaxed because
         * the condition is being checked in the code and
         * we want to make it flexible for analysis tools,
         * which have a project that is not deployed in its own
         * folder, but is embedded in the analysis tool.
         */

//		"alter table PROJECTS add constraint UNIQUE_NAME unique(NAME,FOLDER_ID)",
		"alter table PROJECTS add constraint VALID_FOLDER foreign key(FOLDER_ID) references MODEL_FOLDERS(ID)",
		// integration project versions
		"create table PROJECT_VERSIONS (ID int identity, PROJECT_ID varchar not null, VERSION int default '1' not null, BUILD_ID varchar not null, BUILD_VERSION varchar not null, CONTENT varchar not null, FILE_EVENT_ID int not null)",
		"alter table PROJECT_VERSIONS add constraint UNIQUE_PROJECT_VERSIONS unique(PROJECT_ID,VERSION)",
		"alter table PROJECT_VERSIONS add constraint VALID_PROJECT_ID foreign key(PROJECT_ID) references PROJECTS(ID) on delete cascade",
		//"alter table PROJECT_VERSIONS add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

		// integration models (can only exist in an integration project)
		"create table INTEGRATION_MODELS (ID varchar not null primary key, NAME varchar not null, DESCRIPTION varchar default '' not null, PROJECT_ID varchar not null, LAST_VERSION_ID int default '" + DbConstants.NULL + "')",
		"alter table INTEGRATION_MODELS add constraint UNIQUE_NAME unique(NAME,PROJECT_ID)",
		"alter table INTEGRATION_MODELS add constraint VALID_PROJECT foreign key(PROJECT_ID) references PROJECTS(ID)",
		// integration model versions
		"create table INTEGRATION_MODEL_VERSIONS (ID int identity, MODEL_ID varchar not null, VERSION int default '1' not null, BUILD_ID varchar not null, BUILD_VERSION varchar not null, CONTENT varchar not null, FILE_EVENT_ID int not null)",
		"alter table INTEGRATION_MODEL_VERSIONS add constraint UNIQUE_MODEL_VERSIONS unique(MODEL_ID,VERSION)",
		"alter table INTEGRATION_MODEL_VERSIONS add constraint VALID_MODEL_ID foreign key(MODEL_ID) references INTEGRATION_MODELS(ID) on delete cascade",
		//"alter table INTEGRATION_MODEL_VERSIONS add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

        // analysis tools
        "create table ANALYSIS_TOOLS (ID varchar not null primary key, TYPE varchar not null, NAME varchar not null, DESCRIPTION varchar default '' not null, FOLDER_ID int not null, PROJECT_ID varchar default '', LAST_VERSION_ID int default '" + DbConstants.NULL + "')",
        "alter table ANALYSIS_TOOLS add constraint UNIQUE_NAME unique(NAME,FOLDER_ID)",
        "alter table ANALYSIS_TOOLS add constraint VALID_MODEL_TYPE foreign key(TYPE) references MODEL_TYPES",
        "alter table ANALYSIS_TOOLS add constraint VALID_FOLDER foreign key(FOLDER_ID) references MODEL_FOLDERS(ID)",
        // model versions
        "create table ANALYSIS_TOOL_VERSIONS (ID int identity, ANALYSIS_TOOL_ID varchar not null, VERSION int default '1' not null, BUILD_ID varchar not null, BUILD_VERSION varchar not null, CONTENT varchar not null, FILE_EVENT_ID int not null)",
        "alter table ANALYSIS_TOOL_VERSIONS add constraint UNIQUE_ANALYSIS_TOOL_VERSIONS unique(ANALYSIS_TOOL_ID,VERSION)",
        "alter table ANALYSIS_TOOL_VERSIONS add constraint VALID_ANALYSIS_TOOL_ID foreign key(ANALYSIS_TOOL_ID) references ANALYSIS_TOOLS(ID) on delete cascade",
        //"alter table ANALYSIS_TOOL_VERSIONS add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

		// interfaces (for models, projects, and integration models)
		"create table INTERFACES (ID varchar not null primary key, STATUS varchar not null, NAME varchar not null, DESCRIPTION varchar default '' not null, PARENT_ID varchar not null, PARENT_TABLE varchar not null, LAST_VERSION_ID int default '" + DbConstants.NULL + "')",
		"alter table INTERFACES add constraint VALID_STATUS foreign key(STATUS) references INTERFACE_STATUS_TYPES",
		"alter table INTERFACES add constraint VALID_PARENT_TABLE foreign key (PARENT_TABLE) references INTERFACE_PARENT_TABLE_NAMES(TABLE_NAME)",
		// interface versions
		"create table INTERFACE_VERSIONS (ID int identity, INTERFACE_ID varchar not null, VERSION int default '1' not null, BUILD_ID varchar not null, BUILD_VERSION varchar not null, CONTENT varchar not null, MAPPINGS varchar not null, FILE_EVENT_ID int not null)",
		"alter table INTERFACE_VERSIONS add constraint UNIQUE_INTERFACE_VERSIONS unique(INTERFACE_ID,VERSION)",
		"alter table INTERFACE_VERSIONS add constraint VALID_INTERFACE_ID foreign key(INTERFACE_ID) references INTERFACES(ID) on delete cascade",
		//"alter table INTERFACE_VERSIONS add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

		// auxiliary files
		//Qing --redesigned fields for auxiliary files
		"create table AUX_FILES (ID int identity,AUX_FILE_ID varchar not null,MODEL_ID varchar default '',INTERFACE_ID varchar default '',FILE_NAME varchar not null, LOCATION_ON_SERVER varchar default '',VERSION int default '1' not null, FILE_EVENT_ID int not null)",
		//"alter table AUX_FILES add constraint UNIQUE_FILE_NAME unique(FILE_NAME,MODEL_ID)",
		//"alter tabl e AUX_FILES add constraint VALID_MODEL_ID foreign key(MODEL_ID) references MODELS(ID) on delete cascade",
        //"alter table AUX_FILES add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

		// auxiliary files versions
		//"create table AUX_FILE_VERSIONS (ID int, AUX_FILE_ID int not null, VERSION int default '1' not null, LOCATION varchar not null, FILE_EVENT_ID int not null)",
		//"alter table AUX_FILE_VERSIONS add constraint UNIQUE_AUX_FILE_VERSIONS unique(AUX_FILE_ID,VERSION)",
		//"alter table AUX_FILE_VERSIONS add constraint VALID_AUX_FILE foreign key(AUX_FILE_ID) references AUX_FILES(ID)",
		//"alter table AUX_FILE_VERSIONS add constraint VALID_FILE_EVENT_ID foreign key(FILE_EVENT_ID) references FILE_EVENTS(EVENT_ID)", // do not delete file if file events are cleared!

		// user_group_folders
		"create table USER_GROUP_FOLDERS (USER_GROUP_ID int primary key, PUBLIC_MODEL_FOLDER_ID int default '" + DbConstants.NULL +
	        "',PRIVATE_MODEL_FOLDER_ID int default '" + DbConstants.NULL +
	        "',PUBLIC_PLAYSPACE_FOLDER_ID int default '" + DbConstants.NULL +
	        "',PRIVATE_PLAYSPACE_FOLDER_ID int default '" + DbConstants.NULL + "')",
		"alter table USER_GROUP_FOLDERS add constraint VALID_USER_GROUP foreign key(USER_GROUP_ID) references USERS_GROUPS(ID) on delete cascade",
		"alter table USER_GROUP_FOLDERS add constraint VALID_PUBLIC_MODEL_FOLDER_ID foreign key(PUBLIC_MODEL_FOLDER_ID) references MODEL_FOLDERS(ID)",
		"alter table USER_GROUP_FOLDERS add constraint VALID_PRIVATE_MODEL_FOLDER_ID foreign key(PRIVATE_MODEL_FOLDER_ID) references MODEL_FOLDERS(ID)",
		"alter table USER_GROUP_FOLDERS add constraint VALID_PUBLIC_PLAYSPACE_FOLDER_ID foreign key(PUBLIC_PLAYSPACE_FOLDER_ID) references PLAYSPACE_FOLDERS(ID)",
		"alter table USER_GROUP_FOLDERS add constraint VALID_PRIVATE_PLAYSPACE_FOLDER_ID foreign key(PRIVATE_PLAYSPACE_FOLDER_ID) references PLAYSPACE_FOLDERS(ID)",

		// permissions
		"create table OBJECT_PERMISSIONS (OBJECT_ID varchar not null,USER_ID int not null,PERMISSION_ID int not null)",
		"alter table OBJECT_PERMISSIONS add constraint VALID_USER_ID foreign key(USER_ID) references USERS_GROUPS(ID) on delete cascade",
		"alter table OBJECT_PERMISSIONS add constraint VALID_PERMISSION_ID foreign key(PERMISSION_ID) references PERMISSION_TYPES(ID) on delete cascade",

	};

	public static String getDbFileName()
	{
		return m_dbFile;
	}

	public static void createDatabase(String dbFile) throws SQLException
	{
		if (!okToCreateDatabaseFiles(dbFile)) {
			System.out.println("Initialization of database cancelled.");
			System.exit(0);
		}
		try {
			// Load the HSQL Database Engine JDBC driver
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find JDBC driver for hsqldb. Check that hsqldb.jar is in the class path.");
			System.exit(0);
		}

		Connection con = null;
		Statement stmt = null;
		try {
			// create a new database and start it, connection with default username and password
			m_dbFile = dbFile;
			String url = "jdbc:hsqldb:" + m_dbFile;
			con = DriverManager.getConnection(url, "sa", "");
			stmt = con.createStatement();
			con.setAutoCommit(false);
			executeSQL(stmt, createConstantsTables);
			executeSQL(stmt, createDataTables);
			con.commit();

			try {
				DbUtils.setDbUrl(url);
				int rootId = UserGroupDbFunctions.createNewUser("root", "administrator", LoginUtils.encryptPassword("cadlab"), false, false);
				// todo: change default admin password
				/*
				String pwd = "4c99f58279a4315b7f137b58ea97487884bf4c69";    // encrypted password
				byte[] bytePwd = new byte[pwd.toCharArray()];               // byte array version
				int rootId = UserGroupDbFunctions.createNewUser("root","administrator",bytePwd,false,false);
				*/
				int groupId = UserGroupDbFunctions.createNewGroup("administrators", "admin group", true, true);
				UserGroupDbFunctions.editMembersForGroup(groupId, Vectors.create(new Integer(rootId)));
				UserGroupDbFunctions.createNewUser("guest", "guest user", LoginUtils.encryptPassword(""), false, false);
			} catch (XmlRpcException e) {
				System.err.println(e);
			}
		} catch (SQLException e) {
			System.err.println(e);
		} finally {
			if (stmt != null) stmt.close();
			if (con != null) con.close();
		}
		return;
	}

	private static void executeSQL(Statement stmt, String[] stmts)
	{
		int i = 0;
		try {
			for (; i < stmts.length; i++) {
				stmt.executeUpdate(stmts[i]);
			}
		} catch (SQLException e) {
			System.err.println("error executing " + stmts[i]);
			e.printStackTrace();
		}
	}


   private static void createAuxFileFolder()
	{
		File AuxFolder = new File(DomeServer.getServerAuxFileRoot());
		//clean up before creating
		if(AuxFolder.exists()&&AuxFolder.isDirectory()) cleanUpAuxFileFilder();

		if (AuxFolder.mkdir())
			System.out.println("folder AuxFiles successfully created");
		else
			System.out.println("folder AuxFiles creation failed");
	}

	private static void cleanUpAuxFileFilder()
	{
		File AuxFolder = new File(DomeServer.getServerAuxFileRoot());
		FileUtils.deleteDirectoryContents(AuxFolder, true);
	}


	public static void main(String[] args)
	{
		if (args.length == 1)
			m_dbFile = args[0];

		if (args.length <= 1) {
			try {
				createDatabase(m_dbFile);
				System.out.println("Database successfully initialized.");
                createAuxFileFolder();
				System.exit(0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("usage: java DbInit dbFileName" +
			                   "       default - DbInit " + m_dbFile);
			System.exit(0);
		}
	}

	/**
	 * This method will check if desired database file already exists. If so, prompts user to delete files.
	 * If user chooses to delete files, files will be deleted. If user chooses to keep files, method returns
	 * false. If no files exist or files are deleted, method returns true.
	 * @param dbFile desired database file name
	 * @return true if no such database files exist; otherwise, returns false
	 */
	public static boolean okToCreateDatabaseFiles(String dbFile)
	{
		File dbScript, dbProperties, dbData;
		if (dbFile.endsWith(".script"))
			dbFile = (new StringBuffer(dbFile)).delete(dbFile.indexOf(".script"), dbFile.length()).toString();
		dbScript = new File(dbFile + ".script");
		dbProperties = new File(dbFile + ".properties");
		dbData = new File(dbFile + ".data");
		if (dbScript.exists() || dbProperties.exists() || dbData.exists()) {
			int result = TwoButton1Msg.showOption(null, "Initialization options",
			                                      "This database already exists.\n Do you want to overwrite it?",
			                                      "Yes", "Cancel", new Dimension(240, 80));
			if (result == TwoButton1Msg.RIGHT_OPTION) // cancel
				return false;
			return deleteFile(dbScript) && deleteFile(dbProperties) && deleteFile(dbData);
		} else
			return true;
	}

	/**
	 *
	 * @param f file to be deleted
	 * @return true if file exists and is deleted successfully or if file does not exist
	 * If file exists and is not deleted successfully, returns false.
	 */
	private static boolean deleteFile(File f)
	{
		boolean fileDeleted = true;
		if (f.exists()) {
			fileDeleted = f.delete();
			if (!fileDeleted)
				System.err.println("Could not delete " + f.getAbsolutePath());
		}
		return fileDeleted;
	}

}
