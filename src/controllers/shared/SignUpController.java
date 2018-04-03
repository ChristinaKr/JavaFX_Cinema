package controllers.shared;

import helpers.DateStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Account;
import models.AccountDAO;
import models.Customer;
import models.CustomerDAO;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller class for SignUp.fxml
 *
 * Handles the business logic for registering a new Account and Customer in the database
 * and for closing the containing stage.
 */
public class SignUpController {

	@FXML
	TextField tfUsername, tfPassword, tfFamilyName, tfFirstName, tfEmail, tfAddress;
	@FXML
	DatePicker dpBirthdate;
	@FXML
	CheckBox checkNewsletter;

	private Stage stage;


	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the format of the DatePicker.
	 */
	@FXML
	private void initialize() {
		// Sets the format of the DatePicker to DD/MM/YYYY
		dpBirthdate.setConverter(new DateStringConverter());
	}

	/**
	 * Performs input verification and adds a new Account and Customer to the database
	 *
	 * This method retrieves the content of the input fields on the associated view
	 * and performs existence tests and a test for the syntactical correctness of the
	 * provided e-Mail address. If all tests pass, a new Account and a new Customer are created
	 * with their data fields filled in with the information that was just checked. These
	 * new items are then added to their respective tables in the database and the stage is closed.
	 */
	@FXML
	private void signup() {
		// Regular expression that matches eMail addresses
		// adapted from http://emailregex.com
		String eMailRegex = "(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." +
			"[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c" +
			"\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b" +
			"\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)" +
			"+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|" +
			"[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
			"[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a" +
			"\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

		// Verifies that all fields are filled
		if (tfUsername.getText().isEmpty() ||
			tfPassword.getText().isEmpty() ||
			tfFamilyName.getText().isEmpty() ||
			tfFirstName.getText().isEmpty() ||
			tfEmail.getText().isEmpty() ||
			tfAddress.getText().isEmpty() ||
			// Verifies that a value has been chosen in the date picker
			dpBirthdate.getValue() == null) {
			// Displays a warning popup if any of the input fields were left blank
			Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill " +
				"in all fields.");
			alert.setHeaderText("Incomplete Information");
			alert.setTitle("Warning");
			alert.showAndWait();
		// Verifies that the provided eMail address has a valid format
		} else if (!tfEmail.getText().matches(eMailRegex)) {
			// Displays a warning popup if the email does not match the regular expression
			Alert alert = new Alert(Alert.AlertType.WARNING, "Please provide " +
				"a valid email address.");
			alert.setHeaderText("Incorrect Email");
			alert.setTitle("Warning");
			alert.showAndWait();
		// Verifies that the provided birthdate is not in the future
		} else if (dpBirthdate.getValue().isAfter(LocalDate.now())) {
			// Displays a warning popup
			Alert alert = new Alert(Alert.AlertType.WARNING, "Sorry, we do not serve time travellers!");
			alert.setHeaderText("Greetings from the past");
			alert.setTitle("Warning");
			alert.showAndWait();
		// Verifies that the provided birthdate is at least 16 years in the past
		} else if (dpBirthdate.getValue().isAfter(LocalDate.now().minusYears(16))) {
			// Displays a warning popup
			Alert alert = new Alert(Alert.AlertType.WARNING, "You have to be " +
				"at least 16 years old to make an account.");
			alert.setHeaderText("Invalid age");
			alert.setTitle("Warning");
			alert.showAndWait();
		} else {
			// Creates a new account object
			Account account = new Account();
			// Sets the username and password with the information provided by the user
			account.setUsername(tfUsername.getText());
			account.setPassword(tfPassword.getText());
			// Sets the employee value to 0
			// The sign up view is only intended for the creation of new customers
			account.setEmployee(0);

			// Creates a new customer object
			Customer customer = new Customer();
			// Fills the object with information provided by the user
			customer.setUsername(tfUsername.getText());
			customer.setFamilyname(tfFamilyName.getText());
			customer.setFirstname(tfFirstName.getText());
			customer.setEmail(tfEmail.getText());
			customer.setAddress(tfAddress.getText());
			customer.setBirthdate(dpBirthdate.getValue().toString());
			if (checkNewsletter.isSelected()) {
				customer.setNewsletter(1);
			} else {
				customer.setNewsletter(0);
			}

			// Adds the new account and customer to the database in their respective tables
			try {
				AccountDAO.addAccount(account);
				CustomerDAO.addCustomer(customer);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Closes the view's stage
			stage.close();
		}
	}

	/**
	 * Setter method for this controller's stage object
	 *
	 * This method is required to allow the controller to close the stage
	 * after successful completion of the sig nup process.
 	 */
	void setStage(Stage stage) {
		this.stage = stage;
	}
}
