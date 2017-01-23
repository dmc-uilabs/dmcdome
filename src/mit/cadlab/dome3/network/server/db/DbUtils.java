// DbUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.db;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;

/**
 * This class includes functions that are useful in working with databases.
 */
public class DbUtils
{

	static
	{
		getDbDriver();
	}

	public static final Vector NO_VECTOR = new Vector();

	private static String dbUrl = "";
	private static String dbUserName = "sa";
	private static String dbPwd = "";
	private static Connection dbConn = null;

	private static void getDbDriver()
	{
		try {
			// Load the HSQL Database Engine JDBC driver
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find JDBC driver for hsqldb. Check that hsqldb.jar is in the class path.");
			System.exit(0);
		}
	}

	/**
	 * sets the database url
	 * @param dbPort the port to access the database
	 */
	public static void setDbUrl(int dbPort)
	{
		dbUrl = "jdbc:hsqldb:hsql://localhost:" + dbPort;
	}

	public static void setDbUrl(String url)
	{
		dbUrl = url;
	}

	/**
	 * how to get database connection to use for queries
	 * @return database connection
	 */
	public static Connection getConnection() throws SQLException
	{
		if (dbConn == null) {
			if (dbUrl.equals(""))
				throw new RuntimeException("Database URL has not been set! Use DbUtils.setDbUrl.");
			dbConn = DriverManager.getConnection(dbUrl, dbUserName, dbPwd);
		}
		return dbConn;
	}

	/**
	 * how to get a database statement in order to run queries
	 * @return a database statement which can be used to execute queries
	 */
	public static Statement getStatement() throws SQLException
	{
		return getConnection().createStatement();
	}

	public static PreparedStatement getPreparedStatement(String sql) throws SQLException
	{
		return getConnection().prepareStatement(sql);
	}

	/**
	 * Commit database transactions
	 * @throws SQLException
	 */
	public static void commit() throws SQLException
	{
		getConnection().commit();
	}

	/**
	 * Converts a ResultSet to a Vector of Vectors
	 * @param rs the ResultSet to be converted
	 * @return the Vector with the data from the ResultSet
	 * @throws RuntimeException if problems accessing data in resultSet
	 */
	public static Vector resultSetToVectorOfVectors(ResultSet rs)
	{
		if (rs == null)
			return null;
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int nCols = meta.getColumnCount();
			Vector results = new Vector();
			Vector row;
			int i;
			while (rs.next()) {
				row = new Vector(nCols);
				for (i = 1; i <= nCols; i++) {
					row.add(rs.getObject(i));
				}
				results.add(row);
			}
			return results;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	public static Hashtable resultSetToHashMap(ResultSet rs)
	{
		if (rs == null)
			return null;
		try {
			Hashtable results = new Hashtable();
			while (rs.next()) {
				String key = rs.getString(1);
				String value = rs.getString(2);
				results.put(key, value);
			}
			return results;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converts a single row ResultSet to a Vector with the elements of the row
	 * @param rs the ResultSet to be converted
	 * @return the Vector with the data from the ResultSet
	 * @throws RuntimeException if problems accessing data in resultSet or data contains more than one row
	 */
	public static Vector oneRowResultSetToVector(ResultSet rs)
	{
		if (rs == null)
			return null;
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int nCols = meta.getColumnCount();
			Vector results = new Vector();
			if (rs.next()) {
				for (int i = 1; i <= nCols; i++) {
					results.add(rs.getObject(i));
				}
				if (rs.next()) // more than one row
					throw new IllegalArgumentException("more than one row returned!");
				return results;
			}
			return results; // empty
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converts a single col ResultSet to a Vector with the elements of the col
	 * @param rs the ResultSet to be converted
	 * @return the Vector with the data from the ResultSet
	 * @throws RuntimeException if problems accessing data in resultSet or data contains more than one col
	 */
	public static Vector oneColumnResultSetToVector(ResultSet rs)
	{
		if (rs == null)
			return null;
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int nCols = meta.getColumnCount();
			if (nCols != 1)
				throw new IllegalArgumentException("more than one col returned!");
			Vector results = new Vector();
			while (rs.next()) {
				results.add(rs.getObject(1));
			}
			return results; // empty
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * closes all database exceptions
	 * @throws SQLException
	 */
	public static void closeConnections() throws SQLException
	{
		if (dbConn != null) {
			dbConn.close();
			dbConn = null;
		}
	}

	/**
	 * used to create a vector with one item
	 * @param status the item to be added to the vector
	 * @return vector with specified item added to it
	 */
	public static Vector createStatusVector(String status)
	{
		Vector v = new Vector(1);
		v.add(status);
		return v;
	}

	/**
	 * used to insert a new row into a table and get back the value of the generated primary key
	 * the method closes the statement
	 * @param pStmt Statement to be executed
	 * @param returnIdentity true if insert generates an identity value and value should be returned; otherwise, false
	 * @return ID generated, or -1 if no identity to return.
	 * @throws SQLException
	 */
	public static int executeInsert(PreparedStatement pStmt, boolean returnIdentity) throws SQLException
	{
		Connection con = getConnection();
		synchronized (con) {
			if (pStmt.executeUpdate() != 1)
				throw new RuntimeException("Unable to execute insert statement");
			if (returnIdentity) {
				CallableStatement cStmt = con.prepareCall("call identity()");
				ResultSet rs = cStmt.executeQuery();
				Vector v = DbUtils.oneRowResultSetToVector(rs);
				if (v.isEmpty())
					throw new RuntimeException("Unable to get new id");
				return ((Integer) v.get(0)).intValue();
			} else {
				return DbConstants.NULL;
			}
		}
	}

	/**
	 * used to insert a new row into a table and get back the value of the generated primary key
	 * @param query SQLQuery to be executed
	 * @param returnIdentity true if insert generates an identity value and value should be returned; otherwise, false
	 * @return ID generated, or -1 if no identity to return.
	 * @throws SQLException
	 */
	public static int executeInsert(String query, boolean returnIdentity) throws SQLException
	{
		Connection con = getConnection();
		synchronized (con) {
			Statement stmt = getStatement();
			if (stmt.executeUpdate(query) != 1)
				throw new RuntimeException("Unable to execute insert statement");
			if (returnIdentity) {
				CallableStatement cStmt = con.prepareCall("call identity()");
				ResultSet rs = cStmt.executeQuery();
				Vector v = DbUtils.oneRowResultSetToVector(rs);
				if (v.isEmpty())
					throw new RuntimeException("Unable to get new id");
				return ((Integer) v.get(0)).intValue();
			} else {
				return DbConstants.NULL;
			}
		}
	}

	/**
	 * used to execute select query on the db
	 * the method closes the statement
	 * @param pStmt SQL select statement
	 * @param resultIsOneRow should the vector returned be a one row vector
	 * @return the results of the query
	 * @throws SQLException
	 */
	public static Vector executeQuery(PreparedStatement pStmt, boolean resultIsOneRow) throws SQLException
	{
		ResultSet rs = pStmt.executeQuery();
		Vector results;
		if (resultIsOneRow)
			results = oneRowResultSetToVector(rs);
		else
			results = resultSetToVectorOfVectors(rs);
		pStmt.close();
		return results;
	}

	/**
	 * used to execute select query on the db
	 * @param query Statement to be executed
	 * @param resultIsOneRow should the vector returned be a one row vector
	 * @return the results of the query
	 * @throws SQLException
	 */
	public static Vector executeQuery(String query, boolean resultIsOneRow) throws SQLException
	{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		Vector results;
		if (resultIsOneRow)
			results = oneRowResultSetToVector(rs);
		else
			results = resultSetToVectorOfVectors(rs);
		stmt.close();
		return results;
	}

	/**
	 * used to execute select query on the db
	 * @param pStmt Statement to be executed
	 * @return the results of the query
	 * @throws SQLException
	 */
	public static Vector executeQueryColumnToVector(PreparedStatement pStmt) throws SQLException
	{
		ResultSet rs = pStmt.executeQuery();
		Vector results = oneColumnResultSetToVector(rs);
		pStmt.close();
		return results;
	}

	/**
	 * used to execute select query on the db
	 * @param query Statement to be executed
	 * @return the results of the query
	 * @throws SQLException
	 */
	public static Vector executeQueryColumnToVector(String query) throws SQLException
	{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		Vector results = oneColumnResultSetToVector(rs);
		stmt.close();
		return results;
	}

	/**
	 * used to update rows in a table
	 * the method closes the statement
	 * @param pStmt Statement to be executed
	 * @return # of rows modified.
	 * @throws SQLException
	 */
	public static int executeUpdate(PreparedStatement pStmt) throws SQLException
	{
		int rowModified = pStmt.executeUpdate();
		pStmt.close();
		return rowModified;
	}

	/**
	 * used to update rows in a table
	 * the method closes the statement
	 * @param query SQL query to be executed
	 * @return # of rows modified.
	 * @throws SQLException
	 */
	public static int executeUpdate(String query) throws SQLException
	{
		Statement stmt = getStatement();
		int rowModified = stmt.executeUpdate(query);
		stmt.close();
		return rowModified;
	}

	public static Vector executeQuery(String query) throws SQLException
	{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		Hashtable results;
		results = resultSetToHashMap(rs);
		stmt.close();
		Vector v = new Vector(1);
		v.add(results);
		return v;
	}

}
