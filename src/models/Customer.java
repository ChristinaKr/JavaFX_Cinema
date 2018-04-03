package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the customers in the application
 * and is instantiated with rows from the Customers table in the database.
 *
 * Each instance of this class represents a customer of the application.
 * Each customer has a username, a familyname, a firstname, an address, an email address,
 * a birthdate and an integer value for whether they would like to receive a newsletter (1)
 * or not (0).
 *
 * This application uses the Data Access Object (DAO) model for structuring its model.
 * This improves the readability and maintainability of the code by providing uniform
 * ways to interact with all models, as well as with the database and by clearly separating
 * the business logic of the application from its data objects.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class Customer {

	//Customer Properties
	private IntegerProperty newsletter;
	private StringProperty username, familyname, firstname, address, email, birthdate;

	/**
	 * Constructor that initializes all fields
	 */
	public Customer() {
		username = new SimpleStringProperty();
		newsletter = new SimpleIntegerProperty();
		familyname = new SimpleStringProperty();
		firstname = new SimpleStringProperty();
		address = new SimpleStringProperty();
		email = new SimpleStringProperty();
		birthdate = new SimpleStringProperty();
	}

	//----------------------------------------//
	// Setters and getters for all properties //
	//----------------------------------------//

	// Username

	/**
	 *
	 * @return this customer's username
	 */
	public String getUsername() {
		return username.get();
	}

	/**
	 *
	 * @return this customer's username property
	 */
	public StringProperty usernameProperty() {
		return username;
	}

	/**
	 *
	 * @param username this customer's username
	 */
	public void setUsername(String username) {
		this.username.set(username);
	}

	// Newsletter

	/**
	 *
	 * @return this customer's newsletter preference (0 for no, 1 for yes)
	 */
	public int getNewsletter() {
		return newsletter.get();
	}

	/**
	 *
	 * @return  this customer's newsletter preference property
	 */
	public IntegerProperty newsletterProperty() {
		return newsletter;
	}

	/**
	 *
	 * @param newsletter this customer's newsletter preference (0 for no, 1 for yes)
	 */
	public void setNewsletter(int newsletter) {
		this.newsletter.set(newsletter);
	}

	// Familyname

	/**
	 *
	 * @return this customer's familyname
	 */
	public String getFamilyname() {
		return familyname.get();
	}

	/**
	 *
	 * @return this customer's familyname property
	 */
	public StringProperty familynameProperty() {
		return familyname;
	}

	/**
	 *
	 * @param familyname this customer's familyname
	 */
	public void setFamilyname(String familyname) {
		this.familyname.set(familyname);
	}

	// Firstname

	/**
	 *
	 * @return this customer's first name
	 */
	public String getFirstname() {
		return firstname.get();
	}

	/**
	 *
	 * @return this customer's first name property
	 */
	public StringProperty firstnameProperty() {
		return firstname;
	}

	/**
	 *
	 * @param firstname this customer's first name
	 */
	public void setFirstname(String firstname) {
		this.firstname.set(firstname);
	}

	// Address

	/**
	 *
	 * @return this customer's address
	 */
	public String getAddress() {
		return address.get();
	}

	/**
	 *
	 * @return this customer's address propert
	 */
	public StringProperty addressProperty() {
		return address;
	}

	/**
	 *
	 * @param address this customer's address
	 */
	public void setAddress(String address) {
		this.address.set(address);
	}

	// Email

	/**
	 *
	 * @return this customer's email address
	 */
	public String getEmail() {
		return email.get();
	}

	/**
	 *
	 * @returnthis customer's email address property
	 */
	public StringProperty emailProperty() {
		return email;
	}

	/**
	 *
	 * @param email this customer's email address
	 */
	public void setEmail(String email) {
		this.email.set(email);
	}

	// Birthdate

	/**
	 *
	 * @return this customer's birthdate
	 */
	public String getBirthdate() {
		return birthdate.get();
	}

	/**
	 *
	 * @return this customer's birthdate property
	 */
	public StringProperty birthdateProperty() {
		return birthdate;
	}

	/**
	 *
	 * @param birthdate this customer's birthdate
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate.set(birthdate);
	}
}