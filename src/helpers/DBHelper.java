package helpers;

import application.MainApp;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.*;

/**
 * This helper class provides static methods that facilitate the interactions with the SQLite database
 *
 * Every database access object (DAO) in this project relies on these helper methods for
 * the connection and disconnection with the database as well as for the
 * execution of all statements.
 *
 * Sources: http://www.sqlitetutorial.net/sqlite-java/
 *          http://www.swtestacademy.com/database-operations-javafx/
 */
public class DBHelper {
	// Declares constant for the jdbc driver
	private static final String JDBC_DRIVER = "org.sqlite.JDBC";
	// Declares constant for SQLite connection string
	private static final String CONN_URL = "jdbc:sqlite:db/cinema.db";

	// Initializes Connection
	private static Connection conn = null;

	/**
	 * This method sets the Oracle JDBC driver and creates a new database connection
	 */
	private static void dbConnect() {
		try {
			// Sets the Oracle JDBC Driver
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// If the Oracle JDBC Driver cannot be found, logs a warning and rethrows the exception
			MainApp.LOGGER.warning("Cannot find Oracle JDBC Driver");
			e.printStackTrace();
		}
		MainApp.LOGGER.fine("Oracle JDBC Driver Registered!");

		// Creates a connection to the database using the SQLite connection string CONN_URL
		try {
			conn = DriverManager.getConnection(CONN_URL);
			MainApp.LOGGER.fine("Connection to SQLite has been established.");
		} catch (SQLException e) {
			MainApp.LOGGER.warning("Connection Failed!");
			e.printStackTrace();
		}
	}

	/**
	 * This method closes the current database connection through Connection.close()
	 *
	 * @throws SQLException thrown if the connection cannot be closed
	 */
	private static void dbDisconnect() throws SQLException {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}

	/**
	 * Executed an SQL SELECT query and returns the resulting result set
	 *
	 * This method takes an SQL SELECT query as a String as input, connects to the database,
	 * executes the query, retrieves the result and closes the connection before returning the result.
	 *
	 * @param queryStatement The SQL SELECT query that shall be executed
	 * @return Returns a CachedRowSet containing the result set of the SELECT query
	 * @throws SQLException thrown when there is a problem with closing the connection
	 */
	public static ResultSet dbExecuteQuery(String queryStatement) throws SQLException {
		// Declares statement, resultSet and cached result set as null
		Statement statement = null;
		ResultSet resultSet = null;
		CachedRowSetImpl crs = null;
		try {
			// Connects to the database
			dbConnect();
			MainApp.LOGGER.info("Select statement: " + queryStatement);
			// Creates the statement
			statement = conn.createStatement();
			// Executes the query that was provided to the method
			resultSet = statement.executeQuery(queryStatement);
			// CachedRowSet Implementation
			// In order to prevent "java.sql.SQLRecoverableException: Closed Connection: next" error
			crs = new CachedRowSetImpl();
			crs.populate(resultSet);
		} catch (SQLException e) {
			MainApp.LOGGER.warning("Problem occurred at executeQuery operation : " + e);
		} finally {
			if (resultSet != null) {
				// Closes the resultSet
				resultSet.close();
			}
			if (statement != null) {
				// Closes the Statement
				statement.close();
			}
			// Closes the connection
			dbDisconnect();
		}
		// Returns the CachedRowSet containing the query results
		return crs;
	}

	//DB Execute Update (for Update, Insert or Delete) Operation

	/**
	 * Executes an SQL update statement
	 *
	 * This method takes an SQL update query as a String as input, connects to the database,
	 * executes the query, retrieves the result and closes the connection before returning the result.
	 *
	 * This method is used for Updates, Insertions and Deletion operations.
	 *
	 * @param sqlStatement The SQL update query that shall be executed
	 * @throws SQLException thrown when there is a problem with closing the connection
	 */
	public static void dbExecuteUpdate(String sqlStatement) throws SQLException {
		// Declares the Statement as null
		Statement statement = null;
		try {
			// Connects to the database
			dbConnect();
			// Creates the Statement
			statement = conn.createStatement();
			// Runs the executeUpdate operation with the given SQL statement
			statement.executeUpdate(sqlStatement);
		} catch (SQLException e) {
			MainApp.LOGGER.warning("Problem occurred at executeUpdate operation : " + e);
		} finally {
			if (statement != null) {
				// Closes the statement
				statement.close();
			}
			// Disconnects from the database
			dbDisconnect();
		}
	}
}