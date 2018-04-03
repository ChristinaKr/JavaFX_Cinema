package controllers.customer;

import application.MainApp;
import helpers.Helpers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller class for CustomerRoot.fxml
 *
 * This controller sets up its associated view with the currently logged in user's name and
 * provides a shortcut to the logout method in the Helpers package and navigational methods.
 */
public class CustomerRootController {

	@FXML
	Label lblUsername;

	@FXML
	Label lblProfile, lblProgramme;
	@FXML
	ImageView imageLogo;


	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the welcome message for the customer and the JavaCinema logo.
	 */
	@FXML
	private void initialize() {
		lblUsername.setText("Welcome, " + MainApp.username + "!");
		imageLogo.setImage(new Image("file:" + System.getProperty("user.dir") + "/img/Logo.png"));
	}

	/**
	 * Displays the CustomerProfile view in the center of customerRoot
	 */
	@FXML
	private void showProfile() {
		MainApp.customerRoot.setCenter(MainApp.customerProfile);
	}

	/**
	 * Displays the CustomerProgramme view in the center of customerRoot
	 */
	@FXML
	private void showProgramme() {
		MainApp.customerRoot.setCenter(MainApp.customerProgramme);
	}

	/**
	 * Calls the logout method which logs out the currently logged-in user
	 */
	@FXML
	private void logout() {
		Helpers.logout();
	}
}
