package models;

import application.MainApp;
import helpers.DBHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a data access object (DAO) for Customers.
 *
 * Following the DAO design principle, this class acts as a layer between
 * the Model (the Customer) and the business logic of this application.
 * It provides functions to search and return one or multiple Customers from the database
 * as well as to add new Customers to the database.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class CustomerDAO {

	/**
	 * This method searches for a Customer in the database based on the provided username
	 *
	 * @param username customer's username
	 * @return the Customer object that was retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static Customer searchCustomer(String username) throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM customers WHERE username = '" + username + "'";

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsCustomer = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getCustomerFromResultSet method and
			//get Customer Object
			Customer customer = getCustomerFromResultSet(rsCustomer);
			//Return Customer object
			return customer;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("While searching a customer with username " + username
				+ ", an error occurred: " + e);
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates a new Customer object from the provided result set
	 *
	 * @param rs the resultset containing information for the new Customer
	 * @return a Customer object
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static Customer getCustomerFromResultSet(ResultSet rs) throws SQLException {
		//Declare variable customer
		Customer customer = null;
		if (rs.next()) {
			customer = new Customer();
			customer.setUsername(rs.getString("username"));
			customer.setFamilyname(rs.getString("familyname"));
			customer.setFirstname(rs.getString("firstname"));
			customer.setAddress(rs.getString("address"));
			customer.setEmail(rs.getString("email"));
			customer.setBirthdate(rs.getString("birthdate"));
			customer.setNewsletter(rs.getInt("newsletter"));
		}
		return customer;
	}

	/**
	 * This method searches for a list of Customers in the database
	 *
	 * @return a list of all matching Customer objects that were retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static ObservableList<Customer> searchCustomers() throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM customers";

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsCustomers = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getEmployeeList method and get customer object
			ObservableList<Customer> customerList = getCustomerList(rsCustomers);
			//Return customer object
			return customerList;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("SQL select operation has failed: " + e);
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates an observable list of new Customer objects from the provided result set
	 *
	 * @param rs the resultset containing information for the new Customers
	 * @return an ObservableList of Customer objects
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static ObservableList<Customer> getCustomerList(ResultSet rs) throws SQLException {
		//Declare an observable List comprising of Customer objects
		ObservableList<Customer> customerList = FXCollections.observableArrayList();

		while (rs.next()) {
			//Create new Customer object and fill it with information from the database
			Customer customer = new Customer();
			customer.setUsername(rs.getString("username"));
			customer.setFamilyname(rs.getString("familyname"));
			customer.setFirstname(rs.getString("firstname"));
			customer.setAddress(rs.getString("address"));
			customer.setEmail(rs.getString("email"));
			customer.setBirthdate(rs.getString("birthdate"));
			customer.setNewsletter(rs.getInt("newsletter"));
			//Add customer to the ObservableList
			customerList.add(customer);
		}
		return customerList;
	}

	/**
	 * Updates the Customer entry that correlates to the provided Customer object in the database with new information
	 * @param customer the customer to be updated
	 * @throws SQLException thrown if an error occurs during the databse operation
	 */
	public static void updateCustomer(Customer customer) throws SQLException {
		// Creates a new SQL statement with information from the provided customer object
		String updateStatement =
			"UPDATE customers "
			+ "SET "
				+ "familyname = '" + customer.getFamilyname() + "', "
				+ "firstname = '" + customer.getFirstname() + "', "
				+ "address = '" + customer.getAddress() + "', "
				+ "email = '" + customer.getEmail() + "', "
				+ "birthdate = '" + customer.getBirthdate() + "', "
				+ "newsletter = " + customer.getNewsletter() + " "
			+ "WHERE "
				+ "username = '" + customer.getUsername() + "';";
		// Executes the statement
		DBHelper.dbExecuteUpdate(updateStatement);

	}

	/**
	 * Adds a new row to the Customers table in the database based on a provided customer Object
	 *
	 * @param customer the Customer object to be saved in the database
	 * @throws SQLException thrown if an error occurs during the database operation
	 */
	public static void addCustomer(Customer customer) throws SQLException {
		// Creates a new SQL statement with information from the provided customer object
		String addStatement =
			"INSERT INTO customers "
				+ "(username, familyname, firstname, address, email, birthdate, newsletter) "
				+ "VALUES ("
				+ "'" + customer.getUsername() + "', "
				+ "'" + customer.getFamilyname() + "', "
				+ "'" + customer.getFirstname() + "', "
				+ "'" + customer.getAddress() + "', "
				+ "'" + customer.getEmail() + "', "
				+ "'" + customer.getBirthdate() + "', "
				+ "" + customer.getNewsletter() + " "
				+ ");";
		// Executes the statement
		DBHelper.dbExecuteUpdate(addStatement);
	}
}
