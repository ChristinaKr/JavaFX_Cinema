package models;

import application.MainApp;
import helpers.DBHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a data access object (DAO) for Accounts.
 *
 * Following the DAO design principle, this class acts as a layer between
 * the Model (the Account) and the business logic of this application.
 * It provides functions to search and return one or multiple Account from the database,
 * as well as to add a new account to the database.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class AccountDAO {

	/**
	 * This method searches for an Account in the database based on the provided username
	 *
	 * @param username the account's username
	 * @return the Account object that was retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static Account searchAccount(String username) throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM accounts WHERE username='" + username + "'";

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsAccount = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getMovieFromResultSet method and get Account Object
			Account account = getAccountFromResultSet(rsAccount);
			//Return Account object
			return account;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("While searching an account with username " + username
				+ ", an error occurred: " + e.getMessage());
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates a new account object from the provided result set
	 *
	 * @param rs the resultset containing information for the new account
	 * @return an Account object
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static Account getAccountFromResultSet(ResultSet rs) throws SQLException {
		//Declares variable account
		Account account = null;
		if (rs.next()) {
			account = new Account();
			account.setUsername(rs.getString("username"));
			account.setPassword(rs.getString("password"));
			account.setEmployee(rs.getInt("employee"));
		}
		return account;
	}

	/**
	 * This method searches for a list of Accounts in the database
	 *
	 * @return a list of all matching Account objects that were retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static ObservableList<Account> searchAccounts() throws SQLException, ClassNotFoundException {
		// Declares a SELECT statement
		String selectStatement = "SELECT * FROM accounts";

		// Executes SELECT statement
		try {
			// Gets ResultSet from dbExecuteQuery method
			ResultSet rsAccounts = DBHelper.dbExecuteQuery(selectStatement);
			//Sends ResultSet to the getEmployeeList method and get account object
			ObservableList<Account> accountList = getAccountList(rsAccounts);
			//Returns account object
			return accountList;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("SQL select operation has failed: " + e);
			//Rethrows exception
			throw e;
		}
	}

	//Select * from accounts operation

	/**
	 * Internal helper method that creates an observable list of new account objects from the provided result set
	 *
	 * @param rs the resultset containing information for the new accounts
	 * @return an ObservableList of Account objects
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static ObservableList<Account> getAccountList(ResultSet rs) throws SQLException {
		//Declare an observable List comprising of Account objects
		ObservableList<Account> accountList = FXCollections.observableArrayList();

		while (rs.next()) {
			//Create new Account object and fill it with information from the database
			Account account = new Account();
			account.setUsername(rs.getString("username"));
			account.setPassword(rs.getString("password"));
			account.setEmployee(rs.getInt("employee"));
			//Add Account to the ObservableList
			accountList.add(account);
		}
		return accountList;
	}

	/**
	 * Adds a new row to the Accounts table in the database based on a provided account Objet
	 *
	 * @param account the account object to be saved in the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	public static void addAccount(Account account) throws SQLException {
		// Creates a new SQL statement with information from the provided account object
		String addStatement =
			"INSERT INTO accounts "
				+ "(username, password, employee) "
				+ "VALUES ("
				+ "'" + account.getUsername() + "'"
				+ ", '" + account.getPassword() + "'"
				+ ", " + account.getEmployee()
				+ ");";
		// Executes the statement
		DBHelper.dbExecuteUpdate(addStatement);

	}
}
