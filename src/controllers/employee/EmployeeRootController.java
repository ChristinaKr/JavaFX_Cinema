package controllers.employee;

import application.MainApp;
import helpers.Helpers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller class for EmployeeRoot.fxml
 *
 * Provides the business logic for setting the username which is displayed in the header of the associated view
 * as well as for the logout button.
 */
public class EmployeeRootController {

	@FXML
	Label lblUsername;
	@FXML
	ImageView imageLogo;

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the welcome message for the employee and the JavaCinema logo.
	 */
	@FXML
	private void initialize() {
		lblUsername.setText("Welcome, " + MainApp.username + "!");
		imageLogo.setImage(new Image("file:" + System.getProperty("user.dir") + "/img/Logo.png"));
	}

	/**
	 * Calls the logout method from the Helpers class
	 */
	@FXML
	private void logout() {
		Helpers.logout();
	}
}