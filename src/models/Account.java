package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the user accounts in the application
 * and is instantiated with rows from the Accounts table in the database.
 *
 * Each instance of this class represents a user account for the application.
 * Each user account has a username, a password and a number whether it is
 * an employee (1) or a customer(0).
 *
 * This application uses the Data Access Object (DAO) model for structuring its model.
 * This improves the readability and maintainability of the code by providing uniform
 * ways to interact with all models, as well as with the database and by clearly separating
 * the business logic of the application from its data objects.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class Account {

	// Account properties
	private StringProperty username, password;
	private IntegerProperty employee;

	/**
	 * Constructor that initializes all fields
	 */
	public Account() {
		this.username = new SimpleStringProperty();
		this.password = new SimpleStringProperty();
		this.employee = new SimpleIntegerProperty();
	}

	//----------------------------------------//
	// Setters and getters for all properties //
	//----------------------------------------//

	//Username

	/**
	 *
	 * @return this account's associated username
	 */
	public String getUsername() {
		return username.get();
	}

	/**
	 *
	 * @return this account's associated username property
	 */
	public StringProperty usernameProperty() {
		return username;
	}

	/**
	 *
	 * @param username this account's associated username
	 */
	public void setUsername(String username) {
		this.username.set(username);
	}

	//Password

	/**
	 *
	 * @return this account's associated password
	 */
	public String getPassword() {
		return password.get();
	}

	/**
	 *
	 * @return this account's associated password property
	 */
	public StringProperty passwordProperty() {
		return password;
	}

	/**
	 *
	 * @param password this account's associated password
	 */
	public void setPassword(String password) {
		this.password.set(password);
	}

	//Employee

	/**
	 *
	 * @return this account's employee value - 0 for customers, 1 for employees
	 */
	public int getEmployee() {
		return employee.get();
	}

	/**
	 *
	 * @return this account's employee property
	 */
	public IntegerProperty employeeProperty() {
		return employee;
	}

	/**
	 *
	 * @param employee this account's employee value - 0 for customers, 1 for employees
	 */
	public void setEmployee(int employee) {
		this.employee.set(employee);
	}

}
